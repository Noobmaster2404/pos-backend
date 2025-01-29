package com.increff.server.dto;

import com.increff.server.entity.Product;
import com.increff.commons.model.ProductForm;
import com.increff.commons.model.ProductData;
import com.increff.server.flow.ClientFlow;
import com.increff.server.flow.ProductFlow;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductDto extends AbstractDto {
    
    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ClientFlow clientFlow;

    public List<ProductData> getProductsByName(String productName) throws ApiException {
        return productFlow.getProductsByName(productName)
                .stream()
                .map(product -> {
                    try {
                        ProductData data = ConversionHelper.convertToProductData(product);
                        Client client = clientFlow.getClientById(product.getClient().getClientId());
                        data.setClientName(client.getClientName());
                        data.setClientId(client.getClientId());
                        return data;
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public ProductData getProductByBarcode(String barcode) throws ApiException {
        Product product = productFlow.getProductByBarcode(barcode);
        try {
            ProductData data = ConversionHelper.convertToProductData(product);
            Client client = clientFlow.getClientById(product.getClient().getClientId());
            data.setClientName(client.getClientName());
            data.setClientId(client.getClientId());
            return data;
        } catch (ApiException e) {
            throw new ApiException(getPrefix() + e.getMessage());
        }
    }

    public List<ProductData> getProductsByClientId(Integer clientId) throws ApiException {
        return productFlow.getProductsByClientId(clientId)
                .stream()
                .map(product -> {
                    try {
                        return ConversionHelper.convertToProductData(product);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<ProductData> getAllProducts() throws ApiException {
        return productFlow.getAllProducts()
                .stream()
                .map(product -> {
                    try {
                        ProductData data = ConversionHelper.convertToProductData(product);
                        Client client = clientFlow.getClientById(product.getClient().getClientId());
                        data.setClientName(client.getClientName());
                        data.setClientId(client.getClientId());
                        return data;
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public ProductData addProduct(ProductForm form) throws ApiException {
        try {
            checkValid(form);
            normalize(form);
            List<Client> clients = clientFlow.getClientsByName(form.getClientName());
            if (clients.isEmpty()) {
                throw new ApiException("Client not found with name: " + form.getClientName());
            }
            Client client = clients.get(0);  // Get first matching client
            Product product = ConversionHelper.convertToProduct(form, client);
            return ConversionHelper.convertToProductData(productFlow.addProduct(product));
        } catch (Exception e) {
            throw new ApiException(getPrefix() + e.getMessage());
        }
    }

    public ProductData updateProductByBarcode(String barcode, ProductForm form) throws ApiException {
        checkValid(form);
        normalize(form);
        List<Client> clients = clientFlow.getClientsByName(form.getClientName());
        if (clients.isEmpty()) {
            throw new ApiException("Client not found with name: " + form.getClientName());
        }
        Client client = clients.get(0);
        Product product = ConversionHelper.convertToProduct(form, client);
        Product updatedProduct = productFlow.updateProductByBarcode(barcode, product);
        ProductData data = ConversionHelper.convertToProductData(updatedProduct);
        data.setClientName(client.getClientName());
        return data;
    }

    public List<ProductData> bulkAddProducts(List<ProductForm> forms) throws ApiException {
        if (forms.size() > 5000) {
            throw new ApiException(getPrefix() + "Cannot process more than 5000 products at once");
        }

        for (ProductForm form : forms) {
            normalize(form);
        }
        
        List<Product> addedProducts = productFlow.bulkAddProducts(forms.stream()
                .map(form -> {
                    try {
                        List<Client> clients = clientFlow.getClientsByName(form.getClientName());
                        if (clients.isEmpty()) {
                            throw new ApiException("Client not found with name: " + form.getClientName());
                        }
                        Client client = clients.get(0);  // Get first matching client
                        return ConversionHelper.convertToProduct(form, client);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList()));

        return addedProducts.stream()
                .map(product -> {
                    try {
                        ProductData data = ConversionHelper.convertToProductData(product);
                        Client client = product.getClient();
                        data.setClientName(client.getClientName());
                        data.setClientId(client.getClientId());
                        return data;
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
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

    //The method makes separate queries for each product to get client and inventory information.
    //Consider using joins or batch fetching like below.
    // public List<ProductData> getAll() throws ApiException {
    //     List<Product> products = productFlow.getAll();
    //     List<Inventory> inventories = inventoryFlow.getAll();
    //     Map<Integer, Inventory> inventoryMap = inventories.stream()
    //         .collect(Collectors.toMap(
    //             inv -> inv.getProduct().getProductId(),
    //             inv -> inv
    //         ));
        
    //     return products.stream()
    //             .map(product -> {
    //                 try {
    //                     ProductData data = convertToData(product);
    //                     data.setClientName(product.getClient().getClientName()); // Client is already loaded due to @ManyToOne
    //                     data.setQuantity(Optional.ofNullable(inventoryMap.get(product.getProductId()))
    //                             .map(inv -> inv.getQuantity().toString())
    //                             .orElse("0"));
    //                     return data;
    //                 } catch (ApiException e) {
    //                     throw new RuntimeException(e);
    //                 }
    //             })
    //             .collect(Collectors.toList());
    // }

    // public ProductData getProductById(Integer productId) throws ApiException {
    //     Product product = productFlow.getProductById(productId);
    //     ProductData data = ConversionClass.convertToProductData(product);
    //     Client client = clientFlow.getClientById(product.getClient().getClientId());
    //     data.setClientName(client.getClientName());
    //     data.setClientId(client.getClientId());
    //     return data;
    // }

    // public List<ProductData> getByClient(Integer clientId) throws ApiException {
    //     return productFlow.getByClient(clientId)
    //             .stream()
    //             .map(this::convertToData)
    //             .collect(Collectors.toList());
    // }
    //instead of doing this, we can fetch all clients and then make the user select one
    //then we can fetch all products for that client and then make the user select one

}
