@SET scripting_dir=..\..\lv-scripting
@SET target_vhdl=..\..\testing\main.vi
@SET project_dir=..\..\testing\

"C:\Program Files\Java\jdk1.7.0_60\bin\java.exe" -cp "target\classes;lv-scripting-java\target\classes;target\dependency\*" -Dscripting.tools.path="%scripting_dir%" vhdl2lv "%target_vhdl%" "%project_dir%"

@PAUSE
