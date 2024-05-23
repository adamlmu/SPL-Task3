//
// Created by spl211 on 03/01/2022.
//

#include "../include/socketSender.h"
#include <string>
using namespace std;


socketSender::socketSender(ConnectionHandler &handler):handler(handler) { }

void socketSender::readFromKeyboard() {
    while (!handler.getshouldTerminate()) {
        if (!handler.getshouldLogout()) {
            //if it is true - the message in the previous iteration was LOGOUT.
            // Don't wait for "cin" and just continue to the next iterations until the socketReceiver thread receives ack/error for the LOGOUT
            const short bufsize = 1024;
            char buf[bufsize];
            std::cin.getline(buf, bufsize); //console input
            std::string line(buf);
            int len = line.length();
            //split the line
            vector <string> arguments;
            istringstream split(line);
            for (string str; split >> str;)
                arguments.push_back(str);
            //get opcode
            short opcode = getOpcode(arguments[0]);
            if(opcode == 5){//post
                string content = line.substr(5);//if there was an issue with the string that was sent, the substr is incorrect
                encodeAndSend(opcode, arguments, content);
                //handler
            } else if(opcode == 6){ //PM
                //get the username | arguments[1] = username
                string content = line.substr(4 + arguments[1].length());
                encodeAndSend(opcode, arguments, content);
                //handler
            } else {
                //encode and send the message to the server at line 39.
                // But first:
                // set shouldLOGOUT = true if this is a LOGOUT message - so the thread will not get blocked on cin in the next iteration.
                // the thread will go to While to start a new iteration over and over again.
                // If the socketReceiver thread sets shouldLOGOUT = false - it means he got a Logout ERROR, and we continue with the while loop again.
                // If the socketReceiver thread sets shouldTerminate = true - it means he got a Logout ACK, and the while loop ends.
                // Therefore, the thread function(Task) ends and the thread terminates.
                if (opcode == 3) {
                    handler.logout();                           //Set shouldLOGOUT = true
                }
                if (!encodeAndSend(opcode, arguments, "")) {
                    handler.terminate();
                    break;
                }
            }
        }
    }
}

short socketSender::getOpcode(string command) {
    if(command == "REGISTER") return 1;
    if(command == "LOGIN") return 2;
    if(command == "LOGOUT") return 3;
    if(command == "FOLLOW") return 4;
    if(command == "POST") return 5;
    if(command == "PM") return 6;
    if(command == "LOGSTAT") return 7;
    if(command == "STAT") return 8;
    if(command == "BLOCK") return 12;
}

void socketSender::shortToBytes(short num, char* bytesArr) {
    //copy code
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

bool socketSender::encodeAndSend(short opcode, vector<string> arguments, string content) {
    //short to bytes
    bool result = true;
    char opcodeBytes[2];
    shortToBytes(opcode, opcodeBytes);
    //send opcode
    result = handler.sendBytes(opcodeBytes, 2);
    //send all arguments with \0

    if(result & (content.length() != 0)) {
        if (opcode == 6) {
            result = handler.sendArgument(arguments[1]);
        }
        result = handler.sendArgument(content);
    }
    else for(int i = 1; (result && i<arguments.size()) ; i++){
        result = handler.sendArgument(arguments[i]);//sends an argument with null terminator.
    }
    if (result) {
        result = handler.sendBytes(";", 1); }
    //send ;
    return result;
}