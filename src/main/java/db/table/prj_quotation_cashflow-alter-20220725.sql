--alter table prj_quotation_cashflow drop column deposit_deduction;
-- Add/modify columns 
alter table prj_quotation_cashflow add deposit_deduction number;
-- Add comments to the columns 
comment on column prj_quotation_cashflow.deposit_deduction
  is '保证金抵扣金额';
