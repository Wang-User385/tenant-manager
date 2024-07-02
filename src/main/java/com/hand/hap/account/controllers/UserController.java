package com.hand.hap.account.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.dto.User;
import com.hand.hap.account.exception.UserException;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.account.service.IRoleService;
import com.hand.hap.account.service.IUserInfoService;
import com.hand.hap.account.service.IUserService;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.BaseException;
import com.hand.hap.function.dto.ResourceItemAssign;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.sys.dto.SysUserAllocation;
import leaf.bean.LeafRequestData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 用户控制器.
 *
 * @author njq.niu@hand-china.com
 * @date 2016/1/29
 */
@RestController
@RequestMapping(value = {"/sys/user", "/api/sys/user"})
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping(value = "/submitResourceItems")
    public ResponseData submitResourceItems(HttpServletRequest request,
                                            @RequestBody List<ResourceItemAssign> resourceItemAssignList,
                                            @RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) Long functionId) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(userService.updateResourceItemAssign(requestContext, resourceItemAssignList, userId, functionId));
    }

    @PostMapping(value = "/deleteResourceItems")
    public ResponseData removeResourceItems(@RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) Long functionId) {
        userService.deleteResourceItems(userId, functionId);
        return new ResponseData();
    }

    @PostMapping(value = "/queryResourceItems")
    public ResponseData queryResourceItems(HttpServletRequest request, @RequestParam(required = false) Long userId,
                                           @RequestParam(required = false) Long functionId) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(userService.queryResourceItems(requestContext, userId, functionId));
    }

    @PostMapping(value = "/queryFunction")
    public ResponseData queryFunction(HttpServletRequest request, @RequestParam(required = false) Long userId) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(userService.queryFunction(requestContext, userId));
    }

    @PostMapping(value = "/query")
    public ResponseData queryUsers(HttpServletRequest request,
                                   User user,
                                   @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(userService.selectUsers(iRequest, user, pagenum, pagesize));
    }

    @PostMapping(value = "/queryUsersForLov")
    public ResponseData queryUsersForLov(HttpServletRequest request,
                                         User user,
                                         @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(userService.queryUsersForLov(iRequest, user, page, pagesize));
    }

    @PostMapping(value = "/queryById")
    public ResponseData queryUsers(HttpServletRequest request,
                                   @RequestParam("userId") Long userId,
                                   @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        User user = new User();
        user.setUserId(userId);
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(userService.selectUsers(iRequest, user, page, pagesize));
    }

    @RequestMapping(value = "/leaf/query", method = RequestMethod.POST)
    @ResponseBody
    public ResponseData queryUsers(HttpServletRequest request,
                                   @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                   @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize) {
        JSONObject parameter = (JSONObject) requestData.get("parameter");
        User user = parameter.toJavaObject(User.class);
        String roleId = request.getParameter("roleId");
        if (StringUtils.isNotEmpty(roleId)) {
            user.setRoleId(Long.valueOf(roleId));
        }
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(userService.selectUsers(iRequest, user, pagenum, pagesize));
    }

    @PostMapping(value = "/leaf/home/query")
    public ResponseData queryHomeUser(HttpServletRequest request,
                                      HttpSession session,
                                      @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        JSONObject parameter = (JSONObject) requestData.get("parameter");
        IRequest iRequest = createRequestContext(request);
        User user = parameter.toJavaObject(User.class);
        user.setUserId(iRequest.getUserId());
        List<User> userList = userService.select(iRequest, user, 1, 100000);
        String positionName = session.getAttribute(SysUserAllocation.FIELD_POSITIONNAME).toString();
        userList.stream().forEach(item -> item.setPositionName(positionName));
        return new ResponseData(userList);
    }

    @PostMapping(value = "/submit")
    public ResponseData submitUsers(@RequestBody List<User> users,
                                    BindingResult result,
                                    HttpServletRequest request) throws BaseException {
        getValidator().validate(users, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(userService.batchUpdate(createRequestContext(request), users));
    }

    @PostMapping(value = "leaf/submit")
    public ResponseData submitUsers(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                    BindingResult result,
                                    HttpServletRequest request) throws BaseException {
        IRequest iRequest = createRequestContext(request);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<User> users = parameter.toJavaList(User.class);
        getValidator().validate(users, result);
        if (result.hasErrors()) {
            ResponseData rd = new ResponseData(false);
            rd.setMessage(getErrorMessage(result, request));
            return rd;
        }
        return new ResponseData(userService.batchUpdate(iRequest, users));
    }

    @PostMapping(value = "leaf/update")
    public ResponseData submitUsersUpdate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                          BindingResult result,
                                          HttpServletRequest request) throws BaseException {
        ResponseData data = new ResponseData();

        IRequest iRequest = createRequestContext(request);
        try {
            JSONObject parameter = (JSONObject) requestData.get("parameter");
            User user = parameter.toJavaObject(User.class);
            User userName = userService.selectByPrimaryKey(iRequest, user);
            user.setUserName(userName.getUserName());
            userService.updateByPrimaryKeySelective(iRequest, user);
            data.setSuccess(true);
        } catch (Exception e) {
            data.setSuccess(false);
        }
        return data;
    }

    @PostMapping(value = "/update")
    public ResponseData updateUserInfo(HttpServletRequest request, @RequestBody User user) throws BaseException {
        IRequest iRequest = createRequestContext(request);
        userInfoService.update(iRequest, user);
        return new ResponseData(Collections.singletonList(user));
    }

    @PostMapping(value = "/remove")
    public ResponseData remove(@RequestBody List<User> users) throws BaseException {
        userService.batchDelete(users);
        return new ResponseData(users);
    }

    @PostMapping(value = "/leaf/remove")
    public ResponseData remove(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws BaseException {
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<User> users = parameter.toJavaList(User.class);
        users.forEach(item -> {
            if ("update".equals(item.get__status())) {
                item.set__status("delete");
            }
        });
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(userService.batchUpdate(requestContext, users));
    }

    @PostMapping(value = "/{userId}/roles")
    public ResponseData queryUserAndRoles(HttpServletRequest request, @PathVariable Long userId) {
        IRequest iRequest = createRequestContext(request);
        ResponseData rd = new ResponseData();
        User user = new User();
        user.setUserId(userId);
        rd.setRows(roleService.selectRolesByUser(iRequest, user));
        return rd;
    }

    @PostMapping(value = "/password/reset")
    public ResponseData updatePassword(HttpServletRequest request, String password,
                                       String passwordAgain, Long userId) throws UserException {
        IRequest iRequest = createRequestContext(request);
        User user = new User();
        user.setUserId(userId);
        user.setPassword(password);
        userService.resetPassword(iRequest, user, passwordAgain);
        return new ResponseData(true);
    }

    @PostMapping(value = "/leaf/password/reset")
    public ResponseData updatePassword(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws UserException {
        ResponseData data = new ResponseData();
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        JSONObject jsonObject = (JSONObject) parameter.get(0);
        User user = new User();
        user.setUserId(Long.valueOf(jsonObject.get("user_id").toString()));
        user.setPassword((String) jsonObject.get("new_pwd"));
        System.out.print(user.toString());
        String passwordAgain = (String) jsonObject.get("pwd_confirm");

        IRequest iRequest = createRequestContext(request);
        try {
            userService.resetPassword(iRequest, user, passwordAgain);
            data.setSuccess(true);
        } catch (UserException e) {
            data.setSuccess(false);
            data.setMessage(e.getMessage());
        }
        return data;
    }

    @PostMapping(value = "/leaf/home/password/reset")
    public ResponseData updateHomePassword(HttpServletRequest request, @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) throws UserException {
        ResponseData data = new ResponseData();
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        JSONObject jsonObject = (JSONObject) parameter.get(0);
        User user = new User();
        user.setUserId(Long.valueOf(jsonObject.get("user_id").toString()));
        String oldPassword = ((String) jsonObject.get("current_password"));
        String newPassword = ((String) jsonObject.get("new_pwd"));
        System.out.print(user.toString());
        String newPasswordAgain = (String) jsonObject.get("pwd_confirm");

        IRequest iRequest = createRequestContext(request);
        try {
            userService.updateOwnerPassword(iRequest, oldPassword, newPassword, newPasswordAgain);
            data.setSuccess(true);
        } catch (UserException e) {
            data.setSuccess(false);
            data.setMessage(e.getMessage());
        }
        return data;
    }

    @PostMapping(value = "/password/update")
    public ResponseData updatePassword(HttpServletRequest request, String oldPwd,
                                       String newPwd, String newPwdAgain) throws UserException {
        IRequest iRequest = createRequestContext(request);
        userService.updateOwnerPassword(iRequest, oldPwd, newPwd, newPwdAgain);
        return new ResponseData(true);
    }

    @RequestMapping("/selectByPositionId")
    public ResponseData selectByPositionId(@RequestParam Long positionId,
                                           HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        if (positionId == null) {
            return new ResponseData(false, "岗位ID不能为空!");
        }
        return new ResponseData(userService.selectUserByPositionId(positionId));
    }


    @RequestMapping("/leaf/home/profile/photo/update")
    public ResponseData profilePhotoUpdate(HttpServletRequest request, HttpServletResponse response) {
        IRequest iRequest = createRequestContext(request);

        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        ResponseData responseData = new ResponseData(false);
        if (!multipartResolver.isMultipart(request)) {
            return responseData;
        }
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
        Iterator iter = multiRequest.getFileNames();
        try {
            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {
                    userService.updateUserProfile(iRequest, file);
                }
            }
        } catch (Exception e) {
            responseData.setMessage(e.getMessage());
            return responseData;
        }
        return responseData;
    }

}
