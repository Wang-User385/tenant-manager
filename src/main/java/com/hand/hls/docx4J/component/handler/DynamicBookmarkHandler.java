package com.hand.hls.docx4J.component.handler;

import com.hand.hls.cont.dto.HlsDocFileParaTable;
import com.hand.hls.docx4J.Docx4jConstants;
import com.hand.hls.docx4J.IBookmarkHandler;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FJM on 2017/4/20.
 */
@Component
public class DynamicBookmarkHandler implements IBookmarkHandler {

    protected static Logger log = LoggerFactory.getLogger(DynamicBookmarkHandler.class);
    private static ObjectFactory factory = Context.getWmlObjectFactory();

    @Override
    public String getType() {
        return Docx4jConstants.BOOKMARK_TYPE_DYNAMICTABLE;
    }

    @Override
    public void process(WordprocessingMLPackage wordMLPackage, List<HlsDocFileParaTable> tableColumnList, List<HashMap> bookMarkValueList, String bookmark, Map<String, Object> params)  throws Exception{
        replaceTableWithBookmark(wordMLPackage, tableColumnList, bookMarkValueList, bookmark,params);
    }

    public void replaceTableWithBookmark(WordprocessingMLPackage wordMLPackage, List<HlsDocFileParaTable> tableResultList, List<HashMap> bookMarkValueList, String bookmark,Map<String, Object> params)
            throws Exception {
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        Document wmlDocumentEl = documentPart.getJaxbElement();
        Body body = wmlDocumentEl.getBody();
        if (tableResultList != null) {
            replaceTableBookmarkContents(wordMLPackage, body.getContent(),
                    bookMarkValueList, tableResultList, bookmark,params);

        }
        log.info("InsertTableNextRow Done");
    }

    private void replaceTableBookmarkContents(
            WordprocessingMLPackage wordMLPackage,
            List<Object> paragraphs,
            List<HashMap> bookMarkValueList,
            List<HlsDocFileParaTable> tableResultList,
            String bookmark,Map<String, Object> params) throws Exception {

        // now add a table
        List<SectionWrapper> sections = wordMLPackage.getDocumentModel().getSections();
        SectPr sectPr = sections.get(sections.size() - 1).getSectPr();
        int width = sectPr.getPgSz().getW().intValue();
        int cellWidthValue = 0;
        List<Tr> tableRows = new ArrayList<>();

        if (bookMarkValueList != null) {
            for (HashMap valueChild : bookMarkValueList) {
                Tr tr = factory.createTr();

                tableRows.add(tr);

                for (HlsDocFileParaTable tableChild : tableResultList) {
                    Tc tc = factory.createTc();
                    TcPr tcPr = factory.createTcPr();
                    P p = factory.createP();
                    PPr PPr = factory.createPPr();
                    ParaRPr RPr = factory.createParaRPr();
                    org.docx4j.wml.RPr RPr1 = factory.createRPr();
                    RFonts RFonts = factory.createRFonts();
                    String fontFamily = "宋体";
                    String fontSize = "21";
                    if(params != null) {
                        if (params.get("font_family") != null) {
                            fontFamily = params.get("font_family").toString();
                        }
                        if (params.get("font_size") != null) {
                            fontSize = params.get("font_size").toString();//tableChild.getString("font_size");
                        }
                    }
                    RFonts.setHint(STHint.EAST_ASIA);
                    RFonts.setEastAsia(fontFamily);
                    RFonts.setAscii(fontFamily);
                    RFonts.setHAnsi(fontFamily);
                    HpsMeasure sz = new HpsMeasure();
                    if(fontSize != null) {
                        sz.setVal(new BigInteger(fontSize));
                    }
                    RPr.setRFonts(RFonts);
                    RPr1.setRFonts(RFonts);
                    RPr1.setSz(sz);
                    RPr1.setSzCs(sz);
                    RPr.setSz(sz);
                    RPr.setSzCs(sz);
                    PPr.setRPr(RPr);
                    String align = tableChild.getAlignment();
                    if (align != null) {
                        Jc jc = factory.createJc();
                        jc.setVal(JcEnumeration.fromValue(align.toLowerCase()));
                        PPr.setJc(jc);
                    }
                    tc.setTcPr(tcPr);
                    p.getContent().add(PPr);
                    tc.getContent().add(p);
                    TblWidth cellWidth = factory.createTblWidth();
                    tcPr.setTcW(cellWidth);
                    cellWidth.setType("dxa");
                    cellWidthValue = (int) (tableChild.getColumnWidth() != null ? tableChild.getColumnWidth() : width / bookMarkValueList.size());
                    cellWidth.setW(BigInteger.valueOf(cellWidthValue));

                    R run = factory.createR();
                    run.setRPr(RPr1);
                    Text t = factory.createText();
                    if(valueChild.get(tableChild.getColumnName()) != null){
                        t.setValue(valueChild.get(tableChild.getColumnName()).toString());
                    }
                    run.getContent().add(t);
                    p.getContent().add(run);
                    tr.getContent().add(tc);
                }
            }
        }

        //start 2016-11-17 add by 12614
        // ---找到bookmark所在的tr
        String trPath = "//w:bookmarkStart[@w:name='" + bookmark + "']/../../.."; // 直接定位到tr
        List<Object> objects = wordMLPackage.getMainDocumentPart()
                .getJAXBNodesViaXPath(trPath, false);
        if (objects != null && objects.size() != 0) {
            Tr tr = (Tr) XmlUtils.unwrap(objects.get(0));
            Tbl table = (Tbl) tr.getParent();
            int rowIndex = locateRowInTable(table, tr);
            insertRows2Table(table, tableRows, rowIndex + 0); // 将tablerows中的的所有行，插入到指定位置
        }
        //end 2016-11-17 add by 12614

    }

    public void insertRow2Table(Tbl table, Tr tableRow, int index) {
        List<Object> content = table.getContent();
        content.add(index, tableRow);
    }

    private void insertRows2Table(Tbl table, List<Tr> trs, int index) {
        List<Object> content = table.getContent();
        content.addAll(index, trs);
    }

    private int locateRowInTable(Tbl table, Tr tableRow) {
        if (table == null || tableRow == null) {
            return -1;
        }
        List<Object> trs = table.getContent();

        for (Object object : trs) {
            if (object == tableRow) {
                return trs.indexOf(object);
            }
        }
        return -1;
    }

}
