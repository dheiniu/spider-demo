package com.mysteel.spider.consumer.selenium.pool;

import com.mysteel.spider.constant.Config;
import com.mysteel.spider.constant.RedisKey;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
 * @description:谷歌
 * @className: ChromeDriverBean
 * @see： com.mysteel.spider.consumer.selenium.pool
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:08
 */
@Lazy
@Component
public class ChromeDriverBean implements BrowserDriver{

  @Autowired
  private Config config;

  private BlockingQueue<PooledWebDriver> pooledWebDriverQueue;


  @PostConstruct
  public void init() {
    //阻塞队列
    System.setProperty("webdriver.chrome.driver", config.getChromedriverPath());

    int size = this.config.getScoreCount() / this.config.getSeleniumScore();
    this.pooledWebDriverQueue = new ArrayBlockingQueue(size);

    ChromeOptions chromeOptions = new ChromeOptions();

    if(Boolean.TRUE.equals(config.getBrowserProxyOpen())){
      //代理
      DesiredCapabilities cap = new DesiredCapabilities();
      Proxy proxy = new Proxy();
      String proxyIpAndPort= config.getBrowserProxyIp()+":"+config.getBrowserProxyHost();
      proxy.setHttpProxy(proxyIpAndPort).setFtpProxy(proxyIpAndPort).setSslProxy(proxyIpAndPort);
      cap.setCapability(CapabilityType.ForSeleniumServer.AVOIDING_PROXY, true);
      cap.setCapability(CapabilityType.ForSeleniumServer.ONLY_PROXYING_SELENIUM_TRAFFIC, true);
      cap.setCapability(CapabilityType.PROXY, proxy);
      chromeOptions.merge(cap);
    }

    chromeOptions.setHeadless(!Boolean.TRUE.equals(config.getBrowerVisualization()));

    /**
     * 创建窗口个数
     */
    for(int i=0;i<size;i++){
      String uuid = UUID.randomUUID().toString();
      //游览器设置唯一标识
      if(Boolean.TRUE.equals(config.getBrowserProxyOpen())){
        chromeOptions.addArguments("--user-agent="+RedisKey.BROWER_KEY+uuid);
      }
      //初始化
      PooledWebDriver pooledWebDriver = new PooledWebDriver(new ChromeDriver(chromeOptions),pooledWebDriverQueue);
      //设置游览器唯一标识
      pooledWebDriver.setId(uuid);
      pooledWebDriver.manage().window().setSize(new Dimension(800,480));
      pooledWebDriverQueue.add(pooledWebDriver);
    }
    //pooledWebDriverQueue.addAll(Stream.generate(() -> new PooledWebDriver(new ChromeDriver(chromeOptions), pooledWebDriverQueue)).limit(size).collect(Collectors.toList()));
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
