
sys_code_delete(ABS.ACCOUNT_TYPE);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, COLLECTING_ACCOUNT, 募集账户, Y);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, ESCROW_ACCOUNT, 托管账户, Y);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, EXPENSE_ACCOUNT, 手续费账户, Y);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, RAISING_ACCOUNT, 归集账户, Y);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, REGULATORY_ACCOUNT, 监管账户, Y);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, REG_REC_ACCOUNT, 收款账户, Y);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, INTEREST_ACCOUNT, 还息账户, N);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, OTHER, 其他, N);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, PRINCIPAL_ACCOUNT, 还本账户, N);
sys_code_init(ABS.ACCOUNT_TYPE, 发债账户类型, Y, USER, REG_PAY_ACCOUNT, 普通付款账户, N);