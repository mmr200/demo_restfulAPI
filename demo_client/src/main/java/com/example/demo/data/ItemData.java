package com.example.demo.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


/**
 * 商品情報 Entity
 */
@Entity
@Data
@Table(name = "item_info")
public class ItemData {

    /**
     * ID
     */
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;

    /**
     * 名前
     */
    @Column(name = "item_name")
    private String itemName;

    /**
     * 金額
     */
    @Column(name = "price")
    private int price;
    
}