package rpg.pojo;

import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**用户类
 * @author ljq
 *
 */
public class User {
	private Integer id;

	private String nickname;

	private Integer areaid;

	private String updatetime;

	private volatile int hp;

	private volatile int mp;

	private volatile int money;

	private String groupId;
	
	private Integer ghid;
	
	private Integer roletype;
	/**
	 * 1退出，0正常
	 */
	private int liveFlag;
	/**
	 * 0正常 1交易状态 2交易确认状态
	 */
	private volatile int jyFlag;
	/**
	 * 0正常 1交易发出
	 */
	private volatile int jySendFlag;
	
	private String jyId;
	
	private Map<Integer, TaskProcess> doingTask;
	
	private Map<Integer, TaskProcess> finishTask;
	
	private int level;
	private int exp;

	private static AtomicIntegerFieldUpdater<User> hpUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class, "hp");
	private static AtomicIntegerFieldUpdater<User> mpUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class, "mp");
	private static AtomicIntegerFieldUpdater<User> moneyUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class,
			"money");
	private static AtomicIntegerFieldUpdater<User> jyFlagUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class,
			"jyFlag");
	private static AtomicIntegerFieldUpdater<User> jySendFlagUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class,
			"jySendFlag");
	
	public int getJySendFlag() {
		return jySendFlag;
	}
	
	public void getAndSetjySendFlag(User user, int num) {
		jySendFlagUpdater.getAndSet(user, num);
	}
	
	public int getJyFlag() {
		return jyFlag;
	}

	public void getAndSetjyFlag(User user, int num) {
		jyFlagUpdater.getAndSet(user, num);
	}

	public int getLiveFlag() {
		return liveFlag;
	}

	public void setLiveFlag(int liveFlag) {
		this.liveFlag = liveFlag;
	}

	public void getAndSetHp(User user, int num) {
		hpUpdater.getAndSet(user, num);
	}
	
	public void getAndAddHp(User user, int num) {
		hpUpdater.getAndAdd(user, num);
	}

	public void getAndSetMp(User user, int num) {
		mpUpdater.getAndSet(user, num);
	}

	public void getAndSetMoney(User user, int num) {
		moneyUpdater.getAndSet(user, num);
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname == null ? null : nickname.trim();
	}

	public Integer getAreaid() {
		return areaid;
	}

	public void setAreaid(Integer areaid) {
		this.areaid = areaid;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime == null ? null : updatetime.trim();
	}

	public Integer getHp() {
		return hpUpdater.get(this);
	}

	public void setHp(Integer hp) {
		this.hp = hp;
	}

	public Integer getMp() {
		return mpUpdater.get(this);
	}

	public void setMp(Integer mp) {
		this.mp = mp;
	}

	public Integer getMoney() {
		return moneyUpdater.get(this);
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

	public String getJyId() {
		return jyId;
	}
	
	public void setJyId(String jyId) {
		this.jyId = jyId;
	}

	public int getGhId() {
		return ghid;
	}

	public void setGhId(int ghid) {
		this.ghid = ghid;
	}

	public Map<Integer, TaskProcess> getDoingTask() {
		return doingTask;
	}

	public void setDoingTask(Map<Integer, TaskProcess> doingTask) {
		this.doingTask = doingTask;
	}

	public Map<Integer, TaskProcess> getFinishTask() {
		return finishTask;
	}

	public void setFinishTask(Map<Integer, TaskProcess> finishTask) {
		this.finishTask = finishTask;
	}
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public Integer getRoletype() {
		return roletype;
	}

	public void setRoletype(Integer roletype) {
		this.roletype = roletype;
	}
}