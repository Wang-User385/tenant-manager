package com.hand.hap.core.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YLRabbitMqConfiguration {
    /**
     * Direct模式:易靓审核结果消息交换机
     */
    public static final String EXCHANGE_GT_YL = "exchange.gt_yl";

    /**
     * Direct模式:易靓审核结果消息队列
     */
    public static final String QUEUE_GT_YL_ORDER_AUDIT_RESULT = "ORDER_AUDIT_RESULT";

    /**
     * 易靓放款结果通知队列
     */
    public static final String QUEUE_GT_YL_ORDER_LOAN_RESULT = "ORDER_LOAN_RESULT";

    /**
     * 易靓关单结果通知队列
     */
    public static final String QUEUE_GT_YL_ORDER_CLOSED_NOTIFY = "ORDER_CLOSED_NOTIFY";

    /**
     * 易靓还款计划生成通知队列
     */
    public static final String QUEUE_GT_YL_REPAY_PLAN_CREATED_NOTIFY = "REPAY_PLAN_CREATED_NOTIFY";

    /**
     * 易靓逾期算费完成通知队列
     */
    public static final String QUEUE_GT_YL_OVERDUE_CALCULATE_FINISHED_NOTIFY = "OVERDUE_CALCULATE_FINISHED_NOTIFY";

    /**
     * 易靓需代偿通知队列
     */
    public static final String QUEUE_GT_YL_ASSET_NEED_SUBSTITUTE = "ASSET_NEED_SUBSTITUTE";

    /**
     * 易靓需回购通知队列
     */
    public static final String QUEUE_GT_YL_ASSET_NEED_BUYBACK = "ASSET_NEED_BUYBACK";

    /**
     * 易靓代扣签约结果通知队列
     */
    public static final String QUEUE_GT_YL_WITHHOLD_CONTRACT_RESULT = "WITHHOLD_CONTRACT_RESULT";

    /**
     * 易靓期次代扣结果通知队列
     */
    public static final String QUEUE_GT_YL_REPAY_PLAN_REPAID_NOTIFY = "REPAY_PLAN_REPAID_NOTIFY";


    /**
     * 声明Direct交换机对象:易靓审核结果消息交换机
     */
    @Bean
    DirectExchange gtYlExchange() {
        return new DirectExchange(EXCHANGE_GT_YL, true, false);
    }

    @Bean
    Queue gtYlQueueA() {
        return new Queue(QUEUE_GT_YL_ORDER_AUDIT_RESULT, true, false, false);
    }

    @Bean
    Queue gtYlQueueB() {
        return new Queue(QUEUE_GT_YL_ORDER_LOAN_RESULT, true, false, false);
    }

    @Bean
    Queue gtYlQueueC() {
        return new Queue(QUEUE_GT_YL_ORDER_CLOSED_NOTIFY, true, false, false);
    }

    @Bean
    Queue gtYlQueueD() {
        return new Queue(QUEUE_GT_YL_REPAY_PLAN_CREATED_NOTIFY, true, false, false);
    }

    @Bean
    Queue gtYlQueueE() {
        return new Queue(QUEUE_GT_YL_OVERDUE_CALCULATE_FINISHED_NOTIFY, true, false, false);
    }

    @Bean
    Queue gtYlQueueF() {
        return new Queue(QUEUE_GT_YL_ASSET_NEED_SUBSTITUTE, true, false, false);
    }

    @Bean
    Queue gtYlQueueG() {
        return new Queue(QUEUE_GT_YL_ASSET_NEED_BUYBACK, true, false, false);
    }

    @Bean
    Queue gtYlQueueH() {
        return new Queue(QUEUE_GT_YL_WITHHOLD_CONTRACT_RESULT, true, false, false);
    }

    @Bean
    Queue gtYlQueueI() {
        return new Queue(QUEUE_GT_YL_REPAY_PLAN_REPAID_NOTIFY, true, false, false);
    }


    /**
     * 将队列绑定到交换机上:
     */
    @Bean
    Binding DirectBindingA() {
        return BindingBuilder.bind(gtYlQueueA()).to(gtYlExchange()).with("n001");
    }

    @Bean
    Binding DirectBindingB() {
        return BindingBuilder.bind(gtYlQueueB()).to(gtYlExchange()).with("n002");
    }

    @Bean
    Binding DirectBindingC() {
        return BindingBuilder.bind(gtYlQueueC()).to(gtYlExchange()).with("n003");
    }

    @Bean
    Binding DirectBindingD() {
        return BindingBuilder.bind(gtYlQueueD()).to(gtYlExchange()).with("n004");
    }

    @Bean
    Binding DirectBindingE() {
        return BindingBuilder.bind(gtYlQueueE()).to(gtYlExchange()).with("n005");
    }

    @Bean
    Binding DirectBindingF() {
        return BindingBuilder.bind(gtYlQueueF()).to(gtYlExchange()).with("n006");
    }

    @Bean
    Binding DirectBindingG() {
        return BindingBuilder.bind(gtYlQueueG()).to(gtYlExchange()).with("n007");
    }

    @Bean
    Binding DirectBindingH() {
        return BindingBuilder.bind(gtYlQueueH()).to(gtYlExchange()).with("n008");
    }

    @Bean
    Binding DirectBindingI() {
        return BindingBuilder.bind(gtYlQueueI()).to(gtYlExchange()).with("n009");
    }

}
