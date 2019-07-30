package com.mysteel.spider.consumer.selenium.template.library;


import com.mysteel.spider.constant.QueueEnum;
import com.mysteel.spider.consumer.selenium.pool.PooledWebDriver;
import com.mysteel.spider.consumer.selenium.template.TemplateStrategy;
import com.mysteel.spider.entity.Article;
import com.mysteel.spider.entity.ResultMap;
import com.mysteel.spider.entity.TemplateConfig;
import com.mysteel.spider.entity.VerificationCode;
import com.mysteel.spider.provider.RabbitMqMessageSend;
import com.mysteel.spider.util.DamaUtil;
import com.mysteel.spider.util.ResultMapUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:【搜狗微信】网站模板，验证码反扒
 * @className: SouGouWeiXinTemplate
 * @see： com.mysteel.spider.consumer.selenium.template.library
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 14:27
 */
@Component("souGouWeiXinTemplate")
public class SouGouWeiXinTemplate implements TemplateStrategy {

  /**
   * 请求地址
   */
  private static final String DOMAIN = "http://mp.weixin.qq.com/profile?src=3&timestamp=1564381625&ver=1&signature=jsllaDrpw2AfM51Yl5tu-aOkVSeGLatLGjlzmkOHPAlxQNLTmz0MLXXJQbK8Ff-45vHUSLctVm0ljziRuvc90A==";

  /**
   * 列表xpath
   */
  private static final String LIST_URL_XPATH = "//div[@class='weui_media_bd']/h4[@class='weui_media_title']";

  /**
   * 作者xpath
   */
  private static final String AUTHOR_XPATH = "//span[@class='rich_media_meta rich_media_meta_text']";

  /**
   * 标题xpath
   */
  private static final String TITLE_XPATH = "//div[@class='rich_media_area_primary_inner']/div[@id='img-content']/h2[@id='activity-name']";

  /**
   * 时间xpath
   */
  private static final String  TIME_XPATH = "//div[@id='img-content']/div[@id='meta_content']/em[@id='publish_time']";

  @Autowired
  private RabbitMqMessageSend rabbitMqMessageSend;

  @Autowired
  private DamaUtil damaUtil;

  @Override
  public List<ResultMap> get(TemplateConfig templateConfig, PooledWebDriver webDriver) {
    Long time = System.currentTimeMillis();
    if(templateConfig.getContainSubTask()){
      String url = DOMAIN;
      String prefix = url.substring(0,url.lastIndexOf("/"));
      webDriver.get(url);
      try {
        //截取验证码
        WebElement yzm = webDriver.findElement(By.id("verify_img"));
        File scrFile = webDriver.getTakesScreenshot().getScreenshotAs(OutputType.FILE);
        BufferedImage img = ImageIO.read(scrFile);
        BufferedImage dest = img.getSubimage(yzm.getLocation().getX(),yzm.getLocation().getY(),  yzm.getSize().getWidth(), yzm.getSize().getHeight());
        ImageIO.write(dest, "jpg", scrFile);
        //解析
        VerificationCode verificationCode = damaUtil.getVal(scrFile.getPath());
        if(verificationCode.getIsSuccess()){
          //输入验证码
          webDriver.findElement(By.id("input")).sendKeys(verificationCode.getData());
          webDriver.findElement(By.xpath("//div[@class='weui_btn_area btn_box']/a[@id='bt']")).click();
          System.out.println("验证码解析成功【"+verificationCode.getData()+"】");
          Thread.sleep(2000);
        }else{
          System.err.println("验证码解析失败");
        }
      }catch (Exception e){
        System.err.println("无需解码");
      }

      List<WebElement> elements = webDriver.findElements(By.xpath(LIST_URL_XPATH));
      elements.forEach(el ->{
        String targetUrl = prefix+el.getAttribute("hrefs");
        //子任务
        TemplateConfig t  = new TemplateConfig();
        BeanUtils.copyProperties(templateConfig,t);
        Integer priority = t.getPriority()*10;
        t.setContainSubTask(false);
        t.setDomain(targetUrl);
        t.setPriority(priority);
        rabbitMqMessageSend.sendMessage(t,QueueEnum.SELENIUM_QUEUE.getSendRoutingKey(),priority);
      });
      return null;
    }else{
      List<ResultMap> result = new ArrayList<>();
      webDriver.get(templateConfig.getDomain());
      Article article = Article.builder()
        .title(byXpath(TITLE_XPATH,webDriver))
        .time(byXpath(TIME_XPATH,webDriver))
        .url(webDriver.getCurrentUrl())
        .author(byXpath(AUTHOR_XPATH,webDriver))
        .build();
      result.add(ResultMapUtil.entityToMap(article));
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
