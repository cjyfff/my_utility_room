import numpy as np
from boost import build_stump


def load_simp_data():
    data_mat = np.matrix([[1, 2.1],
                          [2, 1.1],
                          [1.3, 1],
                          [1, 1],
                          [2, 1]
                          ])
    class_labels = [1, 1, -1, -1, 1]
    return data_mat, class_labels


def ada_boost_train_ds(data_arr, class_labels, num_lt=40):
    # weak_class_arr：保存弱分类器的列表
    weak_class_arr = []
    m = data_arr.shape[0]
    D = np.mat(np.ones((m, 1)))
    agg_class_est = np.mat(np.zeros((m, 1)))
    for i in xrange(num_lt):
        # class_est: 树庄的分类结果
        best_stump, error, class_est = build_stump(data_arr, class_labels, D)
        print "D:", D.T
        # TODO: alpha有问题
        alpha = float(0.5 * np.log(((1.0 - error) / max(error, 0.000001))))
        best_stump['alpha'] = alpha
        weak_class_arr.append(best_stump)
        print "class_est: ", class_est.T
        expon = np.multiply(-1 * alpha * np.mat(class_labels).T, class_est)
        D = np.multiply(D, np.exp(expon))
        D = D / D.sum()
        agg_class_est += alpha * class_est
        print "agg_class_est: ", agg_class_est.T
        agg_errors = np.multiply(np.sign(agg_class_est) !=
                                 np.mat(class_labels).T, np.ones((m, 1)))
        error_rate = agg_errors.sum() / m
        print "total error: %s\n" % error_rate
        if error_rate == 0.0:
            break
    return weak_class_arr
