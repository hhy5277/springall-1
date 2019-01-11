/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.springall.util.wxpay.sdkplus.service.request.base;

import java.util.Map;

import com.shinnlove.springall.util.wxpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.springall.util.wxpay.sdkplus.consts.WXPayConstants;
import com.shinnlove.springall.util.wxpay.sdkplus.enums.WXPaySignType;
import com.shinnlove.springall.util.wxpay.sdkplus.http.WXPayRequestUtil;
import com.shinnlove.springall.util.wxpay.sdkplus.service.client.AbstractWXPayClient;
import com.shinnlove.springall.util.wxpay.sdkplus.service.handler.WXPayExecuteHandler;
import com.shinnlove.springall.util.wxpay.sdkplus.service.handler.WXPayParamsHandler;
import com.shinnlove.springall.util.wxpay.sdkplus.util.WXPayUtil;

/**
 * 微信支付主动请求抽象类。
 *
 * 各类请求参数处理接口：{@link WXPayParamsHandler}
 * 各类请求公共执行接口：{@link WXPayExecuteHandler}
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayRequestClient.java, v 0.1 2018-12-18 下午4:13 shinnlove.jinsheng Exp $$
 */
public abstract class WXPayRequestClient extends AbstractWXPayClient implements WXPayParamsHandler,
                                                                    WXPayExecuteHandler {

    /** 微信支付请求对象 */
    protected WXPayRequestUtil wxPayRequestExecutor;

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public WXPayRequestClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
    }

    /**
     * @see WXPayParamsHandler#fillRequestParams(java.util.Map, java.util.Map)
     */
    @Override
    public void fillRequestParams(Map<String, String> keyPairs, final Map<String, String> payParams)
                                                                                                    throws Exception {
        // Step1：策略模式上下文填写请求主体信息与签名
        wxPayModeContext.fillRequestMainBodyParams(wxPayMchConfig, payParams);

        // Step2：交给具体的子类完成其他请求必填参数
        fillRequestDetailParams(keyPairs, payParams);

        // Step3：准备签名，特别注意：签名一定是等到最后信息全部填完了再签!!!
        payParams.put(WXPayConstants.NONCE_STR, WXPayUtil.generateUUID());
        // 验签方式
        if (WXPaySignType.MD5.equals(wxPayMchConfig.getSignType())) {
            // MD5
            payParams.put(WXPayConstants.SIGN_TYPE, WXPayConstants.MD5);
        } else if (WXPaySignType.HMACSHA256.equals(wxPayMchConfig.getSignType())) {
            // HMACSHA256
            payParams.put(WXPayConstants.SIGN_TYPE, WXPayConstants.HMACSHA256);
        }
        String sign = WXPayUtil.generateSignature(payParams, wxPayMchConfig.getApiKey(),
            wxPayMchConfig.getSignType());
        payParams.put(WXPayConstants.SIGN, sign);
    }

    /**
     * 抽象填入请求需要的具体字段信息。
     * 
     * @param keyPairs  
     * @param payParams
     */
    public abstract void fillRequestDetailParams(Map<String, String> keyPairs,
                                                 final Map<String, String> payParams);

    /**
     * Setter method for property wxPayRequestExecutor.
     *
     * @param wxPayRequestExecutor value to be assigned to property wxPayRequestExecutor
     */
    public void setWxPayRequestExecutor(WXPayRequestUtil wxPayRequestExecutor) {
        this.wxPayRequestExecutor = wxPayRequestExecutor;
    }

}