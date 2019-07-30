package com.mysteel.spider.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @description:发送方确认,生产者->交换机
 * @className: MyCallBack
 * @see： com.mysteel.spider.callback
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:50
 */
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback {

  private static Logger logger = LoggerFactory.getLogger(MyCallBack.class);

  @Override
  public void confirm(CorrelationData correlationData, boolean ack, String cause) {
    logger.info("【生产者 -> 交换机】:{}",ack);

//    System.out.println("----------------发送确认机制--------------");
//    /**
//     * 判断消息是否发送成功
//     */
//    System.out.println(ack);
//
//    /**
//     * 发送失败错误日志
//     */
//    System.out.println(cause);
//
//    /**
//     * 消息的标识 - 业务信息
//     */
//    System.out.println(correlationData);
  }




}
