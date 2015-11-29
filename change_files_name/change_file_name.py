#! /usr/bin/env python
#coding=utf-8
import os
import sys


def change_files_name(path):
    for file in os.listdir(path):
        os.rename('%s/%s' % (path, file),
                  '%s/%s' % (path, file.split('_')[1]))


if __name__ == '__main__':
    change_files_name(sys.argv[1])
