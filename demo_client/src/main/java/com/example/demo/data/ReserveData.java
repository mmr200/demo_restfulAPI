package com.example.demo.data;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import lombok.Data;

/**
 * 予約情報 Entity
 */
@Entity
@Data
@Table(name = "reserve_info")
public class ReserveData {

    /**
     * ID
     */
    @Id
    @Column(name = "reserve_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reserveId;
    
    /**
     * USER_ID
     */
    @Column(name = "user_id")
    private int userId;
    
    /**
     * 予約時間
     */
    @Column(name = "reserve_time")
    private String reserveTime;
    
    /**
     * 配達日
     */
    @Column(name = "delivery_date")
    private String deliveryDate;
    
    /**
     * 予約した商品情報リスト
     */
    @Transient
    private ArrayList<ReserveDetailData> reserveDetailList;
    
    // リストに新規項目を追加
    public void addList(int id) {
        ReserveDetailData data = new ReserveDetailData();
        data.setReserveId(id);
        reserveDetailList.add(data);
    }
    
 // リストから項目を削除
     public void removeList(int index) {
         
         // 入力欄は最低1行は残す
         if (reserveDetailList.size() > 1) {
             reserveDetailList.remove(index);
         }
     }
}