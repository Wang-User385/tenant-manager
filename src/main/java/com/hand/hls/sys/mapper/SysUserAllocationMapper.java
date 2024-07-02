//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.sys.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sys.dto.SysUserAllocation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserAllocationMapper extends Mapper<SysUserAllocation> {
    List<SysUserAllocation> assignQuery();

    List<SysUserAllocation> editQuery(@Param("userId") Long var1, @Param("allocationId") Long var2);

    void deleteChild(Long var1);

    Long selectEmployeeAssignId(@Param("companyId") Long var1, @Param("employeeId") Long var2, @Param("unitId") Long var3, @Param("positionId") Long var4);

    String selectUserNameByAllocationId(@Param("allocationId") String var1);

    Long selectUserIdByAllocationId(@Param("allocationId") String var1);

    List<String> selectAllocationIdOfAdmin(@Param("companyId") Long var1);

    List<SysUserAllocation> selectUserInfo (@Param("allocationId") Long var2);
}
