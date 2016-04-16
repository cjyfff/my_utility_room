# coding=utf-8
import numpy as np


def stump_classify(data_matrix, dimen, thresh_val, thresh_ineq):
    """
    thresh_ineq决定了一个样本的权重
    """
    ret_array = np.ones((data_matrix.shape[0], 1))
    if thresh_ineq == 'lt':
        ret_array[data_matrix[:, dimen] <= thresh_val] = -1.0
    else:
        ret_array[data_matrix[:, dimen] > thresh_val] = -1
    return ret_array


def build_stump(data_arr, class_labels, D):
    data_mat = np.mat(data_arr)
    label_mat = np.mat(class_labels).T
    m, n = data_mat.shape
    num_steps = 10
    best_stump = {}
    best_class_est = np.mat(np.zeros((m, 1)))
    min_error = np.inf
    for i in xrange(n):
        """
        遍历特征n，寻找哪个特征构造出来的树庄误差最小
        树庄的数据结构：
        dim：哪一项特征
        thresh：阀值
        ineq：特征是小于阀值还是大于阀值
        """
        range_min = data_mat[:, i].min()
        range_max = data_mat[:, i].max()
        step_size = (range_max - range_min) / num_steps
        for j in xrange(-1, int(num_steps) + 1):
            for inequal in ['lt', 'gt']:
                """一步一步试出阀值"""
                thresh_val = (range_min + float(j) * step_size)
                predicted_vals = stump_classify(data_mat, i, thresh_val, inequal)
                err_arr = np.mat(np.ones((m, 1)))
                err_arr[predicted_vals == label_mat] = 0
                weighted_error = D.T * err_arr
                print "split: dim % d, thresh %.2f, thresh ineqal: " \
                      "%s, the weighted error is %.3f" % \
                      (i, thresh_val, inequal, weighted_error)
                if weighted_error < min_error:
                    min_error = weighted_error
                    best_class_est = predicted_vals.copy()
                    best_stump['dim'] = i
                    best_stump['thresh'] = thresh_val
                    best_stump['ineq'] = inequal
    return best_stump, min_error, best_class_est


def ada_classify(dat_to_class, classifier_arr):
    data_mat = np.mat(dat_to_class)
    m = data_mat.shape[0]
    agg_class_est = np.mat(np.zeros((m, 1)))
    for i in xrange(len(classifier_arr)):
        class_est = stump_classify(data_mat, classifier_arr[i]['dim'],
                                   classifier_arr[i]['thresh'],
                                   classifier_arr[i]['ineq'])
        agg_class_est += classifier_arr[i]['alpha'] * class_est
        """
        随着迭代进行，分类的结果会越来越强。
        即小于0的分类越来越小，大于0的越来越大
        """
        print("agg_class_est:", agg_class_est)
    return np.sign(agg_class_est)


if __name__ == '__main__':
    from adaboost import load_simp_data, ada_boost_train_ds
    data_mat, class_labels = load_simp_data()
    # D = np.mat(np.ones((5, 1)) / 5)
    # best_stump, min_error, best_class_est = build_stump(data_mat, class_labels, D)
    # print best_stump

    classifier_arr = ada_boost_train_ds(data_mat, class_labels, 30)
    res = ada_classify([1.3, 1.2], classifier_arr)
    print("res: ", res)
