@SET source_dir=..\examples\vhdl\
@SET destination_dir=..\examples\vhdl2lv

@%JAVA_HOME%\bin\java.exe ^
  -cp config;target\classes;..\lv-scripting-java\target\classes;target\dependency\* ^
  -Dscripting.tools.path=..\lv-scripting-java\lv-scripting ^
  stupaq.vhdl2lv.TranslationDriver ^
  %source_dir%\branch_and_merge.vhd ^
  %source_dir%\many_ports.vhd ^
  %source_dir%\process_outer.vhd ^
  %destination_dir%

@PAUSE
