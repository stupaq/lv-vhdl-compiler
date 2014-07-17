@SET source_dir=..\examples\vhdl\
@SET destination_dir=..\examples\lv2vhdl

@%JAVA_HOME%\bin\java.exe ^
  -cp config;target\classes;..\lv-scripting-java\target\classes;target\dependency\* ^
  -Dscripting.tools.path=..\lv-scripting-java\lv-scripting ^
  stupaq.vhdl2lv.TranslationDriver ^
  %source_dir%\work.branch_and_merge(behavioral).vi ^
  %source_dir%\work.many_ports_outer(behavioral).vi ^
  %source_dir%\work.process_outer(behavioral).vi ^
  %destination_dir%

@PAUSE
