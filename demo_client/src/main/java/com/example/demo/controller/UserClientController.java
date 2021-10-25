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

import com.example.demo.data.UserData;

@Controller
public class UserClientController {

  /**
   * RestTemplateオブジェクト
   */
  @Autowired
  private RestTemplate restTemplate;

  /**
   * APIのURI
   */
  private static final String endPointURI = "http://localhost:8080/api/user/v1/";
    /**
    * ユーザー情報を全件取得し、初期表示画面に遷移する
    * @param model Modelオブジェクト
    * @return 初期表示画面へのパス
    */
    @GetMapping("/user/list")
    public String list(Model model){
       // ユーザー情報リストをAPIで取得する
       ResponseEntity<List<UserData>> response = restTemplate.exchange(
               endPointURI, HttpMethod.GET,
               null, new ParameterizedTypeReference<List<UserData>>() {});
       List<UserData> userList = response.getBody();
       // 各ユーザー情報を編集し、Modelに設定する
       for(UserData userData : userList){
           // 名前をデコード
           userData.setUserName(decode(userData.getUserName()));
       }
       model.addAttribute("userlist", userList);
       return "UserInfoList";
    }
    
    @GetMapping("/user/edit")
    public String edit(Model model, @RequestParam("userId")int id) {
        
        // ユーザー情報をAPIで取得する
        UserData userData = restTemplate.getForObject(
                endPointURI  + id , UserData.class);
        if(userData != null){
            // 名前をデコード
            userData.setUserName(decode(userData.getUserName()));
        }else{
            userData = new UserData();
        }
        // ユーザー情報をModelオブジェクトに設定
        model.addAttribute("userData", userData);
        return "UserEdit";
    }

    @GetMapping("/user/createEdit")
    public String createEdit() {
        return "UserNewEdit";
    }
    
    @PutMapping("/user/create")
    public String create(Model model, @ModelAttribute("UserData") UserData user) {
        // ユーザー情報をAPIで登録する
        restTemplate.put(endPointURI, user);
        
        // ユーザー情報リストをAPIで取得する
        ResponseEntity<List<UserData>> response = restTemplate.exchange(
                endPointURI, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<UserData>>() {});
        List<UserData> userList = response.getBody();
        // 各ユーザー情報を編集し、Modelに設定する
        for(UserData userData : userList){
            // 名前をデコード
            userData.setUserName(decode(userData.getUserName()));
        }
        model.addAttribute("userlist", userList);
        
        return "UserInfoList";
    }
    
    @PutMapping("/user/update")
    public String update(Model model, @ModelAttribute("UserData") UserData user) {
        // ユーザー情報をAPIで更新する
        restTemplate.put(endPointURI, user);	
        
        // ユーザー情報リストをAPIで取得する
        ResponseEntity<List<UserData>> response = restTemplate.exchange(
                endPointURI, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<UserData>>() {});
        List<UserData> userList = response.getBody();
        // 各ユーザー情報を編集し、Modelに設定する
        for(UserData userData : userList){
            // 名前をデコード
            userData.setUserName(decode(userData.getUserName()));
        }
        model.addAttribute("userlist", userList);
        
        return "UserInfoList";
    }
    
    @DeleteMapping("/user/delete")
    public String delete(Model model, @RequestParam("userId")int id) {
        // ユーザー情報をAPIで削除する
        restTemplate.delete(endPointURI + id, UserData.class);
        
        // ユーザー情報リストをAPIで取得する
        ResponseEntity<List<UserData>> response = restTemplate.exchange(
                endPointURI, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<UserData>>() {});
        List<UserData> userList = response.getBody();
        // 各ユーザー情報を編集し、Modelに設定する
        for(UserData userData : userList){
            // 名前をデコード
            userData.setUserName(decode(userData.getUserName()));
        }
        model.addAttribute("userlist", userList);
        
        return "UserInfoList";
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