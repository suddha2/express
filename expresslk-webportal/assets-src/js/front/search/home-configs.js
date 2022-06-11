/**
 * Created by udantha on 7/15/15.
 * Configurations for home page
 */
$(function(){

    var tid = setInterval(tagline_vertical_slide, 2500),
        tagLis = $("#tagline ul li");
    // vertical slide
    function tagline_vertical_slide() {
        var curr = $("#tagline ul li.active");
        curr.removeClass("active").addClass("vs-out");
        setTimeout(function() {
            curr.removeClass("vs-out");
        }, 500);

        var nextTag = curr.next('li');
        if (!nextTag.length) {
            nextTag = $("#tagline ul li").first();
        }
        nextTag.addClass("active");
    }
});