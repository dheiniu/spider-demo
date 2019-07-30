package com.mysteel.spider.consumer;

import com.alibaba.fastjson.JSON;
import com.mysteel.spider.constant.Config;
import com.mysteel.spider.constant.QueueEnum;
import com.mysteel.spider.constant.STaskType;
import com.mysteel.spider.constant.TaskWayEnum;
import com.mysteel.spider.consumer.html.HParser;
import com.mysteel.spider.consumer.selenium.SParser;
import com.mysteel.spider.entity.BaseConfig;
import com.mysteel.spider.entity.TemplateConfig;
import com.mysteel.spider.task.TaskConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:队列监听
 * @className: MessageListener
 * @see： com.mysteel.spider.consumer
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:02
 */
@Component
public class MessageListener {

  private static Logger logger = LoggerFactory.getLogger(MessageListener.class);
  /**
   * 配置文件
   */
  @Autowired
  private Config config;

  @Autowired
  private SParser sParser;

  @Autowired
  private HParser hParser;

  //线程池
  private ExecutorService executor;

  //积分
  private AtomicInteger scoreAtomic;


  @PostConstruct
  public void init(){
    this.scoreAtomic = new AtomicInteger(config.getScoreCount());
    this.executor = Executors.newFixedThreadPool(config.getExecutorSize());
  }


  /**
   * containerFactory - 设置消息手动提交
   * @param message
   * @param channel
   * @throws Exception
   */
  @RabbitListener(queues = "test_html_queue",containerFactory = "simpleRabbitListenerContainerFactory")
  public void getHtml(Message message, Channel channel) {
    try {
      int seleniumMsgCount = channel.queueDeclarePassive(QueueEnum.SELENIUM_QUEUE.getName()).getMessageCount();
      //判断有问题
      if(TaskWayEnum.SELENIUM.getType().equals(config.getPrecedence()) && seleniumMsgCount != 0){
        // p2 = 是否批量退回，p3 = 是否回到队列 ture， false=消息作废
        channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        //批量退回
        //channel.basicNack(message.getMessageProperties().getDeliveryTag(),true,true);
      }else{
        //记录当前执行的任务
        //执行任务
        TemplateConfig templateConfig = JSON.parseObject(message.getBody(),TemplateConfig.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        Future<Integer> val = executor.submit(new TaskHandle(templateConfig,deliveryTag,scoreAtomic,channel,config.getHtmlScore(),hParser));
      }
    }catch (Exception e){
      e.printStackTrace();
    }

  }


  @RabbitListener(queues = "test_selenium_queue",containerFactory = "simpleRabbitListenerContainerFactory")
  public void getSelenium(Message message, Channel channel){
    try {
      //html队列消息数
      int htmlMsgCount = channel.queueDeclarePassive(QueueEnum.HTML_QUEUE.getName()).getMessageCount();
      if(TaskWayEnum.HTML.getType().equals(config.getPrecedence())  && htmlMsgCount != 0){
        //回归队列
        channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,true);
        //批量退回
        //channel.basicNack(message.getMessageProperties().getDeliveryTag(),true,true);
      }else{

        String sType = String.valueOf(JSON.parseObject(new String(message.getBody(),"UTF-8")).get("sType"));
        BaseConfig baseConfig = null;
        if(STaskType.TEMPLATE.getType().equals(sType)){
          baseConfig = JSON.parseObject(message.getBody(),TemplateConfig.class);
        }else{
          baseConfig = JSON.parseObject(message.getBody(),TaskConfig.class);
        }
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        Future<Integer> val = executor.submit(new TaskHandle(baseConfig,deliveryTag,scoreAtomic,channel,config.getSeleniumScore(),sParser));
      }
    }catch (Exception e){
      e.printStackTrace();
    }

  }
}
