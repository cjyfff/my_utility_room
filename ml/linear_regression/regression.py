# coding=utf-8
import numpy as np
import matplotlib.pyplot as plt


def load_data_set():
    data_mat = [[0.1, 2.3],
                [0.2, 2.3],
                [0.5, 2.4],
                [0.6, 2.6]]
    label_mat = [2, 3, 4, 4]
    return data_mat, label_mat


def stand_regress(x_arr, y_arr):
    x_mat = np.mat(x_arr)
    y_mat = np.mat(y_arr).T
    x_tx = x_mat.T * x_mat
    if np.linalg.det(x_tx) == 0.0:
        print "This matrix is singular, cannot do inverse."
        return
    ws = x_tx.I * (x_mat.T * y_mat)
    return ws


def lwlr(test_point, x_arr, y_arr, k=1.0):
    """
    局部加权线性回归
    :param test_point: 结构为(x1, x2, ...)
    :param x_arr:
    :param y_arr:
    :param k:
    :return:
    """
    x_mat = np.mat(x_arr)
    y_mat = np.mat(y_arr).T
    m = x_mat.shape[0]
    """构建对角矩阵，通过矩阵相乘使另一个矩阵的每个元素都乘以对角线上的对应元素"""
    weights = np.mat(np.eye(m))
    for j in xrange(m):
        diff_mat = test_point - x_mat[j, :]
        """
        diff_mat * diff_mat.T代表这待测试点与x_mat[j, :]这个点的距离
        对应原理：
        |x(i) - x| ~ 0 then w(i) ~ 1
        |x(i) - x| ~ 正无穷大 then w(i) ~ 0
        """
        weights[j, j] = np.exp(diff_mat * diff_mat.T / (-2.0 * k ** 2))
    x_tx = x_mat.T * (weights * x_mat)
    if np.linalg.det(x_tx) == 0.0:
        print "This matrix is singular, cannot do inverse."
        return
    ws = x_tx.I * (x_mat.T * (weights * y_mat))
    return test_point * ws


def lwlr_test(test_arr, x_arr, y_arr, k=1.0):
    test_arr, x_arr, y_arr = map(np.array, [test_arr, x_arr, y_arr])
    m = test_arr.shape[0]
    y_hat = np.zeros(m)
    for i in xrange(m):
        y_hat[i] = lwlr(test_arr[i], x_arr, y_arr, k)
    return y_hat


def main():
    x, y = load_data_set()
    x = np.mat(x)
    y = np.mat(y)

    # stand_regress的调用：
    # x = np.mat(x)
    # y = np.mat(y)
    # ws = stand_regress(x, y)
    # print 'ws:',
    # fig = plt.figure()
    # ax = fig.add_subplot(111)
    # ax.scatter(x[:, 1].flatten().A[0], y.T[:, 0].flatten().A[0])
    # x_copy = x.copy()
    # x_copy.sort(0)
    # y_hat = x_copy * ws
    # ax.plot(x_copy[:, 1], y_hat)
    # plt.show()

    # lwlr的调用：
    y_hat = lwlr_test(x, x, y, 0.003)
    print 'lwlr res:', y_hat
    str_ind = x[:, 1].argsort(0)
    x_sort = x[str_ind][:, 0, :]
    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.plot(x_sort[:, 1], y_hat[str_ind])
    ax.scatter(x[:, 1].flatten().A[0], y.T.flatten().A[0], s=2, c='red')
    plt.show()


if __name__ == '__main__':
    main()