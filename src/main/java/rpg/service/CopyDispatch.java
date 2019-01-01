package rpg.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.pojo.BossScene;
import rpg.pojo.Group;
import rpg.pojo.Monster;
import rpg.pojo.User;
import rpg.session.IOsession;
import rpg.util.SendMsg;

/**
 * Boss副本
 * 
 * @author ljq
 *
 */

@Component
public class CopyDispatch {
	public void copy(User user, Channel ch, ChannelGroup group, String msgR) throws DocumentException {
		BossScene scene = new BossScene();
		// 解析bossScene.xml文件，创建副本
		SAXReader sr1 = new SAXReader();
		Document document1 = sr1.read(new File("src\\main\\java\\rpg.conf\\bossScene.xml"));
		Element root1 = document1.getRootElement();
		List<Element> elementList1 = root1.elements();
		for (Element e : elementList1) {
			scene.setSceneid(Integer.valueOf(e.elementText("sceneid")));
			scene.setName(e.elementText("name"));
			scene.setLastedTime(Integer.valueOf(e.elementText("lastedTime")));
			scene.setLayer(Integer.valueOf(e.elementText("layer")));
			String[] split = e.elementText("bossid").split(",");
			ArrayList<Integer> bossidList = new ArrayList<>();
			for (String bossid : split) {
				bossidList.add(Integer.valueOf(bossid));
			}
			scene.setBossid(bossidList);
			HashMap<Integer, Integer> hashMap = new HashMap<>();
			String[] split2 = e.elementText("struct").split(",");
			for (String string : split2) {
				String[] split3 = string.split(":");
				hashMap.put(Integer.valueOf(split3[0]), Integer.valueOf(split3[1]));
			}
			scene.setStruct(hashMap);
		}
		// scene.setSceneid(5001);
		// scene.setName("噩梦之地");
		scene.setGroupId(user.getGroupId());
		scene.setId(0);
		// scene.setLastedTime(600000);
		List<Integer> bossid = scene.getBossid();
		ArrayList<Monster> monsterList = new ArrayList<>();
		SAXReader sr = new SAXReader();
		Document document = sr.read(new File("src\\main\\java\\rpg.conf\\boss.xml"));
		Element root = document.getRootElement();
		List<Element> elementList = root.elements();
		for (Element e : elementList) {
			Monster monster = new Monster();
			monster.setId(Integer.valueOf(e.elementText("id")));
			monster.setName(e.elementText("name"));
			monster.setAliveFlag(true);
			monster.setHp(Integer.valueOf(e.elementText("hp")));
			monster.setAck(Integer.valueOf(e.elementText("ack")));
			monster.setCountAcker(0);
			monster.setMoney(Integer.valueOf(e.elementText("money")));
			String[] split = e.elementText("award").split(",");
			ArrayList<Integer> awardList = new ArrayList<>();
			for (String awardId : split) {
				Integer id = Integer.valueOf(awardId);
				awardList.add(id);
			}
			monster.setAwardList(awardList);
			String[] split1 = e.elementText("skill").split(",");
			ArrayList<Integer> skillList = new ArrayList<>();
			for (String skillId : split1) {
				Integer id = Integer.valueOf(skillId);
				skillList.add(id);
			}
			monster.setSkillList(skillList);
			if (bossid.contains(monster.getId())) {
				monsterList.add(monster);
			}
		}
		scene.setMonsterList(monsterList);
		IOsession.userBossMp.put(user.getGroupId(), scene);
		IOsession.ackStatus.put(ch.remoteAddress(), 2);
		SendMsg.send("进入噩梦之地，Boss:" + "名字：" + monsterList.get(0).getName() + "-血量:" + monsterList.get(0).getHp()
				+ "-攻击力:" + monsterList.get(0).getAck(), ch);
		Group group2 = IOsession.userGroupMp.get(user.getGroupId());
		if (group2 != null) {
			List<User> list = group2.getList();
			for (User user2 : list) {
				if (user2 != group2.getUser()) {
					Channel channel = IOsession.userchMp.get(user2);
					IOsession.ackStatus.put(channel.remoteAddress(), 2);
					SendMsg.send("进入噩梦之地，Boss:" + "名字：" + monsterList.get(0).getName() + "-血量:"
							+ monsterList.get(0).getHp() + "-攻击力:" + monsterList.get(0).getAck(), channel);
				}
			}
		}
	}
}
