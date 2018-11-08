package rpg.pojo;

import org.springframework.stereotype.Component;

@Component("npc")
public class Npc {
	private String name;
	private String msg;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	@Override
	public String toString() {
		return "Npc [name=" + name + "]";
	}
	public String talk() {
		return msg;
	}
}
