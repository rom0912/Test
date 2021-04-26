var amqp = require('../nodejs/node_modules/amqplib/callback_api');

// userId:passwd@host:port
const url = 'amqp://admin:admin@127.0.0.1:5672';

amqp.connect(url, function(error, connect){
    if(error){
        console.log(error);
        return;
    }
    connect.createChannel(function(error, channel){
        if(error){
            console.log(error);
            return;
        }
		// 파라미터 정보
		let msg = {
			"testId" : "test",
			"info" : "12345"
		};
		const exchange = "nodejs";
		channel.assertExchange(exchange, 'topic', {durable : true});
		channel.publish(exchange, 'com.romy.nodejs', Buffer.from(JSON.stringify(msg)));
		
		setTimeout(function() {
			connect.close();
			process.exit(0);
		}, 500);
    });
});
