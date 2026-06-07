package org.mvnsearch.jetbrains.amber.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.mvnsearch.jetbrains.amber.psi.AmberTypes;
import com.intellij.psi.TokenType;

%%

%public
%class AmberLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

WHITE_SPACE=\s+

DIGIT=[0-9]
LETTER=[A-Za-z_]
INTEGER={DIGIT}+
REAL={DIGIT}+\.{DIGIT}+
NUMBER={REAL}|{INTEGER}
IDENTIFIER={LETTER}({LETTER}|{DIGIT})*

SHEBANG="#!"[^\r\n]*
DOC_COMMENT="///"[^\r\n]*
LINE_COMMENT="//"[^\r\n]*

STRING=\"([^\\\"\r\n]|\\.)*\"?
COMMAND=\$([^\\\$]|\\.|\\\r|\\\n)*\$

%%

<YYINITIAL> {
  {WHITE_SPACE}            { return TokenType.WHITE_SPACE; }
  {SHEBANG}                { return AmberTypes.SHEBANG; }
  {DOC_COMMENT}            { return AmberTypes.DOC_COMMENT; }
  {LINE_COMMENT}           { return AmberTypes.LINE_COMMENT; }

  "and"                    { return AmberTypes.AND_KW; }
  "as"                     { return AmberTypes.AS_KW; }
  "await"                  { return AmberTypes.AWAIT_KW; }
  "break"                  { return AmberTypes.BREAK_KW; }
  "cd"                     { return AmberTypes.CD_KW; }
  "clear"                  { return AmberTypes.CLEAR_KW; }
  "const"                  { return AmberTypes.CONST_KW; }
  "continue"               { return AmberTypes.CONTINUE_KW; }
  "cp"                     { return AmberTypes.CP_KW; }
  "disown"                 { return AmberTypes.DISOWN_KW; }
  "echo"                   { return AmberTypes.ECHO_KW; }
  "else"                   { return AmberTypes.ELSE_KW; }
  "exit"                   { return AmberTypes.EXIT_KW; }
  "exited"                 { return AmberTypes.EXITED_KW; }
  "fail"                   { return AmberTypes.FAIL_KW; }
  "failed"                 { return AmberTypes.FAILED_KW; }
  "for"                    { return AmberTypes.FOR_KW; }
  "from"                   { return AmberTypes.FROM_KW; }
  "fun"                    { return AmberTypes.FUN_KW; }
  "if"                     { return AmberTypes.IF_KW; }
  "import"                 { return AmberTypes.IMPORT_KW; }
  "in"                     { return AmberTypes.IN_KW; }
  "is"                     { return AmberTypes.IS_KW; }
  "len"                    { return AmberTypes.LEN_KW; }
  "let"                    { return AmberTypes.LET_KW; }
  "lines"                  { return AmberTypes.LINES_KW; }
  "lock"                   { return AmberTypes.LOCK_KW; }
  "loop"                   { return AmberTypes.LOOP_KW; }
  "ls"                     { return AmberTypes.LS_KW; }
  "main"                   { return AmberTypes.MAIN_KW; }
  "mv"                     { return AmberTypes.MV_KW; }
  "nameof"                 { return AmberTypes.NAMEOF_KW; }
  "not"                    { return AmberTypes.NOT_KW; }
  "or"                     { return AmberTypes.OR_KW; }
  "pid"                    { return AmberTypes.PID_KW; }
  "pub"                    { return AmberTypes.PUB_KW; }
  "pwd"                    { return AmberTypes.PWD_KW; }
  "ref"                    { return AmberTypes.REF_KW; }
  "return"                 { return AmberTypes.RETURN_KW; }
  "rm"                     { return AmberTypes.RM_KW; }
  "shellname"              { return AmberTypes.SHELLNAME_KW; }
  "shellversion"           { return AmberTypes.SHELLVERSION_KW; }
  "silent"                 { return AmberTypes.SILENT_KW; }
  "sleep"                  { return AmberTypes.SLEEP_KW; }
  "status"                 { return AmberTypes.STATUS_KW; }
  "succeeded"              { return AmberTypes.SUCCEEDED_KW; }
  "sudo"                   { return AmberTypes.SUDO_KW; }
  "suppress"               { return AmberTypes.SUPPRESS_KW; }
  "test"                   { return AmberTypes.TEST_KW; }
  "then"                   { return AmberTypes.THEN_KW; }
  "touch"                  { return AmberTypes.TOUCH_KW; }
  "trust"                  { return AmberTypes.TRUST_KW; }
  "unsafe"                 { return AmberTypes.UNSAFE_KW; }
  "while"                  { return AmberTypes.WHILE_KW; }

  "true"                   { return AmberTypes.TRUE; }
  "false"                  { return AmberTypes.FALSE; }
  "null"                   { return AmberTypes.NULL; }

  "Text"                   { return AmberTypes.TYPE_TEXT; }
  "Num"                    { return AmberTypes.TYPE_NUM; }
  "Bool"                   { return AmberTypes.TYPE_BOOL; }
  "Null"                   { return AmberTypes.TYPE_NULL; }
  "Int"                    { return AmberTypes.TYPE_INT; }

  "..="                    { return AmberTypes.DOTDOTEQ; }
  ".."                     { return AmberTypes.DOTDOT; }
  "=="                     { return AmberTypes.EQEQ; }
  "!="                     { return AmberTypes.NEQ; }
  "<="                     { return AmberTypes.LE; }
  ">="                     { return AmberTypes.GE; }
  "+="                     { return AmberTypes.PLUS_EQ; }
  "-="                     { return AmberTypes.MINUS_EQ; }
  "*="                     { return AmberTypes.STAR_EQ; }
  "/="                     { return AmberTypes.SLASH_EQ; }
  "%="                     { return AmberTypes.PERCENT_EQ; }
  "#["                     { return AmberTypes.HASH_LBRACK; }

  "+"                      { return AmberTypes.PLUS; }
  "-"                      { return AmberTypes.MINUS; }
  "*"                      { return AmberTypes.STAR; }
  "/"                      { return AmberTypes.SLASH; }
  "%"                      { return AmberTypes.PERCENT; }
  "="                      { return AmberTypes.EQ; }
  "<"                      { return AmberTypes.LT; }
  ">"                      { return AmberTypes.GT; }
  "?"                      { return AmberTypes.QUESTION; }
  "|"                      { return AmberTypes.PIPE; }

  "("                      { return AmberTypes.LPAREN; }
  ")"                      { return AmberTypes.RPAREN; }
  "["                      { return AmberTypes.LBRACK; }
  "]"                      { return AmberTypes.RBRACK; }
  "{"                      { return AmberTypes.LBRACE; }
  "}"                      { return AmberTypes.RBRACE; }
  ":"                      { return AmberTypes.COLON; }
  ","                      { return AmberTypes.COMMA; }

  {STRING}                 { return AmberTypes.STRING; }
  {COMMAND}                { return AmberTypes.COMMAND; }
  {NUMBER}                 { return AmberTypes.NUMBER; }
  {IDENTIFIER}             { return AmberTypes.IDENTIFIER; }
}

[^]                        { return TokenType.BAD_CHARACTER; }
