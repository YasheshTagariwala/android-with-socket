var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server,{
    pingInterval:1000,
    pintTimeout:2000
});

var fs = require('fs');
var filePath = __dirname + '/privateMessageQueue.json';


var users_list = [];
var offline_private_message = [];

server.listen(3000,function(){
	console.log("Yep Running");
})

io.on('connect',function(socket){
    if(fs.existsSync(filePath)){
        fs.readFile(filePath,function(error,data){
            if(error) throw error;
            offline_private_message = JSON.parse(data);
        });
    }
	allListenersAndEmitters(socket);
})


function allListenersAndEmitters(socket){

	socket.on('privateMessageEmit',function(data){
	    var data = JSON.parse(data);
	    var user = users_list.filter(function(item){return item.email == data.to});
	    if(user.length == 0){
	        offline_private_message.push(data);
	        fs.writeFile(filePath,JSON.stringify(offline_private_message),function(error){
                if(error) throw error;
            });
	    }else{
	        io.sockets.connected[user[0].socket_id].emit('privateMessageGet',{"text":data.message});
	        customPushNotification(user[0].socket_id,data.message,data.from,7);
	    }

	});

	socket.on('connectedDone',function(data){
	    var user = JSON.parse(data);
        users_list.push(user);
        var pending_message = offline_private_message.filter(function(item){ return item.to == user.email});
        var from = "";
        var message = "";
        if(pending_message.length != 0){
            for(var i = 0; i < pending_message.length; i++){
                io.sockets.connected[user.socket_id].emit('privateMessageGet',{"text":pending_message[i].message});
                from = pending_message[i].from;
                message = pending_message[i].message;
            }
            customPushNotification(user.socket_id,message,from,7);
            offline_private_message = offline_private_message.filter(function(item) {return item.to != user.email});
            fs.writeFile(filePath,JSON.stringify(offline_private_message),function(error){
                if(error) throw error;
            });
        }
        console.log(users_list);
	});

	socket.on('otherActivities',function(data){
        var user = users_list.filter(function(item){return item.email == data.to});
        if(user.length != 0){
            customPushNotification(user[0].socket_id,data.message,data.from,data.type);
        }
	})

    socket.on('groupMessage',function(data){
        socket.broadcast.emit('groupMessage',data);
    });


	socket.on('disconnect',function(){
        users_list = users_list.filter(function(item){return item.socket_id != socket.id});
		console.log("user removed " + socket.id);
	});

	socket.emit('connected',{"info":socket.id});
	console.log("User Connected "+socket.id);

	function sendHeartBeat(){
	    socket.emit('ping', { beat : 1 });
		setTimeout(sendHeartBeat,20000);
	}

	setTimeout(sendHeartBeat,20000);
}

function customPushNotification(socketId,message,from,type){
    io.sockets.connected[socketId].emit("pushMessage",{"message":message,"doer":from,"type":type});
}