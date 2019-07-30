package com.mysteel.spider.config;


import com.alibaba.fastjson.JSON;
import com.mysteel.spider.callback.MyCallBack;
import com.mysteel.spider.callback.ReturnCallBack;
import com.mysteel.spider.constant.ExchangeEnum;
import com.mysteel.spider.constant.QueueEnum;
import com.mysteel.spider.constant.QueuePriorityEnum;
import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:RabbitMq配置
 * @className: RabbitConfig
 * @see： com.mysteel.spider.config
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:32
 */
@Configuration
public class RabbitConfig {

  @Value("${rabbitmq.host}")
  private String host;

  @Value("${rabbitmq.username}")
  private String username;

  @Value("${rabbitmq.password}")
  private String password;

  @Value("${rabbitmq.port}")
  private Integer port;

  @Value("${rabbitmq.virtualHost}")
  private String virtualHost;



  @Bean
  public ConnectionFactory connectionFactory(){
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setHost(this.host);
    connectionFactory.setUsername(this.username);
    connectionFactory.setPassword(this.password);
    connectionFactory.setPort(this.port);
    connectionFactory.setVirtualHost(this.virtualHost);
    //开启发送方消息确认机制
    connectionFactory.setPublisherConfirms(true);
    return connectionFactory;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    //消息回调  MyCallBack()
    rabbitTemplate.setConfirmCallback(new MyCallBack());
    //开启失败回调
    rabbitTemplate.setMandatory(true);
    rabbitTemplate.setReturnCallback(new ReturnCallBack());
    //定义消息转换器
    rabbitTemplate.setMessageConverter(new MessageConverter() {
      /**
       * 发送转化
       * @param o
       * @param messageProperties
       * @return
       * @throws MessageConversionException
       */
      @Override
      public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        //消息解析器
        messageProperties.setContentType("text/xml");
        messageProperties.setContentEncoding("UTF-8");
        return new Message(JSON.toJSONBytes(o),messageProperties);
      }

      /**
       * 接收转换
       * @param message
       * @return
       * @throws MessageConversionException
       */
      @Override
      public Object fromMessage(Message message) throws MessageConversionException {
        return null;
      }
    });

    return rabbitTemplate;
  }

  /**
   * 定义交换器
   */
  @Bean
  TopicExchange exchange() {
    return new TopicExchange(ExchangeEnum.EXCHANGE_SPIDER.getName(),true,false);
  }

  /**
   * HTML处理的队列
   * @return
   */
  @Bean
  Queue htmlQueue() {
    Map<String, Object> args= new HashMap<>(1);
    args.put("x-max-priority", QueuePriorityEnum.VALUE.getMax());
    return new Queue(QueueEnum.HTML_QUEUE.getName(), true, false, false, args);
  }

  /**
   * selenium处理的队列
   * @return
   */
  @Bean
  Queue seleniumQueue() {
    Map<String, Object> args= new HashMap<>(1);
    args.put("x-max-priority", QueuePriorityEnum.VALUE.getMax());
    return new Queue(QueueEnum.SELENIUM_QUEUE.getName(), true, false, false, args);
  }


  @Bean
  Binding htmlBinding(Queue htmlQueue, TopicExchange exchange) {
    return BindingBuilder.bind(htmlQueue).to(exchange).with(QueueEnum.HTML_QUEUE.getRoutingKey());
  }

  @Bean
  Binding seleniumBinding(Queue seleniumQueue, TopicExchange exchange) {
    return BindingBuilder.bind(seleniumQueue).to(exchange).with(QueueEnum.SELENIUM_QUEUE.getRoutingKey());
  }

  /**
   * simpleRabbitListenerContainerFactory  定义
   * @return
   */
  @Bean
  public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(){
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
    simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory());
    //手动ack确认
    simpleRabbitListenerContainerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    //设置消息预取的数量 = channel.basicQos(1);
    simpleRabbitListenerContainerFactory.setPrefetchCount(1);
    return simpleRabbitListenerContainerFactory;
  }

}
