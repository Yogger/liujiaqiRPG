package rpg.pojo;

/**npc类
 * @author ljq
 *
 */
public class Npc {
	private int id;
	private String name;
	private String msg;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
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
