package com.hand.hls.eas.utils;


import ws.eas.client.WSContext;
import ws.eas.services.Accountingitemcorefacade.WSaccountingItemCoreFacadeSrvProxy;
import ws.eas.services.Accountingitemcorefacade.WSaccountingItemCoreFacadeSrvProxyServiceLocator;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;


/**
 * @Description:金蝶基础资料同步接口
 * @Author: Wangchao
 * @Date: Created om 10:12 2020/5/7
 */

public class WSaccountingItemCoreFacade {

    public String basicDataSynchronization(String url,String param)throws MalformedURLException,ServiceException,RemoteException {
        java.net.URL endpoint=null;
        WSaccountingItemCoreFacadeSrvProxy proxy=null;
        String returnString="";
        try{
            endpoint=new java.net.URL(url);
            proxy=new WSaccountingItemCoreFacadeSrvProxyServiceLocator().getWSaccountingItemCoreFacade(endpoint);
            returnString = proxy.accountingItemCore(param);

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
