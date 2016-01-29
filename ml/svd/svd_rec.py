# coding=utf-8
import numpy as np


def load_ex_data():
    return [[4, 4, 0, 2, 2],
            [4, 0, 0, 3, 3],
            [4, 0, 0, 1, 1],
            [1, 1, 1, 2, 0],
            [2, 2, 2, 0, 0],
            [1, 1, 1, 0, 0],
            [5, 5, 5, 0, 0]]


def eclud_sim(in_a, in_b):
    """
    欧式距离计算相似度
    np.linalg.norm是计算矩阵的2范数，
    即矩阵每一个元素的平方和的开方
    """
    return 1.0 / (1.0 + np.linalg.norm(in_a - in_b))


def pears_sim(in_a, in_b):
    """皮尔逊相关系数"""
    if len(in_a) < 3:
        return 1.0
    return 0.5 + 0.5 * np.corrcoef(in_a, in_b, rowvar=0)[0][1]


def cos_sim(in_a, in_b):
    """余弦相似度"""
    num = float(np.dot(in_a.T, in_b))
    denom = np.linalg.norm(in_a) * np.linalg.norm(in_b)
    return 0.5 + 0.5 * (num / denom)


def stand_est(data_mat, user, sim_meas, item):
    """
    :param data_mat: 输入矩阵
    :param user: 用户编号
    :param sim_meas: 相似度计算方法
    :param item: 物品编号
    :return:
    """
    n = data_mat.shape[1]
    sim_total = 0.0
    rat_sim_total = 0.0
    for j in xrange(n):
        """对已评分的商品，计算与待评分商品的相似度，返回相似度的累加"""
        user_rating = data_mat[user, j]
        if user_rating == 0:
            continue
        """
        这里的把重叠的量剔除感觉是为了减少计算量，
        当然，重叠量为0的话，两个向量就完全不相似了。
        """
        over_lap = np.nonzero(np.logical_and(data_mat[:, item].A > 0,
                                             data_mat[:, j].A > 0))[0]
        if len(over_lap) == 0:
            similarity = 0
        else:
            similarity = sim_meas(data_mat[over_lap, item],
                                  data_mat[over_lap, j])
        sim_total += similarity
        rat_sim_total += similarity * user_rating
    if sim_total == 0:
        return 0
    return rat_sim_total / sim_total


def svd_est(data_mat, user, sim_means, item):
    n = data_mat.shape[1]
    sim_total = 0.0
    rat_sim_total = 0.0
    U, Sigma, VT = np.linalg.svd(data_mat)
    Sig4 = np.mat(np.eye(4) * Sigma[:4])
    """
    x_formed_items中的每一行代表data_mat中每一列的数据的线性变换，
    因为只取前4个奇异值，所以x_formed_items的维数是(n, 4)，比原矩阵低，
    因此能达到降维的目的。
    """
    x_formed_items = data_mat.T * U[:, :4] * Sig4.I
    print '79', x_formed_items
    for j in xrange(n):
        user_rating = data_mat[user, j]
        if user_rating == 0:
            continue
        similarity = sim_means(x_formed_items[item].T,
                               x_formed_items[j].T)
        print 'the %s and %s similarity is %s:' % (
            item, j, similarity
        )
        sim_total += similarity
        rat_sim_total += similarity * user_rating
    if sim_total == 0:
        return 0
    return rat_sim_total / sim_total


def recommend(data_mat, user, n=3, sim_means=cos_sim, est_method=stand_est):
    """返回未评分的商品的预测的评分，按降序排序"""
    unrated_items = np.nonzero(data_mat[user].A == 0)[1]
    if len(unrated_items) == 0:
        return 'you rated everything'
    item_scores = []
    for item in unrated_items:
        estimated_score = est_method(data_mat, user, sim_means, item)
        item_scores.append((item, estimated_score))
    return sorted(item_scores, key=lambda j: j[1], reverse=True)[:n]


if __name__ == '__main__':
    data = load_ex_data()
    # U, Sigma, VT = np.linalg.svd(data)
    # Sigma = sorted(Sigma, reverse=True)
    # sig2 = np.mat([[Sigma[0], 0],
    #                [0, Sigma[1]]])
    # svd_mat = U[:, :2] * sig2 * VT[:2, :]
    # print 'svd_mat:', svd_mat

    my_mat = np.mat(data)
    print 'eclud_sim:', eclud_sim(my_mat[:, 0], my_mat[:, 4])
    print 'cos_sim:', cos_sim(my_mat[:, 0], my_mat[:, 4])
    print 'pears_sim:', pears_sim(my_mat[:, 0], my_mat[:, 4])

    print 'recommend:', recommend(my_mat, 3, est_method=svd_est)
