package com.hand.hls.sys.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sys.dto.HlsSystemNotice;
import com.hand.hls.sys.dto.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface SysUserMapper extends Mapper<SysUser> {
	void deleteByKey(Long userId);
	public List<SysUser> selectUserFriends(SysUser user);
	public List<HlsSystemNotice> selectAllPerson(SysUser user);
	void deleteChildByUserId(Long userId);
	public SysUser selectUserById(Long userId);

	public SysUser selectUserNameById(Long userId);

	public List<SysUser> selectList(SysUser sysUser);
	public SysUser selectUserPositionById(Long userId);
    SysUser queryUserByCode(@Param("code") String code);
	SysUser queryUserByUserName(@Param("userName") String userName);
	SysUser queryUserByDescription(@Param("description") String description);

	List<SysUser> selectUserByEmployee(SysUser sysUser);

	List<SysUser> userQuery(SysUser sysUser);

	//查找抄送人
	List<SysUser> queryCarbonCopyByAssignee(@Param("usersSet") Set<String> usersSet);

	/**
	 * 查询合同对应主协办项目经理、财务部负责人和资金部门负责人 会计岗
	 *
	 * @param companyId
	 * @param contractId
	 * @param documentCategory
	 * @return
	 */
	List<SysUser> selectContractChiefUnitUser(@Param("companyId") Long companyId, @Param("contractId") Long contractId, @Param("documentCategory") String documentCategory);


	/**
	 * 根据岗位code查询用户
	 *
	 * @param companyId
	 * @param positionCodeList
	 * @return
	 */
	List<SysUser> selectUserByPositionCode(@Param("companyId") Long companyId, @Param("positionCodeList") List<String> positionCodeList);

	/**
	 * 查询风险管理部门负责人
	 * @return
	 */
	List<SysUser> selectHeadOfRiskManagement();

	/**
	 * 根据角色查询allocationId
	 * @param roleCode
	 * @return
	 */
	List<Long> selectAllocationIdByRoleCode(String roleCode);

	List<SysUser> selectEemployeeUtil(SysUser model);

	List<SysUser> findAllocationIdByUserID(@Param("userId") Long userId);
	SysUser findVoteAllocationIdByUserID(@Param("userId") Long userId);
}
