package com.mysteel.spider.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @description:失败回调,交换机->队列
 * @className: ReturnCallBack
 * @see： com.mysteel.spider.callback
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:50
 */
public class ReturnCallBack implements RabbitTemplate.ReturnCallback {

  private static Logger logger = LoggerFactory.getLogger(ReturnCallBack.class);

  @Override
  public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
    //发送了错误才会提醒
    logger.info("【交换机 -> 队列】:{},{}",replyCode,routingKey);
//    System.err.println("----------------失败回调--------------");
//    /**
//     * message: 发送的消息 + 发送消息的配置
//     */
//    System.err.println(message);
//
//    /**
//     * 状态码
//     */
//    System.err.println(replyCode);
//
//    /**
//     * 失败的信息
//     */
//    System.err.println(replyText);
//
//    /**
//     * 交换机
//     */
//    System.err.println(exchange);
//
//    /**
//     * 那个队列  路由键
//     */
//    System.err.println(routingKey);
  }
}
