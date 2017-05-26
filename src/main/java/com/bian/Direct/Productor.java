package com.bian.Direct;

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
        factory.setHost(DirectConfig.HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(DirectConfig.EXCHANGE_NAME, "direct");
        channel.queueDeclare(DirectConfig.QUEUE_NAME, false, false, false, null);
        channel.queueBind(DirectConfig.QUEUE_NAME, DirectConfig.EXCHANGE_NAME, DirectConfig.ROUTING_Key);

        channel.basicPublish(DirectConfig.EXCHANGE_NAME, DirectConfig.ROUTING_Key, null, DirectConfig.MESSAGE.getBytes());
        System.out.println("direct发送成功："+DirectConfig.MESSAGE);
        channel.close();
        connection.close();
    }
}
