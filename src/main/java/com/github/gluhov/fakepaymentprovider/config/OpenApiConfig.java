package com.github.gluhov.fakepaymentprovider.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenApiCustomizer globalResponseCustomizer() {
        return openApi -> {
            Components components = openApi.getComponents();
            if (components == null) {
                components = new Components();
                openApi.setComponents(components);
            }
            
            // Add ErrorResponse schema
            components.addSchemas("ErrorResponse", new Schema<>()
                    .type("object")
                    .addProperty("status", new Schema<>().type("integer").description("HTTP status code"))
                    .addProperty("errors", new Schema<>()
                            .type("array")
                            .items(new Schema<>()
                                    .type("object")
                                    .addProperty("code", new Schema<>().type("string").description("Error code"))
                                    .addProperty("message", new Schema<>().type("string").description("Error message"))
                                    .required(java.util.Arrays.asList("code", "message")))
                            .required(java.util.Arrays.asList("status", "errors"))));

            // Add DTOs schemas for success responses
            addTransactionDtoSchema(components);
            addTransactionResponseDtoSchema(components);
            addTransactionDtoListResponseSchema(components);
            addCustomerDtoSchema(components);
            addCardDataDtoSchema(components);
            addPaymentMethodDtoSchema(components);

            // Add default error responses
            ApiResponses defaultResponses = new ApiResponses();
            defaultResponses.addApiResponse("400", new ApiResponse()
                    .description("Bad Request")
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
            defaultResponses.addApiResponse("401", new ApiResponse()
                    .description("Unauthorized - Authentication required")
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
            defaultResponses.addApiResponse("403", new ApiResponse()
                    .description("Forbidden - Insufficient permissions")
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
            defaultResponses.addApiResponse("404", new ApiResponse()
                    .description("Resource not found")
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
            defaultResponses.addApiResponse("500", new ApiResponse()
                    .description("Internal server error")
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));
            defaultResponses.addApiResponse("502", new ApiResponse()
                    .description("Provider error")
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ErrorResponse")))));

            openApi.getPaths().forEach((path, pathItem) -> {
                // Process each HTTP method separately
                if (pathItem.getGet() != null) {
                    processOperation(path, "GET", pathItem.getGet(), defaultResponses);
                }
                if (pathItem.getPost() != null) {
                    processOperation(path, "POST", pathItem.getPost(), defaultResponses);
                }
                if (pathItem.getPut() != null) {
                    processOperation(path, "PUT", pathItem.getPut(), defaultResponses);
                }
                if (pathItem.getDelete() != null) {
                    processOperation(path, "DELETE", pathItem.getDelete(), defaultResponses);
                }
                if (pathItem.getPatch() != null) {
                    processOperation(path, "PATCH", pathItem.getPatch(), defaultResponses);
                }
            });
        };
    }
    
    private void processOperation(String path, String httpMethod, io.swagger.v3.oas.models.Operation operation, ApiResponses defaultResponses) {
        ApiResponses apiResponses = operation.getResponses();
        if (apiResponses == null) {
            apiResponses = new ApiResponses();
            operation.setResponses(apiResponses);
        }
        
        // Add success responses based on path patterns and HTTP method
        addSuccessResponseForOperation(path, httpMethod, operation, apiResponses);
        
        // Add default error responses
        defaultResponses.forEach((code, response) -> {
            if (!apiResponses.containsKey(code)) {
                apiResponses.addApiResponse(code, response);
            }
        });
    }
    
    private void addSuccessResponseForOperation(String path, String httpMethod, io.swagger.v3.oas.models.Operation operation, ApiResponses apiResponses) {
        String schemaRef = determineSuccessSchemaRef(path, httpMethod, operation);
        if (schemaRef != null) {
            // For POST operations, prefer 201 Created, otherwise use 200 OK
            if ("POST".equals(httpMethod)) {
                if (!apiResponses.containsKey("201")) {
                    apiResponses.addApiResponse("201", new ApiResponse()
                            .description("Created")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(new Schema<>().$ref(schemaRef)))));
                }
            } else {
                if (!apiResponses.containsKey("200")) {
                    apiResponses.addApiResponse("200", new ApiResponse()
                            .description("Success")
                            .content(new Content().addMediaType("application/json",
                                    new MediaType().schema(new Schema<>().$ref(schemaRef)))));
                }
            }
        }
    }
    
    private String determineSuccessSchemaRef(String path, String httpMethod, io.swagger.v3.oas.models.Operation operation) {
        // Check by path patterns first
        if (path.contains("/details")) {
            return "#/components/schemas/TransactionDto";
        } else if (path.contains("/list")) {
            return "#/components/schemas/TransactionDtoListResponse";
        }
        
        // Check by HTTP method
        if ("POST".equals(httpMethod)) {
            // POST operations typically return TransactionResponseDto
            return "#/components/schemas/TransactionResponseDto";
        }
        
        // Check by operation summary or description for additional context
        if (operation.getSummary() != null) {
            String summary = operation.getSummary().toLowerCase();
            if (summary.contains("create") || summary.contains("top up") || summary.contains("topup")) {
                return "#/components/schemas/TransactionResponseDto";
            }
        }
        
        // Default for GET methods on transaction endpoints
        if ("GET".equals(httpMethod) && path.contains("/transaction")) {
            return "#/components/schemas/TransactionDto";
        }
        
        // General default
        return "#/components/schemas/TransactionDto";
    }
    
    private void addTransactionDtoSchema(Components components) {
        components.addSchemas("TransactionDto", new Schema<>()
                .type("object")
                .addProperty("transaction_id", new Schema<>().type("string").format("uuid"))
                .addProperty("created_at", new Schema<>().type("string").format("date-time"))
                .addProperty("updated_at", new Schema<>().type("string").format("date-time"))
                .addProperty("notification_url", new Schema<>().type("string"))
                .addProperty("currency", new Schema<>().type("string"))
                .addProperty("amount", new Schema<>().type("integer").format("int64"))
                .addProperty("language", new Schema<>().type("string"))
                .addProperty("message", new Schema<>().type("string"))
                .addProperty("transaction_status", new Schema<>().type("string")._enum(java.util.Arrays.asList("IN_PROGRESS", "SUCCESS", "FAILED")))
                .addProperty("customer", new Schema<>().$ref("#/components/schemas/CustomerDto"))
                .addProperty("card_data", new Schema<>().$ref("#/components/schemas/CardDataDto"))
                .addProperty("payment_method", new Schema<>().$ref("#/components/schemas/PaymentMethodDto")));
    }
    
    private void addTransactionResponseDtoSchema(Components components) {
        components.addSchemas("TransactionResponseDto", new Schema<>()
                .type("object")
                .addProperty("transaction_id", new Schema<>().type("string").format("uuid"))
                .addProperty("message", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("string")._enum(java.util.Arrays.asList("IN_PROGRESS", "SUCCESS", "FAILED"))));
    }
    
    private void addTransactionDtoListResponseSchema(Components components) {
        components.addSchemas("TransactionDtoListResponse", new Schema<>()
                .type("object")
                .addProperty("transaction_list", new Schema<>()
                        .type("array")
                        .items(new Schema<>().$ref("#/components/schemas/TransactionDto"))));
    }
    
    private void addCustomerDtoSchema(Components components) {
        components.addSchemas("CustomerDto", new Schema<>()
                .type("object")
                .addProperty("first_name", new Schema<>().type("string"))
                .addProperty("last_name", new Schema<>().type("string"))
                .addProperty("country", new Schema<>().type("string")));
    }
    
    private void addCardDataDtoSchema(Components components) {
        components.addSchemas("CardDataDto", new Schema<>()
                .type("object")
                .addProperty("card_number", new Schema<>().type("string").description("Masked card number")));
    }
    
    private void addPaymentMethodDtoSchema(Components components) {
        components.addSchemas("PaymentMethodDto", new Schema<>()
                .type("object")
                .addProperty("type", new Schema<>().type("string")));
    }
}