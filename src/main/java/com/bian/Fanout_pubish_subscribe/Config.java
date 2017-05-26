package com.bian.Fanout_pubish_subscribe;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class Config {
    public final static String FANOUT_EXCHANGE_NAME = "my_fanout_exchange";
    public final static String HOST = "localhost";
    public final static String MESSAGE = "hello world!";
    //fanout发送到所有绑定到路由器的队列
}
