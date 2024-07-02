package leaf.bm.components;

import leaf.database.service.IDatabaseServiceFactory;
import leaf.utils.ObjectRegistryHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uncertain.composite.CompositeMap;

import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: yang
 * Date: 2018/6/11
 * Time: 14:13
 */
@Component
public class DocumentChecker {
    private Logger logger = LoggerFactory.getLogger(DocumentChecker.class);

    public void check(CompositeMap obj, String checkerName,CompositeMap root) throws RuntimeException {
        check(obj,checkerName,root,"");
    }

    public void check(CompositeMap obj, String checkerName,CompositeMap root, String type) throws RuntimeException {
        logger.debug("checker name: [{}], obj: [{}]", checkerName, obj);
        if (obj == null || StringUtils.isEmpty(checkerName)) {
            throw new IllegalArgumentException("empty args");
        }
        int index = checkerName.lastIndexOf(".");
        if (index < 1 || index == (checkerName.length() - 1)) {
            throw new IllegalArgumentException("illegal checker name: " + checkerName);
        }
        String className = checkerName.substring(1, index);
        String methodName = checkerName.substring(index + 1);
        logger.debug("class: {}, method: {}", className, methodName);
        Class<?> aClass = null;
        Object target = null;
        try {
            aClass = Class.forName(className);
            target = aClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length > 1) {
                    if (parameterTypes[1] == IDatabaseServiceFactory.class) {
                        IDatabaseServiceFactory factory = ObjectRegistryHolder.getInstanceOfType(IDatabaseServiceFactory.class);
                        invoke(method, target, obj, factory,root);
                    }
                } else if (parameterTypes[0] == CompositeMap.class) {
                    invoke(method, target, obj);
                } else {
                    throw new RuntimeException("cannot find proper checker to execute");
                }
                break;
            }
        }
    }

    public void invoke(Method method, Object target, Object... params) {
        try {
            method.invoke(target, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isJavaChecker(String checkName) {
        logger.debug("check name: [{}]", checkName);
        return checkName.startsWith("$");
    }
}
