/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese
 * opensource volunteers. you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Any questions about this component can be directed to it's project Web address
 * https://code.google.com/p/opencloudb/.
 *
 */
package com.heeking.net.config;

/**
 * 系统基础配置项
 */
public final class SystemConfig {
	public static final int DEFAULT_PROCESSORS = Runtime.getRuntime().availableProcessors();
	public static final short BEGINTAG_MSG = 127;
	private int socketSoRcvbuf = 1024 * 1024;
	private int socketSoSndbuf = 4 * 1024 * 1024;
	private int socketNoDelay = 1; // 0=false

	private long processorBufferPool;
	private int processorBufferChunk;

	private int packetHeaderSize = 4;
	private int maxPacketSize = 16 * 1024 * 1024;

	public SystemConfig() {

	}

	public int getPacketHeaderSize() {
		return packetHeaderSize;
	}

	public void setPacketHeaderSize(int packetHeaderSize) {
		this.packetHeaderSize = packetHeaderSize;
	}

	public int getMaxPacketSize() {
		return maxPacketSize;
	}

	public void setMaxPacketSize(int maxPacketSize) {
		this.maxPacketSize = maxPacketSize;
	}

	public long getProcessorBufferPool() {
		return processorBufferPool;
	}

	public void setProcessorBufferPool(long processorBufferPool) {
		this.processorBufferPool = processorBufferPool;
	}

	public int getProcessorBufferChunk() {
		return processorBufferChunk;
	}

	public void setProcessorBufferChunk(int processorBufferChunk) {
		this.processorBufferChunk = processorBufferChunk;
	}

	public int getSocketSoRcvbuf() {
		return socketSoRcvbuf;
	}

	public void setSocketSoRcvbuf(int socketSoRcvbuf) {
		this.socketSoRcvbuf = socketSoRcvbuf;
	}

	public int getSocketSoSndbuf() {
		return socketSoSndbuf;
	}

	public void setSocketSoSndbuf(int socketSoSndbuf) {
		this.socketSoSndbuf = socketSoSndbuf;
	}

	public int getSocketNoDelay() {
		return socketNoDelay;
	}

	public void setSocketNoDelay(int socketNoDelay) {
		this.socketNoDelay = socketNoDelay;
	}


}