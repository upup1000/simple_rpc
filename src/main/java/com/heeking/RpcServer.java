package com.heeking;

import java.io.IOException;

import com.heeking.bean.BeanRegister;
import com.heeking.net.NIOAcceptor;
import com.heeking.net.NIOReactorPool;
import com.heeking.net.NetSystem;
import com.heeking.net.config.SystemConfig;
import com.heeking.net.rpc.RpcConnectionFactory;
import com.heeking.net.rpc.RpcIoHandler;
import com.heeking.net.rpc.RpcMsgDecode;
import com.heeking.net.rpc.RpcMsgEncode;
/**
 * rpcServer 启动入口
 * @author zss
 */
public class RpcServer {
	
	public void start(String bindIp,int listenPort,BeanRegister beanregister) throws IOException{
		NetSystem netSystem=new NetSystem();
		netSystem.setNetConfig(new SystemConfig());
		RpcConnectionFactory connectionFactory=new RpcConnectionFactory();
		connectionFactory.setDecode(new RpcMsgDecode());
		connectionFactory.setEncode(new RpcMsgEncode());
		connectionFactory.setHandler(new RpcIoHandler(beanregister));
		NIOReactorPool reactorPool=new NIOReactorPool("rpc-reactor", 8);
		NIOAcceptor accptor=new NIOAcceptor("rpc",bindIp,listenPort,connectionFactory,reactorPool);
		accptor.start();
	}
	
}
