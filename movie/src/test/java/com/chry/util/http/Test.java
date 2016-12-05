package com.chry.util.http;

import java.net.URLEncoder;

public class Test {
	public static void main(String[] agrs) {
        try {
            String responseBody;
    	  String addr = "280 Freeway at Pennsylvania and Mariposa Streets";
  		  String sUrl = "https://maps.google.com/maps/api/geocode/json?address='" + URLEncoder.encode(addr,"utf-8") + 
  					"'&language=zh-CN&sensor=false&key=AIzaSyDTCFArv3QUQJhI3MijeEeXgLa6rsEsMgs";
          responseBody = HttpUtil.accessUrl(sUrl, "127.0.0.1", 7359);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            System.out.println("----------------------------------------");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
