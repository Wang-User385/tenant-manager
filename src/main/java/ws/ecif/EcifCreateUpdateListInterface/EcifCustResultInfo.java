/**
 * EcifCustResultInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.ecif.EcifCreateUpdateListInterface;

public class EcifCustResultInfo  implements java.io.Serializable {
    private String code;

    private String cstNmS;

    private String cstNo;

    private String cstTp;

    private String identNo;

    private String instIdentTp;

    private String msg;

    private String oprtnInd;

    private String retCd;

    public EcifCustResultInfo() {
    }

    public EcifCustResultInfo(
           String code,
           String cstNmS,
           String cstNo,
           String cstTp,
           String identNo,
           String instIdentTp,
           String msg,
           String oprtnInd,
           String retCd) {
           this.code = code;
           this.cstNmS = cstNmS;
           this.cstNo = cstNo;
           this.cstTp = cstTp;
           this.identNo = identNo;
           this.instIdentTp = instIdentTp;
           this.msg = msg;
           this.oprtnInd = oprtnInd;
           this.retCd = retCd;
    }


    /**
     * Gets the code value for this EcifCustResultInfo.
     * 
     * @return code
     */
    public String getCode() {
        return code;
    }


    /**
     * Sets the code value for this EcifCustResultInfo.
     * 
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }


    /**
     * Gets the cstNmS value for this EcifCustResultInfo.
     * 
     * @return cstNmS
     */
    public String getCstNmS() {
        return cstNmS;
    }


    /**
     * Sets the cstNmS value for this EcifCustResultInfo.
     * 
     * @param cstNmS
     */
    public void setCstNmS(String cstNmS) {
        this.cstNmS = cstNmS;
    }


    /**
     * Gets the cstNo value for this EcifCustResultInfo.
     * 
     * @return cstNo
     */
    public String getCstNo() {
        return cstNo;
    }


    /**
     * Sets the cstNo value for this EcifCustResultInfo.
     * 
     * @param cstNo
     */
    public void setCstNo(String cstNo) {
        this.cstNo = cstNo;
    }


    /**
     * Gets the cstTp value for this EcifCustResultInfo.
     * 
     * @return cstTp
     */
    public String getCstTp() {
        return cstTp;
    }


    /**
     * Sets the cstTp value for this EcifCustResultInfo.
     * 
     * @param cstTp
     */
    public void setCstTp(String cstTp) {
        this.cstTp = cstTp;
    }


    /**
     * Gets the identNo value for this EcifCustResultInfo.
     * 
     * @return identNo
     */
    public String getIdentNo() {
        return identNo;
    }


    /**
     * Sets the identNo value for this EcifCustResultInfo.
     * 
     * @param identNo
     */
    public void setIdentNo(String identNo) {
        this.identNo = identNo;
    }


    /**
     * Gets the instIdentTp value for this EcifCustResultInfo.
     * 
     * @return instIdentTp
     */
    public String getInstIdentTp() {
        return instIdentTp;
    }


    /**
     * Sets the instIdentTp value for this EcifCustResultInfo.
     * 
     * @param instIdentTp
     */
    public void setInstIdentTp(String instIdentTp) {
        this.instIdentTp = instIdentTp;
    }


    /**
     * Gets the msg value for this EcifCustResultInfo.
     * 
     * @return msg
     */
    public String getMsg() {
        return msg;
    }


    /**
     * Sets the msg value for this EcifCustResultInfo.
     * 
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }


    /**
     * Gets the oprtnInd value for this EcifCustResultInfo.
     * 
     * @return oprtnInd
     */
    public String getOprtnInd() {
        return oprtnInd;
    }


    /**
     * Sets the oprtnInd value for this EcifCustResultInfo.
     * 
     * @param oprtnInd
     */
    public void setOprtnInd(String oprtnInd) {
        this.oprtnInd = oprtnInd;
    }


    /**
     * Gets the retCd value for this EcifCustResultInfo.
     * 
     * @return retCd
     */
    public String getRetCd() {
        return retCd;
    }


    /**
     * Sets the retCd value for this EcifCustResultInfo.
     * 
     * @param retCd
     */
    public void setRetCd(String retCd) {
        this.retCd = retCd;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof EcifCustResultInfo)) return false;
        EcifCustResultInfo other = (EcifCustResultInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.code==null && other.getCode()==null) || 
             (this.code!=null &&
              this.code.equals(other.getCode()))) &&
            ((this.cstNmS==null && other.getCstNmS()==null) || 
             (this.cstNmS!=null &&
              this.cstNmS.equals(other.getCstNmS()))) &&
            ((this.cstNo==null && other.getCstNo()==null) || 
             (this.cstNo!=null &&
              this.cstNo.equals(other.getCstNo()))) &&
            ((this.cstTp==null && other.getCstTp()==null) || 
             (this.cstTp!=null &&
              this.cstTp.equals(other.getCstTp()))) &&
            ((this.identNo==null && other.getIdentNo()==null) || 
             (this.identNo!=null &&
              this.identNo.equals(other.getIdentNo()))) &&
            ((this.instIdentTp==null && other.getInstIdentTp()==null) || 
             (this.instIdentTp!=null &&
              this.instIdentTp.equals(other.getInstIdentTp()))) &&
            ((this.msg==null && other.getMsg()==null) || 
             (this.msg!=null &&
              this.msg.equals(other.getMsg()))) &&
            ((this.oprtnInd==null && other.getOprtnInd()==null) || 
             (this.oprtnInd!=null &&
              this.oprtnInd.equals(other.getOprtnInd()))) &&
            ((this.retCd==null && other.getRetCd()==null) || 
             (this.retCd!=null &&
              this.retCd.equals(other.getRetCd())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getCode() != null) {
            _hashCode += getCode().hashCode();
        }
        if (getCstNmS() != null) {
            _hashCode += getCstNmS().hashCode();
        }
        if (getCstNo() != null) {
            _hashCode += getCstNo().hashCode();
        }
        if (getCstTp() != null) {
            _hashCode += getCstTp().hashCode();
        }
        if (getIdentNo() != null) {
            _hashCode += getIdentNo().hashCode();
        }
        if (getInstIdentTp() != null) {
            _hashCode += getInstIdentTp().hashCode();
        }
        if (getMsg() != null) {
            _hashCode += getMsg().hashCode();
        }
        if (getOprtnInd() != null) {
            _hashCode += getOprtnInd().hashCode();
        }
        if (getRetCd() != null) {
            _hashCode += getRetCd().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EcifCustResultInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://web.ecif.cib.com/EcifNoCreateCustServiceImpl", "ecifCustResultInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("code");
        elemField.setXmlName(new javax.xml.namespace.QName("", "code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cstNmS");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cstNmS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cstNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cstNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cstTp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cstTp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "identNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("instIdentTp");
        elemField.setXmlName(new javax.xml.namespace.QName("", "instIdentTp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msg");
        elemField.setXmlName(new javax.xml.namespace.QName("", "msg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oprtnInd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "oprtnInd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retCd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "retCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
