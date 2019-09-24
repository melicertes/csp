import logging
from datetime import timedelta
from django.contrib.auth.decorators import login_required
from django.db import transaction
from django.utils import timezone
from django.utils.dateparse import parse_datetime
from django.shortcuts import render, redirect, get_object_or_404
from django.views.decorators.http import require_POST
from django.db.models import Q

from csp.contacts.api import TeamContactSerializer
from csp.contacts.tidata import new_csp_dicts_from_diff_of_two_ti_jsons
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

from csp.central.models import Team

AUDITLOG = logging.getLogger('ctc')

@login_required
def teamcontact_list(request):
    teams = TeamContact.objects.all()
    return render(request, 'contacts/team/list.html', {
        'teams': teams,
    })


@login_required
def teamcontact_view(request, id):
    team = get_object_or_404(TeamContact, id=id)
    try:
        associated_central_team = Team.objects.get(
            short_name=team.short_name, country=team.country)
    except Team.DoesNotExist:
        associated_central_team = None

    own_team = False  # TODO: add checking for own team!

    if own_team:
        show_blocks = TeamContactFullForm.show_blocks[:]  # Copy
    else:
        show_blocks = TeamContactTITUSForm.show_blocks[:]  # Copy

    # 29.08.2019 not showing csp_team block, ignoring values
    show_blocks.remove('csp_team')
    show_blocks.append('associated_central_team')

    return render(request, 'contacts/team/view.html', {
        'team': team,
        'show_blocks': show_blocks,
        'associated_central_team': associated_central_team
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

    # 29.08.2019 not showing csp_team block, ignoring values
    show_blocks = form.show_blocks[:]
    show_blocks.remove('csp_team')

    formsets = {}
    for name, FormSetClass in [
        ('team_members', TeamMemberFormSet),
        ('phone_numbers', TeamPhoneNumberFormSet),
        ('certificates', TeamCertificateFormSet),
        ('memberships', TeamMembershipFormSet),
        ('outband_alerting_contacts', TeamOutbandAlertingContactFormSet),
        ('outband_alerting_accesses', TeamOutbandAlertingAccessFormSet),
    ]:
        if name in show_blocks:
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
        if incoming.seen is None:
            incoming.seen = timezone.now()
            incoming.save()
    else:
        incoming = None

    return render(request, 'contacts/team/edit.html', {
        'team': team,
        'incoming': incoming,
        'form': form,
        'formsets': formsets,
        'show_blocks': show_blocks,
    })


@login_required
def teamcontact_share(request, id):
    team = get_object_or_404(TeamContact, id=id)
    form = TeamSharingTargetsForm(request.POST or None)
    if request.method == 'POST':
        if form.is_valid():
            # CTCs from selected CTCs and LTCs
            trustcircle_ids = [ctc.short_name
                               for ctc in form.cleaned_data['ctcs']]
            team_ids = []
            for ltc in form.cleaned_data['ltcs']:
                trustcircle_ids += [
                    tc['short_name']
                    for tc in ltc.trustcircles.all().values('short_name')]
                # Teams from selected LTCs
                team_ids += [str(t['id'])
                             for t
                             in ltc.teams.all().values('id')]

            trustcircle_ids = set(trustcircle_ids)
            team_ids = set(team_ids)

            if len(trustcircle_ids) + len(team_ids) > 0:
                AUDITLOG.info('Marking ContactTeam "{}" to be shared with trustcircles {} and teams {}'.format(team, trustcircle_ids, team_ids))

                ChangeLog.objects.log('create', team,
                                      target_trustcircles=trustcircle_ids,
                                      target_teams=team_ids)

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

    # Generate Q (search) object for search query
    q = Q()
    searchterm = request.GET.get('qfilter', '')
    if bool(searchterm):
        for word in searchterm.split():
            q |= Q(data_object__icontains=word)

    # Generate list with search query Q (=all if empty)
    incoming = IncomingTeamContact.objects.filter(q).order_by('-created')

    return render(request, 'contacts/incoming/list.html', {
        'incoming': incoming,
        'qfilter': searchterm,
    })


@login_required
@require_POST
def incoming_delete(request):
    incoming = get_object_or_404(IncomingTeamContact, id=request.POST['id'])
    incoming.delete()
    return redirect('incomingcontact_list')


@login_required
@require_POST
def incoming_delete_all(request):
    IncomingTeamContact.objects.all().delete()
    return redirect('incomingcontact_list')


@login_required
def incoming_view(request, id):
    incoming = get_object_or_404(IncomingTeamContact, id=id)

    # Update 'seen' flag if entry yet unseen
    if incoming.seen is None:
        incoming.seen = timezone.now()
        incoming.save()

    # 29.08.2019 not showing csp_team block, ignoring values
    show_blocks = TeamContactTITUSForm.show_blocks[:]  # Copy
    show_blocks.remove('csp_team')

    return render(request, 'contacts/incoming/view.html', {
        'incoming': incoming,
        'incoming_team': incoming.deserialized,
        'existing_team': incoming.get_existing(),
        'show_blocks': show_blocks,
    })


@login_required
def importti_view(request):
    import_ok = False
    errormsg = ''
    import_stats = {
        'success': 0,
        'fail': 0,
        'failed_teams': [],
        'failed_teamnames': ''
    }
    if (request.method == 'POST' and
            'content' in request.POST and
            'source' in request.POST and
            'delete' not in request.POST):

        proceed = True
        source = str(request.POST['source'])
        content = request.POST['content']
        if not (source in ['auth', 'public'] or
                source.startswith('http://') or
                source.startswith('https://') or
                source.startswith('data:')):
            proceed = False
            errormsg = 'Invalid data type submitted'

        if source.startswith('data:'):
            source = 'uploaded'

        if proceed:
            try:
                configkv, _ = ConfigKV.objects.get_or_create(
                    key='ti_json_data', defaults={'value': '[]'})
                oldjson = configkv.value
                with transaction.atomic():
                    for teamdict in new_csp_dicts_from_diff_of_two_ti_jsons(
                            oldjson, content):
                        try:
                            serializer = TeamContactSerializer(data=teamdict)
                            # no unique validator on country, short_name
                            serializer.validators = []
                            serializer.is_valid(raise_exception=True)

                            itc = IncomingTeamContact(
                                csp_id='Trusted Introducer Importer',
                                app_id='Trusted Introducer Importer',
                                target_circle_id=[],
                                target_team_id=[],
                                data_object=teamdict)
                            itc.save()

                            import_stats['success'] += 1
                        except Exception as e:
                            import_stats['fail'] += 1
                            import_stats['failed_teams'].append(teamdict)
                            pass

                    import_stats['failed_teamnames'] = ', '.join(
                        ["({}, {})".format(
                            t.get('short_name', 'GARBLED_SHORT_NAME'),
                            t.get('country', 'GARBLED_COUNTRY'))
                         for t in import_stats['failed_teams']])
                    # Save new JSON on success
                    configkv, _ = ConfigKV.objects.get_or_create(
                        key='ti_json_type', defaults={'value': ''})
                    configkv.value = source
                    configkv.save()

                    configkv, _ = ConfigKV.objects.get_or_create(
                        key='ti_json_timestamp', defaults={'value': ''})
                    configkv.value = str(timezone.now())
                    configkv.save()

                    configkv, _ = ConfigKV.objects.get_or_create(
                        key='ti_json_data', defaults={'value': '[]'})
                    configkv.value = content
                    configkv.save()
                    import_ok = True
            except Exception as e:
                errormsg = 'Error while proccessing the import: {}'.format(e)

    elif (request.method == 'POST' and
            'delete' in request.POST):
        # Delete cache
        configkv, _ = ConfigKV.objects.get_or_create(
            key='ti_json_type', defaults={'value': ''})
        configkv.value = ''
        configkv.save()

        configkv, _ = ConfigKV.objects.get_or_create(
            key='ti_json_timestamp', defaults={'value': ''})
        configkv.value = ''
        configkv.save()

        configkv, _ = ConfigKV.objects.get_or_create(
            key='ti_json_data', defaults={'value': '[]'})
        configkv.value = '[]'
        configkv.save()

    configkv, _ = ConfigKV.objects.get_or_create(
        key='ti_json_type', defaults={'value': ''})
    ti_json_type = configkv.value

    configkv, _ = ConfigKV.objects.get_or_create(
        key='ti_json_timestamp', defaults={'value': ''})
    ti_json_timestamp = parse_datetime(configkv.value)

    return render(
        request, 'contacts/importti/view.html',
        {'import_ok': import_ok,
         'import_stats': import_stats,
         'errormsg': errormsg,
         'ti_json_timestamp': ti_json_timestamp,
         'ti_json_type': ti_json_type})
