using ICSharpCode.SharpZipLib.Zip;
using SDKPackage.Facade;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Web;
using System.Xml;

using PListNet;
using PListNet.Nodes;

namespace SDKPackage.PJPackage
{
    /// <summary>
    /// UpdateGameZip 的摘要说明
    /// </summary>
    public class UpdateGameZip : IHttpHandler
    {
        protected string gameid = GameRequest.GetQueryString("gameid");
        protected string gamename = GameRequest.GetQueryString("gamename");
        protected string platform = GameRequest.GetQueryString("platform");
        protected string gamenamespell = GameRequest.GetQueryString("gamenamespell");
        protected string username = "";//GameRequest.GetQueryString("username");
        protected string lablename = GameRequest.GetQueryString("lablename");
        private string gameVersion;
        private string gameVersionCode;
        private string fileName;
        private string SDKPackageDir;
        private string uploadPatch;
        private string uploadFile;
        //private bool isDefaultVersion;
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();

        public void ProcessRequest(HttpContext context)
        {
            context.Response.ContentType = "text/plain";

            username = context.User.Identity.Name;
            HttpPostedFile zipFile = context.Request.Files[0];
            context.Response.ContentType = "text/plain";
            string txtmessage = "未知异常";
            if (zipFile.FileName == "")
            {
            }
            else
            {
                fileName = zipFile.FileName;
                string zipSize = (zipFile.InputStream.Length / 1024 / 1024).ToString();
                if (platform == "Android")
                {
                    SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageGameFile"];
                    txtmessage = UpdateAndroidZip(zipFile, zipSize);
                }
                else
                {
                    //IOS待确认
                    SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageGameFile"];
                    //UpdateIOSZipOld(zipFile, zipFile.FileName, zipSize);
                    UpdateIOSZip(zipFile, zipSize);
                    txtmessage = "文件上传成功！";
                }

            }
            context.Response.Write(txtmessage);
        }

        public bool IsReusable
        {
            get
            {
                return false;
            }
        }

        /// <summary>
        /// 上传Android ZIP
        /// </summary>
        /// <param name="zipSize"></param>
        private string UpdateAndroidZip(HttpPostedFile zipFile, string zipSize)
        {
            //uploadPatch = SDKPackageDir + gamename + "\\tmp\\";
            uploadPatch = SDKPackageDir + gameid + "\\tmp\\";
            uploadFile = uploadPatch + fileName;
            //isDefaultVersion = true;

            //try
            //{
            if (!System.IO.Directory.Exists(uploadPatch))
            {
                System.IO.Directory.CreateDirectory(uploadPatch);
            }
            if (System.IO.File.Exists(uploadFile))
            {
                File.Delete(uploadFile);
            }
            zipFile.SaveAs(uploadFile);

            if (Directory.Exists(uploadPatch + "\\Game"))
            {
                Directory.Delete(uploadPatch + "\\Game", true);
            }

            string result = UnZip(uploadFile, uploadPatch, null);
            if (result == "")
            {
                //try
                //{
                XmlDocument AndroidManifest = new XmlDocument();
                String AndroidManifestFile = uploadPatch + @"Game\AndroidManifest.xml";

                AndroidManifest.Load(AndroidManifestFile);
                XmlNode manifest = AndroidManifest.SelectSingleNode("manifest");
                gameVersion = manifest.Attributes["android:versionName"].Value;
                gameVersionCode = manifest.Attributes["android:versionCode"].Value;
                string strCollectdate = TextUtility.GetDateTimeLongString();
                //string savePatch = SDKPackageDir + gamename + "\\" + gameVersion + "_" + strCollectdate;
                string savePatch = SDKPackageDir + gameid + "\\" + gameVersion + "_" + strCollectdate;
                string saveFile = savePatch + "\\Game.zip";
                string versionFile = savePatch + "\\version.properties";

                if (!System.IO.Directory.Exists(savePatch))
                {
                    System.IO.Directory.CreateDirectory(savePatch);
                }
                else
                {
                    if (File.Exists(saveFile))
                    {
                        File.Delete(saveFile);
                    }
                    if (File.Exists(versionFile))
                    {
                        File.Delete(versionFile);
                    }
                }

                File.Move(uploadFile, saveFile);
                AndroidManifest.Save(savePatch + "/AndroidManifest.xml");
                StreamWriter sw = new StreamWriter(versionFile, false, Encoding.UTF8);
                sw.WriteLine("version=gameversion");
                sw.WriteLine("version.code=" + gameVersionCode);
                sw.WriteLine("version.name=" + gameVersion);
                sw.Flush();
                sw.Close();
                Dictionary<string, string> dic = new Dictionary<string, string>();
                dic.Add("dwUploadUser", username);
                dic.Add("dwGameVersion", gameVersion);
                dic.Add("dwPageageTable", lablename);
                dic.Add("dwFileSize", zipSize);
                dic.Add("dwGameName", gamename);
                dic.Add("dwGamePlatFrom", platform);
                dic.Add("dwStrCollectDatetime", strCollectdate);
                dic.Add("dwGameID", gameid);
                aideNativeWebFacade.ExecuteStoredProcedure("sdk_AddPackageProject", dic);
                result = "文件上传成功!";
                //}
                //catch (Exception ex)
                //{
                //    result = ex.Message;
                //}
                //Response.Write("<script>window.opener.document.getElementById(\"hfreturnVal\").value = \"success\"</script>");
                //this.saveButton.Text = "保存";
                //this.saveButton.Enabled = true;
            }
            return result;
        }

        /// <summary>
        /// 上传Android ZIP
        /// </summary>
        /// <param name="zipSize"></param>
        private string UpdateIOSZip(HttpPostedFile zipFile, string zipSize)
        {
            //uploadPatch = SDKPackageDir + gamename + "\\tmp\\";
            uploadPatch = SDKPackageDir + gameid + "\\iostmp\\";
            uploadFile = uploadPatch + fileName;
            //isDefaultVersion = true;

            //try
            //{
            if (!System.IO.Directory.Exists(uploadPatch))
            {
                System.IO.Directory.CreateDirectory(uploadPatch);
            }
            if (System.IO.File.Exists(uploadFile))
            {
                File.Delete(uploadFile);
            }
            zipFile.SaveAs(uploadFile);

            if (Directory.Exists(uploadPatch + "\\Game"))
            {
                Directory.Delete(uploadPatch + "\\Game", true);
            }

            string result = UnZip(uploadFile, uploadPatch, null);
            if (result == "")
            {

                String InfoPlistFile = uploadPatch + @"Game\Info.plist";

                using (var stream = new FileStream(InfoPlistFile, FileMode.Open))
			    {
                    var node = PList.Load(stream);
                    var dicNode = node as DictionaryNode;
                    var gameVersionNode = dicNode["CFBundleVersion"] as StringNode;
                    string gameVersion = gameVersionNode.Value;
                    string strCollectdate = TextUtility.GetDateTimeLongString();
                    string savePatch = SDKPackageDir + gameid + "\\" + gameVersion + "_" + strCollectdate;
                    string saveFile = savePatch + "\\Game.zip";

                    if (!System.IO.Directory.Exists(savePatch))
                    {
                        System.IO.Directory.CreateDirectory(savePatch);
                    }
                    else
                    {
                        if (File.Exists(saveFile))
                        {
                            File.Delete(saveFile);
                        }
                    }

                    File.Move(uploadFile, saveFile);

                    Dictionary<string, string> dic = new Dictionary<string, string>();
                    dic.Add("dwUploadUser", username);
                    dic.Add("dwGameVersion", gameVersion);
                    dic.Add("dwPageageTable", lablename);
                    dic.Add("dwFileSize", zipSize);
                    dic.Add("dwGameName", gamename);
                    dic.Add("dwGamePlatFrom", platform);
                    dic.Add("dwStrCollectDatetime", strCollectdate);
                    dic.Add("dwGameID", gameid);
                    aideNativeWebFacade.ExecuteStoredProcedure("sdk_AddPackageProject", dic);
                    result = "文件上传成功!";
                }
            }
            return result;
        }

        private void UpdateIOSZipOld(HttpPostedFile zipFile, string filename, string zipSize)
        {
            string strCollectdate = TextUtility.GetDateTimeLongString();
            //uploadPatch = SDKPackageDir + gamenamespell + "/" + strCollectdate + "/";
            uploadPatch = SDKPackageDir + gameid + "/" + strCollectdate + "/";
            uploadFile = uploadPatch + fileName;
            //isDefaultVersion = true;

            //try
            //{
            if (!System.IO.Directory.Exists(uploadPatch))
            {
                System.IO.Directory.CreateDirectory(uploadPatch);
            }
            if (System.IO.File.Exists(uploadFile))
            {
                File.Delete(uploadFile);
            }
            zipFile.SaveAs(uploadFile);

            Dictionary<string, string> dic = new Dictionary<string, string>();
            dic.Add("dwUploadUser", username);
            dic.Add("dwGameVersion", filename);
            dic.Add("dwPageageTable", lablename);
            dic.Add("dwFileSize", zipSize);
            dic.Add("dwGameName", gamename);
            dic.Add("dwGamePlatFrom", platform);
            dic.Add("dwStrCollectDatetime", strCollectdate);
            dic.Add("dwGameID", gameid);
            aideNativeWebFacade.ExecuteStoredProcedure("sdk_AddPackageProject", dic);
            //Response.Write("<script>window.opener.document.getElementById(\"hfreturnVal\").value = \"success\"</script>");
        }

        public static string UnZip(string fileToUnZip, string zipedFolder, string password)
        {
            //bool result = true;
            string result = "";
            FileStream fs = null;
            ZipInputStream zipStream = null;
            ZipEntry ent = null;
            string fileName;

            if (!File.Exists(fileToUnZip))
                return "包文件内容错误";//return false;

            if (!Directory.Exists(zipedFolder))
                Directory.CreateDirectory(zipedFolder);

            try
            {
                zipStream = new ZipInputStream(File.OpenRead(fileToUnZip));
                if (!string.IsNullOrEmpty(password)) zipStream.Password = password;
                while ((ent = zipStream.GetNextEntry()) != null)
                {
                    if (ent.Name.Contains("AndroidManifest.xml") || ent.Name.Contains("Game/Info.plist"))
                    {
                        fileName = Path.Combine(zipedFolder, ent.Name);
                        fileName = fileName.Replace('/', '\\');//change by Mr.HopeGi   

                        //if (fileName.EndsWith("\\"))
                        //{
                        Directory.CreateDirectory(zipedFolder + "Game\\");
                        //continue;
                        //}

                        fs = File.Create(fileName);
                        int size = 2048;
                        byte[] data = new byte[size];
                        while (true)
                        {
                            size = zipStream.Read(data, 0, data.Length);
                            if (size > 0)
                            {
                                fs.Write(data, 0, size);
                                fs.Flush();
                            }
                            else
                                break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                result = "找不到Game文件夹，请将工程文件放在Game文件夹下压缩";//ex.Message;//result = false;
            }
            finally
            {
                if (fs != null)
                {
                    fs.Close();
                    fs.Dispose();
                }
                if (zipStream != null)
                {
                    zipStream.Close();
                    zipStream.Dispose();
                }
                if (ent != null)
                {
                    ent = null;
                }
                GC.Collect();
                GC.Collect(1);
            }
            return result;
        }

        public static Stream GetTestFileStream(string relativeFilePath)
        {
            const char namespaceSeparator = '.';

            // get calling assembly
            var assembly = System.Reflection.Assembly.GetCallingAssembly();

            // compute resource name suffix (replace Windows/Unix directory separators with namespace separator)
            var relativeName = "." + relativeFilePath
                .Replace('/', namespaceSeparator)
                .Replace('\\', namespaceSeparator)
                .Replace(' ', '_');

            // get resource stream
            var fullName = assembly
                .GetManifestResourceNames()
                .FirstOrDefault(name => name.EndsWith(relativeName, StringComparison.InvariantCulture));
            if (fullName == null)
            {
                throw new Exception(string.Format("Unable to find resource for path \"{0}\". Resource with name ending on \"{1}\" was not found in assembly.", relativeFilePath, relativeName));
            }

            var stream = assembly.GetManifestResourceStream(fullName);
            if (stream == null)
            {
                throw new Exception(string.Format("Unable to find resource for path \"{0}\". Resource named \"{1}\" was not found in assembly.", relativeFilePath, fullName));
            }

            return stream;
        }
    }
}