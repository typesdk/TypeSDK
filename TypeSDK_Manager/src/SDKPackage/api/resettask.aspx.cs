using SDKPackage.Facade;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.api
{
    public partial class resettask : System.Web.UI.Page
    {
        string taskid = GameRequest.GetQueryString("taskid");
        string mode = GameRequest.GetQueryString("mode");

        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                ExecRestTask();
            }
        }

        private void ExecRestTask()
        {

            if (string.IsNullOrEmpty(taskid))
            {
                Response.Write("error");
                return;
            }

            string sql = string.Format(@"update sdk_NewPackageCreateTask set PackageTaskStatus=1 {1} where recid={0}", taskid, string.IsNullOrEmpty(mode) ? "" : ",CompileMode='" + mode + "'");
            int row = aideNativeWebFacade.ExecuteSql(sql);
            if (row < 1)
            {
                Response.Write("error");
                return;
            }
            Response.Write("success");
            return;
        }
    }
}