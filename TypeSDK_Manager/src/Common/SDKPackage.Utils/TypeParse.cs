namespace SDKPackage.Utils
{
    using System;
    using System.Text.RegularExpressions;
    /// <summary>
    /// 类型之间的转换,如:字符串与数字之间的转换
    /// </summary>
    public sealed class TypeParse
    {
        private TypeParse()
        {
        }
        /// <summary>
        /// 判断给定的字符串数组(expression)中的数据是不是都为数值型
        /// </summary>
        /// <param name="strNumber">要确认的字符串数组</param>
        /// <returns>是则返回 true 不是则返回 false</returns>
        public static bool IsNumericArray(string[] strNumber)
        {
            if (strNumber == null)
            {
                return false;
            }
            if (strNumber.Length < 1)
            {
                return false;
            }
            foreach (string str in strNumber)
            {
                if (!Validate.IsNumeric(str))
                {
                    return false;
                }
            }
            return true;
        }
        /// <summary>
        /// long型安全转换为int型
        /// </summary>
        /// <param name="expression"></param>
        /// <returns></returns>
        public static int SafeLongToInt32(long expression)
        {
            if (expression > 0x7fffffffL)
            {
                return 0x7fffffff;
            }
            if (expression < -2147483648L)
            {
                return -2147483648;
            }
            return (int) expression;
        }
        /// <summary>
        ///  object型转换为bool型
        /// </summary>
        /// <param name="expression">要转换的字符串 true OR false</param>
        /// <param name="defValue">缺省值</param>
        /// <returns></returns>
        public static bool StrToBool(object expression, bool defValue)
        {
            if (expression != null)
            {
                if (string.Compare(expression.ToString(), "true", true) == 0)
                {
                    return true;
                }
                if (string.Compare(expression.ToString(), "false", true) == 0)
                {
                    return false;
                }
            }
            return defValue;
        }
        /// <summary>
        /// string型转换为float型
        /// </summary>
        /// <param name="expression">要转换的字符串</param>
        /// <param name="defValue">缺省值</param>
        /// <returns></returns>
        public static float StrToFloat(object expression, float defValue)
        {
            if ((expression == null) || (expression.ToString().Length > 10))
            {
                return defValue;
            }
            float num = defValue;
            if ((expression != null) && Regex.IsMatch(expression.ToString(), @"^([-]|[0-9])[0-9]*(\.\w*)?$"))
            {
                num = Convert.ToSingle(expression);
            }
            return num;
        }
        /// <summary>
        /// string型转换为int型
        /// </summary>
        /// <param name="expression">要转换的字符串</param>
        /// <param name="defValue">缺省值</param>
        /// <returns></returns>
        public static int StrToInt(object expression, int defValue)
        {
            if (expression == null)
            {
                return defValue;
            }
            string input = expression.ToString();
            if (!(((input.Length > 0) && (input.Length <= 11)) && Regex.IsMatch(input, "^[-]?[0-9]*$")))
            {
                return defValue;
            }
            if (((input.Length >= 10) && ((input.Length != 10) || (input[0] != '1'))) && (((input.Length != 11) || (input[0] != '-')) || (input[1] != '1')))
            {
                return defValue;
            }
            return Convert.ToInt32(input);
        }
    }
}

