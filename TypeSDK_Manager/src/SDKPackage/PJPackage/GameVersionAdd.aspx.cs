using ICSharpCode.SharpZipLib.Zip;
using SDKPackage.Facade;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Xml;

namespace SDKPackage.PJPackage
{
    public partial class GameVersionAdd : System.Web.UI.Page
    {
        protected string gameid = GameRequest.GetQueryString("gameid");
        protected string gamename = GameRequest.GetQueryString("gamename");
        protected string platform = GameRequest.GetQueryString("platform");
        protected string gamenamespell = GameRequest.GetQueryString("gamenamespell");
        protected string username = HttpContext.Current.User.Identity.Name;
        //private string gameVersion;
        //private string gameVersionCode;
        //private string fileName;
        //private string SDKPackageDir;
        //private string uploadPatch;
        //private string uploadFile;
        //private bool isDefaultVersion;
        protected bool flag = true;
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            if (string.IsNullOrEmpty(gameid))
            {
                this.LogLabel.Text = "游戏ID参数错误";
                flag = false;
                //this.saveButton.Visible = true;
            } if (string.IsNullOrEmpty(gamename))
            {
                this.LogLabel.Text = "游戏名称参数错误";
                flag = false;
                //this.saveButton.Visible = true;
            } if (string.IsNullOrEmpty(platform))
            {
                this.LogLabel.Text = "平台参数错误";
                flag = false;
                //this.saveButton.Visible = true;
            }
        }

        //protected void saveButton_Click(object sender, EventArgs e)
        //{
        //    this.saveButton.Enabled = false;
        //    this.saveButton.Text = "上传中...";
        //    if (GameVersionFileUpload.FileName == "")
        //    {
        //        LogLabel.Text = "请选择需要上传的游戏项目";
        //        GameVersionFileUpload.Focus();
        //    }
        //    else
        //    {
        //        fileName = GameVersionFileUpload.FileName;
        //        string zipSize = (GameVersionFileUpload.PostedFile.InputStream.Length / 1024 / 1024).ToString();
        //        if (platform == "Android")
        //        {
        //            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageGameFile"];
        //            UpdateAndroidZip(zipSize);
        //        }
        //        else
        //        {
        //            //IOS待确认
        //            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageGameFile"];
        //            UpdateIOSZip(GameVersionFileUpload.FileName, zipSize);
        //        }

        //    }
        //}

        ///// <summary>
        ///// 上传Android ZIP
        ///// </summary>
        ///// <param name="zipSize"></param>
        //private void UpdateAndroidZip(string zipSize)
        //{
        //    uploadPatch = SDKPackageDir + gamename + "\\tmp\\";
        //    uploadFile = uploadPatch + fileName;
        //    //isDefaultVersion = true;

        //    //try
        //    //{
        //    if (!System.IO.Directory.Exists(uploadPatch))
        //    {
        //        System.IO.Directory.CreateDirectory(uploadPatch);
        //    }
        //    if (System.IO.File.Exists(uploadFile))
        //    {
        //        File.Delete(uploadFile);
        //    }
        //    GameVersionFileUpload.SaveAs(uploadFile);

        //    if (Directory.Exists(uploadPatch + "\\Game"))
        //    {
        //        Directory.Delete(uploadPatch + "\\Game", true);
        //    }

        //    if (UnZip(uploadFile, uploadPatch, null))
        //    {
        //        XmlDocument AndroidManifest = new XmlDocument();
        //        String AndroidManifestFile = uploadPatch + @"Game\AndroidManifest.xml";

        //        AndroidManifest.Load(AndroidManifestFile);
        //        XmlNode manifest = AndroidManifest.SelectSingleNode("manifest");
        //        gameVersion = manifest.Attributes["android:versionName"].Value;
        //        gameVersionCode = manifest.Attributes["android:versionCode"].Value;
        //        string strCollectdate = TextUtility.GetDateTimeLongString();
        //        string savePatch = SDKPackageDir + gamename + "\\" + gameVersion + "_" + strCollectdate;
        //        string saveFile = savePatch + "\\Game.zip";
        //        string versionFile = savePatch + "\\version.properties";

        //        if (!System.IO.Directory.Exists(savePatch))
        //        {
        //            System.IO.Directory.CreateDirectory(savePatch);
        //        }
        //        else
        //        {
        //            if (File.Exists(saveFile))
        //            {
        //                File.Delete(saveFile);
        //            }
        //            if (File.Exists(versionFile))
        //            {
        //                File.Delete(versionFile);
        //            }
        //        }

        //        File.Move(uploadFile, saveFile);

        //        StreamWriter sw = new StreamWriter(versionFile, false, Encoding.UTF8);
        //        sw.WriteLine("version=gameversion");
        //        sw.WriteLine("version.code=" + gameVersionCode);
        //        sw.WriteLine("version.name=" + gameVersion);
        //        sw.Flush();
        //        sw.Close();
        //        Dictionary<string, string> dic = new Dictionary<string, string>();
        //        dic.Add("dwUploadUser", Context.User.Identity.Name);
        //        dic.Add("dwGameVersion", gameVersion);
        //        dic.Add("dwPageageTable", CtrlHelper.GetText(TextBoxVersionLabel));
        //        dic.Add("dwFileSize", zipSize);
        //        dic.Add("dwGameName", gamename);
        //        dic.Add("dwGamePlatFrom", platform);
        //        dic.Add("dwStrCollectDatetime", strCollectdate);
        //        dic.Add("dwGameID", gameid);
        //        aideNativeWebFacade.ExecuteStoredProcedure("sdk_AddPackageProject", dic);
        //        Response.Write("<script>window.opener.document.getElementById(\"hfreturnVal\").value = \"success\"</script>");
        //        this.LogLabel.Text = "版本上传成功！";
        //        this.saveButton.Text = "保存";
        //        this.saveButton.Enabled = true;
        //    }
        //}


        //private void UpdateIOSZip(string filename,string zipSize)
        //{
        //    string strCollectdate = TextUtility.GetDateTimeLongString();
        //    uploadPatch = SDKPackageDir + gamenamespell + "/" + strCollectdate + "/";
        //    uploadFile = uploadPatch + fileName;
        //    //isDefaultVersion = true;

        //    //try
        //    //{
        //    if (!System.IO.Directory.Exists(uploadPatch))
        //    {
        //        System.IO.Directory.CreateDirectory(uploadPatch);
        //    }
        //    if (System.IO.File.Exists(uploadFile))
        //    {
        //        File.Delete(uploadFile);
        //    }
        //    GameVersionFileUpload.SaveAs(uploadFile);

        //    Dictionary<string, string> dic = new Dictionary<string, string>();
        //    dic.Add("dwUploadUser", Context.User.Identity.Name);
        //    dic.Add("dwGameVersion", filename);
        //    dic.Add("dwPageageTable", CtrlHelper.GetText(TextBoxVersionLabel));
        //    dic.Add("dwFileSize", zipSize);
        //    dic.Add("dwGameName", gamename);
        //    dic.Add("dwGamePlatFrom", platform);
        //    dic.Add("dwStrCollectDatetime", strCollectdate);
        //    dic.Add("dwGameID", gameid);
        //    aideNativeWebFacade.ExecuteStoredProcedure("sdk_AddPackageProject", dic);
        //    Response.Write("<script>window.opener.document.getElementById(\"hfreturnVal\").value = \"success\"</script>");
        //    this.LogLabel.Text = "版本上传成功！";
        //    this.saveButton.Text = "保存";
        //    this.saveButton.Enabled = true;
        //}

        //public static bool UnZip(string fileToUnZip, string zipedFolder, string password)
        //{
        //    bool result = true;
        //    FileStream fs = null;
        //    ZipInputStream zipStream = null;
        //    ZipEntry ent = null;
        //    string fileName;

        //    if (!File.Exists(fileToUnZip))
        //        return false;

        //    if (!Directory.Exists(zipedFolder))
        //        Directory.CreateDirectory(zipedFolder);

        //    try
        //    {
        //        zipStream = new ZipInputStream(File.OpenRead(fileToUnZip));
        //        if (!string.IsNullOrEmpty(password)) zipStream.Password = password;
        //        while ((ent = zipStream.GetNextEntry()) != null)
        //        {
        //            if (ent.Name.Contains("AndroidManifest.xml"))
        //            {
        //                fileName = Path.Combine(zipedFolder, ent.Name);
        //                fileName = fileName.Replace('/', '\\');//change by Mr.HopeGi   

        //                //if (fileName.EndsWith("\\"))
        //                //{
        //                Directory.CreateDirectory(zipedFolder + "Game\\");
        //                //continue;
        //                //}

        //                fs = File.Create(fileName);
        //                int size = 2048;
        //                byte[] data = new byte[size];
        //                while (true)
        //                {
        //                    size = zipStream.Read(data, 0, data.Length);
        //                    if (size > 0)
        //                    {
        //                        fs.Write(data, 0, size);
        //                        fs.Flush();
        //                    }
        //                    else
        //                        break;
        //                }
        //            }
        //        }
        //    }
        //    catch
        //    {
        //        result = false;
        //    }
        //    finally
        //    {
        //        if (fs != null)
        //        {
        //            fs.Close();
        //            fs.Dispose();
        //        }
        //        if (zipStream != null)
        //        {
        //            zipStream.Close();
        //            zipStream.Dispose();
        //        }
        //        if (ent != null)
        //        {
        //            ent = null;
        //        }
        //        GC.Collect();
        //        GC.Collect(1);
        //    }
        //    return result;
        //}
    }
}