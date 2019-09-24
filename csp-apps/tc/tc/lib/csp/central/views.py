import uuid

from django.contrib.auth.decorators import login_required, permission_required
from django.core.urlresolvers import reverse
from django.shortcuts import get_object_or_404, redirect, render
from django.views.decorators.http import require_POST

from csp.central.models import Team, TrustCircle
from csp.contacts.forms import TeamContactTITUSForm
from csp.contacts.api import TeamContactSerializer

from .forms import TeamForm, CircleForm

# TEAMS ####################

@login_required
def team_list(request):
    teams = Team.objects.all()
    return render(request, 'central/team/list.html', {
        'teams': teams
    })


@login_required
def team_view(request, id):
    team = get_object_or_404(Team, id=id)
    teamcontact = None
    if team.team_selfinfo:
        serializer = TeamContactSerializer(data=team.team_selfinfo)
        serializer.validators = []
        serializer.is_valid(raise_exception=True)
        teamcontact = serializer.as_object()

    show_blocks = TeamContactTITUSForm.show_blocks[:]
    show_blocks.remove('csp_team')
    show_blocks.remove('team')

    protect_delete = team.short_name in ['central-csp', 'central']

    return render(request, 'central/team/view.html', {
        'team': team,
        'teamcontact': teamcontact,
        'circles': team.circles.all(),
        'changes': team.history.all().count(),
        'show_blocks': show_blocks,
        'protect_delete': protect_delete,
    })


@require_POST
@permission_required("ctc.web_write")
def team_delete(request):
    team = get_object_or_404(Team, id=request.POST['id'])
    team.delete()
    return redirect(reverse("team_list"))


@permission_required("ctc.web_write")
def team_edit(request, id=None):
    if id:
        team = get_object_or_404(Team, id=id)
        selected_circle_ids = list(team.circles.all().values_list('id', flat=True))
    else:
        team = Team(id=None)
        selected_circle_ids = []

    form = TeamForm(request.POST or None, instance=team)
    if form.is_valid():
        team = form.save()

        # Handle CircleIDs in POST Response (Bootstrap Table)
        circle_ids = set(_parse_uuids(request.POST["selectedCircles"]))
        circles = TrustCircle.objects.filter(pk__in=circle_ids)
        team.circles = circles

        # trigger update signal for all changed circles
        changed_circle_ids = circle_ids.symmetric_difference(set(selected_circle_ids))
        for circle in TrustCircle.objects.filter(pk__in=changed_circle_ids):
            circle.save()

        return redirect(reverse("team_view", args=[team.id]))

    return render(request, 'central/team/edit.html', {
        'form': form,
        'team': team,
        'circles': TrustCircle.objects.all(),
        'selected_circles_ids': selected_circle_ids
    })


@login_required
def team_history_view(request, id):
    instance = get_object_or_404(Team, id=id).history.all()

    # Santinize Columns
    columns = vars(Team())
    columns = {k: v for k, v in columns.items() if not k.startswith('_')}
    columns['history_date'] = ""
    columns['history_user'] = ""

    context = {
        'history': instance,
        'cols': columns,
    }
    return render(request, 'central/team/history.html', context)


# CIRCLES ####################

@login_required
def circle_list(request):
    circles = TrustCircle.objects.all()
    return render(request, 'central/ctc/list.html', {
        'circles': circles
    })


@login_required
def circle_view(request, id):
    circle = get_object_or_404(TrustCircle, id=id)
    protect_delete = circle.short_name in ['CTC::CSP_ALL', 'CTC::CSP_SHARING']

    return render(request, 'central/ctc/view.html', {
        'teams': circle.teams.all(),
        'circle': circle,
        'changes': circle.history.all().count(),
        'protect_delete': protect_delete,
    })


@require_POST
@permission_required("ctc.web_write")
def circle_delete(request):
    circle = get_object_or_404(TrustCircle, id=request.POST['id'])
    circle.delete()
    return redirect(reverse("circle_list"))


@permission_required("ctc.web_write")
def circle_edit(request, id=None):
    if id:
        circle = get_object_or_404(TrustCircle, id=id)
        selected_team_ids = circle.teams.all().values_list('id', flat=True)
    else:
        circle = TrustCircle(id=None)
        selected_team_ids = []

    form = CircleForm(request.POST or None, instance=circle)
    if form.is_valid():
        circle = form.save()

        # Handle CircleIDs in POST Response (Bootstrap Table)
        selected_team_ids = _parse_uuids(request.POST["selectedTeams"])
        circle.teams = Team.objects.filter(pk__in=selected_team_ids)

        return redirect(reverse("circle_view", args=[circle.id]))

    return render(request, 'central/ctc/edit.html', {
        'circle': circle,
        'form': form,
        'teams': Team.objects.all(),
        'selected_team_ids': selected_team_ids
    })


@permission_required("ctc.web_write")
def circle_history_view(request, id):
    instance = get_object_or_404(TrustCircle, id=id).history.all()

    # Santinize Columns
    columns = vars(TrustCircle())
    columns = {k: v for k, v in columns.items() if not k.startswith('_')}
    columns['history_date'] = ""  # Show column
    columns['history_user'] = ""  # Show column

    context = {
        'history': instance,
        'cols': columns,
    }
    return render(request, 'central/ctc/history.html', context)


# Helpers ####################

def _parse_uuids(val):
    return [uuid.UUID(el) for el in val.split(';') if el]
