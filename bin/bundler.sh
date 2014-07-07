#!/bin/bash
set -e

basedir="`dirname $0`/../"
cd "${basedir}"

revision="`git rev-parse --short HEAD`"

zip -FSr "bundle-${revision}.zip" \
  lv-scripting-java/config \
  lv-scripting-java/lv-scripting \
  lv-scripting-java/target/dependency \
  lv-scripting-java/target/classes \
  lv-scripting-java/*.bat \
  lv-vhdl-frontend/config \
  lv-vhdl-frontend/target/dependency \
  lv-vhdl-frontend/target/classes \
  lv-vhdl-frontend/*.bat \
  examples/vhdl2lv/*.vhd \
  -x examples/vhdl2lv/*.vi \
  examples/demo/ \
  -x examples/demo/*.vi \
  -x *.git* \

