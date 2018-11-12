package rpg.session;

import java.net.SocketAddress;
import java.util.HashMap;

import rpg.pojo.Monster;
import rpg.pojo.User;

/**
 * 登陆状态session
 * @author ljq
 *
 */
public class IOsession {
	//用户信息session
	public static HashMap<SocketAddress, User> mp = new HashMap<SocketAddress, User>();
	//战斗状态
	public static HashMap<SocketAddress, Boolean> ackStatus = new HashMap<SocketAddress, Boolean>();
	//攻击的怪物
	public static HashMap<SocketAddress, Monster> monsterMp = new HashMap<SocketAddress, Monster>();
}
