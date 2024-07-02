package hls.core.hls.service;

import com.hand.hls.exception.DayEndDateNullException;
import com.hand.hls.exception.DayEndTypeNullException;

import java.util.Map;

public interface HlsDayEndService {
    void excEndDay(Map var1) throws DayEndTypeNullException, DayEndDateNullException;
}