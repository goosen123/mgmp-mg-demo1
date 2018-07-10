package com.goosen.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.goosen.commons.annotations.ResponseResult;
import com.goosen.commons.enums.ResultCode;
import com.goosen.commons.exception.BusinessException;
import com.goosen.commons.model.request.login.LoginReqData;
import com.goosen.commons.model.response.BaseCudRespData;
import com.goosen.commons.model.response.login.LoginRespData;
import com.goosen.commons.service.UserService;
import com.goosen.commons.utils.CheckUtil;
import com.goosen.commons.utils.CommonUtil;

@Api(value="登录管理",description="登录管理")
@RestController
public class LoginController extends BaseController{
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	@Resource
	private UserService userService;
	
	@ApiOperation(value="登录")
	@ResponseResult
	@RequestMapping(value = {"login"},method=RequestMethod.POST)
	public LoginRespData login(@Validated @RequestBody LoginReqData reqData){
		
		if(reqData == null)
			throw new BusinessException(ResultCode.PARAM_IS_BLANK);
		
		//判断账号是否已存在
		String account = reqData.getAccount();
		//普通用户
		Integer userType = 2;
		String password = reqData.getPassword();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("userType", userType);
		Map<String,Object> userMap = userService.findOneByParams(params);
		if(userMap == null || userMap.size() == 0)
			throw new BusinessException(ResultCode.USER_NOT_EXIST);
		//检查密码是否正确
		String passwordFan = CommonUtil.getStrValue(userMap, "password");
		if(!passwordFan.equals(password))
			throw new BusinessException(ResultCode.USER_LOGIN_ERROR);
		String userId = CommonUtil.getStrValue(userMap, "id");
		
		//判断是否禁用
		//检验验证码
		
		return (LoginRespData) buildBaseModelRespData(userMap, new LoginRespData());
	}
	
	@ApiOperation(value="退出登录")
	@ResponseResult
	@RequestMapping(value = {"logout"},method=RequestMethod.POST)
	public BaseCudRespData<String> logout(@ApiParam(name="userId",value="用户id",required=true)String userId) {
		
		CheckUtil.notEmpty("userId", "userId", "userId不能空");
		
		return buildBaseCudRespData("");
	}
	
}
