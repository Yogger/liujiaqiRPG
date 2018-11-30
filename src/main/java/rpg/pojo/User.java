package rpg.pojo;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class User {
	private Integer id;

	private String nickname;

	private Integer areaid;

	private String updatetime;

	private volatile int hp;

	private volatile int mp;

	private volatile int money;

	private String groupId;
	
	private int liveFlag;//1退出，0正常

	public int getLiveFlag() {
		return liveFlag;
	}

	public void setLiveFlag(int liveFlag) {
		this.liveFlag = liveFlag;
	}

	private static AtomicIntegerFieldUpdater<User> hpUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class, "hp");
	private static AtomicIntegerFieldUpdater<User> mpUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class, "mp");
	private static AtomicIntegerFieldUpdater<User> moneyUpdater = AtomicIntegerFieldUpdater.newUpdater(User.class,
			"money");

	public void getAndSetHp(User user, int num) {
		hpUpdater.getAndSet(user, num);
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
}