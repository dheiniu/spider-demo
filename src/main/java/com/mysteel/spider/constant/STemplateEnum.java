package com.mysteel.spider.constant;

import lombok.Getter;

/**
 * @description:selenium网站模板类型
 * @className: STemplateEnum
 * @see： com.mysteel.spider.constant
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:35
 */
@Getter
public enum  STemplateEnum {

  /**
   * 掘金 模板
   */
  JUE_JIN("公共模板",1,"juejinTemplate","https://juejin.im/"),

  /**
   * 微信模板
   */
  WEI_XIN("微信模板",2,"weiXinTemplate","https://mp.weixin.qq.com/s/cKlTa4XBeX6gvaW1iQXVyw"),

  /**
   * 天沃科技 模板
   */
  THVOW_COM("天沃科技",3,"thvowTemplate","http://www.thvow.com/newslists.aspx?tid=12"),


  /**
   * DfacTemplate
   */
  DFAC_COM("东风汽车股份有限公司",4,"dfacTemplate","http://www.dfac.com/cn/News/list_32.aspx"),


  /**
   * 搜狗微信
   */
  SOU_GOU_WEI_XIN("搜狗微信",5,"souGouWeiXinTemplate","#"),

  /**
   * 微博
   */
  WEI_BO("微博",6,"weiBoTemplate","#");



  /**
   * 模板名称
   */
  private String name;

  /**
   * 模板code
   */
  private Integer code;
  /**
   * 对应的类
   */
  private String clazzName;

  /**
   * 对应网站的URL
   */
  private String domain;


  STemplateEnum(String name, Integer code,String clazzName,String domain) {
    this.name = name;
    this.clazzName = clazzName;
    this.code = code;
    this.domain = domain;
  }


}
