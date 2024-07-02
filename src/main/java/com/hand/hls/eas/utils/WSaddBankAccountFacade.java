package com.hand.hls.eas.utils;


import ws.eas.services.Addbankaccountfacade.WSaddBankAccountFacadeSrvProxy;
import ws.eas.services.Addbankaccountfacade.WSaddBankAccountFacadeSrvProxyServiceLocator;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;


/**
 * @Description:银行账户同步接口
 * @Author: Wangchao
 * @Date: Created om 10:12 2020/5/7
 */

public class WSaddBankAccountFacade {

    public String bankAccountSynchronization(String url,String param)throws MalformedURLException,ServiceException,RemoteException {
        java.net.URL endpoint=null;
        WSaddBankAccountFacadeSrvProxy proxy=null;
        String returnString="";
        try{
            endpoint=new java.net.URL(url);
            proxy=new WSaddBankAccountFacadeSrvProxyServiceLocator().getWSaddBankAccountFacade(endpoint);
            returnString = proxy.addBankAccount(param);
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
