package com.hand.hls.pam.dto;

import com.hand.hap.mybatis.annotation.ExtensionAttribute;
import com.hand.hap.system.dto.BaseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author Qian Yuanfeng
 * @date 2020/4/14 - 11:59
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ExtensionAttribute(disable = true)
@Table(name = "hls_lease_item")
public class HlsCusLeaseItem   extends  BaseDTO  {

    public static final String FIELD_LEASE_ITEM_ID = "leaseItemId";
    public static final String FIELD_LEASE_ITEM_CODE = "leaseItemCode";
    public static final String FIELD_SHORT_NAME = "shortName";
    public static final String FIELD_FULL_NAME = "fullName";
    public static final String FIELD_LEASE_ITEM_TYPE = "leaseItemType";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_SEARCH_TERM = "searchTerm";
    public static final String FIELD_SERIAL_NUMBER = "serialNumber";
    public static final String FIELD_PATTERN = "pattern";
    public static final String FIELD_SPECIFICATION = "specification";
    public static final String FIELD_UOM = "uom";
    public static final String FIELD_QUANTITY = "quantity";
    public static final String FIELD_CURRENCY = "currency";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_ORIGINAL_ASSET_VALUE = "originalAssetValue";
    public static final String FIELD_NET_ASSET_VALUE = "netAssetValue";
    public static final String FIELD_ACCUMULATED_DEPRECIATION = "accumulatedDepreciation";
    public static final String FIELD_DETENTION = "detention";
    public static final String FIELD_MANUFACTURER_ID = "manufacturerId";
    public static final String FIELD_MANUFACTURER_NAME = "manufacturerName";
    public static final String FIELD_MANUFACTURING_DATE = "manufacturingDate";
    public static final String FIELD_VENDER_ID = "venderId";
    public static final String FIELD_VENDER_NAME = "venderName";
    public static final String FIELD_INSTALLATION_SITE = "installationSite";
    public static final String FIELD_FIXED_ASSETS_SITE = "fixedAssetsSite";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_SHIP_MODEL = "shipModel";
    public static final String FIELD_SHIP_CLASSIFICATION = "shipClassification";
    public static final String FIELD_OVERALL_LENGTH = "overallLength";
    public static final String FIELD_DESIGNED_DRAFT = "designedDraft";
    public static final String FIELD_MOLDED_BREADTH = "moldedBreadth";
    public static final String FIELD_MOLDED_DEPTH = "moldedDepth";
    public static final String FIELD_MAIN_ENGINE = "mainEngine";
    public static final String FIELD_DESIGN_SPEED = "designSpeed";
    public static final String FIELD_DEADWEIGHT_CAPACITY = "deadweightCapacity";
    public static final String FIELD_NAVIGATING_ZONE = "navigatingZone";
    public static final String FIELD_BALLAST_TANK = "ballastTank";
    public static final String FIELD_HOLD_CAPACITY = "holdCapacity";
    public static final String FIELD_RATED_SPEED = "ratedSpeed";
    public static final String FIELD_RATED_POWER = "ratedPower";
    public static final String FIELD_FUEL_CONSUMPTION = "fuelConsumption";
    public static final String FIELD_NATIONALITY = "nationality";
    public static final String FIELD_HULL = "hull";
    public static final String FIELD_AIRCRAFT_CATEGORY = "aircraftCategory";
    public static final String FIELD_AIRPLANE_MODEL = "airplaneModel";
    public static final String FIELD_ENGINE = "engine";
    public static final String FIELD_MTOW = "mtow";
    public static final String FIELD_MAX_RANGE = "maxRange";
    public static final String FIELD_MAX_CRUISING_SPEED = "maxCruisingSpeed";
    public static final String FIELD_PASSENGER_CAPACITY = "passengerCapacity";
    public static final String FIELD_PAYLOAD = "payload";
    public static final String FIELD_LI_LIST_TYPE = "liListType";
    public static final String FIELD_INDUSTRY = "industry";
    public static final String FIELD_DIVISION = "division";
    public static final String FIELD_REF_V01 = "refV01";
    public static final String FIELD_REF_V02 = "refV02";
    public static final String FIELD_REF_V03 = "refV03";
    public static final String FIELD_REF_V04 = "refV04";
    public static final String FIELD_REF_V05 = "refV05";
    public static final String FIELD_REF_N01 = "refN01";
    public static final String FIELD_REF_N02 = "refN02";
    public static final String FIELD_REF_N03 = "refN03";
    public static final String FIELD_REF_N04 = "refN04";
    public static final String FIELD_REF_N05 = "refN05";
    public static final String FIELD_REF_D01 = "refD01";
    public static final String FIELD_REF_D02 = "refD02";
    public static final String FIELD_REF_D03 = "refD03";
    public static final String FIELD_REF_D04 = "refD04";
    public static final String FIELD_REF_D05 = "refD05";
    public static final String FIELD_OWNER_USER_ID = "ownerUserId";
    public static final String FIELD_INVOICE_AMT = "invoiceAmt";
    public static final String FIELD_INVOICE_NUM = "invoiceNum";
    public static final String FIELD_INVOICE_DATE = "invoiceDate";
    public static final String FIELD_GPS_SIM_NUMBER = "gpsSimNumber";
    public static final String FIELD_GPS_PLAT_NAME = "gpsPlatName";
    public static final String FIELD_GPS_ACC_STATUS = "gpsAccStatus";
    public static final String FIELD_GPS_LOCK_STATUS = "gpsLockStatus";
    public static final String FIELD_CDD_LIST_ID = "cddListId";
    public static final String FIELD_FAIR_VALUE_RECOGNITION = "fairValueRecognition";
    public static final String FIELD_ORIGIN_PRICE = "originPrice";
    public static final String FIELD_ORIGIN_BOOK_VALUE = "originBookValue";
    public static final String FIELD_NET_BOOK_VALUE = "netBookValue";
    public static final String FIELD_ASSET_VALUE = "assetValue";
    public static final String FIELD_COMPLETE_VALUE = "completeValue";
    public static final String FIELD_FAIR_VALUE = "fairValue";
    public static final String FIELD_ASSET_TYPES = "assetTypes";
    public static final String FIELD_ASSET_TYPES_SUB = "assetTypesSub";
    public static final String FIELD_REGULATORY_ASSET_CLASS = "regulatoryAssetClass";
    public static final String FIELD_REGULATORY_ASSET_CLASS_SUB = "regulatoryAssetClassSub";
    public static final String FIELD_NOMINAL_TRANSACTION_PRICE = "nominalTransactionPrice";
    public static final String FIELD_INCLUDE_PRINCIPAL_TAX = "includePrincipalTax";
    public static final String FIELD_LEASE_FINANCING_AMT = "leaseFinancingAmt";
    public static final String FIELD_IF_TANGIBLE_MOVABLE = "ifTangibleMovable";
    public static final String FIELD_IF_OBTAIN_DEDUCTION = "ifObtainDeduction";
    public static final String FIELD_LEASE_REGISTRATION_NUMBER = "leaseRegistrationNumber";
    public static final String FIELD_ASSET_STATUS = "assetStatus";
    public static final String FIELD_ASSET_ACCOUNT_GROUP = "assetAccountGroup";

    @Id
    @GeneratedValue
    private Long leaseItemId;

    private Long ownerUserId;

    private String leaseItemCode;

    private String shortName;

    private String fullName;

    private String leaseItemType;

    private String enabledFlag;

    private String searchTerm ;

    private String serialNumber;

    private String pattern;

    private String uom;

    private Long quantity;

    private String currency;

    private Double price;
    @Transient
    private String bpName;

    private Double originalAssetValue;

    private Double netAssetValue;

    private Double accumulatedDepreciation;

    private Double detention;

    private Long manufacturerId;

    private String manufacturerName;

    private Date manufacturingDate;

    private Long venderId;

    private String venderName;

    private String shipModel;

    private String fixedAssetsSite;

    private String description;

    private String installationSite;

    private String shipClassification;


    //公允价值认定方式
    private String fairValueRecognition;

    //原始购买价
    private Double originPrice;
    //账面原值
    private Double originBookValue;

    private Double netBookValue;

    private Double assetValue;

    private Double completeValue;

    private Double fairValue;

    private String assetTypes;

    private String assetTypesSub;

    private String regulatoryAssetClass;

    private String regulatoryAssetClassSub;

    private Double nominalTransactionPrice;

    private Double includePrincipalTax;

    private Double leaseFinancingAmt;

    private String ifTangibleMovable;

    private String ifObtainDeduction;

    private String leaseRegistrationNumber;

    private String assetStatus;

    private String ifRegisteredOwnership;
    private String registrationAgency;
    private String ownershipRegisterStatus;
    private Date estimatedRegisterTime;
    private String otherInformation;
    private String middlemanName;
    private String brand;

    private Date startConstructDate;
    private Date factoryDeliveryDate;

    private String useage;

    private String specification;


    private Date collateralStartDate;
    private String collateralCategories;
    private String collateralClassifyOne;
    private String collateralClassifyTwo;
    private String mortgagor;
    private String mortgageSequence;
    private Date mortgageDateFrom;
    private Date mortgageDateTo;
    private Double mortgageAmount;
    private Date valuationDate;
    private String valuationUnit;
    private String usageType;
    private Double buildingArea;
    private String equityNumber;
    private Double floorArea;
    private String ifSinceTheMortgage;
    private String landLocated;
    private String landUse;

    private String authorityRuleString;

    @Transient
    private String assetTypesN;

    @Transient
    private String assetTypesSubN;

    @Transient
    private String leaseItemTypeN;
    @Transient
    private String mortgagorN;
    @Transient
    private String currencyN;
    @Transient
    private String regulatoryAssetClassN;
    @Transient
    private String regulatoryAssetClassSubN;
    @Transient
    private String collateralCategoriesN;
    @Transient
    private String collateralClassifyOneN;
    @Transient
    private String collateralClassifyTwoN;
    @Transient
    private String mortgageSequenceN;
    @Transient
    private String usageTypeN;
    @Transient
    private String ifSinceTheMortgageN;

    @Transient
    private String  assetStatusN;

    @Transient
    private String ifTangibleMovableN;

    @Transient
    private String ifObtainDeductionN;

    @Transient
    private String fairValueRecognitionN;

    @Transient
    private String ifRegisteredOwnershipN;

    @Transient
    private String venderIdN;

    @Transient
    private String ownershipRegisterStatusN;
    @Transient
    private String patternN;

    @Transient
    private String leaseCreateDate;

    @Transient
    private String ownerUserName;
    @Transient
    private String pledgeStateN;

    private String pledgeState;


    private String ifRequireInsurance;

    @Transient
    private String ifRequireInsuranceN;
    @Transient
    private String resultId;
    @Transient
    private String excelId;


    @Transient
    private Double netAssetValueTotal;
    @Transient
    private Double mortgageAmountTotal;
    @Transient
    private Double assetValueTotal;

    @Transient
    private Double pledgeValueTotal;

    @Transient
    private String  projectNumber;

    @Transient
    private String  contractNumber;
    @Transient
    private String  valuationMethodN;

    private String  valuationMethod;
    private Double  bondAmount;

    @Transient
    private String  contractStatus;

    //PLEDGE_CONTRACT_NUM
    private String pledgeContractNum;
    //PLEDGE_ASSET_VALUE
    private Long pledgeAssetValue;
    //PLEDGE_ASSET_DEC
    private Long pledgeAssetDec;

    //LEASE_ITEM_DET_ID
    private Long leaseItemDetId;

    private String patternDet;

    private String internationalRegistrationMsn;

    private Double assetValueTotalAll;

    @Transient
    private Date leaseStartDate;
    @Transient
    private Long allocationId;
    @Transient
    private Long hostProjectManager;
    @Transient
    private Long documentCreatedBy;

    @Transient
    private String assetName;
    @Transient
    private String imgNum;
    @Transient
    private String invoiceNum;
    @Transient
    private String assetNum;
    @Transient
    private String manufacturer;
}
