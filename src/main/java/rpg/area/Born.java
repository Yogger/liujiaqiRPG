package rpg.area;

import javax.swing.text.MaskFormatter;

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
@Component("born")
public class Born {
	public static final int ID = 1;
	public String name = "出生地";
	@Autowired
	private Npc npc;
	@Autowired
	private Master master;

	@Override
	public String toString() {
		return "Born [name=" + name + ", npc=" + npc.toString() + ", master=" + master.toString() + "]";
	}

}
