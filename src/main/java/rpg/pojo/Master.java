package rpg.pojo;

import org.springframework.stereotype.Component;

@Component("master")
public class Master {
	private boolean deadFlag=true;
	private String name="三狼";
	@Override
	public String toString() {
		return "Master [deadFlag=" + deadFlag + ", name=" + name + "]";
	}
	public boolean isDeadFlag() {
		return deadFlag;
	}
	public void setDeadFlag(boolean deadFlag) {
		this.deadFlag = deadFlag;
	}
}