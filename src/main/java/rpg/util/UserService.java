package rpg.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.data.dao.UserskillMapper;
import rpg.pojo.Buff;
import rpg.pojo.Monster;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userskill;
import rpg.pojo.UserskillExample;
import rpg.pojo.UserskillExample.Criteria;
import rpg.pojo.Userzb;
import rpg.pojo.Zb;
import rpg.session.IOsession;
import rpg.skill.SkillList;

/**用户服务
 * @author ljq
 *
 */
@Component
public class UserService {

	@Autowired
	private UserskillMapper userskillMapper;

	/**
	 * 获取技能列表
	 * 
	 * @param user
	 * @return
	 */
	public List<Userskill> getSkillList(User user) {
		Integer id = user.getAreaid();
		String nickname = user.getNickname();
		UserskillExample example = new UserskillExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(nickname);
		List<Userskill> list = userskillMapper.selectByExample(example);
		return list;
	}

	/**
	 * cd状态，0-技能未被使用过，1-技能cd已到，2-技能cd未到。
	 * 
	 * @param user
	 * @param skillId
	 * @param skill
	 * @return
	 */
	public int cdStatus(User user, String skillId, Skill skill) {
		long millis = System.currentTimeMillis();// 获取当前时间毫秒值
		HashMap<String, Long> map = SkillList.cdMp.get(user);
		if (map != null) {
			Long lastmillis = map.get(skillId);
			if (lastmillis != null) {
				if (millis - lastmillis >= skill.getCd()) {
					return 1;
				} else {
					return 2;
				}
			} else {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * 存储上次使用技能时间
	 * 
	 * @param user
	 * @param skillId
	 */
	public void saveLastCdTime(User user, String skillId) {
		long currentTimeMillis = System.currentTimeMillis();
		HashMap<String, Long> curSkill = new HashMap<String, Long>(500);
		curSkill.put(skillId, currentTimeMillis);
		SkillList.cdMp.put(user, curSkill);
	}

	/**
	 * 检查耐久度，并重新计算攻击力
	 * 
	 * @param user
	 * @param attribute
	 */
	public void checkNjd(User user, UserAttribute attribute) {
		List<Userzb> list1 = IOsession.userZbMp.get(user);
		for (Userzb userzb : list1) {
			if (userzb.getNjd() <= 0) {
				Zb zb = IOsession.zbMp.get(userzb.getZbid());
				if (zb != null && attribute != null) {
					attribute.setAck(attribute.getAck() - zb.getAck() * userzb.getIsuse());
					userzb.setIsuse(0);
				}
			}
		}
	}

	/**
	 * 更新人物buff
	 * 
	 * @param user
	 * @param skill
	 */
	public void updateUserBuff(User user, Skill skill) {
		// 找到所产生的Buff
		Buff buff = IOsession.buffMp.get(Integer.valueOf(skill.getEffect()));
		// 存储上次使用buff时间
		if (buff != null) {
			long currentTimeMillis = System.currentTimeMillis();
			if (IOsession.buffTimeMp.get(user) == null) {
				ConcurrentHashMap<Integer, Long> buffMap = new ConcurrentHashMap<Integer, Long>(500);
				buffMap.put(buff.getId(), currentTimeMillis);
				IOsession.buffTimeMp.put(user, buffMap);
			} else {
				ConcurrentHashMap<Integer, Long> buffMap = IOsession.buffTimeMp.get(user);
				buffMap.put(buff.getId(), currentTimeMillis);
			}
		}
	}

	/**
	 * 更新怪物buff
	 * 
	 * @param user
	 * @param skill
	 * @param monster
	 */
	public void updateMonsterBuff(User user, Skill skill, Monster monster) {
		// 找到所产生的Buff
		Buff buff = IOsession.buffMp.get(Integer.valueOf(skill.getEffect()));
		// 存储上次使用buff时间
		if (buff != null) {
			long currentTimeMillis = System.currentTimeMillis();
			if (IOsession.monsterBuffTimeMp.get(monster) == null) {
				HashMap<Integer, Long> buffMap = new HashMap<Integer, Long>(500);
				buffMap.put(buff.getId(), currentTimeMillis);
				IOsession.monsterBuffTimeMp.put(monster, buffMap);
			} else {
				HashMap<Integer, Long> buffMap = IOsession.monsterBuffTimeMp.get(monster);
				buffMap.put(buff.getId(), currentTimeMillis);
			}
		}
	}

	/**
	 * 检查怪物Buff
	 * 
	 * @param monster
	 * @param ch
	 */
	public String checkMonsterBuff(Monster monster, Channel ch) {
		HashMap<Integer, Long> buffTime = IOsession.monsterBuffTimeMp.get(monster);
		String msg = "";
		if (buffTime != null) {
			for (Entry<Integer, Long> entry : buffTime.entrySet()) {
				// 通过buffID找到具体的buff
				Integer buffId = entry.getKey();
				Buff buff = IOsession.buffMp.get(buffId);
				// 获取使用Buff的时间
				Long lastTime = entry.getValue();
				long currentTimeMillis = System.currentTimeMillis();
				if (currentTimeMillis - lastTime < buff.getLastedTime()) {
					switch (buff.getId()) {
					case 2:
						int monHp = monster.getHp() - buff.getMp();
						if (monHp <= 0) {
							monHp = 0;
							monster.setDeadType(1);
						}
						monster.setHp(monHp);
						if (msg == "") {
							msg += "003";
						}
						msg += monster.getName() + "受到" + buff.getName() + "伤害:" + buff.getMp() + "怪物血量剩余"
								+ monster.getHp();
						break;
					case 5:
						int monHp1 = monster.getHp() - buff.getMp();
						if (monHp1 <= 0) {
							monHp1 = 0;
							monster.setDeadType(1);
						}
						monster.setHp(monHp1);
						if (msg == "") {
							msg += "003";
						}
						msg += monster.getName() + "受到" + buff.getName() + "伤害:" + buff.getMp() + "怪物血量剩余"
								+ monster.getHp();
						break;
					default:
						break;
					}
				}
			}
			return msg;
		}
		return msg;
	}
}
