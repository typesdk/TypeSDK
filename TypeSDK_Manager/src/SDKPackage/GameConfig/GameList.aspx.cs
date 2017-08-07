using ICSharpCode.SharpZipLib.Checksums;
using ICSharpCode.SharpZipLib.Zip;
using SDKPackage.Facade;
using SDKPackage.Kernel;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.GameConfig
{
    public partial class GameList : System.Web.UI.Page
    {
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                BindingList();
                BindingAndroidVersionList();
                BindingIOSVersionList();
                BindingAndroidKeyList();
            }
        }


        /// <summary>
        /// 绑定Android版本
        /// </summary>
        private void BindingAndroidVersionList()
        {
            string sql = "SELECT ID,[MyVersion] FROM [sdk_TypeSdkVersion] WHERE [PlatFormID]=1 ORDER BY [MyVersion] DESC";
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            this.ddlAndroidVersionList.DataSource = ds;
            this.ddlAndroidVersionList.DataTextField = "MyVersion";
            this.ddlAndroidVersionList.DataValueField = "ID";
            this.ddlAndroidVersionList.DataBind();
        }

        /// <summary>
        /// 绑定IOS版本
        /// </summary>
        private void BindingIOSVersionList()
        {
            string sql = "SELECT ID,[MyVersion] FROM [sdk_TypeSdkVersion] WHERE [PlatFormID]=2 ORDER BY [MyVersion] DESC";
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            this.ddlIOSVersionList.DataSource = ds;
            this.ddlIOSVersionList.DataTextField = "MyVersion";
            this.ddlIOSVersionList.DataValueField = "ID";
            this.ddlIOSVersionList.DataBind();
        }

        private void BindingAndroidKeyList()
        {
            string sql = "SELECT [Id],[KeyName] FROM [sdk_SignatureKey]";
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            this.ddlAndroidKeyList.DataSource = ds;
            this.ddlAndroidKeyList.DataTextField = "KeyName";
            this.ddlAndroidKeyList.DataValueField = "ID";
            this.ddlAndroidKeyList.DataBind();
        }

        /// <summary>
        /// 绑定数据
        /// </summary>
        private void BindingList()
        {
            DataSet ds = aideNativeWebFacade.GetGameInfoList(Context.User.Identity.Name);
            this.ListView1.DataSource = ds;
            this.ListView1.DataBind();
        }

        //private static string gameid = "0";
        protected void ListView1_ItemCommand(object sender, ListViewCommandEventArgs e)
        {
            string game = e.CommandArgument.ToString();
            string[] arr_game = game.Split('_');
            string gameid = arr_game[0];
            string gamename = arr_game[1];
            string gamenamespell = arr_game[2];
            if (e.CommandName == "del")
            {
                Message umsg = aideNativeWebFacade.DeleteGame(gameid);
                if (umsg.Success)
                {
                    //准备删除文件
                    DeleteGameAllFile(gamename, gamenamespell);
                    BindingList();
                    MessageLabel.Text = "数据删除成功！";
                }
                else
                {
                    MessageLabel.Text = "数据删除失败！";
                }
            }
        }

        /// <summary>
        /// 删除游戏所有文件
        /// </summary>
        private void DeleteGameAllFile(string gamename, string gamenamespell)
        {
            string SDKGameIcon_Android = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageIcon"] + gamename + "\\";
            string SDKGameConfig_Android = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageConfig"] + gamename + "\\";
            string SDKGameFile_Android = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageGameFile"] + gamename + "\\";
            string SDKGameApk_Android = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageApk"] + gamename + "\\";
            string SDKGameIcon_IOS = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageIcon"] + gamenamespell + "\\";
            string SDKGameFile_IOS = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageGameFile"] + gamenamespell + "\\";
            string SDKGameIPA_IOS = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageIPA"] + gamenamespell + "\\";
            try
            {
                if (Directory.Exists(SDKGameIcon_Android))
                {
                    Directory.Delete(SDKGameIcon_Android, true);
                }
            }
            catch (Exception) { }
            try
            {
                if (Directory.Exists(SDKGameConfig_Android))
                {
                    Directory.Delete(SDKGameConfig_Android, true);
                }
            }
            catch (Exception)
            {

            }
            try
            {
                if (Directory.Exists(SDKGameFile_Android))
                {
                    Directory.Delete(SDKGameFile_Android, true);
                }
            }
            catch (Exception)
            {

            }
            try
            {
                if (Directory.Exists(SDKGameApk_Android))
                {
                    Directory.Delete(SDKGameApk_Android, true);
                }
            }
            catch (Exception)
            {

            }
            try
            {
                if (Directory.Exists(SDKGameIcon_IOS))
                {
                    Directory.Delete(SDKGameIcon_IOS, true);
                }
            }
            catch (Exception)
            {

            }
            try
            {
                if (Directory.Exists(SDKGameFile_IOS))
                {
                    Directory.Delete(SDKGameFile_IOS, true);
                }
            }
            catch (Exception)
            {

            }
            try
            {
                if (Directory.Exists(SDKGameIPA_IOS))
                {
                    Directory.Delete(SDKGameIPA_IOS, true);
                }
            }
            catch (Exception)
            {

            }
        }

        /// <summary>
        /// 添加游戏
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ButtonAddGame_Click(object sender, EventArgs e)
        {
            //if (!UploadGameIcon()) return;
            string gamename = CtrlHelper.GetText(txtGameName);
            string gamedisplayname = CtrlHelper.GetText(txtGameDisplayName);
            string gameAndroidVersionID = CtrlHelper.GetSelectValue(ddlAndroidVersionList);
            string gameIOSVersionID = CtrlHelper.GetSelectValue(ddlIOSVersionList);
            string gameAndroidKeyID = CtrlHelper.GetSelectValue(ddlAndroidKeyList);
            string gameNameSpell = CtrlHelper.GetText(txtGameNameSpell);
            string gameUnityVer = CtrlHelper.GetText(txtUnityVer);
            string gameicon = this.hfGameIcon.Value;
            string gameProductName = CtrlHelper.GetText(txtProductName);
            string gameIsEncryption = this.CheckBoxIsEncryption.Checked ? "1" : "0";
            //追加sdk相关
            string sdkgameid = CtrlHelper.GetText(txtSDKGameID);
            string sdkgamekey = CtrlHelper.GetText(txtSDKGameKey);
            if (gamename == "" || gamedisplayname == "")
            {
                MessageLabel.Text = "游戏名字不能为空";
                return;
            }

            if (sdkgameid == "" || sdkgamekey == "")
            {
                MessageLabel.Text = "SDK通信ID及KEY不能为空";
                return;
            }


            string sql = "";

            if (hfSubmitType.Value == "add")
            {
                Message umsg = aideNativeWebFacade.AddGame(gamename, gamedisplayname, gameAndroidVersionID, gameIOSVersionID, gameAndroidKeyID, "", gameicon, Context.User.Identity.Name, gameNameSpell, gameUnityVer, gameProductName, gameIsEncryption, sdkgameid, sdkgamekey);
                if (umsg.Success)
                {
                    BindingList();
                    MessageLabel.Text = "游戏新增成功";
                    CreateGameImgList(umsg.Content, gamename, gameicon, false);
                    CreateIOSIcon(umsg.Content, gameNameSpell, gameicon, true);
                }
                else
                {
                    MessageLabel.Text = "游戏新增失败";
                }
            }
            else if (hfSubmitType.Value == "edit")
            {
                sql = string.Format(@"UPDATE [sdk_GameInfo] SET  [GameName]='{0}',[GameDisplayName]='{1}',[AndroidVersionID]={2},[IOSVersionID]={3},[AndroidKeyID]={4},
                                      [GameIcon]='{5}',[CreateUser]='{6}',[GameNameSpell]='{8}',[UnityVer]='{9}',ProductName='{10}',IsEncryption='{11}',SDKGameID='{12}',SDKGameKey='{13}' WHERE GAMEID={7}"
                                         , gamename, gamedisplayname, gameAndroidVersionID, gameIOSVersionID, gameAndroidKeyID, gameicon, Context.User.Identity.Name, CtrlHelper.GetInt(hfgameID, 0), gameNameSpell, gameUnityVer, gameProductName, gameIsEncryption, sdkgameid, sdkgamekey);



                int row = aideNativeWebFacade.ExecuteSql(sql);
                if (row > 0)
                {
                    BindingList();
                    MessageLabel.Text = "游戏更新成功";
                    CreateGameImgList(CtrlHelper.GetText(hfgameID), gamename, gameicon, true);
                    CreateIOSIcon(CtrlHelper.GetText(hfgameID), gameNameSpell, gameicon, true);
                }
                else
                {
                    MessageLabel.Text = "游戏更新失败";
                }

            }
            else
            {
                MessageLabel.Text = "抱歉，未识别操作！";
                return;
            }
        }

        /// <summary>
        /// 创建Android游戏图标组
        /// </summary>
        /// <param name="gameid"></param>
        /// <param name="gamename"></param>
        /// <param name="gameicon"></param>
        /// <param name="sqlFlag"></param>
        protected void CreateGameImgList(string gameid, string gamename, string gameicon, bool sqlFlag)
        {
            if (!string.IsNullOrEmpty(gameicon))
            {
                string isgameicon = this.hfIsGameIcon.Value;
                if (isgameicon == gameicon && hfSubmitType.Value != "add")
                {
                    return;
                }
                //MessageLabel.Text += "。图标组自动生成中，请勿关闭页面！";
                //↓↓↓↓↓↓预生成图标↓↓↓↓↓↓  ***********************IOS平台待处理***********************
                string[] IconType = { "drawable", "drawable-ldpi", "drawable-mdpi", "drawable-hdpi", "drawable-xhdpi", "drawable-xxhdpi", "drawable-xxxhdpi", "512" };
                int imgsize = 0;
                string SDKAndroidPackageIcon = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageIcon"];
                string uploadPatch = SDKAndroidPackageIcon + gameid + "\\" + gamename + "_Default\\";
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
                    System.IO.Directory.CreateDirectory(uploadPatch + "512\\");
                }
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
                        case "512": imgsize = 512;
                            break;
                        default:
                            break;
                    }
                    thumbnailPath = uploadPatch + IconType[i] + "\\app_icon.png";
                    ImageHelper.CreateSDKGameDefault(Server.MapPath("\\") + gameicon, thumbnailPath, imgsize, imgsize);

                }
                ImageHelper.CreateSDKGameDefault(Server.MapPath("\\") + gameicon, uploadPatch + "app_icon.png", 512, 512);
                if (sqlFlag)
                {
                    Dictionary<string, string> dic = new Dictionary<string, string>();
                    dic.Add("IconName", gamename + "_Default");
                    dic.Add("SystemID", "1");//Android平台
                    dic.Add("GameID", gameid);//Android平台
                    aideNativeWebFacade.ExecuteStoredProcedure("sdk_setIcon", dic);
                }
            }
        }


        /// <summary>
        /// 创建IOS图标组
        /// </summary>
        private void CreateIOSIcon(string gameid, string gamename, string gameicon, bool sqlFlag)
        {
            string isgameicon = this.hfIsGameIcon.Value;
            if (isgameicon == gameicon && hfSubmitType.Value != "add")
            {
                return;
            }
            string IconName = gamename + "_Default";
            string SDKPackageDir = string.Empty;
            SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageIcon"];

            string uploadPatch = SDKPackageDir + gameid + "\\" + gamename + "_Default\\";
            try
            {
                if (!System.IO.Directory.Exists(uploadPatch))
                {
                    System.IO.Directory.CreateDirectory(uploadPatch);
                    System.IO.Directory.CreateDirectory(uploadPatch + "AppIcon.appiconset\\");
                }
                string[] IconType = { "icon", "icon@2x", "icon-2", "icon-29-2", "Icon-40@2x", "icon-40-2", "Icon-41", "icon-58-2", "Icon-60@2x", "Icon-60@3x", "icon-72", 
                                      "Icon-72@2x", "Icon-76", "icon-80-2", "icon-120-2", "icon-152","Icon-Small","Icon-Small@2x","Icon-Small-50","Icon-Small-50@2x","iTunesArtwork"};
                int imgsize = 0;
                string thumbnailPath = "";
                for (int i = 0; i < IconType.Length; i++)
                {
                    switch (IconType[i])
                    {
                        case "icon": imgsize = 57;
                            break;
                        case "icon@2x": imgsize = 144;
                            break;
                        case "icon-2": imgsize = 57;
                            break;
                        case "icon-29-2": imgsize = 29;
                            break;
                        case "Icon-40@2x": imgsize = 80;
                            break;
                        case "icon-40-2": imgsize = 40;
                            break;
                        case "Icon-41": imgsize = 40;
                            break;
                        case "icon-58-2": imgsize = 58;
                            break;
                        case "Icon-60@2x": imgsize = 120;
                            break;
                        case "Icon-60@3x": imgsize = 180;
                            break;
                        case "icon-72": imgsize = 72;
                            break;
                        case "Icon-72@2x": imgsize = 144;
                            break;
                        case "Icon-76": imgsize = 76;
                            break;
                        case "icon-80-2": imgsize = 80;
                            break;
                        case "icon-120-2": imgsize = 120;
                            break;
                        case "icon-152": imgsize = 152;
                            break;
                        case "Icon-Small": imgsize = 29;
                            break;
                        case "Icon-Small@2x": imgsize = 58;
                            break;
                        case "Icon-Small-50": imgsize = 50;
                            break;
                        case "Icon-Small-50@2x": imgsize = 100;
                            break;
                        case "iTunesArtwork": imgsize = 512;
                            break;
                        default:
                            break;
                    }
                    thumbnailPath = uploadPatch + "AppIcon.appiconset\\" + IconType[i] + ".png";
                    ImageHelper.CreateSDKGameDefault(Server.MapPath("\\") + gameicon, thumbnailPath, imgsize, imgsize);
                }
                ImageHelper.CreateSDKGameDefault(Server.MapPath("\\") + gameicon, uploadPatch + "app_icon.png", 512, 512);
                if (sqlFlag)
                {
                    Dictionary<string, string> dic = new Dictionary<string, string>();
                    dic.Add("IconName", gamename + "_Default");
                    dic.Add("SystemID", "2");//Android平台
                    dic.Add("GameID", gameid);//Android平台
                    aideNativeWebFacade.ExecuteStoredProcedure("sdk_setIcon", dic);
                }

                MessageLabel.Text = "成功上传图标组";
            }
            catch (Exception ex)
            {
                MessageLabel.Text = ex.Message.ToString();
            }
        }
    }
}