package com.hand.hls.csh.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.dto.HlsCusConContractLeaseItem;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.cont.service.IConContractService;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.CshPaymentReqDtMapper;
import com.hand.hls.csh.mapper.CshPaymentReqLnBankAccountMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.csh.service.CshPaymentReqDtService;
import com.hand.hls.csh.service.CshPaymentReqHdService;
import com.hand.hls.csh.service.CshPaymentReqLnService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineMapper;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.ProcessCancelDetail;
import com.hand.hls.prj.service.IProcessCancelDetailService;
import com.hand.hls.sys.service.IFndCompanyService;
import com.hand.hls.utils.DateUtils;
import com.hand.hls.utils.HlsConstantUtil;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import com.hand.hls.wsdl.utils.SapConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class CshPaymentReqLnServiceImpl extends BaseServiceImpl<HlsCusCshPaymentReqLn> implements CshPaymentReqLnService {

	@Autowired
	private HlsCusCshPaymentReqLnMapper mapper;
	@Autowired
	private CshPaymentReqLnBankAccountMapper cshPaymentReqLnBankAccountMapper;

	@Autowired
	private DatabaseLockProvider databaseLockProvider;
	@Autowired
	HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;
	@Autowired
	private HlsCusConContractMapper conContractMapper;
	@Autowired
	private HlsCusBpMasterMapper bpMasterMapper;
	@Autowired
	private HlsCusHlsCreditLineMapper hlsCusHlsCreditLineMapper;
	@Autowired
	private CshPaymentReqHdService paymentReqHdService;
	@Autowired
	private IFndCompanyService fndCompanyService;
	@Autowired
	private IActivitiStartService activitiStartService;

	@Autowired
	private CshPaymentReqDtService cshPaymentReqDtService;

	@Autowired
	HlsCusConContractCashflowService hlsCusConContractCashflowService;
	@Autowired
	private IConContractService conContractService;
	@Autowired
	private IProcessCancelDetailService cancelDetailService;



	private static final Long ZERO = 0L;
	private static final String UNIT_CODE = "unitCode";
	private static final String POSITION_CODE = "positionCode";

	private static final String CSH_PAYMENT_REQ = "CSH_PAYMENT_REQ";
	private static final String PAYMENT_REQ = "PAYMENT_REQ";
	private static final String CSH_PAYMENT_REQ_HD = "cshPaymentReqHd";
	private static final String I_REQUEST = "iRequest";
	private static final String LEASE_CHANNEL = "leaseChannel";
	private static final String DOCUMENT_CATEGORY = "documentCategory";
	private static final String DOCUMENT_TYPE = "documentType";
	private static final String DOCUMENT_ID = "documentId";
	private static final String DOCUMENT_NUMBER = "documentNumber";
	private static final String DOCUMENT_NAME = "documentName";
	private static final String PAYMENT_REQ_ID = "paymentReqId";
	private static final String PAYMENT_REQ_NUMBER = "paymentReqNumber";
	private static final String WORK_FLOW_TYPE = "workFlowType";
	private static final String AMOUNT = "amount";
	private static final String LOANTOTALAMOUNT = "loanTotalAmount";
	private static final String CSH_PAYMENT_HD_STATUS_BACK = "BACK";


	/**
	 * 对应工作流引擎-流程设计页面配置中的流程编码
	 */
	private static final String WORK_FLOW_KEY = "PAYMENT_APPLICATION_WFL";


	@Override
	public List<HlsCusCshPaymentReqLn> selectCshPaymentReqLnDetailByLnID(HlsCusCshPaymentReqLn cshPaymentReqLn) {
	        String[] arr = cshPaymentReqLn.getLnIdStr().split("-");
		    //融资还款-一个申请头下可能有多行 但是页面上只有一个行id 所以在这里先根据行id判断对应的头下面有几个行
		    List<String> fullList = new ArrayList<>();
//		    for(int i=0;i<arr.length;i++){
//				HlsCusCshPaymentReqLn dto = new HlsCusCshPaymentReqLn();
//				dto.setPayment_req_ln_id(Long.parseLong(arr[i]));
//				HlsCusCshPaymentReqLn resultDto = mapper.selectByPrimaryKey(dto);
//				Long hdId = resultDto.getPayment_req_id();//头id
//				String documentCategory = resultDto.getSource_doc_category();//类型
//				//根据头id获取相应的行
//				HlsCusCshPaymentReqLn reqLn = new HlsCusCshPaymentReqLn();
//				reqLn.setPayment_req_id(hdId);
//				List<HlsCusCshPaymentReqLn> reqLnList = new ArrayList<>();
//				if("CON_CONTRACT".equals(documentCategory)){
//					reqLnList = queryByPaymentReqIdCon(reqLn);
//				}else if("FCT_CONTRACT".equalsIgnoreCase(documentCategory)){
//					reqLnList = queryByPaymentReqIdFct(reqLn);
//				}else{
//					reqLnList = mapper.select(reqLn);
//				}
//				for(HlsCusCshPaymentReqLn item:reqLnList){
//					//循环添加行ID
//					fullList.add(item.getPayment_req_ln_id().toString());
//				}
//
//			}
			for(String str:arr){
				//循环添加行ID
				fullList.add(str);
			}
		   // List<String> list =  Arrays.asList(arr);//获取申请头idlist
		    List<HlsCusCshPaymentReqLn> lists = new ArrayList<HlsCusCshPaymentReqLn>();
	        if(fullList != null){
	        	 lists = mapper.selectCshPaymentReqLnDetailByLnID(fullList);
	        	return lists;
	        }
		return lists;
	}


	@Override
	public List<HlsCusCshPaymentReqLn> selectCshPaymentReqLnDetailByLnID(List<String> ln_id) {
		return mapper.selectCshPaymentReqLnDetailByLnID(ln_id);
	}

	@Override
	public List<HlsCusCshPaymentReqLn> queryByHnId(HlsCusCshPaymentReqLn cshPaymentReqLn) {
		return mapper.queryByHnId(cshPaymentReqLn);
	}

	@Override
	public List<HlsCusCshPaymentReqLn> queryForLoanRequest(HlsCusCshPaymentReqLn cshPaymentReqLn,int page,int pageSize) {
		PageHelper.startPage(page,pageSize);
		List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLns = mapper.queryForLoanRequest(cshPaymentReqLn);
		return hlsCusCshPaymentReqLns;
	}

	@Override
	public List<CshPaymentReqLnBankAccount> queryAccount(HlsCusCshPaymentReqLn cshPaymentReqLn){
		CshPaymentReqLnBankAccount cshPaymentReqLnBankAccount = new CshPaymentReqLnBankAccount();
		cshPaymentReqLnBankAccount.setPaymentReqLnId(cshPaymentReqLn.getPaymentReqLnId());
		List<CshPaymentReqLnBankAccount> result = cshPaymentReqLnBankAccountMapper.selectCshPaymentReqLnBankAccount(cshPaymentReqLnBankAccount);
		return result;
	}

	/**
	 * 二期功能：付款申请明细查询
	 *
	 * @param iRequest
	 * @param hlsCusCshPaymentReqLn
	 * @param pagenum
	 * @param pagesize
	 * @return
	 */
	@Override
	public List<HlsCusCshPaymentReqLn> queryCshPaymentReqLn(IRequest iRequest, HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn, int pagenum, int pagesize) {
		PageHelper.startPage(pagenum, pagesize);
		return mapper.queryCshPaymentReqLn2(hlsCusCshPaymentReqLn);
	}

	/**
	 * 二期功能：付款申请创建保存提交
	 *
	 * @param iRequest
	 * @param cshBaseDto
	 * @return
	 * @throws ResMessageException
	 * @throws HlsCusException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public ResponseData cshPaymentReqCreateAndSubmit(IRequest iRequest, CshBaseDto cshBaseDto) throws ResMessageException, HlsCusException {
		ResponseData responseData = new ResponseData(true);
		StringBuilder message = new StringBuilder();
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = cshBaseDto.getHlsCusCshPaymentReqHd();
		if(StringUtils.equals(HlsConstantUtil.SubmitStatus.SUBMIT, cshBaseDto.getSubmitStatus())){
			//databaseLockProvider.lock(hlsCusCshPaymentReqHd);
			HlsCusCshPaymentReqHd hlsCusCshPaymentReqHdCheck = hlsCusCshPaymentReqHdMapper.selectByPrimaryKey(hlsCusCshPaymentReqHd.getPaymentReqId());
			if(!"NEW".equals(hlsCusCshPaymentReqHdCheck.getPaymentReqStatus())&&!"REJECTED".equals(hlsCusCshPaymentReqHdCheck.getPaymentReqStatus())&&!"BACK".equals(hlsCusCshPaymentReqHdCheck.getPaymentReqStatus())){
				throw new ResMessageException("当前付款申请状态不可提交审批!");
			}
		}
		List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList = cshBaseDto.getHlsCusCshPaymentReqLnList();
		Long companyId = iRequest.getCompanyId();
		if (companyId == null) {
			throw new ResMessageException("公司ID获取失败!");
		}
		List<String> checkLeaseChannel = new LinkedList<>();
		List<Long> sourceDocIdList = hlsCusCshPaymentReqLnList.stream().map(HlsCusCshPaymentReqLn::getSourceDocId).distinct().collect(Collectors.toList());
		List<HlsCusConContract> lessorContractList = new LinkedList<>();
		Map<Long, HlsCusConContract> longHlsCusConContractHashMap = new HashMap<>();
		for (Long contractId : sourceDocIdList) {
			HlsCusConContract hlsCusConContract = conContractMapper.selectByPrimaryKey(contractId);
			checkLeaseChannel.add(hlsCusConContract.getLeaseChannel());
			lessorContractList.add(hlsCusConContract);
			longHlsCusConContractHashMap.put(contractId , hlsCusConContract);
		}

//		List<String> lessorCodeCollect = lessorContractList.stream().map(m -> m.getLessorCode()).distinct().collect(Collectors.toList());
//
//		if(lessorCodeCollect.size() != 1){
//			throw new HlsCusException("出租人不一致，不可发起付款申请!");
//		}
//
//		String lessorCode = lessorContractList.get(0).getLessorCode();

		List<String> distinctLeaseChannel = checkLeaseChannel.stream().distinct().collect(Collectors.toList());
		if (distinctLeaseChannel.size() > 1){
			throw new HlsCusException("请选择同一种商业模式下的合同创建付款申请，请检查！");
		}
		//TODO 提交时校验

		double sumAmount = hlsCusCshPaymentReqLnList.stream().mapToDouble(HlsCusCshPaymentReqLn::getAmount).sum();//总金额
		String authorityRuleString = getAuthorityRuleString(iRequest);
		String paymentMethod = hlsCusCshPaymentReqHd.getPaymentMethod();
		//实际申请支付金额 = 申请支付金额 - 抵扣金额 = 抵扣后申请总金额
		hlsCusCshPaymentReqHd.setLoanTotalAmount(hlsCusCshPaymentReqHd.getAfterDeductAmount());
		// 新增
		if (null == hlsCusCshPaymentReqHd.getPaymentReqId()) {
			Date now = new Date();
			hlsCusCshPaymentReqHd.setPaymentReqDate(now);
			hlsCusCshPaymentReqHd.setAmount(sumAmount);
			hlsCusCshPaymentReqHd.setCompanyId(companyId);
			hlsCusCshPaymentReqHd.setDocumentType(HlsConstantUtil.HlsCusCshPaymentReqHd.DOCUMENT_TYPE);
			hlsCusCshPaymentReqHd.setDocumentCategory(HlsConstantUtil.HlsCusCshPaymentReqHd.DOCUMENT_CATEGORY);
			hlsCusCshPaymentReqHd.setBusinessType(HlsConstantUtil.HlsCusCshPaymentReqHd.BUSINESS_TYPE);
			hlsCusCshPaymentReqHd.setPaymentReqStatus(HlsConstantUtil.WorkFlowStatus.NEW);
			hlsCusCshPaymentReqHd.setPaymentReqNumber(paymentReqHdService.getCodeValue(iRequest));
			hlsCusCshPaymentReqHd.setAuthorityRuleString(authorityRuleString);

			hlsCusCshPaymentReqHd.setTransferStatus("NEW");
			hlsCusCshPaymentReqHd.setBusinessType("PAYMENT_REQ");
			hlsCusCshPaymentReqHd.setPaymentType("PAYMENT");
			hlsCusCshPaymentReqHd.setPaymentApprovedStatus("NEW");
			hlsCusCshPaymentReqHd.setUnitId(Long.valueOf(iRequest.getAttribute("unitId")));
			hlsCusCshPaymentReqHd.setCurrency(hlsCusCshPaymentReqHd.getCurrency());
			hlsCusCshPaymentReqHd.setSendFlag("N");

			hlsCusCshPaymentReqHd.setContractCurrency(hlsCusCshPaymentReqHd.getContractCurrency());

			hlsCusCshPaymentReqHd.setEmployeeId(iRequest.getUserId());

			hlsCusCshPaymentReqHd = paymentReqHdService.insertSelective(iRequest, hlsCusCshPaymentReqHd);
		} else {
			paymentReqHdService.updateByPrimaryKeySelective(iRequest, hlsCusCshPaymentReqHd);
		}
		for (HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn : hlsCusCshPaymentReqLnList) {
			hlsCusCshPaymentReqLn.setPaymentReqId(hlsCusCshPaymentReqHd.getPaymentReqId());
			hlsCusCshPaymentReqLn.setPaymentMethod(paymentMethod);
			//hlsCusCshPaymentReqLn.setSubmitDate(now);
			if (null == hlsCusCshPaymentReqLn.getPaymentReqLnId()) {
				//hlsCusCshPaymentReqLn.setAuthorityRuleString(authorityRuleString);
				hlsCusCshPaymentReqLn.set__status(DTOStatus.ADD);
			} else {
				hlsCusCshPaymentReqLn.set__status(DTOStatus.UPDATE);
			}
		}
		List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLns = self().batchUpdate(iRequest, hlsCusCshPaymentReqLnList);



		if (StringUtils.equals(HlsConstantUtil.SubmitStatus.SAVE, cshBaseDto.getSubmitStatus())) {
			List<HlsCusCshPaymentReqHd> list = new ArrayList<>(1);
			list.add(hlsCusCshPaymentReqHd);
			responseData.setRows(list);
			responseData.setSuccess(true);
			return responseData;
		} else if (StringUtils.equals(HlsConstantUtil.SubmitStatus.SUBMIT, cshBaseDto.getSubmitStatus())) {

			self().updatePaymentStatus(iRequest, hlsCusCshPaymentReqHd.getPaymentReqId(), HlsConstantUtil.WorkFlowStatus.APPROVING);
			hlsCusCshPaymentReqHd.setPaymentReqStatus(HlsConstantUtil.WorkFlowStatus.APPROVING);
			List<HlsCusCshPaymentReqHd> wflList = new ArrayList<>(1);
			wflList.add(hlsCusCshPaymentReqHd);
			//工作流
			String paymentBpName = hlsCusCshPaymentReqHd.getPaymentBpName();
			hlsCusCshPaymentReqHd = paymentReqHdService.selectByPrimaryKey(iRequest, hlsCusCshPaymentReqHd);
			Map<String, Object> params = new HashMap<>();
			params.put(IActivitiCommonService.BUSINESS_KEY, hlsCusCshPaymentReqHd.getPaymentReqId());
			params.put(I_REQUEST, iRequest);
			params.put(DOCUMENT_CATEGORY, CSH_PAYMENT_REQ);
			params.put(DOCUMENT_TYPE, PAYMENT_REQ);
			params.put(DOCUMENT_ID, hlsCusCshPaymentReqHd.getPaymentReqId());
			params.put(DOCUMENT_NUMBER, hlsCusCshPaymentReqHd.getPaymentReqNumber());
			params.put(AMOUNT, hlsCusCshPaymentReqHd.getAmount());
			params.put(LOANTOTALAMOUNT, hlsCusCshPaymentReqHd.getLoanTotalAmount());
			params.put(DOCUMENT_NAME, paymentBpName + "[" + hlsCusCshPaymentReqHd.getBpBankName() + "][" + hlsCusCshPaymentReqHd.getBpBankAccountNum() + "]");

			params.put(HlsConstantUtil.WorkFlowParameterKey.WORK_FLOW_TYPE, WORK_FLOW_KEY);
			params.put(CSH_PAYMENT_REQ_HD, JSON.toJSONString(hlsCusCshPaymentReqHd));
			params.put(PAYMENT_REQ_ID, hlsCusCshPaymentReqHd.getPaymentReqId());
			params.put(PAYMENT_REQ_NUMBER, hlsCusCshPaymentReqHd.getPaymentReqNumber());
			activitiStartService.start(iRequest, wflList, params);
			responseData.setSuccess(true);
			responseData.setRows(wflList);
			return responseData;
		}
		throw new HlsCusException(HlsConstantUtil.SubmitStatus.STATUS_UNKOWN_ERROR_MESSAGE);
	}

	/**
	 * 二期功能：付款申请撤回
	 *
	 * @param iRequest
	 * @param paymentReqId
	 * @throws HlsCusException
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void paymentBack(IRequest iRequest, Long paymentReqId) throws HlsCusException {
		Objects.requireNonNull(paymentReqId, "付款申请单据未找到!");

		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = hlsCusCshPaymentReqHdMapper.selectByPrimaryKey(paymentReqId);
		if (StringUtils.equals(HlsConstantUtil.WorkFlowStatus.APPROVED, hlsCusCshPaymentReqHd.getPaymentReqStatus())) {
			throw new HlsCusException("申请单状态为审批通过的单据不能撤回!");
		}
		updatePaymentStatus(iRequest, paymentReqId, CSH_PAYMENT_HD_STATUS_BACK);
	}

	/**
	 * 付款申请行删除
	 *
	 * @param hlsCusCshPaymentReqLnList
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void batchDeleteReqLn(List<HlsCusCshPaymentReqLn> hlsCusCshPaymentReqLnList) {
		//删除付款行的同时删除抵扣行
		for(HlsCusCshPaymentReqLn hlsCusCshPaymentReqLn : hlsCusCshPaymentReqLnList){
			if(hlsCusCshPaymentReqLn.getPaymentReqLnId() != null){
				HlsCusCshPaymentReqDt hlsCusCshPaymentReqDt = new HlsCusCshPaymentReqDt();
				hlsCusCshPaymentReqDt.setPaymentReqLnId(hlsCusCshPaymentReqLn.getPaymentReqLnId());

				cshPaymentReqDtService.deleteDeductInfo(hlsCusCshPaymentReqDt);
			}
		}
		self().batchDelete(hlsCusCshPaymentReqLnList);
	}

	/**
	 * 二期功能：付款申请--合同取消
	 *
	 * @param iRequest
	 * @param hlsCusConContract
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void cancelContract(IRequest iRequest, HlsCusConContract hlsCusConContract) throws HlsCusException {
		//TODO 付款申请--合同取消逻辑
		checkParameter(iRequest, hlsCusConContract);
		conContractService.updatePaymentReturn(
				iRequest,
				hlsCusConContract.getContractIds(),
				hlsCusConContract.getReturnDate(),
				hlsCusConContract.getReturnReason(),
				hlsCusConContract.getReturnDescription()
		);

		for (Long contractId : hlsCusConContract.getContractIds()) {
			HlsCusConContract contract = new HlsCusConContract();
			contract.setContractId(contractId);
			contract = conContractService.selectByPrimaryKey(iRequest, contract);
			ProcessCancelDetail cancelDetail=new ProcessCancelDetail();
			cancelDetail.setDocumentId(contract.getContractId());
			cancelDetail.setDocumentType("CSH");
			cancelDetail.setCancelExplanation(hlsCusConContract.getReturnDescription());
			cancelDetail.setCancelReason(hlsCusConContract.getReturnReason());
			cancelDetail.setCancelType("CANCEL");
			cancelDetail.setCancelDate(new Date());
			cancelDetailService.insertSelective(iRequest,cancelDetail);

			//TODO 更新单据编号为取消
			conContractService.documentNumberCancel(iRequest, "CON_CONTRACT", contractId);

		}

	}


	/**
	 * 合同取消前校验
	 * @param iRequest
	 * @param hlsCusConContract
	 * @throws HlsCusException
	 */
	void checkParameter(IRequest iRequest, HlsCusConContract hlsCusConContract)throws HlsCusException{
		Long[] contractIds = hlsCusConContract.getContractIds();
		if (null == contractIds || contractIds.length == 0) {
			throw new HlsCusException("退回单据id不能为空");
		}
		if (hlsCusConContract.getReturnDate() == null) {
			throw new HlsCusException("退回日期不能为空");
		}
        if (StringUtils.isEmpty(hlsCusConContract.getReturnReason())) {
            throw new HlsCusException("退回理由不能为空");
        }
		if (StringUtils.isEmpty(hlsCusConContract.getReturnDescription())) {
			throw new HlsCusException("退回说明不能为空");
		}
		for (Long contractId : hlsCusConContract.getContractIds()) {
			List<HlsCusConContractCashflow> hlsCusConContractCashflows = hlsCusConContractCashflowService.queryCashflowList(contractId);
			if(Objects.nonNull(hlsCusConContractCashflows)){
				for (HlsCusConContractCashflow hlsCusConContractCashflow : hlsCusConContractCashflows) {
					//现金流未核销（con_contract_cashflow.write_off_flag='NOT'）
					if(!"NOT".equals(hlsCusConContractCashflow.getWriteOffFlag())){
						throw new HlsCusException("存在已核销的现金流，不允许取消合同");
					}
				}
			}
		}
	}


	@Override
	public String getAuthorityRuleString(IRequest iRequest) {
		FndCompany company = new FndCompany();
		company.setCompanyId(iRequest.getCompanyId());
		company = fndCompanyService.selectByPrimaryKey(iRequest, company);
		String unitCode = iRequest.getAttribute(UNIT_CODE);
		String positionCode = iRequest.getAttribute(POSITION_CODE);
		StringBuilder authorityRuleString = new StringBuilder("\"").append(company.getCompanyCode()).append("\"")
				.append(".").append("\"").append(Optional.ofNullable(unitCode).orElse("")).append("\"")
				.append(".").append("\"").append(Optional.ofNullable(positionCode).orElse("")).append("\"")
				.append(".").append("\"").append(Optional.ofNullable(iRequest.getEmployeeCode()).orElse("")).append("\"");
		return authorityRuleString.toString();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updatePaymentStatus(IRequest iRequest, long paymentReqId, String paymentStatus) {
		HlsCusCshPaymentReqHd hlsCusCshPaymentReqHd = new HlsCusCshPaymentReqHd();
		hlsCusCshPaymentReqHd.setPaymentReqId(paymentReqId);
		hlsCusCshPaymentReqHd.setPaymentReqStatus(paymentStatus);
		paymentReqHdService.updateByPrimaryKeySelective(iRequest, hlsCusCshPaymentReqHd);
	}

}
