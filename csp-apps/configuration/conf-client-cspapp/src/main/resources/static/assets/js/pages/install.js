$(document).ready(function() {

    /*
     UI hiding
     */

    $('#reginfo').hide();
    $('#certinfo').show();

    /*
     UI handlers
     */
    $("#ca_bundle").fileinput({
        browseClass: "btn btn-default",
        showCaption: true,
        showRemove: true,
        showUpload: false,
        showPreview: false,
        allowedFileExtensions: ['crt']
    });

    /*
     UI handlers
     */
    $("#ssl_priv_key").fileinput({
        browseClass: "btn btn-default",
        showCaption: true,
        showRemove: true,
        showUpload: false,
        showPreview: false,
        allowedFileExtensions: ['key']
    });

    /*
     UI handlers
     */
    $("#ssl_pub_key").fileinput({
        browseClass: "btn btn-default",
        showCaption: true,
        showRemove: true,
        showUpload: false,
        showPreview: false,
        allowedFileExtensions: ['crt']
    });






    $('body').on('click', '.btn-action', function(e) {
        e.preventDefault();
    });
    $('.btn-add').trigger('click');

    //8-4-4-4-12
    //$('.uuid').mask("00000000-0000-0000-0000-000000000000", {placeholder: "________-____-____-____-____________"});

    //099.099.099.099
    $('.ip').mask("099.099.099.099", {placeholder: "___.___.___.___"});



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

    var filesForm = $("#csp-certs");
    filesForm.submit(function (e) {
        e.preventDefault();
        var formData = new FormData(this);

        $.ajax({
            url: POSTFILES_URL + "/" + $('#csp_id').val(),
            data: formData,
            cache: false,
            processData: false,
            contentType: false,
            type: 'POST',
            success: function(response){
                console.log(response);
                var files = response.responseText;

                if (response.responseCode === 0) {
                    $('#csp-form').find('#files').val(response.responseText);
                    console.log('set files array correctly.');
                    $('#reginfo').show();
                    $('#certinfo').hide();

                }
            }
        });

    });


    var form = $("#csp-form");
    form.validate({
        rules: {
            csp_id: {
                required: true
            },
            csp_name: {
                required: true
            },
            csp_domain_name: {
                required: true
            },
            "csp_contact_name[]": {
                required: true,
                require_from_group: [$(".csp_contact_name").length, ".csp_contact_name"]
            },
            "csp_contact_email[]": {
                required: true,
                email: true,
                require_from_group: [$(".csp_contact_email").length, ".csp_contact_email"]
            },
            "csp_contact_type[]": {
                required: true,
                require_from_group: [$(".csp_contact_type").length, ".csp_contact_type"]
            },
            "csp_internal_ip[]": {
                required: true
            },
            "csp_external_ip[]": {
                required: true
            },
            ca_bundle: {
                required: true
            },
            ssl_priv_key: {
                required: true
            },
            ssl_pub_key: {
                required: true
            }
        }
    });
    form.submit(function (e) {
        e.preventDefault();

        if (form.valid()) {
            $('button.save.btn-success').attr('disabled', 'disabled');
            var formData = JSON.stringify($('#csp-form').serializeObject());
            $.ajax({
                type: 'POST',
                url: POST_URL + "/" + $('#csp_id').val(),
                data: formData,
                processData: false,
                contentType:"application/json; charset=utf-8",
                dataType:"json",
                success: function (response) {
                    if (response.responseCode === 0) {
                        $('#csp-form').find('input, textarea, button, select').val('');
                        $('#csp-form').find('input, textarea, button, select').attr('disabled', 'disabled');
                        $('#result').html('<div class="alert alert-dismissable alert-success"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a>'+response.responseText+'</div>');
                        setTimeout(function () {
                            window.location = REDIRECT_URL;
                        }, 1000);
                    }
                    else {
                        $('#result').html('<div class="alert alert-dismissable alert-danger"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a><strong>Error: '+xhr.responseJSON.responseCode+'</strong><br>'+xhr.responseJSON.responseText+'<br>'+xhr.responseJSON.responseException+'</div>');
                    }
                },
                error:function (xhr, ajaxOptions, thrownError){
                    $('#result').html('<div class="alert alert-dismissable alert-danger"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a><strong>Error: '+xhr.responseJSON.status+'</strong><br>'+xhr.responseJSON.error+'<br>'+xhr.responseJSON.message+'</div>');
                    $('button.save.btn-success').removeAttr('disabled');
                }
            });
        }

    });
});