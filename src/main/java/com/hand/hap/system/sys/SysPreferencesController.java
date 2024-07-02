//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hap.system.controllers.sys;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.core.ILanguageProvider;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.Language;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.dto.SysPreferences;
import com.hand.hap.system.service.ISysPreferencesService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.sys.dto.FndEmployee;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.service.IFndEmployeeService;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hand.hls.sys.service.SysUserAllocationService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

@Controller
public class SysPreferencesController extends BaseController {
    @Autowired
    private ISysPreferencesService sysPreferencesService;
    @Autowired
    private ILanguageProvider languageProvider;
    @Autowired
    private IUserService userService;
    @Autowired
    private IFndEmployeeService fndEmployeeService;
    @Autowired
    private SysUserAllocationService sysUserAllocationService;

    public SysPreferencesController() {
    }

    @RequestMapping(
            value = {"/sys/um/sys_preferences.html"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ModelAndView sysPreferences(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView(this.getViewPath() + "/sys/um/sys_preferences");
        List<Language> languages = this.languageProvider.getSupportedLanguages();
        mv.addObject("languages", languages);
        return mv;
    }

    @RequestMapping(
            value = {"/sys/preferences/savePreferences"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData savePreferences(HttpServletRequest request, HttpServletResponse response, @RequestBody List<SysPreferences> sysPreferences, BindingResult result) {
        this.getValidator().validate(sysPreferences, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(this.getErrorMessage(result, request));
            return rd;
        } else {
            List<SysPreferences> lists = this.sysPreferencesService.saveSysPreferences(this.createRequestContext(request), sysPreferences);
            Iterator var6 = lists.iterator();

            while(var6.hasNext()) {
                SysPreferences preference = (SysPreferences)var6.next();
                if ("locale".equalsIgnoreCase(preference.getPreferences())) {
                    LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
                    if (localeResolver != null) {
                        localeResolver.setLocale(request, response, StringUtils.parseLocaleString(preference.getPreferencesValue()));
                    }
                } else if ("theme".equalsIgnoreCase(preference.getPreferences())) {
                    ThemeResolver themeResolver = RequestContextUtils.getThemeResolver(request);
                    if (themeResolver != null) {
                        themeResolver.setThemeName(request, response, preference.getPreferencesValue());
                    }
                } else if ("timeZone".equalsIgnoreCase(preference.getPreferences())) {
                    WebUtils.setSessionAttribute(request, SessionLocaleResolver.TIME_ZONE_SESSION_ATTRIBUTE_NAME, StringUtils.parseTimeZoneString(preference.getPreferencesValue()));
                    WebUtils.setSessionAttribute(request, "timeZone", preference.getPreferencesValue());
                } else if ("nav".equalsIgnoreCase(preference.getPreferences())) {
                    WebUtils.setSessionAttribute(request, "nav", preference.getPreferencesValue());
                }
            }

            return new ResponseData(lists);
        }
    }

    @RequestMapping(
            value = {"/sys/preferences/queryPreferences"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData queryPreferences(HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        SysPreferences preference = new SysPreferences();
        preference.setUserId(requestContext.getUserId());
        List<SysPreferences> lists = this.sysPreferencesService.querySysPreferencesByDb(requestContext, preference);
        return new ResponseData(lists);
    }

    @RequestMapping(
            value = {"/sys/preferences/autoDelegate/query"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData queryPreferencesForWFLAutoDelegate(HttpServletRequest request) {
        IRequest requestContext = this.createRequestContext(request);
        Long allocationId = (Long)requestContext.getAttribute("allocationId");
        SysPreferences preference = new SysPreferences();
        preference.setUserId(allocationId);
        List<SysPreferences> lists = this.sysPreferencesService.querySysPreferencesByDb(requestContext, preference);
        List<SysPreferences> autoDelegateList = (List)lists.stream().filter((o) -> {
            return "autoDelegate".equalsIgnoreCase(o.getPreferences()) || "deliverStartDate".equalsIgnoreCase(o.getPreferences()) || "deliverEndDate".equalsIgnoreCase(o.getPreferences());
        }).collect(Collectors.toList());
        JSONObject resultMap = new JSONObject();
        if (autoDelegateList != null && autoDelegateList.size() == 3) {
            Iterator var8 = autoDelegateList.iterator();

            while(var8.hasNext()) {
                SysPreferences delegateInfo = (SysPreferences)var8.next();
                resultMap.put(delegateInfo.getPreferences(), delegateInfo.getPreferencesValue());
                if ("autoDelegate".equals(delegateInfo.getPreferences())) {
                    String delegateAllocationId = delegateInfo.getPreferencesValue();
                    FndEmployee fndEmployee = this.fndEmployeeService.selectEmployeeInfoByAllocationId(delegateAllocationId);
                    if (fndEmployee != null) {
                        resultMap.put("autoDelegateName", fndEmployee.getName());
                    } else {
                        resultMap.put("autoDelegateName", "");
                    }
                }
            }
        }

        return new ResponseData(Arrays.asList(resultMap));
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @RequestMapping(
            value = {"/sys/preferences/autoDelegate/save"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData savePreferences(HttpServletRequest request, @ModelAttribute("_request_data") LeafRequestData requestData) {
        IRequest requestContext = this.createRequestContext(request);
        Long userId = (Long) requestContext.getAttribute("userId");
        // 删除历史数据
        deletePreferences(request);

        SysUserAllocation sysUserAllocation = new SysUserAllocation();
        sysUserAllocation.setUserId(userId);
//        sysUserAllocation.setEnabledFlag("Y");
        List<SysUserAllocation> sysUserAllocationList = sysUserAllocationService.selectSelective(requestContext, sysUserAllocation);

        JSONArray param = (JSONArray) requestData.get("parameter");
        List<SysPreferences> updateList = new ArrayList<>();
        List<SysPreferences> insertList = new ArrayList<>();
        StringBuffer message = new StringBuffer();
        ResponseData result = new ResponseData();
        if (sysUserAllocationList.size() > 0) {
            sysUserAllocationList.forEach(item -> {
                if (param.size() != 0) {
                    JSONObject json = (JSONObject) param.get(0);
                    Map<String, String> delegateInfo = (Map) JSON.parseObject(JSON.toJSONString(json, new SerializerFeature[]{SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue}), new TypeReference<Map<String, String>>() {
                    }, new Feature[0]);
                    if (delegateInfo != null) {
                        delegateInfo.forEach((k, v) -> {
                            if ("autoDelegate".equalsIgnoreCase(k) || "deliverStartDate".equalsIgnoreCase(k) || "deliverEndDate".equalsIgnoreCase(k)) {
                                SysPreferences preference = new SysPreferences();
                                preference.setPreferences(k);
                                preference.setUserId(item.getAllocationId());
                                List<SysPreferences> list = this.sysPreferencesService.selectSelective(requestContext, preference);
                                if (list.size() == sysUserAllocationList.size() * 3) {
                                    preference = (SysPreferences) list.get(0);
                                    preference.setPreferencesValue(v);
                                    updateList.add(preference);
                                } else {
                                    preference.setPreferencesValue(v);
                                    insertList.add(preference);
                                }
                            }
                        });
                        // 添加日期校验
                        boolean exec = true;
                        //SysPreferences ference = new SysPreferences();
                        //ference.setUserId(item.getAllocationId());
                        //List<SysPreferences> ferences = this.sysPreferencesService.selectSelective(requestContext, ference);
                        //Date tableStartDate = new Date();
                        //Date tableEndDate = new Date();
                        //for (SysPreferences sysPreferences : ferences) {
                        //    if ("deliverStartDate".equalsIgnoreCase(sysPreferences.getPreferences())) {
                        //        tableStartDate = DateUtil.parseDate(sysPreferences.getPreferencesValue());
                        //    }
                        //    if ("deliverEndDate".equalsIgnoreCase(sysPreferences.getPreferences())){
                        //        tableEndDate = DateUtil.parseDate(sysPreferences.getPreferencesValue());
                        //        // 校验日期是否在期间内，如果在则抛出错误提示
                        //        DateTime deliverStartDate = DateUtil.parseDate(delegateInfo.get("deliverStartDate"));
                        //        // 判断前端定义日期期间是否在数据库中期间内
                        //        if ((deliverStartDate.after(tableStartDate) && deliverStartDate.before(tableEndDate)) || deliverStartDate.getTime() == tableStartDate.getTime() || deliverStartDate.getTime() == tableEndDate.getTime()) {
                        //            exec = false;
                        //            break;
                        //        }
                        //        DateTime deliverEndDate = DateUtil.parseDate(delegateInfo.get("deliverEndDate"));
                        //        if (deliverEndDate.after(tableStartDate) && deliverEndDate.before(tableEndDate) || deliverEndDate.getTime() == tableStartDate.getTime() || deliverEndDate.getTime() == tableEndDate.getTime()) {
                        //            exec = false;
                        //            break;
                        //        }
                        //        // 判断数据库中日期期间是否在前段定义期间内
                        //        if ((tableStartDate.after(deliverStartDate) && tableStartDate.before(deliverEndDate))) {
                        //            exec = false;
                        //            break;
                        //        }
                        //        if ((tableEndDate.after(deliverStartDate) && tableEndDate.before(deliverEndDate))) {
                        //            exec = false;
                        //            break;
                        //        }
                        //    }
                        //}
                        // 执行插入修改
                        if (exec) {
                            updateList.stream().forEach(preference->this.sysPreferencesService.updateByPrimaryKey(requestContext, preference));
                            insertList.stream().forEach(preference->this.sysPreferencesService.insertSelective(requestContext, preference));
                            message.append("保存成功！");
                        }else {
                            message.append("保存失败，你在该时间段内已配置转交人！");
                        }
                        result.setSuccess(exec);
                        result.setMessage(message.toString());
                    }
                }
            });
        } else {
            return result;
        }
        return result;
    }

    @Transactional(
            rollbackFor = {Exception.class}
    )
    @RequestMapping(
            value = {"/sys/preferences/autoDelegate/delete"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    @ResponseBody
    public ResponseData deletePreferences(HttpServletRequest request){
        IRequest requestContext = this.createRequestContext(request);
        Long userId = (Long) requestContext.getAttribute("userId");
        SysPreferences sysPreferences = new SysPreferences();
        sysPreferences.setUserId(userId);
        List<SysPreferences> sysPreferencesList = this.sysPreferencesService.selectSelective(requestContext, sysPreferences);
        this.sysPreferencesService.batchDelete(sysPreferencesList);
        return new ResponseData();
    }
}
