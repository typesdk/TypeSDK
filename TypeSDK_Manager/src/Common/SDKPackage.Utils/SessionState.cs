namespace SDKPackage.Utils
{
    using System;
    using System.Web;
    /// <summary>
    /// 对Session操作进行封装
    /// </summary>
    public class SessionState
    {
        /// <summary>
        /// 从 Session 读取 键为 name 的值
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public static object Get(string name)
        {
            string str = ApplicationSettings.Get("AppPrefix");  
            return HttpContext.Current.Session[str + name];
        }
        /// <summary>
        ///  从 Session 删除 键为 name session 项
        /// </summary>
        /// <param name="name"></param>
        public static void Remove(string name)
        {
            string str = ApplicationSettings.Get("AppPrefix");
            if (HttpContext.Current.Session[str + name] != null)
            {
                HttpContext.Current.Session.Remove(str + name);
            }
        }
        /// <summary>
        /// 删除所有 session 项
        /// </summary>
        public static void RemoveAll()
        {
            HttpContext.Current.Session.RemoveAll();
        }
        /// <summary>
        /// 向 Session 保存 键为 name 的， 值为 value
        /// </summary>
        /// <param name="name"></param>
        /// <param name="value"></param>
        /// <param name="timeOut">过期时间</param>
        public static void Set( string name , object value )
        {
            string str = ApplicationSettings.Get( "AppPrefix" );
            HttpContext.Current.Session.Add( str + name , value );
        }
        /// <summary>
        /// 向 Session 保存 键为 name 的， 值为 value
        /// </summary>
        /// <param name="name"></param>
        /// <param name="value"></param>
        /// <param name="timeOut">过期时间</param>
        public static void Set(string name, object value, int timeOut)
        {
            string str = ApplicationSettings.Get("AppPrefix");
            HttpContext.Current.Session.Add(str + name, value);
            HttpContext.Current.Session.Timeout = timeOut;
        }
    }
}

