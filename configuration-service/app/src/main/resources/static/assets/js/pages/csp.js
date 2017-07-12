$(document).ready(function() {

    /*
     UI handlers
     */
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
    var form = $("#csp-form");
    form.validate({
        rules: {
            csp_id: {
                required: true
            },
            /*
            module_full_name: {
                required: true
            },
            module_version: {
                required: true
            },
            module_priority: {
                required: true
            },
            module_file: {
                required: true,
                extension: "zip"
            }
            */
        }
    });
    form.submit(function (e) {
        e.preventDefault();

        if (form.valid()) {
            var f = document.getElementById('csp-form');
            //var formData = new FormData(f);
            //var formData = JSON.stringify(new FormData(f))
            //var formData = $("#csp-form").serialize();
            var formData = $("#csp-form").serializeObject();
            var data = {};
            var formData = $("#csp-form").serializeArray().map(function(x){data[x.name] = x.value;});

            ///WORKS..................
            var formData = JSON.stringify($('#csp-form').serializeObject())


console.log(formData);
            $.ajax({
                type: 'POST',
                url: POST_URL,
                data: formData,
                processData: false,
                contentType:"application/json; charset=utf-8",
                dataType:"json",
                success: function (response) {
                    console.log(response);

                    if (response.responseCode === 0) {
                        $('#csp-form').find('input, textarea, button, select').val('');
                        $('#csp-form').find('input, textarea, button, select').attr('disabled', 'disabled');
                        $('#result').html('<div class="alert alert-dismissable alert-success"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a>'+response.responseText+'</div>');
                        setTimeout(function () {
                            window.location = REDIRECT_URL;
                        }, 1000);
                    }
                    else {
                        $('#result').html('<div class="alert alert-dismissable alert-danger"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a><strong>'+response.responseText+'</strong><br>'+response.responseException+'</div>');
                    }
                }
            });
        }

    });
});