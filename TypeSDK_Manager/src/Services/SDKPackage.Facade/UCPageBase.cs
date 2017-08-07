using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;
using System.Web;
using System.Web.UI;
using System.Web.UI.HtmlControls;
using System.Web.UI.WebControls;

using SDKPackage.Utils;
using SDKPackage.Kernel;
using SDKPackage.IData;
using System.Security.Cryptography;
using SDKPackage.Entity;
using SDKPackage.Facade;
using System.Data;

namespace SDKPackage.Facade
{
    /// <summary>
    /// 页面基类
    /// </summary>
    public abstract class UCPageBase : Page
    {
        #region Fields

        //标题间的分割线 "_"
        private const string SEPARATE_LINE = " - ";

        //是否验证用户身份
        private bool m_isAuthenticatedUser = false;

        //是否验证会员身份
        private bool m_isAuthenticatedMember = false;

        //是否在线
        private bool m_isOnLine = Fetch.IsUserOnline();

        //站点标识
        private int m_stationID = Fetch.StationID;

        #endregion

        #region 继承属性

        ///// <summary>
        ///// 网站外观
        ///// </summary>
        //protected NativeWebFacade aideWebsiteFacade;

        ///// <summary>
        ///// 用户外观
        ///// </summary>
        //protected AccountsFacade aideUserFacade;

        private static object lockObj = new object();

        ///// <summary>
        ///// 前台外观
        ///// </summary>
        private static volatile NativeWebFacade _aideWebsiteFacade;
        public static NativeWebFacade aideWebsiteFacade
        {
            get
            {
                if (_aideWebsiteFacade == null)
                {
                    lock (lockObj)
                    {
                        if (_aideWebsiteFacade == null)
                            _aideWebsiteFacade = new NativeWebFacade();
                    }
                }
                return _aideWebsiteFacade;
            }
        }

        /////// <summary>
        /////// 库外观
        /////// </summary>
        //private static volatile AccountsFacade _aideUserFacade;
        //public static AccountsFacade aideUserFacade
        //{
        //    get
        //    {
        //        if (_aideUserFacade == null)
        //        {
        //            lock (lockObj)
        //            {
        //                if (_aideUserFacade == null)
        //                    _aideUserFacade = new AccountsFacade();
        //            }
        //        }
        //        return _aideUserFacade;
        //    }
        //}

        /// <summary>
        /// 是否验证用户身份
        /// </summary>
        protected virtual bool IsAuthenticatedUser { get { return m_isAuthenticatedUser; } }

        /// <summary>
        /// 是否验证会员身份
        /// </summary>
        protected virtual bool IsAuthenticatedMember { get { return m_isAuthenticatedMember; } }

        /// <summary>
        /// 设置页面标题
        /// </summary>
        public virtual string ChannelTitle { get { return "首页"; } }

        /// <summary>
        /// 是否在线 在线 true 离线 false
        /// </summary>
        protected bool IsOnLine { get { return m_isOnLine; } }

        /// <summary>
        /// 个人中心登陆地址
        /// </summary>
        protected string LogonUrl;

        /// <summary>
        /// 原始请求地址
        /// </summary>
        protected string RawUrl;

        /// <summary>
        /// 重新定向地址
        /// </summary>
        protected string RedirectUrl;

        /// <summary>
        /// 用户基本Cookies信息
        /// </summary>
        protected UserTicketInfo userTicket;

        /// <summary>
        /// 站点标识
        /// </summary>
        protected int StationID { get { return m_stationID; } }

        #endregion

        #region 页面事件

        #region 构造方法
        /// <summary>
        /// 初始化页面基类
        /// </summary>
        public UCPageBase()
        {
            //aideWebsiteFacade = new NativeWebFacade();
            //aideUserFacade = new AccountsFacade();

            LogonUrl = "/Login.aspx";
            RawUrl = GameRequest.GetUrl();
            RedirectUrl = Utility.UrlDecode(GameRequest.GetQueryString("redirectUrl"));
        }

        #endregion

        /// <summary>
        /// 初始化并验证用户身份
        /// </summary>
        /// <param name="e"></param>
        protected override void OnInit(EventArgs e)
        {
            base.OnInit(e);

            if (IsAuthenticatedUser)
            {
                UserLogon();
            }
            else if (IsOnLine)
            {
                //在线 刷新 Cookie
                userTicket = Fetch.GetUserCookie();
            }
            SetStyle();
        }

        /// <summary>
        /// 添加 links 和 javascript 到页面头部 header
        /// </summary>
        protected override void OnLoad(EventArgs e)
        {
            base.OnLoad(e);

            //header标签
            AddHeaderTitle();

            if (!Page.IsCallback && !Page.IsPostBack)
            {
                AddDefaultLanguages();

                //link
                AddGenericLink("image/x-icon", "shortcut icon", "", "/images/favicon.ico");
                AddGenericLink("image/gif", "icon", "", "/images/animated_favicon1.gif");

                //加载数据
                OnInitLoad();

            }
        }

        /// <summary>
        /// 添加页面标题
        /// </summary>
        /// <param name="e"></param>
        protected override void OnPreRenderComplete(EventArgs e)
        {
            base.OnPreRenderComplete(e);
        }

        /// <summary>
        /// 初始化数据
        /// </summary>
        protected virtual void OnInitLoad()
        { }

        #endregion

        #region 追加标签

        /// <summary>
        /// 添加默认的样式语言
        /// </summary>
        protected virtual void AddDefaultLanguages()
        {
            Response.AppendHeader("Content-Style-Type", "text/css");
            Response.AppendHeader("Content-Script-Type", "text/javascript");
        }

        protected virtual void AddHeaderTitle()
        {
            AddMetaTitle(ApplicationSettings.Get("title"));
        }

        protected virtual void AddMetaTitle(string content)
        {
            if (content == "") return;
            HtmlTitle title = new HtmlTitle();
            title.Text = content;
            Page.Header.Controls.Add(title);
        }

        /// <summary>
        /// 添加 meta 标签到页面头部
        /// </summary>
        protected virtual void AddMetaTag(string name, string value)
        {
            if (string.IsNullOrEmpty(name) || string.IsNullOrEmpty(value))
                return;

            HtmlMeta meta = new HtmlMeta();
            meta.Name = name;
            meta.Content = value;
            Page.Header.Controls.Add(meta);
        }

        /// <summary>
        /// 添加 meta 标签到页面头部
        /// </summary>
        /// <param name="httpEquiv"></param>
        /// <param name="content"></param>
        protected virtual void AddMetaTagForHttpEquiv(string httpEquiv, string content)
        {
            if (string.IsNullOrEmpty(httpEquiv) || string.IsNullOrEmpty(content))
                return;

            HtmlMeta meta = new HtmlMeta();
            meta.HttpEquiv = httpEquiv;
            meta.Content = content;
            Page.Header.Controls.Add(meta);
        }

        /// <summary>
        /// 添加一条常规 link 到页面头部
        /// </summary>
        public virtual void AddGenericLink(string relation, string title, string href)
        {
            HtmlLink link = new HtmlLink();
            link.Attributes["rel"] = relation;
            link.Attributes["title"] = title;
            link.Attributes["href"] = href;
            Page.Header.Controls.Add(link);
        }

        /// <summary>
        /// 添加常规 link 到页面头部
        /// </summary>
        public virtual void AddGenericLink(string type, string relation, string title, string href)
        {
            HtmlLink link = new HtmlLink();
            link.Attributes["type"] = type;
            link.Attributes["rel"] = relation;
            link.Attributes["title"] = title;
            link.Attributes["href"] = href;
            Page.Header.Controls.Add(link);
        }

        /// <summary>
        /// 添加一条 JavaScript 引用到页面头部 head 标签
        /// </summary>
        public virtual void AddJavaScriptInclude(string url)
        {
            HtmlGenericControl script = new HtmlGenericControl("script");
            script.Attributes["type"] = "text/javascript";
            script.Attributes["src"] = url;
            Page.Header.Controls.Add(script);
        }

        /// <summary>
        /// 添加样式引用到页面头部 head 标签
        /// </summary>
        /// <param name="url">相对路径</param>
        public virtual void AddStylesheetInclude(string url)
        {
            HtmlLink link = new HtmlLink();
            link.Attributes["type"] = "text/css";
            link.Attributes["href"] = url;
            link.Attributes["rel"] = "stylesheet";
            link.Attributes["media"] = "screen";
            Page.Header.Controls.Add(link);
        }

        /// <summary>
        /// 添加栏目描述到 meta description 标签
        /// </summary>
        protected virtual void AddMetaDescription(string description)
        {
            AddMetaTag("description", HttpUtility.HtmlEncode(description));
        }

        /// <summary>
        /// 添加栏目关键词到 meta keywords 标签
        /// </summary>
        protected virtual void AddMetaKeywords(string keywords)
        {
            AddMetaTag("keywords", HttpUtility.HtmlEncode(keywords));
        }

        /// <summary>
        /// 添加缓存清理 Meta
        /// </summary>
        protected virtual void AddMetaClearCache()
        {
            AddMetaTagForHttpEquiv("Pragma", "no-cache");
            AddMetaTagForHttpEquiv("Cache-Control", "no-cache");
            AddMetaTagForHttpEquiv("Expires", "0");

            Utility.ClearPageClientCache();
        }

        #endregion

        #region 页面跳转

        /// <summary>
        /// 跳转
        /// </summary>
        /// <param name="url"></param>
        protected virtual void Redirect(string url)
        {
            Response.Clear();
            Response.StatusCode = 301;
            Response.AppendHeader("location", url);
            Response.End();
        }

        /// <summary>
        /// 跳转并添加验证地址
        /// </summary>
        /// <param name="url"></param>
        protected virtual void RedirectAndValidUrl(string url)
        {
            string reurl = string.Format("{0}&RedirectUrl={1}", url, Utility.UrlEncode(RawUrl));
            Redirect(reurl);
        }

        /// <summary>
        /// 客户端跳转
        /// </summary>
        /// <param name="url"></param>
        protected virtual void RedirectByClient(string url)
        {
            //JavaScript.Redirect(url);
        }

        #endregion

        #region  登录用户

        /// <summary>
        /// 获取当前登录用户
        /// </summary>
        /// <returns></returns>
        protected virtual UserInfo GetCurrentUser()
        {
            return aideUserFacade.GetUserBaseInfoByUserID(userTicket.UserID);
        }

        /// <summary>
        /// 根据用户ID获取账号
        /// </summary>
        /// <returns></returns>
        public string GetAccountsByUserID(int userID)
        {
            UserInfo user = aideUserFacade.GetUserBaseInfoByUserID(userID);
            if (user == null)
                return "";
            return user.Accounts;
        }

        /// <summary>
        /// 根据用户ID获取GameID
        /// </summary>
        /// <returns></returns>
        public string GetGameIDByUserID(int userID)
        {
            UserInfo user = aideUserFacade.GetUserBaseInfoByUserID(userID);
            if (user == null)
                return "";
            return user.GameID.ToString();
        }

        /// <summary>
        /// 根据用户ID获取用户昵称
        /// </summary>
        /// <param name="userID"></param>
        /// <returns></returns>
        public string GetNickNameByUserID(int userID)
        {
            UserInfo user = aideUserFacade.GetUserBaseInfoByUserID(userID);
            if (user == null)
                return "";
            return user.NickName.ToString();
        }

        /// <summary>
        /// 根据经验获取等级
        /// </summary>
        /// <param name="exp"></param>
        /// <returns></returns>
        public int GetUserGrade(int exp)
        {
            int x = 0;
            int n = 0;
            for (int i = 1; i < 61; i++)
            {
                x += i * 30;
                n += x;
                if (exp < n)
                {
                    return i - 1;
                }
            }
            return 60;
        }


        #endregion

        #region 用户登录

        /// <summary>
        /// 用户登录
        /// </summary>
        protected virtual void UserLogon()
        {
            if (Fetch.IsUserOnline())
            {
                userTicket = Fetch.GetUserCookie();

                //是否会员
                if (IsAuthenticatedMember)
                {
                    IsMember();
                }
            }
            else
            {
                ReLogon();
            }
        }

        /// <summary>
        /// 重定向到登录页
        /// </summary>
        protected virtual void ReLogon()
        {
            string url = String.Format("{0}?url={1}", LogonUrl, RawUrl);
            Redirect(url);
        }

        /// <summary>
        /// 判断是否会员
        /// </summary>
        protected virtual void IsMember()
        {
            UserInfo user = aideUserFacade.GetUserBaseInfoByUserID(userTicket.UserID);
            if (user == null || user.MasterOrder == 0)
            {
                ShowAndRedirect("会员功能页面，欢迎充值成为会员！", "CardSelect.aspx");
            }
        }

        /// <summary>
        /// 是否申请了保护
        /// </summary>
        /// <returns></returns>
        protected virtual bool IsApplyProtection()
        {
            return (userTicket.ProtectID > 0);
        }

        /// <summary>
        /// 指定用户是否申请了保护
        /// </summary>
        /// <param name="strUserID"></param>
        /// <returns></returns>
        protected virtual bool IsApplyProtection(string strUserID, out AccountsProtect protectInfo)
        {
            int dwUserID = TypeParse.StrToInt(CWHEncryptNet.XorCrevasse(strUserID), 0);
            return IsApplyProtection(dwUserID, out protectInfo);
        }

        /// <summary>
        /// 获取保护信息
        /// </summary>
        /// <param name="userID"></param>
        /// <returns></returns>
        protected virtual bool IsApplyProtection(int userID, out AccountsProtect protectInfo)
        {
            protectInfo = null;
            if (userID <= 0) return false;

            Message msg = aideUserFacade.GetUserSecurityByUserID(userID);
            if (msg == null || !msg.Success) return false;

            protectInfo = msg.EntityList[0] as AccountsProtect;
            return protectInfo.ProtectID > 0;
        }

        #endregion

        #region 公共服务

        #region 网站公告

        /// <summary>
        /// 加载公告
        /// </summary>
        public virtual bool IsNotice
        {
            get { return true; }
        }

        #endregion

        #region 获取时间戳（项目众筹时间）

        /// <summary>
        /// 获取众筹项目剩余时间戳
        /// </summary>
        /// <param name="time"></param>
        /// <param name="crowdfundingDate"></param>
        /// <returns></returns>
        public virtual string GetTimeStamp(DateTime time, int crowdfundingDate)
        {
            TimeSpan ts = time - DateTime.Now;
            int day = ts.Days;
            if (day < -1)

                return "已结束";

            else if (day < 0)

                return "即将结束";

            else
                return day.ToString() + "天";

        }

        #endregion

        #region 全局地址变量

        DataSet dsAddr = aideWebsiteFacade.GetAreaAddr("");

        public virtual string GetAddrName(int addrID)
        {
            if (dsAddr.Tables[0].Rows.Count == 0)
                return "";
            return dsAddr.Tables[0].Select("Area_ID=" + addrID)[0]["Area_Name"].ToString();
        }

        #endregion

        #region 获取项目进度




        /// <summary>
        /// 获取项目进度
        /// </summary>
        /// <param name="crowdfundingState"></param>
        /// <param name="amountAll"></param>
        /// <param name="amountNow"></param>
        /// <returns></returns>
        public virtual string GetProjectRate(int crowdfundingState, int amountAll, int amountNow)
        {
            string str = string.Empty;
            string strPercent = "0%";
            switch (crowdfundingState)
            {
                case 0:
                    strPercent = (amountNow / amountAll * 100).ToString();
                    str = "<span class=\"per\">" + strPercent + "%</span><p><b style=\"width: " + strPercent + "%\"></b></p>";
                    break;
                case 1:
                    str = "<span class=\"yr\">预热中，可预约认购提交投资意愿</span>";
                    break;
                case 2:
                    strPercent = (amountNow / amountAll * 100).ToString();
                    str = "<span class=\"per\">" + strPercent + "%</span><p><b style=\"width: " + strPercent + "%\"></b></p>";
                    break;
                case 3:
                    //strPercent = (amountNow / amountAll * 100).ToString();
                    strPercent = "100";
                    str = "<span class=\"per\">" + strPercent + "%</span><p><b style=\"width: " + strPercent + "%\"></b></p>";
                    break;
                case 4:
                    strPercent = (amountNow / amountAll * 100).ToString();
                    str = "<span class=\"per\">" + strPercent + "%</span><p><b style=\"width: " + strPercent + "%\"></b></p>";
                    break;
                default: break;

            }
            return str;
        }

        #endregion

        #region 产品众筹支持模块
        public virtual string GetProductAmountList(decimal ProductAmount, string ProductAmount_Summary, int ProductAmount_No, int i, int crowdfundingState)
        {
            string strVal = string.Empty;
            if (ProductAmount > 0)
            {
                strVal = string.Format(@"<div class='detailReturn'><div class='Returntop webkit-box mb10'>
                    <div class='Returntitle webkit-box-flex'><span class='span1'>支持 <i class='font-yen support-yen'>¥</i>{0}</span>           
                        <a href='' class='ui-button theme_color2 mr5 fr ' style='width:100px;'>去支持</a>
				    </div>
                <div class='blank'></div>
	    		</div>
			    <p>{1}</p>
                <p style='text-align: right;'><span ><span class='sp_cheng'>0</span>位支持者
                    (剩余<span class='sp_cheng'>
                    {2}</span>位)
                </span></p>
		    </div>
    	<div class='blank'></div>", ProductAmount, ProductAmount_Summary, ProductAmount_No);
            }
            return strVal;
        }
        public virtual string GetProductAmountList(System.Collections.ArrayList list)
        {
            //string strVal = string.Empty;
            //            strVal = string.Format(@"<div class='detailReturn'><div class='Returntop webkit-box mb10'>
            //                    <div class='Returntitle webkit-box-flex'><span class='span1'>支持 <i class='font-yen support-yen'>¥</i>{0}</span>           
            //                        <a href='' class='ui-button theme_color2 mr5 fr ' style='width:100px;'>去支持</a>
            //				    </div>
            //                <div class='blank'></div>
            //	    		</div>
            //			    <p>{1}</p>
            //                <p style='text-align: right;'><span ><span class='sp_cheng'>0</span>位支持者
            //                    (剩余<span class='sp_cheng'>
            //                    {2}</span>位)
            //                </span></p>
            //		    </div>
            //    	<div class='blank'></div>", list[0], list[1], list[2]);
            //return strVal;
            StringBuilder strVal = new StringBuilder();
            strVal.Append("12312312sdgfresgsrtehg");
            return strVal.ToString();
        }

        #endregion

        #endregion

        #region 交互提示

        #region 消息对话框

        /// <summary>
        /// 显示消息提示对话框
        /// </summary>
        /// <param name="msg">提示信息</param>
        public void Show(string msg)
        {
            Page.ClientScript.RegisterStartupScript(Page.GetType(), "message", "<script language='javascript' defer>alert('" + msg.ToString() + "');</script>");
        }

        /// <summary>
        /// 控件点击 消息确认提示框
        /// </summary>
        /// <param name="page">当前页面指针，一般为this</param>
        /// <param name="msg">提示信息</param>
        public static void ShowConfirm(System.Web.UI.WebControls.WebControl Control, string msg)
        {
            Control.Attributes.Add("onclick", "return confirm('" + msg + "');");
        }

        /// <summary>
        /// 显示消息提示对话框，并进行页面跳转
        /// </summary>
        /// <param name="page">当前页面指针，一般为this</param>
        /// <param name="msg">提示信息</param>
        /// <param name="url">跳转的目标URL</param>
        public void ShowAndRedirect(string msg, string url)
        {
            StringBuilder Builder = new StringBuilder();
            Builder.Append("<script language='javascript' defer>");
            Builder.AppendFormat("alert('{0}');", msg);
            Builder.AppendFormat("top.location.href='{0}'", url);
            Builder.Append("</script>");
            Page.ClientScript.RegisterStartupScript(Page.GetType(), "message", Builder.ToString());

        }

        #endregion

        #region 页面切换提示

        //提示样式类
        private static string[] ALERT_STYLE_CLASS = { "tips_correct_big", "green14font bold", "tips_error_big", "red14font bold" };

        /// <summary>
        /// 提示信息
        /// </summary>
        /// <param name="isError"></param>
        public virtual void RenderAlertInfo(bool isError, string alertText, int step)
        {
            bool moveSuccess = SwitchStep(step);

            if (moveSuccess)
            {
                //查找提示控件
                Label lblAlertIcon = this.FindControl("lblAlertIcon") as Label;
                Label lblAlertInfo = this.FindControl("lblAlertInfo") as Label;
                if (lblAlertIcon != null && lblAlertInfo != null)
                {
                    if (isError)
                    {
                        lblAlertIcon.CssClass = ALERT_STYLE_CLASS[2];
                        lblAlertInfo.CssClass = ALERT_STYLE_CLASS[3];
                        lblAlertInfo.Text = alertText;
                    }
                    else
                    {
                        lblAlertIcon.CssClass = ALERT_STYLE_CLASS[0];
                        lblAlertInfo.CssClass = ALERT_STYLE_CLASS[1];
                        lblAlertInfo.Text = alertText;
                    }
                }
            }
        }

        /// <summary>
        /// 提示信息
        /// </summary>
        /// <param name="isError"></param>
        /// <param name="alertText"></param>
        public virtual void RenderAlertInfo2(bool isError, string alertText)
        {
            RenderAlertInfo(isError, alertText, 2);
        }

        /// <summary>
        /// 提示信息
        /// </summary>
        /// <param name="isError"></param>
        /// <param name="alertText"></param>
        public virtual void RenderAlertInfo3(bool isError, string alertText)
        {
            RenderAlertInfo(isError, alertText, 3);
        }

        /// <summary>
        /// 步骤切换
        /// </summary>
        /// <param name="moveStep"></param>
        public virtual bool SwitchStep(int moveStep)
        {
            //Panel 查找
            Control step1 = this.FindControl("pnlStep1");
            Control step2 = this.FindControl("pnlStep2");
            Control step3 = this.FindControl("pnlStep3");

            //PlaceHolder
            if (step1 == null) step1 = this.FindControl("phStep1");
            if (step2 == null) step2 = this.FindControl("phStep2");
            if (step3 == null) step3 = this.FindControl("phStep3");

            //Form 查找
            if (step1 == null) step1 = this.FindControl("fmStep1");
            if (step2 == null) step2 = this.FindControl("fmStep2");
            if (step3 == null) step3 = this.FindControl("fmStep3");

            bool moveSuccess = false;

            if (step1 != null)
            {
                moveSuccess = true;
                step1.Visible = (moveStep == 1 || moveStep == 0);
            }
            if (step2 != null)
            {
                moveSuccess = true;
                step2.Visible = (moveStep == 2);
            }
            if (step3 != null)
            {
                moveSuccess = true;
                step3.Visible = (moveStep == 3);
            }

            return moveSuccess;
        }

        #endregion

        #endregion

        #region 刷新令牌

        //页面刷新令牌
        protected const string UC_Page_Refresh = "61631d1b1f3624e6ec24a4e6b8852a79";

        /// <summary>
        /// 是否刷新
        /// </summary>
        protected virtual bool IsPageRefresh
        {
            get
            {
                string clientToken = Utility.GetCookie(UC_Page_Refresh);
                string serverToken = SessionState.Get(UC_Page_Refresh) as string;

                if (string.IsNullOrEmpty(clientToken) && string.IsNullOrEmpty(serverToken))
                {
                    return false;
                }

                if (!clientToken.Equals(serverToken))
                {
                    return false;
                }

                return true;
            }
        }

        /// <summary>
        /// 更新服务端刷新令牌
        /// </summary>
        protected void SetRefreshServerToken()
        {
            string tokenPage = DateTime.Now.Ticks.ToString();
            SessionState.Set(UC_Page_Refresh, tokenPage);
        }

        /// <summary>
        /// 设置刷新令牌
        /// </summary>
        protected void SetRefreshToken()
        {
            string tokenPage = DateTime.Now.Ticks.ToString();
            SessionState.Set(UC_Page_Refresh, tokenPage);
            Utility.WriteCookie(UC_Page_Refresh, tokenPage);
        }

        /// <summary>
        /// 初始刷新令牌
        /// </summary>
        protected void InitRefreshToken()
        {
            SessionState.Set(UC_Page_Refresh, "");
            Utility.WriteCookie(UC_Page_Refresh, "");
        }

        #endregion

        #region 统一设置调用的JS

        /// <summary>
        /// 统一设置调用的
        /// </summary>
        private void SetStyle()
        {
            if (Header != null)
            {
                RegJs(this, "/js/Common.js");
            }
        }

        /// <summary>
        /// 注册一段脚本包含到页面上
        /// </summary>
        /// <param name="url">js文件路径</param>
        public void RegJs(System.Web.UI.Page page, string url)
        {
            Dictionary<string, string> dic = new Dictionary<string, string>();
            dic.Add("type", "text/javascript");
            dic.Add("src", url);
            System.Web.UI.HtmlControls.HtmlGenericControl js = CreateGenericControl("script", dic);
            page.Header.Controls.Add(js);
        }

        /// <summary>
        /// 创建一个HtmlGenericControl类型的标签
        /// </summary>
        /// <param name="tagName">标签名称</param>
        /// <param name="dic">属性列表</param>
        /// <returns></returns>
        public System.Web.UI.HtmlControls.HtmlGenericControl CreateGenericControl(string tagName, IDictionary<string, string> dic)
        {
            System.Web.UI.HtmlControls.HtmlGenericControl obj = new System.Web.UI.HtmlControls.HtmlGenericControl();
            obj.TagName = tagName;
            foreach (KeyValuePair<string, string> kvp in dic)
            {
                obj.Attributes.Add(kvp.Key, kvp.Value);
            }
            return obj;
        }
        #endregion

        #region 给定的日期时间距离现在的天/小时/分钟/秒数
        /// <summary>
        /// 给定的日期时间距离现在的天数。
        /// </summary>
        /// <param name="date">给定的日期时间字符串。</param>       
        /// <returns>返回与现在相差的秒数。</returns>
        public static int StrDateDiffDays(DateTime date)
        {
            TimeSpan span = (TimeSpan)(DateTime.Now - date);
            if (span.TotalDays > 2147483647)
            {
                return 0x7fffffff;
            }
            if (span.TotalSeconds < -2147483648)
            {
                return -2147483648;
            }
            return (int)span.TotalDays;
        }
        /// <summary>
        /// 给定的日期时间累加上给定的小时数，与现在相差的小时数。
        /// </summary>
        /// <param name="time">给定的日期时间字符串。</param>
        /// <param name="hours">累加的小时数。</param>
        /// <returns>返回与现在相差的小时数。</returns>
        public static int StrDateDiffHours(string time, int hours)
        {
            if ((time == "") || (time == null))
            {
                return 1;
            }
            TimeSpan span = (TimeSpan)(DateTime.Now - DateTime.Parse(time).AddHours((double)hours));
            if (span.TotalHours > 2147483647)
            {
                return 0x7fffffff;
            }
            if (span.TotalHours < -2147483648)
            {
                return -2147483648;
            }
            return (int)span.TotalHours;
        }

        /// <summary>
        /// 给定的日期时间累加上给定的分钟数，与现在相差的分钟数。
        /// </summary>
        /// <param name="time">给定的日期时间字符串。</param>
        /// <param name="minutes">累加的分钟数。</param>
        /// <returns>返回与现在相差的分钟数。</returns>
        public static int StrDateDiffMinutes(string time, int minutes)
        {
            if ((time == "") || (time == null))
            {
                return 1;
            }
            TimeSpan span = (TimeSpan)(DateTime.Now - DateTime.Parse(time).AddMinutes((double)minutes));
            if (span.TotalMinutes > 2147483647)
            {
                return 0x7fffffff;
            }
            if (span.TotalMinutes < -2147483648)
            {
                return -2147483648;
            }
            return (int)span.TotalMinutes;
        }

        /// <summary>
        /// 给定的日期时间累加上给定的秒数，与现在相差的秒数。
        /// </summary>
        /// <param name="time">给定的日期时间字符串。</param>
        /// <param name="sec">累加的秒数。</param>
        /// <returns>返回与现在相差的秒数。</returns>
        public static int StrDateDiffSeconds(string time, int sec)
        {
            TimeSpan span = (TimeSpan)(DateTime.Now - DateTime.Parse(time).AddSeconds((double)sec));
            if (span.TotalSeconds > 2147483647)
            {
                return 0x7fffffff;
            }
            if (span.TotalSeconds < -2147483648)
            {
                return -2147483648;
            }
            return (int)span.TotalSeconds;
        }

        /// <summary>
        /// 把给定的日期格式化为距现在的模糊时间段，比如 1 分钟前
        /// </summary>
        /// <param name="dateSpan"></param>
        /// <returns></returns>
        public static string FormatDateSpan(object dateSpan)
        {
            DateTime dtDateSpan = (DateTime)dateSpan;
            TimeSpan span = (TimeSpan)(DateTime.Now - dtDateSpan);

            if (span.TotalDays >= 365)
            {
                return String.Format("{0} 年前", (int)(span.TotalDays / 365));
            }
            else if (span.TotalDays >= 30)
            {
                return String.Format("{0} 月前", (int)(span.TotalDays / 30));
            }
            else if (span.TotalDays > 7 && (span.TotalDays / 7 <= 4))
            {
                return String.Format("{0} 周前", (int)(span.TotalDays / 7));
            }
            else if (span.TotalDays >= 1)
            {
                return String.Format("{0} 天前", (int)span.TotalDays);
            }
            else if (span.TotalHours >= 1)
            {
                return String.Format("{0} 小时前", (int)span.TotalHours);
            }
            else if (span.TotalMinutes >= 1)
            {
                return String.Format("{0} 分钟前", (int)span.TotalMinutes);
            }

            return "1 分钟前";
        }
        #endregion

        #region MD5加密

        /// <summary>
        /// MD5加密函数
        /// </summary>
        /// <param name="text">需要加密的字符串</param>
        /// <param name="isLower">是否小写</param>
        /// <param name="bit">16位或32位</param>
        /// <returns>密文</returns>
        public string EncryptMD5(string text, bool isLower, int bit)
        {
            string ciphertext = string.Empty;
            if (bit != 32 && bit != 16)
            {
                return ciphertext;
            }
            if (bit == 32)    //32位
            {
                MD5 md5 = MD5.Create();//实例化一个md5对像
                byte[] s = md5.ComputeHash(Encoding.UTF8.GetBytes(text));    // 加密后是一个字节类型的数组，这里要注意编码UTF8/Unicode等的选择　
                // 通过使用循环，将字节类型的数组转换为字符串，此字符串是常规字符格式化所得
                for (int i = 0; i < s.Length; i++)
                {
                    ciphertext = ciphertext + s[i].ToString("X");   // 将得到的字符串使用十六进制类型格式。格式后的字符是小写的字母，如果使用大写（X）则格式后的字符是大写字符
                }
            }
            else    //16位
            {
                MD5CryptoServiceProvider md5 = new MD5CryptoServiceProvider();
                ciphertext = BitConverter.ToString(md5.ComputeHash(UTF8Encoding.Default.GetBytes(text)), 4, 8);
                ciphertext = ciphertext.Replace("-", "");
            }
            if (isLower)
            {
                ciphertext = ciphertext.ToLower();
            }
            return ciphertext;
        }



        public string EncryptMD5(string text, int bit, string _input_charset)
        {
            StringBuilder sb = new StringBuilder(bit);
            MD5 md5 = new MD5CryptoServiceProvider();
            byte[] t = md5.ComputeHash(Encoding.GetEncoding(_input_charset).GetBytes(text));
            for (int i = 0; i < t.Length; i++)
            {
                sb.Append(t[i].ToString("x").PadLeft(2, '0'));
            }

            return sb.ToString();
        }

        #endregion

        //        #region 页面异常
        //        protected override void OnError(EventArgs e)
        //        {
        //            base.OnError(e);

        //            //Context.Response.Redirect("SorryMistake.aspx");

        //            Exception contextExcp = Context.Server.GetLastError();

        //#if DEBUG
        //            new Terminator().Throw(contextExcp.ToString());
        //#else
        //            new Terminator().Throw(contextExcp.Message);
        //#endif          
        //        }
        //        #endregion
    }
}
