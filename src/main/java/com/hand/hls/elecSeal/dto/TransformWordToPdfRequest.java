//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.dto;


import com.hand.hls.elecSeal.utils.TransType;

public class TransformWordToPdfRequest extends RequestDto {
    private TransformWordToPdfRequestBody body;

    public TransformWordToPdfRequest() {
        this.setTxCode(TransType.Tx3002.getTxCode());
    }

    public TransformWordToPdfRequestBody getBody() {
        return this.body;
    }

    public void setBody(TransformWordToPdfRequestBody body) {
        this.body = body;
    }

    public String toString() {
        return "TransformWordToPdfRequest [body=" + this.body + ", toString()=" + super.toString() + "]";
    }
}
