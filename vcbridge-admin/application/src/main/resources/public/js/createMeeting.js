$(document).ready(function() {
  var max_fields = 10; // maximum input boxes allowed
  var wrapper = $(".input_fields_wrap"); // Fields wrapper
  var add_button = $("#add_field_button"); // Add button ID

  var delete_button = $("#delete_button"); // Delete button
  var new_email = $("#new_email"); // new email input

  var x = 1; // initlal text box count

  $(delete_button).click(function(e) {
    e.preventDefault();
    $("tr.useremail").each(function() {
      $this = $(this)
      if ($(this).find("input.todelete").is(":checked")) {
        $(this).remove();
      }
    });
  });
  $(add_button).click(function(e) { // on add input
    // button click
    e.preventDefault();
    if (new_email.val().trim() != "") {
      $(wrapper).append('<tr class="useremail"><td><input class="todelete" type="checkbox" value=""></input></td><td><input hidden="true" type="text" value="' + new_email.val() + '" name="emails[' + x + ']"/><div>' + new_email.val() + '</div></td></tr>'); // add
      // input
      // box
      new_email.val("");
      new_email.focus();
      x++; // text box increment
    }
  });

  $(wrapper).on("click", ".remove_field", function(e) { // user click on remove
    // text
    e.preventDefault();
    $(this).parent('div').remove();
    x--;
  });
  $('#submit-participants').click(function() {
    var lines = $('#participants_bulk_ta').val().split(/[\s,;]+/);
    for (var i = 0; i < lines.length; i++) {
      if (lines[i].trim() != "") {
        $(wrapper).append('<tr class="useremail"><td><input class="todelete" type="checkbox" value=""></input></td><td><input hidden="true" type="text" value="' + lines[i] + '" name="emails[' + x + ']"/><div>' + lines[i] + '</div></td></tr>'); // add
        // input
        // box
        x++; // text box increment
      }
    }
    $('#participants_bulk_ta').val("");
  });
  $('#createMeetingForm').on('submit', function(e) { // use on if jQuery
    // 1.7+
    e.preventDefault(); // prevent
    // form from
    // submitting
    console.log($(".datetimeinput").val());
    console.log($("#timepicker").val());
    $(".datetimeoutput").val(moment.tz($("#datepicker").val() + 'T' + $("#timepicker").val(), 'MM-DD-YYYYTHH:mm', $("#selecttimezone").val()).format());
    console.log($("#selecttimezone").val());
    console.log($(".datetimeoutput").val());

    this.submit();
  });

  $('#datepicker').datepicker({
    format : "mm-dd-yyyy",
    orientation : "auto bottom"
  });

});