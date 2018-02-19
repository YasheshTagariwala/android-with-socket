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
	allListners(socket);
	allEmiters(socket);
})


function allListners(socket){

	socket.on('privateMessage',function(data){	
		var sockets = io.sockets.sockets;
		socket.broadcast.emit('privateMessage',data);
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

function allEmiters(socket){	

	socket.emit('connected',{"info":socket.id});
	console.log("Yo Connected "+socket.id);

	function sendHeartBeat(){		
		setTimeout(sendHeartBeat,20000);
	    socket.emit('ping', { beat : 1 });
	}

	setTimeout(sendHeartBeat,20000);	
}
