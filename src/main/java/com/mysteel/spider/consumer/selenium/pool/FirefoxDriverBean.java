package com.mysteel.spider.consumer.selenium.pool;

import com.mysteel.spider.constant.Config;
import com.mysteel.spider.constant.RedisKey;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @description:火狐
 * @className: FirefoxDriverBean
 * @see： com.mysteel.spider.consumer.selenium.pool
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:09
 */
@Lazy
@Component
public class FirefoxDriverBean implements BrowserDriver{

  @Autowired
  private Config config;

  private BlockingQueue<PooledWebDriver> pooledWebDriverQueue;

  @PostConstruct
  public void  init (){
    System.setProperty("webdriver.gecko.driver", config.getGeckodriverPath());
    int size = this.config.getScoreCount() / this.config.getSeleniumScore();
    //阻塞队列
    this.pooledWebDriverQueue = new ArrayBlockingQueue(size);
    FirefoxOptions firefoxOptions = new FirefoxOptions();
    if(config.getBrowserProxyOpen()){
      //代理
      DesiredCapabilities cap = new DesiredCapabilities();
      Proxy proxy = new Proxy();
      String proxyIpAndPort= config.getBrowserProxyIp()+":"+config.getBrowserProxyHost();
      proxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort).setSslProxy(proxyIpAndPort);
      cap.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
      cap.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
      cap.setCapability(CapabilityType.PROXY, proxy);
      firefoxOptions.merge(cap);
    }
    //游览器可视化设置
    firefoxOptions.setHeadless(!Boolean.TRUE.equals(config.getBrowerVisualization()));

    //
    for(int i=0;i<size;i++){
      String uuid = UUID.randomUUID().toString();
      //游览器设置唯一标识
      if(Boolean.TRUE.equals(config.getBrowserProxyOpen())){
        firefoxOptions.addArguments("--user-agent="+RedisKey.BROWER_KEY+uuid);
      }
      //初始化
      PooledWebDriver pooledWebDriver = new PooledWebDriver(new FirefoxDriver(firefoxOptions),pooledWebDriverQueue);
      //设置游览器唯一标识
      pooledWebDriver.setId(uuid);
      pooledWebDriver.manage().window().setSize(new Dimension(800,480));
      pooledWebDriverQueue.add(pooledWebDriver);
    }
    //创建连接
    //pooledWebDriverQueue.addAll(Stream.generate(() -> new PooledWebDriver(new FirefoxDriver(firefoxOptions), pooledWebDriverQueue)).limit(size).collect(Collectors.toList()));
  }


  @Override
  public PooledWebDriver get() {
    try {
      return pooledWebDriverQueue.take();
    }catch (InterruptedException e){
      e.printStackTrace();
      return null;
    }
  }
}
