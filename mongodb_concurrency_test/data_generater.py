#! /usr/bin/env python
# coding=utf-8
import pymongo
from uuid import uuid4

MONGO_HOST = 'mongodb://127.0.0.1:27017/'

DB01 = 'test_db_01'
DB02 = 'test_db_02'
NUM = 5000

mc = pymongo.MongoClient(MONGO_HOST)
db01 = mc[DB01]
db02 = mc[DB02]

g = lambda: uuid4().hex[:10]

for db in [db01, db02]:
    num = 1
    for i in xrange(NUM):
        if i % 10000 == 0 and i != 0:
            print "Done %d w" % num
            num += 1
        if i % 4 == 0:
            db.coll01.save({'contry': 'china', 'uid': g(), 'name': g()})
        elif i % 4 == 1:
            db.coll01.save({'contry': 'usa', 'uid': g(), 'name': g()})
        elif i % 4 == 2:
            db.coll01.save({'contry': 'uk', 'uid': g(), 'name': g()})
        else:
            db.coll01.save({'contry': 'canada', 'uid': g(), 'name': g()})

    num = 1
    for i in xrange(NUM):
        if i % 10000 == 0 and i != 0:
            print "Done %d w" % num
            num += 1
        if i % 4 == 0:
            db.coll02.save({'contry': 'china', 'uid': g(), 'name': g()})
        elif i % 4 == 1:
            db.coll02.save({'contry': 'usa', 'uid': g(), 'name': g()})
        elif i % 4 == 2:
            db.coll02.save({'contry': 'uk', 'uid': g(), 'name': g()})
        else:
            db.coll02.save({'contry': 'canada', 'uid': g(), 'name': g()})
