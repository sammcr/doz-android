package com.samanthacontreras.samm.doz;

import java.util.ArrayList;

/**
 * Created by samm on 5/24/18.
 */

public class Product {
    private int id, category_id;
    private String name, created_at, updated_at, url, description;
    private float price, weight, width, length, discount;
    private ArrayList<String> sizes;


    public Product(int id, int category_id, String name, String created_at, String updated_at, String url, String description, float price, float weight, float width, float length, float discount, ArrayList<String> sizes) {
        this.id = id;
        this.category_id = category_id;
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.url = url;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.width = width;
        this.length = length;
        this.discount = discount;
        this.sizes = sizes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public ArrayList<String> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<String> sizes) {
        this.sizes = sizes;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }
}
