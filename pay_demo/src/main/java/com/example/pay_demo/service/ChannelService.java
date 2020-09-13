package com.example.pay_demo.service;

import java.io.IOException;

import com.example.pay_demo.vo.channel.ChannelQueryResult;
import com.example.pay_demo.vo.channel.ChannelResp;
import org.springframework.stereotype.Service;

/**
 * Created by jiashen on 2020/9/7.
 */
@Service
public class ChannelService {

    public ChannelResp pay(String orderId) throws IOException {
        return new ChannelResp("0000", "success");
    }

    public ChannelResp query(String orderId) throws IOException {
        return new ChannelResp("0000", "success");
    }

    public ChannelResp cancel(String orderId) throws IOException {
        return new ChannelResp("0000", "success");
    }

    /**
     * 针对特定渠道的查询结果，判断订单是否支付成功，以及是否需要冲正
     * @param orderId
     * @return
     */
    public ChannelQueryResult queryAndGetCancelCondition(String orderId) {
        ChannelQueryResult cancelResult;

        try {
            ChannelResp channelResp = this.query(orderId);
            if ("0000".equals(channelResp.getCode())) {
                cancelResult = new ChannelQueryResult(true, false, channelResp.getCode(), channelResp.getMsg());
            // 某些错误，例如用户账户异常，余额不足，不需要发起冲正
            } else if ("1022".equals(channelResp.getCode())) {
                cancelResult = new ChannelQueryResult(false, false, channelResp.getCode(), channelResp.getMsg());
            } else {
                cancelResult = new ChannelQueryResult(false, true, channelResp.getCode(), channelResp.getMsg());
            }
        } catch (IOException ioe) {
            cancelResult = new ChannelQueryResult(false, true, "9997", ioe.getMessage());
        }

        return cancelResult;
    }

}
