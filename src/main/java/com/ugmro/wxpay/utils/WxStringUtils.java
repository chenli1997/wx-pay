package com.ugmro.wxpay.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author chen
 * @date 2019/5/28 10:51
 */
public class WxStringUtils {

    /**
     * 从 微信返回的 request中 读取 数据并转换成Map
     * @param request
     * @return
     */
    public static Map<String, String> getStrFromRequset(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        String line = "";
        StringBuffer inputString = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            inputString.append(line);
        }
        request.getReader().close();
        return WXPayUtil.xmlToMap(inputString.toString());
    }

    /**
     * 微信付款 JSAPI 前端需要参数
     * @param outTradeNo 随机字符串，当前使用订单号 
     * @param appid  商户ID
     * @param prepayId JSAPI 下单成功返回参数
     * @param mchkey 商户号
     * @return
     * @throws Exception
     */
    public static HashMap<String, String> jsApi_Sign(String outTradeNo, String appid, String prepayId, String mchkey) throws Exception {
        String timeStamp = Long.toString(System.currentTimeMillis() / 1000);
        HashMap<String, String> jssdk = new HashMap<String, String>();
        jssdk.put("appId", appid);		// 商户ID
        jssdk.put("timeStamp", currentTimeMillistamp);		// 时间戳
        jssdk.put("nonceStr", outTradeNo);		// 随机字符串，当前使用订单号 
        jssdk.put("package", "prepay_id="+prepayId);		// 下单成功返回参数
        jssdk.put("signType", "MD5");		// 签名加密方式
        String sign = WXPayUtil.generateSignature(jssdk, mchkey);		// 商户号和参数进行加密--生成签名-用于验证参数是否被篡改
        jssdk.put("paySign", sign);		// 设置签名
        return jssdk;		// 返回前端需要参数
    }
}
