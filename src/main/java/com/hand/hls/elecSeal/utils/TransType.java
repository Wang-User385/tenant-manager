//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;

public enum TransType {
    Tx1001("Tx1001", "POST", "/Tx1001", "上传模板文件"),
    Tx1002("Tx1002", "POST", "/Tx1002", "下载模板文件"),
    Tx1003("Tx1003", "POST", "/Tx1003", "合成业务数据数据到PDF文件"),
    Tx1004("Tx1004", "POST", "/Tx1004", "合成多媒体数据到pdf附件中"),
    Tx1005("Tx1005", "POST", "/Tx1005", "合并多个pdf文件"),
    Tx1006("Tx1006", "POST", "/Tx1006", "PDF文档添加水印"),
    Tx1007("Tx1007", "POST", "/Tx1007", "PDF文档签骑缝图片"),
    Tx1008("Tx1008", "POST", "/Tx1008", "添加机构信息"),
    Tx1009("Tx1009", "POST", "/Tx1009", "更改机构信息"),
    Tx1010("Tx1010", "POST", "/Tx1010", "删除机构信息"),
    Tx1011("Tx1011", "POST", "/Tx1011", "合成多媒体数据到ofd附件中"),
    Tx2001("Tx2001", "POST", "/Tx2001", "制作方形印模图片"),
    Tx2002("Tx2002", "POST", "/Tx2002", "制作长方形印模图片"),
    Tx2003("Tx2003", "POST", "/Tx2003", "制作圆形印模图片"),
    Tx2004("Tx2004", "POST", "/Tx2004", "制作印章"),
    Tx2005("Tx2005", "POST", "/Tx2005", "更新印章"),
    Tx2006("Tx2006", "POST", "/Tx2006", "删除印章"),
    Tx3001("Tx3001", "POST", "/Tx3001", "HTML转换成PDF"),
    Tx3002("Tx3002", "POST", "/Tx3002", "WORD转换成PDF"),
    Tx3003("Tx3003", "POST", "/Tx3003", "IMAGE转换成PDF"),
    Tx3004("Tx3004", "POST", "/Tx3004", "PDF转换成图片"),
    Tx4001("Tx4001", "POST", "/Tx4001", "PDF签章"),
    Tx4002("Tx4002", "POST", "/Tx4002", "PDF复合签章"),
    Tx4003("Tx4003", "POST", "/Tx4003", "PDF列表复合签章"),
    Tx4004("Tx4004", "POST", "/Tx4004", "PDF列表分离式复合签章"),
    Tx4005("Tx4005", "POST", "/Tx4005", "PDF验章"),
    Tx4006("Tx4006", "POST", "/Tx4006", "撤销PDF印章"),
    Tx4007("Tx4007", "POST", "/Tx4007", "计算PDF签章Hash值"),
    Tx4008("Tx4008", "POST", "/Tx4008", "PDF合成外部签名"),
    Tx4009("Tx4009", "POST", "/Tx4009", "PDF合成外部签名并签章"),
    Tx4010("Tx4010", "POST", "/Tx4010", "PDF本地签章"),
    Tx4011("Tx4011", "POST", "/Tx4011", "申请并下载证书"),
    Tx4012("Tx4012", "POST", "/Tx4012", "PDF骑缝章签章"),
    Tx4013("Tx4013", "POST", "/Tx4013", "pdf's 带证据 Hash 签名"),
    Tx4014("Tx4014", "POST", "/Tx4014", "pdf's Hash 签名"),
    Tx4015("Tx4015", "POST", "/Tx4015", "PDF骑缝章全签章"),
    Tx4016("Tx4016", "POST", "/Tx4016", "Web签章"),
    Tx4017("Tx4017", "POST", "/Tx4017", "消息签名"),
    Tx4018("Tx4018", "POST", "/Tx4018", "Web验章"),
    Tx4019("Tx4019", "POST", "/Tx4019", "消息签名验签"),
    Tx4101("Tx4101", "POST", "/Tx4101", "OFD签章"),
    Tx4105("Tx4105", "POST", "/Tx4105", "OFD验章"),
    Tx5001("Tx5001", "POST", "/Tx5001", "空转报文检查通信是否正常"),
    Tx5002("Tx5002", "POST", "/Tx5002", "心跳检测"),
    Tx5003("Tx5003", "GET", "/Tx5003", "心跳检测"),
    Tx5004("Tx5004", "POST", "/Tx5004", "查询P10数量"),
    Tx5005("Tx5005", "POST", "/Tx5005", "查询预植场景证书数量"),
    Tx5006("Tx5006", "GET", "/Tx5006", "心跳检测"),
    Tx5007("Tx5007", "GET", "/Tx5007", "心跳检测"),
    Tx6001("Tx6001", "POST", "/Tx6001", "查询合同信息"),
    Tx6002("Tx6002", "POST", "/Tx6002", "下载合同文件"),
    Tx6003("Tx6003", "POST", "/Tx6003", "查询印章信息"),
    Tx8001("Tx8001", "GET", "/Tx8001", "合成业务数据及复合签章");

    String txCode = "";
    String httpMethod = "";
    String appendUrl = "";
    String description = "";

    private TransType(String txCode, String httpMethod, String appendUrl, String description) {
        this.txCode = txCode;
        this.httpMethod = httpMethod;
        this.appendUrl = appendUrl;
        this.description = description;
    }

    public String getTxCode() {
        return this.txCode;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public String getAppendUrl() {
        return this.appendUrl;
    }

    public String getDescription() {
        return this.description;
    }

    public static TransType getTransType(String txCode) {
        TransType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            TransType txtype = var1[var3];
            if (txtype.getTxCode().equals(txCode)) {
                return txtype;
            }
        }

        return null;
    }

    public static String getTxCode(String httpMethod, String appendUrl) {
        TransType[] var2 = values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            TransType txtype = var2[var4];
            if (txtype.getHttpMethod().equals(httpMethod) && txtype.getAppendUrl().equals(appendUrl)) {
                return txtype.getTxCode();
            }
        }

        return null;
    }
}
