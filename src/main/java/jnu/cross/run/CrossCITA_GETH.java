package jnu.cross.run;

import com.alibaba.fastjson.JSONObject;
import com.citahub.cita.protocol.core.DefaultBlockParameter;
import com.citahub.cita.protocol.core.methods.response.AppBlock;
import jnu.cross.Response;
import jnu.cross.config.CITAConfig;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author SDKany
 * @ClassName CrossCITA_GETH
 * @Date 2023/7/30 11:37
 * @Version V1.0
 * @Description
 */
public class CrossCITA_GETH {
    public static void main(String[] args) throws IOException {
        // 读取CITA上的区块，如果参数小于0，则读最新的
        String hexData = readCITA(-1);
        System.out.println("完成从CITA的数据读取，hexData = " + hexData);

        // 将上述数据发送到本地8081端口的submitGeth接口，并获取返回值，返回值为一个JSON String类型的Response
        String responseString = sendToGeth(hexData);
        // 将responseString 解析成Response对象
        Response response = JSONObject.parseObject(responseString, Response.class);
        // 获取transaction的hash值
        String transactionHash = response.getData();
        System.out.println("完成向Geth提交数据，transactionHash = " + transactionHash);

        // 循环查询上述交易是否被写入区块
        while(true){
            // 将transaction的hash值发送到本地8081端口的submitGeth接口，并获取返回值
            String responseString2 = checkState(transactionHash);
            //System.out.println(responseString2);
            // 解析responseString2为Response对象
            Response response2 = JSONObject.parseObject(responseString2, Response.class);
            if (response2.getCode() == 0){ // code为0说明写入了区块链
                System.out.println(response2.getMessage() + ", data = " + response2.getData());
                break; // 退出循环
            }else{
                System.out.println(response2.getMessage());
            }
            try {
                Thread.sleep(3000); // 休眠3秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readCITA(long blockNumber) throws IOException {
        AppBlock.Block block = CITAConfig.citaj.appGetBlockByNumber(blockNumber < 0 ? DefaultBlockParameter.valueOf("latest"): DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNumber)), true).send().getBlock();
        //System.out.println("获取到的data为：" + block.getHash());
        return block.getHash(); // 先暂时返回block的hash了
    }

    public static String sendToGeth(String hexData){
        return httpRequest("http://localhost:8081/cross/submitGeth?hexData=" + hexData);
    }

    public static String checkState(String transHash){
        return httpRequest("http://localhost:8081/cross/readGethTransactionState?transHash=" + transHash);
    }

    public static String httpRequest(String URLString){
        StringBuffer result = new StringBuffer();
        //连接
        HttpURLConnection connection = null;
        OutputStream os = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            //创建连接对象
            URL url = new URL(URLString);
            //System.out.println(url);
            //创建连接
            connection = (HttpURLConnection) url.openConnection();
            //设置请求方法
            connection.setRequestMethod("POST");
            //设置连接超时时间
            connection.setConnectTimeout(15000);
            //设置读取超时时间
            connection.setReadTimeout(15000);
            //DoOutput设置是否向httpUrlConnection输出，DoInput设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
            //设置是否可读取
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            //设置权限
            //设置请求头等
            //开启连接
            //connection.connect();
            //读取响应
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                if (null != is) {
                    br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String temp = null;
                    while (null != (temp = br.readLine())) {
                        result.append(temp);
                        result.append("\r\n");
                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭连接
            connection.disconnect();
        }
        //System.out.println(result);
        return result.toString();
    }
}
