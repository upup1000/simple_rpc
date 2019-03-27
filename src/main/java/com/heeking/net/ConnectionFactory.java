package com.heeking.net;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;

import com.heeking.net.codec.IMsgDecode;
import com.heeking.net.codec.IMsgEncode;

/**
 * 连接创建工厂
 * @author zss
 */
public abstract class ConnectionFactory {
	/**
	 * 创建一个具体的连接
	 * 
	 * @param channel
	 * @return Connection
	 * @throws IOException
	 */
	protected abstract Connection makeConnection(SocketChannel channel) throws IOException;

	/**
	 * NIOHandler是无状态的，多个连接共享一个，因此建议作为 Factory的私有变量
	 * @return NIOHandler
	 */
	@SuppressWarnings("rawtypes")
	protected abstract NIOHandler getNIOHandler();

	protected abstract IMsgDecode getMsgDecode();

	protected abstract IMsgEncode getMsgEncode();

	@SuppressWarnings("unchecked")
	public Connection make(SocketChannel channel) throws IOException {
		channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		// 子类完成具体连接创建工作
		Connection c = makeConnection(channel);
		// 设置连接的参数
		NetSystem.getInstance().setSocketParams(c);
		// 设置NIOHandler
		c.setHandler(getNIOHandler());
		c.setDecode(getMsgDecode());
		c.setEncode(getMsgEncode());
		return c;
	}
}