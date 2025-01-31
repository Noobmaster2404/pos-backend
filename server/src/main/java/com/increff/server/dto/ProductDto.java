package com.increff.server.dto;

import com.increff.server.entity.Product;
import com.increff.commons.model.ProductForm;
import com.increff.commons.model.ProductData;
import com.increff.server.api.ClientApi;
import com.increff.server.flow.ProductFlow;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
@Component
public class ProductDto extends AbstractDto {
    
    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ClientApi clientApi;

    public List<ProductData> getProductsByName(String productName) throws ApiException {
        List<Product> products = productFlow.getProductsByName(productName);
        return ConversionHelper.convertToProductData(products);
    }

    //TODO: Read: Conversion class pure function

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        Product product = productFlow.getProductByBarcode(barcode);
        return ConversionHelper.convertToProductData(product);
    }

    public List<ProductData> getProductsByClientId(Integer clientId) throws ApiException {
        List<Product> products = productFlow.getProductsByClientId(clientId);
        return ConversionHelper.convertToProductData(products);
    }

    public List<ProductData> getAllProducts() throws ApiException {
        List<Product> products = productFlow.getAllProducts();
        return ConversionHelper.convertToProductData(products);
    }

    public ProductData addProduct(ProductForm form) throws ApiException {
        try {
            // checkValid(form);
            normalize(form);
            Client client = clientApi.getClientByName(form.getClientName());
            Product product = ConversionHelper.convertToProduct(form, client);
            return ConversionHelper.convertToProductData(productFlow.addProduct(product));
        } catch (Exception e) {
            throw new ApiException(getPrefix() + e.getMessage());
        }
    }
    //TODO: Remove this try catch since its only attaching a prefix

    public ProductData updateProductByBarcode(String barcode, ProductForm form) throws ApiException {
        // checkValid(form);
        normalize(form);
        Client client = clientApi.getClientByName(form.getClientName());
        Product product = ConversionHelper.convertToProduct(form, client);
        Product updatedProduct = productFlow.updateProductByBarcode(barcode, product);
        ProductData data = ConversionHelper.convertToProductData(updatedProduct);
        return data;
    }

    public List<ProductData> bulkAddProducts(List<ProductForm> forms) throws ApiException {
        if (forms.size() > 5000) {
            throw new ApiException(getPrefix() + "Cannot process more than 5000 products at once");
        }
        for (ProductForm form : forms) {
            normalize(form);
        }
        Map<ProductForm, Client> productClientMap = new HashMap<>();
        for (ProductForm form : forms) {
            try {
                Client client = clientApi.getClientByName(form.getClientName());
                productClientMap.put(form, client);
            } catch (ApiException e) {
                throw new ApiException(getPrefix() + e.getMessage());
            }
        }
        //use for loop instead of stream and lambda because lambda cannot throw checked exceptions
        List<Product> addedProducts = productFlow.bulkAddProducts(
            ConversionHelper.convertToProduct(productClientMap));
        return addedProducts.stream()
                .map(product -> {
                    ProductData data = ConversionHelper.convertToProductData(product);
                    return data;
                })
                .collect(Collectors.toList());
    }

    @Override
    protected String getPrefix() {
        return "Product: ";
    }

    private void normalize(ProductForm form) throws ApiException {
        super.normalize(form);
        // Special handling for image path - keeping original case
        if (Objects.nonNull(form.getImagePath())) {
            String imagePath = form.getImagePath().trim();
            String lowerCasePath = imagePath.toLowerCase();
            if (!lowerCasePath.endsWith(".png") && 
                !lowerCasePath.endsWith(".jpg") && 
                !lowerCasePath.endsWith(".jpeg")) {
                throw new ApiException(getPrefix() + "Invalid image format. Only PNG, JPG, and JPEG files are allowed");
            }
            form.setImagePath(imagePath);
        }
    }

}
