@CALL %~dp0\..\lv-scripting-java\configure.bat

@%JAVA_HOME%\bin\java.exe ^
  -cp %java_classpath% ^
  -Dscripting.tools.path=%scripting_tools% ^
  demo_highlight ^
  demo

@PAUSE
