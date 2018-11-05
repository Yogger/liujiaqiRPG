package rpg.area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.pojo.Master;
import rpg.pojo.Npc;

/**
 * 森林
 * 
 * @author ljq
 *
 */
@Component("senlin")
public class Senlin {
	public static final int ID = 3;
	public String name = "森林";
	@Autowired
	private Npc npc;
	@Autowired
	private Master master;

	@Override
	public String toString() {
		return "senlin [name=" + name + ", npc=" + npc.toString() + ", master=" + master.toString() + "]";
	}
}
