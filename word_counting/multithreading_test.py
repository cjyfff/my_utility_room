#! /usr/bin/env python
# coding=utf-8
import sys
import threading

lock = threading.Lock()
queen = []
finish_flag = 0


def my_map(file):
    global finish_flag
    for line in file:
        word_list = line.split()
        lock.acquire()
        map(lambda x: queen.append(','.join(['%s' % x, "1"])), word_list)
        lock.release()
    lock.acquire()
    finish_flag = 1
    lock.release()


def my_reduce(word_count):
    global finish_flag
    while 1:
        try:
            lock.acquire()
            data = queen.pop(0)
        except IndexError:
            if finish_flag:
                break
            continue
        finally:
            lock.release()
        word = data.split(',')[0].strip()
        count = data.split(',')[1]
        if word not in word_count:
            word_count[word] = 1
        else:
            word_count[word] += int(count)


def main():
    try:
        fp = open(sys.argv[1], 'r')
    except IOError:
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

if __name__ == '__main__':
    main()
