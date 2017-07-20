$(document).ready(function(){
    //099.099.099.099
    $('.v').mask("0.0.000", {placeholder: "_._.___"});


    $('.checkbox-app').checkboxpicker({});

    $('.checkbox-app').on('change', function(){
        var $moduleId = $(this).attr('data-module-id');
        var $enabled = $(this).prop('checked');
        if ($enabled === true) {
            $('#module_set_version' + $moduleId).removeAttr('disabled');

        }
        else {
            $('#module_set_version' + $moduleId).attr('disabled', 'disabled');
        }
    });


    var form = $("#management-form");
    form.submit(function (e) {
        e.preventDefault();

        var hasError = false;

        //error checking
        $("input[id*='module_enable']").each(function(){
            var moduleId = $(this).attr('id').replace("module_enable", "");
            var enabled = $("#module_enable"+moduleId).prop("checked");
            if (enabled && $("#module_set_version"+moduleId).val() === "") {
                $("#module_set_version"+moduleId).attr("required");
                $("#module_set_version"+moduleId).parent().addClass("has-error");
                if ($("#module_set_version"+moduleId).parent().has("span").length == 0) {
                    $("#module_set_version"+moduleId).parent().append('<span class="help-block">This field is required.</span>');
                }
                hasError = true;
            }
            else {
                $("#module_set_version"+moduleId).parent().removeClass("has-error");
                $("#module_set_version"+moduleId).parent().children("span").remove();
            }
        });

        if (form.valid() && !hasError) {
            var formData = new Object();
            formData.cspId = $("#cspId").val();
            var modules = [];
            $("input[id*='module_enable']").each(function(){
                var moduleId = $(this).attr('id').replace("module_enable", "");

                var enabled = $("#module_enable"+moduleId).prop("checked");
                var shortName = $("#module_short_name"+moduleId).val();
                var installedVersion = $("#module_installed_version"+moduleId).val();
                var setVersion = $("#module_set_version"+moduleId).val();

                var row = new Object();
                row.moduleId = moduleId;
                row.enabled = enabled;
                row.shortName = shortName;
                row.installedVersion = installedVersion;
                row.setVersion = setVersion;
                modules.push(row);
            });

            formData.modules = modules;



            console.log(formData);
            console.log(JSON.stringify(formData));
            $.ajax({
                type: 'POST',
                url: POST_URL,
                data: JSON.stringify(formData),
                processData: false,
                contentType:"application/json; charset=utf-8",
                dataType:"json",
                success: function (response) {
                    console.log(response);

                    if (response.responseCode === 0) {
                        $('#management-form').find('input, textarea, button, select').val('');
                        $('#management-form').find('input, textarea, button, select').attr('disabled', 'disabled');
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