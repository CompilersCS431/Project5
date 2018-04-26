package ProjFive;

import ProjFive.analysis.*;
import ProjFive.node.*;
import java.util.*;

public class PrintTree extends DepthFirstAdapter {
    public SymbolTable symbolTable;
    private Stack < String > stack;
    private String temp;
    public ArrayList < String > error;
    private boolean isId;
    private String myParent;
    private String prevParent;
	private int forCount ;
	private int whileCount ;
	private int switchCount ;
	private int ifCount ;
	private int elseCount ;

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
        elseCount = 0;
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

        temp = classstmt + id + lcurly + methodstmtseq + rcurly;
        //System.out.println("class decl " + temp);
        stack.push(temp);
        temp = "";
    }

    public void caseATypevarliststmtClassmethodstmt(ATypevarliststmtClassmethodstmt node) {
        node.getType().apply(this);
        node.getId().apply(this);
		
        String parentId = stack.peek();
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = parentId;
		
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

        if (type.equals("VOID")) {
            if (stmtseq.contains("RETURN")) {
                error.add("Void method cannot return value;");
            }
        }

        symbolTable.addMethod(m);

        temp = type + id + lparen + varlist + rparen + lcurly + stmtseq + rcurly;
        //System.out.println("varlist classmethodstmt " + temp);
        stack.push(temp);
        temp = "";

        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
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
        //System.out.println("one or more methodstmtseq " + temp );
        stack.push(temp);
        temp = "";
    }

    public void caseAEmptyproductionMethodstmtseqs(AEmptyproductionMethodstmtseqs node) {
        stack.push("");
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

        symbolTable.addMethod(m);

        temp = type + id + lparen + varlist + rparen + lcurly + stmtseq + rcurly;
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
        if (!idlist.equals("")) {
            String[] ids = idlist.split(",");
            for (int i = 0; i < ids.length; i++) {
                Variable
                var = new Variable(ids[i], type);
            }
        }

        temp = id + idlist + colon + type + semicolon;
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

        temp = id + arr + assignment + anychars + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAPrintstmtMethodstmtseq(APrintstmtMethodstmtseq node) {
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

        temp = id + arr + assignment + get + lparen + rparen + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAIncrementMethodstmtseq(AIncrementMethodstmtseq node) {
        String parent = myParent ;
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getIncrement().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String incr = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        String scopeId = id + "_" + parent ;
        
        if (!symbolTable.containsVar(scopeId)) {
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " has not been declared and cannot be incremented.");
            } else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("STRING")) {
                    error.add("The variable " + id + " is type STRING and cannot be incremented.");
                } else if (type.equals("BOOLEAN")) {
                    error.add("The variable " + id + " is type BOOLEAN and cannot be incremented.");
                }
            }
        } else {
            Variable v = symbolTable.getVar(scopeId);
            String type = v.getType();
            if (type.equals("STRING")) {
                error.add("The variable " + id + " is type STRING and cannot be incremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("The variable " + id + " is type BOOLEAN and cannot be incremented.");
            }
        }

        temp = id + arr + incr + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseADecrementMethodstmtseq(ADecrementMethodstmtseq node) {
        String parent = myParent ;
        node.getId().apply(this);
        node.getOptionalidarray().apply(this);
        node.getDecrement().apply(this);
        node.getSemicolon().apply(this);

        String semicolon = stack.pop();
        String decr = stack.pop();
        String arr = stack.pop();
        String id = stack.pop();

        String scopeId = id + "_" + parent ;
        
        if (!symbolTable.containsVar(scopeId)) {
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " has not been declared and cannot be decremented.");
            } else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("STRING")) {
                    error.add("The variable " + id + " is type STRING and cannot be decremented.");
                } else if (type.equals("BOOLEAN")) {
                    error.add("The variable " + id + " is type BOOLEAN and cannot be decremented.");
                }
            }
        } else {
            Variable v = symbolTable.getVar(scopeId);
            String type = v.getType();
            if (type.equals("STRING")) {
                error.add("The variable " + id + " is type STRING and cannot be decremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("The variable " + id + " is type BOOLEAN and cannot be decremented.");
            }
        }

        temp = id + arr + decr + semicolon;
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

        if (!arr.equals("")) {
            String type = secondid + " Array";
            Variable v = new Variable(id, type);
            symbolTable.addVar(v);
        } else {
            Variable v = new Variable(id, secondid);
            symbolTable.addVar(v);
        }

        temp = id + arr + assignment + newstmt + secondid + lparen + rparen + semicolon;
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

        temp = id + arr + assignment + semicolon;
        //System.out.println("bool methodstmtseq " + temp );
        stack.push(temp);
        temp = "";
    }

    public void caseAOneormoreStmtseq(AOneormoreStmtseq node) {
        node.getStmt().apply(this);
        node.getStmtseq().apply(this);

        String seq = stack.pop();
        String stmt = stack.pop();

        temp = stmt + seq;
        //System.out.println("stmtseq " + temp );
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

        String scopeId = id + "_" + parent;
        if (!symbolTable.containsVar(scopeId)) {
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " has not been declared yet. " +
                    "A variable must be declared before it can be asigned a value.");
            } else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("INT")) {
                    if (expr.contains(".")) {
                        error.add("The variable " + id + " is type INT and cannot store a real number.");
                    }
                } else if (type.equals("BOOLEAN")) {
                    if (!expr.equals("TRUE") || !expr.equals("FALSE")) {
                        error.add("The variable " + id + " is type BOOLEAN and can only store boolean values");
                    }
                } else if (type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")) {
                    if (expr.charAt(0) == '"') {
                        error.add("The variable " + id + " is type " + type + " and cannot store a string");
                    }
                }

            }
        }
        //check if the type matches      
        else {
            Variable v = symbolTable.getVar(scopeId);
            String type = v.getType();
            if (type.equals("INT")) {
                if (expr.contains(".")) {
                    error.add("The variable " + id + " is type INT and cannot store a real number.");
                }
            } else if (type.equals("BOOLEAN")) {
                if (!expr.equals("TRUE") || !expr.equals("FALSE")) {
                    error.add("The variable " + id + " is type BOOLEAN and can only store boolean values");
                }
            } else if (type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")) {
                if (expr.charAt(0) == '"') {
                    error.add("The variable " + id + " is type " + type + " and cannot store a string");
                }
            }

        }

        temp = id + arr + assignment + expr + semicolon;
        //System.out.println("assignment stmt " + temp);
        stack.push(temp);
        temp = "";
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

        String scopeId = id + "_" + parent;
        //check if variable has been declared
        if (!symbolTable.containsVar(scopeId)) {
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " has not been declared yet. " +
                    "A variable must be declared before it can be asigned a value.");
            } else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (!type.equals("STRING")) {
                    error.add("The variable " + id + " is type " + type.toUpperCase() + " and cannot store string literals.");
                }
            }
        } else { //check if the type of the var is a string
            Variable v = symbolTable.getVar(scopeId);
            String type = v.getType();
            if (!type.equals("STRING")) {
                error.add("The variable " + id + " is type " + type + " and cannot store string literals.");
            }
        }


        temp = id + arr + assignment + anychars + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseAIdlistStmt(AIdlistStmt node) {
        String idParent = myParent;

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


        String idScope = id + "_" + idParent;
        if(!idParent.equals("")){
            if(arr.equals("")){
                if (symbolTable.containsVar(idScope)) {
                    error.add("The variable " + id + " has already been declared in " + idParent + ".");
                } else {
                    Variable v = new Variable(idScope, type);
                    symbolTable.addVar(v);
                }
            }
            else {
                String arrType = type + "_ARRAY";
                String num = arr.replace("[", "");
                num = num.replace("]", "");                
                Variable v = new Variable(idScope, arrType);
                v.setValue(num.trim());
                symbolTable.addVar(v);
            }
        }
        else {
            if(arr.equals("")){
                if (symbolTable.containsVar(id)) {
                    error.add("The variable " + id + " has already been declared in " + idParent + ".");
                } else {
                    Variable v = new Variable(id, type);
                    symbolTable.addVar(v);
                }
            }
            else {
                String arrType = type + "_ARRAY";
                Variable v = new Variable(id, arrType);
                symbolTable.addVar(v);
            }
        }


        if (!idlist.equals("")) {
            String[] ids = idlist.split(",");
            for (int i = 0; i < ids.length; i++) {
                if (symbolTable.containsVar(id)) {
                    error.add("The variable " + id + " has already been declared.");
                } else {
                    Variable
                    var = new Variable(ids[i], type);
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
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = "IF" + ifCount;
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
        //System.out.println("Ifstmt " + temp);
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

        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
		whileCount++;
    }

    public void caseAForStmt(AForStmt node) {
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = "FOR" + forCount;
        node.getFor().apply(this);
        node.getLparen().apply(this);
        node.getOptionaltype().apply(this);
        String peekType = stack.peek();
        node.getId().apply(this);
        String peekId = stack.peek();

        if (!peekType.equals("")) { //if the type is declared, add it to the symbolTable with its scope
            String scopeId = peekId + "_FOR" + forCount;
            if (!symbolTable.containsVar(scopeId)) {
                Variable v = new Variable(scopeId, peekType);
                symbolTable.addVar(v);

            } else {
                error.add("The variable " + peekId + " has already been declared.");
            }
        } else {
            if (!symbolTable.containsVar(peekId)) {
                error.add("The variable " + peekId + " has not been declared.");
            }
        }

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


        if (optionaltype.equals("")) { //if type is not declared must be in symbol table already
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " must be declared before it can be assigned a value.");
            } else { //if it is in symbol table the expr assignment must match the type
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("INT")) {
                    if (expr.contains(".")) {
                        error.add("The variable " + id + " is type INT and cannot store a real number.");
                    }
                } else if (type.equals("BOOLEAN")) {
                    if (!expr.equals("TRUE") || !expr.equals("FALSE")) {
                        error.add("The variable " + id + " is type BOOLEAN and can only store boolean values");
                    }
                } else if (type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")) {
                    if (expr.charAt(0) == '"') {
                        error.add("The variable " + id + " is type " + type + " and cannot store a string");
                    }
                }

            }
        } else if (optionaltype.equals("INT")) {
            if (expr.contains(".")) {
                error.add("The variable " + id + " is type INT and cannot store a real number.");
            } else if (optionaltype.equals("INT") || optionaltype.equals("DOUBLE") || optionaltype.equals("BOOLEAN")) {
                if (expr.charAt(0) == '"') {
                    error.add("The variable " + id + " is type " + optionaltype + " and cannot store a string");
                }
            } else if (optionaltype.equals("BOOLEAN")) {
                if (!expr.equals("TRUE") || !expr.equals("FALSE")) {
                    error.add("The variable " + id + " is type BOOLEAN and can only store boolean values");
                }
            }
        }

        temp = forStmt + lparen + optionaltype + id + assignment + expr + firstsemi + bool + secondsemi + orstmts + rparen + lcurly + stmtseq + rcurly;
        stack.push(temp);
        temp = "";

        //reset the scope
        myParent = prevParent;
        prevParent = tempPrevParent;
		forCount++;
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

        temp = id + arr + assignment + get + lparen + rparen + semicolon;
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

        temp = put + lparen + id + arr + rparen + semicolon;
        stack.push(temp);
        temp = "";
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

        String scopeId = id + "_" + parent;

        if (!symbolTable.containsVar(scopeId)) {
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " has not been declared and cannot be incremented.");
            } else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("STRING")) {
                    error.add("The variable " + id + " is type STRING and cannot be incremented.");
                } else if (type.equals("BOOLEAN")) {
                    error.add("The variable " + id + " is type BOOLEAN and cannot be incremented.");
                }
            }
        } else {
            Variable v = symbolTable.getVar(scopeId);
            String type = v.getType();
            if (type.equals("STRING")) {
                error.add("The variable " + id + " is type STRING and cannot be incremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("The variable " + id + " is type BOOLEAN and cannot be incremented.");
            }
        }

        temp = id + arr + increment + semicolon;
        stack.push(temp);
        temp = "";
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

        String scopeId = id + "_" + parent;

        if (!symbolTable.containsVar(scopeId)) {
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " has not been declared and cannot be decremented.");
            } else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("STRING")) {
                    error.add("The variable " + id + " is type STRING and cannot be decremented.");
                } else if (type.equals("BOOLEAN")) {
                    error.add("The variable " + id + " is type BOOLEAN and cannot be decremented.");
                }
            }
        } else {
            Variable v = symbolTable.getVar(scopeId);
            String type = v.getType();
            if (type.equals("STRING")) {
                error.add("The variable " + id + " is type STRING and cannot be decremented.");
            } else if (type.equals("BOOLEAN")) {
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

        if (!optionalid.equals("")) {
            String type = secondid + " Array";
            Variable v = new Variable(id, type);
            symbolTable.addVar(v);
        } else {
            Variable v = new Variable(id, secondid);
            symbolTable.addVar(v);
        }

        temp = id + optionalid + assignment + newkeyword + secondid + lparen + rparen + semicolon;
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

        String idScope = id + "_" + parent;

        if (!symbolTable.containsVar(idScope)) { //check if there is a variable with this scope
            if (!symbolTable.containsVar(id)) { //check if the symbol table contains the var
                error.add("The variable " + id + " has not been declared and cannot be assigned a value.");
            } else { //booleans can only be stored into INT, REAL or BOOLEAN 
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("STRING")) {
                    error.add("The variable " + id + " is type STRING and cannot store a boolean value.");
                }
            }
        } else {
            Variable v = symbolTable.getVar(idScope);
            String type = v.getType();
            if (type.equals("STRING")) {
                error.add("The variable " + id + " is type STRING and cannot store a boolean value.");
            }
        }


        temp = id + arr + bool + semicolon;
        stack.push(temp);
        temp = "";
    }

    public void caseASwitchStmt(ASwitchStmt node) {
        String tempPrevParent = prevParent;
        prevParent = myParent;
        myParent = "SWITCH" + switchCount;
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
        //System.out.println("Optlidlist " + temp);
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
        myParent = "ELSE" + elseCount;
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
		elseCount++ ;
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

        String idScope = id + "_" + parent;
        if (!symbolTable.containsVar(idScope)) {
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " has not been declared and cannot be incremented.");
            } else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("STRING")) {
                    error.add("The variable " + id + " is type STRING and cannot be incremented.");
                } else if (type.equals("BOOLEAN")) {
                    error.add("The variable " + id + " is type BOOLEAN and cannot be incremented.");
                }
            }
        } else {
            Variable v = symbolTable.getVar(idScope);
            String type = v.getType();
            if (type.equals("STRING")) {
                error.add("The variable " + id + " is type STRING and cannot be incremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("The variable " + id + " is type BOOLEAN and cannot be incremented.");
            }
        }

        temp = id + incr;
        stack.push(temp);
        temp = "";
    }

    public void caseADecrementOrstmts(ADecrementOrstmts node) {
        String parent = myParent;

        node.getId().apply(this);
        node.getDecrement().apply(this);

        String decr = stack.pop();
        String id = stack.pop();

        String idScope = id + "_" + parent;
        if (!symbolTable.containsVar(idScope)) {
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " has not been declared and cannot be decremented.");
            } else {
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("STRING")) {
                    error.add("The variable " + id + " is type STRING and cannot be decremented.");
                } else if (type.equals("BOOLEAN")) {
                    error.add("The variable " + id + " is type BOOLEAN and cannot be decremented.");
                }
            }
        } else {
            Variable v = symbolTable.getVar(idScope);
            String type = v.getType();
            if (type.equals("STRING")) {
                error.add("The variable " + id + " is type STRING and cannot be decremented.");
            } else if (type.equals("BOOLEAN")) {
                error.add("The variable " + id + " is type BOOLEAN and cannot be decremented.");
            }
        }

        temp = id + decr;
        stack.push(temp);
        temp = "";

    }

    public void caseAAssignmentOrstmts(AAssignmentOrstmts node) {
        String parent = myParent;
        node.getId().apply(this);
        node.getAssignment().apply(this);
        node.getExpr().apply(this);

        String expr = stack.pop();
        String assignment = stack.pop();
        String id = stack.pop();

        String idScope = id + "_" + parent;
        if (!symbolTable.containsVar(idScope)) {
            if (!symbolTable.containsVar(id)) {
                error.add("The variable " + id + " must be declared before it can be assigned a value.");
            } else { //if it is in symbol table the expr assignment must match the type
                Variable v = symbolTable.getVar(id);
                String type = v.getType();
                if (type.equals("INT")) {
                    if (expr.contains(".")) {
                        error.add("The variable " + id + " is type INT and cannot store a real number.");
                    }
                } else if (type.equals("BOOLEAN")) {
                    if (!expr.equals("TRUE") || !expr.equals("FALSE")) {
                        error.add("The variable " + id + " is type BOOLEAN and can only store boolean values");
                    }
                } else if (type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")) {
                    if (expr.charAt(0) == '"') {
                        error.add("The variable " + id + " is type " + type + " and cannot store a string");
                    }
                }

            }
        } else {
            Variable v = symbolTable.getVar(idScope);
            String type = v.getType();
            if (type.equals("INT")) {
                if (expr.contains(".")) {
                    error.add("The variable " + id + " is type INT and cannot store a real number.");
                }
            } else if (type.equals("BOOLEAN")) {
                if (!expr.equals("TRUE") || !expr.equals("FALSE")) {
                    error.add("The variable " + id + " is type BOOLEAN and can only store boolean values");
                }
            } else if (type.equals("INT") || type.equals("DOUBLE") || type.equals("BOOLEAN")) {
                if (expr.charAt(0) == '"') {
                    error.add("The variable " + id + " is type " + type + " and cannot store a string");
                }
            }
        }

        temp = id + assignment + expr;
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

        //error checking processing
        Variable t;
        Variable e;
        String termType = "";
        String exprType = "";

        if (termIsId) {
            String termIdScope = term + "_" + parent;
            if (!symbolTable.containsVar(termIdScope)) { //if the term is an id check its type and declaration
                if (!symbolTable.containsVar(term)) {
                    error.add("The variable " + term + " is undeclared. " +
                        "Cannot add an undeclared variable.");
                } else {
                    t = symbolTable.getVar(term);
                    termType = t.getType();

                    if (termType.equals("STRING")) {
                        error.add("The variable " + term + " is type STRING. " +
                            "Cannot do arithmetic addition with a string literatl.");
                    }
                }
            } else {
                t = symbolTable.getVar(termIdScope);
                termType = t.getType();

                if (termType.equals("STRING")) {
                    error.add("The variable " + term + " is type STRING. " +
                        "Cannot do arithmetic addition with a string literatl.");
                }
            }
        } else if (term.contains("\"")) {
            error.add(term + " is a STRING. Cannot add a string literal.");
        }

        if (exprIsId) { //if the expr is an id check its type and declaration
            String exprIdScope = expr + "_" + parent;
            if (!symbolTable.containsVar(exprIdScope)) {
                if (!symbolTable.containsVar(expr)) {
                    error.add("The variable " + expr + " is undeclared. " +
                        "Cannot add an undeclared variable.");
                } else {
                    e = symbolTable.getVar(expr);
                    exprType = e.getType();

                    if (exprType.equals("STRING")) {
                        error.add("The variable " + expr + " is type STRING. " +
                            "Cannot do arithmetic addition with a string literatl.");
                    }
                }
            } else {
                e = symbolTable.getVar(exprIdScope);
                exprType = e.getType();

                if (exprType.equals("STRING")) {
                    error.add("The variable " + expr + " is type STRING. " +
                        "Cannot do arithmetic addition with a string literatl.");
                }
            }
        } else if (expr.contains("\"")) {
            error.add(expr + " is a STRING. Cannot add a string literal.");
        }

        temp = expr + addop + term;
        stack.push(temp);
        temp = "";
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

        Variable t;
        Variable f;
        String termType = "";
        String factorType = "";

        //if term is an id check its type
        if (termIsId) {
            String tIdScope = term + "_" + parent;
            if (!symbolTable.containsVar(tIdScope)) { //if the term is an id check its type and declaration
                if (!symbolTable.containsVar(term)) {
                    error.add("The variable " + term + " is undeclared. " +
                        "Cannot add an undeclared variable.");
                } else {
                    t = symbolTable.getVar(term);
                    termType = t.getType();

                    if (termType.equals("STRING")) {
                        error.add("The variable " + term + " is type STRING. " +
                            "Cannot do arithmetic addition with a string literatl.");
                    }
                }
            } else {
                t = symbolTable.getVar(tIdScope);
                termType = t.getType();

                if (termType.equals("STRING")) {
                    error.add("The variable " + term + " is type STRING. " +
                        "Cannot do arithmetic addition with a string literatl.");
                }
            }
        } else if (term.contains("\"")) {
            error.add(term + " is a STRING. Cannot add a string literal.");
        }


        //if factor is an id check its type
        if (factorIsId) {
            String fIdScope = factor + "_" + parent;
            if (!symbolTable.containsVar(fIdScope)) { //if the term is an id check its type and declaration
                if (!symbolTable.containsVar(factor)) {
                    error.add("The variable " + factor + " is undeclared. " +
                        "Cannot add an undeclared variable.");
                } else {
                    f = symbolTable.getVar(factor);
                    factorType = f.getType();

                    if (factorType.equals("STRING")) {
                        error.add("The variable " + factor + " is type STRING. " +
                            "Cannot do arithmetic addition with a string literatl.");
                    }
                }
            } else {
                f = symbolTable.getVar(fIdScope);
                factorType = f.getType();

                if (factorType.equals("STRING")) {
                    error.add("The variable " + factor + " is type STRING. " +
                        "Cannot do arithmetic addition with a string literatl.");
                }
            }
        } else if (factor.contains("\"")) {
            error.add(factor + " is a STRING. Cannot add a string literal.");
        }

        temp = term + mult + factor;
        stack.push(temp);
        temp = "";
    }

    public void caseAFactorTerm(AFactorTerm node) {
        node.getFactor().apply(this);

    }

    public void caseAParenexprFactor(AParenexprFactor node) {
        String parent = myParent;
        node.getLparen().apply(this);
        node.getExpr().apply(this);
        boolean expIsId = isId;
        isId = false;
        node.getRparen().apply(this);

        String rparen = stack.pop();
        String expr = stack.pop();
        String lparen = stack.pop();

        //error check
        if (expIsId) {
            String idScope = expr + "_" + parent;
            if (!symbolTable.containsVar(idScope)) {
                if (!symbolTable.containsVar(expr)) {
                    error.add("The variable " + expr + " is undeclared." +
                        " A variable must be declared before using it in a statement.");
                }
            }
        }

        temp = lparen + expr + rparen;
        stack.push(temp);
        temp = "";
    }

    public void caseAMinusfactorFactor(AMinusfactorFactor node) {
        String parent = myParent;

        node.getMinus().apply(this);
        node.getFactor().apply(this);
        boolean isFactorId = isId;
        isId = false;

        String factor = stack.pop();
        String minus = stack.pop();

        if (isFactorId) {
            String idScope = factor + "_" + parent;
            if (!symbolTable.containsVar(idScope)) {
                if (!symbolTable.containsVar(factor)) {
                    error.add("The variable " + factor + " is undeclared. " +
                        "A variable must be declared before using it in a statement.");
                } else {
                    Variable v = symbolTable.getVar(factor);
                    String type = v.getType();
                    if (type.equals("STRING")) {
                        error.add("The variable " + factor + " is type STRING. Cannot - a string literal.");
                    }
                }
            } else {
                Variable v = symbolTable.getVar(idScope);
                String type = v.getType();
                if (type.equals("STRING")) {
                    error.add("The variable " + factor + " is type STRING. Cannot - a string literal.");
                }
            }
        }

        temp = minus + factor;
        stack.push(temp);
        temp = "";
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
        node.getId().apply(this);
    }

    public void caseAEmptyproductionOptionalidarray(AEmptyproductionOptionalidarray node) {
        stack.push("");
    }

    public void caseAArrayOptionalidarray(AArrayOptionalidarray node) {
        node.getLsquare().apply(this);
        node.getNumber().apply(this);
        node.getRsquare().apply(this);

        String rsquare = stack.pop();
        String number = stack.pop();
        String lsquare = stack.pop();

        temp = rsquare + number + lsquare;
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
        boolean isFirstExprId = isId;
        isId = false;
        node.getCond().apply(this);
        node.getSecondexpr().apply(this);
        boolean isSecondExprId = isId;
        isId = false;

        String secondexpr = stack.pop();
        String cond = stack.pop();
        String firstexpr = stack.pop();
        Variable firstExpVar;
        Variable secondExpVar;
        String firstExpType = "";
        String secondExpType = "";

        if (isFirstExprId) { //if the expr is an id check its type and declaration
            String firstExprIdScope = firstexpr + "_" + parent;
            if (!symbolTable.containsVar(firstExprIdScope)) {
                if (!symbolTable.containsVar(firstexpr)) {
                    error.add("The variable " + firstexpr + " is undeclared. " +
                        "Cannot add an undeclared variable.");
                } else {
                    firstExpVar = symbolTable.getVar(firstexpr);
                    firstExpType = firstExpVar.getType();

                    if (firstexpr.equals("STRING")) {
                        error.add("The variable " + firstexpr + " is type STRING. " +
                            "Cannot do arithmetic addition with a string literatl.");
                    }
                }
            } else {
                firstExpVar = symbolTable.getVar(firstExprIdScope);
                firstExpType = firstExpVar.getType();

                if (firstExpType.equals("STRING")) {
                    error.add("The variable " + firstexpr + " is type STRING. " +
                        "Cannot do arithmetic addition with a string literatl.");
                }
            }
        } else if (firstexpr.contains("\"")) {
            error.add(firstexpr + " is a STRING. Cannot add a string literal.");
        }

        if (isSecondExprId) { //if the expr is an id check its type and declaration
            String secondExprIdScope = secondexpr + "_" + parent;
            if (!symbolTable.containsVar(secondExprIdScope)) {
                if (!symbolTable.containsVar(secondexpr)) {
                    error.add("The variable " + secondexpr + " is undeclared. " +
                        "Cannot add an undeclared variable.");
                } else {
                    secondExpVar = symbolTable.getVar(secondexpr);
                    secondExpType = secondExpVar.getType();

                    if (secondExpType.equals("STRING")) {
                        error.add("The variable " + secondexpr + " is type STRING. " +
                            "Cannot do arithmetic addition with a string literatl.");
                    }
                }
            } else {
                secondExpVar = symbolTable.getVar(secondExprIdScope);
                secondExpType = secondExpVar.getType();

                if (secondExpType.equals("STRING")) {
                    error.add("The variable " + secondexpr + " is type STRING. " +
                        "Cannot do arithmetic addition with a string literatl.");
                }
            }
        } else if (secondexpr.contains("\"")) {
            error.add(secondexpr + " is a STRING. Cannot add a string literal.");
        }


        temp = firstexpr + cond + secondexpr;
        stack.push(temp);
        temp = "";

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
}