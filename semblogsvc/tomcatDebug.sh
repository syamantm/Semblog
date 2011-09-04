export MAVEN_OPTS="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

echo $MAVEN_OPTS

echo "MAVEN_OPTS = Starting tomcat in Debug, post:8000"

mvn tomcat:run -Dlog4j.configuration="file:log4j.xml"
