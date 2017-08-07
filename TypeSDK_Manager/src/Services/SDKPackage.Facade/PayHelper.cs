using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SDKPackage.Utils;
using SDKPackage.Entity;

namespace Game.Facade
{
    /// <summary>
    /// 订单助手
    /// </summary>
    public class PayHelper
    {
        /// <summary>
        /// 获取交易流水号
        /// </summary>
        /// <returns></returns>
        public static string GetOrderID()
        {
            //构造订单号 (形如:20101201102322159111111)
            StringBuffer tradeNoBuffer = new StringBuffer();
            tradeNoBuffer += TextUtility.GetDateTimeLongString();
            tradeNoBuffer += TextUtility.CreateRandom(8, 1, 0, 0, 0, "");

            return tradeNoBuffer.ToString();
        }

        /// <summary>
        /// 获取交易流水号
        /// </summary>
        /// <param name="prefix"></param>
        /// <returns></returns>
        public static string GetOrderIDByPrefix(string prefix)
        {
            //构造订单号 (形如:20101201102322159111111)
            int orderIDLength = 32;
            int randomLength = 6;
            StringBuffer tradeNoBuffer = new StringBuffer();

            tradeNoBuffer += prefix;
            tradeNoBuffer += TextUtility.GetDateTimeLongString();

            if ((tradeNoBuffer.Length + randomLength) > orderIDLength)
                randomLength = orderIDLength - tradeNoBuffer.Length;

            tradeNoBuffer += TextUtility.CreateRandom(randomLength, 1, 0, 0, 0, "");

            return tradeNoBuffer.ToString();
        }
    }
}
