//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.dto;

public class RequestDto {
    private String txCode = "";
    private RequestHead head = null;

    public RequestDto() {
    }

    public RequestHead getHead() {
        return this.head;
    }

    public void setHead(RequestHead head) {
        this.head = head;
    }

    public String getTxCode() {
        return this.txCode;
    }

    public void setTxCode(String txCode) {
        this.txCode = txCode;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("RequestDto");
        return stringBuffer.append(" [txCode=").append(this.txCode).append(", head=").append(this.head).append("]").toString();
    }
}
