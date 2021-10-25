package com.example.demo.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import com.example.demo.data.ItemData;

@Controller
public class ItemClientController {

  /**
   * RestTemplateオブジェクト
   */
  @Autowired
  private RestTemplate restTemplate;
  
  /**
   * APIのURI
   */
  private static final String endPointURI = "http://localhost:8080/api/item/v1/";
  
    /**
    * 商品情報を全件取得し、初期表示画面に遷移する
    * @param model Modelオブジェクト
    * @return 初期表示画面へのパス
    */
    @GetMapping("/item/list")
    public String list(Model model){
       // 商品情報リストをAPIで取得する
       ResponseEntity<List<ItemData>> response = restTemplate.exchange(
               endPointURI, HttpMethod.GET,
               null, new ParameterizedTypeReference<List<ItemData>>() {});
       List<ItemData> itemList = response.getBody();
       // 各商品情報を編集し、Modelに設定する
       for(ItemData itemData : itemList){
           // 名前をデコード
           itemData.setItemName(decode(itemData.getItemName()));
       }
       model.addAttribute("itemList", itemList);
       return "ItemInfoList";
    }
    
    @GetMapping("/item/edit")
    public String edit(Model model, @RequestParam("itemId")int id) {
        
        // 商品情報をAPIで取得する
        ItemData itemData = restTemplate.getForObject(
                endPointURI + id, ItemData.class);
        if(itemData != null){
            // 名前をデコード
            itemData.setItemName(decode(itemData.getItemName()));
        }else{
            itemData = new ItemData();
        }
        // 商品情報をModelオブジェクトに設定
        model.addAttribute("itemData", itemData);
        return "ItemEdit";
    }

    @GetMapping("/item/createEdit")
    public String createEdit() {
        return "ItemNewEdit";
    }
    
    @PutMapping("/item/create")
    public String create(Model model, @ModelAttribute("ItemData") ItemData item) {
        // 商品情報をAPIで登録する
        restTemplate.put(endPointURI, item);
        
        // 商品情報リストをAPIで取得する
        ResponseEntity<List<ItemData>> response = restTemplate.exchange(
                endPointURI, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<ItemData>>() {});
        List<ItemData> itemList = response.getBody();
        // 各商品情報を編集し、Modelに設定する
        for(ItemData ItemData : itemList){
            // 名前をデコード
            ItemData.setItemName(decode(ItemData.getItemName()));
        }
        model.addAttribute("itemList", itemList);
        
        return "ItemInfoList";
    }
    
    @PutMapping("/item/update")
    public String update(Model model, @ModelAttribute("ItemData") ItemData item) {
        // 商品情報をAPIで更新する
        restTemplate.put(endPointURI, item);	
        
        // 商品情報リストをAPIで取得する
        ResponseEntity<List<ItemData>> response = restTemplate.exchange(
                endPointURI, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<ItemData>>() {});
        List<ItemData> itemList = response.getBody();
        // 各商品情報を編集し、Modelに設定する
        for(ItemData ItemData : itemList){
            // 名前をデコード
            ItemData.setItemName(decode(ItemData.getItemName()));
        }
        model.addAttribute("itemList", itemList);
        
        return "ItemInfoList";
    }
    
    @DeleteMapping("/item/delete")
    public String delete(Model model, @RequestParam("itemId")int id) {
        // 商品情報をAPIで削除する
        restTemplate.delete(endPointURI + id, ItemData.class);
        
        // 商品情報リストをAPIで取得する
        ResponseEntity<List<ItemData>> response = restTemplate.exchange(
                endPointURI, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<ItemData>>() {});
        List<ItemData> itemList = response.getBody();
        // 各商品情報を編集し、Modelに設定する
        for(ItemData ItemData : itemList){
            // 名前をデコード
            ItemData.setItemName(decode(ItemData.getItemName()));
        }
        model.addAttribute("itemList", itemList);
        
        return "ItemInfoList";
    }
    
      /**
      * 引数の文字列をデコードする
      * @param data 任意の文字列
      * @return エンコード後の文字列
      */
     private String decode(String data){
         if(StringUtils.isEmpty(data)){
             return data;
         }
         String retVal = null;
         try{
             retVal = URLDecoder.decode(data, "UTF-8");
         }catch (UnsupportedEncodingException e) {
             System.err.println(e);
         }
         return retVal;
     }
}