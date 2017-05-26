package com.bian.Default;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Productor {


	public static void main(String[] args) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(Config.HOST);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(Config.QUEUE_NAME,false,false,false,null);

		channel.basicPublish("", Config.QUEUE_NAME, null, Config.MESSAGE.getBytes());
		System.out.println("发送成功："+ Config.MESSAGE);
		channel.close();
		connection.close();
	}
}
