package com.mysteel.spider.consumer;

import com.alibaba.fastjson.JSON;
import com.mysteel.spider.entity.BaseConfig;
import com.mysteel.spider.entity.ResultMap;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:任务
 * @className: TaskHandle
 * @see： com.mysteel.spider.consumer
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:03
 */
public class TaskHandle implements Callable<Integer> {

  private static Logger logger = LoggerFactory.getLogger(TaskHandle.class);

  //管道
  private Channel channel;
  //消息Id
  private long deliveryTag;
  //计数器
  private AtomicInteger scoreAtomic;
  //消耗积分
  private int consumeScore;
  //处理器
  private Parser parser;
  //配置
  private BaseConfig baseConfig;


  public TaskHandle(BaseConfig baseConfig, long deliveryTag, AtomicInteger scoreAtomic, Channel channel, int consumeScore, Parser parser){
    this.baseConfig = baseConfig;
    this.deliveryTag = deliveryTag;
    this.scoreAtomic = scoreAtomic;
    this.channel  = channel;
    this.consumeScore = consumeScore;
    this.parser = parser;
  }

  @Override
  public Integer call() {
    Long start = System.currentTimeMillis();
    try {
      if(scoreAtomic.addAndGet(-consumeScore) >= 0){
        //消息确认
        channel.basicAck(deliveryTag,false);
        //业务
        //结果集
        List<ResultMap> resultMaps = parser.execute(baseConfig);

        if(!CollectionUtils.isEmpty(resultMaps)){
          resultMaps.forEach( result ->  System.out.println("["+Thread.currentThread().getName()+" 耗时:"+(System.currentTimeMillis() - start)+"ms]:"+JSON.toJSONString(result)));
        }
        //logger.info("[任务成功消费]");
        //录入数据库
      }else{
        //logger.info("资源不足[{}]任务退回",scoreAtomic.get());
        channel.basicNack(deliveryTag,false,true);
      }
      return scoreAtomic.addAndGet(consumeScore);
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
  }
}
