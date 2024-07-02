package com.hand.hls.wsdl.components;

import com.hand.hls.wsdl.utils.UniqId;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class InterfaceConstantsComponent {

    //发送系统产生MSGID，消息的唯一标识
     public static final String REQ_TRACE_ID = "REQ_TRACE_ID";
    //报文发送时间，14位数字
    public static final String REQ_SEND_TIME = "REQ_SEND_TIME";
    //发送方系统编码名称
    public static final String REQ_SRC_SYS = "REQ_SRC_SYS";
    //接收方系统编码名称
    public static final String  REQ_TAR_SYS = "REQ_TAR_SYS";
    //PO上定义的服务名称，必须按PO命令规范
    public static final String REQ_SERVER_NAME = "REQ_SERVER_NAME";
    //是否为异步报文
    public static final String REQ_SYN_FLAG = "REQ_SYN_FLAG";
    //业务数据ID
    public static final String REQ_BSN_ID = "REQ_BSN_ID";
    //重试次数
    public static final  String REQ_RETRY_TIMES = "REQ_RETRY_TIMES";
    //是否为重复报文
    public static final  String REQ_REPEAT_FLAG = "REQ_REPEAT_FLAG";
    //重复周期，单位为秒
    public static final String REQ_REPEAT_CYCLE = "REQ_REPEAT_CYCLE";
    //发送放系统编码

    public static String SEND_CODE;
    //接收方系统编码

    public static String RES_CODE;

    //PO上定义的服务名称，必须按PO命令规范,填写WSDL里面Operation名称
    public static final String SI_VENDOR_MASTERDATA_REQ = "SI_VENDOR_MASTERDATA_REQ";

    //0为非异步报文
    public static final String NOT_ASYNC = "0";
    //1为异步
    public static final String ASYNC = "1";

    public static final String REQ_BASEINFO = "REQ_BASEINFO";

    public static final String MESSAGE = "MESSAGE";

    public static final String HEADER = "HEADER";
    public static final String PAYLIST = "PAYLIST";
    public static final String ITMES = "ITMES";
    public static final String ITEM = "ITEM";

    public static final String ITEMS = "ITEMS";

    public static final String TYPE = "TYPE";

    public static final String VENDOR_NUM = "VENDOR_NUM";

    public static final String KUNNR = "KUNNR";

    public static final String S = "S";

    public static final String SUCCESS = "SUCCESS";

    public static final String FAILURE = "FAILURE";

    public static final String SENDING = "SENDING";

    public static final String BP_MASTER = "BP_MASTER";

    public static final String BP_VENDER = "BP_VENDER";

    public static final String SUCCESS_INFO = "执行完成！";

    public static final String ERROE_INFO = "执行时出现问题:";

    public static final String STATUS = "STATUS";

    public static final String INPUT = "INPUT";

    public static final Logger logger = LoggerFactory.getLogger(InterfaceConstantsComponent.class);

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    //客户主数据接口
    //请求头信息
    public static final String HEAD = "head";
    public static final String TX_CODE = "txCode";
    public static final String SYSTEM_ID = "systemId";
    public static final String SERIAL_NUMBER = "serialNumber";
    public static final String TX_TIME = "txTime";
    public static final String VERSION = "version";
    public static final String REMARK = "remark";
    //请求体信息
    public static final String BODY = "body";
    public static final String ZT_CUS_BASIC = "ztCusBasic";
    public static final String ZKUNNR = "zkunnr";
    public static final String ZTHRDNO = "zthrdno";
    public static final String NAME_ORG1 = "name_org1";
    public static final String ZZHLX = "zzhlx";
    public static final String BP_IDTYPE = "bp_idtype";
    public static final String BP_ID_NUM = "bp_id_num";
    public static final String T_NR_CALL = "t_nr_call";
    public static final String ZZZLX = "zzzlx";
    public static final String BU_SORT1 = "bu_sort1";
    public static final String REF_POSTA = "ref_posta";
    public static final String REF_POST = "ref_post";
    public static final String CITY_CODE = "city_Code";
    public static final String STREET = "street";
    public static final String T_NUMBERT = "t_numbert";
    public static final String POST_COD2 = "post_cod2";
    public static final String E_ADDRESS = "e_address";
    public static final String BP_CLASS = "bp_class";
    public static final String BP_ROL_ID = "bp_rol_id";
    public static final String BP_ID = "bp_id";
    //法人主数据crEnterpriseBaseInfo
    public static final String CR_ENTERPRISE_BASE_INFO = "crEnterpriseBaseInfo";
    public static final String CUST_ID_KEY = "cust_id_key";
    public static final String CUST_ID = "cust_id";
    public static final String CUST_NAME = "cust_name";
    public static final String CUST_NAME_SHORT = "cust_name_short";
    public static final String REGISTRATION_DT = "registration_dt";
    public static final String BUSINESS_LICENSE_DUE_DATE = "business_license_due_date";
    public static final String REGISTERED_CAPITAL = "registered_capital";
    public static final String PAID_UP_CAPITAL = "paid_up_capital";
    public static final String COUNTRY = "country";
    public static final String REGISTEREDPROVINCE = "registeredprovince";
    public static final String REGISTEREDCITY = "registeredcity";
    public static final String REGISTEREDCOUNTY = "registeredcounty";
    public static final String REGISTER_ADDRESS = "register_address";
    public static final String OFFICE_ADDR_PROVINCE = "office_addr_province";
    public static final String OFFICE_ADDR_CITY = "office_addr_city";
    public static final String OFFICE_ADDR_COUNTY = "office_addr_county";
    public static final String OFFICE_ADDRESS = "office_address";
    public static final String IS_OVEREAS = "is_overeas";
    public static final String IND_M = "ind_m";
    public static final String IND_D = "ind_d";
    public static final String IND_Z = "ind_z";
    public static final String IND_X = "ind_x";
    public static final String ZIP = "zip";
    public static final String PHONE = "phone";
    public static final String FAX = "fax";
    public static final String OWENERSHIP_TYPE = "owenership_type";
    public static final String COMPANY_SCALE = "company_scale";
    //自然人主数据crPersonalBaseInfo
    public static final String CR_PERSONAL_BASE_INFO = "crPersonalBaseInfo";
    public static final String SEX = "sex";
    public static final String BIRTH = "birth";
    public static final String AGE = "age";
    public static final String EDUCATION = "education";
    public static final String NATIVE_PLACE = "native_place";
    public static final String HOUSEHOLD_STATUS = "household_status";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String AREA = "area";
    public static final String HOUSING_PROPERTY = "housing_property";
    public static final String WORK_PLACE = "work_place";
    public static final String ENTERPRISE_NATURE = "enterprise_nature";
    public static final String INDUSTRY = "industry";
    public static final String MARITAL_STATUS = "marital_status";
    public static final String ADRESS = "adress";
    public static final String ADMIN_ID = "admin_id";
    public static final String ADMIN_NAME = "admin_name";
    public static final String ADMIN_ORGAN = "admin_organ";
    public static final String CUST_STATUS = "cust_status";
    public static final String JOBS = "jobs";
    public static final String PROFESSION = "profession";

    public static final String ZT_CUS_COMP = "ztCusComp";
    public static final String COMPANY = "company";
    public static final String SPECIAL_SEND_FLAG = "special_send_flag";
    //返回信息
    public static final String RET_CODE = "retCode";
    public static final String SAP_SUCCESS_CODE = "0000";
    public static final String ET_CUS = "et_cus";




    //风险系统编码
    public static String MDM_CODE;

    public static String getMdmCode() {
        return MDM_CODE;
    }


    //获取32位随机数
    public static String getReqTraceId(){
        //14位的当前系统时间(格式为：yyyyMMddHHmmss) + 当前电脑的IP地址的最后两位 + 当前线程的hashCode的前9位 + 7位的随机数
        return UniqId.getInstanceWithLog().getUniqID();
    }

    //获取当前时间戳
    public static String getReqSendTime(){
        return simpleDateFormat.format(new Date());
    }
    //发起方系统编码名称
    public static String getReqSrcSys(){
        return SEND_CODE;
    }


    //接收方系统编码名称
    public static String getReqTarSys(){
        return RES_CODE;
    }



    //PO上定义的服务名称，必须按PO命令规范
    public static String getReqServerName(){
        return SI_VENDOR_MASTERDATA_REQ;
    }

    //是否为异步报文
    public static String getReqSynFlag(String result){
        return result;
    }

    public static JSONObject getVenderResult(JSONObject jsonObject){
        List<String> list = new ArrayList<>();
        list.add("soap:Envelope");
        list.add("soap:Body");
        list.add("ns2:MT_VENDOR_MASTERDATA_RSP");
        list.add("E_RESPONSE");
        list.add("MESSAGE");
        list.add("RETURN_LIST");

        for(int i = 0; i < list.size(); i++){
            if(jsonObject.getJSONObject(list.get(i)) != null){
                jsonObject = jsonObject.getJSONObject(list.get(i));
            }
        }
        return jsonObject;
    }

    public static JSONObject getBpResult(JSONObject jsonObject){
        List<String> list = new ArrayList<>();
        list.add("soap:Envelope");
        list.add("soap:Body");
        list.add("ns2:MT_CUSTORM_MASTERDATA_RSP");
        list.add("E_RESPONSE");
        list.add("MESSAGE");
        list.add("RETURN_LIST");

        for(int i = 0; i < list.size(); i++){
            if(jsonObject.getJSONObject(list.get(i)) != null){
                jsonObject = jsonObject.getJSONObject(list.get(i));
            }
        }
        return jsonObject;
    }

    public static String getMapValue(Map map,String key){
        if(map.get(key) == null){
            return "";
        }
        return map.get(key).toString();
    }

    public static String WITHHOLD_RES_CODE;



    public static String getWithholdResCode() {
        return WITHHOLD_RES_CODE;
    }
}
