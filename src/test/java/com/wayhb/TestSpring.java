package com.wayhb;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.netty.HelloServer;
import com.netty.HelloServerHandler;

import _01discard.DiscardServer;

public class TestSpring {
    
    private static Logger log = Logger.getLogger(TestSpring.class); 

    public static void main(String[] args) throws Exception  {
        // TODO Auto-generated method stub
        //加载spirng配置文件
        ApplicationContext context= new ClassPathXmlApplicationContext("classpath:server.xml");
        HelloServer helloServer = (HelloServer) context.getBean("helloServer");
        helloServer.run();
    }
}
