from datetime import timedelta
from django.contrib.auth.decorators import login_required
from django.db import transaction
from django.utils import timezone
from django.shortcuts import render, redirect, get_object_or_404
from django.views.decorators.http import require_POST

from csp.integration.models import ChangeLog

from csp.common.models import ConfigKV

from .models import (
    TeamContact,
    PersonContact,
    LocalTrustCircle,
    IncomingTeamContact)

from .forms import (
    TeamContactFullForm,
    TeamContactTITUSForm,
    TeamMemberFormSet,
    TeamPhoneNumberFormSet,
    TeamMembershipFormSet,
    TeamCertificateFormSet,
    TeamOutbandAlertingAccessFormSet,
    TeamOutbandAlertingContactFormSet,
    PersonContactForm,
    PersonPhoneNumberFormSet,
    PersonCertificateFormSet,
    PersonMembershipFormSet,
    LocalTrustCircleForm,
    TeamSharingTargetsForm
)


@login_required
def teamcontact_list(request):
    teams = TeamContact.objects.all()
    return render(request, 'contacts/team/list.html', {
        'teams': teams,
    })


@login_required
def teamcontact_view(request, id):
    team = get_object_or_404(TeamContact, id=id)
    own_team = False  # TODO: add checking for own team!

    if own_team:
        show_blocks = TeamContactFullForm.show_blocks
    else:
        show_blocks = TeamContactTITUSForm.show_blocks

    return render(request, 'contacts/team/view.html', {
        'team': team,
        'show_blocks': show_blocks,
    })


@login_required
@require_POST
def teamcontact_delete(request):
    team = get_object_or_404(TeamContact, id=request.POST['id'])
    team.delete()
    return redirect('teamcontact_list')


@login_required
def teamcontact_edit(request, id=None, incoming_id=None):
    if id:
        team = get_object_or_404(TeamContact, id=id)
    else:
        team = TeamContact(id=None)

    own_team = False  # TODO: FIXME

    if own_team:
        form = TeamContactFullForm(request.POST or None, instance=team)
    else:
        form = TeamContactTITUSForm(request.POST or None, instance=team)

    formsets = {}
    for name, FormSetClass in [
        ('team_members', TeamMemberFormSet),
        ('phone_numbers', TeamPhoneNumberFormSet),
        ('certificates', TeamCertificateFormSet),
        ('memberships', TeamMembershipFormSet),
        ('outband_alerting_contacts', TeamOutbandAlertingContactFormSet),
        ('outband_alerting_accesses', TeamOutbandAlertingAccessFormSet),
    ]:
        if name in form.show_blocks:
            formsets[name] = FormSetClass(request.POST or None, instance=team)

    if request.method == 'POST':
        form.is_valid() and all([f.is_valid() for f in formsets.values()])
        form.clean()
        for formset in formsets.values():
            formset.clean()
        if form.is_valid() and all([f.is_valid() for f in formsets.values()]):
            with transaction.atomic():
                team = form.save()
                for formset in formsets.values():
                    formset.save()
            return redirect('teamcontact_view', team.id)

    # Diff
    if incoming_id:
        incoming = get_object_or_404(IncomingTeamContact, id=incoming_id)
    else:
        incoming = None

    return render(request, 'contacts/team/edit.html', {
        'team': team,
        'incoming': incoming,
        'form': form,
        'formsets': formsets,
        'show_blocks': form.show_blocks,
    })


@login_required
def teamcontact_share(request, id):
    team = get_object_or_404(TeamContact, id=id)
    form = TeamSharingTargetsForm(request.POST or None)
    if request.method == 'POST':
        if form.is_valid():
            trustcircle_ids = [ctc.name for ctc in form.cleaned_data['ctcs']]
            for ltc in form.cleaned_data['ltcs']:
                trustcircle_ids += [
                    tc['name']
                    for tc in ltc.trustcircles.all().values('name')]

            trustcircle_ids = set(trustcircle_ids)
            ChangeLog.objects.log('create', team,
                                  target_trustcircles=trustcircle_ids)
            return redirect('teamcontact_list')

    return render(request, 'contacts/team/share.html', {
        'team': team,
        'form': form,
        })


@login_required
def personcontact_list(request):
    persons = PersonContact.objects.all()
    return render(request, 'contacts/person/list.html', {
        'persons': persons,
    })


@login_required
def personcontact_view(request, id):
    person = get_object_or_404(PersonContact, id=id)
    return render(request, 'contacts/person/view.html', {
        'person': person,
    })


@login_required
@require_POST
def personcontact_delete(request):
    person = get_object_or_404(PersonContact, id=request.POST['id'])
    person.delete()
    return redirect('personcontact_list')


@login_required
def personcontact_edit(request, id=None):
    if id:
        person = get_object_or_404(PersonContact, id=id)
    else:
        person = PersonContact(id=None)

    form = PersonContactForm(request.POST or None, instance=person)

    formsets = {}
    for name, FormSetClass in [
        ('phone_numbers', PersonPhoneNumberFormSet),
        ('certificates', PersonCertificateFormSet),
        ('memberships', PersonMembershipFormSet),
    ]:
        formsets[name] = FormSetClass(request.POST or None, instance=person)

    if request.method == 'POST':
        if form.is_valid() and all([f.is_valid() for f in formsets.values()]):
            with transaction.atomic():
                person = form.save()
                for formset in formsets.values():
                    formset.save()
            return redirect('personcontact_view', person.id)

    return render(request, 'contacts/person/edit.html', {
        'person': person,
        'form': form,
        'formsets': formsets,
    })


@login_required
def ltc_list(request):
    ltcs = LocalTrustCircle.objects.all()
    return render(request, 'contacts/ltc/list.html', {
        'ltcs': ltcs,
    })


@login_required
def ltc_view(request, id):
    ltc = get_object_or_404(LocalTrustCircle, id=id)
    return render(request, 'contacts/ltc/view.html', {
        'ltc': ltc,
    })


@login_required
@require_POST
def ltc_delete(request):
    ltc = get_object_or_404(LocalTrustCircle, id=request.POST['id'])
    ltc.delete()
    return redirect('ltc_list')


@login_required
def ltc_edit(request, id=None):
    if id:
        ltc = get_object_or_404(LocalTrustCircle, id=id)
    else:
        ltc = LocalTrustCircle(id=None)

    form = LocalTrustCircleForm(request.POST or None, instance=ltc)
    if form.is_valid():
        ltc = form.save()
        return redirect('ltc_view', ltc.id)

    return render(request, 'contacts/ltc/edit.html', {
        'ltc': ltc,
        'form': form,
    })


@login_required
def incoming_list(request):
    # Cleanup stale IncomingTeamContacts
    remove_incoming_after_days = 31
    try:
        remove_incoming_after_days = int(ConfigKV.objects.get(
            key='remove_incoming_after_days').value)
    except:
        pass
    finally:
        update_older = timezone.now() - timedelta(
            days=remove_incoming_after_days)
        if remove_incoming_after_days > 0:
            IncomingTeamContact.objects.filter(
                created__lte=update_older).delete()

    incoming = IncomingTeamContact.objects.all().order_by('-created')

    return render(request, 'contacts/incoming/list.html', {
        'incoming': incoming,
    })


@login_required
@require_POST
def incoming_delete(request):
    incoming = get_object_or_404(IncomingTeamContact, id=request.POST['id'])
    incoming.delete()
    return redirect('incomingcontact_list')


@login_required
def incoming_view(request, id):
    incoming = get_object_or_404(IncomingTeamContact, id=id)
    return render(request, 'contacts/incoming/view.html', {
        'incoming': incoming,
        'incoming_team': incoming.deserialized,
        'existing_team': incoming.get_existing(),
        'show_blocks': TeamContactTITUSForm.show_blocks,
    })
