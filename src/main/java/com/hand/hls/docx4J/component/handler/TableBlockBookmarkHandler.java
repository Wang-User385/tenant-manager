package com.hand.hls.docx4J.component.handler;


import com.hand.hls.cont.dto.HlsDocFileParaTable;
import com.hand.hls.docx4J.Docx4jConstants;
import com.hand.hls.docx4J.IBookmarkHandler;
import com.hand.hls.docx4J.utils.Docx4jUtils;
import org.apache.commons.collections.CollectionUtils;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.finders.RangeFinder;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBException;
import java.util.*;

/**
 * @author yang.yang07@hand-china.com
 */
@Component
public class TableBlockBookmarkHandler implements IBookmarkHandler {

    private final Logger log = LoggerFactory.getLogger(TableBlockBookmarkHandler.class);

    @Override
    public String getType() {
        return Docx4jConstants.BOOKMARK_TYPE_TABLE_ROW_REPEAT;
    }

    @Override
    public void process(WordprocessingMLPackage wordMLPackage, List<HlsDocFileParaTable> tableColumnList, List<HashMap> bookMarkValueList, String bookmark, Map<String, Object> params) throws Exception {
        replaceBlockWithBookmark(wordMLPackage, tableColumnList, bookMarkValueList);
    }


    public void replaceBlockWithBookmark(WordprocessingMLPackage wordMLPackage, List<HlsDocFileParaTable> bookMarkValueList, List<HashMap> tableResultList) throws Exception {
        if (CollectionUtils.isEmpty(bookMarkValueList) || CollectionUtils.isEmpty(tableResultList)) {
            log.warn("没有查出对应的值,本次动态表格行书签替换结束。");
            return;
        }
        replaceBlockBookmarkContents(wordMLPackage, bookMarkValueList, tableResultList);
        log.info("动态表格行书签替换成功");
    }

    private void replaceBlockBookmarkContents(WordprocessingMLPackage wordMLPackage, List<HlsDocFileParaTable> bookMarkValueList, List<HashMap> tableResultList)
            throws Exception {

        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
//        1. 通过书签拿到所有的tr节点
        List<Tr> trs = getAllBookMarkParentTr(mainDocumentPart, bookMarkValueList);

        if (CollectionUtils.isEmpty(trs)) {
            log.warn("没有找到书签对应的Tr节点");
            return;
        }
        // 2. 将文档中的包含Tr的标签先移除,并找到插入的下标，后续再添加带有值的Tr标签
        int insertIndex = removeSeriesTrFromBody(mainDocumentPart, trs);
        if (insertIndex < 0) {
            return;
        }
        // 将compositeMap转换成list

        List<String> fontFamilyList = new ArrayList<>();
        List<String> fontSizeList = new ArrayList<>();
        List<String> underLineList = new ArrayList<>();
        List<String> boldList = new ArrayList<>();
        List<String> tableCols = new ArrayList<>();

        for (HlsDocFileParaTable map : bookMarkValueList) {
            String column_desc = map.getColumnDesc();
            tableCols.add(column_desc);
            //TODO 目前doc_file_para_table表中暂无以下4个字段，所以设置为空
            fontFamilyList.add(null);
            fontSizeList.add(null);
            underLineList.add(null);
            boldList.add(null);
        }
        List<List<String>> multiValues = new ArrayList<>();
        for (HashMap map : tableResultList) {
            List<String> values = new ArrayList<>();
            for (String title : tableCols) {
                String value = (map.get(title) != null ? map.get(title) : "").toString();
                values.add(value);
            }

            multiValues.add(values);
        }

        // 3. 遍历需要替换的值，生成一个最终替换的List<Tr>
        List<Tr> insertValues = new ArrayList<>();
        for (List<String> values : multiValues) {
            List<Tr> newTrs = replaceSeriesBookmarkWithContent(trs, tableCols, values, fontFamilyList, fontSizeList, underLineList, boldList);
            insertValues.addAll(newTrs);
        }

        // 4. 将生成的List<P>插入到文档的指定位置

        final Tbl table = getParentTable(trs.get(0));
        table.getContent().addAll(insertIndex, insertValues);

        // 5. 保存文档
    }

    /**
     * 删除连续的Tr标签，并返回第一个Tr的index
     *
     * @param doc
     * @param trs
     * @return
     */
    public int removeSeriesTrFromBody(MainDocumentPart doc, List<Tr> trs) {
        final Tr tr = trs.get(0);
        final Tbl table = getParentTable(tr);
        final List<Object> content = table.getContent();

        int first = 0xffffffff >>> 1;
        for (Tr item : trs) {
            int index = content.indexOf(item);
            if (index < 0) {
                continue;
            }
            if (index < first) {
                first = index;
            }
            content.remove(index);
        }
        return first;
    }

    /**
     * 找到所有书签的父Tr 节点
     *
     * @param doc
     * @param bookmarkNames
     * @return 连续的Tr标签，包含了所有的书签在内
     * @throws XPathBinderAssociationIsPartialException
     * @throws JAXBException
     */
    public List<Tr> getAllBookMarkParentTr(MainDocumentPart doc, List<HlsDocFileParaTable> bookmarkNames)
            throws XPathBinderAssociationIsPartialException, JAXBException {
        List<Tr> result = new ArrayList<>();

        int first = 0xffffffff >>> 1; // 用于记录书签的第一个p标签的下标
        int last = -1; // 用来记录有书签的最后一个p标签

        Iterator<HlsDocFileParaTable> iterator = bookmarkNames.iterator();
        Tbl parentTable = null;
        List<Object> content = new ArrayList<>();
        while (iterator.hasNext()) {
            HlsDocFileParaTable next = iterator.next();
            String bookmark = next.getColumnName();
//            bm>p>tc>tr
            String xpath = "//w:bookmarkStart[@w:name='" + bookmark + "']/../../..";
            List<Object> jaxbNodesViaXPath = doc.getJAXBNodesViaXPath(xpath, false);
            if (jaxbNodesViaXPath != null && jaxbNodesViaXPath.size() > 0) {
                Object o = jaxbNodesViaXPath.get(0);
                if (o instanceof Tr && parentTable == null) {
//                    o should be
                    parentTable = getParentTable((Tr) o);
                    content = parentTable.getContent();
                }
                int index = content.indexOf(o);
                if (index > last) {
                    last = index;
                }
                if (index < first) {
                    first = index;
                }
            }
        }
        if (first > -1 && last >= first) {
            for (int i = first; i <= last; i++) {
                Object object = content.get(i);
                if (object instanceof Tr) {
                    result.add((Tr) object);
                }
            }
        }
        return result;
    }


    /**
     * 替换连续P标签中指定书签为指定值
     *
     * @param trs
     * @param bookmarkNames
     * @param bookmarkValues
     * @return
     */
    public List<Tr> replaceSeriesBookmarkWithContent(List<Tr> trs, List<String> bookmarkNames,
                                                     List<String> bookmarkValues, List<String> fontFamilyList,
                                                     List<String> fontSizeList, List<String> underlineList, List<String> boldList) {
        // 先复制一份，避免直接对paras修改，影响主体逻辑
        List<Tr> result = new ArrayList<>();
        for (Tr tr : trs) {
            Tr deepCopy = XmlUtils.deepCopy(tr);
            result.add(deepCopy);
        }

        RangeFinder rt = new RangeFinder("CTBookmark", "CTMarkupRange");
        new TraversalUtil(result, rt);

        for (CTBookmark bm : rt.getStarts()) {
            // 先判断书签的名字是否为空
            String bookmarkName = bm.getName();

            if (bookmarkName == null || !bookmarkNames.contains(bookmarkName)) {
                continue;
            }
            final int bookmarkIndex = bookmarkNames.indexOf(bookmarkName);
            final Object parent = XmlUtils.unwrap(bm.getParent());
            List<Object> content = null;
            if (parent instanceof ContentAccessor) {
                content = ((ContentAccessor) parent).getContent();
            } else if (parent instanceof List) {
                content = (List) parent;
            }
            if (content == null) {
                log.warn("Can not find parent content of bm: {}", bookmarkName);
                continue;
            }
            int rangeStart = -1;
            int rangeEnd = -1;
            for (int i = 0; i < content.size(); i++) {
                Object ox = content.get(i);
                Object listEntry = XmlUtils.unwrap(ox);
                if (listEntry.equals(bm)) {
                    rangeStart = i;
                } else if (listEntry instanceof CTMarkupRange) {
                    if (((CTMarkupRange) listEntry).getId().equals(bm.getId())) {
                        rangeEnd = i;
                        break;
                    }
                }
            }

            if (rangeStart >= 0 && rangeEnd > rangeStart) {
                // Delete the bookmark range
                for (int j = rangeEnd; j >= rangeStart; j--) {
                    log.debug("移除书签");
                    content.remove(j);
                }
                final String value = bookmarkValues.get(bookmarkIndex);
                final String fontFamily = fontFamilyList.get(bookmarkIndex);
                final String fontSize = fontSizeList.get(bookmarkIndex);
                final String underline = underlineList.get(bookmarkIndex);
                final String bold = boldList.get(bookmarkIndex);
                String[] contentArr = value.split("\n");
                if (parent instanceof P) {
                    R run = Docx4jUtils.makeRWithContent(contentArr[0], fontFamily, fontSize, underline, bold);
                    content.add(rangeStart, run);
                    final P realP = (P) parent;
                    final Object pParent = realP.getParent();
                    List<Object> theTopList = null;
                    int currentPIndex = -1;
                    if (pParent instanceof ContentAccessor) {
                        theTopList = ((ContentAccessor) pParent).getContent();
                        currentPIndex = theTopList.indexOf(parent);
                    }
                    for (int j = 1, len = contentArr.length; theTopList != null && currentPIndex > -1 && j < len; j++) {
                        run = Docx4jUtils.makeRWithContent(contentArr[j], fontFamily, fontSize, underline, bold);
                        P paraP = new P();
                        paraP.setPPr(realP.getPPr());
                        paraP.getPPr().setRPr(realP.getPPr().getRPr());
                        paraP.getContent().add(run);
                        theTopList.add(currentPIndex + j, paraP);
                    }
                } else {
                    for (int j = 0, len = contentArr.length; j < len; j++) {
                        R run = Docx4jUtils.makeRWithContent(contentArr[j], fontFamily, fontSize, underline, bold);
                        P paraP = new P();
                        paraP.getContent().add(run);
                        content.add(rangeStart + j, paraP);
                    }
                }
            }
        }
        return result;
    }

    private Tbl getParentTable(Tr tr) {
        final Object parent = XmlUtils.unwrap(tr.getParent());
        if (parent instanceof Tbl) {
            return (Tbl) parent;
        }
        throw new RuntimeException("Can not find parent Table of Tr. parent is: " + parent);
    }
}
