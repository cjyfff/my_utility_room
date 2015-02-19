#! /usr/bin/env python
#coding=utf-8
import pymongo
import time


def get_version_num(coll):
    version = coll.find_one({"version": True})
    version_num = version.get('version_num', 1)
    return version_num

mongo_client = pymongo.MongoClient('mongodb://localhost:27017/')
test_db = mongo_client['testA']
accounts = test_db['accounts']


while 1:
    version_num = get_version_num(accounts)
    useless_docs = accounts.find({
                       "$or": [
                           {"is_useless": True},
                           {"delete_version": {"$lt": version_num}}
                        ]
                  })
    map(lambda x: accounts.remove({"_id": x['_id']}), useless_docs)
    time.sleep(1)
