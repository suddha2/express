/**
 * Created by udantha on 9/16/16.
 */

$(function () {
    $( ".attach-dp" ).datepicker({
        dateFormat: 'yy-mm-dd',
        minDate: new Date(),
        maxDate: "+2m"
    });
    $('#trigger-search-dp').on('click', function () {
        $(this).closest('div').find('.attach-dp').datepicker("show");
    });

    //attach combo box
    $(".city-dropdown").combobox();

    //disable after submit
    $('#tb-search-scehdule').on('submit', function () {
        //disable submit and show preloader
        $('#btn-search-schedules')
            .addClass('btn-loading');
    })

});