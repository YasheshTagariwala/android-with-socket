var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server,{
    pingInterval:1000,
    pintTimeout:2000
});


var users_list = [];
var offline_private_message = [];

server.listen(3000,function(){
	console.log("Yep Running");
})

io.on('connect',function(socket){
	allListenersAndEmitters(socket);
})


function allListenersAndEmitters(socket){

	socket.on('privateMessageEmit',function(data){
	    var data = JSON.parse(data);
	    var user = users_list.filter(function(item){return item.email == data.to});
	    if(user.length == 0){
	        offline_private_message.push(data);
	        console.log(offline_private_message);
	    }else{
	        io.sockets.connected[user[0].socket_id].emit('privateMessageGet',{"text":data.message});
	        io.sockets.connected[user[0].socket_id].emit("pushMessage",{"message":data.message,"doer":data.from});
	    }

	});

	socket.on('connectedDone',function(data){
	    var user = JSON.parse(data);
        users_list.push(user);
        var pending_message = offline_private_message.filter(function(item){ return item.to == user.email});
        if(pending_message.length != 0){
            for(var i = 0; i < pending_message.length; i++){
                io.sockets.connected[user.socket_id].emit('privateMessageGet',{"text":pending_message[i].message});
                io.sockets.connected[user.socket_id].emit("pushMessage",{"message":pending_message[i].message,"doer":pending_message[i].from});
            }
            offline_private_message = offline_private_message.filter(function(item) {return item.to != user.email});
        }
        console.log(offline_private_message);
        console.log(users_list);
	});

    socket.on('groupMessage',function(data){
        socket.broadcast.emit('groupMessage',data);
    });


	socket.on('disconnect',function(){
        users_list = users_list.filter(function(item){return item.socket_id != socket.id});
		console.log("user removed " + socket.id);
	});

	socket.on('heartbeat',function(data){
		console.log("beating");
	});


	socket.emit('connected',{"info":socket.id});
	console.log("Yo Connected "+socket.id);

	function sendHeartBeat(){
	    socket.emit('ping', { beat : 1 });
		setTimeout(sendHeartBeat,20000);
	}

	setTimeout(sendHeartBeat,20000);

}
