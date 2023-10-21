grammar AlmazFunctions;

program: function+;

mainFunction: funcWord 'main' '(' ')' functionBlock;
function: funcWord ID '(' ')' functionBlock;

functionBlock: '{' statement* '}';

statement: printStatement;

printStatement: 'println' '(' expression ')' ';';

// Added rule for variable declaration
variableDeclaration: 'var' ID '=' expression ';';

// Added rule for assignment statement
assignmentStatement: ID '=' expression ';';

// Other rules for expressions, terms, factors, etc. ...

funcWord: 'func ';

expression: term (('+'|'-') term)*;
term: factor (('*'|'/') factor)*;
factor: INT | ID | STRING | '(' expression ')';

ID: [a-zA-Z]+;
INT: ('0'..'9')+;
STRING: '"' .*? '"';

WS: [ \t\r\n]+ -> skip;
