package com.hand.hls.wsdl.utils;

import com.hand.hls.wsdl.components.InterfaceConstantsComponent;
import net.sf.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;

/**
 * @author ferry
 * @date 2019-08-13
 * @description sap接口层常量默认方法接口，用于定义用到的字面量以及一些通用方法
 */

public interface SapConstants extends InterfaceConstants{
    DecimalFormat item_df = new DecimalFormat("00000000");
    DecimalFormat version_df = new DecimalFormat("000");
    Long _0 = 0L;
    Long _1 = 1L;
    Long _8 = 8L;
    Long _9 = 9L;
    Long _11 = 11L;
    Long _13 = 13L;
    Long _60 = 60L;
    Long _61 = 61L;
    Long _65 = 65L;
    Long _66 = 66L;
    Long _67 = 67L;
    Long _100 = 100L;
    Long _101 = 101L;
    int __100 = 100;
    String E103 = "E103";
    String E203 = "E203";
    String E301 = "E301";
    String E801 = "E801";
    String E990 = "E990";
    String E999 = "E999";
    String RECEIPT_ADVANCE_RECEIPT = "RECEIPT_ADVANCE_RECEIPT";
    String ADVANCE_RECEIPT_CREDIT = "ADVANCE_RECEIPT_CREDIT";



    String ID = "ID";
    String TYPE = "TYPE";
    String BILL_ID = "BILL_ID";
    String BUKRS = "BUKRS";
    String DEBIT_NO = "DEBIT_NO";
    String DED_DATE = "DED_DATE";
    String DEBIT_MANE = "DEBIT_MANE";
    String CON_ID = "CON_ID";
    String CON_NO = "CON_NO";
    String CONTAXT = "CONTAXT";
    String WRBTR1 = "WRBTR1";
    String WRBTR2 = "WRBTR2";
    String WRBTR3 = "WRBTR3";
    String WRBTR4 = "WRBTR4";
    String WRBTR5 = "WRBTR5";
    String WRBTR6 = "WRBTR6";
    String BPNUMBER = "BPNUMBER";
    String PER = "PER";
    String ZATTRIBUTE1 = "ZATTRIBUTE1";
    String ZATTRIBUTE2 = "ZATTRIBUTE2";
    String ZATTRIBUTE3 = "ZATTRIBUTE3";
    String ZATTRIBUTE4 = "ZATTRIBUTE4";
    String ZATTRIBUTE5 = "ZATTRIBUTE5";

    String NO_ERP = "NO_ERP";
    String SERIAL_NO = "SERIAL_NO";
    String SERIAL_NO_DEFAULT = "01";
    String SERIAL_NO_ERP = "SERIAL_NO_ERP";
    String DATE = "DATE";
    String BILL_TYPE = "BILL_TYPE";
    String VENDOR_NUM = "VENDOR_NUM";
    String LIFNR = "LIFNR";
    String VENDOR_MANE = "VENDOR_MANE";
    String KUNNR = "KUNNR";
    String KUNNR_SAP = "KUNNR_SAP";
    String KUNNR_TEXT = "KUNNR_TEXT";
    String BANK_ACC = "BANK_ACC";
    String AMT = "AMT";
    String CUR_CODE = "CUR_CODE";
    String PAYMENT_TYPE = "PAYMENT_TYPE";
    String PAYSYSBANKCODE = "PAYSYSBANKCODE";
    String OPP_BANK_NAME = "OPP_BANK_NAME";
    String OPP_BANK_ACC = "OPP_BANK_ACC";
    String OPP_ACC_NAME = "OPP_ACC_NAME";
    String PURPOSE = "PURPOSE";
    String CONSOLIDATED = "CONSOLIDATED";
    String Y = "Y";

    String SERIAL_ID = "SERIAL_ID";
    String BANK_NAME = "BANK_NAME";
    String VERSIONID = "VERSIONID";
    String RE_AUM = "RE_AUM";
    String OPP_ACC_NO = "OPP_ACC_NO";
    String OPP_ACC = "OPP_ACC";
    String TRANS_TIME = "TRANS_TIME";
    String ABS = "ABS";
    String READAPT_FLAG = "READAPT_FLAG";
    String ADVANCE_RECEIPT = "ADVANCE_RECEIPT";
    String X = "X";

    String ITEMNUM = "ITEMNUM";
    String VENDER = "VENDER";
    String TERMINAL_BP = "TERMINAL_BP";
    String TERMINAL_BP_AMT = "TERMINAL_BP_AMT";
    String ZATTRIBUTE6 = "ZATTRIBUTE6";
    String ZATTRIBUTE7 = "ZATTRIBUTE7";
    String ZATTRIBUTE8 = "ZATTRIBUTE8";
    String ZATTRIBUTE9 = "ZATTRIBUTE9";
    String ZATTRIBUTE10 = "ZATTRIBUTE10";



    String OPERTYPE = "OPERTYPE";
    String TFTATA = "TFTATA";
    String DEBIT2_NO = "DEBIT2_NO";
    String COSTID = "COSTID";
    String KOSTL = "KOSTL";
    String STARTDATE = "STARTDATE";
    String OVERDATE = "OVERDATE";
    String IN_RATE = "IN_RATE";
    String TAX_LX = "TAX_LX";
    String TAX_SXF = "TAX_SXF";
    String WAERS = "WAERS";
    String EARLY_OVER = "EARLY_OVER";
    String ZSFCX = "ZSFCX";
    String LEASE = "LEASE";
    String LEASEBACK = "LEASEBACK";
    String RENTPAYPLAN_ITEM = "RENTPAYPLAN_ITEM";
    String FEEPAYPLAN_ITEM = "FEEPAYPLAN_ITEM";
    String PAY_DATE = "PAY_DATE";
    String RENT = "RENT";
    String PRINCIPAL = "PRINCIPAL";
    String INTEREST = "INTEREST";
    String INTEREST_TAX = "INTEREST_TAX";
    String TAX = "TAX";
    String CHANGE = "CHANGE";
    String ITEM_TYPE = "ITEM_TYPE";
    String SERVICE_F = "SERVICE_F";



    String STATUS = "STATUS";
    String INPUT = "INPUT";
    String ITEM = "ITEM";
    String ITMES = "ITMES";
    String IN_TABLE = "IN_TABLE";
    String REQ_BASEINFO = "REQ_BASEINFO";
    String MESSAGE = "MESSAGE";
    String HEAD = "HEAD";
    String S = "S";
    String E = "E";
    //BFS接受失败
    String STATUS_1 ="1";
    //BFS接受成功
    String STATUS_2 ="2";
    //BFS已存在
    String STATUS_3 ="3";
    String PROCESSING = "PROCESSING";


    /**
     * OA待办常量
     */
    String OA_APP_NAME ="APPNAME";
    String OA_MODEL_NAME ="MODELNAME";
    String OA_MODEL_ID ="MODELID";
    String OA_SUBJECT ="SUBJECT";
    String OA_LINK ="LINK";
    String OA_TYPE ="TYPE";
    String OA_OPT_TYPE ="OPTTYPE";
    String OA_KEY ="KEY";
    String OA_PARAM_ONE ="PARAM1";
    String OA_PARAM_TWO ="PARAM2";
    String OA_TARGETS ="TARGETS";
    String OA_CREATE_TIME ="CREATETIME";
    String OA_RETURN_STATE ="RETURNSTATE";
    String OA_MESSAGE ="MESSAGE";

    /**
     *代扣常量
     */
    String REQ_LEASEINCOME_INFO = "REQ_LEASEINCOME_INFO";
    String NIS_WAGE = "NIS_WAGE";
    String NIS_WAGE_DETAIL = "NIS_WAGE_DETAIL";
    String VOUCHER_TYPE = "VOUCHER_TYPE";
    String BATCH_NUMBER = "BATCH_NUMBER";
    String REQ_DATE = "REQ_DATE";
    String WISH_PAYDAY = "WISH_PAYDAY";
    String CORP_CODE = "CORP_CODE";
    String PAYEE_ACC_NO = "PAYEE_ACC_NO";
    String PAYEE_BANK_NAME = "PAYEE_BANK_NAME";
    String PAYEE_ACC_NAME = "PAYEE_ACC_NAME";
    String PAYEE_PROV = "PAYEE_PROV";
	String PAYEE_CITY = "PAYEE_CITY";
	String CUR = "CUR";
	String REMARK = "REMARK";
	String TOTAL = "TOTAL";
	String REVERSE1 = "REVERSE1";
	String REVERSE2 = "REVERSE2";
	String REVERSE3 = "REVERSE3";
	String SUCCESS_AMT = "SUCCESS_AMT";
	String CONTRACT_NO = "CONTRACT_NO";
	String BIF_CODE = "BIF_CODE";
	String BUSINESS_TYPE = "BUSINESS_TYPE";

    String INCOME_MONAY = "INCOME_MONAY";
    String PAYER_ACC_NO = "PAYER_ACC_NO";
    String PAYER_BANK = "PAYER_BANK";
    String PAYER_NAME = "PAYER_NAME";
	String PAYER_PROV = "PAYER_PROV";
	String PAYER_CITY = "PAYER_CITY";
	String DIF_BANK = "DIF_BANK";
	String AREA_SIGN ="AREA_SIGN";
	String REVERSE4 = "REVERSE4";
	String REVERSE5 = "REVERSE5";
	String REVERSE6 = "REVERSE6";


    /**
     *代扣接收常量
     */
    String REQ_LEASEINCOMERESULT_INFO="REQ_LEASEINCOMERESULT_INFO";
	String BIS_EXC="BIS_EXC";
	String BATCH_TRANS_TIME="BATCH_TRANS_TIME";
	String BATCH_STATUS="BATCH_STATUS";
	String BATCH_LOG="BATCH_LOG";
	String RESERVE1="RESERVE1";
	String RESERVE2="RESERVE2";

    String VOUCHER_STAT="VOUCHER_STAT";
    String RETURN_MSG="RETURN_MSG";
    String RETURNED="RETURNED";
    String CONFIRMED="CONFIRMED";
    String RESERVE3="RESERVE3";
    String RESERVE4="RESERVE4";

    /**
     * 发送中
     */
    String SENDING = "SENDING";

    /**
     * 响应成功
     */
    String SUCCESS = "SUCCESS";

    /**
     * 响应失败
     */
    String FAILURE = "FAILURE";

    String SEND_SAP_ERROR = "推送sap失败，请稍后重试！";


    String SOAP_Envelope = "SOAP:Envelope";
    String SOAP_Body = "SOAP:Body";
    String n0_MT_CONTRACTPAY_INFO_RSP = "n0:MT_CONTRACTPAY_INFO_RSP";
    String NS1_MT_INCOME_RESULT_RSP = "ns1:MT_INCOME_RESULT_RSP";
    String E_RESPONSE = "E_RESPONSE";
    String RETURN_LIST = "RETURN_LIST";
    String n0_MT_RECEIPT_APPLY_RSP = "n0:MT_RECEIPT_APPLY_RSP";
    String n0_MT_BAOZHJKOUJIAN_RSP = "n0:MT_BAOZHJKOUJIAN_RSP";
    String n0_MT_DEBIT_MDATA_RSP = "n0:MT_DEBIT_MDATA_RSP";
    String OT_PAYMENT = "OT_PAYMENT";
    String ns1_MT_INCOME_RSP ="ns1:MT_INCOME_RSP";
    String RSP_LEASEINCOME_INFO = "RSP_LEASEINCOME_INFO";

    String NOMINAL_COST_DEDUCT = "NOMINAL_COST_DEDUCT";

    default JSONObject initCommonHeader(){
        JSONObject reqBaseinfo = new JSONObject();
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_TRACE_ID , InterfaceConstantsComponent.getReqTraceId());
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_SEND_TIME , InterfaceConstantsComponent.getReqSendTime());
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_SRC_SYS , InterfaceConstantsComponent.getReqSrcSys());
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_TAR_SYS , InterfaceConstantsComponent.getReqTarSys());
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_SERVER_NAME , InterfaceConstantsComponent.getReqServerName());
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_SYN_FLAG , InterfaceConstantsComponent.getReqSynFlag(InterfaceConstantsComponent.ASYNC));
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_BSN_ID ,EMPTY);
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_RETRY_TIMES ,EMPTY);
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_REPEAT_FLAG ,EMPTY);
        reqBaseinfo.put(InterfaceConstantsComponent.REQ_REPEAT_CYCLE ,EMPTY);
        return reqBaseinfo;
    }

    default JSONObject renderResponseHeader(JSONObject response, String sapce){
        return response.getJSONObject(SOAP_Envelope).getJSONObject(SOAP_Body).getJSONObject(sapce)
                .getJSONObject(E_RESPONSE).getJSONObject(MESSAGE);
    }

    default JSONObject renderResponseResultMessage(JSONObject response){
        return renderResponseHeader(response, n0_MT_CONTRACTPAY_INFO_RSP).getJSONObject(RETURN_LIST);
    }

    default String getInterfaceAddress(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return e.getMessage();
        }
    }
}
