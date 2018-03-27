from __future__ import absolute_import

from rest_framework import views
from rest_framework.response import Response

from .models import get_suggestions_for


class SuggestionView(views.APIView):
    def get(self, request, name):
        search = request.GET.get('q', '').strip()
        suggestions = get_suggestions_for(name, search)
        result = [{'id': val, 'text': val} for val in suggestions]
        return Response({'results': result})
