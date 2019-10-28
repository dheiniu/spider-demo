package com.mysteel.spider.demo.bean;

import lombok.Data;

/**
 * @version V1.0
 * @description:${TODO}
 * @projectName spider-demo
 * @className: Plate
 * @see：com.mysteel.spider.demo.bean
 * @author: 资讯研发部-黄志杰
 * @date: 2019/9/11 10:19
 */
@Data
public class Plate<T> {

  private T item;

}
