-- Create table
create table CSH_DEPOSIT_manage_hd
(
  manage_hd_id    NUMBER(38) not null,
  manage_number VARCHAR2(100),
  contract_id                   NUMBER(38),
  cashflow_id          NUMBER(38),
  handling_method      VARCHAR2(100),
  execution_result          VARCHAR2(30),
  --以下标准who字段
  object_version_number   NUMBER(38) default 1,
  request_id              NUMBER(38) default -1,
  program_id              NUMBER(38) default -1,
  created_by              NUMBER(38) default -1,
  creation_date           TIMESTAMP(6) default CURRENT_TIMESTAMP,
  last_updated_by         NUMBER(38) default -1,
  description             VARCHAR2(100),
  last_update_date        TIMESTAMP(6) default CURRENT_TIMESTAMP,
  last_update_login       NUMBER(38) default -1
);
comment on table CSH_DEPOSIT_manage_hd is '保证金管理表';
-- Add comments to the columns
comment on column CSH_DEPOSIT_manage_hd.manage_number
  is '方案编号';
comment on column CSH_DEPOSIT_manage_hd.contract_id
  is '合同id';
comment on column CSH_DEPOSIT_manage_hd.cashflow_id
  is '合同现金流ID';
comment on column CSH_DEPOSIT_manage_hd.handling_method
  is '保证金处理方式(0-保证金代付期中租金，1-保证金处理方式变更)';
comment on column CSH_DEPOSIT_manage_hd.execution_result
  is '执行结果(0-新建，1-审批中，2-审批通过，3-已核销)';
-- Create/Recreate primary, unique and foreign key constraints 
alter table CSH_DEPOSIT_manage_hd
  add constraint CSH_DEPOSIT_manage_hd_PK primary key (manage_hd_id);
create sequence CSH_DEPOSIT_manage_hd_s;
