namespace SDKPackage.Utils
{
    using System;
    using System.IO;
    using System.Runtime.Serialization.Formatters.Binary;
    using System.Xml.Serialization;
    /// <summary>
    /// 提供类的序列化的辅助操作
    /// </summary>
    public class SerializationHelper
    {
        private SerializationHelper()
        {
        }
        /// <summary>
        /// 反序列化成 obj 对象 From byte[] 
        /// </summary>
        /// <param name="buffer"></param>
        /// <returns></returns>
        public static object Deserialize(byte[] buffer)
        {
            BinaryFormatter formatter = new BinaryFormatter();
            MemoryStream serializationStream = new MemoryStream(buffer, 0, buffer.Length, false);
            object obj2 = formatter.Deserialize(serializationStream);
            serializationStream.Close();
            return obj2;
        }
        /// <summary>
        /// 反序列化对象
        /// </summary>
        /// <param name="type">对象类型</param>
        /// <param name="filename">文件名</param>
        /// <returns>返回反序列化后的对象</returns>
        public static object Deserialize(Type type, string filename)
        {
            FileStream stream = null;
            object obj2;
            try
            {
                stream = new FileStream(filename, FileMode.Open, FileAccess.Read, FileShare.ReadWrite);
                obj2 = new XmlSerializer(type).Deserialize(stream);
            }
            catch (Exception exception)
            {
                throw exception;
            }
            finally
            {
                if (stream != null)
                {
                    stream.Close();
                }
            }
            return obj2;
        }
        /// <summary>
        /// 序列化泛型对象为 byte[]
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="t">要序列化的泛型对象</param>
        /// <returns></returns>
        public static byte[] Serialize<T>(T t)
        {
            BinaryFormatter formatter = new BinaryFormatter();
            MemoryStream serializationStream = new MemoryStream(0x2800);
            formatter.Serialize(serializationStream, t);
            serializationStream.Seek(0L, SeekOrigin.Begin);
            byte[] buffer = new byte[(int) serializationStream.Length];
            serializationStream.Read(buffer, 0, buffer.Length);
            serializationStream.Close();
            return buffer;
        }
        /// <summary>
        /// 序列化对象到文件
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="t">要序列化的泛型对象</param>
        /// <param name="filename">文件名</param>
        public static void Serialize<T>(T t, string filename)
        {
            FileStream stream = null;
            try
            {
                stream = new FileStream(filename, FileMode.Create, FileAccess.Write, FileShare.ReadWrite);
                new XmlSerializer(t.GetType()).Serialize((Stream) stream, t);
            }
            catch (Exception exception)
            {
                throw exception;
            }
            finally
            {
                if (stream != null)
                {
                    stream.Close();
                }
            }
        }
    }
}

