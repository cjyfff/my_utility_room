from django.views.generic.base import View
from django.http import HttpResponse

from .tasks import test_task


class KeepAliveTestView(View):
    def get(self, request, **kwargs):
        result = test_task.delay()
        resp = result.get()
        return resp
