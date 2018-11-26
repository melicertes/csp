from django.contrib.auth.models import AbstractUser


class User(AbstractUser):
    """
    Subclass the built-in Django user to override the has_perm method.
    Database representation is unchanged.
    """

    def has_perm(self, perm, obj=None):
        oam_perms = getattr(self, 'oam_permissions', [])
        if oam_perms and perm in oam_perms:
            return True
        return super(User, self).has_perm(perm, obj)
