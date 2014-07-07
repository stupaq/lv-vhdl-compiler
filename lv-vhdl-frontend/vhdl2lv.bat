@SET target_vhdl=..\examples\vhdl2lv\all.vhd
@SET project_dir=..\examples\vhdl2lv

%JAVA_HOME%\bin\java.exe -cp config;target\classes;..\lv-scripting-java\target\classes;target\dependency\* -Dscripting.tools.path=..\lv-scripting-java\lv-scripting vhdl2lv "%target_vhdl%" "%project_dir%"

@PAUSE
