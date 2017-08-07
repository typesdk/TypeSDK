
package com.type.sdk.android.qihoo;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * QihooUserInfo，是应用服务器请求360服务器得到的360用户信息数据。
 */
public class QihooUserInfo {

    private String id; // 360用户ID，缺省返回。

    private String name; // 360用户名，缺省返回。

    private String avatar; // 360用户头像url，缺省返回。

    private String sex; // 360用户性别，仅在fields中包含时候才返回，返回值为：男，女或者未知。

    private String area; // 360用户地区，仅在fields中包含时候才返回。

    private String nick; // 360用户昵称，无值时候返回空。

    public static QihooUserInfo parseJson(String jsonString) {
        QihooUserInfo userInfo = null;
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObj = new JSONObject(jsonString);
                String status = jsonObj.getString("status");
                JSONObject dataJsonObj = jsonObj.getJSONObject("data");
                if (status != null && status.equals("ok")) {
                    // 必返回项
                    String id = dataJsonObj.getString("id");
                    String name = dataJsonObj.getString("name");
                    String avatar = dataJsonObj.getString("avatar");

                    userInfo = new QihooUserInfo();
                    userInfo.setId(id);
                    userInfo.setName(name);
                    userInfo.setAvatar(avatar);

                    // 非必返回项
                    if (dataJsonObj.has("sex")) {
                        String sex = dataJsonObj.getString("sex");
                        userInfo.setSex(sex);
                    }

                    if (dataJsonObj.has("area")) {
                        String area = dataJsonObj.getString("area");

                        userInfo.setArea(area);
                    }

                    if (dataJsonObj.has("nick")) {
                        String nick = dataJsonObj.getString("nick");
                        userInfo.setNick(nick);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return userInfo;
    }

    public static QihooUserInfo parseUserInfo(JSONObject joInfo) {
        QihooUserInfo userInfo = null;
        if (joInfo != null) {
            try {
                // 必须返回
                String name = joInfo.getString("name");
                String avatar = joInfo.getString("avatar");

                userInfo = new QihooUserInfo();
                userInfo.setName(name);
                userInfo.setAvatar(avatar);

                // 非必返回项
                if (joInfo.has("sex")) {
                    String sex = joInfo.getString("sex");
                    userInfo.setSex(sex);
                }

                if (joInfo.has("area")) {
                    String area = joInfo.getString("area");

                    userInfo.setArea(area);
                }

                if (joInfo.has("nick")) {
                    String nick = joInfo.getString("nick");
                    userInfo.setNick(nick);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userInfo;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

}
