/**
 * Created by udantha on 10/28/16.
 */
var Booking = {

    init: function () {
        var self = this;

        $(document).on('submit', '#bookingForm', function (event) {
            event.preventDefault();
            event.stopPropagation();

            self._processBooking($(this));
            return false;
        });
    },

    _processBooking: function (oForm) {
        //validate
        if(SeatLayout.getSelectedSeatCount()<1){
            Alert.showError("Select a seat number to proceed.");
            return;
        }

        PreLoader.activate();
        var sessionId = window.SearchConfig ? window.SearchConfig.sessionId:null;
        var pData = 'sessionId='+ sessionId +'&'+ oForm.serialize()
        $.ajax({
            type: "POST",
            url: '/legacy/ticketbox/placebooking',
            data: pData,
            success: function (data) {
                if(data && data.result){
                    $('#booking-success-curtain').show();
                }else{
                    Alert.showError(data.error? data.error : 'Error in saving.');
                }
            },
            complete: function () {
                PreLoader.deactivate();
            },
            dataType: 'json'
        });
    }
};

$(function () {
    Booking.init();
});