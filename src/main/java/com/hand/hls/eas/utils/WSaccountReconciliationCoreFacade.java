package com.hand.hls.eas.utils;


import ws.eas.services.WSaccountReconciliationCoreFacade.WSaccountReconciliationCoreFacadeSrvProxy;
import ws.eas.services.WSaccountReconciliationCoreFacade.WSaccountReconciliationCoreFacadeSrvProxyServiceLocator;
import ws.eas.services.WSdelVoucherByIdFacade.WSdelVoucherByIdFacadeSrvProxy;
import ws.eas.services.WSdelVoucherByIdFacade.WSdelVoucherByIdFacadeSrvProxyServiceLocator;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;


/**
 * @Description:对账同步接口
 * @Author: Wangchao
 * @Date: Created om 10:12 2020/5/15
 */

public class WSaccountReconciliationCoreFacade {

    public String checkAccountSyn(String url,String param)throws MalformedURLException,ServiceException,RemoteException {
        java.net.URL endpoint=null;
        WSaccountReconciliationCoreFacadeSrvProxy proxy=null;
        String returnString="";
        try{
            endpoint=new java.net.URL(url);
            proxy=new WSaccountReconciliationCoreFacadeSrvProxyServiceLocator().getWSaccountReconciliationCoreFacade(endpoint);
            returnString = proxy.accountReconciliationCore(param);
        }
        catch(MalformedURLException e){
            e.printStackTrace();
            throw new MalformedURLException(e.toString());
        }
        catch(ServiceException e){
            e.printStackTrace();
            throw new ServiceException(e);
        }
        catch (RemoteException e) {
            e.printStackTrace();
            throw new RemoteException(e.toString());

        }
        return returnString;
    }
}
