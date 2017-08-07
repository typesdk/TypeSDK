namespace SDKPackage.Utils
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Runtime.InteropServices;
    /// <summary>
    /// 集合辅助
    /// </summary>
    public static class CollectionHelper
    {
        /// <summary>
        /// 对集合中的每个元素执行action
        /// </summary>
        /// <typeparam name="TObject"></typeparam>
        /// <param name="collection"></param>
        /// <param name="action"></param>
        public static void ActionOnEach<TObject>(IEnumerable<TObject> collection, Action<TObject> action)
        {
            ActionOnSpecification<TObject>(collection, action, null);
        }
        /// <summary>
        /// 对集合中满足predicate条件的元素执行action。如果没有条件，predicate传入null。
        /// </summary>
        /// <typeparam name="TObject"></typeparam>
        /// <param name="collection"></param>
        /// <param name="action"></param>
        /// <param name="predicate"></param>
        public static void ActionOnSpecification<TObject>(IEnumerable<TObject> collection, Action<TObject> action, Predicate<TObject> predicate)
        {
            if (collection != null)
            {
                if (predicate == null)
                {
                    foreach (TObject local in collection)
                    {
                        action(local);
                    }
                }
                else
                {
                    foreach (TObject local in collection)
                    {
                        if (predicate(local))
                        {
                            action(local);
                        }
                    }
                }
            }
        }
        /// <summary>
        /// 从已排序的列表中，采用二分查找找到目标在列表中的位置。
        ///    如果刚好有个元素与目标相等，则返回true，且minIndex会被赋予该元素的位置；
        ///    否则，返回false，且minIndex会被赋予比目标小且最接近目标的元素的位置
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="sortedList"></param>
        /// <param name="target"></param>
        /// <param name="minIndex"></param>
        /// <returns></returns>
        public static bool BinarySearch<T>(IList<T> sortedList, T target, out int minIndex) where T: IComparable
        {
            if (target.CompareTo(sortedList[0]) == 0)
            {
                minIndex = 0;
                return true;
            }
            if (target.CompareTo(sortedList[0]) < 0)
            {
                minIndex = -1;
                return false;
            }
            if (target.CompareTo(sortedList[sortedList.Count - 1]) == 0)
            {
                minIndex = sortedList.Count - 1;
                return true;
            }
            if (target.CompareTo(sortedList[sortedList.Count - 1]) > 0)
            {
                minIndex = sortedList.Count - 1;
                return false;
            }
            int num = 0;
            int num2 = sortedList.Count - 1;
            while ((num2 - num) > 1)
            {
                int num3 = (num + num2) / 2;
                if (target.CompareTo(sortedList[num3]) == 0)
                {
                    minIndex = num3;
                    return true;
                }
                if (target.CompareTo(sortedList[num3]) < 0)
                {
                    num2 = num3;
                }
                else
                {
                    num = num3;
                }
            }
            minIndex = num;
            return false;
        }
        /// <summary>
        /// 集合中是否包含满足predicate条件的元素。
        /// </summary>
        /// <typeparam name="TObject"></typeparam>
        /// <param name="source"></param>
        /// <param name="predicate"></param>
        /// <returns></returns>
        public static bool Contains<TObject>(IEnumerable<TObject> source, Predicate<TObject> predicate)
        {
            TObject local;
            return Contains<TObject>(source, predicate, out local);
        }
        /// <summary>
        /// 集合中是否包含满足predicate条件的元素。
        /// </summary>
        /// <typeparam name="TObject"></typeparam>
        /// <param name="source"></param>
        /// <param name="predicate"></param>
        /// <param name="specification"></param>
        /// <returns></returns>
        public static bool Contains<TObject>(IEnumerable<TObject> source, Predicate<TObject> predicate, out TObject specification)
        {
            specification = default(TObject);
            foreach (TObject local in source)
            {
                if (predicate(local))
                {
                    specification = local;
                    return true;
                }
            }
            return false;
        }
        /// <summary>
        /// 从集合中选取符合条件的元素
        /// </summary>
        /// <typeparam name="TObject"></typeparam>
        /// <param name="source"></param>
        /// <param name="predicate"></param>
        /// <returns></returns>
        public static IList<TObject> Find<TObject>(IEnumerable<TObject> source, Predicate<TObject> predicate)
        {
            IList<TObject> list = new List<TObject>();
            ActionOnSpecification<TObject>(source, delegate (TObject ele) {
                list.Add(ele);
            }, predicate);
            return list;
        }
        /// <summary>
        /// 返回符合条件的第一个元素
        /// </summary>
        /// <typeparam name="TObject"></typeparam>
        /// <param name="source"></param>
        /// <param name="predicate"></param>
        /// <returns></returns>
        public static TObject FindFirst<TObject>(IEnumerable<TObject> source, Predicate<TObject> predicate)
        {
            foreach (TObject local in source)
            {
                if (predicate(local))
                {
                    return local;
                }
            }
            return default(TObject);
        }
        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="ary"></param>
        /// <param name="startIndex"></param>
        /// <param name="count"></param>
        /// <returns></returns>
        public static T[] GetPart<T>(T[] ary, int startIndex, int count)
        {
            return GetPart<T>(ary, startIndex, count, false);
        }
        /// <summary>
        /// 
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="ary"></param>
        /// <param name="startIndex"></param>
        /// <param name="count"></param>
        /// <param name="reverse"></param>
        /// <returns></returns>
        public static T[] GetPart<T>(T[] ary, int startIndex, int count, bool reverse)
        {
            int num;
            if (startIndex >= ary.Length)
            {
                return null;
            }
            if (ary.Length < (startIndex + count))
            {
                count = ary.Length - startIndex;
            }
            T[] localArray = new T[count];
            if (!reverse)
            {
                for (num = 0; num < count; num++)
                {
                    localArray[num] = ary[startIndex + num];
                }
                return localArray;
            }
            for (num = 0; num < count; num++)
            {
                localArray[num] = ary[((ary.Length - startIndex) - 1) - num];
            }
            return localArray;
        }
        /// <summary>
        /// 检查集合是否是 null 或 empty
        ///    true 为 null 或 empty 否则 为  false
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="collection"></param>
        /// <returns></returns>
        public static bool IsNullOrEmpty<T>(ICollection<T> collection)
        {
            if (collection != null)
            {
                return (collection.Count == 0);
            }
            return true;
        }
        /// <summary>
        /// 检查集合是否是 null 或 empty
        ///    true 为 null 或 empty 否则 为  false
        /// </summary>
        /// <param name="collection"></param>
        /// <returns></returns>
        public static bool IsNullOrEmpty(ICollection collection)
        {
            if (collection != null)
            {
                return (collection.Count == 0);
            }
            return true;
        }
    }
}

