from __future__ import absolute_import

import pytest

from csp.central.models import Team

from csp.integration.models import ChangeLog

pytestmark = pytest.mark.django_db


@pytest.fixture
def team():
    ChangeLog.objects.all().delete()

    return Team.objects.create(
        short_name='Team1',
        name='Team 1',
        host_organisation='Host1',
        description='Desc1',
        country='Germany',
        established='2018-01-01',
        status='Active'
    )


def test_create_team_creates_changelog_entry(team):
    assert ChangeLog.objects.count() == 1
    log = ChangeLog.objects.get()
    assert log.action == 'create'
    assert log.model_name == 'central.team'
    assert log.model_pk == str(team.id)
    assert log.to_share
    assert not log.target_trustcircles


def test_update_team_creates_changelog_entry(team):
    ChangeLog.objects.all().delete()

    team.name = 'Changed name'
    team.save()

    assert ChangeLog.objects.count() == 1
    log = ChangeLog.objects.get()
    assert log.action == 'update'
    assert log.model_name == 'central.team'
    assert log.model_pk == str(team.id)
    assert log.to_share
    assert not log.target_trustcircles


def test_delete_team_creates_changelog_entry(team):
    ChangeLog.objects.all().delete()

    team_uuid = str(team.pk)
    team.delete()

    assert ChangeLog.objects.count() == 1
    log = ChangeLog.objects.get()
    assert log.action == 'delete'
    assert log.model_name == 'central.team'
    assert log.model_pk == team_uuid
    assert log.to_share
    assert not log.target_trustcircles
