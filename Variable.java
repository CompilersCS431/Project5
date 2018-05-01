package ProjFive;

public class Variable {
	private String name;
	private String type;
	private String value;
        private int index ;

	public Variable(String name, String type) {
            this.name = name;
            this.type = type;
            value = null;
            index = -1 ;
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
        
        public void setIndex(int i)
        {
            index = i ;
        }
        
        public int getIndex()
        {
            return index ;
        }

}
