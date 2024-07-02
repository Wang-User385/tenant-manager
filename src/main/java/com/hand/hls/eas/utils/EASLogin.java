package com.hand.hls.eas.utils;


import ws.eas.client.WSContext;
import ws.eas.services.EASLogin.EASLoginProxy;
import ws.eas.services.EASLogin.EASLoginProxyServiceLocator;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;


/**
 * @Description:金蝶登录接口
 * @Author: Wangchao
 * @Date: Created om 10:12 2020/4/23
 */
public class EASLogin {

    public WSContext easDoLogin(String url,String userName,String password,String slnName,String dcName,String language,int dbType)throws MalformedURLException,ServiceException,RemoteException {
        java.net.URL endpoint=null;
        EASLoginProxy proxy=null;
        WSContext cxt = null;
        try{
            endpoint=new java.net.URL(url);
            proxy=new EASLoginProxyServiceLocator().getEASLogin(endpoint);
            cxt = proxy.login(userName, password, slnName, dcName, language, dbType);

        }
        catch(MalformedURLException e){
            e.printStackTrace();
            cxt = null;
            throw new MalformedURLException(e.toString());
        }
        catch(ServiceException e){
            e.printStackTrace();
            cxt = null;
            throw new ServiceException(e);
        }
        catch (RemoteException e) {
            e.printStackTrace();
            cxt = null;
            throw new RemoteException(e.toString());

        }
        return cxt;
    }
}
