package com.hand.hap.core.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HDRabbitMqConfiguration {
    /**
     * Direct模式:汉得审核结果消息交换机
     */
    public static final String EXCHANGE_GT_HD = "exchange.gt_hd";

    /**
     * Direct模式:汉得审核结果消息队列
     */
    public static final String QUEUE_GT_HD_ORDER_AUDIT_RESULT = "HD-YL-N001";

    /**
     * 汉得放款结果通知队列
     */
    public static final String QUEUE_GT_HD_ORDER_LOAN_RESULT = "HD-YL-N002";

    /**
     * 汉得关单结果通知队列
     */
    public static final String QUEUE_GT_HD_ORDER_CLOSED_NOTIFY = "HD-YL-N003";

    /**
     * 汉得还款计划生成通知队列
     */
    public static final String QUEUE_GT_HD_REPAY_PLAN_CREATED_NOTIFY = "HD-YL-N004";

    /**
     * 汉得逾期算费完成通知队列
     */
    public static final String QUEUE_GT_HD_OVERDUE_CALCULATE_FINISHED_NOTIFY = "HD-YL-N005";

    /**
     * 汉得需代偿通知队列
     */
    public static final String QUEUE_GT_HD_ASSET_NEED_SUBSTITUTE = "HD-YL-N006";

    /**
     * 汉得需回购通知队列
     */
    public static final String QUEUE_GT_HD_ASSET_NEED_BUYBACK = "HD-YL-N007";

    /**
     * 汉得代扣签约结果通知队列
     */
    public static final String QUEUE_GT_HD_WITHHOLD_CONTRACT_RESULT = "HD-YL-N008";

    /**
     * 汉得期次代扣结果通知队列
     */
    public static final String QUEUE_GT_HD_REPAY_PLAN_REPAID_NOTIFY = "HD-YL-N009";


    /**
     * 声明Direct交换机对象:汉得审核结果消息交换机
     */
    @Bean
    DirectExchange gtHdExchange() {
        return new DirectExchange(EXCHANGE_GT_HD, true, false);
    }

    @Bean
    Queue gtHdQueueA() {
        return new Queue(QUEUE_GT_HD_ORDER_AUDIT_RESULT, true, false, false);
    }

    @Bean
    Queue gtHdQueueB() {
        return new Queue(QUEUE_GT_HD_ORDER_LOAN_RESULT, true, false, false);
    }

    @Bean
    Queue gtHdQueueC() {
        return new Queue(QUEUE_GT_HD_ORDER_CLOSED_NOTIFY, true, false, false);
    }

    @Bean
    Queue gtHdQueueD() {
        return new Queue(QUEUE_GT_HD_REPAY_PLAN_CREATED_NOTIFY, true, false, false);
    }

    @Bean
    Queue gtHdQueueE() {
        return new Queue(QUEUE_GT_HD_OVERDUE_CALCULATE_FINISHED_NOTIFY, true, false, false);
    }

    @Bean
    Queue gtHdQueueF() {
        return new Queue(QUEUE_GT_HD_ASSET_NEED_SUBSTITUTE, true, false, false);
    }

    @Bean
    Queue gtHdQueueG() {
        return new Queue(QUEUE_GT_HD_ASSET_NEED_BUYBACK, true, false, false);
    }

    @Bean
    Queue gtHdQueueH() {
        return new Queue(QUEUE_GT_HD_WITHHOLD_CONTRACT_RESULT, true, false, false);
    }

    @Bean
    Queue gtHdQueueI() {
        return new Queue(QUEUE_GT_HD_REPAY_PLAN_REPAID_NOTIFY, true, false, false);
    }


    /**
     * 将队列绑定到交换机上:
     */
    @Bean
    Binding DirectBindingA() {
        return BindingBuilder.bind(gtHdQueueA()).to(gtHdExchange()).with("n001");
    }

    @Bean
    Binding DirectBindingB() {
        return BindingBuilder.bind(gtHdQueueB()).to(gtHdExchange()).with("n002");
    }

    @Bean
    Binding DirectBindingC() {
        return BindingBuilder.bind(gtHdQueueC()).to(gtHdExchange()).with("n003");
    }

    @Bean
    Binding DirectBindingD() {
        return BindingBuilder.bind(gtHdQueueD()).to(gtHdExchange()).with("n004");
    }

    @Bean
    Binding DirectBindingE() {
        return BindingBuilder.bind(gtHdQueueE()).to(gtHdExchange()).with("n005");
    }

    @Bean
    Binding DirectBindingF() {
        return BindingBuilder.bind(gtHdQueueF()).to(gtHdExchange()).with("n006");
    }

    @Bean
    Binding DirectBindingG() {
        return BindingBuilder.bind(gtHdQueueG()).to(gtHdExchange()).with("n007");
    }

    @Bean
    Binding DirectBindingH() {
        return BindingBuilder.bind(gtHdQueueH()).to(gtHdExchange()).with("n008");
    }

    @Bean
    Binding DirectBindingI() {
        return BindingBuilder.bind(gtHdQueueI()).to(gtHdExchange()).with("n009");
    }

}
