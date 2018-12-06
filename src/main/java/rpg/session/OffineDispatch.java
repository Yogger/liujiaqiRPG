package rpg.session;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import rpg.area.SceneBossRefresh;
import rpg.area.offineFlagRefresh;
import rpg.pojo.BossScene;
import rpg.pojo.Group;
import rpg.pojo.Monster;
import rpg.pojo.User;
import rpg.skill.SkillList;

@Component("offineDispatch")
public class OffineDispatch {
	
	public void groupOffine(Channel ch) {
		User user = IOsession.mp.get(ch.remoteAddress());
		if(user!=null) {
		user.setLiveFlag(1);
		Group group = IOsession.userGroupMp.get(user.getGroupId());
		if(group!=null) {
			//回收战斗状态
//			Integer integer = IOsession.ackStatus.get(ch.remoteAddress());
//			if(integer!=null) {
//				integer=null;
//				IOsession.ackStatus.remove(ch.remoteAddress());
//			}
			//如果是队长
			if(group.getUser().getNickname().equals(user.getNickname())) {
				List<User> list = group.getList();
				if(list.size()==1) {
					System.out.println("移除前"+list.size());
					group.setUser(null);
					list.remove(user);
					System.out.println("移除后"+list.size());
				} else {
				for (User user2 : list) {
					if(user2!=user) {
						group.setUser(user2);
						list.remove(user);
//						user.setGroupId(null);
						for (User user3 : list) {
							Channel channel = IOsession.userchMp.get(user3);
							channel.writeAndFlush(user2.getNickname()+"成为队长");
						}
						System.out.println("执行");
						break;
					}
				}
			}
			}
			//不是队长
			else {
				List<User> list = group.getList();
				list.remove(user);
//				user.setGroupId(null);
				for (User user3 : list) {
					if(user3!=user) {
					Channel channel = IOsession.userchMp.get(user3);
					channel.writeAndFlush(user.getNickname()+"离开队伍");
					}
				}
			}
			//移除怪物攻击目标
			// 获取地图中的怪物
			BossScene bossScene = IOsession.userBossMp.get(user.getGroupId());
			if(bossScene!=null) {
			ArrayList<Monster> monsterList = bossScene.getMonsterList();
			// 找到场景内怪物
			for (int i=0;i<monsterList.size();i++) {
				Monster monster = monsterList.get(i);
				if(monster!=null) {
					List<User> userList = monster.getUserList();
					userList.remove(user);
				}
			}
			IOsession.monsterThreadPool.execute(new offineFlagRefresh(user, ch, bossScene));
			} else {
				IOsession.mp.remove(ch.remoteAddress());
				IOsession.userchMp.remove(user);
				IOsession.userZbMp.remove(user);
				SkillList.cdMp.remove(user);
				IOsession.buffTimeMp.remove(user);
				IOsession.attMp.remove(user);
				IOsession.userBagMp.remove(user);
				IOsession.nameMap.remove(user.getNickname());
				user=null;
			}
		} else {
			IOsession.mp.remove(ch.remoteAddress());
			IOsession.userchMp.remove(user);
			IOsession.userZbMp.remove(user);
			SkillList.cdMp.remove(user);
			IOsession.buffTimeMp.remove(user);
			IOsession.attMp.remove(user);
			IOsession.userBagMp.remove(user);
			IOsession.nameMap.remove(user.getNickname());
			user=null;
		}
//		user=null;
//		IOsession.mp.remove(ch.remoteAddress());
		}
		}
}