//
// Created by spl211 on 03/01/2022.
//
#include "../include/connectionHandler.h"
#include <string>
#include <vector>
using namespace std;
#ifndef CLIENT_SOCKETSENDER_H
#define CLIENT_SOCKETSENDER_H


class socketSender {
public:
    socketSender(ConnectionHandler &handler);
    void readFromKeyboard();
    short getOpcode(std::string command); //Takes REGISTER returns 1
    void shortToBytes(short opcode, char* bytesArr);
    bool encodeAndSend(short opcode, vector<string> arguments, string content);

private:
    ConnectionHandler& handler;
};


#endif //CLIENT_SOCKETSENDER_H
