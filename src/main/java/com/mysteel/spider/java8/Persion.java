package com.mysteel.spider.java8;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @version V1.0
 * @description:${TODO}
 * @projectName spider-demo
 * @className: Persion
 * @see：com.mysteel.spider.java8
 * @author: 资讯研发部-黄志杰
 * @date: 2019/8/13 14:29
 */
public class Persion {

  public static void main(String[] args) {
    new Persion().test("abc", () -> System.out.println(0b1111011));

    Consumer consumer = (t)-> System.out.println(t);
    consumer.accept("12333");
    Integer[] arr = {1,3,32,123,14,6,10};

    System.out.println(Arrays.toString(Arrays.stream(arr).filter((t)-> t != 3).collect(Collectors.toList()).toArray()));

  }

  private void test(String name,PersonCallBack personCallBack){
    System.out.println(name);
    personCallBack.callback();
  }


}
