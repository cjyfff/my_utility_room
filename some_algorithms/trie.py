

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


def main():
    tree = TrieTree()
    tree.add('abc')
    tree.add('bcd')
    tree.add('ab')
    print(tree.tree)
    print(tree.search('abc'))
    print(tree.search('ab'))


if __name__ == '__main__':
    main()
