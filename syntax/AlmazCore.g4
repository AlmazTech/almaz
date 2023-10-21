grammar AlmazCore;

import AlmazFunctions, AlmazVariables;

program: mainFunction;

WS: [ \t\r\n]+ -> skip;
