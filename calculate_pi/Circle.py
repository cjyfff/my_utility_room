#! /usr/bin/env python
#coding=utf-8
import random
import time

time_start = time.time()

inside_count = 0
simulate_total = 25000000

for i in xrange(simulate_total):
    randA = random.random()
    randB = random.random()
    if ((randA * randA + randB * randB) < 1):
        inside_count = inside_count + 1

print (inside_count * 1.0 / simulate_total * 4)
print ("Time spent", time.time() - time_start)
