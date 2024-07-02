-- Create table
drop table CSH_WRITE_OFF_temp;
create table CSH_WRITE_OFF_temp
(
  
  temp_id                NUMBER(38) not null,
  uuid_str               VARCHAR2(300) not null,
  write_off_type              VARCHAR2(100) not null,
  transaction_category  VARCHAR2(300) not null,
  transaction_type      VARCHAR2(4000) not null,
  write_off_date              DATE not null,
  write_off_due_amount        NUMBER(20,2),
  write_off_principal         NUMBER(20,2),
  write_off_interest          NUMBER(20,2),
  contract_id                 NUMBER(38),
  cashflow_id                 NUMBER(38),
  times                       NUMBER(38),
  cf_item                     NUMBER(38),
  cf_type                     NUMBER(38),
  deposit_deduction           VARCHAR2(100),
  contract_number VARCHAR2(200),
  company_id  NUMBER(38),
  proposed_launch_date DATE,
  pp_contract_number VARCHAR2(200),
  pp_contract_name VARCHAR2(200),
  sum_toufang_amount NUMBER(38),
  loan_total_amount NUMBER(38),
  project_id NUMBER(38),
  project_name VARCHAR2(200),
  finance_amount NUMBER(38),
  unit_name VARCHAR2(200),
  unit_id NUMBER(38),
  bp_id NUMBER(38),
  bp_name VARCHAR2(200),
  contract_balance NUMBER(38),
  currency VARCHAR2(200),
  currency_id NUMBER(38),
  contract_currency_id NUMBER(38),
  contract_currency VARCHAR2(200),
  object_version_number       NUMBER(38) default 1,
  request_id                  NUMBER(38) default -1,
  program_id                  NUMBER(38) default -1,
  created_by                  NUMBER(38) default -1,
  creation_date               TIMESTAMP(6) default CURRENT_TIMESTAMP,
  last_updated_by             NUMBER(38) default -1,
  description                 VARCHAR2(100),
  last_update_date            TIMESTAMP(6) default CURRENT_TIMESTAMP,
  last_update_login           NUMBER(38) default -1
);
comment on table CSH_WRITE_OFF_temp
  is '抵扣、退还临时表';
  comment on column CSH_WRITE_OFF_temp.contract_id
  is '合同ID';
  comment on column CSH_WRITE_OFF_temp.temp_id
  is '主键';
  comment on column CSH_WRITE_OFF_temp.temp_id is '主键';
  comment on column CSH_WRITE_OFF_temp.uuid_str is 'UUID字符串';
  comment on column CSH_WRITE_OFF_temp.write_off_type is '核销类型';
  comment on column CSH_WRITE_OFF_temp.transaction_category is '事务类别';
  comment on column CSH_WRITE_OFF_temp.transaction_type is '事务类型';
  comment on column CSH_WRITE_OFF_temp.write_off_date is '核销日期';
  comment on column CSH_WRITE_OFF_temp.write_off_due_amount is '核销金额';
  comment on column CSH_WRITE_OFF_temp.write_off_principal is '核销本金';
  comment on column CSH_WRITE_OFF_temp.write_off_interest is '核销利息';
  comment on column CSH_WRITE_OFF_temp.cashflow_id is '现金流id';
  comment on column CSH_WRITE_OFF_temp.times is '期数';
  comment on column CSH_WRITE_OFF_temp.cf_item is '现金流项目';
  comment on column CSH_WRITE_OFF_temp.cf_type is '现金流项目类型';
  comment on column CSH_WRITE_OFF_temp.deposit_deduction is '抵扣类型(抵扣或者退还)';
  comment on column CSH_WRITE_OFF_temp.contract_number is '合同编号';
  comment on column CSH_WRITE_OFF_temp.company_id is '公司id';
  comment on column CSH_WRITE_OFF_temp.proposed_launch_date is '租赁起始日期';
  comment on column CSH_WRITE_OFF_temp.pp_contract_number is '项目合同编号';
  comment on column CSH_WRITE_OFF_temp.pp_contract_name is '项目合同名称';
  comment on column CSH_WRITE_OFF_temp.sum_toufang_amount is '已经投放金额';
  comment on column CSH_WRITE_OFF_temp.loan_total_amount is '放款总金额';
  comment on column CSH_WRITE_OFF_temp.project_id is '项目id';
comment on column CSH_WRITE_OFF_temp.project_name is '项目名称';
  comment on column CSH_WRITE_OFF_temp.finance_amount is '融资额';
  comment on column CSH_WRITE_OFF_temp.unit_name is '部门';
  comment on column CSH_WRITE_OFF_temp.unit_id is '部门id';
  comment on column CSH_WRITE_OFF_temp.bp_id is '商业伙伴id';
  comment on column CSH_WRITE_OFF_temp.bp_name is '承租人名称';
comment on column CSH_WRITE_OFF_temp.contract_balance is '合同差额（支付单）';
  comment on column CSH_WRITE_OFF_temp.currency is '项目币种';
  comment on column CSH_WRITE_OFF_temp.currency_id is '项目币种id';
  comment on column CSH_WRITE_OFF_temp.contract_currency_id is '合同币种id';
  comment on column CSH_WRITE_OFF_temp.contract_currency is '合同币种';
-- Create/Recreate primary, unique and foreign key constraints 
alter table CSH_WRITE_OFF_temp
  add constraint CSH_WRITE_OFF_temp_PK primary key (temp_id);
