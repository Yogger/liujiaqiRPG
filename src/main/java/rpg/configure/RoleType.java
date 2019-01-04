package rpg.configure;

/**角色类型
 * @author ljq
 *
 */
public enum RoleType {
	ZHANSHI(1),
	MUSHI(2),
	FASHI(3),
	ZHAOHUANSHI(4);
	private final int value;

	private RoleType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
}
