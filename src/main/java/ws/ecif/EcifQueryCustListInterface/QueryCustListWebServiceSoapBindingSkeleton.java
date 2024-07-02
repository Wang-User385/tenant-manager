/**
 * QueryCustListWebServiceSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.ecif.EcifQueryCustListInterface;

public class QueryCustListWebServiceSoapBindingSkeleton implements IQueryCustListWebService, org.apache.axis.wsdl.Skeleton {
    private IQueryCustListWebService impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "BusinessDate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("queryCustList", _params, new javax.xml.namespace.QName("", "customer"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/QueryCustListWebService", "customer"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/QueryCustListWebService", "queryCustList"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("queryCustList") == null) {
            _myOperations.put("queryCustList", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("queryCustList")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("Exception");
        _fault.setQName(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/QueryCustListWebService", "Exception"));
        _fault.setClassName("Exception");
        _fault.setXmlType(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/QueryCustListWebService", "Exception"));
        _oper.addFault(_fault);
    }

    public QueryCustListWebServiceSoapBindingSkeleton() {
        this.impl = new QueryCustListWebServiceSoapBindingImpl();
    }

    public QueryCustListWebServiceSoapBindingSkeleton(IQueryCustListWebService impl) {
        this.impl = impl;
    }
    public Customer[] queryCustList(String businessDate) throws java.rmi.RemoteException, Exception
    {
        Customer[] ret = impl.queryCustList(businessDate);
        return ret;
    }

}
