function queryParams() {
    return {
        //type: ['name', 'domain'],
        sort: 'name',
        direction: 'asc',
        per_page: 10,
        page: 1
    };
}

$(document).ready(function(){
    $('a.disabled').on('click', function(e){
        e.preventDefault();
    });

    $('body').on('click', 'a.module-version-delete', function(e) {
        e.preventDefault();
        var moduleVersionId = $(this).attr('data-module-version-id');
        var moduleVersionName = $(this).attr('data-module-version-name');
        BootstrapDialog.confirm({
            title: 'Confirmation',
            size: BootstrapDialog.SIZE_NORMAL,
            message: 'Are you sure to delete Module Version: <strong>' + moduleVersionName + '</strong> and its related information?',
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
                        url: REMOVE_URL + "/" + moduleVersionId,
                        processData: false,
                        contentType:"application/json; charset=utf-8",
                        dataType:"json",
                        success: function (response) {
                            console.log(response);

                            if (response.responseCode === 0) {
                                setTimeout(function () {
                                    $('#module-version-table').bootstrapTable('refresh');
                                }, 100);
                            }
                            else {
                                BootstrapDialog.show({
                                    title: 'Error',
                                    type: BootstrapDialog.TYPE_DANGER,
                                    size: BootstrapDialog.SIZE_NORMAL,
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