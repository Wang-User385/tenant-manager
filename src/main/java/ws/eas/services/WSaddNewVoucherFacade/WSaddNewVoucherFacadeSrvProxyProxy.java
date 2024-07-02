package ws.eas.services.WSaddNewVoucherFacade;

public class WSaddNewVoucherFacadeSrvProxyProxy implements WSaddNewVoucherFacadeSrvProxy {
  private String _endpoint = null;
  private WSaddNewVoucherFacadeSrvProxy wSaddNewVoucherFacadeSrvProxy = null;
  
  public WSaddNewVoucherFacadeSrvProxyProxy() {
    _initWSaddNewVoucherFacadeSrvProxyProxy();
  }
  
  public WSaddNewVoucherFacadeSrvProxyProxy(String endpoint) {
    _endpoint = endpoint;
    _initWSaddNewVoucherFacadeSrvProxyProxy();
  }
  
  private void _initWSaddNewVoucherFacadeSrvProxyProxy() {
    try {
      wSaddNewVoucherFacadeSrvProxy = (new WSaddNewVoucherFacadeSrvProxyServiceLocator()).getWSaddNewVoucherFacade();
      if (wSaddNewVoucherFacadeSrvProxy != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)wSaddNewVoucherFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)wSaddNewVoucherFacadeSrvProxy)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (wSaddNewVoucherFacadeSrvProxy != null)
      ((javax.xml.rpc.Stub)wSaddNewVoucherFacadeSrvProxy)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public WSaddNewVoucherFacadeSrvProxy getWSaddNewVoucherFacadeSrvProxy() {
    if (wSaddNewVoucherFacadeSrvProxy == null)
      _initWSaddNewVoucherFacadeSrvProxyProxy();
    return wSaddNewVoucherFacadeSrvProxy;
  }
  
  public String addNewVoucher(String param) throws java.rmi.RemoteException, ws.eas.client.WSaddNewVoucherFacade.WSInvokeException{
    if (wSaddNewVoucherFacadeSrvProxy == null)
      _initWSaddNewVoucherFacadeSrvProxyProxy();
    return wSaddNewVoucherFacadeSrvProxy.addNewVoucher(param);
  }
  
  
}