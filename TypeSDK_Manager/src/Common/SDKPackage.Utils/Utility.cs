namespace SDKPackage.Utils
{
    using Microsoft.JScript;
    using System;
    using System.Collections.Generic;
    using System.Configuration;
    using System.Data;
    using System.Drawing;
    using System.IO;
    using System.Management;
    using System.Net;
    using System.Text;
    using System.Text.RegularExpressions;
    using System.Web;
    using System.Web.UI;
    /// <summary>
    /// 工具类,对常用方法重新进行封装，及获取一些常用环境变量
    /// </summary>
    public class Utility
    {
        /// <summary>
        /// 程序集版本
        /// </summary>
        public const string ASSEMBLY_VERSION = "4.0.0";
        /// <summary>
        /// 把动态页面转换成静态页面并输出
        /// </summary>
        /// <param name="path"></param>
        /// <param name="outPath"></param>
        public static void Aspx2XHtml( string path , string outPath )
        {
            FileStream stream;
            Page page = new Page();
            StringWriter writer = new StringWriter();
            page.Server.Execute( path , writer );
            if ( System.IO.File.Exists( page.Server.MapPath( outPath ) ) )
            {
                System.IO.File.Delete( page.Server.MapPath( outPath ) );
                stream = System.IO.File.Create( page.Server.MapPath( outPath ) );
            }
            else
            {
                stream = System.IO.File.Create( page.Server.MapPath( outPath ) );
            }
            byte[ ] bytes = Encoding.UTF8.GetBytes( writer.ToString() );
            stream.Write( bytes , 0 , bytes.Length );
            stream.Close();
        }
        /// <summary>
        /// 清空客户端浏览器的缓存,设置页面不被缓存
        /// </summary>
        public static void ClearPageClientCache( )
        {
            if ( HttpContext.Current != null )
            {
                HttpContext.Current.Response.Buffer = false;
                HttpContext.Current.Response.Expires = 0;
                HttpContext.Current.Response.ExpiresAbsolute = DateTime.Now.AddDays( -1.0 );
                HttpContext.Current.Response.AddHeader( "pragma" , "no-cache" );
                HttpContext.Current.Response.AddHeader( "cache-control" , "private" );
                HttpContext.Current.Response.CacheControl = "no-cache";
                HttpContext.Current.Response.Cache.SetCacheability( HttpCacheability.NoCache );
                HttpContext.Current.Response.Cache.SetAllowResponseInBrowserHistory( true );
                HttpContext.Current.Response.Cookies.Clear();
            }
        }
        /// <summary>
        /// 设置页面不被缓存
        /// </summary>
        public static void SetPageNoCache( )
        {
            if ( HttpContext.Current != null )
            {
                HttpContext.Current.Response.Buffer = true;
                HttpContext.Current.Response.ExpiresAbsolute = DateTime.Now.AddSeconds( -1.0 );
                HttpContext.Current.Response.Expires = 0;
                HttpContext.Current.Response.CacheControl = "no-cache";
                HttpContext.Current.Response.AddHeader( "Pragma" , "No-Cache" );
            }
        }
        /// <summary>
        /// 字符转数字版本
        /// </summary>
        /// <param name="strVersion"></param>
        /// <returns></returns>
        public static int ConvertVersionStr2Int( string strVersion )
        {
            if ( !Validate.IsIP( strVersion ) )
            {
                return 0;
            }
            string[ ] strArray = strVersion.Split( new char[ ] { '.' } );
            return ( ( ( ( System.Convert.ToInt32( strArray[ 0 ] ) << 0x18 ) | ( System.Convert.ToInt32( strArray[ 1 ] ) << 0x10 ) ) | ( System.Convert.ToInt32( strArray[ 2 ] ) << 8 ) ) | System.Convert.ToInt32( strArray[ 3 ] ) );
        }
        /// <summary>
        ///  将数据表转换成JSON类型串
        /// </summary>
        /// <param name="dt"></param>
        /// <returns></returns>
        public static StringBuilder DataTableToJSON( DataTable dt )
        {
            return DataTableToJson( dt , true );
        }
        /// <summary>
        /// 将数据表转换成JSON类型串
        /// </summary>
        /// <param name="dt">要转换的数据表</param>
        /// <param name="dtDispose">数据表转换结束后是否dispose掉</param>
        /// <returns></returns>
        public static StringBuilder DataTableToJson( DataTable dt , bool dtDispose )
        {
            StringBuilder builder = new StringBuilder();
            builder.Append( "[\r\n" );
            string[ ] strArray = new string[ dt.Columns.Count ];
            int index = 0;
            string format = "{{";
            string str2 = "";
            foreach ( DataColumn column in dt.Columns )
            {
                object obj2;
                strArray[ index ] = column.Caption.ToLower().Trim();
                format = format + "'" + column.Caption.ToLower().Trim() + "':";
                str2 = column.DataType.ToString().Trim().ToLower();
                if ( ( ( ( str2.IndexOf( "int" ) > 0 ) || ( str2.IndexOf( "deci" ) > 0 ) ) || ( ( str2.IndexOf( "floa" ) > 0 ) || ( str2.IndexOf( "doub" ) > 0 ) ) ) || ( str2.IndexOf( "bool" ) > 0 ) )
                {
                    obj2 = format;
                    format = string.Concat( new object[ ] { obj2 , "{" , index , "}" } );
                }
                else
                {
                    obj2 = format;
                    format = string.Concat( new object[ ] { obj2 , "'{" , index , "}'" } );
                }
                format = format + ",";
                index++;
            }
            if ( format.EndsWith( "," ) )
            {
                format = format.Substring( 0 , format.Length - 1 );
            }
            format = format + "}},";
            index = 0;
            object[ ] args = new object[ strArray.Length ];
            foreach ( DataRow row in dt.Rows )
            {
                foreach ( string str3 in strArray )
                {
                    args[ index ] = row[ strArray[ index ] ].ToString().Trim().Replace( @"\" , @"\\" ).Replace( "'" , @"\'" );
                    string str4 = args[ index ].ToString();
                    if ( str4 != null )
                    {
                        if ( !( str4 == "True" ) )
                        {
                            if ( str4 == "False" )
                            {
                                goto Label_028E;
                            }
                        }
                        else
                        {
                            args[ index ] = "true";
                        }
                    }
                    goto Label_029C;
                Label_028E:
                    args[ index ] = "false";
                Label_029C:
                    index++;
                }
                index = 0;
                builder.Append( string.Format( format , args ) );
            }
            if ( builder.ToString().EndsWith( "," ) )
            {
                builder.Remove( builder.Length - 1 , 1 );
            }
            if ( dtDispose )
            {
                dt.Dispose();
            }
            return builder.Append( "\r\n];" );
        }


        public static string escape( string str )
        {
            return GlobalObject.escape( str );
        }
        /*
        /// <summary>
        /// 以 32 位 MD5 加密加CookieToken后缀的形式产生 cookie 密文
        /// </summary>
        /// <param name="s"></param>
        /// <returns></returns>
        public static string GenerateToken(string s)
        {
            if ((s == null) || (0 == s.Length))
            {
                s = string.Empty;
            }
            return MD5(s + ApplicationSettings.Get("CookieToken"));
        }*/
        /// <summary>
        ///  32 位 MD5 加密 
        /// </summary>
        /// <param name="s"></param>
        /// <returns></returns>
        public static string MD5( string s )
        {
            return TextEncrypt.MD5EncryptPassword( s );
        }
        /// <summary>
        /// 获取web.config的配置项
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        public static string GetAppSetting( string key )
        {
            return ApplicationSettings.Get( key );
        }

        /*public static string GetAssemblyCopyright()
        {
            return ApplicationEnvironment.GetAssemblyCopyright();
        }

        public static string GetAssemblyProductName()
        {
            return ApplicationEnvironment.GetAssemblyProductName();
        }

        public static string GetAssemblyVersion()
        {
            return ApplicationEnvironment.GetAssemblyVersion();
        }
        */
        /*
         /// <summary>
         /// 取CPU编号
         /// </summary>
         /// <returns></returns>
         public static string GetCpuID()
         {
             ManagementClass class2 = new ManagementClass("Win32_Processor");
             string s = "";
             try
             {
                 ManagementObjectCollection instances = class2.GetInstances();
                 foreach (ManagementObject obj2 in instances)
                 {
                     s = obj2.Properties["ProcessorId"].Value.ToString();
                     goto Label_008F;
                 }
             }
             catch
             {
                 s = "";
             }
             finally
             {
                 class2.Dispose();
             }
         Label_008F:
             return BitConverter.ToString(Encoding.GetEncoding("GB2312").GetBytes(s)).Replace("-", "");
         }
         /// <summary>
         /// 取第一块硬盘编号
         /// </summary>
         /// <returns></returns>
         public static string GetHardDiskID()
         {
             string s = "";
             ManagementClass class2 = new ManagementClass("Win32_DiskDrive");
             ManagementObjectCollection instances = class2.GetInstances();
             foreach (ManagementObject obj2 in instances)
             {
                 s = obj2.Properties["Model"].Value.ToString();
                 obj2.Dispose();
             }
             class2.Dispose();
             instances.Dispose();
             return BitConverter.ToString(Encoding.GetEncoding("GB2312").GetBytes(s)).Replace("-", "");
         }
         */
        /// <summary>
        /// 取机器名
        /// </summary>
        /// <returns></returns>
        public static string GetHostName( )
        {
            return Dns.GetHostName();
        }
        /*
        public static string GetIPAddressByMac(string macAddress)
        {
            List<LocalIPAndMac> localIPAndMac = GetLocalIPAndMac();
            foreach (LocalIPAndMac mac in localIPAndMac)
            {
                if (string.Compare(mac.MACAddress, macAddress, true) == 0)
                {
                    return mac.IPAddress;
                }
            }
            return "";
        }

        public static List<LocalIPAndMac> GetLocalIPAndMac()
        {
            List<LocalIPAndMac> list = new List<LocalIPAndMac>();
            ManagementObjectCollection instances = new ManagementClass("Win32_NetworkAdapterConfiguration").GetInstances();
            foreach (ManagementObject obj2 in instances)
            {
                try
                {
                    if ((bool) obj2["IPEnabled"])
                    {
                        string mac = obj2["MacAddress"].ToString().Replace(':', '-');
                        Array array = (Array) obj2.Properties["IpAddress"].Value;
                        string ip = array.GetValue(0).ToString();
                        list.Add(new LocalIPAndMac(ip, mac));
                    }
                }
                catch
                {
                }
                obj2.Dispose();
            }
            return list;
        }

        public static string GetMACAddress()
        {
            string str = " ";
            ManagementClass class2 = new ManagementClass("Win32_NetworkAdapterConfiguration");
            ManagementObjectCollection instances = class2.GetInstances();
            foreach (ManagementObject obj2 in instances)
            {
                if ((bool) obj2["IPEnabled"])
                {
                    str = obj2["MacAddress"].ToString();
                }
                obj2.Dispose();
            }
            class2.Dispose();
            instances.Dispose();
            return str.Replace(":", "");
        }

        public static string GetMACByIPAddress(string ipAddress)
        {
            List<LocalIPAndMac> localIPAndMac = GetLocalIPAndMac();
            foreach (LocalIPAndMac mac in localIPAndMac)
            {
                if (string.Compare(mac.IPAddress, ipAddress, true) == 0)
                {
                    return mac.MACAddress;
                }
            }
            return "";
        }

        public static string GetMachineSerial()
        {
            return GetMACAddress();
        }
        */
        /// <summary>
        /// 获取操作系统版本
        /// </summary>
        /// <returns></returns>
        public static string GetOSVersion( )
        {
            string s = Environment.OSVersion.Version.ToString();
            return BitConverter.ToString( Encoding.GetEncoding( "GB2312" ).GetBytes( s ) ).Replace( "-" , "" );
        }
        /// <summary>
        /// 从HTML中获取文本,保留br,p,img
        /// </summary>
        /// <param name="HTML"></param>
        /// <returns></returns>
        public static string GetTextFromHTML( string HTML )
        {
            Regex regex = new Regex( "</?(?!br|/?p|img)[^>]*>" , RegexOptions.IgnoreCase );
            return regex.Replace( HTML , "" );
        }
        /// <summary>
        /// 长整型转换成IP地址字符串形式
        /// </summary>
        /// <param name="ipNumber"></param>
        /// <returns></returns>
        public static string Int2IP( long ipNumber )
        {
            IPAddress address = new IPAddress( ipNumber );
            return address.ToString();
        }
        /// <summary>
        /// IP 地址字符串形式转换成长整型
        /// </summary>
        /// <param name="ip"></param>
        /// <returns></returns>
        public static long IP2Int( string ip )
        {
            if ( !Validate.IsIP( ip ) )
            {
                return -1L;
            }
            string[ ] strArray = ip.Split( new char[ ] { '.' } );
            long num = long.Parse( strArray[ 3 ] ) * 0x1000000L;
            num += int.Parse( strArray[ 2 ] ) * 0x10000;
            num += int.Parse( strArray[ 1 ] ) * 0x100;
            return ( num + int.Parse( strArray[ 0 ] ) );
        }
        /// <summary>
        /// 判断给定的字符串数组(strNumber)中的数据是不是都为数值型
        /// </summary>
        /// <param name="strNumber"></param>
        /// <returns></returns>
        public static bool IsNumericArray( string[ ] strNumber )
        {
            return TypeParse.IsNumericArray( strNumber );
        }



        /*
        /// <summary>
        /// 接收数据处理
        /// </summary>
        /// <param name="cipHerBuffer"></param>
        /// <param name="cipherKey"></param>
        /// <returns></returns>
        public static object ReceiveDataLaunch(byte[] cipHerBuffer, string cipherKey)
        {
            return SerializationHelper.Deserialize(CompressHelper.DeflateDecompress(AES.DecryptBuffer(cipHerBuffer, cipherKey)));
        }
        /// <summary>
        /// 发送数据处理
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="t"></param>
        /// <param name="cipherKey"></param>
        /// <returns></returns>
        public static byte[] SendDataLaunch<T>(T t, string cipherKey)
        {
            return AES.EncryptBuffer(CompressHelper.DeflateCompress(SerializationHelper.Serialize<T>(t)), cipherKey);
        }
        */
        /// <summary>
        /// 页面跳转
        /// </summary>
        /// <param name="url"></param>
        public static void Redirect( string url )
        {
            if ( ( HttpContext.Current != null ) && !string.IsNullOrEmpty( url ) )
            {
                HttpContext.Current.Response.Redirect( url );
                HttpContext.Current.Response.StatusCode = 0x12d;
                HttpContext.Current.Response.End();
            }
        }

        /// <summary>
        /// 以指定的ContentType输出指定文件
        /// </summary>
        /// <param name="filepath">文件路径</param>
        /// <param name="filename">输出的文件名</param>
        /// <param name="filetype">将文件输出时设置的ContentType</param>
        public static void ResponseFile( string filepath , string filename , string filetype )
        {
            if ( HttpContext.Current != null )
            {
                Stream stream = null;
                byte[ ] buffer = new byte[ 0x2710 ];
                try
                {
                    stream = new FileStream( filepath , FileMode.Open , FileAccess.Read , FileShare.ReadWrite );
                    long length = stream.Length;
                    HttpContext.Current.Response.ContentType = filetype;
                    HttpContext.Current.Response.AddHeader( "Content-Disposition" , "attachment;filename=" + UrlEncode( filename.Trim() ).Replace( "+" , " " ) );
                    while ( length > 0L )
                    {
                        if ( HttpContext.Current.Response.IsClientConnected )
                        {
                            int count = stream.Read( buffer , 0 , 0x2710 );
                            HttpContext.Current.Response.OutputStream.Write( buffer , 0 , count );
                            HttpContext.Current.Response.Flush();
                            buffer = new byte[ 0x2710 ];
                            length -= count;
                        }
                        else
                        {
                            length = -1L;
                        }
                    }
                }
                catch ( Exception exception )
                {
                    HttpContext.Current.Response.Write( "Error : " + exception.Message );
                }
                finally
                {
                    if ( stream != null )
                    {
                        stream.Close();
                    }
                }
                HttpContext.Current.Response.End();
            }
        }
        /// <summary>
        /// 查找非 UTF8 编码的文件
        /// </summary>
        /// <param name="directory"></param>
        /// <returns></returns>
        public static string[ ] SearchUTF8File( string directory )
        {
            StringBuilder builder = new StringBuilder();
            FileInfo[ ] files = new DirectoryInfo( directory ).GetFiles();
            for ( int i = 0; i < files.Length; i++ )
            {
                if ( files[ i ].Extension.ToLower().Equals( ".htm" ) )
                {
                    FileStream sbInputStream = new FileStream( files[ i ].FullName , FileMode.Open , FileAccess.Read );
                    bool flag = IsUTF8( sbInputStream );
                    sbInputStream.Close();
                    if ( !flag )
                    {
                        builder.Append( files[ i ].FullName );
                        builder.Append( "\r\n" );
                    }
                }
            }
            return TextUtility.SplitStrArray( builder.ToString() , "\r\n" );
        }
        /// <summary>
        /// 是否为 UTF8 编码
        /// </summary>
        /// <param name="sbInputStream">文件输入流</param>
        /// <returns>是返回 true,否则 false</returns>
        private static bool IsUTF8( FileStream sbInputStream )
        {
            bool flag = true;
            long length = sbInputStream.Length;
            byte num2 = 0;
            for ( int i = 0; i < length; i++ )
            {
                byte num4 = ( byte )sbInputStream.ReadByte();
                if ( ( num4 & 0x80 ) != 0 )
                {
                    flag = false;
                }
                if ( num2 == 0 )
                {
                    if ( num4 >= 0x80 )
                    {
                        do
                        {
                            num4 = ( byte )( num4 << 1 );
                            num2 = ( byte )( num2 + 1 );
                        }
                        while ( ( num4 & 0x80 ) != 0 );
                        num2 = ( byte )( num2 - 1 );
                        if ( num2 == 0 )
                        {
                            return false;
                        }
                    }
                }
                else
                {
                    if ( ( num4 & 0xc0 ) != 0x80 )
                    {
                        return false;
                    }
                    num2 = ( byte )( num2 - 1 );
                }
            }
            if ( num2 > 0 )
            {
                return false;
            }
            if ( flag )
            {
                return false;
            }
            return true;
        }

        #region 类型转换
        public static bool StrToBool( object expression , bool defValue )
        {
            return TypeParse.StrToBool( expression , defValue );
        }

        public static bool StrToBool( string expression , bool defValue )
        {
            return TypeParse.StrToBool( expression , defValue );
        }

        public static float StrToFloat( object strValue , float defValue )
        {
            return TypeParse.StrToFloat( strValue , defValue );
        }

        public static float StrToFloat( string strValue , float defValue )
        {
            return TypeParse.StrToFloat( strValue , defValue );
        }

        public static int StrToInt( object expression , int defValue )
        {
            return TypeParse.StrToInt( expression , defValue );
        }

        public static int StrToInt( string expression , int defValue )
        {
            return TypeParse.StrToInt( expression , defValue );
        }
        #endregion

        /// <summary>
        /// 将字符串转换为Color
        /// </summary>
        /// <param name="color"></param>
        /// <returns></returns>
        public static Color ToColor( string color )
        {
            int num;
            int num2;
            char[ ] chArray;
            int blue = 0;
            color = color.TrimStart( new char[ ] { '#' } );
            color = Regex.Replace( color.ToLower() , "[g-zG-Z]" , "" );
            switch ( color.Length )
            {
                case 3:
                    chArray = color.ToCharArray();
                    num = System.Convert.ToInt32( chArray[ 0 ].ToString() + chArray[ 0 ].ToString() , 0x10 );
                    num2 = System.Convert.ToInt32( chArray[ 1 ].ToString() + chArray[ 1 ].ToString() , 0x10 );
                    blue = System.Convert.ToInt32( chArray[ 2 ].ToString() + chArray[ 2 ].ToString() , 0x10 );
                    return Color.FromArgb( num , num2 , blue );

                case 6:
                    chArray = color.ToCharArray();
                    num = System.Convert.ToInt32( chArray[ 0 ].ToString() + chArray[ 1 ].ToString() , 0x10 );
                    num2 = System.Convert.ToInt32( chArray[ 2 ].ToString() + chArray[ 3 ].ToString() , 0x10 );
                    blue = System.Convert.ToInt32( chArray[ 4 ].ToString() + chArray[ 5 ].ToString() , 0x10 );
                    return Color.FromArgb( num , num2 , blue );
            }
            return Color.FromName( color );
        }

        #region 跟踪调试
        /// <summary>
        /// 跟踪调试输出一个对象
        /// </summary>
        /// <param name="obj"></param>
        public static void Trace( object obj )
        {
            string format = "<div style='border:1px solid #96C2F1;background-color: #F7F7FF;font-size:14px;font-family:宋体;text-align:right;margin: 0px auto;margin-bottom:5px;margin-right:5px;float:left; text-align:left; line-height:25px; width:800px;'><h5 style='margin: 1px;background-color:#E2EAF8;height: 24px;'>跟踪信息：</h5>{0}</div>";
            HttpContext.Current.Response.Write( string.Format( format , obj.ToString() ) );
        }
        /// <summary>
        /// 跟踪调试输出一个对象,不加修饰
        /// </summary>
        /// <param name="obj"></param>
        public static void TraceWhite( object obj )
        {
            HttpContext.Current.Response.Write( obj.ToString() );
        }
        #endregion

        public static string unescape( string str )
        {
            return GlobalObject.unescape( str );
        }

        public static string Url2HyperLink( string text )
        {
            string pattern = @"(http|ftp|https):\/\/[\w]+(.[\w]+)([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])";
            MatchCollection matchs = Regex.Matches( text , pattern , RegexOptions.Compiled | RegexOptions.IgnoreCase );
            foreach ( Match match in matchs )
            {
                text = text.Replace( match.ToString() , "<a target=\"_blank\" href=\"" + match.ToString() + "\">" + match.ToString() + "</a>" );
            }
            return text;
        }
        public static string HtmlDecode( string str )
        {
            return HttpUtility.HtmlDecode( str );
        }

        public static string HtmlEncode( string str )
        {
            return HttpUtility.HtmlEncode( str );
        }

        public static string UrlDecode( string str )
        {
            return HttpUtility.UrlDecode( str );
        }

        public static string UrlEncode( string str )
        {
            return HttpUtility.UrlEncode( str );
        }

        #region Cookie
        public static void WriteCookie( string strName , string strValue )
        {
            HttpCookie cookie = HttpContext.Current.Request.Cookies[ strName ];
            if ( cookie == null )
            {
                cookie = new HttpCookie( strName );
            }
            cookie.Value = strValue;
            HttpContext.Current.Response.AppendCookie( cookie );
        }

        public static void WriteCookie( string strName , string strValue , int expires )
        {
            HttpCookie cookie = HttpContext.Current.Request.Cookies[ strName ];
            if ( cookie == null )
            {
                cookie = new HttpCookie( strName );
            }
            cookie.Value = strValue;
            cookie.Expires = DateTime.Now.AddMinutes( ( double )expires );
            HttpContext.Current.Response.AppendCookie( cookie );
        }

        public static void WriteCookie( string strName , string key , string strValue )
        {
            HttpCookie cookie = HttpContext.Current.Request.Cookies[ strName ];
            if ( cookie == null )
            {
                cookie = new HttpCookie( strName );
            }
            cookie[ key ] = strValue;
            HttpContext.Current.Response.AppendCookie( cookie );
        }
        public static string GetCookie( string strName )
        {
            if ( ( HttpContext.Current.Request.Cookies != null ) && ( HttpContext.Current.Request.Cookies[ strName ] != null ) )
            {
                return HttpContext.Current.Request.Cookies[ strName ].Value.ToString();
            }
            return "";
        }

        public static string GetCookie( string strName , string key )
        {
            if ( ( ( HttpContext.Current.Request.Cookies != null ) && ( HttpContext.Current.Request.Cookies[ strName ] != null ) ) && ( HttpContext.Current.Request.Cookies[ strName ][ key ] != null ) )
            {
                return HttpContext.Current.Request.Cookies[ strName ][ key ].ToString();
            }
            return "";
        }
        #endregion

        public static string CurrentPath
        {
            get
            {
                if ( HttpContext.Current == null )
                {
                    return string.Empty;
                }
                string path = HttpContext.Current.Request.Path;
                path = path.Substring( 0 , path.LastIndexOf( "/" ) );
                if ( path == "/" )
                {
                    return string.Empty;
                }
                return path;
            }
        }

        public static string CurrentUrl
        {
            get
            {
                return GameRequest.GetUrl();
            }
        }

        public static string GetAppLogDirectory
        {
            get
            {
                string fullPath = ConfigurationManager.AppSettings[ "AppLogDirectory" ];
                if ( string.IsNullOrEmpty( fullPath ) )
                {
                    fullPath = "AppLog";
                }
                fullPath = TextUtility.GetFullPath( fullPath );
                if ( !Directory.Exists( fullPath ) )
                {
                    Directory.CreateDirectory( fullPath );
                }
                return fullPath;
            }
        }

        public static string GetIPDbFilePath
        {
            get
            {
                return ApplicationSettings.Get( "IPDbFilePath" );
            }
        }

        public static bool GetWriteAppLog
        {
            get
            {
                bool flag = false;
                string str = ConfigurationManager.AppSettings[ "WriteAppLog" ];
                if ( !string.IsNullOrEmpty( str ) )
                {
                    flag = System.Convert.ToBoolean( str );
                }
                return flag;
            }
        }

        public static string RawUrl
        {
            get
            {
                return GameRequest.GetRawUrl();
            }
        }

        public static string Referrer
        {
            get
            {
                return GameRequest.GetUrlReferrer();
            }
        }

        public static string ServerDomain
        {
            get
            {
                return GameRequest.GetServerDomain();
            }
        }

        public static string UserBrowser
        {
            get
            {
                return GameRequest.GetUserBrowser();
            }
        }

        public static string UserIP
        {
            get
            {
                return GameRequest.GetUserIP();
            }
        }
    }
}

