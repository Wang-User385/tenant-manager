/**
 * IQueryCustListWebService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.ecif.EcifQueryCustListInterface;

public interface IQueryCustListWebService extends java.rmi.Remote {
    public Customer[] queryCustList(String businessDate) throws java.rmi.RemoteException, Exception;
}
