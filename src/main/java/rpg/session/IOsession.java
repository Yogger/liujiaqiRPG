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
	public static HashMap<SocketAddress, User> mp = new HashMap<SocketAddress, User>();
	public static HashMap<SocketAddress, Boolean> ackStatus = new HashMap<SocketAddress, Boolean>();
	public static HashMap<SocketAddress, Monster> monsterMp = new HashMap<SocketAddress, Monster>();
}
