#!/bin/bash

if [ -z "${1}" ]; then
    echo "Pass version, user, and group on command line"
    echo You are:
    id
    exit
fi
if [ -z "${2}" ]; then
    echo "Pass version, user, and group on command line"
    echo You are:
    id
    exit
fi

if [ -z "${3}" ]; then
    echo "Pass version, user, and group on command line"
    echo You are:
    id
    exit
fi

version=$1
user=$2
group=$3
dir=/opt
iccdir=$dir/icc
dist=$PWD/dist

if [ -d $dir/icc-pre-${version} ]; then
    rm -rf $dir/icc-pre-${version} > /dev/null 2>&1 
fi

if [ ! -d $dir ]; then
    sudo mkdir $dir
    sudo chown $user:$group $dir
fi

if [ -d $iccdir ]; then
    mv $iccdir $dir/icc-pre-${version}
fi


if [ ! -d $iccdir ]; then
    sudo mkdir $iccdir
    sudo chown $user:$group $iccdir
    mkdir $iccdir/bin
    mkdir $iccdir/repo
    mkdir $iccdir/conf
fi

./deploy-iccj.bash $user $group

if [ ! -d $dist ]; then
    mkdir $dist
fi

cp changelog.txt $dist/icc-${version}-changelog.txt

cd $dir


rm -f $dist/iccj-${version}.zip  > /dev/null 2>&1
zip -r $dist/iccj-${version}.zip icc

# for immediate testing:
rm -rf icc-${version}-dist > /dev/null 2>&1

mv icc icc-${version}-dist

unzip $dist/iccj-${version}.zip

sudo chown -R $user:$group icc

