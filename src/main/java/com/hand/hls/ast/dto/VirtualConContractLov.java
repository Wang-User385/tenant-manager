package com.hand.hls.ast.dto;

import leaf.annotation.LovField;

public class VirtualConContractLov {
    @LovField(prompt = "合同ID", field = "contract_id")
    private String contractId;
    @LovField(prompt = "合同编号", field = "contract_number", forQuery = true, forDisplay = true, displayWidth = 250)
    private String contractNumber;
    @LovField(prompt = "合同名称", field = "contract_name", forQuery = true, forDisplay = true, displayWidth = 250)
    private String contractName;
    @LovField(prompt = "税率", field = "tax_type_rate")
    private String taxTypeRate;

    public String getTaxTypeRate() {
        return taxTypeRate;
    }

    public void setTaxTypeRate(String taxTypeRate) {
        this.taxTypeRate = taxTypeRate;
    }

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


}
