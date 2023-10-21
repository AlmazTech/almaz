grammar AlmazCore;

import AlmazFunctions, AlmazVariables;

program: mainFunction* function*;

WS: [ \t\r\n]+ -> skip;
