package com.heeking.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.AbstractQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heeking.net.codec.IMsgDecode;
import com.heeking.net.codec.IMsgEncode;

/**
 * 网络连接抽象
 * 
 * @author zss
 */
public abstract class Connection implements ClosableConnection {
	public static Logger LOGGER = LoggerFactory.getLogger(Connection.class);
	protected String host;
	protected int port;
	protected int localPort;
	protected long id;

	public enum State {
		connecting, connected, closing, closed, failed
	}

	private State state = State.connecting;
	protected final SocketChannel channel;

	private SelectionKey processKey;
	private static final int OP_NOT_READ = ~SelectionKey.OP_READ;
	private static final int OP_NOT_WRITE = ~SelectionKey.OP_WRITE;
	private ByteBuffer readBuffer;
	private final AbstractQueue<Object> reviceMsg = new ConcurrentLinkedQueue<Object>();
	private final AbstractQueue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<ByteBuffer>();
	protected boolean isClosed;
	protected boolean isSocketClosed;
	protected long startupTime;
	protected long lastReadTime;
	protected long lastWriteTime;
	protected int netInBytes;
	protected int netOutBytes;
	protected int pkgTotalSize;
	protected int pkgTotalCount;
	private long idleTimeout;
	@SuppressWarnings("rawtypes")
	protected NIOHandler handler;
	protected IMsgDecode decode;
	protected IMsgEncode encode;

	public Connection(SocketChannel channel) {
		this.channel = channel;
		this.isClosed = false;
		this.startupTime = System.currentTimeMillis();
		this.lastReadTime = startupTime;
		this.lastWriteTime = startupTime;
	}

	public void resetPerfCollectTime() {
		netInBytes = 0;
		netOutBytes = 0;
		pkgTotalCount = 0;
		pkgTotalSize = 0;
	}

	public long getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getId() {
		return id;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isIdleTimeout() {
		return System.currentTimeMillis() > Math.max(lastWriteTime, lastReadTime) + idleTimeout;

	}

	public void setDecode(IMsgDecode decode) {
		this.decode = decode;
	}

	public void setEncode(IMsgEncode encode) {
		this.encode = encode;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public long getStartupTime() {
		return startupTime;
	}

	public long getLastReadTime() {
		return lastReadTime;
	}

	public long getLastWriteTime() {
		return lastWriteTime;
	}

	public long getNetInBytes() {
		return netInBytes;
	}

	public long getNetOutBytes() {
		return netOutBytes;
	}

	private ByteBuffer allocate() {
		return ByteBuffer.allocate(1024);
	}

	public void setHandler(NIOHandler<? extends Connection> handler) {
		this.handler = handler;
	}

	@SuppressWarnings("rawtypes")
	public NIOHandler getHandler() {
		return this.handler;
	}

	@SuppressWarnings("unchecked")
	public void handle() {
		for (Object obj : reviceMsg) {
			this.handler.handle(this, obj);
		}
		if (!this.writeQueue.isEmpty()) {
			enableWrite(true);
		}
	}

	public void write(Object msg) {
		this.encode.encode(msg, writeQueue);
	}

	public boolean isConnected() {
		return (this.state == Connection.State.connected);
	}

	@SuppressWarnings("unchecked")
	public void close(String reason) {
		if (!isClosed) {
			closeSocket();
			this.cleanup();
			readBuffer.clear();
			isClosed = true;
			NetSystem.getInstance().removeConnection(this);
			LOGGER.info("close connection,reason:" + reason + " ," + this.getClass());
			if (handler != null) {
				handler.onClosed(this, reason);
			}
		}
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void idleCheck() {
		if (isIdleTimeout()) {
			LOGGER.info(toString() + " idle timeout");
			close(" idle ");
		}
	}

	/**
	 * 清理资源
	 */

	protected void cleanup() {
		this.writeQueue.clear();
	}

	public AbstractQueue<ByteBuffer> getWriteQueue() {
		return writeQueue;
	}

	@SuppressWarnings("unchecked")
	public void register(Selector selector) throws IOException {
		processKey = channel.register(selector, SelectionKey.OP_READ, this);
		NetSystem.getInstance().addConnection(this);
		readBuffer = allocate();
		this.handler.onConnected(this);

	}

	public void doWriteQueue() {
		try {
			boolean noMoreData = write0();
			lastWriteTime = System.currentTimeMillis();
			if (noMoreData) {
				if ((processKey.isValid() && (processKey.interestOps() & SelectionKey.OP_WRITE) != 0)) {
					disableWrite();
				}

			} else {

				if ((processKey.isValid() && (processKey.interestOps() & SelectionKey.OP_WRITE) == 0)) {
					enableWrite(false);
				}
			}

		} catch (IOException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("caught err:", e);
			}
			close("err:" + e);
		}

	}

	private boolean write0() throws IOException {
		for (;;) {
			int written = 0;
			ByteBuffer buffer = writeQueue.poll();
			if (buffer != null) {
				buffer.flip();
				while (buffer.hasRemaining()) {
					written = channel.write(buffer);
					if (written > 0) {
						netOutBytes += written;
						NetSystem.getInstance().addNetOutBytes(written);
					} else {
						break;
					}
				}
			} else {
				return true;
			}
		}
	}

	private void disableWrite() {
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() & OP_NOT_WRITE);
		} catch (Exception e) {
			LOGGER.warn("can't disable write " + e + " con " + this);
		}

	}

	public void enableWrite(boolean wakeup) {
		boolean needWakeup = false;
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			needWakeup = true;
		} catch (Exception e) {
			LOGGER.warn("can't enable write " + e);

		}
		if (needWakeup && wakeup) {
			processKey.selector().wakeup();
		}
	}

	public void disableRead() {

		SelectionKey key = this.processKey;
		key.interestOps(key.interestOps() & OP_NOT_READ);
	}

	public void enableRead() {

		boolean needWakeup = false;
		try {
			SelectionKey key = this.processKey;
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
			needWakeup = true;
		} catch (Exception e) {
			LOGGER.warn("enable read fail " + e);
		}
		if (needWakeup) {
			processKey.selector().wakeup();
		}
	}

	public void setState(State newState) {
		this.state = newState;
	}

	public void reviceBuffer(ByteBuffer in) {
		if (this.readBuffer != null) {
			in.flip();
			if (readBuffer.remaining() > in.remaining()) {
				readBuffer.put(in);
				readBuffer.flip();
			} else {
				readBuffer.flip();
				ByteBuffer newBuf = ByteBuffer.allocate(readBuffer.remaining() + in.remaining());
				newBuf.order(readBuffer.order());
				newBuf.put(readBuffer);
				newBuf.put(in);
				newBuf.flip();
				readBuffer = newBuf;
			}
		} else {
			in.flip();
			readBuffer = in;
		}
		decode.decode(readBuffer, reviceMsg);
		int oldPostion = readBuffer.position();
		if (oldPostion != readBuffer.position()) {
			if (readBuffer.hasRemaining()) {
				readBuffer.compact();
			} else {
				readBuffer = null;
			}
		}
		handle();
	}

	/**
	 * 异步读取数据,only nio thread call
	 * 
	 * @throws IOException
	 */
	protected void asynRead() throws IOException {
		if (this.isClosed) {
			return;
		}
		boolean readAgain = true;
		int got = 0;
		while (readAgain) {
			ByteBuffer byteBuff = allocate();
			got = channel.read(byteBuff);
			switch (got) {
			case 0: {
				// 如果空间不够了，继续分配空间读取
				if (byteBuff.remaining() != 0) {
					readAgain = false;
				}
				break;
			}
			case -1: {
				readAgain = false;
				break;
			}
			default: {// readed some bytes
				if (byteBuff.hasRemaining()) {
					readAgain = false;
				}
			}
			}
			this.reviceBuffer(byteBuff);
		}
	}

	private void closeSocket() {
		if (channel != null) {
			boolean isSocketClosed = true;
			try {
				processKey.cancel();
				channel.close();
			} catch (Throwable e) {
			}
			boolean closed = isSocketClosed && (!channel.isOpen());
			if (!closed) {
				LOGGER.warn("close socket of connnection failed " + this);
			}
		}
	}

	public State getState() {
		return state;
	}

	public int getPkgTotalSize() {
		return pkgTotalSize;
	}

	public int getPkgTotalCount() {
		return pkgTotalCount;
	}

	@Override
	public String toString() {
		return "Connection [host=" + host + ",  port=" + port + ", id=" + id + ", state=" + state + ", startupTime="
				+ startupTime + ", lastReadTime=" + lastReadTime + ", lastWriteTime=" + lastWriteTime + "]";
	}
}
