package rpg.pojo;

import java.util.List;

/**队伍类
 * @author ljq
 *
 */
public class Group {
	private String id;
	private User user;
	private List<User> list;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return the list
	 */
	public List<User> getList() {
		return list;
	}
	/**
	 * @param list the list to set
	 */
	public void setList(List<User> list) {
		this.list = list;
	}
	
}
