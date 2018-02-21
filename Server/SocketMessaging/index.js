var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io').listen(server);


var users_list = [];

server.listen(3000,function(){
	console.log("Yep Running");
})

app.get('/',function(req,res){
	res.sendFile(__dirname+'/index.html');
})

io.on('connection',function(socket){
	allListenersAndEmitters(socket);
})


function allListenersAndEmitters(socket){

	socket.on('privateMessageEmit',function(data){
		for(var i=0;i<users_list.length;i++){
		    io.sockets.connected[users_list[i][JSON.parse(data).to].socket_id].emit('privateMessageGet',{"text":JSON.parse(data).message});
//            socket.broadcast.to(users_list[i][JSON.parse(data).to].socket_id).emit('privateMessageGet',{"text":JSON.parse(data).message});
		}
	});

	socket.on('connectedDone',function(data){
		users_list.push(JSON.parse(data));
	});	

    socket.on('groupMessage',function(data){
//        var sockets = io.sockets.sockets;
        socket.broadcast.emit('groupMessage',data);
    });


	socket.on('disconnect',function(){
		users_list = users_list.filter(item => item.socket_id == socket.id);
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
