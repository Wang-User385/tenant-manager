package ws.eas.services.WSdelVoucherByIdFacade;

public class WSdelVoucherByIdFacadeSrvProxyProxy implements WSdelVoucherByIdFacadeSrvProxy {
  private String _endpoint = null;
  private WSdelVoucherByIdFacadeSrvProxy wSdelVoucherByIdFacadeSrvProxy = null;
  
  public WSdelVoucherByIdFacadeSrvProxyProxy() {
    _initWSdelVoucherByIdFacadeSrvProxyProxy();
  }
  
  public WSdelVoucherByIdFacadeSrvProxyProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSdelVoucherByIdFacadeSrvProxyProxy();
  }
  
  private void _initWSdelVoucherByIdFacadeSrvProxyProxy() {
    try {
      wSdelVoucherByIdFacadeSrvProxy = (new WSdelVoucherByIdFacadeSrvProxyServiceLocator()).getWSdelVoucherByIdFacade();
      if (wSdelVoucherByIdFacadeSrvProxy != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSdelVoucherByIdFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSdelVoucherByIdFacadeSrvProxy)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSdelVoucherByIdFacadeSrvProxy != null)
      ((javax.xml.rpc.Stub)wSdelVoucherByIdFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public WSdelVoucherByIdFacadeSrvProxy getWSdelVoucherByIdFacadeSrvProxy() {
    if (wSdelVoucherByIdFacadeSrvProxy == null)
      _initWSdelVoucherByIdFacadeSrvProxyProxy();
    return wSdelVoucherByIdFacadeSrvProxy;
  }
  
  public String delVoucherById(String voucherId, String comOrgNum) throws java.rmi.RemoteException, ws.eas.client.WSdelVoucherByIdFacade.WSInvokeException{
    if (wSdelVoucherByIdFacadeSrvProxy == null)
      _initWSdelVoucherByIdFacadeSrvProxyProxy();
    return wSdelVoucherByIdFacadeSrvProxy.delVoucherById(voucherId, comOrgNum);
  }
  
  
}