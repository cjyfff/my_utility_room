#! /usr/bin/env python
#coding=utf-8
import sys
import threading

l = threading.Semaphore(0)
r = threading.Lock()
temp_list = []
finish_flag = 0


def my_map(file):
    global finish_flag
    temp = 0
    for line in file:
        word_list = line.split()
        r.acquire()
        map(lambda x: temp_list.append(','.join(['%s' % x, "1"])), word_list)
        r.release()
        if temp == 0:
            l.release()
            temp += 1
    finish_flag = 1


def my_reduce(word_count):
    global finish_flag
    l.acquire()
    while 1:
        try:
            r.acquire()
            data = temp_list.pop(0)
        except IndexError:
            r.release()
            if finish_flag:
                break
            continue
        r.release()
        word = data.split(',')[0].strip()
        count = data.split(',')[1]
        if word not in word_count:
            word_count[word] = 1
        else:
            word_count[word] += int(count)


def main():
    try:
        fp = open(sys.argv[1], 'r')
    except:
        print "Can't open the file."
        sys.exit(1)
    word_count = {}
    threads = []
    t1 = threading.Thread(target=my_map, args=(fp, ), )
    threads.append(t1)
    t2 = threading.Thread(target=my_reduce, args=(word_count, ), )
    threads.append(t2)

    for t in threads:
        t.start()
    for t in threads:
        t.join()
    print word_count
    fp.close()

if __name__=='__main__':
    main()
