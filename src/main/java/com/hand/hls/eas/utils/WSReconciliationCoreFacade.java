package com.hand.hls.eas.utils;


import ws.eas.services.WSreconciliationCoreFacade.WSreconciliationCoreFacadeSrvProxy;
import ws.eas.services.WSreconciliationCoreFacade.WSreconciliationCoreFacadeSrvProxyServiceLocator;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;


/**
 * @Description:新对账同步接口
 * @Author: Wangchao
 * @Date: Created om 10:12 2020/5/15
 */

public class WSReconciliationCoreFacade {

    public String checkAccountSyn(String url,String param)throws MalformedURLException,ServiceException,RemoteException {
        java.net.URL endpoint=null;
        WSreconciliationCoreFacadeSrvProxy proxy=null;
        String returnString="";
        try{
            endpoint=new java.net.URL(url);
            proxy=new WSreconciliationCoreFacadeSrvProxyServiceLocator().getWSreconciliationCoreFacade(endpoint);
            returnString = proxy.reconciliationCore(param);
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
