/**
 * WSGLWebServiceFacadeSrvProxy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ws.eas.services.WSGLWebServiceFacade;

public interface WSGLWebServiceFacadeSrvProxy extends java.rmi.Remote {
    public String[][] getAsstActType(String orgNumber, String acctTypeNum) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[][] getOrg(String number) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[][] getVoucher(String orgNumber, String year, String period, int fromRow, int toRow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public int deleteVoucher(String companyNumber, String period, String voucherNumber, String fexp) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[][] getAccount(String orgNumber, int fromRow, int toRow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[][] getAccountBalance(String orgNumber, String year, String period, int fromRow, int toRow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String checkVoucher(String comNumber, int year, int periodNumber, String number, double amount) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String importAccountBalance(ws.eas.kingdee.WSWSAccountBalance[] accountBalanceCols) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public void importInitAssistBalance(ws.eas.kingdee.WSWSAssistBalance[] initAssistBalanceBalance) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String importAssistBalance(ws.eas.kingdee.WSWSAssistBalance[] assistBalanceCol) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String importInitAccountBalance(ws.eas.kingdee.WSWSAccountBalance[] initAccountBalanceCol) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String exploreVoucher(String companyId, int year, int periodNumber, String voucherType, String number) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public int deleteVoucherByID(String companyNumber, String voucherID) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[] importVoucherOfReturnID(ws.eas.client.WSWSVoucher[] col, int isSubmit, int isVerify, int isCashflow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[][] getAcctTypeDetail(String orgNumber, String asstActTypeNum) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[][] getAssitBalance(String orgnumber, String accountNumber, String year, String period, int fromRow, int toRow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[] findVoucher(String comNumber, int year, int periodNnumber) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public boolean deleteBalance(String companyNumber, int year, int period) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[] importVoucher(ws.eas.client.WSWSVoucher[] col, int isSubmit, int isVerify, int isCashflow) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
    public String[][] getAcctType(String orgNumber) throws java.rmi.RemoteException, ws.eas.client.WSInvokeException;
}
