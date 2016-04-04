# coding=utf-8
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
    D = np.mat(np.ones((m, 1)) / m)
    agg_class_est = np.mat(np.zeros((m, 1)))
    for i in xrange(num_lt):
        # class_est: 树庄的分类结果
        best_stump, error, class_est = build_stump(data_arr, class_labels, D)
        print "D:", D.T
        alpha = float(0.5 * np.log(((1.0 - error) / max(error, 1e-16))))
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
    return weak_class_arr, agg_class_est


def plotROC(pred_strengths, class_labels):
    """
    从右上角（1.0, 1.0）开始遍历，遍历的顺序为按预测值
    有小到大开始，预测值小的，但是被归为1的，说明真阳率下降，向y轴负方向移动，
    预测值小的，被归为0（或-1）的，说明假阴率下降，向x轴负方向移动
    """
    import matplotlib.pyplot as plt
    cur = (1.0, 1.0)
    y_sum = 0.0
    num_pos_clas = (np.array(class_labels) == 1.0).sum()
    y_step = 1 / float(num_pos_clas)
    x_step = 1 / float(len(class_labels) - num_pos_clas)
    sorted_indicies = pred_strengths.argsort()
    fig = plt.figure()
    fig.clf()
    ax = plt.subplot(111)
    for index in sorted_indicies.tolist()[0]:
        if class_labels[index] == 1.0:
            del_x = 0
            del_y = y_step
        else:
            del_x = x_step
            del_y = 0
            y_sum += cur[1]
        ax.plot([cur[0], cur[0] - del_x], [cur[1], cur[1] - del_y], c='b')
        cur = (cur[0] - del_x, cur[1] - del_y)
    ax.plot([0, 1], [0, 1], 'b--')
    plt.xlabel('False Positive Rate')
    plt.ylabel('True Positive Rate')
    plt.title('ROC curve for AdaBoost Horse Colic Detection System')
    ax.axis([0, 1, 0, 1])
    print "the Area Under the Curve is: ", y_sum * x_step
    plt.show()


if __name__ == '__main__':
    data_mat, class_labels = load_simp_data()
    classifier_array, agg_class_est = ada_boost_train_ds(data_mat, class_labels, 9)
    """
    classifier_array是保存弱分类器的数组，格式如下：
    [{'dim': 0, 'ineq': 'lt', 'thresh': 1.3, 'alpha': 0.6931471805599453},
     {'dim': 1, 'ineq': 'lt', 'thresh': 1.0, 'alpha': 0.9729550745276565},
     {'dim': 0, 'ineq': 'lt', 'thresh': 0.90000000000000002, 'alpha': 0.8958797346140273}]
    """
    print('classifier_array:', classifier_array)
    """
    agg_class_est是没有被二值化的预测结果，值的绝对值越大，表示置信度越高
    """
    plotROC(agg_class_est.T, class_labels)
