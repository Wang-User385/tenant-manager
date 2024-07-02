package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.HlsFinStatementHd;
import com.hand.hls.fnd.dto.HlsFinStatementTmpltLn;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by haibin on 2017/6/27.
 */
public interface HlsFinStatementHdService extends IBaseService<HlsFinStatementHd>, ProxySelf<HlsFinStatementHdService> {
    public List getList(HlsFinStatementTmpltLn hlsFinStatementTmpltLn, int page, int pagesize);
    public List<Map<String,String>> getClounm(HlsFinStatementTmpltLn hlsFinStatementTmpltLn);
    public boolean statementSubmit(IRequest iRequest, HlsFinStatementTmpltLn hlsFinStatementTmpltLn);
    public List<HlsFinStatementHd> hdQuery(HlsFinStatementHd hlsFinStatementHd, int page, int pagesize);
    public List<HlsFinStatementHd> hdDistinctQuery(HlsFinStatementHd hlsFinStatementHd, int page, int pagesize);
    public List<HlsFinStatementHd> hdGuaranQuery(HlsFinStatementHd hlsFinStatementHd, int page, int pagesize);

    public List<HlsFinStatementHd> hdColumnQuery(HlsFinStatementHd hlsFinStatementHd);
    boolean statementDelete(IRequest iRequest, HlsFinStatementTmpltLn hlsFinStatementTmpltLn);
    boolean statementLnDelete(IRequest iRequest, HlsFinStatementHd hlsFinStatementHd);


    void financialVertificate(IRequest iRequest, Long hdId) throws ExcelException, SQLException, ParseException;

    void exportFinancialReport(HttpServletRequest request, HttpServletResponse response, HlsFinStatementHd hlsFinStatementHd) throws IOException, InvocationTargetException, IllegalAccessException;

}
