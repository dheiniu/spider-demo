package com.mysteel.spider.constant;

import lombok.Getter;

/**
 * @description:selenium任务类型
 * @className: STaskType
 * @see： com.mysteel.spider.constant
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:35
 */
@Getter
public enum STaskType {

  /**
   * 模板类型
   */
  TEMPLATE("template"),

  /**
   * 步骤类型
   */
  STEP("step");

  private String type;

  STaskType(String type) {
    this.type = type;
  }
}
