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
	allOn(socket);
	allEmit(socket);
})


function allOn(socket){

	socket.on('message',function(data){	
		var sockets = io.sockets.sockets;
		// sockets.forEach(function(soc){
		// 	if(soc.id != socket.id){
		// 		soc.emit('message',data);
		// 	}
		// })
		// console.log(data.text + " ,from:= "+socket.id);
		socket.broadcast.emit('message',data);
		// socket.emit('message',data);
	});

	socket.on('connectedDone',function(data){
		users_list.push(JSON.parse(data));
		console.log("User added" + data);
	});	



	socket.on('disconnect',function(){
		users_list = users_list.filter(item => item.socket_id != socket.id);
		console.log("user removed" + users_list);
	});

	socket.on('heartbeat',function(data){
		console.log("beating");
	});

}

function allEmit(socket){	

	socket.emit('connected',{"info":socket.id});
	console.log("Yo Connected "+socket.id);

	function sendHeartBeat(){		
		setTimeout(sendHeartBeat,20000);
	    socket.emit('ping', { beat : 1 });
	}

	setTimeout(sendHeartBeat,20000);	
}
