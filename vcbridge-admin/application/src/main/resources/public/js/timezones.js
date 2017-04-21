$(document).ready(function() {
	console.log("ohhhh");
	timezone = moment.tz.names();
    for (var i = 0; i < timezone.length; i++) {
    	console.log(timezone[i]);
      $('select').append('<option value="' + timezone[i] + '">' + timezone[i] + '</option>');
    }
    //$('select').selectpicker();
    
});