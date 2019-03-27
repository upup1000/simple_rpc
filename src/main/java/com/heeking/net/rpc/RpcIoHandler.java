package com.heeking.net.rpc;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heeking.bean.BeanRegister;
import com.heeking.message.RpcRequestMsg;
import com.heeking.message.RpcResponse;
import com.heeking.net.Connection;
import com.heeking.net.NIOHandler;

import rpc.Product;

/**
 * rpc ioHander
 * 
 * @author zss
 */
public class RpcIoHandler implements NIOHandler<RpcConnection> {
	public static Logger LOGGER = LoggerFactory.getLogger(Connection.class);
	private BeanRegister beanRegister;

	public RpcIoHandler(BeanRegister beanRegister) {
		this.beanRegister = beanRegister;
	}

	@Override
	public void onConnected(RpcConnection con) throws IOException {
		LOGGER.debug("onConnected host:"+con.getHost()+" port"+con.getPort());
	}

	@Override
	public void onConnectFailed(RpcConnection con, Throwable e) {
		LOGGER.debug("onConnectFailed host:"+con.getHost()+" port"+con.getPort());
	}

	@Override
	public void onClosed(RpcConnection con, String reason) {
		LOGGER.debug("onClosed host:"+con.getHost()+" port"+con.getPort());
	}

	@Override
	public void handle(RpcConnection con, Object msg) {
		RpcRequestMsg rpcRequest = (RpcRequestMsg) msg;
		String className = rpcRequest.getClassName();
		// 获取到方法名
		String methodName = rpcRequest.getMethodName();
		// 获取到参数类型列表
		Class<?>[] parameterTypes1 = rpcRequest.getParameterTypes();
		// 获取到参数列表
		Object[] parameters = rpcRequest.getParameters();
		// 获取到具字节码对象
		Class<?> clz;
		try {
			clz = Class.forName(className);
			Object serviceBean = beanRegister.getBean(className);
			Method method1 = clz.getMethod(methodName, parameterTypes1);
			Object result = method1.invoke(serviceBean, parameters);
			RpcResponse rpcResponse = new RpcResponse();
			rpcResponse.setResponseId(UUID.randomUUID().toString());
			rpcResponse.setRequestId(rpcRequest.getRequestId());
			rpcResponse.setResult(result);
			con.write(rpcResponse);
		} catch (SecurityException 
				| IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}
}
