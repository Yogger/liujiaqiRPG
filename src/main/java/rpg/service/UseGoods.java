package rpg.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import rpg.data.dao.UserbagMapper;
import rpg.pojo.Buff;
import rpg.pojo.User;
import rpg.pojo.Userbag;
import rpg.pojo.Yaopin;
import rpg.session.IOsession;
import rpg.util.SendMsg;

/**
 * 使用物品逻辑
 * 
 * @author ljq
 *
 */
@Component
public class UseGoods {

	@Autowired
	private UserbagMapper userbagMapper;

	public void use(User user, Channel ch, ChannelGroup group, String msgR) {
		try {
			IOsession.lock.lock();
			String[] msg = msgR.split("\\s+");
			String nickname = user.getNickname();
			// UserbagExample example = new UserbagExample();
			// Criteria criteria = example.createCriteria();
			// criteria.andUsernameEqualTo(nickname);
			// List<Userbag> list = userbagMapper.selectByExample(example);
			List<Userbag> list = IOsession.userBagMp.get(user);
			for (Userbag userbag : list) {
				Yaopin yaopin = IOsession.yaopinMp.get(userbag.getGid());
				if (yaopin != null) {
					if (yaopin.getName().equals(msg[1])) {
						// 找到药品所产生的Buff
						Buff buff = IOsession.buffMp.get(yaopin.getBuff());
						// 存储上次使用buff时间
						long currentTimeMillis = System.currentTimeMillis();
						if (IOsession.buffTimeMp.get(user) == null) {
							ConcurrentHashMap<Integer, Long> buffMap = new ConcurrentHashMap<Integer, Long>();
							buffMap.put(buff.getId(), currentTimeMillis);
							IOsession.buffTimeMp.put(user, buffMap);
						} else {
							ConcurrentHashMap<Integer, Long> buffMap = IOsession.buffTimeMp.get(user);
							buffMap.put(buff.getId(), currentTimeMillis);
						}
						userbag.setNumber(userbag.getNumber() - 1);
						SendMsg.send(yaopin.getName() + "使用成功，剩余" + userbag.getNumber(), ch);
					}
				}
			}
		} finally {
			IOsession.lock.unlock();
		}
	}
}