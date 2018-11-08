package rpg.pojo;

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