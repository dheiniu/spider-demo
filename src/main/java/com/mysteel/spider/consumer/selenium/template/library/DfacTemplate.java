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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:【东风汽车股份】网站模板
 * @className: DfacTemplate
 * @see： com.example.demo.rabbitmq.service.selenium.template.library
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:12
 */
@Component("dfacTemplate")
public class DfacTemplate implements TemplateStrategy {

  /**
   * 请求地址
   */
  private static final String DOMAIN = "http://www.dfac.com/cn/News/list_32.aspx";

  /**
   * 列表xpath
   */
  private static final String LIST_URL_XPATH = "//li/h2[@class='tit']/a";

  /**
   * 标题xpath
   */
  private static final String TITLE_XPATH = "//h2[@class='tit']";

  /**
   * 时间xpath
   */
  private static final String  TIME_XPATH = "//div[@class='date']";


  @Autowired
  private RabbitMqMessageSend rabbitMqMessageSend;

  @Override
  public List<ResultMap> get(TemplateConfig templateConfig, PooledWebDriver webDriver) {
    try {
      if(DOMAIN.equals(templateConfig.getDomain()) && templateConfig.getContainSubTask()){
        webDriver.get(DOMAIN);
        //滚动到底部翻页，  这里只操作了翻一页，具体翻多少页，看业务
        webDriver.getJsExecutor().executeScript("window.scrollTo(0, document.body.scrollHeight)");
        //等待2秒
        Thread.sleep(2000);
        //获取列表链接
        List<WebElement> els =webDriver.findElements(By.xpath(LIST_URL_XPATH));
        for (WebElement el : els){
          String url = el.getAttribute("href");
          if(!StringUtils.isEmpty(url)){
            //生成子任务,推送队列
            TemplateConfig t  = new TemplateConfig();
            BeanUtils.copyProperties(templateConfig,t);
            //优先级设置
            Integer priority = t.getPriority()*10;
            //该子任务是否还存在子任务
            t.setContainSubTask(false);
            //目标网址
            t.setDomain(url);
            t.setPriority(priority);
            rabbitMqMessageSend.sendMessage(t,QueueEnum.SELENIUM_QUEUE.getSendRoutingKey(), t.getPriority());
          }
        }
        return null;
      }else{
        //解析内容
        webDriver.get(templateConfig.getDomain());
        Article article = Article.builder()
          .title(byXpath(TITLE_XPATH,webDriver))
          .time(byXpath(TIME_XPATH,webDriver))
          .url(webDriver.getCurrentUrl())
          .build();
        List<ResultMap> result = new ArrayList<>();
        result.add(ResultMapUtil.entityToMap(article));
        return result;
      }
    }catch (Exception e){
      e.printStackTrace();
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
