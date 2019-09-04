package com.ugmro.wxpay.config;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.ugmro.wxpay.Exception.WxPayException;
import com.ugmro.wxpay.constant.TradeType;
import com.ugmro.wxpay.constant.WXPayConfig;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chen
 * @date
 */
public class WXPayClient {
    /**
     * 创建微信工具类对象
     */
    private static WXPay wxPay;
    /**
     * 创建传入参数Map
     */
    private Map<String, String> paramMap = new HashMap<>();
    private WXPayConfig wxPayConfig;

    public WXPayClient(WXPayConfig wxPayConfig) {
        try {
            this.wxPayConfig = wxPayConfig;
            wxPay = new WXPay(wxPayConfig);
            paramMap = wxPay.fillRequestData(paramMap);
            // 页面显示的名称
            paramMap.put("body", wxPayConfig.getAttach());
            // 回调地址
            paramMap.put("notify_url", wxPayConfig.getNotifyUrl());
            // 服务端IP地址
            paramMap.put("spbill_create_ip", wxPayConfig.getSpbillCreateIp());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成 微信支付 QrCode（扫码付）
     *
     * @param totalAmount 需支付金额（元）
     * @param outTradeNo  流水号
     * @return
     * @throws WxPayException
     */
    public String tradePrecreate_QRCode(BigDecimal totalAmount, String outTradeNo) throws WxPayException {
        try {
            Map<String, String> paramMap = this.paramMap;
            // 下单
            paramMap = unifiedOrder(paramMap, totalAmount, outTradeNo, TradeType.NATIVE);
            String qrCode = paramMap.get("code_url");
            if (!StringUtils.isEmpty(qrCode)) {
                return qrCode;
            }
            throw new WxPayException("二维码生成失败.(" + paramMap.get("return_msg") + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * JSAPI 下单
     *
     * @param totalAmount 需支付金额（元）
     * @param outTradeNo  流水号
     * @return
     * @throws WxPayException
     */
    public String tradePrecreate_JSAPI(BigDecimal totalAmount, String outTradeNo, String openId) throws WxPayException {

        try {
            Map<String, String> paramMap = this.paramMap;
            // openId
            paramMap.put("openid", openId);
            // 下单
            paramMap = unifiedOrder(paramMap, totalAmount, outTradeNo, TradeType.JSAPI);
            String prepayId = paramMap.get("prepay_id");
            if (StringUtils.isEmpty(prepayId)) {
                throw new WxPayException("JSAPI 下单失败.(" + paramMap.get("return_msg") + ")");
            }
            System.out.println(paramMap);
            return prepayId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * H5 支付
     *
     * @param totalAmount 需支付金额（元）
     * @param outTradeNo  流水号
     * @param redirectUrl 支付成功返回地址
     * @return
     * @throws WxPayException
     */
    public String tradePrecreate_H5(BigDecimal totalAmount, String outTradeNo, String redirectUrl) throws WxPayException {
        try {
            Map<String, String> paramMap = this.paramMap;
            paramMap = unifiedOrder(paramMap, totalAmount, outTradeNo, TradeType.H5);
            String url = paramMap.get("code_url");
            if (!StringUtils.isEmpty(url)) {
                return StringUtils.isEmpty(redirectUrl) ? url : url + "&redirect_url=" + redirectUrl;
            }
            throw new WxPayException("H5 下单失败.(" + paramMap.get("return_msg") + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 统一下单
     *
     * @param paramMap
     * @param totalAmount
     * @param outTradeNo
     * @return
     */
    private Map<String, String> unifiedOrder(Map<String, String> paramMap, BigDecimal totalAmount, String outTradeNo, String tradeType) throws Exception {
        // 订单流水号
        paramMap.put("out_trade_no", outTradeNo);
        // 交易类型
        paramMap.put("trade_type", tradeType);
        //微信支付是以分为单位
        paramMap.put("total_fee", totalAmount.multiply(new BigDecimal(100)).intValue() + "");
        // 下单
        return wxPay.unifiedOrder(paramMap);
    }

    /**
     * 微信支付回调函数
     *
     * @param param
     * @param outTradeNo 验证流水号
     * @param totalFee   需支付金额
     * @return
     * @throws Exception
     */
    public Map<String, String> callBack(Map<String, String> param, String outTradeNo, BigDecimal totalFee) throws Exception {
        // 验证数据是否正确
        checkPayInfo(param, outTradeNo, totalFee);
        // 付款成功-逻辑
        if (param.get("return_code").equals(WXPayConstants.SUCCESS)) {
            // 生成返回参数 通知微信确认成功
            Map<String, String> resultParam = new HashMap<>(5);
            // 支付凭证（使用完，remove掉）
            resultParam.put("transactionVoucher", param.toString());
            resultParam.put("return_code", "SUCCESS");
            resultParam.put("return_msg", "OK");
            return resultParam;
        }
        return null;
    }

    /**
     * 验证支付信息是否正确
     *
     * @param params
     * @param outTradeNo
     * @param totalFee
     * @return
     * @throws WxPayException
     */
    private Boolean checkPayInfo(Map<String, String> params, String outTradeNo, BigDecimal totalFee) throws WxPayException {
        // 1.验证商户号是否正确
        String mchId = params.get("mch_id");
        if (mchId == null || !mchId.equals(wxPayConfig.getMchID())) {
            throw new WxPayException("验证商户号不正确.");
        }

        // 3、验证app_id是否为该商户本身。
        if (!params.get("appid").equals(wxPayConfig.getAppID())) {
            throw new WxPayException("appid不一致.");
        }

        // 3、验证流水号是否正确out_trade_no
        String out_trade_no = params.get("out_trade_no");
        if (StringUtils.isEmpty(outTradeNo) || !out_trade_no.equals(outTradeNo)) {
            throw new WxPayException("流水号不一致.");
        }

        // 4、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额）
        long total_fee = new BigDecimal(params.get("total_fee")).longValue();
        long totalPrice = totalFee.multiply(new BigDecimal(100)).longValue();
        if (total_fee != totalPrice) {
            throw new WxPayException("订单金额不一致.");
        }
        return true;
    }
}
