import itertools
from operator import itemgetter
from django.contrib.auth.decorators import login_required
from django.db import transaction
from django.db.models import Q
from django.shortcuts import render, redirect
from django.core.urlresolvers import reverse

from .models import ConfigKV, Suggestion, Country
from csp.central.models import Team, TrustCircle
from csp.contacts.models import TeamContact, PersonContact, LocalTrustCircle


@login_required
def home_view(request):
    circles = TrustCircle.objects.all()
    teams = Team.objects.all()

    return render(request, 'central/home.html', {
        'circles': circles,
        'teams': teams,
        'ltc_count': LocalTrustCircle.objects.count(),
        'contact_count': TeamContact.objects.count() + PersonContact.objects.count(),
    })


@login_required
def search_view(request):
    q = request.GET.get('q', '')

    if q == '':
        return redirect(reverse("index"))

    circle_query = (Q(name__icontains=q) |
                    Q(short_name__icontains=q))
    team_query = (Q(name__icontains=q) |
                  Q(short_name__icontains=q) |
                  Q(country__icontains=q))
    ltc_query = (Q(name__icontains=q) |
                 Q(short_name__icontains=q) |
                 Q(description__icontains=q))
    localteam_query = (Q(name__icontains=q) |
                       Q(short_name__icontains=q) |
                       Q(description__icontains=q) |
                       Q(country__icontains=q))
    people_query = (Q(full_name__icontains=q) |
                    Q(email__icontains=q))

    circles = TrustCircle.objects.filter(circle_query)
    teams = Team.objects.filter(team_query)
    ltcs = LocalTrustCircle.objects.filter(ltc_query)
    localteams = TeamContact.objects.filter(localteam_query)
    people = PersonContact.objects.filter(people_query)

    return render(request, 'user/search.html', {
        'circles': circles,
        'teams': teams,
        'ltcs': ltcs,
        'localteams': localteams,
        'people': people,
        'contact_count': localteams.count() + people.count(),
        'q': q,
    })


@login_required
def settings_view(request):
    # Load defaults
    messages = []
    expire_days_error = None
    option_expire_days_obj, _ = ConfigKV.objects.get_or_create(
            key='remove_incoming_after_days',
            defaults={'value': '31'})
    option_expire_days_val = int(option_expire_days_obj.value)

    # Load defaults for tc-admin
    suggestions = []
    countries = []

    if request.method == "POST":
        # Clean and apply forms
        if 'expire_days' in request.POST:
            # clean
            try:
                option_expire_days_val = int(request.POST['expire_days'])
            except ValueError:
                option_expire_days_val = 0
            if option_expire_days_val > 0:
                # apply
                option_expire_days_obj.value = option_expire_days_val
                option_expire_days_obj.save()
                messages.append({
                    'messages': 'Configuration updated: Will expire '
                    'received data after {} days'.format(
                        option_expire_days_val),
                    'level': 'success'})
            else:
                expire_days_error = "Please provide a positive number"

        if request.user.has_perm('ctc.web_write'):
            # Clean and apply autosuggest form req
            if 'autosuggest' in request.POST and \
                    'name' in request.POST and \
                    'suggest_' + request.POST['name'] in request.POST:
                varname = request.POST['name']
                entryset = request.POST.getlist('suggest_' + varname)
                # Apply
                with transaction.atomic():
                    Suggestion.objects.filter(name=varname).delete()
                    Suggestion.objects.bulk_create([
                        Suggestion(name=varname, value=entry)
                        for entry in entryset])
            # Clean and apply countries reqs
            if 'remove_country' in request.POST:
                try:
                    Country.objects.filter(
                        id=request.POST['remove_country']).delete()
                    messages.append({
                        'message': 'Country "{}" was deleted'
                            .format(request.POST['remove_country']),
                        'level': 'success'})
                except:
                    messages.append({
                        'message': 'Country "{}" was NOT deleted'
                            .format(request.POST['remove_country']),
                        'level': 'danger'})

            if 'add_country_name' in request.POST and \
                    'add_country_abbr' in request.POST:
                try:
                    new_c = Country(id=request.POST['add_country_abbr'],
                                    name=request.POST['add_country_name'])
                    new_c.save()
                    messages.append({
                        'message': 'Country "{}" was added'
                            .format(request.POST['add_country_name'],
                                    request.POST['add_country_abbr']),
                        'level': 'success'})

                except:
                    messages.append({
                        'message': 'Country "{}" was NOT added'
                            .format(request.POST['add_country_name'],
                                    request.POST['add_country_abbr']),
                        'level': 'danger'})

    # Load suggestions + countries
    if request.user.has_perm('ctc.web_write'):
        suggestions_unsorted = {}
        db_suggestions = sorted(
            Suggestion.objects.all().values(),
            key=itemgetter('name'))
        for name, values in itertools.groupby(
                db_suggestions, key=lambda x: x['name']):
            suggestions_unsorted[name] = sorted(
                values, key=itemgetter('value'))
        for key in sorted(suggestions_unsorted.keys()):
            suggestions.append({"fieldname": key,
                                "suggestions": suggestions_unsorted[key]})

        # Load countries
        countries = Country.objects.all().order_by('id').values()

    return render(request, 'user/configuration.html', {
        'option_expire_days': option_expire_days_val,
        'option_expire_days_error': expire_days_error,
        'suggestions': suggestions,
        'countries': countries,
        'messages': messages
    })
