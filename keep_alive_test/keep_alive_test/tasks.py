"""定义celery worker"""
import celery
import time

from django.http import HttpResponse


@celery.task
def test_task():
    def select_db_or_cache():
        """查询数据库操作，假如满足指定条件，返回True"""
        # return True
        return False

    i = 0
    while i < 30:
        # 轮询数据库，监控数据变化，以达到监控事件变化的目的
        if select_db_or_cache():
            break
        time.sleep(1)
        i += 1
    return HttpResponse('fuck')
