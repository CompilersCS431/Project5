package ProjFive;

public class Variable {
	private String name;
	private String type;
	private String value;

	public Variable(String name, String type, String value){
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public Variable(String name, String type) {
		this.name = name;
		this.type = type;
		value = null;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

}
