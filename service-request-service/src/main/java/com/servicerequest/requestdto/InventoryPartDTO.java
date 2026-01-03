package com.servicerequest.requestdto;


import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class InventoryPartDTO {
	
    private String id;
    
    @Min(value = 1, message = "Price must be greater than 0")
    private double price;
}
