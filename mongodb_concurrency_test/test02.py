#! /usr/bin/env python
# coding=utf-8
"""
测试mongodb在不同collection中的并发读写性能
"""

import pymongo
import timeit
import time
import multiprocessing as mul
from uuid import uuid4

MONGO_HOST = 'mongodb://127.0.0.1:27017/'
DB01 = 'test_db_01'
NUM = 200

g = lambda: uuid4().hex[:10]


def write(signal):
    mc = pymongo.MongoClient(MONGO_HOST)
    db01 = mc[DB01]
    for i in xrange(NUM):
        db01.coll01.save({'contry': 'china', 'uid': g(), 'name': g()})
        time.sleep(0.05)
    with signal.get_lock():
        signal.value = 0


def read(signal):
    mc = pymongo.MongoClient(MONGO_HOST)
    db01 = mc[DB01]
    n = 1
    while signal.value:
        db01.coll02.find({'contry': 'china'}).count()
        n += 1
    print "total read: %d" % n


def main():
    proc_list = []
    signal = mul.Value('i', 1)
    p1 = mul.Process(target=read, args=(signal, ))
    proc_list.append(p1)
    p2 = mul.Process(target=write, args=(signal, ))
    proc_list.append(p2)

    for p in proc_list:
        p.start()

    for p in proc_list:
        p.join()

if __name__ == '__main__':
    print timeit.timeit('main()', 'from __main__ import main', number=1)