package com.hand.hls.plm.pli.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.plm.pli.dto.PlmPliContract;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IPlmPliContractService extends IBaseService<PlmPliContract>, ProxySelf<IPlmPliContractService> {


    /**
     * PLM_PLI_CONTRACT表数据查询
     * @param iRequest
     * @param dto
     * @param page
     * @param pageSize
     * @return
     */
    List<PlmPliContract> selectPlmPliContractData(IRequest iRequest, PlmPliContract dto, int page, int pageSize);


    List<PlmPliContract> selectOtherAll(PlmPliContract dto, int page, int pageSize);

    List<PlmPliContract> selectOtherAllProject(PlmPliContract dto, int page, int pageSize);

    List<PlmPliContract> addOtherAll(List<PlmPliContract> dto);

    List<PlmPliContract> selectMeetingRiskDescription(PlmPliContract dto, int page, int pageSize);

    /**
     * 导出
     * @param iRequest
     * @param request
     * @param response
     * @param dto
     */
    void exportFivePlmPliContract(IRequest iRequest, HttpServletRequest request, HttpServletResponse response, PlmPliContract dto);

    /**
     * 添加合同信息,并添加船舶租赁物检查信息
     * @param request
     * @param dto
     * @return
     */
    List<PlmPliContract> contractSelect(IRequest request, List<PlmPliContract> dto);

}
