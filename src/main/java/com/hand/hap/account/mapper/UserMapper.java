//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hand.hap.account.mapper;

import com.hand.hap.account.dto.User;
import com.hand.hap.hr.dto.Employee;
import com.hand.hap.mybatis.common.Mapper;
import java.util.List;

import com.hand.hls.fnd.dto.HlsEmployee;
import org.apache.ibatis.annotations.Param;

public interface UserMapper extends Mapper<User> {
    User selectByAttribute3(String attribute3);

    User selectByUserName(String var1);

    User selectByUserId(String var1);

    List<User> selectUserNameByEmployeeCode(String var1);

    List<User> selectUserByEmployeeCode(String var1);

    List<User> selectByIdList(List<Long> var1);

    int updatePassword(@Param("userId") Long var1, @Param("password") String var2);

    int updateFirstLogin(@Param("userId") Long var1, @Param("status") String var2);

    int updateBasic(User var1);

    List<User> selectUsers(User var1);

    List<User> selectUsersOption(User var1);

    List<String> getDirector(@Param("userName") String var1);

    List<String> getDeptDirector(@Param("userName") String var1);

    List<String> selectByPostionCode(@Param("positionCode") String var1, @Param("companyId") Long var2);

    List<String> selectByRoleCode(@Param("roleCode") String var1);

    Employee queryInfoByUserId(@Param("userId") String var1);

    Long selectEmployeeAssginIdByUserId(@Param("userId") Long var1);

    List<User> selectForLov(User var1);

    User queryUserByUserName(@Param("userName") String var1);

    User queryUserByCode(@Param("code") String var1);

    User selectUserById(Long var1);

    List<User> selectUserByPositionId(@Param("positionId") Long var1);

    User queryUserByAllocationId(@Param("allocationId") String var1);

    List<User> selectUsersByRole(User user);

    List<User> selectUsersByRoleNew(User user);

    /**
     * 查询员工--
     */
    User queryByEmployeeId(@Param("employeeId") Long employeeId);
    /**
     * 根据公司和名称选择业务经理
     */
    List<HlsEmployee> selectSalesByEmployeeName(@Param("employeeName") String employeeName);
}
