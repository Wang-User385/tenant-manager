package com.hand.hls.ast.constants;

/**
 * description
 *
 * @author 许泽文 2022/11/03 16:12
 */
public enum MessageTransactionStatus {

    P("就绪","P"),
    S("成功","S"),
    F("失败","F");

    private String name;
    private String value;

    MessageTransactionStatus(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
