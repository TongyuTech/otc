package tech.tongyu.examples.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tech.tongyu.examples.util.Constants.*;


public class RestClient {
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);
    private static final String restRequestLog = "%s call service: %s with parameters: \n %s \n";
    private static final String restResponseLog = "%s service: %s returned result: \n %s \n";

    public RestClient(String host, String service_name) {
        this.restTemplate = new RestTemplate();
        this.host = host;
        this.port = 16016;
        this.service_name = service_name;
    }

    RestTemplate restTemplate;
    String host;
    int port;
    String service_name;

    public Map<String, Object> postWithAuth(String api, Map<String,Object> data, String token) {
        String res =  template(api,"POST", JsonUtil.toJson(data),token);
        Map resData = JsonUtil.fromJson(res);
        return resData;
    }

    public Map<String, Object> post(String api, Map<String,Object> data) {
        String res =  template(api,"POST", JsonUtil.toJson(data),"");
        Map resData = JsonUtil.fromJson(res);
        return resData;
    }

    public Map<String, Object> rpc(String api, Map<String,Object> data) {
        Map<String,Object> param = new HashMap();
        param.put("method",api);
        param.put("params",data);
        String res =  template("api/rpc","POST", JsonUtil.toJson(param),"");
        Map resData = JsonUtil.fromJson(res);
        return resData;
    }

    public Map<String, Object> getWithAuth(String api, String token) {
        String res =  template(api,"GET", "",token);
        Map resData = JsonUtil.fromJson(res);
        return resData;
    }

    public Map<String, Object> get(String api) {
        String res = template(api,"GET", "","");
        Map resData = JsonUtil.fromJson(res);
        return resData;
    }

    public List<String> apiList() {
        String res = template("api/list","GET", "","");
        String apis = res.replaceAll("\\[","").replaceAll("\\]","").replaceAll("\"","");
        return Arrays.asList(apis.split(","));
    }

    private String template(String api, String requestType, String data, String token) {
        HttpHeaders headers = generateHeader(token);
        HttpEntity<String> entity = new HttpEntity<String>(data, headers);
        ResponseEntity<String> response = null;
        String url = generateURL(api);
        try{
            logger.info(String.format(restRequestLog, FormattedTimestamp.formatTimestampToIso(new Timestamp(System.currentTimeMillis())),
                    service_name, data));
            if(requestType.toUpperCase() == "POST")
                response = restTemplate.postForEntity(url, entity, String.class);
            else if(requestType.toUpperCase() == "GET")
                response = restTemplate.getForEntity(url, String.class);
            String res = response.getBody();
            logger.info(String.format(restResponseLog, FormattedTimestamp.formatTimestampToIso(new Timestamp(System.currentTimeMillis())),
                    service_name, res));
            return res;
        } catch (HttpStatusCodeException e){
            throw new CustomerException(ErrorCode.REMOTE_ERROR, String.format(CALL_REMOTE_SERVER_ERROR, service_name,
                    e.getResponseBodyAsString()));
        }
    }

    private HttpHeaders generateHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add(AUTHORIZATION_DESP, BEARER + token);
        return headers;
    }

    private Map<String, Object> checkRes(Map<String, Object> res, String service_name) {
        if (!res.containsKey(RESULT)){
            if (res.containsKey(ERROR) && (res.get(ERROR) != null) && ((Map<String, Object>)res.get(ERROR)).containsKey(MESSAGE)){
                throw new CustomerException(ErrorCode.REMOTE_ERROR, String.format(CALL_REMOTE_SERVER_ERROR, service_name,
                        (String)((Map<String, Object>)res.get(ERROR)).get(MESSAGE)));
            } else {
                throw new CustomerException(ErrorCode.UNKNOWN, String.format(CALL_REMOTE_SERVER_ERROR, service_name, null));
            }
        }
        return res;
    }

    private String generateURL(String api) {
        StringBuffer sb = new StringBuffer();
        sb.append("http://").append(host).append(":").append(port)
                .append("/").append(service_name).append("/").append(api);
        return sb.toString();
    }
}
