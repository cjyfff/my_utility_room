# coding=utf-8
import numpy as np
import matplotlib.pyplot as plt


def load_data_set():
    return [[4, 4], [0, 2], [2, 1],
            [4, 0], [0, 3], [3, 6],
            [4, 0], [0, 1], [1, 8],
            [1, 1], [1, 2], [0, 4],
            [2, 2], [2, 0], [0, -1],
            [1, 1], [1, 0], [0, 4],
            [5, 5], [5, 0], [0, 7]]


def dist_eclud(vec_a, vec_b):
    """计算两个向量的距离"""
    return np.linalg.norm(vec_a - vec_b)


def rand_cent(data_set, k):
    """随机选k个点作为初始质心"""
    n = data_set.shape[1]
    centroids = np.mat(np.zeros((k, n)))
    for j in xrange(n):
        min_j = min(data_set[:, j])
        range_j = float(max(np.array(data_set)[:, j]) - min_j)
        centroids[:, j] = min_j + range_j * np.random.rand(k, 1)
    return centroids


def k_means(data_set, k, dist_means=dist_eclud, create_cent=rand_cent):
    m = data_set.shape[0]
    cluster_assment = np.mat(np.zeros((m, 2)))
    centroids = create_cent(data_set, k)
    cluster_changed = True
    while cluster_changed:
        cluster_changed = False
        for i in xrange(m):
            min_dist = np.inf
            label = -1
            """判断在k个类别中，离哪个类别的质心较近"""
            for j in xrange(k):
                dist_ji = dist_means(centroids[j, :], data_set[i, :])
                if dist_ji < min_dist:
                    min_dist = dist_ji
                    label = j
            """cluster_assment每一行的内容就是(分类, 最小距离的平方)"""
            if cluster_assment[i, 0] != label:
                cluster_changed = True
            cluster_assment[i, :] = label, min_dist ** 2
        for lab in xrange(k):
            """
            把data_set中对应分类lab的数据找出来，
            质心就是这些数据在axis=0上的平均值
            """
            pts_in_clust = data_set[np.nonzero(cluster_assment[:, 0].A == lab)[0]]
            centroids[lab, :] = np.mean(pts_in_clust, axis=0)
    return centroids, cluster_assment


def show(data_set, k, centroids, cluster_assment):
    num_samples, dim = data_set.shape
    mark = ['or', 'ob', 'og', 'ok', '^r', '+r', 'sr', 'dr', '<r', 'pr']
    for i in xrange(num_samples):
        mark_index = int(cluster_assment[i, 0])
        plt.plot(data_set[i, 0], data_set[i, 1], mark[mark_index])
    mark = ['Dr', 'Db', 'Dg', 'Dk', '^b', '+b', 'sb', 'db', '<b', 'pb']
    for i in xrange(k):
        plt.plot(centroids[i, 0], centroids[i, 1], mark[i], markersize=12)
    plt.show()


def main():
    data_mat = np.mat(load_data_set())
    my_centroids, clust_assing = k_means(data_mat, 2)
    show(data_mat, 2, my_centroids, clust_assing)


if __name__ == '__main__':
    main()
