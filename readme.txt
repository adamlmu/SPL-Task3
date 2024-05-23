for Reactor Server:
mvn clean
mvn compile
mvn exec:java -Dexec.mainClass=”bgu.spl.net.impl.BGSServer.ReactorMain” -Dexec.args=”<PORT> <num of threads>” 

or TPC Server:
mvn clean
mvn compile
mvn exec:java -Dexec.mainClass=”bgu.spl.net.impl.BGSServer.TPCMain” -Dexec.args=”<PORT>”

for client:
make clean
make
./bin/BGSclient <IP address> <PORT>

examples:
REGISTER user1 pass 01-01-2000
LOGIN user1 pass 1
LOGOUT
FOLLOW 0 user2
POST content
PM user2 content
LOGSTAT
STAT user2|user3|user4
BLOCK user2

filtered words are in DataBase, can add filtered word via DataBase.setFilteredWords(List) or via DataBase constructor as implemented in code