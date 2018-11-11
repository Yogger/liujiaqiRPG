package rpg.server;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.SchedulingException;

import rpg.area.Area;
import rpg.area.Scene;
import rpg.pojo.Monster;
import rpg.pojo.Npc;
import rpg.pojo.Skill;
import rpg.skill.SkillList;

public class Main {
	public static void main(String[] args) throws Exception {
		ArrayList<Npc> npcList = new ArrayList<Npc>();
		ArrayList<Monster> monsterList = new ArrayList<Monster>();
		initMonster(monsterList);
		initNpc(npcList);
		initScene(monsterList, npcList);
//		init();
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:server.xml");
		ServerMain serverMain = (ServerMain) context.getBean("serverMain");
		serverMain.run();
	}

	// 初始化怪物
	private static void initMonster(ArrayList<Monster> monsterList) throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\monster.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Monster monster = new Monster();
			monster.setName(e.elementText("name"));
			monster.setAliveFlag(true);
			monster.setHp(Integer.valueOf(e.elementText("hp")));
			monster.setAck(Integer.valueOf(e.elementText("ack")));
			monsterList.add(monster);
		}
	}

	// 初始化npc
	public static void initNpc(ArrayList<Npc> npcList) throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\npc.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Npc npc = new Npc();
			npc.setName(e.elementText("name"));
			npc.setMsg(e.elementText("msg"));
			npcList.add(npc);
		}
	}

	// 初始化场景
	public static void initScene(ArrayList<Monster> monsterList, ArrayList<Npc> npcList) throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\Scene.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		List<Scene> sceneList = new ArrayList();
		for (Element e : elementList) {
			Scene scene = new Scene();
			scene.setName(e.elementText("name"));
			scene.setId(Integer.valueOf(e.elementText("id")));
			// 设置npc
			String[] npcId = e.elementText("npc").split(",");
			LinkedList<Npc> list = new LinkedList<Npc>();
			for (String npc : npcId) {
				Integer id = Integer.valueOf(npc);
				Npc npc2 = npcList.get(id - 1);
				list.add(npc2);
			}
			scene.setNpcList(list);
			// 设置怪物
			String[] monsterId = e.elementText("monster").split(",");
			LinkedList<Monster> list2 = new LinkedList<>();
			for (String monster : monsterId) {
				Integer id = Integer.valueOf(monster);
				Monster monster2 = monsterList.get(id - 1);
				list2.add(monster2);
			}
			scene.setMonsterList(list2);
			// 设置地图的连通
			String[] Near = e.elementText("near").split(",");
			for (String near : Near) {
				Area.mp2[Integer.valueOf(e.elementText("id"))][Integer.valueOf(near)] = 1;
			}
			//
			Area.mp1.put(e.elementText("name"), Integer.valueOf(e.elementText("id")));
			Area.sceneList.add(scene);
		}
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
		SLMmonster.setHp(5000);
		SLMmonster.setAck(1);
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

		Skill skill = new Skill();
		skill.setId(1);
		skill.setName("毒雷引爆");
		skill.setCd(3000);
		skill.setMp(10);
		skill.setHurt(5);
		skill.setEffect("中毒");
		Skill sl = new Skill();
		sl.setId(2);
		sl.setName("神罗天罡");
		sl.setCd(5000);
		sl.setMp(20);
		sl.setHurt(60);
		sl.setEffect("护盾");
		SkillList.mp.put("1", skill);
		SkillList.mp.put("3", sl);
	}
}