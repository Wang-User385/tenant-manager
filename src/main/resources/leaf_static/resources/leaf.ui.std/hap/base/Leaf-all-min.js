/*
 * Leaf UI Library.
 * Copyright(c) 2010, Hand China Co.,Ltd.
 * 
 * http://www.hand-china.com
 */

/**
 * @class Leaf Leaf UI 核心工具类.
 * @author 牛佳庆
 * @singleton
 */

// 解决IE8 forEach兼容性问题
if (!Array.prototype.forEach) {
    Array.prototype.forEach = function forEach(callback, thisArg) {
        var T, k;
        if (this == null) {
            throw new TypeError("this is null or not defined");
        }
        var O = Object(this);
        var len = O.length >>> 0;
        if (typeof callback !== "function") {
            throw new TypeError(callback + " is not a function");
        }
        if (arguments.length > 1) {
            T = thisArg;
        }
        k = 0;
        while (k < len) {
            var kValue;
            if (k in O) {
                kValue = O[k];
                callback.call(T, kValue, k, O);
            }
            k++;
        }
    };
}


Ext.Ajax.timeout = 1800000;

$L = Leaf = {version: '1.0', revision: '$Rev:$'};
$jq = jQuery.noConflict();
// $L.firstFire = false;
$L.fireWindowResize = function () {
    if ($L.winWidth != $L.getViewportWidth() || $L.winHeight != $L.getViewportHeight()) {
        $L.winHeight = $L.getViewportHeight();
        $L.winWidth = $L.getViewportWidth();
        $L.Cover.resizeCover();
    }
}

$L.isDefined = function(value){
    if(Ext.isDefined(value)){
        return true;
    }else{
        return false;
    }
}

//浏览器类型
$L.BrowserType = function() {
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    var isOpera = userAgent.indexOf("Opera") > -1; //判断是否Opera浏览器
    // var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera; //判断是否IE浏览器
    var isIE=window.ActiveXObject || "ActiveXObject" in window
    // var isEdge = userAgent.indexOf("Windows NT 6.1; Trident/7.0;") > -1 && !isIE; //判断是否IE的Edge浏览器
    var isEdge = userAgent.indexOf("Edge") > -1; //判断是否IE的Edge浏览器
    var isFF = userAgent.indexOf("Firefox") > -1; //判断是否Firefox浏览器
    var isSafari = userAgent.indexOf("Safari") > -1 && userAgent.indexOf("Chrome") == -1; //判断是否Safari浏览器
    var isChrome = userAgent.indexOf("Chrome") > -1 && userAgent.indexOf("Safari") > -1&&!isEdge; //判断Chrome浏览器

    if (isIE)
    {
        var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
        reIE.test(userAgent);
        var fIEVersion = parseFloat(RegExp["$1"]);
        if(userAgent.indexOf('MSIE 6.0')!=-1){
            return "IE6";
        }else if(fIEVersion == 7)
        { return "IE7";}
        else if(fIEVersion == 8)
        { return "IE8";}
        else if(fIEVersion == 9)
        { return "IE9";}
        else if(fIEVersion == 10)
        { return "IE10";}
        else if(userAgent.toLowerCase().match(/rv:([\d.]+)\) like gecko/)){
            return "IE11";
        }
        else
        { return "0"}//IE版本过低
    }//isIE end

    if (isFF) { return "FF";}
    if (isOpera) { return "Opera";}
    if (isSafari) { return "Safari";}
    if (isChrome) { return "Chrome";}
    if (isEdge) { return "Edge";}
}
$L.isIE = function(){
    var type = $L.BrowserType();
    if(type.indexOf("IE") != -1 || type == 'Edge'){
        return true;
    }else{
        return false;
    }
}
/**
 * 内部页面body的padding值
 */
$L.usedHeight = 20;// -6
$L.usedWidth = 20;// -6
$L.minViewportWidth = 1000;
$L.minViewportHeight = 500;
$L.auWindow = new Ext.util.Observable();

if (Ext.isIE6) Ext.EventManager.on(window, "resize", $L.fireWindowResize, this);


$L.PARENT_DOMAIN = true;
try {
    parent.document
} catch (e) {
    $L.PARENT_DOMAIN = false;
}

$L.cache = {};
$L.cmps = {};
$L.onReady = function (fn, scope, options) {
    if (window['__host']) {
        if (!$L.loadEvent) $L.loadEvent = new Ext.util.Event();
        $L.loadEvent.addListener(fn, scope, options);
    } else {
        Ext.onReady(fn, scope, options);
    }
}// Ext.onReady;

$L.get = Ext.get;
// $L.focusWindow;
// $L.focusTab;
$L.defaultDateFormat = "isoDate";
$L.defaultDateTimeFormat = "yyyy-mm-dd HH:MM:ss";
$L.defaultChineseLength = 2;

/**
 * 页面地址重定向
 *
 * @param {String}
 *            url
 */
$L.go = function (url) {
    if (!url)return;
    var r = Math.random();
    window.location.href = url + (url.indexOf('?') == -1 ? '?' : '&') + '__r__=' + r;
}


/**
 * 对页面url参数进行加密处理
 *
 * @param {String}
 *            url
 */
//window.encryptOpen = function(url,tar,con){
//	var param = url.indexOf("?");
//	if(param>0){
//		var key = "1234567qwertyuiASDFGVFR8";
//		var oldUrl = url.split("?");
//		var arr = url.substr(param+1).split("&");
//		var str ="";
//		for(var i=0;i<arr.length;i++){
//			var num = arr[i].indexOf("=");
//			if(num>0){
//				var name = arr[i].substring(0,num);
//				var value = name + "=leaf-" + $L.encryptByDES(arr[i].substr(num+1),key);
//			}
//			if(str==""){
//				str += value;
//			}else{
//				str +="&" + value;
//			}
//		}
//		url = oldUrl[0] +"?"+ str;
//		window.open(url,tar,con);
//	}else{
//		window.open(url,tar,con);
//	}
//}
//
//$L.encryptByDES = function(message,key){
//	var keyHex = CryptoJS.enc.Utf8.parse(key);
//	var encrypted = CryptoJS.DES.encrypt(message, keyHex, {
//        mode: CryptoJS.mode.ECB,
//        padding: CryptoJS.pad.Pkcs7
//    });
//	return encodeURIComponent(encrypted.toString());
//}

/**
 * 将对象居中
 *
 * @param {Object/String}
 *            el Leaf组件对象或者是DOM对象或者是对象的ID字符串
 */
$L.center = function (el) {
    var ele;
    if (typeof(el) == "string") {
        var cmp = $L.CmpManager.get(el)
        if (cmp) {
            if (cmp.wrap) {
                ele = cmp.wrap;
            }
        } else {
            ele = Ext.get(el);
        }
    } else {
        ele = Ext.get(el);
    }
    var screenWidth = $L.getViewportWidth();
    var screenHeight = $L.getViewportHeight();
    var x = Math.max(0, (screenWidth - ele.getWidth()) / 2);
    var y = Math.max(0, (screenHeight - ele.getHeight()) / 2);
    ele.setStyle('position', 'absolute');
    ele.moveTo(x, y);
}

/**
 * 设置主题
 *
 * @param {String}
 *            theme 主题名
 */
$L.setCookie = function (cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + "; " + expires;
}
$L.setTheme = function (theme) {
    if (theme) {
        var exp = new Date();
        $L.setCookie("app_theme", "", -1);
        exp.setTime(exp.getTime() + 24 * 3600 * 1000);
        document.cookie = "app_theme=" + escape(theme) + ";expires=" + exp.toGMTString();
        window.location.reload();
    }
}
$L.setThemeUnload = function (theme) {
    if (theme) {
        var exp = new Date();
        $L.setCookie("app_theme", "", -1);
        exp.setTime(exp.getTime() + 24 * 3600 * 1000);
        document.cookie = "app_theme=" + escape(theme) + ";expires=" + exp.toGMTString();
    }
}
$L.getTheme = function () {
    return this.getCookie("app_theme");
}

$L.setSubjectAndTheme = function (subject, theme) {
    if (theme) {
        var exp = new Date();
        $L.setCookie("app_theme", "", -1);
        exp.setTime(exp.getTime() + 24 * 3600 * 1000);
        document.cookie = "app_theme=" + escape(theme) + ";expires=" + exp.toGMTString();
    }
    if (subject) {
        var exp = new Date();
        var oldSubject = $L.getCookie("app_subject");
        $L.setCookie("app_subject", "", -1);
        exp.setTime(exp.getTime() + 24 * 3600 * 1000);
        document.cookie = "app_subject=" + escape(subject) + ";expires=" + exp.toGMTString();
        var hrefUrl = window.location.href;
        if (subject === 'HAP' && oldSubject !== 'HAP') {
            hrefUrl = hrefUrl.replace("leaf_main.lview", "main.lview")
        } else if(subject === 'LEAF' && oldSubject !== 'LEAF'){
            hrefUrl = hrefUrl.replace("main.lview", "leaf_main.lview")
        }

        window.location.replace(hrefUrl);
    }
}
$L.setSubjectUnload = function (subject) {
    if (subject) {
        var exp = new Date();
        $L.setCookie("app_subject", "", -1);
        exp.setTime(exp.getTime() + 24 * 3600 * 1000);
        document.cookie = "app_subject=" + escape(subject) + ";expires=" + exp.toGMTString();
    }
}
$L.getSubject = function () {
    return this.getCookie("app_subject");
}
$L.CmpManager = function () {
    return {
        put: function (id, cmp) {
            if (!this.cache) this.cache = {};
            if (this.cache[id] != null) {
                alert("错误: ID为' " + id + " '的组件已经存在!");
                return;
            }
            if (cmp.hostid) {
                var host = Ext.get(cmp.hostid);
                // var host = Ext.getBody().child('[host_id='+cmp.hostid+']');
                (host.cmps = host.cmps || {})[id] = cmp;
                cmp['__host'] = host;
            } else if (window['__host']) {
                window['__host'].cmps[id] = cmp;
                cmp['__host'] = window['__host'];
            }

// if($L.focusWindow) $L.focusWindow.cmps[id] = cmp;
// if($L.focusTab) $L.focusTab.cmps[id] = cmp;
            this.cache[id] = cmp;
            cmp.on('mouseover', $L.CmpManager.onCmpOver, $L.CmpManager);
            cmp.on('mouseout', $L.CmpManager.onCmpOut, $L.CmpManager);
        },
        onCmpOver: function (cmp, e) {
            if ($L.validInfoType != 'tip') return;
            if (($L.Grid && cmp instanceof $L.Grid) || ($L.Table && cmp instanceof $L.Table) || ($L.GridBox && cmp instanceof $L.GridBox)) {
                var ds = cmp.dataset;
                if (!ds || !e.target)return;
                var target = Ext.fly(e.target).findParent('td');
                if (target) {
                    var atype = Ext.fly(target).getAttributeNS("", "atype");
                    if (atype == 'grid-cell' || atype == 'table-cell' || atype == 'gridbox-cell') {
                        var rid = Ext.fly(target).getAttributeNS("", "recordid");
                        var record = ds.findById(rid);
                        if (record) {
                            var name = Ext.fly(target).getAttributeNS("", "dataindex");
                            var field = record.getMeta().getField(name)
                            if (!field)return;
                            var msg = record.valid[name] || field.get('tooltip');
                            if (Ext.isEmpty(msg))return;
                            $L.ToolTip.show(target, msg);
                        }
                    }
                }
            } else {
                if (cmp.binder) {
                    var ds = cmp.binder.ds;
                    if (!ds)return;
                    var record = cmp.record;
                    if (!record)return;
                    var field = record.getMeta().getField(cmp.binder.name)
                    var msg = record.valid[cmp.binder.name] || field.get('tooltip');
                    if (Ext.isEmpty(msg))return;
                    $L.ToolTip.show(cmp.id, msg);
                }
            }
        },
        onCmpOut: function (cmp, e) {
            if ($L.validInfoType != 'tip') return;
            $L.ToolTip.hide();
        },
        getAll: function () {
            return this.cache;
        },
        remove: function (id) {
            var cmp = this.cache[id];
            if (cmp) {
                if (cmp['__host'] && cmp['__host'].cmps) {
                    delete cmp['__host'].cmps[id];
                }
                cmp.un('mouseover', $L.CmpManager.onCmpOver, $L.CmpManager);
                cmp.un('mouseout', $L.CmpManager.onCmpOut, $L.CmpManager);
                delete this.cache[id];
            }
        },
        get: function (id) {
            if (!this.cache) return null;
            return this.cache[id];
        }
    };
}();
Ext.Ajax.on("requestexception", function (conn, response, options) {
    if ($L.slideBarEnable) $L.SideBar.enable = $L.slideBarEnable;
    $L.manager.fireEvent('ajaxerror', $L.manager, response.status, response);
    if ($L.logWindow) {
        var record = $le('HTTPWATCH_DATASET').getCurrentRecord();
        var st = $L['_startTime'];
        var ed = new Date();
        record.set('spend', ed - st);
        record.set('status', response.status);
        record.set('result', response.statusText);
        record.set('response', response.statusText);
    }
    switch (response.status) {
        case 404:
            $L.showErrorMessage(response.status + _lang['ajax.error'], _lang['ajax.error.404'] + '"' + response.statusText + '"', null, 400, 150);
            break;
        case 500:
            $L.showErrorMessage(response.status + _lang['ajax.error'], response.responseText, null, 500, 300);
            break;
        case 0:
            break;
        default:
            $L.showErrorMessage(_lang['ajax.error'], response.statusText);
            break;
    }
}, this);

/**
 * 获取Leaf控件的对象，可以使用简写方式的$le()方法
 *
 * @param {String}
 *            id Leaf控件的id
 */
$ = $L.getCmp = function (id) {
    var cmp = $L.CmpManager.get(id) || parent.$L.CmpManager.get(id)
    if (cmp == null) {
// alert('未找到组件:' + id)
        window.console && console.error('未找到组件:' + id);
    }
    return cmp;
}

$le = $L.getCmp = function (id) {
    var cmp = $L.CmpManager.get(id)
    if (cmp == null) {
// alert('未找到组件:' + id)
        window.console && console.error('未找到组件:' + id);
    }
    return cmp;
}

/**
 * 设置cookie
 *
 * @param {String}
 *            name cookie名
 * @param {String}
 *            value cookie值
 * @param {Number}
 *            days 有效期(单位是天),默认是sessions
 */
$L.setCookie = function (name, value, days) {
    var pathname = location.pathname;
    pathname = pathname.substring(0, pathname.lastIndexOf('/') + 1);
    var exp = null;
    if (days) {
        exp = new Date();
        exp.setTime(exp.getTime() + days * 24 * 60 * 60 * 1000);
    }
    document.cookie = name + "=" + escape(value) + ';path = ' + pathname + ((exp) ? (';expires=' + exp.toGMTString()) : '');
}

/**
 * 根据cookie名获取cookie值
 *
 * @param {String}
 *            name cookie名
 */
$L.getCookie = function (name) {
    var arr = document.cookie.match(new RegExp("(^| )" + name + "=([^;]*)(;|$)"));
    if (arr != null) return unescape(arr[2]);
    return null;

}

/**
 * 获取页面可视高度
 *
 * @return {Number} 页面可视高度
 */
$L.getViewportHeight = function () {
    var viewportHeight = $L.minViewportHeight;
    if (Ext.isIE || Ext.isIE9 || Ext.isIE10) {
        viewportHeight = Ext.isStrict ? document.documentElement.clientHeight :
            document.body.clientHeight;
    } else {
        viewportHeight = self.innerHeight;
    }

    if (viewportHeight < $L.minViewportHeight) {
        viewportHeight = $L.minViewportHeight
    }

    return viewportHeight;
}
/**
 * 获取页面可视宽度
 *
 * @return {Number} 页面可视宽度
 */
$L.getViewportWidth = function () {
    var viewportWidth = $L.minViewportWidth;
    if (Ext.isIE || Ext.isIE9 || Ext.isIE10) {
        viewportWidth = Ext.isStrict ? document.documentElement.clientWidth :
            document.body.clientWidth;
    } else {
        viewportWidth = self.innerWidth;
    }

    // if (viewportWidth < $L.minViewportWidth) {
    //     viewportWidth = $L.minViewportWidth;
    // }

    return viewportWidth;
}
// $L.recordSize = function(){
// var w = $L.getViewportWidth();
// var h = $L.getViewportHeight();
// document.cookie = "vw="+w;
// document.cookie = "vh="+h;
// }
// $L.recordSize();
/**
 * post的方式提交数据，同{@link Leaf.DataSet#post}
 *
 * @param {String}
 *            action 提交的url地址
 * @param {Object}
 *            data 数据集合
 * @param {String}
 *            target 提交目标
 */
$L.post = function (action, data, target) {
    if(action.indexOf("?") == -1){
        action = action + "?_csrf="+ $jq('meta[name=_csrf]').attr('content');
    }else{
        action = action + "&_csrf="+ $jq('meta[name=_csrf]').attr('content');
    }
    var form = Ext.getBody().createChild({
        style: 'display:none',
        tag: 'form',
        method: 'post',
        action: action,
        target: target || '_self'
    });
    for (var key in data) {
        var v = data[key]
        if (v) {
            if (v instanceof Date) v = v.format('isoDate');// TODO:时分秒如何处理?
            form.createChild({tag: "input", type: "hidden", name: key, value: v});
        }
    }
    form.dom.submit();
    form.remove();
}
/**
 * POST方式的Ajax请求
 * <p>
 * opt对象的属性: <div class="mdetail-params">
 * <ul>
 * <li><code>url</code> <div class="sub-desc">提交的url地址</div></li>
 * <li><code>para</code> <div class="sub-desc">提交的参数</div></li>
 * <li><code>scope</code> <div class="sub-desc">作用域</div></li>
 * <li><code>sync</code> <div class="sub-desc">是否同步,默认false</div></li>
 * <li><code>success</code> <div class="sub-desc">成功的回调函数</div></li>
 * <li><code>error</code> <div class="sub-desc">错误的回调函数</div></li>
 * <li><code>failure</code> <div class="sub-desc">ajax调用失败的回调函数</div></li>
 * <li><code>lockMessage</code> <div class="sub-desc">锁屏消息</div></li>
 * </ul>
 * </div>
 * </p>
 *
 * @param {Object}
 *            opt 参数对象
 */
$L.request = function (opt) {
    var url = opt.url,
        isRest = /\/rest\//.test(url) || opt.isRest,
        para = opt.para || {},
        xmlData = opt.xmlData,
        jsonData = opt.jsonData,
        successCall = opt.success,
        errorCall = opt.error,
        scope = opt.scope,
        failureCall = opt.failure,
        lockMessage = opt.lockMessage,
        body = Ext.getBody(),
        opts = Ext.apply({}, opt.opts),
        method = opt.method,
        params = {};
    if (!Ext.isEmpty(lockMessage)) {
        $L.Masker.mask(body, lockMessage);
    }
    $L.manager.fireEvent('ajaxstart', url, para);
    if ($L.logWindow) {
        $L['_startTime'] = new Date();
        $le('HTTPWATCH_DATASET').create({'url': url, 'request': Ext.util.JSON.encode({parameter: para})})
    }
    if (isRest) {
        if (method != 'GET') {
            var dtoName = opt.dtoName,
                restDataFormat = opt.restDataFormat,
                arr = params[dtoName] = [], records = {};
            if (restDataFormat) {
                var format = $L.getRenderer(restDataFormat);
                if (format == null) {
                    alert("未找到" + restDataFormat + "方法");
                }
            }
            if (format) {
                params = format(para);
            } else {
                Ext.each(para, function (p) {
                    var _p = {};
                    Ext.iterate(p, function (key, value) {
                        if (/^@/.test(key)) {
                            _p[key] = value;
                        }
                    });
                    Ext.applyIf(_p, p);
                    delete _p._id;
                    delete _p._status;
                    arr.push(_p);
                });
            }
            params = Ext.util.JSON.encode(params);
        }
    } else if(method == 'GET'){
        if(para){
            var paraStr = '';
            for(var key in para){
                paraStr += '&'+key+'='+para[key];
            }
            if(paraStr.length > 0) {
                url = url + (url.lastIndexOf('?') > 0 ? paraStr : '?' + paraStr.substring(1));
            }
        }
    } else {
        para.randomString = Math.random().toString() + new Date().toString();
        params['_request_data'] = Ext.util.JSON.encode(Ext.apply({parameter: para}, opt.ext))
    }
    var header = $jq('meta[name=_csrf_header]').attr('content');
    var token = $jq('meta[name=_csrf]').attr('content');
    var publicKey = $jq('meta[name=_publicKey]').attr('content');
    if (method != 'GET') {
        if (!opt.headers) {
            opt.headers = {};
        }
        opt.headers[header] = token;
    }
    if (url.indexOf("autocrud") == -1 && url.indexOf(".lsc") == -1 && url.indexOf(".lview") == -1) {
        if (opt.headers) {
            opt.headers.springFlag = "Y";
        } else {
            opt.headers = {
                springFlag: "Y"
            };
        }
    }
    if(JSEncrypt){

    }
    return Ext.Ajax.request({
        url: url,
        method: method || 'POST',
        xmlData: xmlData,
        jsonData: jsonData,
        params: params,
        opts: opts,
        sync: opt.sync,
        headers: opt.headers,
        success: function (response, options) {
            if (!Ext.isEmpty(lockMessage)) {
                $L.Masker.unmask(body);
            }
            if (response) {
                if ($L.logWindow) {
                    var st = $L['_startTime'];
                    var ed = new Date();
                    var record = $le('HTTPWATCH_DATASET').getCurrentRecord();
                    record.set('spend', ed - st);
                    record.set('result', response.statusText);
                    record.set('status', response.status);
                    record.set('response', response.responseText);
                }
                $L.manager.fireEvent('ajaxcomplete', url, para, response);
                var res = null;
                if (Ext.isEmpty(response.responseText)) {
                    successCall.call(scope, {result: {}}, options);
                } else {
                    try {
                        res = Ext.decode(response.responseText);
                    } catch (e) {
                        $L.showErrorMessage(_lang['ajax.error'], _lang['ajax.error.format']);
                        return;
                    }
                    if (res) {
                        if (isRest) {
                            if (res.status == 'query') {
                                res.result = res.result || {};
                                res.result.totalCount = Number(res.totalCount);
                                res.success = true;
                            } else if (res.modifiedResult || res.returnCode == 'S') {
                                var record_arr = [];
                                res.result = {}
                                // Ext.each(res.modifiedResult.record,function(r){
                                // record_arr.push(records[r.code]);
                                // });
                                res.result.record = para;
                                res.success = true;
                            } else if (res.returnCode == 'E') {
                                res.success = false;
                                res.error = {message: res.returnMessage};
                            }
                        }
                        if (!res.success) {
                            $L.manager.fireEvent('ajaxfailed', $L.manager, url, para, res);
                            if (res.error) {
                                if (res.error.code && (res.error.code == 'session_expired' || res.error.code == 'login_required')) {
                                    if ($L.manager.fireEvent('timeout', $L.manager))
                                        $L.showErrorMessage(_lang['ajax.error'], _lang['session.expired']);
                                } else {
                                    var st = res.error.stackTrace;
                                    st = (st) ? st.replaceAll('\r\n', '</br>') : '';
                                    if (res.error.message) {
                                        var h = (st == '') ? 150 : 250;
                                        $L.showErrorMessage(_lang['ajax.error'], res.error.message + '</br>' + st, null, 400, h);
                                    } else {
                                        $L.showErrorMessage(_lang['ajax.error'], st, null, 400, 250);
                                    }
                                }
                                if (errorCall)
                                    errorCall.call(scope, res, options);
                            }else if(res.message){
                                $L.showInfoMessage(_lang['dataset.info'], res.message);
                                if (errorCall)
                                    errorCall.call(scope, res, options);
                            }
                        } else {
                            if (successCall) {
                                successCall.call(scope, res, options);
                                opt.showSuccessTip = opt.showSuccessTip || false;
                            }
                            if (opt.showSuccessTip) {
                                $L.manager.fireEvent('ajaxsuccess', opt.successTip);
                            }
                        }
                    }
                }
            }
        },
        failure: function (response, options) {
            if (!Ext.isEmpty(lockMessage)) {
                $L.Masker.unmask(body);
            }
            if (failureCall) failureCall.call(scope, response, options);
        },
        scope: scope
    });
}
$L.parseXML = function (strxml) {
    var xmldom = null;
    if (typeof DOMParser != "undefined") {
        try {
            xmldom = new DOMParser().parseFromString(strxml, "text/xml");
        } catch (e) {
            throw new Error("XML parsing error:" + e.message);
        }
        var errors = xmldom.getElementsByTagName("parsererror");
        if (errors.length) {
            throw new Error("XML parsing error:" + errors[0].textContent);
        }
    } else if (document.implementation.hasFeature("LS", "3.0")) {
        var implementation = document.implementation,
            parser = implementaion.createLSParser(implementation.MODE_SYNCHRONOUS, null),
            input = implementation.createLSInput();
        input.stringData = strxml;
        xmldom = parser.parse(input);
    } else if (typeof ActiveXObject != "undefined") {
        xmldom = new ActiveXObject("Microsoft.XMLDOM");
        xmldom.loadXML(strxml);
        if (xmldom.parseError != 0) {
            throw new Error("XML parsing error:" + xmldom.parseError.reason);
        }
    } else {
        throw new Error("No XML parser available.");
    }
    return xmldom;
}
Leaf.dateFormat = function () {
    var masks = {
        "default": "ddd mmm dd yyyy HH:MM:ss",
        shortDate: "m/d/yy",
        mediumDate: "mmm d, yyyy",
        longDate: "mmmm d, yyyy",
        fullDate: "dddd, mmmm d, yyyy",
        shortTime: "h:MM TT",
        mediumTime: "h:MM:ss TT",
        longTime: "h:MM:ss TT Z",
        isoDate: "yyyy-mm-dd",
        isoTime: "HH:MM:ss",
        isoDateTime: "yyyy-mm-dd'T'HH:MM:ss",
        isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
    };
    var token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
        timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
        timezoneClip = /[^-+\dA-Z]/g,
        pad = function (val, len) {
            val = String(val);
            len = len || 2;
            while (val.length < len) val = "0" + val;
            return val;
        },
        hasTimeStamp = function (mask, token) {
            return !!String(masks[mask] || mask || masks["default"]).match(token);
        },
        _parseDate = function (string, mask, fun) {
            for (var i = 0, arr = mask.match(token), numbers = string.match(/\d+/g), value, index = 0; i < arr.length; i++) {
                if (numbers.length == arr.length) value = numbers[i];
                else if (numbers.length == 1) value = parseInt(string.slice(index, index += arr[i].length), 10);
                else value = parseInt(string.slice(index = mask.search(arr[i]), index + arr[i].length));
                switch (arr[i]) {
                    case "mm":
                        ;
                    case "m":
                        value--;
                        break;
                }
                fun(arr[i], value);
            }
        };
    return {
        pad: pad,
        parseDate: function (string, mask, utc) {
            if (typeof string != "string" || string == "")return null;
            mask = String(masks[mask] || mask || masks["default"]);
            if (mask.slice(0, 4) == "UTC:") {
                mask = mask.slice(4);
                utc = true;
            }
            var date = new Date(1970, 1, 2, 0, 0, 0),
                _ = utc ? "setUTC" : "set",
                d = date[_ + "Date"],
                m = date[_ + "Month"],
                yy = date[_ + "FullYear"],
                y = date[_ + "Year"],
                H = date[_ + "Hours"],
                M = date[_ + "Minutes"],
                s = date[_ + "Seconds"],
                L = date[_ + "Milliseconds"],
                // o = utc ? 0 : date.getTimezoneOffset();
                flags = {
                    d: d,
                    dd: d,
                    m: m,
                    mm: m,
                    yy: y,
                    yyyy: yy,
                    h: H,
                    hh: H,
                    H: H,
                    HH: H,
                    M: M,
                    MM: M,
                    s: s,
                    ss: s,
                    l: L,
                    L: L
                };
            try {
                _parseDate(string, mask, function ($0, value) {
                    flags[$0].call(date, value);
                });
            } catch (e) {
                throw new SyntaxError("invalid date");
            }
            if (isNaN(date)) throw new SyntaxError("invalid date");
            return date;
        },
        format: function (date, mask, utc) {
            if (arguments.length == 1 && (typeof date == "string" || date instanceof String) && !/\d/.test(date)) {
                mask = date;
                date = undefined;
            }
            date = date ? new Date(date) : new Date();
            if (isNaN(date)) throw new SyntaxError("invalid date");

            mask = String(masks[mask] || mask || masks["default"]);
            if (mask.slice(0, 4) == "UTC:") {
                mask = mask.slice(4);
                utc = true;
            }

            var _ = utc ? "getUTC" : "get",
                d = date[_ + "Date"](),
                D = date[_ + "Day"](),
                m = date[_ + "Month"](),
                y = date[_ + "FullYear"](),
                H = date[_ + "Hours"](),
                M = date[_ + "Minutes"](),
                s = date[_ + "Seconds"](),
                L = date[_ + "Milliseconds"](),
                o = utc ? 0 : date.getTimezoneOffset(),
                flags = {
                    d: d,
                    dd: pad(d),
                    m: m + 1,
                    mm: pad(m + 1),
                    yy: String(y).slice(2),
                    yyyy: y,
                    h: H % 12 || 12,
                    hh: pad(H % 12 || 12),
                    H: H,
                    HH: pad(H),
                    M: M,
                    MM: pad(M),
                    s: s,
                    ss: pad(s),
                    l: pad(L, 3),
                    L: pad(L > 99 ? Math.round(L / 10) : L),
                    t: H < 12 ? "a" : "p",
                    tt: H < 12 ? "am" : "pm",
                    T: H < 12 ? "A" : "P",
                    TT: H < 12 ? "AM" : "PM",
                    Z: utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
                    o: (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
                    S: ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
                };
            return mask.replace(token, function ($0) {
                return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
            });
        },
        isDateTime: function (mask) {
            return hasTimeStamp(mask, /([HhMs])\1?/);
        }
    };
}();

Ext.applyIf(String.prototype, {
    trim: function () {
        return this.replace(/(^\s*)|(\s*$)/g, "");
    }
});
Ext.applyIf(Date.prototype, {
    format: function (mask, utc) {
        return Leaf.dateFormat.format(this, mask, utc);
    }
});
Ext.applyIf(Array.prototype, {
    add: function (o) {
        if (this.indexOf(o) == -1)
            this[this.length] = o;
    },
    find: function (property, value) {
        var r = null;
        for (var i = 0, length = this.length; i < length; i++) {
            var item = this[i];
            if (item[property] == value) {
                r = item;
                break;
            }
        }
        return r;
    },
    map: function (callback, scope) {
        var arr = [];
        for (var i = 0, length = this.length; i < length; i++) {
            arr.push(callback.call(scope || window, this[i], i))
        }
        return arr;
    }
});
Ext.applyIf(String.prototype, {
    replaceAll: function (s1, s2) {
        return this.replace(new RegExp(s1, "gm"), s2);
    }
});
Ext.applyIf(String.prototype, {
    parseDate: function (mask, utc) {
        return Leaf.dateFormat.parseDate(this.toString(), mask, utc);
    }
});
$L.TextMetrics = function () {
    // var shared;
    return {
        measure: function (el, text, fixedWidth) {
            // if(!shared){
            var shared = $L.TextMetrics.Instance(el, fixedWidth);
            // }
            // shared.bind(el);
            // shared.setFixedWidth(fixedWidth || 'auto');
            return shared.getSize(text);
        }
    };
}();
$L.TextMetrics.Instance = function (bindTo, fixedWidth) {
    var p = '<div style="left:-10000px;top:-10000px;position:absolute;visibility:hidden"></div>',
        ml = Ext.get(Ext.DomHelper.append(Ext.get(bindTo), p)),
// var ml = new Ext.Element(document.createElement('div'));
// document.body.appendChild(ml.dom);
// ml.position('absolute');
// ml.setLeft(-1000);
// ml.setTop(-1000);
// ml.hide();
        instance = {
            getSize: function (text) {
                ml.update(text);
                var s = {
                    width: ml.getWidth(),
                    height: ml.getHeight()
                };
                ml.remove();
                return s;
            },
            bind: function (el) {
                var a = ['padding', 'font-size', 'font-style', 'font-weight', 'font-family', 'line-height', 'text-transform', 'letter-spacing'],
                    len = a.length, r = {};
                for (var i = 0; i < len; i++) {
                    r[a[i]] = Ext.fly(el).getStyle(a[i]);
                }
                ml.setStyle(r);
            },
            setFixedWidth: function (width) {
                ml.setWidth(width);
            }
        };
    if (fixedWidth) {
        ml.setWidth(fixedWidth);
    }
    instance.bind(bindTo);
    return instance;
};
$L.ToolTip = function () {
    var q = {
        init: function () {
            var sf = this;
            Ext.onReady(function () {
                sf.tip = new Ext.Template('<div class="tip-wrap item-shadow">{shadow}<div class="tip-body"></div><div class="triangle"><div class="triangle1"></div><div class="triangle2"></div></div></div>').insertFirst(document.body, {
                    shadow: Ext.isIE ? '<div class="item-ie-shadow"></div>' : ''
                }, true);
// sf.shadow = Ext.get(sdom);
                sf.body = sf.tip.first("div.tip-body");
            });
        },
        show: function (el, text) {
            if (this.tip == null) {
                this.init();
                // return;
            }
            this.tip.show();
// this.shadow.show();
            this.body.update(text)
            var ele;
            if (typeof(el) == "string") {
                if (this.sid == el) return;
                this.sid = el;
                var cmp = $L.CmpManager.get(el)
                if (cmp) {
                    if (cmp.wrap) {
                        ele = cmp.wrap;
                    }
                }
            } else {
                ele = Ext.get(el);
            }
// this.shadow.setWidth(this.tip.getWidth())
// this.shadow.setHeight(this.tip.getHeight())
            this.correctPosition(ele);
        },
        correctPosition: function (ele) {
            /*
             * 减去20是body得padding值
             */
            var screenWidth = $L.getViewportWidth() - 20;
            var screenHeight = $L.getViewportHeight();
            var x = ele.getX();
//            var sx = ele.getX() - 2;
            /*
             * 设置图标按钮文本提示显示位置在其上方，若按钮图标距离顶部的高度不足以显示提示文本时，
             * 提示文本显示在其下方
             * 向平     2017-10-26
             */
            var y = ele.getY();
//            var sy = ele.getY() - this.tip.getHeight() - 15;
            var triangle = this.tip;
            triangle.removeClass("tip-wrapLeftTop");
            triangle.removeClass("tip-wrapLeftBottom");
            triangle.removeClass("tip-wrapRightTop");
            triangle.removeClass("tip-wrapRightBottom");
            if (x + this.tip.getWidth() - (ele.getWidth() / 2) < screenWidth && y > this.tip.getHeight() + 13) {
                triangle.addClass("tip-wrapLeftTop");
                x = ele.getX();
                y = ele.getY() - this.tip.getHeight() - 15;
            }
            else if (x + this.tip.getWidth() - (ele.getWidth() / 2) > screenWidth && y > this.tip.getHeight() + 13) {
                triangle.addClass("tip-wrapRightTop");
                x = ele.getX() - this.tip.getWidth() + ele.getWidth() / 2;
                y = ele.getY() - this.tip.getHeight() - 15;
            }
            else if (x + this.tip.getWidth() - (ele.getWidth() / 2) > screenWidth && y < this.tip.getHeight() + 13) {
                triangle.addClass("tip-wrapRightBottom");
                x = ele.getX() - this.tip.getWidth() + ele.getWidth() / 2;
                y = ele.getY() + ele.getHeight() + 10;
            }
            else {
                triangle.addClass("tip-wrapLeftBottom");
                x = ele.getX();
                y = ele.getY() + ele.getHeight() + 10;
            }
            this.tip.setX(x);
            this.tip.setY(y);
//            this.tip.setX(x);
//            this.tip.setY(ele.getY());
// this.shadow.setX(sx);
// this.shadow.setY(this.tip.getY()+ 2)
        },
        hide: function () {
            this.sid = null;
            if (this.tip != null) this.tip.hide();
// if(this.shadow != null) this.shadow.hide();
        }
    }
    return q
}();
$L.ToolFloat = function () {
    var q = {
        init: function () {
            var sf = this;
            Ext.onReady(function () {
                sf.tip = new Ext.Template('<div class="float-wrap float-item-shadow">{shadow}<div class="float-body"></div></div>').insertFirst(document.body, {
                    shadow: Ext.isIE ? '<div class="float-ie-shadow"></div>' : ''
                }, true);
                sf.body = sf.tip.first("div.float-body");
            });
        },
        show: function (el, text) {
            if (this.tip == null) {
                this.init();
                // return;
            }
            this.tip.show();
            this.body.update(text)
            var ele;
            if (typeof(el) == "string") {
                if (this.sid == el) return;
                this.sid = el;
                var cmp = $L.CmpManager.get(el)
                if (cmp) {
                    if (cmp.wrap) {
                        ele = cmp.wrap;
                    }
                }
            } else {
                ele = Ext.get(el);
            }
            this.correctPosition(ele);
        },
        correctPosition: function (ele) {
            /*
             * 减去20是body得padding值
             */
            var screenWidth = $L.getViewportWidth() - 20;
            var screenHeight = $L.getViewportHeight();
            var x = ele.getX();
//            var sx = ele.getX() - 2;
            /*
             * 设置图标按钮文本提示显示位置在其上方，若按钮图标距离顶部的高度不足以显示提示文本时，
             * 提示文本显示在其下方
             * 向平     2017-10-26
             */
            var y = ele.getY();
//            var sy = ele.getY() - this.tip.getHeight() - 15;
            var triangle = this.tip;
            triangle.removeClass("tip-wrapLeftTop");
            triangle.removeClass("tip-wrapLeftBottom");
            triangle.removeClass("tip-wrapRightTop");
            triangle.removeClass("tip-wrapRightBottom");
            if (x + this.tip.getWidth() - (ele.getWidth() / 2) < screenWidth && y > this.tip.getHeight() + 13) {
                triangle.addClass("tip-wrapLeftTop");
                x = ele.getX();
                y = ele.getY() - this.tip.getHeight();
            }
            else if (x + this.tip.getWidth() - (ele.getWidth() / 2) > screenWidth && y > this.tip.getHeight() + 13) {
                triangle.addClass("tip-wrapRightTop");
                x = ele.getX() - this.tip.getWidth() + ele.getWidth() / 2;
                y = ele.getY() - this.tip.getHeight();
            }
            else if (x + this.tip.getWidth() - (ele.getWidth() / 2) > screenWidth && y < this.tip.getHeight() + 13) {
                triangle.addClass("tip-wrapRightBottom");
                x = ele.getX() - this.tip.getWidth() + ele.getWidth() / 2;
                y = ele.getY() + ele.getHeight();
            }
            else {
                triangle.addClass("tip-wrapLeftBottom");
                x = ele.getX();
                y = ele.getY() + ele.getHeight();
            }
            this.tip.setX(x);
            this.tip.setY(y);
        },
        hide: function () {
            this.sid = null;
            if (this.tip != null) this.tip.hide();
        }
    }
    return q
}();

//obj.type success , error  warning
$L.SideBar = function () {
    var m = {
        enable: true,
        bar: null,
        show: function (obj) {
            var msg = obj.msg;
            var type = obj.type || 'success';
            if (!this.enable)return;
// this.hide();
            var sf = this;
            if ($L.PARENT_DOMAIN && parent.showSideBar) {
                parent.showSideBar(msg || '')
            } else {
                this.hide();
                var p;
                if (msg) {
                    p = '<div class="item-slideBar"><div class="slidebar-img ' + type + '"></div><div class="inner" style="float: left">' + msg + '</div></div>';
                } else {
                    p = obj.html;
                }
                this.bar = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(), p));
                this.bar.setStyle('z-index', 999999);
                var screenWidth = $L.getViewportWidth();
                var screenHeight = $L.getViewportHeight();
                var x = Math.max(0, (screenWidth - this.bar.getWidth()) / 2);
                var y = Math.max(0, (screenHeight - this.bar.getHeight()) / 2);
                // this.bar.setY(y);
                // this.bar.setX(x);
                this.bar.setStyle('position', 'fixed');
                this.bar.setStyle('top', '30px');
                this.bar.setStyle('left', x + 'px');
                //this.bar.setStyle('margin-left', '' + x + 'px');
                this.bar.fadeIn();
// this.bar.animate({height: {to: 50, from: 0}},0.35,function(){
                setTimeout(function () {
                    sf.hide();
                }, obj.duration || 2000);
// },'easeOut','run');
            }
        },
        hide: function () {
            if ($L.PARENT_DOMAIN && parent.hideSideBar) {
                parent.hideSideBar()
            } else {
                if (this.bar) {
                    Ext.fly(this.bar).fadeOut();
                    Ext.fly(this.bar).remove();
                    this.bar = null;
                }
            }
        }
    }
    return m;
}();
$L.Status = function () {
    var m = {
        bar: null,
        enable: true,
        show: function (msg) {
            if (!this.enable)return;
            this.hide();
            if ($L.PARENT_DOMAIN && parent.showStatus) {
                parent.showStatus(msg);
            } else {
                var p = '<div class="item-statusBar" unselectable="on">' + msg + '</div>';
                this.bar = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(), p));
                this.bar.setStyle('z-index', 999998);
            }
        },
        hide: function () {
            if ($L.PARENT_DOMAIN && parent.hideStatus) {
                parent.hideStatus();
            } else {
                if (this.bar) {
                    Ext.fly(this.bar).remove();
                    this.bar = null;
                }
            }
        }
    }
    return m;
}();
$L.Cover = function () {
    var m = {
        bodyOverflow: null,
        sw: null,
        sh: null,
        container: {},
        cover: function (el) {
// if(!$L.Cover.bodyOverflow)$L.Cover.bodyOverflow =
// Ext.getBody().getStyle('overflow');
            var scrollWidth = Ext.isStrict ? document.documentElement.scrollWidth : document.body.scrollWidth;
            var scrollHeight = Ext.isStrict ? document.documentElement.scrollHeight : document.body.scrollHeight;
            var screenWidth = Math.max(scrollWidth, $L.getViewportWidth());
            var screenHeight = Math.max(scrollHeight, $L.getViewportHeight());
            var p = '<DIV tabIndex="-1" class="leaf-cover"' + (Ext.isIE6 ? ' style="position:absolute;width:' + (screenWidth - 1) + 'px;height:' + (screenHeight - 1) + 'px;' : '') + '" unselectable="on" hideFocus></DIV>';
            var cover = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(), p));
            cover.on('focus', function (e) {
                e.stopPropagation();
                Ext.fly(el).focus()
            });
            cover.setStyle('z-index', Ext.fly(el).getStyle('z-index') - 1);
// Ext.getBody().setStyle('overflow','hidden');
            $L.Cover.container[el.id] = cover;
            cover.addClass('animated fadeIn03');
            setTimeout(function () {
                cover.removeClass('animated fadeIn03');
            }, 600);
        },
        uncover: function (el) {
            var cover = $L.Cover.container[el.id];
            cover.removeClass('animated fadeIn03');
            if (cover) {
                Ext.fly(cover).remove();
                $L.Cover.container[el.id] = null;
                delete $L.Cover.container[el.id];
            }
            var reset = true;
            for (key in $L.Cover.container) {
                if ($L.Cover.container[key]) {
                    reset = false;
                    break;
                }
            }
// if(reset&&$L.Cover.bodyOverflow)Ext.getBody().setStyle('overflow',$L.Cover.bodyOverflow);
        },
        resizeCover: function () {
            for (key in $L.Cover.container) {
                var cover = $L.Cover.container[key];
                Ext.fly(cover).setStyle('display', 'none');
            }
            setTimeout(function () {
                var scrollWidth = Ext.isStrict ? document.documentElement.scrollWidth : document.body.scrollWidth;
                var scrollHeight = Ext.isStrict ? document.documentElement.scrollHeight : document.body.scrollHeight;
                var screenWidth = Math.max(scrollWidth, $L.getViewportWidth()) - 1;
                var screenHeight = Math.max(scrollHeight, $L.getViewportHeight()) - 1;
                for (key in $L.Cover.container) {
                    var cover = $L.Cover.container[key];
                    Ext.fly(cover).setWidth(screenWidth);
                    Ext.fly(cover).setHeight(screenHeight);
                    Ext.fly(cover).setStyle('display', '');
                }
            }, 1)
        }
    }
    return m;
}();
$L.Masker = function () {
    var m = {
        container: {},
        mask: function (el, msg) {
            if ($L.Masker.container[el.id]) {
                return;
            }
            msg = msg || _lang['mask.loading'];
            var el = Ext.get(el);
            var w = el.getWidth();
            var h = el.getHeight();// leftp:0px;top:0px; 是否引起resize?
            var vh = Math.min(h - 2, 30);
            var zi = el.getStyle('z-index') == 'auto' ? 0 : Number(el.getStyle('z-index'));
            if (el === Ext.getBody()) {
                w = $L.getViewportWidth() - 2;
                h = $L.getViewportHeight() - 2;
                vh = Math.min(h - 2, 30);
                zi = 99999;
            }
            var p = '<div class="leaf-mask"  style="left:-10000px;top:-10000px;width:' + w + 'px;height:' + h + 'px;position: absolute;"><div unselectable="on"></div><span style="top:' + (h / 2 - 11) + 'px;height:' + vh + 'px;line-height:' + (vh - 2) + 'px">' + msg + '</span></div>';
            var wrap = el.parent('body') ? el.parent() : el.child('body') || el;
            var masker = new Ext.Template(p).insertFirst(wrap.dom, {}, true);
            masker.setStyle('z-index', zi + 1);
            masker.setXY(el.getXY());
            masker.addClass('animated fadeIn03');
            setTimeout(function () {
                masker.removeClass('animated fadeIn03');
            }, 600);
            var sp = masker.child('span');
            // var size = $L.TextMetrics.measure(sp,msg);
            // sp.setLeft((w-size.width - 45)/2)
            sp.setLeft((w - sp.getWidth() - 45) / 2)
            $L.Masker.container[el.id] = masker;
            masker.on('mousewheel', function (e) {
                e.stopEvent()
            });
        },
        unmask: function (el) {
            var masker = $L.Masker.container[el.id];
            if (masker) {
                Ext.fly(masker).remove();
                $L.Masker.container[el.id] = null;
                delete $L.Masker.container[el.id];
            }
        }
    }
    return m;
}();
Ext.util.JSON.encodeDate = function (o) {
    var pad = function (n) {
        return n < 10 ? "0" + n : n;
    };
    var r = '"' + o.getFullYear() + "-" +
        pad(o.getMonth() + 1) + "-" +
        pad(o.getDate());
    if (o.xtype == 'timestamp') {
        r = r + " " +
            pad(o.getHours()) + ":" +
            pad(o.getMinutes()) + ":" +
            pad(o.getSeconds())
    }
    r += '"';
    return r
};
$L.evalList = [];
$L.evaling = false;
$L.doEvalScript = function () {
    $L.evaling = true;
    var list = $L.evalList;
    var o = list.shift();
    if (!o) {
        window['__host'] = null;
        $L.evaling = false;
        if ($L.loadEvent) {
            $L.loadEvent.fire();
            $L.loadEvent = null;
        }
        return;
    }
    var sf = o.sf, html = o.html, loadScripts = o.loadScripts, callback = o.callback, host = o.host, id = o.id;
    var dom = sf.dom;

    if (host) window['__host'] = host;
    var links = [];
    var scripts = [];
    var hd = document.getElementsByTagName("head")[0];
    for (var i = 0; i < hd.childNodes.length; i++) {
        var he = hd.childNodes[i];
        if (he.tagName == 'LINK') {
            links.push(he.href);
        } else if (he.tagName == 'SCRIPT') {
            scripts.push(he.src);
        }
    }
    var jsre = /(?:<script([^>]*)?>)((\n|\r|.)*?)(?:<\/script>)/ig;
    var jsSrcRe = /\ssrc=([\'\"])(.*?)\1/i;

    var cssre = /(?:<link([^>]*)?>)((\n|\r|.)*?)/ig;
    var cssHreRe = /\shref=([\'\"])(.*?)\1/i;
    var cssMediaRe = /\smedia=([\'\"])(.*?)\1/i;

    var cssm;
    while (cssm = cssre.exec(html)) {
        var attrs = cssm[1];
        var srcMatch = attrs ? attrs.match(cssHreRe) : false;
        var mediaMatch = attrs ? attrs.match(cssMediaRe) : false;
        if (srcMatch && srcMatch[2]) {
            var included = false;
            for (var i = 0; i < links.length; i++) {
                var link = links[i];
                if (link.indexOf(srcMatch[2]) != -1) {
                    included = true;
                    break;
                }
            }
            if (!included) {
                var s = document.createElement("link");
                s.type = 'text/css';
                s.rel = 'stylesheet';
                s.href = srcMatch[2];
                if (mediaMatch) s.media = mediaMatch[2];
                hd.appendChild(s);
            }
        }
    }
    var match;
    var jslink = [];
    var jsscript = [];
    var tmpscript = [];
    while (match = jsre.exec(html)) {
        var attrs = match[1];
        var srcMatch = attrs ? attrs.match(jsSrcRe) : false;
        if (srcMatch && srcMatch[2]) {
            var included = false;
            for (var i = 0; i < scripts.length; i++) {
                var script = scripts[i];
                if (script.indexOf(srcMatch[2]) != -1) {
                    included = true;
                    break;
                }
            }
            if (!included) {
                jslink[jslink.length] = {
                    src: srcMatch[2],
                    type: 'text/javascript'
                }
            }
        } else if (match[2] && match[2].length > 0 && match[0] && match[0].indexOf("text/tmplate")<0 ) {
            jsscript[jsscript.length] = match[2];
        } else if(match[2] && match[2].length > 0 && match[0] && match[0].indexOf("text/tmplate") >= 0){
            //增加tmplate类型script支持
            var str1 = match[0].substr(match[0].indexOf("id=\"")+4)
            var tmpId = str1.substr(0,str1.indexOf("\""))
            if(!!tmpId){
                tmpscript[tmpscript.length] = {
                    str:match[2],
                    id:tmpId
                };
            }
        }
    }
    var loaded = 0;

    var finishLoad = function () {
        try {
            for (j = 0, k = jsscript.length; j < k; j++) {
                var jst = jsscript[j];
                if (o.destroying === true) break;
                try {
                    if (window.execScript) {
                        window.execScript(jst);
                    } else {
                        window.eval(jst);
                    }
                } catch (e) {
                    window.console && console.error("执行代码: " + jst + '\n' + e.stack);
                }
            }
            for (j = 0 ; j < tmpscript.length; j++) {
                var tmp = tmpscript[j];
                if (o.destroying === true) break;
                try {
                    if(document.getElementById(tmp.id) == null){
                        var s = document.createElement("SCRIPT");
                        s.type = "text/tmplate";
                        s.id = tmp.id;
                        s.innerHTML = tmp.str;
                        document.body.appendChild(s);
                    }
                } catch (e) {
                    window.console && console.error("创建模板script: " + tmp + '\n' + e.stack);
                }
            }
        } catch (e) {
        }
        var el = document.getElementById(id);
        if (el) {
            Ext.removeNode(el);
        }
// Ext.fly(dom).setStyle('display', 'block');
        Ext.fly(dom).show();
        if (typeof callback == "function") {
            callback();
        }
        $L.doEvalScript();
    }

    var continueLoad = function () {
        var js = jslink[loaded];
        var s = document.createElement("script");
        s.src = js.src;
        s.type = js.type;
        s[Ext.isIE ? "onreadystatechange" : "onload"] = onReadOnLoad;
        s["onerror"] = onErrorLoad;
        hd.appendChild(s);
    }

    var onReadOnLoad = function () {
        var isready = Ext.isIE ? (!this.readyState || this.readyState == "loaded" || this.readyState == "complete") : true;
        if (isready) {
            loaded++;
            if (loaded == jslink.length) {
                finishLoad();
            } else {
                continueLoad();
            }
        }
    }

    var onErrorLoad = function (evt) {
        loaded++;
        alert('无法加载脚本:' + evt.target.src);
        if (loaded == jslink.length) {
            finishLoad();
        } else {
            continueLoad();
        }
    }

    if (jslink.length > 0) {
        continueLoad();
    } else if (jslink.length == 0) {
        for (j = 0, k = jsscript.length; j < k; j++) {
            var jst = jsscript[j];
            if (o.destroying === true) break;
            try {
                if (window.execScript) {
                    window.execScript(jst);
                } else {
                    window.eval(jst);
                }
            } catch (e) {
                window.console && console.error("执行代码: " + jst + '\n' + e.stack);
            }
        }
        var el = document.getElementById(id);
        if (el) {
            Ext.removeNode(el);
        }
// Ext.fly(dom).setStyle('display', 'block');
        Ext.fly(dom).show();
        if (typeof callback == "function") {
            callback();
        }
        $L.doEvalScript();
    }
}
Ext.Element.prototype.update = function (html, loadScripts, callback, host) {
    if (typeof html == "undefined") {
        html = "";
    }
    if (loadScripts !== true) {
        this.dom.innerHTML = html;
        if (typeof callback == "function") {
            callback();
        }
        return this;
    }

    var id = Ext.id();
    var sf = this;
    var dom = this.dom;
    html += '<span id="' + id + '"></span>';
    Ext.lib.Event.onAvailable(id, function () {
        $L.evalList.push({
            html: html,
            loadScripts: loadScripts,
            callback: callback,
            host: host,
            id: id,
            sf: sf
        });
        if (!$L.evaling)
            $L.doEvalScript()
    });

// Ext.fly(dom).setStyle('display', 'none');
    Ext.fly(dom).hide();
    dom.innerHTML = html.replace(/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/ig, "").replace(/(?:<link.*?>)((\n|\r|.)*?)/ig, "");
    return this;
}

Ext.EventObjectImpl.prototype['isSpecialKey'] = function () {
    var k = this.keyCode;
// return (this.type == 'keypress' && this.ctrlKey) || k==8 || k== 46 || k == 9
// || k == 13 || k == 40 || k == 27 || k == 44 ||
    return (this.type == 'keypress' && this.ctrlKey) || k == 9 || k == 13 || k == 40 || k == 27 ||
        (k == 16) || (k == 17) ||
        (k >= 18 && k <= 20) ||
        (k >= 33 && k <= 35) ||
        (k >= 36 && k <= 39) ||
        (k >= 44 && k <= 45);
}
Ext.removeNode = Ext.isIE && !Ext.isIE8 ? function () {
        var d;
        return function (n) {
            if (n && n.tagName != 'BODY') {
                (Ext.enableNestedListenerRemoval) ? Ext.EventManager.purgeElement(n, true) : Ext.EventManager.removeAll(n);
                if (!d) {
                    d = document.createElement('div');
                    d.id = '_removenode';
                    d.style.cssText = 'position:absolute;display:none;left:-10000px;top:-10000px';
                }
// d = d || document.createElement('<div id="_removenode"
// style="position:absolute;display:none;left:-10000px;top:-10000px">');
                if (!d.parentNode) document.body.appendChild(d);
                d.appendChild(n);
                d.innerHTML = '';
                delete Ext.elCache[n.id];
            }
        }
    }() : function (n) {
        if (n && n.parentNode && n.tagName != 'BODY') {
            (Ext.enableNestedListenerRemoval) ? Ext.EventManager.purgeElement(n, true) : Ext.EventManager.removeAll(n);
            n.parentNode.removeChild(n);
            delete Ext.elCache[n.id];
        }
    }
$L.parseDate = function (str) {
    if (typeof str == 'string') {

        // TODO:临时, 需要服务端解决
// if(str.indexOf('.0') !=-1) str = str.substr(0,str.length-2);

        var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);
        if (results && results.length > 3)
            return new Date(parseInt(results[1]), parseInt(results[2], 10) - 1, parseInt(results[3], 10));
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}).?(\d{0,3}) *$/);
        if (results && results.length > 6)
            return new Date(parseInt(results[1]), parseInt(results[2], 10) - 1, parseInt(results[3], 10), parseInt(results[4], 10), parseInt(results[5], 10), parseInt(results[6], 10));
    }
    return null;
}
$L.getRenderer = function (renderer) {
    if (!renderer) return null;
    /*
     * var rder; if(renderer.indexOf('Leaf.') != -1){ rder =
     * $L[renderer.substr(7,renderer.length)] }else{ rder = window[renderer]; }
     */
    try {
        return eval(renderer);
    } catch (e) {
        return window[renderer];
    }
}
$L.RowNumberRenderer = function (value, record, name) {
    if (record && record.ds) {
        var ds = record.ds;
        return (ds.currentPage - 1) * ds.pagesize + ds.indexOf(record) + 1;
    }
}
/**
 * 将日期转换成默认格式的字符串，默认格式是根据Leaf.defaultDateFormat来定义的.如果没有特殊指定,默认格式为yyyy-mm-dd
 *
 * @param {Date}
 *            date 转换的日期
 * @return {String}
 */
$L.formatDate = function (date) {
    if (!date)return '';
    if (date.format)return date.format($L.defaultDateFormat);
    return date;
}
/**
 * 将日期转换成yyyy-mm-dd HH:MM:ss格式的字符串
 *
 * @param {Date}
 *            date 需要转换的日期
 * @return {String} 转换后的字符串
 */
$L.formatDateTime = function (date) {
    if (!date)return '';
    if (date.format)return date.format($L.defaultDateTimeFormat);
    return date;
}
/**
 * 将数值根据精度转换成带有千分位的字符串
 *
 * @param {Number}
 *            value 数值
 * @param {Number}
 *            decimalprecision 小数点位数
 * @return {String}
 */
$L.formatNumber = function (value, decimalprecision) {
    if (Ext.isEmpty(value))return '';
    value = String(value).replace(/,/g, '');
    if (isNaN(value))return '';
    if (!isNaN(decimalprecision)) value = Number(value).toFixed(decimalprecision);
    var ps = $L.parseScientific(value).split('.');
    var sub = (ps.length == 2) ? '.' + ps[1] : '';
    var whole = ps[0];
    var r = /(\d+)(\d{3})/;
    while (r.test(whole)) {
        whole = whole.replace(r, '$1' + ',' + '$2');
    }
    v = whole + sub;
    return v;
}

/**
 * 传入的record.data key下划线形式转化为驼峰形式
 *
 */
$L.formatCamelCase = function (data) {
    if (!data) return '';
    jsonToHump(data);
    return data;
}

/**
 * 将${/parameter}字符串转换成 键值对 对象
 *
 * @param {String}
 *            ${/parameter}字符串
 * @return {Object}
 */
$L.getAllParameter = function (str) {
    if (!str)return {};
    var obj = {};
    str.replaceAll(/{|}/,"").split(",").forEach(function(i){
        obj[i.split("=")[0].replace(/^\s/,"")] = i.split("=")[1];
    })
    return obj;
}
// 字符串的下划线格式转驼峰格式，eg：hello_world => helloWorld
function underline2Hump(s) {
    return s.replace(/_(\w)/g, function (all, letter) {
        return letter.toUpperCase()
    })
}

// 字符串的驼峰格式转下划线格式，eg：helloWorld => hello_world
function hump2Underline(s) {
    return s.replace(/([A-Z])/g, '_$1').toLowerCase()
}

// JSON对象的key值转换为驼峰式
function jsonToHump(obj) {
    if (obj instanceof Array) {
        obj.forEach(function (v, i) {
            jsonToHump(v)
        })
    } else if (obj instanceof Object) {
        Object.keys(obj).forEach(function (key) {
            var newKey = underline2Hump(key)
            if (newKey !== key) {
                obj[newKey] = obj[key]
                delete obj[key]
            }
            jsonToHump(obj[newKey])
        })
    }
}

// JSON对象的key值转换为下划线格式
function jsonToUnderline(obj) {
    if (obj instanceof Array) {
        obj.forEach(function (v, i) {
            jsonToUnderline(v)
        })
    } else if (obj instanceof Object) {
        Object.keys(obj).forEach(function (key) {
            var newKey = hump2Underline(key)
            if (newKey !== key) {
                obj[newKey] = obj[key]
                delete obj[key]
            }
            jsonToUnderline(obj[newKey])
        })
    }
}

/**
 * 将数值转换成带有千分位的字符串，并保留两位小数
 *
 * @param {Number}
 *            value 数值
 * @return {String}
 */
$L.formatMoney = function (v) {
    return $L.formatNumber(v, 2)
}
/**
 * 将科学技术法的数值转换成普通数值字符串
 *
 * @param {Number}
 *            value 数值
 * @return {String}
 */
$L.parseScientific = function (v) {
    if ((v = String(v)).search(/e/i) == -1) {
        return v;
    } else {
        var re = v.split(/e/i),
            doubleStr = re[0],
            negative = doubleStr.match(/-/) || '',
            inf = doubleStr.indexOf('.'),
            str = doubleStr.replace(/[-.]/g, ''),
            eStr = parseInt(re[1]) - (inf == -1 ? 0 : str.length - inf);
        if (eStr > 0) {
            for (var i = 0; i < eStr; i++) {
                str += '0';
            }
        } else {
            eStr = str.length + eStr;
            if (eStr > 0) {
                str = str.substring(0, eStr) + '.' + str.substring(eStr)
            } else {
                var prex = '0.';
                for (var i = 0; i > eStr; i--) {
                    prex += '0';
                }
                str = prex + str;
            }
        }
        return negative + str;
    }
}
/**
 * 将字符串的千分位去除
 *
 * @param {Number}
 *            value 数值
 * @param {String}
 *            rv 带有千分位的数值字符串
 * @return {Number} 数值
 */
$L.removeNumberFormat = function (rv) {
    rv = String(rv || '');
    while (rv.indexOf(',') != -1) {
        rv = rv.replace(',', '');
    }
    return isNaN(rv) ? parseFloat(rv) : rv;
}

$L.EventManager = Ext.extend(Ext.util.Observable, {
    constructor: function () {
        $L.EventManager.superclass.constructor.call(this);
        this.initEvents();
    },
    initEvents: function () {
        this.addEvents(
            'ajaxerror',
            'ajaxsuccess',
            'ajaxfailed',
            'ajaxstart',
            'ajaxcomplete',
            'valid',
            'timeout',
            'beforeunload'
        );
    }
});
$L.manager = new $L.EventManager();
$L.manager.on('ajaxstart', function () {
    $L.Status.show(_lang['eventmanager.start']);
})
$L.manager.on('timeout', function () {
    $L.Status.hide();
})
$L.manager.on('ajaxerror', function () {
    $L.Status.hide();
})
$L.manager.on('ajaxcomplete', function () {
    $L.Status.hide();
})
$L.manager.on('ajaxsuccess', function (tip) {
    $L.SideBar.show({msg: tip || _lang['eventmanager.success']})
})

$L.regEvent = function (name, hanlder) {
    $L.manager.on(name, hanlder);
}

$L.validInfoType = 'area';
$L.validInfoTypeObj = '';
$L.setValidInfoType = function (type, obj) {
    $L.validInfoType = type;
    $L.validInfoTypeObj = obj;
}

$L.invalidRecords = {};
$L.addInValidReocrd = function (id, record) {
    var rs = $L.invalidRecords[id];
    if (!rs) {
        $L.invalidRecords[id] = rs = [];
    }
    var has = false;
    for (var i = 0; i < rs.length; i++) {
        var r = rs[i];
        if (r.id == record.id) {
            has = true;
            break;
        }
    }
    if (!has) {
        rs.add(record)
    }
}
$L.removeInvalidReocrd = function (id, record) {
    var rs = $L.invalidRecords[id];
    if (!rs) return;
    for (var i = 0; i < rs.length; i++) {
        var r = rs[i];
        if (r.id == record.id) {
            rs.remove(r)
            break;
        }
    }
}
$L.getInvalidRecords = function (pageid) {
    var records = [];
    for (var key in $L.invalidRecords) {
        var ds = $L.CmpManager.get(key)
        if (ds.pageid == pageid) {
            var rs = $L.invalidRecords[key];
            records = records.concat(rs);
        }
    }
    return records;
}
$L.isInValidReocrdEmpty = function (pageid) {
    var isEmpty = true;
    for (var key in $L.invalidRecords) {
        var ds = $L.CmpManager.get(key)
        if (ds.pageid == pageid) {
            var rs = $L.invalidRecords[key];
            if (rs.length != 0) {
                isEmpty = false;
                break;
            }
        }
    }
    return isEmpty;
}
$L.manager.on('valid', function (manager, ds, valid) {
    switch ($L.validInfoType) {
        case 'area':
            $L.showValidTopMsg(ds);
            break;
        case 'message':
            $L.showValidWindowMsg(ds);
            break;
    }
})
$L.showValidWindowMsg = function (ds) {
    var empty = $L.isInValidReocrdEmpty(ds.pageid);
    if (empty == true) {
        if ($L.validWindow) $L.validWindow.close();
    }
    if (!$L.validWindow && empty == false) {
        $L.validWindow = $L.showWarningMessage(_lang['valid.fail'], '', 400, 200);
        $L.validWindow.on('close', function () {
            $L.validWindow = null;
        })
    }
    var sb = [];
    var rs = $L.getInvalidRecords(ds.pageid);
    for (var i = 0; i < rs.length; i++) {
        var r = rs[i];
        var index = r.ds.data.indexOf(r) + 1
        sb[sb.length] = _lang['valid.fail.note'] + '<a href="#" onclick="$le(\'' + r.ds.id + '\').locate(' + index + ')">(' + r.id + ')</a>:';

        for (var k in r.valid) {
            sb[sb.length] = r.valid[k] + ';'
        }
        sb[sb.length] = '<br/>';
    }
    if ($L.validWindow) $L.validWindow.body.child('div').update(sb.join(''))
}
$L.pageids = [];
$L.showValidTopMsg = function (ds) {
    var empty = $L.isInValidReocrdEmpty(ds.pageid);
    if (empty == true) {
        var d = Ext.get(ds.pageid + '_msg');
        if (d) {
            d.hide();
            d.setStyle('display', 'none')
            d.update('');
        }
        return;
    }
    var rs = $L.getInvalidRecords(ds.pageid);
    var sb = [];
    for (var i = 0; i < rs.length; i++) {
        var r = rs[i];
        var index = r.ds.data.indexOf(r) + 1
        sb[sb.length] = _lang['valid.fail.note'] + '<a href="#" onclick="$le(\'' + r.ds.id + '\').locate(' + index + ')">(' + r.id + ')</a>:';

        for (var k in r.valid) {
            sb[sb.length] = r.valid[k] + ';'
        }
        sb[sb.length] = '<br/>';
    }
    var d = Ext.get(ds.pageid + '_msg');
    if (d) {
        d.update(sb.join(''));
        d.show(true);
    }
}
Ext.fly(document.documentElement).on('keydown', function (e, t) {
// if(e.altKey&&e.keyCode == 76){
// if(!$L.logWindow) {
// $L.logWindow = new $L.Window({modal:false,
// url:'log.screen',title:'AjaxWatch', height:550,width:530});
// $L.logWindow.on('close',function(){
// delete $L.logWindow;
// })
// }
// }
    var tagName = t.tagName.toUpperCase();
    e.keyCode == 8 && tagName != 'INPUT' && tagName != 'TEXTAREA' && e.stopEvent();
})
$L.startCustomization = function () {
    var cust = $L.CmpManager.get('_customization');
    if (cust == null) {
        cust = new $L.Customization({id: '_customization'});
    }
    cust.start();
}
$L.stopCustomization = function () {
    var cust = $L.CmpManager.get('_customization');
    if (cust != null) {
        cust.stop();
        $L.CmpManager.remove('_customization');
    }
}
/**
 * 将数字金额转换成大写金额.
 *
 * @param {Number}
 *            amount 金额
 * @return {String} 大写金额
 */
$L.convertMoney = function (mnum) {
    mnum = Math.abs(mnum);
    var unitArray = [["元", "万", "亿"], ["仟", "", "拾", "佰"], ["零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"]];
    totalarray = new Array();

    totalarray = mnum.toString().split(".");
    if (totalarray.length == 1) {
        totalarray[1] = "00"
    } else if (totalarray[1].length == 1) {
        totalarray[1] += '0';
    }
    integerpart = new Array();
    decimalpart = new Array();
    var strout = "";
    for (var i = 0; i < totalarray[0].length; i++) {
        integerpart[i] = totalarray[0].charAt(i);
    }
    for (var i = 0; i < totalarray[1].length; i++) {
        decimalpart[i] = totalarray[1].charAt(i);
    }
    for (var i = 0; i < integerpart.length; i++) {
        var strTemp = (integerpart.length - i - 1) % 4 == 0
            ? unitArray[0][parseInt((integerpart.length - i) / 4)]
            : (integerpart[i] == 0)
                ? ""
                : unitArray[1][((integerpart.length - i) % 4)]
        strout = strout + unitArray[2][integerpart[i]] + strTemp;
    }
    strout = strout.replace(new RegExp(/零+/g), "零");
    strout = strout.replace("零万", "万");
    strout = strout.replace("零亿", "亿");
    strout = strout.replace("零元", "元");
    strout = strout.replace("亿万", "亿");
    var strdec = ""
    if (decimalpart[0] == 0 && decimalpart[1] == 0) {
        strdec = "整";
    } else {
        if (decimalpart[0] == 0) {
            strdec = "零"
        } else {
            strdec = unitArray[2][decimalpart[0]] + '角';
        }
        if (decimalpart[1] != 0) {
            strdec += unitArray[2][decimalpart[1]] + '分';
        }
    }
    strout += strdec;
    if (mnum < 0)
        strout = "负" + strout;
    return strout;
}
$L.setValidInfoType('tip');

$L.escapeHtml = function (str) {
    if (Ext.isEmpty(str) || !Ext.isString(str))
        return str;
    return String(str).replace(/&/gm, '&amp;').replace(/\"/gm, '&quot;').replace(/\(/gm, '&#40;').replace(/\)/gm, '&#41;').replace(/\+/gm, '&#43;').replace(/\%/gm, '&#37;')
        .replace(/</gm, '&lt;').replace(/>/gm, '&gt;');
}
$L.unescapeHtml = function (str) {
    if (Ext.isEmpty(str) || !Ext.isString(str))
        return str;
    return String(str).replace(/&amp;/gm, '&').replace(/&quot;/gm, '"').replace(/&#40;/gm, '(').replace(/&#41;/gm, ')').replace(/&#43;/gm, '+').replace(/&#37;/gm, '%')
        .replace(/&lt;/gm, '<').replace(/&gt;/gm, '>');
}
$L.doExport = function (dataset, cols, mergeCols, type, separator, filename, generate_state, param) {
//
    var p = {"parameter": {"_column_config_": {}}}, columns = [], parentMap = {},
        _parentColumn = function (pcl, cl) {
            if (!(Ext.isDefined(pcl.forexport) ? pcl.forexport : true))return null;
            var json = Ext.encode(pcl);
            var c = parentMap[json];
            if (!c) c = {prompt: pcl.prompt};
            parentMap[json] = c;
            (c["column"] = c["column"] || []).add(cl);
            if (pcl._parent) {
                return _parentColumn(pcl._parent, c)
            }
            return c;
        };
    for (var i = 0; i < cols.length; i++) {
        var column = cols[i], forExport = Ext.isDefined(column.forexport) ? column.forexport : true;
        if (column.type != 'rowcheck' && column.type != 'rowradio' && column.type != 'rownumber' && forExport) {
            var c = {prompt: column.prompt}
            if (column.width) c.width = column.width;
            if (column.name) c.name = column.exportfield || column.name;
            if (column.exportdatatype) c.datatype = column.exportdatatype;
            if (column.exportdataformat) c.dataformat = column.exportdataformat;
            c.align = column.align || "left";
            var o = column._parent ? _parentColumn(column._parent, c) : c;
            if (o) columns.add(o);
        }
    }
    p["parameter"]["_column_config_"]["column"] = columns;
    p["_generate_state"] = Ext.isEmpty(generate_state) ? true : generate_state;
    p["_format"] = type || "xlsx";
    if (separator) p["separator"] = separator;
    if (filename) p["_file_name_"] = filename;
    if (mergeCols) {
        var _merge_column_ = [];
        Ext.each(mergeCols, function (item) {
            _merge_column_.push({name: item});
        });
        p["parameter"]["_merge_column_"] = _merge_column_;
    }
    var r, q = param || {};
    if (dataset.qds) r = dataset.qds.getCurrentRecord();
    if (r) Ext.apply(q, r.data);
    Ext.apply(q, dataset.qpara);
    for (var k in q) {
        if (Ext.isEmpty(q[k], false) || q[k].xtype=='dataset') delete q[k];
    }
    Ext.apply(p.parameter, q)
    var form = document.createElement("form");
    form.target = "_export_window";
    form.method = "post";
    var url = dataset.queryurl;
    var token = $jq('meta[name=_csrf]').attr('content');
    if (url) {
        if (dataset && dataset.qds && dataset.querymethod == 'GET'){
            var rpara = dataset.qds.getCurrentRecord().data;
            if(rpara){
                var paraStr = '';
                for(var key in rpara){
                    paraStr += '&'+key+'='+rpara[key];
                }
                if(paraStr.length > 0) {
                    url = url + (url.lastIndexOf('?') > 0 ? paraStr : '?' + paraStr.substring(1));
                }
            }
        }
        form.action = url + (url.indexOf('?') == -1 ? '?' : '&') + 'r=' + Math.random()+"&_csrf="+token;
    }
    var iframe = Ext.get('_export_window') || new Ext.Template('<iframe id ="_export_window" name="_export_window" style="position:absolute;left:-10000px;top:-10000px;width:1px;height:1px;display:none"></iframe>').insertFirst(document.body, {}, true)
    var s = document.createElement("input");
    s.id = "_request_data";
    s.type = 'hidden';
    s.name = '_request_data';
    s.value = Ext.encode(p);
    form.appendChild(s);
    document.body.appendChild(form);
    form.submit();
    Ext.fly(form).remove();
}


$L.isChinese = function (value) {
    return /^[\u4E00-\u9FA5_%]+$/.test(value.trim());
}
$L.isLetter = function (value) {
    return /^[a-zA-Z_%]+$/.test(value.trim());
}
$L.isUpperCase = function (value) {
    return /^[A-Z_%]+$/.test(value.trim());
}
$L.isLowerCase = function (value) {
    return /^[a-z_%]+$/.test(value.trim());
}
$L.isNumber = function (value) {
    return Ext.isNumber(Number(value));
}
$L.isDate = function () {
    var formats = [
        'mm/dd/yyyy',
        'yyyy-mm-dd'
    ];
    return function (value) {
        if (!Ext.isString(value))return false;
        for (var i = formats.length; i--;) {
            try {
                value.parseDate(formats[i]);
                return true;
            } catch (e) {
            }
        }
        return false;
    }
}();
$L.isEmpty = Ext.isEmpty;
$L.checkNotification = function (cmps) {
    var result = null;
    if (Ext.isObject(cmps)) {
        for (var key in cmps) {
            var cmp = cmps[key];
            if (cmp.cmps) {
                result = $L.checkNotification(cmp.cmps)
            } else if (!result && cmp instanceof Leaf.DataSet) {
                result = (cmp.notification && cmp.isModified()) ? cmp.notification : null;
            }
            if (result)break;
        }
    }
    return result;
}

window.onbeforeunload = function () {
    var message = [];
    $L.manager.fireEvent('beforeunload', message);
    if (message.length != 0) return message[0];
}
if (Ext.isIE) {// for fix IE event's order bug
    (function () {
        var elProto = Ext.Element.prototype,
            on = elProto.on,
            un = elProto.un,
            objs = {};
        elProto.on = elProto.addListener = function (eventName, handler, scope, opt) {
            var sf = this, listeners = objs[sf.id] || (objs[sf.id] = []);
            sf.un(eventName, handler, scope);
            on.call(sf, eventName, handler, scope, opt);
            Ext.each(listeners, function (obj) {
                var _e = obj.eventName,
                    _h = obj.handler,
                    _s = obj.scope;
                un.call(sf, _e, _h, _s);
                on.call(sf, _e, _h, _s, obj.opt);
            });
            listeners.unshift({
                eventName: eventName,
                handler: handler,
                scope: scope,
                opt: opt
            });
            return sf;
        }
        elProto.un = elProto.removeListener = function (eventName, handler, scope) {
            var sf = this, listeners = objs[sf.id],
                index = Ext.each(listeners, function (obj) {
                    if (obj.eventName === eventName && obj.handler == handler && obj.scope == scope) {
                        return false;
                    }
                });
            if (Ext.isDefined(index)) {
                listeners.splice(index, 1);
            }
            un.call(sf, eventName, handler, scope);
            return sf;
        }
    })();
}
/*
 * else if(!Ext.isIE9 && !Ext.isIE10) { (function(){ var _proto =
 * Number.prototype, _toFixed = _proto.toFixed; _proto.toFixed =
 * function(deci) { var s = String(this); if(s.indexOf('e') != -1){ var arr =
 * s.split('e'); if(arr[1]<0){ if(arr[0].indexOf('.') == -1){ arr[1] -=1; }
 * return _toFixed.call(Number(arr[0]+1+'e'+arr[1]),deci); }else{ return
 * _toFixed.call(this,deci); } }else if(s.indexOf('.') == -1){ return
 * _toFixed.call(this,deci); }else{ return _toFixed.call(Number(s+1),deci); } }
 * })(); }
 */
$L.FixMath = (function () {
    var POW = Math.pow,
        mul = function (a, b) {
            var m = 0, s1 = String(a), s2 = String(b),
                l1 = s1.indexOf('.'),
                l2 = s2.indexOf('.'),
                e1 = s1.indexOf('e'),
                e2 = s2.indexOf('e');
            if (e1 != -1) {
                m -= Number(s1.substr(e1 + 1));
                s1 = s1.substr(0, e1);
            }
            if (e2 != -1) {
                m -= Number(s2.substr(e2 + 1));
                s2 = s2.substr(0, e2);
            }
            if (l1 != -1) m += s1.length - l1 - 1;
            if (l2 != -1) m += s2.length - l2 - 1;
            return Number(s1.replace('.', '')) * Number(s2.replace('.', '')) / POW(10, m);
        },
        div = function (a, b) {
            var re = String(a / b),
                i = re.indexOf('.');
            if (i != -1) {
                re = Number(re).toFixed(16 - i - 1)
            }
            return Number(re);
        },
        plus = function (a, b) {
            var m1 = 0, m2 = 0, m3,
                s1 = String(a), s2 = String(b),
                l1 = s1.indexOf('.'),
                l2 = s2.indexOf('.'),
                e1 = s1.indexOf('e'),
                e2 = s2.indexOf('e');
            if (e1 != -1) {
                m1 -= Number(s1.substr(e1 + 1));
                s1 = s1.substr(0, e1);
            }
            if (e2 != -1) {
                m2 -= Number(s2.substr(e2 + 1));
                s2 = s2.substr(0, e2);
            }
            if (l1 != -1) m1 += s1.length - l1 - 1;
            if (l2 != -1) m2 += s2.length - l2 - 1;
            if (m2 > m1) {
                m3 = m2;
                m1 = m2 - m1;
                m2 = 0;
            } else if (m1 > m2) {
                m3 = m1;
                m2 = m1 - m2;
                m1 = 0;
            } else {
                m3 = m1;
                m1 = m2 = 0;
            }
            return (Number(s1.replace('.', '')) * POW(10, m1) + Number(s2.replace('.', '')) * POW(10, m2)) / POW(10, m3);
        },
        minus = function (a, b) {
            return plus(a, -b);
        },
        pow = function (a, b) {
            var re = String(POW(a, b)),
                i = re.indexOf('.');
            if (i != -1) {
                re = Number(re).toFixed(16 - i - 1)
            }
            return Number(re);
        };
    return {
        pow: pow,
        minus: minus,
        plus: plus,
        div: div,
        mul: mul
    }
})();

$L.merge = function () {
    function clone(object) {
        var _clone = function () {
        };
        _clone.prototype = object
        return new _clone();
    }

    function mergeOne(source, key, current) {
        if (Ext.isObject(current)) {
            if (!Ext.isObject(source[key])) source[key] = clone(current)
            _merge(source[key], current);
        } else if (Ext.isArray(current)) {
            source[key] = [].concat(current);
        } else {
            source[key] = current;
        }
        return source;
    }

    function _merge(source, k, v) {
        if (Ext.isString(k)) return mergeOne(source, k, v);
        for (var i = 1, l = arguments.length; i < l; i++) {
            var object = arguments[i];
            for (var key in object) mergeOne(source, key, object[key]);
        }
        return source;
    }

    return function () {
        return _merge.apply(null, Ext.toArray(arguments));
    }
}();
/**
 * @class Leaf.DataSet
 * @extends Ext.util.Observable
 *          <p>
 *          DataSet是一个数据源，也是一个数据集合，它封装了所有数据的操作，校验，提交等操作.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.AUTO_ID = 1000;
$L.DataSet = Ext.extend(Ext.util.Observable, {
    constructor: function (config) {// datas,fields, type
        var sf = this;
        $L.DataSet.superclass.constructor.call(this);
        config = config || {};
        this.querymethod = config.querymethod;
        this.deletemethod = config.deletemethod;
        this.deleteurl = config.deleteurl || '';
        if (config.listeners) {
            this.on(config.listeners);
        }
        this.validateEnable = true;
        this.pageid = config.pageid;
        this.spara = {};
        this.notification = config.notification;
        this.selected = [];
        this.sorttype = config.sorttype || 'remote';
        this.maxpagesize = config.maxpagesize || 1000;
        this.indexchangedisquery = config.indexchangedisquery || false;
        this.pagesize = config.pagesize || 10;
        if (this.pagesize > this.maxpagesize)
            this.pagesize = this.maxpagesize;
        this.submiturl = config.submiturl || '';
        this.queryurl = config.queryurl || '';
        this.fetchall = config.fetchall || false;
        // Hybris
        this.dtoname = config.dtoname || '';
        this.hybrisws = config.hybrisws;
        this.restDataFormat = config.restdataformat;

        this.totalcountfield = config.totalcountfield || 'totalCount';
        this.selectable = config.selectable || false;
        this.selectionmodel = config.selectionmodel || 'multiple';
        this.selectfunction = config.selectfunction;
        this.autocount = config.autocount;
        this.autopagesize = config.autopagesize;
        this.bindtarget = config.bindtarget;
        this.bindname = config.bindname;
        this.processfunction = config.processfunction;
        this.modifiedcheck = config.modifiedcheck;
        this.loading = false;
        this.qpara = {};
        this.fields = {};
        this.resetConfig();
        this.id = config.id || Ext.id();
        this.hostid = config.hostid;
        this.keepData = false;
        this.prompt = config.prompt;
        $L.CmpManager.put(this.id, this)
        if (this.bindtarget && this.bindname) this.bind($le(this.bindtarget), this.bindname);// $le(this.bindtarget).bind(this.bindname,this);
        this.qds = Ext.isEmpty(config.querydataset) ? null : $le(config.querydataset);
        if (this.qds != null && this.qds.getCurrentRecord() == null) this.qds.create();
        this.initEvents();
        if (config.fields) this.initFields(config.fields)
        if (config.datas && config.datas.length != 0) {
            var datas = config.datahead ? this.convertData(config.datahead, config.datas) : config.datas;
            this.autocount = false;
            this.loadData(datas);
// $L.onReady(function(){
// sf.locate(sf.currentIndex,true); //不确定有没有影响
// });
        }
        if (config.autoquery === true) {
            $L.onReady(function () {
                sf.query();
            });
        }
        if (config.autocreate == true) {
            if (this.data.length == 0)
                this.create();
        }
    },
    convertData: function (head, datas) {
        var nds = [];
        for (var i = 0; i < datas.length; i++) {
            var d = datas[i], nd = {};
            for (var j = 0; j < head.length; j++) {
                if (!Ext.isEmpty(d[j], true))
                    nd[head[j]] = d[j];
            }
            nds.push(nd);
        }
        return nds;
    },
    destroy: function () {
        var sf = this, id = sf.id, o = sf.qtId,
            bindtarget = sf.bindtarget,
            bindname = sf.bindname,
            manager = $L.CmpManager;
        sf.processListener('un');
        o &&
        Ext.Ajax.abort(o);
        bindtarget && bindname && (o = manager.get(bindtarget)) &&
        o.clearBind(bindname);
        Ext.iterate(sf.fields, function (key, field) {
            field.type == 'dataset' &&
            sf.clearBind(key);
        });
        manager.remove(id);
        delete $L.invalidRecords[id]
    },
    reConfig: function (config) {
//    	 this.resetConfig();
//	     Ext.apply(this, config);
        /*
         * 向平 2017.10.19
         * 修复isModified方法导致数据丢失的问题
         */
        if (config) {
            this.resetConfig();
            Ext.apply(this, config);
        }
    },
    /**
     * 取消绑定.
     */
    clearBind: function (name) {
        var sf = this, fields = sf.fields,
            ds = fields[name].pro['dataset'];
        ds &&
        ds.processBindDataSetListener(sf, 'un');
        delete fields[name];
        Ext.each(sf.getAll(), function (r) {
            r.data[name] = null;
            delete r.data[name];
        });
    },
    processBindDataSetListener: function (ds, ou) {
        var bdp = this.onDataSetModify;
// this[ou]('beforecreate', this.beforeCreate, this);//TODO:有待测试
        this[ou]('add', bdp, this);
        this[ou]('remove', bdp, this);
        this[ou]('select', this.onDataSetSelect, this);
        this[ou]('update', bdp, this);
        this[ou]('indexchange', bdp, this);
        this[ou]('clear', bdp, this);
        this[ou]('load', this.onDataSetLoad, this);
        this[ou]('reject', bdp, this);
        ds[ou]('indexchange', this.onDataSetIndexChange, this);
        ds[ou]('load', this.onBindDataSetLoad, this);
        ds[ou]('remove', this.onBindDataSetLoad, this);
        ds[ou]('clear', this.removeAll, this);
    },
    /**
     * 将组件绑定到某个DataSet的某个Field上.
     *
     * @param {Leaf.DataSet}
     *            dataSet 绑定的DataSet.
     * @param {String}
     *            name Field的name.
     *
     */
    bind: function (ds, name) {
        if (ds.fields[name]) {
            alert('重复绑定 ' + name);
            return;
        }
        this.processBindDataSetListener(ds, 'un');
        this.processBindDataSetListener(ds, 'on');
        var field = new $L.Record.Field({
            name: name,
            type: 'dataset',
            dataset: this
        });
        ds.fields[name] = field;
    },
    onBindDataSetLoad: function (ds, options) {
        if (ds.getAll().length == 0) this.removeAll();
    },
    onDataSetIndexChange: function (ds, record) {
        if (record.isNew != true) {
            this.qpara = {};
            Ext.apply(this.qpara, record.data);
        }
        if (!this.indexchangedisquery) {
            //未设置indexchangedisquery=true时 每次indexchange都要重新查询
            this.query(1, {record: record});
        }else if(!record.get(this.bindname) && record.isNew != true){
            //设置indexchangedisquery=true时，根据“数据不存在 且 数据未被更改”来判断是否重新查询
            this.query(1, {record: record});
        }
    },
    onDataSetModify: function () {
        var bt = $L.CmpManager.get(this.bindtarget);
        if (bt) {
            this.refreshBindDataSet(bt.getCurrentRecord(), this.getConfig())
        }
    },
    onDataSetSelect: function (ds, record) {
        var bt = $L.CmpManager.get(this.bindtarget);
        if (bt) {
            var datas = bt.data;
            var found = false;
            for (var i = 0; i < datas.length; i++) {
                var dr = datas[i];
                var dc = dr.get(this.bindname);
                if (dc) {
                    for (var j = 0; j < dc.data.length; j++) {
                        var r = dc.data[j];
                        if (r.id == record.id) {
                            dc.selected = this.selected;
                            found = true;
                            break;
                        }
                    }
                    if (found) break;
                }
            }
        }
    },
    onDataSetLoad: function (ds, options) {
        var record;
        if (options && options.opts && options.opts.record) {
            record = options.opts.record;
        } else {
            var bt = $L.CmpManager.get(this.bindtarget);
            if (bt) record = bt.getCurrentRecord();
        }
        if (record)
            this.refreshBindDataSet(record, ds.getConfig())
    },
    refreshBindDataSet: function (record, config) {
        if (!record)return;
        // record.set(this.bindname,config,true)//this.getConfig()
        record.data[this.bindname] = config;

// for(var k in this.fields){
// var field = this.fields[k];
// if(field.type == 'dataset'){
// var ds = field.pro['dataset'];
// // if(ds && clear==true)ds.resetConfig();
// record.set(field.name,ds.getConfig(),true)
// }
// }
    },
    beforeCreate: function (ds, record, index) {
        if (this.data.length == 0) {
            this.create({}, false);
        }
    },
    resetConfig: function () {
        this.data = [];
        this.selected = [];
        this.gotoPage = 1;
        this.currentPage = 1;
        this.currentIndex = 1;
        this.totalCount = 0;
        this.totalPage = 0;
        this.isValid = true;
// this.bindtarget = null;
// this.bindname = null;
    },
    getConfig: function () {
        var c = {};
// c.id = this.id;
        c.xtype = 'dataset';
        c.data = this.data;
        c.selected = this.selected;
        c.isValid = this.isValid;
// c.bindtarget = this.bindtarget;
// c.bindname = this.bindname;
        c.gotoPage = this.gotoPage;
        c.currentPage = this.currentPage;
        c.currentIndex = this.currentIndex;
        c.totalCount = this.totalCount;
        c.totalPage = this.totalPage;
        c.fields = this.fields;
        return c;
    },
    processListener: function (ou) {
        if (this.notification) {
            $L.manager[ou]('beforeunload', this.onBeforeUnload, this)
        }
    },
    onBeforeUnload: function (ms) {
        if (this.isModified()) {
            ms.add(this.notification);
        }
    },
    initEvents: function () {
        this.addEvents(
            /**
             * @event ajaxfailed ajax调用失败.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Object}
             *            res res.
             * @param {Object}
             *            opt opt.
             */
            'ajaxfailed',
            /**
             * @event beforecreate 数据创建前事件.返回true则新增一条记录,false则不新增直接返回
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Object}
             *            object 新增的数据对象.
             */
            'beforecreate',
            /**
             * @event metachange meta配置改变事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 当前的record.
             * @param {Leaf.Record.Meta}
             *            meta meta配置对象.
             * @param {String}
             *            type 类型.
             * @param {Object}
             *            value 值.
             */
            'metachange',
            /**
             * @event fieldchange field配置改变事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 当前的record.
             * @param {Leaf.Record.Field}
             *            field Field配置对象.
             * @param {String}
             *            type 类型.
             * @param {Object}
             *            value 值.
             */
            'fieldchange',
            /**
             * @event add 数据增加事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 增加的record.
             * @param {Number}
             *            index 指针.
             */
            'add',
            /**
             * @event remove 数据删除事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 删除的record.
             * @param {Number}
             *            index 指针.
             */
            'remove',
            /**
             * @event beforeremove 数据删除前.如果为true则删除一条记录,false则不删除直接返回
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Array}
             *            records 将要删除的数据集合
             */
            'beforeremove',
            /**
             * @event afterremove
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'afterremove',
            /**
             * @event beforeupdate 数据更新前.如果为true则更新记录,false直接返回
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 更新的record.
             * @param {String}
             *            name 更新的field.
             * @param {Object}
             *            value 更新的值.
             * @param {Object}
             *            oldvalue 更新前的值.
             */
            'beforeupdate',
            /**
             * @event update 数据更新事件. "update", this, record, name, value
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 更新的record.
             * @param {String}
             *            name 更新的field.
             * @param {Object}
             *            value 更新的值.
             * @param {Object}
             *            oldvalue 更新前的值.
             */
            'update',
            /**
             * @event clear 清除数据事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'clear',
            /**
             * @event query 查询事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Object}
             *            queryParam 参数.
             * @param {Object}
             *            options 选项.
             */
            'query',
            /**
             * @event beforeload 准备加载数据事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'beforeload',
            /**
             * @event load 加载数据事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'load',
            /**
             * @event loadfailed 加载数据失败.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Object}
             *            res res.
             * @param {Object}
             *            opt opt.
             */
            'loadfailed',
            /**
             * @event refresh 刷新事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'refresh',
            /**
             * @event valid DataSet校验事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 校验的record.
             * @param {String}
             *            name 校验的field.
             * @param {Boolean}
             *            valid 校验结果. true 校验成功 false 校验失败
             */
            'valid',
            /**
             * @event indexchange DataSet当前指针改变事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 当前record.
             */
            'indexchange',
            /**
             * @event beforeselect 选择数据前事件. 返回true表示可以选中,false表示不能选中
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 选择的record.
             */
            'beforeselect',
            /**
             * @event select 选择数据事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 选择的record.
             */
            'select',
            /**
             * @event unselect 取消选择数据事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 取消选择的record.
             */
            'unselect',
            /**
             * @event selectall 选择数据事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'selectall',
            /**
             * @event unselectall 取消选择数据事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'unselectall',
            /**
             * @event reject 数据重置事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Leaf.Record}
             *            record 取消选择的record.
             * @param {String}
             *            name 重置的field.
             * @param {Object}
             *            value 重置的值.
             */
            'reject',
            /**
             * @event wait 等待数据准备事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'wait',
            /**
             * @event afterwait 等待数据准备完毕事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'afterwait',
            /**
             * @event beforesubmit 数据提交前事件.如果为false则中断提交请求
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             */
            'beforesubmit',
            /**
             * @event submit 数据提交事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {String}
             *            url 提交的url.
             * @param {Array}
             *            datas 提交的数据.
             */
            'submit',
            /**
             * @event submitsuccess 数据提交成功事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Object}
             *            res 返回结果res.
             */
            'submitsuccess',
            /**
             * @event submitfailed 数据提交失败事件.
             * @param {Leaf.DataSet}
             *            dataSet 当前DataSet.
             * @param {Object}
             *            res 返回结果res.
             */
            'submitfailed'
        );
        this.processListener('on');
    },
    addField: function (fd, notCheck) {
        if (notCheck !== true) {
            var rf = fd.returnfield,
                vf = fd.valuefield;
            if (rf && vf) {
                var mapping = fd.mapping || [], has = false;
                for (var i = 0, l = mapping.length; i < l; i++) {
                    var m = mapping[i];
                    if (m.from == vf && m.to == rf) {
                        has = true
                        break;
                    }
                }
                if (!has) {
                    mapping.push({from: vf, to: rf});
                    fd.mapping = mapping;
                }
            }
        }
        var field = new $L.Record.Field(fd);
        this.fields[field.name] = field;
    },
    removeField: function (name) {
        this.fields[name] = null;
        delete this.fields[name];
    },
    initFields: function (fields) {
        for (var i = 0, len = fields.length; i < len; i++) {
            this.addField(fields[i], true);
        }
    },
    /**
     * 获取Field配置.
     *
     * @param {String}
     *            name Field的name.
     * @return {Leaf.Record.Field} field配置对象
     */
    getField: function (name) {
        return this.fields[name];
    },
    beforeLoadData: function (datas) {
        if (this.processfunction) {
            var fun = $L.getRenderer(this.processfunction);
            if (fun) {
                return fun.call(window, datas);
            }
        }
        return datas;
    },
    clearFilter: function () {
        if (this.backup) {
            this.data = this.backup;
            delete this.backup;
        }
    },
    filter: function (callback, scope) {
        var d = this.backup || this.data, nd = [];
        this.backup = d;
        Ext.each(d, function (o) {
            if (callback.call(scope || this, o, nd) !== false) {
                nd.push(o);
            }
        }, this)
        this.data = nd;
    },
    loadData: function (datas, num, options) {
        this.clearFilter();
        datas = this.beforeLoadData(datas);
        // 保留原数据的情况，不需要对数据进行
        if (!this.keepData) {
            this.data = [];
        }
        this.selected = [];
        if (num && this.fetchall == false) {
            this.totalCount = num;
            this.totalPage = Math.ceil(this.totalCount / this.pagesize);
        } else {
            this.totalCount = datas.length;
            this.totalPage = 1;
        }


        for (var i = 0, len = datas.length; i < len; i++) {
            var data = datas[i].data || datas[i];
            for (var key in this.fields) {
                var field = this.fields[key];
                if (field) {
                    data[key] = this.processData(data, key, field)
                }
            }
            var record = new $L.Record(data, datas[i].field);
            record.setDataSet(this);
            this.data.push(record);
        }
// if(this.sortInfo) this.sort();

        this.fireEvent("beforeload", this, this.data);

        var needFire = true;
        if (this.bindtarget && options) {
            var cr = $L.CmpManager.get(this.bindtarget).getCurrentRecord();
            if (options.opts.record && cr != options.opts.record) {
                this.refreshBindDataSet(options.opts.record, this.getConfig());
                needFire = false;
            }
        }
        if (needFire)
            this.fireEvent("load", this, options);

        // 每次完成数据加载后，重置一下KeepData状态为false
        this.keepData = false;
    },
    sort: function (f, direction) {
        if (this.getAll().length == 0)return;
        if (this.sorttype == 'remote') {
            if (direction == '') {
                delete this.qpara['sort_name'];
                delete this.qpara['sort_order'];
            } else {
                this.setQueryParameter('sort_name', f);
                this.setQueryParameter('sort_order', direction);
            }
            this.query();
        } else {
            this.data.sort(function (a, b) {
                var rs = a.get(f) > b.get(f)
                return (direction == 'desc' ? (rs ? -1 : 1) : (rs ? 1 : -1));
            })
            this.fireEvent('refresh', this);
        }
    },
    /**
     * 创建一条记录
     *
     * @param {Object}
     *            data 数据对象
     * @param {Number}
     *            index 指定位置.若不指定则添加到最后.
     * @return {Leaf.Record} record 返回创建的record对象
     */
    create: function (data, index) {
        if (Ext.isNumber(data)) {
            index = data;
            data = null;
        }
// var dirty = !!data;//MAS云新增特性
        data = data || {}
        if (this.fireEvent("beforecreate", this, data)) {
            // if(valid !== false) if(!this.validCurrent())return;
            var dd = {};
            for (var k in this.fields) {
                var field = this.fields[k],
                    dv = field.getPropertity('defaultvalue'),
                    name = field.name;
                if (dv && !data[name]) {
                    dd[name] = dv;
                    dd[name] = this.processData(dd, name, field);
                } else {
                    dd[name] = this.processData(data, name, field);
// dd[field.name] = this.processValueListField(dd,data[field.name],field);
                }
            }
            var data = Ext.apply(data || {}, dd);
            var record = new $L.Record(data);
// if(dirty)record.dirty = true;//MAS云新增特性
            this.add(record, index)
            // var index = (this.currentPage-1)*this.pagesize + this.data.length;
            // this.locate(index, true);
            return record;
        }
    },
    /**
     * 获取所有新创建的数据.
     *
     * @return {Array} 所有新创建的records
     */
    getNewRecords: function () {
        var records = this.getAll();
        var news = [];
        for (var k = 0, l = records.length; k < l; k++) {
            var record = records[k];
            if (record.isNew == true) {
                news.push(record);
            }
        }
        return news;
    },
// validCurrent : function(){
// var c = this.getCurrentRecord();
// if(c==null)return true;
// return c.validateRecord();
// },
    /**
     * 新增数据.
     *
     * @param {Leaf.Record}
     *            record 需要新增的Record对象.
     * @param {Number}
     *            index 指定位置.若不指定则添加到最后.
     */
    add: function (record, index) {
        var d = this.data;
        if (d.indexOf(record) != -1)return;
        if (Ext.isEmpty(index) || index > d.length) index = d.length;
        record.isNew = true;
        record.setDataSet(this);
// var index = this.data.length;
        d.splice(index, 0, record);
// for(var k in this.fields){
// var field = this.fields[k];
// if(field.type == 'dataset'){
// var ds = field.pro['dataset'];
// ds.resetConfig()
// }
// }
        this.currentIndex = (this.currentPage - 1) * this.pagesize + index + 1;
        this.fireEvent("add", this, record, index);
        this.locate(this.currentIndex, true);
    },
    /**
     * 获取当前Record的数据对象
     *
     * @return {Object}
     */
    getCurrentObject: function () {
        return this.getCurrentRecord().getObject();
    },
    /**
     * 获取当前指针的Record.
     *
     * @return {Leaf.Record} 当前指针所处的Record
     */
    getCurrentRecord: function () {
        if (this.data.length == 0) return null;
        return this.data[this.currentIndex - (this.currentPage - 1) * this.pagesize - 1];
    },
    /**
     * 移除数据.
     *
     * @param {Leaf.Record}
     *            record 需要移除的Record.
     */
    remove: function (record) {
        if (!record) {
            record = this.getCurrentRecord();
        }
        if (!record)return;
        var rs = [].concat(record);
        if (this.fireEvent("beforeremove", this, rs)) {
            var rrs = [];
            for (var i = 0; i < rs.length; i++) {
                var r = rs[i]
                if (r.isNew) {
                    this.removeLocal(r);
                } else {
                    rrs[rrs.length] = r;
                }
            }
            this.removeRemote(rrs);
        } else {
            this.fireEvent("submitfailed", this, rs);
        }
    },
    removeRemote: function (rs) {
        var sf = this,
            submiturl = sf.deleteurl || sf.submiturl,
            p = [],
            isRest = /\/rest\//.test(submiturl) || sf.hybrisws,
            dtoName = sf.dtoname;
        if (Ext.isEmpty(submiturl)) return;
        Ext.each(rs, function (r) {
            var data = {
                _id: r.id,
                _status: 'delete'
            };
            Ext.iterate(sf.fields, function (key, f) {
                if (f && f.type == 'dataset') delete r.data[key];
            });
            data = Ext.apply(data, r.data);
            p.push(data);
        });
// var p = [d];
// for(var i=0;i<p.length;i++){
// p[i] = Ext.apply(p[i],sf.spara)
// }
        if (p.length) {
            $L.request(Ext.apply({
                url: submiturl,
                para: p,
                ext: sf.spara,
                success: sf.onRemoveSuccess,
                error: sf.onSubmitError,
                scope: sf,
                method:sf.deletemethod,
                failure: sf.onAjaxFailed,
                opts: sf.bindtarget ? {record: $L.CmpManager.get(sf.bindtarget).getCurrentRecord(), dataSet: sf} : null
            }, isRest ? {
                    isRest: true,
                    dtoName: dtoName,
                    restDataFormat: sf.restDataFormat,
                    // method:'Delete',
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json'
                    }
                } : {}));
        }

    },
    onRemoveSuccess: function (res, options) {
        if (res.result.record) {
            var datas = [].concat(res.result.record);
            if (this.bindtarget) {
                var bd = $L.CmpManager.get(this.bindtarget);
                if (bd.getCurrentRecord() == options.opts.record) {
                    for (var i = 0; i < datas.length; i++) {
                        var data = datas[i];
                        this.removeLocal(this.findById(data['_id'] || data['__id']), true);
                    }
                } else {
                    var config = options.opts.record.get(this.bindname);
                    var ds = new $L.DataSet({});
                    ds.reConfig(config);
                    for (var i = 0; i < datas.length; i++) {
                        var data = datas[i];
                        ds.removeLocal(ds.findById(data['_id'] || data['__id']), true);
                    }
                    this.refreshBindDataSet(options.opts.record, ds.getConfig())
                    delete ds;
                }
            } else {
                for (var i = 0; i < datas.length; i++) {
                    var data = datas[i];
                    this.removeLocal(this.findById(data['_id'] || data['__id']), true);
                }
            }
            this.fireEvent('afterremove', this);
        }
    },
    removeLocal: function (record, count, notLocate) {
        $L.removeInvalidReocrd(this.id, record)
        var index = this.data.indexOf(record);
        if (index == -1)return;
        this.data.remove(record);
        if (count) this.totalCount--;
        this.selected.remove(record);
// if(this.data.length == 0){
// this.removeAll();
// return;
// }
        if (!notLocate)
            if (this.data.length != 0) {
                var lindex = this.currentIndex - (this.currentPage - 1) * this.pagesize;
                if (lindex < 0)return;
                if (lindex <= this.data.length) {
                    this.locate(this.currentIndex, true);
                } else {
                    this.pre();
                }
            }
        this.fireEvent("remove", this, record, index);
        if (!this.selected.length) {
            this.fireEvent('unselectall', this, this.selected);
        }
    },
    /**
     * 获取当前数据集下的所有数据.
     *
     * @return {Array} records 当前数据集的所有Record.
     */
    getAll: function () {
        return this.data;
    },
    /**
     * 查找数据.
     *
     * @param {String}
     *            property 查找的属性.
     * @param {Object}
     *            value 查找的属性的值.
     * @param {Leaf.Record}
     *            exceptRecord 当符合查找条件的第一个record为exceptRecord，将跳过该record继续查找.
     * @return {Leaf.Record} 符合查找条件的第一个record
     */
    find: function (property, value, exceptRecord, allRecord) {
        var r = null;
        var rs = [];
        if (!allRecord) {
            this.each(function (record) {
                if (!exceptRecord || record != exceptRecord) {
                    var v = record.get(property);
                    if (v == value) {
                        r = record;
                        return false;
                    }
                }
            }, this)
            return r;
        } else {
            this.each(function (record) {
                if (!exceptRecord || record != exceptRecord) {
                    var v = record.get(property);
                    if (v == value) {
                        rs.push(record);
//    	                return false;
                    }
                }
            }, this)
            return rs;
        }
    },
    /**
     * 根据id查找数据.
     *
     * @param {Number}
     *            id id.
     * @return {Leaf.Record} 查找的record
     */
    findById: function (id) {
        var find = null;
        for (var i = 0, len = this.data.length; i < len; i++) {
            if (this.data[i].id == id) {
                find = this.data[i]
                break;
            }
        }
        return find;
    },
    /**
     * 删除所有数据.
     */
    removeAll: function () {
        this.currentIndex = 1;
        this.totalCount = 0;
        this.data = [];
        this.selected = [];
        this.fireEvent("clear", this);
    },
    /**
     * 返回指定record的位置
     *
     * @param {Leaf.Record}
     *            record
     * @return {int}
     */
    indexOf: function (record) {
        return this.data.indexOf(record);
    },
    /**
     * 获取指定位置的record
     *
     * @param {Number}
     *            位置
     */
    getAt: function (index) {
        return this.data[index];
    },
    each: function (fn, scope) {
        var items = [].concat(this.data); // each safe for removal
        for (var i = 0, len = items.length; i < len; i++) {
            if (fn.call(scope || items[i], items[i], i, len) === false) {
                break;
            }
        }
    },
    processCurrentRow: function () {
        var r = this.getCurrentRecord();
        for (var k in this.fields) {
            var field = this.fields[k];
            if (field.type == 'dataset') {
                var ds = field.pro['dataset'];
                if (r && r.data[field.name]) {
                    ds.reConfig(r.data[field.name]);
                } else {
                    ds.resetConfig();
                }
                ds.fireEvent('refresh', ds);
                ds.processCurrentRow();
            }
        }
        if (r) this.fireEvent("indexchange", this, r);
    },
    /**
     * 获取所有选择的数据.
     *
     * @return {Array} 所有选择数据.
     */
    getSelected: function () {
        return this.selected;
    },
    /**
     * 获取所有选择的数据json格式.
     *
     * @return {Array} 所有选择数据.
     */
    getSelectedJson: function () {
        var arr = [];
        for(var i = 0; i < this.selected.length; i++){
            arr.push(this.selected[i].getObject());
        }
        return arr;
    },
    /**
     * 选择所有数据.
     */
    selectAll: function () {
        if (!this.selectable)return;
        for (var i = 0, l = this.data.length; i < l; i++) {
            if (!this.execSelectFunction(this.data[i]))continue;
            this.select(this.data[i], true);
        }
        this.fireEvent('selectall', this, this.selected);
    },
    /**
     * 取消所有选择.
     */
    unSelectAll: function () {
        if (!this.selectable)return;
        for (var i = 0, l = this.data.length; i < l; i++) {
            if (!this.execSelectFunction(this.data[i]))continue;
            this.unSelect(this.data[i], true);
        }
        this.fireEvent('unselectall', this, this.selected);
    },
    /**
     * 选择某个record.
     *
     * @param {Leaf.Record}
     *            record 需要选择的record.
     */
    select: function (r, isSelectAll) {
        if (!this.selectable)return;
        if (typeof(r) == 'string' || typeof(r) == 'number') r = this.findById(r);
        if (!r) return;
        if (this.selected.indexOf(r) != -1)return;
// if(!this.execSelectFunction(r))return;
        if (this.fireEvent("beforeselect", this, r)) {
            r.isSelected = true;
            if (this.selectionmodel == 'multiple') {
                this.selected.add(r);
                this.fireEvent('select', this, r, isSelectAll);
            } else {
                var or = this.selected[0];
                this.unSelect(or);
                this.selected = []
                this.selected.push(r);
                this.fireEvent('select', this, r);
            }
        }
    },
    /**
     * 取消选择某个record.
     *
     * @param {Leaf.Record}
     *            record 需要取消选择的record.
     */
    unSelect: function (r, isSelectAll) {
        if (!this.selectable)return;
        if (typeof(r) == 'string' || typeof(r) == 'number') r = this.findById(r);
        if (!r) return;
        if (this.selected.indexOf(r) == -1) return;
        this.selected.remove(r);
        r.isSelected = false;
        this.fireEvent('unselect', this, r, isSelectAll);
    },
    execSelectFunction: function (r) {
        if (this.selectfunction) {
            var selfun = $L.getRenderer(this.selectfunction);
            if (selfun == null) {
                alert("未找到" + this.selectfunction + "方法!")
            } else {
                var b = selfun.call(window, r);
                if (Ext.isDefined(b))return b;
            }
        }
        return true;
    },
    /**
     * 定位到某个指针位置.
     *
     * @param {Number}
     *            index 指针位置.
     */
    locate: function (index, force, keepData) {
        /*
         * 徐昭 2017/11/06 14:17
         * DataSet通过bindName绑定时，currentIndex默认为1,若index=1,绑定的dataset没有查询。
         * 徐昭 2017/11/21 10:27
         * 恢复if(this.currentIndex === index && force !== true)的判断
         * 为避免上述问题，在执行locate方法时将force参数设为true
         */
        if (this.currentIndex === index && force !== true) {
            return;
        }
        if (this.fetchall == true && index > ((this.currentPage - 1) * this.pagesize + this.data.length)) {
            return;
        }
        // 对于没有autcount的,判断最后一页
        if (!this.autocount && index > ((this.currentPage - 1) * this.pagesize + this.data.length) && this.data.length < this.pagesize) {
            return;
        }
// if(valid !== false) if(!this.validCurrent())return;
        if (index <= 0) {
            return;
        }
        if (index <= 0 || (this.autocount && (index > this.totalCount + this.getNewRecords().length))) {
            return;
        }
        var lindex = index - (this.currentPage - 1) * this.pagesize;
        // 只读性请求，不判断当前数据是否在Data内
        if (!keepData && this.data[lindex - 1]) {
            this.currentIndex = index;
        } else {
            if (this.modifiedcheck && this.isModified()) {
                //多次提示有未保存的数据的问题
                if(!this.hasNotSaveFlag){
                    var sf = this;
                    sf.hasNotSaveFlag = true;
                    $L.showInfoMessage(_lang['dataset.info'], _lang['dataset.info.locate'],function(){
                        sf.hasNotSaveFlag = false;
                    })
                }
            } else {
                this.currentIndex = index;
                this.currentPage = Math.ceil(index / this.pagesize);
                this.keepData = keepData;
                this.query(this.currentPage);
                return;
            }
        }
        this.processCurrentRow();
        if (this.selectionmodel == 'single') {
            var r = this.getAt(index - this.pagesize * (this.currentPage - 1) - 1)
            if (this.execSelectFunction(r))
                this.select(r);
        }
    },
    /**
     * 定位到某页.
     *
     * @param {Number}
     *            page 页数.
     */
    goPage: function (page, keepData) {
        if (page > 0) {
            this.gotoPage = page;
            var go = (page - 1) * this.pagesize + 1;
            var news;
            // 如果当前keepData为true，说明是只读性访问，不存在新增数据
            if (keepData) {
                news = 0;
            } else {
                news = this.getAll().length - this.pagesize;
            }

            if (this.currentPage < page && news > 0) go += news;
// var go = Math.max(0,page-2)*this.pagesize + this.data.length + 1;
            this.locate(go, false, keepData);
        }
    },
    /**
     * 定位到所有数据的第一条位置.
     */
    first: function () {
        this.locate(1);
    },
    /**
     * 向前移动一个指针位置.
     */
    pre: function () {
        if (arguments[0] == 'tree') {
            for (var i = this.currentIndex - 2; i >= 0; i--) {
                if (this.data[i].get("tree_level") == 1 || this.data[i].expand == true) {
                    this.locate(i + 1);
                    return;
                }
            }
        } else {
            this.locate(this.currentIndex - 1);
        }
    },
    /**
     * 向后移动一个指针位置.
     */
    next: function () {
        if (arguments[0] == 'tree') {
            for (var i = this.currentIndex + 1; i < this.data.length; i++) {
                if (this.data[i].get("tree_level") == 1 || this.data[i].expand == true) {
                    this.locate(i + 1);
                    return;
                }
            }
        } else {
            this.locate(this.currentIndex + 1);
        }
    },
    /**
     * 定位到第一页.
     */
    firstPage: function () {
        this.goPage(1);
    },
    /**
     * 向前移动一页.
     */
    prePage: function () {
        this.goPage(this.currentPage - 1);
    },
    /**
     * 向后移动一页.
     */
    nextPage: function () {
        this.goPage(this.currentPage + 1);
    },
    /**
     * 加载后一页，同时保留之前数据
     */
    loadNextPage: function () {
        this.goPage(this.currentPage + 1, true);
    },
    /**
     * 定位到最后一页.
     */
    lastPage: function () {
        this.goPage(this.totalPage);
    },
    /**
     * 仅对dataset本身进行校验,不校验绑定的子dataset.
     *
     * @param {Boolean}
     *            selected 校验选中的记录.
     * @return {Boolean} valid 校验结果.
     */
    validateSelf: function (selected) {
        return this.validate(selected, true, false)
    },
    /**
     * 设置dataset是否进行校验
     *
     * @return {Boolean} enable 是否校验.
     */
    setValidateEnable: function (enable) {
        this.validateEnable = enable;
    },

    /**
     * 对当前数据集进行校验.
     *
     * @param {Boolean}
     *            selected 校验选中的记录.
     * @return {Boolean} valid 校验结果.
     */
    validate: function (selected, fire, vc) {
        if (!this.validateEnable)return true;
        this.isValid = true;
        var current = this.getCurrentRecord();
        if (!current)return true;
        var records = selected ? this.getSelected() : this.getAll();
        var dmap = {};
        var hassub = false;
        var unvalidRecord = null;
        var issubValid = true;
        if (vc !== false)
            for (var k in this.fields) {
                var field = this.fields[k];
                if (field.type == 'dataset') {
                    hassub = true;
                    var d = field.pro['dataset'];
                    dmap[field.name] = d;
                }
            }
        for (var k = 0, l = records.length; k < l; k++) {
            var record = records[k];
            // 有些项目是虚拟的字段,例如密码修改
// if(record.dirty == true || record.isNew == true) {
            if (!record.validateRecord()) {
                this.isValid = false;
                unvalidRecord = record;
                $L.addInValidReocrd(this.id, record);
            } else {
                $L.removeInvalidReocrd(this.id, record);
            }
            if (this.isValid == false) {
                if (hassub)break;
            } else {
                for (var key in dmap) {
                    var ds = dmap[key];
                    if (record.data[key]) {
                        ds.reConfig(record.data[key]);
                        if (!ds.validate()) {
                            issubValid = this.isValid = false;
                            unvalidRecord = record;
                        } else
                            ds.reConfig(current.data[key]);// 循环校验完毕后,重新定位到当前行
                    }
                }

                if (this.isValid == false) {
                    break;
                }

// }
            }
        }

        if (unvalidRecord != null) {
            var r = this.indexOf(unvalidRecord);
            if (r != -1) this.locate(r + 1);
        }
        if (fire !== false && issubValid !== false) {
            $L.manager.fireEvent('valid', $L.manager, this, this.isValid);
            if (!this.isValid) {
                var valid = unvalidRecord.valid, unvalidMessage;
                var unvalidMessage = '';
                var errorCount = 0;
                for (var key in valid) {
                    if(valid[key]==valid[key+'_n']){
                        continue;
                    }
                    unvalidMessage += valid[key];
                    /*
                     * 添加对placeHolder的判断，有prompt就得到prompt
                     * 没有如果有placeHolder就得到placeHolder
                     * 向平  2017-12-27
                     */
                    if (unvalidRecord.ds.prompt) {
                        unvalidMessage += '&nbsp;' + unvalidRecord.ds.prompt;
                    } else if (unvalidRecord.ds.placeholder) {
                        unvalidMessage += '&nbsp;' + unvalidRecord.ds.placeholder;
                    } else {
                        // unvalidMessage += '&nbsp;' + unvalidRecord.ds.id;
                    }
                    /*
                     * 添加对placeHolder的判断，有prompt就得到prompt
                     * 没有如果有placeHolder就得到placeHolder
                     * 向平  2017-12-27
                     */
                    if (unvalidRecord.ds.fields && unvalidRecord.ds.fields[key] && unvalidRecord.ds.fields[key].pro) {
                        if (unvalidRecord.ds.fields[key].pro.requiredmessage != null) {
                            if(valid[key] != unvalidRecord.ds.fields[key].pro.requiredmessage) {
                            unvalidMessage += '-' + unvalidRecord.ds.fields[key].pro.requiredmessage;
                            }
                        } else if (unvalidRecord.ds.fields[key].pro.prompt) {
                            unvalidMessage += '-' + unvalidRecord.ds.fields[key].pro.prompt;
                        } else if (unvalidRecord.ds.fields[key].pro.placeholder) {
                            unvalidMessage += '-' + unvalidRecord.ds.fields[key].pro.placeholder;
                        }
                    } else {
                        unvalidMessage += '-' + key.toLowerCase();
                    }
                    unvalidMessage += '<br/>';
                    errorCount += 1;
                    // break;
                }
                $L.showInfoMessage(_lang['dataset.info'], unvalidMessage || _lang['dataset.info.validate'], null, 350, 85 + errorCount * 20);
            }
        }
        return this.isValid;
    },
    /**
     * 设置查询的Url.
     *
     * @param {String}
     *            url 查询的Url.
     */
    setQueryUrl: function (url) {
        this.queryurl = url;
    },
    /**
     * 设置查询的参数.
     *
     * @param {String}
     *            para 参数名.
     * @param {Object}
     *            value 参数值.
     */
    setQueryParameter: function (para, value) {
        this.qpara[para] = value;
    },
    /**
     * 设置查询的DataSet.
     *
     * @param {Leaf.DataSet}
     *            ds DataSet.
     */
    setQueryDataSet: function (ds) {
        this.qds = ds;
        if (this.qds.getCurrentRecord() == null) this.qds.create();
    },
    /**
     * 设置提交的Url.
     *
     * @param {String}
     *            url 提交的Url.
     */
    setSubmitUrl: function (url) {
        this.submiturl = url;
    },
    /**
     * 设置提交的参数.
     *
     * @param {String}
     *            para 参数名.
     * @param {Object}
     *            value 参数值.
     */
    setSubmitParameter: function (para, value) {
        this.spara[para] = value;
    },


    /**
     * 设置删除的Url
     * @param url
     */
    setDeleteUrl: function(url){
        this.deleteurl = url;
    },

    /**
     * 设置查询的method
     * @param method
     */
    setQueryMethod: function(method){
        this.querymethod = method;
    },

    /**
     * 设置删除的method
     * @param method
     */
    setDeleteMethod: function(method){
      this.deletemethod = method;
    },

    isAllReady: function (isSelected) {
        var sf = this, records = isSelected ? sf.getSelected() : sf.getAll(), isReady = true;
        for (var i = 0, l = records.length; i < l; i++) {
            var r = records[i];
            if (!r.isReady) {
                isReady = false;
                break;
            }
            Ext.iterate(r.data, function (name, item) {
                if (item && item.xtype == 'dataset') {
                    var field = sf.fields[name];
                    var ds = field.pro['dataset'];
                    ds.reConfig(item);
                    if (!ds.isAllReady(isSelected)) {
                        isReady = false;
                        return false;
                    }
                }
            });
        }
        return isReady;
    },
    /**
     * 等待ds中的所有record都ready后执行回调函数
     *
     * @param {String}
     *            isAll 判断所有的record还是选中的record
     * @param {Function}
     *            callback 回调函数
     * @param {Object}
     *            scope 回调函数的作用域
     */
    wait: function (isAll, callback, scope) {
        var sf = this, records = isAll ? sf.getAll() : sf.getSelected();
        sf.fireBindDataSetEvent('wait');
        for (var i = 0, r; r = records[i]; i++) {
            Ext.iterate(r.data, function (name, item) {
                if (item && item.xtype == 'dataset') {
                    records = records.concat(item.data);
                }
            });
        }
        var intervalId = setInterval(function () {
            for (var i = 0, l = records.length; i < l; i++) {
                if (!records[i].isReady)return;
            }
            clearInterval(intervalId);
            sf.fireBindDataSetEvent('afterwait');
            if (callback) callback.call(scope || window);
        }, 10);
    },
    /**
     * 查询数据.
     *
     * @param {Number}
     *            page(可选) 查询的页数.
     */
    query: function (page, opts) {
        $L.slideBarEnable = $L.SideBar.enable;
        if (!this.queryurl) return;
        var beforeQueryFlag = this.fireEvent("beforequery", this, this.qpara, opts);
        if(beforeQueryFlag != false) {
            if (this.qds) {
                if (this.qds.getCurrentRecord() == null) this.qds.create();
                this.qds.wait(true, function () {

                    /*
                     * 徐昭 2017/12/12 14:26
                     * 如果dataset的bindtarget和queryDataSet相同时，不做queryDataSet的校验
                     */
//	    		if(!this.qds.validate()) return;
                    if (this.bindtarget != this.qds.id) {
                        if (!this.qds.validate()) return;
                    }
                    this.doQuery(page, opts);
                }, this);
            } else {
                this.doQuery(page, opts);
            }
        }
    },
    doQuery: function (page, opts) {
        var sf = this,
            r, q = {},
            pagesize = sf.pagesize,
            autocount = sf.autocount,
            qds = sf.qds;
        sf.loading = true;
        if (qds) r = qds.getCurrentRecord();
        if (!page) sf.currentIndex = 1;
        page = sf.currentPage = page || 1;
        if (r != null) Ext.apply(q, r.data);
        sf.fireEvent("query", sf, sf.qpara, opts);
        /*
         * fixed by 20839
         * 保证qurl能取到query事件函数中更改后的新sf.queryurl
         */
        var qurl = sf.queryurl,
            isRest = /\/rest\//.test(qurl) || sf.hybrisws,
            isModel = /\/autocrud\//.test(qurl),
            isRest = isRest && !isModel;
        Ext.apply(q, sf.qpara);
        for (var k in q) {
            var v = q[k];
            if (Ext.isEmpty(v, false) || v.xtype == 'dataset') delete q[k];
        }
        if (isRest) {
            var format_xmlattribute = function (key) {
                    if (/^@/.test(key)) {
                        return key.substring(1);
                    }
                    return key;
                },
                path = qurl.split(/[?#]/g)[0].match(/\/([^\/]*)$/)[1],
                para = path + '_autocount=' + autocount +
                    '&' + path + '_size=' + pagesize +
                    '&' + path + '_page=' + (page - 1),
                // attributes=[],
                query = [];
            /*
             * Ext.iterate(sf.fields,function(key){
             * attributes.push(format_xmlattribute(key)); });
             * if(attributes.length)
             * para+='&'+dtoName+'_attributes='+attributes.join(',');
             */
            Ext.iterate(q, function (key, value) {
                var qfield = qds.fields[key], returnfield;
                if (!qfield || !(returnfield = qfield.getPropertity('returnfield')) || returnfield == key) {
                    if (value != 'true' && value != 'false') {
                        value = "'" + value + "'";
                    }
                    query.push('{' + format_xmlattribute(key) + '}' + (String(value).indexOf('%') == -1 ? ' = ' : ' like ') + value);
                }
            })
            if (query.length)
                para += '&' + path + '_query=' + encodeURIComponent(query.join(' and '))
        } else {
            var para = 'pagesize=' + pagesize +
                '&pagenum=' + page +
                '&pageSize=' + pagesize +
                '&pageNum=' + page +
                '&_fetchall=' + sf.fetchall +
                '&_autocount=' + autocount
            // + '&_rootpath=list'
        }
        var url = Ext.urlAppend(qurl, para);

// sf.fireBindDataSetEvent("beforeload", sf);//主dataset无数据,子dataset一直loading
        if(sf.qtId) Ext.Ajax.abort(sf.qtId);
        sf.qtId = $L.request(Ext.apply({
            url: url,
            success: sf.onLoadSuccess,
            error: sf.onLoadError,
            scope: sf,
            failure: sf.onAjaxFailed,
            opts: opts,
            method:sf.querymethod,
            ext: opts ? opts.ext : null,
            queryDataFormat: sf.queryDataFormat
        }, isRest ? {
                isRest: true,
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            } : {
                para: q
            }));
    },
    /**
     * 判断当前数据集是否发生改变.
     *
     * @return {Boolean} modified 是否发生改变.
     */
    isModified: function () {
        var modified = false;
        var records = this.getAll();
        for (var k = 0, l = records.length; k < l; k++) {
            var record = records[k];
            if (record.dirty == true || record.isNew == true) {
                modified = true;
                break;
            } else {
                for (var key in this.fields) {
                    var field = this.fields[key];
                    if (field.type == 'dataset') {
                        var ds = field.pro['dataset'];
                        ds.reConfig(record.data[field.name]);
                        if (ds.isModified()) {
                            modified = true;
                            break;
                        }
                    }
                }
            }
        }
        return modified;
    },
// isDataModified : function(){
// var modified = false;
// for(var i=0,l=this.data.length;i<l;i++){
// var r = this.data[i];
// if(r.dirty || r.isNew){
// modified = true;
// break;
// }
// }
// return modified;
// },
    /**
     * 以json格式返回当前数据集.
     *
     * @return {Object} json 返回的json对象.
     */
    getJsonData: function (selected, fields) {
        var sf = this,
            datas = [],
            items = selected ? sf.getSelected() : sf.data,
            dsfields = sf.fields;
        Ext.each(items, function (r) {
// var isAdd = r.dirty; //MAS云新增特性
            var isAdd = r.dirty || r.isNew,
                d = Ext.apply({}, r.data);
            Ext.iterate(d, function (k, item) {
                if (fields && fields.indexOf(k) == -1) {
                    delete d[k];
                } else {
                    if (item && item.xtype == 'dataset') {
                        // if(item.data.length > 0){
                        var ds = new $L.DataSet({});// $le(item.id);
                        // ds.fields = item.data[0].ds.fields;
                        ds.reConfig(item)
                        isAdd = isAdd == false ? ds.isModified() : isAdd;
                        d[k] = ds.getJsonData();
                        // }
                    }
                }
            });
            d['_id'] = r.id;
            d['_status'] = r.isNew ? 'insert' : 'update';
            if (isAdd || selected) {
                datas.push(d);
            }
        });

        return datas;
    },
    checkEmptyData: function (items) {
        var sf = this;
        Ext.each(items, function (data) {
            Ext.iterate(data, function (key, d, f) {
                if (Ext.isArray(d)) {
                    sf.checkEmptyData(d);
                } else if (d === '') {
                    data[key] = null;
                }
            })
        });
    },
    doSubmit: function (url, items) {
        var sf = this,
            url = url || sf.submiturl;
        if (Ext.isEmpty(url)) return;
        var isRest = /\/rest\//.test(url) || sf.hybrisws;
        sf.fireBindDataSetEvent("submit", url, items);
// var p = items;//sf.getJsonData();
        sf.checkEmptyData(items);
// for(var i=0;i<p.length;i++){
// var data = p[i]
// for(var key in data){
// var f = sf.fields[key];
// if(f && f.type != 'dataset' && data[key]==='')data[key]=null;
// }
// // p[i] = Ext.apply(p[i],sf.spara)
// }

        // if(p.length > 0) {
// sf.fireEvent("submit", sf);
        $L.request(Ext.apply({
            showSuccessTip: true,
            url: url,
            ext: sf.spara,
            success: sf.onSubmitSuccess,
            error: sf.onSubmitError,
            scope: sf,
            failure: sf.onAjaxFailed,
            para: items
        }, isRest ? {
                isRest: true,
                dtoName: sf.dtoname,
                restDataFormat: sf.restDataFormat,
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            } : {}));
        // }
    },


    doNewSubmit: function (url, items) {
        var sf = this,
            url = url || sf.submiturl;
        if (Ext.isEmpty(url)) return;
        var isRest = /\/rest\//.test(url) || sf.hybrisws;
        sf.fireBindDataSetEvent("submit", url, items);
        $L.request(Ext.apply({
            showSuccessTip: true,
            url: url,
            ext: sf.spara,
            success: sf.onSubmitSuccess,
            error: sf.onSubmitError,
            scope: sf,
            failure: sf.onAjaxFailed,
            para: items
        }, isRest ? {
            isRest: true,
            dtoName: sf.dtoname,
            restDataFormat: sf.restDataFormat,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        } : {}));
        // }
    },


    /**
     * 提交选中数据.
     *
     * @param {String}
     *            url(可选) 提交的url.
     * @param {Array}
     *            fields(可选) 根据选定的fields提交.
     */
    submitSelected: function (url, fields) {
        this.submit(url, fields, true);
    },
    /**
     * 提交数据.
     *
     * @param {String}
     *            url(可选) 提交的url.
     * @param {Array}
     *            fields(可选) 根据选定的fields提交.
     */
    submit: function (url, fields, selected) {
        var sf = this;
        sf.wait(!selected, function () {
            sf.fireEvent("beforesubmit", sf)
            && sf.validate(selected)
            && sf.doSubmit(url, sf.getJsonData(selected, fields));
        });
    },
    /**
     * post方式提交数据.
     *
     * @param {String}
     *            url(可选) 提交的url.
     */
    post: function (url) {
        var sf = this, r = sf.getCurrentRecord();
        r && sf.wait(true, function () {
            sf.validate() && $L.post(url, r.data);
        });
    },
    /**
     * 重置数据.
     */
    reset: function () {
        var record = this.getCurrentRecord();
        if (!record || !record.fields)return;
        for (var c in record.fields) {
            var v = record.fields[c].get('defaultvalue');
            if (v != record.get(c))
                record.set(c, v == undefined || v == null ? "" : v);
        }
    },
    fireBindDataSetEvent: function () {// event
        var a = Ext.toArray(arguments);
        var event = a[0];
        a[0] = this;
        this.fireEvent.apply(this, [event].concat(a))
// this.fireEvent(event,this);
        for (var k in this.fields) {
            var field = this.fields[k];
            if (field.type == 'dataset') {
                var ds = field.pro['dataset'];
                if (ds) {
                    ds.fireBindDataSetEvent(event)
                }
            }
        }
    },
    beforeEdit: function (record, name, value, oldvalue) {
        return this.fireEvent("beforeupdate", this, record, name, value, oldvalue);
    },
    afterEdit: function (record, name, value, oldvalue) {
        this.fireEvent("update", this, record, name, value, oldvalue);
    },
    afterReject: function (record, name, value) {
        this.fireEvent("reject", this, record, name, value);
    },
    onSubmitSuccess: function (res) {
        var datas = []
        if (res.result.record) {
            datas = [].concat(res.result.record);
            this.commitRecords(datas, true)
        }
        this.fireBindDataSetEvent('submitsuccess', res);
    },
    commitRecords: function (datas, fire, record) {
        // this.resetConfig();
        for (var i = 0, l = datas.length; i < l; i++) {
            var data = datas[i];
            var r = this.findById(data['_id'] || data['__id']);
            if (!r) continue;
            if (r.isNew) this.totalCount++;
            r.commit();
            var haschange = false;
            for (var k in data) {
                var field = k;
                var f = this.fields[field];
                if (f && f.type == 'dataset') {
                    var ds = f.pro['dataset'];
                    ds.reConfig(r.data[f.name]);
                    if (data[k].record) {
                        ds.commitRecords([].concat(data[k].record), this.getCurrentRecord() === r && fire, r);
                    }
                } else if (f && f.type == 'hidden') {
                    continue
                } else {
                    var ov = r.get(field);
                    var nv = data[k]
                    if (field == '_id' || field == '_status' || field == '__parameter_parsed__') continue;
                    if (f) {
                        nv = this.processData(data, k, f);
                    }
                    if (ov != nv) {
                        haschange = true;
                        if (fire) {
                            // 由于commit放到上面,这个时候不改变状态,防止重复提交
                            r.set(field, nv, true);
                        } else {
                            r.data[field] = nv;
                            if (record) record.data[this.bindname] = this.getConfig();
                        }
                    }
                }
            }
            // 提交后，如果没有sequence，就不会有值改变，所以手动触发一下update。
            if (!haschange && fire) {
                r.set('__for_update__', true, true);
                delete r.data['__for_update__'];
                r.commit();
            }
// r.commit();//挪到上面了,record.set的时候会触发update事件,重新渲染.有可能去判断isNew的状态
        }
    },
    processData: function (data, key, field) {
        var v = data[key];
        if (v) {
            var dt = field.getPropertity('datatype');
            dt = dt ? dt.toLowerCase() : '';
            switch (dt) {
                case 'date':
                    v = $L.parseDate(v);
                    break;
                case 'java.util.date':
                    v = $L.parseDate(v);
                    break;
                case 'java.sql.date':
                    v = $L.parseDate(v);
                    break;
                case 'java.sql.timestamp':
                    v = $L.parseDate(v);
                    v.xtype = 'timestamp';
                    break;
                case 'int':
                    v = parseInt(v);
                    break;
                case 'float':
                    v = parseFloat(v);
                    break;
                case 'boolean':
                    v = v == "true";
                    break;
            }
        }
        // TODO:处理options的displayField
        return this.processValueListField(data, v, field);
    },
    processValueListField: function (data, v, field) {
        var op = field.getPropertity('options');
        var df = field.getPropertity('displayfield');
        var vf = field.getPropertity('valuefield');
        var mp = field.getPropertity('mapping')
        if (df && vf && op && mp && !v) {
            var rf;
            for (var i = 0; i < mp.length; i++) {
                var map = mp[i];
                if (vf == map.from) {
                    rf = map.to;
                    break;
                }
            }
            var rv = data[rf];
            var options = $le(op);
            if (options && !Ext.isEmpty(rv)) {
                var r = options.find(vf, rv);
                if (r) {
                    v = r.get(df);
                }
            }
        }
        return v;
    },
    onSubmitError: function (res) {
// $L.showErrorMessage('错误',
// res.error.message||res.error.stackTrace,null,400,200);
        this.fireBindDataSetEvent('submitfailed', res);
    },
    onLoadSuccess: function (res, options) {
        try {
            if (res == null) return;
            if (!res.result.record) res.result.record = [];
            this.res = res;
            var records = [].concat(res.result.record);
            // var total = res.result.totalCount;
            var total = res.result[this.totalcountfield]
            var datas = [];
            if (records.length > 0) {
                for (var i = 0, l = records.length; i < l; i++) {
                    var item = {
                        data: records[i]
                    }
                    datas.push(item);
                }
            } else if (records.length == 0) {
                this.currentIndex = 0
            }
            this.loading = false;
            this.loadData(datas, total, options);
            if (datas.length != 0)
                this.locate(this.currentIndex, true);
            $L.SideBar.enable = $L.slideBarEnable;
            this.qtId = null;
            this.fireEvent('loadsuccess', this);
        } catch (e) {
            window.console && console.error(e.stack);
        }
    },
    onAjaxFailed: function (res, opt) {
        this.fireBindDataSetEvent('ajaxfailed', res, opt);
        this.qtId = null;
    },
    onLoadError: function (res, opt) {
        this.fireBindDataSetEvent('loadfailed', res, opt);
// $L.showWarningMessage('错误',
// res.error.message||res.error.stackTrace,null,350,150);
        this.loading = false;
        $L.SideBar.enable = $L.slideBarEnable;
        this.qtId = null;

        this.keepData = false;
    },
    onFieldChange: function (record, field, type, value) {
        this.fireEvent('fieldchange', this, record, field, type, value)
    },
    onMetaChange: function (record, meta, type, value) {
        this.fireEvent('metachange', this, record, meta, type, value)
    },
    onRecordValid: function (record, name, valid) {
        if (valid == false && this.isValid !== false) this.isValid = false;
        this.fireEvent('valid', this, record, name, valid)
    },
    setAllFieldsReadOnly: function(bool){
        var fields = this.fields;
        for(var name in fields){
            fields[name].setReadOnly(bool);
        }
    }
//    spreedsheet : function(){
//
//    }
});

/**
 * @class Leaf.Record
 *        <p>
 *        Record是一个数据对象.
 * @constructor
 * @param {Object}
 *            data 数据对象.
 * @param {Array}
 *            fields 配置对象.
 */
$L.Record = function (data, fields) {
    /**
     * Record的id. (只读).
     *
     * @type Number
     * @property
     */
    this.id = ++$L.AUTO_ID;
    /**
     * Record的数据 (只读).
     *
     * @type Object
     * @property
     */
    this.data = data;
    /**
     * Record的Fields (只读).
     *
     * @type Object
     * @property
     */
    this.fields = {};
    /**
     * Record的验证信息 (只读).
     *
     * @type Object
     * @property
     */
    this.valid = {};
    /**
     * Record的验证结果 (只读).
     *
     * @type Boolean
     * @property
     */
    this.isValid = true;
    /**
     * 是否是新数据 (只读).
     *
     * @type Boolean
     * @property
     */
    this.isNew = false;
    /**
     * 是否发生改变 (只读).
     *
     * @type Boolean
     * @property
     */
    this.dirty = false;
    /**
     * 编辑状态 (只读).
     *
     * @type Boolean
     * @property
     */
    this.editing = false;
    /**
     * 编辑信息对象 (只读).
     *
     * @type Object
     * @property
     */
    this.modified = null;
    /**
     * 是否是已就绪数据 (只读).
     *
     * @type Boolean
     * @property
     */
    this.isReady = true;
    /**
     * 是否被选中
     *
     * @type Boolean
     * @property
     */
    this.isSelected = false;
    this.meta = new $L.Record.Meta(this);
    if (fields) this.initFields(fields);
};
$L.Record.prototype = {
    commit: function () {
        this.editing = false;
        this.valid = {};
        this.isValid = true;
        this.isNew = false;
        this.dirty = false;
        this.modified = null;
    },
    initFields: function (fields) {
        for (var i = 0, l = fields.length; i < l; i++) {
            var f = new $L.Record.Field(fields[i]);
            f.record = this;
            this.fields[f.name] = f;
        }
    },
    validateRecord: function () {
        this.isValid = true;
        this.valid = {};
        var df = this.ds.fields;
        var rf = this.fields;
        var names = [];
        for (var k in df) {
            if (df[k].type != 'dataset')
                names.push(k);
        }

        for (var k in rf) {
            if (names.indexOf(k) == -1) {
                if (rf[k].type != 'dataset')
                    names.push(k);
            }
        }
        for (var i = 0, l = names.length; i < l; i++) {
            if (this.isValid == true) {
                this.isValid = this.validate(names[i]);
            } else {
                this.validate(names[i]);
            }
        }
        return this.isValid;
    },
    validate: function (name) {
        var sf = this,
            valid = true,
            oldValid = sf.valid[name],
            v = sf.get(name),
            field = sf.getMeta().getField(name),
            validator = field.get('validator'),
            requiredFunc = field.get('requiredfunction'),
            vv = v && v.trim ? v.trim() : v;
        if (Ext.isEmpty(vv)) {
            var required = field.get('required') == true;
            if (requiredFunc) {
                var rf = eval(requiredFunc);
                if (rf) {
                    required = rf(sf, name, v);
                } else {
                    alert('未找到函数' + requiredFunc);
                }
            }
            if (required) {
                sf.valid[name] = field.get('requiredmessage') || _lang['dataset.validate.required'];
                valid = false;
            }
        }
        if (valid == true) {
            var isvalid = true;
            if (validator) {
                var vc = eval(validator);
                if (vc) {
                    isvalid = vc(sf, name, v);
                    if (isvalid !== true) {
                        valid = false;
                        sf.valid[name] = isvalid;
                    }
                } else {
                    alert('未找到函数' + validator)
                }
            }
        }
        if (valid == true) delete sf.valid[name];
        if (oldValid != sf.valid[name] || !Ext.isDefined(oldValid))
            sf.ds.onRecordValid(sf, name, valid);
        return valid;
    },
    setDataSet: function (ds) {
        this.ds = ds;
    },
    /**
     * 获取field对象
     *
     * @param {String}
     *            name
     * @return {Leaf.Record.Field}
     */
    getField: function (name) {
        return this.getMeta().getField(name);
    },
    getMeta: function () {
        return this.meta;
    },
    copy: function (record) {
        if (record == this) {
            alert('不能copy自身!');
            return;
        }
        if (record.dirty) {
            for (var n in record.modified) {
                this.set(n, record.get(n))
            }
        }
    },
    /**
     * 设置值.
     *
     * @param {String}
     *            name 设定值的名字.
     * @param {Object}
     *            value 设定的值.
     * @param {Boolean}
     *            notDirty true 不改变record的dirty状态.
     */
    set: function (name, value, notDirty) {
        var old = this.data[name];
        if (this.ds.beforeEdit(this, name, value, old)) {
            if (!(old === value || (Ext.isEmpty(old) && Ext.isEmpty(value)) || (Ext.isDate(old) && Ext.isDate(value) && old.getTime() == value.getTime()))) {
                if (!notDirty) {
                    this.dirty = true;
                    if (!this.modified) {
                        this.modified = {};
                    }
                    if (typeof this.modified[name] == 'undefined') {
                        this.modified[name] = old;
                    }
                }
                this.data[name] = value;
                if (!this.editing && this.ds) {
                    this.ds.afterEdit(this, name, value, old);
                }
                /*
                 * set方法不触发validate，只有在submit或者显式validate时触发
                 */
//                this.validate(name)
            }
            /*
             * 放在这个位置日期判断会弹出两次提示框
             * 2017-12-14  向平
             */
            this.validate(name)
        }
    },
    /**
     * 设置值.
     *
     * @param {String}
     *            name 名字.
     * @return {Object} value 值.
     */
    get: function (name) {
        return this.data[name];
    },
    /**
     * 返回record的data对象. 可以通过obj.xx的方式获取数据
     *
     * @return {Object}
     */
    getObject: function () {
        return Ext.apply({}, this.data);
    },
    /**
     * 更新data数据.
     *
     * @param {Object}
     *            o
     */
    setObject: function (o) {
        for (var key in o) {
            this.set(key, o[key]);
        }
    },
    reject: function (silent) {
        var m = this.modified;
        for (var n in m) {
            if (typeof m[n] != "function") {
                this.data[n] = m[n];
                this.ds.afterReject(this, n, m[n]);
            }
        }
        delete this.modified;
        this.editing = false;
        this.dirty = false;
    },
// beginEdit : function(){
// this.editing = true;
// this.modified = {};
// },
// cancelEdit : function(){
// this.editing = false;
// delete this.modified;
// },
// endEdit : function(){
// delete this.modified;
// this.editing = false;
// if(this.dirty && this.ds){
// this.ds.afterEdit(this);//name,value怎么处理?
// }
// },
    onFieldChange: function (name, type, value) {
        var field = this.getMeta().getField(name);
        this.ds.onFieldChange(this, field, type, value);
    },
    onFieldClear: function (name) {
        var field = this.getMeta().getField(name);
        this.ds.onFieldChange(this, field);
    },
    onMetaChange: function (meta, type, value) {
        this.ds.onMetaChange(this, meta, type, value);
    },
    onMetaClear: function (meta) {
        this.ds.onMetaChange(this, meta);
    },
    setDirty: function (dirty) {
        this.dirty = dirty;
    }
}
$L.Record.Meta = function (r) {
    this.record = r;
    this.pro = {};
}
$L.Record.Meta.prototype = {
    clear: function () {
        this.pro = {};
        this.record.onMetaClear(this);
    },
    getField: function (name) {
        if (!name)return null;
        var f = this.record.fields[name];
        var df = this.record.ds.fields[name];
        var rf;
        if (!f) {
            if (df) {
                f = new $L.Record.Field({name: df.name, type: df.type || 'string'});
            } else {
                f = new $L.Record.Field({name: name, type: 'string'});//
            }
            f.record = this.record;
            this.record.fields[f.name] = f;
        }

        var pro = {};
        if (df) pro = Ext.apply(pro, df.pro);
        pro = Ext.apply(pro, this.pro);
        pro = Ext.apply(pro, f.pro);
        delete pro.name;
        delete pro.type;
        f.snap = pro;
        return f;
    },
    setRequired: function (r) {
        var op = this.pro['required'];
        if (op !== r) {
            this.pro['required'] = r;
            this.record.onMetaChange(this, 'required', r);
        }
    },
    setReadOnly: function (r) {
        var op = this.pro['readonly'];
        if (op !== r) {
            this.pro['readonly'] = r;
            this.record.onMetaChange(this, 'readonly', r);
        }
    }
}
/**
 * @class Leaf.Record.Field
 *        <p>
 *        Field是一个配置对象，主要配置指定列的一些附加属性，例如非空，只读，值列表等信息.
 * @constructor
 * @param {Object}
 *            data 数据对象.
 */
$L.Record.Field = function (c) {
    this.name = c.name;
    this.type = c.type;
    this.pro = c || {};
    this.record;
};
$L.Record.Field.prototype = {
    /**
     * 清除所有配置信息.
     */
    clear: function () {
        this.pro = {};
        this.record.onFieldClear(this.name);
    },
    setPropertity: function (type, value) {
        var op = this.pro[type];
        if (op !== value) {
            this.pro[type] = value;
            if (this.snap) this.snap[type] = value;
            if (this.record) this.record.onFieldChange(this.name, type, value);
        }
    },
    /**
     * 获取配置信息
     *
     * @param {String}
     *            name 配置名
     * @return {Object} value 配置值
     */
    get: function (name) {
        var v = null;
        if (this.snap) {
            v = this.snap[name];
        } else if (this.pro) {
            v = this.pro[name];
        }
        return v;
    },
    getPropertity: function (name) {
        return this.pro[name]
    },
    /**
     * 设置当前Field是否必输
     *
     * @param {Boolean}
     *            required 是否必输.
     */
    setRequired: function (r) {
        this.setPropertity('required', r);
        var promptSpans = Ext.query('span[title=' + this.name + '].prompt-required');
        for (var key = 0; key < promptSpans.length; key++) {
            var promptSpan = Ext.get(promptSpans[key]);
            r ? promptSpan.addClass("prompt-required-show") : promptSpan.removeClass("prompt-required-show");
        }
        if (!r && this.record) this.record.validate(this.name);
    },
    /**
     * 当前Field是否必输.
     *
     * @return {Boolean} required 是否必输.
     */
    isRequired: function () {
        return this.get('required');
    },
    /**
     * 设置当前Field是否只读.
     *
     * @param {Boolean}
     *            readonly 是否只读
     */
    setReadOnly: function (r) {
        if (r && this.record) delete this.record.valid[this.name];
        this.setPropertity('readonly', r);
    },
    /**
     * 当前Field是否只读.
     *
     * @return {Boolean} readonly 是否只读
     */
    isReadOnly: function () {
        return this.get('readonly');
    },
    /**
     * 设置当前Field的数据集.
     *
     * @param {Object}
     *            r 数据集
     */
    setOptions: function (r) {
        this.setPropertity('options', r);
    },
    /**
     * 获取当前的数据集.
     *
     * @return {Object} r 数据集
     */
    getOptions: function () {
        return this.get('options');
    },
    /**
     * 设置当前Field的映射. 例如：
     * <p>
     * var mapping = [{from:'name', to: 'code'},{from:'service', to: 'name'}];
     * </p>
     * field.setMapping(mapping);
     *
     * @return {Array} mapping 映射列表.
     *
     */
    setMapping: function (m) {
        this.setPropertity('mapping', m);
    },
    /**
     * 获取当前的映射.
     *
     * @return {Array} array 映射集合
     */
    getMapping: function () {
        return this.get('mapping');
    },
    /**
     * 设置Lov弹出窗口的Title.
     *
     * @param {String}
     *            title lov弹出窗口的Tile
     */
    setTitle: function (t) {
        this.setPropertity('title', t);
    },
    /**
     * 设置Lov弹出窗口的宽度.
     *
     * @param {Number}
     *            width lov弹出窗口的Width
     */
    setLovWidth: function (w) {
        this.setPropertity('lovwidth', w);
    },
    /**
     * 设置Lov弹出窗口的高度.
     *
     * @param {Number}
     *            height lov弹出窗口的Height
     */
    setLovHeight: function (h) {
        this.setPropertity('lovheight', h);
    },
    /**
     * 设置Lov弹出窗口中grid的高度. 配置这个主要是由于查询条件可能存在多个，导致查询的form过高.
     *
     * @param {Number}
     *            height lov弹出窗口的grid组件的Height
     */
    setLovGridHeight: function (gh) {
        this.setPropertity("lovgridheight", gh)
    },
    /**
     * 设置Lov的Model对象. Lov的配置可以通过三种方式.(1)model (2)service (3)url.
     *
     * @param {String}
     *            model lov配置的model.
     */
    setLovModel: function (m) {
        this.setPropertity("lovmodel", m)
    },
    /**
     * 设置Lov的Service对象. Lov的配置可以通过三种方式.(1)model (2)service (3)url.
     *
     * @param {String}
     *            service lov配置的service.
     */
    setLovService: function (m) {
        this.setPropertity("lovservice", m)
    },
    /**
     * 设置Lov的Url地址. Lov的配置可以通过三种方式.(1)model (2)service (3)url.
     * 通过url打开的lov，可以不用调用setLovGridHeight
     *
     * @param {String}
     *            url lov打开的url.
     */
    setLovUrl: function (m) {
        this.setPropertity("lovurl", m)
    },
    /**
     * 设置Lov的查询参数
     *
     * @param {String}
     *            name
     * @param {Object}
     *            value
     */
    setLovPara: function (name, value) {
        var p = this.get('lovpara') || {};
        if (value == null) {
            delete p[name]
        } else {
            p[name] = value;
        }
        this.setPropertity("lovpara", p)
    }

}
/**
 * @class Leaf.Component
 * @extends Ext.util.Observable
 *          <p>
 *          所有组件对象的父类.
 *          <p>
 *          所有的子类将自动继承Component的所有属性和方法.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.Component = Ext.extend(Ext.util.Observable, {
    focusCss: 'item-focus',
    constructor: function (config) {
        $L.Component.superclass.constructor.call(this);
        this.id = config.id || Ext.id();
        this.hostid = config.hostid;
        $L.CmpManager.put(this.id, this)
        this.initConfig = config;
        this.isHidden = false;
        this.isFireEvent = false;
        this.hasFocus = false;
        this.initComponent(config);
        this.initEvents();
        this.hidden && this.setVisible(false);
    },
    initComponent: function (config) {
        config = config || {};
        Ext.apply(this, config);
        this.wrap = Ext.get(this.id);
        if (this.listeners) {
            this.on(this.listeners);
        }
    },
    processListener: function (ou) {
        this.processMouseOverOut(ou)
//    	if(this.id == 'mainTab'){
//
//    	}
        if ((this.clientresize && (!Ext.isEmpty(this.marginwidth) || !Ext.isEmpty(this.marginheight)) || this.constructor == $L.Window)) {
// this.windowResizeListener();//TODO:以后修改服务端component,去掉自身尺寸的判断
            Ext.EventManager[ou](window, "resize", this.windowResizeListener, this);
            $L.auWindow.on("resize", this.windowResizeListener, this);
        }
    },
    processMouseOverOut: function (ou) {
        if (this.wrap) {
            this.wrap[ou]("mouseover", this.onMouseOver, this);
            this.wrap[ou]("mouseout", this.onMouseOut, this);
        }
    },
    initEvents: function () {
        this.addEvents(
            /**
             * @event focus 获取焦点事件
             * @param {Component}
             *            this 当前组件.
             */
            'focus',
            /**
             * @event blur 失去焦点事件
             * @param {Component}
             *            this 当前组件.
             */
            'blur',
            /**
             * @event change 组件值改变事件.
             * @param {Component}
             *            this 当前组件.
             * @param {Object}
             *            value 新的值.
             * @param {Object}
             *            oldValue 旧的值.
             */
            'change',
            /**
             * @event valid 组件验证事件.
             * @param {Component}
             *            this 当前组件.
             * @param {Leaf.Record}
             *            record record对象.
             * @param {String}
             *            name 对象绑定的Name.
             * @param {Boolean}
             *            isValid 验证是否通过.
             */
            'valid',
            /**
             * @event mouseover 鼠标经过组件事件.
             * @param {Component}
             *            this 当前组件.
             * @param {EventObject}
             *            e 鼠标事件对象.
             */
            'mouseover',
            /**
             * @event mouseout 鼠标离开组件事件.
             * @param {Component}
             *            this 当前组件.
             * @param {EventObject}
             *            e 鼠标事件对象.
             */
            'mouseout');
        this.processListener('on');
    },
    windowResizeListener: function () {
        if (Ext.isIE7 || Ext.isIE6) {
            return;
        }
        var ht, wd;
        var _rc = 'refresh';
        Ext.getBody().addClass(_rc);
// Ext.getBody().setStyle('overflow','hidden')
        /**
         * resize只做宽度的自动调整，高度不进行调整
         */
        if (!Ext.isEmpty(this.marginheight) && !this.autoadjustheight) {
            ht = Leaf.getViewportHeight() - Math.max($L.usedHeight, 0);
            var h = ht - this.marginheight,
                /*
                 * 徐昭 2017/12/22 16:22
                 * 高度重绘时增加最小高度的判断
                 */
                mh = this.minheight || 100;
            this.setHeight(h >= mh ? h : mh);
        }
        if (!Ext.isEmpty(this.marginwidth)) {
            wd = Leaf.getViewportWidth();
            var v = wd - this.marginwidth - Math.max($L.usedWidth, 0);
            this.setWidth(v);
            // 非标准做法,中集特殊要求！
            // this.setWidth(v < this.initConfig.width ? v :
            // this.initConfig.width);
        }

        Ext.getBody().removeClass(_rc);
// Ext.getBody().setStyle('overflow','auto');
    },
    isEventFromComponent: function (el) {
        return this.wrap.contains(el) || this.wrap.dom === (el.dom ? el.dom : el);
    },
    move: function (x, y) {
        if (!Ext.isEmpty(x)) this.wrap.setX(x);
        if (!Ext.isEmpty(y)) this.wrap.setY(y);
    },
    getBindName: function () {
        return this.binder ? this.binder.name : null;
    },
    getBindDataSet: function () {
        return this.binder ? this.binder.ds : null;
    },
    /**
     * 将组件绑定到某个DataSet的某个Field上.
     *
     * @param {String/Leaf.DataSet}
     *            dataSet 绑定的DataSet. 可以是具体某个DataSet对象，也可以是某个DataSet的id.
     * @param {String}
     *            name Field的name.
     */
    bind: function (ds, name) {
        this.clearBind();
        if (typeof(ds) == 'string') {
            ds = $le(ds);
        }
        if (!ds)return;
        this.binder = {
            ds: ds,
            name: name
        }
        this.record = ds.getCurrentRecord();
        var field = ds.fields[this.binder.name];
        if (field) {
            var config = {};
            Ext.apply(config, this.initConfig);
            Ext.apply(config, field.pro);
            delete config.name;
            delete config.type;
            this.initComponent(config);

        }
        ds.on('metachange', this.onRefresh, this);
        ds.on('valid', this.onValid, this);
        ds.on('remove', this.onRemove, this);
        ds.on('clear', this.onClear, this);
        ds.on('update', this.onUpdate, this);
        ds.on('reject', this.onUpdate, this);
        ds.on('fieldchange', this.onFieldChange, this);
        ds.on('indexchange', this.onRefresh, this);
        this.onRefresh(ds)
    },
    /**
     * 清除组件的绑定信息.
     * <p>
     * 删除所有绑定的事件信息.
     */
    clearBind: function () {
        if (this.binder) {
            var bds = this.binder.ds;
            bds.un('metachange', this.onRefresh, this);
            bds.un('valid', this.onValid, this);
            bds.un('remove', this.onRemove, this);
            bds.un('clear', this.onClear, this);
            bds.un('update', this.onUpdate, this);
            bds.un('reject', this.onUpdate, this);
            bds.un('fieldchange', this.onFieldChange, this);
            bds.un('indexchange', this.onRefresh, this);
        }
        this.binder = null;
        this.record = null;
        this.value = null;
    },
    /**
     * <p>
     * 销毁组件对象.
     * </p>
     * <p>
     * 1.删除所有绑定的事件.
     * </p>
     * <p>
     * 2.从对象管理器中删除注册信息.
     * </p>
     * <p>
     * 3.删除dom节点.
     * </p>
     */
    destroy: function () {
        this.processListener('un');
        $L.CmpManager.remove(this.id);
        this.clearBind();
        delete this.wrap;
    },
    onMouseOver: function (e) {
        this.fireEvent('mouseover', this, e);
    },
    onMouseOut: function (e) {
        this.fireEvent('mouseout', this, e);
    },
    onRemove: function (ds, record) {
        if (this.binder.ds == ds && this.record == record) {
            this.clearValue();
        }
    },
    onCreate: function (ds, record) {
        this.clearInvalid();
        this.record = ds.getCurrentRecord();
        this.setValue('', true);
    },
    onRefresh: function (ds) {
        if (this.isFireEvent == true || this.isHidden == true) return;
        this.clearInvalid();
        this.render(ds.getCurrentRecord());
    },
    render: function (record) {
        this.record = record;
        if (this.record) {
            var value = this.record.get(this.binder.name);
            var field = this.record.getMeta().getField(this.binder.name);
            var config = {};
            Ext.apply(config, this.initConfig);
            Ext.apply(config, field.snap);
            this.initComponent(config);
            if (this.record.valid[this.binder.name]) {
                this.markInvalid();
            }
            // TODO:和lov的设值有问题
// if(this.value == value) return;
            if (!Ext.isEmpty(value, true)) {
                this.setValue(value, true);
            } else {
                this.clearValue();
            }
        } else {
            this.setValue('', true);
        }
    },
    onValid: function (ds, record, name, valid) {
        if (this.binder.ds == ds && this.binder.name == name && this.record == record) {
            if (valid) {
                this.fireEvent('valid', this, this.record, this.binder.name, true)
                this.clearInvalid();
            } else {
                this.fireEvent('valid', this, this.record, this.binder.name, false);
                this.markInvalid();
            }
        }
    },
    onUpdate: function (ds, record, name, value) {
        if (this.binder.ds == ds && this.record == record && this.binder.name == name && this.getValue() !== value) {
            this.setValue(value, true);
        }
    },
    onFieldChange: function (ds, record, field) {
        if (this.binder.ds == ds && this.record == record && this.binder.name == field.name) {
            this.onRefresh(ds);
        }
    },
    onClear: function (ds) {
        this.clearValue();
    },
    /**
     * 设置当前的值.
     *
     * @param {Object}
     *            value 值对象
     * @param {Boolean}
     *            silent 是否更新到dataSet中
     */
    setValue: function (v, silent) {
        var ov = this.value;
        this.value = v;
        if (silent === true)return;
        if (this.binder) {
            this.record = this.binder.ds.getCurrentRecord();
            if (this.record == null) {
                this.record = this.binder.ds.create({}, false);
            }
            this.record.set(this.binder.name, v);
            if (Ext.isEmpty(v, true)) delete this.record.data[this.binder.name];
        }
        // if(ov!=v){
        if (!(ov === v || (Ext.isEmpty(ov) && Ext.isEmpty(v)))) {
            this.fireEvent('change', this, v, ov);
        }
    },
    /**
     * 返回当前值
     *
     * @return {Object} value 返回值.
     */
    getValue: function () {
        var v = this.value;
        v = (v === null || v === undefined ? '' : v);
        return v;
    },
    setWidth: function (w) {
        if (this.width == w) return;
        this.width = w;
        this.wrap.setWidth(w);
    },
    setHeight: function (h) {
        if (this.height == h) return;
        this.height = h;
        this.wrap.setHeight(h);
    },
    /**
     * 显示组件
     */
    show: function () {
        this.wrap.show();
        var prompt = Ext.fly(this.id + '_prompt');
        if (prompt) {
            prompt.show();
        }
    },
    /**
     * 隐藏组件
     */
    hide: function () {
        this.wrap.hide();
        var prompt = Ext.fly(this.id + '_prompt');
        if (prompt) {
            prompt.hide();
        }
    },
    setVisible: function (v) {
        this[v ? 'show' : 'hide']();
    },
    clearInvalid: function () {
    },
    markInvalid: function () {
    },
    clearValue: function () {
    },
    initMeta: function () {
    },
    setDefault: function () {
    },
    setRequired: function () {
    },
    onDataChange: function () {
    }
});
/**
 * @class Leaf.Field
 * @extends Leaf.Component
 *          <p>
 *          带有input标记的输入类的组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.Field = Ext.extend($L.Component, {
    autoselect: true,
    transformcharacter: true,
    validators: [],
    requiredCss: 'item-notBlank',
    readOnlyCss: 'item-readOnly',
    emptyTextCss: 'item-emptyText',
    invalidCss: 'item-invalid',
    wrapInvalidCss: 'item-wrap-invalid',
    wrapRequiredCss: 'item-wrap-notBlank',
    constructor: function (config) {
        config.required = config.required || false;
        config.readonly = config.readonly || false;
        config.autocomplete = config.autocomplete || false;
        config.autocompletefield = config.autocompletefield || null;
//		config.autocompletesize = config.autocompletesize||2;
        /*
         * 徐昭 2017/12/20 14:22
         * 自动查询触发字符长度改为1
         */
        config.autocompletesize = config.autocompletesize || 1;
        config.autocompletepagesize = config.autocompletepagesize || 10;
        this.context = config.context || '';
        $L.Field.superclass.constructor.call(this, config);
    },
    initElements: function () {
        this.el = this.wrap.child('input[atype=field.input]');
        this.inputWrap = this.wrap.child('.item-input-wrap');
        if (this.wrap.child(".tipIcon")) {
            this.tipIcon = this.wrap.child(".tipIcon");
        }
    },
    initComponent: function (config) {
        var sf = this;
        $L.Field.superclass.initComponent.call(sf, config);
        sf.service = sf.autocompleteservice || sf.lovservice || sf.lovmodel;
        sf.para = {}
        sf.initElements();
        sf.originalValue = sf.getValue();
        sf.applyEmptyText();
        sf.initStatus();
// sf.hidden && sf.setVisible(false);
        sf.initService()
        sf.initAutoComplete();
        var inputWrap = sf.wrap.child(".item-input-wrap");
        if (sf.tipdisplay) {
            var tipIcon = sf.wrap.child(".tipIcon");
            tipIcon.setStyle("display", "block");
            if (sf.width === inputWrap.getWidth()) {
                if (inputWrap.hasClass("item-trigger-wrap")) {
                    inputWrapW = inputWrap.getWidth() - tipIcon.getWidth() - 25 + "px";
                } else {
                    inputWrapW = inputWrap.getWidth() - tipIcon.getWidth() - 5 + "px";
                }
                inputWrap.setStyle({"width": inputWrapW, "float": "left"});
            }
        }
    },
    processListener: function (ou) {
        var sf = this;
        $L.Field.superclass.processListener.call(sf, ou);
// sf.el[ou](Ext.isIE || Ext.isSafari3 ? "keydown" : "keypress", sf.fireKey,
// sf);
        sf.el[ou]("focus", sf.onFocus, sf)
            [ou]("blur", sf.onBlur, sf)
            [ou]("change", sf.onChange, sf)
            [ou]("keyup", sf.onKeyUp, sf)
            [ou]("keydown", sf.onKeyDown, sf)
            [ou]("keypress", sf.onKeyPress, sf);
// [ou]("mouseup", sf.onMouseUp, sf)
// [ou]("mouseover", sf.onMouseOver, sf)
// [ou]("mouseout", sf.onMouseOut, sf);
        if (this.wrap.child(".tipIcon")) {
            sf.tipIcon[ou]("mouseover", sf.onTipMouseOver, sf);
            sf.tipIcon[ou]("mouseout", sf.onTipMouseOut, sf);
        }
    },
    processMouseOverOut: function (ou) {
        var sf = this;
        sf.el[ou]("mouseover", sf.onMouseOver, sf)
            [ou]("mouseout", sf.onMouseOut, sf);
    },
    initEvents: function () {
        $L.Field.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event keydown 键盘按下事件.
             * @param {Leaf.Field}
             *            field field对象.
             * @param {EventObject}
             *            e 键盘事件对象.
             */
            'keydown',
            /**
             * @event keyup 键盘抬起事件.
             * @param {Leaf.Field}
             *            field field对象.
             * @param {EventObject}
             *            e 键盘事件对象.
             */
            'keyup',
            /**
             * @event keypress 键盘敲击事件.
             * @param {Leaf.Field}
             *            field field对象.
             * @param {EventObject}
             *            e 键盘事件对象.
             */
            'keypress',
            /**
             * @event enterdown 回车键事件.
             * @param {Leaf.Field}
             *            field field对象.
             * @param {EventObject}
             *            e 键盘事件对象.
             */
            'enterdown');
    },
    onTipMouseOver: function () {
        var sf = this,
            tipIcon = sf.wrap.child(".tipIcon");
        $L.ToolFloat.show(tipIcon, sf.promptContentRenderer());
    },
    promptContentRenderer: function (record) {
        var sf = this,
            renderer = sf.tipcontentrenderer,
            NOT_FOUND = '未找到',
            METHOD = '方法',
            value = $L.escapeHtml(value);
        if (renderer) {//&&!IS_EMPTY(value)  去掉对value是否为空的判断
            var rder = $L.getRenderer(renderer);
            if (rder == null) {
                alert(NOT_FOUND + renderer + METHOD)
                return value;
            }
            value = rder(value, record);
        }
        return value == null ? '' : value;
    },
    onTipMouseOut: function () {
        $L.ToolFloat.hide();
    },
    destroy: function () {
        var sf = this, view = sf.autocompleteview;
        $L.Field.superclass.destroy.call(sf);
        if (view) {
            view.destroy();
            view.un('select', sf.onViewSelect, sf);
            delete sf.autocompleteview;
        }
        delete this.el;
    },
    setWidth: function (w) {
        this.wrap.setStyle("width", (w + 3) + "px");
        // this.el.setStyle("width",w+"px");
    },
    setHeight: function (h) {
        this.wrap.setStyle("height", h + "px");
        this.el.setStyle("height", h + "px");
    },
// setVisible: function(v){
// this.wrap[v?'show':'hide']();
// // if(v==true)
// // this.wrap.show();
// // else
// // this.wrap.hide();
// },
    initStatus: function () {
        var sf = this;
        sf.clearInvalid();
        sf.initReadOnly(sf.readonly);
        sf.initRequired(sf.required, sf.readonly);
        sf.initEditable(sf.editable);
        sf.initMaxLength(sf.maxlength);
    },
// onMouseOver : function(e){
// $L.ToolTip.show(this.id, "测试");
// },
// onMouseOut : function(e){
// $L.ToolTip.hide();
// },
    onChange: function (e) {
    },
    onKeyUp: function (e) {
        this.fireEvent('keyup', this, e);
    },
    onKeyDown: function (e) {
        var sf = this, keyCode = e.keyCode;
        sf.fireEvent('keydown', sf, e);
        if ((sf.isEditor == true && keyCode == 9) || ((sf.readonly || !sf.editable) && keyCode == 8)) e.stopEvent();// 9:tab
                                                                                                                    // 8:backspace
        if (keyCode == 13 || keyCode == 27) {// 13:enter 27:esc
            sf.blur();// 为了获取到新的值
            if (keyCode == 13) {
                (function () {
                    sf.fireEvent('enterdown', sf, e);
                }).defer(5);
            }
        }
    },
    onKeyPress: function (e) {
        this.fireEvent('keypress', this, e);
    },
// fireKey : function(e){
// this.fireEvent("keydown", this, e);
// },
    onFocus: function (e) {
        // (Ext.isGecko||Ext.isGecko2||Ext.isGecko3) ? this.select() :
        // this.select.defer(10,this);
        var sf = this;
        sf.autoselect && sf.select.defer(1, sf);
        if (!sf.hasFocus) {
            sf.hasFocus = true;
            sf.startValue = sf.getValue();
            if (sf.emptytext && !sf.readonly) {
                sf.el.dom.value == sf.emptytext && sf.setRawValue('');
                sf.wrap.removeClass(sf.emptyTextCss);
            }
            sf.wrap.addClass(sf.focusCss);
            sf.fireEvent("focus", sf);
        }
    },
// onMouseUp : function(e){
// this.isSelect && e.stopEvent();
// this.isSelect = false;
// },
    processValue: function (v) {
        return v;
    },
    onBlur: function (e) {
        var sf = this;
        if (sf.hasFocus) {
            sf.hasFocus = false;
// var rv = sf.getRawValue();
// rv = sf.processMaxLength(rv);
// rv = sf.processValue(rv);
// if(String(rv) !== String(sf.startValue)){
// sf.fireEvent('change', sf, rv, sf.startValue);
// }

            !sf.readonly && sf.setValue(sf.processValue(sf.processMaxLength(sf.getRawValue())));
            sf.wrap.removeClass(sf.focusCss);
            sf.fireEvent("blur", sf);
        }
    },
    processMaxLength: function (rv) {
        var sb = [], cLength = $L.defaultChineseLength;
        if (this.isOverMaxLength(rv)) {
            for (var i = 0, k = 0; i < rv.length; i++) {
                var cr = rv.charAt(i),
                    cl = cr.match(/[^\x00-\xff]/g);
                k += cl != null && cl.length > 0 ? cLength : 1;
                if (k <= this.maxlength) {
                    sb.push(cr);
                } else {
                    break;
                }
            }
            return sb.join('');
        }
        return rv;
    },
    setValue: function (v, silent) {
        var sf = this;
        if (sf.emptytext && sf.el && !Ext.isEmpty(v)) {
            sf.wrap.removeClass(sf.emptyTextCss);
        }
        sf.setRawValue(sf.formatValue(Ext.isEmpty(v) ? '' : v));
        sf.applyEmptyText();
        $L.Field.superclass.setValue.call(sf, v, silent);
    },
    formatValue: function (v) {
        var sf = this, rder = sf.renderer ? $L.getRenderer(sf.renderer) : null,
            binder = sf.binder;
        return rder != null ? rder(v, sf.record, binder && binder.name) : v;
    },
    getRawValue: function () {
        var sf = this, v = sf.el.getValue(), typecase = sf.typecase;
        v = v === sf.emptytext || v === undefined ? '' : v;
        if (sf.isDbc(v)) {
            v = sf.dbc2sbc(v);
        }
        if (typecase) {
            if (typecase == 'upper') {
                v = v.toUpperCase();
            } else if (typecase == 'lower') {
                v = v.toLowerCase();
            }
        }
        return v;
    },
// getValue : function(){
// var v= this.value;
// v=(v === null || v === undefined ? '' : v);
// return v;
// },
    initRequired: function (required, readonly) {
        // 只读的字段不提示是否必输
        if (!readonly) {
            var sf = this;
            if (sf.currentRequired == required)return;
            sf.clearInvalid();
            sf.currentRequired = sf.required = required;
            sf.inputWrap[required ? 'addClass' : 'removeClass'](sf.requiredCss);
            var wrapElement = Ext.get(sf.inputWrap).parent('.item-wrap');
            if (wrapElement) {
                wrapElement[required ? 'addClass' : 'removeClass'](sf.wrapRequiredCss);
            }
        }
    },
    initEditable: function (editable) {
        var sf = this;
        if (sf.currentEditable == editable)return;
        sf.currentEditable = sf.editable = editable;
        sf.el.dom.readOnly = sf.readonly ? true : (editable === false);
    },
    initReadOnly: function (readonly) {
        var sf = this;
        if (sf.currentReadonly == readonly)return;
        sf.currentReadonly = sf.readonly = readonly;
        sf.el.dom.readOnly = readonly;
        sf.inputWrap[readonly ? 'addClass' : 'removeClass'](sf.readOnlyCss);
    },
    isOverMaxLength: function (str) {
        if (!this.maxlength) return false;
        var c = 0, i = 0, cLength = $L.defaultChineseLength;
        for (; i < str.length; i++) {
            var cl = str.charAt(i).match(/[^\x00-\xff]/g);
// var st = escape(str.charAt(i));
            c += cl != null && cl.length > 0 ? cLength : 1;
        }
        return c > this.maxlength;
    },
    initMaxLength: function (maxlength) {
        if (maxlength)
            this.el.dom.maxLength = maxlength;
    },
    initService: function () {
        var sf = this, svc = sf.service;
        if (svc) {
            sf.service = sf.processParmater(svc);
        }
    },
    initAutoComplete: function () {
        var sf = this,
            svc = sf.service,
            view = sf.autocompleteview,
            name = sf.binder && sf.binder.name,
            field = sf.autocompletefield || name,
            displayField;
        if (sf.autocomplete && svc) {
            if (!view) {
                view = sf.autocompleteview = new $L.AutoCompleteView({
                    id: sf.id,
                    el: sf.el,
                    hostid: sf.hostid,
                    fuzzyfetch: !(sf.fuzzyfetch == "false"),
                    cmp: sf
                });
                view.on('select', sf.onViewSelect, sf);
            } else if (!view.active) {
                view.processListener('on');
            }
            view.active = true;
            Ext.each(sf.getMapping(), function (map, index) {
                if (map.to == name) {
                    displayField = map.from;
                    return false;
                } else if (!index) {
                    displayField = map.from;
                }
            });
            var autoUrl = svc;
            if(autoUrl&&autoUrl.indexOf('~')!=-1){
                autoUrl = svc.split("~");
                autoUrl = autoUrl[0];
                autoUrl = sf.context + autoUrl.substring(1);
            }else{
                autoUrl = sf.context + 'autocrud/' + svc + '/query';
            }
            view.bind({
                url: autoUrl,
                name: field,
                displayField: displayField,
                size: sf.autocompletesize,
                pagesize: sf.autocompletepagesize,
                renderer: sf.autocompleterenderer,
                binder: sf.binder,
                fetchremote: sf.fetchremote === false ? false : true
            });
        } else if (view) {
            view.processListener('un');
            view.active = false;
        }
    },
    onViewSelect: function (r) {
        var sf = this, record = sf.record;
        Ext.each(r && sf.getMapping(), function (map) {
            var from = r.get(map.from);
            record.set(map.to, Ext.isEmpty(from) ? '' : from);
        });
    },
    getMapping: function () {
        var mapping, r = this.record, name = this.binder.name;
        if (r) {
            var field = r.getMeta().getField(name);
            if (field) {
                mapping = field.get('mapping');
            }
        }
        return mapping ? mapping : [{from: name, to: name}];
    },
    applyEmptyText: function () {
        var sf = this, emptytext = sf.emptytext;
        if (emptytext && sf.getRawValue().length < 1) {
            sf.setRawValue(emptytext);
            sf.wrap.addClass(sf.emptyTextCss);
        }
    },
    processParmater: function (url) {
        var li = url.indexOf('?')
        if (li != -1) {
            this.para = Ext.urlDecode(url.substring(li + 1, url.length));
            return url.substring(0, li);
        }
        return url;
    },
    getPara: function () {
        return Ext.apply({}, this.getFieldPara(), this.para);
    },
    getFieldPara: function (obj) {
        return (obj = this.record)
            && (obj = obj.getMeta().getField(this.binder.name))
            && Ext.apply({}, obj.get('lovpara'));
    },
// validate : function(){
// if(this.readonly || this.validateValue(this.getValue())){
// this.clearInvalid();
// return true;
// }
// return false;
// },
    clearInvalid: function () {
        this.invalidMsg = null;
        this.inputWrap.removeClass(this.invalidCss);
        var wrapElement = Ext.get(this.inputWrap).parent('.item-wrap');
        if (wrapElement) {
            wrapElement.removeClass(this.wrapInvalidCss);
        }
// this.fireEvent('valid', this);
    },
    markInvalid: function (msg) {
        this.invalidMsg = msg;
        this.inputWrap.addClass(this.invalidCss);
        var wrapElement = Ext.get(this.inputWrap).parent('.item-wrap');
        if (wrapElement) {
            wrapElement.addClass(this.wrapInvalidCss);
        }
    },
// validateValue : function(value){
// if(value.length < 1 || value === this.emptyText){ // if it's blank
// if(!this.required){
// this.clearInvalid();
// return true;
// }else{
// this.markInvalid('字段费控');//TODO:测试
// return false;
// }
// }
// Ext.each(this.validators.each, function(validator){
// var vr = validator.validate(value)
// if(vr !== true){
// //TODO:
// return false;
// }
// })
// return true;
// },
    select: function (start, end) {
        if (!this.hasFocus)return;
        var v = this.getRawValue();
        if (v.length > 0) {
            start = start === undefined ? 0 : start;
            end = end === undefined ? v.length : end;
            var d = this.el.dom;
            if (start === 0 && end === v.length && d.select) {
                d.select();
            } else {
                if (d.setSelectionRange) {
                    d.setSelectionRange(start, end);
                } else if (d.createTextRange) {
                    var range = d.createTextRange();
                    range.moveStart("character", start);
                    range.moveEnd("character", end - v.length);
                    range.select();
                }
            }
        }
// this.isSelect = true;
    },
    setRawValue: function (v) {
        var dom = this.el.dom;
        if (dom.value === (v = Ext.isEmpty(v) ? '' : v)) return;
        return (dom.title = v,dom.value = v);
    },
    reset: function () {
        var sf = this;
        sf.setValue(sf.originalValue);
        sf.clearInvalid();
        sf.applyEmptyText();
    },
    /**
     * 组件获得焦点
     */

    focus: function () {
        this.el.dom.focus();
        /*
         * 因为上面的onFocus有fireEvent监听，这里再加上的话会触发两次
         * 所以去掉这个事件
         * 向平  2017-12-08
         */
//    	this.fireEvent('focus', this);
    },
    /**
     * 组件失去焦点
     */
    blur: function () {
        this.el.blur();
        this.fireEvent('blur', this);
    },
    clearValue: function () {
        this.setValue('', true);
        this.clearInvalid();
        this.applyEmptyText();
    },
    /**
     * 设置prompt
     *
     * @param {String}
     *            text prompt.
     */
    setPrompt: function (text) {
        var prompt = Ext.fly(this.id + '_prompt');
        if (prompt) {
            prompt.update(text);
        }
    },
    isDbc: function (s) {
        if (!this.transformcharacter) return false;
        var dbc = false;
        for (var i = 0; i < s.length; i++) {
            var c = s.charCodeAt(i);
            if ((c > 65248) || (c == 12288)) {
                dbc = true
                break;
            }
        }
        return dbc;
    },
    dbc2sbc: function (str) {
        var result = [];
        for (var i = 0; i < str.length; i++) {
            var code = str.charCodeAt(i);// 获取当前字符的unicode编码
            if (code >= 65281 && code <= 65373 && code !== 65288 && code !== 65289) {// 在这个unicode编码范围中的是所有的英文字母已及各种字符   全角的左右圆括号不转为半角圆括号
                result.push(String.fromCharCode(code - 65248));// 把全角字符的unicode编码转换为对应半角字符的unicode码
            } else if (code == 12288) {// 空格
                result.push(' ');
            } else {
                result.push(str.charAt(i));
            }
        }
        return result.join('');
    }
});
/**
 * @class Leaf.Box
 * @extends Leaf.Component
 *          <p>
 *          Box组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.Box = Ext.extend($L.Component, {
    constructor: function (config) {
        this.errors = [];
        $L.Box.superclass.constructor.call(this, config);
    },
// initComponent : function(config){
// config = config || {};
// Ext.apply(this, config);
    // TODO:所有的组件?
// for(var i=0;i<this.cmps.length;i++){
// var cmp = $le(this.cmps[i]);
// if(cmp){
// cmp.on('valid', this.onValid, this)
// cmp.on('invalid', this.onInvalid,this)
// }
// }
// },
    initEvents: function () {
// this.addEvents('focus','blur','change','invalid','valid');
    },
    onValid: function (cmp, record, name, isvalid) {
        if (isvalid) {
            this.clearError(cmp.id);
        } else {
            var error = record.errors[name];
            if (error) {
                this.showError(cmp.id, error.message)
            }
        }
    },
    showError: function (id, msg) {
        Ext.fly(id + '_vmsg').update(msg)
    },
    clearError: function (id) {
        Ext.fly(id + '_vmsg').update('')
    },
    clearAllError: function () {
        for (var i = 0; i < this.errors.length; i++) {
            this.clearError(this.errors[i])
        }
    }
});
/**
 * @class Leaf.ImageCode
 * @extends Leaf.Component
 *          <p>
 *          图片验证码组件.
 * @author njq.niu@hand-china.com
 * @constructor
 */
$L.ImageCode = Ext.extend($L.Component, {
    processListener: function (ou) {
        $L.ImageCode.superclass.processListener.call(this, ou);
        this.wrap[ou]("click", this.onClick, this);
    },
    onClick: function () {
        if (this.enable == true)
            this.refresh();
    },
    setEnable: function (isEnable) {
        if (isEnable == true) {
            this.enable = true;
            this.refresh();
        } else {
            this.enable = false;
            this.wrap.dom.src = "";
        }
    },
    /**
     * 重新加载验证码
     *
     */
    refresh: function () {
        this.wrap.dom.src = "imagecode?r=" + Math.random();
    }
});
/**
 * @class Leaf.Label
 * @extends Leaf.Component
 *          <p>
 *          Label组件.
 * @author njq.niu@hand-china.com
 * @constructor
 */
$L.Label = Ext.extend($L.Component, {
    onUpdate: function (ds, record, name, value) {
        if (this.binder.ds == ds && this.binder.name == name) {
            this.updateLabel(record, name, value);
        }
    },
    /**
     * 绘制Label
     *
     * @param {Leaf.Record}
     *            record record对象
     */
    render: function (record) {
        this.record = record;
        if (this.record) {
            var value = this.record.get(this.binder.name);
            this.updateLabel(this.record, this.binder.name, value);
        }
    },
    updateLabel: function (record, name, value) {
        var rder = $L.getRenderer(this.renderer);
        if (rder != null) {
            value = rder.call(window, value, record, name);
        }
        this.wrap.update(value);
    },
    setPrompt: function (text) {
        var prompt = Ext.fly(this.id + '_prompt');
        if (prompt) {
            prompt.update(text);
        }
    }
});
/**
 * @class Leaf.Link
 * @extends Leaf.Component
 *          <p>
 *          Link组件.
 * @author njq.niu@hand-china.com
 * @constructor
 */
$L.Link = Ext.extend($L.Component, {
    params: {},
    constructor: function (config) {
        this.url = config.url || "";
        $L.Link.superclass.constructor.call(this, config);
    },
    processListener: function (ou) {
    },
    reset: function () {
        this.params = {};
    },
    /**
     * 增加参数值
     *
     * @param {String}
     *            name 参数名
     * @param {Object}
     *            value 参数值
     */
    set: function (name, value) {
        this.params[name] = value;
    },
    /**
     * 返回参数值
     *
     * @param {String}
     *            name 参数名
     * @return {Object} obj 返回值
     */
    get: function (name) {
        return this.params[name];
    },
    /**
     * 返回生成的URL
     *
     * @return {String} url
     */
    getUrl: function () {
        var url;
        var pr = Ext.urlEncode(this.params);
        if (Ext.isEmpty(pr)) {
            url = this.url;
        } else {
            url = this.url + (this.url.indexOf('?') == -1 ? '?' : '&') + Ext.urlEncode(this.params);
        }
        return url;
    }
});
$L.HotKey = function () {
    var CTRL = 'CTRL',
        ALT = 'ALT',
        SHIFT = 'SHIFT',
        hosts = {},
        enable = true,
        onKeyDown = function (e, t) {
            var key = e.keyCode, bind = [], handler, sf = this;
            if (key != 16 && key != 17 && key != 18) {
                e.ctrlKey &&
                bind.push(CTRL);
                e.altKey &&
                bind.push(ALT);
                e.shiftKey &&
                bind.push(SHIFT);
                bind.push(String.fromCharCode(key));
                handler = hosts[sf.id][bind.join('+').toUpperCase()];
                if (handler) {
                    e.stopEvent();
                    if (enable) {
                        enable = false;
                        var focuser = Ext.get(t),
                            tagName = t.tagName.toLowerCase(),
                            fns = function (e) {
                                Ext.each(handler, function (fn) {
                                    return fn();
                                });
                                focuser.un('focus', fns);
                            }
                        if (tagName == 'input' || tagName == 'textarea')
                            focuser.on('focus', fns).blur().focus();
                        else
                            fns();
                    }
                }
            }
        },
        onKeyUp = function () {
            enable = true;
        },
        on = function (host) {
            host.on('keydown', onKeyDown, host, {stopPropagation: true})
                .on('keyup', onKeyUp);
        },
        pub = {
            addHandler: function (bind, handler) {
                var binds = bind.toUpperCase().split('+'), key = [],
                    host = window['__host'] || Ext.getBody(),
                    id = host.id,
                    keys = hosts[id];
                if (!keys) {
                    hosts[id] = keys = {};
                    on(host);
                }
                binds.indexOf(CTRL) != -1 &&
                key.push(CTRL);
                binds.indexOf(ALT) != -1 &&
                key.push(ALT);
                binds.indexOf(SHIFT) != -1 &&
                key.push(SHIFT);
                if (key.length < binds.length) {
                    key.push(binds.pop());
                    key = key.join('+');
                    (keys[key] || (keys[key] = [])).add(handler);
                }
            }
        };
    return pub;
}();
(function (A) {
    var TR = 'TR',
        SELECTED_CLS = 'autocomplete-selected',
        EVT_CLICK = 'click',
        EVT_MOUSE_MOVE = 'mousemove',
        EVT_MOUSE_DOWN = 'mousedown',
        TEMPLATE = ['<div id="{id}" tabIndex="-2" class="item-popup item-shadow" style="visibility:hidden;background-color:#fff;width:{width}px">{shadow}', '<div class="item-popup-content"></div>', '</div>'],
        SHADOW_TEMPLATE = ['<div id="{id}" class="item-ie-shadow">', '</div>'],
        AUTO_COMPLATE_TABLE_START = '<table class="autocomplete" cellspacing="0" cellpadding="2">';
    A.AutoCompleteView = Ext.extend($L.Component, {
        constructor: function (config) {
            var sf = this;
            config.id = config.id + '_autocomplete';
            sf.isLoaded = false;
            sf.maxHeight = 280;
            sf.minWidth = 180;
            sf.delay = 500;
            $L.AutoCompleteView.superclass.constructor.call(sf, config);
        },
        initComponent: function (config) {
            var sf = this;
            $L.AutoCompleteView.superclass.initComponent.call(sf, config);
            sf.wrap = new Ext.Template(TEMPLATE).insertFirst(document.body, {
                width: sf.width,
                height: sf.height,
                id: sf.id,
                shadow: Ext.isIE ? SHADOW_TEMPLATE.join('') : ''
            }, true);
            sf.popupContent = sf.wrap.child('div.item-popup-content');
// sf.shadow = new
// Ext.Template(SHADOW_TEMPLATE).insertFirst(document.body,{width:sf.width,height:sf.height,id:sf.id+'_shadow'},true);
            sf.ds = new A.DataSet({id: sf.id + "_ds", autocount: false, hostid: sf.id});
        },
        processListener: function (ou) {
            $L.AutoCompleteView.superclass.processListener.call(this, ou);
            var sf = this,
                ds = sf.ds;
            sf.el[ou]('keyup', sf.onKeyUp, sf)
                [ou]('keydown', sf.onKeyDown, sf)
                [ou]('blur', sf.onBlur, sf);
            ds[ou]('load', sf.onLoad, sf);
            ds[ou]('query', sf.onQuery, sf);
            sf.wrap[ou](EVT_CLICK, sf.onClick, sf)
                [ou]('mousedown', sf.onMouseDown, sf, {preventDefault: true})
        },
        initEvents: function () {
            $L.AutoCompleteView.superclass.initEvents.call(this);
            this.addEvents(
                /**
                 * @event select 选择记录.
                 * @param {Leaf.Record}
                 *            r 选择的记录.
                 */
                'select',
                EVT_CLICK);
        },
        bind: function (obj) {
            Ext.apply(this, obj);
        },
        destroy: function () {
            var sf = this, wrap = sf.wrap;
            Ext.iterate(wrap.cmps, function (key, cmp) {
                try {
                    cmp.destroy && cmp.destroy();
                } catch (e) {
                    alert('销毁AutoCompleteView出错: ' + e)
                }
                ;
            })
// sf.ds.destroy();
// sf.shadow.remove();
            $L.AutoCompleteView.superclass.destroy.call(sf);
            wrap.remove();
            delete sf.ds;
// delete sf.shadow;
        },
        onQuery: function () {
            var sf = this;
            sf.popupContent.update('<table cellspacing="0" cellpadding="2"><tr tabIndex="-2"><td>' + _lang['lov.query'] + '</td></tr></table>')
                .un(EVT_MOUSE_MOVE, sf.onMove, sf);
            sf.correctViewSize();
        },
        onLoad: function () {
            var sf = this,
                datas = sf.ds.getAll(),
                l = datas.length, view = sf.popupContent,
                sb;
            sf.selectedIndex = null;
            if (l == 0) {
                sb = [AUTO_COMPLATE_TABLE_START, '<tr tabIndex="-2"><td>', _lang['lov.notfound'], '</td></tr></table>'];
            } else {
                sb = sf.createListView(datas, sf.binder);
                view.on(EVT_MOUSE_MOVE, sf.onMove, sf);
            }
            sf.isLoaded = true;
            view.update(sb.join(''));
            sf.correctViewSize();
        },
        onKeyDown: function (e) {
            if (this.isShow) {
                var sf = this,
                    keyCode = e.keyCode,
                    index = sf.selectedIndex;
                if (keyCode == 13) {
                    if (index != null) {
                        sf.el.blur();
                        (function () {
                            sf.onSelect(index);
                            sf.hide();
                        }).defer(10, sf);
                    } else {
                        sf.hide();
                    }
                } else if (keyCode == 27 || keyCode == 9) {
                    sf.hide();
// sf.el.blur();
                } else if (sf.ds.getAll().length > 0) {
                    if (keyCode == 38) {
                        sf.selectItem(index == null ? -1 : index - 1, true);
                    } else if (keyCode == 40) {
                        sf.selectItem(index == null ? 0 : index + 1, true);
                    }
                }
            }
        },
        onKeyUp: function (e) {
            var sf = this, svc = sf.url,
                cmp = sf.cmp,
                v = (cmp ? cmp.getRawValue() : sf.el.getValue()).trim(),
                code = e.keyCode;
            sf.fireEvent('keyup', sf, e);
            if (code > 40 || (code < 37 && code != 13 && code != 27 && code != 9 && code != 17)) {
                if (v.length >= sf.size) {
                    if (sf.showCompleteId) clearTimeout(sf.showCompleteId);
                    sf.showCompleteId = function () {
                        var ds = sf.ds;
                        ds.setQueryUrl(Ext.urlAppend(svc, Ext.urlEncode(cmp ? cmp.getPara() : sf.para)));
                        ds.setQueryParameter(sf.name, sf.fuzzyfetch ? v + '%' : v);
                        ds.pagesize = sf.pagesize;
                        sf.show();
                        ds.query();
                        delete sf.showCompleteId;
                    }.defer(sf.delay);
                } else {
                    if (sf.showCompleteId) {
                        clearTimeout(sf.showCompleteId);
                        delete sf.showCompleteId;
                    }
                    sf.hide();
                }
            }
        },
        onBlur: function (e) {
            var sf = this;
            if (sf.showCompleteId) {
                clearTimeout(sf.showCompleteId);
                delete sf.showCompleteId;
            }
        },
        onMove: function (e, t) {
            this.selectItem((Ext.fly(t).findParent(TR) || t).tabIndex);
        },
        onClick: function (e, t) {
            t = Ext.fly(t).findParent(TR) || t;
            if (t.tagName != TR) {
                return;
            }
            this.onSelect(t);
            this.hide();
        },
        onMouseDown: function () {
            var sf = this;
            (function () {
                sf.el.focus();
            }).defer(Ext.isIE ? 1 : 0, sf);
        },
        onSelect: function (target) {
            var sf = this, r,
                index = Ext.isNumber(target) ? target : target.tabIndex;
            if (index > -1) {
                r = sf.ds.getAt(index);
            }
            sf.fireEvent('select', r);
            sf.el.focus();
        },
        selectItem: function (index, focus) {
            if (Ext.isEmpty(index) || index < -1) {
                return;
            }
            var sf = this, node = sf.getNode(index), selectedIndex = sf.selectedIndex;
            if (node && (index = node.tabIndex) != selectedIndex) {
                if (!Ext.isEmpty(selectedIndex)) {
                    Ext.fly(sf.getNode(selectedIndex)).removeClass(SELECTED_CLS);
                }
                sf.selectedIndex = index;
                if (focus) sf.focusRow(index);
                Ext.fly(node).addClass(SELECTED_CLS);
            }
        },
        focusRow: function (row) {
            var binder = this.binder,
                displayFields = binder ? binder.ds.getField(binder.name).getPropertity('displayFields') : null,
                head = displayFields && displayFields.length ? 23 : 0,
                r = 22,
                ub = this.popupContent,
                stop = ub.getScroll().top,
                h = ub.getHeight(),
                sh = ub.dom.scrollWidth > ub.dom.clientWidth ? 16 : 0;
            if (row * r < stop) {
                ub.scrollTo('top', row * r - 1)
            } else if ((row + 1) * r + head > (stop + h - sh)) {// this.ub.dom.scrollHeight
                ub.scrollTo('top', (row + 1) * r - h + sh + head);
            }
        },
        getNode: function (index) {
            var nodes = this.popupContent.query('tr[tabindex!=-2]'), l = nodes.length;
            if (index >= l) index = index % l;
            else if (index < 0) index = l + index % l;
            return nodes[index];
        },
        show: function () {
            var sf = this, view;
            if (!sf.isShow) {
                sf.isShow = true;
                view = sf.popupContent;
                sf.position();
                view.dom.className = 'item-popup-content item-comboBox-view';
                view.update('');
                sf.wrap.show();
// sf.shadow.show();
                Ext.get(document).on(EVT_MOUSE_DOWN, sf.trigger, sf);
            }
            $jq(".item-comboBox-view").niceScroll({horizrailenabled: false});
        },
        trigger: function (e) {
            var sf = this;
            if (!sf.wrap.contains(e.target) && (!sf.owner || !sf.owner.wrap.contains(e.target))) {
                sf.hide();
            }
        },
        hide: function (e) {
            var sf = this;
            if (sf.isShow) {
                sf.isShow = false;
                sf.isLoaded = false;
                Ext.get(document).un(EVT_MOUSE_DOWN, sf.trigger, sf)
                sf.wrap.hide();
// sf.shadow.hide();
            }
        },
        position: function () {
            var sf = this,
                wrap = sf.cmp ? sf.cmp.wrap : sf.el,
                scroll = Ext.getBody().getScroll(),
                sl = scroll.left,
                st = scroll.top,
                xy = wrap.getXY(),
                _x = xy[0] - sl,
                _y = xy[1] - st,
                W = sf.getWidth(),
                H = sf.getHeight(),
                PH = wrap.getHeight(),
                PW = wrap.getWidth(),
                BH = A.getViewportHeight() - 3,
                BW = A.getViewportWidth() - 3,
                x = ((_x + W) > BW ? ((BW - W) < 0 ? _x : (BW - W)) : _x) + sl;
            y = ((_y + PH + H) > BH ? ((_y - H) < 0 ? (_y + PH) : (_y - H)) : (_y + PH)) + st;
            sf.moveTo(x, y);
        },
        createListView: function (datas, binder) {
            var sb = [AUTO_COMPLATE_TABLE_START],
                displayFields;
            if (binder) {
                displayFields = binder.ds.getField(binder.name).getPropertity('displayFields');
                if (displayFields && displayFields.length) {
                    sb.push('<tr tabIndex="-2" class="autocomplete-head">');
                    Ext.each(displayFields, function (field) {
                        sb.push('<td>', field.prompt, '</td>');
                    });
                    sb.push('</tr>');
                }
            }
            for (var i = 0, l = datas.length; i < l; i++) {
                var d = datas[i];
                sb.push('<tr tabIndex="', i, '"', i % 2 == 1 ? ' class="autocomplete-row-alt"' : '', '>', this.getRenderText(d, displayFields), '</tr>');	// sf.litp.applyTemplate(d)等数据源明确以后再修改
            }
            sb.push('</table>');
            return sb;
        },
        getRenderText: function (record, displayFields) {
            var sf = this,
                rder = A.getRenderer(sf.renderer),
                text = [],
                fn = function (t) {
                    var v = record.get(t);
                    text.push('<td>', Ext.isEmpty(v) ? '&#160;' : v, '</td>');
                };
            if (rder) {
                text.push(rder.call(window, sf, record));
            } else if (displayFields && displayFields.length) {
                Ext.each(displayFields, function (field) {
                    fn(field.name);
                });
            } else {
                fn(sf.displayField);
            }
            return text.join('');
        },
        correctViewSize: function () {
            var sf = this,
                table = sf.popupContent.child('table'),
                width = Math.max(sf.minWidth, table.getWidth());
            sf.setHeight(Math.max(Math.min(table.getHeight() + 2, sf.maxHeight), 20));
            sf.setWidth(width);
            table.setStyle({width: '100%'});
            sf.position();
        },
        moveTo: function (x, y) {
            this.wrap.moveTo(x, y);
// this.shadow.moveTo(x+3,y+3);
        },
        setHeight: function (h) {
            this.wrap.setHeight(h);
// this.shadow.setHeight(h);
        },
        setWidth: function (w) {
            this.wrap.setWidth(w);
// this.shadow.setWidth(w);
        },
        getHeight: function () {
            return this.wrap.getHeight();
        },
        getWidth: function () {
            return this.wrap.getWidth();
        }
    });


})($L);
/**
 * @class Leaf.DynamicElement
 * @extends Leaf.Component
 *          <p>
 *          窗口组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.DynamicElement = Ext.extend($L.Component, {
    constructor: function (id) {
        this.cmps = {};
        $L.DynamicElement.superclass.constructor.call(this, {id: id});
    },
    initComponent: function (config) {
        $L.DynamicElement.superclass.initComponent.call(this, config);
        var sf = this;
        sf.wrap.cmps = sf.cmps;
        if (sf.url) {
            sf.load(sf.url, config.params)
        }
    },
    initEvents: function () {
        $L.DynamicElement.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event unload 卸载完毕.
             */
            'unload',
            /**
             * @event load 加载完毕.
             */
            'load');
    },
    clearBody: function () {
        for (var key in this.cmps) {
            var cmp = this.cmps[key];
            if (cmp.destroy) {
                try {
                    cmp.destroy();
                } catch (e) {
                    alert('DynamicElement卸载失败: ' + e)
                }
            }
        }
    },
    /**
     * 卸载.
     *
     */
    unload: function () {
        this.clearBody();
        this.fireEvent('unload', this);
    },
    /**
     * 加载.
     *
     * @param {String}
     *            url 加载的url
     * @param {Object}
     *            params 加载的参数
     */
    load: function (url, params) {
        this.clearBody();
        var header = $jq('meta[name=_csrf_header]').attr('content');
        var token = $jq('meta[name=_csrf]').attr('content');
        var headers = {};
        headers[header] = token;
        Ext.Ajax.request({
            url: url,
            headers:headers,
            params: params || {},
            success: this.onLoad.createDelegate(this)
        });
    },
    onLoad: function (response, options) {
        var html = response.responseText;
        var res
        try {
            res = Ext.decode(response.responseText);
        } catch (e) {
        }
        if (res && res.success == false) {
            if (res.error) {
                if (res.error.code && res.error.code == 'session_expired' || res.error.code == 'login_required') {
                    if ($L.manager.fireEvent('timeout', $L.manager))
                        $L.showErrorMessage(_lang['ajax.error'], _lang['session.expired']);
                } else {
                    $L.manager.fireEvent('ajaxfailed', $L.manager, options.url, options.para, res);
                    var st = res.error.stackTrace;
                    st = (st) ? st.replaceAll('\r\n', '</br>') : '';
                    if (res.error.message) {
                        var h = (st == '') ? 150 : 250;
                        $L.showErrorMessage(_lang['window.error'], res.error.message + '</br>' + st, null, 400, h);
                    } else {
                        $L.showErrorMessage(_lang['window.error'], st, null, 400, 250);
                    }
                }
            }
            return;
        }
        var sf = this;
        sf.wrap.update(html, true, function () {
            sf.fireEvent('load', sf)
        }, sf.wrap);
    },
    destroy: function () {
        var wrap = this.wrap;
        $L.DynamicElement.superclass.destroy.call(this);
        this.clearBody();
        wrap.remove();
    }
});
/**
 * @class Leaf.Button
 * @extends Leaf.Component
 *          <p>
 *          按钮组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.Button = Ext.extend($L.Component, {
    disableCss: 'item-btn-disabled',
    overCss: 'item-btn-over',
    pressCss: 'item-btn-pressed',
    disabled: false,
// constructor: function(config) {
// $L.Button.superclass.constructor.call(this, config);
// },
    initComponent: function (config) {
        $L.Button.superclass.initComponent.call(this, config);
        this.el = this.wrap.child('button[atype=btn]');
        this.textEl = this.el.child('div');
// if(this.hidden == true)this.setVisible(false)
        if (this.disabled == true) this.disable();
    },
    processListener: function (ou) {
        $L.Button.superclass.processListener.call(this, ou);
        this.wrap[ou]("click", this.onClick, this);
        this.wrap[ou]("mousedown", this.onMouseDown, this);
        this.el[ou]("focus", this.onFocus, this);
        this.el[ou]("blur", this.onBlur, this);
        this.el[ou]("keydown", this.onKeyDown, this);
    },
    initEvents: function () {
        $L.Button.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event click 鼠标点击事件.
             * @param {Leaf.Button}
             *            button 按钮对象.
             * @param {EventObject}
             *            e 键盘事件对象.
             */
            'click');
    },
    /**
     * 点击按钮
     */
    click: function () {
        this.el.dom.click();
    },
    destroy: function () {
        $L.Button.superclass.destroy.call(this);
        delete this.el;
    },
    /**
     * 设置按钮是否可见.
     *
     * @param {Boolean}
     *            visiable 是否可见.
     */
// setVisible: function(v){
// if(v==true)
// this.wrap.show();
// else
// this.wrap.hide();
// },
// destroy : function(){
// $L.Button.superclass.destroy.call(this);
// this.el.un("click", this.onClick, this);
// delete this.el;
// },
    /**
     * 获取焦点
     */
    focus: function () {
        if (this.disabled)return;
        this.el.dom.focus();
    },
    /**
     * 失去焦点
     */
    blur: function () {
        if (this.disabled) return;
        this.el.dom.blur();
    },
    /**
     * 设置不可用状态
     */
    disable: function () {
        this.disabled = true;
        this.wrap.addClass(this.disableCss);
        this.el.dom.disabled = true;
    },
    /**
     * 设置可用状态
     */
    enable: function () {
        this.disabled = false;
        this.wrap.removeClass(this.disableCss);
        this.el.dom.disabled = false;
    },
    onMouseDown: function (e) {
        if (!this.disabled) {
            Ext.get(document.documentElement).un("mousedown", this.onMouseDown, this);
            this.wrap.removeClass(this.pressCss);
        }
    },
    onMouseUp: function (e) {
        if (!this.disabled) {
            Ext.get(document.documentElement).un("mouseup", this.onMouseUp, this);
            this.wrap.removeClass(this.pressCss);
        }
    },
    onKeyDown: function (e) {
        if (!this.disabled && e.keyCode == 13) {
            this.wrap.addClass(this.pressCss);
            Ext.get(document.documentElement).on("keyup", this.onKeyUp, this);
        }
    },
    onKeyUp: function (e) {
        if (!this.disabled && e.keyCode == 13) {
            Ext.get(document.documentElement).un("keyup", this.onKeyUp, this);
            if (this.wrap) this.wrap.removeClass(this.pressCss);
        }
    },
    onClick: function (e) {
        if (!this.disabled) {
            e.stopEvent();
            this.fireEvent("click", this, e);
        }
    },
    onFocus: function (e) {
        this.hasFocus = true;
        this.onMouseOver(e);
    },
    onBlur: function (e) {
        this.hasFocus = false;
        this.onMouseOut(e)
    },
    onMouseOver: function (e) {
        if (!this.disabled)
            this.wrap.addClass(this.overCss);
        var skin = this.wrap.getAttribute('auskin');
        if (skin === 'icon' || skin === 'custom') {
            var text = this.wrap.getAttribute("text");
            $L.ToolTip.show(this.wrap, text);
        }
        $L.Button.superclass.onMouseOver.call(this, e);
    },
    onMouseOut: function (e) {
        if (!this.disabled)
            this.wrap.removeClass(this.overCss);
        $L.Button.superclass.onMouseOut.call(this, e);
    },
    /**
     * 设置按钮的文本.
     *
     * @param {String}
     *            text 文本.
     */
    setText: function (text) {
        this.textEl.update(text);
    },
    /**
     * 设置按钮的图标.
     *
     * @param {String}
     *            url 图片路径.
     * @param {String}
     *            align 图片定位，可选值：left|top.
     */
    setIcon: function (url, align) {
        this.wrap.addClass(this.textEl.setStyle({'background-image': 'url(' + url + ')'}).dom.innerHTML.replace(/\s+|&nbsp;/g, '') == '' ?
            'item-btn-icon' : ((this.iconalign = align) == 'top' ?
                'item-btn-icon-text-top' : 'item-btn-icon-text'));
    },
    setHeight: function (h) {
        var sf = this;
        if (sf.height == h) return;
        sf.height = h;
        sf.el.setHeight(h);
        sf.textEl.setStyle({
            height: sf.iconalign == 'top' ? '' : (h + 'px')
        });
    }
});
/*
 * 修改提示框中按钮的大小和样式
 */
// $L.Button.getTemplate = function(id,text,width){
$L.Button.getTemplate = function (id, text, width, height) {
// return '<TABLE class="item-btn " id="'+id+'" style="WIDTH: '+(width||60)+'px"
// cellSpacing="0"><TBODY><TR><TD class="item-btn-tl"><I></I></TD><TD
// class="item-btn-tc"></TD><TD class="item-btn-tr"><I></I></TD></TR><TR><TD
// class="item-btn-ml"><I></I></TD><TD class="item-btn-mc"><BUTTON hideFocus
// style="HEIGHT: 17px" atype="btn"><div>'+text+'</div></BUTTON></TD><TD
// class="item-btn-mr"><I></I></TD></TR><TR><TD
// class="item-btn-bl"><I></I></TD><TD class="item-btn-bc"></TD><TD
// class="item-btn-br"><I></I></TD></TR></TBODY></TABLE><script>new
// Leaf.Button({"id":"'+id+'"});</script>';
    return '<TABLE class="item-btn " id="' + id + '" style="WIDTH: ' + (width || 80) + 'px; HEIGHT: ' + (height || 26) + 'px" cellSpacing="0"><TBODY><!--<TR><TD class="item-btn-tl"><I></I></TD><TD class="item-btn-tc"></TD><TD class="item-btn-tr"><I></I></TD></TR>--><TR><!--<TD class="item-btn-ml"><I></I></TD>--><TD class="item-btn-mc" auskin="white"><BUTTON hideFocus skin="white" style="HEIGHT: 17px" atype="btn"><div auskin="white">' + text + '</div></BUTTON></TD><!--<TD class="item-btn-mr"><I></I></TD>--></TR><!--<TR><TD class="item-btn-bl"><I></I></TD><TD class="item-btn-bc"></TD><TD class="item-btn-br"><I></I></TD></TR>--></TBODY></TABLE><script>new Leaf.Button({"id":"' + id + '"});</script>';
}
$L.SwitchButton = Ext.extend($L.Component, {
    curCls: 'cur',
    initComponent: function (config) {
        $L.SwitchButton.superclass.initComponent.call(this, config);
    },
    processListener: function (ou) {
        $L.SwitchButton.superclass.processListener.call(this, ou);
        this.wrap[ou]('click', this.onClick, this);
    },
    initEvents: function () {
        $L.SwitchButton.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event select 选择事件.
             * @param {Leaf.Tab}
             *            tab Tab对象.
             * @param {String}
             *            newValue value1.
             * @param {String}
             *            oldValue value2.
             */
            'select'
        );
    },
    onClick: function (e) {
        var li = Ext.fly(e.target).findParent('li');
        if (li)
            this.selectButton(Ext.fly(li).getAttributeNS("", "code"));
    },
    selectButton: function (value) {
        if (this.activeValue == value) return;
        var oli = this.wrap.child('li[code=' + this.activeValue + ']'), li = this.wrap.child('li[code=' + value + ']'), ov;
        if (li) {
            if (oli) {
                oli.removeClass(this.curCls);
                ov = Ext.fly(oli).getAttributeNS("", "code");
            }
            li.addClass(this.curCls);
            this.activeValue = value;
            this.fireEvent('select', this, value, ov)
        }
    }
})
/**
 * @class Leaf.ToogleButton
 * @extends Leaf.Component
 *          <p>
 *          ToogleButton.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.ToogleButton = Ext.extend($L.Component, {
    disableCss: 'item-btn-disabled',
    disabled: false,
    toogled: false,
    plusText: '&#xe6e3;',
    minusText: '&#xe6c4;',
    initComponent: function (config) {
        $L.ToogleButton.superclass.initComponent.call(this, config);
        if (this.disabled == true) this.disable();
        var sf = this;
        $L.onReady(function () {
            sf.initToogleEl();
        });

    },
    processListener: function (ou) {
        $L.ToogleButton.superclass.processListener.call(this, ou);
        this.wrap[ou]("click", this.onClick, this);
    },
    initEvents: function () {
        $L.ToogleButton.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event click 鼠标点击事件.
             * @param {Leaf.Button}
             *            button 按钮对象.
             * @param {EventObject}
             *            e 键盘事件对象.
             */
            'click');
    },
    destroy: function () {
        $L.ToogleButton.superclass.destroy.call(this);
        delete this.wrap;
    },
    blur: function () {
        if (this.disabled) return;
        this.el.dom.blur();
    },
    disable: function () {
        this.disabled = true;
        this.wrap.addClass(this.disableCss);
    },
    enable: function () {
        this.disabled = false;
        this.wrap.removeClass(this.disableCss);
    },
    initToogleEl: function () {
        if (this.toogleid) {
            var el = Ext.get(this.toogleid);
            if (el) {
                el.setStyle('display', this.toogled ? 'block' : 'none')
            }
        }
    },
    onClick: function (e) {
        if (!this.disabled) {
            e.stopEvent();
            this.toogled = !this.toogled;
            this.setText(this.toogled ? this.minusText : this.plusText);
            this.initToogleEl();
            this.fireEvent("click", this, this.toogled, e);
        }
    },
    setText: function (text) {
        this.wrap.update(text);
    }
});
/**
 * @class Leaf.CheckBox
 * @extends Leaf.Component
 *          <p>
 *          可选组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.CheckBox = Ext.extend($L.Component, {
    checkedCss: 'item-ckb-c',
    uncheckedCss: 'item-ckb-u',
    readonyCheckedCss: 'item-ckb-readonly-c',
    readonlyUncheckedCss: 'item-ckb-readonly-u',
    constructor: function (config) {
        config.checked = config.checked || false;
        config.readonly = config.readonly || false;
        $L.CheckBox.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        this.checkedvalue = 'Y';
        this.uncheckedvalue = 'N';
        $L.CheckBox.superclass.initComponent.call(this, config);
        this.wrap = Ext.get(this.id);
        this.el = this.wrap.child('div[atype=checkbox]');
    },
    processListener: function (ou) {
        this.wrap
            [ou]('mousedown', this.onMouseDown, this)
            [ou]('click', this.onClick, this);
        this.el[ou]('keydown', this.onKeyDown, this);
        this.el[ou]('focus', this.onFocus, this)
        this.el[ou]('blur', this.onBlur, this)
    },
    initEvents: function () {
        $L.CheckBox.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event click 鼠标点击事件.
             * @param {Leaf.CheckBox}
             *            checkBox 可选组件.
             * @param {Boolean}
             *            checked 选择状态.
             */
            'click');
    },
    destroy: function () {
        $L.CheckBox.superclass.destroy.call(this);
        delete this.el;
    },
    onKeyDown: function (e) {
        var keyCode = e.keyCode;
        if (keyCode == 32) {
            this.onClick.call(this, e);
            e.stopEvent();
        }
    },
    onMouseDown: function (e) {
        var sf = this;
        sf.hasFocus && e.stopEvent();
        sf.focus.defer(Ext.isIE ? 1 : 0, sf);
    },
    onClick: function (event) {
        if (!this.readonly) {
            this.checked = this.checked ? false : true;
            this.setValue(this.checked);
            this.fireEvent('click', this, this.checked);
            this.focus();
        }
    },
    focus: function () {
        this.el.focus();
    },
    blur: function () {
        this.el.blur();
    },
    onFocus: function () {
        var sf = this;
        /*
         * 添加对ie11浏览器的判断
         * 向平     2018-03-20
         */
        var isIE11 = (/Trident\/7\./).test(navigator.userAgent);
        if (!sf.hasFocus) {
            sf.hasFocus = true;
            sf.el.addClass(sf.focusCss);
            sf.fireEvent('focus', sf);
        }
        /*
         * 解决复选框在火狐浏览器下和ie11下需要点击两下才能选择的问题
         * 2017-12-19
         * 向平
         */
        if (Ext.isGecko2 || (Ext.isGecko && !isIE11) || Ext.isGecko3) {
            sf.onClick();
        }
    },
    onBlur: function () {
        var sf = this;
        if (sf.hasFocus) {
            sf.hasFocus = false;
            sf.el.removeClass(sf.focusCss);
            sf.fireEvent('blur', sf);
        }
    },
    setValue: function (v, silent) {
        if (typeof(v) === 'boolean') {
            this.checked = v ? true : false;
        } else {
            this.checked = ('' + v == '' + this.checkedvalue)
// this.checked = v === this.checkedvalue ? true : false;
        }
        this.initStatus();
        var value = this.checked == true ? this.checkedvalue : this.uncheckedvalue;
        $L.CheckBox.superclass.setValue.call(this, value, silent);
    },
    getValue: function () {
        var v = this.value;
        v = (v === null || v === undefined ? '' : v);
        return v;
    },
// setReadOnly:function(b){
// if(typeof(b)==='boolean'){
// this.readonly=b?true:false;
// this.initStatus();
// }
// },
    initStatus: function () {
        this.el.removeClass(this.checkedCss);
        this.el.removeClass(this.uncheckedCss);
        this.el.removeClass(this.readonyCheckedCss);
        this.el.removeClass(this.readonlyUncheckedCss);
        if (this.readonly) {
            this.el.addClass(this.checked ? this.readonyCheckedCss : this.readonlyUncheckedCss);
        } else {
            this.el.addClass(this.checked ? this.checkedCss : this.uncheckedCss);
        }
    },
    clearValue: function () {
        this.setValue(this.uncheckedvalue, true);
    }
});
/**
 * @class Leaf.CheckButton
 * @extends Leaf.Component
 *          <p>
 *          可选组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.CheckButton = Ext.extend($L.Component, {
    checkedCss: 'item-ckb-c',
    uncheckedCss: 'item-ckb-u',
    readonyCheckedCss: 'item-ckb-readonly-c',
    readonlyUncheckedCss: 'item-ckb-readonly-u',
    constructor: function (config) {
        config.checked = config.checked || false;
        config.readonly = config.readonly || false;
        $L.CheckButton.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        this.checkedvalue = 'Y';
        this.uncheckedvalue = 'N';
        $L.CheckButton.superclass.initComponent.call(this, config);
        this.wrap = Ext.get(this.id);
    },
    processListener: function (ou) {
        this.wrap[ou]('click', this.onClick, this);
    },
    initEvents: function () {
        $L.CheckButton.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event click 鼠标点击事件.
             * @param {Leaf.CheckButton}
             *            checkBox 可选组件.
             * @param {Boolean}
             *            checked 选择状态.
             */
            'click');
    },
    onKeyDown: function (e) {
        var keyCode = e.keyCode;
        if (keyCode == 32) {
            this.onClick.call(this, e);
            e.stopEvent();
        }
    },
    onMouseDown: function (e) {
        var sf = this;
        sf.hasFocus && e.stopEvent();
        sf.focus.defer(Ext.isIE ? 1 : 0, sf);
    },
    onClick: function (event) {
        if (!this.readonly) {
            this.checked = this.checked ? false : true;
            this.setValue(this.checked);
            this.fireEvent('click', this, this.checked);
        }
    },
    setValue: function (v, silent) {
        if (typeof(v) === 'boolean') {
            this.checked = v ? true : false;
        } else {
            this.checked = ('' + v == '' + this.checkedvalue)
        }
        this.initStatus();
        var value = this.checked == true ? this.checkedvalue : this.uncheckedvalue;
        $L.CheckButton.superclass.setValue.call(this, value, silent);
    },
    getValue: function () {
        var v = this.value;
        v = (v === null || v === undefined ? '' : v);
        return v;
    },
// setReadOnly:function(b){
// if(typeof(b)==='boolean'){
// this.readonly=b?true:false;
// this.initStatus();
// }
// },
    initStatus: function () {
        this.wrap.removeClass(this.checkedCss);
        this.wrap.removeClass(this.uncheckedCss);
        this.wrap.removeClass(this.readonyCheckedCss);
        this.wrap.removeClass(this.readonlyUncheckedCss);
        if (this.readonly) {
            this.wrap.addClass(this.checked ? this.readonyCheckedCss : this.readonlyUncheckedCss);
        } else {
            this.wrap.addClass(this.checked ? this.checkedCss : this.uncheckedCss);
        }
    },
    clearValue: function () {
        this.setValue(this.uncheckedvalue, true);
    }
});
/**
 * @class Leaf.Radio
 * @extends Leaf.Component
 *          <p>
 *          单选框组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.Radio = Ext.extend($L.Component, {
    ccs: 'item-radio-img-c',
    ucs: 'item-radio-img-u',
    rcc: 'item-radio-img-readonly-c',
    ruc: 'item-radio-img-readonly-u',
// optionCss:'item-radio-option',
    imgCss: 'item-radio-img',
    valueField: 'value',
    constructor: function (config) {
        config.checked = config.checked || false;
        config.readonly = config.readonly || false;
        $L.Radio.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        $L.Radio.superclass.initComponent.call(this, config);
        this.wrap = Ext.get(this.id);
        this.nodes = Ext.DomQuery.select('.item-radio-option', this.wrap.dom);
        this.initStatus();
// this.select(this.selectIndex);
    },
    processListener: function (ou) {
        $L.Radio.superclass.processListener.call(this, ou);
        this.wrap[ou]('click', this.onClick, this);
        this.wrap[ou]("keydown", this.onKeyDown, this);
        this.wrap[ou]('focus', this.onFocus, this);
        this.wrap[ou]('blur', this.onBlur, this);
    },
    clearInvalid: function () {
// this.invalidMsg = null;
        this.wrap.removeClass('item-invalid');
    },
    markInvalid: function (msg) {
        this.wrap.addClass('item-invalid');
    },
    focus: function () {
        this.wrap.focus();
    },
    blur: function () {
        this.wrap.blur();
    },
    onFocus: function () {
        this.fireEvent('focus', this);
    },
    onBlur: function () {
        this.fireEvent('blur', this);
    },
    onKeyDown: function (e) {
        var sf = this, keyCode = e.keyCode,
            options = sf.options,
            valueField = sf.valueField;
        sf.fireEvent('keydown', sf, e);
        if (keyCode == 13) {
            (function () {
                sf.fireEvent('enterdown', sf, e);
            }).defer(5);
        } else {
            var i = options.indexOf(sf.getValueItem());
            if (keyCode == 40 || keyCode == 39) {
                ++i < options.length && sf.setValue(options[i][valueField]);
                e.stopEvent();
            } else if (keyCode == 38 || keyCode == 37) {
                --i >= 0 && sf.setValue(options[i][valueField]);
                e.stopEvent();
            }
        }
    },
    initEvents: function () {
        $L.Radio.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event click 点击事件.
             * @param {Leaf.Tree}
             *            Radio对象
             * @param {Object}
             *            当前选中的值
             */
            'click',
            /**
             * @event keydown 键盘事件.
             * @param {Leaf.Tree}
             *            Radio对象
             * @param {Event}
             *            键盘事件对象
             */
            'keydown',
            /**
             * @event enterdown 回车事件.
             * @param {Leaf.Tree}
             *            Radio对象
             * @param {Event}
             *            键盘事件对象
             */
            'enterdown');
    },
    setValue: function (value, silent) {
        if (value == '')return;
        $L.Radio.superclass.setValue.call(this, value, silent);
        this.initStatus();
        this.focus();
    },
    getItem: function () {
        var item = this.getValueItem();
        if (item != null) {
            item = new $L.Record(item);
        }
        return item;
    },
    getValueItem: function () {
        var v = this.getValue();
        var l = this.options.length;
        var r = null;
        for (var i = 0; i < l; i++) {
            var o = this.options[i];
            if (o[this.valueField] == v) {
                r = o;
                break;
            }
        }
        return r;
    },
    select: function (i) {
        var v = this.getItemValue(i);
        if (v) {
            this.setValue(v);
        }
    },
    getValue: function () {
        var v = this.value;
        v = (v === null || v === undefined ? '' : v);
        return v;
    },
// setReadOnly:function(b){
// if(typeof(b)==='boolean'){
// this.readonly=b?true:false;
// this.initStatus();
// }
// },
    initStatus: function () {
        var l = this.nodes.length;
        for (var i = 0; i < l; i++) {
            var node = Ext.fly(this.nodes[i]).child('.' + this.imgCss);
            node.removeClass(this.ccs);
            node.removeClass(this.ucs);
            node.removeClass(this.rcc);
            node.removeClass(this.ruc);
            var value = Ext.fly(this.nodes[i]).getAttributeNS("", "itemvalue");
            // if((i==0 && !this.value) || value === this.value){
            if (value == this.value) {
                this.readonly ? node.addClass(this.rcc) : node.addClass(this.ccs);
            } else {
                this.readonly ? node.addClass(this.ruc) : node.addClass(this.ucs);
            }
        }
    },
    getItemValue: function (i) {
        var node = Ext.fly(this.nodes[i]);
        if (!node)return null;
        var v = node.getAttributeNS("", "itemvalue");
        return v;
    },
    onClick: function (e) {
        if (!this.readonly) {
            var l = this.nodes.length;
            for (var i = 0; i < l; i++) {
                var node = Ext.fly(this.nodes[i]);
                if (node.contains(e.target)) {
                    var v = node.getAttributeNS("", "itemvalue");
                    this.setValue(v);
                    this.fireEvent('click', this, v);
                    break;
                }
            }

        }
    }
});
/**
 * @class Leaf.TextField
 * @extends Leaf.Field
 *          <p>
 *          文本输入组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.TextField = Ext.extend($L.Field, {
    initComponent: function (config) {
        $L.TextField.superclass.initComponent.call(this, config);
        var sf = this,
            restrict = sf.restrict,
            typecase = sf.typecase;
        if (restrict) {
            sf.restrict = restrict.replace(/^\[|\]$/mg, '');
        }
        typecase &&
        sf.el.setStyle('text-transform', typecase + 'case');
    },
    isCapsLock: function (e) {
        var keyCode = e.getKey(),
            isShift = e.shiftKey;
        if (((keyCode >= 65 && keyCode <= 90) && !isShift) || ((keyCode >= 97 && keyCode <= 122) && isShift)) {
            if (this.dcl != true)
                $L.showWarningMessage(_lang['textfield.warn'], _lang['textfield.warn.capslock']);
            this.dcl = true;
        } else {
            this.dcl = false;
        }
    },
    onKeyPress: function (e) {
        var sf = this, k = e.getCharCode(),
            restrict = sf.restrict,
            restrictinfo = sf.restrictinfo;
        if ((Ext.isGecko || Ext.isOpera) && (e.isSpecialKey() || k == 8 || k == 46)) {// BACKSPACE
            // or
            // DELETE
            return;
        }
        // restrict匹配的是输入的字符串，不是每次键盘按下的字符
        // if (restrict && !new RegExp('[' + restrict + ']').test(String.fromCharCode(k))) {
        //     if (restrictinfo) $L.ToolTip.show(sf.id, restrictinfo);
        //     e.stopEvent();
        //     return;
        // }
        $L.TextField.superclass.onKeyPress.call(sf, e);
        if (sf.detectCapsLock) sf.isCapsLock(e);
    },
    processValue: function (v) {
        var sf = this,
            restrict = sf.restrict,
            restrictinfo = sf.restrictinfo;
        if (restrict) {
            if (restrictinfo && !new RegExp(restrict).test(v))
                $L.ToolTip.show(sf.id, restrictinfo);
        }
        return v;
    }
})
/**
 * @class Leaf.NumberField
 * @extends Leaf.TextField
 *          <p>
 *          数字输入组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.NumberField = Ext.extend($L.TextField, {
    allowdecimals: true,
    allownegative: true,
    allowformat: true,
    baseChars: "0123456789",
    decimalSeparator: ".",
    decimalprecision: 2,
    constructor: function (config) {
        $L.NumberField.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        var sf = this;
        $L.NumberField.superclass.initComponent.call(sf, config);
        sf.max = Ext.isEmpty(config.max) ? Number.MAX_VALUE : Number(config.max);
        sf.min = Ext.isEmpty(config.min) ? -Number.MAX_VALUE : Number(config.min);
        sf.restrict = sf.baseChars + '';
        sf.restrictinfo = _lang['numberfield.only'];
        if (sf.allowdecimals) {
            sf.restrict += sf.decimalSeparator;
        }
        if (sf.allownegative) {
            sf.restrict += "-";
        }
    },
// initEvents : function(){
// $L.NumberField.superclass.initEvents.call(this);
// },
    onBlur: function (e) {
        $L.ToolTip.hide();
        $L.NumberField.superclass.onBlur.call(this, e);
    },
    formatValue: function (v) {
        var sf = this, rv = $L.parseScientific(sf.fixPrecision(sf.parseValue(v)));
        if (sf.allowformat) rv = $L.formatNumber(rv);
        return $L.NumberField.superclass.formatValue.call(sf, rv);
    },
    processMaxLength: function (rv) {
        var s = $L.parseScientific(rv).split('.'), isNegative = false;
        if (s[0].search(/-/) != -1) isNegative = true;
        return (isNegative ? '-' : '') + $L.NumberField.superclass.processMaxLength.call(this, s[0].replace(/[-,]/g, '')) + (s[1] ? '.' + s[1] : '');
    },
    initMaxLength: function (maxlength) {
        if (maxlength && !this.allowdecimals)
            this.el.dom.maxLength = maxlength;
    },
    processValue: function (v) {
        var sf = this, info;
        v = sf.parseValue(v);
        if (v > sf.max) {
            v = sf.max;
            info = _lang['numberfield.max'] + v;
        } else if (v < sf.min) {
            v = sf.min
            info = _lang['numberfield.min'] + v;
        }
        if (info) $L.ToolTip.show(sf.id, info);
        return v;
    },
    onFocus: function (e) {
        var sf = this;
        if (!sf.readonly && sf.allowformat) {
            sf.setRawValue($L.removeNumberFormat(sf.getRawValue()));
        }
        $L.NumberField.superclass.onFocus.call(sf, e);
    },
    parseValue: function (value) {
        var sf = this;
        value = String(value);
        if (value.indexOf(",") != -1) value = value.replace(/,/g, "");
        if (!sf.allownegative) value = value.replace('-', '');
        if (!sf.allowdecimals) value = value.indexOf(".") == -1 ? value : value.substring(0, value.indexOf("."));
        value = parseFloat(sf.fixPrecision(value.replace(sf.decimalSeparator, ".")));
        return isNaN(value) ? '' : value;
    },
    fixPrecision: function (value) {
        var nan = isNaN(value);
        if (!this.allowdecimals || this.decimalprecision == -1 || nan || !value) {
            return nan ? '' : value;
        }
        var vs = parseFloat(value).toFixed(this.decimalprecision);
        if (this.allowpad == false) vs = String(parseFloat(vs))
        return vs;
    }
})
/**
 * @class Leaf.Spinner
 * @extends Leaf.TextField
 *          <p>
 *          微调范围输入组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.Spinner = Ext.extend($L.NumberField, {
// constructor: function(config) {
// $L.Spinner.superclass.constructor.call(this, config);
// },
    initComponent: function (config) {
        var sf = this;
        $L.Spinner.superclass.initComponent.call(sf, config);
        var decimal = String(sf.step = Number(config.step || 1)).split('.')[1];
        sf.decimalprecision = decimal ? decimal.length : 0;
        sf.btn = sf.wrap.child('div.item-spinner-btn');
        $L.onReady(function () {
            sf.setTriggerBtnPosition();
        });
    },
    processListener: function (ou) {
        var sf = this;
        $L.Spinner.superclass.processListener.call(sf, ou);
        sf.btn[ou]('mouseover', sf.onBtnMouseOver, sf)
            [ou]('mouseout', sf.onBtnMouseOut, sf)
            [ou]('mousedown', sf.onBtnMouseDown, sf)
            [ou]('mouseup', sf.onBtnMouseUp, sf);
    },
    onBtnMouseOver: function (e, t) {
        if (this.readonly)return;
        Ext.fly(t).addClass('spinner-over');
    },
    onBtnMouseOut: function (e, t) {
        if (this.readonly)return;
        Ext.fly(t).removeClass('spinner-over');
        this.onBtnMouseUp(e, t);
    },
    onBtnMouseDown: function (e, t) {
        var target = Ext.fly(t);
        if (this.readonly || !target.parent('span'))return;
        var isPlus = !!target.addClass('spinner-select').parent('.item-spinner-plus'),
            sf = this;
        sf.goStep(isPlus, function () {
            sf.intervalId = setInterval(function () {
                clearInterval(sf.intervalId);
                sf.intervalId = setInterval(function () {
                    sf.goStep(isPlus, null, function () {
                        clearInterval(sf.intervalId);
                    });
                }, 40);
            }, 500);
        });
    },
    onBtnMouseUp: function (e, t) {
        var sf = this;
        if (sf.readonly)return;
        Ext.fly(t).removeClass('spinner-select');
        if (sf.intervalId) {
            clearInterval(sf.intervalId);
            sf.setValue(sf.getRawValue());
            delete sf.intervalId;
        }
    },
    /**
     * 递增
     */
    plus: function () {
        this.goStep(true, function (n) {
            this.setValue(n);
        });
    },
    /**
     * 递减
     */
    minus: function () {
        this.goStep(false, function (n) {
            this.setValue(n);
        });
    },
    goStep: function (isPlus, callback, callback2) {
        if (this.readonly)return;
        var sf = this,
            step = sf.step,
            min = sf.min,
            max = sf.max,
            raw = sf.getRawValue(),
            n = raw ? Number(raw)
                + (isPlus ? step : -step) : (0 < min ? min : (0 > max ? max : 0)),
            mod = sf.toFixed(sf.toFixed(n - min) % step);
        n = sf.toFixed(n - (mod == step ? 0 : mod));
        if (n <= max && n >= min) {
            sf.setRawValue(sf.formatValue(n));
            if (callback) callback.call(sf, n);
        } else {
            if (callback2) callback2.call(sf, n)
        }
    },
    toFixed: function (n) {
        return Number(n.toFixed(this.decimalprecision));
    },
    setHeight: function (h) {
        var sf = this;
        if (this.height == h) return;
        $L.Spinner.superclass.setHeight.call(sf, h);
        sf.setTriggerBtnPosition();
    },
    setTriggerBtnPosition: function () {
        this.btn.setStyle({'padding-top': Math.round((this.btn.getHeight() - 20) / 2) + 'px'});
    }
});
/**
 * @class Leaf.TriggerField
 * @extends Leaf.TextField
 *          <p>
 *          触发类组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.TriggerField = Ext.extend($L.TextField, {
    constructor: function (config) {
        $L.TriggerField.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        $L.TriggerField.superclass.initComponent.call(this, config);
        this.trigger = this.wrap.child('div[atype=triggerfield.trigger]');
        this.initPopup();
    },
    initPopup: function () {
        if (this.initpopuped == true) return;
        this.popup = this.wrap.child('div[atype=triggerfield.popup]');
        this.popupContent = this.popup.child('div.item-popup-content');
        Ext.isIE && new Ext.Template('<div class="item-ie-shadow"></div>').insertFirst(this.popup, {});
// this.shadow = this.wrap.child('div[atype=triggerfield.shadow]');
        Ext.getBody().insertFirst(this.popup);
// Ext.getBody().insertFirst(this.shadow);
        this.initpopuped = true
    },
    initEvents: function () {
        $L.TriggerField.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event expand 展开事件.
             * @param {Leaf.TriggerField}
             *            triggerField 所有可展开控件对象.
             */
            'expand',
            /**
             * @event collapse 收缩事件.
             * @param {Leaf.TriggerField}
             *            triggerField 所有可展开控件对象.
             */
            'collapse'
        );
    },
    processListener: function (ou) {
        $L.TriggerField.superclass.processListener.call(this, ou);
        this.trigger[ou]('click', this.onTriggerClick, this, {preventDefault: true})
        this.popup[ou]('click', this.onPopupClick, this, {stopPropagation: true})
    },
    /**
     * 判断当时弹出面板是否展开
     *
     * @return {Boolean} isexpanded 是否展开
     */
    isExpanded: function () {
        var xy = this.popup.getXY();
        return !(xy[0] < -500 || xy[1] < -500)
    },
    setWidth: function (w) {
        this.wrap.setStyle("width", (w + 3) + "px");
        // this.el.setStyle("width",(w-20)+"px");
    },
    onPopupClick: function () {
        this.hasExpanded = true;
        this.el.focus();
    },
    onFocus: function () {
        $L.TriggerField.superclass.onFocus.call(this);
        if (!this.readonly && !this.isExpanded() && !this.hasExpanded) this.expand();
        this.hasExpanded = false;
    },
    onBlur: function (e) {
// if(this.isEventFromComponent(e.target)) return;
// if(!this.isExpanded()){
        this.hasFocus = false;
        this.wrap.removeClass(this.focusCss);
        this.fireEvent("blur", this);
// }
    },
    onKeyDown: function (e) {
        switch (e.keyCode) {
            case 9:
            case 13:
            case 27:
                if (this.isExpanded()) this.collapse();
                break;
            case 40:
                if (!this.isExpanded() && !this.readonly) this.expand();
        }
        $L.TriggerField.superclass.onKeyDown.call(this, e);
    },
    isEventFromComponent: function (el) {
        return $L.TriggerField.superclass.isEventFromComponent.call(this, el) || this.popup.dom == el || this.popup.contains(el);
    },
    destroy: function () {
        if (this.isExpanded()) {
            this.collapse();
        }
// this.shadow.remove();
// this.popup.remove();
        $L.TriggerField.superclass.destroy.call(this);
        delete this.popup;
        delete this.popupContent;
// delete this.shadow;
    },
    triggerBlur: function (e, t) {
        if (!this.isEventFromComponent(t)) {
            if (this.isExpanded()) {
                this.collapse();
            }
        }
    },
    setVisible: function (v) {
        $L.TriggerField.superclass.setVisible.call(this, v);
        if (v == false && this.isExpanded()) {
            this.collapse();
        }
    },
    /**
     * 折叠弹出面板
     */
    collapse: function () {
        Ext.get(document.documentElement).un("mousedown", this.triggerBlur, this);
        this.popup.moveTo(-1000, -1000);
        this.popup.removeClass('animated zoomInDownPopup');
        this.popup.addClass('animated zoomOut');
// this.shadow.moveTo(-1000,-1000);
        this.fireEvent("collapse", this);
    },
    /**
     * 展开弹出面板
     */
    expand: function () {
// Ext.get(document.documentElement).on("mousedown", this.triggerBlur, this,
// {delay: 10});
        // 对于某些行上的cb，如果是二级关联的情况下，会expand多次，导致多次绑定事件
        Ext.get(document.documentElement).un("mousedown", this.triggerBlur, this);
        Ext.get(document.documentElement).on("mousedown", this.triggerBlur, this);
        this.syncPopup();
        this.fireEvent("expand", this);
    },
    syncPopup: function () {
        var sf = this,
            wrap = sf.wrap,
            popup = sf.popup,
            scroll = Ext.getBody().getScroll(),
            sl = scroll.left,
            st = scroll.top,
            xy = wrap.getXY(),
            _x = xy[0] - sl,
            _y = xy[1] - st,
            W = popup.getWidth(),
            H = popup.getHeight(),
            PH = wrap.getHeight(),
            PW = wrap.getWidth(),
            BH = $L.getViewportHeight() - 3,
            BW = $L.getViewportWidth() - 3,
            x = ((_x + W) > BW ? ((BW - W) < 0 ? _x : (BW - W)) : _x) + sl;
//			y=((_y+PH+H)>BH?((_y-H)<0?(_y+PH):(_y-H)):(_y+PH))+st+2;
        y = _y + PH + st + 2;
        popup.removeClass('animated zoomOut');
        /*
         * 当下拉选展开是出现在控件的上面时，添加由下到上的动画效果，否则为由上到下
         * 向平  2018-01-22
         */
        if (!Ext.isIE8) {
            if (BH - _y > PH / 2 + H) {
                popup.addClass('animated zoomInDownPopup');
                setTimeout(function () {
                    popup.removeClass('animated zoomInDownPopup');
                }, 600);
                popup.moveTo(x, y);
            } else {
                y = _y - H / 10 + st;
                popup.addClass('animated zoomInUpPopup');
                setTimeout(function () {
                    popup.removeClass('animated zoomInUpPopup');
                }, 600);
                popup.moveTo(x, y);
            }
        } else {
            popup.moveTo(x, y);
        }

// sf.shadow.moveTo(x+3,y+3);
    },
    onTriggerClick: function () {
        if (this.readonly) return;
        if (this.isExpanded()) {
            this.collapse();
        } else {
            this.expand();
            this.el.focus();
        }
    }
});
/**
 * @class Leaf.ComboBox
 * @extends Leaf.TriggerField
 *          <p>
 *          Combo组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.ComboBox = Ext.extend($L.TriggerField, {
    maxHeight: 262,
    blankOption: true,
    rendered: false,
    selectedClass: 'item-comboBox-selected',
    // currentNodeClass:'item-comboBox-current',
// constructor : function(config) {
// $L.ComboBox.superclass.constructor.call(this, config);
// },
    initComponent: function (config) {
        $L.ComboBox.superclass.initComponent.call(this, config);
        var opt = config.options;
        if (opt) {
            this.setOptions(opt);
        } else {
            this.clearOptions();
        }
    },
    initEvents: function () {
        $L.ComboBox.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event select 选择事件.
             * @param {Leaf.Combobox}
             *            combo combo对象.
             * @param {Object}
             *            value valueField的值.
             * @param {String}
             *            display displayField的值.
             * @param {Leaf.Record}
             *            record 选中的Record对象
             */
            'select');
        var self = this;
        Ext.get(document.body)['on']("mousewheel", function(){
            self.collapse();
        }, self);
    },
// onTriggerClick : function() {
// this.doQuery();
// $L.ComboBox.superclass.onTriggerClick.call(this);
// },
    onBlur: function (e) {
        if (this.hasFocus) {
            $L.ComboBox.superclass.onBlur.call(this, e);
            if (!this.readonly/* !this.isExpanded() */) {
                var raw = this.getRawValue();
                if (raw != this.value) {
                    if (this.fetchrecord === false) {
                        this.setValue(raw)
                    } else {
                        var record = this.getRecordByDisplay(raw);
                        this.setValue(record && this.getRenderText(record) || '');
                    }
                }
            }
        }
    },
    getRecordByDisplay: function (name) {
        if (!this.optionDataSet)return null;
        var record = null;
        Ext.each(this.optionDataSet.getAll(), function (r) {
            if (this.getRenderText(r) == name) {
                record = r;
                return false;
            }
        }, this);
        return record;
    },
    /**
     * 展开下拉菜单.
     */
    expand: function () {
        if (!this.optionDataSet)return;
        if (this.rendered === false) this.doQuery();
        $L.ComboBox.superclass.expand.call(this);
// !this.isExpanded() && $L.ComboBox.superclass.expand.call(this);
        var v = this.getValue();
        this.currentIndex = this.getIndex(v);
// if(!this.currentIndex) return;
        if (!Ext.isEmpty(v)) {
            this.selectItem(this.currentIndex, true);
        }
        if (!Ext.isIE8) {
            $jq('div.item-comboBox-view').getNiceScroll().resize();
        }
    },
    onKeyDown: function (e) {
        if (this.readonly)return;
        var current = Ext.isEmpty(this.selectedIndex) ? -1 : this.selectedIndex,
            keyCode = e.keyCode;
        if (keyCode == 40 || keyCode == 38) {
            this.inKeyMode = true;
            if (keyCode == 38) {
                current--;
                if (current >= 0) {
                    this.selectItem(current, true)
                }
            } else if (keyCode == 40) {
                current++;
                if (current < this.view.dom.childNodes.length) {
                    this.selectItem(current, true)
                }
            }
        } else if (this.inKeyMode && keyCode == 13) {
            this.inKeyMode = false;
            var cls = this.selectedClass;
            Ext.each(this.view.dom.childNodes, function (t) {
                if (Ext.fly(t).hasClass(cls)) {
                    this.onSelect(t)
                    return false;
                }
            }, this);
            this.collapse();
            return;
        }
        $L.ComboBox.superclass.onKeyDown.call(this, e);
    },
    onKeyUp: function (e) {
        if (this.readonly || !this.editable)return;
        var c = e.keyCode;
        if (!e.isSpecialKey() || c == 8 || c == 46) {
// if(this.timeoutId)
// clearTimeout(this.timeoutId)
// this.timeoutId = function(){
            this.doQuery(this.getRawValue());
            if (!this.isExpanded())
                $L.ComboBox.superclass.expand.call(this);
            else
                this.syncPopup();
            this.rendered = false;
// delete this.timeoutId;
// }.defer(300,this);
        }
        $L.ComboBox.superclass.onKeyUp.call(this, e);
    },
    /**
     * 收起下拉菜单.
     */
    collapse: function () {
        $L.ComboBox.superclass.collapse.call(this);
        if (!Ext.isEmpty(this.currentIndex))
            Ext.fly(this.getNode(this.currentIndex)).removeClass(this.selectedClass);
// this.doQuery();
    },
    clearOptions: function () {
        this.processDataSet('un');
        this.optionDataSet = null;
    },
    setOptions: function (name) {
        var ds = typeof(name) === 'string' ? $le(name) : name;
        if (this.optionDataSet != ds) {
            this.processDataSet('un');
            this.optionDataSet = ds;
            this.processDataSet('on');
            this.rendered = false;
            if (!Ext.isEmpty(this.value)) this.setValue(this.value, true)
        }
    },
    processDataSet: function (ou) {
        var ds = this.optionDataSet,
            loadFn = this.onDataSetLoad;
        if (ds) {
            ds[ou]('load', loadFn, this);
            ds[ou]('query', loadFn, this);
            ds[ou]('add', loadFn, this);
            ds[ou]('update', loadFn, this);
            ds[ou]('remove', loadFn, this);
            ds[ou]('clear', loadFn, this);
            ds[ou]('reject', loadFn, this);
        }
    },
    onDataSetLoad: function () {
        this.rendered = false;
        if (this.isExpanded()) {
            this.expand();
        }
    },
    onRender: function () {
        if (!this.view) {
            this.view = this.popupContent.update('<ul></ul>').child('ul')
                .on('click', this.onViewClick, this)
                .on('mousemove', this.onViewMove, this);
        }

        if (this.optionDataSet) {
            this.initList();
            this.rendered = true;
        }
        this.correctViewSize();
    },
    correctViewSize: function () {
        var widthArray = [],
            mw = this.wrap.getWidth();
        var sf = this,
            wrap = sf.wrap,
            popup = sf.popup,
            scroll = Ext.getBody().getScroll(),
            st = scroll.top,
            xy = wrap.getXY(),
            _y = xy[1] - st,
            H = popup.getHeight(),
            PH = wrap.getHeight(),
            BH = $L.getViewportHeight() - 3;
        Ext.each(this.view.dom.childNodes, function (li) {
            mw = Math.max(mw, $L.TextMetrics.measure(li, li.innerHTML).width) || mw;
        });
        var lh = Math.max(27, Math.min(this.popupContent.child('ul').getHeight() + 2, this.maxHeight));
        this.popup.setWidth(mw).setHeight(lh);
    },
    onViewClick: function (e, t) {
        if (t.tagName != 'LI') {
            return;
        }
        this.onSelect(t);
        this.collapse();
    },
// onViewOver:function(e,t){
// this.inKeyMode = false;
// },
    onViewMove: function (e, t) {
// if(this.inKeyMode){ // prevent key nav and mouse over conflicts
// return;
// }
        this.selectItem(t.tabIndex);
    },
    onSelect: function (target) {
        var index = target.tabIndex;
        if (index == -1)return;
        var sf = this, value = null, display = '', record = null;
        if (sf.blankoption) {
            index--;
        }
        if (index != -1) {
            record = sf.optionDataSet.getAt(index);
            value = record.get(sf.valuefield);
            display = sf.getRenderText(record);// record.get(this.displayfield);
        }
        sf.setValue(display, null, record);
        sf.fireEvent('select', sf, value, display, record);

    },
// initQuery: function(){//事件定义中调用
// this.doQuery();
// },
    doQuery: function (q) {
// if(q === undefined || q === null){
// q = '';
// }
// if(forceAll){
// this.store.clearFilter();
// }else{
// this.store.filter(this.displayField, q);
// }
        var ds = this.optionDataSet;
        if (ds)
            if (Ext.isEmpty(q)) {
                ds.clearFilter();
            } else {
                var reg = new RegExp(q.replace(/[+?*.^$\[\](){}\\|]/g, function (v) {
                        return '\\' + v;
                    }), 'i'),
                    field = this.displayfield;
                ds.filter(function (r) {
                    return reg.test(r.get(field));
                }, this);
            }
        // 值过滤先不添加
        this.onRender();
    },
    initList: function () {
// this.refresh();
        this.currentIndex = this.selectedIndex = null;
        var ds = this.optionDataSet, v = this.view;
// this.litp=new Ext.Template('<li
// tabIndex="{index}">{'+this.displayfield+'}&#160;</li>');
        if (ds.loading == true) {
            v.update('<li tabIndex="-1">' + _lang['combobox.loading'] + '</li>');
        } else {
            var sb = [], n = 0;
            if (this.blankoption) {
                sb.push('<li tabIndex="0">&nbsp;</li>');
                n = 1;
            }
            Ext.each(ds.getAll(), function (d, i) {
// var d = Ext.apply(datas[i].data, {index:i})
// var rder = $L.getRenderer(this.renderer);
// var text = this.getRenderText(datas[i]);
                sb.push('<li');
                if (this.tipfield && d.get(this.tipfield)) {
                    sb.push(' title="', d.get(this.tipfield), '"')
                }
                sb.push(' tabIndex="', i + n, '">', this.getRenderText(d), '</li>');
            }, this);
// if(sb.length){
            v.update(sb.join(''));
// }
        }
    },
    getRenderText: function (record) {
        var rder = $L.getRenderer(this.displayrenderer);
        if (rder) {
            return rder(this, record);
        } else {
            return record.get(this.displayfield);
        }
    },
// refresh:function(){
// this.view.update('');
// this.selectedIndex = null;
// },
    selectItem: function (index, focus) {
        if (Ext.isEmpty(index) || index == -1) {
            return;
        }
        var node = this.getNode(index),
            sindex = this.selectedIndex,
            cls = this.selectedClass;
        if (node && node.tabIndex != sindex) {
            if (!Ext.isEmpty(sindex)) {
                Ext.fly(this.getNode(sindex)).removeClass(cls);
            }
            this.selectedIndex = node.tabIndex;
            if (focus) this.focusRow(this.selectedIndex);
            Ext.fly(node).addClass(cls);
        }
    },
    focusRow: function (row) {
        var r = 27,
            ub = this.popupContent,
            stop = ub.getScroll().top,
            h = ub.getHeight(),
            sh = ub.dom.scrollWidth > ub.dom.clientWidth ? 17 : 1;
        if (row * r < stop) {
            ub.scrollTo('top', row * r - 1)
        } else if ((row + 1) * r > (stop + h - sh)) {// this.ub.dom.scrollHeight
            ub.scrollTo('top', (row + 1) * r - h + sh);
        }
    },
    getNode: function (index) {
        return this.view.query('li[tabindex!=-1]')[index];
    },
    destroy: function () {
        if (this.view) {
            this.view.un('click', this.onViewClick, this)
            // .un('mouseover',this.onViewOver,this)
                .un('mousemove', this.onViewMove, this);
        }
        this.processDataSet('un');
        $L.ComboBox.superclass.destroy.call(this);
        delete this.view;
    },
// getText : function() {
// return this.text;
// },
// processValue : function(rv){
// var r = this.optionDataSet == null ? null :
// this.optionDataSet.find(this.displayfield, rv);
// if(r != null){
// return r.get(this.valuefield);
// }else{
// return this.value;
// }
// },
// formatValue : function(){
// var v = this.getValue();
// var r = this.optionDataSet == null ? null :
// this.optionDataSet.find(this.valuefield, v);
// this.text = '';
// if(r != null){
// this.text = r.get(this.displayfield);
// }else{
// // this.text = v;
// }
// return this.text;
// },
    setValue: function (v, silent, vr) {
        $L.ComboBox.superclass.setValue.call(this, v, silent);
        var r = this.record;
        if (r && !silent) {
            var field = r.getMeta().getField(this.binder.name);
            if (field) {
                var raw = this.getRawValue(),
                    record = vr || this.getRecordByDisplay(raw);
                Ext.each(field.get('mapping'), function (map) {
                    var vl = record ? record.get(map.from) : (this.fetchrecord === false ? raw : '');
// var vl = record ? (record.get(map.from)||'') : '';
// if(vl!=''){
                    if (!Ext.isEmpty(vl, true)) {
                        // 避免render的时候发生update事件
// if(silent){
// r.data[map.to] = vl;
// }else{
                        r.set(map.to, vl);
// }
                    } else {
                        delete r.data[map.to];
                    }
                }, this);
            }
        }
    },
    getIndex: function (v) {
        var df = this.displayfield;
        return Ext.each(this.optionDataSet.getAll(), function (d) {
            if (d.data[df] == v) {
                return false;
            }
        });
    }
});
/**
 * @class Leaf.DateField
 * @extends Leaf.Component
 *          <p>
 *          日期组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.DateField = Ext.extend($L.Component, {
    bodyTpl: ['<TABLE cellspacing="0">',
        '<CAPTION class="item-dateField-caption">',
        '{preYearBtn}',
        '{nextYearBtn}',
        '{preMonthBtn}',
        '{nextMonthBtn}',
        '<SPAN>',
        '<SPAN atype="item-year-span" style="margin-right:5px;cursor:pointer"></SPAN>',
        '<SPAN atype="item-month-span" style="cursor:pointer"></SPAN>',
        '</SPAN>',
        '</CAPTION>',
        '<THEAD class="item-dateField-head">',
        '<TR>',
        '<TD>{sun}</TD>',
        '<TD>{mon}</TD>',
        '<TD>{tues}</TD>',
        '<TD>{wed}</TD>',
        '<TD>{thur}</TD>',
        '<TD>{fri}</TD>',
        '<TD>{sat}</TD>',
        '</TR>',
        '</THEAD>',
        '<TBODY>',
        '</TBODY>',
        '</TABLE>'],
    preMonthTpl: '<DIV class="item-dateField-pre" title="{preMonth}" onclick="$le(\'{id}\').preMonth()"></DIV>',
    nextMonthTpl: '<DIV class="item-dateField-next" title="{nextMonth}" onclick="$le(\'{id}\').nextMonth()"></DIV>',
    preYearTpl: '<DIV class="item-dateField-preYear" title="{preYear}" onclick="$le(\'{id}\').preYear()"></DIV>',
    nextYearTpl: '<DIV class="item-dateField-nextYear" title="{nextYear}" onclick="$le(\'{id}\').nextYear()"></DIV>',
    popupTpl: '<DIV class="item-popup" atype="date-popup" style="vertical-align: middle;background-color:#fff;visibility:hidden"></DIV>',
    initComponent: function (config) {
        $L.DateField.superclass.initComponent.call(this, config);
        if (this.height) this.rowHeight = (this.height - 18 * (Ext.isIE ? 3 : 2)) / 6;
        var data = {
            sun: _lang['datefield.sun'],
            mon: _lang['datefield.mon'],
            tues: _lang['datefield.tues'],
            wed: _lang['datefield.wed'],
            thur: _lang['datefield.thur'],
            fri: _lang['datefield.fri'],
            sat: _lang['datefield.sat']
        }
        if (this.enableyearbtn == "both" || this.enableyearbtn == "pre")
            data.preYearBtn = new Ext.Template(this.preYearTpl).apply({
                preYear: _lang['datefield.preYear'],
                id: this.id
            });
        if (this.enableyearbtn == "both" || this.enableyearbtn == "next")
            data.nextYearBtn = new Ext.Template(this.nextYearTpl).apply({
                nextYear: _lang['datefield.nextYear'],
                id: this.id
            });
        if (this.enablemonthbtn == "both" || this.enablemonthbtn == "pre")
            data.preMonthBtn = new Ext.Template(this.preMonthTpl).apply({
                preMonth: _lang['datefield.preMonth'],
                id: this.id
            });
        if (this.enablemonthbtn == "both" || this.enablemonthbtn == "next")
            data.nextMonthBtn = new Ext.Template(this.nextMonthTpl).apply({
                nextMonth: _lang['datefield.nextMonth'],
                id: this.id
            });
        this.body = new Ext.Template(this.bodyTpl).append(this.wrap.dom, data, true);
        this.yearSpan = this.body.child("span[atype=item-year-span]");
        this.monthSpan = this.body.child("span[atype=item-month-span]");
        this.popup = new Ext.Template(this.popupTpl).append(this.body.child('caption').dom, {}, true);
        // this.popup = new
        // Ext.Template(this.popupTpl).append(this.wrap.dom,true);
    },
    processListener: function (ou) {
        $L.DateField.superclass.processListener.call(this, ou);
        this.body[ou]('mousewheel', this.onMouseWheel, this);
        this.body[ou]("mouseover", this.onMouseOver, this);
        this.body[ou]("mouseout", this.onMouseOut, this);
        this.body[ou]("click", this.onSelect, this);
        this.yearSpan[ou]("click", this.onViewShow, this);
        this.monthSpan[ou]("click", this.onViewShow, this);
        // this.body[ou]("keydown",this.onKeyDown,this);
    },
    initEvents: function () {
        $L.DateField.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event select 选择事件.
             * @param {Leaf.DateField}
             *            dateField 日期组件.
             * @param {Date}
             *            date 选择的日期.
             */
            'select',
            /**
             * @event draw 绘制事件.
             * @param {Leaf.DateField}
             *            dateField 日期组件.
             */
            'draw');
    },
    destroy: function () {
        $L.DateField.superclass.destroy.call(this);
        delete this.preMonthBtn;
        delete this.nextMonthBtn;
        delete this.body;
    },
    onMouseWheel: function (e) {
        this[(e.getWheelDelta() > 0 ? 'pre' : 'next') + (e.ctrlKey ? 'Year' : 'Month')]();
        e.stopEvent();
    },
    onMouseOver: function (e, t) {
        this.out();
        if (((t = Ext.fly(t)).hasClass('item-day') || (t = t.parent('.item-day'))) && t.getAttributeNS("", '_date') != '0') {
            $L.DateField.superclass.onMouseOver.call(this, e);
            this.over(t);
        }
    },
    onMouseOut: function (e) {
        $L.DateField.superclass.onMouseOut.call(this, e);
        this.out();
    },
    over: function (t) {
        t = t || this.body.last().child('td.item-day')
        this.overTd = t;
        t.addClass('dateover');
    },
    out: function () {
        if (this.overTd) {
            this.overTd.removeClass('dateover');
            this.overTd = null;
        }
    },
    onSelect: function (e, t) {
        var sf = this, td = Ext.get(t), _date;
        if (td.parent('div[atype="date-popup"]')) {
            sf.onViewClick(e, td);
        } else {
            _date = td.getAttributeNS('', '_date');
            if (_date && _date != '0') {
                sf.fireEvent("select", e, t, sf, new Date(Number(_date)));
            }
        }
    },
    onSelectDay: function (o) {
        if (!o.hasClass('onSelect')) o.addClass('onSelect');
    },
    onViewShow: function (e, t) {
        var span = Ext.get(t);
        this.focusSpan = span;
        var head = this.body.child('thead'), xy = head.getXY();
        this.popup.moveTo(xy[0], xy[1]);
        this.popup.setWidth(head.getWidth());
        this.popup.setHeight(head.getHeight() + head.next().getHeight());
        if (span.getAttributeNS("", "atype") == "item-year-span")
            this.initView(this.year, 100, true);
        else
            this.initView(7, 60);
        Ext.get(document.documentElement).on("mousedown", this.viewBlur, this);
        this.popup.show();
    },
    onViewHide: function () {
        Ext.get(document.documentElement).un("mousedown", this.viewBlur, this);
        this.popup.hide();
    },
    viewBlur: function (e, t) {
        if (!this.popup.contains(t) && !(this.focusSpan.contains(t) || this.focusSpan.dom == t)) {
            this.onViewHide();
        }
    },
    onViewClick: function (e, t) {
        if (t.hasClass('item-day')) {
            if (this.focusSpan.getAttributeNS("", "atype") == "item-year-span")
                this.year = t.getAttributeNS("", '_data');
            else
                this.month = t.getAttributeNS("", '_data');
            this.year--;
            this.nextYear();
            this.onViewHide();
        }
    },
    /**
     * 当前月
     */
    nowMonth: function () {
        this.predraw(new Date());
    },
    /**
     * 上一月
     */
    preMonth: function () {
        this.predraw(new Date(this.year, this.month - 2, 1, this.hours, this.minutes, this.seconds));
    },
    /**
     * 下一月
     */
    nextMonth: function () {
        this.predraw(new Date(this.year, this.month, 1, this.hours, this.minutes, this.seconds));
    },
    /**
     * 上一年
     */
    preYear: function () {
        this.predraw(new Date(this.year - 1, this.month - 1, 1, this.hours, this.minutes, this.seconds));
    },
    /**
     * 下一年
     */
    nextYear: function () {
        this.predraw(new Date(this.year + 1, this.month - 1, 1, this.hours, this.minutes, this.seconds));
    },
    /**
     * 根据日期画日历
     *
     * @param {Date}
     *            date 当前日期
     */
    predraw: function (date, notFire) {
        if (!date || !date instanceof Date) date = new Date();
        this.date = date;
        this.hours = date.getHours();
        this.minutes = date.getMinutes();
        this.seconds = date.getSeconds();
        this.year = date.getFullYear();
        this.month = date.getMonth() + 1;
        this.draw(new Date(this.year, this.month - 1, 1, this.hours, this.minutes, this.seconds));
        if (!notFire) this.fireEvent("draw", this);
    },
    /**
     * 渲染日历
     */
    draw: function (date) {
        // 用来保存日期列表
        var arr = [], year = date.getFullYear(), month = date.getMonth() + 1, hour = date.getHours(), minute = date.getMinutes(), second = date.getSeconds();
        this.yearSpan.update(year + _lang['datefield.year']);
        this.monthSpan.update(month + _lang['datefield.month']);
        // 用当月第一天在一周中的日期值作为当月离第一天的天数,用上个月的最后天数补齐
        for (var i = 1, firstDay = new Date(year, month - 1, 1).getDay(), lastDay = new Date(year, month - 1, 0).getDate(); i <= firstDay; i++) {
            arr.push((this.enablebesidedays == "both" || this.enablebesidedays == "pre") ? new Date(year, month - 2, lastDay - firstDay + i, hour, minute, second) : null);
        }
        // 用当月最后一天在一个月中的日期值作为当月的天数
        for (var i = 1, monthDay = new Date(year, month, 0).getDate(); i <= monthDay; i++) {
            arr.push(new Date(year, month - 1, i, hour, minute, second));
        }
        // 用下个月的前几天补齐6行
        for (var i = 1, monthDay = new Date(year, month, 0).getDay(), besideDays = 43 - arr.length; i < besideDays; i++) {
            arr.push((this.enablebesidedays == "both" || this.enablebesidedays == "next") ? new Date(year, month, i, hour, minute, second) : null);
        }
        // 先清空内容再插入(ie的table不能用innerHTML)
        var body = this.body.dom.tBodies[0];
        while (body.firstChild) {
            Ext.fly(body.firstChild).remove();
        }
        // 插入日期
        var k = 0;
        while (arr.length) {
            // 每个星期插入一个tr
            var row = Ext.get(body.insertRow(-1));
            row.set({'r_index': k});
            if (k % 2 == 0) row.addClass('week-alt');
            if (this.rowHeight) row.setHeight(this.rowHeight);
            k++;
            // 每个星期有7天
            for (var i = 1; i <= 7; i++) {
                var d = arr.shift();
                if (Ext.isDefined(d)) {
                    var cell = Ext.get(row.dom.insertCell(-1));
                    var div = document.createElement("div");
                    cell.appendChild(div);
                    div = Ext.get(div);
                    if (d) {
                        div.set({'c_index': i - 1});
                        div.addClass(date.getMonth() == d.getMonth() ? "item-day" : "item-day item-day-besides");
                        /*
                         * if(((i-1)==0||(i-1)==6)&&(date.getMonth()==d.getMonth())){
                         * cell.set({'style':'color:#f15353'}); }
                         */
                        div.update(this.renderCell(cell, d, d.getDate(), month) || d.getDate());
                        if (div.disabled) {
                            div.set({'_date': '0'});
                            div.addClass("item-day-disabled");
                        } else {
                            div.set({'_date': ('' + d.getTime())});
                            if (this.format) div.set({'title': d.format(this.format)})
                        }
                        // 判断是否今日
                        if (this.isSame(d, new Date())) div.addClass("onToday");
                        // 判断是否选择日期
                        if (this.selectDay && this.isSame(d, this.selectDay)) this.onSelectDay(div);
                    } else div.update('&#160;');
                }
            }
        }
    },
    renderCell: function (cell, date, day, currentMonth) {
        if (this.dayrenderer)
            return $L.getRenderer(this.dayrenderer).call(this, cell, date, day, currentMonth);
    },
    /**
     * 判断是否同一日
     *
     * @param {Date}
     *            d1 日期1
     * @param {Date}
     *            d2 日期2
     * @return {Boolean} 是否同一天
     */
    isSame: function (d1, d2) {
        if (!d2.getFullYear || !d1.getFullYear)return false;
        return (d1.getFullYear() == d2.getFullYear() && d1.getMonth() == d2.getMonth() && d1.getDate() == d2.getDate());
    },
    initView: function (num, width, isYear) {
        var html = ["<table cellspacing='0' cellpadding='0' width='100%'><tr><td width='45%'></td><td width='10%'></td><td width='45%'></td></tr>"];
        for (var i = 0, rows = (isYear ? 5 : 6), year = num - rows, year2 = num; i < rows; i++) {
            html.push("<tr><td class='item-day' _data='" + year + "'>" + year + "</td><td></td><td class='item-day' _data='" + year2 + "'>" + year2 + "</td></tr>");
            year += 1;
            year2 += 1;
        }
        html.push("");
        if (isYear) {
            html.push("<tr><td><div class='item-dateField-pre' onclick='$le(\"" + this.id + "\").initView(" + (num - 10) + "," + width + ",true)'></div></td>");
            html.push("<td><div class='item-dateField-close' onclick='$le(\"" + this.id + "\").onViewHide()'></div></td>")
            html.push("<td><div class='item-dateField-next' onclick='$le(\"" + this.id + "\").initView(" + (num + 10) + "," + width + ",true)'></div></td></tr>");
        } else {
            html.push("<td colspan='3' align='center'><div class='item-dateField-close' onclick='$le(\"" + this.id + "\").onViewHide()'></div></td>")
        }
        html.push("</table>");
        this.popup.update(html.join(''));
    }
});
/**
 * @class Leaf.DatePicker
 * @extends Leaf.TriggerField
 *          <p>
 *          DatePicker组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.DatePicker = Ext.extend($L.TriggerField, {
    nowTpl: ['<DIV class="item-day" style="cursor:pointer" title="{title}">{now}</DIV>'],
    constructor: function (config) {
        this.dateFields = [];
        this.cmps = {};
        $L.DatePicker.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        $L.DatePicker.superclass.initComponent.call(this, config);
        Ext.isIE6 && this.popup.setHeight(184);
        this.initFormat();
        this.initDatePicker();
    },
    initFormat: function () {
        this.format = this.format || $L.defaultDateFormat;
    },
    initDatePicker: function () {
        if (!this.inited) {
            this.initDateField();
            this.initFooter();
            this.inited = true;
// this.processListener('un');
// this.processListener('on');
        }
    },
    initDateField: function () {
        this.popup.setStyle({'width': 260 * this.viewsize + 'px'})
        if (this.dateFields.length == 0) {
// window['__host']=this;
            for (var i = 0; i < this.viewsize; i++) {
                var cfg = {
                    id: this.id + '_df' + i,
                    hostid: this.id,
                    height: 130,
                    enablemonthbtn: 'none',
                    enablebesidedays: 'none',
                    dayrenderer: this.dayrenderer,
                    listeners: {
// "select":this.onSelect.createDelegate(this),
                        "draw": this.onDraw.createDelegate(this),
                        "mouseover": this.mouseOver.createDelegate(this),
                        "mouseout": this.mouseOut.createDelegate(this)
                    }
                }
                if (i == 0) {
                    if (this.enablebesidedays == "both" || this.enablebesidedays == "pre")
                        cfg.enablebesidedays = "pre";
                    if (this.enablemonthbtn == "both" || this.enablemonthbtn == "pre")
                        cfg.enablemonthbtn = "pre";
                    if (this.enableyearbtn == "both" || this.enableyearbtn == "pre")
                        cfg.enableyearbtn = "pre";
                }
                if (i == this.viewsize - 1) {
                    if (this.enablebesidedays == "both" || this.enablebesidedays == "next")
                        cfg.enablebesidedays = cfg.enablebesidedays == "pre" ? "both" : "next";
                    if (this.enablemonthbtn == "both" || this.enablemonthbtn == "next")
                        cfg.enablemonthbtn = cfg.enablemonthbtn == "pre" ? "both" : "next";
                    if (this.enableyearbtn == "both" || this.enableyearbtn == "next")
                        cfg.enableyearbtn = cfg.enableyearbtn == "pre" ? "both" : "next";
                } else Ext.fly(this.id + '_df' + i).dom.style.cssText = "border-right:1px solid #BABABA";
                this.dateFields.add(new $L.DateField(cfg));
            }
// window['__host']=null;
        }
    },
    initFooter: function () {
        if (!this.now) this.now = new Ext.Template(this.nowTpl).append(this.popup.child("div.item-dateField-foot").dom, {
            now: _lang['datepicker.today'],
            title: new Date().format(this.format)
        }, true);
        var now = new Date(),
            cell = this.now,
            dr = this.dayrenderer;
        dr && $L.getRenderer(dr).call(this, cell, now, now.getDate());
        if (cell.disabled) {
            cell.set({'_date': '0'});
            cell.addClass("item-day-disabled");
        } else {
            cell.set({"_date": new Date(now.getFullYear(), now.getMonth(), now.getDate(), 0, 0, 0).getTime()});
        }
    },
    initEvents: function () {
        $L.DatePicker.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event select 选择事件.
             * @param {Leaf.DatePicker}
             *            datePicker 日期选择组件.
             * @param {Date}
             *            date 选择的日期.
             */
            'select');
    },
    processListener: function (ou) {
        $L.DatePicker.superclass.processListener.call(this, ou);
        this.el[ou]('click', this.mouseOut, this);
        this.popup[ou]("click", this.onSelect, this);
    },
    mouseOver: function (cmp, e) {
        if (this.focusField) this.focusField.out();
        this.focusField = cmp
    },
    mouseOut: function () {
        if (this.focusField) this.focusField.out();
        this.focusField = null;
    },
    onKeyUp: function (e) {
        if (this.readonly)return;
        $L.DatePicker.superclass.onKeyUp.call(this, e);
        var c = e.keyCode;
        if (!e.isSpecialKey() || c == 8 || c == 46) {
            try {
                this.selectDay = this.getRawValue().parseDate(this.format);
                this.wrapDate(this.selectDay);
                $L.Component.prototype.setValue.call(this, this.selectDay || "");
                this.predraw(this.selectDay);
            } catch (e) {
            }
        }
    },
    onKeyDown: function (e) {
        if (this.readonly)return;
        if (this.focusField) {
            switch (e.keyCode) {
                case 37:
                    this.goLeft(e);
                    break;
                case 38:
                    this.goUp(e);
                    break;
                case 39:
                    this.goRight(e);
                    break;
                case 40:
                    this.goDown(e);
                    break;
                case 13:
                    this.onSelect(e, this.focusField.overTd);
                default: {
                    if (this.focusField) this.focusField.out();
                    this.focusField = null;
                }
            }
        } else {
            $L.DatePicker.superclass.onKeyDown.call(this, e);
            if (e.keyCode == 40) {
                this.focusField = this.dateFields[0];
                this.focusField.over();
            }
        }
    },
    goLeft: function (e) {
        var field = this.focusField;
        var td = field.overTd, prev = td.prev('.item-day');
        field.out();
        if (prev) {
            field.over(prev);
        } else {
            var f = this.dateFields[this.dateFields.indexOf(field) - 1],
                index = td.parent().getAttributeNS('', 'r_index')
            if (f) {
                this.focusField = f;
            } else {
                field.preMonth();
                this.focusField = this.dateFields[this.dateFields.length - 1];
            }
            var l = this.focusField.body.child('tr[r_index=' + index + ']').select('td.item-day')
            this.focusField.over(l.item(l.getCount() - 1));
        }
        e.stopEvent();
    },
    goUp: function (e) {
        var field = this.focusField;
        var td = field.overTd, prev = td.parent().prev(), index = td.getAttributeNS('', 'c_index'), t;
        field.out();
        if (prev) t = prev.child('td[c_index=' + index + ']');
        if (t) field.over(t);
        else {
            var f = this.dateFields[this.dateFields.indexOf(field) - 1];
            if (f) {
                this.focusField = f;
            } else {
                field.preMonth();
                this.focusField = this.dateFields[0];
            }
            var l = this.focusField.body.select('td[c_index=' + index + ']')
            this.focusField.over(l.item(l.getCount() - 1));
        }
    },
    goRight: function (e) {
        var field = this.focusField;
        var td = field.overTd, next = td.next('.item-day'), parent = td.parent();
        field.out();
        if (next) {
            field.over(next);
        } else {
            var f = this.dateFields[this.dateFields.indexOf(field) + 1];
            if (f) {
                this.focusField = f;
            } else {
                field.nextMonth();
                this.focusField = this.dateFields[0];
            }
            this.focusField.over(this.focusField.body.child('tr[r_index=' + parent.getAttributeNS('', 'r_index') + ']').child('td.item-day'));
        }
        e.stopEvent();
    },
    goDown: function (e) {
        var field = this.focusField;
        var td = field.overTd, next = td.parent().next(), t, index = td.getAttributeNS('', 'c_index');
        field.out();
        if (next) t = next.child('td[c_index=' + index + ']');
        if (t) field.over(t);
        else {
            var f = this.dateFields[this.dateFields.indexOf(field) + 1];
            if (f) {
                this.focusField = f;
            } else {
                field.nextMonth();
                this.focusField = this.dateFields[this.dateFields.length - 1];
            }
            this.focusField.over(this.focusField.body.child('td[c_index=' + index + ']'));
        }
    },
    onDraw: function (field) {
        if (this.dateFields.length > 1) this.sysnDateField(field);
    },
    onSelect: function (e, t) {
// if(((t =Ext.fly(t)).hasClass('item-day'))){
        var _date = Ext.fly(t).getAttributeNS('', '_date');
        if (_date && _date != '0') {
            var sf = this, date = new Date(Number(_date));
            sf.collapse();
            sf.processDate(date);
            sf.setValue(date);
            sf.fireEvent("select", sf, date);
        }
// }
    },
    wrapDate: function (d) {
    },
    processDate: function (d) {
    },
    onBlur: function (e) {
        if (this.hasFocus) {
            $L.DatePicker.superclass.onBlur.call(this, e);
            if (!this.readonly && !this.isExpanded()) {
                try {
                    var d = this.getRawValue().parseDate(this.format)
                    this.wrapDate(d);
                    this.setValue(d || "");
                } catch (e) {
                    // alert(e.message);
                    this.setValue("");
                }
            }
        }
    },
    formatValue: function (date) {
        if (date instanceof Date)return date.format(this.format);
        return date;
    },
    expand: function () {
        this.selectDay = this.getValue();
        this.predraw(this.selectDay);
        $L.DatePicker.superclass.expand.call(this);
    },
    collapse: function () {
        $L.DatePicker.superclass.collapse.call(this);
        this.focusField = null;
    },
    destroy: function () {
        var sf = this, wrap = sf.wrap;
        $L.DatePicker.superclass.destroy.call(this);
        Ext.iterate(wrap.cmps, function (key, cmp) {
            try {
                cmp.destroy && cmp.destroy();
            } catch (e) {
                alert('销毁datePicker出错: ' + e)
            }
            ;
        })
        delete this.format;
        delete this.viewsize;
// setTimeout(function(){
// for(var key in sf.cmps){
// var cmp = sf.cmps[key];
// if(cmp.destroy){
// try{
// cmp.destroy();
// }catch(e){
// alert('销毁window出错: ' + e)
// }
// }
// }
// },10)
    },
    predraw: function (date) {
        if (date && date instanceof Date) {
            this.selectDay = new Date(date);
        } else {
            date = new Date();
            date.setHours(this.hour || 0);
            date.setMinutes(this.minute || 0);
            date.setSeconds(this.second || 0);
            date.setMilliseconds(0);
        }
        this.draw(date);
    },
    draw: function (date) {
        this.dateFields[0].selectDay = this.selectDay;
        this.dateFields[0].format = this.format;
        this.dateFields[0].predraw(date);
    },
    sysnDateField: function (field) {
        var date = new Date(field.date);
        for (var i = 0; i < this.viewsize; i++) {
            if (field == this.dateFields[i]) date.setMonth(date.getMonth() - i);
        }
        for (var i = 0; i < this.viewsize; i++) {
            this.dateFields[i].selectDay = this.selectDay;
            if (i != 0) date.setMonth(date.getMonth() + 1);
            this.dateFields[i].format = this.format;
            if (field != this.dateFields[i])
                this.dateFields[i].predraw(date, true);
        }
    }
});
/**
 * @class Leaf.DateTimePicker
 * @extends Leaf.DatePicker
 *          <p>
 *          DatePicker组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.DateTimePicker = Ext.extend($L.DatePicker, {
    initFormat: function () {
        this.format = this.format || $L.defaultDateTimeFormat;
    },
    initFooter: function () {
        this.hourSpan = this.popup.child("input[atype=field.hour]");
        this.minuteSpan = this.popup.child("input[atype=field.minute]");
        this.secondSpan = this.popup.child("input[atype=field.second]");
        this.hourSpanParent = this.hourSpan.parent();
    },
    processListener: function (ou) {
        $L.DateTimePicker.superclass.processListener.call(this, ou);
        if (this.hourSpan) {
            this.hourSpan[ou]("click", this.onDateClick, this, {stopPropagation: true});
            this.hourSpan[ou]("focus", this.onDateFocus, this);
            this.hourSpan[ou]("blur", this.onDateBlur, this);
            this.minuteSpan[ou]("focus", this.onDateFocus, this);
            this.minuteSpan[ou]("blur", this.onDateBlur, this);
            this.minuteSpan[ou]("click", this.onDateClick, this, {stopPropagation: true});
            this.secondSpan[ou]("focus", this.onDateFocus, this);
            this.secondSpan[ou]("blur", this.onDateBlur, this);
            this.secondSpan[ou]("click", this.onDateClick, this, {stopPropagation: true});
            this.hourSpanParent[ou]("keydown", this.onDateKeyDown, this);
            this.hourSpanParent[ou]("keyup", this.onDateKeyUp, this);
        }
    },
    onDateKeyDown: function (e) {
        var c = e.keyCode, el = e.target;
        if (c == 13) {
            el.blur();
        } else if (c == 27) {
            el.value = el.oldValue || "";
            el.blur();
        } else if (c != 8 && c != 9 && c != 37 && c != 39 && c != 46 && (c < 48 || c > 57 || e.shiftKey)) {
            e.stopEvent();
            return;
        }
    },
    onDateKeyUp: function (e) {
        var c = e.keyCode, el = e.target;
        if (c != 8 && c != 9 && c != 37 && c != 39 && c != 46 && (c < 48 || c > 57 || e.shiftKey)) {
            e.stopEvent();
            return;
        } else {
            if (this.value && this.value instanceof Date) {
                var date = new Date(this.value.getTime());
                this.processDate(date);
                this.setValue(date);
                // this.fireEvent('select',this, date);
            }
            this.draw(new Date(this.dateFields[0].year, this.dateFields[0].month - 1, 1, this.hourSpan.dom.value, this.minuteSpan.dom.value, this.secondSpan.dom.value));
        }
    },
    onDateClick: function () {
    },
    onDateFocus: function (e) {
        Ext.fly(e.target.parentNode).addClass("item-dateField-input-focus");
        e.target.select();
    },
    onDateBlur: function (e) {
        var el = e.target;
        Ext.fly(el.parentNode).removeClass("item-dateField-input-focus");
        if (!el.value.match(/^[0-9]*$/)) el.value = el.oldValue || "";
    },
    draw: function (date) {
        $L.DateTimePicker.superclass.draw.call(this, date);
        if (this.hourSpan.dom.value) {
            if (this.hourSpan.dom.value > 9) {
                this.hourSpan.dom.oldValue = this.hourSpan.dom.value = $L.dateFormat.pad(this.dateFields[0].hours);
            }
        } else {
            this.hourSpan.dom.oldValue = this.hourSpan.dom.value = $L.dateFormat.pad(this.dateFields[0].hours);
        }
        if (this.minuteSpan.dom.value) {
            if (this.minuteSpan.dom.value > 9) {
                this.minuteSpan.dom.oldValue = this.minuteSpan.dom.value = $L.dateFormat.pad(this.dateFields[0].minutes);
            }
        } else {
            this.minuteSpan.dom.oldValue = this.minuteSpan.dom.value = $L.dateFormat.pad(this.dateFields[0].minutes);
        }
        if (this.secondSpan.dom.value) {
            if (this.secondSpan.dom.value > 9) {
                this.secondSpan.dom.oldValue = this.secondSpan.dom.value = $L.dateFormat.pad(this.dateFields[0].seconds);
            }
        } else {
            this.secondSpan.dom.oldValue = this.secondSpan.dom.value = $L.dateFormat.pad(this.dateFields[0].seconds);
        }
    },
    processDate: function (d) {
        if (d) {
            d.setHours((el = this.hourSpan.dom).value.match(/^[0-9]*$/) ? el.value : el.oldValue);
            d.setMinutes((el = this.minuteSpan.dom).value.match(/^[0-9]*$/) ? el.value : el.oldValue);
            d.setSeconds((el = this.secondSpan.dom).value.match(/^[0-9]*$/) ? el.value : el.oldValue);
            this.wrapDate(d)
        }
    },
    wrapDate: function (d) {
        if (d)
            d.xtype = 'timestamp';
    }
// ,collapse : function(){
// $L.DateTimePicker.superclass.collapse.call(this);
// if(this.getRawValue()){
// var d = this.selectDay;
// if(d){
// d.setHours((el=this.hourSpan.dom).value.match(/^[0-9]*$/)?el.value:el.oldValue);
// d.setMinutes((el=this.minuteSpan.dom).value.match(/^[0-9]*$/)?el.value:el.oldValue);
// d.setSeconds((el=this.secondSpan.dom).value.match(/^[0-9]*$/)?el.value:el.oldValue);
// }
// d.xtype = 'timestamp';
// this.setValue(d);
// }
// }
});
$L.ToolBar = Ext.extend($L.Component, {
    constructor: function (config) {
        $L.ToolBar.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        $L.ToolBar.superclass.initComponent.call(this, config);
    },
    initEvents: function () {
        $L.ToolBar.superclass.initEvents.call(this);
    }
});
$L.NavBar = Ext.extend($L.ToolBar, {
    initComponent: function (config) {
        $L.NavBar.superclass.initComponent.call(this, config);
        var sf = this,
            type = sf.type,
            wrap = sf.wrap;
        sf.dataSet = $le(sf.dataSet);
        sf.navInfo = wrap.child('div[atype=displayInfo]');// Ext.get(sf.infoId);
        if (sf.type != "simple" && sf.type != "tiny") {
            sf.pageInput = $le(sf.inputId);
            sf.currentPage = wrap.child('div[atype=currentPage]');
            sf.pageInfo = wrap.child('div[atype=pageInfo]');// Ext.get(sf.pageId);

            if (sf.comboBoxId) {
                sf.pageSizeInput = $le(sf.comboBoxId);
// sf.pageSizeInfo = wrap.child('div[atype=pageSizeInfo]');
                sf.pageSizeInfo2 = wrap.child('div[atype=pageSizeInfo2]');
// sf.pageSizeInfo.update(_lang['toolbar.pageSize']);
                sf.pageSizeInfo2.update(_lang['toolbar.pageSize2']);
            }
            sf.pageInfo.update(_lang['toolbar.total'] + '&#160;&#160;' + _lang['toolbar.page']);
            sf.currentPage.update(_lang['toolbar.ctPage']);
        }
    },
    processListener: function (ou) {
        var sf = this;
        $L.NavBar.superclass.processListener.call(sf, ou);
        sf.dataSet[ou]('load', sf.onLoad, sf);
        if (sf.type != "simple" && sf.type != "tiny") {
            sf.pageInput[ou]('change', sf.onPageChange, sf);
            if (sf.pageSizeInput) {
                sf.pageSizeInput[ou]('change', sf.onPageSizeChange, sf);
            }
        }
    },
    onLoad: function () {
        var sf = this, ds = sf.dataSet,
            pagesize = ds.pagesize,
            input = sf.pageSizeInput;
        sf.navInfo.update(sf.creatNavInfo());
        if (sf.type != "simple" && sf.type != "tiny") {
            if(sf.pageSizeInfo2) {
                sf.pageSizeInfo2.update("每页");
            }
            sf.pageInput.setValue(ds.currentPage, true);
            sf.pageInfo.update('/' + ds.totalPage);
            if (input && !input.optionDataSet) {
                if (ds.fetchall) {
                    pagesize = ds.totalCount;
                    input.initReadOnly(true);
                }
                var pageSize = [10, 20, 50, 100,'all'];
                if (pageSize.indexOf(pagesize) == -1) {
                    pageSize.unshift(pagesize);
                    pageSize.sort(function (a, b) {
                        return a - b
                    });
                }
                var datas = [];
                while (Ext.isDefined(pageSize[0])) {
                    var ps = pageSize.shift();
                    datas.push({'code': ps, 'name': ps});
                }
                var dataset = new $L.DataSet({'datas': datas});
                input.valuefield = 'code';
                input.displayfield = 'name';
                input.setOptions(dataset);
                input.setValue(pagesize, true);
            }
        }
    },
    creatNavInfo: function () {
        var sf = this,
            ds = sf.dataSet,
            currentPage = ds.currentPage,
            totalPage = ds.totalPage,
            totalCount = ds.totalCount,
            pagesize = ds.pagesize,
            type = sf.type,
            html = [];
        if (ds.fetchall) pagesize = totalCount;
        if (type == "simple") {
            if (totalPage) {
                html.push('<span>共' + totalPage + '页</span>');
                html.push(currentPage == 1 ? '<span>' + _lang['toolbar.firstPage'] + '</span>' : sf.createAnchor(_lang['toolbar.firstPage'], 1));
                html.push(currentPage == 1 ? '<span>' + _lang['toolbar.prePage'] + '</span>' : sf.createAnchor(_lang['toolbar.prePage'], currentPage - 1));
                for (var i = 1; i < 4 && i <= totalPage; i++) {
                    html.push(i == currentPage ? '<b>' + currentPage + '</b>' : sf.createAnchor(i, i));
                }
                if (totalPage > sf.maxPageCount) {
                    if (currentPage > 5) sf.createSplit(html);
                    for (var i = currentPage - 1; i < currentPage + 2; i++) {
                        if (i > 3 && i < totalPage - 2) {
                            html.push(i == currentPage ? '<b>' + currentPage + '</b>' : this.createAnchor(i, i));
                        }
                    }
                    if (currentPage < totalPage - 4) this.createSplit(html);
                } else if (totalPage > 6) {
                    for (var i = 4; i < totalPage - 2; i++) {
                        html.push(i == currentPage ? '<b>' + currentPage + '</b>' : this.createAnchor(i, i));
                    }
                }
                for (var i = totalPage - 2; i < totalPage + 1; i++) {
                    if (i > 3) {
                        html.push(i == currentPage ? '<b>' + currentPage + '</b>' : this.createAnchor(i, i));
                    }
                }
                html.push(currentPage == totalPage ? '<span>' + _lang['toolbar.nextPage'] + '</span>' : this.createAnchor(_lang['toolbar.nextPage'], currentPage + 1));
                html.push(currentPage == totalPage ? '<span>' + _lang['toolbar.lastPage'] + '</span>' : this.createAnchor(_lang['toolbar.lastPage'], totalPage));
            }
        } else if (type == 'tiny') {
            html.push(currentPage == 1 ? '<span>' + _lang['toolbar.firstPage'] + '</span>' : this.createAnchor(_lang['toolbar.firstPage'], 1));
            html.push(currentPage == 1 ? '<span>' + _lang['toolbar.prePage'] + '</span>' : this.createAnchor(_lang['toolbar.prePage'], currentPage - 1));
            html.push(sf.createAnchor(_lang['toolbar.nextPage'], currentPage + 1));
            html.push('<span>第' + currentPage + '页</span>');
        } else {
            var from = ((currentPage - 1) * pagesize + 1),
                to = currentPage * pagesize,
                theme = $L.getTheme();
            if (to > totalCount && totalCount > from) to = totalCount;
            if (to == 0) from = 0;
            html.push(_lang['toolbar.visible'], _lang['toolbar.entry'], ' ', from, ' - ', to);
            if (theme != 'mac')
                html.push(' ', _lang['toolbar.total'], ' ', totalCount);
        }
        return html.join('');
    },
    createAnchor: function (text, page) {
        return '<a href="javascript:$le(\'' + this.dataSet.id + '\').goPage(' + page + ')">' + text + '</a>';
    },
    createSplit: function (html) {
        html.push('<span>···</span>');
    },
    onPageChange: function (el, value, oldvalue) {
        var ds = this.dataSet;
        if (isNaN(value) || value <= 0 || value > ds.totalPage) {
            el.setValue(oldvalue, true);
        } else {
            ds.goPage(value);
        }
    },

// onPageChange : function(el,value,oldvalue){
// if(this.dataSet.totalPage == 0){
// el.setValue(1);
// }else if(isNaN(value) || value<=0 || value>this.dataSet.totalPage){
// el.setValue(oldvalue)
// }else if(this.dataSet.currentPage!=value){
// this.dataSet.goPage(value);
// }
// },
    onPageSizeChange: function (el, value, oldvalue) {
        var max = this.dataSet.maxpagesize;
        if(value == "all"){
            this.dataSet.pagesize = this.dataSet.totalCount;
            this.dataSet.query();

        }else if (isNaN(value) || value < 0) {
            el.setValue(oldvalue, true);
        } else if (value > max) {
            $L.showMessage(_lang['toolbar.errormsg'], _lang['toolbar.maxPageSize'] + max + _lang['toolbar.item'], null, 240);
            el.setValue(oldvalue, true);
        } else {
            this.dataSet.pagesize = Math.round(value);
            this.dataSet.query();
        }
    }
});
(function (A) {
    var cache = [],
        documentOverFlow,
        WINDOW_MANAGER = A.WindowManager = {
            put: function (win) {
                cache.add(win)
            },
            getAll: function () {
                return cache;
            },
            getCurrentCache:function(){
                if(cache.length){
                    return cache[cache.length-1];
                }else{
                    return null;
                }
            },
            remove: function (win) {
                cache.remove(win);
            },
            get: function (id) {
                var win = null;
                Ext.each(cache, function (w) {
                    if (w.id == id) {
                        win = w;
                        return false;
                    }
                });
                return win;
            },
            getZindex: function () {
                var zindex = 600;
                Ext.each(cache, function (win) {
                    zindex = Math.max(Number(win.wrap.getStyle('z-index')) || 0, zindex);
                });
                return zindex;
            },
            getTopZindex: function () {
                var zindex = 8999;
                Ext.each(cache, function (win) {
                    zindex = Math.max(Number(win.wrap.getStyle('z-index')) || 0, zindex);
                });
                return zindex;
            },
            hasFullWindow: function () {
                var has = false;
                Ext.each(cache, function (win) {
                    if (win.fullScreen && !win.usecache || win.isShow) {
                        has = true;
                    }
                });
                return has;
            },
            hasSideWindow: function () {
                var hsw = false;
                Ext.each(cache, function (win) {
                    if (win.side && !win.usecache || win.isShow) {
                        hsw = true;
                    }
                });
                return hsw;
            }
        }
    /**
     * @class Leaf.Window
     * @extends Leaf.Component
     *          <p>
     *          窗口组件.
     * @author njq.niu@hand-china.com
     * @constructor
     * @param {Object}
     *            config 配置对象.
     */
    A.Window = Ext.extend(A.Component, {
        constructor: function (config) {
            var win = WINDOW_MANAGER.get(config.id);
            if (win) {
                if (win.usecache) {
                    win.show();
                }
                return;
            }
            var sf = this;
            sf.draggable = true;
            sf.closeable = true;
            sf.refreshable = config.refreshable || true;
            sf.fullScreen = false;
            sf.side = null;
            sf.modal = config.modal || true;
            sf.cmps = {};
// A.focusWindow = null;
            A.Window.superclass.constructor.call(sf, config);
        },
        initComponent: function (config) {
            var sf = this;
            A.Window.superclass.initComponent.call(sf, config);
            sf.rememberDocumentOverflow();
            WINDOW_MANAGER.put(sf);
            sf.width = 1 * (sf.width || 350);
            sf.height = 1 * (sf.height || 400);
            sf.titleheight = (sf.titleheight || 32);
            if (sf.fullScreen) {
                sf.width = A.getViewportWidth();
                sf.height = A.getViewportHeight() - sf.titleheight;
                sf.draggable = false;
                sf.side = 'right';
//            sf.marginheight=1;
//            sf.marginwidth=1;
            } else if (sf.side) {
                sf.draggable = false;
                if (sf.side == 'top' || sf.side == 'bottom') {
                    sf.width = A.getViewportWidth() - 2;
                } else if (sf.side == 'left' || sf.side == 'right') {
//                sf.height=A.getViewportHeight()-42;
                    sf.height = A.getViewportHeight() - sf.titleheight;
                }
            }
            var url = sf.url,
                isIE = Ext.isIE,
                wrap = sf.wrap = new Ext.Template(sf.getTemplate(config)).insertFirst(document.body, {
                    id: sf.id,
                    title: sf.title,
                    titleheight: sf.titleheight,
                    width: sf.width,
                    bodywidth: sf.width,
                    height: sf.height,
                    url: url ? ('url="' + url + '"') : '',
                    clz: (sf.fullScreen ? 'full-window ' : '') + (sf.className || ''),
                    shadow: isIE ? '<DIV class="item-ie-shadow"></DIV>' : ''
                }, true);
            wrap.cmps = sf.cmps;
            sf.title = wrap.child('div[atype=window.title]');
            sf.head = wrap.child('td[atype=window.head]');
            sf.body = wrap.child('div[atype=window.body]');
            sf.closeBtn = wrap.child('div[atype=window.close]');
            sf.refreshBtn = wrap.child('div[atype=window.refresh]');
            if (sf.draggable) sf.initDraggable();
            if (!sf.closeable) sf.closeBtn.refreshBtn();
            if (!sf.refreshable) sf.refreshBtn.hide();
            if (!sf.side && sf.url && sf.url.indexOf("sys_lov.lview") == -1 && (A.getViewportHeight() - sf.titleheight - 2) <= sf.height) {
                sf.side = 'right';
            }
            if (sf.side) {
                if (sf.side == 'top') {
                    sf.move(0, 0);
                    sf.toFront();
                    sf.focus.defer(10, sf);
                    sf.wrap.addClass('animated zoomInSideTop');
                    setTimeout(function () {
                        sf.wrap.removeClass('animated zoomInSideTop');
                    }, 600);
                } else if (sf.side == 'bottom') {
                    sf.move(0, A.getViewportHeight() - sf.titleheight - 2 - sf.height);
                    sf.toFront();
                    sf.focus.defer(10, sf);
                    sf.wrap.addClass('animated zoomInSideBottom');
                    setTimeout(function () {
                        sf.wrap.removeClass('animated zoomInSideBottom');
                    }, 600);
                } else if (sf.side == 'left') {
                    sf.move(0, 0);
                    sf.toFront();
                    sf.focus.defer(10, sf);
                    sf.wrap.addClass('animated zoomInSideLeft');
                    setTimeout(function () {
                        sf.wrap.removeClass('animated zoomInSideLeft');
                    }, 600);
                } else if (sf.side == 'right') {
                    var scroll = sf.wrap.parent().getScroll(),
                        sy = scroll.top;
                    sf.move(A.getViewportWidth() - sf.width, sy);
                    try {
                        if (global_body_no_scroll_flag && global_body_no_scroll_flag == 'Y') {
                            sf.move(A.getViewportWidth() - sf.width, sy);
                        } else {
                    sf.move(A.getViewportWidth() - sf.width, 0);
                        }
                    }catch (e) {
                        sf.move(A.getViewportWidth() - sf.width, 0);
                    }
                    if (Ext.isIE8) {
                        sf.move(A.getViewportWidth() - sf.width + 17, sy);
                    }
                    sf.toFront();
                    sf.focus.defer(10, sf);
                    sf.wrap.addClass('animated zoomInSideRight');
                    setTimeout(function () {
                        sf.wrap.removeClass('animated zoomInSideRight');
                    }, 600);
                }
            } else if (Ext.isEmpty(config.x) || Ext.isEmpty(config.y) || sf.fullScreen) {
                sf.center();
                sf.wrap.addClass('animated zoomIn');
                setTimeout(function () {
                    sf.wrap.removeClass('animated zoomIn');
                }, 600);
            } else {
                sf.move(config.x, config.y);
                sf.toFront();
                sf.focus.defer(10, sf);
                sf.wrap.addClass('animated zoomIn');
                setTimeout(function () {
                    sf.wrap.removeClass('animated zoomIn');
                }, 600);
            }
            url && sf.load(url, config.params);
            //如果首页窗口置于置于最上方
            if (top.location === self.location) {
                this.wrap.setStyle('z-index', WINDOW_MANAGER.getTopZindex());
            }
        },
        processListener: function (ou) {
            var sf = this;
            A.Window.superclass.processListener.call(sf, ou);
            sf.closeable &&
            sf.closeBtn[ou]("click", sf.onCloseClick, sf)
                [ou]("mouseover", sf.onCloseOver, sf)
                [ou]("mouseout", sf.onCloseOut, sf)
                [ou]("mousedown", sf.onCloseDown, sf);
            sf.refreshBtn && sf.refreshBtn[ou]('click',sf.onFefreshClick,sf);
            sf.wrap[ou]("click", sf.onClick, sf, {stopPropagation: false})
                [ou]("keydown", sf.onKeyDown, sf);
            sf.draggable && sf.head[ou]('mousedown', sf.onMouseDown, sf);
            sf.body[ou]('DOMSubtreeModified', sf.bodyUpdate, sf);
        },
        initEvents: function () {
            A.Window.superclass.initEvents.call(this);
            this.addEvents(
                /**
                 * @event beforeclose 窗口关闭前的事件.
                 *        <p>
                 *        监听函数返回值为false时，不执行关闭
                 *        </p>
                 * @param {Window}
                 *            this 当前窗口. *
                 */
                'beforeclose',
                /**
                 * @event close 窗口关闭事件.
                 * @param {Window}
                 *            this 当前窗口. *
                 */
                'close',
                /**
                 * @event load 窗口加载完毕.
                 * @param {Window}
                 *            this 当前窗口.
                 */
                'load');
        },
        bodyUpdate: function () {
            // if(!Ext.isIE8){
            //     $("div.win-content").getNiceScroll().resize();
            // }
        },
        onClick: function (e) {
            if (!this.modal) this.toFront();
        },
        onKeyDown: function (e) {
            var key = e.getKey();
            if (key == 9) {
                var fk, lk, ck, cmp, cmps = this.cmps;
                for (var k in cmps) {
                    cmp = cmps[k];
                    if (cmp.focus) {
                        if (!fk) fk = k;
                        lk = k;
                    }
                    if (cmp.hasFocus) {
                        ck = k;
                    }
                }
                if (e.shiftKey) {
                    var temp = lk;
                    lk = fk;
                    fk = temp;
                }
                if (ck == lk) {
                    e.stopEvent();
                    if (cmp && cmp.blur) cmp.blur();
                    fk && cmps[fk].focus();
                }
            } else if (key == 27) {
                e.stopEvent();
                this.close();
            }
        },
        initDraggable: function () {
            this.head.addClass('item-draggable');
        },
        /**
         * 窗口获得焦点.
         *
         */
        focus: function () {
            this.wrap.focus();
        },
        /**
         * 窗口居中.
         *
         */
        center: function () {
            var sf = this,
                screenWidth = A.getViewportWidth(),
                screenHeight = A.getViewportHeight(),
                scroll = Ext.getBody().getScroll(),
                sl = scroll.left,
                st = scroll.top,
                x = sl + Math.max((screenWidth - sf.width) / 2, 0),
                y = st + Math.max((screenHeight - sf.height - (Ext.isIE ? 26 : 23)) / 2, 0);
// sf.shadow.setWidth(sf.wrap.getWidth());
// sf.shadow.setHeight(sf.wrap.getHeight());
            if (sf.fullScreen) {
                x = sl;
                y = st;
                sf.move(x, y, true);
            } else {
                sf.move(x, y)
            }
// sf.wrap.moveTo(x,y);
            sf.toFront();
            sf.focus.defer(10, sf);
        },
        /**
         * 移动窗口到指定位置.
         *
         */
        move: function (x, y, m) {
            this.wrap.moveTo(x, y);
        },
        getTemplate: function (config) {
            if (config.isDialog) {
                return [
                    '<DIV id="{id}" class="win-wrap  {clz}" style="left:-10000px;top:-10000px;width:{width}px;outline:none" hideFocus tabIndex="-1" {url}>',
                    '{shadow}',
                    '<TABLE cellSpacing="0" cellPadding="0" border="0" width="100%">',
                    '<TBODY>',
                    '<TR>',
                    '<TD class="win-caption win-dialog">',
                    '<TABLE cellSpacing="0" class="win-cap" unselectable="on" style="-moz-user-select:none;"  cellPadding="0" width="100%" border="0" unselectable="on">',
                    '<TBODY>',
                    '<TR>',
                    '<TD unselectable="on" class="win-caption-label" atype="window.head" width="99%">',
                    '<DIV unselectable="on" atype="window.title" unselectable="on">{title}</DIV>',
                    '<div class="win-refresh" atype="window.refresh" unselectable="on" style=""  title="重新加载"></div>',
                    '</TD>',
                    '<TD unselectable="on" class="win-caption-button" noWrap>',
                    '<DIV class="win-close" atype="window.close" unselectable="on"></DIV>',
                    '</TD>',
                    /* '<TD><DIV style="width:5px;"/></TD>', */
                    '</TR>',
                    '</TBODY>',
                    '</TABLE>',
                    '</TD>',
                    '</TR>',
                    '<TR>',
                    '<TD class="win-body" vAlign="top" unselectable="on">',
                    '<DIV class="win-content win-dialog-content" id="win-content" atype="window.body" style="position:relative;width:100%;" unselectable="on"></DIV>',
                    '</TD>',
                    '</TR>',
                    '</TBODY>',
                    '</TABLE>',
                    '</DIV>'
                ]
            }
            if (true) {
                return [
                    '<DIV id="{id}" class="win-wrap  {clz}" style="left:-10000px;top:-10000px;width:{width}px;outline:none" hideFocus tabIndex="-1" {url}>',
                    '{shadow}',
                    '<TABLE cellSpacing="0" cellPadding="0" border="0" width="100%">',
                    '<TBODY>',
                    '<TR style="height:{titleheight}px;" >',
                    '<TD class="win-caption" style="background: #fff;border-bottom: 1px solid rgba(220,223,230,1)">',
                    '<TABLE cellSpacing="0" class="win-cap" unselectable="on" style="height:23px;-moz-user-select:none;"  cellPadding="0" width="100%" border="0" unselectable="on">',
                    '<TBODY>',
                    '<TR>',
                    '<TD unselectable="on" class="win-caption-label" atype="window.head" width="93%">',
                    '<DIV unselectable="on" atype="window.title" unselectable="on" style="margin-left: 16px;color: rgba(75,80,90,0.85)">{title}</DIV>',
                    '<div class="win-refresh" atype="window.refresh" unselectable="on" style=""  title="重新加载"></div>',
                    '</TD>',
                    '<TD unselectable="on" class="win-caption-button" noWrap style="display: block;float: right">',
                    '<DIV class="win-close" atype="window.close" unselectable="on" style=""></DIV>',
                    '</TD>',
                    /* '<TD><DIV style="width:5px;"/></TD>', */
                    '</TR>',
                    '</TBODY>',
                    '</TABLE>',
                    '</TD>',
                    '</TR>',
                    '<TR style="height:{height}px">',
                    '<TD class="win-body" vAlign="top" unselectable="on">',
                    '<DIV class="win-content" id="win-content" atype="window.body" style="position:relative;width:{bodywidth}px;height:{height}px;" unselectable="on"></DIV>',
                    '</TD>',
                    '</TR>',
                    '</TBODY>',
                    '</TABLE>',
                    '</DIV>'
                ];
            } else {
                return [
                    '<DIV id="{id}" class="win-wrap  {clz}" style="left:-10000px;top:-10000px;width:{width}px;outline:none" hideFocus tabIndex="-1" {url}>',
                    '{shadow}',
                    '<TABLE cellSpacing="0" cellPadding="0" border="0" width="100%">',
                    '<TBODY>',
                    '<TR>',
                    '<TD unselectable="on" class="win-caption-label" atype="window.head" width="99%">',
                    '</TD>',
                    '</TR>',
                    '<TR style="height:{height}px">',
                    '<TD class="win-body" vAlign="top" unselectable="on">',
                    '<DIV class="win-close-fixed" atype="window.close" unselectable="on"></DIV>',
                    '<DIV class="win-content" atype="window.body" style="position:relative;width:{bodywidth}px;height:{height}px;" unselectable="on"></DIV>',
                    '</TD>',
                    '</TR>',
                    '</TBODY>',
                    '</TABLE>',
                    '</DIV>'
                ];
            }
        },
        /**
         * 窗口定位到最上层.
         *
         */
        toFront: function () {
            var myzindex = this.wrap.getStyle('z-index');
            var zindex = WINDOW_MANAGER.getZindex();
            if (myzindex == 'auto') myzindex = 0;
            if (myzindex < zindex) {
                this.wrap.setStyle('z-index', zindex + 5);
                if (this.modal) A.Cover.cover(this.wrap);
            }
            if (this.modal) {
                // 去除下面window遮盖的透明度
                var alls = WINDOW_MANAGER.getAll()
                for (var i = 0; i < alls.length; i++) {
                    var pw = alls[i];
                    if (pw != this) {
                        var cover = A.Cover.container[pw.wrap.id];
                        if (cover) cover.setStyle({
                            filter: 'alpha(opacity=0)',
                            opacity: '0',
                            mozopacity: '0'
                        })
                    }
                }
            }
            /*
             * 如果窗口含有side属性，在打开时去除父页面的滚动条
             * 向平   2018-01-05
             */
            /*if (this.side) {
                var style = document.documentElement.style;
                documentOverFlow = style.overflow;
                style.overflow = "hidden";
            }*/

// A.focusWindow = this;
        },
        onMouseDown: function (e) {
            var sf = this;
            if(e.target.classList.contains("win-refresh")) return
            // e.stopEvent();
            sf.toFront();
            var xy = sf.wrap.getXY();
            sf.relativeX = xy[0] - e.getPageX();
            sf.relativeY = xy[1] - e.getPageY();
            sf.screenWidth = A.getViewportWidth();
            sf.screenHeight = A.getViewportHeight();
            if (!this.proxy) this.initProxy();
            this.proxy.show();
            Ext.get(document.documentElement).on("click", sf.stopClick, sf);
            Ext.get(document.documentElement).on("mousemove", sf.onMouseMove, sf);
            Ext.get(document.documentElement).on("mouseup", sf.onMouseUp, sf);
// sf.focus();
        },
        stopClick: function (e) {
            e.stopEvent();
            Ext.get(document.documentElement).un("click", this.stopClick, this);
        },
        onMouseUp: function (e) {
            e.stopEvent();
            var sf = this;
            Ext.get(document.documentElement).un("mousemove", sf.onMouseMove, sf);
            Ext.get(document.documentElement).un("mouseup", sf.onMouseUp, sf);
            if (sf.proxy) {
                sf.wrap.moveTo(sf.proxy.getX(), sf.proxy.getY());
                sf.proxy.hide();
            }
        },
        onMouseMove: function (e) {
            e.stopEvent();
            var sl = document[Ext.isStrict && !Ext.isWebKit ? 'documentElement' : 'body'].scrollLeft;
            var st = document[Ext.isStrict && !Ext.isWebKit ? 'documentElement' : 'body'].scrollTop;
            var sw = sl + this.screenWidth;
            var sh = st + this.screenHeight;
            var tx = e.getPageX() + this.relativeX;
            var ty = e.getPageY() + this.relativeY;
            if (tx <= sl) tx = sl;
            if ((tx + this.width) >= (sw - 3)) tx = sw - this.width - 3;
            if (ty <= st) ty = st;
            if ((ty + this.height) >= (sh - 30)) ty = Math.max(sh - this.height - 30, 0);
            this.proxy.moveTo(tx, ty);
        },
        checkDataSetNotification: function () {
            var r = Leaf.checkNotification(this.cmps);
            if (r) {
                var sf = this;
                A.showConfirm(_lang['dataset.info'], r, function () {
                    sf.close(true);
                })
                return false;
            }
            return true;
        },
        showLoading: function () {
            this.body.update(_lang['window.loading']);
            this.body.setStyle('text-align', 'center');
            this.body.setStyle('line-height', 5);
        },
        clearLoading: function () {
            this.body.update('');
            this.body.setStyle('text-align', '');
            this.body.setStyle('line-height', '');
        },
        initProxy: function () {
            var sf = this;
            var p = '<DIV style="border:1px dashed black;Z-INDEX: 10000; LEFT: 0px; WIDTH: 100%; CURSOR: move; POSITION: absolute; TOP: 0px; HEIGHT: 621px;-moz-user-select:none;" unselectable="on"></DIV>'
            sf.proxy = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(), p));
// sf.proxy.hide();
            var xy = sf.wrap.getXY();
            sf.proxy.setWidth(sf.wrap.getWidth());
            sf.proxy.setHeight(sf.wrap.getHeight());
            sf.proxy.setLocation(xy[0], xy[1]);
        },
        onCloseClick: function (e) {
            e.stopEvent();
            this.close();
        },
        onCloseOver: function (e) {
            this.closeBtn.addClass("win-btn-over");
        },
        onCloseOut: function (e) {
            this.closeBtn.removeClass("win-btn-over");
        },
        onCloseDown: function (e) {
            this.closeBtn.removeClass("win-btn-over");
            this.closeBtn.addClass("win-btn-down");
            Ext.get(document.documentElement).on("mouseup", this.onCloseUp, this);
        },
        onCloseUp: function (e) {
            this.closeBtn.removeClass("win-btn-down");
            Ext.get(document.documentElement).un("mouseup", this.onCloseUp, this);
        },
        hide: function () {
            var sf = this;
            sf.isShow = false;
            if (sf.modal) A.Cover.uncover(sf.wrap);
            sf.wrap.setStyle({
                display: 'none'
            });
            sf.restoreDocumentOverflow();
        },
        show: function () {
            var sf = this;
            if (sf.modal) A.Cover.cover(sf.wrap);
            sf.wrap.setStyle({
                display: ''
            });
            sf.rememberDocumentOverflow();
            sf.isShow = true;
        },

        rememberDocumentOverflow: function () {
            var sf = this;
            if (sf.fullScreen || (sf.side && sf.side == 'right')) {
                if(WINDOW_MANAGER.getCurrentCache()){
                    $jq(Ext.fly(WINDOW_MANAGER.getCurrentCache()).dom.wrap.dom).find(".win-content").css("overflow","hidden")
                }else{
                    Ext.fly(document.documentElement).setStyle("overflow","hidden");
                }
            }
        },
        restoreDocumentOverflow: function () {
            var sf = this;
            if (sf.fullScreen || (sf.side && sf.side == 'right')) {
                if(WINDOW_MANAGER.getCurrentCache()){
                    $jq(Ext.fly(WINDOW_MANAGER.getCurrentCache()).dom.wrap.dom).find(".win-content").css("overflow","")
                }else{
                    Ext.fly(document.documentElement).setStyle("overflow","");
                }
            }
        },
        close: function (nocheck) {
            var sf = this;
            jQuery(".grid-uh").click();
            if (sf.fireEvent('beforeclose', sf)) {
                /*
                 * 当窗口是由右向左弹出,点击关闭是让窗口由左向右收起
                 * 向平     2017-11-15
                 */
                if (sf.side === 'right') {
                    sf.wrap.addClass('animated slideOutRight');
                }
                /*
                 * 当窗口是由左向右弹出,点击关闭是让窗口由右向左收起
                 * 向平     2018-02-27
                 */
                if (sf.side === 'left') {
                    sf.wrap.addClass('animated slideOutLeft');
                }

                sf.wrap.addClass('animated zoomOut');
                // 增加淡出效果
                if (sf.modal) {
                    // 去除下面window遮盖的透明度
                    var alls = WINDOW_MANAGER.getAll();
                    for (var i = 0; i < alls.length - 1; i++) {
                        var pw = alls[i];
                        if (pw != sf) {
                            var cover = A.Cover.container[pw.wrap.id];
                        }
                    }

                    var cw = alls[alls.length - 1];
                    if (cw) {
                        var cover = A.Cover.container[cw.wrap.id];
                        if (cover) {
                            cover.addClass('animated fadeOut03');
                        }
                    }
                }


                // 调整成渐变效果
                var d = new Ext.util.DelayedTask(function () {
                    if (!nocheck && !sf.checkDataSetNotification()) return;
                    if (sf.usecache) {
                        sf.hide();
                        return;
                    }
                    if (sf.wrap) sf.wrap.destroying = true;
                    WINDOW_MANAGER.remove(sf);
                    sf.restoreDocumentOverflow();
                    if (sf.fullScreen && !WINDOW_MANAGER.hasFullWindow() && !WINDOW_MANAGER.hasSideWindow() && !Ext.isIE8) {
                        /*$jq("html").getNiceScroll().remove();
                        $jq("html").niceScroll({enablemousewheel: true});*/
                    }
                    if (sf.side && !WINDOW_MANAGER.hasSideWindow() && !WINDOW_MANAGER.hasFullWindow() && !Ext.isIE8) {
                        /*$jq("html").getNiceScroll().remove();
                        $jq("html").niceScroll({enablemousewheel: true});*/
                    }
                    sf.destroy();
                    sf.fireEvent('close', sf);

                    if (this.modal) {
                        // 去除下面window遮盖的透明度
                        var alls = WINDOW_MANAGER.getAll()
                        for (var i = 0; i < alls.length - 1; i++) {
                            var pw = alls[i];
                            if (pw != sf) {
                                var cover = A.Cover.container[pw.wrap.id];
                                if (cover) cover.setStyle({
                                    filter: 'alpha(opacity=0)',
                                    opacity: '0',
                                    mozopacity: '0'
                                })
                            }
                        }


                        var cw = alls[alls.length - 1];
                        if (cw) {
                            var cover = A.Cover.container[cw.wrap.id];
                            if (cover) {
                                cover.setStyle({
                                    opacity: '',
                                    mozopacity: ''
                                })
                                cover.dom.style.cssText = cover.dom.style.cssText.replace(/filter[^;]*/i, '');
                            }
                        }
                    }
                });

                if (Ext.isIE6 || Ext.isIE7 || Ext.isIE8 || Ext.isIE9) {
                    d.delay(0);
                } else {
                    d.delay(300);
                }
            }
            if (!Ext.isIE8) {
                /*$jq("html").getNiceScroll().remove();
                $jq("html").niceScroll({enablemousewheel: true});*/
            }
        },
        clearBody: function () {
            for (var key in this.cmps) {
                var cmp = this.cmps[key];
                if (cmp.destroy) {
                    try {
                        cmp.destroy();
                    } catch (e) {
                        alert('销毁window出错: ' + e)
                    }
                }
            }
        },
        destroy: function () {
// A.focusWindow = null;
            var wrap = this.wrap;
            if (!wrap)return;
            if (this.proxy) this.proxy.remove();
            if (this.modal) A.Cover.uncover(this.wrap);
            A.Window.superclass.destroy.call(this);
            this.clearBody();
            delete this.title;
            delete this.head;
            delete this.body;
            delete this.closeBtn;
            delete this.refreshBtn;
            delete this.proxy;
            wrap.remove();
// var sf = this;
// setTimeout(function(){
// for(var key in sf.cmps){
// var cmp = sf.cmps[key];
// if(cmp.destroy){
// try{
// cmp.destroy();
// }catch(e){
// alert('销毁window出错: ' + e)
// }
// }
// }
// },10)
        },
        /**
         * 窗口加载.
         *
         * @param {String}
         *            url 加载的url
         * @param {Object}
         *            params 加载的参数
         */
        load: function (url, params) {
// var cmps = A.CmpManager.getAll();
// for(var key in cmps){
// this.oldcmps[key] = cmps[key];
// }
            this.clearBody();
            this.isLoading = true;
            this.showLoading();
            var header = $jq('meta[name=_csrf_header]').attr('content');
            var token = $jq('meta[name=_csrf]').attr('content');
            if(url.indexOf("function_code")==-1){
                if(!params){
                    params = {};
                }
                if(params&&!params.function_code&&typeof leaf_current_function_code!=="undefined"){
                    params.function_code = leaf_current_function_code;
                }
            }
            var headers = {};
            headers[header] = token;
            Ext.Ajax.request({
                url: url,
                headers:headers,
                params: params || {},
                success: this.onLoad.createDelegate(this)
            });
        },
        /*win内页面重新加载*/
        onFefreshClick: function (e) {
            if(!this.isLoading){
                var sf = this;
                if(!!sf.url){
                    if (sf.fullScreen) {
                        sf.width = A.getViewportWidth();
                        sf.height = A.getViewportHeight() - sf.titleheight;
                        sf.draggable = false;
                        sf.side = 'right';
                    } else if (sf.side) {
                        sf.draggable = false;
                        if (sf.side == 'top' || sf.side == 'bottom') {
                            sf.width = A.getViewportWidth() - 2;
                        } else if (sf.side == 'left' || sf.side == 'right') {
                            sf.height = A.getViewportHeight() - sf.titleheight;
                        }
                    }
                    sf.wrap.setWidth(sf.width);
                    sf.wrap.setHeight(sf.height);
                    sf.wrap.child(".win-content").setWidth(sf.width);
                    sf.wrap.child(".win-content").setHeight(sf.height);
                    sf.load(sf.url, sf.initConfig.params);
                }
            }
        },
        setChildzindex: function (z) {
            for (var key in this.cmps) {
                var c = this.cmps[key];
                c.setZindex(z)
            }
        },
        setWidth: function (w) {
            w = A.getViewportWidth();
            A.Window.superclass.setWidth.call(this, w);
            this.body.setWidth(w - 2);
        },
        setHeight: function (h) {
            var sf = this,
                scroll = Ext.getBody().getScroll(),
                sl = scroll.left,
                st = scroll.top;
            h = A.getViewportHeight() - 40;
            Ext.fly(sf.body.dom.parentNode.parentNode).setHeight(h);
            sf.body.setHeight(h);
            sf.wrap.moveTo(sl, st);
        },
        onLoad: function (response, options) {
            if (!this.body) return;
            this.clearLoading();
            this.isLoading = false;
            var html = response.responseText;
            var res
            try {
                res = Ext.decode(response.responseText);
            } catch (e) {
            }
            if (res && res.success == false) {
                if (res.error) {
                    if (res.error.code && res.error.code == 'session_expired' || res.error.code == 'login_required') {
                        if (A.manager.fireEvent('timeout', A.manager))
                            A.showErrorMessage(_lang['ajax.error'], _lang['session.expired']);
                    } else {
                        A.manager.fireEvent('ajaxfailed', A.manager, options.url, options.para, res);
                        var st = res.error.stackTrace;
                        st = (st) ? st.replaceAll('\r\n', '</br>') : '';
                        if (res.error.message) {
                            var h = (st == '') ? 150 : 250;
                            A.showErrorMessage(_lang['window.error'], res.error.message + '</br>' + st, null, 400, h);
                        } else {
                            A.showErrorMessage(_lang['window.error'], st, null, 400, 250);
                        }
                    }
                }
                return;
            }
            var sf = this
            this.body.update(html, true, function () {
// var cmps = A.CmpManager.getAll();
// for(var key in cmps){
// if(sf.oldcmps[key]==null){
// sf.cmps[key] = cmps[key];
// }
// }
                sf.fireEvent('load', sf)
            }, this.wrap);

            if (!Ext.isIE8) {
                if (!$jq(sf.body.dom).find("div").hasClass("navigationBarContent")) {
                    /*$jq(sf.body.dom).niceScroll();*/
                }
            }
        }
    });
    /**
     *
     * 显示提示信息窗口
     *
     * @param {String}
     *            title 标题
     * @param {String}
     *            msg 内容
     * @param {Function}
     *            callback 回调函数
     * @param {int}
     *            width 宽度
     * @param {int}
     *            height 高度
     * @return {Window} 窗口对象
     */
    A.showMessage = function (title, msg, callback, width, height) {
        return A.showTypeMessage(title, msg, width || 300, height || 120, 'win-info', callback);
    }
    /**
     * 显示带警告图标的窗口
     *
     * @param {String}
     *            title 标题
     * @param {String}
     *            msg 内容
     * @param {Function}
     *            callback 回调函数
     * @param {int}
     *            width 宽度
     * @param {int}
     *            height 高度
     * @return {Window} 窗口对象
     */
    A.showWarningMessage = function (title, msg, callback, width, height) {
        return A.showTypeMessage(title, msg, width || 300, height || 120, 'win-warning', callback);
    }
    /**
     * 显示带信息图标的窗口
     *
     * @param {String}
     *            title 标题
     * @param {String}
     *            msg 内容
     * @param {Function}
     *            callback 回调函数
     * @param {int}
     *            width 宽度
     * @param {int}
     *            height 高度
     * @return {Window} 窗口对象
     */
    A.showInfoMessage = function (title, msg, callback, width, height) {
        return A.showTypeMessage(title, msg, width || 300, height || 120, 'win-info', callback);
    }
    /**
     * 显示带错误图标的窗口
     *
     * @param {String}
     *            title 标题
     * @param {String}
     *            msg 内容
     * @param {Function}
     *            callback 回调函数
     * @param {int}
     *            width 宽度
     * @param {int}
     *            height 高度
     * @return {Window} 窗口对象
     */
    A.showErrorMessage = function (title, msg, callback, width, height) {
        return A.showTypeMessage(title, msg, width || 300, height || 120, 'win-error', callback);
    }
    /**
     * 显示带成功图标的窗口
     *
     * @param {String}
     *            title 标题
     * @param {String}
     *            msg 内容
     * @param {Function}
     *            callback 回调函数
     * @param {int}
     *            width 宽度
     * @param {int}
     *            height 高度
     * @return {Window} 窗口对象
     */
    A.showSuccessMessage = function (title, msg, callback, width, height) {
        return A.showTypeMessage(title, msg, width || 300, height || 120, 'win-success', callback);
    }

    A.showTypeMessage = function (title, msg, width, height, css, callback) {
        var iconCss;
        if (css == 'win-info') {
            iconCss = "title-info"
        } else if (css == 'win-warning') {
            iconCss = "title-warning"
        } else if (css == 'win-error') {
            iconCss = "title-error"
        } else if (css == 'win-success') {
            iconCss = "title-success"
        } else {
            iconCss = "title-question"
        }

        title = '<div class="title-icon ' + iconCss + '"></div>' + '<p style="float: left;margin: 0">' + title + '</p>';
        var cutObj = A.cutTextLength({
            lines:2,
            str:msg,
            width:(width>433?width:433) - 82
        });
        msg = cutObj.cutStr;
        if(cutObj.cutFlag){
            var msg = '<div class="win-icon ' + css + '"><div class="win-type" title="'+cutObj.str+'" style="max-height:' + (height - 55) + 'px;min-height: 60px">' + msg + '</div></div>';
        }else{
        var msg = '<div class="win-icon ' + css + '"><div class="win-type" style="max-height:' + (height - 55) + 'px;min-height: 60px">' + msg + '</div></div>';
        }
        return A.showOkWindow(title, msg, width, height, callback);
    }
    /**
     * 带图标的确定窗口.
     *
     * @param {String}
     *            title 标题
     * @param {String}
     *            msg 内容
     * @param {Function}
     *            okfun 确定的callback
     * @param {Function}
     *            cancelfun 取消的callback
     * @param {int}
     *            width 宽度
     * @param {int}
     *            height 高度
     * @return {Window} 窗口对象
     */
    A.showConfirm = function (title, msg, okfun, cancelfun, width, height) {
        return A.showOkCancelWindow(title, msg, okfun, cancelfun, width, height);
    }
// A.hideWindow = function(){
// var cmp = A.CmpManager.get('leaf-msg')
// if(cmp) cmp.close();
// }
// A.showWindow = function(title, msg, width, height, cls){
// cls = cls ||'';
// var cmp = A.CmpManager.get('leaf-msg')
// if(cmp == null) {
// cmp = new A.Window({id:'leaf-msg',title:title, height:height,width:width});
// if(msg){
// cmp.body.update('<div class="'+cls+'"
// style="height:'+(height-68)+'px;">'+msg+'</div>');
// }
// }
// return cmp;
// }
    /**
     * 带确定取消按钮的窗口.
     *
     * @param {String}
     *            title 标题
     * @param {String}
     *            msg 内容
     * @param {Function}
     *            okfun 确定的callback
     * @param {Function}
     *            cancelfun 取消的callback
     * @param {int}
     *            width 宽度
     * @param {int}
     *            height 高度
     * @return {Window} 窗口对象
     */
    A.showOkCancelWindow = function (title, msg, okfun, cancelfun, width, height) {
        // var cmp = A.CmpManager.get('leaf-msg-ok-cancel')
        // if(cmp == null) {
        /*
         * 判断传过来的width是否大于400，是就用传过来的值作为宽度，否则使用默认宽度
         * 向平  2017-11-29
         */
        if (width > 433) {
            width = width;
        } else {
            width = 433;
        }
        /* width = width||400;
         height = height||120;*/
        var id = Ext.id(), okid = 'leaf-msg-ok' + id, cancelid = 'leaf-msg-cancel' + id,
            cancelbtnhtml = A.Button.getTemplate(cancelid, _lang['window.button.cancel']),
            okbtnhtml = A.Button.getTemplate(okid, _lang['window.button.ok']),
            cmp = new A.Window({
                id: 'leaf-msg-ok-cancel' + id,
                closeable: true,
                title: '<div class="title-icon title-question"></div>' + '<p style="float: left;margin: 0">' + title + '</p>',
                height: height || 239,
                width: width || 433,
                isDialog: true,
                refreshable:false
            });
        if (!Ext.isEmpty(msg, true)) {
            msg = '<div class="win-icon win-question"><div class="win-type" style="width:' + (width - 70) + 'px;max-height:' + (height - 55) + 'px;min-height: 60px">' + msg + '</div></div>';
            cmp.body.update(msg + '<center style="float:right;padding:5px;padding-bottom: 24px;padding-right: 28px"><table cellspacing="5"><tr><td style="padding-left:5px;padding-right:5px;">' + okbtnhtml + '</td><td style="padding-left:5px;padding-right:5px;">' + cancelbtnhtml + '</td></tr></table></center>', true, function () {
                var cancelbtn = $le(cancelid);
                var okbtn = $le(okid);
                cmp.cmps[okid] = okbtn;
                cmp.cmps[cancelid] = cancelbtn;
                okbtn.on('click', function () {
                    if (okfun && okfun.call(this, cmp) === false)return;
                    cmp.close();
                });
                cancelbtn.on('click', function () {
                    if (cancelfun && cancelfun.call(this, cmp) === false)return;
                    cmp.close();
                });
                okbtn.focus.defer(10, okbtn);
            });
        }
        // }
        return cmp;
    }
    A.showYesNoCancelWindow = function (title, msg, yesfun, nofun, width, height) {
        // var cmp = A.CmpManager.get('leaf-msg-ok-cancel')
        // if(cmp == null) {
        width = width || 433;
        height = height || 159;
        var id = Ext.id(),
            yesid = 'leaf-msg-yes' + id,
            noid = 'leaf-msg-no' + id,
            cancelid = 'leaf-msg-cancel' + id,
            yesbtnhtml = A.Button.getTemplate(yesid, _lang['window.button.yes']),
            nobtnhtml = A.Button.getTemplate(noid, _lang['window.button.no']),
            cancelbtnhtml = A.Button.getTemplate(cancelid, _lang['window.button.cancel']),
            cmp = new A.Window({
                id: 'leaf-msg-yes-no-cancel' + id,
                closeable: true,
                title: '<div class="title-icon title-question"></div>' + '<p style="float: left;margin: 0">' + title + '</p>',
                height: height || 239,
                width: width || 433,
                refreshable:false
            });
        if (!Ext.isEmpty(msg, true)) {
            msg = '<div class="win-icon win-question"><div class="win-type" style="width:' + (width - 70) + 'px;max-height:' + (height - 55) + 'px;min-height: 60px;margin-top:30px;">' + msg + '</div></div>';
            cmp.body.update(msg + '<center style="padding:5px;padding-bottom: 24px;padding-left: 100px"><table cellspacing="5"><tr><td style="padding-left:5px;padding-right:5px;">' + yesbtnhtml + '</td><td style="padding-left:5px;padding-right:5px;">' + nobtnhtml + '</td><td>' + cancelbtnhtml + '</td></tr></table></center>', true, function () {
                var yesbtn = $le(yesid),
                    nobtn = $le(noid),
                    cancelbtn = $le(cancelid);
                cmp.cmps[yesid] = yesbtn;
                cmp.cmps[noid] = nobtn;
                cmp.cmps[cancelid] = cancelbtn;
                yesbtn.on('click', function () {
                    if (yesfun && yesfun.call(this, cmp) === false)return;
                    cmp.close();
                });
                nobtn.on('click', function () {
                    if (nofun && nofun.call(this, cmp) === false)return;
                    cmp.close();
                });
                cancelbtn.on('click', function () {
                    cmp.close();
                });
                yesbtn.focus.defer(10, yesbtn);
            });
        }
        // }
        return cmp;
    }
    /**
     * 带确定按钮的窗口.
     *
     * @param {String}
     *            title 标题
     * @param {String}
     *            msg 内容
     * @param {Function}
     *            okfun 确定的callback
     * @param {Function}
     *            cancelfun 取消的callback
     * @param {int}
     *            width 宽度
     * @param {int}
     *            height 高度
     * @return {Window} 窗口对象
     */
    A.showOkWindow = function (title, msg, width, height, callback) {
        // var cmp = A.CmpManager.get('leaf-msg-ok');
        // if(cmp == null) {
        if (width > 433) {
            width = width;
        } else {
            width = 433;
        }
        var id = Ext.id(), yesid = 'leaf-msg-yes' + id,
            btnhtml = A.Button.getTemplate(yesid, _lang['window.button.ok']),
            cmp = new A.Window({
                id: 'leaf-msg-ok' + id,
                closeable: true,
                title: title,
                height: height || 239,
                width: width || 433,
                isDialog: true,
                refreshable:false
            });
        if (!Ext.isEmpty(msg, true)) {
            msg = '<div class="win-icon win-question"><div class="win-type" style="width:' + (width - 70) + 'px;max-height:' + (height - 55) + 'px;min-height: 60px">' + msg + '</div></div>';
            cmp.body.update(msg + '<center style="padding:12px;float:right;margin-bottom: 24px">' + btnhtml + '</center>', true, function () {
                var btn = $le(yesid);
                cmp.cmps[yesid] = btn;
                btn.on('click', function () {
                    if (callback && callback.call(this, cmp) === false)return;
                    cmp.close();
                });
                // btn.focus();
                btn.focus.defer(10, btn);
            });
        }
        // }
        return cmp;
    }
    A.cutTextLength = function(opts){
        var lines = opts.lines;
        var str = opts.str;
        var width = opts.width || 200;
        var maxLength = width* lines;
        var temp_str = "";
        var flag = false;
        var temp_span = $jq('<span style="display:none;font-size:14px;"></span>').appendTo($jq("body"));
        for(var i = 0; i < str.length; i++){
            temp_str = str.substring(0,i+1);
            temp_span.html(temp_str);
            if(temp_span.width() >= maxLength){
                flag = true;
                temp_str = temp_str.substring(0,temp_str.length-3);
                temp_str = temp_str + "...";
                break;
            }
        }
        temp_span.remove();
        return {
            cutStr:temp_str,
            str:str,
            cutFlag:flag
        };
    }
    /**
     * 上传附件窗口.
     *
     * @param {String}
     *            path 当前的context路径
     * @param {String}
     *            title 上传窗口标题
     * @param {int}
     *            pkvalue pkvalue
     * @param {String}
     *            source_type source_type
     * @param {int}
     *            max_size 最大上传大小(单位kb) 0表示不限制
     * @param {String}
     *            file_type 上传类型(*.doc,*.jpg)
     * @param {String}
     *            callback 回调函数的名字
     */
    A.showUploadWindow = function (path, title, source_type, pkvalue, max_size, file_type, callback) {
        new Leaf.Window({
            id: 'upload_window',
            url: path + '/upload.lview?callback=' + callback + '&pkvalue=' + pkvalue + '&source_type=' + source_type + '&max_size=' + (max_size || 0) + '&file_type=' + (file_type || '*.*'),
            title: title || _lang['window.upload.title'],
            height: 330,
            width: 595
        });
    };
})($L);
(function (A) {
    var CH_REG = /[^\x00-\xff]/g,
        _N = '',
        TR$TABINDEX = 'tr[tabindex]',
        DIV$ITEM_RECEIVER_INFO = 'div.item-receiver-info',
        SYMBOL = ';',
        SELECTED_CLS = 'autocomplete-selected',
        EVT_MOUSE_MOVE = 'mousemove',
        EVT_COMMIT = 'commit';
    /**
     * @class Leaf.MultiTextField
     * @extends Leaf.TextField
     *          <p>
     *          多文本输入组件.
     * @author huazhen.wu@hand-china.com
     * @constructor
     * @param {Object}
     *            config 配置对象.
     */
    A.MultiTextField = Ext.extend(A.TextField, {
        infoTpl: ['<div class="item-receiver-info" _data="{data}" style="float:left;margin-right:10px;"><span class="item-receiver-info-name">{text};</span></div>'],
        processListener: function (ou) {
            var sf = this;
            A.MultiTextField.superclass.processListener.call(sf, ou);
            sf.wrap[ou]('mousedown', sf.onWrapFocus, sf, {preventDefault: true})
                [ou]('mouseup', sf.onWrapClick, sf);
        },
        initEvents: function () {
            A.MultiTextField.superclass.initEvents.call(this);
            this.addEvents(
                /**
                 * @event commit commit事件.
                 * @param {Leaf.MultiTextField}
                 *            multiTextField 当前MultiTextField组件.
                 * @param {Leaf.Record}
                 *            r1 当前MultiTextField绑定的Record
                 * @param {Leaf.Record}
                 *            r2 选中的Record.
                 */
                EVT_COMMIT
            );
        },
        onWrapClick: function (e, t) {
            t = Ext.fly(t);
            if (t.hasClass('item-receiver-info-close')) {
                this.removeItem(t.parent(DIV$ITEM_RECEIVER_INFO));
            }
        },
        onWrapFocus: function (e, t) {
            var sf = this;
            e.stopEvent();
            if (Ext.isIE && t !== sf.el.dom) sf.hasFocus = false;
            sf.focus.defer(Ext.isIE ? 1 : 0, sf);
        },
        onBlur: function () {
            var sf = this, view = sf.autocompleteview;
            if (sf.hasFocus) {
                if (Ext.isIE && sf.hasChange) {// for IE
                    sf.fetchRecord();
                    sf.hasChange = false;
                } else if (!sf.fetching && ( !view || !view.isShow)) {
                    A.MultiTextField.superclass.onBlur.call(sf);
                }
                sf.hasFocus = false;
                sf.wrap.removeClass(sf.focusCss);
            }
        },
        onChange: function () {
            var sf = this, value = sf.getRawValue(),
                view = sf.autocompleteview;
            A.MultiTextField.superclass.onChange.call(sf);
            if (!view || !view.isShow)
                if (sf.hasFocus) {
                    sf.fetchRecord();
                } else if (Ext.isIE) {
                    sf.hasChange = true;// for IE
                }
        },
        processValue: function (v) {
            if (this.binder) {
                var name = this.binder.name, arr = [];
                Ext.each(this.items, function (item) {
                    arr.push(item[name]);
                });
                return arr.join(SYMBOL);
            } else {
                return v;
            }
        },
        formatValue: function (v) {
            var sf = this, v, r = sf.record, binder = sf.binder, name, mapTos = [];
            sf.clearAllItems();
            if (r && !Ext.isEmpty(v = r.get(name = sf.binder.name))) {
                Ext.each(sf.getMapping(), function (map) {
                    var to = map.to, toValue = String(r.get(to));
                    if (name != to) {
                        mapTos.push({name: to, values: Ext.isEmpty(toValue) ? [] : toValue.split(SYMBOL)});
                    }
                })
                Ext.each(String(v).split(SYMBOL), function (item, index) {
                    var obj = {};
                    Ext.each(mapTos, function (mapTo) {
                        obj[mapTo.name] = mapTo.values[index];
                    });
                    obj[name] = item;
                    sf.items.push(obj);
                    sf.addItem(A.MultiTextField.superclass.formatValue.call(sf, item)).item = obj;
                });
            }
            return _N;
        },
        onKeyDown: function (e) {
            var sf = this, value = sf.getRawValue(), length = sf.getValueLength(value);
            if (e.keyCode === 8) {
                if (value === _N) {
                    sf.removeItem(sf.el.prev());
                } else {
                    sf.setSize(length - 1);
                }
            } else if (e.keyCode === 186) {
                sf.fetchRecord();
                e.stopEvent();
            } else
                sf.setSize(length + 1);
            A.MultiTextField.superclass.onKeyDown.call(sf, e);
        },
        getValueLength: function (str) {
            var c = 0, i = 0, cLength = A.defaultChineseLength;
            for (; i < str.length; i++) {
                var cl = str.charAt(i).match(CH_REG);
                c += cl != null && cl.length > 0 ? cLength : 1;
            }
            return c;
        },
        onKeyUp: function () {
            this.setSize(this.getValueLength(this.getRawValue()));
        },
        onViewSelect: function (r) {
            var sf = this;
            if (!r) {
                if (sf.autocompleteview.isLoaded)
                    sf.fetchRecord();
            } else {
                sf.commit(r);
            }
            sf.focus();
        },
        commit: function (r, lr, mapping) {
            var sf = this, record = lr || sf.record, name = sf.binder.name, obj = {};
            if (record && r) {
                Ext.each(mapping || sf.getMapping(), function (map) {
                    var from = r.get(map.from),
                        v = record.get(map.to);
                    if (!Ext.isEmpty(from)) {
                        obj[map.to] = from;
                        if (!Ext.isEmpty(v)) {
                            from = v + SYMBOL + from;
                        }
                    } else {
                        from = v;
                    }
                    record.set(map.to, from);
                });
            }
            sf.win.close();
            sf.fireEvent(EVT_COMMIT, sf, record, r)
        },
        setSize: function (size) {
            this.el.set({size: size + 2 || 1});
        },
        addItem: function (text, noCloseBtn) {
            if (text) {
                var sf = this,
                    tpl = sf.infoTpl;
                sf.setSize(1);
                return new Ext.Template(noCloseBtn ? tpl[0] + tpl[2] : tpl).insertBefore(sf.el, {
                    text: text,
                    data: text
                }, true);
            }
        },
        removeItem: function (t) {
            if (t) {
                var sf = this, r = sf.record;
                Ext.each(sf.getMapping(), function (map) {
                    var arr = [];
                    Ext.each(sf.items.remove(t.item), function (item) {
                        arr.push(item[map.to]);
                    });
                    r.set(map.to, arr.join(SYMBOL));
                });
            }
        },
        clearAllItems: function () {
            this.items = [];
            this.wrap.select(DIV$ITEM_RECEIVER_INFO).remove();
        },
        fetchRecord: function () {
            if (this.readonly || !this.binder)return;
            var sf = this,
                binder = sf.binder,
                v = sf.getRawValue(),
                record = sf.record,
                name = binder.name;
            if (sf.fetchremote) {
                sf.fetching = true;
                var url,
                    svc = sf.service,
                    mapping = sf.getMapping(),
                    p = {},
                    sidebar = A.SideBar,
                    autocompletefield = sf.autocompletefield;
                if (!Ext.isEmpty(svc)) {
                    url = Ext.urlAppend(sf.context + 'autocrud/' + svc + '/query?pagenum=1&_fetchall=false&_autocount=false', Ext.urlEncode(sf.getPara()));
                }
                if (record == null && binder)
                    record = binder.ds.create({}, false);
                record.isReady = false;
                if (autocompletefield) {
                    p[autocompletefield] = v;
                } else {
                    Ext.each(mapping, function (map) {
                        if (name == map.to) {
                            p[map.from] = v;
                        }
                    });
                }
                A.slideBarEnable = sidebar.enable;
                sidebar.enable = false;
                if (Ext.isEmpty(v) || Ext.isEmpty(svc)) {
                    sf.fetching = false;
                    record.isReady = true;
                    sidebar.enable = A.slideBarEnable;
                    return;
                }
                sf.setRawValue(_N);
                var info = sf.addItem(_lang['lov.query'], true);
                sf.qtId = A.request({
                    url: url, para: p, success: function (res) {
                        var r = new A.Record({});
                        if (res.result.record) {
                            var datas = [].concat(res.result.record), l = datas.length;
                            if (l > 0) {
                                if (sf.fetchsingle && l > 1) {
                                    var sb = sf.createListView(datas, binder).join(_N),
                                        div = new Ext.Template('<div style="position:absolute;left:0;top:0">{sb}</div>').append(document.body, {'sb': sb}, true),
                                        cmp = sf.fetchSingleWindow = new A.Window({
                                            id: sf.id + '_fetchmulti',
                                            closeable: true,
                                            title: '请选择',
                                            height: Math.min(div.getHeight(), sf.maxHeight),
                                            width: Math.max(div.getWidth(), 200),
                                            refreshable:false
                                        });
                                    div.remove();
                                    cmp.body.update(sb)
                                        .on(EVT_MOUSE_MOVE, sf.onViewMove, sf)
                                        .on('dblclick', function (e, t) {
                                            t = Ext.fly(t).parent(TR$TABINDEX);
                                            var index = t.dom.tabIndex;
                                            if (index < -1)return;
                                            var r2 = new A.Record(datas[index]);
                                            sf.commit(r2, record, mapping);
                                            cmp.close();
                                        })
                                        .child('table').setWidth('100%');
                                } else {
                                    r = new A.Record(datas[0]);
                                }
                            }
                        }
                        sf.fetching = false;
                        info.remove();
                        sf.commit(r, record, mapping);
                        record.isReady = true;
                        sidebar.enable = A.slideBarEnable;
                    }, error: sf.onFetchFailed, scope: sf
                });
            } else {
                var v2 = record.get(name);
                record.set(name, Ext.isEmpty(v2) ? v : v2 + SYMBOL + v);
            }
        },
        createListView: function (datas, binder, isRecord) {
            var sb = ['<table class="autocomplete" cellspacing="0" cellpadding="2">'],
                displayFields = binder.ds.getField(binder.name).getPropertity('displayFields');
            if (displayFields && displayFields.length) {
                sb.push('<tr tabIndex="-2" class="autocomplete-head">');
                Ext.each(displayFields, function (field) {
                    sb.push('<td>', field.prompt, '</td>');
                });
                sb.push('</tr>');
            }
            for (var i = 0, l = datas.length; i < l; i++) {
                var d = datas[i];
                sb.push('<tr tabIndex="', i, '"', i % 2 == 1 ? ' class="autocomplete-row-alt"' : _N, '>', this.getRenderText(isRecord ? d : new A.Record(d), displayFields), '</tr>');	// sf.litp.applyTemplate(d)等数据源明确以后再修改
            }
            sb.push('</table>');
            return sb;
        },
        getRenderText: function (record, displayFields) {
            var sf = this,
                rder = A.getRenderer(sf.autocompleterenderer),
                text = [],
                fn = function (t) {
                    var v = record.get(t);
                    text.push('<td>', Ext.isEmpty(v) ? '&#160;' : v, '</td>');
                };
            if (rder) {
                text.push(rder(sf, record));
            } else if (displayFields) {
                Ext.each(displayFields, function (field) {
                    fn(field.name);
                });
            } else {
                fn(sf.autocompletefield)
            }
            return text.join(_N);
        },
        onViewMove: function (e, t) {
            this.selectItem((Ext.fly(t).findParent(TR$TABINDEX) || t).tabIndex);
        },
        selectItem: function (index) {
            if (Ext.isEmpty(index) || index < -1) {
                return;
            }
            var sf = this, node = sf.getNode(index), selectedIndex = sf.selectedIndex;
            if (node && node.tabIndex != selectedIndex) {
                if (!Ext.isEmpty(selectedIndex)) {
                    Ext.fly(sf.getNode(selectedIndex)).removeClass(SELECTED_CLS);
                }
                sf.selectedIndex = node.tabIndex;
                Ext.fly(node).addClass(SELECTED_CLS);
            }
        },
        getNode: function (index) {
            var nodes = this.fetchSingleWindow.body.query('tr[tabindex!=-2]'), l = nodes.length;
            if (index >= l) index = index % l;
            else if (index < 0) index = l + index % l;
            return nodes[index];
        },
        select: function () {
        }
    });
})($L);
(function (A) {
    var _N = '',
        TR$TABINDEX = 'tr[tabindex]',
        WIDTH = 'width',
        PX = 'px',
        SELECTED_CLS = 'autocomplete-selected',
        EVT_CLICK = 'click',
        EVT_MOUSE_MOVE = 'mousemove',
        EVT_BEFORE_COMMIT = 'beforecommit',
        EVT_COMMIT = 'commit',
        EVT_BEFORE_TRIGGER_CLICK = 'beforetriggerclick',
        EVT_FETCHING = 'fetching',
        EVT_FETCHED = 'fetched';

    /**
     * @class Leaf.Lov
     * @extends Leaf.TextField
     *          <p>
     *          Lov 值列表组件.
     * @author njq.niu@hand-china.com
     * @constructor
     * @param {Object}
     *            config 配置对象.
     */
    A.Lov = Ext.extend(A.TextField, {
        constructor: function (config) {
            var sf = this;
            sf.isWinOpen = false;
            sf.fetching = false;
            sf.fetchremote = true;
            sf.maxHeight = 240;
            A.Lov.superclass.constructor.call(sf, config);
        },
        initComponent: function (config) {
            var sf = this;
            A.Lov.superclass.initComponent.call(this, config);
// lovservice = sf.lovservice,
// lovmodel = sf.lovmodel,
// autocomplete = sf.autocomplete;
// field = sf.autocompletefield,
// view = sf.autocompleteview;
// if(!Ext.isEmpty(lovservice)){
// svc = sf.lovservice = sf.processParmater(lovservice);
// }else if(!Ext.isEmpty(lovmodel)){
// svc = sf.lovmodel = sf.processParmater(lovmodel);
// }
// if(sf.autocomplete && svc){
// if(!field){
// Ext.each(sf.getMapping(),function(map){
// if(map.to == sf.binder.name) field = sf.autocompletefield = map.from;
// });
// }
// if(view){
// view.destroy();
// view.un('select',sf.onViewSelect,sf);
// }
// view = sf.autocompleteview = new A.AutoCompleteView({
// id:sf.id,
// el:sf.el,
// url:sf.context + 'autocrud/'+svc+'/query',
// name:field,
// size:sf.autocompletesize,
// pagesize:sf.autocompletepagesize,
// renderer:sf.autocompleterenderer,
// binder : sf.binder
// });
// view.bind(sf);
// view.on('select',sf.onViewSelect,sf);
// }
            sf.trigger = sf.wrap.child('div[atype=triggerfield.trigger]');
        },
        processParmater: function (url) {
            var li = url.indexOf('?')
            if (li != -1) {
                this.para = Ext.urlDecode(url.substring(li + 1, url.length));
                return url.substring(0, li);
            }
            return url;
        },
        processListener: function (ou) {
            var sf = this, view = sf.autocompleteview;
            A.Lov.superclass.processListener.call(sf, ou);
            sf.trigger[ou]('mousedown', sf.onWrapFocus, sf, {preventDefault: true})
                [ou](EVT_CLICK, sf.onTriggerClick, sf, {preventDefault: true});
        },
        initEvents: function () {
            A.Lov.superclass.initEvents.call(this);
            this.addEvents(
                /**
                 * @event beforecommit commit之前事件.
                 * @param {Leaf.Lov}
                 *            lov 当前Lov组件.
                 * @param {Leaf.Record}
                 *            r1 当前lov绑定的Record
                 * @param {Leaf.Record}
                 *            r2 选中的Record.
                 */
                EVT_BEFORE_COMMIT,
                /**
                 * @event commit commit事件.
                 * @param {Leaf.Lov}
                 *            lov 当前Lov组件.
                 * @param {Leaf.Record}
                 *            r1 当前lov绑定的Record
                 * @param {Leaf.Record}
                 *            r2 选中的Record.
                 */
                EVT_COMMIT,
                /**
                 * @event beforetriggerclick 点击弹出框按钮之前的事件。
                 * @param {Leaf.Lov}
                 *            lov 当前Lov组件.
                 */
                EVT_BEFORE_TRIGGER_CLICK,
                /**
                 * @event fetching 正在获取记录的事件
                 * @param {Leaf.Lov}
                 *            lov 当前Lov组件.
                 */
                EVT_FETCHING,
                /**
                 * @event fetched 获得记录的事件
                 * @param {Leaf.Lov}
                 *            lov 当前Lov组件.
                 */
                EVT_FETCHED);
        },
        onWrapFocus: function (e, t) {
            var sf = this;
            e.stopEvent();
            sf.focus.defer(Ext.isIE ? 1 : 0, sf);
        },
        onTriggerClick: function (e) {
            e.stopEvent();
            var sf = this, view = sf.autocompleteview;
            if (sf.fireEvent(EVT_BEFORE_TRIGGER_CLICK, sf)) {
                sf.showLovWindow();
            }
        },
        destroy: function () {
            var sf = this;
            if (sf.qtId) {
                Ext.Ajax.abort(sf.qtId);
            }
            A.Lov.superclass.destroy.call(sf);
        },
        clearBind: function () {
            var sf = this;
            A.Lov.superclass.clearBind.call(sf);
            sf.lovurl = null;
            sf.service = null;
            sf.autocompleteservice = null
            sf.lovservice = null;
            sf.lovmodel = null;
        },
        setWidth: function (w) {
            this.wrap.setStyle(WIDTH, (w + 3) + PX);
// this.el.setStyle(WIDTH,(w-20)+PX);
        },
        onBlur: function () {
            var sf = this, view = sf.autocompleteview;
            if (!view || !view.isShow) {
                $L.Lov.superclass.onBlur.call(sf);
            }
        },
        onChange: function (e) {
            var sf = this, view = sf.autocompleteview;
            A.Lov.superclass.onChange.call(sf);
            if (!view || !view.isShow)
                sf.fetchRecord();

        },
        onKeyDown: function (e) {
            if (this.isWinOpen)return;
            var sf = this, keyCode = e.keyCode,
                view = sf.autocompleteview;
            if (!view || !view.isShow) {
                if (!e.ctrlKey && keyCode == 40) {
                    e.stopEvent();
                    sf.showLovWindow();
                }
                A.Lov.superclass.onKeyDown.call(sf, e);
            }
        },
        onViewSelect: function (r) {
            var sf = this;
            if (!r) {
                if (sf.autocompleteview.isLoaded)
                    sf.fetchRecord();
            } else {
                sf.setValue('');
                sf.commit(r);
            }
            sf.focus();
        },
        createListView: function (datas, binder, isRecord) {
            var sb = ['<table class="autocomplete" cellspacing="0" cellpadding="2">'],
                displayFields = binder.ds.getField(binder.name).getPropertity('displayFields');
            if (displayFields && displayFields.length) {
                sb.push('<tr tabIndex="-2" class="autocomplete-head">');
                Ext.each(displayFields, function (field) {
                    sb.push('<td>', field.prompt, '</td>');
                });
                sb.push('</tr>');
            }
            for (var i = 0, l = datas.length; i < l; i++) {
                var d = datas[i];
                sb.push('<tr tabIndex="', i, '"', i % 2 == 1 ? ' class="autocomplete-row-alt"' : _N, '>', this.getRenderText(isRecord ? d : new $L.Record(d), displayFields), '</tr>');	// sf.litp.applyTemplate(d)等数据源明确以后再修改
            }
            sb.push('</table>');
            return sb;
        },
        getRenderText: function (record, displayFields) {
            var sf = this,
                rder = A.getRenderer(sf.autocompleterenderer),
                text = [],
                fn = function (t) {
                    var v = record.get(t);
                    text.push('<td>', Ext.isEmpty(v) ? '&#160;' : v, '</td>');
                };
            if (rder) {
                text.push(rder(sf, record));
            } else if (displayFields) {
                Ext.each(displayFields, function (field) {
                    fn(field.name);
                });
            } else {
                fn(sf.autocompletefield)
            }
            return text.join(_N);
        },
        canHide: function () {
            return this.isWinOpen == false;
        },
        commit: function (r, lr, mapping) {
            var sf = this, record = lr || sf.record;
            if (sf.fireEvent(EVT_BEFORE_COMMIT, sf, record, r) !== false) {
                if (sf.win) sf.win.close();
// sf.setRawValue(_N)

                if (record && r) {
                    Ext.each(mapping || sf.getMapping(), function (map) {
                        var from = r.get(map.from);
                        record.set(map.to, Ext.isEmpty(from) ? _N : from);
                    });
                }
// else{
// sf.setValue()
// }

                sf.fireEvent(EVT_COMMIT, sf, record, r)
            }
        },
// setValue: function(v, silent){
// A.Lov.superclass.setValue.call(this, v, silent);
// if(this.record && this.dataRecord && silent !== true){
// var mapping = this.getMapping();
// for(var i=0;i<mapping.length;i++){
// var map = mapping[i];
// this.record.set(map.to,this.dataRecord.get(map.from));
// }
// }
// },
        onWinClose: function () {
            var sf = this;
            sf.isWinOpen = false;
            sf.win = null;
            if (!Ext.isIE6 && !Ext.isIE7) {// TODO:不知什么地方会导致冲突,ie6 ie7 会死掉
                sf.focus();
            } else {
                (function () {
                    sf.focus()
                }).defer(10);
            }
        },
        getLovPara: function () {
            return this.getPara();
        },
        fetchRecord: function () {
            var sf = this;
            if (sf.readonly == true || !sf.fetchremote) return;
            sf.fetching = true;
            var v = sf.getRawValue(), url,
                svc = sf.service,
                mapping = sf.getMapping(),
                record = sf.record, p = {},
                binder = sf.binder,
                sidebar = A.SideBar,
                autocompletefield = sf.autocompletefield;
            if (!Ext.isEmpty(v) && sf.fuzzyfetch) {
                v += '%';
            }
            if (!Ext.isEmpty(svc)) {
// url = sf.context +
// 'sys_lov.lsc?svc='+sf.lovservice+'&pagesize=1&pagenum=1&_fetchall=false&_autocount=false&'+
// Ext.urlEncode(sf.getLovPara());
                var autoUrl = svc;
                if(autoUrl&&autoUrl.indexOf('~')!=-1){
                    autoUrl = svc.split("~");
                    autoUrl = autoUrl[0];
                    autoUrl = sf.context + autoUrl.substring(1)+'?pagenum=1&_fetchall=false&_autocount=false';
                }else{
                    autoUrl = sf.context + 'autocrud/' + svc + '/query?pagenum=1&_fetchall=false&_autocount=false';
                }
                url = Ext.urlAppend(autoUrl, Ext.urlEncode(sf.getLovPara()));
            }
            if (record == null && binder)
                record = binder.ds.create({}, false);
            record.isReady = false;
            if (autocompletefield) {
                p[autocompletefield] = v;
                Ext.each(mapping, function (map) {
                    record.set(map.to, _N);
                });
            } else {
                Ext.each(mapping, function (map) {
                    if (binder.name == map.to) {
                        p[map.from] = v;
                    }
                    record.set(map.to, _N);
                });
            }
            A.slideBarEnable = sidebar.enable;
            sidebar.enable = false;
            if (Ext.isEmpty(v) || Ext.isEmpty(svc)) {
                sf.fetching = false;
                record.isReady = true;
                sidebar.enable = A.slideBarEnable;
                return;
            }
            $L.Masker.mask(sf.wrap, _lang['lov.query']);
// sf.setRawValue(_lang['lov.query'])
            sf.fireEvent(EVT_FETCHING, sf);
            sf.qtId = A.request({
                url: url, para: p, success: function (res) {
                    var r = new A.Record({});
                    if (res.result.record) {
                        var datas = [].concat(res.result.record), l = datas.length;
                        if (l > 0) {
                            if (sf.fetchsingle && l > 1) {
                                var sb = sf.createListView(datas, binder).join(_N),
                                    div = new Ext.Template('<div style="position:absolute;left:0;top:0">{sb}</div>').append(document.body, {'sb': sb}, true),
                                    xy = sf.wrap.getXY(),
                                    cmp = sf.fetchSingleWindow = new A.Window({
                                        id: sf.id + '_fetchmulti',
                                        closeable: true,
                                        title: '请选择',
                                        height: Math.min(div.getHeight(), sf.maxHeight),
                                        width: Math.max(div.getWidth(), 200),
                                        x: xy[0],
                                        y: xy[1] + sf.wrap.getHeight(),
                                        refreshable:false
                                    });
                                div.remove();
                                cmp.on('close', function () {
                                    sf.focus();
                                });
                                cmp.body.update(sb)
                                    .on(EVT_MOUSE_MOVE, sf.onViewMove, sf)
                                    .on('dblclick', function (e, t) {
                                        t = Ext.fly(t).parent(TR$TABINDEX);
                                        var index = t.dom.tabIndex;
                                        if (index < -1)return;
                                        var r2 = new A.Record(datas[index]);
                                        sf.commit(r2, record, mapping);
                                        cmp.close();
                                    })
                                    .child('table').setWidth('100%');
                            } else {
                                r = new A.Record(datas[0]);
                            }
                        }
                    }
                    sf.fetching = false;
                    $L.Masker.unmask(sf.wrap);
// sf.setRawValue(_N);
                    sf.commit(r, record, mapping);
                    record.isReady = true;
                    sidebar.enable = A.slideBarEnable;
                    sf.fireEvent(EVT_FETCHED, sf);
                }, error: sf.onFetchFailed, scope: sf
            });
        },
        onViewMove: function (e, t) {
            this.selectItem((Ext.fly(t).findParent(TR$TABINDEX) || t).tabIndex);
        },
        selectItem: function (index) {
            if (Ext.isEmpty(index) || index < -1) {
                return;
            }
            var sf = this, node = sf.getNode(index), selectedIndex = sf.selectedIndex;
            if (node && node.tabIndex != selectedIndex) {
                if (!Ext.isEmpty(selectedIndex)) {
                    Ext.fly(sf.getNode(selectedIndex)).removeClass(SELECTED_CLS);
                }
                sf.selectedIndex = node.tabIndex;
                Ext.fly(node).addClass(SELECTED_CLS);
            }
        },
        getNode: function (index) {
            var nodes = this.fetchSingleWindow.body.query('tr[tabindex!=-2]'), l = nodes.length;
            if (index >= l) index = index % l;
            else if (index < 0) index = l + index % l;
            return nodes[index];
        },
        onFetchFailed: function (res) {
            var sf = this;
            sf.fetching = false;
            A.SideBar.enable = A.slideBarEnable;
            sf.fireEvent(EVT_FETCHED, sf);
        },
        showLovWindow: function () {
            var sf = this;
            if (sf.fetching || sf.isWinOpen || sf.readonly) return;

            var v = sf.getRawValue(),
                lovurl = sf.lovurl,
                svc = sf.service, ctx = sf.context,
                w = Math.max(sf.lovwidth || 515, 515),
                url;
            sf.blur();
            var h = 505,girdH=355;
            if($L.getViewportHeight()<505){
                h = 435;
                girdH=275;
                }
            if (sf.lovheight&&parseFloat(sf.lovheight) >= h) {
                    h = sf.lovheight;
                }
            if (!Ext.isEmpty(lovurl)) {
                url = Ext.urlAppend(lovurl, Ext.urlEncode(sf.getFieldPara()));
            } else if (!Ext.isEmpty(svc)) {
// url = sf.context + 'sys_lov.lview?url='+encodeURIComponent(sf.context +
// 'sys_lov.lsc?svc='+sf.lovservice + '&'+
// Ext.urlEncode(sf.getLovPara()))+'&service='+sf.lovservice+'&';
                if(svc[0] == '/'){
                    url = ctx + 'sys_lov_common.lview?url=' + encodeURIComponent(Ext.urlAppend(ctx + sf.controller.substr(1), Ext.urlEncode(sf.getLovPara()))) + '&service=' + encodeURIComponent(svc) + '&dto=' + encodeURIComponent(sf['dto']) + '&lovCacheKey=' + encodeURIComponent(sf['layout_code'] + '|' + sf['tab_code'] + '|' + sf['real_column_name']);
                    ;
                }else if(svc[0] == '$') {
                    url = ctx + 'sys_lov_common.lview?url=' + encodeURIComponent(ctx + sf.controller.substr(1), Ext.urlEncode(sf.getLovPara())) + '&service=' + encodeURIComponent(svc)+ '&lovCacheKey=' + encodeURIComponent(sf['layout_code']+'|'+sf['tab_code']+'|'+sf['real_column_name']);
                }else {
                    url = ctx + 'sys_lov.lview?url=' + encodeURIComponent(Ext.urlAppend(ctx + 'autocrud/' + svc + '/query', Ext.urlEncode(sf.getLovPara()))) + '&service=' + svc;
                }
            }
            if (url) {
                sf.isWinOpen = true;
                sf.win = new A.Window({
                    title: sf.title || 'Lov',
                    url: Ext.urlAppend(url, "lovid=" + sf.id + "&key=" + encodeURIComponent(v) + "&gridheight=" + (sf.lovgridheight||Math.max(sf.lovgridheight || girdH, girdH)) + "&innerwidth=" + (w - 15) + "&lovautoquery=" + (Ext.isEmpty(sf.lovautoquery) ? 'true' : sf.lovautoquery) + "&lovlabelwidth=" + Math.max(sf.lovlabelwidth || 110, 110) + "&lovpagesize=" + (sf.lovpagesize || '')),
                    height: h,
                    width: w
                });
                sf.win.on('close', sf.onWinClose, sf);
            }
        },
        isEventFromComponent: function (el) {
            var popup = this.autocompleteview;
            return $L.Lov.superclass.isEventFromComponent.call(this, el) || (popup && popup.wrap.contains(el));
        }
    });
    /**
     * @class Leaf.Tledit
     * @extends Leaf.TextField
     *          <p>
     *          Tledit 多语言输入框
     * @author zhao.xu@hand-china.com
     */
    A.Tledit = Ext.extend($L.TextField, {
        constructor: function (config) {
            var sf = this;
            sf.isWinOpen = false;
            A.Tledit.superclass.constructor.call(sf, config);
        },
        initComponent: function (config) {
            var sf = this;
            A.Tledit.superclass.initComponent.call(this, config);
            sf.trigger = sf.wrap.child('div[atype=triggerfield.trigger]');
        },
        processListener: function (ou) {
            var sf = this;
            A.Tledit.superclass.processListener.call(sf, ou);
            sf.trigger[ou]('mousedown', sf.onWrapFocus, sf, {preventDefault: true})
                [ou](EVT_CLICK, sf.onTriggerClick, sf, {preventDefault: true});
        },
        initEvents: function () {
            A.Tledit.superclass.initEvents.call(this);
            this.addEvents(
                EVT_BEFORE_TRIGGER_CLICK);
        },
        onWrapFocus: function (e, t) {
            var sf = this;
            e.stopEvent();
            sf.focus.defer(Ext.isIE ? 1 : 0, sf);
        },
        commit: function (r, currentLang) {
            var sf = this,
                fieldName = sf.getBindName(),
//    		field_multi =fieldName+'_multi';
                record = sf.record;
            if (record && r && currentLang) {
                sf.setValue(r.get(currentLang));
//    		record.set(field_multi,r.data);
                for (key in r.data) {
//
                    /**
                     * 徐昭 2017/11/06 19:03
                     * 多语言字段修改,王子明让添加的多语言后缀
                     */
                    record.set(fieldName + '_' + key + '_mutl', r.data[key]);
                }
            }
            if (sf.win) sf.win.close();
        },
        onWinClose: function () {
            var sf = this;
            sf.isWinOpen = false;
            sf.win = null;
            if (!Ext.isIE6 && !Ext.isIE7) {// TODO:不知什么地方会导致冲突,ie6 ie7 会死掉
                sf.focus();
            } else {
                (function () {
                    sf.focus()
                }).defer(10);
            }
        },
        onTriggerClick: function (e) {
            e.stopEvent();
            var sf = this;
            if (sf.fireEvent(EVT_BEFORE_TRIGGER_CLICK, sf)) {
                sf.showTleditWindow();
            }
        },
        showTleditWindow: function () {
            var sf = this,
                ctx = sf.context,
                record = sf.record,
                descIdField = sf.descidfield,
                width = sf.tleditWidth || 400,
                height = sf.tleditHeight || 190,
                margin = sf.tleditMargin || 30;
            description_id = record ? (record.get(descIdField) || '') : '',
                url = ctx + 'sys_tledit.lview?description_id=' + description_id;
            sf.isWinOpen = true;
            sf.win = new A.Window({
                title: sf.title || '输入多语言信息',
                url: Ext.urlAppend(url, "tleditid=" + sf.id + "&margin=" + margin),
                width: width,
                height: height,
                refreshable:false
            });
            sf.win.on('close', sf.onWinClose, sf);
        }
    });
})($L);
/*
 * (function(A){ var TEMPLATE = ['<div tabIndex="-2" class="item-popup"
 * style="visibility:hidden;background-color:#fff;">','</div>'],
 * SHADOW_TEMPLATE = ['<div class="item-shadow" style="visibility:hidden;">','</div>'],
 * EVT_MOUSE_DOWN = 'mousedown', EVT_SHOW = 'show', EVT_HIDE = 'hide',
 * EVT_RENDER = 'render', EVT_BEFORE_RENDER = 'beforerender'; A.Popup =
 * Ext.extend(A.Component,{ constructor : function(config) { var id =
 * 'leaf-item-popup',popup = A.CmpManager.get(id); if(popup)return popup;
 * config.id=id; A.Popup.superclass.constructor.call(this, config); },
 * initComponent : function(config){ var sf = this;
 * A.Popup.superclass.initComponent.call(sf,config); sf.wrap = new
 * Ext.Template(TEMPLATE).insertFirst(document.body,{width:sf.width,height:sf.height},true);
 * sf.shadow = new
 * Ext.Template(SHADOW_TEMPLATE).insertFirst(document.body,{width:sf.width,height:sf.height},true); },
 * initEvents : function(){ A.Popup.superclass.initEvents.call(this);
 * this.addEvents( EVT_SHOW, EVT_HIDE, EVT_BEFORE_RENDER, EVT_RENDER ); },
 * processDataSet: function(ou){ var sf = this,ds = sf.optionDataSet; if(ds){
 * ds[ou]('load', sf.onDataSetLoad, sf); ds[ou]('query', sf.onDataSetQuery, sf); } },
 *
 * onDataSetQuery : function(){ this.fireEvent(EVT_BEFORE_RENDER,this) },
 * onDataSetLoad : function(){ this.fireEvent(EVT_RENDER,this) }, // update :
 * function(){ // this.wrap.update.apply(this.wrap,Ext.toArray(arguments)); // },
 * show : function(){ var sf = this; if(!sf.isShow){ sf.isShow=true;
 * sf.fireEvent(EVT_SHOW,sf); sf.wrap.show(); sf.shadow.show();
 * Ext.get(document).on(EVT_MOUSE_DOWN,sf.trigger,sf); } }, trigger :
 * function(e){ var sf = this; if(!sf.wrap.contains(e.target)
 * &&(!sf.owner||!sf.owner.wrap.contains(e.target))){ sf.hide(); } }, hide :
 * function(e){ var sf = this; if(sf.isShow){ sf.isShow=false;
 * sf.fireEvent(EVT_HIDE,sf) Ext.get(document).un(EVT_MOUSE_DOWN,sf.trigger,sf)
 * sf.wrap.hide(); sf.shadow.hide(); } }, moveTo : function(x,y){
 * this.wrap.moveTo(x,y); this.shadow.moveTo(x+3,y+3); }, setHeight :
 * function(h){ this.wrap.setHeight(h); this.shadow.setHeight(h); }, setWidth :
 * function(w){ //this.wrap.setWidth(w); this.shadow.setWidth(w); }, getHeight :
 * function(){ return this.wrap.getHeight(); }, getWidth : function(){ return
 * this.wrap.getWidth(); }, addClass : function(className){
 * this.wrap.dom.className = "item-popup "+className; // if(this.customClass ==
 * className)return; //
 * if(this.customClass)this.wrap.removeClass(this.customClass); //
 * this.customClass = className; // this.wrap.addClass(this.customClass); },
 * bind : function(ds,cmp){ var sf = this; sf.owner = cmp; if(sf.optionDataSet !=
 * ds){ sf.processDataSet('un'); sf.optionDataSet = ds; sf.processDataSet('on'); } },
 * destroy : function(){ A.Popup.superclass.destroy.call(this);
 * this.processDataSet('un'); delete this.shadow; } });
 *
 * })($L);
 */
/**
 * @class Leaf.MultiLov
 * @extends Leaf.Lov
 *          <p>
 *          MultiLov 值列表组件.
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.MultiLov = Ext.extend($L.MultiTextField, {
    constructor: function (config) {
        this.fetchremote = true;
        $L.MultiLov.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        var sf = this;
        $L.MultiLov.superclass.initComponent.call(sf, config);
        sf.trigger = sf.wrap.child('div[atype=triggerfield.trigger]');
        sf.saveRecord = {};
    },
    initEvents: function () {
        $L.MultiLov.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event beforetriggerclick 点击弹出框按钮之前的事件。
             * @param {Leaf.Lov}
             *            lov 当前Lov组件.
             */
            'beforetriggerclick'
        );
    },
    processListener: function (ou) {
        var sf = this;
        $L.MultiLov.superclass.processListener.call(sf, ou);
        sf.trigger[ou]('mousedown', sf.onWrapFocus, sf, {preventDefault: true})
            [ou]('click', sf.onTriggerClick, sf, {preventDefault: true});
    },
    onTriggerClick: function (e) {
        e.stopEvent();
        var sf = this, view = sf.autocompleteview;
        if (sf.fireEvent('beforetriggerclick', sf)) {
            sf.showLovWindow();
        }
    },
    getLovPara: function () {
        return this.getPara();
    },
    onWinClose: function () {
        var sf = this;
        sf.isWinOpen = false;
        sf.win = null;
        if (!Ext.isIE6 && !Ext.isIE7) {// TODO:不知什么地方会导致冲突,ie6 ie7 会死掉
            sf.focus();
        } else {
            (function () {
                sf.focus()
            }).defer(10);
        }
    },
    showLovWindow: function () {
        var sf = this;
        var fieldRecord = sf.saveRecord;
        if (sf.fetching || sf.isWinOpen || sf.readonly) return;
        var v = sf.getRawValue(),
            lovurl = sf.lovurl,
            svc = sf.service, ctx = sf.context,
            w = sf.lovwidth || 600,
            h = sf.lovheight || 400,
            pagesize = sf.pagesize || '',
            url;
        sf.blur();
        if (!Ext.isEmpty(lovurl)) {
            url = Ext.urlAppend(lovurl, Ext.urlEncode(sf.getFieldPara()));
        } else if (!Ext.isEmpty(svc)) {
// url = sf.context + 'sys_lov.lview?url='+encodeURIComponent(sf.context +
// 'sys_lov.lsc?svc='+sf.lovservice + '&'+
// Ext.urlEncode(sf.getLovPara()))+'&service='+sf.lovservice+'&';
            var showCode = sf.autocompletefield;
            if (!showCode) {
                var mapps = sf.getMapping();
                var showCode = mapps[1].from;
            }
            if(svc[0] == '/'){
                url = ctx + 'sys_multiLov.lview?url=' + encodeURIComponent(Ext.urlAppend(ctx + sf.controller.substr(1), Ext.urlEncode(sf.getLovPara()))) + '&service=' + encodeURIComponent(svc) + '&dto=' + encodeURIComponent(sf['dto']) + '&fieldRecord=' + encodeURIComponent(JSON.stringify(fieldRecord)) + '&showCode=' + showCode + '&lovCacheKey=' + encodeURIComponent(sf['layout_code'] + '|' + sf['tab_code'] + '|' + sf['real_column_name']);
            }else if(svc[0] == '$') {
                url = ctx + 'sys_multiLov.lview?url=' + encodeURIComponent(ctx + sf.controller.substr(1), Ext.urlEncode(sf.getLovPara())) + '&service=' + encodeURIComponent(svc)+ '&lovCacheKey=' + encodeURIComponent(sf['layout_code']+'|'+sf['tab_code']+'|'+sf['real_column_name'])  + '&fieldRecord=' + encodeURIComponent(JSON.stringify(fieldRecord)) + '&showCode=' + showCode;
            }else {
                url = ctx + 'sys_multiLov.lview?url=' + encodeURIComponent(Ext.urlAppend(ctx + 'autocrud/' + svc + '/query', Ext.urlEncode(sf.getLovPara()))) + '&service=' + svc  + '&fieldRecord=' + encodeURIComponent(JSON.stringify(fieldRecord)) + '&showCode=' + showCode;
            }

            if(!!pagesize){
                url += (url.indexOf("?")<0 ? "?" : "&") + "pageSize="+pagesize;
            }


        }
        if (url) {
            sf.isWinOpen = true;
            sf.win = new $L.Window({
                title: sf.title || 'Lov',
                url: Ext.urlAppend(url, "lovid=" + sf.id + "&key=" + encodeURIComponent(v) + "&gridheight=" + (sf.lovgridheight - 60 || 200) + "&innerwidth=" + (w - 30) + "&innergridwidth=" + Math.round((w - 90) / 2) + "&lovautoquery=" + (Ext.isEmpty(sf.lovautoquery) ? 'true' : sf.lovautoquery) + "&lovlabelwidth=" + (sf.lovlabelwidth || 75) + "&lovpagesize=" + (sf.lovpagesize || '')),
                height: h,
                width: w
            });
            sf.win.on('close', sf.onWinClose, sf);
        }
    },
    commit: function (r, lr, mapping) {
        var sf = this, record = lr || sf.record, name = sf.binder.name, obj = {};
        var item_textField = sf.wrap.child('.item-textField');
        if (r.length > 0) {
            item_textField.setStyle('display', 'none');
        } else {
            item_textField.setStyle('display', 'block');
        }
        sf.saveRecord = {};
        sf.clearAllItems();
        sf.record.ds.reset();
        for (var i = 0; i < r.length; i++) {
            if (record && r[i]) {
                sf.saveRecord[i] = r[i].data;
                Ext.each(mapping || sf.getMapping(), function (map) {
                    var from = r[i].get(map.from),
                        v = record.get(map.to);
                    if (!Ext.isEmpty(from)) {
                        obj[map.to] = from;
                        if (!Ext.isEmpty(v)) {
                            var SYMBOL = ";";
                            from = v + SYMBOL + from;
                        }
                    } else {
                        from = v;
                    }
                    record.set(map.to, from);
                });
            }
        }
        sf.win.close();
//        sf.fireEvent(EVT_COMMIT, sf, record, r)
    },
    commitAll: function (r, lr, mapping) {
        var sf = this, record = lr || sf.record, name = sf.binder.name, obj = {};
        var item_textField = sf.wrap.child('.item-textField');
        if (r.length > 0) {
            item_textField.setStyle('display', 'none');
        } else {
            item_textField.setStyle('display', 'block');
        }
        sf.saveRecord = {};
        sf.clearAllItems();
        sf.record.ds.reset();
        for (var i = 0; i < r.length; i++) {
            if (record && r[i] && i==0) {
                sf.saveRecord[i] = r[i].data;
                Ext.each(mapping || sf.getMapping(), function (map) {
                    var from = r[i].get(map.from);
                    record.set(map.to,from);
                });
                var obj = record.getObject();
                sf.record.ds.remove(record);
                sf.record.ds.create(obj);
            }else{
                var obj = {};
                sf.saveRecord[i] = r[i].data;
                Ext.each(mapping || sf.getMapping(), function (map) {
                    var from = r[i].get(map.from);
                    obj[map.to] = from;
                });
                sf.record.ds.create(obj);
            }
        }
        sf.win.close();
        sf.fireEvent("aftercommitall", sf, r)
    },
    setSize: function (size) {
        this.el.set({size: size + 2 || 1});
    }
});
/**
 * @class Leaf.TextArea
 * @extends Leaf.Field
 *          <p>
 *          TextArea组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.TextArea = Ext.extend($L.Field, {
    constructor: function (config) {
        $L.TextArea.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        $L.TextArea.superclass.initComponent.call(this, config);
    },
    initEvents: function () {
        $L.TextArea.superclass.initEvents.call(this);
    },
    initElements: function () {
        this.inputWrap = this.el = this.wrap;
    },
    onKeyDown: function (e) {
    }
// ,setRawValue : function(v){
// this.el.update(v === null || v === undefined ? '' : v);
// }
// ,getRawValue : function(){
// var v = this.el.dom.innerHTML;
// if(v === this.emptytext || v === undefined){
// v = '';
// }
// return v;
// }
})
$L.Customization = Ext.extend(Ext.util.Observable, {
    constructor: function (config) {
        $L.Customization.superclass.constructor.call(this);
        this.id = config.id || Ext.id();
        $L.CmpManager.put(this.id, this)
        this.initConfig = config;
    },
    start: function (config) {
        var sf = this;
        this.scanInterval = setInterval(function () {
            var cmps = $L.CmpManager.getAll();
            for (var key in cmps) {
                var cmp = cmps[key];
                if (cmp.iscust == true) {
                    cmp.on('mouseover', sf.onCmpOver, sf);
                }
            }
        }, 500);
    },
    mask: function (el) {
        var w = el.getWidth();
        var h = el.getHeight();// leftp:0px;top:0px; 是否引起resize?
        var p = '<div title="点击设置个性化" style="border:2px solid #000;cursor:pointer;left:-10000px;top:-10000px;width:' + (w) + 'px;height:' + (h) + 'px;position: absolute;"><div style="width:100%;height:100%;filter: alpha(opacity=0);opacity: 0;mozopacity: 0;background-color:#ffffff;"> </div></div>';
        this.masker = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(), p));
        this.masker.setStyle('z-index', 10001);
        var xy = el.getXY();
        this.masker.setX(xy[0] - 2);
        this.masker.setY(xy[1] - 2);
        this.masker.on('click', this.onClick, this);
        this.cover.on('mouseover', this.onCmpOut, this);
    },
    onClick: function () {
        var path = window.location.pathname;
        var str = path.indexOf('modules');
        var screen_path = path.substring(str, path.length);
        var screen = screen_path.substring(screen_path.lastIndexOf('/') + 1, screen_path.length);
        var context_path = path.substring(0, str);
        var parent = this.el.parent('[url]');
        if (parent) {
            var url = parent.getAttributeNS("", "url");
            if (url) {
                url = url.split('?')[0];
                if (url.indexOf(context_path) == -1) {
                    var li = url.lastIndexOf('/');
                    if (li != -1) {
                        url = url.substring(li + 1, url.length);
                    }
                    screen_path = screen_path.replaceAll(screen, url);
                } else {
                    screen_path = url.substring(url.indexOf(context_path) + new String(context_path).length, url.length);
                }
            }
        }

        new Leaf.Window({
            id: 'sys_customization_window',
            url: context_path + 'modules/sys/sys_customization_window.lview?screen_path=' + screen_path + '&id=' + this.cmp.id,
            title: '个性化设置',
            height: 170,
            width: 400
        });
        this.onCmpOut();
    },
    hideMask: function () {
        if (this.masker) {
            Ext.fly(this.masker).remove();
            this.masker = null;
        }
    },
    showCover: function () {
        var scrollWidth = Ext.isStrict ? document.documentElement.scrollWidth : document.body.scrollWidth;
        var scrollHeight = Ext.isStrict ? document.documentElement.scrollHeight : document.body.scrollHeight;
        var screenWidth = Math.max(scrollWidth, Leaf.getViewportWidth());
        var screenHeight = Math.max(scrollHeight, Leaf.getViewportHeight());
        var st = (Ext.isIE6 ? 'position:absolute;width:' + (screenWidth - 1) + 'px;height:' + (screenHeight - 1) + 'px;' : '')
// var p = '<DIV class="leaf-cover" style="'+st+'" unselectable="on"></DIV>';
        var p = '<DIV class="leaf-cover" style="' + st + 'filter: alpha(opacity=0);background-color: #fff;opacity: 0;mozopacity: 0;" unselectable="on"></DIV>';
        this.cover = Ext.get(Ext.DomHelper.insertFirst(Ext.getBody(), p));
        this.cover.setStyle('z-index', 9999);
    },
    hideCover: function () {
        if (this.cover) {
            this.cover.un('mouseover', this.onCmpOut, this);
            Ext.fly(this.cover).remove();
            this.cover = null;
        }
    },
    getEl: function (cmp) {
        var el;
        if (Leaf.Grid && cmp instanceof Leaf.Grid) {
            el = cmp.wb;
        } else {
            el = cmp.wrap;
        }

        return el;
    },
    onCmpOver: function (cmp, e) {
        if (this.isInSpotlight) return;
        this.isInSpotlight = true;
        this.showCover();
        this.cmp = cmp;
        this.el = this.getEl(cmp);
        if (this.el) {
// this.backgroundcolor = this.el.getStyle('background-color');
// this.currentPosition = this.el.getStyle('position');
            this.currentZIndex = this.el.getStyle('z-index');
// this.el.setStyle('background-color','#fff')
// this.el.setStyle('position','relative');
            this.el.setStyle('z-index', 10000);
        }
        this.mask(this.el)
    },
    onCmpOut: function (e) {
        this.isInSpotlight = false;
        if (this.el) {
// this.el.setStyle('position',this.currentPosition||'')
            this.el.setStyle('z-index', this.currentZIndex);
// this.el.setStyle('background-color', this.backgroundcolor||'');
            this.el = null;
        }
        this.hideMask();
        this.hideCover();
        this.cmp = null;
    },
    stop: function () {
        if (this.scanInterval) clearInterval(this.scanInterval)
        this.onCmpOut();
        var cmps = $L.CmpManager.getAll();
        for (var key in cmps) {
            var cmp = cmps[key];
            if (cmp.iscust == true) {
                cmp.un('mouseover', this.onCmpOver, this);
            }
        }
    }
});
$L.QueryForm = Ext.extend($L.Component, {
    initComponent: function (config) {
        $L.QueryForm.superclass.initComponent.call(this, config);
        var sf = this, wrap = sf.bodyWrap = sf.wrap.child('.form_body_wrap');
        if (wrap) {
            sf.body = wrap.first();
            sf.hasbody = true;
            if (!sf.isopen) sf.body.hide();
        }
        sf.searchInput = $L.CmpManager.get(sf.id + '_query');
        sf.rds = $L.CmpManager.get(sf.resulttarget);
    },
    processListener: function (ou) {
        $L.QueryForm.superclass.processListener.call(this, ou);
        Ext.fly(document)[ou]('click', this.formBlur, this);
    },
    formBlur: function (e, t) {
        if (!this.isEventFromComponent(t)) {
            this.close();
        }
    },
    bind: function (ds) {
        if (Ext.isString(ds)) {
            ds = $le(ds);
        }
        this.qds = ds;
    },
    doSearch: function () {
        var sf = this,
            input = sf.searchInput,
            queryhook = sf.queryhook,
            queryfield = sf.queryfield;
        if (sf.rds) {
            if (!sf.isopen && input) {
                var value = input.getValue(),
                    qds = sf.qds;
                if (queryhook) {
                    queryhook(value, qds);
                } else if (queryfield)
                    if (qds.getCurrentRecord()) qds.getCurrentRecord().set(queryfield, value);
            }
            sf.rds.query();
            sf.close();
        }
    },
    doReset: function () {
        var sf = this;
        sf.qds.reset();
        sf.close();
    },
    open: function () {
        var sf = this, body = sf.body;
        if (sf.isopen && sf.hasbody)return;
        sf.isopen = true;
        sf.bodyWrap.parent('TBODY').setStyle('display', 'block');
        if (sf.isopen) body.show()
        sf.bodyWrap.setHeight(body.getHeight() + 10);
        sf.bodyWrap.fadeIn();
    },
    close: function () {
        var sf = this;
        if (sf.isopen && sf.hasbody) {
            sf.isopen = false;
            sf.body.hide();
            sf.bodyWrap.parent('TBODY').setStyle('display', 'none');
            sf.bodyWrap.setHeight(0, true);
        }
    },
    trigger: function () {
        this[this.isopen ? 'close' : 'open']();
    },
    reset: function () {
        if (this.searchInput) this.searchInput.setValue('');
        this.qds.reset();
    }
});
/**
 * @class Leaf.ComboBox
 * @extends Leaf.TriggerField
 *          <p>
 *          Combo组件.
 * @author njq.niu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.MultiComboBox = Ext.extend($L.ComboBox, {
    initEvents: function () {
        $L.MultiComboBox.superclass.initEvents.call(this);
        this.addEvents(
            /**
             * @event unselect 选择事件.
             * @param {Leaf.Combobox}
             *            combo combo对象.
             * @param {Object}
             *            value valueField的值.
             * @param {String}
             *            display displayField的值.
             * @param {Leaf.Record}
             *            record 选中的Record对象
             */
            'unselect');
    },
    onBlur: function (e) {
        if (this.hasFocus) {
            $L.ComboBox.superclass.onBlur.call(this, e);
        }
    },
    onKeyDown: function (e) {
    },
    onKeyUp: function (e) {
    },
    clearOptions: function () {
        this.processDataSet('un');
        this.optionDataSet = null;
    },
    setOptions: function (name) {
        var sf = this,
            ds = typeof(name) === 'string' ? $le(name) : name;
        if (sf.optionDataSet != ds) {
            sf.processDataSet('un');
            sf.optionDataSet = ds;
            sf.processDataSet('on');
            sf.rendered = false;
            ds.selectable = true;
            if (!Ext.isEmpty(sf.value)) sf.setValue(sf.value, true)
        }
    },
    processDataSet: function (ou) {
        var sf = this,
            ds = sf.optionDataSet;
        $L.MultiComboBox.superclass.processDataSet.call(sf, ou);
        if (ds) {
            ds[ou]('select', sf.onDatasetSelect, sf)
            ds[ou]('unselect', sf.onDatasetUnSelect, sf);
        }
    },
    onDatasetSelect: function (ds, record) {
        var sf = this, v = [];
        if (sf.rendered) {
            sf.view.select('li .item-ckb').item(ds.indexOf(record) + 1).removeClass('item-ckb-u').addClass('item-ckb-c');
            if (ds.getSelected().length == ds.getAll().length) {
                sf.view.select('li.item-multicombobox-select-all .item-ckb').removeClass('item-ckb-u').addClass('item-ckb-c');
            }
        }
        Ext.each(ds.getSelected(), function (r) {
            v.push(r.get(sf.displayfield));
        });
        sf.setValue(v.join(';'));
    },
    onDatasetUnSelect: function (ds, record) {
        var sf = this, v = [];
        if (sf.rendered) {
            sf.view.select('li .item-ckb').item(ds.indexOf(record) + 1).removeClass('item-ckb-c').addClass('item-ckb-u');
            sf.view.select('li.item-multicombobox-select-all .item-ckb').removeClass('item-ckb-c').addClass('item-ckb-u');
        }
        Ext.each(ds.getSelected(), function (r) {
            v.push(r.get(sf.displayfield));
        });
        sf.setValue(v.join(';'));
    },
    onViewClick: function (e, t) {
        t = Ext.fly(t)
        if (t.is('div.item-ckb')) {
            t = t.parent('li');
        } else if (!t.is('li')) {
            return;
        }
        this.onSelect(t.dom);
    },
    onSelect: function (target) {
        var sf = this,
            index = target.tabIndex,
            ds = sf.optionDataSet;
        if (index == -1) {
            if ((target = Ext.fly(target)).hasClass('item-multicombobox-select-all')) {
                if (ds.getSelected().length == ds.getAll().length) {
                    target.select('div').removeClass('item-ckb-c').addClass('item-ckb-u');
                    ds.unSelectAll();
                } else {
                    target.select('div').removeClass('item-ckb-').addClass('item-ckb-c');
                    ds.selectAll();
                }
            }
            return;
        }
        var record = sf.optionDataSet.getAt(index),
            value = record.get(sf.valuefield),
            display = sf.getRenderText(record),
            method = ds.getSelected().indexOf(record) == -1 ? 'select' : 'unSelect';
        ds[method](record);
        sf.fireEvent(method.toLowerCase(), sf, value, display, record);

    },
    initList: function () {
        var sf = this,
            ds = sf.optionDataSet,
            v = sf.view;
        sf.currentIndex = sf.selectedIndex = null;
        if (ds.loading == true) {
            v.update('<li tabIndex="-1">' + _lang['ComboBox.loading'] + '</li>');
        } else {
            var sb = [], selected = ds.getSelected();
            sb.push('<li tabIndex="-1" class="item-multicombobox-select-all"><div class="item-ckb item-ckb-', selected.length == ds.getAll().length ? 'c' : 'u', '"></div>', '全选', '</li>')
            Ext.each(ds.getAll(), function (d, i) {
                sb.push('<li tabIndex="', i, '"><div class="item-ckb item-ckb-', selected.indexOf(d) == -1 ? 'u' : 'c', '"></div>', sf.getRenderText(d), '</li>');
            });
            v.update(sb.join(''));
        }
    },
    setValue: function (v, silent, vr) {
        var sf = this, r, field, ds = sf.optionDataSet;
        $L.ComboBox.superclass.setValue.call(sf, v, silent);
        if (r = sf.record) {
            if (silent) {
                ds.unSelectAll();
                Ext.each(v && v.split(';'), function (_v) {
                    ds.select(ds.find(sf.displayfield, _v));
                });
            } else if (field = r.getMeta().getField(sf.binder.name)) {
                Ext.each(field.get('mapping'), function (map) {
                    var vl = [];
                    Ext.each(ds.getSelected(), function (record) {
                        vl.push(record.get(map.from));
                    });
                    r.set(map.to, vl.join(';'));
                });
            }
        }
    },
    getIndex: function (v) {
        return null;
    }
});
/**
 * @class Leaf.PercentField
 * @extends Leaf.NumberField
 *          <p>
 *          百分数输入组件.
 *          </p>
 * @author huazhen.wu@hand-china.com
 * @constructor
 * @param {Object}
 *            config 配置对象.
 */
$L.PercentField = Ext.extend($L.NumberField, {
    formatValue: function (v) {
        if (Ext.isEmpty(v))return '';
        return $L.PercentField.superclass.formatValue.call(this, $L.FixMath.mul(v, 100));
    },
    processValue: function (v) {
        if (Ext.isEmpty(v))return '';
        return $L.FixMath.div($L.PercentField.superclass.processValue.call(this, v), 100);
    }
});
$L.SideBarPanel = Ext.extend($L.Component, {
    constructor: function (config) {
        this.collapsible = true;
        this.cmps = {};
        $L.SideBarPanel.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        $L.SideBarPanel.superclass.initComponent.call(this, config);
        this.collapseBtn = this.wrap.child('.arrow');
        this.body = this.wrap.child('.bar-body');
        this.wrap.cmps = this.cmps;
        this.initSize();
        this.center();
        if (this.url) {
            this.load(this.url)
        }
    },
    processListener: function (ou) {
        $L.SideBarPanel.superclass.processListener.call(this, ou);
        if (this.collapsible) {
            this.collapseBtn[ou]("click", this.onCollapseBtnClick, this);
        }
    },
    initSize: function () {
        if (this.fullHeight) {
            var screenHeight = $L.getViewportHeight();
            this.height = screenHeight;
            this.wrap.setHeight(screenHeight);
        }
        this.collapseBtn.setStyle('top', (this.height - 45) / 2 + 'px');
    },
    center: function () {
        var screenHeight = $L.getViewportHeight();
        var st = document[Ext.isStrict ? 'documentElement' : 'body'].scrollTop;
        var y = st + Math.max((screenHeight - this.height - (Ext.isIE ? 26 : 23)) / 2, 0);
        this.wrap.setStyle('top', y + 'px');
    },
    onCollapseBtnClick: function () {
        var w = this.wrap.getWidth() - 2;
        if (w == 0) {
            this.wrap.setWidth(this.width, {
                duration: .35,
                easing: 'easeOut',
                callback: function () {
                    this.body.setStyle('display', 'block');
                },
                scope: this
            });
        } else {
            this.body.setStyle('display', 'none');
            this.wrap.setWidth(0, true)

        }

    },
    showLoading: function () {
        this.body.update(_lang['window.loading']);
        this.body.setStyle('text-align', 'center');
        this.body.setStyle('line-height', 5);
    },
    clearLoading: function () {
        this.body.update('');
        this.body.setStyle('text-align', '');
        this.body.setStyle('line-height', '');
    },
    clearBody: function () {
        for (var key in this.cmps) {
            var cmp = this.cmps[key];
            if (cmp.destroy) {
                try {
                    cmp.destroy();
                } catch (e) {
                    alert('销毁sidebar出错: ' + e)
                }
            }
        }
    },
    load: function (url) {
        this.clearBody();
        this.showLoading();
        var header = $jq('meta[name=_csrf_header]').attr('content');
        var token = $jq('meta[name=_csrf]').attr('content');
        var headers = {};
        headers[header] = token;
        Ext.Ajax.request({
            url: url,
            header:headers,
            success: this.onLoad.createDelegate(this)
        });
    },
    onLoad: function (response, options) {
        if (!this.body) return;
        this.clearLoading();
        var html = response.responseText;
        var res
        try {
            res = Ext.decode(response.responseText);
        } catch (e) {
        }
        if (res && res.success == false) {
            if (res.error) {
                if (res.error.code && res.error.code == 'session_expired' || res.error.code == 'login_required') {
                    if ($L.manager.fireEvent('timeout', $L.manager))
                        $L.showErrorMessage(_lang['ajax.error'], _lang['session.expired']);
                } else {
                    $L.manager.fireEvent('ajaxfailed', $L.manager, options.url, options.para, res);
                    var st = res.error.stackTrace;
                    st = (st) ? st.replaceAll('\r\n', '</br>') : '';
                    if (res.error.message) {
                        var h = (st == '') ? 150 : 250;
                        $L.showErrorMessage(_lang['window.error'], res.error.message + '</br>' + st, null, 400, h);
                    } else {
                        $L.showErrorMessage(_lang['window.error'], st, null, 400, 250);
                    }
                }
            }
            return;
        }
        var sf = this;
        this.body.update(html, true, function () {
            var w = sf.wrap.getWidth() - 2;
            if (w == 0) sf.body.setStyle('display', 'none');
// var cmps = $L.CmpManager.getAll();
// for(var key in cmps){
// if(sf.oldcmps[key]==null){
// sf.cmps[key] = cmps[key];
// }
// }
            sf.fireEvent('load', sf)
        }, this.wrap);
    }
})

$L.Form = Ext.extend($L.Component, {
    constructor: function (config) {
        var sf = this;
        sf.toggleBtnId = config.togglebtnid;
        sf.defaultExpand = config.defaultexpand;
        sf.currentExpand = config.defaultexpand;
        $L.Form.superclass.constructor.call(this, config);
    },
    initComponent: function (config) {
        $L.Form.superclass.initComponent.call(this, config);
        var sf = this;
        /*
         *添加form_body_out以及下面的onControlClick事件是为了实现form_body的展开和收缩
         *定义一个sf.isShrink属性，其值为boolean类型
         *true : 当前状态为收缩
         *false : 当前状态为展开
         * 向平
         */
        sf.isShrink = false;
        sf.shrink = sf.wrap.child(".form_body_out");
        sf.formBody = sf.wrap.child(".form_body");
        sf.formTitle = sf.wrap.child("td.form-title");
    },
    processListener: function (ou) {
        var sf = this;
        $L.Form.superclass.processListener.call(sf, ou);
        if (sf.shrink) {
            sf.shrink[ou]("click", sf.onShrinkToggle, sf);
        }
//        this.formTitle[ou]("mouseover", this.onMouseover,  this);
    },
//    onMouseover : function(e){
//    	var text = this.title;
//    	$L.ToolTip.show(this.formTitle,text);
//    },
    /*
     * 添加一个boolean的参数force，
     * force值:
     * 			true : 将form收起来
     * 			false : 将form展开
     */
    onShrinkToggle: function (force) {
        var sf = this,
            formWidth = sf.wrap.getWidth();
        if (typeof(force) === "boolean") {
            sf.isShrink = !force;
        }
        if (sf.isShrink) {
            if (this.shrink.parent(".layout-table").parent(".layout-table")) {
                this.shrink.parent(".layout-table").parent(".layout-table").setStyle("width", formWidth + "px");
            }
            this.shrink.parent(".layout-table").setStyle("width", formWidth + "px");
            this.formBody.removeClass("form_body_hide");
            this.shrink.removeClass("form_body_spread");
            sf.isShrink = false;
        }
        else {
            if (this.shrink.parent(".layout-table").parent(".layout-table")) {
                this.shrink.parent(".layout-table").parent(".layout-table").setStyle("width", formWidth + "px");
            }
            this.shrink.parent(".layout-table").setStyle("width", formWidth + "px");
            this.formBody.addClass("form_body_hide");
            this.shrink.addClass("form_body_spread");
            sf.isShrink = true;
        }
    },
    initEvents: function () {
        $L.Form.superclass.initEvents.call(this);
    },
    /*
     * 徐昭 2018-1-15 11:28:24
     * form添加setTitle方法
     */
    setTitle: function (title) {
        var sf = this,
            width = (title ? title.length : 0) * 14 + 10 + 2;
        if (sf.formTitle) {
            sf.formTitle.setWidth(width);
            sf.formTitle.dom.innerText = title ? title : '';
        }
    },
    doToggle: function () {
        var sf = this;
        sf.currentExpand = !sf.currentExpand;
        var button = Ext.get(sf.toggleBtnId).query('button')[0];
        var textDiv = Ext.get(sf.toggleBtnId).query('div')[0];

        Ext.get(button).toggleClass("item-icon-btn-more");
        Ext.get(button).toggleClass("item-icon-btn-less");
        button.setAttribute('text', sf.currentExpand ? _lang['toggle.shrink'] : _lang['toggle.expand']);
        button.setAttribute('title', sf.currentExpand ? _lang['toggle.shrink'] : _lang['toggle.expand']);
        textDiv.setAttribute('text', sf.currentExpand ? _lang['toggle.shrink'] : _lang['toggle.expand']);
        textDiv.innerHTML = sf.currentExpand ? _lang['toggle.shrink'] : _lang['toggle.expand'];

        Ext.get(this.wrap).query('*[cantoggle=""]').forEach(function (ele) {
            Ext.get(ele).toggleClass("item-shrink");
        });
    }
});

$L.ButtonGroup = Ext.extend($L.Component, {
    initComponent: function (config) {
        $L.ButtonGroup.superclass.initComponent.call(this, config);
        var sf = this,
            wrap = sf.bodyWrap = sf.wrap.child(".item-btn-group-body");
        if (wrap) {
            sf.body = wrap.first();
            sf.hasbody = true;
//			if(!sf.isopen)sf.body.hide();
        }
        sf.item_icon = sf.wrap.child('div.item-icon-text').setStyle('float', 'left');
        sf.button = sf.wrap.child('button.operation');
        sf.button.createChild('<div class="operation-icon"/>');
    },
    processListener: function (ou) {
        var sf = this;
        $L.ButtonGroup.superclass.processListener.call(sf, ou);
        sf.bodyWrap[ou]("mouseover", sf.onMouseOver, sf);
        sf.bodyWrap[ou]("mouseout", sf.onMouseOut, sf);
    },
    onMouseOver: function () {
        var sf = this;
        sf.open();
    },
    onMouseOut: function () {
        var sf = this;
        sf.close();
    },
    /*
     * 将按钮组原来的点击显示所有按钮更换成鼠标经过显示所有按钮
     * 向平    2017-11-09
     */
    open: function () {
        var sf = this, body = sf.body;
//		if(sf.isopen && sf.hasbody)return;
//		sf.isopen = true;
        sf.bodyWrap.parent('TBODY').setStyle({'display': 'block', 'position': 'absolute'});
//        if(sf.isopen)body.show()
        sf.bodyWrap.setHeight(body.getHeight());
        //sf.bodyWrap.fadeIn();
    },
    close: function () {
        var sf = this;
        if (sf.hasbody) {
//			sf.isopen = false;
//			sf.body.hide();
            sf.bodyWrap.parent('TBODY').setStyle('display', 'none');
//			sf.bodyWrap.setHeight(0,true);
        }
    }
//	trigger : function(){
//		this[this.isopen?'close':'open']();
//	}
});

$L.TagBox = Ext.extend($L.Component, {
    constructor: function (config) {
        var sf = this;
        sf.bindTarget = config.bindtarget;
        sf.ds = $le(sf.bindTarget);
        $L.TagBox.superclass.constructor.call(sf, config);
    },
    initComponent: function (config) {
        $L.TagBox.superclass.initComponent.call(this, config);
        var sf = this;
        sf.processDataSetListener('on');
    },
    processListener: function (ou) {
        var sf = this;
        $L.TagBox.superclass.processListener.call(sf, ou);
        sf.wrap[ou]("click", sf.onClick, sf);
    },
    processDataSetListener: function (ou) {
        var sf = this;
        if (sf.ds) {
            sf.ds[ou]('load', sf.onLoad, sf);
            sf.ds[ou]('update', sf.onLoad, sf);
            sf.ds[ou]('add', sf.onLoad, sf);
        }
    },
    onLoad: function () {
        this.initList();
    },
    onClick: function (e) {
        if (Ext.get(e.target).dom.className === "tagBox") {
            var tagSpan = Ext.get(e.target),
                tagView = Ext.get(tagSpan.dom.children[0]),
                sf = this,
                clientW = document.documentElement.clientWidth,
                tagTop = tagSpan.dom.offsetHeight,
                tagLeft = tagSpan.dom.offsetLeft,
                tagBox = sf.wrap.child(".item-tagBox"),
                tagAll = tagBox.dom.children;
            for (var i = 0; i < tagAll.length; i++) {
                if (tagSpan.id === tagAll[i].id && tagSpan.dom.lastChild.style.display === 'none') {
                    tagAll[i].lastChild.style.display = 'block';
                    var tagW = tagAll[i].lastChild.offsetWidth;
                    var tagTriangle = Ext.get(tagAll[i].lastChild);
                    if (tagLeft + tagW > clientW) {
                        tagAll[i].lastChild.style.right = '5px';
                        tagTriangle.dom.lastChild.style.right = '30px';
                    } else {
                        tagTriangle.dom.lastChild.style.left = '15px';
                    }
                    tagAll[i].lastChild.style.top = tagTop + 10 + 'px';
                } else {
                    tagAll[i].lastChild.style.display = 'none';
                }
            }
        } else {
            var tagSpan = Ext.get(Ext.get(e.target).findParent(".tagBox")),
                tagView = tagSpan.child(".item-tagBox-view"),
                sf = this,
                clientW = document.documentElement.clientWidth,
                tagTop = tagSpan.dom.offsetHeight,
                tagLeft = tagSpan.dom.offsetLeft,
                tagBox = sf.wrap.child(".item-tagBox"),
                tagAll = tagBox.dom.children;
            for (var i = 0; i < tagAll.length; i++) {
                if (tagSpan.id === tagAll[i].id && tagSpan.dom.lastChild.style.display === 'none') {
                    tagAll[i].lastChild.style.display = 'block';
                    var tagW = tagAll[i].lastChild.offsetWidth;
                    var tagTriangle = Ext.get(tagAll[i].lastChild);
                    if (tagLeft + tagW > clientW) {
                        tagAll[i].lastChild.style.right = '5px';
                        tagTriangle.dom.lastChild.style.right = '30px';
                    } else {
                        tagTriangle.dom.lastChild.style.left = '15px';
                    }
                    tagAll[i].lastChild.style.top = tagTop + 10 + 'px';
                } else {
                    tagAll[i].lastChild.style.display = 'none';
                }
            }
        }
    },
    renderText: function (record, tag) {
        var sf = this,
            renderer = sf.renderer,
            NOT_FOUND = '未找到',
            METHOD = '方法',
            value = $L.escapeHtml(value);
        if (renderer) {//&&!IS_EMPTY(value)  去掉对value是否为空的判断
            var rder = $L.getRenderer(renderer);
            if (rder == null) {
                alert(NOT_FOUND + renderer + METHOD)
                return value;
            }
            value = rder(value, record, tag);
        }
        return value == null ? '' : value;
    },
    contentRenderer: function (record) {
        var sf = this,
            renderer = sf.contentrenderer,
            NOT_FOUND = '未找到',
            METHOD = '方法',
            value = $L.escapeHtml(value);
        if (renderer) {//&&!IS_EMPTY(value)  去掉对value是否为空的判断
            var rder = $L.getRenderer(renderer);
            if (rder == null) {
                alert(NOT_FOUND + renderer + METHOD)
                return value;
            }
            value = rder(value, record);
        }
        return value == null ? '' : value;
    },
    initList: function () {
        var records = this.ds.getAll(),
            sf = this,
            tagBox = sf.wrap.child(".item-tagBox"),
            displayField = sf.displayfield,
            tpl = '';
        for (var i = 0; i < records.length; i++) {
            var dataName = records[i].get(displayField);
            if (sf.renderText(records[i], dataName)) {
                tpl += '<span class="tagBox" id="tagBox-' + records[i].id + '">' + sf.renderText(records[i], dataName) + '<div class="item-tagBox-view" data-tabIndex="tagBox-' + records[i].id + '" style="display:none;">' + sf.contentRenderer(records[i]) + '<div class="triangle"><div class="triangle1"></div><div class="triangle2"></div></div></div></span>';
            } else {
                tpl += '<span class="tagBox" id="tagBox-' + records[i].id + '">' + dataName + '<div class="item-tagBox-view" data-tabIndex="tagBox-' + records[i].id + '" style="display:none;">' + sf.contentRenderer(records[i]) + '<div class="triangle"><div class="triangle1"></div><div class="triangle2"></div></div></div></span>';
            }
        }
        tagBox.update(tpl);
    }
});

/*
 * 右边导航栏对象
 *
 * */
$L.RightNavBars = Ext.extend($L.Component, {
    constructor: function (config) {
        $L.RightNavBars.superclass.constructor.call(this, config);
    },
    //通过index索引返回对应的对象
    getItem: function (index) {
        var item = "该位置未找到对象!";
        jQuery(".hls-selectBar div.contain").each(function (i, obj) {
            if (index == i) {
                item = obj;
            }
        });
        return item;
    },
    //切换图片源 index src
    setImgBySrc: function (index, src) {
        jQuery(".hls-selectBar div.contain img").each(function (i, obj) {
            if (index == i) {
                jQuery(obj).attr("src", src);
            }
        });
    },
    //设置第几个的背景颜色
    setItemBackground: function (index, color) {
        jQuery(".hls-selectBar div.contain div").each(function (i, obj) {
            if (index == i) {
                jQuery(obj).css("background", color);
            }
        });
    },
    //设置右侧距离
    setRightDistance:function(d){
        jQuery(".hls-selectBar").css({
            "right":d+"px"
        });
    }
});
jQuery(window).click(function () {
    var fastButton = $jq(top.document.getElementById("fastButton"));
    if (fastButton.height() == 60) {
        jQuery("#fast-menu-toolbar").css("background", "none");
        jQuery(".fast_toolbar").animate({height: "0px"}, function () {
            jQuery(".fast_toolbar").hide();
        });
    }
});

$L.doGridLayoutCustom = function(id){
    Leaf.onReady(function(){
        // if($jq("#"+id).hasClass("layout-form")&&$jq("#"+id).width()>$jq("#"+id).closest(".item-tab-body").width()){
        //     $jq("#"+id).width($jq("#"+id).closest(".item-tab-body").width()-20);
        // }
    });
};

/**
 * 促发tab选择
 * @param {object} 促发tab选择的原始对象
 */
$L.callSelectTab = function(object){
    var sf = object;
    if($jq("#"+sf.id).closest(".item-tab-body").height()) {
        if($jq("#"+sf.id).closest(".tab").length>0){
            var curTabId= $jq("#"+sf.id).closest(".tab")[0].id;
            var tabPannelId = $jq("#" + sf.id).closest(".item-tab").attr("id");
            if($L.CmpManager.get(tabPannelId)&&$(tabPannelId).activeBody&&$(tabPannelId).activeBody.id==curTabId){
                var activeTab = $(tabPannelId).activeTab;
                $(tabPannelId).resizeTab($(tabPannelId).getTab(activeTab).index);
            }
        }
    }
};