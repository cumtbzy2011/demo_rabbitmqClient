package com.bian.Topic_regex;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Consumer2 {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(com.bian.Fanout_pubish_subscribe.Config.HOST);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(60000);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Config.TOPIC_EXCHANGE_NAME, "topic");
        String queueName = channel.queueDeclare().getQueue();
        //以正则reg声明routing-key的name
        channel.queueBind(queueName, Config.TOPIC_EXCHANGE_NAME, "kernel.*");
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String messsage = new String(delivery.getBody(), "UTF-8");
            String key = delivery.getEnvelope().getRoutingKey();
            System.out.println("2类消费者收到：" + messsage + ", rkey:" + key);
        }
    }
}
