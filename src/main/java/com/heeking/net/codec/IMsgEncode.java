package com.heeking.net.codec;

import java.nio.ByteBuffer;
import java.util.AbstractQueue;

public interface IMsgEncode {

	public void encode(Object msg,AbstractQueue<ByteBuffer> writeMsgQueue);
}
