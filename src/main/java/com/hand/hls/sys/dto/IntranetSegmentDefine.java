package com.hand.hls.sys.dto;



import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import org.hibernate.validator.constraints.Length;
import javax.persistence.Table;
import com.hand.hap.system.dto.BaseDTO;


/**
 * @Description：内网网段定义
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/1 14:44
 * @Version：1.0
 */

@ExtensionAttribute(disable=true)
@Table(name = "SYS_INTRANET_SEGMENT_DEFINE")
public class IntranetSegmentDefine extends BaseDTO {

     public static final String FIELD_INTRANET_DEFINE_ID = "intranetDefineId";
     public static final String FIELD_INTRANET_FROM = "intranetFrom";
     public static final String FIELD_INTRANET_TO = "intranetTo";
     public static final String FIELD_ENABLE_FLAG = "enableFlag";
     public static final String FIELD_DESCRIPTION = "description";


     @Id
     @GeneratedValue
     private Long intranetDefineId;

     @Length(max = 30)
     private String intranetFrom;

     @Length(max = 30)
     private String intranetTo;

     @Length(max = 2)
     private String enableFlag;

     @Length(max = 300)
     private String description;


     public void setIntranetDefineId(Long intranetDefineId){
         this.intranetDefineId = intranetDefineId;
     }

     public Long getIntranetDefineId(){
         return intranetDefineId;
     }

     public void setIntranetFrom(String intranetFrom){
         this.intranetFrom = intranetFrom;
     }

     public String getIntranetFrom(){
         return intranetFrom;
     }

     public void setIntranetTo(String intranetTo){
         this.intranetTo = intranetTo;
     }

     public String getIntranetTo(){
         return intranetTo;
     }

     public void setEnableFlag(String enableFlag){
         this.enableFlag = enableFlag;
     }

     public String getEnableFlag(){
         return enableFlag;
     }

     public void setDescription(String description){
         this.description = description;
     }

     public String getDescription(){
         return description;
     }

     }
