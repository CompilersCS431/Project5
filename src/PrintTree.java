package ProjFive;

import ProjFive.analysis.*;
import ProjFive.node.*;
import java.util.*;

public class PrintTree extends DepthFirstAdapter {

	public PrintTree() {
		
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
		node.getLcurly.apply(this);
		node.getMethodstmtseqs.apply(this);
		node.getRcurly.apply(this);
	}
	
	public void caseATypevarliststmtClassmethodstmt(ATypevarliststmtClassmethodstmt node) {
		node.getType().apply(this);
		node.getId().apply(this);
		node.getLparen.apply(this);
		node.getVarlist.apply(this);
		node.getRparen.apply(this);
		node.getLcurly.apply(this);
		node.getStmtseq.apply(this);
		node.getRcurly.apply(this);
	}
	
	public void caseAIdlisttypeClassmethodstmt(AIdlisttypeClassmethodstmt node) {
		node.getId().apply(this);
		node.getOptlidlist.apply(this);
		node.getColon.apply(this);
		node.getType.apply(this);
		node.getSemicolon.apply(this);
	}

	public void caseAOneormoreMethodstmtseqs(AOneormoreMethodstmtseqs node) {
		node.getMethodstmtseqs().apply(this);
		node.getMethodstmtseq().apply(this);
	}

	public void caseAEmptyproductionMethodstmtseqs(AEmptyproductionMethodstmtseqs node) {
		
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
	}

	public void caseAIdtypeMethodstmtseq(AIdtypeMethodstmtseq node) {
		node.getId().apply(this);
		node.getOptlidlist().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAOneormoreStmtseq(AOneormoreStmtseq node) {
		node.getStmt().apply(this);
		node.getStmtseq().apply(this);
	}

	public void caseEmptyproductionAStmtseq(AEmptyproductionStmtseq node) {

	}

	public void caseAExprassignmentStmt(AExprassignmentStmt node) {
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getExpr().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAExpranycharStmt(AExpranycharStmt node) {
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getAnychars().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAIdlistStmt(AIdlistStmt node) {
		node.getId().apply(this);
		node.getOptlidlist().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getOptionalidarray().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAIfbooleanStmt(AIfbooleanStmt node) {
		node.getIf().apply(this);
		node.getLparen().apply(this);
		node.getBoolean().apply(this);
		node.getRparen().apply(this);
		node.getThen().apply(this);
		node.getOptionalelse().apply(this);
	}

	public void caseAIfidStmt(AIfidStmt node) {
		node.getIf().apply(this);
		node.getLparen().apply(this);
		node.getId().apply(this);
		node.getRparen().apply(this);
		node.getThen().apply(this);
		node.getOptionalelse().apply(this);
	}

	public void caseAWhileStmt(AWhileStmt node) {
		node.getWhile().apply(this);
		node.getLparen().apply(this);
		node.getBoolean().apply(this);
		node.getRparen().apply(this);
		node.getLcurly().apply(this);
		node.getStmtseq().apply(this);
		node.getRcurly().apply(this);
	}

	public void caseAForSmt(AForStmt node) {
		node.getFor().apply(this);
		node.getLparen().apply(this);
		node.getOptionaltype.apply(this);
		node.getId().apply(this);
		node.getAssignment().apply(this);
		node.getExpr().apply(this);
		node.getFirstsemicolon().apply(this);
		node.getOrstmts().apply(this);
		node.getRparen().apply(this);
		node.getLcurly().apply(this);
		node.getStmtseq().apply(this);
		node.getRcurly().apply(this);
	}

	public void caseAGetStmt(AGetStmt node) {
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getGet().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAPutStmt(APutStmt node) {
		node.getPut().apply(this);
		node.getLparen().apply(this);
		node.getOptionalidarray().apply(this);
		node.getRparen().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAIncrementStmt(AIncrementStmt node){
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getIncrement().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseADecrementStmt(ADecrementStmt node) {
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getDecrement().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseANewassignmentStmt(ANewassignmentStmt node) {
		node.getFirstid().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getNew().apply(this);
		node.getSecondi().apply(this);
		node.getLparen().apply(this);
		node.getRparen().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAIdvarlisttwoStmt(AIdvarlisttwoStmt node){
		node.getId().apply(this);
		node.getLparne().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAMultiplevarlisttwoStmt(AMultiplevarlisttwoStmt node) {
		node.getFirstid().apply(this);
		node.getOptionalidarray().apply(this);
		node.getPeriod().apply(this);
		node.getSecondid().apply(this);
		node.getLparen().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);
		node.getOptlvarlisttwo().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAReturnStmt(AReturnStmt node){
		node.getReturn().apply(this);
		node.getExpr().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAReturnbooleanStmt(AReturnbooleanStmt node) {
		node.getReturn().apply(this);
		node.getBoolean().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAIdbooleanStmt(AIdbooleanStmt node){
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
		node.getAssignment().apply(this);
		node.getBoolean().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseASwitchStmt(ASwitchStmt node){
		node.getSwitch().apply(this);
		node.getFactor().apply(this);
		node.getLcurly().apply(this);
		node.getCase().apply(this);
		node.getLparen().apply(this);
		node.getNumber().apply(this);
		node.getRparen().apply(this);
		node.getFirstcolon().apply(this);
		node.getFirststmtseq().apply(this);
		node.getOptlbreak().apply(this);
		node.getOptionalswitches().apply(this);
		node.getDefault().apply(this);
		node.getSecondcolon().apply(this);
		node.getSecondstmtseq().apply(this);
		node.getRcurly().apply(this);
	}

	public void caseACommaidlistOptlidlist(ACommaidlistOptlidlist node) {
		node.getCommaidlist().apply(this);
		node.getId().apply(this);
		node.getOptlidlist().apply(this);
	}

	public void caseAEmptyproductionOptlidlist(AEmptyproductionOptlidlist node) {
		
	}

	public void caseANoelseOptionalelse(ANoelseOptionalelse node){
		node.getLcurly().apply(this);
		node.getStmtseq().apply(this);
		node.getRcurly().apply(this);
	}

	public void caseAElseOptionalelse(AElseOptionalelse node){
		node.getFirstlcurly().apply(this);
		node.getFirststmtseq().apply(this);
		node.getFirstrcurly().apply(this);
		node.getElse().apply(this);
		node.getSecondlcurly().apply(this);
		node.getSecondstmtseq().apply(this);
		node.getSecondrcurly().apply(this);
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
	}

	public void caseAEmptyproductionOptionalswitchcases(AEmptyproductionOptionalswitchcases node) {
		
	}

	public void caseABreakOptlbreak(ABreakOptlbreak node){
		node.getBreak().apply(this);
		node.getSemicolon().apply(this);
	}

	public void caseAEmptyproductionOptlbreak(AEmptyproductionOptlbreak node) {
		
	}

	public void caseAIncrementOrstmts(AIncrementOrstmts node) {
		node.getId().apply(this);
		node.getIncrement().apply(this);
	}

	public void caseADecrementOrstmts(ADecrementOrstmts node) {
		node.getId().apply(this);
		node.getDecrement().apply(this);
	}

	public void caseAAssignmentOrstmts(AAssignmentOrstmts node) {
		node.getId().apply(this);
		node.getAssignment().apply(this);
		node.getExpr().apply(this);
	}

	public void caseANonemptyOptlidvarlisttwo(ANonemptyOptlidvarlisttwo node) {
		node.getPeriod().apply(this);
		node.getId().apply(this);
		node.getLparen().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);
		node.getOptlidvarlisttwo().apply(this);
	}

	public void caseATypeOptionaltype(ATypeOptionaltype node) {
		node.getType().apply(this);
	}

	public void caseAEmptyproductionOptionaltype(AEmptyproductionOptionaltype node) {

	}

	public void caseAMultipleVarlist(AMultipleVarlist node) {
		node.getId().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getOptionalidarray().apply(this);
		node.getCommaidarray().apply(this);
	}

	public void caseASingleVarlist(ASingleVarlist node){
		node.getId().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getOptionalidarray().apply(this);
	}

	
	public void caseAEmptyproductionVarlist(AEmptyproductionVarlist node) {
		
	}

	public void caseAOptlcommaidarrCommaidarray(AOptlcommaidarrCommaidarray node) {
		node.getComma().apply(this);
		node.getId().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getOptionalidarray().apply(this);
		node.getCommaidarray().apply(this);
	}

	public void caseAOneidarrayCommaidarray(AOneidarrayCommaidarray node) {
		node.getComma().apply(this);
		node.getId().apply(this);
		node.getColon().apply(this);
		node.getType().apply(this);
		node.getOptionalidarray().apply(this);
	}

	public void caseAMultipleVarlisttwo(AMultipleVarlisttwo node){
		node.getCommaexprstring().apply(this);
		node.getExpr().apply(this);
	}
	
	public void caseASingleVarlisttwo(ASingleVarlisttwo node){
		node.getExpr().apply(this);
	}

	public void caseAEmptyproductionVarlisttwo(AEmptyproductionVarlisttwo node) {
		
	}

	public void caseAOptlcommaexprstrCommaexprstring(AOptlcommaexprstrCommaexprstring node){
		node.getCommaexprstring().apply(this);
		node.getExpr().apply(this);
		node.getComma().apply(this);
	}

	public void caseASingleCommaexprstring(ASingleCommaexprstring node){
		node.getExpr().apply(this);
		node.getComma().apply(this);
	}

	public void caseAMultipleExpr(AMultipleExpr node) {
		node.getExpr().apply(this);
		node.getAddop().apply(this);
		node.getTerm().apply(this);
	}

	public void caseASingleExpr(ASingleExpr node) {
		node.getTerm().apply(this);
	}

	public void caseATermmultopTerm(ATermmultopTerm node) {
		node.getTerm().apply(this);
		node.getMultop.apply(this);
		node.getFactor().apply(this);
	}

	public void caseAFactorTerm(AFactorTerm node) {
		node.getFactor().apply(this);
	}

	public void caseAParenexprFactor(AParenexprFactor node) {
		node.getLparen().apply(this);
		node.getExpr().apply(this);
		node.getRparen().apply(this);
	}

	public void caseAMinusfactorFactor(AMinusfactorFactor node) {
		node.getMinus().apply(this);
		node.getFactor().apply(this);
	}

	public void caseAIntFactor(AIntFactor node) {
		node.getNumber().apply(this);
	}

	public void caseARealFactor(ARealFactor node) {
		node.getReal().apply(this);
	}

	public void caseABooleanFactor(ABooleanFactor node) {
		node.getLparen().apply(this);
		node.getBoolean().apply(this);
		node.getRparen().apply(this);
	}

	public void caseAIdarrayFactor(AIdarrayFactor node) {
		node.getId().apply(this);
		node.getOptionalidarray().apply(this);
	}
	
	public void caseAIdvarlisttwoFactor(AIdvarlisttwoFactor node) {
		node.getId().apply(this);
		node.getLparen().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);
	}

	public void caseAIdarrvarlisttwoFactor(AIdarrvarlisttwoFactor node) {
		node.getFirstuniqueid().apply(this);
		node.getOptionalidarray().apply(this);
		node.getPeriod().apply(this);
		node.getSeconduniqueid().apply(this);
		node.getLparen().apply(this);
		node.getVarlisttwo().apply(this);
		node.getRparen().apply(this);
	}

	public void caseAArrayOptionalidarray(AArrayOptionalidarray node) {
		node.getLsquare().apply(this);
		node.getNumber().apply(this);
		node.getRsquare().apply(this);
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

	public void caseACondexpr(ACondexpr node) {
		node.getFirstexpr().apply(this);
		node.getCond().apply(this);
		node.getSecondexpr().apply(this);
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

	public void caseALetterLetterordigit(ALetterLetterordigit node) {
		node.getLetter().apply(this);
		node.getLetterordigit().apply(this);
	}
	
	public void caseADigitLetterordigit(ADigitLetterordigit node) {
		node.getDigit().apply(this);
		node.getLetterordigit().apply(this);
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
	
}

