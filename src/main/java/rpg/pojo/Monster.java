package rpg.pojo;

import org.springframework.stereotype.Component;
/**
 * 怪物
 * @author ljq
 *
 */
@Component("monster")
public class Monster {
	private boolean aliveFlag;
	private String name;
	@Override
	public String toString() {
		return "Master [aliveFlag=" + aliveFlag + ", name=" + name + "]";
	}
	public boolean isDeadFlag() {
		return aliveFlag;
	}
	public void setDeadFlag(boolean aliveFlag) {
		this.aliveFlag = aliveFlag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}