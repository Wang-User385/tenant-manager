package com.hand.hls.fnd.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.fnd.dto.HlsCusEmployee;
import com.hand.hls.fnd.dto.HlsEmployee;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 员工mapper 二开
 * Created by yy.chen on 2018/4/15
 */
public interface HlsCusEmployeeMapper extends Mapper<HlsEmployee> {

    List<HlsEmployee> selectUserNameByEmployeeAssignId(@Param("companyId") Long hlsCusFctProject, @Param("employeeAssignId") Long employeeAssignId);//根据业务模式确定业务总监审批规则
    List<HlsEmployee> selectUserNameByEmployeeId(@Param("companyId") Long hlsCusFctProject, @Param("userId") Long userId);//根据业务模式确定业务总监审批规则

    List<HlsEmployee> selectWitness(HlsEmployee hlsEmployee);

    List<HlsEmployee> selectAllEmployeeForFctPrj(HlsEmployee hlsEmployee);

    /*查询主办部门负责人*/
    List<HlsCusEmployee> queryManageDeptDirector(HlsCusEmployee hlsCusEmployee);

    List<HlsCusEmployee> queryParentManager(HlsCusEmployee hlsCusEmployee);

    List<HlsCusEmployee> selectSecretaryByComIdAndPositionId(HlsCusEmployee hlsCusEmployee);

    List<HlsCusEmployee> selectUnitIdByEmployeeCodeAndCompanyId(HlsCusEmployee hlsCusEmployee);

    List<HlsCusEmployee> selectEmailAndName(HlsCusEmployee employee);


    List<HlsCusEmployee> selectEmployeeCodeByPositionCodeList(HlsCusEmployee hlsCusEmployee);

    List<HlsCusEmployee> selectEmployeeNameByCode(HlsCusEmployee hlsCusEmployee);

    List<HlsCusEmployee> selectAllEmployeeInfoByCode(HlsCusEmployee hlsCusEmployee);

    List<HlsCusEmployee> selectEmployeeAssignIdByEmployeeId(HlsCusEmployee hlsCusEmployee);


    //根据部门查询部门负责人
    List<HlsCusEmployee> queryUnitManagerByUnitId(HlsCusEmployee hlsCusEmployee);
    //根据部门查询部门负责人
    List<HlsCusEmployee> queryUnitManagerByUnitIdNew(HlsCusEmployee hlsCusEmployee);
    //根据部门和公司查询员工信息
    List<HlsCusEmployee> queryUserByUnitId(HlsCusEmployee hlsCusEmployee);

    HlsCusEmployee appPersonalInformationQuery(@Param("employeeCode") String employeeCode); // 根据员工代码查询个人信息

    HlsCusEmployee appPersonalInformationQueryByPhone(@Param("phone") String phone); // 根据员工代码查询个人信息

    List<HlsCusEmployee> queryManagerByType(Map<String, String> params); // 查询经办人信息

    List<String> unitTypeQueryByEmployeeCode(@Param("employeeCode") String employeeCode); // 根据员工代码查询部门代码

    /**
     * 获取需要需要抄送的人员
     * @param companyId
     * @param unitId
     * @param contractId
     * @param documentCategory
     * @return
     */
    List<HlsCusEmployee> selectCarbonEmployees(@Param("companyId") Long companyId, @Param("unitId") Long unitId, @Param("contractId") Long contractId, @Param("documentCategory") String documentCategory);



    /**
     * 多个业务主办查询 审批规则
     * @param companyId
     * @param employeeAssignIdList
     * @return
     */
    List<HlsEmployee> selectUserNameByEmployeeAssignIdList(@Param("companyId") Long companyId, @Param("employeeAssignIdList") List<Long> employeeAssignIdList);


    /**
     * 对个部门领导 审批规则
     * @param companyId
     * @param employeeAssignIdList
     * @return
     */
    List<HlsEmployee> selectManagerUserNameByEmployeeAssignIdList(@Param("companyId") Long companyId, @Param("employeeAssignIdList") List<Long> employeeAssignIdList);


    /**
     * 主办和协办
     * @param companyId
     * @param employeeAssignId
     * @param assistantAssignId
     * @return
     */
    List<HlsEmployee>  selectProjectMajorAndAssistant(@Param("companyId") Long companyId, @Param("employeeAssignId") Long employeeAssignId, @Param("assistantAssignId") Long assistantAssignId);


    List<HlsEmployee> queryManyParentManager(@Param("unitIdList") List<Long> unitIdList);

    List<String> getUnitPositionUsername(@Param("username") String username);

    HlsEmployee getEmployeeCode(@Param("userId") Long var1);

    List<HlsCusEmployee> queryAssignByAllocation(HlsCusEmployee hlsCusEmployee);

    List<HlsCusEmployee> queryBusinessLeader(HlsCusEmployee hlsCusEmployee);

    List<HlsEmployee> selectProjectAssistant(@Param("companyId") Long hlsCusFctProject, @Param("projectAssistant") Long projectAssistant);//根据业务模式确定业务总监审批规则

}
