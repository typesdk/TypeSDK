using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security.Cryptography;
using System.IO;
using System.Configuration;

namespace SDKPackage.Utils
{
    public class SymmetricMethod
    {
        private SymmetricAlgorithm mobjCryptoService;
        
        /// <summary>
        /// 对称加密类的构造函数
        /// </summary>
        public SymmetricMethod( )
        {
            mobjCryptoService = new RijndaelManaged();            
        }
        /// <summary>
        /// 获得密钥
        /// </summary>
        /// <returns>密钥</returns>
        private byte[ ] GetLegalKey( )
        {
            string sTemp = "A7Df09!325Bg6A5aB@40ahkFCklAuB4D#40Dqy0D7oD8$AvB8Dd6b%aDa8Ae8709*44D41d";
            mobjCryptoService.GenerateKey();
            byte[ ] bytTemp = mobjCryptoService.Key;
            int KeyLength = bytTemp.Length;
            if ( sTemp.Length > KeyLength )
                sTemp = sTemp.Substring( 0 , KeyLength );
            else if ( sTemp.Length < KeyLength )
                sTemp = sTemp.PadRight( KeyLength , ' ' );
            return ASCIIEncoding.ASCII.GetBytes( sTemp );
        }
        /// <summary>
        /// 获得初始向量IV
        /// </summary>
        /// <returns>初试向量IV</returns>
        private byte[ ] GetLegalIV( )
        {
            string sTemp = "GF46dD87%AgD2(3FjC467Bk%&B241A95Fk&7tD3452f*96b4465(e797fAa44A6be8Aa259";
            mobjCryptoService.GenerateIV();
            byte[ ] bytTemp = mobjCryptoService.IV;
            int IVLength = bytTemp.Length;
            if ( sTemp.Length > IVLength )
                sTemp = sTemp.Substring( 0 , IVLength );
            else if ( sTemp.Length < IVLength )
                sTemp = sTemp.PadRight( IVLength , ' ' );
            return ASCIIEncoding.ASCII.GetBytes( sTemp );
        }
        /// <summary>
        /// 加密方法
        /// </summary>
        /// <param name="Source">待加密的串</param>
        /// <returns>经过加密的串</returns>
        public string Encrypto( string Source )
        {
            if ( string.IsNullOrEmpty( Source ) )
                return "";
            byte[ ] bytIn = UTF8Encoding.UTF8.GetBytes( Source );
            MemoryStream ms = new MemoryStream();
            mobjCryptoService.Key = GetLegalKey();
            mobjCryptoService.IV = GetLegalIV();
            ICryptoTransform encrypto = mobjCryptoService.CreateEncryptor();
            CryptoStream cs = new CryptoStream( ms , encrypto , CryptoStreamMode.Write );
            cs.Write( bytIn , 0 , bytIn.Length );
            cs.FlushFinalBlock();
            ms.Close();
            byte[ ] bytOut = ms.ToArray();
            return Convert.ToBase64String( bytOut );
        }
        /// <summary>
        /// 解密方法
        /// </summary>
        /// <param name="Source">待解密的串</param>
        /// <returns>经过解密的串</returns>
        public string Decrypto( string Source )
        {
            if ( string.IsNullOrEmpty( Source ) )
                return "";
            try
            {
                byte[ ] bytIn = Convert.FromBase64String( Source );
                MemoryStream ms = new MemoryStream( bytIn , 0 , bytIn.Length );
                mobjCryptoService.Key = GetLegalKey();
                mobjCryptoService.IV = GetLegalIV();
                ICryptoTransform encrypto = mobjCryptoService.CreateDecryptor();
                CryptoStream cs = new CryptoStream( ms , encrypto , CryptoStreamMode.Read );
                StreamReader sr = new StreamReader( cs );
                return sr.ReadToEnd();
            }
            catch
            {
                return "";
            }
        }         
       
    }
}
