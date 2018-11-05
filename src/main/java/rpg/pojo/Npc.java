package rpg.pojo;

import org.springframework.stereotype.Component;

@Component("npc")
public class Npc {
	public  String name="杂货老板";
	@Override
	public String toString() {
		return "Npc [name=" + name + "]";
	}
}
