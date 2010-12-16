#!/bin/sh

protoc -I=protobuf --java_out=java protobuf/ogre/*.proto
