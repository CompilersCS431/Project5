package ProjFive;

import ProjFive.analysis.*;
import ProjFive.node.*;
import java.util.*;

public class PrintTree extends DepthFirstAdapter {
    public static SymbolTable symbolTable;
    private Stack < String > stack;
    private String temp;
    public ArrayList < String > error;
    private boolean isId, inClass, inMethod;
    private String myParent;
    private String prevParent, className, methodName;
    private int forCount ;
    private int whileCount ;
    private int switchCount ;
    private int ifCount ;
    public static ArrayList<String> parentArr ;

    public PrintTree() {
        symbolTable = new SymbolTable();
        stack = new Stack < String > ();
        temp = new String();
        error = new ArrayList < > ();
        isId = false;
        myParent = new String();
        prevParent = new String();
        forCount = 0;
        whileCount = 0;
        switchCount = 0 ;
        ifCount = 0;
        parentArr = new ArrayList<>() ;
        inClass = false ;
        inMethod = false ;
        className = "" ;
        methodName = "" ;
    }

    public void caseAProg(AProg node) {
        node.getBegin().apply(this);
        node.getClassmethodstmts().apply(this);
        node.getEnd().apply(this);
    }

    public void caseANonemptyClassmethodstmts(ANonemptyClassmethodstmts node) {
        node.getClassmethodstmts().apply(this);
        node.getClassmethodstmt().apply(this);
    }

    public void caseAEmptyproductionClassmethodstmts(AEmptyproductionClassmethodstmts node) {
        stack.push("");
    }

    public void caseAClassdeclClassmethodstmt(AClassdeclClassmethodstmt node) { //class declaration
        inClass = true ;
        node.getTclass().apply(this);
        node.getId().apply(this);
        
        //add class to symbol table
        String peekId = stack.peek() ;
        Classes c = new Classes(peekId) ;
        className = peekId ;
        
        //error checking for declaring a class multiple times
        if(symbolTable.containsClass(peekId))
        {
            error.add("The class " + peekId + " has already been declared.") ; 
        }
        else 
        {
            symbolTable.addClass(c) ;

        }
        
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = peekId ;
        parentArr.add(0, peekId) ;
        
        node.getLcurly().apply(this);
        node.getMethodstmtseqs().apply(this);
        node.getRcurly().apply(this);

        String rcurly = stack.pop();
        String methodstmtseq = stack.pop();
        String lcurly = stack.pop();
        String id = stack.pop();
        String classstmt = stack.pop();

        temp = classstmt + id + lcurly + methodstmtseq + rcurly;
        //System.out.println("class decl " + temp);
        stack.push(temp);
        temp = "";
        
        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
        
        inClass = false ;
        className = "" ;
    }

    public void caseATypevarliststmtClassmethodstmt(ATypevarliststmtClassmethodstmt node) { //global method declaration
        inMethod = true ;
        
        node.getType().apply(this);
        String peekType = stack.peek() ;
        node.getId().apply(this);
        methodName = stack.peek() ;
        Method m = new Method(methodName, peekType);
        if(symbolTable.containsMethod(methodName))
        {
            error.add("The method " + methodName + " has already been declared in the global scope.") ;
        }    
        else
        {
            symbolTable.addMethod(m) ;
        }
        
        node.getLparen().apply(this);
        node.getVarlist().apply(this);
        node.getRparen().apply(this);
        node.getLcurly().apply(this);
        node.getStmtseq().apply(this);
        node.getRcurly().apply(this);

        String rcurly = stack.pop();
        String stmtseq = stack.pop();
        String lcurly = stack.pop();
        String rparen = stack.pop();
        String varlist = stack.pop();
        String lparen = stack.pop();
        String id = stack.pop();
        String type = stack.pop();

        

        String paramId = "";
        String paramType = "";
        if (!varlist.equals("")) {
            String[] varlistArr = varlist.split(",");
            for (int i = 0; i < varlistArr.length; i++) {
                String[] varInfo = varlistArr[i].split(":");
                paramId = varInfo[0];
                paramType = varInfo[1];
                Variable v = new Variable(paramId, paramType);
                m.addParam(v);
            }
        }

        //error checking that the type matches the return type 
        if (type.equals("VOID")) {
            if (stmtseq.contains("RETURN")) {
                error.add("Error in " + id + ". Void method cannot return value;");
            }
        }
        else
        {
            //check for other return statements 
        }

        //update the method in the method table with the parameters
        symbolTable.addMethod(m);

        temp = type + id + lparen + varlist + rparen + lcurly + stmtseq + rcurly;
        stack.push(temp);
        temp = "";
        
        inMethod = false; 
        methodName = "" ;
    }

    public void caseAIdlisttypeClassmethodstmt(AIdlisttypeClassmethodstmt node) { //global var declarations
        node.getId().apply(this);
        node.getOptlidlist().apply(this);
        node.getColon().apply(this);
        node.getType().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String type = stack.pop();
        String colon = stack.pop();
        String idlist = stack.pop();
        String id = stack.pop();
        
        Variable v = new Variable(id, type);
        symbolTable.addVar(v);

        if (!idlist.equals("")) {
            String[] ids = idlist.split(",");
            for (int i = 0; i < ids.length; i++) {
                Variable
                var = new Variable(ids[i], type);
                symbolTable.addVar(var);
            }
        }

        temp = id + idlist + colon + type + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAOneormoreMethodstmtseqs(AOneormoreMethodstmtseqs node) {
        node.getMethodstmtseqs().apply(this);
        node.getMethodstmtseq().apply(this);

        String methodstmtseqs = stack.pop();
        String methodstmtseq = stack.pop();

        temp = methodstmtseq + methodstmtseqs;
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionMethodstmtseqs(AEmptyproductionMethodstmtseqs node) {
        stack.push("");
    }

    public void caseATypevarlistMethodstmtseq(ATypevarlistMethodstmtseq node) { //method declaration in class
        inMethod = true ;
        //get the class
        Classes c = symbolTable.getMyClass(className) ;
        
        String tempPrevParent = prevParent ;
        prevParent = myParent ;
        
        node.getType().apply(this);
        node.getId().apply(this);
        
        String peekId = stack.peek() ;
        myParent = peekId ;
        methodName = peekId ;
        
        node.getLparen().apply(this);
        node.getVarlist().apply(this);
        node.getRparen().apply(this);
        node.getLcurly().apply(this);
        node.getStmtseq().apply(this);
        node.getRcurly().apply(this);

        String rcurly = stack.pop();
        String stmtseq = stack.pop();
        String lcurly = stack.pop();
        String rparen = stack.pop();
        String varlist = stack.pop();
        String lparen = stack.pop();
        String id = stack.pop();
        String type = stack.pop();

        Method m = new Method(id, type);

        if (!varlist.equals("")) {
            String[] varlistArr = varlist.split(",");
            for (int i = 0; i < varlistArr.length; i++) {
                String[] params = varlistArr[i].split(":");
                String paramId = params[0];
                String paramType = params[1];

                Variable v = new Variable(paramId, paramType);
                m.addParam(v);
            }
        }
        
        //error checking for methods that are already declared
        if(c.containsMethod(id))
        {
            error.add("The method " + id + " has already been declared in the class " + className) ;
        }
        else 
        {
            c.addMethod(m);
            symbolTable.addClass(c) ;
        }
        
        temp = type + id + lparen + varlist + rparen + lcurly + stmtseq + rcurly;
        stack.push(temp);
        temp = "";
        
        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
        
        inMethod = false ;
    }

    public void caseAIdtypeMethodstmtseq(AIdtypeMethodstmtseq node) { //class variable declarations - in class still
        Classes c = symbolTable.getMyClass(className) ;
        
        node.getId().apply(this);
        node.getOptlidlist().apply(this);
        node.getColon().apply(this);
        node.getType().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String type = stack.pop();
        String colon = stack.pop();
        String idlist = stack.pop();
        String id = stack.pop();

        //error checking for duplicate class vars being declared
        Variable v = new Variable(id, type);
        if(c.containsVar(id))
        {
            error.add("The variable " + id + " has already been declared in the class " + className) ;
        }
        else {
            c.addVar(v);
        }
        
        if (!idlist.equals("")) {
            String[] ids = idlist.split(",");
            for (int i = 0; i < ids.length; i++) {
                if(c.containsVar(ids[i]))
                {
                    error.add("The variable " + ids[i] + " has already been declared in the class " + className) ;
                }
                else
                {
                    Variable var = new Variable(ids[i], type);
                    c.addVar(var) ;
                }  
            }
        }
        
        symbolTable.addClass(c) ;

        temp = id + idlist + colon + type + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAAssignstringMethodstmtseq(AAssignstringMethodstmtseq node) { //in class - string declaration
        Classes c = symbolTable.getMyClass(className) ;
        
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getAssignment().apply(this);
        node.getAnychars().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String anychars = stack.pop();
        String assignment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();
        
        int i = -1 ;
        if(!arr.equals(""))
        {
            String temp = arr.replaceAll("[", "") ;
            temp = temp.replaceAll("]", "") ;
            i = Integer.parseInt(temp.trim()) ;
        }
        
        //error checking
        if(c.containsVar(id))
        {
            Variable v = c.getVar(id) ;
            v.setValue(anychars);
            if(i > -1)
            {
                v.setIndex(i);
            }
        }
        else 
        {
            error.add("Error in " + myParent + ". The variable " + id + " has not"
                    + "been declared and cannot be assigned a string.") ;
        }
       
           
        temp = id + arr + assignment + anychars + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAPrintstmtMethodstmtseq(APrintstmtMethodstmtseq node) { //pirnt statement inside classes
        Classes c = symbolTable.getMyClass(className) ;
        
        node.getPut().apply(this);
        node.getLparen().apply(this);
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getRparen().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String rparen = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();
        String lparen = stack.pop();
        String put = stack.pop();
        
        if(!c.containsVar(id))
        {
            error.add("Error in " + className + ". The variable " + id + " has not"
                    + "been declared and cannot be assigned a string.") ;
        } 

        temp = put + lparen + id + arr + rparen + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAAssignmentMethodstmtseq(AAssignmentMethodstmtseq node) {
        Classes c = symbolTable.getMyClass(className) ;
        
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getAssignment().apply(this);
        node.getGet().apply(this);
        node.getLparen().apply(this);
        node.getRparen().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String rparen = stack.pop();
        String lparen = stack.pop();
        String get = stack.pop();
        String assignment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();
        
        if(!c.containsVar(id))
        {
            error.add("Error in " + className + ". The variable " + id + " has not been declared yet." ) ;
        }

        temp = id + arr + assignment + get + lparen + rparen + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAIncrementMethodstmtseq(AIncrementMethodstmtseq node) { //still in class
        Classes c = symbolTable.getMyClass(className) ;
        
        String parent = myParent ;
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getIncrement().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String incr = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();
        
        temp = id + arr + incr + semicolon;
        stack.push(temp);
        temp = "";
        
        if(!c.containsVar(id))
        {
            error.add("Error in " + className + ". The variable " + id + " has not been declared yet." ) ;
        }
        else 
        {
            Variable v = c.getVar(id) ;
            if(v.getType().equals("STRING") || v.getType().equals("STRING_ARRAY"))
            {
                error.add("Error in " + className + ". The variable " + id + " is type STRING and cannot be incremented.") ;
            }
            else if(v.getType().equals("BOOLEAN") || v.getType().equals("BOOBLEAN_ARRAY"))
            {
                error.add("Error in " + className + ". The variable " + id + " is type BOOLEAN and cannot be incremented.") ;
            }
        }
    }

    public void caseADecrementMethodstmtseq(ADecrementMethodstmtseq node) { //still in class
        Classes c = symbolTable.getMyClass(className) ;
        
        String parent = myParent ;
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getDecrement().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String decr = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();
        temp = id + arr + decr + semicolon;
        stack.push(temp);
        temp = "";

        if(!c.containsVar(id))
        {
            error.add("Error in " + myParent + ". The variable " + id + " has not been declared yet." ) ;
        }
    }

    public void caseADeclobjectMethodstmtseq(ADeclobjectMethodstmtseq node) {
        node.getFirstid().apply(this);
        node.getOptionalidarray().apply(this);
        node.getAssignment().apply(this);
        node.getNew().apply(this);
        node.getSecondid().apply(this);
        node.getLparen().apply(this);
        node.getRparen().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String rparen = stack.pop();
        String lparen = stack.pop();
        String secondid = stack.pop();
        String newstmt = stack.pop();
        String assignment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();      

        temp = id + arr + assignment + newstmt + secondid + lparen + rparen + semicolon;
        stack.push(temp);
        temp = "";
        
        Classes c = symbolTable.getMyClass(myParent) ;
        if(!arr.equals(""))
        {
            secondid = secondid + "_ARRAY" ;
        }
        
        Variable v = new Variable(id, secondid);
        c.addVar(v) ;
        symbolTable.addClass(c) ;
    }

    public void caseAAssignbooleanMethodstmtseq(AAssignbooleanMethodstmtseq node) { //get id
        Classes c = symbolTable.getMyClass(className) ;
        
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getAssignment().apply(this);
        node.getBoolean().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String bool = stack.pop();
        String assignment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        temp = id + arr + assignment + semicolon;
        stack.push(temp);
        temp = "";
        
        if(!c.containsVar(id))
        {
            error.add("Error in " + myParent + ". The variable " + id + " has not been declared yet." ) ;
        }
    }

    public void caseAOneormoreStmtseq(AOneormoreStmtseq node) {
        node.getStmt().apply(this);
        node.getStmtseq().apply(this);

        String seq = stack.pop();
        String stmt = stack.pop();

        temp = stmt + seq;
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionStmtseq(AEmptyproductionStmtseq node) {
        stack.push("");
    }

    public void caseAExprassignmentStmt(AExprassignmentStmt node) {
        String parent = myParent;
        
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getAssignment().apply(this);
        node.getExpr().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String expr = stack.pop();
        String assignment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        temp = id + arr + assignment + expr + semicolon;
        stack.push(temp);
        temp = "";

        Classes c ;
        Method m ;
        if(inClass)
        {
            if(symbolTable.containsClass(prevParent))
            {
                /*String methodName = scopeMethod(id) ;
                if(!methodName.equals(""))
                {
                    
                }*/
            }
        }
        else if(inMethod)
        {
            
        }
        //error checking to compare the type against the expression being assigned
        Variable v ;
        String type ;
        String idScope = getVarId(parent, id);
        if(parent.equals(""))
        {
            parent = "GLOBAL";
        }
        if(idScope != null)
        {
            v = symbolTable.getVar(idScope);
            type = v.getType();

            if(type.equals("INT"))
            {
                if(expr.contains("."))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type INT and cannot store a real numbers.") ;
                }
                if(expr.contains("\""))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type INT and cannot store a string literal.");
                }
            }
            else if(type.equals("BOOLEAN"))
            {
                if(!expr.equals("TRUE") || !expr.equals("FALSE") || !expr.equals("0") | !expr.equals("1"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type BOOLEAN and can only store boolean values.") ;
                }
            }
            else if(type.equals("STRING"))
            {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                    + "The variable " + id + " is type STRING and can only store string literals.");
            }
            else if (type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")) {
                if (expr.charAt(0) == '"') {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type " + type + " and cannot store a string");
                }
            }
            else if(type.contains("ARRAY"))
            {
                if(arr.equals(""))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is an array. Please specify the array index.") ;
                }
                if(type.equals("INT_ARRAY"))
                {
                    if(expr.contains("."))
                    {
                        error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                                + "The variable " + id + " is  " + type.replaceAll("_ARRAY", "") + " ARRAY and cannot store a real numbers.") ;
                    }
                    if(expr.contains("\""))
                    {
                        error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                                + "The variable " + id + " is  " + type.replaceAll("_ARRAY", "") + " ARRAY and cannot store a string literal.");
                    }
                }
                else if(type.equals("BOOLEAN_ARRAY"))
                {
                    if(!expr.equals("TRUE") || !expr.equals("FALSE") || !expr.equals("0") | !expr.equals("1"))
                    {
                        error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                                + "The variable " + id + " is  " + type.replaceAll("_ARRAY", "") + " ARRAYand can only store boolean values.") ;
                    }
                }
                else if(type.equals("STRING_ARRAY"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                        + "The variable " + id + " is  " + type.replaceAll("_ARRAY", "") + " ARRAY and can only store string literals.");
                }
                else if (type.equals("INT_ARRAY") || type.equals("DOUBLE_ARRAY") || type.equals("BOOLEAN_BOOLEAN")) {
                    if (expr.charAt(0) == '"') {
                        error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                                + "The variable " + id + " is a " + type.replaceAll("_ARRAY", "") + " ARRAY and cannot store a string");
                    }
                }

            }
        }
        else {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                + "The variable " + id + " is undeclared. A variable must be declared before it can be assigned a value.");
        }
    }

    public void caseAExpranycharStmt(AExpranycharStmt node) {
        String parent = myParent;
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getAssignment().apply(this);
        node.getAnychars().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String anychars = stack.pop();
        String assignment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        temp = id + arr + assignment + anychars + semicolon;
        stack.push(temp);
        temp = "";

        Variable v ;
        String type ;
        String idScope = getVarId(parent, id);
        if(parent.equals(""))
        {
            parent = "GLOBAL" ;
        }
        if(idScope != null){
            v = symbolTable.getVar(idScope);
            type = v.getType();
            if(arr.equals(""))
            {
                if (!type.equals("STRING")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type " + type.toUpperCase() + " and cannot store a string literal.");
                }
            }
            else
            {
                if (!type.equals("STRING_ARRAY")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is not an array.");
                }
            }
        }
        else if(inClass)
        {
            Classes c = symbolTable.getMyClass(id) ;
            v = c.getVar(id) ;
            type = v.getType() ;
            if(arr.equals(""))
            {
                if (!type.equals("STRING")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type " + type.toUpperCase() + " and cannot store a string literal.");
                }
            }
            else
            {
                if (!type.equals("STRING_ARRAY")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is not an array.");
                }
            }
        }
        else {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                    "The variable " + id + " is undeclared. A variable must be declared before it can be assigned a value.") ;
        }
    }

    public void caseAIdlistStmt(AIdlistStmt node) {
        String parent = myParent;

        node.getId().apply(this);
        node.getOptlidlist().apply(this);
        node.getColon().apply(this);
        node.getType().apply(this);
        node.getOptionalidarray().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String arr = stack.pop();
        String type = stack.pop();
        String colon = stack.pop();
        String idlist = stack.pop();
        String id = stack.pop();

        temp = id + idlist + colon + type + arr + semicolon;
        stack.push(temp);
        temp = "";

        //error checking for scope and var re-declaration
        Variable v ;
        String idScope = id + "_" + parent ;        
        if(symbolTable.containsVar(idScope))
        {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                    "The variable " + id + " has already been declared in " + parent.replaceAll("\\d",""));
        }
        else
        {
            if(!parent.equals("")){
                v = new Variable(idScope, type);
                symbolTable.addVar(v);
            }
        }

        if (!idlist.equals("")) {
            String[] ids = idlist.split(",");
            for (int i = 0; i < ids.length; i++) {
                idScope = ids[i] + "_" + parent ;
                if(symbolTable.containsVar(idScope))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " has already been declared in " + parent.replaceAll("\\d",""));
                }
                else
                {
                    if(!parent.equals("")){
                        v = new Variable(idScope, type);
                        symbolTable.addVar(v);
                    }
                }

            }
        }
    }

    public void caseAIfbooleanStmt(AIfbooleanStmt node) {
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = "IF" + ifCount;
        parentArr.add(0, myParent);
        node.getIf().apply(this);
        node.getLparen().apply(this);
        node.getBoolid().apply(this);
        node.getRparen().apply(this);
        node.getThen().apply(this);
        node.getOptionalelse().apply(this);

        String elsestmt = stack.pop();
        String then = stack.pop();
        String rparen = stack.pop();
        String boolid = stack.pop();
        String lparen = stack.pop();
        String ifstmt = stack.pop();

        temp = ifstmt + lparen + boolid + rparen + then + elsestmt;
        stack.push(temp);
        temp = "";

        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
        ifCount++;
    }

    public void caseAWhileStmt(AWhileStmt node) {
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = "WHILE" + whileCount;
        parentArr.add(0, myParent);
        node.getWhile().apply(this);
        node.getLparen().apply(this);
        node.getBoolean().apply(this);
        node.getRparen().apply(this);
        node.getLcurly().apply(this);
        node.getStmtseq().apply(this);
        node.getRcurly().apply(this);

        String rcurly = stack.pop();
        String stmtseq = stack.pop();
        String lcurly = stack.pop();
        String rparen = stack.pop();
        String bool = stack.pop();
        String lparen = stack.pop();
        String whilesmt = stack.pop();

        temp = whilesmt + lparen + bool + rparen + lcurly + stmtseq + rcurly;
        stack.push(temp);
        temp = "";

        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
        whileCount++;
    }

    public void caseAForStmt(AForStmt node) {
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = "FOR" + forCount;
        parentArr.add(0, myParent);
        node.getFor().apply(this);
        node.getLparen().apply(this);
        node.getOptionaltype().apply(this);
        String peekType = stack.peek();
        node.getId().apply(this);
        String peekId = stack.peek();

        //add the iterator declaration if there is one
        String scopeId = peekId + "_" + myParent ;
        if(!peekType.equals(""))
        {
            if(symbolTable.containsVar(scopeId))
            {
                error.add("Error in FOR. The variable " + peekId + " has already been declared.");
            }
            else {
                Variable v = new Variable(scopeId, peekType);
                symbolTable.addVar(v);
            }
        }
        else {
            if(!symbolTable.containsVar(peekId))
            {
                error.add("Error in FOR. The variable " + peekId + " is not declared and cannot be assigned a value.");
            }
        }

        node.getAssignment().apply(this);
        node.getExpr().apply(this);
        String peekExp = stack.peek() ;
        if(peekType.equals("INT"))
        {
            if(peekExp.contains("."))
            {
                error.add("Error in FOR. The variable " + peekId + " is type INT and cannot store a real number.");
            }
            else if(peekExp.contains("\""))
            {
                error.add("Error in FOR. The variable " + peekId + " is type INT and cannot store a string literal.");
            }
        }
        else if(peekType.equals("BOOLEAN"))
        {
            if (!peekExp.equals("TRUE") || !peekExp.equals("FALSE") || !peekExp.equals("0") || !peekExp.equals("1")) {
                error.add("Error in FOR. The variable " + peekId + " is type BOOLEAN and can only store boolean values");
            }
        }
        else if (peekType.equals("INT") || peekType.equals("DOUBLE") || peekType.equals("BOOLEAN")) {
            if (peekExp.charAt(0) == '"') {
                error.add("The variable " + peekId + " is type " + peekType + " and cannot store a string");
            }
        }

        node.getFirstsemicolon().apply(this);
        node.getBoolean().apply(this);
        node.getSecondsemicolon().apply(this);
        node.getOrstmts().apply(this);
        node.getRparen().apply(this);
        node.getLcurly().apply(this);
        node.getStmtseq().apply(this);
        node.getRcurly().apply(this);

        String rcurly = stack.pop();
        String stmtseq = stack.pop();
        String lcurly = stack.pop();
        String rparen = stack.pop();
        String orstmts = stack.pop();
        String secondsemi = stack.pop();
        String bool = stack.pop();
        String firstsemi = stack.pop();
        String expr = stack.pop();
        String assignment = stack.pop();
        String id = stack.pop();
        String optionaltype = stack.pop();
        String lparen = stack.pop();
        String forStmt = stack.pop();

        temp = forStmt + lparen + optionaltype + id + assignment + expr + firstsemi + bool + secondsemi + orstmts + rparen + lcurly + stmtseq + rcurly;
        stack.push(temp);
        temp = "";

        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
        forCount++;
    }

    public void caseAGetStmt(AGetStmt node) {
        String parent = myParent ;
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getAssignment().apply(this);
        node.getGet().apply(this);
        node.getLparen().apply(this);
        node.getRparen().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String rparen = stack.pop();
        String lparen = stack.pop();
        String get = stack.pop();
        String assignment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        temp = id + arr + assignment + get + lparen + rparen + semicolon;
        stack.push(temp);
        temp = "";

        Variable v ;
        String type ;
        String idScope = getVarId(parent, id);
        if(parent.equals(""))
        {
            parent = "GLOBAL";
        }
        if(idScope == null)
        {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                    "The variable " + id + " has not been declared.");
        }
        else {
            v = symbolTable.getVar(idScope);
            type = v.getType();
            if(type.contains("ARRAY"))
            {
                if(arr.equals(""))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is an array. Please specify the index.") ;
                }
            }
        }
    }

    public void caseAPutStmt(APutStmt node) {
        String parent = myParent ;
        node.getPut().apply(this);
        node.getLparen().apply(this);
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getRparen().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String rparen = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();
        String lparen = stack.pop();
        String put = stack.pop();

        temp = put + lparen + id + arr + rparen + semicolon;
        stack.push(temp);
        temp = "";

        Variable v ;
        String type ;
        String idScope = getVarId(parent, id);
        if(parent.equals(""))
        {
            parent = "GLOBAL" ;
        }
        if(idScope == null)
        {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                    "The variable " + id + " has not been declared.") ;
        }
        else {
            v = symbolTable.getVar(idScope);
            type = v.getType();
            if(type.contains("ARRAY"))
            {
                if(arr.equals(""))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is an array. Please specify the index.") ;
                }
            }
        }

    }

    public void caseAIncrementStmt(AIncrementStmt node) {
        String parent = myParent;
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getIncrement().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String increment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        temp = id + arr + increment + semicolon;
        stack.push(temp);
        temp = "";

        Variable v ;
        String type ;
        String idScope = getVarId(parent, id);
        if(parent.equals(""))
        {
            parent = "GLOBAL" ;
        }
        if(idScope != null)
        {
            v = symbolTable.getVar(idScope);
            type = v.getType();
            if (type.equals("STRING")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type STRING and cannot be incremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type BOOLEAN and cannot be incremented.");
            }
            else if(type.contains("ARRAY"))
            {
                if(arr.equals(""))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is an array. Please specify the index.");
                }
                else if (type.equals("STRING_ARRAY")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is a STRING ARRAY and cannot be incremented.");
                } else if (type.equals("BOOLEAN_ARRAY")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is a BOOLEAN ARRAY and cannot be incremented.");
                }
            }
        }
        else {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                    "The variable " + id + " has not been declared and cannot be incremented.");
        }

    }

    public void caseADecrementStmt(ADecrementStmt node) {
        String parent = myParent ;
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getDecrement().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String decrement = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        temp = id + arr + decrement + semicolon;
        stack.push(temp);
        temp = "";

        Variable v ;
        String type ;
        String idScope = getVarId(parent, id);
        if(parent.equals(""))
        {
            parent = "GLOBAL" ;
        }
        if(idScope != null)
        {
            v = symbolTable.getVar(idScope);
            type = v.getType();
            if (type.equals("STRING")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type STRING and cannot be decremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type BOOLEAN and cannot be decremented.");
            }
            else if(type.contains("ARRAY"))
            {
                if(arr.equals(""))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is an array. Please specify the index.");
                }
                else if (type.equals("STRING_ARRAY")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is a STRING ARRAY and cannot be decremented.");
                } else if (type.equals("BOOLEAN_ARRAY")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is a BOOLEAN ARRAY and cannot be decremented.");
                }
            }
        }
        else
        {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                    "The variable " + id + " has not been declared and cannot be decremented.");
        }


    }

    public void caseANewassignmentStmt(ANewassignmentStmt node) {
        node.getFirstid().apply(this);
        node.getOptionalidarray().apply(this);
        node.getAssignment().apply(this);
        node.getNew().apply(this);
        node.getSecondid().apply(this);
        node.getLparen().apply(this);
        node.getRparen().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String rparen = stack.pop();
        String lparen = stack.pop();
        String secondid = stack.pop();
        String newkeyword = stack.pop();
        String assignment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        temp = id + arr + assignment + newkeyword + secondid + lparen + rparen + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAIdvarlisttwoStmt(AIdvarlisttwoStmt node) {
        node.getId().apply(this);
        node.getLparen().apply(this);
        node.getVarlisttwo().apply(this);
        node.getRparen().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String rparen = stack.pop();
        String varlisttwo = stack.pop();
        String lparen = stack.pop();
        String id = stack.pop();

        temp = id + lparen + varlisttwo + rparen + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAMultiplevarlisttwoStmt(AMultiplevarlisttwoStmt node) {
        node.getFirstid().apply(this);
        node.getOptionalidarray().apply(this);
        node.getPeriod().apply(this);
        node.getSecondid().apply(this);
        node.getLparen().apply(this);
        node.getVarlisttwo().apply(this);
        node.getRparen().apply(this);
        node.getOptlidvarlisttwo().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String optlvarlisttwo = stack.pop();
        String rparen = stack.pop();
        String varlisttwo = stack.pop();
        String lparen = stack.pop();
        String secondid = stack.pop();
        String period = stack.pop();
        String optionalid = stack.pop();
        String id = stack.pop();

        temp = id + optionalid + period + secondid + lparen + varlisttwo + rparen + optlvarlisttwo + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAReturnStmt(AReturnStmt node) {
        node.getReturn().apply(this);
        node.getExpr().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String expr = stack.pop();
        String returnstmt = stack.pop();

        temp = returnstmt + expr + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAIdbooleanStmt(AIdbooleanStmt node) {
        String parent = myParent;
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getAssignment().apply(this);
        node.getBoolean().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String bool = stack.pop();
        String assignment = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        temp = id + arr + bool + semicolon;
        stack.push(temp);
        temp = "";

        Variable v ;
        String type ;
        String idScope = getVarId(parent, id);
        if(parent.equals(""))
        {
            parent = "GLOBAL" ;
        }
        if(idScope != null)
        {
            v = symbolTable.getVar(idScope);
            type = v.getType();
            if (type.equals("STRING")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type STRING and cannot be incremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type BOOLEAN and cannot be incremented.");
            }
            else if(type.contains("ARRAY"))
            {
                if(arr.equals(""))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is an array. Please specify the index.");
                }
                else if (type.equals("STRING_ARRAY")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is a STRING ARRAY and cannot be incremented.");
                } else if (type.equals("BOOLEAN_ARRAY")) {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + id + " is a BOOLEAN ARRAY and cannot be incremented.");
                }
            }
        }
        else {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                    "The variable " + id + " has not been declared and cannot be incremented.");
        }

    }

    public void caseASwitchStmt(ASwitchStmt node) {
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = "SWITCH" + switchCount;
        parentArr.add(0, myParent);
        node.getSwitch().apply(this);
        node.getFirstlparen().apply(this);
        node.getExpr().apply(this);
        node.getFirstrparen().apply(this);
        node.getLcurly().apply(this);
        node.getCase().apply(this);
        node.getSecondlparen().apply(this);
        node.getNumber().apply(this);
        node.getSecondrparen().apply(this);
        node.getFirstcolon().apply(this);
        node.getFirststmtseq().apply(this);
        node.getOptlbreak().apply(this);
        node.getOptionalswitchcases().apply(this);
        node.getDefault().apply(this);
        node.getSecondcolon().apply(this);
        node.getSecondstmtseq().apply(this);
        node.getRcurly().apply(this);

        String rcurly = stack.pop();
        String secondstmtseq = stack.pop();
        String secondcolon = stack.pop();
        String defaultstmt = stack.pop();
        String optlswitch = stack.pop();
        String optlbreak = stack.pop();
        String stmtseq = stack.pop();
        String colon = stack.pop();
        String secondrparen = stack.pop();
        String number = stack.pop();
        String secondlparen = stack.pop();
        String casestmt = stack.pop();
        String lcurly = stack.pop();
        String rparen = stack.pop();
        String expr = stack.pop();
        String lparen = stack.pop();
        String switchstmt = stack.pop();

        temp = switchstmt + lparen + expr + rparen + lcurly + casestmt + secondlparen + number + secondrparen + colon + stmtseq + optlbreak + optlswitch + defaultstmt + secondcolon + secondstmtseq + rcurly;
        stack.push(temp);
        temp = "";

        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
        switchCount++;
    }

    public void caseACommaidlistOptlidlist(ACommaidlistOptlidlist node) {
        node.getComma().apply(this);
        node.getId().apply(this);
        node.getOptlidlist().apply(this);

        String optlid = stack.pop();
        String id = stack.pop();
        String comma = stack.pop();

        temp = comma + id + optlid;
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionOptlidlist(AEmptyproductionOptlidlist node) {
        stack.push("");
    }

    public void caseANoelseOptionalelse(ANoelseOptionalelse node) {
        node.getLcurly().apply(this);
        node.getStmtseq().apply(this);
        node.getRcurly().apply(this);

        String rcurly = stack.pop();
        String stmtseq = stack.pop();
        String lcurly = stack.pop();

        temp = lcurly + stmtseq + rcurly;
        stack.push(temp);
        temp = "";
    }

    public void caseAElseOptionalelse(AElseOptionalelse node) {
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = "ELSE" + ifCount;
        parentArr.add(0, myParent);
        node.getFirstlcurly().apply(this);
        node.getFirststmtseq().apply(this);
        node.getFirstrcurly().apply(this);
        node.getElse().apply(this);
        node.getSecondlcurly().apply(this);
        node.getSecondstmtseq().apply(this);
        node.getSecondrcurly().apply(this);

        String secondrcurly = stack.pop();
        String secondstmtseq = stack.pop();
        String secondlcurly = stack.pop();
        String elsestmt = stack.pop();
        String rcurly = stack.pop();
        String stmtseq = stack.pop();
        String lcurly = stack.pop();

        temp = lcurly + stmtseq + rcurly + elsestmt + secondlcurly + secondstmtseq + secondrcurly;
        stack.push(temp);
        temp = "";

        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
    }

    public void caseACaselistOptionalswitchcases(ACaselistOptionalswitchcases node) {
        node.getCase().apply(this);
        node.getLparen().apply(this);
        node.getNumber().apply(this);
        node.getRparen().apply(this);
        node.getColon().apply(this);
        node.getStmtseq().apply(this);
        node.getOptlbreak().apply(this);
        node.getOptionalswitchcases().apply(this);

        String optlswitches = stack.pop();
        String br = stack.pop();
        String stmtseq = stack.pop();
        String colon = stack.pop();
        String rparen = stack.pop();
        String number = stack.pop();
        String lparen = stack.pop();
        String cases = stack.pop();

        temp = cases + lparen + number + rparen + colon + stmtseq + br + optlswitches;
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionOptionalswitchcases(AEmptyproductionOptionalswitchcases node) {
        stack.push("");
    }

    public void caseABreakOptlbreak(ABreakOptlbreak node) {
        node.getBreak().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String br = stack.pop();
        temp = br + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionOptlbreak(AEmptyproductionOptlbreak node) {
        stack.push("");
    }

    public void caseAIncrementOrstmts(AIncrementOrstmts node) {
        String parent = myParent;

        node.getId().apply(this);
        node.getIncrement().apply(this);

        String incr = stack.pop();
        String id = stack.pop();

        temp = id + incr;
        stack.push(temp);
        temp = "";

        Variable v ;
        String type ;
        String idScope = getVarId(parent, id) ;
        if(parent.equals(""))
        {
            parent = "GLOBAL" ;
        }
        if(idScope != null)
        {
            v = symbolTable.getVar(idScope);
            type = v.getType();
            if (type.equals("STRING")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type STRING and cannot be incremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type BOOLEAN and cannot be incremented.");
            }
        }
        else {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                    "The variable " + id + " has not been declared and cannot be incremented.");
        }
    }

    public void caseADecrementOrstmts(ADecrementOrstmts node) {
        String parent = myParent;

        node.getId().apply(this);
        node.getDecrement().apply(this);

        String decr = stack.pop();
        String id = stack.pop();

        temp = id + decr;
        stack.push(temp);
        temp = "";

        Variable v ;
        String type ;
        String idScope = getVarId(parent, id) ;
        if(parent.equals(""))
        {
            parent = "GLOBAL" ;
        }
        if(idScope != null)
        {
            v = symbolTable.getVar(idScope);
            type = v.getType();
            if (type.equals("STRING")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type STRING and cannot be decremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + id + " is type BOOLEAN and cannot be decremented.");
            }
        }
        else {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                    "The variable " + id + " has not been declared and cannot be decremented.");
        }
    }

    public void caseAAssignmentOrstmts(AAssignmentOrstmts node) {
        String parent = myParent;
        node.getId().apply(this);
        node.getAssignment().apply(this);
        node.getExpr().apply(this);

        String expr = stack.pop();
        String assignment = stack.pop();
        String id = stack.pop();

        temp = id + assignment + expr;
        stack.push(temp);
        temp = "";

        Variable v ;
        String type ;
        String idScope = getVarId(parent, id);
        if(parent.equals(""))
        {
            parent = "GLOBAL" ;
        }
        if(idScope != null)
        {
            v = symbolTable.getVar(idScope);
            type = v.getType();

            if(type.equals("INT"))
            {
                if(expr.contains("."))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type INT and cannot store a real numbers.") ;
                }
                if(expr.contains("\""))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type INT and cannot store a string literal.");
                }
            }
            else if(type.equals("BOOLEAN"))
            {
                if(!expr.equals("TRUE") || !expr.equals("FALSE") || !expr.equals("0") | !expr.equals("1"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type BOOLEAN and can only store boolean values.") ;
                }
            }
            else if(type.equals("STRING"))
            {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                    + "The variable " + id + " is type STRING and can only store string literals.");
            }
            else if (type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")) {
                if (expr.charAt(0) == '"') {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                            + "The variable " + id + " is type " + type + " and cannot store a string");
                }
            }
        }
        else {
            error.add("Error in " + parent.replaceAll("\\d", "") + ". "
                + "The variable " + id + " is undeclared. A variable must be declared before it can be assigned a value.");
        }
    }

    public void caseANonemptyOptlidvarlisttwo(ANonemptyOptlidvarlisttwo node) {
        node.getPeriod().apply(this);
        node.getId().apply(this);
        node.getLparen().apply(this);
        node.getVarlisttwo().apply(this);
        node.getRparen().apply(this);
        node.getOptlidvarlisttwo().apply(this);

        String optlvarlisttwo = stack.pop();
        String rparen = stack.pop();
        String varlist = stack.pop();
        String lparen = stack.pop();
        String id = stack.pop();
        String period = stack.pop();

        temp = period + id + lparen + varlist + rparen + optlvarlisttwo;
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionOptlidvarlisttwo(AEmptyproductionOptlidvarlisttwo node) {
        stack.push("");
    }

    public void caseATypeOptionaltype(ATypeOptionaltype node) {
        node.getType().apply(this);
    }

    public void caseAEmptyproductionOptionaltype(AEmptyproductionOptionaltype node) {
        stack.push("");
    }

    public void caseAMultipleVarlist(AMultipleVarlist node) {
        node.getId().apply(this);
        node.getColon().apply(this);
        node.getType().apply(this);
        node.getOptionalidarray().apply(this);
        node.getCommaidarray().apply(this);

        String commaid = stack.pop();
        String arr = stack.pop();
        String type = stack.pop();
        String colon = stack.pop();
        String id = stack.pop();

        temp = id + colon + type + arr + commaid;
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionVarlist(AEmptyproductionVarlist node) {
        stack.push("");
    }

    public void caseAOptlcommaidarrCommaidarray(AOptlcommaidarrCommaidarray node) {
        node.getComma().apply(this);
        node.getVarlist().apply(this);

        String varlist = stack.pop();
        String comma = stack.pop();

        temp = comma + varlist;
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionCommaidarray(AEmptyproductionCommaidarray node) {
        stack.push("");
    }

    public void caseAMultipleVarlisttwo(AMultipleVarlisttwo node) {
        node.getExprorbool().apply(this);
        node.getMorevarlisttwo().apply(this);

        String varlisttwo = stack.pop();
        String expr = stack.pop();
        temp = expr + varlisttwo;
        stack.push(temp);
        temp = "";
    }


    public void caseAEmptyproductionVarlisttwo(AEmptyproductionVarlisttwo node) {
        stack.push("");
    }

    public void caseAExprExprorbool(AExprExprorbool node) {
        node.getExpr().apply(this);
    }

    public void caseABooleanExprorbool(ABooleanExprorbool node) {
        node.getBoolean().apply(this);
    }

    public void caseAMultipleMorevarlisttwo(AMultipleMorevarlisttwo node) {
        node.getComma().apply(this);
        node.getVarlisttwo().apply(this);

        String varlisttwo = stack.pop();
        String comma = stack.pop();

        temp = comma + varlisttwo;
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionMorevarlisttwo(AEmptyproductionMorevarlisttwo node) {
        stack.push("");
    }

    public void caseAMultipleExpr(AMultipleExpr node) {
        String parent = myParent;
        node.getExpr().apply(this);
        boolean exprIsId = isId;
        isId = false;
        node.getAddop().apply(this);
        node.getTerm().apply(this);
        boolean termIsId = isId;
        isId = false;

        String term = stack.pop();
        String addop = stack.pop();
        String expr = stack.pop();

        temp = expr + addop + term;
        stack.push(temp);
        temp = "";

        Variable t ;
        Variable e ;
        String tType ;
        String eType ;

        if(termIsId)
        {
            if(term.contains("["))
            {
                term = term.replaceAll("[", "") ;
                term = term.replaceAll("]", "") ;
            }

            String tIdScope = getVarId(parent, term) ;
            if(parent.equals("")){
                parent = "GLOBAL" ;
            }
            if(tIdScope != null)
            {
                t = symbolTable.getVar(tIdScope) ;
                tType = t.getType() ;
                if(tType.equals("STRING") || tType.equals("STRING_ARRAY"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + term + " is type STRING. Cannot complete arithmetic"
                                + " addition with a string.") ;
                }
            }
            else {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + term + " has not been declared."
                                + " Cannot use an undeclared variable in arithmetic addition.") ;
            }
        }

        if(exprIsId)
        {
            if(expr.contains("["))
            {
                expr = expr.replaceAll("[", "") ;
                expr = expr.replaceAll("]", "") ;
            }

            String eIdScope = getVarId(parent, expr) ;
            if(parent.equals(""))
            {
                parent = "GLOBAL" ;
            }

            if(eIdScope != null)
            {
                e = symbolTable.getVar(eIdScope) ;
                eType = e.getType() ;
                if(eType.equals("STRING") || eType.equals("STRING_ARRAY"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                            "The variable " + expr + " is type STRING. Cannot complete arithmetic"
                                    + " addition with a string.") ;
                }
            }
            else
            {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + expr + " has not been declared."
                                + " Cannot use an undeclared variable in arithmetic addition.") ;
            }
        }
    }

    public void caseASingleExpr(ASingleExpr node) {
        node.getTerm().apply(this);
    }

    public void caseATermmultopTerm(ATermmultopTerm node) {
        String parent = myParent;

        node.getTerm().apply(this);
        boolean termIsId = isId;
        isId = false;
        node.getMultop().apply(this);
        node.getFactor().apply(this);
        boolean factorIsId = isId;
        isId = false;

        String factor = stack.pop();
        String mult = stack.pop();
        String term = stack.pop();

        temp = term + mult + factor;
        stack.push(temp);
        temp = "";

        Variable t ;
        Variable f ;
        String tType ;
        String fType ;

        if(termIsId)
        {
            if(term.contains("["))
            {
                term = term.replaceAll("[", "") ;
                term = term.replaceAll("]", "") ;
            }

            String tIdScope = getVarId(parent, term) ;
            if(parent.equals(""))
            {
                parent = "GLOBAL" ;
            }
            if(tIdScope != null)
            {
                t = symbolTable.getVar(tIdScope) ;
                tType = t.getType() ;
                if(tType.equals("STRING") || tType.equals("STRING_ARRAY"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + term + " is type STRING. Cannot complete arithmetic"
                                + " addition with a string.") ;
                }
            }
            else {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + term + " has not been declared."
                                + " Cannot use an undeclared variable in arithmetic addition.") ;
            }
        }

        if(factorIsId)
        {
            if(factor.contains("["))
            {
                factor = factor.replaceAll("[", "") ;
                factor = factor.replaceAll("]", "") ;
            }

            String fIdScope = getVarId(parent, factor) ;
            if(parent.equals(""))
            {
                parent = "GLOBAL" ;
            }
            if(fIdScope != null)
            {
                f = symbolTable.getVar(fIdScope) ;
                fType = f.getType() ;
                if(fType.equals("STRING") || fType.equals("STRING_ARRAY"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + factor + " is type STRING. Cannot complete arithmetic"
                                + " addition with a string.") ;
                }
            }
            else
            {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + factor + " has not been declared."
                                + " Cannot use an undeclared variable in arithmetic addition.") ;
            }
        }
    }

    public void caseAFactorTerm(AFactorTerm node) {
        node.getFactor().apply(this);

    }

    public void caseAParenexprFactor(AParenexprFactor node) {
        String parent = myParent;
        node.getLparen().apply(this);
        node.getExpr().apply(this);
        boolean exprIsId = isId;
        isId = false;
        node.getRparen().apply(this);

        String rparen = stack.pop();
        String expr = stack.pop();
        String lparen = stack.pop();

        temp = lparen + expr + rparen;
        stack.push(temp);
        temp = "";

        Variable e ;
        if(exprIsId)
        {
            if(expr.contains("["))
            {
                expr = expr.replaceAll("[", "") ;
                expr = expr.replaceAll("]", "") ;
            }

            String eIdScope = getVarId(parent, expr) ;
            if(parent.equals(""))
            {
                parent = "GLOBAL" ;
            }

            if(eIdScope == null)
            {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + expr + " has not been declared."
                                + " Cannot use an undeclared variable in arithmetic addition.") ;
            }
        }
    }

    public void caseAMinusfactorFactor(AMinusfactorFactor node) {
        String parent = myParent;

        node.getMinus().apply(this);
        node.getFactor().apply(this);
        boolean factorIsId = isId;
        isId = false;

        String factor = stack.pop();
        String minus = stack.pop();

        temp = minus + factor;
        stack.push(temp);
        temp = "";

        Variable f ;
        String fType ;
        if(factorIsId)
        {
            if(factor.contains("["))
            {
                factor = factor.replaceAll("[", "") ;
                factor = factor.replaceAll("]", "") ;
            }

            String fIdScope = getVarId(parent, factor) ;
            if(parent.equals(""))
            {
                parent = "GLOBAL" ;
            }
            if(fIdScope != null)
            {
                f = symbolTable.getVar(fIdScope) ;
                fType = f.getType() ;
                if(fType.equals("STRING") || fType.equals("STRING_ARRAY"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + factor + " is type STRING. Cannot negate a string.") ;
                }
            }
            else
            {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + factor + " has not been declared."
                                + " Cannot use an undeclared variable in arithmetic addition.") ;
            }
        }
    }

    public void caseAIntFactor(AIntFactor node) {
        node.getNumber().apply(this);
    }

    public void caseARealFactor(ARealFactor node) {
        node.getReal().apply(this);
    }

    public void caseAIdarrayFactor(AIdarrayFactor node) {
        node.getArrayorid().apply(this);
    }

    public void caseAIdvarlisttwoFactor(AIdvarlisttwoFactor node) {
        node.getId().apply(this);
        node.getLparen().apply(this);
        node.getVarlisttwo().apply(this);
        node.getRparen().apply(this);

        String rparen = stack.pop();
        String varlisttwo = stack.pop();
        String lparen = stack.pop();
        String id = stack.pop();

        temp = id + lparen + varlisttwo + rparen;
        temp = "";
    }

    public void caseAIdarrvarlisttwoFactor(AIdarrvarlisttwoFactor node) {
        node.getArrayorid().apply(this);
        node.getPeriod().apply(this);
        node.getId().apply(this);
        node.getLparen().apply(this);
        node.getVarlisttwo().apply(this);
        node.getRparen().apply(this);

        String rparen = stack.pop();
        String varlisttwo = stack.pop();
        String lparen = stack.pop();
        String id = stack.pop();
        String period = stack.pop();
        String arrayorid = stack.pop();

        temp = arrayorid + period + id + lparen + varlisttwo + rparen;
        stack.push(temp);
        temp = "";
    }

    public void caseAArrayArrayorid(AArrayArrayorid node) {
        node.getId().apply(this);
        node.getLsquare().apply(this);
        node.getNumber().apply(this);
        node.getRsquare().apply(this);

        String rsquare = stack.pop();
        String number = stack.pop();
        String lsquare = stack.pop();
        String id = stack.pop();

        temp = id + lsquare + number + rsquare;
        stack.push(temp);
        temp = "";

    }

    public void caseAIdArrayorid(AIdArrayorid node) {
        isId = true ;
        node.getId().apply(this);
    }

    public void caseAEmptyproductionOptionalidarray(AEmptyproductionOptionalidarray node) {
        stack.push("");
    }

    public void caseAArrayOptionalidarray(AArrayOptionalidarray node) {
        isId = true ;
        node.getLsquare().apply(this);
        node.getNumber().apply(this);
        node.getRsquare().apply(this);

        String rsquare = stack.pop();
        String number = stack.pop();
        String lsquare = stack.pop();

        temp = lsquare + number + rsquare;
        stack.push(temp);
        temp = "";
    }

    public void caseATrueBoolean(ATrueBoolean node) {
        node.getTrue().apply(this);
    }

    public void caseAFalseBoolean(AFalseBoolean node) {
        node.getFalse().apply(this);
    }

    public void caseACondexprBoolean(ACondexprBoolean node) {
        node.getCondexpr().apply(this);
    }

    public void caseABooleanBoolid(ABooleanBoolid node) {
        node.getBoolean().apply(this);
    }

    public void caseAIdBoolid(AIdBoolid node) {
        node.getId().apply(this);
    }

    public void caseACondexpr(ACondexpr node) {
        String parent = myParent;


        node.getFirstexpr().apply(this);
        boolean firstExprIsId = isId;
        isId = false;
        node.getCond().apply(this);
        node.getSecondexpr().apply(this);
        boolean secondExprIsId = isId;
        isId = false;

        String secondexpr = stack.pop();
        String cond = stack.pop();
        String firstexpr = stack.pop();

        temp = firstexpr + cond + secondexpr;
        stack.push(temp);
        temp = "";

        Variable firstExpVar;
        Variable secondExpVar;
        String firstExpType = "";
        String secondExpType = "";

        if(firstExprIsId)
        {
            if(firstexpr.contains("["))
            {
                firstexpr = firstexpr.replaceAll("[", "") ;
                firstexpr = firstexpr.replaceAll("]", "") ;
            }

            String firstExprIdScope = getVarId(parent, firstexpr) ;
            if(parent.equals(""))
            {
                parent = "GLOBAL" ;
            }
            if(firstExprIdScope != null)
            {
                firstExpVar = symbolTable.getVar(firstExprIdScope) ;
                firstExpType = firstExpVar.getType() ;
                if(firstExpType.equals("STRING") || firstExpType.equals("STRING_ARRAY"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + firstexpr + " is type STRING. Cannot use a string in a conditional expression.") ;
                }
            }
            else
            {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + firstexpr + " has not been declared."
                                + " Cannot use an undeclared variable in a conditional expression.") ;
            }
        }

        if(secondExprIsId)
        {
            if(secondexpr.contains("["))
            {
                secondexpr = secondexpr.replaceAll("[", "") ;
                secondexpr = secondexpr.replaceAll("]", "") ;
            }

            String secondExprIdScope = getVarId(parent, secondexpr) ;
            if(parent.equals(""))
            {
                parent = "GLOBAL" ;
            }
            if(secondExprIdScope != null)
            {
                secondExpVar = symbolTable.getVar(secondExprIdScope) ;
                secondExpType = secondExpVar.getType() ;
                if(secondExpType.equals("STRING") || secondExpType.equals("STRING_ARRAY"))
                {
                    error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + secondexpr + " is type STRING. Cannot use a string in a conditional expression.") ;
                }
            }
            else
            {
                error.add("Error in " + parent.replaceAll("\\d", "") + ". " +
                        "The variable " + secondexpr + " has not been declared."
                                + " Cannot use an undeclared variable in a conditional expression.") ;
            }
        }

    }

    public void caseALtCond(ALtCond node) {
        node.getCondlt().apply(this);
    }

    public void caseAGtCond(AGtCond node) {
        node.getCondgt().apply(this);
    }

    public void caseAEqualCond(AEqualCond node) {
        node.getCondeq().apply(this);
    }

    public void caseANotequalCond(ANotequalCond node) {
        node.getCondneq().apply(this);
    }

    public void caseAGeqCond(AGeqCond node) {
        node.getCondgeq().apply(this);
    }

    public void caseALeqCond(ALeqCond node) {
        node.getCondleq().apply(this);
    }

    public void caseAPlusAddop(APlusAddop node) {
        node.getPlus().apply(this);
    }

    public void caseAMinusAddop(AMinusAddop node) {
        node.getMinus().apply(this);
    }

    public void caseAMultiplyMultop(AMultiplyMultop node) {
        node.getMultiply().apply(this);
    }

    public void caseADivideMultop(ADivideMultop node) {
        node.getDivide().apply(this);
    }

    public void caseAIntType(AIntType node) {
        node.getTint().apply(this);
    }

    public void caseARealType(ARealType node) {
        node.getTreal().apply(this);
    }

    public void caseAStringType(AStringType node) {
        node.getTstring().apply(this);
    }

    public void caseABoolType(ABoolType node) {
        node.getTbool().apply(this);
    }

    public void caseAVoidType(AVoidType node) {
        node.getTvoid().apply(this);
    }

    public void caseAIdType(AIdType node) {
        node.getId().apply(this);
    }

    public void caseTTint(TTint node) {
        stack.push(node.getText());
    }

    public void caseTTreal(TTreal node) {
        stack.push(node.getText());
    }

    public void caseTTstring(TTstring node) {
        stack.push(node.getText());
    }

    public void caseTTbool(TTbool node) {
        stack.push(node.getText());
    }

    public void caseTTvoid(TTvoid node) {
        stack.push(node.getText());
    }
    public void caseTIf(TIf node) {
        stack.push(node.getText());
    }

    public void caseTThen(TThen node) {
        stack.push(node.getText());
    }

    public void caseTWhile(TWhile node) {
        stack.push(node.getText());
    }

    public void caseTElse(TElse node) {
        stack.push(node.getText());
    }

    public void caseTIncrement(TIncrement node) {
        stack.push(node.getText());
    }

    public void caseTDecrement(TDecrement node) {
        stack.push(node.getText());
    }

    public void caseTGet(TGet node) {
        stack.push(node.getText());
    }
    public void caseTNew(TNew node) {
        stack.push(node.getText());
    }

    public void caseTReturn(TReturn node) {
        stack.push(node.getText());
    }

    public void caseTPut(TPut node) {
        stack.push(node.getText());
    }

    public void caseTTclass(TTclass node) {
        stack.push(node.getText());
    }

    public void caseTFor(TFor node) {
        stack.push(node.getText());
    }

    public void caseTSwitch(TSwitch node) {
        stack.push(node.getText());
    }

    public void caseTBreak(TBreak node) {
        stack.push(node.getText());
    }

    public void caseTCase(TCase node) {
        stack.push(node.getText());
    }

    public void caseTDefault(TDefault node) {
        stack.push(node.getText());
    }

    public void caseTBegin(TBegin node) {
        stack.push(node.getText());
    }

    public void caseTEnd(TEnd node) {
        stack.push(node.getText());
    }

    public void caseTTrue(TTrue node) {
        stack.push(node.getText());
    }

    public void caseTFalse(TFalse node) {
        stack.push(node.getText());
    }

    public void caseTLparen(TLparen node) {
        stack.push(node.getText());
    }

    public void caseTRparen(TRparen node) {
        stack.push(node.getText());
    }

    public void caseTLsquare(TLsquare node) {
        stack.push(node.getText());
    }

    public void caseTRsquare(TRsquare node) {
        stack.push(node.getText());
    }

    public void caseTLcurly(TLcurly node) {
        stack.push(node.getText());
    }

    public void caseTRcurly(TRcurly node) {
        stack.push(node.getText());
    }

    public void caseTPeriod(TPeriod node) {
        stack.push(node.getText());
    }

    public void caseTComma(TComma node) {
        stack.push(node.getText());
    }

    public void caseTSemicolon(TSemicolon node) {
        stack.push(node.getText());
    }

    public void caseTColon(TColon node) {
        stack.push(node.getText());
    }

    public void caseTAssignment(TAssignment node) {
        stack.push(node.getText());
    }

    public void caseTId(TId node) {
        isId = true ;
        stack.push(node.getText());
    }

    public void caseTNumber(TNumber node) {

        stack.push(node.getText());
    }

    public void caseTReal(TReal node) {

        stack.push(node.getText());
    }

    public void caseTPlus(TPlus node) {

        stack.push(node.getText());
    }

    public void caseTMinus(TMinus node) {

        stack.push(node.getText());
    }

    public void caseTMultiply(TMultiply node) {

        stack.push(node.getText());
    }

    public void caseTDivide(TDivide node) {

        stack.push(node.getText());
    }

    public void caseTCondlt(TCondlt node) {

        stack.push(node.getText());
    }

    public void caseTCondgt(TCondgt node) {

        stack.push(node.getText());
    }

    public void caseTCondeq(TCondeq node) {

        stack.push(node.getText());
    }

    public void caseTCondneq(TCondneq node) {

        stack.push(node.getText());
    }

    public void caseTCondgeq(TCondgeq node) {

        stack.push(node.getText());
    }

    public void caseTCondleq(TCondleq node) {

        stack.push(node.getText());
    }

    public void caseTAnychars(TAnychars node) {

        stack.push(node.getText());
    }

    public static String getVarId(String parent, String id){
        String idScope ;
        int pIndex = 0;

        while(!parentArr.get(pIndex).equals(parent) && pIndex < parentArr.size() - 1){
            pIndex++;
        }

        idScope = id + "_" + parentArr.get(pIndex);
        while(!symbolTable.containsVar(idScope) && pIndex < parentArr.size() - 1){
            pIndex++ ;
            idScope = id + "_" + parentArr.get(pIndex);
        }

        if(pIndex >= parentArr.size())
        {
            if(!symbolTable.containsVar(id))
            {
                return null ;
            }
            else
            {
                Variable v = symbolTable.getVar(id);
                return v.getName() ;
            }
        }
        else
        {
            if(!symbolTable.containsVar(idScope))
            {
                if(!symbolTable.containsVar(id)){
                    return null ;
                }
                else
                {
                    Variable v = symbolTable.getVar(id);
                    return v.getName();
                }
            }
            else {
                Variable v = symbolTable.getVar(idScope);
                return v.getName() ;
            }
        }
    }
    
   /* public String scopeMethod(String varId)
    {
       
        HashMap<String, Classes> classesMap = symbolTable.getAllClasses() ;
        Set<String> keys = classesMap.keySet() ;
        for(int i = 0; i < parentArr.size() ; i++)
        {
            String parentId = parentArr.get(i); 
            
            for(String k : keys)
            {
                Classes c = classesMap.get(k) ;
                HashMap<String, Method> methodMap = c.getAllMethods() ;
                
                if(methodMap.containsKey(parentId))
                {
                    Method m = methodMap.get(parentArr.get(i)) ;
                    if(m.containsVar(varId)){
                        return parentArr.get(i);
                    }
                }
                
            }
        }
        
        return "" ;
    }
    */
    
    public static String getVarIdInMethodInClass(String parent, String id, String varClassName, String varMethodName){
        String idScope ;
        int pIndex = 0;

        if(!parent.equals(varMethodName))
        {
            while(!parentArr.get(pIndex).equals(parent) && pIndex < parentArr.size() - 1){
                pIndex++;
            }   
            
            Classes tempClass = symbolTable.getMyClass(varClassName) ;
            Method tempMethod = tempClass.getMethod(varMethodName) ;
            HashMap <String, Variable> methodVariables = tempMethod.getAllVars() ;
            idScope = id + "_" + parent ;
            
            while(!methodVariables.containsKey(idScope) && pIndex < parentArr.size() - 1){
                pIndex++ ;
                idScope = id + "_" + parentArr.get(pIndex);
            }
            
            if(pIndex >= parentArr.size())
            {   //check for local var declaration in method 
                if(!methodVariables.containsKey(id))
                {
                    return null ;
                }
                else
                {
                    Variable v = methodVariables.get(id);
                    return v.getName() ;
                }
            }
            else
            {   //check case when method uses global var
                if(!symbolTable.containsVar(idScope))
                {
                    if(!symbolTable.containsVar(id)){
                        return null ;
                    }
                    else
                    {
                        Variable v = symbolTable.getVar(id);
                        return v.getName();
                    }
                }
                else {
                    Variable v = symbolTable.getVar(idScope);
                    return v.getName() ;
                }
            }
        }
        
        return "" ;
    } 
    
    public static String getVarIdInMethodNotInClass(String parent, String id, String varMethodName){
        String idScope ;
        int pIndex = 0;

        if(!parent.equals(varMethodName))
        {
            while(!parentArr.get(pIndex).equals(parent) && pIndex < parentArr.size() - 1){
                pIndex++;
            }   
            
            Method tempMethod = symbolTable.getMethod(varMethodName) ;
            HashMap <String, Variable> methodVariables = tempMethod.getAllVars() ;
            idScope = id + "_" + parent ;
            
            while(!methodVariables.containsKey(idScope) && pIndex < parentArr.size() - 1){
                pIndex++ ;
                idScope = id + "_" + parentArr.get(pIndex);
            }
            
            if(pIndex >= parentArr.size())
            {   //check for local var declaration in method 
                if(!methodVariables.containsKey(id))
                {
                    return null ;
                }
                else
                {
                    Variable v = methodVariables.get(id);
                    return v.getName() ;
                }
            }
            else
            {   //check case when method uses global var
                if(!symbolTable.containsVar(idScope))
                {
                    if(!symbolTable.containsVar(id)){
                        return null ;
                    }
                    else
                    {
                        Variable v = symbolTable.getVar(id);
                        return v.getName();
                    }
                }
                else {
                    Variable v = symbolTable.getVar(idScope);
                    return v.getName() ;
                }
            }
        }
        
        return "" ;
    } 
}
