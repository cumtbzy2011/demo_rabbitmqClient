package com.bian.Topic_regex;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Productor {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(com.bian.Fanout_pubish_subscribe.Config.HOST);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(60000);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Config.TOPIC_EXCHANGE_NAME, "topic");
        String[] routing_keys = new String[]{"kernal.info", "cron.warning",
                "auth.info", "kernel.critical"};
        Arrays.stream(routing_keys)
                .forEach(key -> {
                    try {
                        String message = UUID.randomUUID().toString();
                        channel.basicPublish(Config.TOPIC_EXCHANGE_NAME, key, null, message.getBytes());
                        System.out.println("发送“" + message + "”至：" + key);
                    } catch (Exception e) {

                    }
                });
        channel.close();
        connection.close();
    }
}
