from django.conf import settings
from django.contrib.auth import authenticate, login, logout
from django.shortcuts import redirect, render
from django.core.urlresolvers import reverse


def login_view(request):
    if request.method == "POST":
        in_username = request.POST["username"]
        in_password = request.POST["password"]
        user = authenticate(username=in_username, password=in_password)
        if user is not None:
            login(request, user)
            return redirect(reverse("index"))
        else:
            return render(request, 'user/login.html', {'error': 'yes'})
    return render(request, 'user/login.html', {'error': 'no'})


def logout_view(request):
    logout(request)
    return redirect(settings.LOGIN_URL)
