package rpc.server;

import java.io.IOException;

import com.heeking.RpcServer;
import com.heeking.bean.BeanRegister;

import rpc.IProductService;
import rpc.ProductServiceImpl;

public class RpcServerTest {

	public static void main(String[] args) throws IOException {
		String ip = "127.0.0.1";
		int port = 5897;
		
        //注册的远程服务器实现
		BeanRegister beanregister = new BeanRegister();
		beanregister.register(IProductService.class.getName(), new ProductServiceImpl());
		
		RpcServer rpcServer = new RpcServer();
		rpcServer.start(ip, port, beanregister);

	}
}
