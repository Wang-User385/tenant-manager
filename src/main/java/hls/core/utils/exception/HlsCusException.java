package hls.core.utils.exception;

import com.hand.hap.core.exception.BaseException;

/**
 *
 * @description  通用异常
 * @name HlsCusException.java
 * @date 2018年11月2日
 * @version 1.0
 * @update tengfei.liu@hand-china.com
 */
public class HlsCusException extends BaseException {

    private static final long serialVersionUID = 9046687211507280513L;

    private static final String CODE = "HLS";

    public HlsCusException(String descriptionKey, Object[] parameters) {
        super(CODE, descriptionKey, parameters);
    }
    public HlsCusException(String descriptionKey) {
        super(CODE, descriptionKey,null);
    }
}
