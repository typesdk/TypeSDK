using SDKPackage.Utils;
using SDKPackage.Facade;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Data;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.GameConfig
{
    public partial class GamePlatformIcon : System.Web.UI.Page
    {
        NativeWebFacade aideNativeWebFacade = new NativeWebFacade();
        protected string gameid = GameRequest.GetQueryString("gameid");
        protected string gamename = GameRequest.GetQueryString("gamename");
        protected string androidversionid = GameRequest.GetQueryString("androidversionid");
        protected string iosversionid = GameRequest.GetQueryString("iosversionid");
        protected string gamedisplayname = GameRequest.GetQueryString("gamedisplayname");
        protected string platformid = GameRequest.GetQueryString("platformid");
        protected string platformname = GameRequest.GetQueryString("platformname");
        protected string pluginid = GameRequest.GetQueryString("pluginid");
        protected string systemname = GameRequest.GetQueryString("systemid");
        protected void Page_Load(object sender, EventArgs e)
        {
            DataSet ds = aideNativeWebFacade.GetGamePlatformIcon(int.Parse(gameid), int.Parse(platformid));
            if (ds.Tables[0].Rows.Count == 1)
            {
                labelCurrIcon.Text = ds.Tables[0].Rows[0]["iconName"].ToString();
            }
        }
    }
}