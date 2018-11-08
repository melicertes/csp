import pytest
import json
import copy

from django.core.management import call_command

from csp.central.models import Team


pytestmark = pytest.mark.django_db


TEAMDATA = {
    "dataParams": {
        "cspId": "CERT-GR",
        "recordId": "central.team:578c0e4e-ebaf-455b-a2a1-faffb14be9e1",
        "applicationId": "trustCircle",
        "dateTime": "2018-01-01T12:00Z",
    },
    "dataType": "trustCircle",
    "sharingParams": {"toShare": "true", "isExternal": "False"},
    "dataObject": json.dumps([{
        "fields": {
            "status": "bfa2069f-ebb1-426d-ab20-d8af724baa82",
            "established": "2017-03-27",
            "additional_countries": [],
            "name": "CERT-Bundxczx",
            "host_organisation": "German xcvcxcvxc",
            "created": "2017-04-25T14:07:24.533Z",
            "country": "41d68647-1c3a-4681-8c7e-e85cd374d2cd",
            "nis_team_types": ["04215624-e922-4e7d-9eb3-d8e005cdff2e", "2282af0e-292d-400b-ba31-047d9d43f229"],
            "short_name": "CERT-BUND",
            "csp_installed": False,
            "nis_sectors": [],
            "csp_id": "",
            "csp_domain": "http://localhost:8081",
            "description": "CERT of the German Government"
        },
        "model": "central.team",
        "pk": "578c0e4e-ebaf-455b-a2a1-faffb14be9e1"
    }]),
}


def test_put_on_nonexistant_team_creates_it(client):
    response = client.put('/api/v1/adapter/integrationData',
                          data=json.dumps(TEAMDATA),
                          content_type='application/json')

    assert response.status_code == 200, response.content


def test_put_on_existing_team_updates_data(client):
    call_command('loaddata', 'initial_common.json')

    response = client.put('/api/v1/adapter/integrationData',
                          data=json.dumps(TEAMDATA),
                          content_type='application/json')

    assert response.status_code == 200, response.content


def test_post_on_existing_team_updates_it(client):
    call_command('loaddata', 'initial_common.json')

    response = client.post('/api/v1/adapter/integrationData',
                           data=json.dumps(TEAMDATA),
                           content_type='application/json')

    assert response.status_code == 200, response.content


def test_post_on_nonexisting_team_creates_record(client):
    call_command('loaddata', 'initial_common.json')

    newid = "578c0e4e-ebaf-455b-a2a1-DEADBEEF0000"
    data = copy.deepcopy(TEAMDATA)
    obj = json.loads(data['dataObject'])
    data['dataParams']['recordId'] = 'central.team:%s' % newid
    obj[0]['pk'] = newid
    obj[0]['fields']['short_name'] = 'NEW TEAM'
    data['dataObject'] = json.dumps(obj)

    response = client.post('/api/v1/adapter/integrationData',
                           data=json.dumps(data),
                           content_type='application/json')

    assert response.status_code == 200, response.content
    assert Team.objects.filter(pk=newid).exists()


def test_delete_on_nonexistant_team_returns_404(client):
    data = {
        "dataParams": {
            "cspId": "CERT-GR",
            "recordId": "central.team:578c0e4e-ebaf-455b-a2a1-faffb14be9e1",
            "applicationId": "trustCircle",
            "dateTime": "2018-01-01T12:00Z",
        },
        "dataType": "trustCircle",
        "sharingParams": {"toShare": "true", "isExternal": "False"},
        "dataObject": None,
    }
    response = client.delete('/api/v1/adapter/integrationData',
                             data=json.dumps(data),
                             content_type='application/json')

    assert response.status_code == 400, response.content


def test_delete_on_existant_team_returns_200(client):
    call_command('loaddata', 'initial_common.json')
    call_command('loaddata', 'initial_trustcircles.json')

    team = Team.objects.create(
        short_name='Test',
        name='test',
        host_organisation='test',
        description='test',
        country='test',
        established='2018-01-01',
        status='Active')

    data = {
        "dataParams": {
            "cspId": "CERT-GR",
            "recordId": "central.team:%s" % str(team.pk),
            "applicationId": "trustCircle",
            "dateTime": "2018-01-01T12:00Z",
        },
        "dataType": "trustCircle",
        "sharingParams": {"toShare": "true", "isExternal": "False"},
        "dataObject": None,
    }
    response = client.delete('/api/v1/adapter/integrationData',
                             data=json.dumps(data),
                             content_type='application/json')

    assert response.status_code == 200, response.content
    assert not Team.objects.filter(pk=team.pk).exists()
