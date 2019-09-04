package com.ugmro.wxpay.Exception;

/**
 * 自定义微信支付一次
 * @author chen
 * @date 2019/5/27 18:24
 */
public class WxPayException extends Exception {

    public WxPayException(){
        super();
    }

    public WxPayException(String msg){
        super(msg);
    }
}
