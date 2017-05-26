package com.bian.Fanout_pubish_subscribe;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Publisher {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Config.HOST);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(60000);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Config.FANOUT_EXCHANGE_NAME, "fanout");
        channel.basicPublish(Config.FANOUT_EXCHANGE_NAME, "s随意写s", null, Config.MESSAGE.getBytes());
        System.out.println("publish发送成功："+Config.MESSAGE);
        channel.close();
        connection.close();
    }
}
