"""
使用trie tree实现一个简单的分词模块
"""


class TrieTree(object):
    def __init__(self):
        self.tree = {}

    def add(self, word):
        tree = self.tree

        for char in word:
            if char in tree:
                tree = tree[char]
            else:
                tree[char] = {}
                tree = tree[char]
        tree['exist'] = True

    def search(self, word):
        tree = self.tree

        for char in word:
            if char in tree:
                tree = tree[char]
            else:
                return False

        if 'exist' in tree and tree['exist'] is True:
            return True
        return False


def load_dict(dict_path, stop_word_dict_path,
              punctuation_dict_path, trie_tree):
    print('Loading dict...')
    with open(dict_path, 'r') as fp:
        line = fp.readline()
        while line:
            trie_tree.add(line.strip())
            line = fp.readline()

    stop_word_lst = []

    with open(stop_word_dict_path, 'r') as fp:
        line = fp.readline()
        while line:
            stop_word_lst.append(line.strip())
            line = fp.readline()

    with open(punctuation_dict_path, 'r') as fp:
        line = fp.readline()
        while line:
            stop_word_lst.append(line.strip())
            line = fp.readline()

    print('Loading completed.')
    return trie_tree, stop_word_lst


def segment_word(sentence, stop_word_lst, trie_tree):
    """采用最大长度的分词原则"""
    term = ''
    signal = False
    lst = []
    count = 0
    for word in sentence:
        if word in stop_word_lst:
            lst.append(word)
            count += 1
            term = ''
            signal = False
            continue
        term += word
        if trie_tree.search(term) and not signal:
            lst.append(term)
            signal = True
            continue
        if trie_tree.search(term) and signal:
            lst[count] = term
            continue
        if not trie_tree.search(term) and signal:
            term = word
            count += 1
            signal = False
            continue
        if not trie_tree.search(term) and not signal:
            pass

    for word in lst:
        print(word)


def main(sentence):
    tree = TrieTree()
    _tree, stop_word_lst = load_dict('./dict/sougou.dic',
                                     './dict/stopword.dic',
                                     './dict/punctuation.dic',
                                     tree)
    segment_word(sentence, stop_word_lst, _tree)


if __name__ == '__main__':
    sen = '虽然我不是医生，但是我确实感到医生真是太高危职业了'
    main(sen)
