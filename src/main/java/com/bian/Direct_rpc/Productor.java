package com.bian.Direct_rpc;

import com.rabbitmq.client.*;

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
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(DirectRpcConfig.EXCHANGE_NAME, "direct");
        channel.queueDeclare(DirectRpcConfig.QUEUE_NAME, false, false, false, null);
        channel.queueBind(DirectRpcConfig.QUEUE_NAME, DirectRpcConfig.EXCHANGE_NAME, DirectRpcConfig.ROUTING_Key);

        channel.exchangeDeclare(DirectRpcConfig.REPLY_EXCHANGE_NAME, "direct");
        channel.queueDeclare(DirectRpcConfig.REPLY_QUEUE_NAME, false, false, false, null);
        channel.queueBind(DirectRpcConfig.REPLY_QUEUE_NAME, DirectRpcConfig.REPLY_EXCHANGE_NAME, DirectRpcConfig.REPLY_ROUTING_Key);

        String uuid = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties()
                .builder()
                .correlationId(uuid)
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
