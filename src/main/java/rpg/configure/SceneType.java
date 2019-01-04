package rpg.configure;

public enum SceneType {
	QI_SHI_ZHI_DI("起始之地"),
	SEN_LIN("森林"),
	CHEN_BAO("城堡"),
	CUN_ZI("村子");
	
	private String value;

	private SceneType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
}
