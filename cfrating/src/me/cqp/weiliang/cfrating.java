package me.cqp.weiliang;

import javax.swing.JOptionPane;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;


import com.sobte.cqp.jcq.message.CQCode;
import com.sobte.cqp.jcq.message.CoolQCode;
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
import jdk.nashorn.internal.parser.JSONParser;
import jdk.nashorn.internal.runtime.JSONFunctions;

class User implements Comparable,Serializable {
    Long QQ;
    String cf_id;
    int rate;

    public User(Long QQ, String cf_id) {
        this.QQ = QQ;
        this.cf_id = cf_id;
        this.rate = 0;
    }

    public Long getQQ() {
        return QQ;
    }

    public void setQQ(Long QQ) {
        this.QQ = QQ;
    }

    public String getCf_id() {
        return cf_id;
    }

    public void setCf_id(String cf_id) {
        this.cf_id = cf_id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return QQ.equals(user.QQ);
    }

    @Override
    public int compareTo(Object o) {
        User user = (User) o;
        if(rate == ((User) o).rate)
        {
            if(QQ == ((User) o).QQ) return 0;
            if(QQ > ((User) o).QQ) return 1;
            return -1;
        }
        if(rate > ((User) o).rate) return -1;
        return 1;
    }
}


public class cfrating extends JcqAppAbstract implements ICQVer, IMsg, IRequest {
    Vector<User> cf_user = new Vector<>();
    Set<Long> hidden_user = new TreeSet<>();
    Set<Long> not_candidate = new TreeSet<>();
    Map<Long,Integer> rate_change = new TreeMap<>();
    public static void main(String[] args) {
        // CQ??????????????????????????????JCQ??????????????????????????????????????????????????????????????????CQDebug???????????????
        CQ = new CQDebug();//new CQDebug("????????????","????????????") ?????????????????????????????????????????????
        CQ.logInfo("[JCQ] TEST Demo", "????????????");// ??????????????????CQ???????????????????????????????????????
        // ????????????????????????????????????????????????
        cfrating demo = new cfrating();
        Map<String,String> mp = new TreeMap<>();
        Random rand = new Random();
        Long qq;
        CQCode tem = new CQCode();
        try {
            CQ.logInfo("photo",tem.imageUseGet("https://userpic.codeforces.com/917008/title/b010be1171671242.jpg"));
        } catch (Exception e)
        {
            CQ.logInfo("photo",e.toString());
        }
        String json = doGet("https://codeforces.com/api/user.rating", "handle=coder_tq");
        int old_rating = JSON.parseObject(json).getJSONArray("result").getJSONObject(JSON.parseObject(json).getJSONArray("result").size()-1).getIntValue("oldRating");
        CQ.logInfo("old",""+old_rating);
        CQ.logInfo("json",""+ JSON.parseObject(doGet("https://codeforces.ml/api/user.info", "handles=" + "coder_tq")).getJSONArray("result").getJSONObject(0).getIntValue("rating"));
        CQ.logInfo("json",doGet("https://codeforces.ml/api/user.info","handles="+"coder_tq"));
        // ????????????????????????????????????,??????JCQ?????????????????????????????????

    }
    public String appInfo() {
        String AppID = "me.cqp.weiliang.cfrating";
        return CQAPIVER + "," + AppID;
    }
    public int startup() {
        this.appDirectory = CQ.getAppDirectory();
        //????????????
        load();

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
    //POST??????
    public static String doPost(String url, Map<String, String> param) {
        // ??????Httpclient??????
        HttpClientBuilder builder = HttpClients.custom();
        builder.setUserAgent("Mozilla/5.0(Windows;U;Windows NT 5.1;en-US;rv:0.9.4)");
        CloseableHttpClient httpClient = builder.build();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // ??????Http Post??????
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("token","BA7fR5upumlP3zEJtyBtMnPNit6DPjZ8rQEjFPAZmDqI5x6LtU3fwNxkFLv5");
            // ??????????????????
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // ????????????
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
            }
            // ??????http??????
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
    //GET??????
    public static String doGet(String url, String param) {

        // ??????Httpclient??????

        HttpClientBuilder builder = HttpClients.custom();
        builder.setUserAgent("Mozilla/5.0(Windows;U;Windows NT 5.1;en-US;rv:0.9.4)");
        CloseableHttpClient httpClient = builder.build();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // ??????Http Get??????
           HttpGet httpGet = new HttpGet(url+"?"+param);
            // ??????http??????
            response = httpClient.execute(httpGet);
            //CQ.logInfo("????????????",""+response.getCode());
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

    public static JSONObject parseJson(String jsonstring)
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
    public void save()
    {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"cf_user.txt"));
            oos.writeObject(cf_user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"hidden_user.txt"));
            oos.writeObject(hidden_user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"not_candidate.txt"));
            oos.writeObject(not_candidate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.appDirectory+"rate_change.txt"));
            oos.writeObject(rate_change);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void load()
    {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"cf_user.txt"));
            cf_user = (Vector<User>) ois.readObject();
        } catch (Exception e) {
            CQ.logInfo("load",e.toString());
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"hidden_user.txt"));
            hidden_user = (TreeSet<Long>) ois.readObject();
        } catch (Exception e) {
            CQ.logInfo("load",e.toString());
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"not_candidate.txt"));
            not_candidate = (TreeSet<Long>) ois.readObject();
        } catch (Exception e) {
            CQ.logInfo("load",e.toString());
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.appDirectory+"rate_change.txt"));
            rate_change = (TreeMap<Long,Integer>) ois.readObject();
        } catch (Exception e) {
            CQ.logInfo("load",e.toString());
        }
    }

    public int privateMsg(int subType, int msgId, long fromQQ, String msg, int font) {
        // ??????????????????
        if(fromQQ == 2023443884||fromQQ == 909413805)
        {
            try {
                if(msg.length()>4&&msg.substring(0,4).equals("????????????"))
                {
                    String[] tem = msg.split(" ");
                    not_candidate.add(Long.parseLong(tem[1]));
                    save();
                    CQ.sendPrivateMsg(fromQQ,"??????");
                }
            }//??????
            catch (Exception e)
            {
                CQ.sendPrivateMsg(fromQQ,"??????");
            }
            try {
                if(msg.length()>4&&msg.substring(0,4).equals("????????????"))
                {
                    String[] tem = msg.split(" ");
                    not_candidate.remove(Long.parseLong(tem[1]));
                    save();
                    CQ.sendPrivateMsg(fromQQ,"??????");
                }
            }//????????????
            catch (Exception e)
            {
                CQ.sendPrivateMsg(fromQQ,"??????");
            }
            try {
                if(msg.length()>2&&msg.substring(0,2).equals("??????"))
                {
                    String[] tem = msg.split(" ");
                    hidden_user.add(Long.parseLong(tem[1]));
                    save();
                    CQ.sendPrivateMsg(fromQQ,"????????????");
                }
            }//??????
            catch (Exception e)
            {
                CQ.sendPrivateMsg(fromQQ,"????????????");
            }
            try {
            if(msg.length()>4&&msg.substring(0,4).equals("????????????"))
            {
                String[] tem = msg.split(" ");
                hidden_user.remove(Long.parseLong(tem[1]));
                save();
                CQ.sendPrivateMsg(fromQQ,"??????????????????");
            }
            }//????????????
            catch (Exception e)
            {
                CQ.sendPrivateMsg(fromQQ,"??????????????????");
            }
            try {
                if(msg.length()>2&&msg.substring(0,2).equals("??????"))
                {
                    String[] tem = msg.split(" ");
                    for(User i : cf_user)
                    {
                        if(i.QQ == Long.parseLong(tem[1]))
                        {
                            cf_user.remove(i);
                            break;
                        }
                    }
                    cf_user.add(new User(Long.parseLong(tem[1]),tem[2]));
                    CQ.sendPrivateMsg(fromQQ,"????????????");
                    save();
                }
            }//??????
            catch (Exception e)
            {
                CQ.sendPrivateMsg(fromQQ,"????????????");
            }
            try {
                if(msg.length()>4&&msg.substring(0,4).equals("????????????"))
                {
                    String[] tem = msg.split(" ");
                    for(User i : cf_user)
                    {
                        if(i.QQ == Long.parseLong(tem[1]))
                        {
                            cf_user.remove(i);
                            break;
                        }
                    }
                    CQ.sendPrivateMsg(fromQQ,"??????????????????");
                    save();
                }
            }//????????????
            catch (Exception e)
            {
                CQ.sendPrivateMsg(fromQQ,"??????????????????");
                //CQ.sendPrivateMsg(fromQQ,e.toString());
            }

            try {
                if(msg.length()>=6&&msg.substring(0,6).equals("??????????????????"))
                {
                    rate_change.clear();
                    int con = 0;
                    for(User i : cf_user)
                    {
                        CQ.sendPrivateMsg(fromQQ,"???????????????"+con++ +"/"+cf_user.size());
                        try {
                            String json = doGet("https://codeforces.com/api/user.info", "handles=" + i.cf_id);
                            i.setRate(JSON.parseObject(json).getJSONArray("result").getJSONObject(0).getIntValue("rating"));
                            json = doGet("https://codeforces.com/api/user.rating", "handle=" + i.cf_id);
                            int old_rating = JSON.parseObject(json).getJSONArray("result").getJSONObject(JSON.parseObject(json).getJSONArray("result").size()-1).getIntValue("oldRating");
                            rate_change.put(i.QQ,old_rating);
                            //CQ.sendPrivateMsg(fromQQ,""+old_rating+"test msg");
                            CQ.logInfo("JSON",json);
                        }
                        catch (Exception e)
                        {
                            CQ.sendPrivateMsg(fromQQ,e.toString());
                            i.setRate(-999);
                        }
                    }
                    CQ.sendPrivateMsg(fromQQ,"????????????");
                    save();
                }
            }//??????????????????
            catch (Exception e)
            {
                CQ.sendPrivateMsg(fromQQ,e.toString());
            }
        }
        try {
            if(msg.length()>=6&&msg.substring(0,6).equals("??????????????????"))
            {
                load();
                Collections.sort(cf_user);
                int con = 1;
                int rank = 1;
                String send_msg = "???????????????cf????????????\n";
                for(User i : cf_user)
                {
                    String real_rank = "("+rank++ +")";
                    if(not_candidate.contains(i.QQ)) {
                        real_rank = "(*)";
                        rank--;
                    }
                    String cf_id = i.cf_id;
                    if(hidden_user.contains(i.QQ)&&fromQQ!=2023443884&&fromQQ!=909413805) cf_id = "***";
                    if(hidden_user.contains(i.QQ)) cf_id += "??????????????????";
                    //send_msg += "??????: "+CQ.getStrangerInfo(i.QQ).getNick()+" QQ: "+ i.QQ + " cf_id: " + i.cf_id + " cf_rate: " + i.rate + "\n\n" ;
                    send_msg += con++ + real_rank + "\n    ??????: "+ CQ.getStrangerInfo(i.getQQ()).getNick() +"\n    QQ: "+ i.QQ + "\n    cf_id: " + cf_id + "\n    cf_rate: " + i.rate + "\n    cf_rate_change: " + (i.rate-rate_change.get(i.QQ)) + "\n";
                }
                CQ.sendPrivateMsg(fromQQ,send_msg);
            }
        }//??????????????????
        catch (Exception e)
        {
            CQ.sendPrivateMsg(fromQQ,e.toString());
        }
        return MSG_IGNORE;
    }
    public int groupMsg(int subType, int msgId, long fromGroup, long fromQQ, String fromAnonymous, String msg,
                        int font) {
        if(fromQQ == 80000000L)
        {
            return MSG_IGNORE;
        }
        try {
            if(msg.equals("??????????????????"))
            {
                load();
                Collections.sort(cf_user);
                int con = 1;
                int rank = 1;
                String send_msg = "???????????????cf????????????\n";
                for(User i : cf_user)
                {
                    if(rank > 16) break;
                    String cf_id = i.cf_id;
                    String real_rank = "("+rank++ +")";
                    if(not_candidate.contains(i.QQ)) {
                        real_rank = "(*)";
                        rank--;
                    }
                    if(hidden_user.contains(i.QQ)) cf_id = "***";
                    if(hidden_user.contains(i.QQ)) cf_id += "??????????????????";
                    //send_msg += "??????: "+CQ.getStrangerInfo(i.QQ).getNick()+" QQ: "+ i.QQ + " cf_id: " + i.cf_id + " cf_rate: " + i.rate + "\n\n" ;
                    if(CQ.getGroupMemberInfo(fromGroup,i.getQQ())!=null&&!CQ.getGroupMemberInfo(fromGroup,i.getQQ()).getCard().equals("")&&CQ.getGroupMemberInfo(fromGroup,i.getQQ()).getCard()!=null)
                        send_msg += con++ + real_rank + "\n    " + CQ.getGroupMemberInfo(fromGroup,i.getQQ()).getCard() + "\n    " + cf_id + "\n    " + i.rate + "(" + ((i.rate-rate_change.get(i.QQ))>0?"+":"") + (i.rate-rate_change.get(i.QQ)) + ")\n";
                    else send_msg += con++ + real_rank + "\n    " + CQ.getStrangerInfo(i.getQQ()).getNick() + "\n    " + cf_id + "\n    " + i.rate + "(" + ((i.rate-rate_change.get(i.QQ))>0?"+":"") + (i.rate-rate_change.get(i.QQ)) + ")\n" ;
                }
                CQ.sendGroupMsg(fromGroup,send_msg);
            }
        }
        catch (Exception e)
        {
            CQ.sendPrivateMsg(fromQQ,e.toString());
        }
        try {
            if(msg.equals("??????????????????"))
            {
                if(CQ.getGroupMemberInfo(fromGroup,fromQQ).getAuthority() == 1) return MSG_IGNORE;
                rate_change.clear();
                int con = 0;
                for(User i : cf_user)
                {
                    CQ.sendPrivateMsg(fromQQ,"???????????????"+con++ +"/"+cf_user.size());
                    try {
                        String json = doGet("https://codeforces.com/api/user.info", "handles=" + i.cf_id);
                        i.setRate(JSON.parseObject(json).getJSONArray("result").getJSONObject(0).getIntValue("rating"));
                        json = doGet("https://codeforces.com/api/user.rating", "handle=" + i.cf_id);
                        int old_rating = JSON.parseObject(json).getJSONArray("result").getJSONObject(JSON.parseObject(json).getJSONArray("result").size()-1).getIntValue("oldRating");
                        rate_change.put(i.QQ,old_rating);
                        CQ.logInfo("JSON",json);
                    }
                    catch (Exception e)
                    {
                        i.setRate(-999);
                    }
                }
                CQ.sendPrivateMsg(fromQQ,"????????????");
                CQ.sendGroupMsg(fromGroup,"????????????");
                save();
            }
        }
        catch (Exception e)
        {
            //CQ.sendPrivateMsg(fromQQ,"??????");
        }
        return MSG_IGNORE;
    }
    public int discussMsg(int subtype, int msgId, long fromDiscuss, long fromQQ, String msg, int font) {
        // ??????????????????
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