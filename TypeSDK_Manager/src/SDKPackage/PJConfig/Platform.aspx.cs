using SDKPackage.Facade;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.PJConfig
{
    public partial class Platform : System.Web.UI.Page
    {
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected string platfromid = string.Empty;
        protected string myversion = string.Empty;
        static DataSet ds = new DataSet();

        protected void Page_Load(object sender, EventArgs e)
        {
            platfromid = this.ddlPlatforms.SelectedValue;
            myversion = this.ddlSdkVersionList.SelectedValue;
            if (!IsPostBack)
            {
                BindingMyVersion();
                BindingDsPlatformVersion();
            }
        }
        private void BindingDsPlatformVersion()
        {
            string systemid = this.ddlPlatforms.SelectedValue;
            //string sqlAndroid = string.Format(@"select id,PlatformID,[Version] from [sdk_PlatformVersion] where SystemID=" + systemid + " order by Version desc");
            string sqlAndroid = string.Format(@"select id,PlatformID,[Version],[SystemID] from [sdk_PlatformVersion] order by Version desc");
            ds = aideNativeWebFacade.GetDataSetBySql(sqlAndroid);
        }

        protected DataSet GetDropDownListPlatformVersionDataSource(string platformID, string sdkVersionID)
        {
            DataSet dsPlatform = SetDsHead();
            var dr = ds.Tables[0].Select("PlatformID=" + platformID);
            if (dr.Count() > 0)
            {
                foreach (var row in dr)
                {
                    dsPlatform.Tables[0].ImportRow(row);
                }
                //排序
                if (!string.IsNullOrEmpty(sdkVersionID))
                {
                    var _dr = dsPlatform.Tables[0].Select("ID=" + sdkVersionID);
                    if (_dr.Count() > 0)
                    {
                        DataRow dr2 = dsPlatform.Tables[0].NewRow();
                        dr2["id"] = _dr[0].ItemArray[0];
                        dr2["PlatformID"] = _dr[0].ItemArray[1];
                        dr2["Version"] = _dr[0].ItemArray[2];
                        dsPlatform.Tables[0].Rows.Remove(_dr[0]);
                        dsPlatform.Tables[0].Rows.InsertAt(dr2, 0);
                    }

                }
                //return dsPlatform;
            }
            return dsPlatform;

        }

        private DataSet SetDsHead()
        {
            DataSet ds = new DataSet();
            DataTable dt = new DataTable();
            dt.Columns.Add("id", typeof(string));
            dt.Columns.Add("PlatformID", typeof(string));
            dt.Columns.Add("Version", typeof(string));
            dt.Columns.Add("SystemID", typeof(string));
            ds.Tables.Add(dt);
            return ds;
        }

        /// <summary>
        /// 绑定数银渠道版本
        /// </summary>
        private void BindingMyVersion()
        {
            string sql = "select id , MyVersion from [sdk_TypeSdkVersion] where platformid=" + platfromid + " order by MyVersion desc";
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);

            this.ddlSdkVersionList.DataSource = ds;
            this.ddlSdkVersionList.DataTextField = "MyVersion";
            this.ddlSdkVersionList.DataValueField = "id";
            this.ddlSdkVersionList.DataBind();
            string id = ds.Tables[0].Rows[0]["id"].ToString();
            this.ddlSdkVersionList.SelectedItem.Value = id;
        }

        /// <summary>
        /// 绑定数据
        /// </summary>
        private void BindingData()
        {
            string version = this.ddlSdkVersionList.SelectedValue;
            string sql = string.Format(@"select pf.id,pf.SdkVersion,pf.MyVersionID,dpf.platformname,dpf.platformdisplayname from [sdk_Platform] pf,
                                         sdk_DefaultPlatform dpf  where pf.PlatformID=dpf.id and pf.MyVersionID={0} and pf.SystemID={1}", version, platfromid);
            DataSet ds = aideNativeWebFacade.GetDataSetBySql(sql);
            this.ListView1.DataSource = ds;
            this.ListView1.DataBind();
        }


        /// <summary>
        /// 切换平台
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        protected void ddlPlatforms_SelectedIndexChanged(object sender, EventArgs e)
        {
            BindingMyVersion();
            //BindingData();
        }

        protected void ListView1_ItemCommand(object sender, ListViewCommandEventArgs e)
        {
            if (e.CommandName == "nullity")
            {
                int id = Convert.ToInt32(e.CommandArgument.ToString().Split(',')[0]);
                byte nullity = Convert.ToByte(e.CommandArgument.ToString().Split(',')[1]);
                string sql = string.Format(@"update [sdk_DefaultPlatform] set nullity={0} where id={1}", nullity == 0 ? 1 : 0, id);
                aideNativeWebFacade.ExecuteSql(sql);
                this.ListView1.DataBind();
            }
        }
    }
}