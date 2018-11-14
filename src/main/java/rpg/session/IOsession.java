package rpg.session;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rpg.pojo.Buff;
import rpg.pojo.Monster;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userbag;
import rpg.pojo.Userzb;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;

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
	// 装备关系
	public static HashMap<Integer, Zb> zbMp = new HashMap<Integer, Zb>();
	// 属性关系
	public static HashMap<User, UserAttribute> attMp = new HashMap<User, UserAttribute>();
	// 用户背包
	public static HashMap<User, List<Userbag>> userBagMp = new HashMap<User, List<Userbag>>();
	// 用户装备
	public static HashMap<User, List<Userzb>> userZbMp = new HashMap<User, List<Userzb>>();
}
