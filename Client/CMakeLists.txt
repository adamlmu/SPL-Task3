cmake_minimum_required(VERSION 3.20)
project(Client)
SET(LDFLAGS "-lboost_system -lboost_thread -pthread")
SET(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${LDFLAGS}")
SET(CMKAE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} ${GCC_COVERAGE_LINK_FLAGS}")
set(CMAKE_CXX_STANDARD 11)

add_executable(Client ./src/Client.cpp ./src/connectionHandler.cpp ./src/SocketReceiver.cpp ./src/socketSender.cpp)
