namespace SDKPackage.Kernel
{
    using System;
    using System.Collections.Generic;
    using System.Data;
    using System.Data.Common;
    /// <summary>
    /// 分页管理器
    /// </summary>
    public class PagerManager
    {
        private DbHelper m_dbHelper;
        private IDictionary<int, PagerSet> m_fixedCacher;
        private PagerParameters m_prams;

        public PagerManager(DbHelper dbHelper)
        {
            this.m_dbHelper = dbHelper;
        }

        public PagerManager(string connectionString)
        {
            this.m_dbHelper = new DbHelper(connectionString);
        }

        public PagerManager(PagerParameters prams, DbHelper dbHelper)
        {
            this.m_prams = prams;
            this.m_dbHelper = dbHelper;
        }

        public PagerManager(PagerParameters prams, string connectionString)
        {
            this.m_prams = prams;
            this.m_dbHelper = new DbHelper(connectionString);
            if (prams.CacherSize > 0)
            {
                this.m_fixedCacher = new Dictionary<int, PagerSet>(prams.CacherSize);
            }
        }
        /// <summary>
        /// 缓存分页数据对象
        /// </summary>
        /// <param name="index"></param>
        /// <param name="pagerSet"></param>
        private void CacheObject(int index, PagerSet pagerSet)
        {
            if (this.m_fixedCacher != null)
            {
                this.m_fixedCacher.Add(index, pagerSet);
            }
            else if (this.m_prams.CacherSize > 0)
            {
                this.m_fixedCacher = new Dictionary<int, PagerSet>(this.m_prams.CacherSize);
                this.m_fixedCacher.Add(index, pagerSet);
            }
        }
        /// <summary>
        /// 获取分页数据对象
        /// </summary>
        /// <param name="index"></param>
        /// <returns></returns>
        private PagerSet GetCachedObject(int index)
        {
            if (this.m_fixedCacher == null)
            {
                return null;
            }
            if (!this.m_fixedCacher.ContainsKey(index))
            {
                return null;
            }
            return this.m_fixedCacher[index];
        }
        /// <summary>
        /// 获取显示字段
        /// </summary>
        /// <param name="fields"></param>
        /// <param name="fieldAlias"></param>
        /// <returns></returns>
        protected string GetFieldString(string[] fields, string[] fieldAlias)
        {
            int num;
            if (fields == null)
            {
                fields = new string[] { "*" };
            }
            string str = "";
            if (fieldAlias == null)
            {
                for (num = 0; num < fields.Length; num++)
                {
                    str = str + " " + fields[num];
                    if (num != (fields.Length - 1))
                    {
                        str = str + " , ";
                    }
                    else
                    {
                        str = str + " ";
                    }
                }
                return str;
            }
            for (num = 0; num < fields.Length; num++)
            {
                str = str + " " + fields[num];
                if (fieldAlias[num] != null)
                {
                    str = str + " as " + fieldAlias[num];
                }
                if (num != (fields.Length - 1))
                {
                    str = str + " , ";
                }
                else
                {
                    str = str + " ";
                }
            }
            return str;
        }
        /// <summary>
        /// 获取分页数据
        /// </summary>
        /// <returns></returns>
        public PagerSet GetPagerSet()
        {
            return this.GetPagerSet(this.m_prams);
        }
        /// <summary>
        /// 获取分页数据
        /// </summary>
        /// <param name="pramsPager"></param>
        /// <returns></returns>
        public PagerSet GetPagerSet(PagerParameters pramsPager)
        {
            DataSet set;
            if (this.m_prams == null)
            {
                this.m_prams = pramsPager;
            }
            if (pramsPager.PageIndex < 0)
            {
                return null;
            }
            List<DbParameter> prams = new List<DbParameter> {
                this.m_dbHelper.MakeInParam("TableName", pramsPager.Table),
                this.m_dbHelper.MakeInParam("ReturnFields", this.GetFieldString(pramsPager.Fields, pramsPager.FieldAlias)),
                this.m_dbHelper.MakeInParam("PageSize", pramsPager.PageSize),
                this.m_dbHelper.MakeInParam("PageIndex", pramsPager.PageIndex),
                this.m_dbHelper.MakeInParam("Where", pramsPager.WhereStr),
                this.m_dbHelper.MakeInParam("Orderfld", pramsPager.PKey),
                this.m_dbHelper.MakeInParam("OrderType", pramsPager.Ascending ? 0 : 1),
                this.m_dbHelper.MakeOutParam("PageCount", typeof(int)),
                this.m_dbHelper.MakeOutParam("RecordCount", typeof(int))
            };
            set = new DataSet();
            //this.m_dbHelper.RunProc("WEB_PageView", prams, out set);
            return new PagerSet(pramsPager.PageIndex, pramsPager.PageSize, Convert.ToInt32(prams[prams.Count - 3].Value), Convert.ToInt32(prams[prams.Count - 2].Value), set) { PageSet = { DataSetName = "PagerSet_" + pramsPager.Table } };
        }
        /// <summary>
        ///  获取分页数据
        /// </summary>
        /// <param name="pramsPager"></param>
        /// <returns></returns>
        public PagerSet GetPagerSet2(PagerParameters pramsPager)
        {
            DataSet set;
            if (this.m_prams == null)
            {
                this.m_prams = pramsPager;
            }
            if (pramsPager.PageIndex < 0)
            {
                return null;
            }
            List<DbParameter> prams = new List<DbParameter> {
                this.m_dbHelper.MakeInParam("TableName", pramsPager.Table),
                this.m_dbHelper.MakeInParam("ReturnFields", this.GetFieldString(pramsPager.Fields, pramsPager.FieldAlias)),
                this.m_dbHelper.MakeInParam("PageSize", pramsPager.PageSize),
                this.m_dbHelper.MakeInParam("PageIndex", pramsPager.PageIndex),
                this.m_dbHelper.MakeInParam("Where", pramsPager.WhereStr),
                this.m_dbHelper.MakeInParam("Orderby", pramsPager.PKey),
                this.m_dbHelper.MakeOutParam("PageCount", typeof(int)),
                this.m_dbHelper.MakeOutParam("RecordCount", typeof(int))
            };
            this.m_dbHelper.RunProc("WEB_PageView2", prams, out set);            
            return new PagerSet(pramsPager.PageIndex, pramsPager.PageSize, Convert.ToInt32(prams[prams.Count - 3].Value), Convert.ToInt32(prams[prams.Count - 2].Value), set) { PageSet = { DataSetName = "PagerSet_" + pramsPager.Table } };
          
        }
    }
}

