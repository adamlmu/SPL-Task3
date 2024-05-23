//
// Created by spl211 on 03/01/2022.
//

#include "../include/SocketReceiver.h"
using namespace std;
SocketReceiver::SocketReceiver(ConnectionHandler &handler):handler(handler){}

void SocketReceiver::readFromSocket() {
    //loop : !handler.shouldTerminate()
    while (!handler.getshouldTerminate()) {
// 00 00 00 00 ;
// 00 00 00 00 content ;
//read 2 bytes for notification/ack/error (save this in char[] or char*) result
//read 2 bytes for opcode answer (register, login etc...) (save this in char[] or char*) opcode
//read the rest till you find a ; (save this in char[] or char*) content
        char result[2];
        char opcode[2];
        string content;
        bool isSent = true;
        if(!handler.getBytes(result, 2)) {
            isSent = false;
        }
        else if(!handler.getBytes(opcode, 2)){
            isSent = false;
        }
        else if (!handler.getContent(content)){
            isSent = false;
        }
        if(!isSent) {
            handler.terminate();
        }
        decodeAndPrint(result, opcode, content);

//Need to terminate if getBytes/getContent failed
    }
}
short SocketReceiver::bytesToShort(char* bytes) {
    //copy from their code
    short result = (short)((bytes[0] & 0xff) << 8);
    result += (short)(bytes[1] & 0xff);
    return result;
}

bool SocketReceiver::decodeAndPrint(char* result, char* opcode, string content) {
    short res = bytesToShort(result);
    short op = bytesToShort(opcode);
    content.pop_back(); //remove the ;
    if(res == 9){//notification
        op = (short)((opcode[0] & 0xff) << 8);
        cout << "NOTIFICATION " << content << endl;
    }
    else if(res == 10){//ack
        if(op == 3) {
            cout << "ACK 3" << endl;
            handler.terminate();                                   //Set shouldTerminate = true
        }
        else if (content.length()==0){
            cout << "ACK " << to_string(op) << endl;
        }
        else {
            cout << "ACK " << to_string(op) << " " << content << endl;
        }
    }
    else { //error
        cout << "ERROR " << op << endl;
        if (op==3) {
            handler.cancelLOGOUT();                           //Set shouldLOGOUT = false
        }
    }
}