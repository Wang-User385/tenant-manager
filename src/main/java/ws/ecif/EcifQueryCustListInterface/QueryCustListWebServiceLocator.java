/**
 * QueryCustListWebServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.ecif.EcifQueryCustListInterface;

public class QueryCustListWebServiceLocator extends org.apache.axis.client.Service implements QueryCustListWebService {

    public QueryCustListWebServiceLocator() {
    }


    public QueryCustListWebServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public QueryCustListWebServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for queryCustListPort
    private String queryCustListPort_address = "http://168.6.101.12:8080/CIBFL-ECIF/ws/CustBasicInofService/queryCustList";

    public String getqueryCustListPortAddress() {
        return queryCustListPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String queryCustListPortWSDDServiceName = "queryCustListPort";

    public String getqueryCustListPortWSDDServiceName() {
        return queryCustListPortWSDDServiceName;
    }

    public void setqueryCustListPortWSDDServiceName(String name) {
        queryCustListPortWSDDServiceName = name;
    }

    public IQueryCustListWebService getqueryCustListPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(queryCustListPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getqueryCustListPort(endpoint);
    }

    public IQueryCustListWebService getqueryCustListPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            QueryCustListWebServiceSoapBindingStub _stub = new QueryCustListWebServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getqueryCustListPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setqueryCustListPortEndpointAddress(String address) {
        queryCustListPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (IQueryCustListWebService.class.isAssignableFrom(serviceEndpointInterface)) {
                QueryCustListWebServiceSoapBindingStub _stub = new QueryCustListWebServiceSoapBindingStub(new java.net.URL(queryCustListPort_address), this);
                _stub.setPortName(getqueryCustListPortWSDDServiceName());
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
        if ("queryCustListPort".equals(inputPortName)) {
            return getqueryCustListPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/QueryCustListWebService", "QueryCustListWebService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.ws.ecif.cib.com/QueryCustListWebService", "queryCustListPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("queryCustListPort".equals(portName)) {
            setqueryCustListPortEndpointAddress(address);
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
