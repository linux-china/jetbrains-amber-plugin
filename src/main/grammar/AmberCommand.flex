package org.mvnsearch.jetbrains.commandline.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import org.mvnsearch.jetbrains.commandline.psi.AmberCommandTypes;
import com.intellij.psi.TokenType;

%%

%public
%class AmberCommandLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%state INTERP

WHITE_SPACE=[ \t\f]+
NEWLINE=(\r\n|\r|\n)+
COMMENT="#"[^\r\n]*

VARIABLE=[A-Za-z_][A-Za-z0-9_]*

DQ_STRING=\"([^\\\"]|\\.)*\"?
SQ_STRING='[^']*'?

// a bare word/argument such as a path or file name (e.g. /usr/bin, file.name,
// xxx-yyy.name). A word may *contain* '-' but must not *start* with one, so a
// leading '-' / '--' is still tokenized as an option dash. ':' and '=' stay
// separators. Backslash escapes are allowed anywhere.
WORD_HEAD=([^ \t\f\r\n|<>&;{}\"'#=:\-]|\\.)
WORD_TAIL=([^ \t\f\r\n|<>&;{}\"'#=:]|\\.)
WORD={WORD_HEAD}{WORD_TAIL}*

// glob wildcards: '*', '?' and '[...]' bracket expressions. A GLOB is a
// filename-like run that contains at least one wildcard metacharacter. Like a
// WORD it must not start with '-' (so a leading '-'/'--' stays an option dash),
// unless it starts with the wildcard itself.
GLOB_PLAIN=([^ \t\f\r\n|<>&;{}\"'#=:*?\[\]]|\\.)
GLOB_PLAIN_HEAD=([^ \t\f\r\n|<>&;{}\"'#=:*?\[\]\-]|\\.)
GLOB_BRACKET=\[[^\]\r\n]*\]
GLOB_META=([*?]|{GLOB_BRACKET})
GLOB={GLOB_META}({GLOB_PLAIN}|{GLOB_META})*|{GLOB_PLAIN_HEAD}{GLOB_PLAIN}*{GLOB_META}({GLOB_PLAIN}|{GLOB_META})*

%%

<YYINITIAL> {
  {WHITE_SPACE}            { return TokenType.WHITE_SPACE; }
  {NEWLINE}                { return AmberCommandTypes.NEWLINE; }
  {COMMENT}                { return AmberCommandTypes.COMMENT; }

  "&&"                     { return AmberCommandTypes.AMP_AMP; }
  "||"                     { return AmberCommandTypes.PIPE_PIPE; }
  ">>"                     { return AmberCommandTypes.GT_GT; }
  "|"                      { return AmberCommandTypes.PIPE; }
  ">"                      { return AmberCommandTypes.GT; }
  "<"                      { return AmberCommandTypes.LT; }
  "&"                      { return AmberCommandTypes.AMP; }
  ";"                      { return AmberCommandTypes.SEMICOLON; }

  "--"                     { return AmberCommandTypes.DASH_DASH; }
  "-"                      { return AmberCommandTypes.DASH; }
  "="                      { return AmberCommandTypes.EQ; }
  ":"                      { return AmberCommandTypes.COLON; }
  "."                      { return AmberCommandTypes.DOT; }

  "{"                      { yybegin(INTERP); return AmberCommandTypes.LBRACE; }

  {DQ_STRING}              { return AmberCommandTypes.DQ_STRING; }
  {SQ_STRING}              { return AmberCommandTypes.SQ_STRING; }
  {GLOB}                   { return AmberCommandTypes.GLOB; }
  {WORD}                   { return AmberCommandTypes.WORD; }
}

<INTERP> {
  {WHITE_SPACE}            { return TokenType.WHITE_SPACE; }
  {VARIABLE}               { return AmberCommandTypes.VARIABLE; }
  "}"                      { yybegin(YYINITIAL); return AmberCommandTypes.RBRACE; }
}

[^]                        { return TokenType.BAD_CHARACTER; }
