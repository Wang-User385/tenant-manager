package com.hand.hap.activiti.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.eas.service.IHlsCusEasLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by wangchao on 2020/6/10.
 */
@Component
public class EasVenderBasicSycnExecutor {

    private ExecutorService executor = Executors.newCachedThreadPool();



    @Autowired
    private IHlsCusEasLoginService hlsCusEasLoginService;

    private static IHlsCusEasLoginService service;

    public void fun(IRequest iRequest,Long paymentReqId) throws Exception {

    executor.submit(new Runnable(){
    @Override
    public void run() {
         try {
             service=hlsCusEasLoginService;
             service.easVenderDataSyn(iRequest,paymentReqId);
              }catch(Exception e) {
                e.printStackTrace();
              }
     }
 });
}

}
