#!/bin/sh

cd `dirname $0`
protoc -I=protobuf --java_out=java protobuf/ogre/*.proto &&

#add @SuppressWarnings("all") to and decrease visibility of generated .java files
for file in java/com/berniecode/ogre/wireformat/V1*.java; do sed -i "" -e 's/public final class/@SuppressWarnings("all")\
final class/' $file; done