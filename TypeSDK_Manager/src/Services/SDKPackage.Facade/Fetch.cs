using System;
using System.Collections;
using System.Collections.Generic;
using System.Data;
using System.Globalization;
using System.Text;
using System.Web;

using SDKPackage.Entity;
using SDKPackage.Kernel;
using SDKPackage.Utils;

namespace SDKPackage.Facade
{
    /// <summary>
    /// 为网站提供全局服务，如：用户Cookie读写等等
    /// </summary>
    public sealed class Fetch
    {
        #region Fields

        private const string UC_COOKIE_NAME = "flb";                        //Cookie组名称
        private const int UC_FACEIF_DEFAULT = 1;                            //用户头像
        private const string UC_COOKIE_LOGON_TOKEN = "gwLogonToken";        //登录Cookie令牌
        private const string CookieEncryptKey = "yinheshuyin@($^_Alex";          //Cookie加密文本
        //private static ISoapHeaderUC m_soapHeaderUC= null;                //授权配置
        private static List<string> m_protectionQuestiongs = null;          //密保问题        
        private static object lockFetchObj = new object();

        //private static volatile AccountsFacade _accountsData;
        //public static AccountsFacade accountsData
        //{
        //    get
        //    {
        //        if (_accountsData == null)
        //        {
        //            lock (lockFetchObj)
        //            {
        //                if (_accountsData == null)
        //                    _accountsData = new AccountsFacade();
        //            }
        //        }
        //        return _accountsData;
        //    }
        //}

        #endregion

        #region 构造方法

        private Fetch()
        { }

        static Fetch()
        {
            GetProtectionQuestions();
        }

        #endregion

        #region 公开属性

        /// <summary>
        /// 站点标识
        /// </summary>
        public static int StationID
        {
            //get { return Convert.ToInt32(m_soapHeaderUC.StationID); }
            get
            {
                return 1;
            }
        }

        /// <summary>
        /// 网站根路径
        /// </summary>
        public static string WebRoot
        {
            get { return "/"; }
        }

        #endregion

        #region 网站相关

        /// <summary>
        /// 获取验证码数值
        /// </summary>
        /// <returns></returns>
        public static string GetVerifyCode()
        {
            string vcode = "";
            if (HttpContext.Current != null && HttpContext.Current.Session != null)
            {
                vcode = SessionState.Get(Fetch.UC_VERIFY_CODE_KEY) as string;
                //Session 保存，无需解密
                if (!string.IsNullOrEmpty(vcode))
                {
                    return vcode;
                }
            }
            else
            {
                vcode = Utility.GetCookie(Fetch.UC_VERIFY_CODE_KEY);
            }

            if (!String.IsNullOrEmpty(vcode))
            {
                vcode = CWHEncryptNet.XorCrevasse(vcode);
            }

            return vcode;
        }

        #endregion

        #region 用户相关

        ///// <summary>
        ///// 根据用户ID获取用户名
        ///// </summary>
        ///// <param name="userID"></param>
        ///// <returns></returns>
        //public static string GetAccountsByUserID(int userID)
        //{
        //    return accountsData.GetUserBaseInfoByUserID(userID) == null ? "" : accountsData.GetUserBaseInfoByUserID(userID).Accounts;
        //}

        ///// <summary>
        ///// 根据用户ID获取用户昵称
        ///// </summary>
        ///// <param name="userID"></param>
        ///// <returns></returns>
        //public static string GetNickNameByUserID(int userID)
        //{
        //    return accountsData.GetUserBaseInfoByUserID(userID) == null ? "" : accountsData.GetUserBaseInfoByUserID(userID).NickName;
        //}

        /// <summary>
        /// 获取ID号码描述，没分配 显示 “尚未分配”
        /// </summary>
        /// <param name="gameID"></param>
        /// <returns></returns>
        public static string GetGameID(int gameID)
        {
            if (gameID <= 0) return "尚未分配";

            return gameID.ToString();
        }

        /// <summary>
        /// 获取用户标识
        /// </summary>
        /// <param name="strUserID"></param>
        /// <returns></returns>
        public static int GetUserID(string strUserID)
        {
            int dwUserID = TypeParse.StrToInt(CWHEncryptNet.XorCrevasse(strUserID), 0);
            return dwUserID;
        }

        /// <summary>
        /// 获取加密后的用户标识
        /// </summary>
        /// <param name="userID"></param>
        /// <returns></returns>
        public static string GetStrUserID(int userID)
        {
            return CWHEncryptNet.XorEncrypt(userID.ToString());
        }

        #endregion

        #region 密保问题

        /// <summary>
        /// 密保验证正确
        /// 0 没有答对问题
        /// 1 答对一个
        /// 3 答对三个
        /// </summary>
        public static int IsProtectionCorrect
        {
            get
            {
                if (HttpContext.Current == null ||
                    HttpContext.Current.Session == null ||
                    HttpContext.Current.Session[Fetch.UC_PROTECTION_VALID_CORRECT] == null)
                {
                    return 0;
                }

                return TypeParse.StrToInt(HttpContext.Current.Session[Fetch.UC_PROTECTION_VALID_CORRECT], 0);
            }
        }

        /// <summary>
        /// 设置答对密保数目
        /// </summary>
        /// <param name="correct"></param>
        public static void SetProtectionCorrect(int correct)
        {
            HttpContext context = HttpContext.Current;
            if (context != null && context.Session != null)
            {
                HttpContext.Current.Session[Fetch.UC_PROTECTION_VALID_CORRECT] = correct;
            }
        }

        //密保问题(配置信息)
        private static void GetProtectionQuestions()
        {
            m_protectionQuestiongs = new List<string>();

            DataSet ds = new DataSet();
            ds.ReadXml(TextUtility.GetFullPath("/config/protection.xml"));
            DataRow[] drSet = ds.Tables["Item"].Select("Questions_ID=0");

            foreach (DataRow dr in drSet)
            {
                m_protectionQuestiongs.Add(dr[0].ToString());
            }
        }

        /// <summary>
        /// 密保问题
        /// </summary>
        public static List<string> ProtectionQuestiongs
        {
            get { return m_protectionQuestiongs; }
        }

        /// <summary>
        /// 获取密保问题
        /// </summary>
        public static int GetProtectionQuestionID
        {
            get
            {
                int questionID = TypeParse.StrToInt(Fetch.GetCookie("questionID"), 0);
                return questionID;
            }
        }

        /// <summary>
        /// 获取当前会话中保持的申请密保用户键
        /// </summary>
        /// <param name="userID"></param>
        /// <returns></returns>
        public static string GetSessionProtectionKey(int userID)
        {
            return string.Format("question_userID_{0}", userID);
        }

        ///// <summary>
        ///// 记忆老的密保回答
        ///// </summary>
        ///// <param name="protection"></param>
        //public static void SetOldProtectionInfo(AccountsProtect protection)
        //{
        //    if (protection == null) return;
        //    string sessionKey = string.Format("old_{0}", Fetch.GetSessionProtectionKey(protection.UserID));
        //    SessionState.Set(sessionKey, protection);
        //}

        ///// <summary>
        ///// 获取老的密保回答
        ///// </summary>
        ///// <returns></returns>
        //public static AccountsProtect GetOldProtectionInfo(int userID)
        //{
        //    string sessionKey = string.Format("old_{0}", Fetch.GetSessionProtectionKey(userID));
        //    return SessionState.Get(sessionKey) as AccountsProtect;
        //}

        #endregion

        #region cookie

        #region 常规读写

        /// <summary>
        /// 写 Cookies
        /// </summary>
        /// <param name="strName">项</param>
        /// <param name="strValue">值</param>
        public static void SetCookie(string strName, string strValue)
        {
            HttpContext context = HttpContext.Current;
            if (context == null)
            {
                return;
            }

            DateTime now = DateTime.Now;
            HttpCookie cookie = context.Request.Cookies[UC_COOKIE_NAME];
            if (cookie == null)
            {
                cookie = new HttpCookie(UC_COOKIE_NAME);
                TimeSpan minute = new TimeSpan(30, 0, 0, 0);
                cookie.Expires = now + minute;
                cookie.Values[strName] = HttpUtility.UrlEncode(strValue);
            }
            else
            {
                TimeSpan minute = new TimeSpan(30, 0, 0, 0);
                cookie.Expires = now + minute;
                cookie.Values[strName] = Utility.UrlEncode(strValue);
            }

            context.Response.AppendCookie(cookie);
        }

        /// <summary>
        /// 写cookie值
        /// </summary>
        /// <param name="strName">名称</param>
        /// <param name="intValue">值</param>
        public static void SetCookie(string strName, int intValue)
        {
            SetCookie(strName, intValue.ToString());
        }

        /// <summary>
        /// 获得网站cookie值
        /// </summary>
        /// <param name="strName">项</param>
        /// <returns>值</returns>
        public static string GetCookie(string strName)
        {
            HttpContext context = HttpContext.Current;
            if (context == null)
            {
                return "";
            }
            if (context.Request.Cookies != null && context.Request.Cookies[UC_COOKIE_NAME] != null && context.Request.Cookies[UC_COOKIE_NAME][strName] != null)
            {
                return HttpUtility.UrlDecode(context.Request.Cookies[UC_COOKIE_NAME][strName].ToString());
            }
            return "";
        }

        /// <summary>
        /// 删除 Cookies by Cookie 键值
        /// </summary>
        /// <param name="cookieName"></param>
        public static void DeleteCookie(string cookieName)
        {
            HttpContext context = HttpContext.Current;
            if (context == null)
            {
                return;
            }

            HttpCookie cookie = new HttpCookie(cookieName);
            cookie.Values.Clear();
            cookie.Expires = DateTime.Now.AddYears(-1);
            context.Response.AppendCookie(cookie);
        }

        /// <summary>
        /// 删除 Cookies
        /// </summary>
        public static void DeleteSiteCookies()
        {
            DeleteCookie(UC_COOKIE_NAME);
        }

        #endregion

        #region User cookie

        ///// <summary>
        ///// 设置用户cookie
        ///// </summary>
        ///// <param name="userTicket"></param>
        //public static void SetUserCookie(UserTicketInfo userTicket)
        //{
        //    if (userTicket == null)
        //    {
        //        return;
        //    }

        //    string jsonText = userTicket.SerializeText();
        //    string ciphertext = AES.Encrypt(jsonText, CookieEncryptKey);
        //    Fetch.SetCookie(Fetch.UC_COOKIE_LOGON_TOKEN, ciphertext);
        //}

        ///// <summary>
        ///// 获取用户对象
        ///// </summary>
        ///// <returns></returns>
        //public static UserTicketInfo GetUserCookie()
        //{
        //    string plaintext = Fetch.GetCookie(Fetch.UC_COOKIE_LOGON_TOKEN);

        //    //// 大厅cookie处理
        //    //object pfCookieUserName = HttpContext.Current.Request.Cookies["Accounts"];
        //    //object pfCookieUserID = HttpContext.Current.Request.Cookies["UserID"];

        //    //if (string.IsNullOrEmpty(plaintext))
        //    //{
        //    //    // 大厅Cookie存在
        //    //    if (pfCookieUserName != null && pfCookieUserID != null)
        //    //    {
        //    //        plaintext = PalaformWriteCookie();
        //    //    }
        //    //}
        //    //else
        //    //{
        //    //    // 如果网页与大厅cookie都存在，验证是否一致
        //    //    string tempStr = AES.Decrypt(plaintext, CookieEncryptKey);
        //    //    if (!string.IsNullOrEmpty(tempStr) && pfCookieUserName != null && pfCookieUserID != null)
        //    //    {
        //    //        UserTicketInfo tempModel = UserTicketInfo.DeserializeObject(tempStr);
        //    //        if (tempModel.UserID.ToString() != pfCookieUserID.ToString() || tempModel.Accounts != pfCookieUserName.ToString())
        //    //        {
        //    //            Fetch.DeleteCookie(Fetch.UC_COOKIE_LOGON_TOKEN);
        //    //            plaintext = PalaformWriteCookie();
        //    //        }
        //    //    }
        //    //}

        //    //cookie为空
        //    if (TextUtility.EmptyTrimOrNull(plaintext))
        //    {
        //        return null;
        //    }

        //    //验证cookie格式
        //    string jsonText = Utils.AES.Decrypt(plaintext, CookieEncryptKey);
        //    if (TextUtility.EmptyTrimOrNull(jsonText))
        //    {
        //        return null;
        //    }

        //    return UserTicketInfo.DeserializeObject(jsonText);
        //}

        ///// <summary>
        ///// 大厅登陆写入cookie
        ///// </summary>
        //public static string PalaformWriteCookie()
        //{
        //    if (HttpContext.Current.Request.Cookies["Accounts"] != null && HttpContext.Current.Request.Cookies["Password"] != null)
        //    {
        //        string accounts = HttpContext.Current.Request.Cookies["Accounts"].Value.ToString();
        //        string password = HttpContext.Current.Request.Cookies["Password"].Value.ToString();
        //        string machineID = HttpContext.Current.Request.Cookies["MachineID"].Value.ToString();
        //        string mac = HttpContext.Current.Request.Cookies["MacAddress"].Value.ToString();
        //        password = password.Trim();
        //        accounts = accounts.Trim();
        //        UserInfo suInfo = new UserInfo(0, accounts, 0, password.Trim());
        //        suInfo.LastLogonIP = GameRequest.GetUserIP();
        //        AccountsFacade accountsFacade = new AccountsFacade();
        //        Message umsg = accountsFacade.Logon(suInfo, true);
        //        if (umsg.Success)
        //        {
        //            UserInfo ui = umsg.EntityList[0] as UserInfo;
        //            ui.LogonPass = password.Trim();
        //            if (machineID != null)
        //            {
        //                ui.LastLogonMachine = "PC++" + machineID;
        //            }
        //            if (mac != null)
        //            {
        //                ui.RegisterMachine = mac;
        //            }
        //            Fetch.SetUserCookie(ui.ToUserTicketInfo());
        //            return Fetch.GetCookie(Fetch.UC_COOKIE_LOGON_TOKEN);
        //        }
        //    }
        //    return "";
        //}

        /// <summary>
        /// 删除用户cookie
        /// </summary>
        public static void DeleteUserCookie()
        {
            Fetch.DeleteCookie(Fetch.UC_COOKIE_NAME);
        }

        ///// <summary>
        ///// 用户是否在线 判断用户登录 cookie
        ///// 在线 true 离线 false
        ///// </summary>
        ///// <returns></returns>
        //public static bool IsUserOnline()
        //{
        //    UserTicketInfo uti = Fetch.GetUserCookie();
        //    if (uti == null || uti.UserID <= 0)
        //    {
        //        return false;
        //    }

        //    return true;
        //}

        ///// <summary>
        ///// 后台登录并重写 Cookie
        ///// </summary>
        //public static void ReWriteUserCookie()
        //{
        //    if (Fetch.IsUserOnline())
        //    {
        //        UserTicketInfo uti = Fetch.GetUserCookie();
        //        Message msg = accountsData.Logon(new UserInfo(uti), false);
        //        if (msg.Success)
        //        {
        //            UserInfo usInfo = msg.EntityList[0] as UserInfo;
        //            UserTicketInfo utiServer = usInfo.ToUserTicketInfo();
        //            utiServer.LogonPass = uti.LogonPass;

        //            Fetch.SetUserCookie(utiServer);
        //        }
        //    }
        //}

        #endregion

        #endregion

        #region 系统信息

        /// <summary>
        /// 获取缓存的使用状况
        /// </summary>
        /// <returns></returns>
        public static string GetCacheCurrentRunStatus()
        {
            StringBuilder builderCacheName = new StringBuilder();
            StringBuilder builderText = new StringBuilder();
            IDictionaryEnumerator a = HttpRuntime.Cache.GetEnumerator();

            a.Reset();
            a.MoveNext();

            for (int i = 0; i < HttpRuntime.Cache.Count; i++)
            {
                builderCacheName.Append(a.Key);
                if (i < HttpRuntime.Cache.Count - 1) builderCacheName.Append("&#10;&#13;");

                a.MoveNext();
            }

            //输出状态   
            builderText.AppendFormat("内存使用量：{0}KB &nbsp; ", (GC.GetTotalMemory(false) / 1024).ToString("#,#"));
            builderText.AppendFormat("共使用 <span title=\"{1}\">{0}</span> 个系统缓存对象", HttpRuntime.Cache.Count, builderCacheName.ToString());

            return builderText.ToString();
        }

        #endregion

        #region 常规变量

        /// <summary>
        /// 验证码Session键值
        /// </summary>
        public const string UC_VERIFY_CODE_KEY = "uc_verifycode_key";

        /// <summary>
        /// 密码允许错误次数
        /// </summary>
        public const int DW_PASSWD_ERROR_COUNT = 3;

        /// <summary>
        /// 密保序号
        /// </summary>
        public const string UC_PROTECTION_QUESTION_ID = "protection_question_id";

        /// <summary>
        /// 密保验证正确
        /// </summary>
        public const string UC_PROTECTION_VALID_CORRECT = "protection_valid_correct";

        #endregion

        #region 时间戳

        /// <summary>
        /// 获取上周的开始时间和结束时间
        /// </summary>
        public static string GetLastWeekTime()
        {
            DateTime dt = DateTime.Now;
            DateTime startWeek = dt.AddDays(0 - 7 - Convert.ToInt32(dt.DayOfWeek.ToString("d")));  //本周周日
            DateTime endWeek = startWeek.AddDays(6);  //本周周日六
            return GetTimeByDate(startWeek, endWeek);
        }

        /// <summary>
        /// 获取指定日期的开始时间和结束时间(日期格式：开始时间$结束时间)
        /// </summary>
        /// <param name="bgDate"></param>
        /// <param name="enDate"></param>
        /// <returns></returns>
        public static string GetTimeByDate(DateTime bgDate, DateTime enDate)
        {
            DateTime bgTime = new DateTime(bgDate.Year, bgDate.Month, bgDate.Day, 0, 0, 0);
            DateTime enTime = new DateTime(enDate.Year, enDate.Month, enDate.Day, 23, 59, 59);
            return Convert.ToString(bgTime) + "$" + Convert.ToString(enTime);
        }

        #endregion

    }
}
