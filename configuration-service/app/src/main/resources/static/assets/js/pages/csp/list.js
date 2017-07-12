function queryParams() {
    return {
        //type: ['name', 'domain'],
        sort: 'name',
        direction: 'asc',
        per_page: 10,
        page: 1
    };
}

$(document).ready(function() {

    $('body').on('click', 'a.csp-delete', function(e) {
        e.preventDefault();
        var cspId = $(this).attr('data-csp-id');
        BootstrapDialog.confirm({
            title: 'Confirmation',
            size: BootstrapDialog.SIZE_SMALL,
            message: 'Are you sure to delete CSP:<br><strong>' + cspId + '</strong><br> and its related information?',
            type: BootstrapDialog.TYPE_WARNING,
            closable: false,
            draggable: true,
            btnCancelLabel: 'Cancel', // <-- Default value is 'Cancel',
            btnOKLabel: 'Confirm',
            btnOKClass: 'btn-warning', // <-- If you didn't specify it, dialog type will be used,
            callback: function(result) {
                // result will be true if button was click, while it will be false if users close the dialog directly.
                if(result) {
                    $.ajax({
                        type: 'POST',
                        url: REMOVE_URL + "/" + cspId,
                        processData: false,
                        contentType:"application/json; charset=utf-8",
                        dataType:"json",
                        success: function (response) {
                            console.log(response);

                            if (response.responseCode === 0) {
                                // $('#csp-form').find('input, textarea, button, select').val('');
                                // $('#csp-form').find('input, textarea, button, select').attr('disabled', 'disabled');
                                // $('#result').html('<div class="alert alert-dismissable alert-success"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a>'+response.responseText+'</div>');
                                setTimeout(function () {
                                    $('#csp-table').bootstrapTable('refresh');
                                }, 1000);
                            }
                            else {
                                BootstrapDialog.show({
                                    title: 'Error',
                                    type: BootstrapDialog.TYPE_DANGER,
                                    size: BootstrapDialog.SIZE_SMALL,
                                    message: response.responseText,
                                    closable: true,
                                    closeByBackdrop: false,
                                    closeByKeyboard: false,
                                    buttons: [{
                                        label: 'Close',
                                        action: function(dialogRef){
                                            dialogRef.close();
                                        }
                                    }]
                                });
                            }
                        }
                    });
                }else {
                    //just close
                }
            }
        });
    });

});