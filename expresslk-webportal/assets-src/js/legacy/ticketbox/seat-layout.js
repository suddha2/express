/**
 * Created by udantha on 10/25/16.
 */
var SeatLayout = {

    aSelectedSeats: {
        seats: [],
        seatNos: []
    },
    oCurrentSchedule: {},
    oSeatLayoutHolder: null,

    init: function () {
        var self = this;
        //initialize event trigers, bindings
        $(document).on('click', '.show-seatlayout', function () {
            var oBtn = $(this);
            //trigger pre-loader
            oBtn.addClass('btn-loading');
            var data = $(this).data();
            self.loadSeatLayout({
                boardingLocation: data.boarding,
                dropoffLocation: data.drop,
                busType: data.bustype,
                scheduleId: data.scid
            }, function () {
                //loaded
                oBtn.removeClass('btn-loading');
            });
        });

        this.oSeatLayoutHolder = $('#seatLayoutPlaceholder');
    },

    /**
     * get number of current selected seats
     * @returns {Number}
     */
    getSelectedSeatCount: function () {
        return (this.aSelectedSeats.seats.length);
    },

    loadSeatLayout: function (params, fnComplete) {
        var self = this,
            scheduleId = params.scheduleId;
        //request initiated
        self.oSeatLayoutHolder.html('');
        //set session id
        params.sessionId = window.SearchConfig ? window.SearchConfig.sessionId:null;
        //get data
        $.post('/legacy/ticketbox/seatinfo', params, function (data) {
            if(data && data.result){
                //re-set selected seats
                self.aSelectedSeats = {
                    seats: [],
                    seatNos: [],
                    amount: 0,
                    baseMarkups: 0,
                    baseDiscount: 0,
                    baseTax: 0,
                    baseCharges: 0,
                    total: 0
                };
                //set current schedule
                self.oCurrentSchedule = window.SearchResult.hasOwnProperty(scheduleId)? window.SearchResult[scheduleId] : {};
                //throw error if data is not available
                if(!window.SearchResult.hasOwnProperty(scheduleId)){
                    Alert.showError("Search data is not available. Please re load the browser.");
                    return;
                }

                //set seat layout
                var sLayout = $('#seatLayout').html(),
                    oReplaceHtml = {};

                //get seat layout paramters
                var oSeatParams = self._generateSeatProfile(data.result.seats);
                $.extend(oReplaceHtml, oSeatParams.tmplHtml);
                //get boarding dropoff locations
                var oBoardDropParams = self._populateBoardingDropLocations();
                $.extend(oReplaceHtml, oBoardDropParams.tmplHtml);

                //compile html
                var sCompiled = Tmpl.compile(sLayout, oReplaceHtml);
                //append to DOM
                self.oSeatLayoutHolder.html(sCompiled);

                //replace DOM objects
                self.oSeatLayoutHolder.find('._seatlayout_target')
                    .replaceWith(oSeatParams.tmplDom);

                //scroll down
                $('html,body').animate({
                    scrollTop: self.oSeatLayoutHolder.offset().top
                });
            }else{
                Alert.showError(data.error? data.error : 'Error loading seat layout.');
            }
            fnComplete.call(this);
        }, 'json');
    },

    /**
     * Generate seats
     * @param seatInfo
     * @returns {{tmplDom: Array, tmplHtml: {seatTotX: number, seatTotY: number}}}
     * @private
     */
    _generateSeatProfile: function (seatInfo) {
        var self = this;
        var seatSize = 40,
            x = 0,
            y = 0,
            seatCollection = [];
        //loop through seats
        for(var i in seatInfo){
            var seat = seatInfo[i],
                xSize = seat['x']*seatSize,
                ySize = seat['y']*seatSize;
            //get seat class
            var seatClass = '';
            if(!seat.available){
                seatClass = 'seat-na';
            }else if(seat.booked){
                seatClass = 'seat-booked';
            }else if(seat.booking_status=='pay_at_bus'){
                seatClass = 'seat-payatbus';
            }else if(seat.booking_status=='tentative'){
                seatClass = 'seat-tentative';
            }
            var seatStyle = 'right: '+ ySize +'px; top: '+ xSize +'px; ';
            //get the maximum x and y to calculate total size
            x = seat['x']>x ? seat['x'] : x;
            y = seat['y']>y ? seat['y'] : y;
            //set gender if available
            var gender = '';
            if(seat['gender']==='Female'){
                gender = '<i class="fa fa-female morf morf-f"></i>';
            }else if(seat['gender']==='Male'){
                gender = '<i class="fa fa-male morf morf-m"></i>';
            }

            //build seat
            var oSeatObj = $('<div class="busseat-arrangement-item seat-available '+ seatClass +'" style="'+ seatStyle +'">' +
                '<span class="busseat-name">'+ seat['number'] +'</span>' +
                gender +
                '</div>');
            //save seat data
            oSeatObj.data('seatinfo', seat);
            oSeatObj.on('click', function () {
                self._seatSelection($(this));
            });
            seatCollection.push(oSeatObj);
        }
        var totX = (y + 1) * seatSize,
            totY = (x + 1) * seatSize;

        return {
            tmplDom: seatCollection,
            tmplHtml: $.extend(
                { seatTotX: totX, seatTotY: totY },
                self.oCurrentSchedule
            )
        };
    },

    _seatSelection: function($oSeat){
        var self = this;
        var seat = $oSeat.data('seatinfo');
        //if seat is booked or not available, break
        if(seat['available']!==true || seat['booked']===true){
            return;
        }

        var seatNo = seat['number'],
            seatId = seat['id'];
        //check if seat exists in the array
        //var arrayIndex = this.aSelectedSeats.seats.indexOf( seatId );
        var arrayIndex = $.inArray(seatId, self.aSelectedSeats.seats);
        if(arrayIndex > -1){
            //remove element from array
            this.aSelectedSeats.seats.splice(arrayIndex, 1);
            //remove face value
            this.aSelectedSeats.seatNos.splice(arrayIndex, 1);
            //deduct price
            this.aSelectedSeats.amount          -= parseFloat(self.oCurrentSchedule.price.fare);
            this.aSelectedSeats.baseMarkups     -= parseFloat(self.oCurrentSchedule.price.baseMarkups);
            this.aSelectedSeats.baseDiscount    -= parseFloat(self.oCurrentSchedule.price.baseDiscount);
            this.aSelectedSeats.baseTax         -= parseFloat(self.oCurrentSchedule.price.baseTax);
            this.aSelectedSeats.baseCharges     -= parseFloat(self.oCurrentSchedule.price.baseCharges);
            this.aSelectedSeats.total           -= parseFloat(self.oCurrentSchedule.price.total);
            $oSeat.removeClass('seat-selected');
            //remove passenger row
            $('#slPassengerRow-'+ seatNo).remove();

        }else{

            //if selected is more than allowed
            if(this.aSelectedSeats.seats.length > 8){
                Alert.showError("You are not allowed to book so many seats :-)");
                return;
            }

            //add to the array
            this.aSelectedSeats.seats.push(seatId);
            this.aSelectedSeats.seatNos.push(seatNo);

            this.aSelectedSeats.amount          += parseFloat(self.oCurrentSchedule.price.fare);
            this.aSelectedSeats.baseMarkups     += parseFloat(self.oCurrentSchedule.price.baseMarkups);
            this.aSelectedSeats.baseDiscount    += parseFloat(self.oCurrentSchedule.price.baseDiscount);
            this.aSelectedSeats.baseTax         += parseFloat(self.oCurrentSchedule.price.baseTax);
            this.aSelectedSeats.baseCharges     += parseFloat(self.oCurrentSchedule.price.baseCharges);
            this.aSelectedSeats.total           += parseFloat(self.oCurrentSchedule.price.total);
            $oSeat.addClass('seat-selected');

            //add new passenger row
            self._addPassengerRow(seatNo);
        }
        //trigger a redraw of seat info
        this._setBusData();
    },

    _addPassengerRow: function (seatNo) {
        var oPassContainer = $('#seatinfoPassengers');
        var sTemplate = $('#seatLayout-passengerRow').html();

        var sCompiledRow = Tmpl.compile(sTemplate, {
            seatNumber: seatNo
        });
        //append row
        oPassContainer.append(sCompiledRow);
    },

    /**
     *
     * @returns {{tmplHtml: {dropList: *, boardList: *}}}
     * @private
     */
    _populateBoardingDropLocations: function () {
        var self = this;
        var aBoarding = self.oCurrentSchedule.boardingLocations,
            aDropOff = self.oCurrentSchedule.dropoffLocations;

        function generateOptions(oPlaces) {
            var html = '';
            for (var i in oPlaces){
                html += '<option value="'+ oPlaces[i].id +'">'+ oPlaces[i].name +'</option>';
            }
            return html;
        }

        return {
            tmplHtml: {
                dropList: generateOptions(aDropOff),
                boardList: generateOptions(aBoarding)
            }
        };
    },

    _setBusData: function () {
        var oReplaceData = $.extend({}, this.aSelectedSeats, { seatCount: this.aSelectedSeats.seats.length });
        //find replacables
        for (var key in oReplaceData){
            var obj = this.oSeatLayoutHolder.find('.tmplReplace-'+ key);
            if(obj.length>0){
                obj.html( oReplaceData[key] );
            }
        }
    }
};

$(function () {
    SeatLayout.init();
});