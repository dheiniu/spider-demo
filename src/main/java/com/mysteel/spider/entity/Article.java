package com.mysteel.spider.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @description:文章module
 * @className: Article
 * @see： com.mysteel.spider.entity
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:00
 */
@Data
@Builder
public class Article {

  private String url;

  //标题
  private String title;

  //作者
  private String author;

  //来源
  private String source;

  //时间
  private String time;
}
