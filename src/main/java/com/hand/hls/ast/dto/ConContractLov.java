package com.hand.hls.ast.dto;

import leaf.annotation.LovField;

public class ConContractLov {
    @LovField(prompt = "合同ID", field = "contract_id")
    private String contractId;
    @LovField(prompt = "支付表编号", field = "contract_number", forQuery = true, forDisplay = true, displayWidth = 250)
    private String contractNumber;
    @LovField(prompt = "支付表名称", field = "contract_name", forQuery = true, forDisplay = true, displayWidth = 250)
    private String contractName;
    @LovField(prompt = "支付表状态", field = "contract_status", displayWidth = 250)
    private String contractStatus;

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }
}
