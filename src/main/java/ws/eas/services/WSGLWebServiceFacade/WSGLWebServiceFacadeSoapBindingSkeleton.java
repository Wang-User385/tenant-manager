/**
 * WSGLWebServiceFacadeSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.eas.services.WSGLWebServiceFacade;

public class WSGLWebServiceFacadeSoapBindingSkeleton implements WSGLWebServiceFacadeSrvProxy, org.apache.axis.wsdl.Skeleton {
    private WSGLWebServiceFacadeSrvProxy impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "orgNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "acctTypeNum"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("getAsstActType", _params, new javax.xml.namespace.QName("", "getAsstActTypeReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOfArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "getAsstActType"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAsstActType") == null) {
            _myOperations.put("getAsstActType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAsstActType")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "number"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("getOrg", _params, new javax.xml.namespace.QName("", "getOrgReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOfArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "getOrg"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getOrg") == null) {
            _myOperations.put("getOrg", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getOrg")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "orgNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "year"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "period"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "fromRow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "toRow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getVoucher", _params, new javax.xml.namespace.QName("", "getVoucherReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOfArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "getVoucher"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getVoucher") == null) {
            _myOperations.put("getVoucher", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getVoucher")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "companyNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "period"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "voucherNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "fexp"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteVoucher", _params, new javax.xml.namespace.QName("", "deleteVoucherReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "deleteVoucher"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteVoucher") == null) {
            _myOperations.put("deleteVoucher", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteVoucher")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "orgNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "fromRow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "toRow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getAccount", _params, new javax.xml.namespace.QName("", "getAccountReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOfArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "getAccount"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAccount") == null) {
            _myOperations.put("getAccount", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAccount")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "orgNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "year"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "period"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "fromRow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "toRow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getAccountBalance", _params, new javax.xml.namespace.QName("", "getAccountBalanceReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOfArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "getAccountBalance"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAccountBalance") == null) {
            _myOperations.put("getAccountBalance", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAccountBalance")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "comNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "year"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "periodNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "number"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "amount"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"), double.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("checkVoucher", _params, new javax.xml.namespace.QName("", "checkVoucherReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "checkVoucher"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("checkVoucher") == null) {
            _myOperations.put("checkVoucher", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("checkVoucher")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "accountBalanceCols"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOf_tns2_WSWSAccountBalance"), ws.eas.kingdee.WSWSAccountBalance[].class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("importAccountBalance", _params, new javax.xml.namespace.QName("", "importAccountBalanceReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "importAccountBalance"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("importAccountBalance") == null) {
            _myOperations.put("importAccountBalance", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("importAccountBalance")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "initAssistBalanceBalance"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOf_tns2_WSWSAssistBalance"), ws.eas.kingdee.WSWSAssistBalance[].class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("importInitAssistBalance", _params, null);
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "importInitAssistBalance"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("importInitAssistBalance") == null) {
            _myOperations.put("importInitAssistBalance", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("importInitAssistBalance")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "assistBalanceCol"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOf_tns2_WSWSAssistBalance"), ws.eas.kingdee.WSWSAssistBalance[].class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("importAssistBalance", _params, new javax.xml.namespace.QName("", "importAssistBalanceReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "importAssistBalance"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("importAssistBalance") == null) {
            _myOperations.put("importAssistBalance", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("importAssistBalance")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "initAccountBalanceCol"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOf_tns2_WSWSAccountBalance"), ws.eas.kingdee.WSWSAccountBalance[].class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("importInitAccountBalance", _params, new javax.xml.namespace.QName("", "importInitAccountBalanceReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "importInitAccountBalance"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("importInitAccountBalance") == null) {
            _myOperations.put("importInitAccountBalance", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("importInitAccountBalance")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "companyId"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "year"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "periodNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "voucherType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "number"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("exploreVoucher", _params, new javax.xml.namespace.QName("", "exploreVoucherReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "exploreVoucher"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("exploreVoucher") == null) {
            _myOperations.put("exploreVoucher", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("exploreVoucher")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "companyNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "voucherID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteVoucherByID", _params, new javax.xml.namespace.QName("", "deleteVoucherByIDReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "deleteVoucherByID"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteVoucherByID") == null) {
            _myOperations.put("deleteVoucherByID", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteVoucherByID")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "col"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOf_tns3_WSWSVoucher"), ws.eas.client.WSWSVoucher[].class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "isSubmit"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "isVerify"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "isCashflow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("importVoucherOfReturnID", _params, new javax.xml.namespace.QName("", "importVoucherOfReturnIDReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "importVoucherOfReturnID"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("importVoucherOfReturnID") == null) {
            _myOperations.put("importVoucherOfReturnID", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("importVoucherOfReturnID")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "orgNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "asstActTypeNum"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("getAcctTypeDetail", _params, new javax.xml.namespace.QName("", "getAcctTypeDetailReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOfArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "getAcctTypeDetail"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAcctTypeDetail") == null) {
            _myOperations.put("getAcctTypeDetail", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAcctTypeDetail")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "orgnumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "accountNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "year"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "period"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "fromRow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "toRow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getAssitBalance", _params, new javax.xml.namespace.QName("", "getAssitBalanceReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOfArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "getAssitBalance"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAssitBalance") == null) {
            _myOperations.put("getAssitBalance", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAssitBalance")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "comNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "year"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "periodNnumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("findVoucher", _params, new javax.xml.namespace.QName("", "findVoucherReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "findVoucher"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("findVoucher") == null) {
            _myOperations.put("findVoucher", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("findVoucher")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "companyNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "year"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "period"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("deleteBalance", _params, new javax.xml.namespace.QName("", "deleteBalanceReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "deleteBalance"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("deleteBalance") == null) {
            _myOperations.put("deleteBalance", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("deleteBalance")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "col"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOf_tns3_WSWSVoucher"), ws.eas.client.WSWSVoucher[].class, false, false),
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "isSubmit"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "isVerify"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "isCashflow"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("importVoucher", _params, new javax.xml.namespace.QName("", "importVoucherReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "importVoucher"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("importVoucher") == null) {
            _myOperations.put("importVoucher", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("importVoucher")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "orgNumber"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false),
        };
        _oper = new org.apache.axis.description.OperationDesc("getAcctType", _params, new javax.xml.namespace.QName("", "getAcctTypeReturn"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "ArrayOfArrayOf_xsd_string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://webservice.app.gl.fi.eas.kingdee.com", "getAcctType"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAcctType") == null) {
            _myOperations.put("getAcctType", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAcctType")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("WSInvokeException");
        _fault.setQName(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSGLWebServiceFacade", "fault"));
        _fault.setClassName("glwebservicefacade.client.WSInvokeException");
        _fault.setXmlType(new javax.xml.namespace.QName("urn:client.glwebservicefacade", "WSInvokeException"));
        _oper.addFault(_fault);
    }

    public WSGLWebServiceFacadeSoapBindingSkeleton() {
        this.impl = new WSGLWebServiceFacadeSoapBindingImpl();
    }

    public WSGLWebServiceFacadeSoapBindingSkeleton(WSGLWebServiceFacadeSrvProxy impl) {
        this.impl = impl;
    }
    public String[][] getAsstActType(String orgNumber, String acctTypeNum) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[][] ret = impl.getAsstActType(orgNumber, acctTypeNum);
        return ret;
    }

    public String[][] getOrg(String number) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[][] ret = impl.getOrg(number);
        return ret;
    }

    public String[][] getVoucher(String orgNumber, String year, String period, int fromRow, int toRow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[][] ret = impl.getVoucher(orgNumber, year, period, fromRow, toRow);
        return ret;
    }

    public int deleteVoucher(String companyNumber, String period, String voucherNumber, String fexp) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        int ret = impl.deleteVoucher(companyNumber, period, voucherNumber, fexp);
        return ret;
    }

    public String[][] getAccount(String orgNumber, int fromRow, int toRow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[][] ret = impl.getAccount(orgNumber, fromRow, toRow);
        return ret;
    }

    public String[][] getAccountBalance(String orgNumber, String year, String period, int fromRow, int toRow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[][] ret = impl.getAccountBalance(orgNumber, year, period, fromRow, toRow);
        return ret;
    }

    public String checkVoucher(String comNumber, int year, int periodNumber, String number, double amount) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String ret = impl.checkVoucher(comNumber, year, periodNumber, number, amount);
        return ret;
    }

    public String importAccountBalance(ws.eas.kingdee.WSWSAccountBalance[] accountBalanceCols) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String ret = impl.importAccountBalance(accountBalanceCols);
        return ret;
    }

    public void importInitAssistBalance(ws.eas.kingdee.WSWSAssistBalance[] initAssistBalanceBalance) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        impl.importInitAssistBalance(initAssistBalanceBalance);
    }

    public String importAssistBalance(ws.eas.kingdee.WSWSAssistBalance[] assistBalanceCol) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String ret = impl.importAssistBalance(assistBalanceCol);
        return ret;
    }

    public String importInitAccountBalance(ws.eas.kingdee.WSWSAccountBalance[] initAccountBalanceCol) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String ret = impl.importInitAccountBalance(initAccountBalanceCol);
        return ret;
    }

    public String exploreVoucher(String companyId, int year, int periodNumber, String voucherType, String number) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String ret = impl.exploreVoucher(companyId, year, periodNumber, voucherType, number);
        return ret;
    }

    public int deleteVoucherByID(String companyNumber, String voucherID) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        int ret = impl.deleteVoucherByID(companyNumber, voucherID);
        return ret;
    }

    public String[] importVoucherOfReturnID(ws.eas.client.WSWSVoucher[] col, int isSubmit, int isVerify, int isCashflow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[] ret = impl.importVoucherOfReturnID(col, isSubmit, isVerify, isCashflow);
        return ret;
    }

    public String[][] getAcctTypeDetail(String orgNumber, String asstActTypeNum) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[][] ret = impl.getAcctTypeDetail(orgNumber, asstActTypeNum);
        return ret;
    }

    public String[][] getAssitBalance(String orgnumber, String accountNumber, String year, String period, int fromRow, int toRow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[][] ret = impl.getAssitBalance(orgnumber, accountNumber, year, period, fromRow, toRow);
        return ret;
    }

    public String[] findVoucher(String comNumber, int year, int periodNnumber) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[] ret = impl.findVoucher(comNumber, year, periodNnumber);
        return ret;
    }

    public boolean deleteBalance(String companyNumber, int year, int period) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        boolean ret = impl.deleteBalance(companyNumber, year, period);
        return ret;
    }

    public String[] importVoucher(ws.eas.client.WSWSVoucher[] col, int isSubmit, int isVerify, int isCashflow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[] ret = impl.importVoucher(col, isSubmit, isVerify, isCashflow);
        return ret;
    }

    public String[][] getAcctType(String orgNumber) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException
    {
        String[][] ret = impl.getAcctType(orgNumber);
        return ret;
    }

}
