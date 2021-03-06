package com.bian.Direct_rpc_autoReconn;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class DirectRpcConfig {
    public final static String HOST = "localhost";
    public final static String QUEUE_NAME = "hello_rpc_queue";
    public final static String EXCHANGE_NAME = "hello_rpc_exchange";
    public final static String ROUTING_Key = "hello_rpc_routing";
    public final static String REPLY_QUEUE_NAME = "hello_rpc_reply_queue";
    public final static String REPLY_EXCHANGE_NAME = "hello_rpc_reply_exchange";
    public final static String REPLY_ROUTING_Key = "hello_rpc_reply_routing";
    public final static String MESSAGE = "hello world!";
    public final static boolean DURABLE = true;
    public final static AMQP.BasicProperties BASIC_PROPERTIES = MessageProperties.PERSISTENT_TEXT_PLAIN;

}
