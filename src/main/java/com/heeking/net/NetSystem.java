package com.heeking.net;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.NetworkChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heeking.net.config.SystemConfig;

/**
 * 存放当前所有连接的信息
 * @author zss
 */
public class NetSystem {
	private static final Logger LOGGER = LoggerFactory.getLogger(NetSystem.class);
	public static final int RUNNING = 0;
	public static final int SHUTING_DOWN = -1;
	private static NetSystem INSTANCE;
	private final ConcurrentMap<Long, Connection> allConnections;
	private long netInBytes;
	private long netOutBytes;
	private SystemConfig netConfig;
	private NIOConnector connector;

	public static NetSystem getInstance() {
		return INSTANCE;
	}

	public NetSystem() {
		this.allConnections = new ConcurrentHashMap<Long, Connection>();
		INSTANCE = this;
	}
	public NIOConnector getConnector() {
		return connector;
	}

	public void setConnector(NIOConnector connector) {
		this.connector = connector;
	}

	public int getWriteQueueSize() {
		int total = 0;
		for (Connection con : allConnections.values()) {
			total += con.getWriteQueue().size();
		}

		return total;

	}

	public SystemConfig getNetConfig() {
		return netConfig;
	}

	public void setNetConfig(SystemConfig netConfig) {
		this.netConfig = netConfig;
	}

	public long getNetInBytes() {
		return netInBytes;
	}

	public void addNetInBytes(long bytes) {
		netInBytes += bytes;
	}

	public long getNetOutBytes() {
		return netOutBytes;
	}

	public void addNetOutBytes(long bytes) {
		netOutBytes += bytes;
	}

	/**
	 * 添加一个连接到系统中被监控
	 * @param c
	 */
	public void addConnection(Connection c) {
		allConnections.put(c.getId(), c);
	}

	public ConcurrentMap<Long, Connection> getAllConnectios() {
		return allConnections;
	}


	public void removeConnection(Connection con) {
		this.allConnections.remove(con.getId());

	}

	public void setSocketParams(Connection con) throws IOException {
		int sorcvbuf =netConfig.getSocketSoRcvbuf();
		int sosndbuf = netConfig.getSocketSoSndbuf();
		int soNoDelay =netConfig.getSocketNoDelay();
		NetworkChannel channel = con.getChannel();
		channel.setOption(StandardSocketOptions.SO_RCVBUF, sorcvbuf);
		channel.setOption(StandardSocketOptions.SO_SNDBUF, sosndbuf);
		channel.setOption(StandardSocketOptions.TCP_NODELAY, soNoDelay == 1);
		channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
	}

}
