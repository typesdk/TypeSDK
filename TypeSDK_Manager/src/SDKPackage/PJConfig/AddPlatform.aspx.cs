using SDKPackage.Facade;
using SDKPackage.Kernel;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.PJConfig
{
    public partial class AddPlatform : System.Web.UI.Page
    {
        int myversionid = GameRequest.GetQueryInt("myversionid", 0);
        int platformid = GameRequest.GetQueryInt("platformid", 0);
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            if (myversionid == 0 || platformid == 0)
            {
                lblLog.Text = "参数错误";
                this.btnSubmit.Visible = true;
            }

        }

        protected void btnSubmit_Click(object sender, EventArgs e)
        {
            if (myversionid == 0 || platformid == 0)
            {
                lblLog.Text = "参数错误";
                return;
            }
            string platformIcon = this.hfPlatformIcon.Value;
            string platformname = CtrlHelper.GetText(txtPlatformName);
            string platformdisplayname = CtrlHelper.GetText(txtPlatformDisplayName);
            string sdkversion = CtrlHelper.GetText(txtSdkVersion);
            if (platformid == 1)
            {
                string SDKPackageDir = ConfigurationManager.AppSettings["SDKPackageDir"];
                SDKPackageDir = SDKPackageDir + "SDK\\Channel_SDK\\" + platformname + "\\" + sdkversion + "\\";
                if (!System.IO.Directory.Exists(SDKPackageDir))
                {
                    this.lblLog.Text = "渠道版本创建失败:该渠道版本文件夹不存在！";
                    return;
                }
            }
            Message umsg = aideNativeWebFacade.AddPlatForm(platformname, platformdisplayname, sdkversion, myversionid, platformid, platformIcon,Context.User.Identity.Name);
            if (umsg.Success)
            {
                Response.Write("<script>window.opener.document.getElementById(\"hfreturnVal\").value = \"success\"</script>");
            }
            lblLog.Text = umsg.Content;
        }


    }
}