namespace SDKPackage.Kernel
{    
    using System;
    using System.Data;
    using System.Runtime.CompilerServices;
    using SDKPackage.Utils;
    /// <summary>
    /// 单页数据集
    /// </summary>
    [Serializable]
    public class PagerSet
    {
        /// <summary>
        /// 单页数据集
        /// </summary>
        public DataSet PageSet { get; set; }
        /// <summary>
        /// 总页数
        /// </summary>
        public int PageCount { get; set; }
        /// <summary>
        /// 要显示的页码(页索引)
        /// </summary>
        public int PageIndex { get; set; }
        /// <summary>
        /// 每页的大小
        /// </summary>
        public int PageSize { get; set; }
        /// <summary>
        /// 总记录数
        /// </summary>
        public int RecordCount { get; set; }

        public PagerSet( )
        {
            this.PageIndex = 1;
            this.PageSize = 10;
            this.PageCount = 0;
            this.RecordCount = 0;
            this.PageSet = new DataSet( "PagerSet" );
        }

        public PagerSet( int pageIndex , int pageSize , int pageCount , int recordCount , DataSet pageSet )
        {
            this.PageIndex = pageIndex;
            this.PageSize = pageSize;
            this.PageCount = pageCount;
            this.RecordCount = recordCount;
            this.PageSet = pageSet;
        }
        /// <summary>
        ///  检测 DataSet 数据集是否为空;是空值，返回 false；不是返回 true
        /// </summary>
        /// <returns></returns>
        public bool CheckedPageSet( )
        {
            return Validate.CheckedDataSet( this.PageSet );
        }       
    }
}

