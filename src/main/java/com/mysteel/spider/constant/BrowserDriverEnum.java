package com.mysteel.spider.constant;

import lombok.Getter;

/**
 * @description:游览器
 * @className: BrowserDriverEnum
 * @see： com.mysteel.spider.constant
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:33
 */
@Getter
public enum BrowserDriverEnum {

  /**
   *  谷歌
   */
  CHROME("谷歌","chrome"),

  /**
   *  火狐
   */
  FIRE_FOX("火狐","firefox");


  /**
   * 队列名称
   */
  private String name;
  /**
   * 队列路由键
   */
  private String key;


  BrowserDriverEnum(String name, String key) {
    this.name = name;
    this.key = key;
  }
}
