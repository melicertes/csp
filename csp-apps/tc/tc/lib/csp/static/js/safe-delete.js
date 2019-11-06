(function ( $ ) {

    $.fn.safeDelete = function(options){
        var container = document.createElement('div');

        $(container).attr('class','signUp modal fade');
        $(container).attr('role','dialog');
        $(container).attr('aria-labelledby','modalSection');

        if(options === undefined) var options = {
            popupTitle : "Type DELETE then click the button",
            yesCallback : function(){},
            noCallback : function(){},
            safeText : "DELETE",
            closeOnSelection : true,
            deleteButton : "DELETE",
            cancelButton : "NEVERMIND"            
        };

        var closeOnSelection = options.closeOnSelection !== undefined ? options.closeOnSelection : true;
        if(options.popupTitle == undefined) options.popupTitle = 'Type DELETE then click the button';
        if(options.safeText == undefined) options.safeText = 'DELETE';
        if(options.deleteButton == undefined) options.deleteButton = 'DELETE';
        if(options.cancelButton == undefined) options.cancelButton = 'NEVERMIND';

        $(container).html('\
        <div class="modal-dialog">\
            <div class="modal-content">\
                <div class="modal-header">\
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>\
                    <h4 class="modal-title"></h4>\
                </div>\
                <div class="modal-body" style="padding-bottom:0px;">\
                    <input type="text" class="form-group form-control safe-delete-text" />\
                </div>\
                <div class="modal-footer">\
                    <button type="button" class="btn btn-danger pull-right safe-delete-yes"></button>\
                    <button type="button" class="btn btn-success pull-left safe-delete-no"></button>\
                </div>\
            </div>\
        </div>\
        ');

        var modalTitle = $(container).find('.modal-title').first();
        var safeText = $(container).find('.safe-delete-text').first();
        var safeYes = $(container).find('.safe-delete-yes').first();
        var safeNo = $(container).find('.safe-delete-no').first();

        $(safeYes).html(options.deleteButton);
        $(safeNo).html(options.cancelButton);

        $(safeText).val('');
        $(safeYes).attr('disabled','disabled');
        $(modalTitle).html(options.popupTitle);
        if(closeOnSelection) $(safeYes).off().on('click', function(){ $(container).modal('hide'); });
        if(closeOnSelection) $(safeNo).off().on('click', function(){ $(container).modal('hide'); });
        $(safeText).off().on('keyup', function(){
            if($(this).val() == options.safeText)
                $(safeYes).removeAttr('disabled');
            else
                $(safeYes).attr('disabled','disabled');
        });
        if(options.yesCallback !== undefined) $(safeYes).on('click', options.yesCallback);
        if(options.noCallback !== undefined) $(safeNo).on('click', options.noCallback);
        $(container).modal({show: true, backdrop: 'static'});

        return this;
    }

}( jQuery ));