package rpc.client;

import java.util.concurrent.CountDownLatch;

import rpc.IProductService;
import rpc.Product;

public class RpcClientTest {
	private RpcProxy rpcProxy;
	private IProductService productService;

	public void init(String host, int port) {
		rpcProxy = new RpcProxy(host, port);
		rpcProxy.connect();
		productService = rpcProxy.getInstance(IProductService.class);
	}

	public void testSave() throws Exception {
		productService.save(new Product(2L, "002", "内衣", 10f));
	}

	public void testDelete() throws Exception {
		productService.deleteById(2L);
	}

	public void testUpdate() throws Exception {
		productService.update(new Product(2L, "002", "内衣", 1f));
	}

	public void closeProxy() {
		rpcProxy.close();
	}

	public static void main(String[] args) throws InterruptedException {
		RpcClientTest app = new RpcClientTest();
		app.init("127.0.0.1", 5897);
		for (int i = 0; i < 100; i++) {
			Product product = app.productService.get(1L);
			System.out.println(product.getName());
		}
	}
}
