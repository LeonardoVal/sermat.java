package sermat.parser;

import java_cup.runtime.Symbol;
import java.util.*;
import java.io.*;

%%

%unicode
%line
%column
%class Lexer
%cupsym Tokens
%cup
%implements Tokens

%{:

	public static List<Symbol> tokens(Reader input) throws IOException {
		Lexer lexer = new Lexer(input);
		List<Symbol> result = new ArrayList<Symbol>();
		for (Symbol token = lexer.next_token(); token.sym != Tokens.EOF; token = lexer.next_token()) {
			result.add(token);
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		Lexer lexer;
		if (args.length < 1) args = new String[]{ "" };
		for (int i = 0; i < args.length; ++i) {
			lexer = new Lexer(new InputStreamReader(args[i].length() > 0 ? new FileInputStream(args[i]) : System.in, "UTF8"));
			System.out.println(args[i] +":");
			for (Symbol token = lexer.next_token(); token.sym != Tokens.EOF; token = lexer.next_token()) {
				System.out.println("\t#"+ token.sym +" "+ token.value);
			}
		}
	}

%}

%%
"="
	{ return new Symbol(EQUALS, yyline, yycolumn, yytext()); }
","
	{ return new Symbol(COMMA, yyline, yycolumn, yytext()); }
":"
	{ return new Symbol(COLON, yyline, yycolumn, yytext()); }
"{"
	{ return new Symbol(LEFT_BRACE, yyline, yycolumn, yytext()); }
"}"
	{ return new Symbol(RIGHT_BRACE, yyline, yycolumn, yytext()); }
"["
	{ return new Symbol(LEFT_BRACK, yyline, yycolumn, yytext()); }
"]"
	{ return new Symbol(RIGHT_BRACK, yyline, yycolumn, yytext()); }
"("
	{ return new Symbol(LEFT_PAR, yyline, yycolumn, yytext()); }
")"
	{ return new Symbol(RIGHT_PAR, yyline, yycolumn, yytext()); }
"null"
	{ return new Symbol(NULL, yyline, yycolumn, yytext()); }
"true"
	{ return new Symbol(TRUE, yyline, yycolumn, yytext()); }	
"false"
	{ return new Symbol(FALSE, yyline, yycolumn, yytext()); }

NaN|[+-]?Infinity|[+-]?[0-9]+(\.[0-9]+)?([eE][+-]?[0-9]+)?
	{ String $1 = yytext(); Double $0 = Double.parseDouble($1); 
	  return new Symbol(NUM, yyline, yycolumn, yytext()); }
	  
[\$A-Z_a-z][\$\-\.A-Z_a-z0-9]*
	{ String $1 = yytext(); String $0 = $1;
	  return new Symbol(ID, yyline, yycolumn, yytext()); }

\"([^\\\"]|\\[\0-\uFFFF])*\"
	{ String $1 = yytext(); String $0 = $1;
	  return new Symbol(STR, yyline, yycolumn, yytext()); }

[ \t\r\n\f]+
	{ /* Ignore */ }
	
\/\*+([^\*]|\*+[^\/])*\*+\/
	{ /* Ignore */ }

.
	{ return new Symbol(error, yyline, yycolumn, "Unexpected input <"+ yytext() +">!"); }