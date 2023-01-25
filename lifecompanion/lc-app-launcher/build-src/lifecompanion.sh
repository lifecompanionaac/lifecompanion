#!/bin/sh

# This LifeCompanion launcher is used by both Mac and Unix
# The script is auto-generated and might be replaced in updates
# NEVER EDIT THIS SCRIPT or your changes could be lost

cd ${0%/*}/..

# Create data directory
if [ ! -d 'data' ]; then
    mkdir 'data'
fi

# Read xmxConfiguration from installation.properties
XMX_VALUE='-Xmx1G'
if [ -f 'data/installation.properties' ]; then
  XMX_VALUE='-Xmx'$(cat 'data/installation.properties' | grep "xmxConfiguration" | cut -d'=' -f2 | sed -e 's/\r//g')
fi

# Check if update process should be launched
LAUNCH_DIR='application'
if [ -f 'update/updated.flag' ]; then
  LAUNCH_DIR='update'
  UPDATE_DOWNLOAD_FINISHED_FLAG='-updateDownloadFinished'
else
  # Detect classpath to use for plugin
  if [ -f 'data/plugins/plugin-classpath' ]; then
    CLASSPATH_ARG='-classpath '$(head -n 1 data/plugins/plugin-classpath)
  fi
fi

# Make java runnable
PATH_TO_CMD=$LAUNCH_DIR/bin/java
chmod +x $PATH_TO_CMD

# Fix for process spawn on Unix/Mac
chmod +x $LAUNCH_DIR/lib/jspawnhelper

# launch VM with params
$PATH_TO_CMD -Djava.net.useSystemProxies=true\
  $XMX_VALUE \
	${CLASSPATH_ARG-'-Dnooparg'}\
  --add-reads lifecompanion.merged.module=javafx.base\
  --add-reads lifecompanion.merged.module=org.slf4j\
  --add-exports=javafx.graphics/com.sun.glass.ui=org.lifecompanion.app\
  --add-opens=javafx.graphics/javafx.stage=org.lifecompanion.app\
  --add-opens=javafx.graphics/com.sun.javafx.tk.quantum=org.lifecompanion.app\
  -splash:data/lifecompanion_splashscreen.png\
  -m org.lifecompanion.app/org.lifecompanion.LifeCompanion\
	${UPDATE_DOWNLOAD_FINISHED_FLAG-''}\
	"$@" &
