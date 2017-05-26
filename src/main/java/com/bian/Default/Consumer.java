package com.bian.Default;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Consumer {

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(Config.HOST);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		//创建此队列防止队列不存在
		channel.queueDeclare(Config.QUEUE_NAME, false, false, false, null);
		QueueingConsumer consumer = new QueueingConsumer(channel);
		//绑定消费者和队列，autoAck=false需要手动确认
		channel.basicConsume(Config.QUEUE_NAME, false, consumer);

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			System.out.println("收到消息："+message);
			//手动确认消息，multiple=false代表不是确认所有消息，只确认tag对应的消息
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		}
	}
}
