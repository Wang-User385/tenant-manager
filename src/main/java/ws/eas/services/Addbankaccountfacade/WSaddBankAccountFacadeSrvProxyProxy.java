package ws.eas.services.Addbankaccountfacade;

public class WSaddBankAccountFacadeSrvProxyProxy implements WSaddBankAccountFacadeSrvProxy {
  private String _endpoint = null;
  private WSaddBankAccountFacadeSrvProxy wSaddBankAccountFacadeSrvProxy = null;
  
  public WSaddBankAccountFacadeSrvProxyProxy() {
    _initWSaddBankAccountFacadeSrvProxyProxy();
  }
  
  public WSaddBankAccountFacadeSrvProxyProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSaddBankAccountFacadeSrvProxyProxy();
  }
  
  private void _initWSaddBankAccountFacadeSrvProxyProxy() {
    try {
      wSaddBankAccountFacadeSrvProxy = (new WSaddBankAccountFacadeSrvProxyServiceLocator()).getWSaddBankAccountFacade();
      if (wSaddBankAccountFacadeSrvProxy != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSaddBankAccountFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSaddBankAccountFacadeSrvProxy)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSaddBankAccountFacadeSrvProxy != null)
      ((javax.xml.rpc.Stub)wSaddBankAccountFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public WSaddBankAccountFacadeSrvProxy getWSaddBankAccountFacadeSrvProxy() {
    if (wSaddBankAccountFacadeSrvProxy == null)
      _initWSaddBankAccountFacadeSrvProxyProxy();
    return wSaddBankAccountFacadeSrvProxy;
  }
  
  public String addBankAccount(String param) throws java.rmi.RemoteException, ws.eas.client.Accountingitemcorefacade.WSInvokeException{
    if (wSaddBankAccountFacadeSrvProxy == null)
      _initWSaddBankAccountFacadeSrvProxyProxy();
    return wSaddBankAccountFacadeSrvProxy.addBankAccount(param);
  }
  
  
}