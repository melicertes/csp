from itertools import izip_longest
from django import template
from django.forms import widgets
from csp.common.forms import Suggest, SuggestMultiple

register = template.Library()


@register.filter
def ucfirst(val):
    if not val:
        return ''
    val = unicode(val)
    return ' '.join(word[0].upper() + word[1:] for word in val.split())


@register.inclusion_tag('contacts/includes/field_uuid_value.html')
def field_uuid_value(obj, fieldname, label=None):
    return field_value(obj, fieldname, label=label)


@register.inclusion_tag('contacts/includes/field_value.html')
def field_value(obj, fieldname, label=None):
    field = obj._meta.get_field(fieldname)
    value = getattr(obj, fieldname)
    if field.many_to_many:
        value = [o for o in value.all()]
        verbose_name = field.related_model._meta.verbose_name_plural
    elif field.many_to_one and value:
        verbose_name = value._meta.verbose_name
    else:
        verbose_name = field.verbose_name
    if isinstance(value, (list, tuple)):
        value = ', '.join([unicode(e) for e in value])
    return {
        'label': label if label else ucfirst(verbose_name),
        'value': value,
    }


@register.inclusion_tag('contacts/includes/related_fields.html')
def related_fields(obj, fieldname, title_all=None, title_single=None):
    field = obj._meta.get_field(fieldname)
    model = field.related_model
    fields = [f for f in model._meta.get_fields() if f != field.remote_field]
    related_objects = getattr(obj, fieldname).all()
    return {
        'title_all': title_all or ucfirst(model._meta.verbose_name_plural),
        'title_single': title_single or ucfirst(model._meta.verbose_name),
        'fields': fields,
        'related_objects': related_objects,
    }


@register.inclusion_tag('includes/form_field.html')
def form_field(field):
    return {
        'field': field,
    }


class EscapeScriptNode(template.Node):
    def __init__(self, nodelist):
        super(EscapeScriptNode, self).__init__()
        self.nodelist = nodelist

    def render(self, context):
        out = self.nodelist.render(context)
        escaped_out = out.replace(u'</script>', u'<\\/script>')
        return escaped_out


@register.tag
def escapescript(parser, token):
    nodelist = parser.parse(('endescapescript',))
    parser.delete_first_token()
    return EscapeScriptNode(nodelist)


@register.inclusion_tag('contacts/includes/related_fields_diffview.html')
def related_fields_diffview(formset, incoming, fieldname, title_all=None,
                            title_single=None, diffdatablockstyle=""):

    if hasattr(incoming, 'deserialized'):
        incoming = incoming.deserialized

    incoming_set = getattr(incoming, fieldname, [])
    incoming_set.reverse()
    zip_objects = izip_longest(
        formset,
        getattr(incoming,
                fieldname) if incoming is not None else [])

    fields = []
    if incoming is not None:
        field = incoming._meta.get_field(fieldname)
        model = field.related_model
        fields = [f for f in model._meta.get_fields()
                  if f != field.remote_field]
        title_all = title_all or ucfirst(model._meta.verbose_name_plural)
        title_single = title_single or ucfirst(model._meta.verbose_name)

    return {
        'title_all': title_all,
        'title_single': title_single,
        'fields': fields,
        'zip_objects': list(zip_objects),
        'formset': formset,
        'incoming': incoming,
        'fieldname': fieldname,
        'diffdatablockstyle': diffdatablockstyle,
    }


@register.inclusion_tag(
    'contacts/includes/related_fields_diffview_teammember.html')
def related_fields_diffview_teammember(formset, incoming, fieldname,
                                       title_all=None, title_single=None):
    def zip_matching_pairs(xs, ys):
        """
        Keep the original formset sorting to stay compatible
        with the frontend JS code
        """
        return_set = []

        # Match y's email to each x
        for x in xs:
            x_email = x.initial['email']
            loc = None
            pos = 0
            el = None
            for y in ys:
                if y.email == x_email:
                    loc = pos
                    break
                pos += 1
            if loc is not None:
                el = ys.pop(loc)
            else:
                el = None
            return_set.append((x, el))

        # Append remaining ys with no x
        for y in ys:
            return_set.append((None, y))

        return return_set

    if hasattr(incoming, 'deserialized'):
        incoming = incoming.deserialized

    zip_objects = zip_matching_pairs(
        formset,
        getattr(incoming,
                fieldname) if incoming is not None else [])

    fields = []
    if incoming is not None:
        field = incoming._meta.get_field(fieldname)
        model = field.related_model
        fields = [f for f in model._meta.get_fields()
                  if f != field.remote_field]
        title_all = title_all or ucfirst(model._meta.verbose_name_plural)
        title_single = title_single or ucfirst(model._meta.verbose_name)
    return {
        'title_all': title_all,
        'title_single': title_single,
        'fields': fields,
        'zip_objects': list(zip_objects),
        'formset': formset,
        'incoming': incoming,
        'fieldname': fieldname
    }


@register.inclusion_tag('contacts/includes/diff_field_value.html')
def diff_field_value(form, obj, fieldname, label=None):
    field = obj._meta.get_field(fieldname)
    value = getattr(obj, fieldname)
    if field.many_to_many:
        value = [o for o in value.all()]
        verbose_name = field.related_model._meta.verbose_name_plural
    elif field.many_to_one and value:
        verbose_name = value._meta.verbose_name
    else:
        verbose_name = field.verbose_name

    if isinstance(value, (list, tuple)):
        valuestring = ', '.join([unicode(e) for e in value])
    else:
        valuestring = value

    auto_id = 'NEW'
    fieldtype = 'unknown'
    has_changed = True
    try:
        if type(form[fieldname].field.widget) == Suggest:
            fieldtype = 'selectsingle'
        elif type(form[fieldname].field.widget) == SuggestMultiple:
            fieldtype = 'selectmultiple'
        elif type(form[fieldname].field.widget) == widgets.CheckboxInput:
            fieldtype = 'boolean'
        elif type(form[fieldname].field.widget) in [
                widgets.TextInput, widgets.DateInput, widgets.Textarea,
                widgets.EmailInput]:
            fieldtype = 'simple'

        auto_id = form[fieldname].auto_id
        has_changed = form.initial.get(fieldname) != getattr(obj, fieldname)
    except:
        pass

    return {
        'label': label if label else ucfirst(verbose_name),
        'value': valuestring,
        'datavalue': value,
        'fieldtype': fieldtype,
        'name': fieldname,
        'auto_id': auto_id,
        'has_changed': has_changed,
    }
