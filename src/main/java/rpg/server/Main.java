package rpg.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import rpg.area.Area;
import rpg.area.Refresh;
import rpg.area.Scene;
import rpg.pojo.Buff;
import rpg.pojo.EmailRpg;
import rpg.pojo.Level;
import rpg.pojo.Monster;
import rpg.pojo.Npc;
import rpg.pojo.Skill;
import rpg.pojo.Task;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.skill.SkillList;

/**
 * 启动类
 * 
 * @author ljq
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		ArrayList<Npc> npcList = new ArrayList<Npc>();
		ArrayList<Monster> monsterList = new ArrayList<Monster>();
		initMonster(monsterList);
		initNpc(npcList);
		initScene(monsterList, npcList);
		initSkill();
		initYaopin();
		initBuff();
		initZb();
		initStore();
		initEmail();
		initTask();
		initLevel();
		ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("buff线程").build();
		ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
		scheduled.scheduleAtFixedRate(new Refresh(), 0, 2000, TimeUnit.MILLISECONDS);
//		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Refresh(), 0, 2000, TimeUnit.MILLISECONDS);
//		init();
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:server.xml");
		ServerMain serverMain = (ServerMain) context.getBean("serverMain");
		serverMain.run();
	}

	private static void initLevel() throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\level.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Level level = new Level();
			level.setId(Integer.valueOf(e.elementText("id")));
			level.setExpl(Integer.valueOf(e.elementText("expL")));
			level.setExpr(Integer.valueOf(e.elementText("expR")));
			IOsession.levelMp.put(Integer.valueOf(e.elementText("id")), level);
		}
	}

	private static void initTask() throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\task.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Task task = new Task();
			task.setId(Integer.valueOf(e.elementText("id")));
			task.setName(e.elementText("name"));
			task.setReqid(Integer.valueOf(e.elementText("reqid")));
			task.setNum(Integer.valueOf(e.elementText("num")));
			task.setMoney(Integer.valueOf(e.elementText("money")));
			task.setAwardId(Integer.valueOf(e.elementText("award")));
			IOsession.taskMp.put(Integer.valueOf(e.elementText("id")), task);
		}
	}

	private static void initEmail() {
		ArrayList<EmailRpg> arrayList1 = new ArrayList<EmailRpg>();
		ArrayList<EmailRpg> arrayList2 = new ArrayList<EmailRpg>();
		IOsession.alluserEmail.put("q", arrayList1);
		IOsession.alluserEmail.put("a", arrayList2);
		IOsession.alluserEmail.put("z", arrayList2);
		IOsession.alluserEmail.put("b", arrayList2);
	}

	/**
	 * 初始化商店
	 * 
	 * @throws Exception
	 */
	private static void initStore() throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\store.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			IOsession.STORE_SYSTEM.setName(e.elementText("name"));
			HashMap<Integer, Yaopin> yaopinMap = new HashMap<>(500);
			HashMap<Integer, Zb> zbMap = new HashMap<>(500);
			String[] yaopinId = e.elementText("yaopin").split(",");
			for (String yaopin : yaopinId) {
				Yaopin yaopin2 = IOsession.yaopinMp.get(Integer.valueOf(yaopin));
				yaopinMap.put(yaopin2.getId(), yaopin2);
			}
			String[] zbId = e.elementText("zb").split(",");
			for (String zb : zbId) {
				Zb zb2 = IOsession.zbMp.get(Integer.valueOf(zb));
				zbMap.put(zb2.getId(), zb2);
			}
			IOsession.STORE_SYSTEM.setYaopinMap(yaopinMap);
			IOsession.STORE_SYSTEM.setZbMap(zbMap);
		}
	}

	/**
	 * 装备资源初始化
	 * 
	 * @throws Exception
	 */
	private static void initZb() throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\zb.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Zb zb = new Zb();
			zb.setId(Integer.valueOf(e.elementText("id")));
			zb.setLevel(Integer.valueOf(e.elementText("level")));
			zb.setType(Integer.valueOf(e.elementText("type")));
			zb.setName(e.elementText("name"));
			zb.setAck(Integer.valueOf(e.elementText("attribute")));
			zb.setPrice(Integer.valueOf(e.elementText("price")));
			zb.setNjd(Integer.valueOf(e.elementText("njd")));
			IOsession.zbMp.put(Integer.valueOf(e.elementText("id")), zb);
		}
	}

	/**
	 * 初始化buff
	 * 
	 * @throws Exception
	 */
	private static void initBuff() throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\buff.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Buff buff = new Buff();
			buff.setId(Integer.valueOf(e.elementText("id")));
			buff.setName(e.elementText("name"));
			buff.setMp(Integer.valueOf(e.elementText("mp")));
			buff.setLastedTime(Long.valueOf(e.elementText("lastedTime")));
			IOsession.buffMp.put(Integer.valueOf(e.elementText("id")), buff);
		}
	}

	/**
	 * 初始化药品
	 * 
	 * @throws Exception
	 */
	private static void initYaopin() throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\yaopin.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Yaopin yaopin = new Yaopin();
			yaopin.setName(e.elementText("name"));
			yaopin.setId(Integer.valueOf(e.elementText("id")));
			yaopin.setBuff(Integer.valueOf(e.elementText("buff")));
			yaopin.setPrice(Integer.valueOf(e.elementText("price")));
			IOsession.yaopinMp.put(Integer.valueOf(e.elementText("id")), yaopin);
		}
	}

	/**
	 * 初始化技能
	 * 
	 * @throws Exception
	 */
	private static void initSkill() throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\skill.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Skill skill = new Skill();
			skill.setId(Integer.valueOf(e.elementText("id")));
			skill.setName(e.elementText("name"));
			skill.setCd(Integer.valueOf(e.elementText("cd")));
			skill.setMp(Integer.valueOf(e.elementText("mp")));
			skill.setHurt(Integer.valueOf(e.elementText("hurt")));
			skill.setEffect(e.elementText("effect"));
			SkillList.mp.put(e.elementText("id"), skill);
		}
	}

	/**
	 * 初始化怪物
	 * 
	 * @param monsterList
	 * @throws Exception
	 */
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
			monster.setId(Integer.valueOf(e.elementText("id")));
			monster.setExp(Integer.valueOf(e.elementText("exp")));
			monster.setMoney(Integer.valueOf(e.elementText("money")));
			String[] split = e.elementText("award").split(",");
			ArrayList<Integer> awardList = new ArrayList<>();
			for (String awardId : split) {
				Integer id = Integer.valueOf(awardId);
				awardList.add(id);
			}
			monster.setAwardList(awardList);
			IOsession.moster.put(Integer.valueOf(e.elementText("id")), monster);
			Monster monster1 = (Monster) monster.clone();
			monsterList.add(monster1);
		}
	}

	/**
	 * 初始化npc
	 * 
	 * @param npcList
	 * @throws Exception
	 */
	public static void initNpc(ArrayList<Npc> npcList) throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\npc.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Npc npc = new Npc();
			npc.setId(Integer.valueOf(e.elementText("id")));
			npc.setName(e.elementText("name"));
			npc.setMsg(e.elementText("msg"));
			npcList.add(npc);
		}
	}

	/**
	 * 初始化场景
	 * 
	 * @param monsterList
	 * @param npcList
	 * @throws Exception
	 */
	public static void initScene(ArrayList<Monster> monsterList, ArrayList<Npc> npcList) throws Exception {
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\Scene.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
//		List<Scene> sceneList = new ArrayList();
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
			String[] near = e.elementText("near").split(",");
			for (String near1 : near) {
				Area.mp2[Integer.valueOf(e.elementText("id"))][Integer.valueOf(near1)] = 1;
			}
			//
			Area.mp1.put(e.elementText("name"), Integer.valueOf(e.elementText("id")));
			Area.sceneList.add(scene);
		}
	}

//	public static void init() {
//		Npc Zhnpc = new Npc();
//		Npc Wqnpc = new Npc();
//		Zhnpc.setName("杂货老板");
//		Zhnpc.setMsg("hey,小伙子,欢迎杂货店");
//		Wqnpc.setName("武器老板");
//		Wqnpc.setMsg("欢迎光临武器店,十八般武器任你挑");
//
//		Monster SLMmonster = new Monster();
//		SLMmonster.setName("史莱姆");
//		SLMmonster.setAliveFlag(true);
//		SLMmonster.setHp(5000);
//		SLMmonster.setAck(1);
//		Monster MDSmonster = new Monster();
//		MDSmonster.setName("美杜莎");
//		MDSmonster.setAliveFlag(true);
//		MDSmonster.setHp(200);
//		MDSmonster.setAck(15);
//
//		Scene born = new Scene();
//		born.setId(1);
//		born.setName("起始之地");
//		LinkedList<Npc> list1 = new LinkedList<Npc>();
//		list1.add(Wqnpc);
//		list1.add(Zhnpc);
//		born.setNpcList(list1);
//		LinkedList<Monster> list2 = new LinkedList<Monster>();
//		list2.add(SLMmonster);
//		born.setMonsterList(list2);
//		Area.sceneList.add(born);
//
//		Scene village = new Scene();
//		village.setId(2);
//		village.setName("村庄");
//		LinkedList<Npc> list3 = new LinkedList<Npc>();
//		list3.add(Zhnpc);
//		village.setNpcList(list3);
//		village.setMonsterList(list2);
//		Area.sceneList.add(village);
//
//		Scene senlin = new Scene();
//		senlin.setId(3);
//		senlin.setName("森林");
//		senlin.setNpcList(list1);
//		senlin.setMonsterList(list2);
//		Area.sceneList.add(senlin);
//
//		Scene chenBao = new Scene();
//		chenBao.setId(4);
//		chenBao.setName("城堡");
//		chenBao.setNpcList(list1);
//		chenBao.setMonsterList(list2);
//		Area.sceneList.add(chenBao);
//
//		Skill skill = new Skill();
//		skill.setId(1);
//		skill.setName("毒雷引爆");
//		skill.setCd(3000);
//		skill.setMp(10);
//		skill.setHurt(5);
//		skill.setEffect("中毒");
//		Skill sl = new Skill();
//		sl.setId(2);
//		sl.setName("神罗天罡");
//		sl.setCd(5000);
//		sl.setMp(20);
//		sl.setHurt(60);
//		sl.setEffect("护盾");
//		SkillList.mp.put("1", skill);
//		SkillList.mp.put("3", sl);
//	}
}