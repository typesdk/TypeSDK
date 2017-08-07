using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.PJPackage
{
    public partial class SelectPlatform : System.Web.UI.Page
    {
        protected string platform = GameRequest.GetQueryString("platform");
        protected string gameId = GameRequest.GetQueryString("gameid");
        protected string gameName = GameRequest.GetQueryString("gameName");
        protected string gameDisplayName = GameRequest.GetQueryString("gameDisplayName");
        protected string gamenamespell = GameRequest.GetQueryString("gamenamespell");
        protected bool isBack = false;
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!string.IsNullOrEmpty(gameName) && !string.IsNullOrEmpty(gameDisplayName))
                isBack = true;
        }
    }
}