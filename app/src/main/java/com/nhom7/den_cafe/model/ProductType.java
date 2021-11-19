package com.nhom7.den_cafe.model;

import java.io.Serializable;

public class ProductType implements Serializable {
    private String typeId;
    private String typeName;
    private String typeImage;

    public ProductType(String typeId, String typeName, String typeImage) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.typeImage = typeImage;
    }

    public ProductType(String typeName, String typeImage) {
        this.typeName = typeName;
        this.typeImage = typeImage;
    }

    public ProductType() {

    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeImage() {
        return typeImage;
    }

    public void setTypeImage(String typeImage) {
        this.typeImage = typeImage;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
