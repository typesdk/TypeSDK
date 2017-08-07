namespace SDKPackage.Utils
{
    using System;
    using System.Text;

    public class StringBuffer
    {
        private StringBuilder m_InnerBuilder;

        public StringBuffer()
        {
            this.m_InnerBuilder = new StringBuilder();
        }

        public StringBuffer(int capacity)
        {
            this.m_InnerBuilder = new StringBuilder(capacity);
        }

        public StringBuffer(string value)
        {
            this.m_InnerBuilder = new StringBuilder(value);
        }

        public StringBuffer(StringBuilder innerBuilder)
        {
            this.m_InnerBuilder = innerBuilder;
        }

        public StringBuffer(int capacity, int maxCapacity)
        {
            this.m_InnerBuilder = new StringBuilder(capacity, maxCapacity);
        }

        public StringBuffer(string value, int capacity)
        {
            this.m_InnerBuilder = new StringBuilder(value, capacity);
        }

        public StringBuffer(string value, int startIndex, int length, int capacity)
        {
            this.m_InnerBuilder = new StringBuilder(value, startIndex, length, capacity);
        }

        public static StringBuffer operator +(StringBuffer buffer, char value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, char[] value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, bool value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, byte value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, decimal value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, double value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, short value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, int value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, long value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, object value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, sbyte value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, float value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, string value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, ushort value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, uint value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public static StringBuffer operator +(StringBuffer buffer, ulong value)
        {
            buffer.InnerBuilder.Append(value);
            return buffer;
        }

        public void Remove(int startIndex, int length)
        {
            this.m_InnerBuilder.Remove(startIndex, length);
        }

        public void Replace(string oldValue, string newValue)
        {
            this.m_InnerBuilder.Replace(oldValue, newValue);
        }

        public override string ToString()
        {
            return this.InnerBuilder.ToString();
        }

        public StringBuilder InnerBuilder
        {
            get
            {
                return this.m_InnerBuilder;
            }
        }

        public int Length
        {
            get
            {
                return this.m_InnerBuilder.Length;
            }
        }
    }
}

