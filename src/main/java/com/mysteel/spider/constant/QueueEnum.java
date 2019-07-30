package com.mysteel.spider.constant;

import lombok.Getter;

/**
 * @description:队列
 * @className: QueueEnum
 * @see： com.mysteel.spider.constant
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:35
 */
@Getter
public enum QueueEnum {


  /**
   *  html队列
   */
  HTML_QUEUE("test_html_queue","html.#","html.demo"),

  /**
   *  selenium队列
   */
  SELENIUM_QUEUE("test_selenium_queue","selenium.#","selenium.demo");



  /**
   * 队列名称
   */
  private String name;
  /**
   * 队列路由键
   */
  private String routingKey;

  /**
   * 发送消息到该队列的示例值(通用)
   */
  private String sendRoutingKey;


  QueueEnum(String name, String routingKey,String sendRoutingKey) {
    this.name = name;
    this.routingKey = routingKey;
    this.sendRoutingKey = sendRoutingKey;
  }


}
