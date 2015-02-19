#! /usr/bin/env python
#coding=utf-8
import pymongo

mongo_client = pymongo.MongoClient('mongodb://localhost:27017/')
test_db = mongo_client['testA']
accounts = test_db['accounts']


def get_version_num(coll):
    version = coll.find_one({"version": True})
    version_num = version.get('version_num', 1)
    return version_num


def get_account(coll, account_name):
    version_num = get_version_num(coll)

    return coll.find_one({
               "account": account_name,
               "create_version": {"$lte": version_num},
               "$or": [
                    {"delete_version": {"$exists": False}},
                    {"delete_version": {"gt": version_num}}
                ]
        })

account_A = get_account(accounts, "A")
account_B = get_account(accounts, "B")

if account_A:
    print "account A's balance is %d" % account_A['balance']
if account_B:
    print "account B's balance is %d" % account_B['balance']
