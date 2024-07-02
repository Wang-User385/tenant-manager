//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.dto;

public class TransformWordToPdfRequestBody {
    private String inputSource = "";
    private String outputFilepath = "";

    public TransformWordToPdfRequestBody() {
    }

    public String getInputSource() {
        return this.inputSource;
    }

    public void setInputSource(String inputSource) {
        this.inputSource = inputSource;
    }

    public String getOutputFilepath() {
        return this.outputFilepath;
    }

    public void setOutputFilepath(String outputFilepath) {
        this.outputFilepath = outputFilepath;
    }

    public String toString() {
        StringBuffer strBuffer = new StringBuffer("TransformWordToPdfRequest");
        return strBuffer.append(" [inputSource=").append(this.inputSource).append(", outputFilepath=").append(this.outputFilepath).append("]").toString();
    }
}
