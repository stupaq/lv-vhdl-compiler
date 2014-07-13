@SET source_dir=..\examples\vhdl2lv\
@SET destination_dir=..\examples\vhdl2lv

@%JAVA_HOME%\bin\java.exe ^
  -cp config;target\classes;..\lv-scripting-java\target\classes;target\dependency\* ^
  -Dscripting.tools.path=..\lv-scripting-java\lv-scripting vhdl2lv ^
  %source_dir%\branch_and_merge.vhd ^
  %source_dir%\many_ports.vhd ^
  %source_dir%\process_outer.vhd ^
  %source_dir%\main.vhd ^
  %destination_dir%

@PAUSE
