from django.conf.urls import include, url
from .views import KeepAliveTestView

urlpatterns = [
    url(r'^test$', KeepAliveTestView.as_view(), name='keep_alive_test'),
    # url(r'^admin/', include(admin.site.urls)),
]
