from django.views.generic.base import View
from django.http import HttpResponse


class KeepAliveTestView(View):
    def get(self, request, **kwargs):
        return HttpResponse('fuck')
