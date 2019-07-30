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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description:【天沃科技】网站模板
 * @className: ThvowTemplate
 * @see： com.example.demo.rabbitmq.service.selenium.template.library
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:12
 */
@Component("thvowTemplate")
public class ThvowTemplate implements TemplateStrategy {

  /**
   * 请求地址
   */
  private static final String DOMAIN = "http://www.thvow.com/newslists.aspx?tid=12";

  /**
   * 列表xpath
   */
  private static final String LIST_URL_XPATH = "/html/body/div[4]/div[2]/div[2]/ul/li/dd/h2/a";

  /**
   * 标题xpath
   */
  private static final String TITLE_XPATH = "//div[@class='newcenter']/div[@class='newright']/div[@class='newContent']/h2";


  /**
   * 时间xpath
   */
  private static final String  TIME_XPATH = "//div[@class='newright']/div[@class='newContent']/p[2]";


  @Autowired
  private RabbitMqMessageSend rabbitMqMessageSend;


  @Override
  public List<ResultMap> get(TemplateConfig templateConfig, PooledWebDriver webDriver) {
    //两种方式
    //way1 -> 一个游览器执行任务
    //way2 -> 任务分割给多个游览器执行
   return way1(templateConfig,webDriver);
   //return way2(templateConfig,webDriver);
  }


  /**
   * 方式2，一步到位
   * @param templateConfig
   * @param webDriver
   * @return
   */
  private List<ResultMap> way2(TemplateConfig templateConfig, PooledWebDriver webDriver){
    List<ResultMap> resultMaps = new ArrayList<>();
    try {
      webDriver.get(DOMAIN);
      List<String> urlList = new ArrayList<>();
      int idx = 0;
      while (true){
        List<WebElement> els = webDriver.findElements(By.xpath(LIST_URL_XPATH));
        if(els.size() <= idx){
          webDriver.navigate().back();
          break;
        }else{
          els.get(idx).click();
          //等待
          webDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
          urlList.add(webDriver.getCurrentUrl());
          webDriver.navigate().back();
          idx++;
        }
      }
      Iterator<String> iterator = urlList.iterator();
      while (iterator.hasNext()){
        Thread.sleep(1000);
        //执行JS多开句柄
        webDriver.getJsExecutor().executeScript("window.open('"+iterator.next()+"')");
      }
      //获取所有句柄
      List<String> it2 = new ArrayList<String>(webDriver.getWindowHandles());
      if(!CollectionUtils.isEmpty(it2) && it2.size() > 1){
        Collections.reverse(it2);

        for(int i=0; i<it2.size()-1; i++){
          webDriver.switchTo().window(it2.get(i));
          Article article = Article.builder()
            .title(byXpath(TITLE_XPATH,webDriver))
            .time(byXpath(TIME_XPATH,webDriver))
            .url(webDriver.getCurrentUrl())
            .build();
          resultMaps.add(ResultMapUtil.entityToMap(article));
        }
      }
    }catch (Exception e){
      e.printStackTrace();
    }
    return resultMaps;
  }


  /**
   * 方式一，分子任务
   * @param templateConfig
   * @param webDriver
   * @return
   */
  private List<ResultMap> way1(TemplateConfig templateConfig, PooledWebDriver webDriver){
    if(templateConfig.getContainSubTask() && DOMAIN.equals(templateConfig.getDomain())){
      getUrls(templateConfig,webDriver);
      return null;
    }else{
      List<ResultMap> result = new ArrayList<>();
      result.add(ResultMapUtil.entityToMap(getVal(templateConfig,webDriver)));
      return result;
    }
  }

  /**
   * 解析内容
   * @param templateConfig
   * @param webDriver
   * @return
   */
  private Article getVal(TemplateConfig templateConfig, PooledWebDriver webDriver){
    webDriver.get(templateConfig.getDomain());
    return Article.builder()
      .title(byXpath(TIME_XPATH,webDriver))
      .time(byXpath(TIME_XPATH,webDriver))
      .url(webDriver.getCurrentUrl())
      .build();
  }

  /**
   * 获取列表地址，构建子任务
   * @param templateConfig
   * @param webDriver
   */
  private void getUrls(TemplateConfig templateConfig, PooledWebDriver webDriver){
    try {
      webDriver.get(DOMAIN);
      List<String> urlList = new ArrayList<>();
      int idx = 0;
      while (true){
        List<WebElement> els =  webDriver.findElements(By.xpath(LIST_URL_XPATH));
        if(els.size() <= idx){
          webDriver.navigate().back();
          break;
        }else{
          els.get(idx).click();
          //等待
          webDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
          urlList.add(webDriver.getCurrentUrl());
          System.out.println(webDriver.getCurrentUrl());
          webDriver.navigate().back();
          idx++;
        }
      }
      Iterator<String> iterator = urlList.iterator();
      while (iterator.hasNext()){
        TemplateConfig t = new TemplateConfig();
        BeanUtils.copyProperties(templateConfig,t);
        Integer priority = t.getPriority()*10;
        t.setDomain(iterator.next());
        t.setContainSubTask(false);
        t.setPriority(priority);
        rabbitMqMessageSend.sendMessage(t,QueueEnum.SELENIUM_QUEUE.getSendRoutingKey(),priority);
      }
    }catch (Exception e){
      e.printStackTrace();
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
