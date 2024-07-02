package com.hand.hls.fnd.dto;

/**
 * @author: wenqiang.zhang@hand-china.com
 * @version: 1.0
 * @name: ImpData
 * @description: 导入临时表dto实体类
 * @date: 2017-08-07 10:44
 */

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@ExtensionAttribute(disable=true)
@Table(name = "FND_IMP_DATA")
public class HlsCusImpData extends BaseDTO {

    public static final String FIELD_DATA_ID = "dataId";
    public static final String FIELD_TEMP_CODE = "tempCode";
    public static final String FIELD_IMP_STATUS = "impStatus";
    public static final String FIELD_BATCH_NUM = "batchNum";
    public static final String FIELD_IMP_MSG = "impMsg";
    public static final String FIELD_OBJ_TBL_NAME = "objTblName";
    public static final String FIELD_OBJ_TBL_ID = "objTblId";

    public static final String IMP_STATUS_PARSE_SUCCESS="PS";//解析成功
    public static final String IMP_STATUS_PARSE_FAILURE="PF";//解析失败
    public static final String IMP_STATUS_IMP_SUCCESS="IS";//导入成功
    public static final String IMP_STATUS_IMP_FAILURE="IF";//导入失败
    /**
     * 主键
     */
    @Id
    @GeneratedValue
    private Float dataId;

    /**
     * 模板编码
     */
    private String tempCode;

    /**
     * 导入状态
     */
    private String impStatus;

    /**
     * 批次号，外键
     */
    private Float batchId;

    /**
     * 导入信息
     */
    private String impMsg;

    /**
     * 目标表名
     */
    private String objTblName;

    /**
     * 目标表ID
     */
    private Float objTblId;

    /**
     * 60个临时字段
     */
    private String v1;
    private String v2;
    private String v3;
    private String v4;
    private String v5;
    private String v6;
    private String v7;
    private String v8;
    private String v9;
    private String v10;
    private String v11;
    private String v12;
    private String v13;
    private String v14;
    private String v15;
    private String v16;
    private String v17;
    private String v18;
    private String v19;
    private String v20;
    private String v21;
    private String v22;
    private String v23;
    private String v24;
    private String v25;
    private String v26;
    private String v27;
    private String v28;
    private String v29;
    private String v30;
    private String v31;
    private String v32;
    private String v33;
    private String v34;
    private String v35;
    private String v36;
    private String v37;
    private String v38;
    private String v39;
    private String v40;
    private String v41;
    private String v42;
    private String v43;
    private String v44;
    private String v45;
    private String v46;
    private String v47;
    private String v48;
    private String v49;
    private String v50;
    private String v51;
    private String v52;
    private String v53;
    private String v54;
    private String v55;
    private String v56;
    private String v57;
    private String v58;
    private String v59;
    private String v60;

    public Float getDataId() {
        return dataId;
    }

    public HlsCusImpData setDataId(Float dataId) {
        this.dataId = dataId;
        return this;
    }

    public String getTempCode() {
        return tempCode;
    }

    public HlsCusImpData setTempCode(String tempCode) {
        this.tempCode = tempCode;
        return this;
    }

    public String getImpStatus() {
        return impStatus;
    }

    public HlsCusImpData setImpStatus(String impStatus) {
        this.impStatus = impStatus;
        return this;
    }

    public Float getBatchId() {
        return batchId;
    }

    public HlsCusImpData setBatchId(Float batchId) {
        this.batchId = batchId;
        return this;
    }

    public String getImpMsg() {
        return impMsg;
    }

    public HlsCusImpData setImpMsg(String impMsg) {
        this.impMsg = impMsg;
        return this;
    }

    public HlsCusImpData setImpMsg(List<String> impMsgs){
        /*this.impMsg = StringUtils.join(impMsgs,"]");
        return this;*/
        this.impMsg=null;
        return addImpMsg(impMsgs);
    }

    public HlsCusImpData addImpMsg(String impMsg){
        StringBuffer sb=new StringBuffer();
        impMsg=sb.append('[').append(impMsg).append(']').toString();
        if(impMsg.length()>240){
            impMsg=impMsg.substring(0, 240);
        }
        if(this.impMsg==null){
            this.impMsg=impMsg;
        }else {
            this.impMsg+=impMsg;
        }
        return this;
    }

    public HlsCusImpData addImpMsg (List<String> impMsgs){
        StringBuffer sb=new StringBuffer();
        for(String s:impMsgs){
            sb.append('[').append(s).append(']');
        }
        String message=sb.toString();
        if(message.length()>240){
            message=message.substring(0, 240);
        }
        if(this.impMsg==null){
            this.impMsg=message;
        }else {
            this.impMsg+=message;
        }
        return this;
    }

    public String getObjTblName() {
        return objTblName;
    }

    public HlsCusImpData setObjTblName(String objTblName) {
        this.objTblName = objTblName;
        return this;
    }

    public Float getObjTblId() {
        return objTblId;
    }

    public HlsCusImpData setObjTblId(Float objTblId) {
        this.objTblId = objTblId;
        return this;
    }

    public String getV1() {
        return v1;
    }

    public HlsCusImpData setV1(String v1) {
        this.v1 = v1;
        return this;
    }

    public String getV2() {
        return v2;
    }

    public HlsCusImpData setV2(String v2) {
        this.v2 = v2;
        return this;
    }

    public String getV3() {
        return v3;
    }

    public HlsCusImpData setV3(String v3) {
        this.v3 = v3;
        return this;
    }

    public String getV4() {
        return v4;
    }

    public HlsCusImpData setV4(String v4) {
        this.v4 = v4;
        return this;
    }

    public String getV5() {
        return v5;
    }

    public HlsCusImpData setV5(String v5) {
        this.v5 = v5;
        return this;
    }

    public String getV6() {
        return v6;
    }

    public HlsCusImpData setV6(String v6) {
        this.v6 = v6;
        return this;
    }

    public String getV7() {
        return v7;
    }

    public HlsCusImpData setV7(String v7) {
        this.v7 = v7;
        return this;
    }

    public String getV8() {
        return v8;
    }

    public HlsCusImpData setV8(String v8) {
        this.v8 = v8;
        return this;
    }

    public String getV9() {
        return v9;
    }

    public HlsCusImpData setV9(String v9) {
        this.v9 = v9;
        return this;
    }

    public String getV10() {
        return v10;
    }

    public HlsCusImpData setV10(String v10) {
        this.v10 = v10;
        return this;
    }

    public String getV11() {
        return v11;
    }

    public HlsCusImpData setV11(String v11) {
        this.v11 = v11;
        return this;
    }

    public String getV12() {
        return v12;
    }

    public HlsCusImpData setV12(String v12) {
        this.v12 = v12;
        return this;
    }

    public String getV13() {
        return v13;
    }

    public HlsCusImpData setV13(String v13) {
        this.v13 = v13;
        return this;
    }

    public String getV14() {
        return v14;
    }

    public HlsCusImpData setV14(String v14) {
        this.v14 = v14;
        return this;
    }

    public String getV15() {
        return v15;
    }

    public HlsCusImpData setV15(String v15) {
        this.v15 = v15;
        return this;
    }

    public String getV16() {
        return v16;
    }

    public HlsCusImpData setV16(String v16) {
        this.v16 = v16;
        return this;
    }

    public String getV17() {
        return v17;
    }

    public HlsCusImpData setV17(String v17) {
        this.v17 = v17;
        return this;
    }

    public String getV18() {
        return v18;
    }

    public HlsCusImpData setV18(String v18) {
        this.v18 = v18;
        return this;
    }

    public String getV19() {
        return v19;
    }

    public HlsCusImpData setV19(String v19) {
        this.v19 = v19;
        return this;
    }

    public String getV20() {
        return v20;
    }

    public HlsCusImpData setV20(String v20) {
        this.v20 = v20;
        return this;
    }

    public String getV21() {
        return v21;
    }

    public HlsCusImpData setV21(String v21) {
        this.v21 = v21;
        return this;
    }

    public String getV22() {
        return v22;
    }

    public HlsCusImpData setV22(String v22) {
        this.v22 = v22;
        return this;
    }

    public String getV23() {
        return v23;
    }

    public HlsCusImpData setV23(String v23) {
        this.v23 = v23;
        return this;
    }

    public String getV24() {
        return v24;
    }

    public HlsCusImpData setV24(String v24) {
        this.v24 = v24;
        return this;
    }

    public String getV25() {
        return v25;
    }

    public HlsCusImpData setV25(String v25) {
        this.v25 = v25;
        return this;
    }

    public String getV26() {
        return v26;
    }

    public HlsCusImpData setV26(String v26) {
        this.v26 = v26;
        return this;
    }

    public String getV27() {
        return v27;
    }

    public HlsCusImpData setV27(String v27) {
        this.v27 = v27;
        return this;
    }

    public String getV28() {
        return v28;
    }

    public HlsCusImpData setV28(String v28) {
        this.v28 = v28;
        return this;
    }

    public String getV29() {
        return v29;
    }

    public HlsCusImpData setV29(String v29) {
        this.v29 = v29;
        return this;
    }

    public String getV30() {
        return v30;
    }

    public HlsCusImpData setV30(String v30) {
        this.v30 = v30;
        return this;
    }

    public String getV31() {
        return v31;
    }

    public HlsCusImpData setV31(String v31) {
        this.v31 = v31;
        return this;
    }

    public String getV32() {
        return v32;
    }

    public HlsCusImpData setV32(String v32) {
        this.v32 = v32;
        return this;
    }

    public String getV33() {
        return v33;
    }

    public HlsCusImpData setV33(String v33) {
        this.v33 = v33;
        return this;
    }

    public String getV34() {
        return v34;
    }

    public HlsCusImpData setV34(String v34) {
        this.v34 = v34;
        return this;
    }

    public String getV35() {
        return v35;
    }

    public HlsCusImpData setV35(String v35) {
        this.v35 = v35;
        return this;
    }

    public String getV36() {
        return v36;
    }

    public HlsCusImpData setV36(String v36) {
        this.v36 = v36;
        return this;
    }

    public String getV37() {
        return v37;
    }

    public HlsCusImpData setV37(String v37) {
        this.v37 = v37;
        return this;
    }

    public String getV38() {
        return v38;
    }

    public HlsCusImpData setV38(String v38) {
        this.v38 = v38;
        return this;
    }

    public String getV39() {
        return v39;
    }

    public HlsCusImpData setV39(String v39) {
        this.v39 = v39;
        return this;
    }

    public String getV40() {
        return v40;
    }

    public HlsCusImpData setV40(String v40) {
        this.v40 = v40;
        return this;
    }

    public String getV41() {
        return v41;
    }

    public HlsCusImpData setV41(String v41) {
        this.v41 = v41;
        return this;
    }

    public String getV42() {
        return v42;
    }

    public HlsCusImpData setV42(String v42) {
        this.v42 = v42;
        return this;
    }

    public String getV43() {
        return v43;
    }

    public HlsCusImpData setV43(String v43) {
        this.v43 = v43;
        return this;
    }

    public String getV44() {
        return v44;
    }

    public HlsCusImpData setV44(String v44) {
        this.v44 = v44;
        return this;
    }

    public String getV45() {
        return v45;
    }

    public HlsCusImpData setV45(String v45) {
        this.v45 = v45;
        return this;
    }

    public String getV46() {
        return v46;
    }

    public HlsCusImpData setV46(String v46) {
        this.v46 = v46;
        return this;
    }

    public String getV47() {
        return v47;
    }

    public HlsCusImpData setV47(String v47) {
        this.v47 = v47;
        return this;
    }

    public String getV48() {
        return v48;
    }

    public HlsCusImpData setV48(String v48) {
        this.v48 = v48;
        return this;
    }

    public String getV49() {
        return v49;
    }

    public HlsCusImpData setV49(String v49) {
        this.v49 = v49;
        return this;
    }

    public String getV50() {
        return v50;
    }

    public HlsCusImpData setV50(String v50) {
        this.v50 = v50;
        return this;
    }

    public String getV51() {
        return v51;
    }

    public HlsCusImpData setV51(String v51) {
        this.v51 = v51;
        return this;
    }

    public String getV52() {
        return v52;
    }

    public HlsCusImpData setV52(String v52) {
        this.v52 = v52;
        return this;
    }

    public String getV53() {
        return v53;
    }

    public HlsCusImpData setV53(String v53) {
        this.v53 = v53;
        return this;
    }

    public String getV54() {
        return v54;
    }

    public HlsCusImpData setV54(String v54) {
        this.v54 = v54;
        return this;
    }

    public String getV55() {
        return v55;
    }

    public HlsCusImpData setV55(String v55) {
        this.v55 = v55;
        return this;
    }

    public String getV56() {
        return v56;
    }

    public HlsCusImpData setV56(String v56) {
        this.v56 = v56;
        return this;
    }

    public String getV57() {
        return v57;
    }

    public HlsCusImpData setV57(String v57) {
        this.v57 = v57;
        return this;
    }

    public String getV58() {
        return v58;
    }

    public HlsCusImpData setV58(String v58) {
        this.v58 = v58;
        return this;
    }

    public String getV59() {
        return v59;
    }

    public HlsCusImpData setV59(String v59) {
        this.v59 = v59;
        return this;
    }

    public String getV60() {
        return v60;
    }

    public HlsCusImpData setV60(String v60) {
        this.v60 = v60;
        return this;
    }

    public String getV(int i){
        switch (i){
            case 1: return getV1();
            case 2: return getV2();
            case 3: return getV3();
            case 4: return getV4();
            case 5: return getV5();
            case 6: return getV6();
            case 7: return getV7();
            case 8: return getV8();
            case 9: return getV9();
            case 10: return getV10();
            case 11: return getV11();
            case 12: return getV12();
            case 13: return getV13();
            case 14: return getV14();
            case 15: return getV15();
            case 16: return getV16();
            case 17: return getV17();
            case 18: return getV18();
            case 19: return getV19();
            case 20: return getV20();
            case 21: return getV21();
            case 22: return getV22();
            case 23: return getV23();
            case 24: return getV24();
            case 25: return getV25();
            case 26: return getV26();
            case 27: return getV27();
            case 28: return getV28();
            case 29: return getV29();
            case 30: return getV30();
            case 31: return getV31();
            case 32: return getV32();
            case 33: return getV33();
            case 34: return getV34();
            case 35: return getV35();
            case 36: return getV36();
            case 37: return getV37();
            case 38: return getV38();
            case 39: return getV39();
            case 40: return getV40();
            case 41: return getV41();
            case 42: return getV42();
            case 43: return getV43();
            case 44: return getV44();
            case 45: return getV45();
            case 46: return getV46();
            case 47: return getV47();
            case 48: return getV48();
            case 49: return getV49();
            case 50: return getV50();
            case 51: return getV51();
            case 52: return getV52();
            case 53: return getV53();
            case 54: return getV54();
            case 55: return getV55();
            case 56: return getV56();
            case 57: return getV57();
            case 58: return getV58();
            case 59: return getV59();
            case 60: return getV60();
            default:return null;
        }
    }

    public HlsCusImpData setV(int i, String value){
        switch (i){
            case 1: return setV1(value);
            case 2: return setV2(value);
            case 3: return setV3(value);
            case 4: return setV4(value);
            case 5: return setV5(value);
            case 6: return setV6(value);
            case 7: return setV7(value);
            case 8: return setV8(value);
            case 9: return setV9(value);
            case 10: return setV10(value);
            case 11: return setV11(value);
            case 12: return setV12(value);
            case 13: return setV13(value);
            case 14: return setV14(value);
            case 15: return setV15(value);
            case 16: return setV16(value);
            case 17: return setV17(value);
            case 18: return setV18(value);
            case 19: return setV19(value);
            case 20: return setV20(value);
            case 21: return setV21(value);
            case 22: return setV22(value);
            case 23: return setV23(value);
            case 24: return setV24(value);
            case 25: return setV25(value);
            case 26: return setV26(value);
            case 27: return setV27(value);
            case 28: return setV28(value);
            case 29: return setV29(value);
            case 30: return setV30(value);
            case 31: return setV31(value);
            case 32: return setV32(value);
            case 33: return setV33(value);
            case 34: return setV34(value);
            case 35: return setV35(value);
            case 36: return setV36(value);
            case 37: return setV37(value);
            case 38: return setV38(value);
            case 39: return setV39(value);
            case 40: return setV40(value);
            case 41: return setV41(value);
            case 42: return setV42(value);
            case 43: return setV43(value);
            case 44: return setV44(value);
            case 45: return setV45(value);
            case 46: return setV46(value);
            case 47: return setV47(value);
            case 48: return setV48(value);
            case 49: return setV49(value);
            case 50: return setV50(value);
            case 51: return setV51(value);
            case 52: return setV52(value);
            case 53: return setV53(value);
            case 54: return setV54(value);
            case 55: return setV55(value);
            case 56: return setV56(value);
            case 57: return setV57(value);
            case 58: return setV58(value);
            case 59: return setV59(value);
            case 60: return setV60(value);
            default:return this;
        }
    }

    public HlsCusImpData setV(String key, String value){
        switch (key.trim().toLowerCase()){
            case "v1": return setV1(value);
            case "v2": return setV2(value);
            case "v3": return setV3(value);
            case "v4": return setV4(value);
            case "v5": return setV5(value);
            case "v6": return setV6(value);
            case "v7": return setV7(value);
            case "v8": return setV8(value);
            case "v9": return setV9(value);
            case "v10": return setV10(value);
            case "v11": return setV11(value);
            case "v12": return setV12(value);
            case "v13": return setV13(value);
            case "v14": return setV14(value);
            case "v15": return setV15(value);
            case "v16": return setV16(value);
            case "v17": return setV17(value);
            case "v18": return setV18(value);
            case "v19": return setV19(value);
            case "v20": return setV20(value);
            case "v21": return setV21(value);
            case "v22": return setV22(value);
            case "v23": return setV23(value);
            case "v24": return setV24(value);
            case "v25": return setV25(value);
            case "v26": return setV26(value);
            case "v27": return setV27(value);
            case "v28": return setV28(value);
            case "v29": return setV29(value);
            case "v30": return setV30(value);
            case "v31": return setV31(value);
            case "v32": return setV32(value);
            case "v33": return setV33(value);
            case "v34": return setV34(value);
            case "v35": return setV35(value);
            case "v36": return setV36(value);
            case "v37": return setV37(value);
            case "v38": return setV38(value);
            case "v39": return setV39(value);
            case "v40": return setV40(value);
            case "v41": return setV41(value);
            case "v42": return setV42(value);
            case "v43": return setV43(value);
            case "v44": return setV44(value);
            case "v45": return setV45(value);
            case "v46": return setV46(value);
            case "v47": return setV47(value);
            case "v48": return setV48(value);
            case "v49": return setV49(value);
            case "v50": return setV50(value);
            case "v51": return setV51(value);
            case "v52": return setV52(value);
            case "v53": return setV53(value);
            case "v54": return setV54(value);
            case "v55": return setV55(value);
            case "v56": return setV56(value);
            case "v57": return setV57(value);
            case "v58": return setV58(value);
            case "v59": return setV59(value);
            case "v60": return setV60(value);
            default: return this;
        }
    }

    public String getV(String key){
        switch (key.trim().toLowerCase()){
            case "v1": return getV1();
            case "v2": return getV2();
            case "v3": return getV3();
            case "v4": return getV4();
            case "v5": return getV5();
            case "v6": return getV6();
            case "v7": return getV7();
            case "v8": return getV8();
            case "v9": return getV9();
            case "v10": return getV10();
            case "v11": return getV11();
            case "v12": return getV12();
            case "v13": return getV13();
            case "v14": return getV14();
            case "v15": return getV15();
            case "v16": return getV16();
            case "v17": return getV17();
            case "v18": return getV18();
            case "v19": return getV19();
            case "v20": return getV20();
            case "v21": return getV21();
            case "v22": return getV22();
            case "v23": return getV23();
            case "v24": return getV24();
            case "v25": return getV25();
            case "v26": return getV26();
            case "v27": return getV27();
            case "v28": return getV28();
            case "v29": return getV29();
            case "v30": return getV30();
            case "v31": return getV31();
            case "v32": return getV32();
            case "v33": return getV33();
            case "v34": return getV34();
            case "v35": return getV35();
            case "v36": return getV36();
            case "v37": return getV37();
            case "v38": return getV38();
            case "v39": return getV39();
            case "v40": return getV40();
            case "v41": return getV41();
            case "v42": return getV42();
            case "v43": return getV43();
            case "v44": return getV44();
            case "v45": return getV45();
            case "v46": return getV46();
            case "v47": return getV47();
            case "v48": return getV48();
            case "v49": return getV49();
            case "v50": return getV50();
            case "v51": return getV51();
            case "v52": return getV52();
            case "v53": return getV53();
            case "v54": return getV54();
            case "v55": return getV55();
            case "v56": return getV56();
            case "v57": return getV57();
            case "v58": return getV58();
            case "v59": return getV59();
            case "v60": return getV60();
            default: return null;
        }
    }
}
