#!/bin/bash

SMT_REPORTS_LAYOUTS_LIST="/tmp/SMT/all-emcraft-layouts.txt"

#DIR1="/tmp/SMT/2014-07-21/"
#DIR2="/tmp/SMT/2014-10-23.141413/"

DIR1=$1
DIR2=$2

# tr ' ' '+' replaces each space with the plus symbol to form the filenames
#
for i in `cat $SMT_REPORTS_LAYOUTS_LIST | tr ' ' '+'` ; do
	./smtreport.sh -b $DIR1/$i.txt $DIR2/$i.txt
done

