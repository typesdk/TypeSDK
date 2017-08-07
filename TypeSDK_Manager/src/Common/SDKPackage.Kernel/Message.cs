namespace SDKPackage.Kernel
{
    using System;
    using System.Collections;
    using System.Runtime.CompilerServices;
    /// <summary>
    /// 携带消息类
    /// </summary>
    [Serializable]
    public class Message : IMessage
    {
        private int m_messageID;
        private bool m_success;
        /// <summary>
        /// 消息标识
        /// </summary>
        public int MessageID
        {
            get
            {
                return this.m_messageID;
            }
            set
            {
                this.m_messageID = value;
                this.m_success = this.m_messageID == 0;
            }
        }
        /// <summary>
        /// 成功状态
        /// </summary>
        public bool Success
        {
            get
            {
                return this.m_success;
            }
            set
            {
                this.m_success = value;
                if ( this.m_success )
                {
                    this.m_messageID = 0;
                }
                else
                {
                    this.m_messageID = -1;
                }
            }
        }
        public Message( )
        {
            this.MessageID = 0;
            this.Success = true;
            this.Content = string.Empty;
            this.EntityList = new ArrayList();
        }

        public Message( bool isSuccess )
            : this( isSuccess , "" )
        {
        }

        public Message( bool isSuccess , string content )
            : this()
        {
            this.MessageID = isSuccess ? 0 : -1;
            this.Content = content;
        }

        public Message( int messageID , string content )
            : this()
        {
            this.MessageID = messageID;
            this.Content = content;
        }

        public Message( bool isSuccess , string content , ArrayList entityList )
            : this( isSuccess , content )
        {
            this.EntityList = entityList;
        }

        public Message( int messageID , string content , ArrayList entityList )
            : this( messageID , content )
        {
            this.EntityList = entityList;
        }
        /// <summary>
        /// 增加携带对象
        /// </summary>
        /// <param name="entityList"></param>
        public void AddEntity( ArrayList entityList )
        {
            this.EntityList = entityList;
        }
        /// <summary>
        /// 增加携带对象
        /// </summary>
        /// <param name="entity"></param>
        public void AddEntity( object entity )
        {
            this.EntityList.Add( entity );
        }
        /// <summary>
        /// 清空携带对象集
        /// </summary>
        public void ResetEntityList( )
        {
            if ( this.EntityList != null )
            {
                this.EntityList.Clear();
            }
        }
        /// <summary>
        /// 消息内容
        /// </summary>
        public string Content { get; set; }
        /// <summary>
        /// 携带对象列表
        /// </summary>
        public ArrayList EntityList { get; set; }


    }
}

