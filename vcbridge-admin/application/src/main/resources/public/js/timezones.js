$(document).ready(function() {
	timezone = moment.tz.names();
    for (var i = 0; i < timezone.length; i++) {
      $('select').append('<option value="' + timezone[i] + '">' + timezone[i] + '</option>');
    }
    //$('select').selectpicker();
});