package rpg.pojo;

import org.springframework.stereotype.Component;

@Component("monster")
public class Monster {
	private boolean aliveFlag=true;
	private String name="三狼";
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
}