package com.mysteel.spider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @description:图片识别验证码
 * @className: VerificationCode
 * @see： com.mysteel.spider.entity
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 11:00
 */
@Data
@Builder
@AllArgsConstructor
public class VerificationCode {

  /**
   * 是否成功
   */
  private Boolean isSuccess;

  /**
   * 值
   */
  private String data;

  /**
   * 错误信息
   */
  private String error;

}
