package com.mysteel.spider.consumer.selenium.pool;

import lombok.Data;
import org.openqa.selenium.*;
import org.springframework.util.CollectionUtils;

import java.io.Closeable;
import java.util.*;


/**
 * @description:封装WebDriver
 * @className: PooledWebDriver
 * @see： com.mysteel.spider.consumer.selenium.pool
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:09
 */
@Data
public class PooledWebDriver implements WebDriver,Closeable{

  private String id;

  private Queue<PooledWebDriver> pooledWebDriverQueue;

  public PooledWebDriver(WebDriver driver,Queue<PooledWebDriver> pooledWebDriverQueue) {
    this.driver = driver;
    this.pooledWebDriverQueue = pooledWebDriverQueue;
  }

  @Override
  public void get(String s) {
    driver.get(s);
  }

  @Override
  public String getCurrentUrl() {
    return driver.getCurrentUrl();
  }

  @Override
  public String getTitle() {
    return driver.getTitle();
  }

  @Override
  public List<WebElement> findElements(By by) {
    return driver.findElements(by);
  }

  @Override
  public WebElement findElement(By by) {
    return driver.findElement(by);
  }

  @Override
  public String getPageSource() {
    return driver.getPageSource();
  }

  @Override
  public void close() {
//    driver.close();
    init();
  }

  /**
   * 游览器进行初始化
   */
  private void init(){
    manage().deleteAllCookies();
    //关闭,tab，只留下一个
    List<String> handles = new ArrayList<String>(getWindowHandles());
    if(!CollectionUtils.isEmpty(handles) && handles.size() > 1){
      for(int i=0; i<handles.size(); i++){
        WebDriver window = driver.switchTo().window(handles.get(i));
        if(i < handles.size()-1){
          window.close();
        }
      }
    }
    pooledWebDriverQueue.offer(this);
  }

  public JavascriptExecutor getJsExecutor(){
    return (JavascriptExecutor)driver;
  }

  public TakesScreenshot getTakesScreenshot(){
    return (TakesScreenshot)driver;
  }

  /**
   * 不允许关闭游览器
   */
  @Override
  public void quit() {
    //driver.quit();
  }

  @Override
  public Set<String> getWindowHandles() {
    return driver.getWindowHandles();
  }

  @Override
  public String getWindowHandle() {
    return driver.getWindowHandle();
  }

  @Override
  public TargetLocator switchTo() {
    return driver.switchTo();
  }

  @Override
  public Navigation navigate() {
    return driver.navigate();
  }

  @Override
  public Options manage() {
    return driver.manage();
  }

  private WebDriver driver;

}
