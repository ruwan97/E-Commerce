package com.rc.ecommerce.enums;

public enum CategoryType {
    ELECTRONICS(1, "Electronics"),
    CLOTHING(2, "Clothing"),
    BEAUTY(3, "Beauty"),
    HEALTH(4, "Health"),
    HOME_AND_KITCHEN(5, "Home and Kitchen"),
    GROCERY(6, "Grocery"),
    SPORTS_AND_OUTDOORS(7, "Sports and Outdoors"),
    AUTOMOTIVE(8, "Automotive"),
    OTHER(9, "Other");

    private final int id;
    private final String description;

    CategoryType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static CategoryType getById(int id) {
        for (CategoryType category : CategoryType.values()) {
            if (category.id == id) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid Category Id: " + id);
    }
}
