/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

DIGIT 		= [0-9]
ALPHA		= [a-zA-Z]
NON_NL_WS 	= [\ \t\b\f\r\v\013] 

OBJECTID	= [a-z]({ALPHA}|{DIGIT}|_)*
TYPEID		= [A-Z]({ALPHA}|{DIGIT}|_)*
CONST_INT	= {DIGIT}+ 
STRING_TEXT	= [^\\\"\n\0]*
COMMENT_TEXT	= [^*)(\n]*


TRUE		= "t"[rR][uU][eE]
FALSE		= "f"[aA][lL][sS][eE]
CLASS		= [cC][lL][aA][sS][sS]
IF		= [iI][fF] 
THEN		= [tT][hH][eE][nN]
ELSE		= [eE][lL][sS][eE]
FI		= [fF][iI]
IN		= [iI][nN]
INHERITS	= [iI][nN][hH][eE][rR][iI][tT][sS]
ISVOID		= [iI][sS][vV][oO][iI][dD]
LET		= [lL][eE][tT]
LOOP		= [lL][oO][oO][pP]
POOL		= [pP][oO][oO][lL]
WHILE		= [wW][hH][iI][lL][eE]
CASE		= [cC][aA][sS][eE]
ESAC		= [eE][sS][aA][cC]
NEW		= [nN][eE][wW]
OF		= [oO][fF]
NOT		= [nN][oO][tT]

%state STRING
%state COMMENT
%state BLOCK_COMMENT
%state STRING_ERROR

%{

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  
*/

    // Max size of string constants
    static int MAX_STR_CONST = 1025;

    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();

    /* Takes a String or a Char, appends to buffer, and checks length */
    private boolean appendAndCheckLength(Object s) {
	string_buf.append(s);
	if (string_buf.length() >= MAX_STR_CONST) { return false; }
	return true;
    }

    private int curr_lineno = 1;
    int get_curr_lineno() {
	return curr_lineno;
    }

    private AbstractSymbol filename;

    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }

    AbstractSymbol curr_filename() {
	return filename;
    }

    private int nestedCommentCount = 0;

    private char getASCII(char c) {
        switch(c) {
            case 'n':
                return '\n';
            case 't':
                return '\t';
            case 'f':
                return '\f';
            case 'b':
                return '\b';
            default:
                return c;
        }
    }
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. 
*/

    // empty for now
%init}

%eofval{

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  
*/

    switch(yy_lexical_state) {
    case YYINITIAL:
	break;
    case STRING:
	yybegin(YYINITIAL);
	return new Symbol(TokenConstants.ERROR, "EOF in string constant");	
    case STRING_ERROR:
    case BLOCK_COMMENT:
	yybegin(YYINITIAL);
	return new Symbol(TokenConstants.ERROR, "EOF in comment");
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup
%char
%line

%%

<YYINITIAL> \"				{ string_buf.setLength(0); yybegin(STRING); } 
<STRING> \n				{ /* If newline encountered inside string literal, assume forgotten ", continue at next line */ 
						curr_lineno++; 
						yybegin(YYINITIAL);
						return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
					}
<STRING> (\0|'\0')			{ yybegin(STRING_ERROR); return new Symbol(TokenConstants.ERROR, "String contains null character"); }
<STRING> \\\n				{ 
						if (!appendAndCheckLength('\n')) {
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String constant too long");
						}
					}
<STRING> \\.				{ 
						if ((int)yytext().charAt(1) == 0) { /* Null char in string */
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String contains null character");
						}
						if (!appendAndCheckLength(getASCII(yytext().charAt(1)))) {
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String constant too long");
						}
					}
<STRING> {STRING_TEXT}			{
						if (!appendAndCheckLength(yytext())) {
							yybegin(STRING_ERROR);
							return new Symbol(TokenConstants.ERROR, "String constant too long");
						}
					} 
<STRING> \"				{ yybegin(YYINITIAL); return new Symbol(TokenConstants.STR_CONST, AbstractTable.stringtable.addString(string_buf.toString())); }

<STRING_ERROR> [^\n\"]			{ }
<STRING_ERROR> (\n|\")			{ curr_lineno++; yybegin(YYINITIAL); }

<YYINITIAL> \n				{ curr_lineno++; }
<YYINITIAL> {TRUE}			{ return new Symbol(TokenConstants.BOOL_CONST, new Boolean(true)); }
<YYINITIAL> {FALSE} 			{ return new Symbol(TokenConstants.BOOL_CONST, new Boolean(false)); }
<YYINITIAL> {CLASS}		 	{ return new Symbol(TokenConstants.CLASS); }
<YYINITIAL> {IF} 			{ return new Symbol(TokenConstants.IF); }
<YYINITIAL> {THEN}			{ return new Symbol(TokenConstants.THEN); }
<YYINITIAL> {ELSE}	 		{ return new Symbol(TokenConstants.ELSE); }
<YYINITIAL> {FI}		 	{ return new Symbol(TokenConstants.FI); }
<YYINITIAL> {IN}		 	{ return new Symbol(TokenConstants.IN); }
<YYINITIAL> {INHERITS}		 	{ return new Symbol(TokenConstants.INHERITS); }
<YYINITIAL> {ISVOID}		 	{ return new Symbol(TokenConstants.ISVOID); }
<YYINITIAL> {LET}		 	{ return new Symbol(TokenConstants.LET); }
<YYINITIAL> {LOOP}		 	{ return new Symbol(TokenConstants.LOOP); }
<YYINITIAL> {POOL}		 	{ return new Symbol(TokenConstants.POOL); }
<YYINITIAL> {WHILE}		 	{ return new Symbol(TokenConstants.WHILE); }
<YYINITIAL> {CASE}		 	{ return new Symbol(TokenConstants.CASE); }
<YYINITIAL> {ESAC}		 	{ return new Symbol(TokenConstants.ESAC); }
<YYINITIAL> {NEW}		 	{ return new Symbol(TokenConstants.NEW); }
<YYINITIAL> {OF}		 	{ return new Symbol(TokenConstants.OF); }
<YYINITIAL> {NOT}		 	{ return new Symbol(TokenConstants.NOT); }

<YYINITIAL> "--"			{ yybegin(COMMENT); }
<COMMENT> \n				{ curr_lineno++; yybegin(YYINITIAL); }
<COMMENT> .				{ }

<YYINITIAL> "(*"			{ yybegin(BLOCK_COMMENT); }
<BLOCK_COMMENT>	\n			{ curr_lineno++; }
<BLOCK_COMMENT> "(*"			{ nestedCommentCount++; }
<BLOCK_COMMENT> "*)"			{
						if (nestedCommentCount == 0) 	{ yybegin(YYINITIAL); }
						else 				{ nestedCommentCount--; }
					} 
<BLOCK_COMMENT> {COMMENT_TEXT}		{ } 
<BLOCK_COMMENT> .			{ }
<YYINITIAL> "*)"			{ return new Symbol(TokenConstants.ERROR, "Unmatched *)"); } 

<YYINITIAL> "*"				{ return new Symbol(TokenConstants.MULT); }
<YYINITIAL> "("				{ return new Symbol(TokenConstants.LPAREN); }
<YYINITIAL> ";"				{ return new Symbol(TokenConstants.SEMI); }
<YYINITIAL> "-"				{ return new Symbol(TokenConstants.MINUS); }
<YYINITIAL> ")"				{ return new Symbol(TokenConstants.RPAREN); }
<YYINITIAL> "<"				{ return new Symbol(TokenConstants.LT); }
<YYINITIAL> ","				{ return new Symbol(TokenConstants.COMMA); }
<YYINITIAL> "/"				{ return new Symbol(TokenConstants.DIV); }
<YYINITIAL> "+"				{ return new Symbol(TokenConstants.PLUS); }
<YYINITIAL> "<-"			{ return new Symbol(TokenConstants.ASSIGN); }
<YYINITIAL> "."				{ return new Symbol(TokenConstants.DOT); }
<YYINITIAL> "<="			{ return new Symbol(TokenConstants.LE); }
<YYINITIAL> "="				{ return new Symbol(TokenConstants.EQ); }
<YYINITIAL> ":"				{ return new Symbol(TokenConstants.COLON); }
<YYINITIAL> "{"				{ return new Symbol(TokenConstants.LBRACE); }
<YYINITIAL> "}"				{ return new Symbol(TokenConstants.RBRACE); }
<YYINITIAL> "~"				{ return new Symbol(TokenConstants.NEG); }
<YYINITIAL> "@"				{ return new Symbol(TokenConstants.AT); }
<YYINITIAL> "=>"			{ return new Symbol(TokenConstants.DARROW); }

<YYINITIAL> {NON_NL_WS}			{ } 
<YYINITIAL> {TYPEID}			{ return new Symbol(TokenConstants.TYPEID, AbstractTable.idtable.addString(yytext())); }
<YYINITIAL> {OBJECTID}			{ return new Symbol(TokenConstants.OBJECTID, AbstractTable.idtable.addString(yytext())); }
<YYINITIAL> {CONST_INT}			{ return new Symbol(TokenConstants.INT_CONST, AbstractTable.inttable.addString(yytext())); }
.                               	{ return new Symbol(TokenConstants.ERROR, yytext()); } 
