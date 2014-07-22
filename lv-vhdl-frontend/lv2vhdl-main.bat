@SET source_dir=..\examples\vhdl2lv
@SET destination_dir=..\examples\lv2vhdl

@%JAVA_HOME%\bin\java.exe ^
  -cp config;target\classes;..\lv-scripting-java\target\classes;target\dependency\* ^
  -Dscripting.tools.path=..\lv-scripting-java\lv-scripting ^
  stupaq.translation.lv2vhdl.TranslationDriver ^
  %source_dir%\work.main(behavioral).vi ^
  %destination_dir%

@PAUSE
