#! /usr/bin/env python
#coding=utf-8
import pymongo


def get_version_num(coll):
    version = coll.find_one({"version": True})
    version_num = version.get('version_num', 1)
    return version_num


mongo_client = pymongo.MongoClient('mongodb://localhost:27017/')
test_db = mongo_client['testA']
accounts = test_db['accounts']

version_num = get_version_num(accounts)

account_datas = accounts.find({
               "create_version": {"$lte": version_num},
               "$or": [
                    {"delete_version": {"$exists": False}},
                    {"delete_version": {"gt": version_num}}
                ]
        })

for acc in account_datas:
    if acc.get('account') == "A":
        account_A = acc.get('balance')
    if acc.get('account') == "B":
        account_B = acc.get('balance')


print "account A's balance is %d" % account_A
print "account B's balance is %d" % account_B
