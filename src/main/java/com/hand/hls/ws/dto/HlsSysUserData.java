package com.hand.hls.ws.dto;

import com.hand.hap.mail.dto.BaseDTO;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;

import javax.persistence.Table;

/**
 * Created by 63171 on 2020/3/17.
 */

@ExtensionAttribute(disable = true)
@Table(name = "sys_user")
public class HlsSysUserData extends BaseDTO {

    private String userName;

    private String description;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user_name) {
        this.userName = user_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
