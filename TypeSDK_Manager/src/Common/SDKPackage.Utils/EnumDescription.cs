namespace SDKPackage.Utils
{
    using System;
    using System.Collections.Generic;
    using System.Reflection;

    [AttributeUsage(AttributeTargets.Field | AttributeTargets.Enum)]
    public class EnumDescription : Attribute
    {
        private static IDictionary<string, IList<EnumDescription>> EnumDescriptionCache = new Dictionary<string, IList<EnumDescription>>();

        private FieldInfo m_fieldIno;

        private string m_description;
        public string Description
        {
            get
            {
                return this.m_description;
            }
        }
        private int m_enumRank;
        public int EnumRank
        {
            get
            {
                return this.m_enumRank;
            }
        }

        public int EnumValue
        {
            get
            {
                return (int)this.m_fieldIno.GetValue(null);
            }
        }

        public string FieldName
        {
            get
            {
                return this.m_fieldIno.Name;
            }
        }

        public enum SortType
        {
            Default,
            DisplayText,
            Rank
        }

        public EnumDescription(string description)
            : this(description, 5)
        {
        }

        public EnumDescription(string description, int enumRank)
        {
            this.m_description = description;
            this.m_enumRank = enumRank;
        }
        /// <summary>
        /// 是否存在枚举值
        /// </summary>
        /// <param name="enumType"></param>
        /// <param name="enumValue"></param>
        /// <returns></returns>
        public static bool ExistsEnumValue(Type enumType, int enumValue)
        {
            List<EnumDescription> fieldTexts = GetFieldTexts(enumType) as List<EnumDescription>;
            if (CollectionHelper.IsNullOrEmpty<EnumDescription>(fieldTexts))
            {
                return false;
            }
            return fieldTexts.Exists(item => item.EnumValue == enumValue);
        }
        /// <summary>
        /// 获取枚举的描述文本
        /// </summary>
        /// <param name="enumType">枚举类型</param>
        /// <returns></returns>
        public static string GetEnumText(Type enumType)
        {
            EnumDescription[] customAttributes = (EnumDescription[])enumType.GetCustomAttributes(typeof(EnumDescription), false);
            if (customAttributes.Length < 1)
            {
                return string.Empty;
            }
            return customAttributes[0].Description;
        }

        /// <summary>
        /// 获得指定枚举类型中，指定值的描述文本
        /// </summary>
        /// <param name="enumValue">枚举值，不要作任何类型转换</param>
        /// <returns>描述字符串</returns>
        public static string GetFieldText(object enumValue)
        {
            List<EnumDescription> fieldTexts = GetFieldTexts(enumValue.GetType()) as List<EnumDescription>;
            if (CollectionHelper.IsNullOrEmpty<EnumDescription>(fieldTexts))
            {
                return string.Empty;
            }
            EnumDescription description = fieldTexts.Find(item => item.m_fieldIno.Name.Equals(enumValue.ToString()));
            if (description == null)
            {
                return string.Empty;
            }
            return description.Description;
        }
        /// <summary>
        /// 获取枚举类型定义的所有文本，按定义的顺序返回
        /// </summary>
        /// <param name="enumType">枚举类型</param>
        /// <returns>所有定义的文本</returns>
        public static IList<EnumDescription> GetFieldTexts(Type enumType)
        {
            return GetFieldTexts(enumType, SortType.Default);
        }
        /// <summary>
        /// 获取枚举类型定义的所有文本
        /// </summary>
        /// <param name="enumType">枚举类型</param>
        /// <param name="sortType">排序类型</param>
        /// <returns>枚举描述集合</returns>
        public static IList<EnumDescription> GetFieldTexts(Type enumType, SortType sortType)
        {
            if (!EnumDescriptionCache.ContainsKey(enumType.FullName))
            {
                FieldInfo[] fields = enumType.GetFields();
                IList<EnumDescription> list = new List<EnumDescription>();
                foreach (FieldInfo info in fields)
                {
                    object[] customAttributes = info.GetCustomAttributes(typeof(EnumDescription), false);
                    if (customAttributes.Length == 1)
                    {
                        EnumDescription item = (EnumDescription)customAttributes[0];
                        item.m_fieldIno = info;
                        list.Add(item);
                    }
                }
                EnumDescriptionCache.Add(enumType.FullName, list);
            }
            IList<EnumDescription> list2 = EnumDescriptionCache[enumType.FullName];
            if (list2.Count <= 0)
            {
                throw new NotSupportedException("枚举类型[" + enumType.Name + "]未定义属性EnumValueDescription");
            }
            if (sortType != SortType.Default)
            {
                for (int i = 0; i < list2.Count; i++)
                {
                    for (int j = i; j < list2.Count; j++)
                    {
                        bool flag = false;
                        switch (sortType)
                        {
                            case SortType.DisplayText:
                                if (string.Compare(list2[i].Description, list2[j].Description) > 0)
                                {
                                    flag = true;
                                }
                                break;

                            case SortType.Rank:
                                if (list2[i].EnumRank > list2[j].EnumRank)
                                {
                                    flag = true;
                                }
                                break;
                        }
                        if (flag)
                        {
                            EnumDescription description2 = list2[i];
                            list2[i] = list2[j];
                            list2[j] = description2;
                        }
                    }
                }
            }
            return list2;
        }


    }
}

