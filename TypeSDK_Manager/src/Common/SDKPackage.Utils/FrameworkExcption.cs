namespace SDKPackage.Utils
{
    using SDKPackage.Utils.Properties;
    using System;
    /// <summary>
    /// 在应用框架中发生非致命工具操作错误时引发的异常
    /// </summary>
    public class FrameworkExcption : Exception
    {
        public FrameworkExcption(string message) : base(GetException(message))
        {
        }

        public FrameworkExcption(string message, params string[] args) : base(GetException(message, args))
        {
        }

        internal static string GetException(string name)
        {
            return AppExceptions.ResourceManager.GetString(name);
        }

        internal static string GetException(string name, params string[] args)
        {
            return string.Format(AppExceptions.ResourceManager.GetString(name), (object[]) args);
        }
    }
}

