HTML Audio Notifier
====================

A simple build notifier for Jenkins that intercepts all build-events and plays sounds directly in a browser
when builds fail.

Development
-----------
* https://wiki.jenkins-ci.org/display/JENKINS/Extend+Jenkins
* https://wiki.jenkins-ci.org/display/JENKINS/Unit+Test

Creating an eclipse-project
---------------------------
mvn -DdownloadSources=true -DdownloadJavadocs=true eclipse:eclipse

Running the plugin locally
--------------------------
mvn hpi:run
