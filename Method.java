package ProjFive;

import java.util.*;

public class Method {
	private String name;
	private String returnType;
	//hash table of local variables 
	private HashMap<String, Variable> localVarTable;
	//HashMap for parameters
	private ArrayList<Variable> parameters;

	public Method(String name, String returnType) {
		this.name = name;
		this.returnType = returnType;	
		localVarTable = new HashMap();
		parameters = new ArrayList();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return returnType;
	}

	public void addParam(Variable v) {
            parameters.add(v);

	}

	public void addVar(Variable v) 
        {
            localVarTable.put(v.getName(), v);
	}

	public boolean containsParam(Variable v) {
            return parameters.contains(v);
	}

	public boolean containsVar(String id) {
		return localVarTable.containsKey(id);
	}

	public ArrayList getAllParams() {
            return parameters;
	}

	public Variable getParam(Variable v) {
            if(parameters.contains(v)) {
                int i = parameters.indexOf(v);
                return parameters.get(i) ;
            }
            else {
                //do something if the parameter does not exist
                return null;
            }
	}

	public Variable getVar(String id) {
		if(localVarTable.containsKey(id)) {
			return localVarTable.get(id);
		}
		else {
			//do something if the variable does not exist 
			return null;
		}
	}
        
        public HashMap getAllVars()
        {
            return localVarTable ;
        }

}
