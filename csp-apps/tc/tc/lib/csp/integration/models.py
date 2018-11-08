import logging

from django.db import models
from django.db.models.signals import post_save, post_delete
from django.contrib.postgres.fields import ArrayField

from csp.central.models import Team, TrustCircle

LOG = logging.getLogger('ctc')


ACTIONS = (
    ('create', 'Create'),
    ('update', 'Update'),
    ('delete', 'Delete'),
)


class ChangeLogManager(models.Manager):
    def log(self, action, obj, target_trustcircles=None, target_teams=None):
        # Support old style single target calls, also listifies sets
        if type(target_trustcircles) in [str, unicode]:
            target_trustcircles = [target_trustcircles]
        elif type(target_trustcircles) == set:
            target_trustcircles = list(target_trustcircles)

        if type(target_teams) in [str, unicode]:
            target_teams = [target_teams]
        elif type(target_teams) == set:
            target_teams = list(target_teams)

        if target_trustcircles:
            self.create(
                action=action,
                model_pk=obj.pk,
                model_name='{}.{}'.format(obj._meta.app_label, obj._meta.model_name),
                to_share=getattr(obj, 'to_share', True),
                target_trustcircles=target_trustcircles)

        if target_teams:
            self.create(
                action=action,
                model_pk=obj.pk,
                model_name='{}.{}'.format(obj._meta.app_label, obj._meta.model_name),
                to_share=getattr(obj, 'to_share', True),
                target_teams=target_teams)

        if target_teams is None and target_trustcircles is None:
            self.create(
                action=action,
                model_pk=obj.pk,
                model_name='{}.{}'.format(obj._meta.app_label, obj._meta.model_name),
                to_share=getattr(obj, 'to_share', True))


class ChangeLog(models.Model):
    created = models.DateTimeField(auto_now_add=True)
    action = models.CharField(max_length=50, choices=ACTIONS)
    model_name = models.CharField(max_length=50)
    model_pk = models.CharField(max_length=128)
    to_share = models.BooleanField(default=True)
    target_trustcircles = ArrayField(
        models.CharField(max_length=255, null=True, blank=True),
        default=None,
        blank=True,
        null=True)
    target_teams = ArrayField(
        models.CharField(max_length=255, null=True, blank=True),
        default=None,
        blank=True,
        null=True)

    objects = ChangeLogManager()

    class Meta:
        ordering = ['created']

    def __unicode__(self):
        return '{} on {}:{}'.format(self.action, self.model_name, self.model_pk)


def log_create_or_change(sender, instance, created, **kwargs):
    ChangeLog.objects.log('create' if created else 'update', instance)
    LOG.info('{} "{}" {}'.format(instance._meta.model_name, instance,
                                 'created' if created else 'updated'))


def log_delete(sender, instance, **kwargs):
    ChangeLog.objects.log('delete', instance)
    LOG.info('{} "{}" deleted'.format(instance._meta.model_name, instance))


for model in (Team, TrustCircle):
    post_save.connect(log_create_or_change, sender=model)
    post_delete.connect(log_delete, sender=model)
