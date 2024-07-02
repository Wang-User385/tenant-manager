ALTER TABLE `gyzl_dev`.`con_contract`
MODIFY COLUMN `penalty_rate` double(20, 10) NULL DEFAULT NULL COMMENT '违约金利率（天）' ;