using SDKPackage.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace SDKPackage.PJPackage
{
    public partial class SelectGame : System.Web.UI.Page
    {
        protected int rowsCount = 0;
        protected string platform = GameRequest.GetQueryString("platform");
        protected string gameId = GameRequest.GetQueryString("gameid");
        protected string gamenamespell=GameRequest.GetQueryString("gamenamespell");
        protected bool isBack = false;
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!string.IsNullOrEmpty(gameId))
                isBack = true;
            this.saveusername.Value = Context.User.Identity.Name;
        }
    }
}