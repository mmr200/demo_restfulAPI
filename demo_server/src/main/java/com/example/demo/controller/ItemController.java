package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.ItemData;
import com.example.demo.repository.ItemRepository;
import com.example.demo.service.ItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * JSON形式で返却するためRestControllerを指定
 */
@RestController
public class ItemController {

    /**
     * 商品情報テーブル(Item_info)へアクセスするリポジトリ
     */
    @Autowired
    private ItemRepository repository;
    
    /**
     * 商品情報 Service
     */
    @Autowired
    private ItemService service;
    
    /**
     * APIのURI（パラメータなし）
     */
    private static final String endPointURI = "/api/item/v1/";
    /**
     * APIのURI（パラメータあり）
     */
    private static final String endPointURI2 = "/api/item/v1/{itemId}";
    
    /**
     * 商品情報一覧画面を表示
     * @param model Model
     * @return 商品情報一覧画面のHTML
     */
    //@RequestMapping(value = "/getItemList", method = RequestMethod.GET)
    @GetMapping(value = endPointURI)
    public String getItemList(Model model) {
      List<ItemData> itemlist = service.searchAll();
      return getJsonData(itemlist);
    }
    
    /**
     * 商品情報データを1件取得する
     * @return 商品情報データ(JSON形式)
     */
    @GetMapping(value = endPointURI2)
    private String getItemData(@PathVariable("itemId") Integer id){
        // 商品データを取得し、取得できなければそのまま返す
        ItemData itemInfo = repository.findItemDataByItemId(id);
        // 商品データが取得できなかった場合は、null値を返す
        if(itemInfo == null){
            return null;
        }
        // 名前をエンコード
        itemInfo.setItemName(encode(itemInfo.getItemName()));
        // 取得した商品データをJSON文字列に変換し返却
        return getJsonData(itemInfo);
    }
    
    /**
     * 商品情報を1件登録する
     */
    @PutMapping(value = endPointURI)
    private void registItemData(@RequestBody ItemData item){
        // 商品データを1件登録する
        repository.save(item);
    }
    
    /**
     * 商品情報を1件削除する
     */
    @DeleteMapping(value = endPointURI2)
    private void deleteItemData(@PathVariable("itemId") Integer id){
        // 商品データを削除する
        repository.deleteById(id);
    }
    
    /**
     * 引数の文字列をエンコードする
     * @param data 任意の文字列
     * @return エンコード後の文字列
     */
    private String encode(String data){
        if(ObjectUtils.isEmpty(data)){
            return data;
        }
        String retVal = null;
        try{
            retVal = URLEncoder.encode(data, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            System.err.println(e);
        }
        return retVal;
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