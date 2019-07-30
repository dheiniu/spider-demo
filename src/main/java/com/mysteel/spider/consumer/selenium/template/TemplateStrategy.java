package com.mysteel.spider.consumer.selenium.template;


import com.mysteel.spider.consumer.selenium.pool.PooledWebDriver;
import com.mysteel.spider.entity.ResultMap;
import com.mysteel.spider.entity.TemplateConfig;

import java.util.List;


/**
 * @description:TODO
 * @className: TemplateStrategy
 * @see： com.mysteel.spider.consumer.selenium.template
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:10
 */
public interface TemplateStrategy {

  List<ResultMap> get(TemplateConfig templateConfig, PooledWebDriver webDriver);

//  List<Article> getList(TemplateConfig spiderConfig,PooledWebDriver webDriver);
}
