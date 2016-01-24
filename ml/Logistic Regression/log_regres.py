# coding=utf-8
import numpy as np


def load_dataset():
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
    data_matrix = np.mat(data_mat_in)
    label_mat = np.mat(class_labels).T
    m, n = np.shape(data_matrix)
    alpha = 0.001
    max_cycles = 500
    weights = np.ones((n, 1))
    for k in xrange(max_cycles):
        h = sigmoid(data_matrix * weights)
        error = (label_mat - h)
        """
        data_matrix.T * error代表cost function，
        向量的内积就是协方差，梯度上升算法中，多次迭代，求得最大值
        就代表由w组成的模型误差最小
        """
        weights += alpha * data_matrix.T * error
    return weights
