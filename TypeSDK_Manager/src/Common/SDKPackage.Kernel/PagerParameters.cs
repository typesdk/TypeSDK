namespace SDKPackage.Kernel
{
    using System;
    /// <summary>
    /// 分页参数
    /// </summary>
    public class PagerParameters
    {
        #region 属性
        private bool m_ascending;
        /// <summary>
        /// 排序方向
        /// </summary>
        public bool Ascending
        {
            get
            {
                return this.m_ascending;
            }
            set
            {
                this.m_ascending = value;
            }
        }
        private int m_cacherSize;
        /// <summary>
        /// 缓存页面大小
        /// </summary>
        public int CacherSize
        {
            get
            {
                return this.m_cacherSize;
            }
            set
            {
                this.m_cacherSize = value;
            }
        }
        private string[ ] m_fieldAlias;
        /// <summary>
        ///  字段别名集合
        /// </summary>
        public string[ ] FieldAlias
        {
            get
            {
                return this.m_fieldAlias;
            }
            set
            {
                this.m_fieldAlias = value;
            }
        }
        private string[ ] m_fields;
        /// <summary>
        /// 字段集合
        /// </summary>
        public string[ ] Fields
        {
            get
            {
                return this.m_fields;
            }
            set
            {
                this.m_fields = value;
            }
        }
        private int m_pageIndex;
        /// <summary>
        /// 页面索引
        /// </summary>
        public int PageIndex
        {
            get
            {
                return this.m_pageIndex;
            }
            set
            {
                this.m_pageIndex = value;
            }
        }
        private int m_pageSize;
        /// <summary>
        /// 页面大小
        /// </summary>
        public int PageSize
        {
            get
            {
                return this.m_pageSize;
            }
            set
            {
                this.m_pageSize = value;
            }
        }
        private string m_pkey;
        /// <summary>
        /// 主键
        /// </summary>
        public string PKey
        {
            get
            {
                return this.m_pkey;
            }
            set
            {
                this.m_pkey = value;
            }
        }
        private string m_table;
        /// <summary>
        /// 表名称
        /// </summary>
        public string Table
        {
            get
            {
                return this.m_table;
            }
            set
            {
                this.m_table = value;
            }
        }
        private string m_whereStr;
        /// <summary>
        ///  条件语句,需要加 WHERE
        /// </summary>
        public string WhereStr
        {
            get
            {
                return this.m_whereStr;
            }
            set
            {
                this.m_whereStr = value;
            }
        }
        #endregion

        /// <summary>
        /// 分页参数
        /// </summary>
        public PagerParameters( )
        {
            this.m_ascending = true;
            this.m_pageIndex = 1;
            this.m_pageSize = 100;
            this.m_cacherSize = 0;
            this.m_pkey = "";
            this.m_whereStr = "";
            this.m_table = "";
        }

        public PagerParameters( string table , string pkey , int pageIndex )
        {
            this.m_ascending = true;
            this.m_pageSize = 20;
            this.m_cacherSize = 0;
            this.m_table = table;
            this.m_pkey = pkey;
            this.m_pageIndex = pageIndex;
        }

        public PagerParameters( string table , string pkey , int pageIndex , int pageSize )
            : this( table , pkey , pageIndex )
        {
            this.m_pageSize = pageSize;
        }

        public PagerParameters( string table , string pkey , int pageIndex , string whereStr )
            : this( table , pkey , pageIndex )
        {
            this.m_whereStr = whereStr;
        }

        public PagerParameters( string table , string pkey , string whereStr , int pageIndex , int pageSize )
            : this( table , pkey , pageIndex , whereStr )
        {
            this.m_pageSize = pageSize;
        }

        public PagerParameters( string table , string pkey , string whereStr , int pageIndex , int pageSize , string[ ] fields )
            : this( table , pkey , whereStr , pageIndex , pageSize )
        {
            this.Fields = fields;
        }

        public PagerParameters( string table , string pkey , string whereStr , int pageIndex , int pageSize , string[ ] fields , string[ ] fieldAlias )
            : this( table , pkey , whereStr , pageIndex , pageSize , fields )
        {
            this.FieldAlias = fieldAlias;
        }
    }
}

