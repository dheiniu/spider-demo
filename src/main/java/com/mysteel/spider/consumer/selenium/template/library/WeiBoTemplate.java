package com.mysteel.spider.consumer.selenium.template.library;

import com.mysteel.spider.consumer.selenium.pool.PooledWebDriver;
import com.mysteel.spider.consumer.selenium.template.TemplateStrategy;
import com.mysteel.spider.entity.ResultMap;
import com.mysteel.spider.entity.TemplateConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @description:微博模板
 * @className: WeiBoTemplate
 * @see： com.mysteel.spider.consumer.selenium.template.library
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/31 13:57
 */
@Component("weiBoTemplate")
public class WeiBoTemplate implements TemplateStrategy {

  /**
   * domain
   */
  private static final String DOMAIN = "https://weibo.com/";

  @Override
  public List<ResultMap> get(TemplateConfig templateConfig, PooledWebDriver webDriver) {
    //全局隐式等待
    webDriver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
    //设定网址
    webDriver.get("https://passport.weibo.cn/signin/login?entry=mweibo&res=wel&wm=3349&r=http%3A%2F%2Fm.weibo.cn%2F");
    //显示等待控制对象
    WebDriverWait webDriverWait=new WebDriverWait(webDriver,10);
    //等待输入框可用后输入账号密码
    webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("loginName"))).sendKeys("13576131499");
    webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("loginPassword"))).sendKeys("1995495");
    //点击登录
    webDriver.findElement(By.id("loginAction")).click();
    //等待2秒用于页面加载，保证Cookie响应全部获取。
    try {
      Thread.sleep(2000);
      //获取Cookie并打印
      Set<Cookie> cookies=webDriver.manage().getCookies();
      Iterator iterator=cookies.iterator();
      while (iterator.hasNext()){
        System.out.println(iterator.next().toString());
        System.out.println("----------------------------------");
      }
    }catch (Exception e){
      System.out.println("cookie获取失败");
    }
    return null;
  }
}
