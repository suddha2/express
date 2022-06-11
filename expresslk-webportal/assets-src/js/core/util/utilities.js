/**
 * Created by udantha on 1/16/15.
 */
var Util = {

    /**
     * Detect if the environment is mobile or not
     * @returns {boolean}
     */
    isMobile: function(){
        return (window.innerWidth <= 800);
    },

    /**
     *
     * @param startTime Date object of the start time
     * @param endTime Date object of the end time
     * @returns {[]} [ hours, minutes, seconds] array of Hours minutes and seconds
     */
    getTimeDifferenceBetween: function(startTime, endTime){
        var ms = moment(endTime).diff(moment(startTime));
        var d = moment.duration(ms);
        var s = Math.floor(d.asHours()) + moment.utc(ms).format(":mm:ss");
        return s.split(':');
    },

    /**
     * Return the formatted date for search
     * @returns {string}
     */
    getSearchDefaultDate: function () {
        //return moment().add(1, 'days').format('YYYY-MM-DD');
        return moment().format('YYYY-MM-DD');
    }
};