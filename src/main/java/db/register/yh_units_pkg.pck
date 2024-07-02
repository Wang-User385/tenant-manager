create or replace package yh_units_pkg is

  -- Author  : MI
  -- Created : 2020/2/18 14:50:02
  -- Purpose :

  function get_process_ids(p_project_id number) return varchar2;

end yh_units_pkg;
/
create or replace package body yh_units_pkg is

  function process_ids_unit(p_process_ids varchar2, p_proc_inst_id number)
    return varchar2 is
    v_process_ids varchar2(2000);
  begin
    v_process_ids := p_process_ids;
    if p_proc_inst_id is not null then
      v_process_ids := v_process_ids || p_proc_inst_id || ',';
    end if;
  
    return v_process_ids;
  end;

  /*p_project_id 进件ID*/
  function get_process_ids(p_project_id number) return varchar2 is
    v_process_ids   varchar2(2000);
    v_proc_inst_id  number;
    v_count         number;
    v_chance        hls_credit_line_chance%rowtype;
    v_jd_project_id number;
  begin
  
    if p_project_id is not null then
      --立项
      select C.*
        into v_chance
        from hls_credit_line_chance c
       where c.bp_id = (select p.MANUFACTURER_ID
                          from prj_project p
                         where p.project_id = p_project_id)
         and c.credit_line_status not in ('CANCEL', 'REJECTED');
    
      select count(1)
        into v_count
        from act_hi_procinst ahp
       where ahp.business_key_ = v_chance.chance_id
         and ahp.proc_def_id_ like 'FCT_PROJECTCREATE_WFL%';
    
      if v_count > 0 then
        select ahp.proc_inst_id_
          into v_proc_inst_id
          from act_hi_procinst ahp
         where ahp.business_key_ = v_chance.chance_id
           and ahp.proc_def_id_ like 'FCT_PROJECTCREATE_WFL%';
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => v_proc_inst_id);
      end if;
    
      --项目评审
      select C.PROJECT_ID
        into v_jd_project_id
        from prj_project c
       where c.tenant_id =
             (select p.MANUFACTURER_ID
                from prj_project p
               where p.project_id = p_project_id)
         and c.project_status not in ('CANCEL', 'REJECTED')
         and c.data_class = 'NORMAL'
         and c.DATA_TYPE='NORMAL'
         and c.credit_flag = 'Y';
      select count(1)
        into v_count
        from act_hi_procinst ahp
       where ahp.business_key_ = v_jd_project_id
         and ahp.proc_def_id_ like 'PROJECT_REVIEW_WFL%';
    
      if v_count > 0 then
        select ahp.proc_inst_id_
          into v_proc_inst_id
          from act_hi_procinst ahp
         where ahp.business_key_ = v_jd_project_id
           and ahp.proc_def_id_ like 'PROJECT_REVIEW_WFL%';
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => v_proc_inst_id);
      end if;
    
      --主机厂/经销商准入审批
      --主机厂
      select count(1)
        into v_count
        from act_hi_procinst ahp, prj_project pp
       where ahp.business_key_ = pp.factory_id
         and pp.project_id = p_project_id
         and ahp.proc_def_id_ like 'BP_ADMIT_WORK_FLOW%';
    
      if v_count > 0 then
        select ahp.proc_inst_id_
          into v_proc_inst_id
          from act_hi_procinst ahp, prj_project pp
         where ahp.business_key_ = pp.factory_id
           and pp.project_id = p_project_id
           and ahp.proc_def_id_ like 'BP_ADMIT_WORK_FLOW%';
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => v_proc_inst_id);
      end if;
    
      --经销商
      select count(1)
        into v_count
        from act_hi_procinst ahp, prj_project pp
       where ahp.business_key_ = pp.bp_id_vender
         and pp.project_id = p_project_id
         and ahp.proc_def_id_ like 'BP_ADMIT_WORK_FLOW%';
    
      if v_count > 0 then
        select ahp.proc_inst_id_
          into v_proc_inst_id
          from act_hi_procinst ahp, prj_project pp
         where ahp.business_key_ = pp.factory_id
           and pp.project_id = p_project_id
           and ahp.proc_def_id_ like 'BP_ADMIT_WORK_FLOW%';
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => v_proc_inst_id);
      end if;
    
      --进件审批
      select count(1)
        into v_count
        from act_hi_procinst ahp
       where ahp.business_key_ = p_project_id
         and ahp.proc_def_id_ like 'PRJ_PROJECT%';
    
      if v_count > 0 then
        select ahp.proc_inst_id_
          into v_proc_inst_id
          from act_hi_procinst ahp
         where ahp.business_key_ = p_project_id
           and ahp.proc_def_id_ like 'PRJ_PROJECT%';
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => v_proc_inst_id);
      end if;
    
      --退款申请审批
    
      for refund_cur in (select ahp.proc_inst_id_
                           from act_hi_procinst           ahp,
                                CSH_TRANSACTION_REFUND_LN ctrl,
                                csh_transaction_refund    ctr,
                                csh_transaction           tra,
                                con_contract              cc
                          where ahp.business_key_ = ctr.refund_id
                            and ctr.refund_id = ctrl.refund_id
                            and ctrl.source_transaction_id =
                                tra.transaction_id
                            and tra.contract_id = cc.contract_id
                            and cc.project_id = p_project_id
                            and ahp.proc_def_id_ like
                                'CSH_REFUND_APPLY_WORK_FLOW%') loop
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => refund_cur.proc_inst_id_);
      
      end loop;
    
      --合同投放审查审批
      select count(1)
        into v_count
        from act_hi_procinst ahp
       where ahp.business_key_ = p_project_id
         and ahp.proc_def_id_ like 'PROJECT_SIGN_WORK_FLOW%';
    
      if v_count > 0 then
        select ahp.proc_inst_id_
          into v_proc_inst_id
          from act_hi_procinst ahp
         where ahp.business_key_ = p_project_id
           and ahp.proc_def_id_ like 'PROJECT_SIGN_WORK_FLOW%';
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => v_proc_inst_id);
      end if;
    
      --保证金抵扣申请
      select count(1)
        into v_count
        from act_hi_procinst           ahp,
             csh_deposit_deduct_req_hd cdd,
             csh_transaction           tra,
             con_contract              cc
       where ahp.business_key_ = cdd.req_hd_id
         and cdd.transaction_id = tra.transaction_id
         and tra.contract_id = cc.contract_id
         and cc.project_id = p_project_id
         and ahp.proc_def_id_ like 'CSH_DEPOSIT_DEDUCT_REQ%';
    
      if v_count > 0 then
        select ahp.proc_inst_id_
          into v_proc_inst_id
          from act_hi_procinst           ahp,
               csh_deposit_deduct_req_hd cdd,
               csh_transaction           tra,
               con_contract              cc
         where ahp.business_key_ = cdd.req_hd_id
           and cdd.transaction_id = tra.transaction_id
           and tra.contract_id = cc.contract_id
           and cc.project_id = p_project_id
           and ahp.proc_def_id_ like 'CSH_DEPOSIT_DEDUCT_REQ%';
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => v_proc_inst_id);
      end if;
    
      --付款申请审批
      for payment_cur in (select ahp.proc_inst_id_
                            from act_hi_procinst    ahp,
                                 csh_payment_req_ln cprl,
                                 csh_payment_req_hd cprh,
                                 con_contract       cc
                           where ahp.business_key_ = cprh.payment_req_id
                             and cprh.payment_req_id = cprl.payment_req_id
                             and cprl.source_doc_id = cc.contract_id
                             and cprl.source_doc_category = 'CON_CONTRACT'
                             and cc.project_id = p_project_id
                             and (ahp.proc_def_id_ like
                                 'CSH_PAYMENT_APPLY_WORK_FLOW%' or
                                 ahp.proc_def_id_ like
                                 'CSH_PAYMENT_APPLY_WORK_FLOW_CAR%')) loop
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => payment_cur.proc_inst_id_);
      
      end loop;
    
      --付款支付复核
      for slip_cur in (select ahp.proc_inst_id_
                         from act_hi_procinst         ahp,
                              Csh_Payment_Req_Slip_Ln cprs,
                              csh_payment_req_ln      cprl,
                              con_contract            cc
                        where ahp.business_key_ = cprs.slip_id
                          and cprs.document_id = cprl.payment_req_ln_id
                          and cprs.document_type = 'CSH_PAYMENT_REQ_LN'
                          and cprl.source_doc_id = cc.contract_id
                          and cprl.source_doc_category = 'CON_CONTRACT'
                          and cc.project_id = p_project_id
                          and ahp.proc_def_id_ like
                              'CSH_PAYMENT_CHECK_WORK_FLOW%'
                       union
                       select ahp.proc_inst_id_
                         from act_hi_procinst           ahp,
                              Csh_Payment_Req_Slip_Ln   cprs,
                              csh_transaction_refund_ln ctrl,
                              csh_transaction           tra,
                              con_contract              cc
                        where ahp.business_key_ = cprs.slip_id
                          and cprs.document_id = ctrl.ln_id
                          and cprs.document_type =
                              'CSH_TRANSACTION_REFUND_LINE'
                          and ctrl.source_transaction_id =
                              tra.transaction_id
                          and tra.contract_id = cc.contract_id
                          and cc.project_id = p_project_id
                          and ahp.proc_def_id_ like
                              'CSH_PAYMENT_CHECK_WORK_FLOW%') loop
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => slip_cur.proc_inst_id_);
      
      end loop;
    
      --合同变更
      for con_cur in (select ahp.proc_inst_id_
                        from act_hi_procinst         ahp,
                             con_contract            cc,
                             con_contract_change_req ccc
                       where ahp.business_key_ = ccc.change_req_id
                         and ccc.contract_id = cc.contract_id
                         and cc.project_id = p_project_id
                         and ahp.proc_def_id_ like 'CONTRACT_CHANGE%') loop
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => con_cur.proc_inst_id_);
      end loop;
    
      --产品定义审批
      for credit_cur in (select ahp.proc_inst_id_
                           from act_hi_procinst        ahp,
                                hls_product_definition hpd
                          where ahp.business_key_ = hpd.definition_id
                            and hpd.reply_product_id = p_project_id
                            and ahp.proc_def_id_ like 'PRODUCT_DEFINITION%') loop
      
        v_process_ids := process_ids_unit(p_process_ids  => v_process_ids,
                                          p_proc_inst_id => credit_cur.proc_inst_id_);
      end loop;
    
      if v_process_ids is not null then
      
        v_process_ids := substr(v_process_ids, 1, length(v_process_ids) - 1);
      
      end if;
    
    end if;
  
    return(v_process_ids);
  end get_process_ids;

end yh_units_pkg;
/
