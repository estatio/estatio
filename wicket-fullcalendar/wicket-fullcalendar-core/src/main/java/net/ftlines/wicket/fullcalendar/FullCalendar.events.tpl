function(start, end, callback) {

    Wicket.Ajax.ajax({
        "u": "${url}",
        "dt": "json",
        "wr":  false,
        "ep": {
            "start": Math.round(start.getTime()),
            "end": Math.round(end.getTime()),
            "timezoneOffset": new Date().getTimezoneOffset(),
            "anticache": ""+new Date().getTime()+"."+Math.random()
        },
        "sh": [function(data, textStatus, jqXHR, attrs) { callback(jqXHR) }]
    });
}