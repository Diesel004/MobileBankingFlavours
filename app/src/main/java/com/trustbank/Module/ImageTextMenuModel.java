package com.trustbank.Module;

/**
 * Created by Trust on 25-02-2016.
 */
public class ImageTextMenuModel {
    private String itemName;
    private int image;
    private String isEnabled;
    private String lableName;


    public ImageTextMenuModel(String itemName, int image, String isEnabled, String lableName) {
        this.itemName = itemName;
        this.image = image;
        this.isEnabled = isEnabled;
        this.lableName = lableName;
    }

    public ImageTextMenuModel(String itemName, int image) {
        this.itemName = itemName;
        this.image = image;
    }

    public ImageTextMenuModel(String itemName, int image, String isEnabled) {
        this.itemName = itemName;
        this.image = image;
        this.isEnabled = isEnabled;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getLableName() {
        return lableName;
    }

    public void setLableName(String lableName) {
        this.lableName = lableName;
    }
}
