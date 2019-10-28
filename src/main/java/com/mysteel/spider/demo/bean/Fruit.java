package com.mysteel.spider.demo.bean;

import lombok.Data;

/**
 * @version V1.0
 * @description:${TODO}
 * @projectName spider-demo
 * @className: Fruit
 * @see：com.mysteel.spider.demo.bean.active
 * @author: 资讯研发部-黄志杰
 * @date: 2019/9/11 10:18
 */
@Data
public class Fruit {

  private String type;




  public static void main(String[] args) {

    Plate<? extends Fruit> p1 = new Plate<Apple>();



    System.out.println(p1.getItem());



    Plate<? super Fruit> p2 = new Plate<Fruit>();


//    p2.setItem(Apple.builder().color("#293821").build());



  }
}
