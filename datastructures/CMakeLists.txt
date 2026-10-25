cmake_minimum_required(VERSION 4.4)
project(datastructures)

set(CMAKE_CXX_STANDARD 20)

add_executable(datastructures main.cpp
        arraylist/ArrayList.cpp
        arraylist/ArrayList.h
        linkedlist/LinkedList.cpp
        linkedlist/LinkedList.h)

enable_testing()
add_subdirectory(tests)
