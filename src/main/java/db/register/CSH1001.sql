/* Leaf 页面注册初始化脚本 */
/* 生成时间 ：2022-07-28 14:41:31*/
/* 资源定义   c*/
resource_init(modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd.lview, 保证金管理, HTML);
resource_init(modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd_entrance.lview, 保证金管理创建入口页, HTML);
resource_init(csh/deposit/manage/hd/queryByField, 保证金管理主页面查询, SERVICE);
resource_init(modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd_pay_detail.lview, 保证金代付期中租金页面, HTML);
resource_init(modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd_change_detail.lview, 处理方式变更页面, HTML);
/* 模块定义 module_init(PRJ, PRJ);*/

/* 功能定义 */
function_init(CSH1001, 保证金管理, Y, CSH_TRX_PAY, modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd.lview, , 20);
/* function_init(CSH1001F1, 保证金管理创建入口, Y, CSH_TRX_PAY, modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd_entrance.lview, , 20);
function_init(CSH1001F2, 保证金代付期中租金, Y, CSH_TRX_PAY, modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd_pay_detail.lview, , 20);
function_init(CSH1001F3, 处理方式变更, Y, CSH_TRX_PAY, modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd_change_detail.lview, , 20);*/
/* 功能资源分配 fun_resource(CSH1001, hls/credit/line/chance/closeCreditChance);*/
fun_resource(CSH1001, modules/COMMON/LAYOUT/hls_common_get_layout_code.lview);
fun_resource(CSH1001, modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd.lview);
fun_resource(CSH1001, modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd_entrance.lview);
fun_resource(CSH1001, modules/LON/LON_CONTRACT/LON001/lon_contract_apply.lview);
fun_resource(CSH1001, modules/CONT/CON_CONTRACT/CONT301/con_contract_rent_payment_confirm_detail.lview);
fun_resource(CSH1001, modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd_pay_detail.lview);
fun_resource(CSH1001, modules/CSH/CSH_TRX/CSH1001/deposit_manage_hd_change_detail.lview);
fun_resource(CSH1001, csh/deposit/manage/hd/queryByField);

/* 角色功能分配 role_function(ADMIN, CSH1001); */
role_function(ADMIN, CSH1001);
/* 角色模块分配role_module(ADMIN, PRJ); */

/* 系统代码 */
sys_code_init(CSH1001.HANDLING_METHOD, 保证金处理方式, Y, USER, 0, 保证金代付期中租金, Y);
sys_code_init(CSH1001.HANDLING_METHOD, 保证金处理方式, Y, USER, 1, 保证金处理方式变更, Y);
sys_code_init(CSH1001.HANDLING_METHOD, 保证金处理方式, Y, USER, 2, 保证金期末退还, Y);
sys_code_init(CSH1001.HANDLING_METHOD, 保证金处理方式, Y, USER, 3, 保证金期末抵扣, Y);

sys_code_init(CSH1001.EXECUTION_RESULT, 执行结果, Y, USER, 0, 新建, Y);
sys_code_init(CSH1001.EXECUTION_RESULT, 执行结果, Y, USER, 1, 审批中, Y);
sys_code_init(CSH1001.EXECUTION_RESULT, 执行结果, Y, USER, 2, 审批通过, Y);
sys_code_init(CSH1001.EXECUTION_RESULT, 执行结果, Y, USER, 3, 已核销, Y);

