package com.hand.hls.sys.components;

import com.hand.hap.account.dto.Role;
import com.hand.hap.account.dto.User;
import com.hand.hap.core.components.SysConfigManager;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.security.IAuthenticationSuccessListener;
import com.hand.hap.system.mapper.UserLoginMapper;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.sys.mapper.UserExtensionMapper;
import com.hand.hls.utils.MacUtil;
import leaf.sys.dto.SysSession;
import leaf.sys.mapper.SysSessionMapper;
import leaf.utils.ConfigUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: yang
 * Date: 2018/8/15
 * Time: 15:31
 */
@Component
public class LeafAuthenticationSuccessListener implements IAuthenticationSuccessListener {
    private static final String DEFAULT_LEAF_LANG = "ZHS";
    public static final String FIELD_USER_ID = "user_id";
    //非对称密钥算法
    private static final String KEY_ALGORITHM = "RSA";
    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 512;

    private static final String PARAM_LANG = "lang";

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private SysSessionMapper sysSessionMapper;
    @Autowired
    private UserExtensionMapper userExtensionMapper;
    @Autowired
    private SysConfigManager sysConfigManager;
    @Autowired
    UserLoginMapper userLoginMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String lang = request.getParameter(PARAM_LANG);
        if (StringUtil.isEmpty(lang)) {
            lang = DEFAULT_LEAF_LANG;
        }
        HttpSession session = request.getSession(false);
        session.setAttribute(PARAM_LANG, lang);
        session.setAttribute(FIELD_USER_ID, session.getAttribute(User.FIELD_USER_ID));
        SysSession sysSession = new SysSession();
        sysSession.setUserId((Long) session.getAttribute(User.FIELD_USER_ID));
        sysSession.setRoleId((Long) session.getAttribute(Role.FIELD_ROLE_ID));
        sysSession.setCompanyId((Long) session.getAttribute(FndCompany.FIELD_COMPANY_ID));
        sysSession.setUserLanguage(lang);

        Date now = new Date(System.currentTimeMillis());
        sysSession.setLoginTime(now);
        sysSession.setLastUpdateDate(now);
        sysSession.setCreationDate(now);

        sysSession.setEncryptedSessionId(session.getId());
        sysSession.setLastUpdatedBy(sysSession.getUserId());
        sysSession.setCreatedBy(sysSession.getUserId());

        String ipAddress = getIpAddress(request);
        sysSession.setClientIpAddress(ipAddress);
        sysSession.setReferer(StringUtils.abbreviate(request.getHeader("Referer"), 240));
        sysSession.setUserAgent(StringUtils.abbreviate(request.getHeader("User-Agent"), 240));

        String macs = null;
        try {
            macs = "";//MacUtil.getMacAddress(ipAddress);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        sysSession.setMachineSerial(macs);
        session.setAttribute("machine_serial", macs);
        sysSessionMapper.insertSelective(sysSession);

        List<Map> themes = userExtensionMapper.selectUserTheme(sysSession.getUserId());
        String targetTheme = "hap";
        if (CollectionUtils.isNotEmpty(themes) && themes.size() == 1) {
            Map map = themes.get(0);
            session.setAttribute("theme", map.get("subject"));
            Object themeO = map.get("theme");
            String theme = String.valueOf(themeO);
            if (theme.toUpperCase(Locale.CHINA).startsWith("HAP-LEAF")) {
                targetTheme = theme;
            }
        }
        session.setAttribute("theme", targetTheme);
        session.setAttribute("session_id", sysSession.getSessionId());
        session.setAttribute("user_id", session.getAttribute("userId"));
        session.setAttribute("role_id", session.getAttribute("roleId"));

        try {
            initKey(session);
        } catch (Exception e) {
            logger.error("Generate Rsa key pair failed.");
        }
    }

    private void initKey(HttpSession session) throws Exception {
        //实例化密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //甲方公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //甲方私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        session.setAttribute("pubKeyString", Base64.getEncoder().encodeToString(publicKey.getEncoded()));
        session.setAttribute("publicKey", publicKey);
        session.setAttribute("privateKey", privateKey);
        session.setAttribute("__aes_key__", ConfigUtils.getProp("encrypt.aes.key", "handhandhandhand"));


    }

    @Override
    public int getOrder() {
        return 11;
    }


    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
