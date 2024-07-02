package com.hand.hls.prj.controllers;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectLeaseItemService;
import leaf.bean.LeafRequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Controller
public class HlsCusPrjProjectLeaseItemController extends BaseController {


    @Autowired
    private HlsCusPrjProjectLeaseItemMapper mapper;

    @RequestMapping(value = "/ct/prj/project/lease/item/query")
    @ResponseBody
    public ResponseData query(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        JSONObject param  =(JSONObject) requestData.get("parameter");
        HlsCusPrjProjectLeaseItem dto = param.toJavaObject(HlsCusPrjProjectLeaseItem.class);
        IRequest requestContext = createRequestContext(request);
        List<HlsCusPrjProjectLeaseItem> list = mapper.queryPrjProjectLeaseItem(dto);
        //集合排序-正序
        Collections.sort(list, new Comparator<HlsCusPrjProjectLeaseItem>() {
            @Override
            public int compare(HlsCusPrjProjectLeaseItem o1, HlsCusPrjProjectLeaseItem o2) {
                int i = o1.getProjectLeaseItemId().intValue() - o2.getProjectLeaseItemId().intValue();
                if (i == 0) {
                    return o1.getProjectLeaseItemId().intValue() - o2.getProjectLeaseItemId().intValue();
                }
                return i;
            }
        });
        return new ResponseData(list);
    }


}