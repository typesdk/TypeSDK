
package com.type.sdk.android.qihoo;

/**
 * QihooPayInfo，请求360SDK支付接口时的参数信息类。
 */
public class QihooPayInfo {
 // 必需参数，用户access token，要使用注意过期和刷新问题，最大64字符。
    private String accessToken;
    // 必需参数，360账号id，整数。
    private String qihooUserId;

    // 必需参数，应用app key。
    private String appKey;

    // 必需参数，值为md5(app_secret +“#”+
    // app_key)全小写，用于签名的密钥不能把app_secret写到应用客户端程序里因此使用这样一个特殊的KEY，应算出值直接写在app中，而不是写md5的计算过程。
    private String privateKey;

    // 必需参数，所购买商品金额，以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
    private String moneyAmount;

    // 必需参数，人民币与游戏充值币的默认比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
    private String exchangeRate;

    // 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
    private String productName;

    // 必需参数，购买商品的商品id，应用指定，最大16字符。
    private String productId;

    // 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
    private String notifyUri;

    // 必需参数，游戏或应用名称，最大16中文字。
    private String appName;

    // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
    // 充到统一的用户账户，各区服角色均可使用）。
    private String appUserName;

    // 必需参数，应用内的用户id。 若应用内绑定360账号和应用账号, 充值不分区服, 充到统一的用户账户, 各区服角色均可使用,
    // 则可用360用户ID。最大32字符。
    private String appUserId;

    // 可选参数，应用扩展信息1，原样返回，最大255字符。
    private String appExt1;

    // 可选参数，应用扩展信息2，原样返回，最大255字符。
    private String appExt2;

    // 可选参数，应用订单号，应用内必须唯一，最大32字符。
    private String appOrderId;

    // 可选参数，支付类型定制
    private String[] payTypes;

    public String[] getPayTypes() {
        return payTypes;
    }

    public void setPayTypes(String[] payTypes) {
        this.payTypes = payTypes;
    }
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getQihooUserId() {
        return qihooUserId;
    }

    public void setQihooUserId(String qihooUserId) {
        this.qihooUserId = qihooUserId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(String moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppUserName() {
        return appUserName;
    }

    public void setAppUserName(String appUserName) {
        this.appUserName = appUserName;
    }

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNotifyUri() {
        return notifyUri;
    }

    public void setNotifyUri(String notifyUri) {
        this.notifyUri = notifyUri;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getAppExt1() {
        return appExt1;
    }

    public void setAppExt1(String appExt1) {
        this.appExt1 = appExt1;
    }

    public String getAppExt2() {
        return appExt2;
    }

    public void setAppExt2(String appExt2) {
        this.appExt2 = appExt2;
    }

    public String getAppOrderId() {
        return appOrderId;
    }

    public void setAppOrderId(String appOrderId) {
        this.appOrderId = appOrderId;
    }

}
