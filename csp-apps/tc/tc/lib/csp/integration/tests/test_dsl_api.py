from __future__ import absolute_import

import mock
import pytest

from django.core.management import call_command

from csp.central.models import Team

from csp.integration.models import ChangeLog
from csp.integration.dsl import DSL

pytestmark = pytest.mark.django_db


@pytest.fixture
def team():
    ChangeLog.objects.all().delete()

    return Team.objects.create(
        id="069cafe8-13d9-46d1-a09c-738a956af572",
        created='2018-01-01T00:00Z',
        short_name='Team1',
        name='Team 1',
        host_organisation='Host1',
        description='Desc1',
        country='Germany',
        established='2018-01-01',
        status='Active'
    )


@pytest.fixture
def dsl():
    obj = DSL('http://localhost/path/to/dsl',
              cert_file=None, key_file=None, cachain_file=None,
              csp_id='CSP-ID', app_id='APP-ID')

    obj.response = mock.Mock()
    obj.response.status_code = 200
    obj.api.post = mock.Mock(return_value=obj.response)
    obj.api.put = mock.Mock(return_value=obj.response)
    obj.api.delete = mock.Mock(return_value=obj.response)

    return obj


def test_create_team_change_dispatch(team, dsl):
    assert ChangeLog.objects.count() == 1
    ChangeLog.objects.update(created='2018-01-01T00:00Z')

    dsl.dispatch_change(ChangeLog.objects.get())

    args, kwargs = dsl.api.post.call_args

    assert dsl.api.post.call_count == 1
    assert args[0] == 'http://localhost/path/to/dsl'

    expected_payload = {
        "dataParams": {
            "originCspId": "CSP-ID",
            "recordId": "central.team:069cafe8-13d9-46d1-a09c-738a956af572",
            "originApplicationId": "APP-ID",
            "dateTime": "2018-01-01T00:00:00+0000",
            "originRecordId": "central.team:069cafe8-13d9-46d1-a09c-738a956af572",
            "cspId": "CSP-ID",
            "applicationId": "APP-ID"
        },
        "dataType": "trustCircle",
        "sharingParams": {
            "toShare": True,
            "isExternal": False
        }
    }

    payload = kwargs['json']
    payload.pop('dataObject')

    assert payload == expected_payload


def test_dispatch_to_ctc_all(dsl, team):
    ChangeLog.objects.all().delete()
    ChangeLog.objects.log('create', team, 'CTC::CSP_ALL')
    ChangeLog.objects.update(created='2018-01-01T00:00Z')

    dsl.dispatch_change(ChangeLog.objects.get())

    args, kwargs = dsl.api.post.call_args

    assert dsl.api.post.call_count == 1
    assert args[0] == 'http://localhost/path/to/dsl'

    expected_payload = {
        "dataParams": {
            "originCspId": "CSP-ID",
            "recordId": "central.team:069cafe8-13d9-46d1-a09c-738a956af572",
            "originApplicationId": "APP-ID",
            "dateTime": "2018-01-01T00:00:00+0000",
            "originRecordId": "central.team:069cafe8-13d9-46d1-a09c-738a956af572",
            "cspId": "CSP-ID",
            "applicationId": "APP-ID"
        },
        "dataType": "trustCircle",
        "sharingParams": {
            "toShare": True,
            "isExternal": False,
            "trustCircleId": ["CTC::CSP_ALL"],
        }
    }

    payload = kwargs['json']
    payload.pop('dataObject')

    assert payload == expected_payload
