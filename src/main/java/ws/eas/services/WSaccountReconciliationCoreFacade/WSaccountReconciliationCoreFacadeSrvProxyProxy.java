package ws.eas.services.WSaccountReconciliationCoreFacade;

public class WSaccountReconciliationCoreFacadeSrvProxyProxy implements WSaccountReconciliationCoreFacadeSrvProxy {
  private String _endpoint = null;
  private WSaccountReconciliationCoreFacadeSrvProxy wSaccountReconciliationCoreFacadeSrvProxy = null;
  
  public WSaccountReconciliationCoreFacadeSrvProxyProxy() {
    _initWSaccountReconciliationCoreFacadeSrvProxyProxy();
  }
  
  public WSaccountReconciliationCoreFacadeSrvProxyProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSaccountReconciliationCoreFacadeSrvProxyProxy();
  }
  
  private void _initWSaccountReconciliationCoreFacadeSrvProxyProxy() {
    try {
      wSaccountReconciliationCoreFacadeSrvProxy = (new WSaccountReconciliationCoreFacadeSrvProxyServiceLocator()).getWSaccountReconciliationCoreFacade();
      if (wSaccountReconciliationCoreFacadeSrvProxy != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSaccountReconciliationCoreFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSaccountReconciliationCoreFacadeSrvProxy)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSaccountReconciliationCoreFacadeSrvProxy != null)
      ((javax.xml.rpc.Stub)wSaccountReconciliationCoreFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public WSaccountReconciliationCoreFacadeSrvProxy getWSaccountReconciliationCoreFacadeSrvProxy() {
    if (wSaccountReconciliationCoreFacadeSrvProxy == null)
      _initWSaccountReconciliationCoreFacadeSrvProxyProxy();
    return wSaccountReconciliationCoreFacadeSrvProxy;
  }
  
  public String accountReconciliationCore(String param) throws java.rmi.RemoteException, ws.eas.client.WSaccountReconciliationCoreFacade.WSInvokeException{
    if (wSaccountReconciliationCoreFacadeSrvProxy == null)
      _initWSaccountReconciliationCoreFacadeSrvProxyProxy();
    return wSaccountReconciliationCoreFacadeSrvProxy.accountReconciliationCore(param);
  }
  
  
}