namespace SDKPackage.Utils
{
    using System;
    using System.Web;
    using System.Management;
    /// <summary>
    /// Request 操作类
    /// </summary>
    public class GameRequest
    {
        private static readonly string[ ] _WebSearchList = new string[ ] { 
            "google", "isaac", "surveybot", "baiduspider", "yahoo", "yisou", "3721", "qihoo", "daqi", "ia_archiver", "p.arthur", "fast-webcrawler", "java", "microsoft-atl-native", "turnitinbot", "webgather", 
            "sleipnir", "msn", "sogou", "lycos", "tom", "iask", "soso", "sina", "baidu", "gougou", "zhongsou"
         };
        /// <summary>
        /// 当前 Http 请求对象
        /// </summary>
        public static HttpRequest Request
        {
            get
            {
                HttpContext current = HttpContext.Current;
                if ( ( current == null ) || ( current.Request == null ) )
                {
                    return null;
                }
                return current.Request;
            }
        }
        private GameRequest( )
        {
        }
        /// <summary>
        /// 获取完整的主机名称及端口号
        /// </summary>
        /// <returns></returns>
        public static string GetCurrentFullHost( )
        {
            HttpRequest request = HttpContext.Current.Request;
            if ( !request.Url.IsDefaultPort )
            {
                return string.Format( "{0}:{1}" , request.Url.Host , request.Url.Port.ToString() );
            }
            return request.Url.Host;
        }
        /// <summary>
        /// 获取主机名称
        /// </summary>
        /// <returns></returns>
        public static string GetHost( )
        {
            if ( HttpContext.Current == null )
            {
                return string.Empty;
            }
            return HttpContext.Current.Request.Url.Host;
        }
        /// <summary>
        /// 获取 Get 请求的float参数值
        /// </summary>
        /// <param name="strName">参数名称</param>
        /// <param name="defValue">设置参数的默认值，供返回使用</param>
        /// <returns>返回转换后的float参数值</returns>
        public static float GetFloat( string strName , float defValue )
        {
            if ( GetQueryFloat( strName , defValue ) == defValue )
            {
                return GetFormFloat( strName , defValue );
            }
            return GetQueryFloat( strName , defValue );
        }
        /// <summary>
        ///  获取表单提交的float参数值
        /// </summary>
        /// <param name="strName">参数名称</param>
        /// <param name="defValue">设置参数的默认值，供返回使用</param>
        /// <returns>返回转换后的float参数值</returns>
        public static float GetFormFloat( string strName , float defValue )
        {
            return TypeParse.StrToFloat( HttpContext.Current.Request.Form[ strName ] , defValue );
        }
        /// <summary>
        /// 获取表单提交的int参数值
        /// </summary>
        /// <param name="strName">参数名称</param>
        /// <param name="defValue">设置参数的默认值，供返回使用</param>
        /// <returns>返回转换后的int参数值</returns>
        public static int GetFormInt( string strName , int defValue )
        {
            return GetFormInt( Request , strName , defValue );
        }
        /// <summary>
        /// 获取表单提交的int参数值
        /// </summary>
        /// <param name="request">请求对象</param>
        /// <param name="strName">参数名称</param>
        /// <param name="defValue">设置参数的默认值，供返回使用</param>
        /// <returns>返回转换后的int参数值</returns>
        public static int GetFormInt( HttpRequest request , string strName , int defValue )
        {
            return TypeParse.StrToInt( request.Form[ strName ] , defValue );
        }
        /// <summary>
        /// 获取表单提交的string参数值
        /// </summary>
        /// <param name="strName">参数名称</param>
        /// <returns>返回转换后的string参数值</returns>
        public static string GetFormString( string strName )
        {
            return GetFormString( Request , strName );
        }
        /// <summary>
        /// 获取表单提交的string参数值
        /// </summary>
        /// <param name="request">请求对象</param>
        /// <param name="strName">参数名称</param>
        /// <returns>返回转换后的string参数值</returns>
        public static string GetFormString( HttpRequest request , string strName )
        {
            if ( ( request == null ) || ( request.Form[ strName ] == null ) )
            {
                return string.Empty;
            }
            return request.Form[ strName ];
        }      
        /// <summary>
        /// 获取 Get 请求的int参数值
        /// </summary>
        /// <param name="strName">参数名称</param>
        /// <param name="defValue">设置参数的默认值，供返回使用</param>
        /// <returns>返回转换后的int参数值</returns>
        public static int GetInt( string strName , int defValue )
        {
            if ( GetQueryInt( strName , defValue ) == defValue )
            {
                return GetFormInt( strName , defValue );
            }
            return GetQueryInt( strName , defValue );
        }
        /// <summary>
        /// 获取当前请求的文件名
        /// </summary>
        /// <returns>返回文件名</returns>
        public static string GetPageName( )
        {
            string[ ] strArray = HttpContext.Current.Request.Url.AbsolutePath.Split( new char[ ] { '/' } );
            return strArray[ strArray.Length - 1 ].ToLower();
        }
        /// <summary>
        /// 返回表单或Url参数的总个数
        /// </summary>
        /// <returns></returns>
        public static int GetParamCount( )
        {
            return ( HttpContext.Current.Request.Form.Count + HttpContext.Current.Request.QueryString.Count );
        }
        /// <summary>
        /// 获取 Get 请求传递的float参数值
        /// </summary>
        /// <param name="strName">参数名称</param>
        /// <param name="defValue">设置参数的默认值，供返回使用</param>
        /// <returns>返回转换后的float参数值</returns>
        public static float GetQueryFloat( string strName , float defValue )
        {
            return TypeParse.StrToFloat( HttpContext.Current.Request.QueryString[ strName ] , defValue );
        }
        /// <summary>
        /// 获取 Get 请求传递的int参数值
        /// </summary>
        /// <param name="strName">参数名称</param>
        /// <param name="defValue">设置参数的默认值，供返回使用</param>
        /// <returns>返回转换后的int参数值</returns>
        public static int GetQueryInt( string strName , int defValue )
        {
            return GetQueryInt( Request , strName , defValue );
        }
        /// <summary>
        /// 获取 Get 请求传递的int参数值
        /// </summary>
        /// <param name="request">请求对象</param>
        /// <param name="strName">参数名称</param>
        /// <param name="defValue">设置参数的默认值，供返回使用</param>
        /// <returns>返回转换后的int参数值</returns>
        public static int GetQueryInt( HttpRequest request , string strName , int defValue )
        {
            return TypeParse.StrToInt( request.QueryString[ strName ] , defValue );
        }
        /// <summary>
        /// 获取 HTTP 查询字符串变量
        /// </summary>
        /// <param name="strName">参数名称</param>
        /// <returns>返回对应参数变量的值</returns>
        public static string GetQueryString( string strName )
        {
            return GetQueryString( Request , strName );
        }
        /// <summary>
        /// 获取 HTTP 查询字符串变量
        /// </summary>
        /// <param name="request">请求对象</param>
        /// <param name="strName">参数名称</param>
        /// <returns>返回对应参数变量的值</returns>
        public static string GetQueryString( HttpRequest request , string strName )
        {
            if ( ( request == null ) || ( request.QueryString[ strName ] == null ) )
            {
                return string.Empty;
            }
            return request.QueryString[ strName ];
        }
        /// <summary>
        /// 获取当前请求的原始 URL 
        /// </summary>
        /// <returns></returns>
        public static string GetRawUrl( )
        {
            return HttpContext.Current.Request.RawUrl;
        }
        /// <summary>
        /// 获取网站域名
        /// </summary>
        /// <returns></returns>
        public static string GetServerDomain( )
        {
            string ipval = HttpContext.Current.Request.Url.Host.ToLower();
            if ( ( ipval.Split( new char[ ] { '.' } ).Length < 3 ) || Validate.IsIP( ipval ) )
            {
                return ipval;
            }
            string str2 = ipval.Remove( 0 , ipval.IndexOf( "." ) + 1 );
            if ( ( ( str2.StartsWith( "com." ) || str2.StartsWith( "net." ) ) || str2.StartsWith( "org." ) ) || str2.StartsWith( "gov." ) )
            {
                return ipval;
            }
            return str2;
        }
        /// <summary>
        /// 获取 Web 服务器变量
        /// </summary>
        /// <param name="strName">参数变量名</param>
        /// <returns>返回对应参数变量的值</returns>
        public static string GetServerString( string strName )
        {
            if ( HttpContext.Current.Request.ServerVariables[ strName ] == null )
            {
                return "";
            }
            return HttpContext.Current.Request.ServerVariables[ strName ].ToString();
        }
        /// <summary>
        /// 获取 HTTP 查询字符串变量
        /// </summary>
        /// <param name="strName">参数变量名</param>
        /// <returns>返回对应参数变量的值</returns>
        public static string GetString( string strName )
        {
            if ( "".Equals( GetQueryString( strName ) ) )
            {
                return GetFormString( strName );
            }
            return GetQueryString( strName );
        }
        /// <summary>
        /// 获取 HTTP 查询字符串变量
        /// </summary>
        /// <param name="request">请求对象</param>
        /// <param name="strName">参数变量名</param>
        /// <returns>返回对应参数变量的值</returns>
        public static string GetString( HttpRequest request , string strName )
        {
            if ( "".Equals( GetQueryString( request , strName ) ) )
            {
                return GetFormString( request , strName );
            }
            return GetQueryString( request , strName );
        }

        public static string GetUrl( )
        {
            return HttpContext.Current.Request.Url.ToString();
        }
        /// <summary>
        /// 获取客户端上次请求的 URL 路径，该请求链接到当前的 URL
        /// </summary>
        /// <returns></returns>
        public static string GetUrlReferrer( )
        {
            Uri urlReferrer = HttpContext.Current.Request.UrlReferrer;
            if ( urlReferrer == null )
            {
                return string.Empty;
            }
            return Convert.ToString( urlReferrer );
        }
        /// <summary>
        /// 获取访问者所使用的浏览器名
        /// </summary>
        /// <returns></returns>
        public static string GetUserBrowser( )
        {
            string str = "Unknown";
            if ( Request == null )
            {
                return str;
            }
            string userAgent = Request.UserAgent;
            switch ( userAgent )
            {
                case null:
                case "":
                    return str;
            }
            userAgent = userAgent.ToLower();
            HttpBrowserCapabilities browser = HttpContext.Current.Request.Browser;
            if ( ( ( ( ( userAgent.IndexOf( "firefox" ) >= 0 ) || ( userAgent.IndexOf( "firebird" ) >= 0 ) ) || ( ( userAgent.IndexOf( "myie" ) >= 0 ) || ( userAgent.IndexOf( "opera" ) >= 0 ) ) ) || ( userAgent.IndexOf( "netscape" ) >= 0 ) ) || ( userAgent.IndexOf( "msie" ) >= 0 ) )
            {
                return ( browser.Browser + browser.Version );
            }
            return "Unknown";
        }
        /// <summary>
        /// 获取客户端的 IP 信息
        /// </summary>
        /// <returns></returns>
        public static string GetUserIP( )
        {
            if ( HttpContext.Current == null )
            {
                return string.Empty;
            }
            string ipval = string.Empty;
            ipval = HttpContext.Current.Request.ServerVariables[ "HTTP_X_FORWARDED_FOR" ];
            switch ( ipval )
            {
                case null:
                case "":
                    ipval = HttpContext.Current.Request.ServerVariables[ "REMOTE_ADDR" ];
                    break;
            }
            if ( ( ipval == null ) || ( ipval == string.Empty ) )
            {
                ipval = HttpContext.Current.Request.UserHostAddress;
            }
            if ( !( ( ( ipval != null ) && ( ipval != string.Empty ) ) && Validate.IsIP( ipval ) ) )
            {
                return "0.0.0.0";
            }
            return ipval;
        }

        /// <summary>
        /// 获取客户端的MAC
        /// </summary>
        /// <returns></returns>
        public static string GetUserMac()
        {
            string mac = null; 
            ManagementObjectSearcher query = new ManagementObjectSearcher("SELECT *  FROM Win32_NetworkAdapterConfiguration"); 
 
            ManagementObjectCollection queryCollection = query.Get();   
 
            foreach (ManagementObject mo in queryCollection)   
            {    
                if (mo["IPEnabled"].ToString() == "True")  
                    mac = mo["MacAddress"].ToString();   
            }                 
            return (mac);   
        }

        ///// <summary>
        ///// 获取用户网卡编号
        ///// </summary>
        ///// <returns></returns>
        //public static string GetUserNetworkCard()
        //{
        //    string _MacAddress = "";
        //    ManagementClass mc = new ManagementClass("Win32_NetworkAdapterConfiguration");
        //    ManagementObjectCollection moc2 = mc.GetInstances();
        //    foreach (ManagementObject mo in moc2)
        //    {
        //        if ((bool)mo["IPEnabled"] == true)
        //            _MacAddress = mo["MacAddress"].ToString();
        //        mo.Dispose();
        //    }
        //    return _MacAddress; 

        //}

        ///// <summary>
        ///// 获取用户磁盘编号
        ///// </summary>
        ///// <returns></returns>
        //public static string GetUserDisc()
        //{
        //    GetVolumeInformation 
        //}


        /// <summary>
        /// 获取访问者所使用的操作系统名
        /// </summary>
        /// <returns></returns>
        public static string GetUserOsname( )
        {
            string str = "Unknown";
            if ( Request != null )
            {
                string userAgent = Request.UserAgent;
                switch ( userAgent )
                {
                    case null:
                    case "":
                        return str;
                }
                if ( userAgent.Contains( "NT 6.1" ) )
                {
                    str = "Windows 7";
                }
                else
                {
                    if ( userAgent.Contains( "NT 6.0" ) )
                    {
                        return "Windows Vista/Server 2008";
                    }
                    if ( userAgent.Contains( "NT 5.2" ) )
                    {
                        return "Windows Server 2003";
                    }
                    if ( userAgent.Contains( "NT 5.1" ) )
                    {
                        return "Windows XP";
                    }
                    if ( userAgent.Contains( "NT 5" ) )
                    {
                        return "Windows 2000";
                    }
                    if ( userAgent.Contains( "NT 4" ) )
                    {
                        return "Windows NT4";
                    }
                    if ( userAgent.Contains( "Me" ) )
                    {
                        return "Windows Me";
                    }
                    if ( userAgent.Contains( "98" ) )
                    {
                        return "Windows 98";
                    }
                    if ( userAgent.Contains( "95" ) )
                    {
                        return "Windows 95";
                    }
                    if ( userAgent.Contains( "Mac" ) )
                    {
                        return "Mac";
                    }
                    if ( userAgent.Contains( "Unix" ) )
                    {
                        return "UNIX";
                    }
                    if ( userAgent.Contains( "Linux" ) )
                    {
                        str = "Linux";
                    }
                    else if ( userAgent.Contains( "SunOS" ) )
                    {
                        str = "SunOS";
                    }
                }
            }
            return str;
        }
        /// <summary>
        /// 获取是否是书籍的浏览器访问的信息
        /// </summary>
        /// <returns>返回浏览器的功能信息</returns>
        public static bool IsBrowserGet( )
        {
            string[ ] strArray = new string[ ] { "ie" , "opera" , "netscape" , "mozilla" , "konqueror" , "firefox" };
            string str = HttpContext.Current.Request.Browser.Type.ToLower();
            for ( int i = 0; i < strArray.Length; i++ )
            {
                if ( str.IndexOf( strArray[ i ] ) >= 0 )
                {
                    return true;
                }
            }
            return false;
        }
        /// <summary>
        /// 返回当前页面是否是跨站提交
        /// </summary>
        /// <returns></returns>
        public static bool IsCrossSitePost( )
        {
            return ( !HttpContext.Current.Request.HttpMethod.Equals( "POST" ) || IsCrossSitePost( GetUrlReferrer() , HttpContext.Current.Request.Url.Host ) );
        }
        /// <summary>
        /// 判断是否是跨站提交
        /// </summary>
        /// <param name="urlReferrer">上个页面地址</param>
        /// <param name="host">网站url</param>
        /// <returns></returns>
        public static bool IsCrossSitePost( string urlReferrer , string host )
        {
            if ( urlReferrer.Length < 7 )
            {
                return true;
            }
            string str = urlReferrer.Remove( 0 , 7 );
            if ( str.IndexOf( ":" ) > -1 )
            {
                str = str.Substring( 0 , str.IndexOf( ":" ) );
            }
            else
            {
                str = str.Substring( 0 , str.IndexOf( '/' ) );
            }
            return ( str != host );
        }
        /// <summary>
        /// 客户端是否使用 GET 方法的 HTTP数据传输
        /// </summary>
        /// <returns></returns>
        public static bool IsGet( )
        {
            return HttpContext.Current.Request.HttpMethod.Equals( "GET" );
        }
        /// <summary>
        /// 客户端是否使用 POST 方法的 HTTP数据传输
        /// </summary>
        /// <returns></returns>
        public static bool IsPost( )
        {
            return HttpContext.Current.Request.HttpMethod.Equals( "POST" );
        }
        /// <summary>
        /// 是否被判断为机器人
        /// </summary>
        /// <returns></returns>
        public static bool IsRobots( )
        {
            return IsSearchEnginesGet();
        }
        /// <summary>
        /// 搜索引擎来源判断
        /// </summary>
        /// <returns></returns>
        public static bool IsSearchEnginesGet( )
        {
            string userAgent = HttpContext.Current.Request.UserAgent;
            if ( ( userAgent == null ) || ( string.Empty == userAgent ) )
            {
                return true;
            }
            userAgent = userAgent.ToLower();
            for ( int i = 0; i < _WebSearchList.Length; i++ )
            {
                if ( -1 != userAgent.IndexOf( _WebSearchList[ i ] ) )
                {
                    return true;
                }
            }
            return GetUserBrowser().Equals( "Unknown" );
        }
        /// <summary>
        /// 保存上载文件的内容
        /// </summary>
        /// <param name="path">上载文件路径</param>
        public static void SaveRequestFile( string path )
        {
            if ( HttpContext.Current.Request.Files.Count > 0 )
            {
                HttpContext.Current.Request.Files[ 0 ].SaveAs( path );
            }
        }
        /// <summary>
        /// 是否从其他连接向本域名连接
        /// </summary>
        public static bool IsPostFromAnotherDomain
        {
            get
            {
                if ( HttpContext.Current.Request.HttpMethod == "GET" )
                {
                    return false;
                }
                return ( GetUrlReferrer().IndexOf( GetServerDomain() ) == -1 );
            }
        }
      
    }
}

