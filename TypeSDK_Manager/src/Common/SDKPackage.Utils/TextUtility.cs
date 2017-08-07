namespace SDKPackage.Utils
{
    using Microsoft.VisualBasic;
    using System;
    using System.Collections.Specialized;
    using System.IO;
    using System.Reflection;
    using System.Security.Cryptography;
    using System.Text;
    using System.Text.RegularExpressions;
    using System.Web;
    using System.Configuration;
    public enum RemoveTextMode
    {
        ByteMode ,
        Normal
    }
    /// <summary>
    /// 提供用于处理字符串的方法
    /// </summary>
    public class TextUtility
    {
        private static readonly string PROLONG_SYMBOL = "...";
        private static RNGCryptoServiceProvider rng = new RNGCryptoServiceProvider();

        private TextUtility( )
        {
        }
        /*
        /// <summary>
        /// 转换字符串为 XML 格式的字符串, 即添加CDATA
        /// </summary>
        /// <param name="originalStr">原始字符串</param>
        /// <returns></returns>
        public static string ConvertCDATAStr(string originalStr)
        {
            return ("<![CDATA[" + originalStr + "]]>");
        }

        /// <summary>
        /// 将指定字符串中的汉字转换为拼音字母，其中非汉字保留为原字符
        /// </summary>
        /// <param name="cnText">中文字符</param>
        /// <returns>返回汉语拼音</returns>
        public static string ConvertCnCharSpellFull(string cnText)
        {
            int[] numArray = new int[] { 
                -20319, -20317, -20304, -20295, -20292, -20283, -20265, -20257, -20242, -20230, -20051, -20036, -20032, -20026, -20002, -19990, 
                -19986, -19982, -19976, -19805, -19784, -19775, -19774, -19763, -19756, -19751, -19746, -19741, -19739, -19728, -19725, -19715, 
                -19540, -19531, -19525, -19515, -19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275, -19270, -19263, -19261, -19249, 
                -19243, -19242, -19238, -19235, -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006, -19003, -18996, -18977, -18961, 
                -18952, -18783, -18774, -18773, -18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697, -18696, -18526, -18518, -18501, 
                -18490, -18478, -18463, -18448, -18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201, -18184, -18183, -18181, -18012, 
                -17997, -17988, -17970, -17964, -17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752, -17733, -17730, -17721, -17703, 
                -17701, -17697, -17692, -17683, -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427, -17417, -17202, -17185, -16983, 
                -16970, -16942, -16915, -16733, -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470, -16465, -16459, -16452, -16448, 
                -16433, -16429, -16427, -16423, -16419, -16412, -16407, -16403, -16401, -16393, -16220, -16216, -16212, -16205, -16202, -16187, 
                -16180, -16171, -16169, -16158, -16155, -15959, -15958, -15944, -15933, -15920, -15915, -15903, -15889, -15878, -15707, -15701, 
                -15681, -15667, -15661, -15659, -15652, -15640, -15631, -15625, -15454, -15448, -15436, -15435, -15419, -15416, -15408, -15394, 
                -15385, -15377, -15375, -15369, -15363, -15362, -15183, -15180, -15165, -15158, -15153, -15150, -15149, -15144, -15143, -15141, 
                -15140, -15139, -15128, -15121, -15119, -15117, -15110, -15109, -14941, -14937, -14933, -14930, -14929, -14928, -14926, -14922, 
                -14921, -14914, -14908, -14902, -14894, -14889, -14882, -14873, -14871, -14857, -14678, -14674, -14670, -14668, -14663, -14654, 
                -14645, -14630, -14594, -14429, -14407, -14399, -14384, -14379, -14368, -14355, -14353, -14345, -14170, -14159, -14151, -14149, 
                -14145, -14140, -14137, -14135, -14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094, -14092, -14090, -14087, -14083, 
                -13917, -13914, -13910, -13907, -13906, -13905, -13896, -13894, -13878, -13870, -13859, -13847, -13831, -13658, -13611, -13601, 
                -13406, -13404, -13400, -13398, -13395, -13391, -13387, -13383, -13367, -13359, -13356, -13343, -13340, -13329, -13326, -13318, 
                -13147, -13138, -13120, -13107, -13096, -13095, -13091, -13076, -13068, -13063, -13060, -12888, -12875, -12871, -12860, -12858, 
                -12852, -12849, -12838, -12831, -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556, -12359, -12346, -12320, -12300, 
                -12120, -12099, -12089, -12074, -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798, -11781, -11604, -11589, -11536, 
                -11358, -11340, -11339, -11324, -11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041, -11038, -11024, -11020, -11019, 
                -11018, -11014, -10838, -10832, -10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533, -10519, -10331, -10329, -10328, 
                -10322, -10315, -10309, -10307, -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254
             };
            string[] strArray = new string[] { 
                "a", "ai", "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian", "biao", 
                "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai", "chan", "chang", 
                "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", 
                "cou", "cu", "cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de", "deng", "di", "dian", "diao", 
                "die", "ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo", "e", "en", "er", "fa", "fan", "fang", 
                "fei", "fen", "feng", "fo", "fou", "fu", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong", 
                "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo", "ha", "hai", "han", "hang", "hao", "he", "hei", 
                "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun", "huo", "ji", "jia", "jian", "jiang", 
                "jiao", "jie", "jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan", "kang", "kao", "ke", 
                "ken", "keng", "kong", "kou", "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan", "lang", 
                "lao", "le", "lei", "leng", "li", "lia", "lian", "liang", "liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", 
                "lv", "luan", "lue", "lun", "luo", "ma", "mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian", 
                "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai", "nan", "nang", "nao", "ne", "nei", "nen", 
                "neng", "ni", "nian", "niang", "niao", "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan", "nue", "nuo", "o", 
                "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", 
                "pu", "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", "qiong", "qiu", "qu", "quan", "que", "qun", "ran", 
                "rang", "rao", "re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san", 
                "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan", "shang", "shao", "she", "shen", "sheng", "shi", "shou", "shu", 
                "shua", "shuai", "shuan", "shuang", "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui", "sun", "suo", "ta", 
                "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan", "tui", 
                "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", 
                "xie", "xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya", "yan", "yang", "yao", "ye", "yi", "yin", 
                "ying", "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang", "zao", "ze", "zei", "zen", 
                "zeng", "zha", "zhai", "zhan", "zhang", "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai", "zhuan", 
                "zhuang", "zhui", "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo"
             };
            byte[] bytes = new byte[2];
            string str = "";
            int num = 0;
            int num2 = 0;
            int num3 = 0;
            char[] chArray = cnText.ToCharArray();
            for (int i = 0; i < chArray.Length; i++)
            {
                bytes = Encoding.Default.GetBytes(chArray[i].ToString());
                num2 = bytes[0];
                num3 = bytes[1];
                num = ((num2 * 0x100) + num3) - 0x10000;
                if ((num > 0) && (num < 160))
                {
                    str = str + chArray[i];
                }
                else
                {
                    for (int j = numArray.Length - 1; j >= 0; j--)
                    {
                        if (numArray[j] < num)
                        {
                            str = str + strArray[j];
                            break;
                        }
                    }
                }
            }
            return str;
        }
        /// <summary>
        ///  将指定字符串中的汉字转换为拼音首字母的缩写，其中非汉字保留为原字符
        /// </summary>
        /// <param name="cnStr">中文字符</param>
        /// <returns>返回汉语拼音首字母</returns>
        public static string ConvertCnCharToSpellFirst(string cnStr)
        {
            StringBuilder builder = new StringBuilder(cnStr.Length);
            foreach (char ch2 in cnStr)
            {
                char ch = ch2;
                byte[] bytes = Encoding.Default.GetBytes(new char[] { ch2 });
                if (bytes.Length == 2)
                {
                    int num = (bytes[0] * 0x100) + bytes[1];
                    if (num < 0xb0a1)
                    {
                        ch = ch2;
                    }
                    else if (num < 0xb0c5)
                    {
                        ch = 'a';
                    }
                    else if (num < 0xb2c1)
                    {
                        ch = 'b';
                    }
                    else if (num < 0xb4ee)
                    {
                        ch = 'c';
                    }
                    else if (num < 0xb6ea)
                    {
                        ch = 'd';
                    }
                    else if (num < 0xb7a2)
                    {
                        ch = 'e';
                    }
                    else if (num < 0xb8c1)
                    {
                        ch = 'f';
                    }
                    else if (num < 0xb9fe)
                    {
                        ch = 'g';
                    }
                    else if (num < 0xbbf7)
                    {
                        ch = 'h';
                    }
                    else if (num < 0xbfa6)
                    {
                        ch = 'g';
                    }
                    else if (num < 0xc0ac)
                    {
                        ch = 'k';
                    }
                    else if (num < 0xc2e8)
                    {
                        ch = 'l';
                    }
                    else if (num < 0xc4c3)
                    {
                        ch = 'm';
                    }
                    else if (num < 0xc5b6)
                    {
                        ch = 'n';
                    }
                    else if (num < 0xc5be)
                    {
                        ch = 'o';
                    }
                    else if (num < 0xc6da)
                    {
                        ch = 'p';
                    }
                    else if (num < 0xc8bb)
                    {
                        ch = 'q';
                    }
                    else if (num < 0xc8f6)
                    {
                        ch = 'r';
                    }
                    else if (num < 0xcbfa)
                    {
                        ch = 's';
                    }
                    else if (num < 0xcdda)
                    {
                        ch = 't';
                    }
                    else if (num < 0xcef4)
                    {
                        ch = 'w';
                    }
                    else if (num < 0xd1b9)
                    {
                        ch = 'x';
                    }
                    else if (num < 0xd4d1)
                    {
                        ch = 'y';
                    }
                    else if (num < 0xd7fa)
                    {
                        ch = 'z';
                    }
                }
                builder.Append(ch);
            }
            return builder.ToString();
        }
        /// <summary>
        /// 转换时间为一个double型数字串，起始 0 为 1970-01-01 08:00:00
        ///    原理就是，每过一秒就在这个数字串上累加一
        /// </summary>
        /// <param name="dateTime">要转换的时间</param>
        /// <returns>返回转换后的数字</returns>
        public static double ConvertDateTimeToInt(DateTime dateTime)
        {
            DateTime time = DateTime.Parse("1970-01-01 08:00:00");
            TimeSpan span = (TimeSpan) (dateTime - time);
            return span.TotalSeconds;
        }
        /// <summary>
        /// 转换一个double型数字串为时间，起始 0 为 1970-01-01 08:00:00
        ///    原理是，每过一秒就在这个数字串上累加一
        /// </summary>
        /// <param name="dVal">要转换的数字</param>
        /// <returns>返回转换后的日期</returns>
        public static DateTime ConvertIntToDateTime(double dVal)
        {
            return DateTime.Parse("1970-01-01 08:00:00").AddSeconds(dVal);
        }
        /// <summary>
        /// 将 Stream 转化成 string
        /// </summary>
        /// <param name="s"></param>
        /// <returns></returns>
        public static string ConvertStreamToString(Stream s)
        {
            string str = "";
            StreamReader reader = new StreamReader(s, Encoding.UTF8);
            char[] buffer = new char[0x100];
            for (int i = reader.Read(buffer, 0, 0x100); i > 0; i = reader.Read(buffer, 0, 0x100))
            {
                string str2 = new string(buffer, 0, i);
                str = str + str2;
            }
            reader.Close();
            return str;
        }*/
        #region 生成随机数
        /// <summary>
        /// 创建验证码随机字符串(数字和字母)
        /// </summary>
        /// <param name="len">最大长度</param>
        /// <returns>返回指定最大长度的随机字符串</returns>
        public static string CreateAuthStr( int len )
        {
            StringBuilder builder = new StringBuilder();
            Random random = new Random();
            for ( int i = 0; i < len; i++ )
            {
                int num2 = random.Next();
                if ( ( num2 % 2 ) == 0 )
                {
                    builder.Append( ( char )( 0x30 + ( ( ushort )( num2 % 10 ) ) ) );
                }
                else
                {
                    builder.Append( ( char )( 0x41 + ( ( ushort )( num2 % 0x1a ) ) ) );
                }
            }
            return builder.ToString();
        }
        /// <summary>
        /// 创建验证码随机字符串
        /// </summary>
        /// <param name="len">最大长度</param>
        /// <param name="onlyNum">是否纯数字</param>
        /// <returns>返回指定最大长度的随机字符串</returns>
        public static string CreateAuthStr( int len , bool onlyNum )
        {
            if ( !onlyNum )
            {
                return CreateAuthStr( len );
            }
            StringBuilder builder = new StringBuilder();
            Random random = new Random();
            for ( int i = 0; i < len; i++ )
            {
                int num2 = random.Next();
                builder.Append( ( char )( 0x30 + ( ( ushort )( num2 % 10 ) ) ) );
            }
            return builder.ToString();
        }
        /// <summary>
        /// 生成随机字符串
        /// </summary>
        /// <param name="length">目标字符串的长度</param>
        /// <param name="useNum">是否包含数字，1=包含，默认为包含</param>
        /// <param name="useLow">是否包含小写字母，1=包含，默认为包含</param>
        /// <param name="useUpp">是否包含大写字母，1=包含，默认为包含</param>
        /// <param name="useSpe">是否包含特殊字符，1=包含，默认为不包含</param>
        /// <param name="custom">要包含的自定义字符，直接输入要包含的字符列表</param>
        /// <returns>指定长度的随机字符串</returns>
        public static string CreateRandom( int length , int useNum , int useLow , int useUpp , int useSpe , string custom )
        {
            byte[ ] data = new byte[ 4 ];
            new RNGCryptoServiceProvider().GetBytes( data );
            Random random = new Random( BitConverter.ToInt32( data , 0 ) );
            string str = null;
            string str2 = custom;
            if ( useNum == 1 )
            {
                str2 = str2 + "0123456789";
            }
            if ( useLow == 1 )
            {
                str2 = str2 + "abcdefghijklmnopqrstuvwxyz";
            }
            if ( useUpp == 1 )
            {
                str2 = str2 + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            }
            if ( useSpe == 1 )
            {
                str2 = str2 + "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
            }
            for ( int i = 0; i < length; i++ )
            {
                str = str + str2.Substring( random.Next( 0 , str2.Length - 1 ) , 1 );
            }
            return str;
        }
        /// <summary>
        /// 获取一个由26个小写字母组成的指定长度的随即字符串
        /// </summary>
        /// <param name="len">最大长度</param>
        /// <returns></returns>
        public static string CreateRandomLowercase( int len )
        {
            StringBuilder builder = new StringBuilder();
            Random random = new Random();
            for ( int i = 0; i < len; i++ )
            {
                int num2 = random.Next();
                builder.Append( ( char )( 0x61 + ( ( ushort )( num2 % 0x1a ) ) ) );
            }
            return builder.ToString();
        }
        /// <summary>
        /// 获取指定长度的纯数字随机数字串(以时间做随机种子)
        /// </summary>
        /// <param name="len"></param>
        /// <returns></returns>
        public static string CreateRandomNum( int len )
        {
            StringBuilder builder = new StringBuilder();
            Random random = new Random( ( int )DateTime.Now.Ticks );
            for ( int i = 0; i < len; i++ )
            {
                int num = random.Next();
                builder.Append( ( char )( 0x30 + ( ( ushort )( num % 10 ) ) ) );
            }
            return builder.ToString();
        }

        public static string CreateRandomNum2( int len )
        {
            StringBuilder builder = new StringBuilder();
            Random random = new Random( GetNewSeed() );
            for ( int i = 0; i < len; i++ )
            {
                int num = random.Next();
                builder.Append( ( char )( 0x30 + ( ( ushort )( num % 10 ) ) ) );
            }
            return builder.ToString();
        }
        /// <summary>
        /// 产生随机数的种子
        /// </summary>
        /// <returns></returns>
        public static int GetNewSeed( )
        {
            byte[ ] data = new byte[ 4 ];
            rng.GetBytes( data );
            return BitConverter.ToInt32( data , 0 );
        }
        #endregion

        /// <summary>
        /// 创建指定长度的临时密码
        /// </summary>
        /// <param name="length">临时密码最大长度</param>
        /// <returns></returns>
        public static string CreateTemporaryPassword( int length )
        {
            string str = Guid.NewGuid().ToString( "N" );
            for ( int i = 0; i < ( length / 0x20 ); i++ )
            {
                str = str + Guid.NewGuid().ToString( "N" );
            }
            return str.Substring( 0 , length );
        }

        #region 截取字符串
        /// <summary>
        /// 从给定字符串(originalVal)左侧开始截取指定长度(cutLength)个字符,[使用字节宽度]
        /// </summary>
        /// <param name="originalVal"></param>
        /// <param name="cutLength"></param>
        /// <returns></returns>
        public static string CutLeft( string originalVal , int cutLength )
        {
            if ( string.IsNullOrEmpty( originalVal ) )
            {
                return string.Empty;
            }
            if ( cutLength < 1 )
            {
                return originalVal;
            }
            byte[ ] bytes = Encoding.Default.GetBytes( originalVal );
            if ( bytes.Length <= cutLength )
            {
                return originalVal;
            }
            int length = cutLength;
            int[ ] numArray = new int[ cutLength ];
            byte[ ] destinationArray = null;
            int num2 = 0;
            for ( int i = 0; i < cutLength; i++ )
            {
                if ( bytes[ i ] > 0x7f )
                {
                    num2++;
                    if ( num2 == 3 )
                    {
                        num2 = 1;
                    }
                }
                else
                {
                    num2 = 0;
                }
                numArray[ i ] = num2;
            }
            if ( ( bytes[ cutLength - 1 ] > 0x7f ) && ( numArray[ cutLength - 1 ] == 1 ) )
            {
                length = cutLength + 1;
            }
            destinationArray = new byte[ length ];
            Array.Copy( bytes , destinationArray , length );
            return Encoding.Default.GetString( destinationArray );
        }
        /// <summary>
        /// 从给定字符串(originalVal)右侧开始截取指定长度(cutLength)个字符,[使用方法Substring()]
        /// </summary>
        /// <param name="originalVal"></param>
        /// <param name="cutLength"></param>
        /// <returns></returns>
        public static string CutRight( string originalVal , int cutLength )
        {
            if ( cutLength < 0 )
            {
                cutLength = Math.Abs( cutLength );
            }
            if ( originalVal.Length <= cutLength )
            {
                return originalVal;
            }
            return originalVal.Substring( originalVal.Length - cutLength );
        }

        public static string CutString( string originalVal , int startIndex )
        {
            return CutString( originalVal , startIndex , originalVal.Length );
        }
        /// <summary>
        ///  从给定字符串(originalVal)的(startIndex)索引位置开始截取指定长度(cutLength)的字符串
        /// </summary>
        /// <param name="originalVal"></param>
        /// <param name="startIndex"></param>
        /// <param name="cutLength"></param>
        /// <returns></returns>
        public static string CutString( string originalVal , int startIndex , int cutLength )
        {
            if ( startIndex >= 0 )
            {
                if ( cutLength < 0 )
                {
                    cutLength *= -1;
                    if ( ( startIndex - cutLength ) < 0 )
                    {
                        cutLength = startIndex;
                        startIndex = 0;
                    }
                    else
                    {
                        startIndex -= cutLength;
                    }
                }
                if ( startIndex > originalVal.Length )
                {
                    return "";
                }
            }
            else if ( ( cutLength >= 0 ) && ( ( cutLength + startIndex ) > 0 ) )
            {
                cutLength += startIndex;
                startIndex = 0;
            }
            else
            {
                return "";
            }
            if ( ( originalVal.Length - startIndex ) < cutLength )
            {
                cutLength = originalVal.Length - startIndex;
            }
            try
            {
                return originalVal.Substring( startIndex , cutLength );
            }
            catch
            {
                return originalVal;
            }
        }

        public static string CutStringProlongSymbol( string originalVal , int cutLength )
        {
            if ( originalVal.Length <= cutLength )
            {
                return originalVal;
            }
            else
            {
                return ( CutLeft( originalVal , cutLength ) + PROLONG_SYMBOL );
            }
        }

        public static string CutStringProlongSymbol( string originalVal , int cutLength , string prolongSymbol )
        {
            if ( string.IsNullOrEmpty( prolongSymbol ) )
            {
                prolongSymbol = PROLONG_SYMBOL;
            }
            return ( CutLeft( originalVal , cutLength ) + prolongSymbol );
        }

        public static string CutStringTitle( object content , int cutLength )
        {
            string str = Regex.Replace( content.ToString() , "<[^>]+>" , "" );
            if ( ( str.Length > cutLength ) && ( str.Length > 2 ) )
            {
                str = str.Substring( 0 , cutLength - 2 ) + "...";
            }
            if ( str.IndexOf( "<" ) > -1 )
            {
                str = str.Remove( str.LastIndexOf( "<" ) , str.Length - str.LastIndexOf( "<" ) );
            }
            return str;
        }
        #endregion

       
        
        public static bool EmptyTrimOrNull( string text )
        {
            return ( ( text == null ) || ( text.Trim().Length == 0 ) );
        }
        /*
        /// <summary>
        /// 格式化字节数字符串
        /// </summary>
        /// <param name="bytes"></param>
        /// <returns></returns>
        public static string FormatBytesStr( int bytes )
        {
            if ( bytes > 0x40000000 )
            {
                double num = bytes / 0x40000000;
                return ( num.ToString( "0" ) + "G" );
            }
            if ( bytes > 0x100000 )
            {
                double num2 = bytes / 0x100000;
                return ( num2.ToString( "0" ) + "M" );
            }
            if ( bytes > 0x400 )
            {
                double num3 = bytes / 0x400;
                return ( num3.ToString( "0" ) + "K" );
            }
            return ( bytes.ToString() + "Bytes" );
        }
        /// <summary>
        /// 格式化字节数字符串(带小数)
        /// </summary>
        /// <param name="bytes"></param>
        /// <returns></returns>
        public static string FormatByteStrF( long bytes )
        {
            decimal num;
            if ( bytes > 0x40000000L )
            {
                num = Convert.ToDecimal( bytes ) / Convert.ToDecimal( 0x40000000 );
                return ( num.ToString( "N" ) + " M" );
            }
            if ( bytes > 0x100000L )
            {
                num = Convert.ToDecimal( bytes ) / Convert.ToDecimal( 0x100000 );
                return ( num.ToString( "N" ) + " M" );
            }
            if ( bytes > 0x400L )
            {
                num = Convert.ToDecimal( bytes ) / Convert.ToDecimal( 0x400 );
                return ( num.ToString( "N" ) + " KB" );
            }
            return ( bytes + " Bytes" );
        }

        /// <summary>
        /// 格式化文件尺寸的方法
        /// </summary>
        /// <param name="size"></param>
        /// <returns></returns>
        public static string FormatFileSize( long size )
        {
            string[ ] strArray = new string[ ] { "B" , "KB" , "MB" , "GB" , "TB" , "PB" , "EB" , "ZB" , "YB" , "NB" , "DB" };
            double num = size;
            int index = 0;
            while ( num > 1024.0 )
            {
                num /= 1024.0;
                index++;
                if ( index == 4 )
                {
                    break;
                }
            }
            return ( num.ToString( "F2" ) + strArray[ index ] );
        }
        */
        #region IP地址
        /// <summary>
        /// 格式化 IP 地址, fields 3,2,1 保留左起三位，二位，一位
        ///    隐藏IP地址最后一位用*号代替
        /// </summary>
        /// <param name="ip"></param>
        /// <param name="fields"></param>
        /// <returns></returns>
        public static string FormatIP( string ip , int fields )
        {
            if ( string.IsNullOrEmpty( ip ) )
            {
                return "(未记录)";
            }
            if ( fields > 3 )
            {
                return ip;
            }
            if ( ip.Contains( ":" ) )
            {
                return "(不支持ipv6)";
            }
            string[ ] strArray = ip.Split( new char[ ] { '.' } );
            if ( strArray.Length != 4 )
            {
                return "(未记录)";
            }
            if ( fields == 3 )
            {
                return ( strArray[ 0 ] + "." + strArray[ 1 ] + "." + strArray[ 2 ] + ".*" );
            }
            if ( fields == 2 )
            {
                return ( strArray[ 0 ] + "." + strArray[ 1 ] + ".*.*" );
            }
            if ( fields == 1 )
            {
                return ( strArray[ 0 ] + ".*.*.*" );
            }
            return "*.*.*.*";
        }
        #endregion

        #region 邮箱地址
        /// <summary>
        ///  Email 编码
        /// </summary>
        /// <param name="originalStr"></param>
        /// <returns></returns>
        public static string EmailEncode( string originalStr )
        {
            string str = TextEncode( originalStr ).Replace( "@" , "&#64;" ).Replace( "." , "&#46;" );
            return JoinString( "<a href='mailto:" , new string[ ] { str , "'>" , str , "</a>" } );
        }
        /// <summary>
        ///  获取 Email 的主机名称
        /// </summary>
        /// <param name="strEmail">Email 地址</param>
        /// <returns></returns>
        public static string GetEmailHostName( string strEmail )
        {
            if ( string.IsNullOrEmpty( strEmail ) || ( strEmail.IndexOf( "@" ) < 0 ) )
            {
                return string.Empty;
            }
            return strEmail.Substring( strEmail.LastIndexOf( "@" ) + 1 ).ToLower();
        }
        #endregion
        /// <summary>
        /// 格式化货币
        /// </summary>
        /// <param name="money"></param>
        /// <returns></returns>
        public static string FormatMoney( decimal money )
        {
            return money.ToString( "0.00" );
        }

        #region 时间日期
        /// <summary>
        /// 二个时间差了多少天,多少小时的计算 
        /// </summary>
        /// <param name="todate"></param>
        /// <param name="fodate"></param>
        /// <returns></returns>
        public static string[ ] DiffDateAndTime( object todate , object fodate )
        {
            string[ ] strArray = new string[ 2 ];
            TimeSpan span = ( TimeSpan )( DateTime.Parse( todate.ToString() ) - DateTime.Parse( fodate.ToString() ) );
            double num = span.TotalSeconds / 86400.0;
            string str = num.ToString();
            int length = num.ToString().Length;
            int startIndex = num.ToString().LastIndexOf( "." );
            int num4 = ( int )Math.Round( num , 10 );
            int num5 = ( int )( double.Parse( "0" + num.ToString().Substring( startIndex , length - startIndex ) ) * 24.0 );
            strArray[ 0 ] = num4.ToString();
            strArray[ 1 ] = num5.ToString();
            return strArray;
        }
        /// <summary>
        /// 二个时间差了多少天,多少小时的计算 
        /// </summary>
        /// <param name="todate"></param>
        /// <param name="fodate"></param>
        /// <param name="v1"></param>
        /// <param name="v2"></param>
        /// <param name="v3"></param>
        /// <param name="v4"></param>
        /// <param name="v5"></param>
        /// <param name="v6"></param>
        /// <returns></returns>
        public static string DiffDateAndTime( object todate , object fodate , string v1 , string v2 , string v3 , string v4 , string v5 , string v6 )
        {
            TimeSpan span = ( TimeSpan )( DateTime.Parse( todate.ToString() ) - DateTime.Parse( fodate.ToString() ) );
            int num = ( ( int )span.TotalDays ) / 0x16d;
            int num2 = ( int )( ( ( span.TotalDays / 365.0 ) - ( ( int )( span.TotalDays / 365.0 ) ) ) * 12.0 );
            int num3 = ( span.Days - ( num * 0x16d ) ) - ( num2 * 30 );
            int hours = span.Hours;
            int minutes = span.Minutes;
            string str = "";
            if ( 0 != num )
            {
                str = str + num.ToString() + v1;
            }
            if ( 0 != num2 )
            {
                str = str + num2.ToString() + v2;
            }
            if ( 0 != num3 )
            {
                str = str + num3.ToString() + v3;
            }
            if ( 0 != hours )
            {
                str = str + hours.ToString() + v4;
            }
            if ( 0 != minutes )
            {
                str = str + minutes.ToString() + v5;
            }
            if ( ( ( ( num == 0 ) && ( num2 == 0 ) ) && ( ( num3 == 0 ) && ( hours == 0 ) ) ) && ( 0 == minutes ) )
            {
                return v6;
            }
            return str;
        }
        /// <summary>
        /// 计算给定的日期时间距离现在的天数
        /// </summary>
        /// <param name="oneDateTime">要计算的日期对象</param>
        /// <returns></returns>
        public static int DiffDateDays( DateTime oneDateTime )
        {
            TimeSpan span = ( TimeSpan )( DateTime.Now - oneDateTime );
            if ( span.TotalDays > 2147483647.0 )
            {
                return 0x7fffffff;
            }
            if ( span.TotalSeconds < -2147483648.0 )
            {
                return -2147483648;
            }
            return ( int )span.TotalDays;
        }
        /// <summary>
        /// 计算给定的日期时间距离现在的天数
        /// </summary>
        /// <param name="oneDateTime">要计算的日期字符串</param>
        /// <returns></returns>
        public static int DiffDateDays( string oneDateTime )
        {
            if ( string.IsNullOrEmpty( oneDateTime ) )
            {
                return 0;
            }
            return DiffDateDays( DateTime.Parse( oneDateTime ) );
        }

        /// <summary>
        /// 把给定的日期格式化为距现在的模糊时间段，比如 1 分钟前
        /// </summary>
        /// <param name="dateSpan">日期</param>
        /// <returns></returns>
        public static string FormatDateSpan( object dateSpan )
        {
            DateTime time = ( DateTime )dateSpan;
            TimeSpan span = ( TimeSpan )( DateTime.Now - time );
            if ( span.TotalDays >= 365.0 )
            {
                return string.Format( "{0} 年前" , ( int )( span.TotalDays / 365.0 ) );
            }
            if ( span.TotalDays >= 30.0 )
            {
                return string.Format( "{0} 月前" , ( int )( span.TotalDays / 30.0 ) );
            }
            if ( span.TotalDays >= 7.0 )
            {
                return string.Format( "{0} 周前" , ( int )( span.TotalDays / 7.0 ) );
            }
            if ( span.TotalDays >= 1.0 )
            {
                return string.Format( "{0} 天前" , ( int )span.TotalDays );
            }
            if ( span.TotalHours >= 1.0 )
            {
                return string.Format( "{0} 小时前" , ( int )span.TotalHours );
            }
            if ( span.TotalMinutes >= 1.0 )
            {
                return string.Format( "{0} 分钟前" , ( int )span.TotalMinutes );
            }
            return "1 分钟前";
        }
        /// <summary>
        /// 格式化日期输出
        /// 1 ToString
        /// 2 ToShortDateString
        /// 3 yyyy年MM月dd日HH点mm分ss秒
        /// 4 yyyy年MM月dd日
        /// 5 yyyy年MM月dd日HH点mm分
        /// 6 yyyy-MM-dd HH:mm
        /// 7 yy年MM月dd日 HH点mm分
        /// </summary>
        /// <param name="oneDateVal">日期对象</param>
        /// <param name="formatType">输出类型</param>
        /// <returns></returns>
        public static string FormatDateTime( DateTime oneDateVal , int formatType )
        {
            double num = 0.0;
            DateTime time = oneDateVal.AddHours( num );
            switch ( formatType )
            {
                case 2:
                    return time.ToShortDateString();

                case 3:
                    return time.ToString( "yyyy年MM月dd日 HH点mm分ss秒" );

                case 4:
                    return time.ToString( "yyyy年MM月dd日" );

                case 5:
                    return time.ToString( "yyyy年MM月dd日 HH点mm分" );

                case 6:
                    return time.ToString( "yyyy-MM-dd HH:mm" );

                case 7:
                    return time.ToString( "yy年MM月dd日 HH点mm分" );
            }
            return time.ToString();
        }
        /// <summary>
        /// 格式化日期输出
        /// 1 ToString
        /// 2 ToShortDateString
        /// 3 yyyy年MM月dd日HH点mm分ss秒
        /// 4 yyyy年MM月dd日
        /// 5 yyyy年MM月dd日HH点mm分
        /// 6 yyyy-MM-dd HH:mm
        /// 7 yy年MM月dd日 HH点mm分
        /// </summary>
        /// <param name="oneDateVal"></param>
        /// <param name="formatType"></param>
        /// <returns></returns>
        public static string FormatDateTime( string oneDateVal , int formatType )
        {
            return FormatDateTime( DateTime.Parse( oneDateVal ) , formatType );
        }
        /// <summary>
        /// 计算模糊时间段，秒换算为X天X时X分X秒
        /// </summary>
        /// <param name="second">秒数</param>
        /// <returns></returns>
        public static string FormatSecondSpan( long second )
        {
            string str;
            TimeSpan span = TimeSpan.FromSeconds( ( double )second );
            if ( span.Days > 0 )
            {
                str = span.Days.ToString() + "天";
            }
            else
            {
                str = string.Empty;
            }
            if ( span.Hours > 0 )
            {
                str = str + span.Hours.ToString() + "时";
            }
            if ( span.Minutes > 0 )
            {
                str = str + span.Minutes.ToString() + "分";
            }
            if ( span.Seconds > 0 )
            {
                str = str + span.Seconds.ToString() + "秒";
            }
            return str;
        }
        /// <summary>
        /// 获取长日期字符串表示 yyyyMMddHHmmss000
        /// </summary>
        /// <returns></returns>
        public static string GetDateTimeLongString( )
        {
            DateTime now = DateTime.Now;
            return ( now.ToString( "yyyyMMddHHmmss" ) + now.Millisecond.ToString( "000" ) );
        }
        /// <summary>
        /// 获取长日期字符串表示 yyyyMMddHHmmss000,可以添加前缀
        /// </summary>
        /// <param name="prefix">前缀字符</param>
        /// <returns></returns>
        public static string GetDateTimeLongString( string prefix )
        {
            if ( string.IsNullOrEmpty( prefix ) )
            {
                prefix = string.Empty;
            }
            return ( prefix + GetDateTimeLongString() );
        }
        #endregion
        

        /*
        /// <summary>
        ///  获取一个文件(path)中引用的文件名称（包括文件扩展名）
        /// </summary>
        /// <param name="path"></param>
        /// <returns></returns>
        public static string GetFileNameFromPath( string path )
        {
            return Path.GetFileName( path );
        }
        /// <summary>
        /// 从 URL 中获取访问的文件名称
        /// </summary>
        /// <param name="url"></param>
        /// <returns></returns>
        public static string GetFilenameFromUrl( string url )
        {
            if ( string.IsNullOrEmpty( url ) )
            {
                return string.Empty;
            }
            string[ ] strArray = SplitStrArray( url , "/" );
            return strArray[ strArray.Length - 1 ].Split( new char[ ] { '?' } )[ 0 ];
        }*/

        public static string AddLast( string originalVal , string lastStr )
        {
            if ( originalVal.EndsWith( lastStr ) )
            {
                return originalVal;
            }
            return ( originalVal + lastStr );
        }
        public static string GetFullPath( string strPath )
        {
            string str = AddLast( AppDomain.CurrentDomain.BaseDirectory , @"\" );
            if ( strPath.IndexOf( ":" ) < 0 )
            {
                string str2 = strPath.Replace( @"..\" , "" );
                if ( str2 != strPath )
                {
                    int num = ( ( strPath.Length - str2.Length ) / @"..\".Length ) + 1;
                    for ( int i = 0; i < num; i++ )
                    {
                        str = str.Substring( 0 , str.LastIndexOf( @"\" ) );
                    }
                    str2 = @"\" + str2;
                }
                strPath = str + str2;
            }
            return strPath;
        }
        /// <summary>
        /// 获取一个目录的绝对路径（适用于WEB应用程序）
        /// </summary>
        /// <param name="folderPath"></param>
        /// <returns></returns>
        public static string GetMapPath( string folderPath )
        {
            if ( folderPath.IndexOf( @":\" ) > 0 )
            {
                return AddLast( folderPath , @"\" );
            }
            if ( folderPath.StartsWith( "~/" ) )
            {
                return AddLast( HttpContext.Current.Server.MapPath( folderPath ) , @"\" );
            }
            string str2 = HttpContext.Current.Request.ApplicationPath + "/";
            return AddLast( HttpContext.Current.Server.MapPath( str2 + folderPath ) , @"\" );
        }

        /// <summary>
        /// 处理文本图片路径（转换为物理领）
        /// </summary>
        /// <param name="content">文本内容</param>
        /// <returns></returns>
        public static string GetContentAboutPitcureUrl(string imgRuleUrlHead, string content)
        {
            MatchCollection mc = Regex.Matches(content, @"(/Upload/Crowdfunding/)\w+[.]([png]{3}|[jpg]{3}|[gif]{3})", RegexOptions.Multiline);
            foreach (System.Text.RegularExpressions.Match m in mc)
            {
                string pitcureUrl = m.ToString();
                content = content.Replace(pitcureUrl, imgRuleUrlHead + pitcureUrl);
            }
            MatchCollection mc2 = Regex.Matches(content, @"\S{27}\w{8}[/]\w{14}[_]\w{4}[.]([png]{3}|[jpg]{3}|[gif]{3})", RegexOptions.Multiline);
            foreach (System.Text.RegularExpressions.Match m in mc2)
            {
                string pitcureUrl = m.ToString();
                content = content.Replace(pitcureUrl, imgRuleUrlHead + pitcureUrl);
            }
            return content;
        }


        public static string GetRealPath( string strPath )
        {
            if ( string.IsNullOrEmpty( strPath ) )
            {
                throw new Exception( "strPath 不能为空！" );
            }
            HttpContext current = null;
            try
            {
                current = HttpContext.Current;
            }
            catch
            {
                current = null;
            }
            if ( current != null )
            {
                return current.Server.MapPath( strPath );
            }
            string str2 = Path.Combine( strPath , "" );
            str2 = str2.StartsWith( @"\\" ) ? str2.Remove( 0 , 2 ) : str2;
            return ( AppDomain.CurrentDomain.BaseDirectory + Path.Combine( strPath , "" ) );
        }
      /*  /// <summary>
        /// 全角转半角
        /// </summary>
        /// <param name="sbc"></param>
        /// <returns></returns>
        public static string GetSBCToCase( string sbc )
        {
            char[ ] chars = sbc.ToCharArray();
            for ( int i = 0; i < chars.Length; i++ )
            {
                byte[ ] bytes = Encoding.Unicode.GetBytes( chars , i , 1 );
                if ( ( bytes.Length == 2 ) && ( bytes[ 1 ] == 0xff ) )
                {
                    bytes[ 0 ] = ( byte )( bytes[ 0 ] + 0x20 );
                    bytes[ 1 ] = 0;
                    chars[ i ] = Encoding.Unicode.GetChars( bytes )[ 0 ];
                }
            }
            return new string( chars );
        }
        /// <summary>
        /// 半角转全角
        /// </summary>
        /// <param name="dbc"></param>
        /// <returns></returns>
        public static string GetDBCToCase( string dbc )
        {
            char[ ] chars = dbc.ToCharArray();
            for ( int i = 0; i < chars.Length; i++ )
            {
                byte[ ] bytes = Encoding.Unicode.GetBytes( chars , i , 1 );
                if ( ( bytes.Length == 2 ) && ( bytes[ 1 ] == 0 ) )
                {
                    bytes[ 0 ] = ( byte )( bytes[ 0 ] - 0x20 );
                    bytes[ 1 ] = 0xff;
                    chars[ i ] = Encoding.Unicode.GetChars( bytes )[ 0 ];
                }
            }
            return new string( chars );
        }*/
        /// <summary>
        /// 判断字符串1，是否存在于字符串数组中
        /// </summary>
        /// <param name="matchStr">要匹配的字符串</param>
        /// <param name="strArray">字符串数组</param>
        /// <returns>存在返回是 true,否 false</returns>
        public static bool InArray( string matchStr , string[ ] strArray )
        {
            if ( !string.IsNullOrEmpty( matchStr ) )
            {
                for ( int i = 0; i < strArray.Length; i++ )
                {
                    if ( matchStr == strArray[ i ] )
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public static bool InArray( string matchStr , string originalStr , string separator )
        {
            string[ ] strArray = SplitStrArray( originalStr , separator );
            for ( int i = 0; i < strArray.Length; i++ )
            {
                if ( matchStr == strArray[ i ] )
                {
                    return true;
                }
            }
            return false;
        }

        public static bool InArray( string matchStr , string[ ] strArray , bool ignoreCase )
        {
            return ( InArrayIndexOf( matchStr , strArray , ignoreCase ) >= 0 );
        }

        public static bool InArray( string matchStr , string strArray , string separator , bool ignoreCase )
        {
            return InArray( matchStr , SplitStrArray( strArray , separator ) , ignoreCase );
        }

        public static int InArrayIndexOf( string originalStr , string[ ] strArray )
        {
            return InArrayIndexOf( originalStr , strArray , true );
        }

        public static int InArrayIndexOf( string originalStr , string[ ] strArray , bool ignoreCase )
        {
            for ( int i = 0; i < strArray.Length; i++ )
            {
                if ( ignoreCase )
                {
                    if ( originalStr.ToLower() == strArray[ i ].ToLower() )
                    {
                        return i;
                    }
                }
                else if ( originalStr == strArray[ i ] )
                {
                    return i;
                }
            }
            return -1;
        }

        public static bool InIPArray( string ip , string[ ] ipArray )
        {
            if ( !string.IsNullOrEmpty( ip ) && Validate.IsIP( ip ) )
            {
                string[ ] strArray = SplitStrArray( ip , "." );
                for ( int i = 0; i < ipArray.Length; i++ )
                {
                    string[ ] strArray2 = SplitStrArray( ipArray[ i ] , "." );
                    int num2 = 0;
                    for ( int j = 0; j < strArray2.Length; j++ )
                    {
                        if ( strArray2[ j ] == "*" )
                        {
                            return true;
                        }
                        if ( ( strArray.Length <= j ) || ( strArray2[ j ] != strArray[ j ] ) )
                        {
                            break;
                        }
                        num2++;
                    }
                    if ( num2 == 4 )
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public static string JavaScriptEncode( object obj )
        {
            if ( null == obj )
            {
                return string.Empty;
            }
            return JavaScriptEncode( obj.ToString() );
        }

        public static string JavaScriptEncode( string originalStr )
        {
            if ( string.IsNullOrEmpty( originalStr ) )
            {
                return string.Empty;
            }
            StringBuilder builder = new StringBuilder( originalStr );
            return builder.Replace( @"\" , @"\\" ).Replace( "/" , @"\/" ).Replace( "'" , @"\'" ).Replace( "\"" , "\\\"" ).Replace( "\r\n" , "\r" ).Replace( "\r" , @"\r" ).ToString();
        }

        public static string Join( string separator , params string[ ] value )
        {
            return JoinString( separator , value );
        }

        public static string JoinString( params string[ ] value )
        {
            return JoinString( string.Empty , value );
        }

        private static string JoinString( string separator , params string[ ] value )
        {
            if ( null == value )
            {
                throw new ArgumentNullException( "value" );
            }
            if ( 0 == value.Length )
            {
                return string.Empty;
            }
            return string.Join( separator , value );
        }

        public static int Length( string originalVal )
        {
            return Encoding.Default.GetBytes( originalVal ).Length;
        }
        /*
        public static string NowDateTime()
        {
            return DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
        }

        public static string NowDateTimeF()
        {
            return DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss:fffffff");
        }

        public static string NowTime()
        {
            return DateTime.Now.ToString("HH:mm:ss");
        }
        */
        public static string RegexReplaceTags( string originalStr , string specialChares , params object[ ] entityClasses )
        {
            string name = "";
            string pattern = "";
            string replacement = "";
            foreach ( object obj2 in entityClasses )
            {
                foreach ( PropertyInfo info in obj2.GetType().GetProperties() )
                {
                    name = info.Name;
                    pattern = specialChares + name + specialChares;
                    replacement = info.GetValue( obj2 , null ).ToString();
                    originalStr = Regex.Replace( originalStr , pattern , replacement , RegexOptions.IgnoreCase );
                }
            }
            return originalStr;
        }
        /*
        public static string Remove( string originalStr , int startIndex )
        {
            return Remove( originalStr , startIndex , false );
        }

        public static string Remove( string originalStr , int startIndex , RemoveTextMode mode )
        {
            if ( mode != RemoveTextMode.ByteMode )
            {
                return Remove( originalStr , startIndex );
            }
            startIndex *= 2;
            if ( originalStr.Length > startIndex )
            {
                originalStr = originalStr.Remove( startIndex );
            }
            if ( Encoding.Default.GetBytes( originalStr ).Length > startIndex )
            {
                originalStr = RemoveByteMode( originalStr , startIndex );
            }
            return originalStr;
        }

        public static string Remove( string originalStr , int startIndex , bool hasProlong )
        {
            if ( originalStr.Length <= startIndex )
            {
                return originalStr;
            }
            if ( !hasProlong )
            {
                return originalStr.Remove( startIndex );
            }
            return ( originalStr.Remove( startIndex ) + PROLONG_SYMBOL );
        }

        public static string Remove( string originalStr , int startIndex , RemoveTextMode mode , bool hasProlong )
        {
            if ( mode != RemoveTextMode.ByteMode )
            {
                return ( Remove( originalStr , startIndex ) + ( hasProlong ? PROLONG_SYMBOL : null ) );
            }
            startIndex *= 2;
            if ( originalStr.Length > startIndex )
            {
                originalStr = originalStr.Remove( startIndex );
            }
            if ( Encoding.Default.GetBytes( originalStr ).Length > startIndex )
            {
                originalStr = RemoveByteMode( originalStr , startIndex );
            }
            return ( originalStr + ( hasProlong ? PROLONG_SYMBOL : null ) );
        }

        private static string RemoveByteMode( string originalStr , int startIndex )
        {
            StringBuilder builder = new StringBuilder( originalStr , originalStr.Length );
            for ( int i = 0; i < builder.Length; i++ )
            {
                if ( Encoding.Default.GetBytes( builder.ToString() ).Length <= startIndex )
                {
                    break;
                }
                builder.Remove( builder.Length - 1 , 1 );
            }
            return builder.ToString();
        }
        */
        public static string RepeatStr( string repeatStr , int repeatCount )
        {
            StringBuilder builder = new StringBuilder( repeatCount );
            for ( int i = 0; i < repeatCount; i++ )
            {
                builder.Append( repeatStr );
            }
            return builder.ToString();
        }
        /// <summary>
        /// 替换非中文字符
        /// </summary>
        /// <param name="originalVal"></param>
        /// <returns></returns>
        public static string ReplaceCnChar( string originalVal )
        {
            if ( string.IsNullOrEmpty( originalVal ) )
            {
                return string.Empty;
            }
            return Regex.Replace( originalVal , @"[^\u4E00-\u9FA5]" , "" );
        }
        /// <summary>
        /// 替换搜索引擎Lucene指认的特殊字符,<![CDATA["+-,&&,||!(){}[]^"~*?:\"]]>
        /// </summary>
        /// <param name="originalVal"></param>
        /// <returns></returns>
        public static string ReplaceLuceneSpecialChar( string originalVal )
        {
            if ( string.IsNullOrEmpty( originalVal ) )
            {
                return string.Empty;
            }
            StringBuilder builder = new StringBuilder( originalVal );
            builder.Replace( "+" , string.Empty );
            builder.Replace( "-" , string.Empty );
            builder.Replace( "&&" , string.Empty );
            builder.Replace( "||" , string.Empty );
            builder.Replace( "!" , string.Empty );
            builder.Replace( "(" , string.Empty );
            builder.Replace( ")" , string.Empty );
            builder.Replace( "{" , string.Empty );
            builder.Replace( "}" , string.Empty );
            builder.Replace( "[" , string.Empty );
            builder.Replace( "]" , string.Empty );
            builder.Replace( "^" , string.Empty );
            builder.Replace( "\"" , string.Empty );
            builder.Replace( "~" , string.Empty );
            builder.Replace( "*" , string.Empty );
            builder.Replace( "?" , string.Empty );
            builder.Replace( ":" , string.Empty );
            builder.Replace( @"\" , string.Empty );
            return builder.ToString();
        }

        public static string ReplaceStrUseSC( string originalStr , StringCollection sc )
        {
            if ( string.IsNullOrEmpty( originalStr ) )
            {
                return string.Empty;
            }
            foreach ( string str in sc )
            {
                originalStr = Regex.Replace( originalStr , str , "*".PadLeft( str.Length , '*' ) , RegexOptions.IgnoreCase );
            }
            return originalStr;
        }

        public static string ReplaceStrUseSC( string originalStr , string[ ] sc )
        {
            if ( string.IsNullOrEmpty( originalStr ) )
            {
                return string.Empty;
            }
            foreach ( string str in sc )
            {
                originalStr = Regex.Replace( originalStr , str , "*".PadLeft( str.Length , '*' ) , RegexOptions.IgnoreCase );
            }
            return originalStr;
        }

        public static string ReplaceStrUseStr( string originalStr , string replacedStr , string replaceStr )
        {
            if ( string.IsNullOrEmpty( originalStr ) )
            {
                return string.Empty;
            }
            return Regex.Replace( originalStr , replacedStr , replaceStr , RegexOptions.IgnoreCase );
        }
        /// <summary>
        ///  用字符串(separator)把给定的字符串(originalStr)分割成字符数组
        /// </summary>
        /// <param name="originalStr"></param>
        /// <param name="separator"></param>
        /// <returns></returns>
        public static string[ ] SplitStrArray( string originalStr , string separator )
        {
            if ( originalStr.IndexOf( separator ) < 0 )
            {
                return new string[ ] { originalStr };
            }
            return Regex.Split( originalStr , separator.Replace( "." , @"\." ) , RegexOptions.IgnoreCase );
        }
        /// <summary>
        /// 分割文本内容 - 按行分割 <![CDATA[<br />,<p>]]>
        /// </summary>
        /// <param name="originalContent"></param>
        /// <param name="splitLines">分割行数</param>
        /// <returns></returns>
        public static string SplitStrUseLines( string originalContent , int splitLines )
        {
            if ( string.IsNullOrEmpty( originalContent ) )
            {
                return string.Empty;
            }
            string str = string.Empty;
            int startIndex = 1;
            int num2 = 0;
            int num3 = originalContent.Length - 5;
            startIndex = 1;
            while ( startIndex < num3 )
            {
                if ( originalContent.Substring( startIndex , 6 ).ToLower().Equals( "<br />" ) )
                {
                    num2++;
                }
                if ( originalContent.Substring( startIndex , 5 ).ToLower().Equals( "<br/>" ) )
                {
                    num2++;
                }
                if ( originalContent.Substring( startIndex , 4 ).ToLower().Equals( "<br>" ) )
                {
                    num2++;
                }
                if ( originalContent.Substring( startIndex , 3 ).ToLower().Equals( "<p>" ) )
                {
                    num2++;
                }
                if ( num2 >= splitLines )
                {
                    break;
                }
                startIndex++;
            }
            if ( num2 >= splitLines )
            {
                if ( startIndex == num3 )
                {
                    str = originalContent.Substring( 0 , startIndex - 1 );
                }
                else
                {
                    str = originalContent.Substring( 0 , startIndex );
                }
                return str;
            }
            return originalContent;
        }
        /// <summary>
        /// 用字符串(separator)分割给定的字符串(originalStr)
        /// </summary>
        /// <param name="originalStr">原始字符串</param>
        /// <param name="separator">分割字符串</param>
        /// <returns></returns>
        public static string SplitStrUseStr( string originalStr , string separator )
        {
            StringBuilder builder = new StringBuilder();
            builder.Append( separator );
            for ( int i = 0; i < originalStr.Length; i++ )
            {
                builder.Append( originalStr[ i ] );
                builder.Append( separator );
            }
            return builder.ToString();
        }

        public static string SqlEncode( string strSQL )
        {
            if ( string.IsNullOrEmpty( strSQL ) )
            {
                return string.Empty;
            }
            return strSQL.Trim().Replace( "'" , "''" );
        }
        /// <summary>
        ///  文本解码
        /// </summary>
        /// <param name="originalStr"></param>
        /// <returns></returns>
        public static string TextDecode( string originalStr )
        {
            StringBuilder builder = new StringBuilder( originalStr );
            builder.Replace( "<br/><br/>" , "\r\n" );
            builder.Replace( "<br/>" , "\r" );
            builder.Replace( "<p></p>" , "\r\n\r\n" );
            return builder.ToString();
        }
        /// <summary>
        /// 文本编码,回车/换行符 到 HTML 转换
        /// </summary>
        /// <param name="originalStr"></param>
        /// <returns></returns>
        public static string TextEncode( string originalStr )
        {
            if ( string.IsNullOrEmpty( originalStr ) )
            {
                return string.Empty;
            }
            StringBuilder builder = new StringBuilder( originalStr );
            builder.Replace( "\r\n" , "<br />" );
            builder.Replace( "\n" , "<br />" );
            return builder.ToString();
        }

        /// <summary>
        ///  转换字符串首字符为小写字符(对英文字符有效)
        /// </summary>
        /// <param name="originalVal"></param>
        /// <returns></returns>
        public static string TransformFirstToLower( string originalVal )
        {
            if ( string.IsNullOrEmpty( originalVal ) )
            {
                return originalVal;
            }
            if ( originalVal.Length >= 2 )
            {
                return ( originalVal.Substring( 0 , 1 ).ToLower() + originalVal.Substring( 1 ) );
            }
            return originalVal.ToUpper();
        }
        /// <summary>
        /// 转换字符串首字符为大写字符(对英文字符有效)
        /// </summary>
        /// <param name="originalVal">原始字符串</param>
        /// <returns></returns>
        public static string TransformFirstToUpper( string originalVal )
        {
            if ( string.IsNullOrEmpty( originalVal ) )
            {
                return originalVal;
            }
            if ( originalVal.Length >= 2 )
            {
                return ( originalVal.Substring( 0 , 1 ).ToUpper() + originalVal.Substring( 1 ) );
            }
            return originalVal.ToUpper();
        }
    }
}

