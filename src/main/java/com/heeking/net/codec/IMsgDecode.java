package com.heeking.net.codec;

import java.nio.ByteBuffer;
import java.util.AbstractQueue;

public interface IMsgDecode {
	public void decode(ByteBuffer nioData,AbstractQueue<Object> reviceMsg);
}
