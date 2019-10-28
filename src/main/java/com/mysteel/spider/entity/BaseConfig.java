package com.mysteel.spider.entity;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:爬虫基础配置
 * @className: BaseConfig
 * @see： com.mysteel.spider.entity
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:04
 */
@Data
@ToString
public class BaseConfig {

  public BaseConfig(){}

  /**
   * 任务ID
   */
  private String uuid;

  /**
   * 任务类型 selenium、html
   */
  private String taskType;

  /**
   * selenium 类型
   */
  private String sType;

  /**
   * 任务优先级
   */
  private Integer priority;


  /**
   * 用户代理
   * Chrome
   * Win7:
   * Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1
   *
   */
  private String userAgent;


  /**
   * 是否使用代理
   */
  private Boolean isProxy;


  /**
   * 请求头
   */
  private Map<String, String> headers = new HashMap<String, String>();


  /**
   * cookies
   */
  private Map<String, Map<String, String>> cookies = new HashMap<String, Map<String, String>>();


}
