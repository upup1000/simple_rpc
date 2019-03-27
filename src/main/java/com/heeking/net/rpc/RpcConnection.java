package com.heeking.net.rpc;

import java.nio.channels.SocketChannel;

import com.heeking.net.Connection;

public class RpcConnection extends Connection {
	public RpcConnection(SocketChannel channel) {
		super(channel);
	}

	@Override
	public String getCharset() {
		return "UTF-8";
	}
}
