using System;
using System.Collections;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Text;
using System.Xml;

namespace SDKPackage.Facade
{
    public class PasswordCard 
    {
        private int _serialNumber;

        public int SerialNumber
        {
            get
            {
                return _serialNumber;
            }
            set
            {
                _serialNumber = value;
            }
        }

        /// <summary>
        /// 构造函数
        /// </summary>
        public PasswordCard()
        {
        }

        ///// <summary>
        ///// 生成序列号
        ///// </summary>
        //public void CreateSerialNumber( )
        //{
        //    string numberList;
        //    bool j = true;
        //    while ( j )
        //    {
        //        numberList = string.Empty;
        //        Random rd = new Random( );
        //        for ( int i = 0; i < 9; i++ )
        //        {
        //            numberList = numberList + rd.Next( 1 , 9 ).ToString( );
        //        }
        //        AccountsFacade accountsFacade = new AccountsFacade( );
        //        if ( !accountsFacade.PasswordIDIsEnable( numberList ) )
        //        {
        //            SerialNumber = Convert.ToInt32( numberList );
        //            j = false;
        //        }
        //    }
        //}

        /// <summary>
        /// 将序列号串每三个数字间插入一个空格，如789521321返回789 521 321。
        /// </summary>
        /// <returns></returns>
        public string AddSpace( )
        {
            string str = SerialNumber.ToString( );
            return str.Substring( 0 , 3 ) + " " + str.Substring( 3 , 3 ) + " " + str.Substring( 6 , 3 );
        }

        /// <summary>
        /// 根据坐标对应的密保卡信息
        /// </summary>
        /// <param name="coordiNate">密保卡坐标</param>
        /// <returns></returns>
        public string GetNumberByCoordinate( string coordinate )
        {
            int number = Convert.ToInt32( System.Configuration.ConfigurationSettings.AppSettings[coordinate] );
            number = ( SerialNumber / number ) % 1000;
            string str = number.ToString( );
            switch ( str.Length )
            {
                case 1:
                    str = str + "00";
                    break;
                case 2:
                    str = str + "0";
                    break;
                default:
                    break;
            }
            return str;
        }

        /// <summary>
        /// 随机三个密保卡坐标
        /// </summary>
        /// <returns></returns>
        public string[] RandomString( )
        {
            string[] str = new string[3];
            string[] x = new string[] {"1","2","3","4" };
            string[] y = new string[] {"A","B","C","D","E","F" };
            Random rad=new Random();
            bool j = true;
            while ( j )
            {
                for ( int i = 0; i < 3; i++ )
                {
                    string xString = x[rad.Next( 0 , 3 )];
                    string yString = y[rad.Next( 0 , 5 )];
                    str[i] = yString + xString;
                }
                j = false;
                if ( str[0] == str[1] || str[0] == str[2] || str[1] == str[2] )
                {
                    j = true;
                }
            }
            return str;
        }

        /// <summary>
        /// 输出密保卡图片字节流
        /// </summary>
        /// <param name="bgPath">图片背景路径</param>
        public byte[] WritePasswordCardImg( string bgPath )
        {
            System.Drawing.Image newImage = System.Drawing.Image.FromFile( bgPath );
            Graphics g = Graphics.FromImage( newImage );
            System.Drawing.Drawing2D.LinearGradientBrush brush = new System.Drawing.Drawing2D.LinearGradientBrush( new Rectangle( 0 , 0 , newImage.Width , newImage.Height ) , Color.Black , Color.Green , 4.0f , true );
            Font font = new System.Drawing.Font( "Arial" , 10 );
            Font fontBold = new System.Drawing.Font( "Arial" , 10 , FontStyle.Bold );
            g.DrawString( AddSpace( ) , fontBold , brush , 65 , 8 );
            string[] coordinate = new string[] { "A1" , "A2" , "A3" , "A4" , "B1" , "B2" , "B3" , "B4" , "C1" , "C2" , "C3" , "C4" , "D1" , "D2" , "D3" , "D4" , "E1" , "E2" , "E3" , "E4" , "F1" , "F2" , "F3" , "F4" };
            int x = 85;
            int y = 72;
            int arrayNumber = 0;
            string str = string.Empty;
            for ( int i = 0; i < 6; i++ )
            {
                for ( int j = 0; j < 4; j++ )
                {
                    str = GetNumberByCoordinate( coordinate[arrayNumber] );
                    g.DrawString( str , font , brush , x , y );
                    arrayNumber = arrayNumber + 1;
                    x = x + 90;
                }
                x = 85;
                y = y + 32;
            }
            System.IO.MemoryStream ms = new System.IO.MemoryStream( );
            newImage.Save( ms , System.Drawing.Imaging.ImageFormat.Jpeg );
            newImage.Dispose( );
            g.Dispose( );
            byte[] bs = ms.ToArray( );
            return bs;
        }
    }
}
