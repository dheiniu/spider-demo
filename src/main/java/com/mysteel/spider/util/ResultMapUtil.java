package com.mysteel.spider.util;

import com.mysteel.spider.entity.ResultMap;

import java.lang.reflect.Field;

/**
 * @version V1.0
 * @description: 实体与map之间的转换
 * @projectName spring-boot-demo
 * @className:
 * @see：com.example.demo.rabbitmq.util
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/17 14:29
 */
public class ResultMapUtil {
  /**
   * 实体类转Map
   * @param object
   * @return
   */
  public static ResultMap entityToMap(Object object) {
    ResultMap map = new ResultMap();
    for (Field field : object.getClass().getDeclaredFields()){
      try {
        boolean flag = field.isAccessible();
        field.setAccessible(true);
        Object o = field.get(object);
        map.put(field.getName(), o+"");
        field.setAccessible(flag);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return map;
  }

  /**
   * Map转实体类
   * @param map 需要初始化的数据，key字段必须与实体类的成员名字一样，否则赋值为空
   * @param entity  需要转化成的实体类
   * @return
   */
  public static <T> T mapToEntity(ResultMap map, Class<T> entity) {
    T t = null;
    try {
      t = entity.newInstance();
      for(Field field : entity.getDeclaredFields()) {
        if (map.containsKey(field.getName())) {
          boolean flag = field.isAccessible();
          field.setAccessible(true);
          Object object = map.get(field.getName());
          if (object!= null && field.getType().isAssignableFrom(object.getClass())) {
            field.set(t, object);
          }
          field.setAccessible(flag);
        }
      }
      return t;
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return t;
  }
}
