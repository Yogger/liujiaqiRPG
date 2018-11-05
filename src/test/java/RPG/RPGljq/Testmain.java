package RPG.RPGljq;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Testmain {

	public static void main(String[] args) {
    	ApplicationContext context= new ClassPathXmlApplicationContext("classpath*:server.xml");
    	Test bean = (Test) context.getBean("test");
    	bean.test();
	}
}
