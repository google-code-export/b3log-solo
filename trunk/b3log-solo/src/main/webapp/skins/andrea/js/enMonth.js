var getEnMonth = function(iMonth){
    /**
     * 1 January, Jan
     * 2 Feburary, Feb
     * 3 March, Mar
     * 4 April, Apr
     * 5 May, May
     * 6 June, Jun
     * 7 July, Jul
     * 8 August, Aug
     * 9 September, Sep
     * 10 October, Oct
     * 11 November, Nov
     * 12 December, Dec
     */
    iMonth = parseInt($.trim(iMonth));
    switch (iMonth){
        case 1:
            return "Jan";
        case 2:
            return "Feb";
        case 3:
            return "Mar";
        case 4:
            return "Apr";
        case 5:
            return "May";
        case 6:
            return "Jun";
        case 7:
            return "Jul";
        case 8:
            return "Aug";
        case 9:
            return "Sep";
        case 10:
            return "Oct";
        case 11:
            return "Nov";
        case 12:
            return "Dec";
        default:
            return "Err";
    }
}

$(document).ready(function(){
    $(".month").each(function(i,n){
        n.innerHTML = getEnMonth(n.innerHTML);
    });
});