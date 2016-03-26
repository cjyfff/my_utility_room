#! /usr/bin/env python
# coding=utf-8
import random
import numpy as np
import matplotlib.pyplot as plt


def load_data_set():
    data_mat = []
    label_mat = []
    fr = open('test_set.txt')
    for line in fr.readlines():
        line_arr = line.strip().split()
        data_mat.append([1.0, float(line_arr[0]), float(line_arr[1])])
        label_mat.append(int(line_arr[2]))
    return data_mat, label_mat


def sigmoid(in_x):
    return 1.0 / (1 + np.exp(-in_x))


def grad_ascent(data_mat_in, class_labels):
    """梯度上升"""
    data_matrix = np.mat(data_mat_in)
    label_mat = np.mat(class_labels).T
    m, n = data_matrix.shape
    alpha = 0.001
    max_cycles = 500
    weights = np.ones((n, 1))
    for k in xrange(max_cycles):
        h = sigmoid(np.dot(data_matrix, weights))
        error = (label_mat - h)
        """
        data_matrix.T * error就是目标函数
        每一次迭代得出的weights都会使得目标函数增长
        """
        weights += alpha * np.dot(data_matrix.T, error)
    return weights


def plot_best_fit(wei):
    weights = wei
    data_mat, label_mat = load_data_set()
    data_arr = np.array(data_mat)
    m = data_arr.shape[0]
    x_cord1 = []
    y_cord1 = []
    x_cord2 = []
    y_cord2 = []
    for i in xrange(m):
        if int(label_mat[i]) == 1:
            x_cord1.append(data_arr[i, 1])
            y_cord1.append(data_arr[i, 2])
        else:
            x_cord2.append(data_arr[i, 1])
            y_cord2.append(data_arr[i, 2])
    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.scatter(x_cord1, y_cord1, s=30, c='red', marker='s')
    ax.scatter(x_cord2, y_cord2, s=30, c='green')
    x = np.arange(-3.0, 3.0, 0.1)
    y = (-weights[0]-weights[1]*x) / weights[2]
    ax.plot(x, y)
    plt.xlabel('X1')
    plt.ylabel('X2')
    plt.show()


def stoc_grad_ascent(data_matrix, class_labels, num_iter=150):
    """随机梯度上升"""
    data_matrix = np.array(data_matrix)
    class_labels = np.array(class_labels)
    m, n = data_matrix.shape
    """假如weights是作为参数传入的话，即可实现线上学习"""
    weights = np.ones(n)
    for j in xrange(num_iter):
        data_index = range(m)
        for i in xrange(m):
            alpha = 4 / (1.0 + j + i) + 0.01
            rand_index = int(random.uniform(0, len(data_index)))
            h = sigmoid(sum(data_matrix[rand_index] * weights))
            error = class_labels[rand_index] - h
            weights += alpha * error * data_matrix[rand_index]
            del(data_index[rand_index])
    return weights


def judge_by_log(x1, x2, weis):
    z = weis[0] + weis[1] * x1 + weis[2] * x2
    res = sigmoid(z)
    if res > 0.5:
        return 1
    return 0


def main():
    data_arr, label_mat = load_data_set()
    print 'data_arr:', data_arr
    print 'label_mat:', label_mat
    w = grad_ascent(data_arr, label_mat)
    # w = stoc_grad_ascent(data_arr, label_mat)
    print('weights:', w)
    plot_best_fit(w)
    x1, x2 = 1, 3
    res = judge_by_log(x1, x2, w)
    print('res:', res)


if __name__ == '__main__':
    main()
