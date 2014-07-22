@SET source_dir=..\examples\vhdl2lv
@SET destination_dir=..\examples\lv2vhdl

@%JAVA_HOME%\bin\java.exe ^
  -cp config;target\classes;..\lv-scripting-java\target\classes;target\dependency\* ^
  -Dscripting.tools.path=..\lv-scripting-java\lv-scripting ^
  stupaq.lv2vhdl.TranslationDriver ^
  %source_dir%\work.add3(behavioral).vi ^
  %source_dir%\work.bin2dec(behavioral).vi ^
  %source_dir%\work.debouncer(behavioral).vi ^
  %source_dir%\work.debouncer(counter).vi ^
  %source_dir%\work.display(behavioral).vi ^
  %source_dir%\work.divider(behavioral).vi ^
  %source_dir%\work.stopwatch(behavioral).vi ^
  %source_dir%\work.switch(behavioral).vi ^
  %source_dir%\work.main(behavioral).vi ^
  %destination_dir%

@PAUSE
