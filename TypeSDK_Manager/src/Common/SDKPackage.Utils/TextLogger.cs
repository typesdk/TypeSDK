namespace SDKPackage.Utils
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Text;
    /// <summary>
    /// 文本日志文件操作类
    /// </summary>
    public static class TextLogger
    {
        public static readonly string APP_LOG_DIRECTORY = Utility.GetAppLogDirectory;
        private static string LOG_SUFFIX = "Log.config";
        private static readonly bool WRITE_APP_LOG = Utility.GetWriteAppLog;
        /// <summary>
        /// 删除文件
        /// </summary>
        /// <param name="path"></param>
        /// <returns></returns>
        public static bool DeleteFile(string path)
        {
            bool flag = false;
            try
            {
                new FileInfo(path).Delete();
                flag = true;
            }
            catch (Exception exception)
            {
                Write(exception.ToString());
            }
            return flag;
        }
        /// <summary>
        /// 获取日志目录下文件列表
        /// </summary>
        /// <returns></returns>
        public static SortedList<SortDateTime, string> GetFileList()
        {
            SortedList<SortDateTime, string> list = new SortedList<SortDateTime, string>();
            DirectoryInfo info = new DirectoryInfo(APP_LOG_DIRECTORY);
            foreach (FileSystemInfo info2 in info.GetFileSystemInfos())
            {
                if (info2.Attributes != FileAttributes.Directory)
                {
                    list.Add(new SortDateTime(info2.LastWriteTime), info2.Name);
                }
            }
            return list;
        }
        /// <summary>
        /// 读取文件日志
        /// </summary>
        /// <returns></returns>
        public static List<TextLoggerEntity> GetTextLogger()
        {
            string filePath = "";
            if (GetFileList().Count > 0)
            {
                filePath = Path.Combine(APP_LOG_DIRECTORY, GetFileList().Values[0]);
            }
            return GetTextLogger(filePath);
        }
        /// <summary>
        /// 读取文件日志
        /// </summary>
        /// <param name="filePath">文件物理路径</param>
        /// <returns></returns>
        public static List<TextLoggerEntity> GetTextLogger(string filePath)
        {
            List<TextLoggerEntity> list = new List<TextLoggerEntity>();
            string[] strArray = LoadFile(filePath).Split(new char[] { '\n' }, StringSplitOptions.RemoveEmptyEntries);
            for (int i = 0; i < strArray.Length; i++)
            {
                string[] strArray2 = strArray[i].Split(new char[] { '|' });
                list.Add(new TextLoggerEntity(DateTime.Parse(strArray2[0].Trim()), strArray2[1], strArray2[2], strArray2[3]));
            }
            return list;
        }
        /// <summary>
        /// 返回文件内容
        /// </summary>
        /// <param name="path">文件件物理路径</param>
        /// <returns></returns>
        public static string LoadFile(string path)
        {
            if (!File.Exists(path))
            {
                return "";
            }
            FileStream stream = new FileStream(path, FileMode.Open, FileAccess.Read, FileShare.Read);
            if (stream == null)
            {
                throw new IOException("Unable to open the file: " + path);
            }
            StreamReader reader = new StreamReader(stream, Encoding.UTF8);
            string str = reader.ReadToEnd();
            reader.Close();
            return str;
        }
        /// <summary>
        /// 写日志文件
        /// </summary>
        /// <param name="logContent">日志内容</param>
        /// <returns></returns>
        public static bool Write(string logContent)
        {
            return Write(logContent, "AppError");
        }
        /// <summary>
        /// 写日志文件
        /// </summary>
        /// <param name="logContent">日志内容</param>
        /// <param name="fileName">日志文件名</param>
        /// <returns>返回值true成功,false失败</returns>
        public static bool Write(string logContent, string fileName)
        {
            bool flag = true;
            if (string.IsNullOrEmpty(fileName))
            {
                fileName = "AppError";
            }
            try
            {
                string str = Path.Combine(APP_LOG_DIRECTORY, DateTime.Now.ToString("yyyyMMdd") + fileName + LOG_SUFFIX);
                FileInfo info = new FileInfo(str);
                if (info.Exists && (info.Length >= 0xc3500L))
                {
                    info.CopyTo(str.Replace(LOG_SUFFIX, TextUtility.CreateRandomNum(5) + LOG_SUFFIX));
                    File.Delete(str);
                }
                FileStream stream = new FileStream(str, FileMode.Append, FileAccess.Write, FileShare.Read);
                StreamWriter writer = new StreamWriter(stream, Encoding.UTF8);
                StringBuilder builder = new StringBuilder();
                builder.AppendFormat("{0:yyyy'/'MM'/'dd' 'HH':'mm':'ss}", DateTime.Now);
                builder.Append("|");
                if (WRITE_APP_LOG)
                {
                    builder.Append(logContent.Replace("\r", "").Replace("\n", "<br />"));
                    builder.Append("|");
                    builder.Append(GameRequest.GetUserIP());
                    builder.Append("|");
                    builder.Append(GameRequest.GetUrl());
                }
                else
                {
                    builder.Append(logContent);
                }
                builder.Append("\r\n");
                writer.Write(builder.ToString());
                writer.Flush();
                writer.Close();
                writer.Dispose();
                stream.Close();
                stream.Dispose();
            }
            catch
            {
                flag = false;
            }
            return flag;
        }
        /// <summary>
        /// 写日志文件
        /// </summary>
        /// <param name="logContent">日志内容</param>
        /// <param name="classUrl">类名或页面名称</param>
        /// <param name="funcName">方法名称</param>
        /// <returns></returns>
        public static bool Write(string logContent, string classUrl, string funcName)
        {
            StringBuilder builder = new StringBuilder();
            if (!string.IsNullOrEmpty(classUrl))
            {
                builder.Append(classUrl);
            }
            if (!string.IsNullOrEmpty(funcName))
            {
                builder.Append("/");
                builder.Append(funcName);
            }
            if (!string.IsNullOrEmpty(logContent))
            {
                if (WRITE_APP_LOG)
                {
                    builder.Append("<br />");
                }
                else
                {
                    builder.Append("\r\n\t");
                }
                builder.Append(logContent);
            }
            return Write(builder.ToString(), "AppDebug");
        }
        /// <summary>
        /// 写日志文件
        /// </summary>
        /// <param name="cType"></param>
        /// <param name="funcName"></param>
        /// <param name="text"></param>
        /// <returns></returns>
        public static bool Write(Type cType, string funcName, string text)
        {
            return Write(cType.Namespace + cType.Name, funcName, text);
        }
        /// <summary>
        /// 自定义日期类(实现倒排序)
        /// </summary>
        public class SortDateTime : IComparable
        {
            public DateTime dateTime;

            public SortDateTime(DateTime oneDateTime)
            {
                this.dateTime = oneDateTime;
            }
            /// <summary>
            /// 重写比较方法
            /// </summary>
            /// <param name="obj"></param>
            /// <returns></returns>
            public int CompareTo(object obj)
            {
                TextLogger.SortDateTime time = obj as TextLogger.SortDateTime;
                if (time.dateTime > this.dateTime)
                {
                    return 1;
                }
                if (time.dateTime < this.dateTime)
                {
                    return -1;
                }
                return 0;
            }
        }
    }
}

