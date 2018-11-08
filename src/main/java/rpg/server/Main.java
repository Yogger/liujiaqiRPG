package rpg.server;

import javax.swing.CellEditor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import rpg.area.Area;
import rpg.area.Scene;
import rpg.pojo.Monster;
import rpg.pojo.Npc;

public class Main {
	public static void main(String[] args) {
		init();
		ApplicationContext context= new ClassPathXmlApplicationContext("classpath*:server.xml");
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
		Area.npcList.add(Zhnpc);
		Area.npcList.add(Wqnpc);
		
		Monster SLMmonster = new Monster();
		SLMmonster.setName("史莱姆");
		SLMmonster.setDeadFlag(true);
		Monster MDSmonster = new Monster();
		MDSmonster.setName("美杜莎");
		MDSmonster.setDeadFlag(true);
		
		Scene born = new Scene();
		born.setId(1);
		born.setName("起始之地");
		born.setNpc(Zhnpc);
		born.setMonster(SLMmonster);
		Area.sceneList.add(born);
		
		Scene village = new Scene();
		village.setId(2);
		village.setName("村庄");
		village.setNpc(Zhnpc);
		village.setMonster(MDSmonster);
		Area.sceneList.add(village);
		
		Scene senlin = new Scene();
		senlin.setId(3);
		senlin.setName("森林");
		senlin.setNpc(Zhnpc);
		senlin.setMonster(SLMmonster);
		Area.sceneList.add(senlin);
		
		Scene chenBao = new Scene();
		chenBao.setId(4);
		chenBao.setName("城堡");
		chenBao.setNpc(Wqnpc);
		chenBao.setMonster(MDSmonster);
		Area.sceneList.add(chenBao);
	}
}
