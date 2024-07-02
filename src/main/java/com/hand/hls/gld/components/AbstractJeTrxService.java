//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.gld.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.gld.dto.JeTrxDtl;
import com.hand.hls.gld.service.IJeTrxDtlService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractJeTrxService {
    protected static final String STATUS = "NEW";
    protected static final String OPERATE_TYPE_CREATE = "CREATE";
    protected static final String OPERATE_TYPE_REVERSE = "REVERSE";
    private ThreadLocal<String> operateType = new ThreadLocal();
    @Autowired
    private IJeTrxDtlService jeTrxDtlService;

    public AbstractJeTrxService() {
    }

    public abstract String getJeTrx();

    public Long getJeTrxId(Map param) {
        return param.get("jeTrxId") != null ? (Long) param.get("jeTrxId") : null;
    }

    public Long getCompanyId(Map param) {
        return param.get("companyId") != null ? (Long) param.get("companyId") : null;
    }

    public Long getContractId(Map param) {
        return param.get("contractId") != null ? (Long) param.get("contractId") : null;
    }

    public String getSourceDoc(Map param) {
        return param.get("sourceDoc") != null ? (String) param.get("sourceDoc") : null;
    }

    public Long getJeSourceId(Map param) {
        return param.get("jeSourceId") != null ? (Long) param.get("jeSourceId") : null;
    }

    public String getJeSourceDoc(Map param) {
        return param.get("jeSourceDoc") != null ? (String) param.get("jeSourceDoc") : null;
    }

    public Long getReverseJeTrxId(Map param) {
        Object reverseJeTrxId = param.get("reverseJeTrxId");
        if (reverseJeTrxId != null) {
            this.operateType.set("REVERSE");
            return (Long) reverseJeTrxId;
        } else {
            this.operateType.set("CREATE");
            return null;
        }
    }

    public Date getReverseJeDate(Map param) {
        return param.get("reverseJeDate") != null ? (Date) param.get("reverseJeDate") : null;
    }

    public void process(IRequest request, Map param) {
        this.operateType.set("CREATE");
        if (this.before(request, param)) {
            JeTrxDtl jeTrxDtl = new JeTrxDtl();
            jeTrxDtl.setJeTrx(this.getJeTrx());
            jeTrxDtl.setJeTrxId(this.getJeTrxId(param));
            jeTrxDtl.setCompanyId(this.getCompanyId(param));
            if (this.getJeSourceId(param) != null) {
                jeTrxDtl.setJeSourceId(this.getJeSourceId(param));
            } else {
                jeTrxDtl.setJeSourceId(this.getContractId(param));
            }

            if (this.getSourceDoc(param) != null) {
                jeTrxDtl.setJeSourceDoc(this.getSourceDoc(param));
            } else {
                jeTrxDtl.setJeSourceDoc(this.getJeSourceDoc(param));
            }

            jeTrxDtl.setStatus("NEW");
            jeTrxDtl.setReverseJeTrxId(this.getReverseJeTrxId(param));
            jeTrxDtl.setReverseJeDate(this.getReverseJeDate(param));
            jeTrxDtl.setOperateType((String) this.operateType.get());
            JeTrxDtl result = (JeTrxDtl) this.jeTrxDtlService.insertSelective(request, jeTrxDtl);


            //直接生成凭证
            /*List<Long> ids = new ArrayList<>();
            Long jeTrxDtlId = result.getJeTrxDtlId();
            ids.add(jeTrxDtlId);
            jeTrxDtlService.createJeLine(request, ids, param);*/

            this.after(request, param, result);
        }
    }

    protected abstract boolean before(IRequest var1, Map var2);

    protected abstract void after(IRequest var1, Map var2, JeTrxDtl var3);
}
