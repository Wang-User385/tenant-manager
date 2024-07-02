/**
 * EASLoginProxy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.eas.services.EASLogin;

public interface EASLoginProxy extends java.rmi.Remote {
    public ws.eas.client.WSContext login(String userName, String password, String slnName, String dcName, String language, int dbType) throws java.rmi.RemoteException;
    public ws.eas.client.WSContext login(String userName, String password, String slnName, String dcName, String language, int dbType, String authPattern, int isEncodePwd) throws java.rmi.RemoteException;
    public ws.eas.client.WSContext login(String userName, String password, String slnName, String dcName, String language, int dbType, String authPattern) throws java.rmi.RemoteException;
    public boolean logout(String userName, String slnName, String dcName, String language) throws java.rmi.RemoteException;
}
