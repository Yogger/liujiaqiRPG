package rpg.session;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rpg.pojo.Buff;
import rpg.pojo.Monster;
import rpg.pojo.User;
import rpg.pojo.Yaopin;

/**
 * 登陆状态session
 * 
 * @author ljq
 *
 */
public class IOsession {
	// 用户信息session
	public static HashMap<SocketAddress, User> mp = new HashMap<SocketAddress, User>();
	// 战斗状态
	public static HashMap<SocketAddress, Boolean> ackStatus = new HashMap<SocketAddress, Boolean>();
	// 攻击的怪物
	public static HashMap<SocketAddress, Monster> monsterMp = new HashMap<SocketAddress, Monster>();
	// 怪物线程池
	public static ExecutorService monsterThreadPool = Executors.newCachedThreadPool();
	// 药品关系
	public static HashMap<Integer, Yaopin> yaopinMp = new HashMap<Integer, Yaopin>();
	// buff关系
	public static HashMap<Integer, Buff> buffMp = new HashMap<Integer, Buff>();
	// buff开始时间
	public static HashMap<User, HashMap<Integer, Long>> buffTimeMp = new HashMap<User, HashMap<Integer, Long>>();
}
