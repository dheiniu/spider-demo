package com.mysteel.spider.entity;

import java.util.HashMap;

/**
 * @description:结果集
 * @className: ResultMap
 * @see： com.mysteel.spider.entity
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:00
 */
public class ResultMap extends HashMap<String,String> {

  public ResultMap(){}

  public ResultMap(String key,String val){
    this.put(key,val);
  }
}
