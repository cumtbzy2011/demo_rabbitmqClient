package com.bian.Direct_rpc_autoReconn;

import com.bian.utils.ThreadUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.impl.recovery.AutorecoveringChannel;
import com.rabbitmq.client.impl.recovery.AutorecoveringConnection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(DirectRpcConfig.HOST);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setTopologyRecoveryEnabled(true);
        //newConnection()实际上是和服务器的一个随机端口建立连接
        AutorecoveringConnection connection = (AutorecoveringConnection) factory.newConnection();
        AutorecoveringChannel channel = (AutorecoveringChannel) connection.createChannel();
        connection.addRecoveryListener(recoverable -> {
            System.out.println("connction断线重连");
        });
        channel.addRecoveryListener(recoverable -> {
            System.out.println("channel断线重连");
        });
        //消费者要求公平转发，同时只接受一个消息
        channel.basicQos(1);
        channel.queueDeclare(DirectRpcConfig.QUEUE_NAME, DirectRpcConfig.DURABLE, false, false, null);
        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String replyExchange = properties.getReplyTo();
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();
                ThreadUtils.sleep(6000L);
                String message = new String(body, "UTF-8");
                System.out.println("rpc收到消息：" + message);
                String replyMessage = message + "too";
                //生产-消费者还有耦合：reply-routing-key?
                this.getChannel().basicPublish(replyExchange, DirectRpcConfig.REPLY_ROUTING_Key, replyProps, replyMessage.getBytes());
            }
            //autoAck在handleDelivery()执行之后执行
        };
        //重写了handleDelivery就不用while(true)了，
        //实际上QueueingConsumer的handlerDelivery是将delivery放到队列中，供nextDelivery()获取

        //consumer提供回调接口，其实channel已经实现了while(true)的功能-会一直阻塞在此方法
        //由于没有重连机制，掉线之后就一直阻塞在这，即使不能再收到消息
        channel.basicConsume(DirectRpcConfig.QUEUE_NAME, true, consumer);
    }
}
