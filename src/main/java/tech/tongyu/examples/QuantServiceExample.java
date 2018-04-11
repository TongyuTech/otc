package tech.tongyu.examples;

import tech.tongyu.examples.util.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuantServiceExample {
    public static void main(String[] args){
        //create a restful client
        RestClient client = new RestClient("47.100.4.128","quant-service");

        // list all supported apis
        List apis = client.apiList();
        apis.forEach(api -> System.out.println(api));

        //for each api, get their usage
        Map<String,Map<String,Object>> res = (Map<String,Map<String,Object>>)
                apis.parallelStream().collect(Collectors.toMap(api->api,api-> client.get("api/info/excel/"+api)));
        res.forEach((k,v) -> System.out.println(k+":"+v));

        //create a vol surface
        Map<String,Object> param = new HashMap<>();
        param.put("val","2018-06-01");
        param.put("spot",1);
        param.put("vol",0.2);
        param.put("daysInYear",365);
        Map res2 = client.rpc("qlVolSurfaceConstCreate",param);
        String volSurf = (String)res2.get("result");

        //compute implied vol
        param.clear();
        param.put("volSurface",volSurf);
        param.put("forward",1);
        param.put("strike",1);
        param.put("expiry","2018-09-01");
        Map vol = client.rpc("qlVolSurfaceImpliedVolGet",param);
        System.out.println(vol);
    }
}
