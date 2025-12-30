package com.inventoryservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "inventory_parts")
@Data
public class InventoryPart {
	
    @Id
    private String id;          

    private String name;        // Engine Oil
    private double price;       
    private int stock;         
    private int reorderLevel; 
	

}
