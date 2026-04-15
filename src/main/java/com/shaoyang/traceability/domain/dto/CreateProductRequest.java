package com.shaoyang.traceability.domain.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateProductRequest {

    @NotBlank(message = "name 不能为空")
    private String name;

    @NotBlank(message = "category 不能为空")
    private String category;

    private String description;

    private String ownerEnterprise;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerEnterprise() {
        return ownerEnterprise;
    }

    public void setOwnerEnterprise(String ownerEnterprise) {
        this.ownerEnterprise = ownerEnterprise;
    }
}
