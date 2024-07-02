function splitUrlParameter(aUrl, key, value) {
    // return aUrl.indexOf('?') > -1 ? aUrl + '&' + key + '=' + value : aUrl + '?' + key + '=' + value;
    // url1或url2里如果有$，再根据是否有参数做处理
    var str = aUrl; //取得整个地址栏
    var num = str.indexOf("?");
    str = str.substr(0,num);
    str = str + "?";
    var parameterObj = urlSearch(aUrl);
    Object.keys(parameterObj).forEach(function(key){
        var val = parameterObj[key];
        if (val){
            str = str + key + "=" + val + "&";
        }
    });

    if(str.indexOf(key) > -1){
        return str;
    }

    if (str.indexOf('$') > -1) {
        if (str.indexOf('=') > -1) {
            return str + '&' + key + '=' + value;
        }
        else {
            return str + key + '=' + value;
        }
    }
    if (str.indexOf('?') > -1) {
        return str + '&' + key + '=' + value;
    }
    if (str.indexOf('$') === -1 && str.indexOf('?') === -1) {
        return str + '?' + key + '=' + value;
    }
}

function urlSearch(url) {
    var obj = {};
    var name, value;
    var str = url; //取得整个地址栏
    var num = str.indexOf("?");
    str = str.substr(num + 1); //取得所有参数   stringvar.substr(start [, length ]

    var arr = str.split("&"); //各个参数放到数组里
    for (var i = 0; i < arr.length; i++) {
        num = arr[i].indexOf("=");
        if (num > 0) {
            name = arr[i].substring(0, num);
            value = arr[i].substr(num + 1);
            obj[name] = value;
        }
    }
    return obj;
}
