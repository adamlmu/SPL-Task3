

# Social Network Server and Client

This assignment involves implementing a simple social network server and client using a binary communication protocol. The server is based on the Thread-Per-Client (TPC) and Reactor patterns, and supports push notifications to send messages between clients or broadcast announcements.

## Server Implementation

1. Modify the existing interfaces to support bidirectional messaging and push notifications.
2. Refactor the Thread-Per-Client and Reactor servers to work with the new interfaces.
3. Implement the BGS (Ben Gurion Social) Protocol, which emulates a simple social network.

## BGS Protocol

The BGS Protocol supports the following commands:

- Register and login users
- Post messages and follow/unfollow other users
- Send private messages (PM)
- Request logged-in user statistics
- Block users

Each command has a specific format and set of parameters. The protocol uses opcodes to identify the command type and defines the message structure for each command.

## Client Implementation

Implement a C++ client with two threads:

1. One thread reads from the keyboard and sends commands to the server.
2. The other thread reads messages from the server and displays them on the screen.

The client should support the commands defined in the BGS Protocol and handle the corresponding server responses.
