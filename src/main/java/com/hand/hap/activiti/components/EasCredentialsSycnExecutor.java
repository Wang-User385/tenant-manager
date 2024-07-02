package com.hand.hap.activiti.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.eas.dto.HlsCusPsotEasTmp;
import com.hand.hls.eas.service.IHlsCusPsotEasTmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by wangchao on 2020/6/10.
 */
@Component
public class EasCredentialsSycnExecutor {

    private ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    private IHlsCusPsotEasTmpService hlsCusPsotEasTmpService;

    private static IHlsCusPsotEasTmpService service;

    public void fun(IRequest iRequest,HlsCusPsotEasTmp dto) throws Exception {

    executor.submit(new Runnable(){
    @Override
    public void run() {
         try {
             service=hlsCusPsotEasTmpService;
             service.gldPost(iRequest,dto);
              }catch(Exception e) {
                e.printStackTrace();
              }
     }
 });
}

}
