package cn.edu.njnu.tools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangzhi on 16-1-12.
 * 用于封装数据上传至服务器端
 */
public class PostDataHelper {

    //记录各个地点所对应的内容信息
    Map<String, JSONObject> postMap = new ConcurrentHashMap<>();

    //数据上传日志
    List<Pair<Date, Boolean>> postLog = new CopyOnWriteArrayList<>();

    protected Map<String, String> placeToPid;

    public PostDataHelper(Map<String, String> placeToPid) {
        this.placeToPid = placeToPid;
    }

    /**
     * 往指定JSONObject里添加一个内容项
     *
     * @param pid  地点对应的pid
     * @param data 待添加的内容项
     */
    public void addData(String pid, JSONArray data) {
        ReentrantLock lock = new ReentrantLock();
        if (postMap.containsKey(pid)) {
            try {
                lock.lock();
                JSONObject json = postMap.get(pid);
                JSONArray array = json.getJSONArray("acs");
                for (int i = 0; i < data.length(); i++)
                    array.put(data.get(i));
                //数据量大于30则触发批量上传
                if (array.length() > 30)
                    post(pid);
            } finally {
                lock.unlock();
            }
        } else {
            JSONObject json = new JSONObject();
            json.put("acs", data);
            postMap.put(pid, json);
        }
    }

    /**
     * 打印日志
     */
    public void printLog() {
        System.out.println(postLog);
    }

    protected void post(String pid) {
        JSONObject data = postMap.get(pid);
        postMap.remove(pid);
        new Thread(() -> {
            postContent(data);
        });
    }

    /**
     * 批量上传数据
     */
    public void post() {
        Set<String> keySet = postMap.keySet();
        for (String pid : keySet) {
            postContent(postMap.get(pid));
        }
    }

    /**
     * 上传数据的三个方法
     */

    public String postIncubator(String place) {
        String data = placeToPid.get(place);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost method = new HttpPost(new ParameterHelper().getPostPlaceURL());
            //生成参数对
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("data", data));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            method.setEntity(entity);

            //请求post
            HttpResponse result = httpClient.execute(method);
            String resData = EntityUtils.toString(result.getEntity());
            //获得结果
            JSONObject resJson = JSONObject.fromObject(resData);
            if (resJson.getInt("code") == 1) {
                JSONObject result2 = resJson.getJSONObject("data");
                if (result2.getInt("status") == 1) {
                    String pid = result2.getString("pid");
                    placeToPid.put(place, pid);
                    return pid;
                } else
                    return null;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String postActivity(JSONObject data) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost method = new HttpPost(new ParameterHelper().getPostPlaceURL());
            //生成参数对
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("data", data.toString()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            method.setEntity(entity);

            //请求post
            HttpResponse result = httpClient.execute(method);
            String resData = EntityUtils.toString(result.getEntity());
            //获得结果
            JSONObject resJson = JSONObject.fromObject(resData);
            if (resJson.getInt("code") == 1) {
                JSONObject result2 = resJson.getJSONObject("data");
                if (result2.getInt("status") == 1) {
                    return result2.getString("pid");
                } else
                    return null;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean postContent(JSONObject data) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost method = new HttpPost(new ParameterHelper().getPostDataURL());
            //生成参数对
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("data", data.toString()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            method.setEntity(entity);

            //请求post
            HttpResponse result = httpClient.execute(method);
            String resData = EntityUtils.toString(result.getEntity());
            //获得结果
            JSONObject resJson = JSONObject.fromObject(resData);
            if (resJson.getInt("code") == 1) {
                JSONObject result2 = resJson.getJSONObject("data");
                return result2.getInt("status") == 1;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
