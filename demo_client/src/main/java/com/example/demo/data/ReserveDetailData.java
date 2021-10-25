package com.example.demo.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 予約情報詳細 Entity
 */
@Entity
@Data
@Table(name = "reserve_info_detail")
public class ReserveDetailData {

    /**
     * ID
     */
    @Id
    @Column(name = "reserve_sub_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reserveSubId;
    
    /**
     * 予約ID
     */
    @Column(name = "reserve_id")
    private int reserveId;
    
    /**
     * 商品ID
     */
    @Column(name = "item_id")
    private int itemId;
    
    /**
     * 数量
     */
    @Column(name = "quantity")
    private int quantity;
}