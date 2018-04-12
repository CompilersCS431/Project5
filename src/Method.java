package ProjFive;

import java.util.*;

public class Method {
	private String name;
	private String returnType;
	//hash table of local variables 
	private HashMap<String, Variable> localVarTable;
	//HashMap for parameters
	private HashMap<String, Variable> parameters;

	public Method(String name, String returnType) {
		this.name = name;
		this.returnType = returnType;	
		localVarTable = new HashMap();
		parameters = new HashMap();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return returnType;
	}

	public void addParam(Variable v) {
		if(!parameters.containsKey(v.getName())) {
			parameters.put(v.getName(), v);
		}
		else {
			//do something if the parameter was already defined
		}
	}

	public void addVar(Variable v) {
		if(!localVarTable.containsKey(v.getName())) {
			localVarTable.put(v.getName(), v);
		}
		else {
			//do something if the var was already defined
		}
	}

	public boolean containsParam(String id) {
		return parameters.containsKey(id);
	}

	public boolean containsVar(String id) {
		return localVarTable.containsKey(id);
	}

	public HashMap getParams() {
		return parameters;
	}

	public Variable getParam(String id) {
		if(parameters.containsKey(id)) {
			return parameters.get(id);
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

}
