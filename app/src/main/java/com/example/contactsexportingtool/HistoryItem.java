package com.example.contactsexportingtool;

import java.io.Serializable;
import java.util.Date;

public class HistoryItem implements Serializable {

    String label, name, extension, storageType;
    Date date;

    public HistoryItem(String label, String name, String extension, String storageType, Date date) {
        this.label = label;
        this.name = name;
        this.extension = extension;
        this.storageType = storageType;
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String showHistoryItem(HistoryItem historyItem){
        String salida = "";
        salida += historyItem.getName() + " | " + historyItem.getExtension() + " | "
                + historyItem.getStorageType() + " | " + historyItem.getDate().toString();
        return salida;
    }
}
