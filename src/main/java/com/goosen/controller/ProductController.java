package com.goosen.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageInfo;
import com.goosen.commons.annotations.GetMappingNoLog;
import com.goosen.commons.annotations.ResponseResult;
import com.goosen.commons.model.response.product.ProductRespData;
import com.goosen.commons.service.ProductService;
import com.goosen.commons.utils.CheckUtil;
import com.goosen.commons.utils.CommonUtil;

@Api(value="商品管理",description="商品管理")
@RestController
@RequestMapping(value="product")
public class ProductController extends BaseController{
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	@Resource
	private ProductService productService;
	
	
	@ApiOperation(value="获取商品详情")
	@GetMappingNoLog
	@ResponseResult
	@RequestMapping(value = {"getDetail"},method=RequestMethod.GET)
    public ProductRespData getDetail(@ApiParam(name="id",value="商品id",required=true)String id){
		
		CheckUtil.notEmpty(id, "id", "商品id不能空");
		Map<String, Object> params = new HashMap<String, Object>();
		if(!CommonUtil.isTrimNull(id))
			params.put("id", id);
		Map<String, Object> map = productService.findOneByParams(params);
		
        return (ProductRespData) buildBaseModelRespData(map, new ProductRespData());
    }
	
	@ApiOperation(value="获取商品列表")
	@GetMappingNoLog
	@ResponseResult
	@RequestMapping(value = {"getList"},method=RequestMethod.GET)
    public List<ProductRespData> getList(@ApiParam(name="productTitle",value="商品标题")String productTitle) throws Exception {
		
		Map<String, Object> params = new HashMap<String, Object>();
		if(!CommonUtil.isTrimNull(productTitle))
			params.put("productTitle", productTitle);
		List<Map<String, Object>> list = productService.findByParams(params);
		
        return (List<ProductRespData>) buildBaseListRespData(list, "product.ProductRespData");
    }
	
	@ApiOperation(value="分页获取商品列表")
	@GetMappingNoLog
	@ResponseResult
	@RequestMapping(value = {"getListByPage"},method=RequestMethod.GET)
    public PageInfo<ProductRespData> getListByPage(@ApiParam(name="pageNum",value="当前页数")Integer pageNum,@ApiParam(name="pageSize",value="页大小")Integer pageSize,@ApiParam(name="productTitle",value="商品标题")String productTitle) throws Exception {
		
		Map<String, Object> params = new HashMap<String, Object>();
		if(!CommonUtil.isTrimNull(productTitle))
			params.put("productTitle", productTitle);
		addPageParams(pageNum, pageSize, params);
		PageInfo<Map<String, Object>> pageInfo = productService.findByParamsByPage(params);
		
        return (PageInfo<ProductRespData>) buildBasePageRespData(pageInfo, "product.ProductRespData");
    }
	
}
