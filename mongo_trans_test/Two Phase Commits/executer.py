#! /usr/bin/env python
#coding=utf-8
import pymongo
import time
import sys
import logging
import os
from uuid import uuid4

mongo_client = pymongo.MongoClient('mongodb://localhost:27017/')
test_db = mongo_client['test']
appid = uuid4().hex[:10]

transactions = test_db['transactions']
accounts = test_db['accounts']

logging.basicConfig(filename=os.path.join(os.getcwd(), 'log.txt'), level=logging.DEBUG)


def handle_invalid(trans):
    transactions.update(
        {'_id': trans['_id'], 'executeApp': appid},
        {'$set': {'state': 'invlid', 'lastModified': time.time()}},
    )
    logging.error('%s: transactions:%s invalid.' %
                  (time.strftime('%Y-%m-%d %H:%M:%S', time.localtime()), trans['_id']))
    sys.exit(1)


def handle_errors(trans):
    transactions.update(
        {'_id': trans['_id'], 'executeApp': appid},
        {'$set': {'state': 'error', 'lastModified': time.time()}},
    )
    logging.error('%s: transactions:%s execute error.' %
                  (time.strftime('%Y-%m-%d %H:%M:%S', time.localtime()), trans['_id']))
    sys.exit(1)


def update_executer(collection, update_tup, reverse_tup, error_fun, trans):
    i = 1
    while i < 3:
        a = collection.update(update_tup[0], update_tup[1])
        if a['err'] or not a['updatedExisting']:
            collection.update(reverse_tup[0], reverse_tup[1])
            if i == 2:
                error_fun(trans)
            i += 1
        else:
            break


def main():

    id = 1
    '''创建事务，id的作用是确保另一个程序运行的时候创建一条新的事务'''
    while 1:
        try:
            transactions.insert({'_id': id, 'source': 'A', 'destination': 'B',
                                 'value': 100, 'state': 'initial', 'executeApp': appid,
                                 'lastModified': time.time()})
            break
        except pymongo.errors.DuplicateKeyError:
            id += 1

    t = transactions.find_one({'state': 'initial', 'executeApp': appid})

    transactions.update(
        {'_id': t['_id'], 'state': 'initial', 'executeApp': appid},
        {'$set': {'state': 'pending', 'lastModified': time.time()}},
    )

    bal_of_account_A = accounts.find_one({'_id': t['source'],
                                          'pendingTransactions': {'$ne': t['_id']}})['balance']

    if bal_of_account_A - t['value'] < 0:
        handle_invalid(t)

    '''***下面操作不成功的话将进行回滚'***'''

    update_executer(
        accounts,
        ({'_id': t['source'], 'pendingTransactions': {'$ne': t['_id']}},
         {'$inc': {'balance': -t['value']}, '$push': {'pendingTransactions': t['_id']}}),
        ({'_id': t['source'], 'pendingTransactions': t['_id']},
         {'$inc': {'balance': t['value']}, '$pull': {'pendingTransactions': t['_id']}}),
        handle_errors,
        t,
    )

    update_executer(
        accounts,
        ({'_id': t['destination'], 'pendingTransactions': {'$ne': t['_id']}},
         {'$inc': {'balance': t['value']}, '$push': {'pendingTransactions': t['_id']}}),
        ({'_id': t['destination'], 'pendingTransactions': t['_id']},
         {'$inc': {'balance': -t['value']}, '$pull': {'pendingTransactions': t['_id']}}),
        handle_errors,
        t,
    )

    '''***上面操作不成功的话将进行回滚***'''

    transactions.update(
        {'_id': t['_id'], 'state': 'pending', 'executeApp': appid},
        {'$set': {'state': 'applied', 'lastModified': time.time()}},
    )

    accounts.update(
        {'_id': t['source'], 'pendingTransactions': t['_id']},
        {'$pull': {'pendingTransactions': t['_id']}},
    )

    accounts.update(
        {'_id': t['destination'], 'pendingTransactions': t['_id']},
        {'$pull': {'pendingTransactions': t['_id']}},
    )

    transactions.update(
        {'_id': t['_id'], 'state': 'applied'},
        {'$set': {'state': 'done', 'lastModified': time.time()}},
    )

if __name__ == '__main__':
    main()
