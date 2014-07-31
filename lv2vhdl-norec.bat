@CALL %~dp0\configure.bat

@%JAVA_HOME%\bin\java.exe ^
  -cp %java_classpath% ^
  -Dscripting.tools.path=%scripting_tools% ^
  -Dtranslation.dependencies.follow=false ^
  stupaq.translation.lv2vhdl.TranslationDriver ^
  %*
