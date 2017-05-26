package com.bian.Fanout_pubish_subscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Subscriber1 {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Config.HOST);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(60000);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Config.FANOUT_EXCHANGE_NAME, "fanout");
        //创建一个非持久、唯一的、自动删除的队列
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, Config.FANOUT_EXCHANGE_NAME, "");
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("1类订阅者收到消息："+message);
        }
    }
}
