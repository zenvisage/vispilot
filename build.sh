#!bin/bash
mvn clean
mvn install
cp lib/*.jar target/viz/WEB-INF/lib/.
