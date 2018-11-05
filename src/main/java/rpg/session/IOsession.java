package rpg.session;

import java.net.SocketAddress;
import java.util.HashMap;

import rpg.pojo.User;

/**
 * 登陆状态session
 * @author ljq
 *
 */
public class IOsession {
	public static HashMap<SocketAddress, User> mp = new HashMap<SocketAddress, User>();
}
