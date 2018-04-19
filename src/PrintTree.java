package ProjFive;

import ProjFive.analysis.*;
import ProjFive.node.*;
import java.util.*;

public class PrintTree extends DepthFirstAdapter {
	private SymbolTable symbolTable;
	private Stack<String> stack;

	public PrintTree() {
		symbolTable = new SymbolTable();
		stack = new Stack<String>();
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

	}
	
	public void caseAClassdeclClassmethodstmt(AClassdeclClassmethodstmt node) {
		node.getTclass().apply(this);
		node.getId().apply(this);
		node.getLcurly().apply(this);
		node.getMethodstmtseqs().apply(this);
		node.getRcurly().apply(this);
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
	}
	
	public void caseAIdlisttypeClassmethodstmt(AIdlisttypeClassmethodstmt node) {
		node.getId().apply(this);
		node.getOptlidlist().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAOneormoreMethodstmtseqs(AOneormoreMethodstmtseqs node) {
		String sb = new String() ;		
		node.getMethodstmtseqs().apply(this);
		node.getMethodstmtseq().apply(this);
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;
	}

	public void caseAEmptyproductionMethodstmtseqs(AEmptyproductionMethodstmtseqs node) {
		stack.push("") ;
	}
	
	public void caseATypevarlistMethodstmtseq(ATypevarlistMethodstmtseq node) {
	    System.out.println("***RCURLY?***") ;		
		String sb = new String() ;		
		
		node.getType().apply(this);
		node.getId().apply(this);
		node.getLparen().apply(this);
		node.getVarlist().apply(this);
		node.getRparen().apply(this);
		node.getLcurly().apply(this);
		node.getStmtseq().apply(this);
		node.getRcurly().apply(this);

	    System.out.println("*Did all the node.gets*") ;
		
		System.out.println("PEEKING: " + stack.peek()) ;
		sb = stack.pop() + sb ;
		System.out.println("PEEKING: " + stack.peek()) ;
		sb = stack.pop() + sb ;
		System.out.println("PEEKING: " + stack.peek()) ;
		sb = stack.pop() + sb ;
		System.out.println("PEEKING: " + stack.peek()) ;
		sb = stack.pop() + sb ;

		System.out.println("STILL WORKS") ;		
		String vList = stack.pop() ;
		sb = vList + sb ;
				
		String[] vArr = vList.split(",") ;
		sb = stack.pop() + sb ;
		String methodId = stack.pop() ;
		String methodType = stack.pop() ;
		sb = methodType + methodId + sb ;
			
		Method m = new Method(methodId , methodType) ;

		System.out.println("IS IT STILL WORKING?") ;
		System.out.println(vList) ;
		for(int i = 0 ; i < vArr.length ; i++)
		{
			String[] ids = vArr[i].split(":") ;
			Variable v = new Variable(ids[0] , ids[1]) ;
			m.addParam(v) ;		
		}

		symbolTable.addMethod(m) ;
		stack.push(sb) ;
	}

	public void caseAIdtypeMethodstmtseq(AIdtypeMethodstmtseq node) {
		String sb = new String() ;		
		node.getId().apply(this);
		node.getOptlidlist().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getSemicolon().apply(this);
		
		sb = stack.pop() + sb ;
		String type = stack.pop() ;
		sb = type + sb ;
		sb = stack.pop() + sb ;
		String idList = stack.pop() ;

		String[] ids = idList.split(",") ;
		for(int i = 0 ; i < ids.length ; i++)
		{
			Variable v = new Variable(ids[i] , type) ;
			symbolTable.addVar(v) ;
		}
		
		sb = idList + sb ;
		String id = stack.pop() ;
		Variable v = new Variable(id , type) ;
		symbolTable.addVar(v) ;
		sb = id + sb ;

		stack.push(sb) ;
	}
	
	public void caseAAssignstringMethodstmtseq(AAssignstringMethodstmtseq node) {
		String sb = new String() ;		
		
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getAnychars().apply(this);
		node.getSemicolon().apply(this);
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;
	}
	
	public void caseAPrintstmtMethodstmtseq(APrintstmtMethodstmtseq node){
		String sb = new String() ;
		
		node.getPut().apply(this);
		node.getLparen().apply(this);
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getRparen().apply(this);
		node.getSemicolon().apply(this);
		
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;
	}
	
	public void caseAAssignmentMethodstmtseq(AAssignmentMethodstmtseq node) {
		String sb = new String() ;
		
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getGet().apply(this);
		node.getLparen().apply(this);
		node.getRparen().apply(this);
		node.getSemicolon().apply(this);

		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;
	}
	
	public void caseAIncrementMethodstmtseq(AIncrementMethodstmtseq node) {
		String sb = new String() ;		
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getIncrement().apply(this);
		node.getSemicolon().apply(this);
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;
	}
	
	public void caseADecrementMethodstmtseq(ADecrementMethodstmtseq node) {
		String sb = new String() ;

		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getDecrement().apply(this);
		node.getSemicolon().apply(this);
		
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;
	}
	
	public void caseADeclobjectMethodstmtseq(ADeclobjectMethodstmtseq node) {
		String sb = new String() ;
				
		node.getFirstid().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getNew().apply(this);
		node.getSecondid().apply(this);
		node.getLparen().apply(this);
		node.getRparen().apply(this);
		node.getSemicolon().apply(this);

		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		String type = stack.pop() ;
		sb = type + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		String arr = stack.pop() ;
		type = type + arr ;
		String id = stack.pop() ;
		sb = arr + sb ;
		sb = id + sb ;
		Variable v = new Variable(id , type) ;
		symbolTable.addVar(v) ;
		stack.push(sb) ;
	}
	
	public void caseAAssignbooleanMethodstmtseq(AAssignbooleanMethodstmtseq node) {
		String sb = new String() ;		
				
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getBoolean().apply(this);
		node.getSemicolon().apply(this);
		
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;
	}

	public void caseAOneormoreStmtseq(AOneormoreStmtseq node) {
		String sb = new String() ;
		node.getStmt().apply(this);
		node.getStmtseq().apply(this);
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;
	}

	public void caseEmptyproductionAStmtseq(AEmptyproductionStmtseq node) {
		stack.push("") ;
	}

	public void caseAExprassignmentStmt(AExprassignmentStmt node) {
	//	String id, expr, array, assignment;
		String sb = new String() ;		

		node.getId().apply(this);	
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getExpr().apply(this);
		node.getSemicolon().apply(this);
		
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;		
		
	}

	public void caseAExpranycharStmt(AExpranycharStmt node) {
		String sb = new String() ;
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getAnychars().apply(this);
		node.getSemicolon().apply(this);
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		sb = stack.pop() + sb ;
		stack.push(sb) ;
	} 

	public void caseAIdlistStmt(AIdlistStmt node) {
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getOptlidlist().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getOptionalidarray().apply(this);
		node.getSemicolon().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());

		stack.push(sb.toString());
	}

	public void caseAIfbooleanStmt(AIfbooleanStmt node) {
		StringBuilder sb = new StringBuilder();

		node.getIf().apply(this);
		node.getLparen().apply(this);
		node.getBoolid().apply(this);
		node.getRparen().apply(this);
		node.getThen().apply(this);
		node.getOptionalelse().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}

	public void caseAWhileStmt(AWhileStmt node) {
		StringBuilder sb = new StringBuilder();

		node.getWhile().apply(this);
		node.getLparen().apply(this);
		node.getBoolean().apply(this);
		node.getRparen().apply(this);
		node.getLcurly().apply(this);
		node.getStmtseq().apply(this);
		node.getRcurly().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());

		stack.push(sb.toString());
	}

	public void caseAForStmt(AForStmt node) {
		StringBuilder sb = new StringBuilder();

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

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		String id = stack.pop();
		sb.insert(0 , id) ;
		String type = stack.pop() ;
		sb.insert(0 , type) ;
		
		if(!type.equals(""))
		{
			Variable v = new Variable(id , type) ;
			symbolTable.addVar(v) ;
		}

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());

		stack.push(sb.toString());
	}

	public void caseAGetStmt(AGetStmt node) {
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getGet().apply(this);
		node.getSemicolon().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		if(stack.peek().equals("")){
			stack.pop();
		}
		else {
			sb.insert(0, stack.pop());
		}

		sb.insert(0, stack.pop());

		stack.push(sb.toString());
	}

	public void caseAPutStmt(APutStmt node) {
		StringBuilder sb = new StringBuilder();
	
		node.getPut().apply(this);
		node.getLparen().apply(this);
		node.getOptionalidarray().apply(this);
		node.getRparen().apply(this);
		node.getSemicolon().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		if(stack.peek().equals("")){
			stack.pop();
		}
		else {
			sb.insert(0, stack.pop());
		}

		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}

	public void caseAIncrementStmt(AIncrementStmt node){
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getIncrement().apply(this);
		node.getSemicolon().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		if(stack.peek().equals("")){
			stack.pop();
		}
		else {
			sb.insert(0, stack.pop());
		}
		sb.insert(0, stack.pop());

		stack.push(sb.toString());
	}

	public void caseADecrementStmt(ADecrementStmt node) {
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getDecrement().apply(this);
		node.getSemicolon().apply(this);
		
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		if(stack.peek().equals("")){
			stack.pop();
		}
		else {
			sb.insert(0, stack.pop());
		}
		sb.insert(0, stack.pop());

		stack.push(sb.toString());
	}

	public void caseANewassignmentStmt(ANewassignmentStmt node) {
		StringBuilder sb = new StringBuilder();

		node.getFirstid().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getNew().apply(this);
		node.getSecondid().apply(this);
		node.getLparen().apply(this);
		node.getRparen().apply(this);
		node.getSemicolon().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		String type = stack.pop();
		sb.insert(0, type);
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		if(stack.peek().equals("")){
			stack.pop();
		}
		else {
			type = type + stack.peek();
			sb.insert(0, stack.pop());
		}	
		String id = stack.pop();

		Variable v = new Variable(id, type);
		symbolTable.addVar(v);
		
		stack.push(sb.toString());
	}

	public void caseAIdvarlisttwoStmt(AIdvarlisttwoStmt node){
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getLparen().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);
		node.getSemicolon().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());

		stack.push(sb.toString());
	}

	public void caseAMultiplevarlisttwoStmt(AMultiplevarlisttwoStmt node) {
		StringBuilder sb = new StringBuilder();
	
		node.getFirstid().apply(this);
		node.getOptionalidarray().apply(this);
		node.getPeriod().apply(this);
		node.getSecondid().apply(this);
		node.getLparen().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);
		node.getOptlidvarlisttwo().apply(this);
		node.getSemicolon().apply(this);

		sb.insert(0, stack.pop());
		if(stack.peek().equals("")){
			stack.pop();
		}
		else {
			sb.insert(0, stack.pop());
		}

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
	
		if(stack.peek().equals("")){
			stack.pop();
		}
		else {
			sb.insert(0, stack.pop());
		}

		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}

	public void caseAReturnStmt(AReturnStmt node){
		StringBuilder sb = new StringBuilder();

		node.getReturn().apply(this);
		node.getExpr().apply(this);
		node.getSemicolon().apply(this);
		
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());		
		stack.push(sb.toString());
	}

	public void caseAIdbooleanStmt(AIdbooleanStmt node){
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getBoolean().apply(this);
		node.getSemicolon().apply(this);
	
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		if(stack.peek().equals("")){
			stack.pop();
			sb.insert(0, stack.pop());
			stack.push(sb.toString());
		}
		else {
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			stack.push(sb.toString());
		}
	}

	public void caseASwitchStmt(ASwitchStmt node){
		StringBuilder sb = new StringBuilder();

		node.getSwitch().apply(this);
		node.getFirstlparen().apply(this);
		node.getExpr().apply(this);
		node.getFirstrparen().apply(this);
		node.getLcurly().apply(this);
		node.getCase().apply(this);
		node.getSecondlparen().apply(this);
		node.getNumber().apply(this);
		node.getSecondlparen().apply(this);
		node.getFirstcolon().apply(this);
		node.getFirststmtseq().apply(this);
		node.getOptlbreak().apply(this);
		node.getOptionalswitchcases().apply(this);
		node.getDefault().apply(this);
		node.getSecondcolon().apply(this);
		node.getSecondstmtseq().apply(this);
		node.getRcurly().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		if(stack.peek().equals("")){
			stack.pop();
			if(stack.peek().equals("")){
				stack.pop();
				sb.insert(0, stack.pop());
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.push(sb.toString());
			}
			else {
				sb.insert(0, stack.pop());
				sb.insert(0, stack.pop());
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.push(sb.toString());
			}
		}
		else {
			sb.insert(0, stack.pop());
			if(stack.peek().equals("")){
				stack.pop();
				sb.insert(0, stack.pop());
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.push(sb.toString());
			}
			else {
				sb.insert(0, stack.pop());
				sb.insert(0, stack.pop());
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.pop();
				sb.insert(0, stack.pop());
				stack.push(sb.toString());
			}
		}
	}

	public void caseACommaidlistOptlidlist(ACommaidlistOptlidlist node) {
		StringBuilder sb = new StringBuilder();

		node.getComma().apply(this);
		node.getId().apply(this);
		node.getOptlidlist().apply(this);
		
		if(stack.peek().equals("")){
			stack.pop();
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			stack.push(sb.toString());
		}
		else {
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			stack.push(sb.toString());
		}
	}

	public void caseAEmptyproductionOptlidlist(AEmptyproductionOptlidlist node) {
		stack.push("");
	}

	public void caseANoelseOptionalelse(ANoelseOptionalelse node){
		StringBuilder sb = new StringBuilder();
		node.getLcurly().apply(this);
		node.getStmtseq().apply(this);
		node.getRcurly().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}

	public void caseAElseOptionalelse(AElseOptionalelse node){
		StringBuilder sb = new StringBuilder();
	
		node.getFirstlcurly().apply(this);
		node.getFirststmtseq().apply(this);
		node.getFirstrcurly().apply(this);
		node.getElse().apply(this);
		node.getSecondlcurly().apply(this);
		node.getSecondstmtseq().apply(this);
		node.getSecondrcurly().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());

		stack.push(sb.toString());
	}

	public void caseACaselistOptionalswitchcases(ACaselistOptionalswitchcases node){
		StringBuilder sb = new StringBuilder();

		node.getCase().apply(this);
		node.getLparen().apply(this);
		node.getNumber().apply(this);
		node.getRparen().apply(this);
		node.getColon().apply(this);
		node.getStmtseq().apply(this);
		node.getOptlbreak().apply(this);
		node.getOptionalswitchcases().apply(this);

		if(stack.peek().equals("")){
			stack.pop();
			if(stack.peek().equals("")){
				stack.pop();
				if(stack.pop().equals("")){
					stack.pop();
					stack.pop();
					stack.pop();
					sb.insert(0, stack.pop());
					stack.pop();
					sb.insert(0, stack.pop());
					stack.push(sb.toString());
					
				}
				else {
					sb.insert(0, stack.pop());
					stack.pop();
					stack.pop();
					sb.insert(0, stack.pop());
					stack.pop();
					sb.insert(0, stack.pop());
					stack.push(sb.toString());
				}
			}
			else {
				sb.insert(0, stack.pop());
				if(stack.peek().equals("")){
					stack.pop();
					stack.pop();
					stack.pop();
					sb.insert(0, stack.pop());
					stack.pop();
					sb.insert(0, stack.pop());
					stack.push(sb.toString());
				}
				else {
					sb.insert(0, stack.pop());
					stack.pop();
					stack.pop();
					sb.insert(0, stack.pop());
					stack.pop();
					sb.insert(0, stack.pop());
					stack.push(sb.toString());
				}
			}
		}
		else {
			sb.insert(0, stack.pop());
			if(stack.peek().equals("")){
				stack.pop();
				if(stack.pop().equals("")){
					stack.pop();
					stack.pop();
					stack.pop();
					sb.insert(0, stack.pop());
					stack.pop();
					sb.insert(0, stack.pop());
					stack.push(sb.toString());
					
				}
				else {
					sb.insert(0, stack.pop());
					stack.pop();
					stack.pop();
					sb.insert(0, stack.pop());
					stack.pop();
					sb.insert(0, stack.pop());
					stack.push(sb.toString());
				}
			}
			else {
				sb.insert(0, stack.pop());
				if(stack.peek().equals("")){
					stack.pop();
					stack.pop();
					stack.pop();
					sb.insert(0, stack.pop());
					stack.pop();
					sb.insert(0, stack.pop());
					stack.push(sb.toString());
				}
				else {
					sb.insert(0, stack.pop());
					stack.pop();
					stack.pop();
					sb.insert(0, stack.pop());
					stack.pop();
					sb.insert(0, stack.pop());
					stack.push(sb.toString());
				}
			}
		}
	}

	public void caseAEmptyproductionOptionalswitchcases(AEmptyproductionOptionalswitchcases node) {
		stack.push("");
	}

	public void caseABreakOptlbreak(ABreakOptlbreak node){
		StringBuilder sb = new StringBuilder();

		node.getBreak().apply(this);
		node.getSemicolon().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}

	public void caseAEmptyproductionOptlbreak(AEmptyproductionOptlbreak node) {
		stack.push("");
	}

	public void caseAIncrementOrstmts(AIncrementOrstmts node) {
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getIncrement().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}

	public void caseADecrementOrstmts(ADecrementOrstmts node) {
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getDecrement().apply(this);
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}

	public void caseAAssignmentOrstmts(AAssignmentOrstmts node) {
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getAssignment().apply(this);
		node.getExpr().apply(this);

		String expr = stack.pop();
		sb.insert(0, expr);
		sb.insert(0, stack.pop());
		String id = stack.pop();
		sb.insert(0, stack.pop());
	}

	public void caseANonemptyOptlidvarlisttwo(ANonemptyOptlidvarlisttwo node) {
		StringBuilder sb = new StringBuilder();

		node.getPeriod().apply(this);
		node.getId().apply(this);
		node.getLparen().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);
		node.getOptlidvarlisttwo().apply(this);

		if(stack.peek().equals("")){
			stack.pop();
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			stack.push(sb.toString());
		}
		else {
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			stack.push(sb.toString());
		}
		
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
		String sb = new String();
		
		node.getId().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getOptionalidarray().apply(this);
		node.getCommaidarray().apply(this);
		
		//System.out.println("******") ;
		//System.out.println(stack.peek()) ;
		sb = stack.pop() + sb ;
		//System.out.println(stack.peek()) ;
		sb = stack.pop() + sb ;
		//System.out.println(stack.peek()) ;
		sb = stack.pop() + sb ;
		//System.out.println(stack.peek()) ;
		sb = stack.pop() + sb ;
		//System.out.println(stack.peek()) ;
		sb = stack.pop() + sb ;
		//System.out.println("******") ;		
		System.out.println(sb) ;
		System.out.println(stack.peek()) ;
		stack.push(sb) ;
		System.out.println(stack.peek()) ;
		
		/*if(stack.peek().equals("")){
			stack.pop();
		}
		else {
			sb.insert(0, stack.pop());
		}
		
		if(stack.peek().charAt(0) == '['){
			StringBuilder temp = new StringBuilder();
			temp.insert(0, stack.pop());
			temp.insert(0, stack.pop());
			String type = temp.toString();
			temp.insert(0, stack.pop());
			String id = stack.pop();
			temp.insert(0, id);
			sb.insert(0, temp);
			Variable v = new Variable(id, type);
			if(!symbolTable.containsVar(id)){
				symbolTable.addVar(v);
			}
			else {
				//msg that var has been declared already
			}
		}
		else {
			String type = stack.pop();
			sb.insert(0, type);
			sb.insert(0, stack.pop());
			String id = stack.pop();
			sb.insert(0, id);
			Variable v = new Variable(id, type);
			if(!symbolTable.containsVar(id)){
				symbolTable.addVar(v);
			}
			else {
				//msg that var has been declared already
			}
		}*/
	}
	
	public void caseAEmptyproductionVarlist(AEmptyproductionVarlist node) {
		stack.push("");
	}

	public void caseAOptlcommaidarrCommaidarray(AOptlcommaidarrCommaidarray node) {
		StringBuilder sb = new StringBuilder();
		
		node.getComma().apply(this);
		node.getVarlist().apply(this);
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());

		stack.push(sb.toString());
	}
	
	public void caseAEmptyproductionCommaidarray(AEmptyproductionCommaidarray node){
		stack.push("");
	}

	public void caseAMultipleVarlisttwo(AMultipleVarlisttwo node){
		StringBuilder sb = new StringBuilder();

		node.getExprorbool().apply(this);
		node.getMorevarlisttwo().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
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
		StringBuilder sb = new StringBuilder();

		node.getComma().apply(this);
		node.getVarlisttwo().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}
	
	public void caseAEmptyproductionMorevarlisttwo(AEmptyproductionMorevarlisttwo node) {
		stack.push("");
	}

	public void caseAMultipleExpr(AMultipleExpr node) {
		StringBuilder sb = new StringBuilder();

		node.getExpr().apply(this);
		node.getAddop().apply(this);
		node.getTerm().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}

	public void caseASingleExpr(ASingleExpr node) {
		node.getTerm().apply(this);
	}

	public void caseATermmultopTerm(ATermmultopTerm node) {
		StringBuilder sb = new StringBuilder();

		node.getTerm().apply(this);
		node.getMultop().apply(this);
		node.getFactor().apply(this);

		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
	}

	public void caseAFactorTerm(AFactorTerm node) {
		node.getFactor().apply(this);
	}

	public void caseAParenexprFactor(AParenexprFactor node) {
		StringBuilder sb = new StringBuilder();
	
		node.getLparen().apply(this);
		node.getExpr().apply(this);
		node.getRparen().apply(this);
		
		sb.insert(0 , stack.pop()) ;
		sb.insert(0 , stack.pop()) ;
		sb.insert(0 , stack.pop()) ;	

		stack.push(sb.toString());
	}

	public void caseAMinusfactorFactor(AMinusfactorFactor node) {
		StringBuilder sb = new StringBuilder();
		node.getMinus().apply(this);
		node.getFactor().apply(this);
		
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());
		stack.push(sb.toString());
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
		StringBuilder sb = new StringBuilder();

		node.getId().apply(this);
		node.getLparen().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);

		if(stack.peek().equals(")")) {
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			stack.push(sb.toString());
		}
		else {
			//do something
		}
	}

	public void caseAIdarrvarlisttwoFactor(AIdarrvarlisttwoFactor node) {
		StringBuilder sb = new StringBuilder(); 

		node.getArrayorid().apply(this);
		node.getPeriod().apply(this);
		node.getId().apply(this);
		node.getLparen().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);

		if(stack.peek().equals(")")){
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			stack.push(sb.toString());
		}
		else {
			// do something here
		}
		
	}
	
	public void caseAArrayArrayorid(AArrayArrayorid node){
		StringBuilder sb = new StringBuilder();
		
		node.getId().apply(this);
		node.getLsquare().apply(this);
		node.getNumber().apply(this);
		node.getRsquare().apply(this);

		if(stack.peek().equals("]")){
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());

			stack.push(sb.toString());
		}
		else if(stack.peek().equals("")){
			//do something here 
		}

	}
	
	public void caseAIdArrayorid(AIdArrayorid node) {
		node.getId().apply(this);
	}

	public void caseAEmptyproductionOptionalidarray(AEmptyproductionOptionalidarray node)
	{
		stack.push("") ;
	}

	public void caseAArrayOptionalidarray(AArrayOptionalidarray node) {
		StringBuilder sb = new StringBuilder();
		node.getLsquare().apply(this);
		node.getNumber().apply(this);
		node.getRsquare().apply(this);

		if(stack.peek().equals("]")){
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
			sb.insert(0, stack.pop());
		}

		System.out.println(sb.toString());

		stack.push(sb.toString());
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
		StringBuilder sb = new StringBuilder();
		node.getFirstexpr().apply(this);
		node.getCond().apply(this);
		node.getSecondexpr().apply(this);
		sb.append(stack.pop());
		sb.insert(0, stack.pop());
		sb.insert(0, stack.pop());

		stack.push(sb.toString());
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

	public void caseAPlusAddop(APlusAddop  node) {
		node.getPlus().apply(this);
	}

	public void caseAMinusAddop(AMinusAddop node) {
		node.getMinus().apply(this);
	}

	public void caseAMultiplyMultop(AMultiplyMultop  node) {
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
	
	public void caseTTint(TTint node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTTreal(TTreal node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTTstring(TTstring node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTTbool(TTbool node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTTvoid(TTvoid node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	public void caseTIf(TIf node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTThen(TThen node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTWhile(TWhile node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTElse(TElse node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTIncrement(TIncrement node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTDecrement(TDecrement node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTGet(TGet node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	public void caseTNew(TNew node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTReturn(TReturn node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTPut(TPut node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTFor(TFor node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTSwitch(TSwitch node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTBreak(TBreak node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTCase(TCase node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTDefault(TDefault node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTBegin(TBegin node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTEnd(TEnd node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTTrue(TTrue node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTFalse(TFalse node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTLparen(TLparen node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTRparen(TRparen node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTLsquare(TLsquare node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTRsquare(TRsquare node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTLcurly(TLcurly node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTRcurly(TRcurly node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTPeriod(TPeriod node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTComma(TComma node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTSemicolon(TSemicolon node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTColon(TColon node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTAssignment(TAssignment node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTId(TId node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTNumber(TNumber node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTReal(TReal node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTPlus(TPlus node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTMinus(TMinus node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTMultiply(TMultiply node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTDivide(TDivide node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTCondlt(TCondlt node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTCondgt(TCondgt node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTCondeq(TCondeq node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTCondneq(TCondneq node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTCondgeq(TCondgeq node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
	
	public void caseTcondleq(TCondleq node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}

	public void caseTAnychars(TAnychars node){
		System.out.println(node.getText());
		stack.push(node.getText());
	}
}

