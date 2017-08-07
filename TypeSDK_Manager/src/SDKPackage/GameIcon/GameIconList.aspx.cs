using SDKPackage.Facade;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Text;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
namespace SDKPackage.GameIcon
{
    public partial class GameIconList : System.Web.UI.Page
    {
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected string systemname;
        protected void Page_Load(object sender, EventArgs e)
        {
            this.saveusername.Value = Context.User.Identity.Name;
            this.systemname = this.DropDownListSystem.SelectedValue;
            if (!IsPostBack)
            { }
        }

        public static string GetDateTimeLongString()
        {
            DateTime now = DateTime.Now;
            return (now.ToString("yyyyMMddHHmmss") + now.Millisecond.ToString("000"));
        }

        /// <summary>
        /// 上传图标
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonAddIcon_Click(object sender, EventArgs e)
        {
            string systemname = this.DropDownListSystem.SelectedValue;
            if (systemname == "1")
            {
                CreateAndroidIcon();
            }
            else
            {
                //IOS代确认
                CreateIOSIcon();
            }


        }

        /// <summary>
        /// 合成图标
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonComposeIcon_Click(object sender, EventArgs e)
        {
            string systemname = this.DropDownListSystem.SelectedValue;
            if (systemname == "1")
            {
                ComposeAndroidIcon();
            }
            else
            {
                //IOS代确认
                ComposeIOSIcon();
            }

        }


        private void createPatch(string patch)
        {
            if (!System.IO.Directory.Exists(patch))
            {
                System.IO.Directory.CreateDirectory(patch);
            }
        }



        /// <summary>
        /// 创建Android图标组
        /// </summary>
        private void CreateAndroidIcon()
        {
            string IconName = IconNameTextBox.Text;

            string systemname = this.DropDownListSystem.SelectedValue;
            string SDKPackageDir = string.Empty;
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageIcon"];
            string imgPath = "";
            if (FileUpload.HasFile)//选择了图片
            {
                string imgUrl = SDKPackageDir + "\\Upload\\";
                createPatch(imgUrl);
                string ssName = FileUpload.FileName;
                imgPath = imgUrl + GetDateTimeLongString() + "." + ssName.Substring(ssName.LastIndexOf(".") + 1, (ssName.Length - ssName.LastIndexOf(".") - 1));
                FileUpload.SaveAs(imgPath);
            }
            if (string.IsNullOrEmpty(imgPath)) return;

            string gameID = this.ddlGameList.SelectedValue;
            string uploadPatch = SDKPackageDir + gameID + "\\" + IconName + "\\";
            try
            {
                if (!System.IO.Directory.Exists(uploadPatch))
                {
                    System.IO.Directory.CreateDirectory(uploadPatch);
                    System.IO.Directory.CreateDirectory(uploadPatch + "drawable\\");
                    System.IO.Directory.CreateDirectory(uploadPatch + "drawable-ldpi\\");
                    System.IO.Directory.CreateDirectory(uploadPatch + "drawable-mdpi\\");
                    System.IO.Directory.CreateDirectory(uploadPatch + "drawable-hdpi\\");
                    System.IO.Directory.CreateDirectory(uploadPatch + "drawable-xhdpi\\");
                    System.IO.Directory.CreateDirectory(uploadPatch + "drawable-xxhdpi\\");
                    System.IO.Directory.CreateDirectory(uploadPatch + "drawable-xxxhdpi\\");
                }
                
                string[] IconType = { "drawable", "drawable-ldpi", "drawable-mdpi", "drawable-hdpi", "drawable-xhdpi", "drawable-xxhdpi", "drawable-xxxhdpi" };
                int imgsize = 0;
                string thumbnailPath = "";
                for (int i = 0; i < IconType.Length; i++)
                {
                    switch (IconType[i])
                    {
                        case "drawable": imgsize = 48;
                            break;
                        case "drawable-ldpi": imgsize = 36;
                            break;
                        case "drawable-mdpi": imgsize = 48;
                            break;
                        case "drawable-hdpi": imgsize = 72;
                            break;
                        case "drawable-xhdpi": imgsize = 96;
                            break;
                        case "drawable-xxhdpi": imgsize = 144;
                            break;
                        case "drawable-xxxhdpi": imgsize = 192;
                            break;
                        default:
                            break;
                    }
                    thumbnailPath = uploadPatch + IconType[i] + "\\app_icon.png";
                    ImageHelper.CreateSDKGameDefault(imgPath, thumbnailPath, imgsize, imgsize);
                }
                ImageHelper.CreateSDKGameDefault(imgPath, uploadPatch + "app_icon.png", 512, 512);
                Dictionary<string, string> dic = new Dictionary<string, string>();
                dic.Add("IconName", IconName);
                dic.Add("SystemID", systemname);
                dic.Add("GameID", this.ddlGameList.SelectedValue);
                aideNativeWebFacade.ExecuteStoredProcedure("sdk_setIcon", dic);
                DropDownListIcon.DataBind();

                MessageLabel.Text = "成功上传图标组";
            }
            catch (Exception ex)
            {
                MessageLabel.Text = ex.Message.ToString();
            }
        }

        /// <summary>
        /// 创建IOS图标组
        /// </summary>
        private void CreateIOSIcon()
        {
            string IconName = IconNameTextBox.Text;

            string systemname = this.DropDownListSystem.SelectedValue;
            string SDKPackageDir = string.Empty;
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageIcon"];
            string imgPath = "";
            if (FileUpload.HasFile)//选择了图片
            {
                string imgUrl = SDKPackageDir + "\\Upload\\";
                createPatch(imgUrl);
                string ssName = FileUpload.FileName;
                imgPath = imgUrl + GetDateTimeLongString() + "." + ssName.Substring(ssName.LastIndexOf(".") + 1, (ssName.Length - ssName.LastIndexOf(".") - 1));
                FileUpload.SaveAs(imgPath);
            }
            if (string.IsNullOrEmpty(imgPath)) return;

            string gameID = this.ddlGameList.SelectedValue;
            string uploadPatch = SDKPackageDir + gameID + "\\" + IconName + "\\";
            try
            {
                if (!System.IO.Directory.Exists(uploadPatch))
                {
                    System.IO.Directory.CreateDirectory(uploadPatch);
                    System.IO.Directory.CreateDirectory(uploadPatch + "AppIcon.appiconset\\");
                }
                
                //string[] IconType = { "icon", "icon@2x", "icon-2", "icon-29-2", "Icon-40@2x", "icon-40-2", "Icon-41", "icon-58-2", "Icon-60@2x", "Icon-60@3x", "icon-72", 
                //                      "Icon-72@2x", "Icon-76", "icon-80-2", "icon-120-2", "icon-152","Icon-Small","Icon-Small@2x","Icon-Small-50","Icon-Small-50@2x","iTunesArtwork"};

                string[] IconType = { "Icon", "Icon@2x", "Icon-120", "Icon-180", "Icon-72", "Icon-144", "Icon-76", "Icon-152", "Icon-167", "iTunesArtwork" };
                int imgsize = 0;
                string thumbnailPath = "";
                for (int i = 0; i < IconType.Length; i++)
                {
                    //switch (IconType[i])
                    //{
                    //    case "icon": imgsize = 57;
                    //        break;
                    //    case "icon@2x": imgsize = 144;
                    //        break;
                    //    case "icon-2": imgsize = 57;
                    //        break;
                    //    case "icon-29-2": imgsize = 29;
                    //        break;
                    //    case "Icon-40@2x": imgsize = 80;
                    //        break;
                    //    case "icon-40-2": imgsize = 40;
                    //        break;
                    //    case "Icon-41": imgsize = 40;
                    //        break;
                    //    case "icon-58-2": imgsize = 58;
                    //        break;
                    //    case "Icon-60@2x": imgsize = 120;
                    //        break;
                    //    case "Icon-60@3x": imgsize = 180;
                    //        break;
                    //    case "icon-72": imgsize = 72;
                    //        break;
                    //    case "Icon-72@2x": imgsize = 144;
                    //        break;
                    //    case "Icon-76": imgsize = 76;
                    //        break;
                    //    case "icon-80-2": imgsize = 80;
                    //        break;
                    //    case "icon-120-2": imgsize = 120;
                    //        break;
                    //    case "icon-152": imgsize = 152;
                    //        break;
                    //    case "Icon-Small": imgsize = 29;
                    //        break;
                    //    case "Icon-Small@2x": imgsize = 58;
                    //        break;
                    //    case "Icon-Small-50": imgsize = 50;
                    //        break;
                    //    case "Icon-Small-50@2x": imgsize = 100;
                    //        break;
                    //    case "iTunesArtwork": imgsize = 512;
                    //        break;
                    //    default:
                    //        break;
                    //}

                    switch (IconType[i])
                    {
                        case "Icon": imgsize = 57;
                            break;
                        case "Icon@2x": imgsize = 114;
                            break;
                        case "Icon-120": imgsize = 120;
                            break;
                        case "Icon-180": imgsize = 180;
                            break;
                        case "Icon-72@2x": imgsize = 72;
                            break;
                        case "Icon-144": imgsize = 144;
                            break;
                        case "Icon-76": imgsize = 76;
                            break;
                        case "Icon-152": imgsize = 152;
                            break;
                        case "Icon-167": imgsize = 167;
                            break;
                        case "iTunesArtwork": imgsize = 512;
                            break;
                        default:
                            break;
                    }
                    thumbnailPath = uploadPatch + "AppIcon.appiconset\\" + IconType[i] + ".png";
                    ImageHelper.CreateSDKGameDefault(imgPath, thumbnailPath, imgsize, imgsize);
                }
                ImageHelper.CreateSDKGameDefault(imgPath, uploadPatch + "app_icon.png", 512, 512);
                Dictionary<string, string> dic = new Dictionary<string, string>();
                dic.Add("IconName", IconName);
                dic.Add("SystemID", systemname);
                dic.Add("GameID", this.ddlGameList.SelectedValue);
                aideNativeWebFacade.ExecuteStoredProcedure("sdk_setIcon", dic);
                DropDownListIcon.DataBind();

                MessageLabel.Text = "成功上传图标组";
            }
            catch (Exception ex)
            {
                MessageLabel.Text = ex.Message.ToString();
            }
        }

        /// <summary>
        /// 合成Android图标组
        /// </summary>
        private void ComposeAndroidIcon()
        {
            string saveIconPatch;
            string bodyIconFile;
            string IconName = TextBox1.Text;
            string systemname = this.DropDownListSystem.SelectedValue;
            string SDKPackageDir = string.Empty;
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageIcon"];
            string iconPatch = SDKPackageDir;
            string gameID = this.ddlGameList.SelectedValue;
            string masterSaveFileName;
            string masterIconPatch;

            masterIconPatch = SDKPackageDir + gameID + "\\" + DropDownListIcon.SelectedValue + "\\";

            string ssUploadFileMd5;
            string ssUploadFileLastName;
            string ssUploadFileName;
            string ssSavePatch;
            string ssSaveFileName;
            if (this.radio_s.Value=="1")
            {
                if (SSFileUpload.HasFile)
                {
                    ssUploadFileName = SSFileUpload.FileName;
                    ssUploadFileLastName = ssUploadFileName.Substring(ssUploadFileName.LastIndexOf(".") + 1, (ssUploadFileName.Length - ssUploadFileName.LastIndexOf(".") - 1));
                    ssUploadFileMd5 = GetMD5HashFromFile(SSFileUpload.FileContent);
                    ssSavePatch = iconPatch + "\\Upload";
                    createPatch(ssSavePatch);

                    ssSaveFileName = ssSavePatch + "\\" + ssUploadFileMd5 + "." + ssUploadFileLastName;
                    SSFileUpload.SaveAs(ssSaveFileName);
                    if (ssUploadFileLastName == "psd")
                    {
                        SDKPackage.PJConfig.ImagePsd _Psd = new SDKPackage.PJConfig.ImagePsd(ssSaveFileName);
                        _Psd.PSDImage.Save(ssSavePatch + "\\" + ssUploadFileMd5 + ".png", System.Drawing.Imaging.ImageFormat.Png);
                        ssSaveFileName = ssSavePatch + "\\" + ssUploadFileMd5 + ".png";
                    }
                    if (SizeDropDownList.SelectedValue != "0")
                    {
                        int sizePx = int.Parse(SizeDropDownList.SelectedValue);
                        ssSaveFileName = SDKPackage.PJConfig.IconCreate.generateCreateMark(ssSaveFileName, sizePx, sizePx);
                    }
                }
                else
                {
                    MessageLabel.Text = "合成图标时没有选择角标！";
                    return;
                }
            }
            else
            {
                ssSaveFileName = Server.MapPath(this.DropDownList2.SelectedValue);
            }

            string IconPatch = SDKPackageDir + gameID + "\\" + IconName + "\\";
            createPatch(IconPatch);

            string[] IconType = { "drawable", "drawable-ldpi", "drawable-mdpi", "drawable-hdpi", "drawable-xhdpi", "drawable-xxhdpi", "drawable-xxxhdpi" };
            //string[] IconType = { "29", "40", "80", "58", "57", "114", "180", "120", "50", "100", "72" ,"144" ,"76" ,"152", "512" };
            string bodyIcon = SDKPackageDir + "white\\";
            try
            {
                
                for (int i = 0; i < IconType.Length; i++)
                {
                    masterSaveFileName = masterIconPatch + IconType[i] + "\\app_icon.png";
                    saveIconPatch = IconPatch + IconType[i];
                    createPatch(saveIconPatch);
                    bodyIconFile = bodyIcon + IconType[i] + "\\app_icon.png";
                    createIcon(masterSaveFileName, ssSaveFileName, saveIconPatch);
                }

                createIcon(masterIconPatch + "app_icon.png", ssSaveFileName, IconPatch);
                Dictionary<string, string> dic = new Dictionary<string, string>();
                dic.Add("IconName", IconName);
                dic.Add("SystemID", systemname);
                dic.Add("GameID", this.ddlGameList.SelectedValue);
                aideNativeWebFacade.ExecuteStoredProcedure("sdk_setIcon", dic);

                DropDownListIcon.DataBind();
                MessageLabel.Text = "成功合成图标组";
            }
            catch (Exception ex)
            {
                MessageLabel.Text = ex.Message.ToString();
            }

        }

        /// <summary>
        /// 合成IOS图标组
        /// </summary>
        private void ComposeIOSIcon()
        {
            string saveIconPatch;
            string bodyIconFile;
            string IconName = TextBox1.Text;
            string systemname = this.DropDownListSystem.SelectedValue;
            string SDKPackageDir = string.Empty;
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageIcon"];
            string iconPatch = SDKPackageDir;

            string gameID = this.ddlGameList.SelectedValue;

            string masterSaveFileName;
            string masterIconPatch;

            masterIconPatch = SDKPackageDir + gameID + "\\" + DropDownListIcon.SelectedValue + "\\AppIcon.appiconset\\";

            string ssUploadFileMd5;
            string ssUploadFileLastName;
            string ssUploadFileName;
            string ssSavePatch;
            string ssSaveFileName;
            if (this.radio_s.Value=="1")
            {
                if (SSFileUpload.HasFile)
                {
                    ssUploadFileName = SSFileUpload.FileName;
                    ssUploadFileLastName = ssUploadFileName.Substring(ssUploadFileName.LastIndexOf(".") + 1, (ssUploadFileName.Length - ssUploadFileName.LastIndexOf(".") - 1));
                    ssUploadFileMd5 = GetMD5HashFromFile(SSFileUpload.FileContent);
                    ssSavePatch = iconPatch + "\\Upload";
                    createPatch(ssSavePatch);
                    ssSaveFileName = ssSavePatch + "\\" + ssUploadFileMd5 + "." + ssUploadFileLastName;
                    SSFileUpload.SaveAs(ssSaveFileName);
                    if (ssUploadFileLastName == "psd")
                    {
                        SDKPackage.PJConfig.ImagePsd _Psd = new SDKPackage.PJConfig.ImagePsd(ssSaveFileName);
                        _Psd.PSDImage.Save(ssSavePatch + "\\" + ssUploadFileMd5 + ".png", System.Drawing.Imaging.ImageFormat.Png);
                        ssSaveFileName = ssSavePatch + "\\" + ssUploadFileMd5 + ".png";
                    }
                    if (SizeDropDownList.SelectedValue != "0")
                    {
                        int sizePx = int.Parse(SizeDropDownList.SelectedValue);
                        ssSaveFileName = SDKPackage.PJConfig.IconCreate.generateCreateMark(ssSaveFileName, sizePx, sizePx);
                    }
                }
                else
                {
                    MessageLabel.Text = "合成图标时没有选择角标！";
                    return;
                }
            }
            else
            {
                ssSaveFileName = Server.MapPath(this.DropDownList2.SelectedValue);
            }

            string IconPatch = SDKPackageDir + gameID + "\\" + IconName + "\\";
            createPatch(IconPatch);

            //string[] IconType = { "icon", "icon@2x", "icon-2", "icon-29-2", "Icon-40@2x", "icon-40-2", "Icon-41", "icon-58-2", "Icon-60@2x", "Icon-60@3x", "icon-72", 
            //                          "Icon-72@2x", "Icon-76", "icon-80-2", "icon-120-2", "icon-152","Icon-Small","Icon-Small@2x","Icon-Small-50","Icon-Small-50@2x","iTunesArtwork"};
            //string[] IconType = { "29", "40", "80", "58", "57", "114", "180", "120", "50", "100", "72" ,"144" ,"76" ,"152", "512" };

            string[] IconType = { "Icon", "Icon@2x", "Icon-120", "Icon-180", "Icon-72", "Icon-144", "Icon-76", "Icon-152", "Icon-167", "iTunesArtwork" };
            string bodyIcon = SDKPackageDir + "white\\";
            try
            {
                for (int i = 0; i < IconType.Length; i++)
                {
                    masterSaveFileName = masterIconPatch + IconType[i] + ".png";
                    saveIconPatch = IconPatch + "\\AppIcon.appiconset\\";//IconType[i];
                    createPatch(saveIconPatch);
                    bodyIconFile = bodyIcon + IconType[i] + "\\app_icon.png";
                    createIcon(masterSaveFileName, ssSaveFileName, saveIconPatch, IconType[i]);
                }
                createIcon(masterIconPatch + "app_icon.png", ssSaveFileName, IconPatch);
                Dictionary<string, string> dic = new Dictionary<string, string>();
                dic.Add("IconName", IconName);
                dic.Add("SystemID", systemname);
                dic.Add("GameID", this.ddlGameList.SelectedValue);
                aideNativeWebFacade.ExecuteStoredProcedure("sdk_setIcon", dic);

                DropDownListIcon.DataBind();
                MessageLabel.Text = "成功合成图标组";
            }
            catch (Exception ex)
            {
                MessageLabel.Text = ex.Message.ToString();
            }

        }

        private void createIcon(string masterIcon, string SSIcon, string savePatch)
        {
            SDKPackage.PJConfig.IconCreate.favoriteImage[] FaImage = new SDKPackage.PJConfig.IconCreate.favoriteImage[1];
            FaImage[0].x = 0;
            FaImage[0].y = 0;
            FaImage[0].imagePath = SSIcon;
            SDKPackage.PJConfig.IconCreate.generateWinterMark(savePatch, masterIcon, FaImage);
        }

        private void createIcon(string masterIcon, string SSIcon, string savePatch, string filename)
        {
            SDKPackage.PJConfig.IconCreate.favoriteImage[] FaImage = new SDKPackage.PJConfig.IconCreate.favoriteImage[1];
            FaImage[0].x = 0;
            FaImage[0].y = 0;
            FaImage[0].imagePath = SSIcon;
            SDKPackage.PJConfig.IconCreate.generateWinterMark_IOS(savePatch, masterIcon, FaImage, filename);
        }

        private static string GetMD5HashFromFile(Stream file)
        {
            try
            {
                StringBuilder sb = new StringBuilder();
                using (System.Security.Cryptography.MD5 md5 = new System.Security.Cryptography.MD5CryptoServiceProvider())
                {
                    byte[] retVal = md5.ComputeHash(file);

                    for (int i = 0; i < retVal.Length; i++)
                    {
                        sb.Append(retVal[i].ToString("x2"));
                    }
                }
                return sb.ToString();
            }
            catch (Exception ex)
            {
                return "error";
                //throw new Exception("error");
            }
        }

        protected void SqlDataSourceGameIcon_Deleted(object sender, SqlDataSourceStatusEventArgs e)
        {
            //e.Command.Parameters["@Id"].Value;
            string SDKPackageDir = string.Empty;
            string platform = CtrlHelper.GetSelectValue(DropDownListSystem);
            
            SDKPackageDir = string.Equals(platform,"1") ? System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageIcon"]
                                                        : System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageIcon"];

            string gameID = this.ddlGameList.SelectedValue;

            //OldValuesParameterFormatString = "old_{0}"
            string IconPath = SDKPackageDir + gameID + "\\" + e.Command.Parameters["@IconName"].Value + "\\";

            DeleteFolder(IconPath);

            MessageLabel.Text = "";
        }

        /// <summary>  
        /// 用递归方法删除文件夹目录及文件  
        /// </summary>  
        /// <param name="dir">带文件夹名的路径</param>   
        public void DeleteFolder(string dir)
        {
            if (Directory.Exists(dir)) //如果存在这个文件夹删除之   
            {
                foreach (string d in Directory.GetFileSystemEntries(dir))
                {
                    if (File.Exists(d))
                        File.Delete(d); //直接删除其中的文件                          
                    else
                        DeleteFolder(d); //递归删除子文件夹   
                }
                Directory.Delete(dir, true); //删除已空文件夹                   
            }
        } 
    }
}