package hls.core.hls.service;

import com.hand.hls.exception.DayEndDateNullException;
import com.hand.hls.exception.DayEndTypeNullException;

import java.util.Map;

/**
 * @author PC
 */
public interface HlsDayEndLsJobService {
    void execute(Map var1) throws Exception;
}
