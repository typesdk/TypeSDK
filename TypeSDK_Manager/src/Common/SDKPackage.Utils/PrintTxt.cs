using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace SDKPackage.Utils
{
    public class PrintTxt
    {
        /// <summary>
        /// 记录日志
        /// </summary>
        /// <param name="path">文件夹路径</param>
        /// <param name="date">内容</param>
        public static void RecordLog(string path, string date)
        {
            string fileName = Path.Combine(path, DateTime.Now.ToString("yyyy-MM-dd") + @".txt");
            using (StreamWriter sw = new StreamWriter(fileName, true))
            {
                sw.WriteLine(date);
            }
        }
    }
}
