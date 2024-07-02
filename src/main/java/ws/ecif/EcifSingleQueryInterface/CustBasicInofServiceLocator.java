/**
 * CustBasicInofServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.ecif.EcifSingleQueryInterface;

public class CustBasicInofServiceLocator extends org.apache.axis.client.Service implements CustBasicInofService {

    public CustBasicInofServiceLocator() {
    }


    public CustBasicInofServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CustBasicInofServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for queryCustDetailBasicInfoPort
    private String queryCustDetailBasicInfoPort_address = "http://168.6.101.12:8080/CIBFL-ECIF/ws/CustBasicInofService/queryCustDetailBasicInfo";

    public String getqueryCustDetailBasicInfoPortAddress() {
        return queryCustDetailBasicInfoPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String queryCustDetailBasicInfoPortWSDDServiceName = "queryCustDetailBasicInfoPort";

    public String getqueryCustDetailBasicInfoPortWSDDServiceName() {
        return queryCustDetailBasicInfoPortWSDDServiceName;
    }

    public void setqueryCustDetailBasicInfoPortWSDDServiceName(String name) {
        queryCustDetailBasicInfoPortWSDDServiceName = name;
    }

    public ICustBasicInofService getqueryCustDetailBasicInfoPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(queryCustDetailBasicInfoPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getqueryCustDetailBasicInfoPort(endpoint);
    }

    public ICustBasicInofService getqueryCustDetailBasicInfoPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            CustBasicInofServiceSoapBindingStub _stub = new CustBasicInofServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getqueryCustDetailBasicInfoPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setqueryCustDetailBasicInfoPortEndpointAddress(String address) {
        queryCustDetailBasicInfoPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ICustBasicInofService.class.isAssignableFrom(serviceEndpointInterface)) {
                CustBasicInofServiceSoapBindingStub _stub = new CustBasicInofServiceSoapBindingStub(new java.net.URL(queryCustDetailBasicInfoPort_address), this);
                _stub.setPortName(getqueryCustDetailBasicInfoPortWSDDServiceName());
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
        if ("queryCustDetailBasicInfoPort".equals(inputPortName)) {
            return getqueryCustDetailBasicInfoPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/CustBasicInofService", "CustBasicInofService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/CustBasicInofService", "queryCustDetailBasicInfoPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("queryCustDetailBasicInfoPort".equals(portName)) {
            setqueryCustDetailBasicInfoPortEndpointAddress(address);
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
