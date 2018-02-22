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
	    if(typeof users_list[JSON.parse(data).to] === 'undefined'){
	        console.log("User Offline Message Queued");
	    }else{
	        io.sockets.connected[users_list[JSON.parse(data).to]].emit('privateMessageGet',{"text":JSON.parse(data).message});
	    }

	});

	socket.on('connectedDone',function(data){
	    var user = JSON.parse(data);
	    users_list[user.email] = user.socket_id;
	});	

    socket.on('groupMessage',function(data){
        socket.broadcast.emit('groupMessage',data);
    });


	socket.on('disconnect',function(){
		var index = users_list.indexOf(socket.id);
        if (index !== -1) users_list.splice(index, 1);
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
