$(document).ready(function() {

    /*
     UI handlers
     */
    $('#refresh-control').checkboxpicker({});
    $('#refresh-control').prop('checked', true);

    $('#to-top').click(function() {
        var scrollPos = $('#row1').position().top; // use the text of the span to create an ID and get the top position of that element
        $('#scroll').animate({ // animate your right div
            scrollTop: scrollPos // to the position of the target
        }, 800);
    });
    $('#to-bottom').click(function(){
        var count = $("#scroll p").length;

        var scrollPos = $('#row' + count).position().top; // use the text of the span to create an ID and get the top position of that element
        $('#scroll').animate({ // animate your right div
            scrollTop: scrollPos // to the position of the target
        }, 800);
    });
    $('#refresh').click(function(){
        var v = $('#refresh-control').is(':checked');
        $('#refresh-control').prop('checked', true);
        ajaxd_status();
        $('#refresh-control').prop('checked', v);
    });

    ajaxd_status();
    var timer = setInterval("ajaxd_status()", REFRESH_INTERVAL);

});

function ajaxd_status() {
    if ($('#refresh-control').is(':checked') == false) return;

    $('div.error-log > img').show();
    $.get(LOG_URL, "", function(data, textStatus) {
        //data contains the JSON object
        //textStatus contains the status: success, error, etc
        if ( textStatus === 'success' ) {
            console.log(data);
            $('div.error-log > div').empty();
            $('div.error-log > img').hide();
            var i = 0;
            $.each(data, function(id, item){
                var type = item.gender;
                var timestamp = '<span class="label label-default">'+item.registered+'</span>&nbsp;';
                var message = item.greeting;
                i++;
                var h = '<p>' +
                    timestamp +
                    '<span id="row'+i+'" class="label label-info">'+type+'</span>&nbsp;'
                    + message +
                    '</p>';
                $('div.error-log > div').append(h);
            });
        }
    }, "json");
};