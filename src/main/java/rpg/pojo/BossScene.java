package rpg.pojo;

import java.util.ArrayList;

public class BossScene {
	private int id;
	private int sceneid;
	private String name;
	private String groupId;
	private ArrayList<Monster> monsterList;
	private long startTime;
	private long lastedTime;
	
	/**
	 * @return the sceneid
	 */
	public int getSceneid() {
		return sceneid;
	}
	/**
	 * @param sceneid the sceneid to set
	 */
	public void setSceneid(int sceneid) {
		this.sceneid = sceneid;
	}
	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the lastedTime
	 */
	public long getLastedTime() {
		return lastedTime;
	}
	/**
	 * @param lastedTime the lastedTime to set
	 */
	public void setLastedTime(long lastedTime) {
		this.lastedTime = lastedTime;
	}
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
