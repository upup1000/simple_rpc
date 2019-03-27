package rpc.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.UUID;

import com.heeking.message.RpcRequestMsg;
import com.heeking.message.RpcResponse;
import com.heeking.util.SerializationUtil;

public class RpcProxy {
	private String host;
	private int port;
	private int timeOut = 100000;
	private Socket socket;

	public RpcProxy(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public void connect() {
		SocketAddress address = new InetSocketAddress(host, port);
		socket = new Socket();
		try {
			socket.connect(address, 5000);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public <T> T getInstance(Class<T> interfaceClass) {
		@SuppressWarnings("unchecked")
		T instance = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						RpcRequestMsg rpcRequest = new RpcRequestMsg();
						// 获取到被调用的类名 和RPC-Server中的serviceMap中的key进行匹配
						String className = method.getDeclaringClass().getName();
						// 获取到方法的参数列表
						Class<?>[] parameterTypes = method.getParameterTypes();
						// 生成一个请求的id
						rpcRequest.setRequestId(UUID.randomUUID().toString());
						rpcRequest.setClassName(className);// 类名
						rpcRequest.setParameterTypes(parameterTypes);// 参数类型列表
						rpcRequest.setParameters(args);// 参数列表
						rpcRequest.setMethodName(method.getName());// 调用的放方法名称
						byte[] bytes = SerializationUtil.serialize(rpcRequest);
						ByteBuffer buffer = ByteBuffer.allocate(bytes.length + 4);
						buffer.putShort((short) 127);
						buffer.putShort((short) bytes.length);
						buffer.put(bytes);
						socket.getOutputStream().write(buffer.array());
						socket.getOutputStream().flush();
						InputStream input = socket.getInputStream();
						DataInputStream dataInput = new DataInputStream(input);
						short flag = dataInput.readShort();
						short length = dataInput.readShort();
						byte[] data = new byte[length];
						int offset = 0;
						while (length > 0) {
							int read = dataInput.read(data, offset, length);
							offset = read;
							length -= read;
						}
						
						RpcResponse resPonse = SerializationUtil.deserialize(data, RpcResponse.class);
						return resPonse.getResult();
					}
				});
		// 返回一个代理对象
		return instance;
	}
}
