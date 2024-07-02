/**
 * WSaccountReconciliationCoreFacadeSrvProxyServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.eas.services.WSaccountReconciliationCoreFacade;

public class WSaccountReconciliationCoreFacadeSrvProxyServiceLocator extends org.apache.axis.client.Service implements WSaccountReconciliationCoreFacadeSrvProxyService {

    public WSaccountReconciliationCoreFacadeSrvProxyServiceLocator() {
    }


    public WSaccountReconciliationCoreFacadeSrvProxyServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WSaccountReconciliationCoreFacadeSrvProxyServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WSaccountReconciliationCoreFacade
    private String WSaccountReconciliationCoreFacade_address = "http://168.6.101.28:6888/ormrpc/services/WSaccountReconciliationCoreFacade";

    public String getWSaccountReconciliationCoreFacadeAddress() {
        return WSaccountReconciliationCoreFacade_address;
    }

    // The WSDD service name defaults to the port name.
    private String WSaccountReconciliationCoreFacadeWSDDServiceName = "WSaccountReconciliationCoreFacade";

    public String getWSaccountReconciliationCoreFacadeWSDDServiceName() {
        return WSaccountReconciliationCoreFacadeWSDDServiceName;
    }

    public void setWSaccountReconciliationCoreFacadeWSDDServiceName(String name) {
        WSaccountReconciliationCoreFacadeWSDDServiceName = name;
    }

    public WSaccountReconciliationCoreFacadeSrvProxy getWSaccountReconciliationCoreFacade() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WSaccountReconciliationCoreFacade_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWSaccountReconciliationCoreFacade(endpoint);
    }

    public WSaccountReconciliationCoreFacadeSrvProxy getWSaccountReconciliationCoreFacade(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            WSaccountReconciliationCoreFacadeSoapBindingStub _stub = new WSaccountReconciliationCoreFacadeSoapBindingStub(portAddress, this);
            _stub.setPortName(getWSaccountReconciliationCoreFacadeWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWSaccountReconciliationCoreFacadeEndpointAddress(String address) {
        WSaccountReconciliationCoreFacade_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (WSaccountReconciliationCoreFacadeSrvProxy.class.isAssignableFrom(serviceEndpointInterface)) {
                WSaccountReconciliationCoreFacadeSoapBindingStub _stub = new WSaccountReconciliationCoreFacadeSoapBindingStub(new java.net.URL(WSaccountReconciliationCoreFacade_address), this);
                _stub.setPortName(getWSaccountReconciliationCoreFacadeWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("WSaccountReconciliationCoreFacade".equals(inputPortName)) {
            return getWSaccountReconciliationCoreFacade();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSaccountReconciliationCoreFacade", "WSaccountReconciliationCoreFacadeSrvProxyService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSaccountReconciliationCoreFacade", "WSaccountReconciliationCoreFacade"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("WSaccountReconciliationCoreFacade".equals(portName)) {
            setWSaccountReconciliationCoreFacadeEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
