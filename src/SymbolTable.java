package ProjFive;

import java.util.*;

public class SymbolTable {

	//hash table for methods
	private HashMap<String, Method> methodsTable;
	//hash table for global vars
	private HashMap<String, Variable> globalVarTable; 

	//constructor
	public SymbolTable() {
		globalVarTable = new HashMap();
		methodsTable = new HashMap();
	}

	public boolean addMethod(Method m){
		if(!methodsTable.containsKey(m.getName())) {
			methodsTable.put(m.getName(), m);
			return true;
		}
		else {
			//do something here if the method has already been declared
			return false;
		}
	}

	public boolean containsMethod(String id){
		return methodsTable.containsKey(id);
	}

	public Method getMethod(String id){
		if(methodsTable.containsKey(id)){
			return methodsTable.get(id);
		}
		else {
			//do something here if the method does not exist 
			return null;
		}
	}

	public boolean addVar(Variable m) {
		if(!globalVarTable.containsKey(m.getName())) {
			globalVarTable.put(m.getName(), m);
			return true;
		}
		else {
			//already declared
			return false;
		}
	}

	public boolean containsVar(String id) {
		return globalVarTable.containsKey(id);
	}

	public Variable getVar(String id) {
		if(globalVarTable.containsKey(id)){
			return globalVarTable.get(id);
		}
		else {
			//do something here if the variable does not exist 
			return null;
		}
	}

}
