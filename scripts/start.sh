#!/bin/bash

cd ${HADOOP_PREFIX}

### START
bin/hdfs namenode -format

sbin/start-dfs.sh

bin/hdfs dfs -mkdir /user
bin/hdfs dfs -mkdir /user/mb346862

sbin/start-yarn.sh
