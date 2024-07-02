package com.hand.hls.abs.controllers;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.abs.dto.HlsCusAbsPkg;
import com.hand.hls.abs.dto.HlsCusAbsProduct;
import com.hand.hls.abs.mapper.HlsCusAbsProductMapper;
import com.hand.hls.abs.service.AbsProductService;
import com.hand.hls.abs.service.HlsCusAbsProductService;
import com.hand.hls.eft.service.HlsCusFundTransferListService;
import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class HlsCusAbsProductController extends BaseController {

    @Autowired
    private HlsCusAbsProductService service;
    @Autowired
    private HlsCusAbsProductMapper mapper;
    @Autowired
    private HlsCusFundTransferListService fundTransferListService;

    /**
     * 产品变更首页
     */
    @RequestMapping(value = "/ct/abs/product/change/home")
    @ResponseBody
    public ResponseData queryChangeHome(HlsCusAbsProduct dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryChangeHome(requestContext, dto, page, pagesize));
    }

    /**
     * 产品变更首页扇形图
     */
    @RequestMapping(value = "/ct/abs/product/change/chart/query")
    @ResponseBody
    public Map<String, Object> queryProductChangeChart() {
        return service.queryProductChangeChart();
    }

    /**
     * 产品变更创建
     */
  /*  @RequestMapping(value = "/ct/abs/product/change/add")
    @ResponseBody
    public ResponseData createChangeProduct(HlsCusAbsProduct dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(Arrays.asList(service.createChangeProduct(requestContext, dto)));
    }*/

    /**
     * ABS产品变更明细页面
     */
    @RequestMapping(value = "/ct/abs/product/change/detail")
    @ResponseBody
    public HlsCusAbsProduct queryChangeDetail(HlsCusAbsProduct dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.queryChangeDetail(requestContext, dto);
    }

    /**
     * 产品变更保存
     */
    @RequestMapping("/ct/abs/product/change/save")
    @ResponseBody
    public HlsCusAbsProduct saveProductChange(@RequestBody HlsCusAbsProduct dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.saveProductChange(requestContext, dto);
    }

    /**
     * 资产变更保存
     */
    @RequestMapping("/ct/abs/asset/change/save")
    @ResponseBody
    public HlsCusAbsProduct saveAssetChange(@RequestBody HlsCusAbsPkg dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.saveAssetChange(requestContext, dto).getHlsCusAbsProduct();

    }

    /**
     * 资产变更计算
     */
    /*@RequestMapping("/ct/abs/asset/change/calc")
    @ResponseBody
    public HlsCusAbsProduct calcAssetChange(@RequestBody HlsCusAbsPkg dto, HttpServletRequest request) throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        return service.calcAssetChange(requestContext, dto).getHlsCusAbsProduct();

    }*/

    /**
     * 资产变更提交
     */
    @RequestMapping("/ct/abs/asset/change/submit")
    @ResponseBody
    public HlsCusAbsProduct submitAssetChange(@RequestBody HlsCusAbsPkg dto, HttpServletRequest request) throws HlsCusException{
        IRequest requestContext = createRequestContext(request);
        return service.submitAssetChange(requestContext, dto);
    }

    /**
     * 产品变更确认
     */
   /* @RequestMapping("/ct/abs/product/change/confirm")
    @ResponseBody
    public HlsCusAbsProduct confirmProductChange(@RequestBody HlsCusAbsProduct dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.confirmProductChange(requestContext, dto);
    }*/


   //HlsCusAbsProduct dto,
    @RequestMapping(value = "/ct/abs/product/queryHome")
    @ResponseBody
    public ResponseData queryProductHome(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAbsProduct dto = param.toJavaObject(HlsCusAbsProduct.class);
        return new ResponseData(service.queryProductHome(requestContext, dto, page, pageSize));
    }


    //HlsCusAbsProduct dto,
    /**
     * ABS产品查询
     * ABS产品首页表格
     * ABS产品明细表单查询
     */
    @RequestMapping(value = "/ct/abs/product/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAbsProduct dto = param .toJavaObject(HlsCusAbsProduct.class);

        return new ResponseData(service.queryProductData(requestContext, dto, page, pageSize));
    }

    /**
     * 产品首页分析图查询
     */
    @RequestMapping(value = "/ct/abs/product/chart")
    @ResponseBody
    public ResponseData queryProductChart(HlsCusAbsProduct dto, HttpServletRequest request) {
        //合同首页环状图
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryProductChart(requestContext, dto));
    }

    /**
     * 产品首页动态查询
     */
/*    @RequestMapping("/ct/abs/product/notice")
    @ResponseBody
    public ResponseData queryProductNotice(HttpServletRequest request, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                           @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest iRequest = this.createRequestContext(request);
        return new ResponseData(service.queryProductNotice(iRequest, page, pageSize));
    }*/

    /*ABS产品创建*/
    @RequestMapping("/ct/abs/product/create")
    public ResponseData create(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        Long projectId = Long.parseLong((String) param.get("project_id"));
        String productName = (String) param.get("product_name");
        String productShortName = (String) param.get("product_short_name");
        String productNumber = (String) param.get("product_number");
        List<HlsCusAbsProduct> hlsCusAbsProductList = service.createProduct(projectId, productShortName, productName, productNumber, requestCtx);
        return new ResponseData(hlsCusAbsProductList);
    }


    //@RequestBody HlsCusAbsPkg dto,
    /**
     * 更新ABS产品
     */
    @RequestMapping(value = "/ct/abs/product/update")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAbsPkg dto = param.toJavaObject(HlsCusAbsPkg.class);

        List<HlsCusAbsProduct> list = new ArrayList<>(1);
        list.add(service.updateProduct(requestCtx, dto));
        return new ResponseData(list);
    }

    //@RequestBody HlsCusAbsPkg dto,
    /**
     * ABS产品归集计算
     */
    @RequestMapping(value = "/ct/abs/product/calculate")
    @ResponseBody
    public ResponseData calc(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) throws HlsCusException{
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
//        HlsCusAbsPkg dto =param.toJavaObject(HlsCusAbsPkg.class);
//        List<HlsCusAbsProduct> list = new ArrayList<>(1);
//        list.add(service.calcProduct(requestCtx, dto));

        Long product_id = Long.parseLong((String) param.get(HlsCusAbsProduct.FIELD_PRODUCT_ID));
        service.calcProduct(requestCtx, product_id);
        return new ResponseData();
    }


    /**
     * ABS产品归集行计算
     */
   /* @RequestMapping(value = "/ct/abs/product/cashCalculate")
    @ResponseBody
    public ResponseData cashCalculate(@RequestBody HlsCusAbsPkg dto, HttpServletRequest request) throws HlsCusException{
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusAbsProduct> list = new ArrayList<>(1);
        list.add(service.calcCashProduct(requestCtx, dto));
        return new ResponseData(list);
    }*/

    //@RequestBody HlsCusAbsPkg dto,
    /**
     * ABS产品确认
     */
    @RequestMapping(value = "/ct/abs/product/submit")
    @ResponseBody
    public ResponseData confirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData ,  HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        if (param.get(HlsCusAbsProduct.FIELD_PRODUCT_ID) != null) {
            Long product_id = Long.parseLong((String) param.get(HlsCusAbsProduct.FIELD_PRODUCT_ID));
            absProductService.submitProduct(requestCtx, product_id);
        }
        return new ResponseData();

      /*  IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusAbsPkg dto = param.toJavaObject(HlsCusAbsPkg.class);
        List<HlsCusAbsProduct> list = new ArrayList<>(1);
        list.add(service.submitProduct(requestCtx, dto));
        return new ResponseData(list);*/
    }

    @Autowired
    AbsProductService absProductService;

    @RequestMapping(value = "/ct/abs/product/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusAbsProduct> dto) {
        service.batchDelete(dto);
        return new ResponseData();
    }

    /**
     * excel 模板下载
     */
    @RequestMapping("/excel/model/download")
    public void excelModelDownload(@RequestParam String path, HttpServletRequest request, HttpServletResponse response) {
        String url = request.getServletContext().getRealPath("resources")+path;
        String[] strs = path.split("/");
        File file = new File(url);
        try (FileInputStream fis = new FileInputStream(file);
             ServletOutputStream sos = response.getOutputStream()
        ){
            response.addHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(strs[strs.length-1], "UTF-8") + "\"");
            response.setContentType("EXCEL;charset=UTF-8");
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentLength(Integer.valueOf(String.valueOf(file.length())));
            int length = -1;
            byte[] bs = new byte[1024];
            while (-1 != (length = fis.read(bs))){
                sos.write(bs,0,length);
            }
            sos.flush();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * excel 导入
     */
    @RequestMapping("/hls/excel/model/download")
    public void excelImport(@RequestParam Long headerId, @RequestParam Long key, HttpServletRequest request) {
        service.excelImport(createRequestContext(request), headerId, key);
    }

    /**
     * 项目首页综合查询
     */
    @RequestMapping(value = "/ct/abs/product/query/for/project")
    @ResponseBody
    public ResponseData queryForProject(HttpServletRequest request, @RequestBody HlsCusAbsProduct hlsCusAbsProduct,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        return new ResponseData(service.queryForProject(createRequestContext(request), hlsCusAbsProduct, page, pageSize));
    }

    /**
     * 产品取消变更
     */
    @RequestMapping("/ct/abs/product/change/cancel")
    @ResponseBody
    public ResponseData cancelProductChange(HttpServletRequest httpServletRequest, HlsCusAbsProduct hlsCusAbsProduct){
        service.cancelProductChange(createRequestContext(httpServletRequest), hlsCusAbsProduct);
        return new ResponseData();
    }


    /**
     * ABS产品报价计算
     */
    @RequestMapping(value = "/ct/abs/product/issue/calculate")
    @ResponseBody
    public ResponseData issueCalculate(@RequestBody HlsCusAbsPkg dto, HttpServletRequest request) throws Exception{
        IRequest requestCtx = createRequestContext(request);
        List<HlsCusAbsProduct> list = new ArrayList<>();
        list.add(service.calculateIssueProduct(requestCtx, dto));
        return new ResponseData(list);
    }


    /**
     * ABS产品更新
     */
    @RequestMapping(value = "/ct/abs/product/updateProduct")
    @ResponseBody
    public ResponseData updateProduct(@RequestBody HlsCusAbsProduct dto, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        service.updateByPrimaryKeySelective(requestCtx,dto);
        return new ResponseData();
    }

    /**
     * 清仓回购保存
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping("/ct/abs/product/buybackSave")
    @ResponseBody
    public ResponseData saveBuyBack(@RequestBody HlsCusAbsPkg dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        service.saveBuyBackData(requestContext, dto);
        return new ResponseData();
    }

    /**
     * 清仓回购提交
     * @param dto
     * @param request
     * @return
     * @throws HlsCusException
     */
    @RequestMapping("/ct/abs/product/buybackSubmit")
    @ResponseBody
    public ResponseData submitBuyBack(@RequestBody HlsCusAbsPkg dto, HttpServletRequest request)  throws HlsCusException{
        IRequest requestContext = createRequestContext(request);
        service.submitBuyBackData(requestContext, dto);
        return new ResponseData();
    }

    @RequestMapping("/cap/finance/line/absInfo")
    @ResponseBody
    public ResponseData queryAbsProductByCapLineId(HlsCusAbsProduct dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        PageHelper.startPage(page,pageSize);
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(mapper.queryAbsProductByCapLineId(dto));
    }


    /**
     * 查询产品通过ID
     * @param dto
     * @param request
     * @return
     */
    @RequestMapping(value = "/ct/abs/product/queryById")
    @ResponseBody
    public ResponseData queryById(HlsCusAbsProduct dto, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(Arrays.asList(service.selectByPrimaryKey(requestContext,dto)));
    }


    /**
     * 产品完结
     * @param httpServletRequest
     * @param hlsCusAbsProduct
     * @return
     */
    @RequestMapping("/ct/abs/product/end")
    @ResponseBody
    public ResponseData endProduct(HttpServletRequest httpServletRequest, HlsCusAbsProduct hlsCusAbsProduct)  throws HlsCusException{
        service.endAbsProduct(createRequestContext(httpServletRequest), hlsCusAbsProduct);
        return new ResponseData();
    }
}

