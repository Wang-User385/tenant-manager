/**
 * EASLoginProxyService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.eas.services.EASLogin;

public interface EASLoginProxyService extends javax.xml.rpc.Service {
    public String getEASLoginAddress();

    public EASLoginProxy getEASLogin() throws javax.xml.rpc.ServiceException;

    public EASLoginProxy getEASLogin(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
