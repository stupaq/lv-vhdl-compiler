A bidirectional translation between LabVIEW and VHDL
====================================================

Prerequisites
-------------

Translation bridge requires Java JDK 7 and LabVIEW 2014 to run.
You will also need to set `JAVA_HOME` to appropriate location, depending on
your Java installation details.

In order to prepare the package by issuing `mvn package`, you also need Maven.
It is however advisable to rely on pre-compiled packages for testing purposes,
they are versioned in more manageable way (i.e. include all fixes for
particular feature).

Note that the bridge uses external binary VI files to communicate with LabVIEW
Scripting subsystem, therefore it is crucial to preserve directory structure of
the package.

It is highly advised to directly invoke `vhdl2lv.bat` and `lv2vhdl.bat`
scripts, which handle class path setup and automatically refer to utility VIs.
In case you are an adventurous man and wish to experience some pain, you can
handle all configuration yourself, `configure.bat` might be a good starting
point.

Usage notes
-----------

The general usage of the tool goes as follows:
* Using your favourite VHDL editor create a project with some initial code
  This step is optional, yet highly advised. Theoretically you can start
  designing the circuit from scratch in LabVIEW.
* Create a configuration script that handles VHDL to LabVIEW conversion. The
  following example assumes that your project consists of three VHDL files
  placed in `vhdl` subdirectory and destination LabVIEW diagrams should be
  placed in `lv` subdirectory.

      @CALL path\to\package\location\vhdl2lv.bat vhdl\a.vhd vhdl\b.vhd vhd\c.vhd lv\

* Run the script and play around with the diagrams.
* Create a configuration script responsible for LabVIEW to VHDL translation.
  The following example assumes that you have one top-level entity named `ent`
  placed in the default library `work`, and that you would like VHDL source to
  be placed in `vhdl2/` directory.

      @CALL path\to\package\location\lv2vhdl.bat lv\work.ent.vi vhdl2\

* Run it and check the codes.

Note that normally you would like to follow file naming conventions enforced by
the tool, so that modified VHDL code would end up in the same files as the
project source.

An example is worth a dozen words, you can find many of them under `examples/`
directory.
Each example project comes with the two mentioned scripts, both ready to be
double-clicked.

Copyright
---------

Copyright (c) 2014 Mateusz Machalica.

