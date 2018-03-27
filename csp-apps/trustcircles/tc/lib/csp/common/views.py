from django.contrib.auth.decorators import login_required
from django.db.models import Q
from django.shortcuts import render

from csp.central.models import Team, TrustCircle
from csp.contacts.models import TeamContact, PersonContact, LocalTrustCircle


@login_required
def search_view(request):
    q = request.GET.get('q', '')
    circlename = request.GET.get('tc', '')
    teamname = request.GET.get('t', '')
    country = request.GET.get('c', '')
    status = request.GET.get('s', '')
    searchmode = False

    if q:
        circle_query = (Q(name__contains=q) | Q(short_name__contains=q))
        team_query = (Q(name__contains=q) | Q(short_name__contains=q) | Q(country__contains=q))
    elif circlename:
        circle_query = (Q(name__contains=circlename) | Q(short_name__contains=circlename))
        team_query = ""
    elif teamname:
        circle_query = ""
        team_query = (Q(name__contains=teamname) | Q(short_name__contains=teamname))
    elif country:
        circle_query = ""
        team_query = (Q(country__contains=country))
    elif status:
        circle_query = ""
        team_query = (Q(status__contains=status))
    else:
        circle_query = ""
        team_query = ""

    if (circle_query == "" and team_query == ""):
        circles = TrustCircle.objects.all()
        teams = Team.objects.all()
    else:
        searchmode = True
        if not circle_query == "":
            circles = TrustCircle.objects.filter(circle_query)
        else:
            circles = TrustCircle.objects.none

        if not team_query == "":
            teams = Team.objects.filter(team_query)
        else:
            teams = Team.objects.none

    return render(request, 'central/search.html', {
        'circles': circles,
        'teams': teams,
        'ltc_count': LocalTrustCircle.objects.count(),
        'contact_count': TeamContact.objects.count() + PersonContact.objects.count(),
        'q': request.GET.get('q', ''),
        'searchmode': searchmode,
    })
