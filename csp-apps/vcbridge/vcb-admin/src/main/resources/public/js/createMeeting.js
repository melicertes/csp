var x = 1;
var max_fields = 10; // maximum input boxes allowed
var wrapper = $(".input_fields_wrap"); // Fields wrapper
var add_button = $("#add_field_button"); // Add button ID

var delete_button = $("#delete_button"); // Delete button
var new_email = $("#new_email"); // new email input

var addParticipants = function (input) {
    var contacts = [];
    var phrases = input.split(/[\n,;]+/);
    // console.log(phrases);
    for (var i = 0; i < phrases.length; i++) {
        if (phrases[i].trim() != "") {
            // console.log('Checking phrase ' + phrases[i]);
            var tokens = phrases[i].trim().split(/\s+/);
            if (tokens.length == 3) {
                if (makeEmailList(tokens[2]).length == 0) {
                    var extracted_emails = makeEmailList(phrases[i]).split(/[\s,;]+/);
                    for (var j = 0; j < extracted_emails.length; j++) {
                        if (extracted_emails[k].trim() != '') {
                            // console.log('Adding no-name contact ' + extracted_emails[j]);
                            contacts.push({
                                name: '',
                                surname: '',
                                email: extracted_emails[j]
                            });
                        }
                    }
                } else {
                    // console.log('Adding contact ' +
                    // makeEmailList(tokens[2]).split(/[\s,;]+/));
                    contacts.push({
                        name: tokens[0].trim(),
                        surname: tokens[1].trim(),
                        email: makeEmailList(tokens[2]).split(/[\s,;]+/)
                    });
                }
            } else {
                var extracted_emails = makeEmailList(phrases[i]).split(/[\s,;]+/);
                for (var k = 0; k < extracted_emails.length; k++) {
                    if (extracted_emails[k].trim() != '') {
                        // console.log('Adding no-name [2] contact ' + extracted_emails[k]);
                        contacts.push({
                            name: '',
                            surname: '',
                            email: extracted_emails[k]
                        });
                    }
                }
            }

        }
    }
    for (var i = 0; i < contacts.length; i++) {
        $(wrapper).append(
            '<tr class="useremail"><td><input class="todelete" type="checkbox" value=""></input></td>    <td><input hidden="true" type="text" value="' + contacts[i].name + '" name="emails[' + x + '].name"/><div>' + contacts[i].name + '</div></td>    <td><input hidden="true" type="text" value="'
            + contacts[i].surname + '" name="emails[' + x + '].surname"/><div>' + contacts[i].surname + '</div></td>    <td><input hidden="true" type="text" value="' + contacts[i].email + '" name="emails[' + x + '].email"/><div>' + contacts[i].email + '</div></td></tr>'); // add
        // input
        // box
        x++; // text box increment
    }
}



var addTCParticipant = function (email, fname, lname) {
    var contacts = [];
    contacts.push({
        name: fname,
        surname: lname,
        email: email
    });
    for (var i = 0; i < contacts.length; i++) {
        $(wrapper).append(
            '<tr class="useremail"><td><input class="todelete" type="checkbox" value=""></input></td>    <td><input hidden="true" type="text" value="' + contacts[i].name + '" name="emails[' + x + '].name"/><div>' + contacts[i].name + '</div></td>    <td><input hidden="true" type="text" value="'
            + contacts[i].surname + '" name="emails[' + x + '].surname"/><div>' + contacts[i].surname + '</div></td>    <td><input hidden="true" type="text" value="' + contacts[i].email + '" name="emails[' + x + '].email"/><div>' + contacts[i].email + '</div></td></tr>'); // add
        // input
        // box
        x++; // text box increment
    }
}



$(document).ready(function () {

    // var x = 1; // initlal text box count

    $(delete_button).click(function (e) {
        e.preventDefault();
        $("tr.useremail").each(function () {
            $this = $(this)
            if ($(this).find("input.todelete").is(":checked")) {
                $(this).remove();
            }
        });
    });
    $(add_button).click(function (e) { // on add input
        // button click
        e.preventDefault();
        addParticipants(new_email.val());
        new_email.val("");
    });

    $(new_email).on('keyup', function (e) {
        e.preventDefault();
        console.log("HIIHIHIHIHI" + e.keyCode);

        if (e.keyCode == 13) {
            addParticipants(new_email.val());
            new_email.val("");
        }
    });

    $(wrapper).on("click", ".remove_field", function (e) { // user click on
        // remove
        // text
        e.preventDefault();
        $(this).parent('div').remove();
        x--;
    });
    $('#submit-participants').click(function () {
        addParticipants($('#participants_bulk_ta').val());
        $('#participants_bulk_ta').val("");
    });
    $('#createMeetingForm').on('submit', function (e) { // use on if jQuery
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
    $('#createMeetingForm').on('keyup keypress', function (e) {
        var keyCode = e.keyCode || e.which;
        if (keyCode === 13) {
            e.preventDefault();
            return false;
        }
    });

    $('#datepicker').datepicker({
        format: "mm-dd-yyyy",
        orientation: "auto bottom",
        todayHighlight: "true",
        todayBtn: "true",
        autoclose: "true"
    });


    var substringMatcher = function(strs) {
        return function findMatches(q, cb) {
            var matches, substringRegex;

            // an array that will be populated with substring matches
            matches = [];

            // regex used to determine if a string contains the substring `q`
            substrRegex = new RegExp(q, 'i');

            // iterate through the pool of strings and for any string that
            // contains the substring `q`, add it to the `matches` array
            $.each(strs, function(i, str) {
                if (substrRegex.test(str)) {
                    matches.push(str);
                }
            });

            cb(matches);
        };
    };

    $.post( "/contacts", function( data ) {
        var emails = data;

        $('.typeahead').typeahead(
            {
                hint: true,
                highlight: true,
                minLength: 1
            },
            {
                name: 'emails',
                source: substringMatcher(emails)
            }
        );
    });
    $('.typeahead').on('typeahead:selected', function(evt, item) {
        console.log(item);
        $.post( "/contact?email=" + item, function( data ) {
            var name_array = data.full_name.split(' ');
            addTCParticipant(item, name_array[0], typeof name_array[1] === 'undefined' ? '' : name_array[1]);
            $('.typeahead').typeahead('val', '');
        });
        // do what you want with the item here

        //$('#tc_email').val("");


    })





});