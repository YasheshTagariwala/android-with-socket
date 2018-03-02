//An Express Object To Create Server
var app = require('express')();

//An Http Object To Start / Host The Server
var server = require('http').Server(app);

//An Socket Object To For All Other Purpose
var io = require('socket.io')(server);

//An FileSystem Object For Read Write Operation
var fs = require('fs');

//A Variable That Stores The Path Of Where Offline Messages Are Stored
var filePath = __dirname + '/privateMessageQueue.json';

//An Array To All Connected Users
var users_list = [];

//An Array To Store All Offline Messages
var offline_private_message = [];

//Start The Server To On Specific Port eg:- (3000)
server.listen(3000,() => {
	console.log("Yep Running");
})

const device = io.of('/socket');
//Event Fired When A Client Is Connected
//Used To Initialize All The Other Socket Event
device.on('connect',(socket) => {
    onConnect(socket);
	allListeners(socket);
})

//All Socket Events Function
function allListeners(socket){

    //Private Message Event
	socket.on('privateMessageEmit',(data) => {
	    privateMessage(data);
	});

    //Connection Done Event
	socket.on('connectedDone',(data) => {
	    connectionDone(data);
	});

    //Other Activities Event
	socket.on('otherActivities',(data) => {
        otherActivities(data);
	})

    //Group Message Event
    socket.on('groupMessage',(data) => {
//        socket.broadcast.emit('groupMessage',data);
    });

    //Disconnect Message Event
	socket.on('disconnect',() => {
        onDisconnect(socket);
	});

}

//A Disconnect Function To Remove User From Connected Client List Array(user_list)
function onDisconnect(socket){
    users_list = users_list.filter((item) => {return item.socket_id != socket.id});
    console.log("user removed " + socket.id);
}

//Sends A Private Message To Specific Client With Push Notification
function privateMessage(data){
    var data = JSON.parse(data);
    var user = users_list.filter((item) => {return item.email == data.to});
    if(user.length == 0){
        offline_private_message.push(data);
        writeMessageToFile(offline_private_message);
    }else{
        io.of('/socket').connected[user[0].socket_id].emit('privateMessageGet',{"text":data.message});
        customPushNotification(user[0].socket_id,data.message,data.from,7);
    }
}

//Checks For Any Offline Messages File Exists / Create It
//And Prints A Log That User Is Connected And Emits Connected Event To Get User Information
function onConnect(socket){
    if(fs.existsSync(filePath)){
        fs.readFile(filePath,(error,data) => {
            if(error) throw error;
            offline_private_message = JSON.parse(data);
        });
    }
    socket.emit('connected',{"info":socket.id});
    console.log("User Connected "+socket.id);
    setTimeout(sendHeartBeat,20000);

    //A HeartBeat Function To Stay Connected With User
    function sendHeartBeat(){
        socket.emit('ping', { beat : 1 });
        setTimeout(sendHeartBeat,20000);
    }
}

//Adds User To Connected Client Array(user_list)
//Checks For Any Offline Messages For That User And Sends If Any
function connectionDone(data){
    var user = JSON.parse(data);
    users_list.push(user);
    var pending_message = offline_private_message.filter((item) => { return item.to == user.email});
    var from = "";
    var message = "";
    if(pending_message.length != 0){
        for(var i = 0; i < pending_message.length; i++){
            io.of('/socket').connected[user.socket_id].emit('privateMessageGet',{"text":pending_message[i].message});
            from = pending_message[i].from;
            message = pending_message[i].message;
        }
        customPushNotification(user.socket_id,message,from,7);
        offline_private_message = offline_private_message.filter((item) => {return item.to != user.email});
        writeMessageToFile(offline_private_message);
    }
    console.log(users_list);
}

//Fired When Sending Push Notification For Any Other Activity(Liked,Commented,Etc..)
function otherActivities(data){
    var user = users_list.filter((item) => {return item.email == data.to});
    if(user.length != 0){
        customPushNotification(user[0].socket_id,data.message,data.from,data.type);
    }
}

//A Custom Push Notification Function Which Can Be Used For Every Other Service To Push Notifications
function customPushNotification(socketId,message,from,type){
    io.of('/socket').connected[socketId].emit("pushMessage",{"message":message,"doer":from,"type":type});
}

//Writes Offline Messages To File
function writeMessageToFile(data){
    fs.writeFile(filePath,JSON.stringify(data),(error) => {
        if(error) throw error;
    });
}
