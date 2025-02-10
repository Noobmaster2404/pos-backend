package com.increff.server.dto;

import com.increff.server.entity.Product;
import com.increff.commons.model.ProductForm;
import com.increff.commons.model.ProductData;
import com.increff.server.api.ClientApi;
import com.increff.server.flow.ProductFlow;
import com.increff.server.helper.ConversionHelper;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Client;
import com.increff.commons.model.PaginatedData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class ProductDto extends AbstractDto {
    
    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ClientApi clientApi;

    @Value("${PAGE_SIZE}")
    private Integer PAGE_SIZE;

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        Product product = productFlow.getProductByBarcode(barcode);
        Client client = clientApi.getCheckClientById(product.getClient().getClientId());

        return ConversionHelper.convertToProductData(product,client.getClientName());
    }

    public PaginatedData<ProductData> getProductsByClientIdAndProductName(Integer clientId, String productName, Integer page) throws ApiException {
        productName = normalizeSearchQuery(productName);
        validateSearchParams(page);

        if(Objects.nonNull(clientId)){
            Client client = clientApi.getCheckClientById(clientId);
            List<Product> products = productFlow.getProductsByClientIdAndProductName(clientId, productName, page);
            List<ProductData> productData = ConversionHelper.convertToProductData(products, client.getClientName());

            return new PaginatedData<>(productData, page, PAGE_SIZE);
        }
        else{
            List<Product> products = productFlow.getProductsByClientIdAndProductName(clientId, productName, page);
            List<Integer> clientIds = products.stream()
                .map(product -> product.getClient().getClientId())
                .distinct()
                .collect(Collectors.toList());  

            Map<Integer, String> clientNamesMap = clientApi.getCheckClientNamesByIds(clientIds);
            List<ProductData> productData = ConversionHelper.convertToProductData(products, clientNamesMap);
            
            return new PaginatedData<>(productData, page, PAGE_SIZE);
        }
    }

    public PaginatedData<ProductData> getAllProducts(Integer page) throws ApiException {
        List<Product> products = productFlow.getAllProducts(page);

        List<Integer> clientIds = products.stream()
            .map(product -> product.getClient().getClientId())
            .distinct()
            .collect(Collectors.toList());
        Map<Integer, String> clientNamesMap = clientApi.getCheckClientNamesByIds(clientIds);
        List<ProductData> productDataList = ConversionHelper.convertToProductData(products, clientNamesMap);

        return new PaginatedData<>(productDataList, page, PAGE_SIZE);
    }

    public ProductData addProduct(ProductForm form) throws ApiException {
        normalize(form);
        checkValid(form);
        Client client = clientApi.getCheckClientById(form.getClientId());
        Product product = ConversionHelper.convertToProduct(form, client);
        Product addedProduct = productFlow.addProduct(product);

        return ConversionHelper.convertToProductData(addedProduct,client.getClientName());
    }

    public ProductData updateProductById(Integer productId, ProductForm form) throws ApiException {
        normalize(form);
        checkValid(form);
        Client client = clientApi.getCheckClientById(form.getClientId());
        Product product = ConversionHelper.convertToProduct(form, client);
        Product updatedProduct = productFlow.updateProductById(productId, product);

        return ConversionHelper.convertToProductData(updatedProduct,client.getClientName());
    }

    public List<ProductData> bulkAddProducts(List<ProductForm> forms) throws ApiException {
        if (forms.size() > 5000) {
            throw new ApiException("Cannot process more than 5000 products at once");
        }
        for (ProductForm form : forms) {
            normalize(form);
            checkValid(form);
        }

        List<Integer> clientIds = forms.stream()
            .map(ProductForm::getClientId)
            .distinct()
            .collect(Collectors.toList());
            
        List<Client> clients = clientApi.getCheckClientsByIds(clientIds);
        List<Product> products = ConversionHelper.convertToProduct(forms, clients);

        List<Product> addedProducts = productFlow.bulkAddProducts(products);

        return ConversionHelper.convertToProductData(addedProducts, clients);
    }

    private void normalize(ProductForm form) throws ApiException {
        super.normalize(form);
        if (Objects.nonNull(form.getImagePath())) {
            String imagePath = form.getImagePath().trim();
            String lowerCasePath = imagePath.toLowerCase();
            if (!lowerCasePath.matches("^(https?://.*\\.(jpg|jpeg|png)|[\\w/\\\\.-]+\\.(jpg|jpeg|png))$")) {
                throw new ApiException("Invalid image format. Only PNG, JPG, and JPEG files are allowed");
            }
            form.setImagePath(imagePath);
        }
    }

    private String normalizeSearchQuery(String query) {
        if (Objects.isNull(query)) {
            return null;
        }
        String normalized = query.trim();
        return normalized.isEmpty() ? null : normalized.toLowerCase();
    }

    private void validateSearchParams(Integer page) throws ApiException {
        if (Objects.isNull(page)) {
            throw new ApiException("Page number cannot be null");
        }
        if (page < 0) {
            throw new ApiException("Page number cannot be negative");
        }
    }

}
