package rpg.server;

import java.util.LinkedList;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import rpg.area.Area;
import rpg.area.Scene;
import rpg.pojo.Monster;
import rpg.pojo.Npc;

public class Main {
	public static void main(String[] args) {
		init();
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:server.xml");
		ServerMain serverMain = (ServerMain) context.getBean("serverMain");
		serverMain.run();
	}

	public static void init() {
		Npc Zhnpc = new Npc();
		Npc Wqnpc = new Npc();
		Zhnpc.setName("杂货老板");
		Zhnpc.setMsg("hey,小伙子,欢迎杂货店");
		Wqnpc.setName("武器老板");
		Wqnpc.setMsg("欢迎光临武器店,十八般武器任你挑");

		Monster SLMmonster = new Monster();
		SLMmonster.setName("史莱姆");
		SLMmonster.setAliveFlag(true);
		SLMmonster.setHp(100);
		SLMmonster.setAck(10);
		Monster MDSmonster = new Monster();
		MDSmonster.setName("美杜莎");
		MDSmonster.setAliveFlag(true);
		MDSmonster.setHp(200);
		MDSmonster.setAck(15);

		Scene born = new Scene();
		born.setId(1);
		born.setName("起始之地");
		LinkedList<Npc> list1 = new LinkedList<Npc>();
		list1.add(Wqnpc);
		list1.add(Zhnpc);
		born.setNpcList(list1);
		LinkedList<Monster> list2 = new LinkedList<Monster>();
		list2.add(SLMmonster);
		born.setMonsterList(list2);
		Area.sceneList.add(born);

		Scene village = new Scene();
		village.setId(2);
		village.setName("村庄");
		LinkedList<Npc> list3 = new LinkedList<Npc>();
		list3.add(Zhnpc);
		village.setNpcList(list3);
		village.setMonsterList(list2);
		Area.sceneList.add(village);

		Scene senlin = new Scene();
		senlin.setId(3);
		senlin.setName("森林");
		senlin.setNpcList(list1);
		senlin.setMonsterList(list2);
		Area.sceneList.add(senlin);

		Scene chenBao = new Scene();
		chenBao.setId(4);
		chenBao.setName("城堡");
		chenBao.setNpcList(list1);
		chenBao.setMonsterList(list2);
		Area.sceneList.add(chenBao);
	}
}
