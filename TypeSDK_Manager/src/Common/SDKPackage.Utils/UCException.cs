namespace SDKPackage.Utils
{
    using System;

    public class UCException : Exception
    {
        public UCException()
        {
        }

        public UCException(string msg) : base(msg)
        {
        }
    }
}

