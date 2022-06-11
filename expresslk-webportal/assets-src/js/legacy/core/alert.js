/**
 * Created by udantha on 10/25/16.
 */
var Alert = {
    oErrorContainer: null,
    oSuccessContainer: null,
    oTimer: null,

    init: function () {
        this.oSuccessContainer = $('<div class="alert alert-success common-success" style="display: none;">'+
                '<ul class="alert-target common-success-text"></ul>'+
            '</div>').appendTo('body');

        this.oErrorContainer = $('<div class="alert alert-danger common-error" style="display: none;" >'+
                '<ul class="alert-target common-error-text"></ul>'+
            '</div>').appendTo('body');
    },

    showError: function (error) {
        this._injectHtml(error, this.oErrorContainer);
    },

    showSuccess: function (success) {
        this._injectHtml(success, this.oSuccessContainer);
    },

    _injectHtml: function (message, container) {
        var aMsg = [],
            html = '';
        if($.type(message)==='boolean' || $.type(message)==='number' || $.type(message)==='string'){
            aMsg = [message];
        }else{
            aMsg = message;
        }
        for(var i in aMsg){
            html += '<li>'+ aMsg[i] +'</li>';
        }
        //inject
        container.find('.alert-target').html(html);
        container.show();
        //clear timer
        if(this.oTimer){
            clearTimeout(this.oTimer);
        }
        this.oTimer = setTimeout(function () {
            container.hide();
        }, 8000);
    }
};

$(function () {
    Alert.init();
});