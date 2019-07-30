package com.mysteel.spider.constant;

import lombok.Getter;

/**
 * @description:MqExchange
 * @className: ExchangeEnum
 * @see： com.mysteel.spider.constant
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:34
 */
@Getter
public enum ExchangeEnum {

  /**
   * 路由
   */
  EXCHANGE_SPIDER("sprider_exchange");

  private String name;

  ExchangeEnum(String name) {
    this.name = name;
  }

}
