package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.ReserveDetailData;
import com.example.demo.repository.ReserveDetailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * JSON形式で返却するためRestControllerを指定
 */
@RestController
public class ReserveDetailController {

    /**
     * 予約情報詳細テーブル(Reserve_info)へアクセスするリポジトリ
     */
    @Autowired
    private ReserveDetailRepository repository;
    
    /**
     * APIのURI（パラメータなし）
     */
    private static final String endPointURI = "/api/reserveDetail/v1/";
    /**
     * APIのURI（パラメータあり）
     */
    private static final String endPointURI2 = "/api/reserveDetail/v1/{reserveId}";
    
    /**
     * 予約情報詳細データを1件取得する
     * @return 予約情報詳細データ(JSON形式)
     */
    @GetMapping(value = endPointURI2)
    private String getReserveDetailData(@PathVariable("reserveId") Integer id){
        
        // 予約データを取得し、取得できなければそのまま返す
        List<ReserveDetailData> ReserveDetailDataList = repository.findItemDataByReserveId(id);
        
        // 予約データが取得できなかった場合は、null値を返す
        if(ReserveDetailDataList == null){
            return null;
        }
        // 取得した予約データをJSON文字列に変換し返却
        return getJsonData(ReserveDetailDataList);
    }
    
    /**
     * 予約情報詳細を登録する
     */
    @PutMapping(value = endPointURI)
    private void registReserveDetailData(@RequestBody ReserveDetailData reserve){
            repository.save(reserve);
    }
    
    /**
     * 予約情報詳細を1件削除する
     */
    @DeleteMapping(value = endPointURI2)
    private void deleteReserveDetailData(@PathVariable("reserveId") Integer id){
        
        // 予約データを取得し、取得できなければそのまま返す
        List<ReserveDetailData> reserveDetailDataList = repository.findItemDataByReserveId(id);
        
        // 予約詳細情報の件数分の削除処理を行う
        for(ReserveDetailData detailData : reserveDetailDataList){
            // 予約データを削除する
            repository.deleteById(detailData.getReserveSubId());
        }
    }
    
    /**
     * 引数のオブジェクトをJSON文字列に変換する
     * @param data オブジェクトのデータ
     * @return 変換後JSON文字列
     */
    private String getJsonData(Object data){
        String retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            retVal = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            System.err.println(e);
        }
        return retVal;
    }

}