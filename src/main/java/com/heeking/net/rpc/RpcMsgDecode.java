package com.heeking.net.rpc;

import java.nio.ByteBuffer;
import java.util.AbstractQueue;

import com.heeking.message.RpcRequestMsg;
import com.heeking.net.codec.IMsgDecode;
import com.heeking.net.config.SystemConfig;
import com.heeking.util.SerializationUtil;
/**
 * rpc 消息解码器实现
 * @author zss
 *
 */
public class RpcMsgDecode implements IMsgDecode {
	@Override
	public void decode(ByteBuffer nioData, AbstractQueue<Object> reviceMsg) {
		while (nioData.remaining() > 4) {
			nioData.mark();
			short beginTag = nioData.getShort();
			if (beginTag != SystemConfig.BEGINTAG_MSG) {
				nioData.reset();
				return;
			}
			short length = nioData.getShort();
			if (nioData.remaining() < length) {
				nioData.reset();
				return;
			} else {
				byte[] body = new byte[length];
				nioData.get(body);
				RpcRequestMsg rpcRequest = SerializationUtil.deserialize(body, RpcRequestMsg.class);
				reviceMsg.offer(rpcRequest);
			}
		}
	}
}
