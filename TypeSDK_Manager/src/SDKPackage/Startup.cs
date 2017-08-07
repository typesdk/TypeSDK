using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(SDKPackage.Startup))]
namespace SDKPackage
{
    public partial class Startup {
        public void Configuration(IAppBuilder app) {
            ConfigureAuth(app);
        }
    }
}
