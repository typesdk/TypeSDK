using ICSharpCode.SharpZipLib.Checksums;
using ICSharpCode.SharpZipLib.Zip;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SDKPackage.Utils
{
    public class ZipHelper
    {
        /// <summary>
        /// 把文件夹里面的文件为一个压缩包文件
        /// </summary>
        /// <param name="sDirectoryPath">需要打包的目录</param>
        /// <param name="FileName">打包之后保存的文件名称，如D:\packing.zip</param>
        /// <param name="scope">打包的范围</param>
        /// <returns></returns>
        public static bool ToFile(string sDirectoryPath, string FileName, PackingScope scope)
        {
            bool result = false;
            List<FileInfo> filesInfo = new List<FileInfo>();
            Crc32 crc = new Crc32();
            ZipOutputStream s = null;
            int i = 1;
            try
            {
                FileInfo filedd = new FileInfo(FileName);
                if (!Directory.Exists(filedd.Directory.FullName))
                {
                    Directory.CreateDirectory(filedd.Directory.FullName);
                }
                s = new ZipOutputStream(File.OpenWrite(FileName));
                s.SetLevel(9);

                DirectoryInfo mainDir = new DirectoryInfo(sDirectoryPath);
                filesInfo = GetFileList(mainDir.FullName, scope);
                foreach (FileInfo file in filesInfo)
                {
                    using (FileStream fs = File.OpenRead(file.FullName))
                    {
                        byte[] buffer = new byte[fs.Length];
                        fs.Read(buffer, 0, buffer.Length);
                        ZipEntry entry = new ZipEntry(ZipEntry.CleanName(file.FullName.Replace(mainDir.FullName, "")));
                        entry.DateTime = DateTime.Now;
                        entry.Comment = i.ToString();
                        entry.ZipFileIndex = i++;
                        entry.Size = fs.Length;
                        fs.Close();
                        crc.Reset();
                        crc.Update(buffer);
                        entry.Crc = crc.Value;
                        s.PutNextEntry(entry);
                        s.Write(buffer, 0, buffer.Length);
                    }
                }
                s.Finish();
                s.Close();
                result = true;
            }

            catch (Exception ex)
            {
                result = false;
                throw new Exception(ex.Message);
            }
            finally
            {
                s.Close();
            }
            return result;
        }

        private static List<FileInfo> GetFileList(string sDirectoryPath, PackingScope scope)
        {
            List<FileInfo> filesInfo = new List<FileInfo>();
            DirectoryInfo dir = new DirectoryInfo(sDirectoryPath);
            if (scope != PackingScope.Folder)
            {
                FileInfo[] files = dir.GetFiles();
                foreach (FileInfo fTemp in files)
                {
                    filesInfo.Add(fTemp);
                }
            }
            if (scope != PackingScope.File)
            {
                DirectoryInfo[] dirs = dir.GetDirectories();
                foreach (DirectoryInfo dirTemp in dirs)
                {
                    List<FileInfo> templist = GetFileList(dirTemp.FullName, PackingScope.All);
                    foreach (FileInfo fileTemp in templist)
                    {
                        filesInfo.Add(fileTemp);
                    }
                }
            }
            return filesInfo;

        }
        public enum PackingScope
        {
            Folder,
            File,
            All
        }
    }
}
