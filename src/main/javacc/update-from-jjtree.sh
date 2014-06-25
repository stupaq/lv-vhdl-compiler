#!/usr/bin/env bash
set -e

nl='\
'

java -cp ~/.m2/repository/net/java/dev/javacc/javacc/6.1.2/javacc-6.1.2.jar jjtree -BUILD_PARSER=false -BUILD_NODE_FILES=false -BUILD_TOKEN_MANAGER=false ../jjtree/vhdl.jjt
cat vhdl.jj \
  | sed "s/\/\*@bgen(jjtree)[^*/]*\*\//${nl}JJTREE_BEGIN${nl}/" \
  | sed "s/\/\*@egen\*\/$/${nl}JJTREE_END/" \
  | sed "s/\/\*@egen\*\//${nl}JJTREE_END${nl}/" \
  | sed "/JTREE_BEGIN/,/JTREE_END/d" \
  | sed "s/{\n}/{}/" > vhdl.jj.bak
mv vhdl.jj.bak vhdl.jj
rm -f JJTVHDL93ParserState.java Node.java VHDL93ParserTreeConstants.java

