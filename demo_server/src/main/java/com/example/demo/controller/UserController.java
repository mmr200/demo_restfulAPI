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

import com.example.demo.entity.UserData;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * JSON形式で返却するためRestControllerを指定
 */
@RestController
public class UserController {

    /**
     * ユーザー情報テーブル(user_info)へアクセスするリポジトリ
     */
    @Autowired
    private UserRepository repository;
    
    /**
     * ユーザー情報 Service
     */
    @Autowired
    private UserService service;
    
    /**
     * APIのURI（パラメータなし）
     */
    private static final String endPointURI = "/api/user/v1/";
    /**
     * APIのURI（パラメータあり）
     */
    private static final String endPointURI2 = "/api/user/v1/{userId}";
    
    /**
     * ユーザー情報一覧画面を表示
     * @param model Model
     * @return ユーザー情報一覧画面のHTML
     */
    @GetMapping(value = endPointURI)
    public String getUserList(Model model) {
      List<UserData> userlist = service.searchAll();
      return getJsonData(userlist);
    }
    
    /**
     * ユーザー情報データを1件取得する
     * @return ユーザー情報データ(JSON形式)
     */
    @GetMapping(value = endPointURI2)
    public String getUserData(@PathVariable("userId") Integer id){
        // ユーザーデータを取得し、取得できなければそのまま返す
        UserData userInfo = repository.findUserDataByUserId(id);
        // ユーザーデータが取得できなかった場合は、null値を返す
        if(userInfo == null){
            return null;
        }
        // 名前をエンコード
        userInfo.setUserName(encode(userInfo.getUserName()));
        // 取得したユーザーデータをJSON文字列に変換し返却
        return getJsonData(userInfo);
    }
    
    /**
     * ユーザー情報を1件登録する
     */
    @PutMapping(value = endPointURI)
    private void registUserData(@RequestBody UserData user){
        // ユーザーデータを1件登録する
        repository.save(user);
    }
    
    /**
     * ユーザー情報を1件削除する
     */
    @DeleteMapping(value = endPointURI2)
    private void deleteUserData(@PathVariable("userId") Integer id){
        // ユーザーデータを削除する
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