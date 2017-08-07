using SDKPackage.IData;
using SDKPackage.Kernel;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SDKPackage.Data.Factory
{
    /// <summary>
    /// 数据访问创建工厂
    /// </summary>
    public class ClassFactory
    {
        /// <summary>
        /// 创建前台库对象实例   
        /// </summary>
        /// <returns></returns>
        public static INativeWebDataProvider GetINativeWebDataProvider()
        {
            return ProxyFactory.CreateInstance<NativeWebDataProvider>(ConfigurationManager.ConnectionStrings["SdkPackageConnString"].ToString());
        }
    }
}
