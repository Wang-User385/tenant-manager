/**
 * WSreconciliationCoreFacadeSrvProxyServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.eas.services.WSreconciliationCoreFacade;

public class WSreconciliationCoreFacadeSrvProxyServiceLocator extends org.apache.axis.client.Service implements WSreconciliationCoreFacadeSrvProxyService {

    public WSreconciliationCoreFacadeSrvProxyServiceLocator() {
    }


    public WSreconciliationCoreFacadeSrvProxyServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WSreconciliationCoreFacadeSrvProxyServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WSreconciliationCoreFacade
    private String WSreconciliationCoreFacade_address = "http://168.6.101.28:6888/ormrpc/services/WSreconciliationCoreFacade";

    public String getWSreconciliationCoreFacadeAddress() {
        return WSreconciliationCoreFacade_address;
    }

    // The WSDD service name defaults to the port name.
    private String WSreconciliationCoreFacadeWSDDServiceName = "WSreconciliationCoreFacade";

    public String getWSreconciliationCoreFacadeWSDDServiceName() {
        return WSreconciliationCoreFacadeWSDDServiceName;
    }

    public void setWSreconciliationCoreFacadeWSDDServiceName(String name) {
        WSreconciliationCoreFacadeWSDDServiceName = name;
    }

    public WSreconciliationCoreFacadeSrvProxy getWSreconciliationCoreFacade() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WSreconciliationCoreFacade_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWSreconciliationCoreFacade(endpoint);
    }

    public WSreconciliationCoreFacadeSrvProxy getWSreconciliationCoreFacade(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            WSreconciliationCoreFacadeSoapBindingStub _stub = new WSreconciliationCoreFacadeSoapBindingStub(portAddress, this);
            _stub.setPortName(getWSreconciliationCoreFacadeWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWSreconciliationCoreFacadeEndpointAddress(String address) {
        WSreconciliationCoreFacade_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (WSreconciliationCoreFacadeSrvProxy.class.isAssignableFrom(serviceEndpointInterface)) {
                WSreconciliationCoreFacadeSoapBindingStub _stub = new WSreconciliationCoreFacadeSoapBindingStub(new java.net.URL(WSreconciliationCoreFacade_address), this);
                _stub.setPortName(getWSreconciliationCoreFacadeWSDDServiceName());
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
        if ("WSreconciliationCoreFacade".equals(inputPortName)) {
            return getWSreconciliationCoreFacade();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSreconciliationCoreFacade", "WSreconciliationCoreFacadeSrvProxyService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSreconciliationCoreFacade", "WSreconciliationCoreFacade"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("WSreconciliationCoreFacade".equals(portName)) {
            setWSreconciliationCoreFacadeEndpointAddress(address);
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
