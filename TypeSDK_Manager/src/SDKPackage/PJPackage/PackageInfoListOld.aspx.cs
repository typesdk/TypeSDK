using SDKPackage.Facade;
using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.PJPackage
{
    public partial class PackageInfoListOld : System.Web.UI.Page
    {
        protected string systemname;
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {
            if (GameRequest.GetQueryString("gameid") != "")
            {
                this.DropDownList1.SelectedValue = GameRequest.GetQueryString("gameid");
            } if (GameRequest.GetQueryString("taskType") != "")
            {
                this.DropDownList2.SelectedValue = GameRequest.GetQueryString("taskType");

            } if (GameRequest.GetQueryString("systemid") != "")
            {
                this.DropDownList3.SelectedValue = GameRequest.GetQueryString("systemid");
            } if (GameRequest.GetQueryString("ck") == "1")
            {
                this.ckMy.Checked = true;
            }
            this.HiddenFieldUserName.Value = Context.User.Identity.Name;
            this.systemname = this.DropDownList3.SelectedValue;
            BindingList();
        }


        private void BindingList()
        {
            string gameid = this.DropDownList1.SelectedValue;
            DataView dv = (DataView)SqlDataSource1.Select((DataSourceSelectArguments.Empty));
            DataTable dt = GetPackageList(dv.Table.DataSet);
            this.GamePlaceList.DataSource = dt;
            this.GamePlaceList.DataBind();
            var p = dt.AsEnumerable().Where(r => r["PackageTaskStatus"].ToString() == "0" || r["PackageTaskStatus"].ToString() == "1" || r["PackageTaskStatus"].ToString() == "2").Select(d => d);

            //if (p != null && p.Count() > 0)
            //{
            //    this.Timer1.Enabled = true;
            //}
            //else
            //{
            //    this.Timer1.Enabled = false;
            //}
        }

        private DataTable GetPackageList(DataSet ds)
        {
            DataTable dt = ds.Tables[0];
            if (Cache["Roleid"] == null || Cache["Roleid"].ToString() == "")
            {
                BindingCache();
            }
            if (Cache["Roleid"].ToString() == "1" && this.DropDownList3.SelectedItem.Text == "Android")
            {
                if (ds.Tables[0].AsEnumerable().Where(r => r["CompileMode"].ToString() != "debug").Count() > 0)
                {
                    dt = ds.Tables[0].AsEnumerable().Where(r => r["CompileMode"].ToString() != "debug").Select(d => d).CopyToDataTable();//ds.Tables[0].Select("CompileMode!=debug").CopyToDataTable().DataSet;
                }
            }
            return dt;
        }

        private void BindingCache()
        {
            string sql = string.Format(@"  select * from [AspNetUserRoles] r inner join AspNetUsers u on r.UserId=u.Id and u.UserName='{0}' and RoleId in (2,3)", Context.User.Identity.Name);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            if (ds.Tables[0].Rows.Count > 0)
                Cache["Roleid"] = "0";
            else
                Cache["Roleid"] = "1";
        }

        protected void Button1_Click(object sender, EventArgs e)
        {
            //this.GamePlaceList.DataBind();
        }

        protected void ButtonDelete_Click(object sender, EventArgs e)
        {
            //this.GamePlaceList.DataBind();
        }

        /// <summary>
        /// 删除任务
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void GamePlaceList_ItemCommand(object sender, ListViewCommandEventArgs e)
        {
            LabelMessage.Text = "";
            if (e.CommandName == "del")
            {
                string recid = e.CommandArgument.ToString();
                string sql = string.Format(@"select gi.*,npct.CreateTaskID,npct.PackageName,npct.PackageTaskStatus from {1} npct inner join sdk_GameInfo gi on npct.GameID=gi.GameID and npct.RecID={0}", recid, systemname == "Android" ? "sdk_NewPackageCreateTask" : "sdk_NewPackageCreateTask_IOS");
                DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
                if (ds.Tables[0].Rows.Count > 0)
                {
                    string filePath = "";
                    string logPath = "";
                    string gamename = ds.Tables[0].Rows[0]["GameName"].ToString();
                    string createtaskid = ds.Tables[0].Rows[0]["CreateTaskID"].ToString();
                    string gamenamespell = ds.Tables[0].Rows[0]["GameNameSpell"].ToString();
                    string packagename = ds.Tables[0].Rows[0]["PackageName"].ToString();
                    string packagetaskstatus = ds.Tables[0].Rows[0]["PackageTaskStatus"].ToString();
                    if (systemname == "Android")
                    {
                        filePath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageApk"] + gamename + "\\" + createtaskid + "\\";// + packagename;
                        logPath = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageLogs"] + createtaskid + "\\";
                    }
                    else
                    {
                        filePath = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageIPA"] + gamenamespell + "\\" + createtaskid + "\\";
                        logPath = System.Configuration.ConfigurationManager.AppSettings["SDKIOSPackageLogs"] + createtaskid + "\\";
                    }
                    try
                    {
                        if (packagetaskstatus == "3")
                        {
                            int fileNo = System.IO.Directory.GetFileSystemEntries(filePath).Length;
                            if (fileNo > 1)
                            {
                                System.IO.File.Delete(filePath + packagename);
                            }
                            else
                            {
                                System.IO.Directory.Delete(filePath, true);
                            }
                            //if (System.IO.Directory.Exists(filePath))
                            //if (System.IO.Directory.Exists(logPath))
                            int logNo = System.IO.Directory.GetFileSystemEntries(logPath).Length;
                            if (logNo > 1)
                            {
                                System.IO.File.Delete(logPath + recid + ".log");
                            }
                            else
                            {
                                System.IO.Directory.Delete(logPath, true);
                            }
                        }
                        aideNativeWebFacade.DeleteNewPackageTask(Convert.ToInt32(recid), systemname);
                        this.GamePlaceList.DataBind();
                    }
                    catch (Exception ex)
                    {
                        LabelMessage.Text = ex.Message;
                        return;
                    }
                }
                else
                {
                    LabelMessage.Text = "未找到关联数据:无法执行删除！";
                }
            }
        }

        protected void Timer1_Tick(object sender, EventArgs e)
        {
            BindingList();
        }

    }
}