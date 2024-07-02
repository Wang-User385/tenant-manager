package ws.eas.services.WSreconciliationCoreFacade;

import ws.eas.client.WSreconciliationCoreFacade.WSInvokeException;

public class WSreconciliationCoreFacadeSrvProxyProxy implements WSreconciliationCoreFacadeSrvProxy {
  private String _endpoint = null;
  private WSreconciliationCoreFacadeSrvProxy wSreconciliationCoreFacadeSrvProxy = null;
  
  public WSreconciliationCoreFacadeSrvProxyProxy() {
    _initWSreconciliationCoreFacadeSrvProxyProxy();
  }
  
  public WSreconciliationCoreFacadeSrvProxyProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSreconciliationCoreFacadeSrvProxyProxy();
  }
  
  private void _initWSreconciliationCoreFacadeSrvProxyProxy() {
    try {
      wSreconciliationCoreFacadeSrvProxy = (new WSreconciliationCoreFacadeSrvProxyServiceLocator()).getWSreconciliationCoreFacade();
      if (wSreconciliationCoreFacadeSrvProxy != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSreconciliationCoreFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSreconciliationCoreFacadeSrvProxy)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSreconciliationCoreFacadeSrvProxy != null)
      ((javax.xml.rpc.Stub)wSreconciliationCoreFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public WSreconciliationCoreFacadeSrvProxy getWSreconciliationCoreFacadeSrvProxy() {
    if (wSreconciliationCoreFacadeSrvProxy == null)
      _initWSreconciliationCoreFacadeSrvProxyProxy();
    return wSreconciliationCoreFacadeSrvProxy;
  }
  
  public String reconciliationCore(String param) throws java.rmi.RemoteException, WSInvokeException {
    if (wSreconciliationCoreFacadeSrvProxy == null)
      _initWSreconciliationCoreFacadeSrvProxyProxy();
    return wSreconciliationCoreFacadeSrvProxy.reconciliationCore(param);
  }
  
  
}