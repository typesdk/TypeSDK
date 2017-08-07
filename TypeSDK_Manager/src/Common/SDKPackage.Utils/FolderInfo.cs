namespace SDKPackage.Utils
{
    using System;

    public class FolderInfo
    {
        private string m_contentType;
        private FsoMethod m_fsoType;
        private string m_fullName;
        private DateTime m_lastWriteTime;
        private long m_length;
        private string m_name;
        private string m_path;
        private byte m_type;

        public FolderInfo()
        {
            this.m_name = "";
            this.m_fullName = "";
            this.m_contentType = "";
            this.m_type = 0;
            this.m_fsoType = FsoMethod.Folder;
            this.m_path = "";
            this.m_lastWriteTime = DateTime.Now;
            this.m_length = 0L;
        }

        public FolderInfo(string name, string fullName, string contentType, byte type, string path, DateTime lastWriteTime, long length)
        {
            this.m_name = name;
            this.m_fullName = fullName;
            this.m_contentType = contentType;
            this.m_type = type;
            this.m_path = path;
            this.m_lastWriteTime = lastWriteTime;
            this.m_length = length;
        }

        public string ContentType
        {
            get
            {
                return this.m_contentType;
            }
            set
            {
                this.m_contentType = value;
            }
        }

        public FsoMethod FsoType
        {
            get
            {
                return this.m_fsoType;
            }
            set
            {
                this.m_fsoType = value;
                this.m_type = (byte) value;
            }
        }

        public string FullName
        {
            get
            {
                return this.m_fullName;
            }
            set
            {
                this.m_fullName = value;
            }
        }

        public DateTime LastWriteTime
        {
            get
            {
                return this.m_lastWriteTime;
            }
            set
            {
                this.m_lastWriteTime = value;
            }
        }

        public long Length
        {
            get
            {
                return this.m_length;
            }
            set
            {
                this.m_length = value;
            }
        }

        public string Name
        {
            get
            {
                return this.m_name;
            }
            set
            {
                this.m_name = value;
            }
        }

        public string Path
        {
            get
            {
                return this.m_path;
            }
            set
            {
                this.m_path = value;
            }
        }

        public byte Type
        {
            get
            {
                return this.m_type;
            }
            set
            {
                this.m_type = value;
                this.m_fsoType = (FsoMethod) value;
            }
        }
    }
}

