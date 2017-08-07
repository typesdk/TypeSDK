namespace SDKPackage.Utils
{
    using System;
    using System.Web.UI;
    using System.Web.UI.HtmlControls;
    using System.Web.UI.WebControls;
    /// <summary>
    /// 控件辅助类
    /// </summary>
    public class CtrlHelper
    {
        /// <summary>
        ///  获取复选框选中值 byte
        /// </summary>
        /// <param name="chk"></param>
        /// <returns></returns>
        public static byte GetCheckBoxValue(CheckBox chk)
        {
            return Convert.ToByte(chk.Checked);
        }
        /// <summary>
        ///  获取控件 Int 参数值,ctrl 必须是文本控件
        /// </summary>
        /// <param name="ctrl"></param>
        /// <param name="defValue"></param>
        /// <returns></returns>
        public static int GetInt(Control ctrl, int defValue)
        {
            if (ctrl == null)
            {
                throw new ArgumentNullException("获取文本内容的控件不能为空！");
            }
            if (ctrl is ITextControl)
            {
                return TypeParse.StrToInt(GetText(ctrl as ITextControl), defValue);
            }
            if (ctrl is HtmlInputControl)
            {
                return TypeParse.StrToInt(GetText(ctrl as HtmlInputControl), defValue);
            }
            if (ctrl is HiddenField)
            {
                return TypeParse.StrToInt(GetText(ctrl as HiddenField), defValue);
            }
            return defValue;
        }
        /// <summary>
        /// 获取下拉列表值
        /// </summary>
        /// <param name="ddlList"></param>
        /// <returns></returns>
        public static string GetSelectValue(DropDownList ddlList)
        {            
            return ddlList.SelectedValue.Trim();
        }
        /// <summary>
        /// 获取下拉列表值
        /// </summary>
        /// <param name="ddlList"></param>
        /// <param name="defValue"></param>
        /// <returns></returns>
        public static byte GetSelectValue(DropDownList ddlList, byte defValue)
        {
            return (byte) TypeParse.StrToInt(GetSelectValue(ddlList), defValue);
        }
        /// <summary>
        /// 获取控件文本内容
        /// </summary>
        /// <param name="valueCtrl"></param>
        /// <returns></returns>
        public static string GetText(HtmlInputControl valueCtrl)
        {
            if (valueCtrl == null)
            {
                throw new ArgumentNullException("获取文本内容的控件不能为空！");
            }
            if (string.IsNullOrEmpty(valueCtrl.Value))
            {
                return "";
            }
            return Utility.HtmlEncode(TextFilter.FilterScript(valueCtrl.Value.Trim()));
        }
        /// <summary>
        /// 获取控件文本内容
        /// </summary>
        /// <param name="textCtrl"></param>
        /// <returns></returns>
        public static string GetText(ITextControl textCtrl)
        {
            if (textCtrl == null)
            {
                throw new ArgumentNullException("获取文本内容的控件不能为空！");
            }
            if (string.IsNullOrEmpty(textCtrl.Text))
            {
                return "";
            }
            return Utility.HtmlEncode(TextFilter.FilterScript(textCtrl.Text.Trim()));
        }
        /// <summary>
        /// 获取控件文本内容
        /// </summary>
        /// <param name="hiddenCtrl"></param>
        /// <returns></returns>
        public static string GetText(HiddenField hiddenCtrl)
        {
            if (hiddenCtrl == null)
            {
                throw new ArgumentNullException("获取文本内容的控件不能为空！");
            }
            if (string.IsNullOrEmpty(hiddenCtrl.Value))
            {
                return "";
            }
            return Utility.HtmlEncode(TextFilter.FilterScript(hiddenCtrl.Value.Trim()));
        }
        /// <summary>
        /// 设置复选框值
        /// </summary>
        /// <param name="chk"></param>
        /// <param name="val"></param>
        public static void SetCheckBoxValue(CheckBox chk, byte val)
        {
            chk.Checked = val != 0;
        }
        /// <summary>
        /// 为文本控件赋值
        /// </summary>
        /// <param name="valueCtrl"></param>
        /// <param name="text"></param>
        public static void SetText(HtmlInputControl valueCtrl, string text)
        {
            if (valueCtrl == null)
            {
                throw new ArgumentNullException("设置文本内容的控件不能为空！");
            }
            valueCtrl.Value = Utility.HtmlDecode(text.Trim());
        }
        /// <summary>
        /// 为文本控件赋值
        /// </summary>
        /// <param name="textCtrl"></param>
        /// <param name="text"></param>
        public static void SetText(ITextControl textCtrl, string text)
        {
            if (textCtrl == null)
            {
                throw new ArgumentNullException("设置文本内容的控件不能为空！");
            }
            textCtrl.Text = Utility.HtmlDecode(text.Trim());
        }
        /// <summary>
        /// 为文本控件赋值
        /// </summary>
        /// <param name="hiddenCtrl"></param>
        /// <param name="text"></param>
        public static void SetText(HiddenField hiddenCtrl, string text)
        {
            if (hiddenCtrl == null)
            {
                throw new ArgumentNullException("设置文本内容的控件不能为空！");
            }
            hiddenCtrl.Value = Utility.HtmlDecode(text.Trim());
        }
        /// <summary>
        /// 为文本控件赋值
        /// </summary>
        /// <param name="textCtrl"></param>
        /// <param name="text"></param>
        public static void SetText(TextBox textCtrl, string text)
        {
            SetText((ITextControl) textCtrl, text);
        }
    }
}

