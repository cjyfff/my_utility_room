# coding=utf-8
"""
测试with的用法，
"""


class EarlyExit(Exception):
    pass


class AlwaysSuccessContext(object):
    """__enter__中的异常不会被__exit__捕捉"""
    def __enter__(self):
        # raise EarlyExit
        return self

    def __exit__(self, exc_type, exc_value, traceback):
        if isinstance(exc_value, EarlyExit):
            print("stat 1", exc_value)
            return True
        if exc_type:
            print("stat 2", exc_type)
            raise exc_value
        return True


with AlwaysSuccessContext() as a:
    print("hello")
    # raise Exception("I am normal exception.")
    raise EarlyExit("I am EarlyExit exception.")
