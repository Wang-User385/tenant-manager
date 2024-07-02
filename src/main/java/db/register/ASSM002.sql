resource_init(modules/CONT/ASSM002/con_contract_account_change_entrance.lview,收租账户变更入口,HTML,Y,Y);
resource_init(modules/CONT/ASSM002/con_contract_change_type_choose.lview,收租账户选择页面,HTML,Y,Y);

function_init(ASSM002,收租账户变更,Y,,modules/CONT/ASSM002/con_contract_account_change_entrance.lview,,1);
function_init(ASSM002F1,收租账户变更-选择页面,Y,,modules/CONT/ASSM002/con_contract_account_change_entrance.lview,ASSM002,1);

fun_resource(ASSM002,modules/CONT/ASSM002/con_contract_account_change_entrance.lview);
fun_resource(ASSM002,modules/CONT/ASSM002/con_contract_change_type_choose.lview);
fun_resource(ASSM002,modules/CONT/CON_CONTRACT/CONT302/con_contract_change_type_choose.lview);


