run manual

*execution environment: win 10

1.Start gradle
$gradle.bat
if you do not install gradle, please use gradlew.bat

2.Run rmiregistry in the project directory
$start rmiregistry -J-Djava.rmi.server.useCodebaseOnly=false -J-Djava.security.policy=F:\handout\policy.ini -J-Djava.rmi.server.codebase=file:///F:\handout\build\classes\java\main\

option:
-J-Djava.rmi.server.useCodebaseOnly:set false is to allow access to server codebase
-J-Djava.security.policy: specify policy file(use absolute path),allow client and server have permission to access the network
-J-Djava.rmi.server.codebase: specify server codebase(use absolute path)

3.Build the project
$gradle build