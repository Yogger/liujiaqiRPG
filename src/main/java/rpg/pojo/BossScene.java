package rpg.pojo;

import java.util.ArrayList;

public class BossScene {
	private int id;
	private String name;
	private String groupId;
	private ArrayList<Monster> monsterList;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return the monsterList
	 */
	public ArrayList<Monster> getMonsterList() {
		return monsterList;
	}
	/**
	 * @param monsterList the monsterList to set
	 */
	public void setMonsterList(ArrayList<Monster> monsterList) {
		this.monsterList = monsterList;
	}
	
}
