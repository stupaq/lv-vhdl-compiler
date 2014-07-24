@CALL %~dp0\configure.bat

@%JAVA_HOME%\bin\java.exe ^
  -cp %java_classpath% ^
  -Dscripting.tools.path=%scripting_tools% ^
  stupaq.translation.vhdl2lv.TranslationDriver ^
  %*
