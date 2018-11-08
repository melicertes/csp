from __future__ import unicode_literals

from datetime import date
import re
import uuid

from django.contrib.postgres.fields import ArrayField, JSONField
from django.core import validators
from django.core.exceptions import ValidationError
from django.db import models
from django.utils import timezone

from csp.central.models import TrustCircle, Team


# ---------------------------------------
# --------------------------------------- FieldValidators
# ---------------------------------------

def validate_phone_list(value):
    for line in value:
        validate_phone(line)


validate_phone = validators.RegexValidator(
    re.compile(r'^\+[0-9]+ [0-9]+ [0-9 ]*[0-9]$'),
    u'Please enter a valid phone number: '
    u'+COUNTRY-CODE AREA-CODE NUMBER [EXTENSION]')


# ---------------------------------------
# --------------------------------------- Models
# ---------------------------------------

class AbstractPhoneNumber(models.Model):
    class Meta:
        abstract = True
        verbose_name = 'Phone Number'
        verbose_name_plural = 'Phone Numbers'

    TAG_CHOICES = (
        ('main', 'Main'),
        ('emergency', 'Emergency'),
        ('fax', 'Fax'))

    VISIBILITY_CHOICES = (
        ('public', 'Public'),
        ('restricted', 'Restricted'),
        ('private', 'Private'))

    tag = models.CharField(max_length=20,
                           choices=TAG_CHOICES,
                           default='main')
    number = models.CharField(max_length=255, blank=True)
    timezone = models.CharField(max_length=255, blank=True, default='UTC')
    number_details = models.CharField(max_length=255, blank=True)
    visibility = models.CharField(max_length=20,
                                  choices=VISIBILITY_CHOICES,
                                  default='private')


class TeamPhoneNumber(AbstractPhoneNumber):
    teamcontact = models.ForeignKey('TeamContact',
                                    related_name='phone_numbers',
                                    on_delete=models.CASCADE,
                                    db_index=True)


class PersonPhoneNumber(AbstractPhoneNumber):
    personcontact = models.ForeignKey('PersonContact',
                                      related_name='phone_numbers',
                                      on_delete=models.CASCADE,
                                      db_index=True)


class TeamMemberPhoneNumber(AbstractPhoneNumber):
    teammember = models.ForeignKey('TeamMember',
                                   related_name='phone_numbers',
                                   on_delete=models.CASCADE,
                                   db_index=True)


class AbstractKeyOrCertificate(models.Model):
    class Meta:
        abstract = True
        verbose_name = 'PGP Key / X.509 Certificate'
        verbose_name_plural = 'PGP Keys / X.509 Certificates'

    TAG_CHOICES = (
        ('main', 'Main'),
        ('additional', 'Additional'))

    METHOD_CHOICES = (
        ('x509', 'X.509 Certificate'),
        ('pgp', 'PGP Key'))

    VISIBILITY_CHOICES = (
        ('private', 'Public'),
        ('restricted', 'Restricted'),
        ('public', 'Private'))

    tag = models.CharField(max_length=20,
                           choices=TAG_CHOICES,
                           default='main')
    method = models.CharField(max_length=20,
                              choices=METHOD_CHOICES,
                              default='x509')
    keyid = models.CharField(max_length=255, blank=True)
    visibility = models.CharField(max_length=20,
                                  choices=VISIBILITY_CHOICES,
                                  default='private')
    data = models.TextField(blank=True)


class TeamKeyOrCertificate(AbstractKeyOrCertificate):
    teamcontact = models.ForeignKey('TeamContact',
                                    related_name='certificates',
                                    on_delete=models.CASCADE)


class PersonKeyOrCertificate(AbstractKeyOrCertificate):
    personcontact = models.ForeignKey('PersonContact',
                                      related_name='certificates',
                                      on_delete=models.CASCADE)


class TeamMemberKeyOrCertificate(AbstractKeyOrCertificate):
    teammember = models.ForeignKey('TeamMember',
                                   related_name='certificates',
                                   on_delete=models.CASCADE)


class AbstractMembership(models.Model):
    class Meta:
        abstract = True
        verbose_name = 'Membership'
        verbose_name_plural = 'Memberships'

    organisation = models.CharField(max_length=255, blank=True)
    membership_state = models.CharField(max_length=255, blank=True)
    since = models.CharField(max_length=255, blank=True)


class TeamMembership(AbstractMembership):
    teamcontact = models.ForeignKey('TeamContact',
                                    related_name='memberships',
                                    on_delete=models.CASCADE,
                                    db_index=True)


class PersonMembership(AbstractMembership):
    personcontact = models.ForeignKey('PersonContact',
                                      related_name='memberships',
                                      on_delete=models.CASCADE,
                                      db_index=True)


class TeamMemberMembership(AbstractMembership):
    teammember = models.ForeignKey('TeamMember',
                                   related_name='memberships',
                                   on_delete=models.CASCADE,
                                   db_index=True)


class OutbandAlertingContact(models.Model):
    teamcontact = models.ForeignKey(
        'TeamContact',
        related_name='outband_alerting_contacts',
        on_delete=models.CASCADE)
    description = models.CharField(max_length=255, blank=True)
    phone_number = models.CharField(max_length=255, blank=True)
    number_type = models.CharField(max_length=255, blank=True)
    calling_time_begin = models.CharField(max_length=255, blank=True)
    calling_time_end = models.CharField(max_length=255, blank=True)
    timezone = models.CharField(max_length=255, blank=True, default='UTC')
    coverage = models.CharField(max_length=255, blank=True)

    class Meta:
        verbose_name = 'Outband Alerting Contact'
        verbose_name_plural = 'Outband Alerting Contacts'


class OutbandAlertingAccess(models.Model):
    teamcontact = models.ForeignKey(
        'TeamContact',
        related_name='outband_alerting_accesses',
        on_delete=models.CASCADE)
    description = models.CharField(max_length=255, blank=True)
    access_pin = models.CharField(max_length=255, blank=True)
    permissions = ArrayField(models.TextField(), default=list, blank=True)

    class Meta:
        verbose_name = 'Outband Alerting Access'
        verbose_name_plural = 'Outband Alerting Accesses'


class TeamContact(models.Model):
    """
    Full model
    """
    class Meta:
        unique_together = (('short_name', 'country'), )

    CSIRT_BOOLEAN_CHOICES = (
        ('', '-'),
        ('supported', 'Supported'),
        ('unsupported', 'Not Supported'))

    id = models.UUIDField(default=uuid.uuid4, primary_key=True, editable=False)
    csp_id = models.CharField('CSP ID', max_length=255, blank=True)
    csp_domain = models.CharField('CSP Domain', max_length=255, blank=True)
    csp_installed = models.BooleanField('CSP Installed', default=False)
    nis_team_types = ArrayField(models.TextField(), default=list, blank=True,
                                verbose_name='NIS Team Types')
    nis_sectors = ArrayField(models.TextField(), default=list, blank=True,
                             verbose_name='NIS Sectors')
    status = models.CharField(max_length=255)
    created = models.DateTimeField('Created on', auto_now_add=True)

    # -- Team
    # PrimaryKeyTuple for team: short_name, country
    short_name = models.CharField('Short Name', max_length=128)
    name = models.CharField('Official Name', max_length=255)
    host_organisation = models.CharField('Host Organisation', max_length=255)
    country = models.CharField(max_length=255)
    additional_countries = ArrayField(models.TextField(), verbose_name='Additional Countries', default=list, blank=True)
    established = models.DateField('Established on', default=date.today)
    description = models.CharField('Description', max_length=255, default='', blank=True)

    # -- Constituency
    constituency_types = ArrayField(models.TextField(), default=list, blank=True)
    constituency_description = models.TextField(blank=True)
    member_locations = ArrayField(models.TextField(), default=list, blank=True)

    # -- Constituency Network
    constituency_asns = ArrayField(models.TextField(), blank=True,
                                   verbose_name='Consituency ASNs')
    constituency_domains = ArrayField(models.TextField(), blank=True,
                                      verbose_name='Consituency Domains')
    constituency_ipranges = ArrayField(models.TextField(), blank=True,
                                       verbose_name='Consituency IP-Ranges')

    # -- Security Notification Scope
    scope_asns = ArrayField(models.TextField(), blank=True, verbose_name='Scope ASNs')
    scope_ipranges = ArrayField(models.TextField(), blank=True, verbose_name='Scope IP-ranges')
    scope_email = ArrayField(models.TextField(), blank=True, verbose_name='Scope Emails')

    # -- Contact Information
    contact_postal_address = models.TextField(blank=True)
    contact_postal_country = models.TextField(blank=True)

    # # -- Phone Numbers
    # phone_numbers = models.ManyToManyField(PhoneNumber)

    # -- Team Email Address
    main_email = models.EmailField(blank=True)
    public_email = models.EmailField(blank=True)
    automated_email = models.EmailField(blank=True)
    automated_email_format = ArrayField(models.TextField(), default=list, blank=True)

    # # -- PGP Key / X.509 Certificates
    # certificates = models.ManyToManyField(KeyOrCertificate)

    # -- Public Information Resources
    public_www = models.TextField(blank=True)
    public_ftp = models.TextField(blank=True)
    public_mailinglist = models.TextField(blank=True)
    public_usenet = models.TextField(blank=True)

    # -- Business Hours
    business_hours = models.TextField(blank=True)
    outside_business_hours = \
        models.TextField(blank=True)
    business_hours_timezone = \
        models.TextField(blank=True, default='UTC')

    # -- Billing Information
    billing_postal_address = models.TextField(blank=True)
    billing_postal_country = models.TextField(blank=True)
    vat_number = models.TextField(blank=True)

    # -- Policies
    classification_policy = models.TextField(blank=True)
    exclusivity_policy = models.TextField(blank=True)
    disclosure_policy = models.TextField(blank=True)
    legal_considerations = models.TextField(blank=True)
    cryptography_policy = models.TextField(blank=True)

    # -- RFC 2350
    url_rfc2350 = models.CharField(max_length=255, blank=True)

    # # -- Memberships
    # memberships = models.ManyToManyField(Membership)

    # -- Services Provided to the Constituency
    reactive_services = ArrayField(models.TextField(), default=list, blank=True)
    reactive_services_comment = models.TextField(blank=True)
    proactive_services = ArrayField(models.TextField(), default=list, blank=True)
    proactive_services_comment = models.TextField(blank=True)
    quality_management = ArrayField(models.TextField(), default=list, blank=True)
    quality_management_comment = models.TextField(blank=True)

    # -- Tools and Expertise
    process_tool = models.TextField(blank=True)
    related_software = models.TextField(blank=True)
    generic = models.TextField(blank=True)
    os = models.TextField(blank=True)
    platform = models.TextField(blank=True)
    network = models.TextField(blank=True)
    other = models.TextField(blank=True)
    references = models.TextField(blank=True)

    # -- Process Information
    accreditations = models.TextField(blank=True)
    projects = models.TextField(blank=True)
    reporting_structure = models.TextField(blank=True)

    # -- Staff information
    education = models.TextField(blank=True)
    headcount_normal = models.TextField(blank=True)
    headcount_backup = models.TextField(blank=True)
    fte_normal = models.TextField(blank=True)
    fte_backup = models.TextField(blank=True)

    # -- Statement of CSIRT-Support
    provide_team_description = \
        models.CharField("Provide team description (MUST)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    provide_team_description_comment = \
        models.TextField(blank=True)

    access_of_accredited_teams = \
        models.CharField("Access of accredited teams to your team's "
                         "data (MUST)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    access_of_accredited_teams_comment = \
        models.TextField(blank=True)

    public_access = \
        models.CharField("Public access to your team's data marked as "
                         "public (MUST)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    public_access_comment = \
        models.TextField(blank=True)

    external_services_rfc2350 = \
        models.CharField("External services described in RFC 2350 "
                         "format (MUST)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    external_services_rfc2350_comment = \
        models.TextField(blank=True)

    adhere_to_tlp = \
        models.CharField("Adhere to the Information Sharing "
                         "Traffic Light Protocol (MUST)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    adhere_to_tlp_comment = \
        models.TextField(blank=True)

    allow_ti_gpg_key = \
        models.CharField("Allow GPG/PGP key signatures by the TI "
                         "service team (MUST)", max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    allow_ti_gpg_key_comment = \
        models.TextField(blank=True)

    commitment_updates = \
        models.CharField("Commitment to provide regular updates "
                         "for team data (MUST)", max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    commitment_updates_comment = \
        models.TextField(blank=True)

    protect_information_received = \
        models.CharField("Protect information received by your "
                         "team (MUST)", max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    protect_information_received_comment = \
        models.TextField(blank=True)

    provide_feedback = \
        models.CharField("Provide feedback to questions of the "
                         "TI service (MUST)", max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    provide_feedback_comment = \
        models.TextField(blank=True)

    allow_site_visits = \
        models.CharField("Allow site visits of the TI service "
                         "team (MUST)", max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    allow_site_visits_comment = \
        models.TextField(blank=True)

    payment_of_fees = \
        models.CharField("Payment of fees for the TI service (MUST)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    payment_of_fees_comment = \
        models.TextField(blank=True)

    registration_two_teams = \
        models.CharField("Registration of two team representative (SHOULD)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    registration_two_teams_comment = \
        models.TextField(blank=True)

    sim3_support = \
        models.CharField("Acceptance of SIM3 Model as framework for "
                         "self-assessment (SHOULD)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    sim3_support_comment = \
        models.TextField(blank=True)

    meetings_attendance = \
        models.CharField("Regular attendance a TF-CSIRT/TI Meetings "
                         "(SHOULD)", max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    meetings_attendance_comment = \
        models.TextField(blank=True)

    csirt_code_of_practice = \
        models.CharField("Compliance with the \"CSIRT Code of "
                         "Practive\" (SHOULD)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    csirt_code_of_practice_comment = \
        models.TextField(blank=True)

    reaction_tests_particiation = \
        models.CharField("Participation in \"Reaction Tests\" (SHOULD)",
                         max_length=20,
                         choices=CSIRT_BOOLEAN_CHOICES)
    reaction_tests_particiation_comment = \
        models.TextField(blank=True)

    # -- IRT object
    managed_by_ti = models.BooleanField(default=False)
    irt_encryption_keys = ArrayField(models.TextField(), default=list, blank=True)
    irt_signature_keys = ArrayField(models.TextField(), default=list, blank=True)
    irt_authentication_keys = ArrayField(models.TextField(), default=list, blank=True)
    irt_emails = ArrayField(models.TextField(), default=list, blank=True)
    irt_abuse_emails = ArrayField(models.TextField(), default=list, blank=True)
    irt_notify_emails = ArrayField(models.TextField(), default=list, blank=True)
    irt_phone_numbers = ArrayField(models.TextField(), default=list, blank=True, validators=[validate_phone_list])
    irt_fax_numbers = ArrayField(models.TextField(), default=list, blank=True, validators=[validate_phone_list])
    irt_emergency_numbers = ArrayField(models.TextField(), default=list, blank=True, validators=[validate_phone_list])
    irt_rfc2350 = models.TextField(blank=True)
    irt_address = models.TextField(blank=True)
    irt_additional_remarks = models.TextField(blank=True)

    # -- Outband Alerting
    outband_alerting_id = models.CharField(blank=True, max_length=255)
    # outband_alerting_contacts = models.ManyToManyField(OutbandAlertingContact)
    # outband_alerting_accesses = models.ManyToManyField(OutbandAlertingAccess)
    # team_members = models.ManyToManyField(Person)

    def __unicode__(self):
        return '%s, %s' % (self.short_name, self.country)


class AbstractPersonContact(models.Model):
    class Meta:
        abstract = True

    EMAIL_VISIBILITY_PRIVATE = 'private'
    EMAIL_VISIBILITY_PUBLIC = 'public'
    EMAIL_VISIBILITY_RESTRICTED = 'restricted'
    EMAIL_VISIBILITY_CHOICES = (
        (EMAIL_VISIBILITY_PUBLIC, 'Public'),
        (EMAIL_VISIBILITY_RESTRICTED, 'Restricted'),
        (EMAIL_VISIBILITY_PRIVATE, 'Private'))

    id = models.UUIDField(default=uuid.uuid4, primary_key=True, editable=False)

    full_name = models.TextField('Full Name', blank=True)
    email_visibility = models.TextField('Email Visibility',
                                        default='private')
    postal_address = models.TextField('Postal Address', blank=True)
    postal_country = models.TextField('Postal Country', blank=True)

    ml_email = models.TextField('Mailinglist Email', blank=True)
    ml_key = models.TextField('Mailinglist Email PGP Key', blank=True)


class PersonContact(AbstractPersonContact):
    email = models.EmailField('Email', blank=False, unique=True)

    def __unicode__(self):
        return '%s' % (self.email)


class TeamMember(AbstractPersonContact):
    email = models.EmailField('Email', blank=False)
    team = models.ForeignKey(TeamContact, null=True, default=None, blank=True,
                             related_name='team_members')
    team_role = models.TextField(blank=True, null=True, default='Team Member')

    host_rep = models.BooleanField(blank=True, default=False)
    constituency_rep = models.BooleanField(blank=True, default=False)

    def save(self, *args, **kwargs):
        super(TeamMember, self).save(*args, **kwargs)


def ltc_prefix_validator(value):
    if not value.startswith('LTC::'):
        raise ValidationError(
            'Local trust circle short names need to start with "LTC::"')


TLP_CHOICES = (
    ('red', 'Red'),
    ('amber', 'Amber'),
    ('green', 'Green'),
    ('white', 'White'),
)


class LocalTrustCircle(models.Model):
    id = models.UUIDField(default=uuid.uuid4, primary_key=True, editable=False)
    short_name = models.CharField(
        '(Short) LTC Name',
        max_length=128,
        unique=True,
        validators=[ltc_prefix_validator],
        help_text='Needs to start with "LTC::"')
    tlp = models.CharField('TLP', max_length=255, choices=TLP_CHOICES, blank=True)
    auth_source = models.CharField('Authoritative Source', max_length=255, blank=True)
    name = models.CharField('(Long) LTC Name', max_length=255)
    description = models.CharField(max_length=255)
    info_url = models.URLField('URL for Public Information', blank=True)
    membership_url = models.URLField('URL for Membership Directory',
                                     blank=True)
    created = models.DateTimeField(default=timezone.now)

    team_contacts = models.ManyToManyField(
        TeamContact,
        blank=True,
        verbose_name='Team Contacts',
        related_name='in_localcircles')

    teams = models.ManyToManyField(
        Team,
        blank=True,
        verbose_name='Teams',
        related_name='in_localcircles')

    trustcircles = models.ManyToManyField(
        TrustCircle,
        blank=True,
        verbose_name='Central Trust Circles',
        related_name='in_localcircles')

    person_contacts = models.ManyToManyField(
        PersonContact,
        blank=True,
        verbose_name='Person Contacts',
        related_name='in_localcircles')

    def __unicode__(self):
        return self.name


class IncomingTeamContact(models.Model):
    created = models.DateTimeField('Received on', auto_now_add=True)
    csp_id = models.TextField('Sent from')
    app_id = models.TextField()
    target_circle_id = ArrayField(models.TextField(), verbose_name='Shared with CTC', blank=True)
    target_team_id = ArrayField(models.TextField(), verbose_name='Shared with Team', blank=True)
    data_object = JSONField()

    class Meta:
        verbose_name = 'Incoming Team Contact'
        verbose_name_plural = 'Incoming Team Contacts'
        ordering = ['created']

    def is_update(self):
        return self.get_existing() is not None

    def get_existing(self):
        return (TeamContact.objects
                .filter(country=self.data_object['country'],
                        short_name=self.data_object['short_name'])
                .first())

    @property
    def deserialized(self):
        if not hasattr(self, '_deserialized'):
            from csp.contacts.api import TeamContactSerializer
            serializer = TeamContactSerializer(data=self.data_object)
            # disable the unique validator on country,short_name
            serializer.validators = []
            serializer.is_valid(raise_exception=True)
            self._deserialized = serializer.as_object()
        return self._deserialized
