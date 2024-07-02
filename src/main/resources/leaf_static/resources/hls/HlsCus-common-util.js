
/**
 * 计算两个日期之间的期限，单位为（月）保留两位小数
 **/

function calcMonth(fromDate,toDate){
    var pre = new Date(changeDateForIe(fromDate));
    var now = new Date(changeDateForIe(toDate));
    var days = now.getTime() - pre.getTime();
    return parseFloat(days*12 / (1000 * 60 * 60 * 24 * 365)).toFixed(0);
}

/**
 * 使用toDate-fromDate得到期限（天数）然后再除以day获取期数（1）
 * @param fromDate
 * @param toDate
 * @param day
 * @returns {int}
 */
function calcGapToCeil(fromDate,toDate,day){
    var pre = new Date(fromDate);
    var now = new Date(toDate);
    var days = now.getTime() - pre.getTime();
    return Math.ceil(Math.ceil(parseFloat(days/ (1000 * 60 * 60 * 24)))/day);
}
/**
 * 查询币种，用于替代快码，本来这个币种是维护在快码中的，但是本项目没有，因此直接在表中查找
 * 获取币种的数据源，返回的是所有ENABLED_FLAG=Y的币种集合
 */
function queryCurrencyDataSource(){
    var currencyDataSource;
    var currencyUrl = '${/request/@context_path}/hls_currency/query';
    $.ajax({
        type: 'GET',
        url: currencyUrl,
        contentType: "application/json; charset=utf-8",
        success: function (datas) {
            currencyDataSource=datas.rows;
        }
    });
    return currencyDataSource;
}


/**
 * 加法
 * @param arg1
 * @param arg2
 * @returns
 */
function accAdd(arg1,arg2){
    var r1,r2,m;
    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0};
    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0};
    m=Math.pow(10,Math.max(r1,r2));
    return (arg1*m+arg2*m)/m;
}

/**
 * 减法
 * @param arg1
 * @param arg2
 * @returns
 */
function accSubtr(arg1,arg2){
    var r1,r2,m,n;
    try{r1=arg1.toString().split(".")[1].length;}catch(e){r1=0;}
    try{r2=arg2.toString().split(".")[1].length;}catch(e){r2=0;}
    m=Math.pow(10,Math.max(r1,r2));
    //动态控制精度长度
    n=(r1>=r2)?r1:r2;
    return ((arg1*m-arg2*m)/m).toFixed(n);
}

/***
 * 乘法，获取精确乘法的结果值
 * @param arg1
 * @param arg2
 * @returns
 */
function accMul(arg1,arg2)
{
    var m=0,s1=arg1.toString(),s2=arg2.toString();
    try{m+=s1.split(".")[1].length}catch(e){};
    try{m+=s2.split(".")[1].length}catch(e){};
    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
}

/***
 * 除法，获取精确除法的结果值
 * 保留6位小数
 * @param arg1
 * @param arg2
 * @returns
 */
function accDivCoupon(arg1,arg2){
    var t1=0,t2=0,r1,r2;
    try{t1=arg1.toString().split(".")[1].length;}catch(e){}
    try{t2=arg2.toString().split(".")[1].length;}catch(e){}
    with(Math){
        r1=Number(arg1.toString().replace(".",""));
        r2=Number(arg2.toString().replace(".",""));
        return ((r1/r2)*pow(10,t2-t1)).toFixed(6);
    }
}
/**
 * 根据部门Id查找部门code
 */
function getPositionCodeByPositionId(positionId) {
    var positionCode = null;
    //按照岗位Id查询岗位编码
    $.ajax({
        url: '${/request/@context_path}/fnd/position/assign/query',
        async: false, //设为同步y
        type: "POST",
        dataType: "json",
        data: {"positionId": positionId},
        contentType: "application/json",
        success: function (data) {
            if(data.rows.length==1){
                var positionData = data.rows[0];
                positionCode = positionData.positionCode;
            }
        }
    });
    return positionCode;
}

function ddd(param){
    return "999999"+param;
}


/**
 * comboBox是否必输并且是否可编辑 created by:tengfei
 * @param name
 * @param flag
 */
function isRequiredComboBox(name,flag,notNull) {
    if(flag==false){
        //只读
        $("#"+name+"").attr("required", false);
        $("#"+name+"").prev().removeClass("k-state-required");
        //$("#floatingWay").parent().removeClass("k-valid-custom");
        //viewModel.data.set(name,null);
        if (notNull) {
            $("#" + name + "").data("kendoComboBox").text("");
        }
        $("#"+name+"").data("kendoComboBox").enable(false);
    }else {
        //必填
        $("#"+name+"").data("kendoComboBox").enable(true);
        $("#"+name+"").attr("required", true);
        $("#"+name+"").prev().addClass("k-state-required");
    }
}

/**
 * 数字框numberictextbox是否必输并且是否可编辑 created by:tengfei
 * @param name
 * @param flag 当flag为tue的时候必填，当flag为false的时候只读，当flag为edit的时候为非必填【可编辑】
 */
function isRequiredNumerictextbox(name,flag, notNull){
    if(flag==false){
        //只读
        $("#"+name+"").attr("required", false);
        $("#"+name+"").prev().removeClass("k-state-required");
        if (notNull) {
            $("#" + name + "").data("kendoNumericTextBox").value("");
        }
        $("#"+name+"").data("kendoNumericTextBox").enable(false);
    }else if(flag==true){
        //必填
        //$("#"+name+"").data("kendoNumericTextBox").value("");
        $("#"+name+"").data("kendoNumericTextBox").enable(true);
        $("#"+name+"").attr("required", true);
        $("#"+name+"").prev().addClass("k-state-required");
    }else {
        //非必填
        $("#"+name+"").data("kendoNumericTextBox").enable(true);
    }

}
/**
 * 计算日期，根据运算符来对基准日期进行处理，返回一个日期的结果
 * @param date 基准日期
 * @param days 天数
 * @param operator 运算符
 */
function calcDateByOperator(date,days,operator){
    try{
        if(date==null || date==undefined){
            throw "日期不能为空";
        }
    }catch(e){
        alert("错误信息：日期不能为空");
    }
    var currentDate=new Date(date).getTime();
    var d1=days*1000*60*60*24;
    if(operator=="-"){
        return new Date((currentDate-d1));
    }else if(operator=="+"){
        return new Date((currentDate+d1));
    }else{
        alert("不支持的运算符类型");
    }
}

/**
 * 由于IE浏览器不支持格式为 2019-4-23的格式，因此需要转换成2019/4/23
 * @param ieDate
 */
function changeDateForIe(ieDate){
    var date=ieDate;
    //将所有的'-'转为'/'即可
    date=date.toString().replace(new RegExp(/-/gm) ,"/"); 　
    var d=new Date(date);
    return d;
}

/**
 * 刷新Grid，适用于
 * @param gridIds
 */
function refreshGridById(gridIds){
    if($.type(gridIds)=="array"){
        var j;
        for(j in gridIds){
            $("#"+gridIds[j]+"").data("kendoGrid").dataSource.page(1);
        }
    }else if($.type(gridIds)=="string"){
        $("#"+gridIds+"").data("kendoGrid").dataSource.page(1);
    }else{
        throw '参数类型有误，请检查';
    }
}





