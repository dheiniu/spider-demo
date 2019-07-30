package com.mysteel.spider.provider;

import com.mysteel.spider.constant.ExchangeEnum;
import com.mysteel.spider.constant.QueuePriorityEnum;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @description:推送队列
 * @className: RabbitMqMessageSend
 * @see： com.mysteel.spider.provider
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:41
 */
@Component
public class RabbitMqMessageSend {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private RedisTemplate redisTemplate;

  /**
   * 推送消息 - 设置优先级
   * @param msg 消息
   * @param priority  优先级别  1-100
   */
  public void sendMessage(Object msg,String rouingKey,Integer priority){
    rabbitTemplate.convertAndSend(ExchangeEnum.EXCHANGE_SPIDER.getName(), rouingKey, msg,
      message -> {
        message.getMessageProperties().setPriority(priority);
        return message;
      });
  }


  /**
   * 推送消息 - 默认优先级
   * @param msg
   * @param rouingKey
   */
  public void sendMessage(Object msg,String rouingKey){
    rabbitTemplate.convertAndSend(ExchangeEnum.EXCHANGE_SPIDER.getName(), rouingKey, msg,
      message -> {
      //消息等级中位数
        message.getMessageProperties().setPriority(QueuePriorityEnum.VALUE.getMin());
        return message;
      });
  }


  /**
   * 发送消息，消息确认机制 - id=消息标识
   * @param msg
   * @param rouingKey
   * @param id
   */
  public void sendMessage(Object msg,String rouingKey,String id){
    CorrelationData correlationData = new CorrelationData(id);
    rabbitTemplate.convertAndSend(ExchangeEnum.EXCHANGE_SPIDER.getName(), rouingKey, msg,
      message -> {
        //消息等级中位数
        message.getMessageProperties().setPriority(QueuePriorityEnum.VALUE.getMin());
        return message;
      },correlationData);
  }





}
