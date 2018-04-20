
var IndexPage = function() {

    var handleFrame = function() {
        var iframe = document.getElementById("vcbframe");
        var pre = document.getElementById("preloader");

        pre.innerHTML = '<img src="/webapp/static/img/ajax-loader.gif" />';

        var unload = function(){
            pre.innerHTML = '<img src="/webapp/static/img/ajax-loader.gif" />';
        };

        var load = function(){
            pre.innerHTML = '';
            iframe.contentWindow.onbeforeunload = unload;
            iframe.onload = load;
        };

        $.get("/app/vcb-link", function(link) {
            iframe.onload = load;
            iframe.src = link;
        });
    };

    var handleUI = function() {
        $(".pop").popover({ trigger: "manual" , html: true, animation:false})
            .on("mouseenter", function () {
                var _this = this;
                $(this).popover("show");
                $(".popover").on("mouseleave", function () {
                    $(_this).popover('hide');
                });
            }).on("mouseleave", function () {
            var _this = this;
            setTimeout(function () {
                if (!$(".popover:hover").length) {
                    $(_this).popover("hide");
                }
            }, 300);
        });
    };

    var ajaxDstatus = function() {
        $.get("/app/vcb-status", function(status) {
            $('#meeting-status').removeClass('Pending').removeClass('Running').removeClass('Expired').removeClass('Completed').removeClass('Cancel').removeClass('Error');
            $('#meeting-status').addClass(status);
            $('#meeting-status').html(status);
        });
    };

    return {
        init: function () {
            handleFrame();
            handleUI();
        },
        status: function() {
            ajaxDstatus();
        }
    }
}();

$(document).ready(function(){
    IndexPage.init();

    setInterval("IndexPage.status()", 1000);
});