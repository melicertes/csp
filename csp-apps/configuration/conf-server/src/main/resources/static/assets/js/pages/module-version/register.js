$(document).ready(function(){

    /*
    UI handlers
     */
    $("#module_file").fileinput({
        browseClass: "btn btn-default",
        showCaption: true,
        showRemove: true,
        showUpload: false,
        showPreview: false,
        allowedFileExtensions: ['zip']
    });

    //099.099.099.099
    $('.v').mask("0.0.000", {placeholder: "_._.___"});



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
    var form = $("#module-version-form");
    form.validate({
        rules: {
            module_full_name: {
                required: true
            },
            module_version: {
                required: true
            },
            module_file: {
                required: true,
                extension: "zip"
            },
            module_description: {
                required: true
            }
        }
    });
    form.submit(function (e) {
        e.preventDefault();

        if (form.valid()) {
            $('button.save.btn-success').attr('disabled', 'disabled');
            var f = document.getElementById('module-version-form');
            var formData = new FormData(f);

            $.ajax({
                type: 'POST',
                url: POST_URL,
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    if (response.responseCode === 0) {
                        $('#module-version-form').find('input, textarea, button, select').val('');
                        $('#module-version-form').find('input, textarea, button, select').attr('disabled', 'disabled');
                        $('#result').html('<div class="alert alert-dismissable alert-success"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a>'+response.responseText+'</div>');
                        setTimeout(function () {
                            window.location = REDIRECT_URL;
                        }, 100);
                    }
                    else {
                        $('#result').html('<div class="alert alert-dismissable alert-danger"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a><strong>Error: '+xhr.responseJSON.responseCode+'</strong><br>'+xhr.responseJSON.responseText+'<br>'+xhr.responseJSON.responseException+'</div>');
                    }
                },
                error:function (xhr, ajaxOptions, thrownError){
                    $('#result').html('<div class="alert alert-dismissable alert-danger"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a><strong>Error: '+xhr.responseJSON.responseCode+'</strong><br>'+xhr.responseJSON.responseText+'<br>'+xhr.responseJSON.responseException+'</div>');
                }
            });
        }

    });


});