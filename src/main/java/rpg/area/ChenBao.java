package rpg.area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rpg.pojo.Master;
import rpg.pojo.Npc;

/**
 * 城堡
 * 
 * @author ljq
 *
 */
@Component("chenbao")
public class ChenBao {
	public static final int ID = 4;
	public String name = "城堡";
	@Autowired
	private Npc npc;
	@Autowired
	private Master master;

	@Override
	public String toString() {
		return "chenbao [name=" + name + ", npc=" + npc.toString() + ", master=" + master.toString() + "]";
	}
}
