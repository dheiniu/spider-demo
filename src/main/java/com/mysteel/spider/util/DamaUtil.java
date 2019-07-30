package com.mysteel.spider.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.mysteel.spider.entity.VerificationCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @description:验证码打码工具类
 * @className: DamaUtil
 * @see： com.mysteel.spider.util
 * @author: 资讯研发部-黄志杰
 * @date: 2019/7/29 10:57
 */
@Component
public class DamaUtil {

  @Value("${verification.code.username}")
  private String username;

  @Value("${verification.code.password}")
  private String password;

  /**
   * boundary就是request头和上传文件内容的分隔符
   */
  private static final String BOUNDARY = "---------------------------68163001211748";

  private static final String URL = "http://v1-http-api.jsdama.com/api.php?mod=php&act=upload";



  /**
   * 验证码识别
   * @param filePath 本地图片地址
   * @return
   */
  public VerificationCode getVal(String filePath){
    Map<String, String> paramMap = new HashMap(2);
    paramMap.put("user_name", username);
    paramMap.put("user_pw", password);
    try {
      URL url=new URL(URL);
      HttpURLConnection connection=(HttpURLConnection)url.openConnection();
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("content-type", "multipart/form-data; boundary="+BOUNDARY);
      connection.setConnectTimeout(30000);
      connection.setReadTimeout(30000);

      OutputStream out = new DataOutputStream(connection.getOutputStream());
      // 普通参数
      if (paramMap != null) {
        StringBuffer strBuf = new StringBuffer();
        Iterator<Entry<String, String>> iter = paramMap.entrySet().iterator();
        while (iter.hasNext()) {
          Entry<String,String> entry = iter.next();
          String inputName = entry.getKey();
          String inputValue = entry.getValue();
          strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
          strBuf.append("Content-Disposition: form-data; name=\""
            + inputName + "\"\r\n\r\n");
          strBuf.append(inputValue);
        }
        out.write(strBuf.toString().getBytes());
      }

      // 图片文件
      if (filePath != null) {
        File file = new File(filePath);
        String filename = file.getName();
        String contentType = "image/jpeg";//这里看情况设置
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
        strBuf.append("Content-Disposition: form-data; name=\""
          + "upload" + "\"; filename=\"" + filename+ "\"\r\n");
        strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
        out.write(strBuf.toString().getBytes());
        DataInputStream in = new DataInputStream(
          new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
          out.write(bufferOut, 0, bytes);
        }
        in.close();
      }
      byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
      out.write(endData);
      out.flush();
      out.close();

      //读取URLConnection的响应
      InputStream in = connection.getInputStream();
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      while (true) {
        int rc = in.read(buf);
        if (rc <= 0) {
          break;
        } else {
          bout.write(buf, 0, rc);
        }
      }
      in.close();
      //结果输出
      //System.out.println(new String(bout.toByteArray()));
      Boolean isSuccess = JSON.parseObject(new String(bout.toByteArray())).getBoolean("result");
      if(isSuccess){
       return VerificationCode.builder().isSuccess(isSuccess).data(JSON.parseObject(JSON.parseObject(new String(bout.toByteArray())).getString("data")).getString("val")).build();

      }else{
        return VerificationCode.builder().isSuccess(isSuccess).data(JSON.parseObject(new String(bout.toByteArray())).getString("data")).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return VerificationCode.builder().isSuccess(false).error(e.getMessage()).build();
    }
  }

  public static void main(String[] args) {
//    File file = FileUtil.file("http://mp.weixin.qq.com/mp/verifycode?cert=1563854059620.5164");
//    FileUtil.getInputStream(file);
    HttpUtil.downloadFile("http://mp.weixin.qq.com/mp/verifycode?cert=1563854059620.5164",FileUtil.file("e:/"+System.currentTimeMillis()+".jpg"));
  }


}
