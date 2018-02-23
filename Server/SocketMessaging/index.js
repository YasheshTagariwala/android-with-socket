var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server,{
    pingInterval:10000,
    pintTimeout:5000
});


var users_list = [];

server.listen(3000,function(){
	console.log("Yep Running");
})

app.get('/',function(req,res){
	res.sendFile(__dirname+'/index.html');
})

io.on('connect',function(socket){
	allListenersAndEmitters(socket);
})


function allListenersAndEmitters(socket){

	socket.on('privateMessageEmit',function(data){
	    var data = JSON.parse(data);
	    var user = users_list.filter(function(item){return item.email == data.to});
	    if(user.length == 0){
	        console.log("User Offline Message Queued");
	    }else{
	        io.sockets.connected[user[0].socket_id].emit('privateMessageGet',{"text":data.message});
	        io.sockets.connected[user[0].socket_id].emit("pushMessage",{"message":data.message,"doer":data.from});
	    }

	});

	socket.on('connectedDone',function(data){
	    var user = JSON.parse(data);
        users_list.push(user);
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
		setTimeout(sendHeartBeat,20000);
	    socket.emit('ping', { beat : 1 });
	}

	setTimeout(sendHeartBeat,20000);

}
