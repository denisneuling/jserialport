cmake_minimum_required(VERSION 3.0.0)

project (jserialport)

find_package(Java REQUIRED COMPONENTS Development)
find_package(JNI REQUIRED)

if (JNI_FOUND)
    message (STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    message (STATUS "JNI_LIBRARIES=${JNI_LIBRARIES}")
endif()
include_directories(${JNI_INCLUDE_DIRS})
include_directories(${JNI_LIBRARIES})

SET (LIB_TYPE SHARED)
SET (CMAKE_JNI_TARGET TRUE)
SET (CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -O3 -std=c++11")

if(WIN32)
	SET(STATIC_LIBRARY_CXX_FLAGS /MD)
	add_compile_options(/EHsc)
	string(REPLACE "/O2" "/Ox" CMAKE_CXX_FLAGS_RELEASE "${CMAKE_CXX_FLAGS_RELEASE}")
else(WIN32)
	SET(STATIC_LIBRARY_CXX_FLAGS)
endif()

file(GLOB GENERIC_SRCS "jserialport-native/src/main/cpp/*.cc")
file(GLOB PLAT_SRCS "${CMAKE_CURRENT_BINARY_DIR}/../src/main/cpp/*.cc")

SET (SRCS ${GENERIC_SRCS} ${PLAT_SRCS})
message("** SRCS: ${SRCS}")

add_library (jserialport ${LIB_TYPE} ${SRCS})

SET_TARGET_PROPERTIES (jserialport PROPERTIES LINKER_LANGUAGE CXX)