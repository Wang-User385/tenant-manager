package hls.core.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import hls.core.fnd.dto.HlsPenaltyProfileDtl;

import java.util.List;

public interface HlsPenaltyProfileDtlMapper extends Mapper<HlsPenaltyProfileDtl> {
    List<HlsPenaltyProfileDtl> selectList(HlsPenaltyProfileDtl var1);

    HlsPenaltyProfileDtl selectAllHlsPenProDtlById(HlsPenaltyProfileDtl var1);
}