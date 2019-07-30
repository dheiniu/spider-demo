package com.mysteel.spider.consumer.selenium.template.library;

import com.mysteel.spider.constant.QueueEnum;
import com.mysteel.spider.consumer.selenium.pool.PooledWebDriver;
import com.mysteel.spider.consumer.selenium.template.TemplateStrategy;
import com.mysteel.spider.entity.Article;
import com.mysteel.spider.entity.ResultMap;
import com.mysteel.spider.entity.TemplateConfig;
import com.mysteel.spider.provider.RabbitMqMessageSend;
import com.mysteel.spider.util.ResultMapUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * @description:【掘金】网站模板
 * @className: JuejinTemplate
 * @see： com.mysteel.spider.consumer.selenium.template.library
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:11
 */
@Component("juejinTemplate")
public class JuejinTemplate implements TemplateStrategy {

  /**
   * 请求地址
   */
  private static final String DOMAIN = "https://juejin.im/";

  /**
   * 列表xpath
   */
  private static final String LIST_URL_XPATH = "//div[@class='info-row title-row']/a[@class='title']";

  /**
   * 作者xpath
   */
  private static final String AUTHOR_XPATH = "//div[@class='author-info-block']/div[@class='author-info-box']/a[@class='username ellipsis']";

  /**
   * 标题xpath
   */
  private static final String TITLE_XPATH = "//article[@class='article']/h1[@class='article-title']";

  /**
   * 时间xpath
   */
  private static final String  TIME_XPATH = "//div[@class='author-info-box']/div[@class='meta-box']/time[@class='time']";


  @Autowired
  private RabbitMqMessageSend rabbitMqMessageSend;



  @Override
  public List<ResultMap> get(TemplateConfig templateConfig, PooledWebDriver webDriver) {
    //判断是否包含子任务
    if(templateConfig.getContainSubTask() && DOMAIN.equals(templateConfig.getDomain())){
      //包含子任务
      webDriver.get(templateConfig.getDomain());
      new WebDriverWait(webDriver, 2).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(LIST_URL_XPATH)));
      List<WebElement> webElements = webDriver.findElements(By.xpath(LIST_URL_XPATH));
      for (WebElement webElement : webElements){
        //子任务
        TemplateConfig t  = new TemplateConfig();
        BeanUtils.copyProperties(templateConfig,t);
        Integer priority = t.getPriority()*10;
        t.setContainSubTask(false);
        t.setDomain(webElement.getAttribute("href"));
        t.setPriority(priority);
        rabbitMqMessageSend.sendMessage(t,QueueEnum.SELENIUM_QUEUE.getSendRoutingKey(),priority);
      }
      return null;
    }else{
      webDriver.get(templateConfig.getDomain());
      List<ResultMap> result = new ArrayList<>();
      result.add(ResultMapUtil.entityToMap( Article.builder()
        .title(byXpath(TITLE_XPATH,webDriver))
        .time(byXpath(TIME_XPATH,webDriver))
        .url(webDriver.getCurrentUrl())
        .author(byXpath(AUTHOR_XPATH,webDriver))
        .build()));
      return result;
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
