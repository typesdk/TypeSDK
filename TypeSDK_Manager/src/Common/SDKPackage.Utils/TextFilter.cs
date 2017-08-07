namespace SDKPackage.Utils
{
    using System;
    using System.Text;
    using System.Text.RegularExpressions;
    using System.Web;
    [Flags]
    public enum FilterType
    {
        /// <summary>
        /// 脚本
        /// </summary>
        Script = 1 ,
        /// <summary>
        /// Html代码
        /// </summary>
        Html = 2 ,
        /// <summary>
        /// 对象
        /// </summary>
        Object = 3 ,
        /// <summary>
        /// 链接脚本
        /// </summary>
        AHrefScript = 4 ,
        /// <summary>
        /// iframe
        /// </summary>
        Iframe = 5 ,
        /// <summary>
        /// frameSet
        /// </summary>
        Frameset = 6 ,
        /// <summary>
        /// Src
        /// </summary>
        Src = 7 ,
        /// <summary>
        /// 脏字
        /// </summary>
        BadWords = 8 ,
        /// <summary>
        /// 全部
        /// </summary>
        All = 0x10
    }
    /// <summary>
    /// 提供字符串过滤方法
    /// </summary>
    public class TextFilter
    {
        private static Regex REGEX_BR = new Regex( @"(\r\n)" , RegexOptions.IgnoreCase );

        private TextFilter( )
        {
        }
        /// <summary>
        /// 过滤链接脚本
        /// </summary>
        /// <param name="content">要过滤的内容</param>
        /// <returns></returns>
        public static string FilterAHrefScript( string content )
        {
            string input = FilterScript( content );
            string pattern = @" href[ ^=]*= *[\s\S]*script *:";
            return Regex.Replace( input , pattern , string.Empty , RegexOptions.IgnoreCase );
        }
        /// <summary>
        /// 过滤全部
        /// </summary>
        /// <param name="content">要过滤的内容</param>
        /// <returns></returns>
        public static string FilterAll( string content )
        {
            content = FilterHtml( content );
            content = FilterScript( content );
            content = FilterAHrefScript( content );
            content = FilterObject( content );
            content = FilterIframe( content );
            content = FilterFrameset( content );
            content = FilterSrc( content );
            content = FilterBadWords( content );
            return content;
        }
        /// <summary>
        /// 
        /// </summary>
        /// <param name="chkStr"></param>
        /// <returns></returns>
        private static string FilterBadWords( string chkStr )
        {
            string str = "";
            if ( string.IsNullOrEmpty( chkStr ) )
            {
                return string.Empty;
            }
            string[ ] strArray = str.Split( new char[ ] { '#' } );
            StringBuilder builder = new StringBuilder();
            for ( int i = 0; i < strArray.Length; i++ )
            {
                string pattern = strArray[ i ].ToString().Trim();
                Match match = new Regex( pattern , RegexOptions.Singleline | RegexOptions.Multiline | RegexOptions.IgnoreCase ).Match( chkStr );
                if ( match.Success )
                {
                    int length = match.Value.Length;
                    builder.Insert( 0 , "*" , length );
                    string replacement = builder.ToString();
                    chkStr = Regex.Replace( chkStr , pattern , replacement , RegexOptions.Singleline | RegexOptions.Multiline | RegexOptions.IgnoreCase );
                }
                builder.Remove( 0 , builder.Length );
            }
            return chkStr;
        }
        /// <summary>
        /// 过滤给定字符串中的回车及换行符
        /// </summary>
        /// <param name="filterStr"></param>
        /// <returns></returns>
        public static string FilterBR( string filterStr )
        {
            Match match = null;
            for ( match = REGEX_BR.Match( filterStr ); match.Success; match = match.NextMatch() )
            {
                filterStr = filterStr.Replace( match.Groups[ 0 ].ToString() , "" );
            }
            return filterStr;
        }
        /// <summary>
        /// 过滤Frameset标签
        /// </summary>
        /// <param name="content"></param>
        /// <returns></returns>
        public static string FilterFrameset( string content )
        {
            string pattern = @"(?i)<Frameset([^>])*>(\w|\W)*</Frameset([^>])*>";
            return Regex.Replace( content , pattern , string.Empty , RegexOptions.IgnoreCase );
        }
        /// <summary>
        /// Html代码过滤
        /// </summary>
        /// <param name="content"></param>
        /// <returns></returns>
        public static string FilterHtml( string content )
        {
            string input = FilterScript( content );
            string pattern = "<[^>]*>";
            return Regex.Replace( input , pattern , string.Empty , RegexOptions.IgnoreCase );
        }
        /// <summary>
        /// 过滤Iframe标签
        /// </summary>
        /// <param name="content"></param>
        /// <returns></returns>
        public static string FilterIframe( string content )
        {
            string pattern = @"(?i)<Iframe([^>])*>(\w|\W)*</Iframe([^>])*>";
            return Regex.Replace( content , pattern , string.Empty , RegexOptions.IgnoreCase );
        }
        /// <summary>
        /// 过滤 Object 标签
        /// </summary>
        /// <param name="content"></param>
        /// <returns></returns>
        public static string FilterObject( string content )
        {
            string pattern = @"(?i)<Object([^>])*>(\w|\W)*</Object([^>])*>";
            return Regex.Replace( content , pattern , string.Empty , RegexOptions.IgnoreCase );
        }
        /// <summary>
        /// 脚本过滤
        /// </summary>
        /// <param name="content"></param>
        /// <returns></returns>
        public static string FilterScript( string content )
        {
            //string pattern = "<script[^>]*?>.*?</script>|<%|%>|select|insert|delete|from|count\\(|drop table|update|truncate|asc\\(|mid\\(|char\\(|xp_cmdshell|exec master|net localgroup administrators|:|net user|\"|\\'|or|exec|exectue|sp_executesql";
            string pattern = "<script[^>]*?>.*?</script>|<%|%>|select|insert|delete|from|count\\(|drop table|update|truncate|asc\\(|mid\\(|char\\(|xp_cmdshell|exec master|net localgroup administrators|:|net user|\"|\\'|exec|exectue|sp_executesql";
            return StripScriptAttributesFromTags(Regex.Replace(content, pattern, string.Empty, RegexOptions.IgnoreCase));
        }
        /// <summary>
        /// 过滤包含 Src 的标签
        /// </summary>
        /// <param name="content"></param>
        /// <returns></returns>
        public static string FilterSrc( string content )
        {
            string input = FilterScript( content );
            string pattern = " src *= *['\"]?[^\\.]+\\.(js|vbs|asp|aspx|php|jsp)['\"]";
            return Regex.Replace( input , pattern , "" , RegexOptions.IgnoreCase );
        }
        /// <summary>
        /// 从HTML中获取文本,保留br,p,img标签
        /// </summary>
        /// <param name="contentHtml">要过滤的Html文本</param>
        /// <returns></returns>
        public static string FilterTextFromHTML( string contentHtml )
        {
            Regex regex = new Regex( "</?(?!br|/?p|img)[^>]*>" , RegexOptions.IgnoreCase );
            return regex.Replace( contentHtml , "" );
        }
        /// <summary>
        /// 过滤字符串尾部的回车/换行/空格
        /// </summary>
        /// <param name="content"></param>
        /// <returns></returns>
        public static string FilterTrim( string content )
        {
            for ( int i = content.Length; i >= 0; i-- )
            {
                char ch = content[ i ];
                if ( !ch.Equals( " " ) )
                {
                    ch = content[ i ];
                }
                if ( ch.Equals( "\r" ) || ( ch = content[ i ] ).Equals( "\n" ) )
                {
                    content.Remove( i , 1 );
                }
            }
            return content;
        }
        /// <summary>
        ///  过滤HTML中的不安全标签
        /// </summary>
        /// <param name="content"></param>
        /// <returns></returns>
        public static string FilterUnsafeHtml( string content )
        {
            content = Regex.Replace( content , @"(\<|\s+)o([a-z]+\s?=)" , "$1$2" , RegexOptions.IgnoreCase );
            content = Regex.Replace( content , @"(script|frame|form|meta|behavior|style)([\s|:|>])+" , "$1.$2" , RegexOptions.IgnoreCase );
            return content;
        }
        /// <summary>
        /// 过滤输入字符串为字母和数字,@,-
        /// </summary>
        /// <param name="inputStr">输入字符串</param>
        /// <returns></returns>
        public static string FilterUserInput( string inputStr )
        {
            return Regex.Replace( inputStr.Trim() , @"[^\w\.@-]" , "" );
        }
        /// <summary>
        ///  过滤所有 XHTML 标签,并编码过滤后返回的字符串
        /// </summary>
        /// <param name="content"></param>
        /// <returns></returns>
        public static string FilterXHtml( string content )
        {
            return FilterXHtml( content , true );
        }
        /// <summary>
        /// 过滤所有 XHTML 标签
        /// </summary>
        /// <param name="content">XHtml 内容</param>
        /// <param name="encode">是否对过滤后的内容编码</param>
        /// <returns></returns>
        public static string FilterXHtml( string content , bool encode )
        {
            if ( !string.IsNullOrEmpty( content ) )
            {
                content = Regex.Replace( content , "<script[^>]*?>.*?</script>" , "" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "<(.[^>]*)>" , "" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , @"([\r\n])[\s]+" , "" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "-->" , "" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "<!--.*" , "" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "&(quot|#34);" , "\"" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "&(amp|#38);" , "&" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "&(lt|#60);" , "<" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "&(gt|#62);" , ">" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "&(nbsp|#160);" , " " , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "&(iexcl|#161);" , "\x00a1" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "&(cent|#162);" , "\x00a2" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "&(pound|#163);" , "\x00a3" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , "&(copy|#169);" , "\x00a9" , RegexOptions.IgnoreCase );
                content = Regex.Replace( content , @"&#(\d+);" , "" , RegexOptions.IgnoreCase );
                content.Replace( "<" , "" );
                content.Replace( ">" , "" );
                content.Replace( "\r\n" , "" );
                if ( encode )
                {
                    content = HttpUtility.HtmlEncode( content ).Trim();
                }
            }
            return content;
        }
        /// <summary>
        ///  过滤XHtml标签
        /// </summary>
        /// <param name="filterType">过滤类型</param>
        /// <param name="filterContent">要过滤内容</param>
        /// <returns></returns>
        public static string Process( FilterType filterType , string filterContent )
        {
            switch ( filterType )
            {
                case FilterType.Script:
                    filterContent = FilterScript( filterContent );
                    return filterContent;

                case FilterType.Html:
                    filterContent = FilterHtml( filterContent );
                    return filterContent;

                case FilterType.Object:
                    filterContent = FilterObject( filterContent );
                    return filterContent;

                case FilterType.AHrefScript:
                    filterContent = FilterAHrefScript( filterContent );
                    return filterContent;

                case FilterType.Iframe:
                    filterContent = FilterIframe( filterContent );
                    return filterContent;

                case FilterType.Frameset:
                    filterContent = FilterFrameset( filterContent );
                    return filterContent;

                case FilterType.Src:
                    filterContent = FilterSrc( filterContent );
                    return filterContent;

                case FilterType.BadWords:
                    filterContent = FilterBadWords( filterContent );
                    return filterContent;

                case ( FilterType.BadWords | FilterType.Script ):
                case ( FilterType.BadWords | FilterType.Html ):
                case ( FilterType.BadWords | FilterType.Object ):
                case ( FilterType.BadWords | FilterType.AHrefScript ):
                case ( FilterType.BadWords | FilterType.Iframe ):
                case ( FilterType.BadWords | FilterType.Frameset ):
                case ( FilterType.BadWords | FilterType.Src ):
                    return filterContent;

                case FilterType.All:
                    filterContent = FilterAll( filterContent );
                    return filterContent;
            }
            return filterContent;
        }

        private static string StripAttributesHandler( Match m )
        {
            if ( m.Groups[ "attribute" ].Success )
            {
                return m.Value.Replace( m.Groups[ "attribute" ].Value , "" );
            }
            return m.Value;
        }

        private static string StripScriptAttributesFromTags( string content )
        {
            string str = "on(blur|c(hange|lick)|dblclick|focus|keypress|(key|mouse)(down|up)|(un)?load\r\n                    |mouse(move|o(ut|ver))|reset|s(elect|ubmit))";
            Regex regex = new Regex( string.Format( "(?inx)\r\n\t\t\t\t\\<(\\w+)\\s+\r\n\t\t\t\t\t(\r\n\t\t\t\t\t\t(?'attribute'\r\n\t\t\t\t\t\t(?'attributeName'{0})\\s*=\\s*\r\n\t\t\t\t\t\t(?'delim'['\"]?)\r\n\t\t\t\t\t\t(?'attributeValue'[^'\">]+)\r\n\t\t\t\t\t\t(\\3)\r\n\t\t\t\t\t)\r\n\t\t\t\t\t|\r\n\t\t\t\t\t(?'attribute'\r\n\t\t\t\t\t\t(?'attributeName'href)\\s*=\\s*\r\n\t\t\t\t\t\t(?'delim'['\"]?)\r\n\t\t\t\t\t\t(?'attributeValue'javascript[^'\">]+)\r\n\t\t\t\t\t\t(\\3)\r\n\t\t\t\t\t)\r\n\t\t\t\t\t|\r\n\t\t\t\t\t[^>]\r\n\t\t\t\t)*\r\n\t\t\t\\>" , str ) );
            return regex.Replace( content , new MatchEvaluator( TextFilter.StripAttributesHandler ) );
        }
    }
}

