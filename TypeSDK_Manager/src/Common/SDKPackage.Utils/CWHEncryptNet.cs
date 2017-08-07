namespace SDKPackage.Utils
{
    using System;
    using System.Text;
    /// <summary>
    /// 异或对称加密
    /// </summary>
    public sealed class CWHEncryptNet
    {
        private static ushort ENCRYPT_KEY_LEN = 8;
        private static ushort MAX_ENCRYPT_LEN = ((ushort) (MAX_SOURCE_LEN * XOR_TIMES));
        private static ushort MAX_SOURCE_LEN = 0x40;
        private static ushort XOR_TIMES = 8;

        private CWHEncryptNet()
        {
        }
        /// <summary>
        /// 解密
        /// </summary>
        /// <param name="encrypData"></param>
        /// <returns></returns>
        public static string XorCrevasse(string encrypData)
        {
            StringBuilder builder = new StringBuilder();
            ushort length = (ushort) encrypData.Length;
            if (length < (ENCRYPT_KEY_LEN * 8))
            {
                return "";
            }
            ushort num2 = Convert.ToUInt16(encrypData.Substring(0, 4), 0x10);
            if (length != (((((num2 + ENCRYPT_KEY_LEN) - 1) / ENCRYPT_KEY_LEN) * ENCRYPT_KEY_LEN) * 8))
            {
                return "";
            }
            for (int i = 0; i < num2; i++)
            {
                string str2 = "";
                string str = "";
                str2 = encrypData.Substring(i * 8, 4);
                str = encrypData.Substring((i * 8) + 4, 4);
                ushort num4 = Convert.ToUInt16(str2, 0x10);
                ushort num5 = Convert.ToUInt16(str, 0x10);
                builder.Append((char) (num4 ^ num5));
            }
            return builder.ToString();
        }
        /// <summary>
        /// 加密
        /// </summary>
        /// <param name="sourceData"></param>
        /// <returns></returns>
        public static string XorEncrypt(string sourceData)
        {
            StringBuilder builder = new StringBuilder();
            ushort[] numArray = new ushort[ENCRYPT_KEY_LEN];
            numArray[0] = (ushort) sourceData.Length;
            Random random = new Random();
            for (int i = 1; i < numArray.Length; i++)
            {
                numArray[i] = (ushort) (random.Next(0, 0xffff) % 0xffff);
            }
            ushort num2 = 0;
            ushort num3 = (ushort) ((((numArray[0] + ENCRYPT_KEY_LEN) - 1) / ENCRYPT_KEY_LEN) * ENCRYPT_KEY_LEN);
            for (ushort j = 0; j < num3; j = (ushort) (j + 1))
            {
                if (j < numArray[0])
                {
                    num2 = (ushort) (sourceData[j] ^ numArray[j % ENCRYPT_KEY_LEN]);
                }
                else
                {
                    num2 = (ushort) (numArray[j % ENCRYPT_KEY_LEN] ^ ((ushort) (random.Next(0, 0xffff) % 0xffff)));
                }
                builder.Append(numArray[j % ENCRYPT_KEY_LEN].ToString("X4")).Append(num2.ToString("X4"));
            }
            return builder.ToString();
        }
    }
}

