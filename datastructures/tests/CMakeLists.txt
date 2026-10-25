add_executable(datastructures_test test_main.cpp
        ../arraylist/ArrayList.cpp
        ../arraylist/ArrayList.h
        ../linkedlist/LinkedList.cpp
        ../linkedlist/LinkedList.h)
add_test(NAME datastructures_test COMMAND datastructures_test)