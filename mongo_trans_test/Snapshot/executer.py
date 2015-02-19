#! /usr/bin/env python
#coding=utf-8
import pymongo
import copy
from uuid import uuid4


def get_snapshot(coll, doc):
    snap = copy.deepcopy(doc)
    snap['_id'] = uuid4().hex
    coll.insert(snap)
    return snap


def get_version_num(coll):
    version = coll.find_one({"version": True})
    version_num = version.get('version_num', 1)
    return version_num


def update_version_num(coll):
    coll.update({"version": True},
                {"$inc": {"version_num": 1}})


def update_executer(coll, snap_A, snap_B):

    def sign_useless_snap(coll, snap_list):
        map(lambda x: coll.update({"_id": x['_id']},
                                  {"$set": {"is_useless": True}}),
            snap_list)

    if snap_A['balance'] < 100:
        sign_useless_snap(coll, [snap_A, snap_B])
        return False

    exe_1 = coll.update({"_id": snap_A['_id']}, {"$inc": {"balance": -100}})
    exe_2 = coll.update({"_id": snap_B['_id']}, {"$inc": {"balance": 100}})

    if exe_1['err'] or exe_2['err'] or not exe_1['updatedExisting'] or \
        not exe_2['updatedExisting']:
        sign_useless_snap(coll, [snap_A, snap_B])
        return False
    return True


def update_delete_version(coll, doc_list):
    version_num = get_version_num(coll)
    map(lambda x: coll.update({"_id": x['_id']},
                              {"$set": {"delete_version": version_num}}),
        doc_list)


def update_create_version(coll, doc_list):
    version_num = get_version_num(coll)
    map(lambda x: coll.update({"_id": x['_id']},
                              {"$set": {"create_version": version_num}}),
        doc_list)


mongo_client = pymongo.MongoClient('mongodb://localhost:27017/')
test_db = mongo_client['testA']
accounts = test_db['accounts']

account_A = accounts.find_one({"account": "A"})
account_B = accounts.find_one({"account": "B"})

snap_A = get_snapshot(accounts, account_A)
snap_B = get_snapshot(accounts, account_B)


if update_executer(accounts, snap_A, snap_B):
    # TODO: 以下3个版本号的修改操作应该设置为原子操作
    # 旧document的删除版本号设置为当前版本号
    update_delete_version(accounts, [account_A, account_B])

    # 将系统的版本号递增
    update_version_num(accounts)

    # 将新的document（即快照）设置创建版本号为当前版本号
    update_create_version(accounts, [snap_A, snap_B])

    print "execute successfully!"

else:
    print "fail to execute!"
