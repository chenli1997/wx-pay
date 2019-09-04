package com.ugmro.wxpay.constant;

/**
 * @author chen
 * @date 2019/5/29 13:14
 */
public interface WXPayConfig extends com.github.wxpay.sdk.WXPayConfig{
    String getAttach();

    String getNotifyUrl();

    String getSpbillCreateIp();
}
