package com.increff.server.api;

import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.server.dao.ProductDao;
import com.increff.server.entity.Product;
import com.increff.commons.exception.ApiException;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductApi {

    @Autowired
    private ProductDao dao;

    public Product addProduct(Product product) throws ApiException {
        Product existingProduct = dao.selectByBarcode(product.getBarcode());
        if (Objects.nonNull(existingProduct)) {
            throw new ApiException("Product with barcode '" + product.getBarcode() + "' already exists");
        }
        dao.insert(product);
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts(Integer page) {
        return dao.selectAll(page);
    }

    public Product updateProductById(Integer productId, Product product) throws ApiException {
        Product existingProduct = dao.select(productId);
        if (Objects.isNull(existingProduct)) {
            throw new ApiException("Product with given id does not exist");
        }
        
        if (!existingProduct.getBarcode().equals(product.getBarcode())) {
            Product existingProductWithNewBarcode = dao.selectByBarcode(product.getBarcode());
            if (Objects.nonNull(existingProductWithNewBarcode)) {
                throw new ApiException("Product with barcode '" + product.getBarcode() + "' already exists");
            }
        }
        
        existingProduct.setBarcode(product.getBarcode());
        existingProduct.setProductName(product.getProductName());
        existingProduct.setClient(product.getClient());
        existingProduct.setImagePath(product.getImagePath());
        existingProduct.setMrp(product.getMrp());
        dao.update(existingProduct);
        return existingProduct;
    }

    @Transactional(readOnly = true)
    public List<Product> getCheckProductsByNamePrefix(String productName, Integer page) throws ApiException {
        if (Objects.isNull(productName) || productName.isEmpty()) {
            return getAllProducts(page);
        }
        List<Product> products = dao.selectByNamePrefix(productName, page);
        if (products.isEmpty()) {
            throw new ApiException("No products found with name prefix: " + productName);
        }
        return products;
    }

    @Transactional(readOnly = true)
    public Product getCheckProductByBarcode(String barcode) throws ApiException {
        Product product = dao.selectByBarcode(barcode);
        if(Objects.isNull(product)){
            throw new ApiException("Product with barcode '" + barcode + "' does not exist");
        }
        return product;
    }

    @Transactional(readOnly = true)
    public Product getCheckProductById(Integer productId) throws ApiException {
        Product product = dao.select(productId);
        if(Objects.isNull(product)){
            throw new ApiException("Product with id '" + productId + "' does not exist");
        }
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> getCheckProductsByClientId(Integer clientId, Integer page) throws ApiException {
        List<Product> products = dao.selectByClientId(clientId, page);
        if (products.isEmpty()) {
            throw new ApiException("No products found for client ID: " + clientId);
        }
        return products;
    }

    @Transactional(readOnly = true)
    public Map<Integer, String> getBarcodesByProductIds(List<Integer> productIds) throws ApiException {
        List<Product> products = dao.selectByProductIds(productIds);
        if(products.size() != productIds.size()){
            throw new ApiException("Some products with given ids do not exist");
        }
        return products.stream()
            .collect(Collectors.toMap(
                Product::getProductId,
                Product::getBarcode
            ));
    }

    @Transactional(readOnly = true)
    public long getTotalCount() {
        return dao.count();
    }

    @Transactional(readOnly = true)
    public long getCountByNamePrefix(String prefix) {
        return dao.countByNamePrefix(prefix);
    }

    @Transactional(readOnly = true)
    public long getCountByClientId(Integer clientId) {
        return dao.countByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public List<Product> getCheckProductsByBarcodes(List<String> barcodes) throws ApiException {
        List<Product> products = dao.selectByBarcodes(barcodes);
        if (products.size() != barcodes.size()) {
            throw new ApiException("Some products with given barcodes do not exist");
        }
        return products;
    }
}
