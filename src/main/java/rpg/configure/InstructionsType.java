package rpg.configure;

/**指令类型
 * @author ljq
 *
 */
public enum InstructionsType {
	CHANGE_ACK_TARGET("a"),
	ESC("esc"),
	ACK("ack"),
	SKILL_KEY_1("1"),
	SKILL_KEY_3("3"),
	ADD("add"),
	SHOWSQ("showsq"),
	SHOW("show"),
	ACCEPT("accept"),
	CREAT("creat"),
	JOIN("join"),
	SHOWUSER("showuser"),
	JS("js"),
	RAISE("raise"),
	DOWN("down"),
	PUT("put"),
	SHOWSTORE("showstore"),
	TAKE("take"),
	T("t"),
	YES("yes"),
	NO("no"),
	DIV("div"),
	Y("y"),
	BUY("buy"),
	SELL("sell"),
	SHOWD("showd"),
	SHOWF("showf");
	
	private String value;

	private InstructionsType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}
