package com.mysteel.spider.constant;

import lombok.Getter;

/**
 * @description:任务类型
 * @className: TaskWayEnum
 * @see： com.mysteel.spider.constant
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:44
 */
@Getter
public enum TaskWayEnum {

  /**
   * 动态 selenium
   */
  SELENIUM("selenium"),

  /**
   * 静态 html
   */
  HTML("html");

  private String type;

  TaskWayEnum(String type) {
    this.type = type;
  }

}
