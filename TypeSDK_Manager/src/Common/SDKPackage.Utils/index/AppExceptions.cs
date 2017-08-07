using System;
using System.CodeDom.Compiler;
using System.ComponentModel;
using System.Diagnostics;
using System.Globalization;
using System.Resources;
using System.Runtime.CompilerServices;

namespace SDKPackage.Utils.Properties
{
    [DebuggerNonUserCode, GeneratedCode("System.Resources.Tools.StronglyTypedResourceBuilder", "2.0.0.0"), CompilerGenerated]
    internal class AppExceptions
    {
        private static CultureInfo resourceCulture;
        private static System.Resources.ResourceManager resourceMan;

        internal AppExceptions()
        {
        }

        [EditorBrowsable(EditorBrowsableState.Advanced)]
        internal static CultureInfo Culture
        {
            get
            {
                return resourceCulture;
            }
            set
            {
                resourceCulture = value;
            }
        }

        internal static string IPdataFileNotExists
        {
            get
            {
                return ResourceManager.GetString("IPdataFileNotExists", resourceCulture);
            }
        }

        [EditorBrowsable(EditorBrowsableState.Advanced)]
        internal static System.Resources.ResourceManager ResourceManager
        {
            get
            {
                if (object.ReferenceEquals(resourceMan, null))
                {
                    System.Resources.ResourceManager manager = new System.Resources.ResourceManager("SDKPackage.Utils.Properties.AppExceptions", typeof(AppExceptions).Assembly);
                    resourceMan = manager;
                }
                return resourceMan;
            }
        }

        internal static string Terminator_ExceptionTemplate
        {
            get
            {
                return ResourceManager.GetString("Terminator_ExceptionTemplate", resourceCulture);
            }
        }

        internal static string WebConfigHasNotAddKey
        {
            get
            {
                return ResourceManager.GetString("WebConfigHasNotAddKey", resourceCulture);
            }
        }
    }
}
