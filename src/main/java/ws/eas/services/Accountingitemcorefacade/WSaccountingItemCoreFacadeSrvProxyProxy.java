package ws.eas.services.Accountingitemcorefacade;

public class WSaccountingItemCoreFacadeSrvProxyProxy implements WSaccountingItemCoreFacadeSrvProxy {
  private String _endpoint = null;
  private WSaccountingItemCoreFacadeSrvProxy wSaccountingItemCoreFacadeSrvProxy = null;
  
  public WSaccountingItemCoreFacadeSrvProxyProxy() {
    _initWSaccountingItemCoreFacadeSrvProxyProxy();
  }
  
  public WSaccountingItemCoreFacadeSrvProxyProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSaccountingItemCoreFacadeSrvProxyProxy();
  }
  
  private void _initWSaccountingItemCoreFacadeSrvProxyProxy() {
    try {
      wSaccountingItemCoreFacadeSrvProxy = (new WSaccountingItemCoreFacadeSrvProxyServiceLocator()).getWSaccountingItemCoreFacade();
      if (wSaccountingItemCoreFacadeSrvProxy != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSaccountingItemCoreFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSaccountingItemCoreFacadeSrvProxy)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSaccountingItemCoreFacadeSrvProxy != null)
      ((javax.xml.rpc.Stub)wSaccountingItemCoreFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public WSaccountingItemCoreFacadeSrvProxy getWSaccountingItemCoreFacadeSrvProxy() {
    if (wSaccountingItemCoreFacadeSrvProxy == null)
      _initWSaccountingItemCoreFacadeSrvProxyProxy();
    return wSaccountingItemCoreFacadeSrvProxy;
  }
  
  public String accountingItemCore(String param) throws java.rmi.RemoteException, ws.eas.client.Accountingitemcorefacade.WSInvokeException{
    if (wSaccountingItemCoreFacadeSrvProxy == null)
      _initWSaccountingItemCoreFacadeSrvProxyProxy();
    return wSaccountingItemCoreFacadeSrvProxy.accountingItemCore(param);
  }
  
  
}