package com.mysteel.spider.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:游览器代理
 * @className: BrowerProxy
 * @see： com.mysteel.spider.entity
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/30 9:51
 */
@Data
@Builder
public class BrowerProxy implements Serializable {

  /**
   * 浏览器标识
   */
  private String browerId;

  /**
   * 任务标识
   */
  private String taskUuid;

  /**
   * 用户代理
   * Chrome
   * Win7:
   * Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1
   *
   */
  private String userAgent;


  /**
   * 请求头
   */
  private Map<String, String> headers = new HashMap<String, String>();


  /**
   * cookies
   */
  private Map<String, Map<String, String>> cookies = new HashMap<String, Map<String, String>>();

}
