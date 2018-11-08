from django import forms
from django.urls import reverse_lazy


class AnnotatedForm(forms.Form):
    disabled = []
    readonly = []
    classes = {
        '__all__': 'form-control',
    }

    def __init__(self, *args, **kwargs):
        super(AnnotatedForm, self).__init__(*args, **kwargs)
        # raise Exception('{}'.format(self.base_fields))
        for field_name, field in self.fields.items():
            widget = self.fields[field_name].widget
            if field_name in self.readonly:
                widget.attrs['readonly'] = True
            if field_name in self.disabled:
                widget.attrs['disabled'] = True
            if field_name in self.classes or '__all__' in self.classes:
                classes = [self.classes.get('__all__'),
                           self.classes.get(field_name),
                           widget.attrs.get('class')]
                widget.attrs['class'] = ' '.join(c for c in classes if c)


class AnnotatedModelForm(forms.ModelForm, AnnotatedForm):
    pass


class Select2(forms.Select):
    def __init__(self, ajax_data=None, allow_new=False, **kwargs):
        super(Select2, self).__init__(**kwargs)
        self.attrs['data-s2'] = ''
        if allow_new:
            self.attrs['data-s2-tags'] = ''
        if ajax_data:
            self.attrs['data-s2-ajax-data'] = ajax_data


class SuggestMixin(object):
    def __init__(self, *args, **kwargs):
        source = kwargs.pop('source')
        super(SuggestMixin, self).__init__(*args, **kwargs)
        self.attrs['data-s2'] = ''
        self.attrs['data-s2-tags'] = ''
        self.attrs['data-s2-url'] = reverse_lazy('suggest_api', args=[source])

    def optgroups(self, name, value, attrs=None):
        selected = set(value)
        subgroup = [self.create_option(name, v, v, selected, i) for i, v in enumerate(value)]
        return [(None, subgroup, 0)]


class Suggest(SuggestMixin, forms.Select):
    pass


class SuggestMultiple(SuggestMixin, forms.SelectMultiple):
    def format_value(self, value):
        # FIXME: for some reason the ArrayField values are returned
        # as a comma-separated string. The field class normally returns a Python
        # array, to need to find the place where it's converted and deactivate!
        # In the meantime, we simply split it back to a list here.
        if not isinstance(value, (tuple, list)):
            value = [] if value is None else value.split(',')
        return [unicode(v) for v in value if v]


class Select2Multiple(forms.SelectMultiple):
    def __init__(self, ajax_data=None, allow_new=False, **kwargs):
        super(Select2Multiple, self).__init__(**kwargs)
        self.attrs['data-s2'] = ''
        if allow_new:
            self.attrs['data-s2-tags'] = ''
        self.ajax_data = ajax_data
        if self.ajax_data:
            self.attrs['data-s2-ajax-data'] = self.ajax_data

    def optgroups(self, name, value, attrs=None):
        if self.ajax_data:
            selected = set(value)
            subgroup = [self.create_option(name, v, v, selected, i) for i, v in enumerate(value)]
            return [(None, subgroup, 0)]
        else:
            super(Select2Multiple, self).optgroups(name, value, attrs)
