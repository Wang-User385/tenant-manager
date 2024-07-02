package com.hand.hls.prj.mapper;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.mybatis.common.Mapper;
import com.hand.hap.system.dto.CodeValue;
import com.hand.hap.system.mapper.CodeValueMapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;
import java.util.Map;
@MapperScan
public interface HlsCodeValueMapper extends Mapper<CodeValue>{

    String selectCodeValuesByCodeNameAndValue(@Param("code") String code, @Param("meaning") String meaning);

    String getMeaningByCodeAndCodeValue(@Param("code") String code, @Param("codeValue") String codeValue);

    String getCodeValueByCodeAndMeaning(@Param("code") String code, @Param("meaning") String meaning);
    String getCodeValueByCodeAndMeaning1(@Param("code") String code, @Param("meaning") String meaning);

    List<String> selectCodeValuesByCode(@Param("code") String code);

    List<String> selectCodeNamesByCode(@Param("code") String code);

    /**
     * 删除CodeValue
     *
     * @param key codeId
     * @return
     */
    int deleteByCodeId(CodeValue key);

    /**
     * 删除CodeValue 多语言表数据
     *
     * @param key
     * @return
     */
    int deleteTlByCodeId(CodeValue key);

    /**
     * 根据codeName查询CodeValue
     *
     * @param codeName
     * @return list
     */
    List<CodeValue> selectCodeValuesByCodeName(String codeName);

    /**
     * 查询消息模版编码LOV
     *
     * @param value
     * @param meaning
     * @return
     */
    List<CodeValue> queryMsgTemCodeLov(@Param("value") String value, @Param("meaning") String meaning);

    /**
     * 查询邮箱帐号编码LOV
     *
     * @param value
     * @param meaning
     * @return
     */
    List<CodeValue> queryEmlAccountCodeLov(@Param("value") String value, @Param("meaning") String meaning);

    /**
     * 根据CodeId查询CodeValue
     *
     * @param codeValue
     * @return
     */
    List<CodeValue> selectCodeValuesByCodeId(CodeValue codeValue);

    /**
     * 查询CodeValue
     *
     * @param parentId
     * @return
     */
    List<CodeValue> selectCodeValuesByParentId(Long parentId);

    /**
     * 获取codeValue
     *
     * @param codeValueId
     * @return
     */
    CodeValue getCodeValueById(Long codeValueId);

    List<Map> selectCodeByCode(JSONObject parms);
}
