using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace SDKPackage.Utils
{
    public class TextConvert
    {
        public static void GetXml(string strVal)
        {
            XmlDocument dom = new XmlDocument();
            dom.LoadXml(strVal);
            XmlElement root = dom.DocumentElement;
            foreach (XmlNode node in root)
            {
                if (!string.IsNullOrEmpty(node.Value))
                    TextEncrypt.Base64Decode(node.Value);
            }
        }
    }
}
