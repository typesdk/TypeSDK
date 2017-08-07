namespace SDKPackage.Utils
{
    using System;
    using System.Collections.Generic;
    using System.Data;
    using System.Diagnostics;
    using System.Text;
    using System.Xml;

    public class XmlOperate
    {
        /// <summary>
        /// XML文档的操作类型
        /// </summary>
        public enum OperateXmlMethod
        {
            /// <summary>
            /// 仅操作属性
            /// </summary>
            XmlProperty ,
            /// <summary>
            ///  仅操作节点
            /// </summary>
            XmlNodes ,
            /// <summary>
            /// 属性和节点都操作
            /// </summary>
            All
        }
        #region 属性
        private string _fileContent;
        private string _filePath;
        private OperateXmlMethod _Method;//XML文档的操作类型
        private string _xPath;
        /// <summary>
        /// XML格式的内容
        /// </summary>
        public string fileContent
        {
            get
            {
                return this._fileContent;
            }
            set
            {
                this._fileContent = value;
            }
        }
        /// <summary>
        ///  XML文件的绝对路径
        /// </summary>
        public string filePath
        {
            get
            {
                return this._filePath;
            }
            set
            {
                this._filePath = value;
            }
        }
        /// <summary>
        /// 操作方式
        /// </summary>
        public OperateXmlMethod Method
        {
            get
            {
                return this._Method;
            }
            set
            {
                this._Method = value;
            }
        }
        /// <summary>
        ///  xPath表达式
        /// </summary>
        public string xPath
        {
            get
            {
                return this._xPath;
            }
            set
            {
                this._xPath = value;
            }
        }
        #endregion 

        public XmlOperate()
        {
        }

        public XmlOperate(string Path)
        {
            this.filePath = Path;
        }
        /// <summary>
        /// 检测指定节点的属性或者子节点名称是否存在
        /// </summary>
        /// <param name="Name">指定属性或子节点</param>
        /// <returns></returns>
        public bool CheckXml( string Name )
        {
            bool flag = false;
            XmlDocument document = new XmlDocument();
            document.Load( this.filePath );
            XmlElement element = ( XmlElement )document.SelectSingleNode( this.xPath );
            switch ( this.Method )
            {
                case OperateXmlMethod.XmlProperty:
                    if ( element.HasAttribute( Name ) )
                    {
                        flag = true;
                    }
                    return flag;

                case OperateXmlMethod.XmlNodes:
                    for ( int i = 0; i < element.ChildNodes.Count; i++ )
                    {
                        if ( element.ChildNodes.Item( i ).Name == Name )
                        {
                            flag = true;
                        }
                    }
                    return flag;
            }
            return flag;
        }
        /// <summary>
        /// 提取指定节点的指定属性或子节点的值
        /// </summary>
        /// <param name="name">指定属性或子节点</param>
        /// <returns></returns>
        public IList<string> GetXml( string name )
        {
            List<string> list = new List<string>();
            int num = 0;
            XmlDocument document = new XmlDocument();
            document.Load( this.filePath );
            XmlNodeList list2 = document.SelectNodes( this.xPath );
            foreach ( XmlNode node in list2 )
            {
                XmlElement element = ( XmlElement )node;
                switch ( this.Method )
                {
                    case OperateXmlMethod.XmlProperty:
                        if ( element.HasAttribute( name ) )
                        {
                            list.Add( element.GetAttribute( name ) );
                        }
                        break;

                    case OperateXmlMethod.XmlNodes:
                        {
                            XmlNodeList childNodes = element.ChildNodes;
                            foreach ( XmlNode node2 in childNodes )
                            {
                                XmlElement element2 = ( XmlElement )node2;
                                if ( element2.Name == name )
                                {
                                    list.Add( element2.InnerText );
                                }
                            }
                            break;
                        }
                }
                num++;
            }
            return list;
        }
        /// <summary>
        /// 提取指定节点的指定属性或子节点的值
        /// </summary>
        /// <param name="nodes">指定节点</param>
        /// <param name="type">0为属性，1为节点</param>
        /// <param name="name">指定属性或子节点</param>
        /// <returns></returns>
        public IList<string> GetXml( string nodes , int type , string name )
        {
            List<string> list = new List<string>();
            int num = 0;
            XmlDocument document = new XmlDocument();
            document.Load( this.filePath );
            XmlNodeList list2 = document.SelectNodes( nodes );
            foreach ( XmlNode node in list2 )
            {
                XmlElement element = ( XmlElement )node;
                switch ( type )
                {
                    case 0:
                        if ( element.HasAttribute( name ) )
                        {
                            list.Add( element.GetAttribute( name ) );
                        }
                        break;

                    case 1:
                        {
                            XmlNodeList childNodes = element.ChildNodes;
                            foreach ( XmlNode node2 in childNodes )
                            {
                                XmlElement element2 = ( XmlElement )node2;
                                if ( element2.Name == name )
                                {
                                    list.Add( element2.InnerText );
                                }
                            }
                            break;
                        }
                }
                num++;
            }
            return list;
        }
        /// <summary>
        /// 提取单个节点的信息(包括属性及其子节点)
        /// </summary>
        /// <param name="nodes"></param>
        /// <returns></returns>
        public XmlElement GetXmlElement( string nodes )
        {
            XmlDocument document = new XmlDocument();
            document.Load( this.filePath );
            return ( XmlElement )document.SelectSingleNode( nodes );
        }
        /// <summary>
        ///  提取指定XML文件某个节点的信息(包括属性及其子节点)
        /// </summary>
        /// <param name="nodes">要提取的节点位置</param>
        /// <param name="method">0提取单个节点，1提取所有相关节点</param>
        /// <returns></returns>
        public XmlNodeList GetXmlNodeList( string nodes , int method )
        {
            XmlDocument document = new XmlDocument();
            if ( !string.IsNullOrEmpty( this.fileContent ) )
            {
                document.LoadXml( this.fileContent );
            }
            else
            {
                document.Load( this.filePath );
            }
            switch ( method )
            {
                case 0:
                    return document.SelectSingleNode( nodes ).ChildNodes;

                case 1:
                    return document.SelectNodes( nodes );
            }
            return null;
        }

        public void ChangeNode(DataTable dt)
        {
            XmlDocument document = new XmlDocument();
            document.Load(this.filePath);
            XmlNode node = document.SelectSingleNode(this.xPath);
            if (node != null)
            {
                XmlElement element = (XmlElement) node;
                for (int i = 0; i < dt.Columns.Count; i++)
                {
                    XmlNodeList childNodes;
                    Debug.WriteLine(string.Format("XML 操作：{0}", i.ToString()));
                    if (dt.Columns[i].ColumnName.StartsWith("@"))
                    {
                        string name = dt.Columns[i].ColumnName.Replace("@", "");
                        if (element.HasAttribute(name))
                        {
                            element.SetAttribute(name, dt.Rows[0][i].ToString());
                        }
                        continue;
                    }
                    if (element.HasChildNodes)
                    {
                        childNodes = element.ChildNodes;
                    }
                    else
                    {
                        continue;
                    }
                    foreach (XmlNode node2 in childNodes)
                    {
                        XmlElement element2;
                        try
                        {
                            element2 = (XmlElement) node2;
                        }
                        catch
                        {
                            continue;
                        }
                        try
                        {
                            if (element2.Name == dt.Columns[i].ColumnName)
                            {
                                try
                                {
                                    element2.InnerXml = dt.Rows[0][i].ToString();
                                }
                                catch
                                {
                                    element2.InnerText = dt.Rows[0][i].ToString();
                                }
                            }
                        }
                        catch
                        {
                            break;
                        }
                    }
                }
                document.Save(this.filePath);
            }
        }

        public void ChangeNode(string parentNodeName, int type, string thename, string thevalue)
        {
            XmlDocument document = new XmlDocument();
            document.Load(this.filePath);
            XmlNodeList list = document.SelectNodes(parentNodeName);
            foreach (XmlNode node in list)
            {
                XmlElement element = (XmlElement) node;
                if (type == 0)
                {
                    if (element.HasAttribute(thename))
                    {
                        element.SetAttribute(thename, thevalue);
                    }
                }
                else if (type == 1)
                {
                    XmlNodeList childNodes = element.ChildNodes;
                    foreach (XmlNode node2 in childNodes)
                    {
                        XmlElement element2 = (XmlElement) node2;
                        if (element2.Name == thename)
                        {
                            try
                            {
                                element2.InnerXml = thevalue;
                            }
                            catch
                            {
                                element2.InnerText = thevalue;
                            }
                        }
                    }
                }
            }
            document.Save(this.filePath);
        }       

        public DataTable ConvertXmlNodeListDataTable(XmlNodeList xlist)
        {
            DataTable table = new DataTable();
            for (int i = 0; i < xlist.Count; i++)
            {
                DataRow row = table.NewRow();
                XmlElement element = (XmlElement) xlist.Item(i);
                int index = 0;
                while (index < element.Attributes.Count)
                {
                    if (!table.Columns.Contains("@" + element.Attributes[index].Name))
                    {
                        table.Columns.Add("@" + element.Attributes[index].Name);
                    }
                    row["@" + element.Attributes[index].Name] = element.Attributes[index].Value;
                    index++;
                }
                for (index = 0; index < element.ChildNodes.Count; index++)
                {
                    if (!table.Columns.Contains(element.ChildNodes.Item(index).Name))
                    {
                        table.Columns.Add(element.ChildNodes.Item(index).Name);
                    }
                    row[element.ChildNodes.Item(index).Name] = element.ChildNodes.Item(index).InnerText;
                }
                table.Rows.Add(row);
            }
            return table;
        }

        public DataTable ConvertXmlNodeListDataTable(XmlNodeList xlist, int type)
        {
            DataTable table = new DataTable();
            for (int i = 0; i < xlist.Count; i++)
            {
                int num2;
                DataRow row = table.NewRow();
                XmlElement element = (XmlElement) xlist.Item(i);
                if (type == 0)
                {
                    num2 = 0;
                    while (num2 < element.Attributes.Count)
                    {
                        if (!table.Columns.Contains("@" + element.Attributes[num2].Name))
                        {
                            table.Columns.Add("@" + element.Attributes[num2].Name);
                        }
                        row["@" + element.Attributes[num2].Name] = element.Attributes[num2].Value;
                        num2++;
                    }
                }
                else if (type == 1)
                {
                    for (num2 = 0; num2 < element.ChildNodes.Count; num2++)
                    {
                        if (!table.Columns.Contains(element.ChildNodes.Item(num2).Name))
                        {
                            table.Columns.Add(element.ChildNodes.Item(num2).Name);
                        }
                        row[element.ChildNodes.Item(num2).Name] = element.ChildNodes.Item(num2).InnerText;
                    }
                }
                table.Rows.Add(row);
            }
            return table;
        }

        public void CreateNode(string nodeName, DataTable dt)
        {
            XmlDocument document = new XmlDocument();
            document.Load(this.filePath);
            XmlNode node = document.SelectSingleNode(this.xPath);
            XmlElement newChild = document.CreateElement(nodeName);
            XmlElement element2 = null;
            if (!object.Equals(dt, null))
            {
                for (int i = 0; i < dt.Columns.Count; i++)
                {
                    if (dt.Columns[i].ColumnName.StartsWith("@"))
                    {
                        string name = dt.Columns[i].ColumnName.Replace("@", "");
                        newChild.SetAttribute(name, dt.Rows[0][i].ToString());
                    }
                    else
                    {
                        element2 = document.CreateElement(dt.Columns[i].ColumnName);
                        try
                        {
                            element2.InnerXml = dt.Rows[0][i].ToString();
                        }
                        catch
                        {
                            element2.InnerText = dt.Rows[0][i].ToString();
                        }
                        newChild.AppendChild(element2);
                    }
                }
            }
            node.AppendChild(newChild);
            document.Save(this.filePath);
        }

        public void CreateNodes(string nodeName, DataTable dt)
        {
            XmlDocument document = new XmlDocument();
            document.Load(this.filePath);
            XmlNode node = document.SelectSingleNode(this.xPath);
            for (int i = 0; i < dt.Rows.Count; i++)
            {
                XmlElement newChild = document.CreateElement(nodeName);
                XmlElement element2 = null;
                if (!object.Equals(dt, null))
                {
                    for (int j = 0; j < dt.Columns.Count; j++)
                    {
                        if (dt.Columns[j].ColumnName.StartsWith("@"))
                        {
                            string name = dt.Columns[j].ColumnName.Replace("@", "");
                            newChild.SetAttribute(name, dt.Rows[i][j].ToString());
                        }
                        else
                        {
                            element2 = document.CreateElement(dt.Columns[j].ColumnName);
                            try
                            {
                                element2.InnerXml = dt.Rows[i][j].ToString();
                            }
                            catch
                            {
                                element2.InnerText = dt.Rows[i][j].ToString();
                            }
                            newChild.AppendChild(element2);
                        }
                    }
                }
                node.AppendChild(newChild);
            }
            document.Save(this.filePath);
        }

        public void CreateNodes(string nodeName, DataTable dt, bool CreateNull)
        {
            XmlDocument document = new XmlDocument();
            document.Load(this.filePath);
            XmlNode node = document.SelectSingleNode(this.xPath);
            for (int i = 0; i < dt.Rows.Count; i++)
            {
                XmlElement newChild = document.CreateElement(nodeName);
                XmlElement element2 = null;
                if (!object.Equals(dt, null))
                {
                    for (int j = 0; j < dt.Columns.Count; j++)
                    {
                        if (dt.Columns[j].ColumnName.StartsWith("@"))
                        {
                            string name = dt.Columns[j].ColumnName.Replace("@", "");
                            if (CreateNull)
                            {
                                newChild.SetAttribute(name, dt.Rows[i][j].ToString());
                            }
                            else if (dt.Rows[i][j].ToString() != "")
                            {
                                newChild.SetAttribute(name, dt.Rows[i][j].ToString());
                            }
                        }
                        else
                        {
                            element2 = document.CreateElement(dt.Columns[j].ColumnName);
                            if (CreateNull)
                            {
                                try
                                {
                                    element2.InnerXml = dt.Rows[i][j].ToString();
                                }
                                catch
                                {
                                    element2.InnerText = dt.Rows[i][j].ToString();
                                }
                                newChild.AppendChild(element2);
                            }
                            else if (dt.Rows[i][j].ToString() != "")
                            {
                                try
                                {
                                    element2.InnerXml = dt.Rows[i][j].ToString();
                                }
                                catch
                                {
                                    element2.InnerText = dt.Rows[i][j].ToString();
                                }
                                newChild.AppendChild(element2);
                            }
                        }
                    }
                }
                node.AppendChild(newChild);
            }
            document.Save(this.filePath);
        }

        public void CreateXml()
        {
            FileManager.Create(this.filePath, FsoMethod.File);
            XmlTextWriter writer = new XmlTextWriter(this.filePath, Encoding.GetEncoding("gb2312")) {
                Formatting = Formatting.Indented,
                Indentation = 3
            };
            writer.WriteStartDocument();
            writer.Flush();
            writer.Close();
        }

        public void CreateXml(string rootNodeName)
        {
            FileManager.Create(this.filePath, FsoMethod.File);
            XmlTextWriter writer = new XmlTextWriter(this.filePath, Encoding.GetEncoding("gb2312")) {
                Formatting = Formatting.Indented,
                Indentation = 3
            };
            writer.WriteStartDocument();
            writer.WriteStartElement(rootNodeName);
            writer.WriteEndElement();
            writer.WriteEndDocument();
            writer.Flush();
            writer.Close();
        }

        public void DeleteNode(string nodeName)
        {
            XmlDocument document = new XmlDocument();
            document.Load(this.filePath);
            XmlNode node = document.SelectSingleNode(this.xPath);
            XmlNode oldChild = node.SelectSingleNode(nodeName);
            if (oldChild != null)
            {
                node.RemoveChild(oldChild);
                document.Save(this.filePath);
            }
        }
      
       
    }
}

