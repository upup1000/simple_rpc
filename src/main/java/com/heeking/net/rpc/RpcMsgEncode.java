package com.heeking.net.rpc;

import java.nio.ByteBuffer;
import java.util.AbstractQueue;

import com.heeking.message.RpcResponse;
import com.heeking.net.codec.IMsgEncode;
import com.heeking.util.SerializationUtil;
/**
 * 消息编码实现
 * @author zss
 *
 */
public class RpcMsgEncode implements IMsgEncode{

	@Override
	public void encode(Object msg, AbstractQueue<ByteBuffer> writeMsgQueue) {
		RpcResponse rpcResponse =(RpcResponse)msg;
		byte[] bytes = SerializationUtil.serialize(rpcResponse);
		ByteBuffer buffer=ByteBuffer.allocate(bytes.length+4);
		buffer.putShort((short)127);
		buffer.putShort((short)bytes.length);
		buffer.put(bytes);
		writeMsgQueue.add(buffer);
	}

}
