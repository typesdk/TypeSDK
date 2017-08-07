namespace SDKPackage.Utils
{
    using SDKPackage.Utils.Properties;
    using System;
    using System.Text;
    using System.Web;
    /// <summary>
    /// 程序终止调度
    /// </summary>
    public class Terminator
    {
        public virtual void Alert(string s)
        {
            this.Echo("<script language='javascript'>alert('" + s.Replace("'", @"\'") + "');history.back();</script>");
            this.End();
        }

        public virtual void Alert(string s, string backurl)
        {
            this.Echo("<script language='javascript'>alert('" + s.Replace("'", @"\'") + "');location.href='" + backurl + "';</script>");
            this.End();
        }

        private void Echo(string s)
        {
            HttpContext.Current.Response.Write(s);
        }

        private void End()
        {
            HttpContext.Current.Response.End();
        }

        public virtual void Throw(string message)
        {
            HttpContext.Current.Response.ContentType = "text/html";
            HttpContext.Current.Response.AddHeader("Content-Type", "text/html");
            this.Throw(message, null, null, null, true);
        }

        public virtual void Throw(string message, string title, string links, string autojump, bool showback)
        {
            HttpContext.Current.Response.ContentType = "text/html";
            HttpContext.Current.Response.AddHeader("Content-Type", "text/html");
            StringBuilder builder = new StringBuilder(this.template);
            builder.Replace("{$Charset}", Encoding.UTF8.BodyName);
            builder.Replace("{$Message}", TextUtility.TextEncode(message));
            builder.Replace("{$Title}", ((title == null) || (title == "")) ? "发生了系统错误, 错误信息已经被记录, 请联系管理员" : title);
            if ((links != null) && (links != ""))
            {
                string[] strArray = links.Split(new char[] { '|' });
                for (int i = 0; i < strArray.Length; i++)
                {
                    string[] strArray2 = strArray[i].Split(new char[] { ',' });
                    if (strArray2.Length > 1)
                    {
                        if (strArray2[1].Trim() == "RefHref")
                        {
                            strArray2[1] = Utility.Referrer;
                        }
                        if ((strArray2[1] != string.Empty) && (strArray2[1] != null))
                        {
                            string str = "<a href='" + strArray2[1] + "'";
                            if (strArray2.Length == 3)
                            {
                                str = str + " target='" + strArray2[2].Trim() + "'";
                            }
                            if (strArray2[0].Trim() == "RefText")
                            {
                                strArray2[0] = TextUtility.TextEncode(Utility.Referrer);
                            }
                            str = str + ">" + strArray2[0].Trim() + "</a>\r\n\t\t\t\t";
                            builder.Replace("{$Links}", str + "{$Links}");
                        }
                    }
                }
            }
            if ((autojump != null) && (autojump != string.Empty))
            {
                builder.Replace("{$AutoJump}", "<meta http-equiv='refresh' content='3; url=" + ((autojump == "back") ? "javascript:history.back()" : autojump) + "' />");
            }
            else
            {
                builder.Replace("{$AutoJump}", "<!-- no jump -->");
            }
            if (showback)
            {
                builder.Replace("{$Links}", "<a href='javascript:history.back()'>返回前一页</a>");
            }
            else
            {
                builder.Replace("{$Links}", "<!-- no back -->");
            }
            this.Echo(builder.ToString());
            this.End();
        }

        public virtual string template
        {
            get
            {
                return AppExceptions.Terminator_ExceptionTemplate;
            }
        }
    }
}

