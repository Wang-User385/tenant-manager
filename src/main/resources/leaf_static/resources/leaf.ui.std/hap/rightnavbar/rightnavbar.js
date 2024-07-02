function display_common_button_click(){
    if(jQuery('#display_common_button_id img')[0].src.indexOf('hide-common')!=-1){
        jQuery(".hls-selectBar div.contain").each(function (i, obj) {
            if (jQuery(obj).context.id!='display_common_button_id') {
                jQuery(obj).css({
                    "display":"none"
                });
            }else{
                jQuery(obj).css({
                    "border-radius":"5px"
                });
            }
        });
        jQuery(".hls-selectBar").css('top','7%');
        jQuery('#display_common_button_id img')[0].src = jQuery('#display_common_button_id img')[0].src.replace('hide-common','display-common');
    }else{
        jQuery(".hls-selectBar div.contain").each(function (i, obj) {
            if (jQuery(obj).context.id!='display_common_button_id') {
                jQuery(obj).css({
                   "display":""
                });
            }else{
                jQuery(obj).css({
                    "border-top-right-radius": "0",
                    "border-top-left-radius": "0",
                    "border-bottom-left-radius": "15px",
                    "border-bottom-right-radius": "15px"
                });
            }
        });
        jQuery(".hls-selectBar").css('top','50%');
        jQuery('#display_common_button_id img')[0].src = jQuery('#display_common_button_id img')[0].src.replace('display-common','hide-common');
    }
}