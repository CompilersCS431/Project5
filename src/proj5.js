//Mai Nou Yang && Nathan Moder
//factor going in to a boolean is surrounded by parens (checked with you and it was okay)

Package ProjFive;

Helpers
	sp  = ' ';
	digit = ['0'..'9'];
	letter = ['a'..'z'] | ['A'..'Z'];
	quote = 34;	
	alphanumeric = letter | digit;
	anychars = [35 .. 255] | 32 | 9 | 13 | 10;
	tab = 9;
	cr = 13;
	lf = 10;
	hid = letter (letter | digit | '_')*;
	hreal = (digit)+'.'(digit)+;
	
Tokens
	tint = 'INT';
	treal = 'REAL';
	tstring = 'STRING';
	tbool = 'BOOLEAN';
	tvoid = 'VOID';
	if = 'IF';
	then = 'THEN';
	while = 'WHILE';
	else = 'ELSE';
	increment = '++';
	decrement = '--';
	get = 'GET()';
	new = 'NEW';
	return = 'RETURN';
	put = 'PUT';
	for = 'FOR';
	switch = 'SWITCH';
	break = 'BREAK';
	case = 'CASE';
	tclass = 'CLASS';
	default = 'DEFAULT';
	begin = 'BEGIN';
	end = 'END';
	true = 'TRUE';
	false = 'FALSE';
	lparen = '(';
	rparen = ')';
	lsquare = '[';
	rsquare = ']';
	lcurly = '{';
	rcurly = '}';
	period = '.';
	comma = ',';
	semicolon = ';';
	colon = ':';
	assignment = ':=';
	id = hid;
	number = digit+;
	spaces = sp+;
	whitespace = tab | cr | lf | cr lf ;
	real = hreal;
	plus = '+';
	minus = '-';
	multiply = '*';
	divide = '/';
	condlt = '<' | 60;
	condgt = '>' | 62;
	condeq = '==';
	condneq = '!==';
	condgeq = '>=';
	condleq = '<=';
	tdigit = digit;
	tletter = letter;
	anychars = quote anychars* quote;
	
			
	
Ignored Tokens
	whitespace, spaces
	;
Productions
	prog =
		begin classmethodstmts end
		;	
	classmethodstmts =
		{nonempty} classmethodstmts classmethodstmt
		| {emptyproduction}
		;
	classmethodstmt =
		{classdecl} tclass id lcurly methodstmtseqs rcurly
		| {typevarliststmt} type id lparen varlist rparen lcurly stmtseq rcurly
		| {idlisttype} id optlidlist colon type semicolon
		; 
	methodstmtseqs =
		{oneormore} methodstmtseqs methodstmtseq
		| {emptyproduction}
		;
	methodstmtseq =
		{typevarlist} type id lparen varlist rparen lcurly stmtseq rcurly
		| {idtype} id optlidlist colon type semicolon
		; 
	stmtseq =
		{oneormore} stmt stmtseq
		| {emptyproduction}
		; 
	stmt = 
		{exprassignment} id optionalidarray assignment expr semicolon
		| {expranychar} id optionalidarray assignment anychars semicolon
		| {idlist} id optlidlist colon type optionalidarray semicolon
    	| {ifboolean} if lparen boolean rparen then optionalelse 
    	| {ifid} if lparen id rparen then optionalelse 
		| {while} while lparen boolean rparen lcurly stmtseq rcurly
		| {for} for lparen optionaltype id assignment expr [firstsemicolon]:semicolon boolean [secondsemicolon]:semicolon orstmts rparen lcurly stmtseq rcurly
		| {get} id optionalidarray assignment get semicolon
		| {put} put lparen id optionalidarray rparen semicolon
		| {increment} id optionalidarray increment semicolon
		| {decrement} id optionalidarray decrement semicolon
		| {newassignment} [firstid]:id optionalidarray assignment new [secondid]:id lparen rparen semicolon
		| {idvarlisttwo} id lparen varlisttwo rparen semicolon
		| {multiplevarlisttwo}[firstid]:id optionalidarray period [secondid]:id lparen varlisttwo rparen optlidvarlisttwo semicolon
		| {return} return expr semicolon
    	| {returnboolean} return boolean semicolon
		| {idboolean} id optionalidarray assignment boolean semicolon
		| {switch} switch  factor lcurly case lparen number rparen [firstcolon]:colon [firststmtseq]:stmtseq optlbreak optionalswitchcases default [secondcolon]:colon [secondstmtseq]:stmtseq rcurly
		;
	optlidlist =
		{commaidlist} comma id optlidlist
		| {emptyproduction}
		;
  optionalelse = 
    {noelse} lcurly stmtseq rcurly
    | {else} [firstlcurly]:lcurly [firststmtseq]:stmtseq
			[firstrcurly]:rcurly else [secondlcurly]:lcurly [secondstmtseq]:stmtseq
			[secondrcurly]:rcurly
    ;
  optionalswitchcases =
		{caselist} case lparen number rparen colon stmtseq optlbreak optionalswitchcases
		| {emptyproduction}
		;
	optlbreak =
		{break} break semicolon
		| {emptyproduction}
		;
	orstmts =
		{increment} id increment
		| {decrement} id decrement
		| {assignment} id assignment expr
		;
	optlidvarlisttwo =
		{nonempty} period id lparen varlisttwo rparen optlidvarlisttwo
		| {emptyproduction} 
		;
	optionaltype =
		{type} type
		| {emptyproduction}
		; 
	varlist =
	    {multiple} id colon type optionalidarray commaidarray
		| {single} id colon type optionalidarray
		| {emptyproduction}  
		;
  commaidarray =
		{optlcommaidarr} comma id colon type optionalidarray commaidarray
		| {oneidarray} comma id colon type optionalidarray
		; 
	varlisttwo =
		{multiple} commaexprstring expr 
		| {single} expr
		| {emptyproduction} 
		; 
	commaexprstring =
	  	{optlcommaexprstr} commaexprstring expr comma 
		| {single} expr comma
	  	;  
	expr =
		{multiple} expr addop term
    	| {single} term
		;
	term =
		{termmultop} term multop factor
    	| {factor} factor
		;
	factor =
		{parenexpr} lparen expr rparen
		| {minusfactor} minus factor
		| {int} number
		| {real} real
		| {boolean} lparen boolean rparen 
     	| {idarray} id optionalidarray
		| {idvarlisttwo} id lparen varlisttwo rparen
		| {idarrvarlisttwo} [firstuniqueid]:id optionalidarray period [seconduniqueid]:id lparen 	varlisttwo rparen
		;
	optionalidarray =
		{array} lsquare number rsquare
		| {emptyproduction}
		;
	boolean =
		{true} true
		| {false} false
		| {condexpr} condexpr
		;
	condexpr = [firstexpr]:expr cond [secondexpr]:expr 
		;
	cond = 
		{lt} condlt  
		| {gt} condgt 
		|{equal} condeq
		| {notequal} condneq 
		| {geq} condgeq 
		| {leq} condleq 		
		;
	addop = 
		{plus} plus 
		| {minus} minus
		;
	multop = 
		{multiply} multiply 
		| {divide} divide
		;
	letterordigit =
		{letter} tletter letterordigit 
		| {digit} tdigit letterordigit
		;
	type = 
		{int} tint 
		| {real} treal 
		| {string} tstring 
		| {bool} tbool 
		| {void} tvoid 
		| {id} id
		;