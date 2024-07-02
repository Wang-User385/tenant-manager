package com.hand.hls.docx4J.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.attachment.dto.SysFile;
import com.hand.hap.attachment.service.ISysFileService;
import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsDocFileParaTable;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.dto.HlsDocFileTempletPara;
import com.hand.hls.cont.dto.HlsDocFileTmpParaLink;
import com.hand.hls.cont.service.HlsDocFileParaTableService;
import com.hand.hls.cont.service.HlsDocFileTempletParaService;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.cont.service.HlsDocFileTmpParaLinkService;
import com.hand.hls.docx4J.Docx4jConstants;
import com.hand.hls.docx4J.IBookmarkHandler;
import com.hand.hls.docx4J.utils.Docx4jUtils;
import com.hand.hls.fnd.components.Datasource2Json;
import org.apache.commons.collections.CollectionUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Body;
import org.docx4j.wml.Document;
import org.docx4j.wml.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * by FJM
 * 所有类型书签替换都要经过此处，以此处为起点
 */
@Component
public class BookMarkReplaceComponent {

    @Autowired
    private TextBookmarkReplaceWithContents textBookmarkReplaceWithContents;

    @Autowired
    private BookmarkHandlerFactory bookmarkHandlerFactory;

    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;

    @Autowired
    private HlsDocFileTmpParaLinkService hlsDocFileTmpParaLinkService;

    @Autowired
    private HlsDocFileTempletParaService hlsDocFileTempletParaService;

    @Autowired
    private HlsDocFileParaTableService hlsDocFileParaTableService;

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private Datasource2Json datasource2Json;

    protected static Logger log = LoggerFactory.getLogger(BookMarkReplaceComponent.class);
    private static boolean DELETE_BOOKMARK = true;
    private static ObjectFactory FACTORY = Context.getWmlObjectFactory();

    private static final String[] OTHER_PART_NAMES = new String[]{
            "/word/header1.xml",
            "/word/footer1.xml",
            "/word/header2.xml",
            "/word/footer2.xml"
    };


    public WordprocessingMLPackage docxCreateBookMarkReplaceWithText(WordprocessingMLPackage wordMLPackage, IRequest iRequest, Map<String, Object> params) throws Exception {

        Long templetId = Long.parseLong(params.get("templetId").toString());

        HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
        hlsDocFileTemplet.setTempletId(templetId);
        hlsDocFileTemplet = hlsDocFileTempletService.selectByPrimaryKey(iRequest, hlsDocFileTemplet);//此处查询的结果为文件模板对应的数据


        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        Document wmlDocumentEl = documentPart.getJaxbElement();
        //拿到docx文件中的body
        Body body = wmlDocumentEl.getBody();


        if (hlsDocFileTemplet != null) {
            HlsDocFileTmpParaLink hlsDocFileTmpParaLink = new HlsDocFileTmpParaLink();
            hlsDocFileTmpParaLink.setTempletId(hlsDocFileTemplet.getTempletId());
            hlsDocFileTmpParaLink.setEnabledFlag("Y");

            List<HlsDocFileTmpParaLink> paraLinks = hlsDocFileTmpParaLinkService.selectListByObject(hlsDocFileTmpParaLink);//获取文件模板与参数的对应关系

            List<HlsDocFileTempletPara> bookMarkValueList = new ArrayList<>();//当前文档模板对应的参数的集合
            HlsDocFileTempletPara parameter = new HlsDocFileTempletPara();

            for (HlsDocFileTmpParaLink link : paraLinks) {
                parameter.setTempletParaId(link.getTempletParaId());
                parameter.setEnabledFlag("Y");
                bookMarkValueList.add(hlsDocFileTempletParaService.selectByPrimaryKey(iRequest, parameter));//将根据模板ID查询到的参数加入到参数集合中
            }


            Map<DataFieldName, String> map = new HashMap<>();
            Map<DataFieldName, String> mapType = new HashMap<>();
            Map<DataFieldName, String> mapFontFamily = new HashMap<>();
            Map<DataFieldName, String> mapFontSize = new HashMap<>();
            Map<DataFieldName, String> mapUnderLine = new HashMap<>();
            Map<DataFieldName, String> mapBold = new HashMap<>();

            List<Long> dsIds = new ArrayList<>();
            Map<String, Object> dsMap = new HashMap<>();
            for (HlsDocFileTempletPara hlsDocFileTempletPara : bookMarkValueList) {
                if (hlsDocFileTempletPara.getDataSourceId() != null) {
                    dsIds.add(hlsDocFileTempletPara.getDataSourceId());
                    dsMap.put(hlsDocFileTempletPara.getDataSourceId().toString(), params);
                }

            }

            String datas = datasource2Json.executeSQLs4Json(dsIds, dsMap);
            JSONObject jsonObjects = JSON.parseObject(datas);

            for (HlsDocFileTempletPara child : bookMarkValueList) {
                String bookmark = child.getBookmark();
                String bookmarkType = child.getBookmarkType();
                //param为executeSql方法所需要的参数。其中sql是必要的
                HashMap param = new HashMap<>();
                if (child.getDataSourceId() == null) {
                    continue;
                }
                //FIX BY fjm(表格只能生成一行)  11.27 17   begin ------------
                JSONObject result = JSON.parseObject(jsonObjects.getString(child.getDataSourceId().toString()));
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                boolean isDefault = false;
                boolean isTable = false;
                if (result.containsKey("default")) {
                    jsonObject = JSON.parseObject(JSON.toJSONString(result.get("default")));
                    isDefault = true;
                } else if (result.containsKey("table")) {
                    jsonArray = JSONArray.parseArray(JSON.toJSONString(result.get("table")));
                    isTable = true;
                } else {
                    continue;
                }
                List<HashMap> data = new ArrayList<>();
                if (isDefault) {
                    param = new HashMap<>(jsonObject);
                    data.add(param);
                }
                if (isTable) {
                    Iterator<Object> jsonArrayIterator = jsonArray.iterator();
                    while (jsonArrayIterator.hasNext()) {
                        JSONObject jsonArrayObject = (JSONObject) jsonArrayIterator.next();
                        param = new HashMap<>(jsonArrayObject);
                        data.add(param);
                    }
                }
                //FIX BY fjm  11.27 17   end ------------
                HashMap bookmarkValueMap = data.stream().findFirst().orElseGet(HashMap::new);//若为替换书签的类型普通的文本或者文本域，则此处返回值为一个HashMap
                if (bookmarkValueMap.isEmpty() && !Docx4jConstants.BOOKMARK_TYPE_WATERMARK.equals(bookmarkType)) {
                    continue;
                }
                if (Docx4jConstants.BOOKMARK_TYPE_TEXT.equals(bookmarkType) ||
                        Docx4jConstants.BOOKMARK_TYPE_TEXTAREA.equals(bookmarkType) ||
                        Docx4jConstants.BOOKMARK_TYPE_WATERMARK.equals(bookmarkType)) {
                    Collection values = bookmarkValueMap.values();
                    for (Object value : values) {
                        map.put(new DataFieldName(bookmark), (value == null) ? null : value.toString());
                    }
                } else {
                    map.put(new DataFieldName(bookmark), "NO_DEAL");//除了文本和文本域之外的书签本身都不处理
                }

                mapType.put(new DataFieldName(bookmark), child.getBookmarkType());
                mapFontFamily.put(new DataFieldName(bookmark), child.getFontFamily());
                mapFontSize.put(new DataFieldName(bookmark), (child.getFontSize() == null) ? null : child.getFontSize().toString());
                mapUnderLine.put(new DataFieldName(bookmark), child.getUnderline());
                mapBold.put(new DataFieldName(bookmark), child.getBlod());

                HlsDocFileParaTable tableChild = new HlsDocFileParaTable();
                tableChild.setTempletParaId(child.getTempletParaId());
                IBookmarkHandler handler = bookmarkHandlerFactory.getHandlerByType(bookmarkType);

                Map<String, Object> paramMap = null;
                if (Docx4jConstants.BOOKMARK_TYPE_WATERMARK.equals(bookmarkType)) {
                    List<SysFile> imgFile = sysFileService.queryFilesByTypeAndKey(iRequest, "DOC_WATER_MARK", child.getTempletParaId().toString());
                    String imgPath = imgFile.stream().findFirst().map(SysFile::getFilePath).orElse("");
                    paramMap = new HashMap<>();
                    paramMap.put("bookmark", bookmark);
                    paramMap.put("bookmarkValue", map.get(new DataFieldName(bookmark)));
                    paramMap.put("fontFamily", child.getFontFamily());
                    paramMap.put("fontSize", child.getFontSize());
                    paramMap.put("bookmarkType", child.getBookmarkType());
                    paramMap.put("imgPath", imgPath);
                }
                if(handler != null){
                    List<HlsDocFileParaTable> tableResultList = hlsDocFileParaTableService.selectList(tableChild);
                    if(child != null && paramMap != null) {
                        paramMap.put("fontFamily", child.getFontFamily());
                        paramMap.put("fontSize", child.getFontSize());
                    }
                    handler.process(wordMLPackage, tableResultList, data, bookmark, paramMap);
                }

            }


            textBookmarkReplaceWithContents.replaceBookmarkContents(body.getContent(), map, mapType, mapFontFamily, mapFontSize, mapUnderLine, mapBold);

            // 替换页眉以及页脚的书签
            for (String otherPartName : OTHER_PART_NAMES) {
                Part otherPart = Docx4jUtils.getNamedPart(wordMLPackage, otherPartName);
                List<Object> partContent = Docx4jUtils.getPartContent(otherPart);
                if (CollectionUtils.isNotEmpty(partContent)) {
                    textBookmarkReplaceWithContents.replaceBookmarkContents(partContent, map, mapType, mapFontFamily, mapFontSize, mapUnderLine, mapBold);
                }

            }
        }

        return wordMLPackage;

    }

}
