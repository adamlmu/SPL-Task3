//
// Created by spl211 on 02/01/2022.
//
#include "../include/SocketReceiver.h"
#include "../include/socketSender.h"
#include "../include/connectionHandler.h"
#include <thread>

using namespace std;

int main(int argc, char *argv[]) {
    string cmd;
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    string host = argv[1];
    short port = atoi(argv[2]);
    ConnectionHandler handler(host, port);

    if (!handler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    socketSender socketSender1(handler);
    SocketReceiver socketReceiver1(handler);

    thread keyboardRunner(&socketSender::readFromKeyboard, socketSender1);
    thread socketRunner(&SocketReceiver::readFromSocket, socketReceiver1);

    keyboardRunner.join();
    socketRunner.join();

    return 0;
}