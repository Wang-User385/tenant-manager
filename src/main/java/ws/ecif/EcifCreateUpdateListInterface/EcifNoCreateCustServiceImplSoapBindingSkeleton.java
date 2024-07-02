/**
 * EcifNoCreateCustServiceImplSoapBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.ecif.EcifCreateUpdateListInterface;

public class EcifNoCreateCustServiceImplSoapBindingSkeleton implements EcifNoCreateCustService, org.apache.axis.wsdl.Skeleton {
    private EcifNoCreateCustService impl;
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
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "customer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://web.ecif.cib.com/EcifNoCreateCustServiceImpl", "ecifNoCreateCustVO"), EcifNoCreateCustVO[].class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("custCreateUpdateList", _params, new javax.xml.namespace.QName("", "custCreateMofyDeleOperObj"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://web.ecif.cib.com/EcifNoCreateCustServiceImpl", "ecifCustResultInfo"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://web.ecif.cib.com/EcifNoCreateCustServiceImpl", "custCreateUpdateList"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("custCreateUpdateList") == null) {
            _myOperations.put("custCreateUpdateList", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("custCreateUpdateList")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("Exception");
        _fault.setQName(new javax.xml.namespace.QName("http://web.ecif.cib.com/EcifNoCreateCustServiceImpl", "Exception"));
        _fault.setClassName("Exception");
        _fault.setXmlType(new javax.xml.namespace.QName("http://web.ecif.cib.com/EcifNoCreateCustServiceImpl", "Exception"));
        _oper.addFault(_fault);
    }

    public EcifNoCreateCustServiceImplSoapBindingSkeleton() {
        this.impl = new EcifNoCreateCustServiceImplSoapBindingImpl();
    }

    public EcifNoCreateCustServiceImplSoapBindingSkeleton(EcifNoCreateCustService impl) {
        this.impl = impl;
    }
    public EcifCustResultInfo[] custCreateUpdateList(EcifNoCreateCustVO[] customer) throws java.rmi.RemoteException, Exception
    {
        EcifCustResultInfo[] ret = impl.custCreateUpdateList(customer);
        return ret;
    }

}
