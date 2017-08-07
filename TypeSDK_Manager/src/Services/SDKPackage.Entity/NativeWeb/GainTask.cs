using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SDKPackage.Entity.NativeWeb
{
    /// <summary>
    /// 实体类 AccountsInfo。(属性说明自动提取数据库字段的描述信息)
    /// </summary>
    [Serializable]
    public partial class GainTask
    {
        #region 常量
        // <summary>
        /// 任务ID
        /// </summary>
        public const string _RecID = "RecID";

        // <summary>
        /// 游戏简称
        /// </summary>
        public const string _GameName = "GameName";

        // <summary>
        /// 游戏编号
        /// </summary>
        public const string _GameID = "GameID";

        // <summary>
        /// 渠道简称
        /// </summary>
        public const string _PlatFormName = "PlatFormName";

        // <summary>
        /// 游戏版本
        /// </summary>
        public const string _GameVersion = "GameVersion";

        // <summary>
        /// 任务批次编号
        /// </summary>
        public const string _StrCollectDatetime = "StrCollectDatetime";

        // <summary>
        /// 图标文件名称
        /// </summary>
        public const string _IconPath = "IconPath";

        /// <summary>
        /// 批次号
        /// </summary>
        public const string _CreateTaskID = "CreateTaskID";

        /// <summary>
        /// 数娱版本
        /// </summary>
        public const string _MyVersion = "MyVersion";

        /// <summary>
        /// 游戏全拼
        /// </summary>
        public const string _GameNameSpell = "GameNameSpell";

        /// <summary>
        /// 开发工具版本
        /// </summary>
        public const string _UnityVer = "UnityVer";

        /// <summary>
        /// 产品名称
        /// </summary>
        public const string _ProductName = "ProductName";

        public const string _ChannelVersio = "ChannelVersion";

        public const string _IsEncryption = "IsEncryption";

        public const string _AdID = "AdID";

        public const string _PlugInID = "PlugInID";

        public const string _PlugInVersion = "PlugInVersion";

        public const string _CompileMode = "CompileMode";

        public const string _KeyName = "KeyName";

        #endregion

        #region 构造函数
        public GainTask()
        {
            m_gamenamespell = "";
            m_unityver = "";
            m_productname = "";
            m_channelversion = "";
            m_adid = "0";
            m_pluginid = 0;
            m_compilemode = "release";
        }
        #endregion

        #region 变量
        private int m_recid;
        private string m_gamename;
        private string m_gameid;
        private string m_platformname;
        private string m_gameversion;
        private string m_strcollectdatetime;
        private string m_iconpath;
        private string m_createtaskid;
        private string m_myversion;
        private string m_gamenamespell;
        private string m_unityver;
        private string m_productname;
        private string m_channelversion;
        private string m_isencryption;
        private string m_adid;
        private int m_pluginid;
        private string m_pluginversion;
        private string m_compilemode;
        private string m_keyname;
        #endregion

        #region 公共

        public int RecID
        {
            get { return m_recid; }
            set { m_recid = value; }
        }

        public string PlatFormName
        {
            get { return m_platformname; }
            set { m_platformname = value; }
        }

        public string GameName
        {
            get { return m_gamename; }
            set { m_gamename = value; }
        }

        public string GameID
        {
            get { return m_gameid; }
            set { m_gameid = value; }
        }


        public string GameVersion
        {
            get { return m_gameversion; }
            set { m_gameversion = value; }
        }

        public string StrCollectDatetime
        {
            get { return m_strcollectdatetime; }
            set { m_strcollectdatetime = value; }
        }

        public string IconPath
        {
            get { return m_iconpath; }
            set { m_iconpath = value; }
        }

        public string CreateTaskID
        {
            get { return m_createtaskid; }
            set { m_createtaskid = value; }
        }

        public string MyVersion
        {
            get { return m_myversion; }
            set { m_myversion = value; }
        }
        public string GameNameSpell
        {

            get { return m_gamenamespell; }
            set { m_gamenamespell = value; }
        }

        public string UnityVer
        {

            get { return m_unityver; }
            set { m_unityver = value; }
        }

        public string ProductName
        {

            get { return m_productname; }
            set { m_productname = value; }
        }

        public string ChannelVersion
        {

            get { return m_channelversion; }
            set { m_channelversion = value; }
        }
        public string IsEncryption
        {

            get { return m_isencryption; }
            set { m_isencryption = value; }
        }
        public string AdID
        {

            get { return m_adid; }
            set { m_adid = value; }
        }


        public int PlugInID
        {

            get { return m_pluginid; }
            set { m_pluginid = value; }
        }
        public string PlugInVersion
        {

            get { return m_pluginversion; }
            set { m_pluginversion = value; }
        }

        public string CompileMode
        {
            get { return m_compilemode; }
            set { m_compilemode = value; }
        }
        public string KeyName
        {
            get { return m_keyname; }
            set { m_keyname = value; }
        }

        #endregion
    }
}
