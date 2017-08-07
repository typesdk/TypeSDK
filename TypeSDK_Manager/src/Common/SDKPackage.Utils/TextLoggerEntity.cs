namespace SDKPackage.Utils
{
    using System;
    using System.Runtime.CompilerServices;

    public class TextLoggerEntity : IComparable
    {
        public TextLoggerEntity(DateTime logDateTime, string logContent, string logIp, string logErrorUrl)
        {
            this.LogDateTime = logDateTime;
            this.LogContent = logContent;
            this.LogIp = logIp;
            this.LogErrorUrl = logErrorUrl;
        }

        public int CompareTo(object obj)
        {
            TextLoggerEntity entity = obj as TextLoggerEntity;
            if (entity.LogDateTime > this.LogDateTime)
            {
                return 1;
            }
            if (entity.LogDateTime < this.LogDateTime)
            {
                return -1;
            }
            return 0;
        }

        public string LogContent { get; set; }

        public DateTime LogDateTime { get; set; }

        public string LogErrorUrl { get; set; }

        public string LogIp { get; set; }
    }
}

