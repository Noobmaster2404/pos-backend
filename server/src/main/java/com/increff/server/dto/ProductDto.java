package com.increff.server.dto;

import com.increff.server.entity.Product;
import com.increff.commons.model.ProductForm;
import com.increff.commons.model.ProductData;
import com.increff.server.api.ClientApi;
import com.increff.server.flow.ProductFlow;
import com.increff.server.helper.ConversionHelper;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public List<ProductData> getProductsByNamePrefix(String productName) throws ApiException {
        List<Product> products = productFlow.getProductsByNamePrefix(productName);

        List<Integer> clientIds = products.stream()
            .map(product -> product.getClient().getClientId())
            .distinct()
            .collect(Collectors.toList());  
        Map<Integer, String> clientNamesMap = clientApi.getCheckClientNamesByIds(clientIds);

        return ConversionHelper.convertToProductData(products, clientNamesMap);
    }

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        Product product = productFlow.getProductByBarcode(barcode);
        Client client = clientApi.getCheckClientById(product.getClient().getClientId());

        return ConversionHelper.convertToProductData(product,client.getClientName());
    }

    public List<ProductData> getProductsByClientId(Integer clientId) throws ApiException {
        List<Product> products = productFlow.getProductsByClientId(clientId);
        Client client = clientApi.getCheckClientById(clientId);

        return ConversionHelper.convertToProductData(products,client.getClientName());
    }

    public List<ProductData> getAllProducts() throws ApiException {
        List<Product> products = productFlow.getAllProducts();

        List<Integer> clientIds = products.stream()
            .map(product -> product.getClient().getClientId())
            .distinct()
            .collect(Collectors.toList());
        Map<Integer, String> clientNamesMap = clientApi.getCheckClientNamesByIds(clientIds);

        return ConversionHelper.convertToProductData(products, clientNamesMap);
    }

    public ProductData addProduct(ProductForm form) throws ApiException {
        normalize(form);
        checkValid(form);
        Client client = clientApi.getCheckClientById(form.getClientId());
        Product product = ConversionHelper.convertToProduct(form, client);
        Product addedProduct = productFlow.addProduct(product);

        return ConversionHelper.convertToProductData(addedProduct,client.getClientName());
    }

    public ProductData updateProductByBarcode(String barcode, ProductForm form) throws ApiException {
        normalize(form);
        checkValid(form);
        Client client = clientApi.getCheckClientById(form.getClientId());
        Product product = ConversionHelper.convertToProduct(form, client);
        Product updatedProduct = productFlow.updateProductByBarcode(barcode, product);

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
            if (!lowerCasePath.endsWith(".png") && 
                !lowerCasePath.endsWith(".jpg") && 
                !lowerCasePath.endsWith(".jpeg")) {
                throw new ApiException("Invalid image format. Only PNG, JPG, and JPEG files are allowed");
            }
            //TODO: Optimize above - extension not in this ...
            form.setImagePath(imagePath);
        }
    }

}
