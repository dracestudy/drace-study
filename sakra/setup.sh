#!/bin/sh

set -e
set -u
set -x

sudo apt update
sudo apt install -y openjdk-8-jdk-headless maven pandoc sed bison flex

if [ ! -d predict-reqs ]; then
    echo "predict-reqs/ is missing. This artifact expects the bundled local dependencies to be present."
    exit 1
fi
(
    cd predict-reqs
    # run these commands to install to local repo
    mvn install:install-file -Dfile=./native/linux64/libz3java.so -DgroupId=com.microsoft.z3 -DartifactId=libz3java  -Dversion=4.4.0 -Dpackaging=so -DgeneratePom=true -Dclassifier=linux64
    mvn install:install-file -Dfile=./native/linux32/libz3java.so -DgroupId=com.microsoft.z3 -DartifactId=libz3java  -Dversion=4.4.0 -Dpackaging=so -DgeneratePom=true -Dclassifier=linux32
    mvn install:install-file -Dfile=./native/osx/libz3java.dylib -DgroupId=com.microsoft.z3 -DartifactId=libz3java  -Dversion=4.4.0 -Dpackaging=dylib -DgeneratePom=true -Dclassifier=osx
    mvn install:install-file -Dfile=./native/windows64/z3java.dll -DgroupId=com.microsoft.z3 -DartifactId=z3java  -Dversion=4.4.0 -Dpackaging=dll -DgeneratePom=true -Dclassifier=windows64
    mvn install:install-file -Dfile=./native/windows32/z3java.dll -DgroupId=com.microsoft.z3 -DartifactId=z3java  -Dversion=4.4.0 -Dpackaging=dll -DgeneratePom=true -Dclassifier=windows32
    mvn install:install-file -Dfile=com.microsoft.z3.jar -DgroupId=com.microsoft.z3 -DartifactId=com.microsoft.z3  -Dversion=4.4.0 -Dpackaging=jar -DgeneratePom=true
    mvn install:install-file -Dfile=lz4-1.3.0-b69d567.jar -DgroupId=net.jpountz.lz4 -DartifactId=lz4  -Dversion=1.3.0-b69d567 -Dpackaging=jar -DgeneratePom=true
)

echo "Select Java 8 as the default Java runtime environment."
sudo update-alternatives --config java

echo "Setup is complete."
exit 0
