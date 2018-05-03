package ProjFive;

import java.util.*;

public class SymbolTable {

    //hash table for methods
    private HashMap<String, Method> methodsTable;
    //hash table for global vars
    private HashMap<String, Variable> globalVarTable; 
    //hash table for methods
    private HashMap<String, Classes> classTable ;

    //constructor
    public SymbolTable() {
            globalVarTable = new HashMap();
            methodsTable = new HashMap();
            classTable = new HashMap() ;
    }

    public void addMethod(Method m){
        methodsTable.put(m.getName(), m);

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
    
    public HashMap getAllMethods()
    {
        return methodsTable ;

    }
    public void addVar(Variable m) {

        globalVarTable.put(m.getName(), m);


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

    public HashMap getAllVars(){
        return globalVarTable ;
    }

    public void addClass(Classes c)
    {
        classTable.put(c.getClassId(), c) ;
    }
    
    public Classes getMyClass(String id)
    {
        if(classTable.containsKey(id))
        {
            return classTable.get(id) ;
        }
        else
        {
            return null ;
        }
    }

    public boolean containsClass(String cid)
    {
        return classTable.containsKey(cid) ;
    }
    
    public HashMap getAllClasses()
    {
        return classTable ;
    }
}
