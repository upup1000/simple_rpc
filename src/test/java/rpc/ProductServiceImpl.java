package rpc;

public class ProductServiceImpl implements IProductService {

	@Override
	public void save(Product product) {
		System.out.println("save" + product);
	}

	@Override
	public void deleteById(Long productId) {
		System.out.println("deleteById" + productId);
	}

	@Override
	public void update(Product product) {
		System.out.println("update" + product);
	}

	@Override
	public Product get(Long productId) {
		return new Product(1000L, "test", "test", 1000f);
	}

}
