cmake_minimum_required(VERSION 4.3)
project(PongClone)

set(CMAKE_CXX_STANDARD 20)

add_executable(PongClone main.cpp
)

target_include_directories(PongClone PRIVATE ${CMAKE_SOURCE_DIR}/include)
target_link_directories(PongClone PRIVATE ${CMAKE_SOURCE_DIR}/lib)

add_library(sfml-system SHARED IMPORTED)
add_library(sfml-window SHARED IMPORTED)
add_library(sfml-graphics SHARED IMPORTED)

add_library(box-2d STATIC IMPORTED)

set_target_properties(sfml-system PROPERTIES
        IMPORTED_LOCATION "${CMAKE_SOURCE_DIR}/lib/libsfml-system.so.3.1.0"
        INTERFACE_INCLUDE_DIRECTORY "${CMAKE_SOURCE_DIR}/include"
)

set_target_properties(sfml-graphics PROPERTIES
        IMPORTED_LOCATION "${CMAKE_SOURCE_DIR}/lib/libsfml-graphics.so.3.1.0"
        INTERFACE_INCLUDE_DIRECTORY "${CMAKE_SOURCE_DIR}/include"
)

set_target_properties(sfml-window PROPERTIES
        IMPORTED_LOCATION "${CMAKE_SOURCE_DIR}/lib/libsfml-window.so.3.1.0"
        INTERFACE_INCLUDE_DIRECTORY "${CMAKE_SOURCE_DIR}/include"
)

set_target_properties(box-2d PROPERTIES
        IMPORTED_LOCATION "${CMAKE_SOURCE_DIR}/lib/libbox2d.a"
        INTERFACE_INCLUDE_DIRECTORY "${CMAKE_SOURCE_DIR}/include"
)

target_link_libraries(PongClone PRIVATE sfml-graphics)
target_link_libraries(PongClone PRIVATE sfml-window)
target_link_libraries(PongClone PRIVATE sfml-system)
target_link_libraries(PongClone PRIVATE box-2d)
