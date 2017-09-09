$(document).ready(function(){

    $('.pie-chart2').easyPieChart({
        easing: 'easeOutBounce',
        size: 300,
        lineWidth: 36,
        lineCap: "square",
        barColor: "#fabf14",
        animate: 800,
        scaleColor: false,
        onStep: function(from, to, percent) {
            $(this.el).find('.percent').text(Math.round(percent)+'%');
        }
    });


    ajaxd_status();
    setInterval("ajaxd_status()", ASYNC_INTERVAL);

});

function ajaxd_status() {
    var chart = window.chart = $('.pie-chart2').data('easyPieChart');
    chart.update(Math.random()*100+1);
    /*
    $.ajax({
        type: "GET",
        url: "",
        success: function(data){
        }
    });
    */
}