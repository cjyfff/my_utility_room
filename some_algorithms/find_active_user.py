"""
《编程之美》2.3节的扩展问题：加入有3个发帖很多的ID，他们的发帖数都超过
帖子总数N的1/4,要求从ID列表中找出这3个ID
"""

import copy

L = [1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4]


def find_active_user(id_list):
    assert(type(id_list) == list)
    u_id_list = copy.deepcopy(id_list)
    while 1:
        tmp_list = []
        idx_list = []
        for idx, u_id in enumerate(u_id_list):
            if u_id not in tmp_list:
                tmp_list.append(u_id)
                idx_list.append(idx)
            if len(tmp_list) > 4:
                break
        if len(idx_list) < 4:
            break
        for idx in sorted(idx_list, reverse=True):
            del u_id_list[idx]
    print(set(u_id_list))


if __name__ == '__main__':
    find_active_user(L)
