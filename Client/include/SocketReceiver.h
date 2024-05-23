//
// Created by spl211 on 03/01/2022.
//
#include "../include/connectionHandler.h"
#include <string>
#ifndef CLIENT_SOCKETRECEIVER_H
#define CLIENT_SOCKETRECEIVER_H


class SocketReceiver {
public:
    SocketReceiver(ConnectionHandler &handler);
    void readFromSocket();
    short bytesToShort(char* bytes);
    bool decodeAndPrint(char* result, char* opcode, std::string content);
private:
    ConnectionHandler& handler;
};


#endif //CLIENT_SOCKETRECEIVER_H
