package com.hand.hls.fnd.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import leaf.annotation.LovField;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@ExtensionAttribute(disable=true)
@Table(name="fnd_city")
public class FndCity extends BaseDTO {
	private String province;
	@Id
	private String city;
	private String description;

	private String enabledFlag;
	private String provincialCapitalFlag;

	@Transient
	private String prodesc;
	@Transient
	private String flag;

	public String getProvincialCapitalFlag() {
		return provincialCapitalFlag;
	}

	public void setProvincialCapitalFlag(String provincialCapitalFlag) {
		this.provincialCapitalFlag = provincialCapitalFlag;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	/**
	 * @return the prodesc
	 */
	public String getProdesc() {
		return prodesc;
	}
	/**
	 * @param prodesc the prodesc to set
	 */
	public void setProdesc(String prodesc) {
		this.prodesc = prodesc;
	}
	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the enabledFlag
	 */
	public String getEnabledFlag() {
		return enabledFlag;
	}
	/**
	 * @param enabledFlag the enabledFlag to set
	 */
	public void setEnabledFlag(String enabledFlag) {
		this.enabledFlag = enabledFlag;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FndCity [province=" + province + ", city=" + city + ", description=" + description + ", enabledFlag="
				+ enabledFlag + "]";
	}
}
