package _01discard;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    //将规则跑起来
    public static void main(String[] args) throws Exception {
    	ApplicationContext context= new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        DiscardServer discardServer = (DiscardServer) context.getBean("discardServer");
        discardServer.run(8080);
        System.out.println("server:run()");
    }
}