package com.hand.hls.elecSeal.dto;

import java.io.File;
import java.util.Map;
 
public class FormParam {
    private String url;
//  private String auth;
//  /**
//   * http请求头里的参数
//   */
//  private Map<String, String> headerParam;
    /**
     * 常规参数
     */
    private Map<String, String> bodyParam;
    /**
     * 待上传的文件参数 filename和file
     */
    private Map<String, File> fileParam;
 
    public String getUrl() {
        return url;
    }
 
    public void setUrl(String url) {
        this.url = url;
    }
 
//  public String getAuth() {
//      return auth;
//  }
//
//  public void setAuth(String auth) {
//      this.auth = auth;
//  }
//
//  public Map<String, String> getHeaderParam() {
//      return headerParam;
//  }
//
//  public void setHeaderParam(Map<String, String> headerParam) {
//      this.headerParam = headerParam;
//  }
 
    public Map<String, String> getBodyParam() {
        return bodyParam;
    }
 
    public void setBodyParam(Map<String, String> bodyParam) {
        this.bodyParam = bodyParam;
    }
 
    public Map<String, File> getFileParam() {
        return fileParam;
    }
 
    public void setFileParam(Map<String, File> fileParam) {
        this.fileParam = fileParam;
    }
}