package com.goosen.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.goosen.commons.annotations.GetMappingNoLog;
import com.goosen.commons.annotations.ResponseResult;
import com.goosen.commons.enums.ResultCode;
import com.goosen.commons.exception.BusinessException;
import com.goosen.commons.model.po.Orders;
import com.goosen.commons.model.po.OrdersProduct;
import com.goosen.commons.model.request.orders.OrdersSubItemReqData;
import com.goosen.commons.model.request.orders.OrdersSubReqData;
import com.goosen.commons.model.response.BaseCudRespData;
import com.goosen.commons.model.response.orders.OrdersRespData;
import com.goosen.commons.service.OrdersProductService;
import com.goosen.commons.service.OrdersService;
import com.goosen.commons.service.ProductAttrService;
import com.goosen.commons.service.ProductService;
import com.goosen.commons.service.UserService;
import com.goosen.commons.utils.BeanUtil;
import com.goosen.commons.utils.CheckUtil;
import com.goosen.commons.utils.CommonUtil;
import com.goosen.commons.utils.IdGenUtil;
import com.goosen.commons.utils.NumberUtil;

@Api(value="订单管理",description="订单管理")
@RestController
@RequestMapping(value="orders")
public class OrdersController extends BaseController{
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	@Resource
	private OrdersService ordersService;
	@Resource
	private OrdersProductService ordersProductService;
	@Resource
	private UserService userService;
	@Resource
	private ProductService productService;
	@Resource
	private ProductAttrService productAttrService;
	
	@ApiOperation(value="提交订单")
	@ResponseResult
	@RequestMapping(value = {"submit"},method=RequestMethod.POST)
	@Transactional(readOnly=false,rollbackFor=Exception.class)
	public BaseCudRespData<String> submit(@Validated @RequestBody OrdersSubReqData reqData){
		
		if(reqData == null)
			throw new BusinessException(ResultCode.PARAM_IS_BLANK);
		
		//判断用户是否存在
		String userId = reqData.getUserId();
		Map<String, Object> paramsUser = new HashMap<String, Object>();
		paramsUser.put("id", userId);
		Map<String,Object> userMap = userService.findOneByParams(paramsUser);
		if(userMap == null && userMap.size() == 0)
			throw new BusinessException(ResultCode.USER_NOT_EXIST);
		String userName = CommonUtil.getStrValue(userMap, "userName");
		
		List<Map<String, Object>> productAttrList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> productList = new ArrayList<Map<String,Object>>();
		Double totalCost = 0.0;
		Integer totalVolume = 0;
		List<OrdersSubItemReqData> itemList = reqData.getItemList();
		for (int i = 0; i < itemList.size(); i++) {
			OrdersSubItemReqData ordersSubItemReqData = itemList.get(i);
			Integer itemVolume = ordersSubItemReqData.getItemVolume();
			if(!CommonUtil.isVaileNum(itemVolume))
				throw new BusinessException(ResultCode.PARAM_IS_INVALID);
			//判断商品属性是否存在
			String productAttrId = ordersSubItemReqData.getProductAttrId();
			if(CommonUtil.isTrimNull(productAttrId))
				throw new BusinessException(ResultCode.PARAM_IS_BLANK);
			Map<String, Object> paramsProductAttr = new HashMap<String, Object>();
			paramsProductAttr.put("id", productAttrId);
			Map<String,Object> productAttrMap = productAttrService.findOneByParams(paramsProductAttr);
			if(productAttrMap == null && productAttrMap.size() == 0)
				throw new BusinessException(ResultCode.PARAM_IS_INVALID);
			Double salePrice = CommonUtil.getDoubleValue(productAttrMap, "salePrice");
			if(salePrice == null || salePrice <= 0.0)
				throw new BusinessException(ResultCode.PARAM_IS_INVALID);
			
			//判断库存不足？？
			Integer stockVolume = CommonUtil.getIntValue(productAttrMap, "stockVolume");
			
			//判断商品是否存在
			String productId = CommonUtil.getStrValue(productAttrMap, "productId");
			if(CommonUtil.isTrimNull(productId))
				throw new BusinessException(ResultCode.PARAM_IS_INVALID);
			Map<String, Object> paramsProduct = new HashMap<String, Object>();
			paramsProduct.put("id", productId);
			Map<String,Object> productMap = productService.findOneByParams(paramsProduct);
			if(productMap == null || productMap.size() == 0)
				throw new BusinessException(ResultCode.PARAM_IS_INVALID);
			totalVolume = totalVolume + itemVolume;
			totalCost = NumberUtil.add(totalCost, NumberUtil.multi(salePrice, CommonUtil.getDoubleValue(itemVolume), 2), 2);
			productAttrList.add(productAttrMap);
			productList.add(productMap);
		}
		
		//生成订单
		Orders record = new Orders();
		record.setId(IdGenUtil.uuid());
		record.setCode(ordersService.createOrdersCode());
		record.setUserId(userId);
		record.setUserName(userName);
		record.setTotalCost(totalCost);
		record.setTotalVolume(totalVolume);
		record.setOrderStatus(0);
		record.setIsPay(0);
		record.setOrderRemark(reqData.getOrderRemark());
		ordersService.save(record);
		//生成订单商品
		for (int i = 0; i < itemList.size(); i++) {
			OrdersSubItemReqData ordersSubItemReqData = itemList.get(i);
			Integer itemVolume = ordersSubItemReqData.getItemVolume();
			Map<String, Object> productMap = productList.get(i);
			Map<String, Object> productAttrMap = productAttrList.get(i);
			Double salePrice = CommonUtil.getDoubleValue(productAttrMap, "salePrice");
			String productId = CommonUtil.getStrValue(productMap, "id");
			String productAttrId = CommonUtil.getStrValue(productAttrMap, "id");
			OrdersProduct ordersProduct = new OrdersProduct();
			BeanUtil.mapToBean(productMap, ordersProduct);
			BeanUtil.mapToBean(productAttrMap, ordersProduct);
			ordersProduct.setId(IdGenUtil.uuid());
			ordersProduct.setOrderId(record.getId());
			ordersProduct.setProductId(productId);
			ordersProduct.setProductAttrId(productAttrId);
			ordersProduct.setItemVolume(itemVolume);
			ordersProduct.setItemCost(NumberUtil.multi(salePrice, CommonUtil.getDoubleValue(itemVolume), 2));
			ordersProductService.save(ordersProduct);
		}
		
		//扣减库存，库存不足回滚？？
//		if(true)
//			throw new BusinessException(ResultCode.PARAM_IS_INVALID);
		
		return buildBaseCudRespData(record.getId());
	}
	
	@ApiOperation(value="获取订单详情")
	@GetMappingNoLog
	@ResponseResult
	@RequestMapping(value = {"getDetail"},method=RequestMethod.GET)
    public OrdersRespData getDetail(@ApiParam(name="id",value="订单id",required=true)String id){
		
		CheckUtil.notEmpty(id, "id", "订单id不能空");
		Map<String, Object> params = new HashMap<String, Object>();
		if(!CommonUtil.isTrimNull(id))
			params.put("id", id);
		Map<String, Object> map = ordersService.findOneByParams(params);
		
        return (OrdersRespData) buildBaseModelRespData(map, new OrdersRespData());
    }
	
	@ApiOperation(value="获取订单列表")
	@GetMappingNoLog
	@ResponseResult
	@RequestMapping(value = {"getList"},method=RequestMethod.GET)
    public List<OrdersRespData> getList(@ApiParam(name="userId",required=true,value="用户id")String userId) throws Exception {
		
		CheckUtil.notEmpty(userId, "userId", "用户id不能空");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		List<Map<String, Object>> list = ordersService.findByParams(params);
		
        return (List<OrdersRespData>) buildBaseListRespData(list, "orders.OrdersRespData");
    }
	
	@ApiOperation(value="分页获取订单列表")
	@GetMappingNoLog
	@ResponseResult
	@RequestMapping(value = {"getListByPage"},method=RequestMethod.GET)
    public PageInfo<OrdersRespData> getListByPage(@ApiParam(name="pageNum",value="当前页数")Integer pageNum,@ApiParam(name="pageSize",value="页大小")Integer pageSize,@ApiParam(name="userId",required=true,value="用户id")String userId) throws Exception {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		addPageParams(pageNum, pageSize, params);
		PageInfo<Map<String, Object>> pageInfo = ordersService.findByParamsByPage(params);
		
        return (PageInfo<OrdersRespData>) buildBasePageRespData(pageInfo, "orders.OrdersRespData");
    }
	
}
