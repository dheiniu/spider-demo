package com.mysteel.spider.consumer.selenium.pool;



/**
 * @description:游览器driver接口
 * @className: BrowserDriver
 * @see： com.mysteel.spider.consumer.selenium.pool
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:08
 */
public interface BrowserDriver {

  /**
   * 获取对象
   * @return
   */
  PooledWebDriver get();

}
