package ProjFive;

import ProjFive.analysis.*;
import ProjFive.node.*;
import java.util.*;

public class SecondPass extends DepthFirstAdapter {
	public SymbolTable symbolTable;
	private Stack<String> stack;
	private String temp;
        public String output ;
        public String dataPart ;
        public String codePart ;
        private int ifCount ;
        private int forCount ;
        private int whileCount ;
        private int switchCount ;
        private int varCount ;
        private int boolCount ;
        private int openRegs ; 
        private int lastInReg = 0 ;
        private int lastOutReg = 0 ;
        private String myParent ;
        private String prevParent ;
        private boolean isId ;
        private boolean[] regStatus = {false , false , false , false , false , false , false , false} ;
        private boolean[] outRegStatus = {false , false , false , false , false , false , false , false} ;
        private List<String> switchConds = new ArrayList<>() ;

	public SecondPass(SymbolTable st) {
            symbolTable = st ;
            stack = new Stack<>();
            temp = new String();
            ifCount = 0 ;
            forCount = 0 ;
            whileCount = 0;
            switchCount = 0;
            varCount = 0;
            boolCount = 0 ;
            openRegs = 8 ; 
            output = "" ;
            codePart = ".text \n" ;
            dataPart = ".data \n" ;
            myParent = new String();
            prevParent = new String();
            isId = false;
	}

	public void caseAProg(AProg node) {
                dataPart = dataPart + "newLine: .asciiz \"\\n\" \n" ;
                dataPart = dataPart + ".align 2 \n" ;
		node.getBegin().apply(this);
		node.getClassmethodstmts().apply(this);
		node.getEnd().apply(this);
                
                //add the variables from the symbol table to the .data section
                HashMap m = symbolTable.getVarTable() ;
                Set<String> keys = m.keySet() ;
                Variable v ;
                String type ;
                for(String key : keys)
                {
                    //System.out.println(key);
                    v = (Variable) (m.get(key));
                    type = v.getType();
                    if(type.equals("STRING")){
                        dataPart = dataPart + key + ": .asciiz " + v.getValue() + "\n.align 2 \n" ;
                    }
                    else {
                        declareVar(key, type);
                    }
                    
                }
                
                codePart = codePart + "EXIT: \n li $v0 , 10 \n syscall" ;
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
                
                String parentId = stack.peek();
                String tempPrevParent = prevParent ;
                prevParent = myParent ;
                myParent = parentId ;
                
		node.getLparen().apply(this);
		node.getVarlist().apply(this);
		node.getRparen().apply(this);
		node.getLcurly().apply(this);
                
                //Generate method declaration
                
		node.getStmtseq().apply(this);
                
                //End method declaration
                
		node.getRcurly().apply(this);
		
		String rcurly = stack.pop();
		String stmtseq = stack.pop();
		String lcurly = stack.pop();
		String rparen = stack.pop();
		String varlist = stack.pop();
		String lparen = stack.pop();
		String id = stack.pop();
		String type = stack.pop();
 /*               
                Method m = symbolTable.getMethod(id);
                HashMap para = m.getParams() ;
                Set<String> keys = para.keySet() ;
                String name = "" ;
                String vType = "" ;
                String mVarName = id + "_" ;
                for(String key : keys)
                {
                    Variable v = (Variable)(para.get(key)) ;
                    name = v.getName() ;
                    vType = v.getType() ;
                    if(vType.equals("REAL"))
                    {
                        dataPart = dataPart + mVarName + name + " .double \n" ;
                    }
                    else if(vType.equals("INT"))
                    {
                        dataPart = dataPart + mVarName + name + " .word \n" ;
                    }
                    else if(vType.equals("BOOLEAN"))
                    {
                        dataPart = dataPart + mVarName + name + " .word \n" ;
                    }
                    else if(vType.equals("STRING"))
                    {
                        dataPart = dataPart + mVarName + name + " .asciiz \n align 2 \n" ;
                    }
                    else
                    {
                        //dealing with classes and objects and stuff...
                    }
                }
                        */
		temp = type + id + lparen + varlist + rparen + lcurly + stmtseq + rcurly ;
		//System.out.println("varlist classmethodstmt " + temp);
		stack.push(temp);
		temp = "";
                
                //reset the scope
                myParent = prevParent ;
                prevParent = tempPrevParent ;
	}
/*	
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
                
                declareVar(id , type) ;
                
                String[] ids = idlist.split(",") ;
                for(int i = 0 ; i < ids.length ; i++)
                {
                    declareVar(ids[i] , type) ;
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
                
                //Method header move things from registers
                
		node.getStmtseq().apply(this);
                
                //End method, put things back
                
		node.getRcurly().apply(this);
		
		String rcurly = stack.pop();
		String stmtseq = stack.pop();
		String lcurly = stack.pop();
		String rparen = stack.pop();
		String varlist = stack.pop();
		String lparen = stack.pop();
		String id = stack.pop();
		String type = stack.pop();
                
                Method m = symbolTable.getMethod(id) ;
                Set<String> keys = m.getParams().keySet() ;
                String mName = id + "_" ;
                for(String key : keys)
                {
                    Variable v = m.getVar(key) ;
                    declareVar(mName + v.getName() , v.getType()) ;
                }
                
		temp = type + id + lparen + varlist + rparen + lcurly + stmtseq + rcurly ;
		//System.out.println("Typevarlist methodstmtseq " + temp );
		stack.push(temp);
		temp = "";
	}*/

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
		temp = id + arr + incr + semicolon ;
		//System.out.println("incr methodstmtseq " + temp);
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
            String parent = myParent ;
            
            node.getId().apply(this);	
            node.getOptionalidarray().apply(this);
            node.getAssignment().apply(this);
            node.getExpr().apply(this);
            //In getExpr we generate proper code

            //after that we set the value of the variable
            node.getSemicolon().apply(this);

            String semicolon = stack.pop();
            String expr = stack.pop();
            String assignment = stack.pop();
            String arr = stack.pop();
            String id = stack.pop();

            //id to check for scope
            String idScope = PrintTree.getVarId(parent, id);
            
            if(symbolTable.containsVar(idScope)){
                codePart = codePart + "move $t0 , $s" + lastOutReg + "\nsw $t0 , " + idScope + "\n" ;
            }
            else {
                codePart = codePart + "move $t0 , $s" + lastOutReg + "\nsw $t0 , " + id + "\n" ;
            }

            temp = id + arr + assignment + expr + semicolon ;
            stack.push(temp);
            temp = "";
	}

	public void caseAExpranycharStmt(AExpranycharStmt node) {
            String parent = myParent ;
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
                
            //check for local scope
            String idScope = PrintTree.getVarId(parent, id);          
            Variable v = symbolTable.getVar(idScope) ;
            v.setValue(anychars) ;
            symbolTable.addVar(v);

            temp = id + arr + assignment + anychars + semicolon ;
            stack.push(temp);
            temp = "";
	} 

	public void caseAIdlistStmt(AIdlistStmt node) {
            String parent = myParent ;
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
	}

	public void caseAIfbooleanStmt(AIfbooleanStmt node) {
            String tempPrevParent = prevParent ;
            prevParent = myParent ;
            myParent = "IF" + ifCount ;
            
            node.getIf().apply(this);
            node.getLparen().apply(this);
            node.getBoolid().apply(this);
            node.getRparen().apply(this);
            node.getThen().apply(this); 

            lastInReg = getOpenInReg(lastInReg) ;
            int condReg = lastInReg ;

            codePart = codePart + "move $t" + condReg + " , $s" + lastOutReg + "\n" ;

            node.getOptionalelse().apply(this);

            String elsestmt = stack.pop();
            String then = stack.pop();
            String rparen = stack.pop();
            String boolid = stack.pop();
            String lparen = stack.pop();
            String ifstmt = stack.pop();

            temp = ifstmt + lparen + boolid + rparen + then + elsestmt ;
            stack.push(temp);
            temp = "" ;

            freeInReg(condReg) ;
            freeAllReg() ;

            //reset the scope
            myParent = prevParent ;
            prevParent = tempPrevParent ;
            ifCount++;
	}

	public void caseAWhileStmt(AWhileStmt node) {
            String tempPrevParent = prevParent ;
            prevParent = myParent ;
            myParent = "WHILE" + whileCount ;
            
            node.getWhile().apply(this);
            node.getLparen().apply(this);

            //get boolean info
            String whileNum = "_" + whileCount ;
            whileCount++ ;

            codePart = codePart + "START_WHILE_LOOP" + whileNum + ": \n" ;

            node.getBoolean().apply(this);

            codePart = codePart + "move $t0 , $s" + lastOutReg + "\n";
            codePart = codePart + "beq $t0 , $zero , END_WHILE_LOOP" + whileNum + "\n" ;

            node.getRparen().apply(this);
            node.getLcurly().apply(this);
            node.getStmtseq().apply(this);

            freeAllReg() ;

            node.getRcurly().apply(this);

            codePart = codePart + "b START_WHILE_LOOP" + whileNum + "\n" ;
            codePart = codePart + "END_WHILE_LOOP" + whileNum + ": \n" ;

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
            freeAllReg() ;
                
            //reset the scope
            myParent = prevParent ;
            prevParent = tempPrevParent ;
	}

	public void caseAForStmt(AForStmt node) {
            String tempPrevParent = prevParent ;
            prevParent  = myParent ;
            myParent = "FOR" + forCount ;
            
            node.getFor().apply(this);
            node.getLparen().apply(this);
            node.getOptionaltype().apply(this);
            String peekType = stack.peek();
            node.getId().apply(this);
            String peekId = stack.peek();
            node.getAssignment().apply(this);
            node.getExpr().apply(this);
            String peekExpr = stack.peek();

            String idScope = PrintTree.getVarId(myParent, peekId);

            Variable v = symbolTable.getVar(idScope);

            codePart = codePart + "sw $s" + lastOutReg + " , " + v.getName() + "\n" ;

            codePart = codePart + "lw $s" + lastInReg + " , " + v.getName() + "\n" ;
            String forNum = "_" + forCount ;
            forCount ++ ;
            String conditionName = "condition_for" + forNum ;
            declareVar(conditionName , "BOOLEAN") ;
            node.getFirstsemicolon().apply(this);

            int preBoolLength = codePart.length() ;
            //trust this returns correct code
            int boolNum = boolCount ;
            node.getBoolean().apply(this);

            String boolCode = codePart.substring(preBoolLength , codePart.length()) ;

            codePart = codePart + "sw $s" + lastOutReg + " , " + conditionName + "\n" ;

            node.getSecondsemicolon().apply(this);

            int preOrLen = codePart.length() ;

            node.getOrstmts().apply(this);

            String orCode = codePart.substring(preOrLen , codePart.length()) ;
            codePart = codePart.substring(0 , preOrLen) ;

            node.getRparen().apply(this);
            node.getLcurly().apply(this);

            //Generate beginning for loop code
            codePart = codePart + "lw $t0 , " + conditionName + "\n" ;
            codePart = codePart + "beq $t0 , $zero , END_FOR_LOOP" + forNum + "\n" ;

            codePart = codePart + "START_FOR_LOOP" + forNum + ":\n" ;
            codePart = codePart + "lw $t" + lastInReg + " , " + v.getName() + "\n" ;

            node.getStmtseq().apply(this);

            freeAllReg() ;

            //Generate end for loop code
            codePart = codePart + orCode ;

            String bc1 = "_"+(boolNum) ;
            boolCount++ ;
            String bc2 = "_"+(boolCount - 1) ;

            String boolCode2 = boolCode.replaceAll(bc1 , bc2) ;

            codePart = codePart + boolCode2 ;

            String[] boolHack = boolCode2.split(bc2 + ":") ;
            String boolEnd = boolHack[boolHack.length - 1] ;
            boolHack = boolEnd.split(" ") ;
            boolEnd = boolHack[1] ;

            codePart = codePart + "sw " + boolEnd + " , " + conditionName + "\n" ;
            codePart = codePart + "lw $t0 , " + conditionName + "\n";
            codePart = codePart + "beq $t0 , $zero , END_FOR_LOOP" + forNum + "\n" ;
            codePart = codePart + "b START_FOR_LOOP" + forNum + "\n" ;
            codePart = codePart + "END_FOR_LOOP" + forNum + ":\n" ;
            //take care of registers or something.

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


            temp = forStmt + lparen + optionaltype + id + assignment + expr + firstsemi + bool + secondsemi + orstmts + rparen + lcurly + stmtseq + rcurly ;

            stack.push(temp);
            temp = "";
            freeAllReg() ;
                
            //reset scope
            myParent = prevParent ;
            prevParent = tempPrevParent ;
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
           
            String idScope = PrintTree.getVarId(parent, id);
            Variable v = symbolTable.getVar(idScope) ;
            String type = v.getType() ;
                
            if(type.equals("INT") || type.equals("BOOLEAN"))
            {
                //read int
                codePart = codePart + "li $v0 , 5 \n" ;
                codePart = codePart + "syscall \n" ;
                codePart = codePart + "sw $v0 , " + idScope + "\n" ;
            }
            else if(type.equals("REAL"))
            {
                //read double
                codePart = codePart + "li $v0 , 7 \n" ;
                codePart = codePart + "syscall \n" ;
                codePart = codePart + "sw $f0 , " + idScope + "\n" ;
            }

            temp = id + arr + assignment + get + lparen + rparen + semicolon ;
            stack.push(temp);
            temp = "";
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
		
            String idScope = PrintTree.getVarId(parent, id) ;
            Variable v = symbolTable.getVar(idScope) ;
            String type = v.getType() ;
                
            lastInReg = getOpenInReg(lastInReg) ;
            int varReg = lastInReg ;

            if(type.equals("INT"))
            {
                codePart = codePart + "lw $t" + varReg + " , " + idScope + "\n" ;
                codePart = codePart + "move $a0 , $t" + varReg + "\n" ; 
                //print integer
                codePart = codePart + "li $v0 , 1 \n" ;
                codePart = codePart + "syscall \n" ;
                codePart = codePart + "la $a0 , newLine \n" ;
                codePart = codePart + "li $v0 , 4 \n" ;
                codePart = codePart + "syscall \n" ;
            }
            else if(type.equals("REAL"))
            {
                codePart = codePart + "lw $t" + varReg + " , " + idScope + "\n" ;
                codePart = codePart + "move $a0 , $t" + varReg + "\n" ; 
                //print double
                codePart = codePart + "li $v0 , 3 \n" ;
                codePart = codePart + "syscall \n" ;
                codePart = codePart + "la $a0 , newLine \n" ;
                codePart = codePart + "li $v0 , 4 \n" ;
                codePart = codePart + "syscall \n" ;
            }
            else if(type.equals("STRING"))
            {
                codePart = codePart + "la $t" + varReg + " , " + idScope + "\n" ;
                codePart = codePart + "move $a0 , $t" + varReg + "\n" ; 
                //print string
                codePart = codePart + "li $v0 , 4 \n" ;
                codePart = codePart + "syscall \n" ;
                codePart = codePart + "la $a0 , newLine \n" ;
                codePart = codePart + "li $v0 , 4 \n" ;
                codePart = codePart + "syscall \n" ;
            }
            else if(type.equals("BOOLEAN"))
            {
                //print integer
                codePart = codePart + "li $v0 , 1 \n" ;
                codePart = codePart + "syscall \n" ;
                codePart = codePart + "la $a0 , newLine \n" ;
                codePart = codePart + "li $v0 , 4 \n" ;
                codePart = codePart + "syscall \n" ;
            }

            freeInReg(varReg) ;

            temp = put + lparen + id + arr + rparen + semicolon ;
            stack.push(temp); 
            temp = "";
	}

	public void caseAIncrementStmt(AIncrementStmt node){
            String parent = myParent ;
            node.getId().apply(this);
            node.getOptionalidarray().apply(this);
            node.getIncrement().apply(this);
            node.getSemicolon().apply(this);

            String semicolon = stack.pop();
            String increment = stack.pop();
            String arr = stack.pop();
            String id = stack.pop();
                
            //changes for scope
            String idScope = PrintTree.getVarId(parent, id) ;
            
            temp = id + arr + increment + semicolon;
            stack.push(temp);
            temp = "";

            lastInReg = getOpenInReg(lastInReg) ;
            lastOutReg = getOpenOutReg(lastOutReg) ;

            codePart = codePart + "lw $t" + lastInReg + " , " + idScope + "\n" ;
            codePart = codePart + "addi $s" + lastOutReg + " , $t" + lastInReg + " , 1 \n" ;
            codePart = codePart + "sw $s" + lastOutReg + " , " + idScope + "\n" ;

            freeInReg(lastInReg) ;
            freeOutReg(lastOutReg) ;       
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
                
            //changes for scope
            String idScope = PrintTree.getVarId(parent, id) ;
		
            temp = id + arr + decrement + semicolon;
            stack.push(temp);
            temp = "";

            lastInReg = getOpenInReg(lastInReg) ;
            lastOutReg = getOpenOutReg(lastOutReg) ;

            codePart = codePart + "lw $t" + lastInReg + " , " + idScope + "\n" ;
            codePart = codePart + "subi $s" + lastOutReg + " , $t" + lastInReg + " , 1 \n" ;
            codePart = codePart + "sw $s" + lastOutReg + " , " + idScope + "\n" ;

            freeInReg(lastInReg) ;
            freeOutReg(lastOutReg) ;      

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
            temp = returnstmt + expr + semicolon ;
            stack.push(temp);
            temp = "";
	}

	public void caseAIdbooleanStmt(AIdbooleanStmt node){
            String parent = myParent ;
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
            String idScope = PrintTree.getVarId(parent, id) ;

            temp = id + arr + bool + semicolon ;
            stack.push(temp);
            temp = "";
	}

	public void caseASwitchStmt(ASwitchStmt node){
            String tempPrevParent = prevParent ;
            prevParent = myParent ;
            myParent = "SWITCH" + switchCount ;

            String switchNum = "_" + switchCount ;
            int switchIndex = switchCount ;
            switchCount ++ ;

            node.getSwitch().apply(this);       
            node.getFirstlparen().apply(this);
            node.getExpr().apply(this);

            codePart = codePart + "move $t0 , $s" + lastOutReg + "\n" ;

            node.getFirstrparen().apply(this);
            node.getLcurly().apply(this);
            node.getCase().apply(this);
            node.getSecondlparen().apply(this);
            node.getNumber().apply(this);
            String num = stack.peek() ;
            codePart = codePart + "li $t1 , " + num + "\n" ;
            codePart = codePart + "beq $t0 , $t1 , CASE" + switchNum + "_" + num + "\n" ;

            int preCaseLen = codePart.length() ;

            node.getSecondrparen().apply(this);
            node.getFirstcolon().apply(this);

            codePart = codePart + "START_SWITCH" + switchNum + ":\n" ;
            codePart = codePart + "CASE" + switchNum + "_" + num + ": \n" ;

            node.getFirststmtseq().apply(this);
            node.getOptlbreak().apply(this);
            String optBreak = stack.peek() ;
            if(!optBreak.equals(""))
            {
                codePart = codePart + "b END_SWITCH" + switchNum + "\n" ;
            }
            freeAllReg() ;

            node.getOptionalswitchcases().apply(this);
            node.getDefault().apply(this);     

            codePart = codePart + "DEFAULT" + switchNum + ": \n" ;

            node.getSecondcolon().apply(this);
            node.getSecondstmtseq().apply(this);
            node.getRcurly().apply(this); 

            codePart = codePart + "END_SWITCH" + switchNum + ": \n" ;

            int postCaseLen = codePart.length() ;

            String switchCond = switchConds.get(switchIndex) ;
            String casePart = codePart.substring(preCaseLen , postCaseLen) ;
            codePart = codePart.substring(0 , preCaseLen) ;
            codePart = codePart + switchCond ;
            codePart = codePart + "b DEFAULT" + switchNum + "\n" ;
            codePart = codePart + casePart ;

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
            stack.push(temp);
            temp = "";

            myParent = prevParent ;
            prevParent = tempPrevParent ;
	}

	public void caseACommaidlistOptlidlist(ACommaidlistOptlidlist node) {
            node.getComma().apply(this);
            node.getId().apply(this);
            node.getOptlidlist().apply(this);

            String optlid = stack.pop();
            String id = stack.pop();
            String comma = stack.pop();

            temp = comma + id + optlid ;
            stack.push(temp);
            temp = "";
	}

	public void caseAEmptyproductionOptlidlist(AEmptyproductionOptlidlist node) {
            stack.push("");
	}

	public void caseANoelseOptionalelse(ANoelseOptionalelse node){	
            node.getLcurly().apply(this);

            String ifNum = "_" + ifCount ;
            ifCount++ ;
            codePart = codePart + "beq $t" + lastInReg + " , $zero , END_IF" + ifNum + "\n" ;
            codePart = codePart + "BEGIN_IF" + ifNum + ": \n" ;

            node.getStmtseq().apply(this);
            node.getRcurly().apply(this);

            codePart = codePart + "END_IF" + ifNum + ": \n" ;

            String rcurly = stack.pop();
            String stmtseq = stack.pop();
            String lcurly = stack.pop();

            temp = lcurly + stmtseq + rcurly ;
            stack.push(temp);
            temp = "";
	}

	public void caseAElseOptionalelse(AElseOptionalelse node){
            String tempPrevParent = prevParent ;
            prevParent = myParent ;
            myParent = "ELSE" + ifCount ;
            node.getFirstlcurly().apply(this);

            String ifNum = "_" + ifCount ;
            ifCount++ ;
            codePart = codePart + "beq $t" + lastInReg + " , $zero , ELSE" + ifNum + "\n" ;
            codePart = codePart + "BEGIN_IF" + ifNum + ": \n" ;

            node.getFirststmtseq().apply(this);
            node.getFirstrcurly().apply(this);

            codePart = codePart + "b END_IF_ELSE" + ifNum + "\n" ;

            node.getElse().apply(this);

            codePart = codePart + "ELSE" + ifNum + ": \n" ; 

            node.getSecondlcurly().apply(this);
            node.getSecondstmtseq().apply(this);
            node.getSecondrcurly().apply(this);

            codePart = codePart + "END_IF_ELSE" + ifNum + ": \n" ;

            String secondrcurly = stack.pop();
            String secondstmtseq = stack.pop();
            String secondlcurly = stack.pop();
            String elsestmt = stack.pop();
            String rcurly = stack.pop();
            String stmtseq = stack.pop();
            String lcurly = stack.pop();

            temp = lcurly + stmtseq + rcurly + elsestmt + secondlcurly + secondstmtseq + secondrcurly ;
            stack.push(temp);
            temp = "";

            //reset the parent
            myParent = prevParent ;
            prevParent = tempPrevParent ;
	}

	public void caseACaselistOptionalswitchcases(ACaselistOptionalswitchcases node){
            String switchNum = "_" + (switchCount - 1) ;
            String switchCond = new String() ;
            if(switchConds.isEmpty())
            {
                    switchCond = "" ;
            }
            else
            {
                    switchCond = switchConds.get(switchCount - 1) ;
            }

            node.getCase().apply(this);
            node.getLparen().apply(this);
            node.getNumber().apply(this);

            String num = stack.peek() ;
            switchCond = switchCond + "li $t1 , " + num + "\n" ;
            switchCond = switchCond + "beq $t0 , $t1 , CASE" + switchNum + "_" + num + "\n" ;

            node.getRparen().apply(this);
            node.getColon().apply(this);

            codePart = codePart + "CASE" + switchNum + "_" + num + ": \n" ;

            node.getStmtseq().apply(this);
            node.getOptlbreak().apply(this);
            String optBreak = stack.peek() ;
            if(!optBreak.equals(""))
            {
                codePart = codePart + "b END_SWITCH" + switchNum + "\n";
            }
            freeAllReg() ;
            switchConds.add(switchCount - 1 , switchCond) ;
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
            stack.push(temp);
            temp = "" ;
	}

	public void caseAEmptyproductionOptlbreak(AEmptyproductionOptlbreak node) {
            stack.push("");
	}

	public void caseAIncrementOrstmts(AIncrementOrstmts node) {
            String parent = myParent ;
            
            node.getId().apply(this);
            node.getIncrement().apply(this);

            String incr = stack.pop();
            String id = stack.pop();

            String idScope = PrintTree.getVarId(parent, id) ;
            temp = id + incr;
            stack.push(temp);
            temp = "";

            lastInReg = getOpenInReg(lastInReg) ;
            lastOutReg = getOpenOutReg(lastOutReg) ;

            codePart = codePart + "lw $t" + lastInReg + " , " + idScope + "\n" ;
            codePart = codePart + "addi $s" + lastOutReg + " , $t" + lastInReg + " , 1 \n" ;
            codePart = codePart + "sw $s" + lastOutReg + " , " + idScope + "\n" ;

            freeInReg(lastInReg) ;
            freeOutReg(lastOutReg) ;
	}

	public void caseADecrementOrstmts(ADecrementOrstmts node) {
            String parent = myParent ;
            node.getId().apply(this);
            node.getDecrement().apply(this);

            String decr = stack.pop();
            String id = stack.pop();

            temp = id + decr;
            stack.push(temp);
            temp = "";
            
            String idScope = PrintTree.getVarId(parent, id) ;

            lastInReg = getOpenInReg(lastInReg) ;
            lastOutReg = getOpenOutReg(lastOutReg) ;

            codePart = codePart + "lw $t" + lastInReg + " , " + idScope + "\n" ;
            codePart = codePart + "subi $s" + lastOutReg + " , $t" + lastInReg + " , 1 \n" ;
            codePart = codePart + "sw $s" + lastOutReg + " , " + idScope + "\n" ;

            freeInReg(lastInReg) ;
            freeOutReg(lastOutReg) ;
	}

	public void caseAAssignmentOrstmts(AAssignmentOrstmts node) {
            String parent = myParent;
            node.getId().apply(this);
            node.getAssignment().apply(this);
            node.getExpr().apply(this);

            String expr = stack.pop();
            String assignment = stack.pop();
            String id = stack.pop();

            temp = id + assignment + expr ;
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

            temp = id + colon + type + arr + commaid ;
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
            temp = "" ;
	}
	
	public void caseAEmptyproductionMorevarlisttwo(AEmptyproductionMorevarlisttwo node) {
            stack.push("");
	}

	public void caseAMultipleExpr(AMultipleExpr node) {
            String parent = myParent ;
            node.getExpr().apply(this);
            
            int firstExpReg = lastOutReg ;
            lastInReg = getOpenInReg(lastInReg) ;
            int firstExpTemp = lastInReg ;
            
            node.getAddop().apply(this);
            String operator = stack.peek() ;
            
            node.getTerm().apply(this);
            
            int secondExpReg = lastOutReg ;
            lastInReg = getOpenInReg(lastInReg) ;
            int secondExpTemp = lastInReg ;

             //codePart = codePart + "MULTIPLE EXPR" ;
            codePart = codePart + "move $t" + firstExpTemp + " , $s" + firstExpReg + "\n" ;
            codePart = codePart + "move $t" + secondExpTemp + " , $s" + secondExpReg + "\n" ;

            if(operator.equals("+"))
            {
                codePart = codePart + "add $s" + lastOutReg + " , $t" + firstExpTemp + " , $t" + secondExpTemp + "\n" ;
            }
            else if(operator.equals("-"))
            {
                codePart = codePart + "sub $s" + lastOutReg + " , $t" + firstExpTemp + " , $t" + secondExpTemp + "\n" ;
            }
            
            int outputReg = lastOutReg ;
            freeAllReg() ;
            lastOutReg = getOpenOutReg(outputReg) ;
            
            String term = stack.pop();
            String addop = stack.pop();
            String expr = stack.pop();

            temp = expr + addop + term ;
            stack.push(temp);
            temp = "";
	}

	public void caseASingleExpr(ASingleExpr node) {
            node.getTerm().apply(this);
	}

	public void caseATermmultopTerm(ATermmultopTerm node) {
            String parent = myParent ;
            
            node.getTerm().apply(this);
            
            int firstTermReg = lastOutReg ;
            lastInReg = getOpenInReg(lastInReg) ;
            int firstTermTemp = lastInReg ;
            
            node.getMultop().apply(this);
            String operator = stack.peek() ;
            
            node.getFactor().apply(this);
            
            int secondTermReg = lastOutReg ;
            lastInReg = getOpenInReg(lastInReg) ;
            int secondTermTemp = lastInReg ;
            
            //codePart = codePart + ("TERM MULTOP TERM");
            codePart = codePart + "move $t" + firstTermTemp + " , $s" + firstTermReg + "\n" ;
            codePart = codePart + "move $t" + secondTermTemp + " , $s" + secondTermReg + "\n" ;
            
            if(operator.equals("*"))
            {
                codePart = codePart + "mult $t" + firstTermTemp + " , $t" + secondTermTemp + "\n" ;
            }
            else if(operator.equals("/"))
            {
                codePart = codePart + "div $t" + firstTermTemp + " , $t" + secondTermTemp + "\n" ;
            }
            
            codePart = codePart + "mflo $s" + lastOutReg + "\n" ;

            String factor = stack.pop();
            String mult = stack.pop();
            String term = stack.pop();
           
            temp = term + mult + factor;
            stack.push(temp);
            temp = "";
	}

	public void caseAFactorTerm(AFactorTerm node) {
            node.getFactor().apply(this);
	}

	public void caseAParenexprFactor(AParenexprFactor node) {	
            node.getLparen().apply(this);
            node.getExpr().apply(this);
            node.getRparen().apply(this);

            String rparen = stack.pop();
            String expr = stack.pop();
            String lparen = stack.pop();
            temp = lparen + expr + rparen;
            stack.push(expr);
            temp = "";
	}

	public void caseAMinusfactorFactor(AMinusfactorFactor node) {
            String parent = myParent ;
            node.getMinus().apply(this);
            node.getFactor().apply(this);

            String factor = stack.pop();
            String minus = stack.pop();

            codePart = codePart + "neg $s" + lastOutReg + " , $s" + lastOutReg + "\n" ;
           
            temp = minus + factor ;
            stack.push(temp);
            temp = "";
	}

	public void caseAIntFactor(AIntFactor node) {
            node.getNumber().apply(this);
            temp = stack.pop();
            stack.push(temp) ;

            lastOutReg = getOpenOutReg(lastOutReg) ;
            codePart = codePart + "li $s" + lastOutReg + " , " + temp + "\n" ;
            temp = "" ;
	}

	public void caseARealFactor(ARealFactor node) {
            node.getReal().apply(this);
            temp = stack.pop();
            stack.push(temp);
            
            lastOutReg = getOpenOutReg(lastOutReg) ;
            codePart = codePart + "ld $s" + lastOutReg + " , " + temp + "\n" ;
            temp = "";
	}

	public void caseAIdarrayFactor(AIdarrayFactor node) {
            String parent = myParent ;
            
            node.getArrayorid().apply(this);
            boolean ifIsId = isId ;
            isId = false ;

            temp = stack.pop();

            String scopeId ;
            if(ifIsId){
                scopeId = PrintTree.getVarId(parent, temp) ;
            }
            else {
                scopeId = temp ;
            }


            lastOutReg = getOpenOutReg(lastOutReg) ;
            int regNum = lastOutReg ;

            String reg = "$s" + regNum ;
            codePart = codePart + "lw " + reg + " , " + scopeId + "\n" ;
 
            stack.push(temp);
            temp = "";

            freeInReg(regNum) ;
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
            stack.push(temp);
            temp = "";
	}
	
	public void caseAIdArrayorid(AIdArrayorid node) {
            node.getId().apply(this);
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

            temp = lsquare + number + rsquare ;
            stack.push(temp);
            temp = "";
	}

	public void caseATrueBoolean(ATrueBoolean node) {
            node.getTrue().apply(this);
 
            codePart = codePart + "li $s" + lastOutReg + " , 1 \n" ;
                
	}

	public void caseAFalseBoolean(AFalseBoolean node) {
            node.getFalse().apply(this);
 
            codePart = codePart + "li $s" + lastOutReg + " , 0 \n" ;
	}

	public void caseACondexprBoolean(ACondexprBoolean node) {
            node.getCondexpr().apply(this);

            codePart = codePart + "move $s" + (lastOutReg + 1) + " , $s" + lastOutReg + "\n" ;
            int out = lastOutReg++ ;
            freeOutReg(lastOutReg) ;
            getOpenOutReg(out) ;
	}
	
	public void caseABooleanBoolid(ABooleanBoolid node) {
            node.getBoolean().apply(this);
	}
	
	public void caseAIdBoolid(AIdBoolid node) {
            node.getId().apply(this);
	}

	public void caseACondexpr(ACondexpr node) {
            String parent = myParent ;
            node.getFirstexpr().apply(this);
            boolean isFirstExpr = isId ;
            isId = false; 
            //get first exp and store it to a register
            lastInReg = getOpenInReg(lastInReg) ;
            int firstExpReg = lastInReg ;
            
            //codePart = codePart +("COND EXPR") ;
            String boolNum = "_" + boolCount ;
            boolCount ++ ;
            codePart = codePart + "move $t" + lastInReg + " , $s" + lastOutReg + "\n" ;
            freeOutReg(lastOutReg) ;
            
            node.getCond().apply(this) ;           
            String cond = stack.peek() ;
 
            node.getSecondexpr().apply(this);
            boolean isSecondExprId = isId ;
            isId = false ;
            //get second exp and store it to a register
            lastInReg = getOpenInReg(lastInReg) ;
            int secondExpReg = lastInReg ;
            codePart = codePart + "move $t" + lastInReg + " , $s" + lastOutReg + "\n" ;
            freeOutReg(lastOutReg) ;
            lastInReg = getOpenInReg(lastInReg) ;
            int trueReg = lastInReg ;
            
            codePart = codePart + "li $t" + trueReg + " , 1 \n" ;
            String breakFinished = " $t"+ firstExpReg + " , $t"+secondExpReg + " , TRUE" + boolNum + "\n" + "b FALSE" + boolNum + "\n" ;
            
            if(cond.equals(">"))
            {
                codePart = codePart + "bgt" + breakFinished ;
            }
            else if(cond.equals("<"))
            {
                codePart = codePart + "blt" + breakFinished ;
            }
            else if(cond.equals(">="))
            {
                codePart = codePart + "bge" + breakFinished ;
            }
            else if(cond.equals("<="))
            {
                codePart = codePart + "ble" + breakFinished ;
            }
            else if(cond.equals("=="))
            {
                codePart = codePart + "beq" + breakFinished ;
            }
            else if(cond.equals("!=="))
            {
                codePart = codePart + "bne" + breakFinished ;
            }
            
            codePart = codePart + "TRUE" + boolNum + ":\n" ;
            codePart = codePart + "move $s" + lastOutReg + " , $t" + trueReg + "\n" ;
            codePart = codePart + "b END_CHECK" + boolNum + "\n" ;
            codePart = codePart + "FALSE" + boolNum + ":\n" ;
            codePart = codePart + "move $s" + lastOutReg + " , $zero \n" ;
            codePart = codePart + "END_CHECK" + boolNum + ":\n" ;
            
            freeInReg(trueReg) ;
            freeInReg(firstExpReg) ;
            freeInReg(secondExpReg) ;

            String secondexpr = stack.pop();
            cond = stack.pop();
            String firstexpr = stack.pop();
     
            temp = firstexpr + cond + secondexpr ;
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
        
    public void declareVar(String name , String type)
    {
        if(name.equals(""))
        {
            return ;
        }
        if(type.equals("REAL"))
        {
            dataPart = dataPart + name + ": .double 0 \n" ;
        }
        else if(type.equals("INT"))
        {
            dataPart = dataPart + name + ": .word 0 \n" ;
        }
        else if(type.equals("BOOLEAN"))
        {
            dataPart = dataPart + name + ": .word 0 \n" ;
        }
        else if(type.equals("STRING"))
        {
            dataPart = dataPart + name + ": .asciiz \n.align 2 \n" ;
        }
        else
        {
            //dealing with classes and objects and stuff...
        }
    }       

    public static boolean isNumeric(String str)
    {
      return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public int getOpenInReg(int reg)
    {
        int count = 0 ; 
        if(!regStatus[reg])
        {
            regStatus[reg] = true ;
            return reg ;
        }
        else
        {
            while(regStatus[reg])
            {
                reg++ ;
                if(reg > 7)
                {
                    reg = 0 ;
                    count++ ;
                    if(count == 1)
                    {
                        System.out.println("REGISTER ERROR") ;
                        return -1 ;
                    }

                }
            }
        }
        regStatus[reg] = true ;
        return reg ;
    }

    public int getOpenOutReg(int reg)
    {
        int count = 0 ; 
        if(!outRegStatus[reg])
        {
            outRegStatus[reg] = true ;
            return reg ;
        }
        else
        {
            while(outRegStatus[reg])
            {
                reg++ ;
                if(reg > 7)
                {
                    reg = 0 ;
                    count++ ;
                    if(count == 1)
                    {
                        System.out.println("REGISTER ERROR") ;
                        return -1 ;
                    }

                }
            }
        }
        outRegStatus[reg] = true ;
        return reg ;       
    }

    public void freeInReg(int reg)
    {
        if(reg > 7)
        {
            System.out.println("REGISTER ERROR") ;
            return ;
        }
        regStatus[reg] = false ;
        int count = 0 ;
        while(regStatus[count])
        {
            count++ ;
        }
        if(count == 0)
        {
            lastInReg = count ;
        }
        else
        {
            lastInReg = count - 1 ;
        }
    }

    public void freeOutReg(int reg)
    {
        if(reg > 7)
        {
            System.out.println("REGISTER ERROR") ;
            return ;
        }
        outRegStatus[reg] = false ;
        int count = 0 ;
        while(outRegStatus[count])
        {
            count++ ;
        }
        if(count == 0)
        {
            lastOutReg = count ;
        }
        else
        {
            lastOutReg = count - 1 ;
        }
    }

    public void freeAllReg()
    {
        lastOutReg = 0 ;
        lastInReg = 0 ;
        for(int i = 0 ; i < regStatus.length ; i++)
        {
            regStatus[i] = false ;
            outRegStatus[i] = false ;
        }
    }

}

