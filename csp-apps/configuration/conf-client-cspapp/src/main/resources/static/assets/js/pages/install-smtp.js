$(document).ready(function() {

    /*
     UI hiding
     */

    $('#smtp').show();


    /*
     Validation
     */
    $.validator.setDefaults({
        highlight: function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        unhighlight: function(element) {
            $(element).closest('.form-group').removeClass('has-error');
        },
        errorElement: 'span',
        errorClass: 'help-block',
        errorPlacement: function(error, element) {
            if(element.parent('.input-group').length) {
                error.insertAfter(element.parent());
            } else if (element.attr('type') == 'file') {
                error.insertAfter(element.parent().parent().parent());
            } else {
                error.insertAfter(element);
            }
        }
    });

    var form = $("#csp-form");

    form.submit(function (e) {
        e.preventDefault();

        if (form.valid()) {

            BootstrapDialog.confirm({
                title: 'Confirmation',
                size: BootstrapDialog.SIZE_NORMAL,
                message: 'The information entered will now be used to <em>update the SMTP details used in sending outgoing emails</em>. ' +
                'You need to make sure <strong>the provided information is correct.</strong>. ' +
                'Note that SMTP details are only applied to the relevant CSP services on restart.' +
                'Please take time to review the provided data before continuing.',
                type: BootstrapDialog.TYPE_WARNING,
                closable: false,
                draggable: true,
                btnCancelLabel: 'Go back and review', // <-- Default value is 'Cancel',
                btnOKLabel: 'Save SMTP details',
                btnOKClass: 'btn-warning', // <-- If you didn't specify it, dialog type will be used,
                callback: function (result) {
                    // result will be true if button was click, while it will be false if users close the dialog directly.
                    if (result) {
                        $('button.save.btn-success').attr('disabled', 'disabled');
                        var formData = JSON.stringify($('#csp-form').serializeObject());
                        $.ajax({
                            type: 'POST',
                            url: POST_URL + "/" + $('#cspId').val(),
                            data: formData,
                            processData: false,
                            contentType: "application/json; charset=utf-8",
                            dataType: "json",
                            success: function (response) {
                                if (response.responseCode === 0) {
                                    $('#csp-form').find('input, textarea, button, select').val('');
                                    $('#csp-form').find('input, textarea, button, select').attr('disabled', 'disabled');
                                    $('#result').html('<div class="alert alert-dismissable alert-success"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a>' + response.responseText + '</div>');
                                    setTimeout(function () {
                                        window.location = REDIRECT_URL;
                                    }, 5000);
                                } else if (response.responseCode === 1) {
                                    $('#csp-form').find('input, textarea, button, select').val('');
                                    $('#csp-form').find('input, textarea, button, select').attr('disabled', 'disabled');
                                    $('#result').html('<div class="alert alert-dismissable alert-warning"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a>' + response.responseText + '</div>');
                                    setTimeout(function () {
                                        window.location = REDIRECT_URL;
                                    }, 10000);
                                } else {
                                    $('#result').html('<div class="alert alert-dismissable alert-danger"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a><strong>Error: ' + response.responseCode + '</strong><br>' + response.responseText + '<br>' + response.responseException + '</div>');
                                    $('button.save.btn-success').removeAttr('disabled');
                                    }
                            },
                            error: function (xhr, ajaxOptions, thrownError) {
                                $('#result').html('<div class="alert alert-dismissable alert-danger"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a><strong>Error: ' + response.status + '</strong><br>' + response.error + '<br>' + response.message + '</div>');
                                $('button.save.btn-success').removeAttr('disabled');
                            }
                        });
                    } else {

                    }
                }
            });
        }

    });
});


