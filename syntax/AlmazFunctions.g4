grammar AlmazFunctions;

mainFunction: funcWord 'main' '(' ')' functionBlock;

function: funcWord ID '(' ')' functionBlock;

functionBlock: '{' statement* '}';

statement: printStatement;

printStatement: 'println' '(' expression ')';

// Other rules for expressions, terms, factors, etc. ...

funcWord: 'func ';

expression: term (('+'|'-') term)*;

term: factor (('*'|'/') factor)*;

factor: INT | ID | STRING | '(' expression ')';

ID: [a-zA-Z]+;
INT: ('0'..'9')+;
STRING: '"' .*? '"';

WS: [ \t\r\n]+ -> skip;
