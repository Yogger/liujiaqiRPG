package rpg.configure;

/**消息大小
 * @author ljq
 *
 */
public enum MsgSize {
	MAX_MSG_SIZE_1(1), 
	MAX_MSG_SIZE_2(2), 
	MAX_MSG_SIZE_3(3),
	MAX_MSG_SIZE_4(4), 
	MAX_MSG_SIZE_5(5), 
	MSG_INDEX_1(1),
	MSG_INDEX_2(2), 
	MSG_INDEX_3(3),
	MSG_INDEX_4(4), 
	MSG_INDEX_5(5);
	private final int value;

	private MsgSize(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
