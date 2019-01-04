package rpg.util;

/**
 * 消息类型枚举
 * 
 * @author ljq
 *
 */
public enum MsgType {
	HEART_BEAT("心跳"),
	USER_BUFF_MSG("001"),
	MONSTER_ACK_MSG("002"),
	MONSTER_BUFF_MSG("003"),
	LOGIN_SUCCES_MSG("000");
	private String value;

	private MsgType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
