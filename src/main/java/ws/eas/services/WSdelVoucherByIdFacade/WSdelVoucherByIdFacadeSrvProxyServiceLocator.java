/**
 * WSdelVoucherByIdFacadeSrvProxyServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.eas.services.WSdelVoucherByIdFacade;

public class WSdelVoucherByIdFacadeSrvProxyServiceLocator extends org.apache.axis.client.Service implements WSdelVoucherByIdFacadeSrvProxyService {

    public WSdelVoucherByIdFacadeSrvProxyServiceLocator() {
    }


    public WSdelVoucherByIdFacadeSrvProxyServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public WSdelVoucherByIdFacadeSrvProxyServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for WSdelVoucherByIdFacade
    private String WSdelVoucherByIdFacade_address = "http://168.6.101.28:6888/ormrpc/services/WSdelVoucherByIdFacade";

    public String getWSdelVoucherByIdFacadeAddress() {
        return WSdelVoucherByIdFacade_address;
    }

    // The WSDD service name defaults to the port name.
    private String WSdelVoucherByIdFacadeWSDDServiceName = "WSdelVoucherByIdFacade";

    public String getWSdelVoucherByIdFacadeWSDDServiceName() {
        return WSdelVoucherByIdFacadeWSDDServiceName;
    }

    public void setWSdelVoucherByIdFacadeWSDDServiceName(String name) {
        WSdelVoucherByIdFacadeWSDDServiceName = name;
    }

    public WSdelVoucherByIdFacadeSrvProxy getWSdelVoucherByIdFacade() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(WSdelVoucherByIdFacade_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getWSdelVoucherByIdFacade(endpoint);
    }

    public WSdelVoucherByIdFacadeSrvProxy getWSdelVoucherByIdFacade(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            WSdelVoucherByIdFacadeSoapBindingStub _stub = new WSdelVoucherByIdFacadeSoapBindingStub(portAddress, this);
            _stub.setPortName(getWSdelVoucherByIdFacadeWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setWSdelVoucherByIdFacadeEndpointAddress(String address) {
        WSdelVoucherByIdFacade_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (WSdelVoucherByIdFacadeSrvProxy.class.isAssignableFrom(serviceEndpointInterface)) {
                WSdelVoucherByIdFacadeSoapBindingStub _stub = new WSdelVoucherByIdFacadeSoapBindingStub(new java.net.URL(WSdelVoucherByIdFacade_address), this);
                _stub.setPortName(getWSdelVoucherByIdFacadeWSDDServiceName());
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
        if ("WSdelVoucherByIdFacade".equals(inputPortName)) {
            return getWSdelVoucherByIdFacade();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSdelVoucherByIdFacade", "WSdelVoucherByIdFacadeSrvProxyService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://168.6.101.28:6888/ormrpc/services/WSdelVoucherByIdFacade", "WSdelVoucherByIdFacade"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("WSdelVoucherByIdFacade".equals(portName)) {
            setWSdelVoucherByIdFacadeEndpointAddress(address);
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
