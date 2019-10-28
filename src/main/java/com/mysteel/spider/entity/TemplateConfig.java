package com.mysteel.spider.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @description:爬虫模板配置
 * @className: TemplateConfig
 * @see： com.mysteel.spider.entity
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:04
 */
@Data
@ToString
public class TemplateConfig extends BaseConfig{

  public TemplateConfig(){
    super();
  }

  /**
   * selenium模板名称
   */
  private String tName;

  /**
   * 目标页
   */
  private String domain;

  /**
   * 是否包含子任务
   */
  private Boolean containSubTask;

}
