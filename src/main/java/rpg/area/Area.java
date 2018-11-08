package rpg.area;

import java.util.HashMap;
import java.util.LinkedList;

import rpg.pojo.Monster;
import rpg.pojo.Npc;

/**
 * 地图类
 * 
 * @return
 */
public class Area {
	public static LinkedList<Npc> npcList =new LinkedList<Npc>();
	public static LinkedList<Scene> sceneList =new LinkedList<Scene>();
	private static HashMap<String, Integer> mp1 = new HashMap<String, Integer>();
	static int[][] mp2 = new int[30][30];
	static {
		mp1.put("起始之地", 1);
		mp1.put("村子", 2);
		mp1.put("森林", 3);
		mp1.put("城堡", 4);
		mp2[1][2]=1;
		mp2[2][1]=1;
		mp2[2][3]=1;
		mp2[2][4]=1;
		mp2[3][2]=1;
		mp2[4][2]=1;
	}

	public static int checkArea(String msg, int idfrom) {
		Integer id = mp1.get(msg);
		if(mp2[idfrom][id]==1) {
			return id;
		} else {
			return 0;
		}
	}
}