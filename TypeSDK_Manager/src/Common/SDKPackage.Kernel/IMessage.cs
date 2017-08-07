namespace SDKPackage.Kernel
{
    using System;
    using System.Collections;
    /// <summary>
    /// 消息接口
    /// </summary>
    public interface IMessage
    {
        /// <summary>
        /// 增加携带对象
        /// </summary>
        /// <param name="entityList"></param>
        void AddEntity(ArrayList entityList);
        /// <summary>
        /// 增加携带对象
        /// </summary>
        /// <param name="entity"></param>
        void AddEntity(object entity);
        /// <summary>
        /// 清空携带对象集
        /// </summary>
        void ResetEntityList();
        /// <summary>
        /// 消息内容
        /// </summary>
        string Content { get; set; }
        /// <summary>
        /// 携带对象列表
        /// </summary>
        ArrayList EntityList { get; set; }
        /// <summary>
        /// 消息标识
        /// </summary>
        int MessageID { get; set; }
        /// <summary>
        /// 成功状态
        /// </summary>
        bool Success { get; set; }
    }
}

