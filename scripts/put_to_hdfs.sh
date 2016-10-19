#!/bin/bash

hdfs dfs -mkdir /user
hdfs dfs -mkdir /user/mikib
hdfs dfs -mkdir /user/mikib/input

hdfs dfs -put $@ /user/mikib/input
