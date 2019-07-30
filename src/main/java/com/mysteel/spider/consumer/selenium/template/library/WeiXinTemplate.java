package com.mysteel.spider.consumer.selenium.template.library;

import com.mysteel.spider.constant.STemplateEnum;
import com.mysteel.spider.consumer.selenium.pool.PooledWebDriver;
import com.mysteel.spider.consumer.selenium.template.TemplateStrategy;
import com.mysteel.spider.entity.Article;
import com.mysteel.spider.entity.ResultMap;
import com.mysteel.spider.entity.TemplateConfig;
import com.mysteel.spider.util.ResultMapUtil;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:【微信】模板
 * @className: WeiXinTemplate
 * @see： com.example.demo.rabbitmq.service.selenium.template.library
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:13
 */
@Component("weiXinTemplate")
public class WeiXinTemplate implements TemplateStrategy {

  /**
   * 请求地址
   */
  private static final String DOMAIN = "https://mp.weixin.qq.com/s/cKlTa4XBeX6gvaW1iQXVyw";

  /**
   * 作者xpath
   */
  private static final String AUTHOR_XPATH = "//span[@id='profileBt']/a[@id='js_name']";

  /**
   * 标题xpath
   */
  private static final String TITLE_XPATH = "//div[@id='page-content']/div[@class='rich_media_area_primary_inner']/div[@id='img-content']/h2[@id='activity-name']";

  /**
   * 时间xpath
   */
  private static final String  TIME_XPATH = "//div[@id='meta_content']/em[@id='publish_time']";

  @Override
  public List<ResultMap> get(TemplateConfig templateConfig, PooledWebDriver webDriver) {
    if(STemplateEnum.WEI_XIN.getDomain().equals(DOMAIN)){
      webDriver.get(DOMAIN);
      List<ResultMap> list = new ArrayList<>();
      list.add(ResultMapUtil.entityToMap(Article.builder()
        .title(byXpath(TITLE_XPATH,webDriver))
        .time(byXpath(TIME_XPATH,webDriver))
        .url(webDriver.getCurrentUrl())
        .author(byXpath(AUTHOR_XPATH,webDriver))
        .build()));
      return list;
    }else{
      return null;
    }
  }

  private String byXpath(String xpath,PooledWebDriver webDriver){
    try {
      return webDriver.findElement(By.xpath(xpath)).getText();
    }catch (Exception e){
      return null;
    }
  }
}
