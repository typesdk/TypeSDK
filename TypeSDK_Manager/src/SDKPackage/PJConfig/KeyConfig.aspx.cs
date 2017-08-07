using System;
using System.Collections.Generic;
using System.Linq;
using System.Data;
using System.Data.SqlClient;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.PJConfig
{
    public partial class KeyConfig : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {

        }

        protected void ButtonAddKey_Click(object sender, EventArgs e)
        {
            try
            {
                string fileName = KeyFileUpload.FileName;
                string SDKPackageDir = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageKey"];
                string SDKPackageDirMapping = System.Configuration.ConfigurationManager.AppSettings["SDKAndroidPackageKeyMapping"];

                string uploadPatch = SDKPackageDir;
                string uploadFile = uploadPatch + fileName;
                string readFile = SDKPackageDirMapping + fileName;
                //if (!System.IO.Directory.Exists(uploadPatch))
                //{
                //    System.IO.Directory.CreateDirectory(uploadPatch);
                //}
                KeyFileUpload.SaveAs(uploadFile);

                string connStr = System.Configuration.ConfigurationManager.ConnectionStrings["SdkPackageConnString"].ToString();
                SqlConnection conn = new SqlConnection(connStr);
                SqlCommand saveKeyCom = new SqlCommand("sdk_addSignatureKey", conn);
                saveKeyCom.CommandType = CommandType.StoredProcedure;
                saveKeyCom.Parameters.Add("@keyId", SqlDbType.Int);
                saveKeyCom.Parameters.Add("@keyName", SqlDbType.NVarChar, 200);
                saveKeyCom.Parameters.Add("@keyStore", SqlDbType.NVarChar, 200);
                saveKeyCom.Parameters.Add("@keyStorePhysics", SqlDbType.NVarChar, 200);
                saveKeyCom.Parameters.Add("@keyStorePassword", SqlDbType.NVarChar, 200);
                saveKeyCom.Parameters.Add("@keyAlias", SqlDbType.NVarChar, 200);
                saveKeyCom.Parameters.Add("@keyAliasPassword", SqlDbType.NVarChar, 200);
                saveKeyCom.Parameters["@keyId"].Value = 0;
                saveKeyCom.Parameters["@keyName"].Value = KeyNameBox.Text;
                saveKeyCom.Parameters["@keyStore"].Value = readFile;
                saveKeyCom.Parameters["@keyStorePhysics"].Value = uploadFile;
                saveKeyCom.Parameters["@keyStorePassword"].Value = KeyStorePasswordBox.Text;
                saveKeyCom.Parameters["@keyAlias"].Value = KeyAliasBox.Text;
                saveKeyCom.Parameters["@keyAliasPassword"].Value = KeyAliasPasswordTextBox.Text;
                saveKeyCom.Connection.Open();
                saveKeyCom.ExecuteNonQuery();
                saveKeyCom.Connection.Close();
                this.LabelLog.Text = "秘钥添加成功！";
                ListView1.DataBind();
            }
            catch(Exception ex)
            {
                this.LabelLog.Text = ex.Message;
            }
        }


    }
}