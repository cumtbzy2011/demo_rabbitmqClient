package com.bian.Direct_rpc_autoReconn;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.recovery.AutorecoveringChannel;
import com.rabbitmq.client.impl.recovery.AutorecoveringConnection;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Productor {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DirectRpcConfig.HOST);
        //设置重连
 //        1)Connection的重连。
//        2)侦听Connection的Listener的恢复。
//        3)重新建立在Connection基础上的Channel。
//        4)侦听Channel的Listener的恢复。
//        5)Channel上的设置，如basicQos，publisher confirm以及事务属性等的恢复。
        factory.setAutomaticRecoveryEnabled(true);
//        1)exchange的重新定义(不包含预定义的exchange)
//        2)queue的重新定义(不包含预定义的queue)
//        3)binding的重新定义(不包含预定义的binding)
//        4)所有Consumer的恢复-basicConsumer()
        factory.setTopologyRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(60000);
        //设置重连监听（回调
        AutorecoveringConnection connection = (AutorecoveringConnection) factory.newConnection();
        AutorecoveringChannel channel = (AutorecoveringChannel) connection.createChannel();
        connection.addRecoveryListener(recoverable -> {
            System.out.println("connection 断线重连");
        });
        channel.addRecoveryListener(recoverable -> {
            System.out.println("channel 断线重连");
        });

        channel.exchangeDeclare(DirectRpcConfig.EXCHANGE_NAME, "direct");
        channel.queueDeclare(DirectRpcConfig.QUEUE_NAME, DirectRpcConfig.DURABLE, false, false, null);
        channel.queueBind(DirectRpcConfig.QUEUE_NAME, DirectRpcConfig.EXCHANGE_NAME, DirectRpcConfig.ROUTING_Key);

        channel.exchangeDeclare(DirectRpcConfig.REPLY_EXCHANGE_NAME, "direct");
        channel.queueDeclare(DirectRpcConfig.REPLY_QUEUE_NAME, false, false, false, null);
        channel.queueBind(DirectRpcConfig.REPLY_QUEUE_NAME, DirectRpcConfig.REPLY_EXCHANGE_NAME, DirectRpcConfig.REPLY_ROUTING_Key);

        String uuid = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties()
                .builder()
                .correlationId(uuid)
                //持久化消息
                .deliveryMode(2)
                .priority(0)
                .replyTo(DirectRpcConfig.REPLY_EXCHANGE_NAME)
                .build();
        channel.basicPublish(DirectRpcConfig.EXCHANGE_NAME, DirectRpcConfig.ROUTING_Key, props, DirectRpcConfig.MESSAGE.getBytes());
        System.out.println("direct发送成功："+ DirectRpcConfig.MESSAGE);

        //获取reply消息
        QueueingConsumer replyConsumer = new QueueingConsumer(channel);
        channel.basicConsume(DirectRpcConfig.REPLY_QUEUE_NAME, true, replyConsumer);
        while (true) {
            QueueingConsumer.Delivery delivery = replyConsumer.nextDelivery();
            String replyUuid = delivery.getProperties().getCorrelationId();
            String replyMessage = new String(delivery.getBody());
            if (uuid.equals(replyUuid)) {
                System.out.println("成功获得对应的reply消息："+ replyMessage);
                break;
            }
        }

        channel.close();
        connection.close();
    }
}
