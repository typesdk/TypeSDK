namespace SDKPackage.Kernel
{
    using SDKPackage.Utils;
    using System;
    using System.Collections.Generic;
    using System.Data;
    using System.Data.SqlClient;
    /// <summary>
    /// 单张数据表访问器
    /// </summary>
    public class TableProvider : BaseDataProvider, ITableProvider
    {
        #region 属性
        private string m_tableName;
        /// <summary>
        /// 目标表名称
        /// </summary>
        public string TableName
        {
            get
            {
                return this.m_tableName;
            }
        }
        #endregion

        public TableProvider(DbHelper database, string tableName) : base(database)
        {
            this.m_tableName = "";
            this.m_tableName = tableName;
        }

        public TableProvider(string connectionString, string tableName) : base(connectionString)
        {
            this.m_tableName = "";
            this.m_tableName = tableName;
        }
        /// <summary>
        /// 批量插入
        /// </summary>
        /// <param name="dataSet"></param>
        /// <param name="columnMapArray"></param>
        public void BatchCommitData(DataSet dataSet, string[][] columnMapArray)
        {
            this.BatchCommitData(dataSet.Tables[0], columnMapArray);
        }
        /// <summary>
        /// 批量插入
        /// </summary>
        /// <param name="table"></param>
        /// <param name="columnMapArray"></param>
        public void BatchCommitData(DataTable table, string[][] columnMapArray)
        {
            using (SqlBulkCopy copy = new SqlBulkCopy(base.Database.ConnectionString))
            {
                copy.DestinationTableName = this.TableName;
                foreach (string[] strArray in columnMapArray)
                {
                    copy.ColumnMappings.Add(strArray[0], strArray[1]);
                }
                copy.WriteToServer(table);
                copy.Close();
            }
        }
        /// <summary>
        /// 批量更新,提交DataTable中的changes到数据库
        /// </summary>
        /// <param name="dt"></param>
        public void CommitData(DataTable dt)
        {
            DataSet dataSet = this.ConstructDataSet(dt);
            base.Database.UpdateDataSet(dataSet, this.TableName);
        }
        /// <summary>
        /// 构造 DataSet
        /// </summary>
        /// <param name="dt"></param>
        /// <returns></returns>
        private DataSet ConstructDataSet(DataTable dt)
        {
            DataSet set = null;
            if (dt.DataSet != null)
            {
                return dt.DataSet;
            }
            set = new DataSet();
            set.Tables.Add(dt);
            return set;
        }
        /// <summary>
        ///  删除目标表中所有满足where条件的记录
        /// </summary>
        /// <param name="where"></param>
        public void Delete(string where)
        {
            string commandText = string.Format("DELETE FROM {0} {1}", this.TableName, where);
            base.Database.ExecuteNonQuery(commandText);
        }
        /// <summary>
        /// 获取满足条件的DataSet
        /// </summary>
        /// <param name="where"></param>
        /// <returns></returns>
        public DataSet Get(string where)
        {
            string commandText = string.Format("SELECT * FROM {0} {1}", this.TableName, where);
            return base.Database.ExecuteDataset(commandText);
        }
        /// <summary>
        ///   获取一个空的DataTable，该DataTable反映了目标表的结构
        /// </summary>
        /// <returns></returns>
        public DataTable GetEmptyTable()
        {
            DataTable emptyTable = base.Database.GetEmptyTable(this.TableName);
            emptyTable.TableName = this.TableName;
            return emptyTable;
        }
        /// <summary>
        /// 返回一个与目标表大纲完全一致的DataRow
        /// </summary>
        /// <returns></returns>
        public DataRow NewRow( )
        {
            DataTable emptyTable = this.GetEmptyTable();
            DataRow row = emptyTable.NewRow();
            for ( int i = 0; i < emptyTable.Columns.Count; i++ )
            {
                row[ i ] = DBNull.Value;
            }
            return row;
        }
        /// <summary>
        /// 获取满足条件的对象
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="where"></param>
        /// <returns></returns>
        public T GetObject<T>(string where)
        {
            DataRow one = this.GetOne(where);
            if (one == null)
            {
                return default(T);
            }
            return DataHelper.ConvertRowToObject<T>(one);
        }
        /// <summary>
        /// 获取满足条件的列表对象,仅一张表
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="where"></param>
        /// <returns></returns>
        public IList<T> GetObjectList<T>(string where)
        {
            DataSet ds = this.Get(where);
            //if (base.CheckedDataSet(ds))
            if ( Validate.CheckedDataSet( ds ) )
            {
                return DataHelper.ConvertDataTableToObjects<T>(ds.Tables[0]);
            }
            return null;
        }
        /// <summary>
        /// 获取满足where条件的第一条记录
        /// </summary>
        /// <param name="where"></param>
        /// <returns></returns>
        public DataRow GetOne(string where)
        {
            DataSet set = this.Get(where);
            if (set.Tables[0].Rows.Count > 0)
            {
                return set.Tables[0].Rows[0];
            }
            return null;
        }
        /// <summary>
        /// 获取目标表中满足where条件的记录总数
        /// </summary>
        /// <param name="where"></param>
        /// <returns></returns>
        public int GetRecordsCount(string where)
        {
            if (where == null)
            {
                where = "";
            }
            string commandText = string.Format("SELECT COUNT(*) FROM {0} {1}", this.TableName, where);
            return int.Parse(base.Database.ExecuteScalarToStr(CommandType.Text, commandText));
        }
        /// <summary>
        /// 将row存放到数据库中
        /// </summary>
        /// <param name="row"></param>
        public void Insert(DataRow row)
        {
            DataTable emptyTable = this.GetEmptyTable();
            try
            {
                DataRow row2 = emptyTable.NewRow();
                for (int i = 0; i < emptyTable.Columns.Count; i++)
                {
                    row2[i] = row[i];
                }
                emptyTable.Rows.Add(row2);
                this.CommitData(emptyTable);
            }
            catch
            {
                throw;
            }
            finally
            {
                emptyTable.Rows.Clear();
                emptyTable.AcceptChanges();
            }
        }
        
        
    }
}

