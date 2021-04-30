package me.cqp.weiliang;

import javax.swing.JOptionPane;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;


import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

//import org.json.JSONArray;
//import org.json.JSONObject;
import com.alibaba.fastjson.*;
import com.sobte.cqp.jcq.entity.Anonymous;
import com.sobte.cqp.jcq.entity.CQDebug;
import com.sobte.cqp.jcq.entity.GroupFile;
import com.sobte.cqp.jcq.entity.ICQVer;
import com.sobte.cqp.jcq.entity.IMsg;
import com.sobte.cqp.jcq.entity.IRequest;
import com.sobte.cqp.jcq.event.JcqAppAbstract;

public class ctzs extends JcqAppAbstract implements ICQVer, IMsg, IRequest {
    String pix = "";
    String suf = "";
    String failq = "";
    String faila = "";
    String appDirectory;
    int default_tk = 1;
    Set<Integer> all_banned_interface = new TreeSet<>();
    Set<Integer> discuss_banned_interface = new TreeSet<>();
    Map<Long,Integer> user_tk_select = new TreeMap<>();

    public static void main(String[] args) {
        // CQ此变量为特殊变量，在JCQ启动时实例化赋值给每个插件，而在测试中可以用CQDebug类来代替他
        CQ = new CQDebug();//new CQDebug("应用目录","应用名称") 可以用此构造器初始化应用的目录
        CQ.logInfo("[JCQ] TEST Demo", "测试启动");// 现在就可以用CQ变量来执行任何想要的操作了
        // 要测试主类就先实例化一个主类对象
        ctzs demo = new ctzs();
        // 下面对主类进行各方法测试,按照JCQ运行过程，模拟实际情况
        demo.startup();// 程序运行开始 调用应用初始化方法
        demo.enable();// 程序初始化完成后，启用应用，让应用正常工作
        // 依次类推，可以根据实际情况修改参数，和方法测试效果
        Random r = new Random();
        CQ.logInfo("查题测试",demo.chati("啊",1,909413805,1));
        // 以下是收尾触发函数
        // demo.disable();// 实际过程中程序结束不会触发disable，只有用户关闭了此插件才会触发
        demo.exit();// 最后程序运行结束，调用exit方法
    }
    public String appInfo() {
        String AppID = "me.cqp.weiliang.ctzs";
        return CQAPIVER + "," + AppID;
    }
    public int startup() {
        this.appDirectory = CQ.getAppDirectory();
        //读取前缀
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"pix.txt"));
            pix = (String) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"suf.txt"));
            suf = (String) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"faila.txt"));
            faila = (String) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"failq.txt"));
            failq = (String) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"user_cur_tk.txt"));
            user_tk_select = (TreeMap<Long,Integer>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"all_banned_interface.txt"));
            all_banned_interface = (Set<Integer>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"discuss_banned_interface.txt"));
            discuss_banned_interface = (Set<Integer>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    public int exit() {
        return 0;
    }
    public int enable() {
        enable = true;
        return 0;
    }
    public int disable() {
        enable = false;
        return 0;
    }
    //POST请求
    public static String doPost(String url, Map<String, String> param) {
        // 创建Httpclient对象
        HttpClientBuilder builder = HttpClients.custom();
        builder.setUserAgent("Mozilla/5.0(Windows;U;Windows NT 5.1;en-US;rv:0.9.4)");
        CloseableHttpClient httpClient = builder.build();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Authorization","tpIWssepTQBcpVBm");
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }
    //GET请求
    public static String doGet(String url, String param) {

        // 创建Httpclient对象

        HttpClientBuilder builder = HttpClients.custom();
        builder.setUserAgent("Mozilla/5.0(Windows;U;Windows NT 5.1;en-US;rv:0.9.4)");
        CloseableHttpClient httpClient = builder.build();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Get请求
           HttpGet httpGet = new HttpGet(url+"?"+param);
            // 执行http请求
            response = httpClient.execute(httpGet);
            //CQ.logInfo("响应状态",""+response.getCode());
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return resultString;
    }

    public JSONObject parseJson(String jsonstring)
    {
        JSONObject returnObject = null;
        try{
            returnObject = JSON.parseObject(jsonstring);
        } catch (Exception e)
        {
            returnObject = JSON.parseObject("{\"success\":\"false\"}");
        }
        return returnObject;
    }

    public JSONObject Interface0(String q)
    {
//        try {
//            q = URLEncoder.encode(q,"utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        Map<String,String> param = new TreeMap<>();
        param.put("question",q);
        //CQ.logInfo("url","http://panel.52wfy.cn/searchtk.php?" + "q="+q);
        //String s = "";
        String s= doPost("http://cx.icodef.com/wyn-nb", param);
        CQ.logInfo("测试返回JSON",s);
        return parseJson(s);
    }
    public JSONObject Interface2(String q)
    {
        Map<String,String> param = new TreeMap<>();
        param.put("keyword",q);
        param.put("token","BA7fR5upumlP3zEJtyBtMnPNit6DPjZ8rQEjFPAZmDqI5x6LtU3fwNxkFLv5");
        String s= doPost("https://app.51xuexiaoyi.com/api/v1/searchQuestion", param);
        CQ.logInfo("测试返回JSON",s);
        return parseJson(s);
    }
    public JSONObject Interface1(String q)
    {
        Map<String,String> param = new TreeMap<>();
        param.put("content",q);
        String s= doPost("http://129.204.175.209/cha_xin.php", param);
        CQ.logInfo("测试返回JSON",s);
        return parseJson(s);
    }

    public void msgSend(int msgType,long from,String msg)
    {
        switch (msgType)
        {
            case 1: CQ.sendPrivateMsg(from,msg);break;
            case 2: CQ.sendGroupMsg(from,msg);break;
            case 3: CQ.sendDiscussMsg(from,msg);break;
        }
    }

    public void xxyMethod(JSONObject o,int msgType,long from) //学小易
    {
        try{
            JSONArray arr = o.getJSONArray("data");
            for(int i = 0; i < arr.size(); i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                msgSend(msgType,from,pix+"问题： " + obj.getString("q") + "\n" + "答案： " + obj.getString("a")+suf);
            }
        }catch (Exception e) {
            msgSend(msgType, from, "请至少输入6个字，若您输入符合要求，换个题库试试叭");
        }
    }

    public String chati(String msg,int msgType,long from,int curtk)//msgType 1=Private 2=Group 3=Discuss
    {
        String returnmsg = "";
        JSONObject tiku = null;
        String answer = "";
        String question = msg;
        //禁用题库
        if(all_banned_interface.contains(curtk)) return "该题库不知道什么原因被管理员禁止了，换个题库试试叭，更换题库方法见空间！";
        if((msgType == 2||msgType == 3)&&discuss_banned_interface.contains(curtk)) return "该题库禁止在群内使用，私聊机器人查询或者换个题库试试叭！";
            tiku = Interface0(msg);
            answer = tiku.getString("data");

        try {
            if(question.equals("")||question.contains("无此题"))
            {
                returnmsg="问题： " + failq + "\n\n" + "答案： " + faila;
            }
            else
                returnmsg="问题： " + question + "\n\n" + "答案： " + answer;
        }
        catch (Exception e)
        {
            returnmsg="问题： " + failq + "\n" + "答案： " + faila;
        }
        returnmsg = returnmsg.replace("\\n"," ");
        return pix+returnmsg+suf;
    }

    public void save()
    {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"pix.txt"));
            oos.writeObject(pix);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"suf.txt"));
            oos.writeObject(suf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"faila.txt"));
            oos.writeObject(faila);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"failq.txt"));
            oos.writeObject(failq);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"user_cur_tk.txt"));
            oos.writeObject(user_tk_select);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"all_banned_interface.txt"));
            oos.writeObject(all_banned_interface);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"discuss_banned_interface.txt"));
            oos.writeObject(discuss_banned_interface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public int privateMsg(int subType, int msgId, long fromQQ, String msg, int font) {
        // 这里处理消息
        if(msg.length() >= 6&&msg.substring(0,6).equals("修改查题接口"))
        {
            try
            {
                int curtk = Integer.parseInt(msg.substring(6,msg.length()));
                if(curtk <= 0) Integer.parseInt("a");
                CQ.sendPrivateMsg(fromQQ,"成功修改查题接口为 ： " + curtk);
                user_tk_select.remove(fromQQ);
                user_tk_select.put(fromQQ,curtk-1);
            } catch (Exception e)
            {
                CQ.sendPrivateMsg(fromQQ,"修改失败！请检查输入！");
            }
            save();
            return MSG_IGNORE;
        }
        if(msg.equals("查看当前查题接口"))
        {
            user_tk_select.computeIfAbsent(fromQQ, k -> default_tk);
            CQ.sendPrivateMsg(fromQQ,"当前查题接口为 "+(user_tk_select.get(fromQQ)%5+1));
            return MSG_IGNORE;
        }
        if(fromQQ == 909413805)
        {
            if(msg.length() >= 5&&msg.substring(0,5).equals("禁用题库:"))
            {
                all_banned_interface.add(Integer.parseInt(msg.substring(5,msg.length())));
                save();
                CQ.sendPrivateMsg(fromQQ,"success");
                return MSG_IGNORE;
            }
            if(msg.length() >= 7&&msg.substring(0,7).equals("取消禁用题库:"))
            {
                all_banned_interface.remove(Integer.parseInt(msg.substring(7,msg.length())));
                save();
                CQ.sendPrivateMsg(fromQQ,"success");
                return MSG_IGNORE;
            }
            if(msg.length() >= 7&&msg.substring(0,7).equals("禁用群聊题库:"))
            {
                discuss_banned_interface.add(Integer.parseInt(msg.substring(7,msg.length())));
                save();
                CQ.sendPrivateMsg(fromQQ,"success");
                return MSG_IGNORE;
            }
            if(msg.length() >= 9&&msg.substring(0,9).equals("取消禁用群聊题库:"))
            {
                discuss_banned_interface.remove(Integer.parseInt(msg.substring(9,msg.length())));
                save();
                CQ.sendPrivateMsg(fromQQ,"success");
                return MSG_IGNORE;
            }
            if(msg.length() >= 9&&msg.substring(0,9).equals("修改查询失败问题:"))
            {
                failq = msg.substring(9,msg.length());
                save();
                CQ.sendPrivateMsg(fromQQ,"success");
                return MSG_IGNORE;
            }
            if(msg.length() >= 9&&msg.substring(0,9).equals("修改查询失败答案:"))
            {
                faila = msg.substring(9,msg.length());
                save();
                CQ.sendPrivateMsg(fromQQ,"success");
                return MSG_IGNORE;
            }
            if(msg.length() >= 5&&msg.substring(0,5).equals("修改前缀:"))
            {
                pix = msg.substring(5,msg.length());
                save();
                CQ.sendPrivateMsg(fromQQ,"success");
                return MSG_IGNORE;
            }
            if(msg.length() >= 5&&msg.substring(0,5).equals("修改后缀:"))
            {
                suf = msg.substring(5,msg.length());
                save();
                CQ.sendPrivateMsg(fromQQ,"success");
                return MSG_IGNORE;
            }
        }
        user_tk_select.computeIfAbsent(fromQQ, k -> default_tk);
        CQ.sendPrivateMsg(fromQQ,chati(msg,1,fromQQ,user_tk_select.get(fromQQ)));
        return MSG_IGNORE;
    }
    public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg,
                        int font) {
        if(fromQQ == 80000000L)
        {
            return MSG_IGNORE;
        }
        if(msg.length() <= 1) return MSG_IGNORE;
        if(msg.substring(0,1).equals("*"))
        {
            msg = msg.substring(1,msg.length());
            user_tk_select.computeIfAbsent(fromQQ, k -> default_tk);
            CQ.sendGroupMsg(fromGroup,CC.at(fromQQ)+"\n"+chati(msg,2,fromGroup,user_tk_select.get(fromQQ)));
        }
        return MSG_IGNORE;
    }
    public int discussMsg(int subtype, int msgId, long fromDiscuss, long fromQQ, String msg, int font) {
        // 这里处理消息
        if(fromQQ == 80000000L)
        {
            return MSG_IGNORE;
        }
        if(msg.length() <= 1) return MSG_IGNORE;
        if(msg.substring(0,1).equals("*"))
        {
            msg = msg.substring(1,msg.length());
            user_tk_select.computeIfAbsent(fromQQ, k -> default_tk);
            CQ.sendDiscussMsg(fromDiscuss,chati(msg,3,fromDiscuss,user_tk_select.get(fromQQ)));
        }
        return MSG_IGNORE;
    }
    public int groupUpload(int subType, int sendTime, long fromGroup, long fromQQ, String file) {
        return MSG_IGNORE;
    }
    public int groupAdmin(int subtype, int sendTime, long fromGroup, long beingOperateQQ) {
        return MSG_IGNORE;
    }
    public int groupMemberDecrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        return MSG_IGNORE;
    }
    public int groupMemberIncrease(int subtype, int sendTime, long fromGroup, long fromQQ, long beingOperateQQ) {
        return MSG_IGNORE;
    }
    public int friendAdd(int subtype, int sendTime, long fromQQ) {
        return MSG_IGNORE;
    }
    public int requestAddFriend(int subtype, int sendTime, long fromQQ, String msg, String responseFlag) {
        return MSG_IGNORE;
    }
    public int requestAddGroup(int subtype, int sendTime, long fromGroup, long fromQQ, String msg,
                               String responseFlag) {
        return MSG_IGNORE;
    }
}