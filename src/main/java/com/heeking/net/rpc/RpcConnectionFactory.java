package com.heeking.net.rpc;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.heeking.net.Connection;
import com.heeking.net.ConnectionFactory;
import com.heeking.net.NIOHandler;
import com.heeking.net.codec.IMsgDecode;
import com.heeking.net.codec.IMsgEncode;

public class RpcConnectionFactory extends ConnectionFactory {
	private NIOHandler<RpcConnection> handler;
	private RpcMsgDecode decode;
	private RpcMsgEncode encode;
	@Override
	protected Connection makeConnection(SocketChannel channel) throws IOException {
		return new RpcConnection(channel);
	}

	public void setHandler(NIOHandler<RpcConnection> handler) {
		this.handler = handler;
	}

	public void setDecode(RpcMsgDecode decode) {
		this.decode = decode;
	}

	public void setEncode(RpcMsgEncode encode) {
		this.encode = encode;
	}

	@Override
	protected NIOHandler<RpcConnection> getNIOHandler() {
		return handler;
	}
	@Override
	protected IMsgDecode getMsgDecode() {
		return decode;
	}

	@Override
	protected IMsgEncode getMsgEncode() {
		return encode;
	}

}
