package com.hand.hls.cont.mapper;

import com.hand.hls.cont.dto.HlsCusConContractArchive;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HlsCusConContractArchiveMapper extends ConContractArchiveMapper<HlsCusConContractArchive> {

    List<HlsCusConContractArchive> archiveQuery(@Param("contractArchiveNumber") String contractArchiveNumber,
                                                @Param("contractNumber") String contractNumber,
                                                @Param("contractName") String contractName);

}
