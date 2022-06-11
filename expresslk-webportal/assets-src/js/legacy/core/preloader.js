/**
 * Created by udantha on 10/28/16.
 */
var PreLoader = {

    oContainer: null,

    init: function () {
        this.oContainer = $('<div class="main-preloader" style="display: none;"></div>').appendTo('body');
    },

    /**
     * Activate preloader
     */
    activate: function () {
        this.oContainer.show();
    },

    /**
     * Hide preloader
     */
    deactivate: function () {
        this.oContainer.hide();
    }
};

$(function () {
    PreLoader.init();
});