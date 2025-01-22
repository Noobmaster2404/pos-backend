package com.increff.server.dto;

import com.increff.server.entity.Product;
import com.increff.commons.model.ProductForm;
import com.increff.commons.model.ProductData;
import com.increff.server.flow.ClientFlow;
import com.increff.server.flow.ProductFlow;
import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Client;
import com.increff.server.entity.Inventory;
import com.increff.server.flow.InventoryFlow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

@Component
public class ProductDto extends AbstractDto {
    
    @Autowired
    private ProductFlow productFlow;

    @Autowired
    private ClientFlow clientFlow;

    @Autowired
    private InventoryFlow inventoryFlow;

    public void add(ProductForm form) throws ApiException {
        try {
            normalize(form);
            Product product = convert(form);
            productFlow.add(product);
        } catch (ApiException e) {
            throw new ApiException(getPrefix() + e.getMessage());
        }
    }

    public void update(Integer productId, ProductForm form) throws ApiException {
        normalize(form);
        
        Product product = convert(form);
        productFlow.update(productId, product);
    }

    public List<ProductData> getAll() throws ApiException {
        //should adding the quantity to the product data be here or in convertToData?
        return productFlow.getAll()
                .stream()
                .map(product -> {
                    try {
                        ProductData data = convertToData(product);
                        Client client = clientFlow.get(product.getClient().getClientId());
                        data.setClientName(client.getClientName());
                        data.setClientId(client.getClientId());
                        try {
                            List<Inventory> inventories = inventoryFlow.getAll();
                            Optional<Inventory> inventory = inventories.stream()
                                .filter(inv -> inv.getProduct().getProductId().equals(product.getProductId()))
                                .findFirst();
                            
                            data.setQuantity(inventory.map(inv -> inv.getQuantity().toString())
                                                    .orElse("0"));
                        } catch (Exception e) {
                            data.setQuantity("0");
                        }
                        
                        return data;
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
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

    public ProductData get(Integer productId) throws ApiException {
        Product product = productFlow.get(productId);
        ProductData data = convertToData(product);
        Client client = clientFlow.get(product.getClient().getClientId());
        data.setClientName(client.getClientName());
        data.setClientId(client.getClientId());
        try {
            Inventory inventory = inventoryFlow.get(productId);
            data.setQuantity(inventory.getQuantity().toString());
        } catch (Exception e) {
            data.setQuantity("0");
        }
        
        return data;
    }

    // public List<ProductData> getByClient(Integer clientId) throws ApiException {
    //     return productFlow.getByClient(clientId)
    //             .stream()
    //             .map(this::convertToData)
    //             .collect(Collectors.toList());
    // }
    //instead of doing this, we can fetch all clients and then make the user select one
    //then we can fetch all products for that client and then make the user select one

    public void bulkUpload(MultipartFile file) throws ApiException {
        validateFile(file);
        List<ProductForm> forms = readTsvFile(file);
        
        for (ProductForm form : forms) {
            normalize(form);
        }
        
        productFlow.bulkAdd(forms.stream()
                .map(form -> {
                    try {
                        return convert(form);
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList()));
    }

    private void validateFile(MultipartFile file) throws ApiException {
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new ApiException(getPrefix() + "File cannot be empty");
        }
        if (!file.getOriginalFilename().endsWith(".tsv")) {
            throw new ApiException(getPrefix() + "Only TSV files are allowed");
        }
    }

    private List<ProductForm> readTsvFile(MultipartFile file) throws ApiException {
        List<ProductForm> forms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineNumber = 0;
            while (Objects.nonNull(line = reader.readLine())) {
                lineNumber++;
                if (lineNumber == 1) continue; // Skip header
                if (lineNumber > 5000) {
                    throw new ApiException(getPrefix() + "File exceeds maximum limit of 5000 products");
                }
                forms.add(parseTsvLine(line, lineNumber));
            }
        } catch (Exception e) {
            throw new ApiException(getPrefix() + "Error reading file: " + e.getMessage());
        }
        return forms;
    }

    private ProductForm parseTsvLine(String line, int lineNumber) throws ApiException {
        String[] fields = line.split("\t");
        if (fields.length != 5) {
            throw new ApiException(getPrefix() + "Invalid number of fields at line " + lineNumber);
        }
        
        try {
            ProductForm form = new ProductForm();
            form.setProductBarcode(fields[0].trim());
            form.setProductName(fields[1].trim());
            form.setClientId(Integer.valueOf(fields[2].trim()));
            form.setProductImagePath(fields[3].trim());
            form.setProductMrp(Double.valueOf(fields[4].trim()));
            return form;
        } catch (NumberFormatException e) {
            throw new ApiException(getPrefix() + "Invalid number format at line " + lineNumber);
        }
    }

    private Product convert(ProductForm form) throws ApiException {
        try {
            Product product = new Product();
            product.setProductBarcode(form.getProductBarcode());
            product.setProductName(form.getProductName());
            Client client = clientFlow.get(form.getClientId());
            if (Objects.isNull(client)) {
                throw new ApiException("Client not found with ID: " + form.getClientId());
            }
            product.setClient(client);
            product.setProductImagePath(form.getProductImagePath());
            product.setProductMrp(form.getProductMrp());
            return product;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Error converting product form: " + e.getMessage());
        }
    }

    private ProductData convertToData(Product product) throws ApiException {
        try {
            ProductData data = new ProductData();
            data.setProductId(product.getProductId());
            data.setProductBarcode(product.getProductBarcode());
            data.setProductName(product.getProductName());
            if (Objects.isNull(product.getClient())) {
                throw new ApiException("Product has no associated client");
            }
            data.setClientId(product.getClient().getClientId());
            data.setProductImagePath(product.getProductImagePath());
            data.setProductMrp(product.getProductMrp());
            return data;
        } catch (Exception e) {
            throw new ApiException("Error converting product to data: " + e.getMessage());
        }
    }

    @Override
    protected String getPrefix() {
        return "Product: ";
    }

    private void normalize(ProductForm form) throws ApiException {
        super.normalize(form);
        // Special handling for image path - keep original case
        if (Objects.nonNull(form.getProductImagePath())) {
            String imagePath = form.getProductImagePath().trim();
            String lowerCasePath = imagePath.toLowerCase();
            if (!lowerCasePath.endsWith(".png") && 
                !lowerCasePath.endsWith(".jpg") && 
                !lowerCasePath.endsWith(".jpeg")) {
                throw new ApiException(getPrefix() + "Invalid image format. Only PNG, JPG, and JPEG files are allowed");
            }
            form.setProductImagePath(imagePath);
        }
    }
}
