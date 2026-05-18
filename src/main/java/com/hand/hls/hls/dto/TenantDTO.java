package com.hand.hls.hls.dto;
import lombok.Data;
import java.util.Date;

@Data
public class TenantDTO {
    private Long id;
    private String name;
    private String contact;
    private String phone;
    private String photoUrl;
    private String address;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
