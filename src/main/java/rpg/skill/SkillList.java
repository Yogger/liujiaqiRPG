package rpg.skill;

import java.util.HashMap;

import rpg.pojo.Skill;
import rpg.pojo.User;

/**技能相关常用容器
 * @author ljq
 *
 */
public class SkillList {
	// 技能列表
	public static HashMap<String, Skill> mp = new HashMap<String, Skill>();
	// 技能cd时间
	public static HashMap<User, HashMap<String, Long>> cdMp = new HashMap<User, HashMap<String, Long>>();
}
