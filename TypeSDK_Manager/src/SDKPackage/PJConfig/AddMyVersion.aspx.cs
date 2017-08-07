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
    public partial class AddMyVersion : System.Web.UI.Page
    {
        int myversionid = GameRequest.GetQueryInt("myversionid",0);
        int platformid = GameRequest.GetQueryInt("platformid", 0);
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            if (myversionid == 0 || platformid==0)
            {
                lblLog.Text = "参数错误";
                this.btnAddMyVersion.Visible = true;
            }
        }

        protected void btnAddMyVersion_Click(object sender, EventArgs e)
        {            
            if (myversionid == 0 || platformid==0)
            {
                lblLog.Text = "参数错误";
                return;
            }
            string version = CtrlHelper.GetText(txtMyVersion);
            if (string.IsNullOrEmpty(version))
            {
                lblLog.Text = "请填写新版本";
                return;
            }
            string SDKPackageDir = ConfigurationManager.AppSettings["SDKPackageDir"];
            SDKPackageDir += "SDK\\Type_SDK\\" + version;
            if (!System.IO.Directory.Exists(SDKPackageDir))
            {
                Response.Write("<script>alert('SDK版本文件夹尚未创建！')</script>");
                return;
            }
            Message umsg = aideNativeWebFacade.AddMyVersion(version, Context.User.Identity.Name, platformid,myversionid);
            if (umsg.Success)
            {
                Response.Write("<script>window.opener.document.getElementById(\"hfreturnVal\").value = \"success\"</script>");
            } 
            lblLog.Text = umsg.Content;
        }
    }
}