/**
 * CustBasicInofServiceSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.ecif.EcifSingleQueryInterface;

public class CustBasicInofServiceSoapBindingSkeleton implements ICustBasicInofService, org.apache.axis.wsdl.Skeleton {
    private ICustBasicInofService impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "oprtnInd"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "cstNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "unifdSoclCrdtCd"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "cstNmS"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "identNo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "instIdentTp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("queryCustDetailBasicInfo", _params, new javax.xml.namespace.QName("", "customer"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/CustBasicInofService", "customer"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/CustBasicInofService", "queryCustDetailBasicInfo"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("queryCustDetailBasicInfo") == null) {
            _myOperations.put("queryCustDetailBasicInfo", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("queryCustDetailBasicInfo")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("Exception");
        _fault.setQName(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/CustBasicInofService", "Exception"));
        _fault.setClassName("Exception");
        _fault.setXmlType(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/CustBasicInofService", "Exception"));
        _oper.addFault(_fault);
    }

    public CustBasicInofServiceSoapBindingSkeleton() {
        this.impl = new CustBasicInofServiceSoapBindingImpl();
    }

    public CustBasicInofServiceSoapBindingSkeleton(ICustBasicInofService impl) {
        this.impl = impl;
    }
    public Customer queryCustDetailBasicInfo(String oprtnInd, String cstNo, String unifdSoclCrdtCd, String cstNmS, String identNo, String instIdentTp) throws java.rmi.RemoteException, Exception
    {
        Customer ret = impl.queryCustDetailBasicInfo(oprtnInd, cstNo, unifdSoclCrdtCd, cstNmS, identNo, instIdentTp);
        return ret;
    }

}
