#! /usr/bin/env python
# coding=utf-8
import random

LABEL_POS_NUM = 100
LABEL_NEV_NUM = 100


def main():
    with open('test_set.txt', 'w+') as fp:
        for i in xrange(LABEL_POS_NUM):
            fp.write('%s %s 1\n' % (2 + random.random(), 3 + random.random()))
        for i in xrange(LABEL_NEV_NUM):
            fp.write('%s %s 0\n' % (random.random(), random.random()))


if __name__ == '__main__':
    main()
