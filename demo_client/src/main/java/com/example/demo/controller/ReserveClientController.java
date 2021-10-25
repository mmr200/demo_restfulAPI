    package com.example.demo.controller;
    
    import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.example.demo.data.ReserveData;
import com.example.demo.data.ReserveDetailData;
import com.example.demo.data.UserData;
    
    @Controller
    public class ReserveClientController {
      /**
       * RestTemplateオブジェクト
       */
      @Autowired
      private RestTemplate restTemplate;
      
      /**
       * APIのURI
       */
      private static final String userURI = "http://localhost:8080/api/user/v1/";
      private static final String itemURI = "http://localhost:8080/api/item/v1/";
      private static final String reserveURI = "http://localhost:8080/api/reserve/v1/";
      private static final String reserveDetailURI = "http://localhost:8080/api/reserveDetail/v1/";
      
      /**
    * 予約情報を全件取得し、初期表示画面に遷移する
    * @param model Modelオブジェクト
    * @return 初期表示画面へのパス
    */
    @GetMapping("/reserve/list")
    public String list(Model model){
       // 予約情報リストを取得する
       model.addAttribute("reserveList", getReserveList());
       return "ReserveInfoList";
    }
    
    @GetMapping("/reserve/edit")
    public String edit(Model model, @RequestParam("reserveId")int id) {
        
        //　ユーザー情報リストをセット
        model.addAttribute("userlist", getUserList());		
    
        // 商品情報リストをAPIで取得する
        model.addAttribute("selectItems", getItemList());

        // 予約情報をAPIで取得する
        ReserveData reserveData = restTemplate.getForObject(
                reserveURI + id, ReserveData.class);

        // 予約情報詳細を取得する
        ArrayList<ReserveDetailData> reserveDetailDataList = getReserveDetailList(id);
        reserveData.setReserveDetailList(reserveDetailDataList);
        
        model.addAttribute("reserveData", reserveData);
        
        return "ReserveEdit";
    }
    
    @GetMapping("/reserve/createEdit")
    public String createEdit(Model model) {
    
        //　ユーザー情報nリストをセット
        model.addAttribute("userlist", getUserList());		
    
        // 商品情報リストをAPIで取得する
        model.addAttribute("selectItems", getItemList());
        
        // リスト最後尾に1行追加
        ReserveData reserveData = new ReserveData();
        ReserveDetailData data = new ReserveDetailData();
        ArrayList<ReserveDetailData> list = new ArrayList<ReserveDetailData>();
        list.add(data);
        reserveData.setReserveDetailList(list);
        model.addAttribute("reserveData", reserveData);
        
        return "ReserveNewEdit";
    }
    
    @PutMapping(value = "/reserve/create", params = "create")
    public String create(Model model, @ModelAttribute("reserveData") ReserveData reserve, 
            @RequestParam("userId")int id) {
    
        // 予約日時を設定
        reserve.setReserveTime(getDataTime());	
        
        // 予約情報をAPIで登録する
        reserve.setUserId(id);
        restTemplate.put(reserveURI, reserve);
        
        // 予約情報リストをAPIで取得する
        List<ReserveData> list = getReserveList();
        model.addAttribute("reserveList", list);
        
        // 予約情報詳細をAPIで登録する
        ArrayList<ReserveDetailData> reserveList = reserve.getReserveDetailList();
        for(ReserveDetailData reserveDetail : reserveList) {
            // 予約データを1件登録する
            reserveDetail.setReserveId(list.get(list.size()-1).getReserveId());
            restTemplate.put(reserveDetailURI, reserveDetail);
        }
    
        return "ReserveInfoList";
    }
    
    @PutMapping(value = "/reserve/update", params = "update")
    public String update(Model model, @ModelAttribute("reserveData") ReserveData reserve) {
    // 予約日時を設定
    reserve.setReserveTime(getDataTime());	
    
        // 予約情報をAPIで登録する
        restTemplate.put(reserveURI, reserve);
        
        // 予約情報詳細をAPIで削除する
        restTemplate.delete(reserveDetailURI + reserve.getReserveId(), ReserveDetailData.class);
        
        // 予約情報リストをAPIで取得する
        model.addAttribute("reserveList", getReserveList());
        
        // 予約情報詳細をAPIで登録する
        ArrayList<ReserveDetailData> reserveList = reserve.getReserveDetailList();
        for(ReserveDetailData reserveDetail : reserveList) {
            // 予約データを1件登録する
            restTemplate.put(reserveDetailURI, reserveDetail);
        }
        
        return "ReserveInfoList";
    }
    
    @DeleteMapping("/reserve/delete")
    public String delete(Model model, @RequestParam("reserveId")int id) {
        // 予約情報をAPIで削除する
        restTemplate.delete(reserveURI + id, ReserveData.class);
        
        // 予約情報詳細をAPIで削除する
        restTemplate.delete(reserveDetailURI + id, ReserveDetailData.class);
        
        // 予約情報リストをAPIで取得する
        model.addAttribute("reserveList", getReserveList());
        
        return "ReserveInfoList";
    }
    
    /**
     * ユーザー情報リストを取得
     * @return ユーザー情報（UserData）のリスト
     */
    private List<UserData> getUserList(){
        
        // ユーザー情報リストをAPIで取得する
        ResponseEntity<List<UserData>> response = restTemplate.exchange(
            userURI, HttpMethod.GET, null, new ParameterizedTypeReference<List<UserData>>() {});
        List<UserData> userList = response.getBody();
        // 各ユーザー情報を編集し、Modelに設定する
        for(UserData userData : userList){
            // 名前をデコード
            userData.setUserName(decode(userData.getUserName()));
        }
        return userList;
    }
    
    /**
     * 商品情報リストを取得
     * @return 商品情報（ItemData）のリスト
     */
    private List<ItemData> getItemList() {
        ResponseEntity<List<ItemData>> responseItem = restTemplate.exchange(
                itemURI, HttpMethod.GET, null, new ParameterizedTypeReference<List<ItemData>>() {});
        List<ItemData> itemList = responseItem.getBody();
        
        // 先頭行に空行を追加
        itemList.add(0, new ItemData());
        
        // 各予約情報を編集し、Modelに設定する
        for(ItemData itemData : itemList){
            // 名前をデコード
            itemData.setItemName(decode(itemData.getItemName()));
        }
        return itemList;
    }
    
    /**
     * 予約情報リストを取得
     * @return 予約情報（ReserveData）のリスト
     */
    private List<ReserveData> getReserveList() {
        ResponseEntity<List<ReserveData>> response = restTemplate.exchange(
                reserveURI, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<ReserveData>>() {});
        List<ReserveData> reserveList = response.getBody();
        
        return reserveList;
    }
    
    /**
     * 予約情報詳細リストを取得
     * @return 予約情報詳細（ReserveDetailData）のリスト
     */
    private ArrayList<ReserveDetailData> getReserveDetailList(int reserveId){
        ResponseEntity<List<ReserveDetailData>> response = restTemplate.exchange(
                reserveDetailURI + reserveId, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<ReserveDetailData>>() {});
        ArrayList<ReserveDetailData> reserveDetailDataList = (ArrayList<ReserveDetailData>) response.getBody();
        
        return reserveDetailDataList;
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
    
    /**
     * ローカル日時（yyyy/MM/dd HH:mm）を取得
     * @RETURN ローカル日時
     */
    private String getDataTime() {
       // 現在日時情報で初期化されたインスタンスの取得
       LocalDateTime nowDateTime = LocalDateTime.now(); 
       DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
       
       // 日時情報を指定フォーマットの文字列で取得
       String dataTime = nowDateTime.format(format);
       return dataTime;
    }
    
    // ②「追加」ボタン押下
    @PutMapping(value = {"/reserve/create", "/reserve/update"}, params = "add")
    public String addList(Model model, @ModelAttribute ReserveData reserveData) {
    
        //　ユーザー情報リストをセット
        model.addAttribute("userlist", getUserList());		
    
        // 商品情報リストをAPIで取得する
        model.addAttribute("selectItems", getItemList());
        
        // リスト最後尾に1行追加
        reserveData.addList(reserveData.getReserveId());
        model.addAttribute("reserveData", reserveData);
        
        String rtn = "ReserveNewEdit";
        if (reserveData.getReserveId() > 0) {
            rtn = "ReserveEdit";
        }
        return rtn;
    }
 
    // ③「削除」ボタン押下
    @PutMapping(value = {"/reserve/create", "/reserve/update"}, params = "remove")
    public String removeList(Model model, @ModelAttribute ReserveData reserveData, HttpServletRequest request) {
    
        //　ユーザー情報リストをセット
        model.addAttribute("userlist", getUserList());		
    
        // 商品情報リストをAPIで取得する
        model.addAttribute("selectItems", getItemList());
        
        // 行番号を指定して削除
        int index = Integer.valueOf(request.getParameter("remove"));
        reserveData.removeList(index);
        
        String rtn = "ReserveNewEdit";
        if (reserveData.getReserveId() > 0) {
            rtn = "ReserveEdit";
        }
        return rtn;
    }
}
    