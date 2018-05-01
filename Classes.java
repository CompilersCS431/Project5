/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProjFive;

import java.util.HashMap;

/**
 *
 * @author yangm89
 */
public class Classes {
    private HashMap<String, Method> methodTable;
    private HashMap<String, Variable> varTable ;
    String id ;
    
    public Classes(String id)
    {
        this.id = id ; 
        methodTable = new HashMap ();
        varTable = new HashMap () ;
    }
    
    public void addVar(Variable v)
    {
        varTable.put(v.getName(),v) ;
    }
    
    public void addMethod(Method m)
    {
        methodTable.put(m.getName(), m) ;
    }
    
    public String getClassId()
    {
        return id ;
    }
    
    public HashMap getClassMethods()
    {
        return methodTable ;
    }    
    
    public HashMap getClassVars()
    {
        return varTable ;
    }
    
    public Method getMethod(String m)
    {
        if(methodTable.containsKey(m))
        {
            return methodTable.get(m) ;
        }
        else 
        {
            return null ;
        }
    }
    
    public Variable getVar(String v)
    {
        if(varTable.containsKey(v))
        {
            return varTable.get(v) ;
        }
        else 
        {
            return null ;
        }
    }
    
    public boolean containsVar(String v)
    {
        return varTable.containsKey(v) ;
    }
    
    public boolean containsMethod(String m)
    {
        return varTable.containsKey(m) ;
    }
}
