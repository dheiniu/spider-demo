package com.mysteel.spider.constant;

import lombok.Getter;

/**
 * @description:队列优先级
 * @className: QueuePriorityEnum
 * @see： com.mysteel.spider.constant
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:35
 */
@Getter
public enum QueuePriorityEnum {


  /**
   * 优先级
   */
  VALUE(1,100);


  private int min;

  private int max;

  QueuePriorityEnum(int min, int max) {
    this.min = min;
    this.max = max;
  }


}
