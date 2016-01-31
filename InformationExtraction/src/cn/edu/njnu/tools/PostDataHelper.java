package cn.edu.njnu.tools;

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

/**
 * Created by zhangzhi on 16-1-12.
 * 用于封装数据上传至服务器端
 */
public class PostDataHelper {

    protected Map<String, String> placeToPid;

    public PostDataHelper(Map<String, String> placeToPid) {
        this.placeToPid = placeToPid;
    }

    /**
     * 上传数据的三个方法
     */

    public String postIncubator(String jsonString) throws IOException {
        CloseableHttpClient httpClient = null;
        HttpPost method = null;
        HttpResponse result = null;
        try {
            httpClient = HttpClients.createDefault();
            method = new HttpPost(new ParameterHelper().getPostPlaceURL());
            //生成参数对
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("data", jsonString));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            method.setEntity(entity);

            //请求post
            result = httpClient.execute(method);
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
        } finally {
            if (method != null)
                method.releaseConnection();
            if (httpClient != null)
                httpClient.close();
        }
    }

    public String postActivity(JSONObject data) throws IOException {
        CloseableHttpClient httpClient = null;
        HttpPost method = null;
        HttpResponse result = null;
        try {
            httpClient = HttpClients.createDefault();
            method = new HttpPost(new ParameterHelper().getPostPlaceURL());
            //生成参数对
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("data", data.toString()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            method.setEntity(entity);

            //请求post
            result = httpClient.execute(method);
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
        } finally {
            if (method != null)
                method.releaseConnection();
            if (httpClient != null)
                httpClient.close();
        }
    }

    public boolean postContent(JSONObject data) throws IOException {
        CloseableHttpClient httpClient = null;
        HttpPost method = null;
        HttpResponse result = null;
        try {
            httpClient = HttpClients.createDefault();
            method = new HttpPost(new ParameterHelper().getPostDataURL());
            //生成参数对
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("data", data.toString()));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            method.setEntity(entity);

            //请求post
            result = httpClient.execute(method);
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
        } finally {
            if (method != null)
                method.releaseConnection();
            if (httpClient != null)
                httpClient.close();
        }
    }

}
