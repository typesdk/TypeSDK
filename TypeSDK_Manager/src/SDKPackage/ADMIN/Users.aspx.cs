using SDKPackage.Facade;
using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.ADMIN
{
    public partial class Users : System.Web.UI.Page
    {
        NativeWebFacade adieNativeWebFacade = new NativeWebFacade();
        protected void Page_Load(object sender, EventArgs e)
        {

        }

        protected string GetRoleGameName(string id)
        {
            DataSet ds = adieNativeWebFacade.GetRoleGame(id);
            if (ds.Tables[0].Rows.Count > 0)
            {
                string gamename = "";
                for (int i = 0; i < ds.Tables[0].Rows.Count; i++)
                {
                    gamename += ds.Tables[0].Rows[i]["GameName"].ToString() + " ";
                }
                return gamename;
            
            }
            return "";
        }
    }
}