package rpg.util;

import java.io.ObjectInputStream.GetField;
import java.util.HashMap;
import java.util.List;

import javax.xml.crypto.dsig.keyinfo.KeyInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.data.dao.UserskillMapper;
import rpg.pojo.Skill;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userskill;
import rpg.pojo.UserskillExample;
import rpg.pojo.Userzb;
import rpg.pojo.Zb;
import rpg.pojo.UserskillExample.Criteria;
import rpg.session.IOsession;
import rpg.skill.SkillList;

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
		HashMap<String, Long> curSkill = new HashMap<String, Long>();
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
}
