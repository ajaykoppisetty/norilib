#!/bin/bash
# This file is part of android-tdd-playground.
# Copyright (c) 2013 <Paul Estrada>
# https://github.com/pestrada/android-tdd-playground

bootanim=""
failcounter=0
echo "Waiting for emulator"
until [[ "$bootanim" =~ "stopped" ]]; do
   bootanim=`adb -e shell getprop init.svc.bootanim 2>&1`
   echo -n "."
   if [[ "$bootanim" =~ "not found" ]]; then
      let "failcounter += 1"
      if [[ $failcounter -gt 10 ]]; then
        echo "Failed to start emulator"
        exit 1
      fi
   fi
   sleep 1
done
