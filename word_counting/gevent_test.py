#! /usr/bin/env python
#coding=utf-8
import sys
import threading
import gevent
from gevent.queue import Queue

datas = Queue()
l = threading.Semaphore(0)


def my_map(text):
    word_list = text.split()
    temp = 0
    for word in word_list:
        count_data = ','.join(['%s' % word, "1"])
        datas.put_nowait(count_data)
        if temp == 0:
            l.release()
            temp = 1


def my_reduce(word_counting):
    l.acquire()
    while not datas.empty():
        data = datas.get()
        word = data.split(',')[0].strip()
        count = data.split(',')[1]
        if word not in word_counting:
            word_counting[word] = 1
        else:
            word_counting[word] += int(count)


def main():
    try:
        fp = open(sys.argv[1], 'r')
    except:
        print "Can't open the file."
        sys.exit(1)

    word_counting = {}
    gevent.joinall([
        gevent.spawn(my_map, fp.read()),
        gevent.spawn(my_reduce, word_counting),
    ])

    print word_counting
    fp.close()

if __name__=='__main__':
    main()
