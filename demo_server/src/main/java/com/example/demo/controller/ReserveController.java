package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.ReserveData;
import com.example.demo.repository.ReserveRepository;
import com.example.demo.service.ReserveService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * JSON形式で返却するためRestControllerを指定
 */
@RestController
public class ReserveController {

    /**
     * 予約情報テーブル(Reserve_info)へアクセスするリポジトリ
     */
    @Autowired
    private ReserveRepository repository;
    
    /**
     * 予約情報 Service
     */
    @Autowired
    private ReserveService service;
    
    /**
     * APIのURI（パラメータなし）
     */
    private static final String endPointURI = "/api/reserve/v1/";
    /**
     * APIのURI（パラメータあり）
     */
    private static final String endPointURI2 = "/api/reserve/v1/{reserveId}";
    
    /**
     * 予約情報一覧画面を表示
     * @param model Model
     * @return 予約情報一覧画面のHTML
     */
    @GetMapping(value = endPointURI)
    public String getRederveList(Model model) {
        List<ReserveData> reservelist = service.searchAll();
        return getJsonData(reservelist);
    }
    
    /**
     * 予約情報データを1件取得する
     * @return 予約情報データ(JSON形式)
     */
    @GetMapping(value = endPointURI2)
    private String getReserveData(@PathVariable("reserveId") Integer id){
        
        // 予約データを取得し、取得できなければそのまま返す
        ReserveData reserveInfo = repository.findItemDataByReserveId(id);
        // 予約データが取得できなかった場合は、null値を返す
        if(reserveInfo == null){
            return null;
        }
        // 取得した予約データをJSON文字列に変換し返却
        return getJsonData(reserveInfo);
    }
    
    /**
     * 予約情報を1件登録する
     */
    @PutMapping(value = endPointURI)
    private void registReserveData(@RequestBody ReserveData reserve){
        // 予約データを1件登録する
        repository.save(reserve);
    }
    
    /**
     * 予約情報を1件削除する
     */
    @DeleteMapping(value = endPointURI2)
    private void deleteReserveData(@PathVariable("reserveId") Integer id){
        // 予約データを削除する
        repository.deleteById(id);
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