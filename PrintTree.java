package ProjFive;

import ProjFive.analysis.*;
import ProjFive.node.*;
import java.util.*;

public class PrintTree extends DepthFirstAdapter {
	public SymbolTable symbolTable;
	private Stack<String> stack;
	private String temp;
        public Stack<String> error ;

	public PrintTree() {
            symbolTable = new SymbolTable();
            stack = new Stack<String>();
            temp = new String();
            error = new Stack<>() ;
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
	
	public void caseAClassdeclClassmethodstmt(AClassdeclClassmethodstmt node) {
		node.getTclass().apply(this);
		node.getId().apply(this);
		node.getLcurly().apply(this);
		node.getMethodstmtseqs().apply(this);
		node.getRcurly().apply(this);
		
		String rcurly = stack.pop();
		String methodstmtseq = stack.pop();
		String lcurly = stack.pop();
		String id = stack.pop();
		String classstmt = stack.pop();
		
		temp = classstmt + id + lcurly + methodstmtseq + rcurly ;
		//System.out.println("class decl " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseATypevarliststmtClassmethodstmt(ATypevarliststmtClassmethodstmt node) {
		node.getType().apply(this);
		node.getId().apply(this);
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
                
                String paramId = "";
                String paramType = "";
                if(!varlist.equals("")){
                    String[] varlistArr = varlist.split(",");
                    for(int i = 0; i < varlistArr.length ; i++ ){
                        String[] varInfo = varlistArr[i].split(":");
                        paramId = varInfo[0];
                        paramType = varInfo[1];
                        Variable v = new Variable(paramId, paramType);
                        m.addParam(v);
                    }
                }
                
                if(type.equals("VOID")){
                    if(stmtseq.contains("RETURN")){
                        error.add("Void method cannot return value;");
                    }
                }
                
                symbolTable.addMethod(m);
                        
		temp = type + id + lparen + varlist + rparen + lcurly + stmtseq + rcurly ;
		//System.out.println("varlist classmethodstmt " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseAIdlisttypeClassmethodstmt(AIdlisttypeClassmethodstmt node) {
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
                
                if(!idlist.equals("")){
                    String[] ids = idlist.split(",");
                    for(int i = 0; i < ids.length ; i++ ){
                        Variable var = new Variable(ids[i], type);
                        symbolTable.addVar(var);
                    }
                }
		
		temp = id + idlist + colon + type + semicolon ;
		//System.out.println("idlist classmethodstmt " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAOneormoreMethodstmtseqs(AOneormoreMethodstmtseqs node) {
		node.getMethodstmtseqs().apply(this);
		node.getMethodstmtseq().apply(this);
		
		String methodstmtseqs = stack.pop();
		String methodstmtseq = stack.pop();
		
		temp = methodstmtseq + methodstmtseqs ;
		//System.out.println("one or more methodstmtseq " + temp );
		stack.push(temp);
		temp = "";
	}

	public void caseAEmptyproductionMethodstmtseqs(AEmptyproductionMethodstmtseqs node) {
		stack.push("") ;
	}
	
	public void caseATypevarlistMethodstmtseq(ATypevarlistMethodstmtseq node) {
		node.getType().apply(this);
		node.getId().apply(this);
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
                
                if(!varlist.equals("")){
                    String[] varlistArr = varlist.split(","); 
                    for( int i = 0 ; i < varlistArr.length ; i++ ){
                        String[] params = varlistArr[i].split(":");
                        String paramId = params[0];
                        String paramType = params[1];
                        
                        Variable v = new Variable(paramId, paramType);
                        m.addParam(v);
                    }
                }
                
                symbolTable.addMethod(m);
		
		temp = type + id + lparen + varlist + rparen + lcurly + stmtseq + rcurly ;
		//System.out.println("Typevarlist methodstmtseq " + temp );
		stack.push(temp);
		temp = "";
	}

	public void caseAIdtypeMethodstmtseq(AIdtypeMethodstmtseq node) {
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
                if(!idlist.equals("")){
                    String[] ids = idlist.split(",");
                    for(int i = 0; i < ids.length ; i++ ){
                        Variable var = new Variable(ids[i], type);
                    }
                }
		
		temp = id + idlist + colon + type + semicolon ; 
		//System.out.println("Idtype methodstmtseq " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseAAssignstringMethodstmtseq(AAssignstringMethodstmtseq node) {
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
		
		temp = id + arr + assignment + anychars + semicolon ;
		//System.out.println("assignment anychars methodstmtseq " + temp );
		stack.push(temp);
		temp = "";
	}
	
	public void caseAPrintstmtMethodstmtseq(APrintstmtMethodstmtseq node){
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
		
		temp = put + lparen + id + arr + rparen + semicolon ;
		//System.out.println("print methodstmtseq " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseAAssignmentMethodstmtseq(AAssignmentMethodstmtseq node) {
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
		
		temp = id + arr + assignment + get + lparen + rparen + semicolon ;
		//System.out.println("assignment methodstmtseq " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseAIncrementMethodstmtseq(AIncrementMethodstmtseq node) {	
            node.getId().apply(this);
            node.getOptionalidarray().apply(this);
            node.getIncrement().apply(this);
            node.getSemicolon().apply(this);

            String semicolon = stack.pop();
            String incr = stack.pop();
            String arr = stack.pop();
            String id = stack.pop();

            if(!symbolTable.containsVar(id)){
                error.add("The variable " + id + " has not been declared and cannot be incremented.");
            }
            else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if(type.equals("STRING")){
                    error.add("The variable " + id + " is type STRING and cannot be incremented.");
                }
                else if(type.equals("BOOLEAN")){
                    error.add("The variable " + id + " is type BOOLEAN and cannot be incremented.");
                }
            }

            temp = id + arr + incr + semicolon ;
            stack.push(temp);
            temp = "";
	}
	
	public void caseADecrementMethodstmtseq(ADecrementMethodstmtseq node) {
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getDecrement().apply(this);
		node.getSemicolon().apply(this);

		String semicolon = stack.pop();
		String decr = stack.pop();
		String arr = stack.pop();
		String id = stack.pop();
                
                if(!symbolTable.containsVar(id)){
                    error.add("The variable " + id + " has not been declared and cannot be decremented.");
                }
                else {
                    Variable v = symbolTable.getVar(id);
                    String type = v.getType();
                    if(type.equals("STRING")){
                        error.add("The variable " + id + " is type STRING and cannot be decremented.");
                    }
                    else if(type.equals("BOOLEAN")){
                        error.add("The variable " + id + " is type BOOLEAN and cannot be decremented.");
                    }
                }
		
		temp = id + arr + decr + semicolon ;
		//System.out.println("decr methodstmtseq " + temp);
		stack.push(temp);
		temp = "";
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
                
                if(!arr.equals("")){
                    String type = secondid + " Array";
                    Variable v = new Variable(id, type);
                    symbolTable.addVar(v);
                }
                else {
                    Variable v = new Variable(id, secondid);
                    symbolTable.addVar(v);
                }
                
		temp = id + arr + assignment + newstmt + secondid + lparen + rparen + semicolon ;
		//System.out.println("object decl methodstmtseq " + temp );
		stack.push(temp);
		temp = "";
	}
	
	public void caseAAssignbooleanMethodstmtseq(AAssignbooleanMethodstmtseq node) {				
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
		
		temp = id + arr + assignment + semicolon ;
		//System.out.println("bool methodstmtseq " + temp );
		stack.push(temp);
		temp = "";
	}

	public void caseAOneormoreStmtseq(AOneormoreStmtseq node) {
		node.getStmt().apply(this);
		node.getStmtseq().apply(this);
		
		String seq = stack.pop();
		String stmt = stack.pop();
		
		temp = stmt + seq ;
		//System.out.println("stmtseq " + temp );
		stack.push(temp);
		temp = "";
	}

	public void caseAEmptyproductionStmtseq(AEmptyproductionStmtseq node) {
            stack.push("") ;
	}

	public void caseAExprassignmentStmt(AExprassignmentStmt node) {
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

            //check if the type matches 
            if(!symbolTable.containsVar(id)){
                error.add("The variable " + id + " has not been declared yet. "
                        + "A variable must be declared before it can be asigned a value.");
            }
            else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if(type.equals("INT")){
                    if(expr.contains(".")){
                        error.add("The variable " + id + " is type INT and cannot store a real number.");
                    }
                }
                else if(type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")){
                    if(expr.charAt(0) == '"'){
                        error.add("The variable " + id + " is type " + type + " and cannot store a string");
                    }
                }
                else if(type.equals("BOOLEAN")){
                    if(!expr.equals("TRUE") || !expr.equals("FALSE")){
                        error.add("The variable " + id + " is type BOOLEAN and can only store boolean values") ;
                    }
                }
            }

            temp = id + arr + assignment + expr + semicolon ;
            //System.out.println("assignment stmt " + temp);
            stack.push(temp);
            temp = "";
	}

	public void caseAExpranycharStmt(AExpranycharStmt node) {
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

            //check if variable has been declared
            if(!symbolTable.containsVar(id)){
                error.add("The variable " + id + " has not been declared yet. "
                        + "A variable must be declared before it can be asigned a value.");
            }
            else { //check if the type of the var is a string
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if(!type.equals("STRING")){
                    error.add("The variable " + id + " is not type STRING and cannot store string literals.");
                }
            }


            temp = id + arr + assignment + anychars + semicolon ;
            //System.out.println("anychar stmt " + temp);
            stack.push(temp);
            temp = "";
	} 

	public void caseAIdlistStmt(AIdlistStmt node) {
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

            if(symbolTable.containsVar(id)){
                error.add("The variable " + id + " has already been declared.");
            }
            else {
                Variable v = new Variable(id, type);
                symbolTable.addVar(v);
            }

            if(!idlist.equals("")){
                String[] ids = idlist.split(",");
                for( int i = 0; i < ids.length ; i++ ){
                    if(symbolTable.containsVar(id)){
                        error.add("The variable " + id + " has already been declared.");
                    }
                    else {
                        Variable var = new Variable(ids[i], type);
                        symbolTable.addVar(var);
                    }
                }
            }

            temp = id + idlist + colon + type + arr + semicolon;
            //System.out.println("IdlistStmt " + temp);
            stack.push(temp);
            temp = "";
	}

	public void caseAIfbooleanStmt(AIfbooleanStmt node) {
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
		
		temp = ifstmt + lparen + boolid + rparen + then + elsestmt ;
		//System.out.println("Ifstmt " + temp);
		stack.push(temp);
		temp = "" ;
		
	}

	public void caseAWhileStmt(AWhileStmt node) {
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
		//System.out.println("while stmts" + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAForStmt(AForStmt node) {
		node.getFor().apply(this);
		node.getLparen().apply(this);
		node.getOptionaltype().apply(this);
		node.getId().apply(this);
		node.getAssignment().apply(this);
		node.getExpr().apply(this);
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
                
                if(optionaltype.equals("")){ //if type is not declared must be in symbol table already
                    if(!symbolTable.containsVar(id)){
                        error.add("The variable " + id + " must be declared before it can be assigned a value.");
                    }
                    else { //if it is in symbol table the expr assignment must match the type
                        Variable v = symbolTable.getVar(id);
                        String type = v.getType();
                        if(type.equals("INT")){
                            if(expr.contains(".")){
                                error.add("The variable " + id + " is type INT and cannot store a real number.");
                            }
                        }
                        else if(type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")){
                            if(expr.charAt(0) == '"'){
                                error.add("The variable " + id + " is type " + type + " and cannot store a string");
                            }
                        }
                        else if(type.equals("BOOLEAN")){
                            if(!expr.equals("TRUE") || !expr.equals("FALSE")){
                                error.add("The variable " + id + " is type BOOLEAN and can only store boolean values") ;
                            }
                        }
                    }
                }
                else if(!optionaltype.equals("")){ //if the type is declared and it is already in the symbol table
                    if(symbolTable.containsVar(id)){
                        error.add("The variable " + id + " has already been declared.");
                    }
                    else { //if the optional type is declared expr assignment must match
                        if(optionaltype.equals("INT")){
                            if(expr.contains(".")){
                                error.add("The variable " + id + " is type INT and cannot store a real number.");
                            }
                        }
                        else if(optionaltype.equals("INT") || optionaltype.equals("DOUBLE") || optionaltype.equals("BOOLEAN")){
                            if(expr.charAt(0) == '"'){
                                error.add("The variable " + id + " is type " + optionaltype + " and cannot store a string");
                            }
                        }
                        else if(optionaltype.equals("BOOLEAN")){
                            if(!expr.equals("TRUE") || !expr.equals("FALSE")){
                                error.add("The variable " + id + " is type BOOLEAN and can only store boolean values") ;
                            }
                        }
                    } 
                }
                
                if(!optionaltype.equals("")){
                    Variable v = new Variable(id, optionaltype);
                    symbolTable.addVar(v);
                }
		
		temp = forStmt + lparen + optionaltype + id + assignment + expr + firstsemi + bool + secondsemi + orstmts + rparen + lcurly + stmtseq + rcurly ;
		
		//System.out.println("Forstmts " + temp );
		stack.push(temp);
		temp = "";

	}

	public void caseAGetStmt(AGetStmt node) {
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
		
		temp = id + arr + assignment + get + lparen + rparen + semicolon ;
		//System.out.println("GET STMT " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAPutStmt(APutStmt node) {
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
		
		temp = put + lparen + id + arr + rparen + semicolon ;
		//System.out.println("PUT STMT " + temp);
		stack.push(temp); 
		temp = "";
	}

	public void caseAIncrementStmt(AIncrementStmt node){
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getIncrement().apply(this);
		node.getSemicolon().apply(this);

		String semicolon = stack.pop();
		String increment = stack.pop();
		String arr = stack.pop();
		String id = stack.pop();
                
                if(!symbolTable.containsVar(id)){
                    error.add("The variable " + id + " has not been declared and cannot be incremented.");
                }
                else {
                    Variable v = symbolTable.getVar(id);
                    String type = v.getType();
                    if(type.equals("STRING")){
                        error.add("The variable " + id + " is type STRING and cannot be incremented.");
                    }
                    else if(type.equals("BOOLEAN")){
                        error.add("The variable " + id + " is type BOOLEAN and cannot be incremented.");
                    }
                }
		
		temp = id + arr + increment + semicolon;
		stack.push(temp);
		temp = "";
	}

	public void caseADecrementStmt(ADecrementStmt node) {
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getDecrement().apply(this);
		node.getSemicolon().apply(this);
		
		String semicolon = stack.pop();
		String decrement = stack.pop();
		String arr = stack.pop();
		String id = stack.pop();
                
                if(!symbolTable.containsVar(id)){
                    error.add("The variable " + id + " has not been declared and cannot be decremented.");
                }
                else {
                    Variable v = symbolTable.getVar(id);
                    String type = v.getType();
                    if(type.equals("STRING")){
                        error.add("The variable " + id + " is type STRING and cannot be decremented.");
                    }
                    else if(type.equals("BOOLEAN")){
                        error.add("The variable " + id + " is type BOOLEAN and cannot be decremented.");
                    }
                }
		
		temp = id + arr + decrement + semicolon;
		stack.push(temp);
		temp = "";

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
		String optionalid = stack.pop();
		String id = stack.pop();
                
                if(!optionalid.equals("")){
                    String type = secondid + " Array";
                    Variable v = new Variable(id, type);
                    symbolTable.addVar(v);
                }
                else {
                    Variable v = new Variable(id, secondid);
                    symbolTable.addVar(v);
                }
		
		temp = id + optionalid + assignment + newkeyword + secondid + lparen + rparen + semicolon ;
		stack.push(temp);
		temp = "";

	}

	public void caseAIdvarlisttwoStmt(AIdvarlisttwoStmt node){
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
		
		temp = id + lparen + varlisttwo + rparen + semicolon ;
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

	public void caseAReturnStmt(AReturnStmt node){
		node.getReturn().apply(this);
		node.getExpr().apply(this);
		node.getSemicolon().apply(this);
		
		String semicolon = stack.pop();
		String expr = stack.pop();
		String returnstmt = stack.pop();
                
                if(expr.charAt(0) == '"'){
                    error.add("Cannot return a value that starts with \"");
                }
                
		temp = returnstmt + expr + semicolon ;
		stack.push(temp);
		temp = "";
	}

	public void caseAIdbooleanStmt(AIdbooleanStmt node){
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
                
                //check if the symbol table contains the var
                if(!symbolTable.containsVar(id)){
                    error.add("The variable " + id + " has not been declared and cannot be assigned a value.");
                }
                else { //booleans can only be stored into INT, REAL or BOOLEAN 
                    Variable v = symbolTable.getVar(id);
                    String type = v.getType();
                    if(type.equals("STRING")){
                        error.add("The variable " + id + " is type STRING and cannot store a boolean value.");
                    }
                }
                
                
		temp = id + arr + bool + semicolon ;
		stack.push(temp);
		temp = "";
	}

	public void caseASwitchStmt(ASwitchStmt node){
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
		
		temp = switchstmt + lparen + expr + rparen + lcurly + casestmt + secondlparen + number + secondrparen + colon + stmtseq + optlbreak + optlswitch + defaultstmt + secondcolon + secondstmtseq + rcurly ;
		//System.out.println("switch stmts " + temp );
		stack.push(temp);
		temp = "";
	}

	public void caseACommaidlistOptlidlist(ACommaidlistOptlidlist node) {
		node.getComma().apply(this);
		node.getId().apply(this);
		node.getOptlidlist().apply(this);

		String optlid = stack.pop();
		String id = stack.pop();
		String comma = stack.pop();
		
		temp = comma + id + optlid ;
		//System.out.println("Optlidlist " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAEmptyproductionOptlidlist(AEmptyproductionOptlidlist node) {
		stack.push("");
	}

	public void caseANoelseOptionalelse(ANoelseOptionalelse node){	
		node.getLcurly().apply(this);
		node.getStmtseq().apply(this);
		node.getRcurly().apply(this);

		String rcurly = stack.pop();
		String stmtseq = stack.pop();
		String lcurly = stack.pop();
		
		temp = lcurly + stmtseq + rcurly ;
		//System.out.println("Noelse " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAElseOptionalelse(AElseOptionalelse node){
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

		temp = lcurly + stmtseq + rcurly + elsestmt + secondlcurly + secondstmtseq + secondrcurly ;
		//System.out.println("Optlelse " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseACaselistOptionalswitchcases(ACaselistOptionalswitchcases node){
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
		
		temp = cases + lparen + number + rparen + colon + stmtseq + br + optlswitches ;
		//System.out.println("switch cases " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAEmptyproductionOptionalswitchcases(AEmptyproductionOptionalswitchcases node) {
		stack.push("");
	}

	public void caseABreakOptlbreak(ABreakOptlbreak node){
		node.getBreak().apply(this);
		node.getSemicolon().apply(this);
		
		String semicolon = stack.pop();
		String br = stack.pop();
		temp = br + semicolon;
		//System.out.println("optlbreak " + temp);
		stack.push(temp);
		temp = "" ;
	}

	public void caseAEmptyproductionOptlbreak(AEmptyproductionOptlbreak node) {
		stack.push("");
	}

	public void caseAIncrementOrstmts(AIncrementOrstmts node) {
		node.getId().apply(this);
		node.getIncrement().apply(this);
		
		String incr = stack.pop();
		String id = stack.pop();
                
                if(!symbolTable.containsVar(id)){
                    error.add("The variable " + id + " has not been declared and cannot be incremented.");
                }
                else {
                    Variable v = symbolTable.getVar(id);
                    String type = v.getType();
                    if(type.equals("STRING")){
                        error.add("The variable " + id + " is type STRING and cannot be incremented.");
                    }
                    else if(type.equals("BOOLEAN")){
                        error.add("The variable " + id + " is type BOOLEAN and cannot be incremented.");
                    }
                }
		
		temp = id + incr;
		//System.out.println("increment " + temp );
		stack.push(temp);
		temp = "";
	}

	public void caseADecrementOrstmts(ADecrementOrstmts node) {
		node.getId().apply(this);
		node.getDecrement().apply(this);
		
		String decr = stack.pop();
		String id = stack.pop();
		
                if(!symbolTable.containsVar(id)){
                    error.add("The variable " + id + " has not been declared and cannot be decremented.");
                }
                else {
                    Variable v = symbolTable.getVar(id);
                    String type = v.getType();
                    if(type.equals("STRING")){
                        error.add("The variable " + id + " is type STRING and cannot be decremented.");
                    }
                    else if(type.equals("BOOLEAN")){
                        error.add("The variable " + id + " is type BOOLEAN and cannot be decremented.");
                    }
                }
                
		temp = id + decr;
		//System.out.println("decrement " + temp );
		stack.push(temp);
		temp = "";

	}

	public void caseAAssignmentOrstmts(AAssignmentOrstmts node) {
            node.getId().apply(this);
            node.getAssignment().apply(this);
            node.getExpr().apply(this);

            String expr = stack.pop();
            String assignment = stack.pop();
            String id = stack.pop();

            if(!symbolTable.containsVar(id)){
                error.add("The variable " + id + " must be declared before it can be assigned a value.");
            }
            else { //if it is in symbol table the expr assignment must match the type
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if(type.equals("INT")){
                    if(expr.contains(".")){
                        error.add("The variable " + id + " is type INT and cannot store a real number.");
                    }
                }
                else if(type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")){
                    if(expr.charAt(0) == '"'){
                        error.add("The variable " + id + " is type " + type + " and cannot store a string");
                    }
                }
                else if(type.equals("BOOLEAN")){
                    if(!expr.equals("TRUE") || !expr.equals("FALSE")){
                        error.add("The variable " + id + " is type BOOLEAN and can only store boolean values") ;
                    }
                }
            }


            temp = id + assignment + expr ;
            //System.out.println("AssignmentStmt "  + temp );
            stack.push(temp);
            temp = "";
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
		
		temp = period + id + lparen + varlist + rparen + optlvarlisttwo ;
		//System.out.println("Optlidvarlisttwo " + temp );
		stack.push(temp);
		temp = "";
	}
	
	public void caseAEmptyproductionOptlidvarlisttwo(AEmptyproductionOptlidvarlisttwo node) {
		stack.push("");
	}

	public void caseATypeOptionaltype(ATypeOptionaltype node) {
		node.getType().apply(this);
		
		temp = stack.pop();
		//System.out.println("optionaltype " + temp);
		stack.push(temp);
		temp = "";
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
		
		temp = id + colon + type + arr + commaid ;
		//System.out.println("MultipleVarlist " + temp );
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
		
		temp = comma + varlist ;
		//System.out.println("OptlCommaidarray " + temp );
		stack.push(temp);
		temp = "";
	}
	
	public void caseAEmptyproductionCommaidarray(AEmptyproductionCommaidarray node){
		stack.push("");
	}

	public void caseAMultipleVarlisttwo(AMultipleVarlisttwo node){
		node.getExprorbool().apply(this);
		node.getMorevarlisttwo().apply(this);
		
		String varlisttwo = stack.pop();
		String expr = stack.pop();
		temp = expr + varlisttwo ;
		//System.out.println("MultipleVarlisttwo " + temp);
		stack.push(temp);
		temp = "";
	}
	

	public void caseAEmptyproductionVarlisttwo(AEmptyproductionVarlisttwo node) {
		stack.push("");
	}
	
	public void caseAExprExprorbool(AExprExprorbool node) {
		node.getExpr().apply(this);
		
		temp = stack.pop();
		//System.out.println("ExprExprorbool " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseABooleanExprorbool(ABooleanExprorbool node) {
		node.getBoolean().apply(this);
		
		temp = stack.pop();
		//System.out.println("BoolExprorbool " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseAMultipleMorevarlisttwo(AMultipleMorevarlisttwo node) {
		node.getComma().apply(this);
		node.getVarlisttwo().apply(this);
		
		String varlisttwo = stack.pop();
		String comma = stack.pop();
		
		temp = comma + varlisttwo;
		//System.out.println("MultipleMorevarlisttwo " + temp);
		stack.push(temp);
		temp = "" ;
	}
	
	public void caseAEmptyproductionMorevarlisttwo(AEmptyproductionMorevarlisttwo node) {
		stack.push("");
	}

	public void caseAMultipleExpr(AMultipleExpr node) {
		node.getExpr().apply(this);
		node.getAddop().apply(this);
		node.getTerm().apply(this);
		
		String term = stack.pop();
		String addop = stack.pop();
		String expr = stack.pop();
		
		temp = expr + addop + term ;
		//System.out.println("MultipleExpr " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseASingleExpr(ASingleExpr node) {
		node.getTerm().apply(this);
		
		temp = stack.pop();
		//System.out.println("SingleExpr " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseATermmultopTerm(ATermmultopTerm node) {
		node.getTerm().apply(this);
		node.getMultop().apply(this);
		node.getFactor().apply(this);

		String factor = stack.pop();
		String mult = stack.pop();
		String term = stack.pop();
		
		temp = term + mult + factor;
		//System.out.println("TermMultop " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAFactorTerm(AFactorTerm node) {
		node.getFactor().apply(this);
		
		temp = stack.pop();
		
		//System.out.println("FactorTerm " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAParenexprFactor(AParenexprFactor node) {	
		node.getLparen().apply(this);
		node.getExpr().apply(this);
		node.getRparen().apply(this);
		
		String rparen = stack.pop();
		String expr = stack.pop();
		String lparen = stack.pop();
		temp = lparen + expr + rparen;
		//System.out.println("ParenexprFactor " + temp );
		stack.push(temp);
		temp = "";

	}

	public void caseAMinusfactorFactor(AMinusfactorFactor node) {
		node.getMinus().apply(this);
		node.getFactor().apply(this);
		
		String factor = stack.pop();
		String minus = stack.pop();
		
		temp = minus + factor ;
		//System.out.println("minusfactor " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAIntFactor(AIntFactor node) {
		node.getNumber().apply(this);
		temp = stack.pop();
		//System.out.println("intFactor " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseARealFactor(ARealFactor node) {
		node.getReal().apply(this);
		temp = stack.pop();
		//System.out.println("realfactor " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAIdarrayFactor(AIdarrayFactor node) {
		node.getArrayorid().apply(this);
		
		temp = stack.pop();
		//System.out.println("IdarrayFactor " + temp );
		stack.push(temp);
		temp = "";
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
		
		temp = id + lparen + varlisttwo + rparen ;
		//System.out.println("IdvarlisttwoFactor " + temp);
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
		
		temp = arrayorid + period + id + lparen + varlisttwo + rparen ;
		//System.out.println("IdarrvarlisttwoFactor " + temp );
		stack.push(temp);
		temp = "";

	}
	
	public void caseAArrayArrayorid(AArrayArrayorid node){
		node.getId().apply(this);
		node.getLsquare().apply(this);
		node.getNumber().apply(this);
		node.getRsquare().apply(this);
		
		String rsquare = stack.pop();
		String number = stack.pop();
		String lsquare = stack.pop();
		String id = stack.pop();
		
		temp = id + lsquare + number + rsquare ;
		//System.out.println("arrayarrayorid " + temp);
		stack.push(temp);
		temp = "";

	}
	
	public void caseAIdArrayorid(AIdArrayorid node) {
		node.getId().apply(this);
		temp = stack.pop();
		//System.out.println("idarrayorid " + temp);
		stack.push(temp);
		temp = "";

	}

	public void caseAEmptyproductionOptionalidarray(AEmptyproductionOptionalidarray node)
	{
		stack.push("") ;
	}

	public void caseAArrayOptionalidarray(AArrayOptionalidarray node) {
		node.getLsquare().apply(this);
		node.getNumber().apply(this);
		node.getRsquare().apply(this);
		
		String rsquare = stack.pop();
		String number = stack.pop();
		String lsquare = stack.pop();
		
		temp = rsquare + number + lsquare ;
		//System.out.println(temp);
		stack.push(temp);
		temp = "";
	}

	public void caseATrueBoolean(ATrueBoolean node) {
		node.getTrue().apply(this);
		temp = stack.pop();
		//System.out.println("truebool " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAFalseBoolean(AFalseBoolean node) {
		node.getFalse().apply(this);
		temp = stack.pop();
		//System.out.println("falsebool " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseACondexprBoolean(ACondexprBoolean node) {
		node.getCondexpr().apply(this);
		temp = stack.pop();
		//System.out.println("condexprboolean " + temp );
		stack.push(temp);
		temp = "";
	}
	
	public void caseABooleanBoolid(ABooleanBoolid node) {
		node.getBoolean().apply(this);
		temp = stack.pop();
		//System.out.println("boolboolid " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseAIdBoolid(AIdBoolid node) {
		node.getId().apply(this);
		temp = stack.pop();
		//System.out.println("idboolid " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseACondexpr(ACondexpr node) {
		node.getFirstexpr().apply(this);
		node.getCond().apply(this);
		node.getSecondexpr().apply(this);
		
		String secondexpr = stack.pop();
		String cond = stack.pop();
		String firstexpr = stack.pop();
		
		temp = firstexpr + cond + secondexpr ;
		//System.out.println("condexpr : " + temp);
		stack.push(temp);
		temp = "";
		
	}

	public void caseALtCond(ALtCond node) {
		node.getCondlt().apply(this);
		temp = stack.pop();
		//System.out.println("cond " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAGtCond(AGtCond node) {
		node.getCondgt().apply(this);
		temp = stack.pop();
		//System.out.println("cond " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAEqualCond(AEqualCond node) {
		node.getCondeq().apply(this);
		temp = stack.pop();
		//System.out.println("cond " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseANotequalCond(ANotequalCond node) {
		node.getCondneq().apply(this);
		temp = stack.pop();
		//System.out.println("cond " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAGeqCond(AGeqCond node) {
		node.getCondgeq().apply(this);
		temp = stack.pop();
		//System.out.println("cond " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseALeqCond(ALeqCond node) {
		node.getCondleq().apply(this);
		temp = stack.pop();
		//System.out.println("cond " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAPlusAddop(APlusAddop  node) {
		node.getPlus().apply(this);
		temp = stack.pop();
		//System.out.println("add " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAMinusAddop(AMinusAddop node) {
		node.getMinus().apply(this);
		temp = stack.pop();
		//System.out.println("add " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAMultiplyMultop(AMultiplyMultop  node) {
		node.getMultiply().apply(this);
		temp = stack.pop();
		//System.out.println("mult " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseADivideMultop(ADivideMultop node) {
		node.getDivide().apply(this);
		temp = stack.pop();
		//System.out.println("mult " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAIntType(AIntType node) {
		node.getTint().apply(this);
		temp = stack.pop();
		//System.out.println("inttype " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseARealType(ARealType node) {
		node.getTreal().apply(this);
		temp = stack.pop();
		//System.out.println("type " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAStringType(AStringType node) {
		node.getTstring().apply(this);
		temp = stack.pop();
		//System.out.println("type " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseABoolType(ABoolType node) {
		node.getTbool().apply(this);
		temp = stack.pop();
		//System.out.println("type " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAVoidType(AVoidType node) {
		node.getTvoid().apply(this);
		temp = stack.pop();
		//System.out.println("void " + temp);
		stack.push(temp);
		temp = "";
	}

	public void caseAIdType(AIdType node) {
		node.getId().apply(this);
		temp = stack.pop();
		//System.out.println("idtype " + temp);
		stack.push(temp);
		temp = "";
	}
	
	public void caseTTint(TTint node){
		
		stack.push(node.getText());
	}
	
	public void caseTTreal(TTreal node){
		
		stack.push(node.getText());
	}
	
	public void caseTTstring(TTstring node){
		
		stack.push(node.getText());
	}
	
	public void caseTTbool(TTbool node){
		
		stack.push(node.getText());
	}
	
	public void caseTTvoid(TTvoid node){
		
		stack.push(node.getText());
	}
	public void caseTIf(TIf node){
		
		stack.push(node.getText());
	}
	
	public void caseTThen(TThen node){
		
		stack.push(node.getText());
	}
	
	public void caseTWhile(TWhile node){
		
		stack.push(node.getText());
	}
	
	public void caseTElse(TElse node){
		
		stack.push(node.getText());
	}
	
	public void caseTIncrement(TIncrement node){
		
		stack.push(node.getText());
	}
	
	public void caseTDecrement(TDecrement node){
		
		stack.push(node.getText());
	}
	
	public void caseTGet(TGet node){
		
		stack.push(node.getText());
	}
	public void caseTNew(TNew node){
		
		stack.push(node.getText());
	}
	
	public void caseTReturn(TReturn node){
		
		stack.push(node.getText());
	}
	
	public void caseTPut(TPut node){
		
		stack.push(node.getText());
	}
	
	public void caseTTclass(TTclass node) {
		stack.push(node.getText());
	}
	
	public void caseTFor(TFor node){
		
		stack.push(node.getText());
	}
	
	public void caseTSwitch(TSwitch node){
		
		stack.push(node.getText());
	}
	
	public void caseTBreak(TBreak node){
		
		stack.push(node.getText());
	}
	
	public void caseTCase(TCase node){
		
		stack.push(node.getText());
	}
	
	public void caseTDefault(TDefault node){
		
		stack.push(node.getText());
	}
	
	public void caseTBegin(TBegin node){
		
		stack.push(node.getText());
	}
	
	public void caseTEnd(TEnd node){
		
		stack.push(node.getText());
	}
	
	public void caseTTrue(TTrue node){
		
		stack.push(node.getText());
	}
	
	public void caseTFalse(TFalse node){
		
		stack.push(node.getText());
	}
	
	public void caseTLparen(TLparen node){
		
		stack.push(node.getText());
	}
	
	public void caseTRparen(TRparen node){
		
		stack.push(node.getText());
	}
	
	public void caseTLsquare(TLsquare node){
		
		stack.push(node.getText());
	}
	
	public void caseTRsquare(TRsquare node){
		
		stack.push(node.getText());
	}
	
	public void caseTLcurly(TLcurly node){
		
		stack.push(node.getText());
	}
	
	public void caseTRcurly(TRcurly node){
		
		stack.push(node.getText());
	}
	
	public void caseTPeriod(TPeriod node){
		stack.push(node.getText());
	}
	
	public void caseTComma(TComma node){
		
		stack.push(node.getText());
	}
	
	public void caseTSemicolon(TSemicolon node){
		
		stack.push(node.getText());
	}
	
	public void caseTColon(TColon node){
		
		stack.push(node.getText());
	}
	
	public void caseTAssignment(TAssignment node){
		
		stack.push(node.getText());
	}
	
	public void caseTId(TId node){
		
		stack.push(node.getText());
	}
	
	public void caseTNumber(TNumber node){
		
		stack.push(node.getText());
	}
	
	public void caseTReal(TReal node){
		
		stack.push(node.getText());
	}
	
	public void caseTPlus(TPlus node){
		
		stack.push(node.getText());
	}
	
	public void caseTMinus(TMinus node){
		
		stack.push(node.getText());
	}
	
	public void caseTMultiply(TMultiply node){
		
		stack.push(node.getText());
	}
	
	public void caseTDivide(TDivide node){
		
		stack.push(node.getText());
	}
	
	public void caseTCondlt(TCondlt node){
		
		stack.push(node.getText());
	}
	
	public void caseTCondgt(TCondgt node){
		
		stack.push(node.getText());
	}
	
	public void caseTCondeq(TCondeq node){
		
		stack.push(node.getText());
	}
	
	public void caseTCondneq(TCondneq node){
		
		stack.push(node.getText());
	}
	
	public void caseTCondgeq(TCondgeq node){
		
		stack.push(node.getText());
	}
	
	public void caseTCondleq(TCondleq node){
		
		stack.push(node.getText());
	}

	public void caseTAnychars(TAnychars node){
		
		stack.push(node.getText());
	}
}

