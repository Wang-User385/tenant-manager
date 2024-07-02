package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterRelation;
import com.hand.hls.bp.mapper.HlsCusBpMasterRelationMapper;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsBpMasterRelation;
import com.hand.hls.prj.dto.HlsDefaultParameter;
import com.hand.hls.prj.mapper.HlsBpMasterMapper;
import com.hand.hls.prj.mapper.HlsBpMasterRelationMapper;
import com.hand.hls.prj.service.IHlsDefaultParameterService;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class HlsDefaultPrameterController extends BaseController {

    @Autowired
    private IHlsDefaultParameterService service;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HlsBpMasterMapper hlsBpMasterMapper;
    @Autowired
    private HlsCusBpMasterRelationMapper hlsBpMasterRelationMapper;

    private static final String MANUFACTURER = "MANUFACTURER";

    private static final String USER_FACTORY_EXCEPTION = "用户主机厂异常!";

    private static final String MANUFACTURER_EXCEPTION = "当前用户未查询到厂商!";

    //根据主机厂查询厂商
    @RequestMapping(value = "/hls/manufacturer/query")
    @ResponseBody
    public Map queryManufacturer(HttpServletRequest request,
                     HttpSession session,
                     Long factoryId) throws HlsCusException {
        Map map = new HashMap();
        HlsCusBpMasterRelation hlsBpMasterRelation = new HlsCusBpMasterRelation();
        hlsBpMasterRelation.setBpId(factoryId);
        hlsBpMasterRelation.setRelationType(MANUFACTURER);
        hlsBpMasterRelation.setEnabledFlag("Y");
        hlsBpMasterRelation = hlsBpMasterRelationMapper.selectOne(hlsBpMasterRelation);
        if(hlsBpMasterRelation==null){
            return map;
        }
        HlsCusBpMaster hlsBpMaster = new HlsCusBpMaster();
        hlsBpMaster.setBpId(hlsBpMasterRelation.getRelatedBpId());
        hlsBpMaster = (HlsCusBpMaster) hlsBpMasterMapper.selectByPrimaryKey(hlsBpMaster);
        if(hlsBpMaster == null){
            throw new HlsCusException(MANUFACTURER_EXCEPTION);
        }
        map.put("manufacturer_id",hlsBpMaster.getBpId());
        map.put("manufacturer_id_n",hlsBpMaster.getBpName());
        map.put("virtual_account_flag",StringUtils.isEmpty(hlsBpMaster.getVirtualAccountFlag())?BaseDTO.NO: hlsBpMaster.getVirtualAccountFlag());
        map.put("success",true);
        return map;
    }

    @RequestMapping(value = "/hls/defalut/para/query")
    @ResponseBody
    //查询
    public ResponseData query(HttpServletRequest request,
                              HttpSession session,
                              @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize,
                              @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws HlsCusException {
        IRequest iRequest = createRequestContext(request);
        RequestHelper.setCurrentRequest(iRequest);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsDefaultParameter dto = param.toJavaObject(HlsDefaultParameter.class);
        if(session.getAttribute("userId").toString()!=null){
            dto.setUserId(Long.parseLong(session.getAttribute("userId").toString()));
        }
        if(session.getAttribute("roleId").toString()!=null){
            dto.setRoleId(Long.parseLong(session.getAttribute("roleId").toString()));
        }
        if(session.getAttribute("companyId").toString()!=null){
            dto.setCompanyId((Long) session.getAttribute("companyId"));
        }

        if(session.getAttribute("unitId").toString()!=null){
            dto.setUnitId((String) session.getAttribute("unitId"));
        }
        if(session.getAttribute("employeeId").toString()!=null){
            dto.setEmployeeId( Long.parseLong((String)session.getAttribute("employeeId")));
        }
        List<HlsDefaultParameter> list =service.queryDefaultPara(iRequest, dto, pagenum, pagesize);
        //默认显示数据，原本从sql中取得，后来从session中获得
        if(list.size()==0){
            list=new ArrayList<>();
            list.add(new HlsDefaultParameter());
        }
        list.get(0).setCompanyId((Long) session.getAttribute("companyId"));
        list.get(0).setRoleId((Long) session.getAttribute("roleId"));
        list.get(0).setUserId((Long) session.getAttribute("userId"));
        list.get(0).setEmployeeId(Long.parseLong((String)session.getAttribute("employeeId")));
        list.get(0).setUnitId((String) session.getAttribute("unitId"));
        list.get(0).setUnitCode((String) session.getAttribute("unitCode"));
        list.get(0).setUnitName((String) session.getAttribute("unitName"));


        //查询主机厂
        User user = new User();
        user.setUserId(dto.getUserId());
        List<User> userList = userMapper.selectUsersOption(user);

        if(CollectionUtils.isEmpty(userList)){
            throw new HlsCusException(USER_FACTORY_EXCEPTION);
        }
        user = userMapper.selectUsersByRoleNew(user).get(0);
        if(user != null){
            list.get(0).setRefN01(user.getBpId());
            list.get(0).setRefV01(user.getBpName());
            list.get(0).setRefV02(user.getBpCategory());
        }
        return new ResponseData(list);
    }
}