package com.hand.hap.core.components;

import com.hand.hap.core.BaseConstants;
import com.hand.hap.message.profile.SystemConfigListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description：根据用户锁定规则，验证失败用户名冻结用户
 * @Author：liangxian.chen@hand-china.com
 * @Date：2023/3/30 15:09
 * @Version：1.0
 */
@Component
public class BlockUserConfig implements SystemConfigListener,BaseConstants {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_BLOCK_KEY = HAP_CACHE + "BLOCK:";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 错误多少次锁住用户，默认5次
     */
    private Integer blockTimes = 5;


    /**
     * 锁定用户多久，默认0分钟不锁定
     */
    private Long blockMinutes = 0L;

    @Override
    public List<String> getAcceptedProfiles() {
        return Arrays.asList("BLOCK_TIMES", "BLOCK_MINUTES");
    }

    @Override
    public void updateProfile(String profileName, String profileValue) {
        try {
            if ("BLOCK_TIMES".equalsIgnoreCase(profileName) && StringUtils.isNotEmpty(profileValue)) {
                this.blockTimes = Integer.parseInt(profileValue);
            }
            if ("BLOCK_MINUTES".equalsIgnoreCase(profileName) && StringUtils.isNotEmpty(profileValue)) {
                this.blockMinutes = Long.parseLong(profileValue);
            }
        }catch(Exception e){
            logger.error("profileName:{},profileValue:{}",profileName,profileValue,e);
        }
    }

    /**
     * 更新登录错误次数
     */
    public String updateBlockTimes(HttpServletRequest request) {
        try {
            String userName = request.getParameter("username");
            if(StringUtils.isNotEmpty(userName) && this.blockMinutes > 0){
                printInfo(request);
                String failureTimes = redisTemplate.opsForValue().get(REDIS_BLOCK_KEY+userName);
                if (failureTimes == null) {
                    redisTemplate.opsForValue().set(REDIS_BLOCK_KEY+userName, "1", this.blockMinutes, TimeUnit.MINUTES);
                } else {
                    Integer times = Integer.parseInt(failureTimes);
                    if (times < this.blockTimes) {
                        redisTemplate.opsForValue().increment(REDIS_BLOCK_KEY+userName, 1);
                        redisTemplate.expire(REDIS_BLOCK_KEY+userName, this.blockMinutes, TimeUnit.MINUTES);
                        if(times >= 1){
                            int surplusTimes = this.blockTimes - times;
                            return "密码错误,剩余" + surplusTimes + "次。";
                        }
                    }else{
                        return "用户已锁定，请"  + (redisTemplate.opsForValue().getOperations().getExpire(REDIS_BLOCK_KEY+userName,TimeUnit.MINUTES) + 1) +  "分钟后重试。";
                    }
                }
            }
        } catch (Exception e) {
            logger.error("updateBlockTimes",e);
        }
        return null;
    }


    /**
     * 查询用户是否锁定
     * @param request 请求信息
     * @return  true为锁定
     */
    public boolean checkBlock(HttpServletRequest request) {
        try {
            String userName = request.getParameter("username");
            if(StringUtils.isNotEmpty(userName) && this.blockMinutes > 0){
                String failureTimes = redisTemplate.opsForValue().get(REDIS_BLOCK_KEY+userName);
                if(failureTimes != null && Integer.parseInt(failureTimes) >= this.blockTimes){
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error("checkBlock",e);
        }
        return false;
    }

    /**
     * 清除被锁定的用户
     * @param request 请求信息
     */
    public void clear(HttpServletRequest request) {
        try {
            String userName = request.getParameter("username");
            if(StringUtils.isNotEmpty(userName)){
                Object key = redisTemplate.opsForValue().get(REDIS_BLOCK_KEY+userName);
                if (key != null) {
                    redisTemplate.delete(REDIS_BLOCK_KEY+userName);
                }
            }
        } catch (Exception e) {
            logger.error("clear",e);
        }
    }

    private void printInfo(HttpServletRequest request) {
        //获取请求的相关信息
        try {
            logger.info("getMethod:{}", request.getMethod());
            logger.info("getQueryString:{}", request.getQueryString());
            logger.info("getProtocol:{}", request.getProtocol());
            logger.info("getContextPath{}", request.getContextPath());
            logger.info("getPathInfo:{}", request.getPathInfo());
            logger.info("getPathTranslated:{}", request.getPathTranslated());
            logger.info("getServletPath:{}", request.getServletPath());
            String xff = request.getHeader("x-forwarded-for");
            if (xff != null) {
                int index = xff.indexOf(',');
                if (index != -1) {
                    xff = xff.substring(0, index);
                }
                xff = xff.trim();
                logger.info("x-forwarded-for:{}", xff);
            }
            logger.info("getRemoteAddr:{}", request.getRemoteAddr());
            logger.info("getRemoteHost:{}", request.getRemoteHost());
            logger.info("getRemotePort:{}", request.getRemotePort());
            logger.info("getLocalAddr:{}", request.getLocalAddr());
            logger.info("getLocalName:{}", request.getLocalName());
            logger.info("getLocalPort:{}", request.getLocalPort());
            logger.info("getServerName:{}", request.getServerName());
            logger.info("getServerPort:{}", request.getServerPort());
            logger.info("getScheme:{}", request.getScheme());
            logger.info("getRequestURL:{}", request.getRequestURL());
        } catch (Exception e) {
            logger.error("printInfo", e);
        }
    }

}





