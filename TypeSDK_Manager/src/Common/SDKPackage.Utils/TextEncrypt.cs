namespace SDKPackage.Utils
{
    using System;
    using System.Security.Cryptography;
    using System.Text;

    public enum MD5ResultMode : byte
    {
        Strong = 0 ,
        Weak = 1
    }

    /// <summary>
    /// 在应用程序中定义用于单向加密文本的方法
    /// </summary>
    public class TextEncrypt
    {
        private TextEncrypt()
        {
        }
        /// <summary>
        ///  Base64 解码
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public static string Base64Decode(string message)
        {
            byte[] bytes = Convert.FromBase64String(message);
            return Encoding.UTF8.GetString(bytes);
        }
        /// <summary>
        /// Base64 编码
        /// </summary>
        /// <param name="message"></param>
        /// <returns></returns>
        public static string Base64Encode(string message)
        {
            return Convert.ToBase64String(Encoding.UTF8.GetBytes(message));
        }
        /// <summary>
        ///  DSA 加密
        /// </summary>
        /// <param name="password">要加密的字符串</param>
        /// <returns></returns>
        public static string DSAEncryptPassword(string password)
        {
            if (password == null)
            {
                throw new ArgumentNullException("password");
            }
            DSACryptoServiceProvider provider = new DSACryptoServiceProvider();
            string str = BitConverter.ToString(provider.SignData(Encoding.UTF8.GetBytes(password)));
            provider.Clear();
            return str.Replace("-", null);
        }
        /// <summary>
        /// MD5 加密
        /// </summary>
        /// <param name="password">要加密的字符串</param>
        /// <returns></returns>
        public static string EncryptPassword(string password)
        {
            if (password == null)
            {
                throw new ArgumentNullException("password");
            }
            return MD5EncryptPassword(password);
        }
        /// <summary>
        /// MD5 加密
        /// </summary>
        /// <param name="password">要加密的字符串</param>
        /// <returns></returns>
        public static string MD5EncryptPassword(string password)
        {
            if (password == null)
            {
                throw new ArgumentNullException("password");
            }
            return MD5EncryptPassword(password, MD5ResultMode.Strong);
        }
        /// <summary>
        /// MD5 加密
        /// </summary>
        /// <param name="password">要加密的字符串</param>
        /// <param name="mode">加密强度</param>
        /// <returns></returns>
        public static string MD5EncryptPassword(string password, MD5ResultMode mode)
        {
            if (password == null)
            {
                throw new ArgumentNullException("password");
            }
            MD5CryptoServiceProvider provider = new MD5CryptoServiceProvider();
            string str = BitConverter.ToString(provider.ComputeHash(Encoding.UTF8.GetBytes(password)));
            provider.Clear();
            if (mode != MD5ResultMode.Strong)
            {
                return str.Replace("-", null).Substring(8, 0x10);
            }
            return str.Replace("-", null);
        }
        /// <summary>
        /// SHA1 加密
        /// </summary>
        /// <param name="password">要加密的字符串</param>
        /// <returns></returns>
        public static string SHA1EncryptPassword(string password)
        {
            if (password == null)
            {
                throw new ArgumentNullException("password");
            }
            SHA1CryptoServiceProvider provider = new SHA1CryptoServiceProvider();
            string str = BitConverter.ToString(provider.ComputeHash(Encoding.UTF8.GetBytes(password)));
            provider.Clear();
            return str.Replace("-", null);
        }
        /// <summary>
        /// SHA256 加密
        /// </summary>
        /// <param name="password">要加密的字符串</param>
        /// <returns></returns>
        public static string SHA256(string password)
        {
            byte[] bytes = Encoding.UTF8.GetBytes(password);
            SHA256Managed managed = new SHA256Managed();
            return Convert.ToBase64String(managed.ComputeHash(bytes));
        }
    }
}

