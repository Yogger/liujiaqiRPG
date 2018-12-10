package rpg.session;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.channel.Channel;
import rpg.pojo.BossScene;
import rpg.pojo.Buff;
import rpg.pojo.EmailRpg;
import rpg.pojo.Gh;
import rpg.pojo.Ghuser;
import rpg.pojo.Group;
import rpg.pojo.Jy;
import rpg.pojo.Monster;
import rpg.pojo.Store;
import rpg.pojo.User;
import rpg.pojo.UserAttribute;
import rpg.pojo.Userbag;
import rpg.pojo.Userzb;
import rpg.pojo.Yaopin;
import rpg.pojo.Zb;

/**
 * 系统资源
 * 
 * @author ljq
 *
 */
public class IOsession {
	// 用户信息session
	public static HashMap<SocketAddress, User> mp = new HashMap<SocketAddress, User>();
	// 用户address关系
	public static HashMap<User, Channel> userchMp = new HashMap<User, Channel>();
	// 战斗状态
	public static HashMap<SocketAddress, Integer> ackStatus = new HashMap<SocketAddress, Integer>();
	// 攻击的怪物
	public static HashMap<SocketAddress, Monster> monsterMp = new HashMap<SocketAddress, Monster>();
	// 怪物线程池
	public static ExecutorService monsterThreadPool = Executors.newCachedThreadPool();
	// 药品关系
	public static HashMap<Integer, Yaopin> yaopinMp = new HashMap<Integer, Yaopin>();
	// buff关系
	public static HashMap<Integer, Buff> buffMp = new HashMap<Integer, Buff>();
	// buff开始时间
	public static HashMap<User, ConcurrentHashMap<Integer, Long>> buffTimeMp = new HashMap<User, ConcurrentHashMap<Integer, Long>>();
	//怪物buff开始时间
	public static HashMap<Monster, HashMap<Integer, Long>> monsterBuffTimeMp = new HashMap<Monster, HashMap<Integer, Long>>();
	// 装备关系
	public static HashMap<Integer, Zb> zbMp = new HashMap<Integer, Zb>();
	// 属性关系
	public static HashMap<User, UserAttribute> attMp = new HashMap<User, UserAttribute>();
	// 用户背包
	public static HashMap<User, List<Userbag>> userBagMp = new HashMap<User, List<Userbag>>();
	// 用户装备
	public static HashMap<User, List<Userzb>> userZbMp = new HashMap<User, List<Userzb>>();
	// 存储队伍信息
	public static HashMap<String, Group> userGroupMp = new HashMap<String, Group>();
	// 存储用户副本关系
	public static HashMap<String, BossScene> userBossMp = new HashMap<String, BossScene>();
	// 商店
	public static final Store store = new Store();
	//用户邮件
	public static HashMap<String, ArrayList<EmailRpg>> alluserEmail = new HashMap<String, ArrayList<EmailRpg>>();
	//交易ID映射交易类
	public static ConcurrentHashMap<String, Jy> jyMap = new ConcurrentHashMap<String, Jy>();
	//用户名映射用户
	public static ConcurrentHashMap<String,User> nameMap = new ConcurrentHashMap<String, User>();
	//工会id-工会
	public static HashMap<Integer, Gh> ghMp = new HashMap<Integer, Gh>();
	//工会id-工会玩家
	public static HashMap<Integer, HashMap<String, Ghuser>> ghUserMp = new HashMap<Integer, HashMap<String, Ghuser>>();
	//工会申请列表
	public static HashMap<Integer, HashMap<String, Long>> ghsqMp = new HashMap<Integer, HashMap<String, Long>>();
}
