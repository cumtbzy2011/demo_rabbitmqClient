package com.bian.Direct_rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DirectRpcConfig.HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(DirectRpcConfig.QUEUE_NAME, false, false, false, null);
        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String replyExchange = properties.getReplyTo();
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();
                String message = new String(body, "UTF-8");
                System.out.println("rpc收到消息：" + message);
                String replyMessage = message + "too";
                //生产-消费者还有耦合：reply-routing-key?
                this.getChannel().basicPublish(replyExchange, DirectRpcConfig.REPLY_ROUTING_Key, replyProps, replyMessage.getBytes());
            }
        };
        //重写了handleDelivery就不用while(true)了，
        //实际上QueueingConsumer的handlerDelivery是将delivery放到队列中，供nextDelivery()获取

        //consumer提供回调接口，其实channel已经实现了while(true)的功能-会一直阻塞在此方法
        //由于没有重连机制，掉线之后就一直阻塞在这，即使不能再收到消息
        channel.basicConsume(DirectRpcConfig.QUEUE_NAME, true, consumer);
    }
}
