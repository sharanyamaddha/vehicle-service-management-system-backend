package com.inventoryservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventoryservice.service.InventoryService;

@RestController
@RequestMapping("/api/parts")
public class InventoryController {
	
	@Autowired
	private InventoryService inventoryService;

}
