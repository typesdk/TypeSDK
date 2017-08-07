using SDKPackage.Facade;
using System;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Xml;

namespace SDKPackage
{
    public partial class _Default : Page
    {
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            //UpdateGamePlatformInfo();
            //Update();
            //testinfoplist();
            //testPList("D://Info.plist");
        }

        private void testPList(string filepath)
        {
            UTF8Encoding utf8 = new UTF8Encoding(false);
            using (StreamReader sr = new StreamReader(filepath, utf8))
            {
                string strxml = sr.ReadToEnd();
                XmlDocument xml = new XmlDocument();
                xml.LoadXml(strxml);
                string strdict = xml.SelectSingleNode("plist").SelectSingleNode("dict").InnerXml;
                strdict = strdict.Replace(" ", "");
                xml.SelectSingleNode("plist").SelectSingleNode("dict").InnerXml = strdict;
                

                UTF8Encoding encoding = new UTF8Encoding(false);
                using (StreamWriter sw = new StreamWriter("D://testplist.plist", false, encoding))
                {
                    string rep = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\"[]>";
                    string rep2 = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">";
                    string innerxml = xml.InnerXml.Replace(rep, rep2);
                    sw.Write(innerxml);
                }

                //string rep = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\"[]>";
                //string rep2 = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">";
                //xml.InnerXml = xml.InnerXml.Replace(rep, rep2);
                //xml.Save(path + "Info.plist");
            }
        }


        private void testinfoplist()
        {
            string SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPList"];
            string[] filelist = Directory.GetDirectories(SDKPackageDir);
            foreach (string filepath in filelist)
            {
                string path = filepath + "\\";
                if (System.IO.Directory.Exists(path))
                {
                    path += "Info.plist";
                    if (System.IO.File.Exists(path))
                    {
                        CheckPList(path);
                    }
                }
            }

        }

        private void CheckPList(string filepath)
        {
            UTF8Encoding utf8 = new UTF8Encoding();
            using (StreamReader sr = new StreamReader(filepath, utf8))
            {
                string strxml = sr.ReadToEnd();
                XmlDocument xml = new XmlDocument();
                xml.LoadXml(strxml);
                string strdict = xml.SelectSingleNode("plist").SelectSingleNode("dict").InnerXml;
                strdict = strdict.Replace(" ", "");
                xml.SelectSingleNode("plist").SelectSingleNode("dict").InnerXml = strdict;
                string path = "D://" + filepath.Substring(49, filepath.Length-59);
                if (!System.IO.Directory.Exists(path))
                {
                    System.IO.Directory.CreateDirectory(path);
                }

                UTF8Encoding encoding = new UTF8Encoding(false);
                using (StreamWriter sw = new StreamWriter(path + "Info.plist", false, encoding))
                {
                    string rep = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\"[]>";
                    string rep2 = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">";
                    string innerxml = xml.InnerXml.Replace(rep, rep2);
                    sw.Write(innerxml);
                }

                //string rep = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\"[]>";
                //string rep2 = "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">";
                //xml.InnerXml = xml.InnerXml.Replace(rep, rep2);
                //xml.Save(path + "Info.plist");
            }
        }


        private void Update()
        {
            string sql = string.Format("select gameid from sdk_gameInfo");
            DataSet dsGame = aideNativeWebFacade.GetDataSetBySql(sql);
            string sql2 = string.Format("select id,parentid from sdk_defaultplatform where platformname like '%_LeBian'");
            DataSet dsPlatform = aideNativeWebFacade.GetDataSetBySql(sql2);
            for (int i = 0; i < dsGame.Tables[0].Rows.Count; i++)
            {
                string gameid = dsGame.Tables[0].Rows[i]["gameid"].ToString();
                for (int j = 0; j < dsPlatform.Tables[0].Rows.Count; j++)
                {
                    string id = dsPlatform.Tables[0].Rows[j]["id"].ToString();
                    string pid = dsPlatform.Tables[0].Rows[j]["parentid"].ToString();

                    string sql3 = string.Format("select 1 from sdk_PlatformConfig where gamename='{0}' and platformname='{1}'", gameid, id);

                    if (aideNativeWebFacade.GetDataSetBySql(sql3).Tables[0].Rows.Count > 0)
                    {
                        string sql4 = string.Format(@"update sdk_PlatformConfig set platformname='{0}',PlugInID=1 where gamename='{1}' and platformname='{2}'", pid, gameid, id);
                        aideNativeWebFacade.ExecuteSql(sql4);
                    }
                }
            }
        }


        private void UpdateGamePlatformInfo()
        {
            string sql = string.Format("  select [VersionPlatFromID] from [sdk_GamePlatFromInfo] group by [VersionPlatFromID]");
            DataSet dsVersionPlatFromID = aideNativeWebFacade.GetDataSetBySql(sql);
            string sql2 = string.Format("select id,parentid from sdk_defaultplatform where platformname like '%_LeBian'");
            DataSet dsPlatform = aideNativeWebFacade.GetDataSetBySql(sql2);
            for (int i = 0; i < dsVersionPlatFromID.Tables[0].Rows.Count; i++)
            {
                for (int j = 0; j < dsPlatform.Tables[0].Rows.Count; j++)
                {
                    string id = dsPlatform.Tables[0].Rows[j]["id"].ToString();
                    string pid = dsPlatform.Tables[0].Rows[j]["parentid"].ToString();
                    string sql3 = string.Format("select 1 from sdk_GamePlatFromInfo where VersionPlatFromID={0}", id);
                    if (aideNativeWebFacade.GetDataSetBySql(sql3).Tables[0].Rows.Count > 0)
                    {
                        string selectid = pid + "_1";
                        string sql4 = string.Format("update sdk_GamePlatFromInfo set VersionPlatFromID={0},PlugInID=1,PlugInVersion='4.8',selectid='{2}' where VersionPlatFromID={1} and systemid=1", pid, id, selectid);
                        aideNativeWebFacade.ExecuteSql(sql4);
                    }
                }
            }

        }
    }
}