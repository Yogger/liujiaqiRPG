package rpg.area;

import io.netty.channel.Channel;
import rpg.pojo.BossScene;
import rpg.pojo.User;
import rpg.session.IOsession;
import rpg.skill.SkillList;

public class offineFlagRefresh implements Runnable {
	
	private User user;
	private Channel ch;
	private BossScene bossScene;
	
	
	public offineFlagRefresh(User user, Channel ch, BossScene bossScene) {
		super();
		this.user = user;
		this.ch = ch;
		this.bossScene = bossScene;
	}


	@Override
	public void run() {
		while(true) {
			try {
//				Thread.sleep(bossScene.getLastedTime());
				Thread.sleep(2000);
				IOsession.mp.remove(ch.remoteAddress());
				IOsession.userchMp.remove(user);
				IOsession.userZbMp.remove(user);
//				IOsession.userGroupMp.remove(user.getGroupId());
				SkillList.cdMp.remove(user);
				IOsession.buffTimeMp.remove(user);
				IOsession.attMp.remove(user);
				IOsession.userBagMp.remove(user);
				IOsession.nameMap.remove(user.getNickname());
				user=null;
				break;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
