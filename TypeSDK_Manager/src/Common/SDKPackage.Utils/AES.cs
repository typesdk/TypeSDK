namespace SDKPackage.Utils
{
    using System;
    using System.Security.Cryptography;
    using System.Text;
    /// <summary>
    /// 高级加密标准 (AES) 算法（又称为 Rijndael）
    /// </summary>
    public sealed class AES
    {
        private static byte[ ] Keys = new byte[ ] { 0x41 , 0x72 , 0x65 , 0x79 , 0x6f , 0x75 , 0x6d , 0x79 , 0x53 , 110 , 0x6f , 0x77 , 0x6d , 0x61 , 110 , 0x3f };

        private AES( )
        {
        }
        /// <summary>
        /// 解密
        /// </summary>
        /// <param name="cipherText">待解密的文本</param>
        /// <param name="cipherkey">密钥</param>
        /// <returns>返回与此实例等效的解密文本</returns>
        public static string Decrypt( string cipherText , string cipherkey )
        {
            try
            {
                cipherkey = TextUtility.CutLeft( cipherkey , 32 );
                cipherkey = cipherkey.PadRight( 32 , ' ' );
                ICryptoTransform transform = new RijndaelManaged { Key = Encoding.UTF8.GetBytes( cipherkey ) , IV = Keys }.CreateDecryptor();
                byte[ ] inputBuffer = Convert.FromBase64String( cipherText );
                byte[ ] bytes = transform.TransformFinalBlock( inputBuffer , 0 , inputBuffer.Length );
                return Encoding.UTF8.GetString( bytes );
            }
            catch
            {
                return "";
            }
        }
        /// <summary>
        /// 解密
        /// </summary>
        /// <param name="cipherText">待解密的字节</param>
        /// <param name="cipherkey">密钥</param>
        /// <returns>返回与此实例等效的解密字节</returns>
        public static byte[ ] DecryptBuffer( byte[ ] cipherText , string cipherkey )
        {
            try
            {
                cipherkey = TextUtility.CutLeft( cipherkey , 32 );
                cipherkey = cipherkey.PadRight( 32 , ' ' );
                RijndaelManaged managed = new RijndaelManaged
                {
                    Key = Encoding.UTF8.GetBytes( cipherkey ) ,
                    IV = Keys
                };
                return managed.CreateDecryptor().TransformFinalBlock( cipherText , 0 , cipherText.Length );
            }
            catch
            {
                return null;
            }
        }
        /// <summary>
        /// 加密
        /// </summary>
        /// <param name="plainText">待加密的文本</param>
        /// <param name="cipherkey">密钥</param>
        /// <returns>返回与此实例等效的加密文本</returns>
        public static string Encrypt( string plainText , string cipherkey )
        {
            cipherkey = TextUtility.CutLeft( cipherkey , 32 );
            cipherkey = cipherkey.PadRight( 32 , ' ' );
            ICryptoTransform transform = new RijndaelManaged { Key = Encoding.UTF8.GetBytes( cipherkey.Substring( 0 , 32 ) ) , IV = Keys }.CreateEncryptor();
            byte[ ] bytes = Encoding.UTF8.GetBytes( plainText );
            return Convert.ToBase64String( transform.TransformFinalBlock( bytes , 0 , bytes.Length ) );
        }
        /// <summary>
        /// 加密
        /// </summary>
        /// <param name="plainText">待加密的字节</param>
        /// <param name="cipherkey">密钥</param>
        /// <returns>返回与此实例等效的加密字节</returns>
        public static byte[ ] EncryptBuffer( byte[ ] plainText , string cipherkey )
        {
            cipherkey = TextUtility.CutLeft( cipherkey , 0x20 );
            cipherkey = cipherkey.PadRight( 0x20 , ' ' );
            RijndaelManaged managed = new RijndaelManaged
            {
                Key = Encoding.UTF8.GetBytes( cipherkey.Substring( 0 , 0x20 ) ) ,
                IV = Keys
            };
            return managed.CreateEncryptor().TransformFinalBlock( plainText , 0 , plainText.Length );
        }
    }
}

