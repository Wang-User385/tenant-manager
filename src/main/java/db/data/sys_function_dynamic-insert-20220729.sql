begin
insert into sys_function_dynamic
    (function_code,
     function_name,
     sequence,
     creation_date,
     created_by,
     last_update_date,
     last_updated_by)
  values
    ('CSH1001F1', '保证金管理创建入口', 10, sysdate, -1, sysdate, -1);
insert into sys_function_dynamic
    (function_code,
     function_name,
     sequence,
     creation_date,
     created_by,
     last_update_date,
     last_updated_by)
  values
    ('CSH1001F2', '保证金代付期中租金', 10, sysdate, -1, sysdate, -1);
insert into sys_function_dynamic
    (function_code,
     function_name,
     sequence,
     creation_date,
     created_by,
     last_update_date,
     last_updated_by)
  values
    ('CSH1001F3', '处理方式变更', 10, sysdate, -1, sysdate, -1);
commit;
end;
