$(document).ready(function(){
    //099.099.099.099
    $('.v').mask("0.0.000", {placeholder: "_._.___"});

    $('.checkbox-app').checkboxpicker({
        html: true,
        offLabel: '<span class="glyphicon glyphicon-remove">',
        onLabel: '<span class="glyphicon glyphicon-ok">'
    });

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
});