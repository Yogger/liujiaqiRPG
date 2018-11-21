package rpg.pojo;

import java.util.List;

import javax.lang.model.type.PrimitiveType;

import org.springframework.stereotype.Component;
/**
 * 怪物
 * @author ljq
 *
 */
@Component("monster")
public class Monster {
	private String name;
	private boolean aliveFlag;
	private int hp;
	private int ack;
	private int countAcker;
	private List<User> userList;
	private List<Integer> awardList;
	private int money;

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public List<Integer> getAwardList() {
		return awardList;
	}

	public void setAwardList(List<Integer> awardList) {
		this.awardList = awardList;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

	public int getCountAcker() {
		return countAcker;
	}

	public void setCountAcker(int countAcker) {
		this.countAcker = countAcker;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAliveFlag() {
		return aliveFlag;
	}

	public void setAliveFlag(boolean aliveFlag) {
		this.aliveFlag = aliveFlag;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}
	
	public int getAck() {
		return ack;
	}
	public void setAck(int ack) {
		this.ack = ack;
	}
	
	@Override
	public String toString() {
		return "Monster [" + name + "-存活状态=" + aliveFlag + "-血量=" + hp + "-攻击力=" + ack + "]";
	}
}