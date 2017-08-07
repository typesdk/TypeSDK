using SDKPackage.Facade;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Xml;

namespace SDKPackage.GameConfig
{
    public partial class GamePlatformReplaceKey : System.Web.UI.Page
    {
        protected string gameid = GameRequest.GetQueryString("gameid");
        protected string gamename = GameRequest.GetQueryString("gamename");
        protected string platformid =  GameRequest.GetQueryString("platformid");
        protected string platformname = GameRequest.GetQueryString("platformname");
        protected string pluginid =  GameRequest.GetQueryString("pluginid");
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                BindingData();
            }
        }

        private void BindingData()
        {
            string SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
            SDKPackageDir += gamename + "\\" + platformname + "\\replace_key.xml";
            if (System.IO.File.Exists(SDKPackageDir))
            {
                LoadXml(SDKPackageDir);
            }
            else
            {
                LoadInitXml();
            }
            string sql = string.Format(@"select SDKKey from [sdk_PlatformConfig] where gamename='{0}' and PlatformName='{1}' and PlugInID={2} and isBuilding=1", gameid, platformid, pluginid);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            this.CheckBoxListIsBinding.DataSource = ds;
            this.CheckBoxListIsBinding.DataTextField = "SDKKey";
            this.CheckBoxListIsBinding.DataValueField = "SDKKey";
            this.CheckBoxListIsBinding.DataBind();

            UpdateCheckBoxListSelected(true);
        }

        /// <summary>
        /// 加载xml
        /// </summary>
        /// <param name="filepath"></param>
        private void LoadXml(string filepath)
        {
            XmlDocument replace_key = new XmlDocument();
            replace_key.Load(filepath);

            WriteText(replace_key);
        }

        private void LoadInitXml()
        {
            string strxml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><project name=\"replaceKey\"><replace dir=\".\" includes=\"AndroidManifest.xml\" encoding=\"UTF-8\"></replace></project>";
            XmlDocument replace_key = new XmlDocument();
            replace_key.LoadXml(strxml);
            //this.TextBoxContext.Text = replace_key.InnerXml.Replace("><", ">\r\n<");
            //this.TextBoxContext.Text = XmlText

            WriteText(replace_key);
        }

        /// <summary>
        /// 添加Replacefilter
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonAddReplacefilter_Click(object sender, EventArgs e)
        {
            try
            {
                int nodeIndex = CtrlHelper.GetInt(TextBoxNodeIndex, 0) - 1;
                if (nodeIndex < 0)
                {
                    return;
                }

                string strxml = this.TextBoxContext.Text;
                XmlDocument replace_key = new XmlDocument();
                replace_key.LoadXml(strxml);


                for (int i = 0; i < this.CheckBoxListIsBinding.Items.Count; i++)
                {
                    if (this.CheckBoxListIsBinding.Items[i].Selected)
                    {
                        string attrName = this.CheckBoxListIsBinding.Items[i].Text;
                        //string attrValue = this.CheckBoxListIsBinding.Items[i].Value;
                        XmlNode node = replace_key.SelectSingleNode("project").SelectNodes("replace")[nodeIndex];//.SelectNodes("//replacefilter[@token='@" + attrName + "@']");
                        XmlNodeList nodelist = node.SelectNodes("replacefilter[@token='@" + attrName + "@']");
                        if (nodelist.Count == 0)
                        {
                            string xmlnode = "<replacefilter token=\"@" + attrName + "@\" value=\"${" + attrName + "}\"/>";
                            replace_key.SelectSingleNode("project").SelectNodes("replace")[nodeIndex].InnerXml += xmlnode;
                        }
                    }
                }
                WriteText(replace_key);

            }
            catch
            {

            }
        }


        private void WriteText(XmlDocument xml)
        {
            using (StringWriter tw = new StringWriter())
            {
                using (XmlTextWriter tw2 = new XmlTextWriter(tw))//创建一个StringWriter实例的XmlTextWriter
                {
                    tw2.Formatting = Formatting.Indented;//设置缩进
                    tw2.Indentation = 1;//设置缩进字数
                    tw2.IndentChar = '\t';//用\t字符作为缩进
                    xml.WriteTo(tw2);
                    this.TextBoxContext.Text = tw.ToString();
                }
            }
        }

        private XmlDataDocument AddReplacefilter(int nodeIndex, string attrName, string attrValue, XmlDataDocument xml)
        {
            XmlNode replace = xml.SelectSingleNode("project").SelectNodes("replace")[nodeIndex];
            if (replace == null) return xml;
            XmlNodeList replacefilterList = replace.SelectNodes("replacefilter");
            foreach (XmlNode node in replacefilterList)
            {
                string token = node.Attributes["token"].Value;
                if (token == attrName)
                {
                    return xml;
                }
            }
            return xml;

        }

        /// <summary>
        /// 添加节点
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonAddNdoe_Click(object sender, EventArgs e)
        {
            string strxml = this.TextBoxContext.Text;
            XmlDocument replace_key = new XmlDocument();
            replace_key.LoadXml(strxml);
            XmlNode project = replace_key.SelectSingleNode("project");
            XmlNodeList nodelist = project.SelectNodes("replace");

            string dir = this.TextBoxDir.Text;
            string includes = this.TextBoxIncludes.Text;
            bool flag = false;
            foreach (XmlNode node in nodelist)
            {
                string d = node.Attributes["dir"].Value;
                string i = node.Attributes["includes"].Value;
                if (d == dir && i == includes)
                {
                    flag = true;
                    break;
                }
            }
            if (!flag)
            {
                string xmlnode = string.Format("<replace dir=\"{0}\" includes=\"{1}\" encoding=\"UTF-8\"></replace>", dir, includes);
                replace_key.SelectSingleNode("project").InnerXml += xmlnode;
                WriteText(replace_key);
            }
            UpdateCheckBoxListSelected(false);
        }


        private void UpdateCheckBoxListSelected(bool selected)
        {
            foreach (ListItem item in this.CheckBoxListIsBinding.Items)
            {
                item.Selected = selected;
            }
        }

        /// <summary>
        /// 保存
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonCreateConfigFile_Click(object sender, EventArgs e)
        {
            string SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"];
            SDKPackageDir += gamename + "\\" + platformname+(pluginid=="0"?"":"_LeBian") + "\\replace_key.xml";
            string strxml = this.TextBoxContext.Text;
            XmlDocument replace_key = new XmlDocument();
            replace_key.LoadXml(strxml);
            replace_key.Save(SDKPackageDir);
            LabelMessage.Text = "文件保存成功！";
        }
    }
}