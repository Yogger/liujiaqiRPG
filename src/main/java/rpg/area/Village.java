package rpg.area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.pojo.Master;
import rpg.pojo.Npc;

/**
 * 村庄
 * 
 * @author ljq
 *
 */
@Component("village")
public class Village {
	public static final int ID = 2;
	public String name = "村庄";
	@Autowired
	private Npc npc;
	@Autowired
	private Master master;

	@Override
	public String toString() {
		return "village [name=" + name + ", npc=" + npc.toString() + ", master=" + master.toString() + "]";
	}
}
