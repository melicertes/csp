# -*- coding: utf-8 -*-
from django.utils.dateparse import parse_date
import json

"""
Helper for TI data
"""


def ti_dict_to_csp_dict(ti):
    """
    Takes a dict of JSON data in the TI format and returns JSON in CSP format.
    Works similar to TeamContactSerializerCSPTeam, with same fields
    (no csp_team fields):
        fields = (
            'additional_countries', <- ...
                    constituency['countries'][['name']] without country['name']
x            'automated_email',  <- ?
x            'automated_email_format',  <- ?
            'business_hours',  <- business_hours
            'business_hours_timezone',  <- business_hours_timezone
            'certificates',  COMPLEX
            'constituency_asns',  <- constituency['asns']
x            'constituency_description',  <- ??
            'constituency_domains',  <- constituency['domains']
            'constituency_ipranges',  <- constituency['ipranges']
            'constituency_types',  <- constituency['types']
            'contact_postal_address',  <- postal_address
x            'contact_postal_country',  ??
            'country',  <- country['name']
x            'description',  ???
            'established',  <- established
        'host_organisation',  <- host_organisation
            'id',  XXXXXXX
!            'main_email',  ???? emails['address'] first
x            'member_locations',  ????
            'memberships',  COMPLEX
            'name',  <- official_name
            'phone_numbers',  COMPLEX
            'public_email',  emails['address'] first
!            'public_ftp',  <- public_info['ftp']
!            'public_mailinglist', <- public_info['mailinglist']
!            'public_usenet', <- public_info['usenet']
!            'public_www', <- public_info['www']
x            'scope_asns',  ??
x            'scope_email', ??
x            'scope_ipranges', ???
            'short_name',  <- name
x            'team_members',  COMPLEX
        )
    of those team_members, phone_numbers, certificates and memberships
    are lists of dicts.

    team_members:
        email <- 'emails', 0, 'address'
×        team_role <- ??
        host_rep = 'host_rep' in 'roles'
        constituency_rep = 'const_rep' in 'roles'

    phone_numbers:
        tag <- 'main': 'voice' in 'usage'
               'emergency': ['emergency', 'voice'] in 'usage'
               'fax': 'fax' in 'usage'
        number <- 'number'
        timezone <- 'timezone'
        number_details <- 'comments'
        visibility = 'private' ???

    memberships:
        organisation <- 'organisation'
        membership_state <- 'state'
        since <- 'since'

    certificates:
        tag = 'main' ???
        method = 'pgp'
        keyid <- 'fingerprint'
        visibility = 'private'???
        data <- 'data'

    """

    # Mandataory
    csp = {
        'csp_id': '',
        'scope_asns': [],
        'scope_email': [],
        'scope_ipranges': [],

    }

    # CSP-key name: (key name[s] in TI, default value)
    team_transform = {
        'business_hours': (get_from_dict, ['business_hours'], ''),
        'business_hours_timezone': (get_from_dict,
                                    ['business_hours_timezone'], ''),
        'contact_postal_address': (get_from_dict, ['postal_address'], ''),
        'established': (get_from_dict, ['established'], '', True),
        'host_organisation': (get_from_dict, ['host_organisation'], ''),
        'name': (get_from_dict, ['official_name'], ''),
        'short_name': (get_from_dict, ['short_name'], ''),
        'constituency_asns': (get_from_dict, ['constituency', 'asns'], []),
        'constituency_domains': (get_from_dict,
                                 ['constituency', 'domains'], []),
        'constituency_ipranges': (get_from_dict,
                                  ['constituency', 'ipranges'], []),
        'constituency_types': (get_from_dict, ['constituency', 'types'], []),
        'country': (get_from_dict, ['country', 'name'], ''),
        'public_email': (get_from_dict, ['emails', 0, 'address'], ''),
        'public_ftp': (get_from_dict, ['public_info', 'ftp'], ''),
        'public_mailinglist': (get_from_dict,
                               ['public_info', 'mailinglist'], ''),
        'public_usenet': (get_from_dict, ['public_info', 'usenet'], ''),
        'public_www': (get_from_dict, ['public_info', 'www'], ''),
        'main_email': (get_from_dict, ['emails', 0, 'address'], ''),
    }

    team_members_transform = {
        'email': (get_from_dict, ['emails', 0, 'address'], ''),
        'host_rep': (has_members_in_dict, 'roles', 'host_rep'),
        'constituency_rep': (has_members_in_dict, 'roles', 'const_rep'),
        'full_name': (get_from_dict, 'name', ''),
        'memberships': (const, []),
        # Just copies dictionary, needs postprocessing later
        'certificates': (get_from_dict, 'pgp_keys', []),
        # Just copies dictionary, needs postprocessing later
        'phone_numbers': (get_from_dict, 'phones', []),
    }

    phone_numbers_transform = {
        'tag': (get_phone_tag, 'usage'),
        'number': (get_from_dict, 'number', ''),
        'timezone': (get_from_dict, 'timezone', ''),
        'number_details': (get_from_dict, 'comments', ''),
        'visibility': (const, 'private'),
    }

    memberships_transform = {
        'organisation': (get_from_dict, 'organisation', ''),
        'membership_state': (get_from_dict, 'state', ''),
        'since': (get_from_dict, 'since', ''),
    }

    certificates_transform = {
        'tag': (const, 'main'),
        'method': (const, 'pgp'),
        'keyid': (get_from_dict, 'fingerprint', ''),
        'visibility': (const, 'private'),
        'data': (get_from_dict, 'data'),
    }

    csp = apply_transform(team_transform, ti, csp)

    # additional countries are countries of the constituency minus country
    csp['additional_countries'] = remove_from_list(
        list_from_dictlist(
            get_from_dict(ti, ['constituency', 'countries'], default=[]),
            'name',
            default=[]),
        get_from_dict(csp, 'country', ''))

    csp['phone_numbers'] = [
        apply_transform(phone_numbers_transform, timember, {})
        for timember in ti_flatten_phone_numbers(get_from_dict(ti, 'phones'))]

    teammembers = [apply_transform(team_members_transform, timember, {})
                   for timember in get_from_dict(ti, 'persons', [])]

    # Post process team_members for certificates and phone_numbers
    for teammember in teammembers:
        teammember['phone_numbers'] = [
            apply_transform(
                phone_numbers_transform,
                person_phone_number,
                {})
            for person_phone_number in ti_flatten_phone_numbers(
                get_from_dict(teammember,
                              'phone_numbers',
                              []))]

        teammember['certificates'] = [
            apply_transform(
                certificates_transform,
                person_certificate,
                {})
            for person_certificate in get_from_dict(
                teammember, 'certificates', [])]

    csp['team_members'] = teammembers
    csp['memberships'] = [apply_transform(memberships_transform, timember, {})
                          for timember in get_from_dict(ti, 'memberships')]

    csp['certificates'] = [
        apply_transform(certificates_transform, timember, {})
        for timember in get_from_dict(ti, 'pgp_keys')]

    return csp


def ti_json_to_dict_of_csp_dicts(jsoninput):
    # Convert list to a dictionary
    # {(short_name, country) => {TeamContact as dict in TI format}}
    csp_dicts = {}
    for ti in json.loads(jsoninput):
        d = ti_dict_to_csp_dict(ti)
        csp_dicts[(d['short_name'], d['country'])] = d
    return csp_dicts


def new_csp_dicts_from_diff_of_two_ti_jsons(oldjson, newjson):
    """
    Takes the TI JSON export which was used last time (oldjson)
    And the current TI JSON export.

    This will produce a list of csp_dicts (dict representations
    of TeamContact), which have been updated by the new JSON.
    It does not include TeamContacts that are not present in the
    JSON export.
    """
    newlist = []
    olddict = ti_json_to_dict_of_csp_dicts(oldjson)
    newdict = ti_json_to_dict_of_csp_dicts(newjson)
    for k, d in newdict.iteritems():
        if k not in olddict:
            newlist.append(d)
        elif not csp_dicts_are_same(olddict[k], d):
            newlist.append(d)
    return newlist


def csp_dicts_are_same(a, b):
    string_fields = ['csp_id', 'business_hours', 'business_hours_timezone',
                     'contact_postal_address', 'established',
                     'host_organisation', 'name', 'short_name', 'country',
                     'public_email', 'public_ftp', 'public_mailinglist',
                     'public_usenet', 'public_www', 'main_email']
    list_fields = ['scope_asns', 'scope_email', 'scope_ipranges',
                   'constituency_asns', 'constituency_domains',
                   'constituency_ipranges', 'constituency_types', ]
    for s in string_fields:
        # If values differ, but not if both do not have it!
        if a.get(s, 'Unique999999') != b.get(s, 'Unique999999'):
            return False
    for l in list_fields:
        if l not in a or l not in b:
            return False
        if sorted(a[l]) != sorted(b[l]):
            return False

    # Complex fields
    complex_fields_sort_order = {
        'phone_numbers': lambda d: (d['number'],
                                    d['tag'],
                                    d['timezone'],
                                    d['number_details']),
        'memberships': lambda d: (d['organisation'],
                                  d['membership_state'],
                                  d['since']),
        'certificates': lambda d: (d['keyid'],
                                   d['data']),
    }
    for ck, cv in complex_fields_sort_order.iteritems():
        if ck not in a or ck not in b:
            return False
        if sorted(a[ck], key=cv) != sorted(b[ck], key=cv):
            return False

    # TEAM_MEMBERS
    def tm_sort(d):
        # Includes all string fields
        return (d['email'],
                d['full_name'],
                d['constituency_rep'],
                d['host_rep'])
    a_tm_list = sorted(a['team_members'], key=tm_sort)
    b_tm_list = sorted(b['team_members'], key=tm_sort)
    if len(a_tm_list) != len(b_tm_list):
        return False
    # With same length, need to ensure equality of each teammember
    for a_tm, b_tm in zip(a_tm_list, b_tm_list):
        # complex fields
        for ck, cv in complex_fields_sort_order.iteritems():
            # Only check phone_numbers and certificates of individual
            # team members
            if ck not in ['phone_numbers', 'certificates']:
                continue
            if ck not in a_tm or ck not in b_tm:
                return False
            if sorted(a_tm[ck], key=cv) != sorted(b_tm[ck], key=cv):
                return False
        # string fields
        if tm_sort(a_tm) != tm_sort(b_tm):
            return False

    return True


def ti_flatten_phone_numbers(ti_phone_dicts):
    """
    Takes a list of TI JSON phone number dicts and flattens
    them according to their 'usage' tag.
    E.g. [{number: '1', usage: ['fax', 'main']}] ->
    [{number: '1', usage: 'fax'}, {'number': '1', usage: 'main'}]
    """
    new_phones = []
    for ti_phone in ti_phone_dicts:
        # if len(ti_phone['usage'] > 1):
        for usage in ti_phone['usage']:
            phone = ti_phone.copy()
            phone['usage'] = usage[:]
            new_phones.append(phone)
        # elif len(ti_phone['usage']) == 1:
        #     phone = ti_phone.copy()
        #     phone['usage'] = ti_phone['usage'][0].copy()
        #     new_phones.append(phone)
        # len(ti_phone['usage']) < 1 = no usage for phone, skip
    return new_phones


def apply_transform(transform_table, src, dest):
    """
    Apply transformations from transform_table to dictionary dest using src.
    Returns and modifies dest.
    """
    for csp_key, path in transform_table.items():
        try:
            dest[csp_key] = path[0](src, *path[1:])
        except SkipException:
            pass
    return dest


def const(_, ret):
    return ret


def has_members_in_dict(haydict, dictkeys, needleslist, yes=True, no=False):
    """
    Same as has_members, but hay is a dict and dictkeys
    is a path to the list in dict.
    """
    return has_members(
        get_from_dict(haydict, dictkeys, []),
        needleslist,
        yes=yes,
        no=no)


def has_members(hay, needleslist, yes=True, no=False):
    """
        Hay is a list.  If hay has all needes in needles list, return yes.
        no otherwise.
    """
    if type(needleslist) == str:
        needleslist = [needleslist]

    for needle in needleslist:
        if needle not in hay:
            return no
    return yes


def get_phone_tag(haydict, dictkeys):
    hay = get_from_dict(haydict, dictkeys)
    if 'emergency' in hay or 'emergency' == hay:
        return 'emergency'
    elif 'voice' in hay or 'voice' == hay:
        return 'main'
    elif 'fax' in hay or 'fax' == hay:
        return 'fax'
    return 'main'


def list_from_dictlist(dictlist, keylist, default=[]):
    if type(keylist) == str:
        keylist = [keylist]

    newlist = default
    for entry in dictlist:
        localdict = entry
        for k in keylist:
            if k in localdict:
                localdict = localdict[k]
            else:
                return newlist
        newlist.append(localdict)
    return newlist


def remove_from_list(l, toremove):
    if toremove in l:
        l.remove(toremove)
    return l


def get_date_from_dict(ti, keylist, default=''):
    """
    Like get_from_dict, but returns default or date object
    """


class SkipException(Exception):
    pass


def get_from_dict(ti, keylist, default='', skip_if_default=False):
    """
    Tries to return a value from dict.
    If keylist is a string, return ti[keylist] or default
    If keylist is a list: return t[key0][key1][...] or default
    """
    if type(keylist) == str:
        keylist = [keylist]

    localdict = ti
    for k in keylist:
        if (  # List
                type(k) == int and
                type(localdict) == list and
                len(localdict) > k
            ) or (  # Dict
                type(localdict) == dict and
                k in localdict):
            localdict = localdict[k]
        else:
            if skip_if_default:
                raise SkipException
            else:
                return default
    if skip_if_default and localdict == default:
        raise SkipException
    return localdict
