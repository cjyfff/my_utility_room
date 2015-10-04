"""定义celery worker"""
import celery
import time

from django.http import HttpResponse


@celery.task
def test_task():
    time.sleep(10)
    return HttpResponse('fuck')
