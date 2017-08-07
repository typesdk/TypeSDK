using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Web;

namespace SDKPackage
{
    /// <summary>
    /// Upload_Img 的摘要说明
    /// </summary>
    public class Upload_Img : IHttpHandler
    {
        private HttpContext context;
        public void ProcessRequest(HttpContext context)
        {
            //String aspxUrl = context.Request.Path.Substring(0, context.Request.Path.LastIndexOf("/") + 1);
            string typeInfo = context.Request.QueryString["imgurl"];

            //文件保存目录路径
            String savePath = "";

            switch (typeInfo)
            {
                case "game":
                    savePath = "/img/gameicon/";
                    break;
                case "platform":
                    savePath = "/img/platformicon/";
                    break;    
            }

            //定义允许上传的文件扩展名
            Hashtable extTable = new Hashtable();
            extTable.Add("image", "gif,jpg,jpeg,png,bmp");

            //最大文件大小
            int maxSize = 1000000;
            this.context = context;

            HttpPostedFile imgFile = context.Request.Files[0];
            if (imgFile == null)
            {
                context.Response.Write("{success:'error',msg:'请选择文件。'}");
                context.Response.End();
                return;
                //showError("请选择文件。");
            }

            String conType = imgFile.ContentType;
            if (conType.ToLower().IndexOf("image") == -1)
            {
                context.Response.Write("{success:'error',msg:'上传文件非法。'}");
                context.Response.End();
                return;
                //showError("上传文件非法。");
            }

            String dirPath = context.Server.MapPath(savePath); //;savePath
            if (!Directory.Exists(dirPath))
            {
                Directory.CreateDirectory(dirPath);
                //context.Response.Write("{success:'error',msg:'上传目录不存在。'}");
                //context.Response.End();
                //return;
                //showError("上传目录不存在。");
            }

            String dirName = context.Request.QueryString["dir"];
            if (String.IsNullOrEmpty(dirName))
            {
                dirName = "image";
            }
            //if (!extTable.ContainsKey(dirName))
            //{
            //    context.Response.Write("{success:'error',msg:'目录名不正确。'}");
            //    context.Response.End();
            //    return;
            //    //showError("目录名不正确。");
            //}

            String fileName = imgFile.FileName;
            String fileExt = Path.GetExtension(fileName).ToLower();

            if (imgFile.InputStream == null || imgFile.InputStream.Length > maxSize)
            {
                context.Response.Write("{success:'error',msg:'上传文件大小超过限制。'}");
                context.Response.End();
                return;
                //showError("上传文件大小超过限制。");
            }

            if (String.IsNullOrEmpty(fileExt) || Array.IndexOf(((String)extTable[dirName]).Split(','), fileExt.Substring(1).ToLower()) == -1)
            {
                context.Response.Write("{success:'error',msg:'上传文件扩展名是不允许的扩展名。\n只允许" + ((String)extTable[dirName]) + "格式。'}");
                context.Response.End();
                return;
                //showError("上传文件扩展名是不允许的扩展名。\n只允许" + ((String)extTable[dirName]) + "格式。");
            }

            ////创建文件夹
            //dirPath += dirName + "/";
            //if (!Directory.Exists(dirPath))
            //{
            //    Directory.CreateDirectory(dirPath);
            //}
            //String ymd = DateTime.Now.ToString("yyyyMMdd", DateTimeFormatInfo.InvariantInfo);
            //dirPath += ymd + "/";
            //if (!Directory.Exists(dirPath))
            //{
            //    Directory.CreateDirectory(dirPath);
            //}

            String newFileName = DateTime.Now.ToString("yyyyMMddHHmmss_ffff", DateTimeFormatInfo.InvariantInfo) + fileExt;
            String filePath = savePath + newFileName;

            imgFile.SaveAs(context.Server.MapPath( filePath));

            //Hashtable hash = new Hashtable();
            //hash["error"] = 0;
            //hash["url"] = filePath;
            context.Response.AddHeader("Content-Type", "text/html; charset=UTF-8");
            //context.Response.Write(JsonMapper.ToJson(hash));
            context.Response.Write("{success:'success',imgsrc:'" + filePath + "'}");
            context.Response.End();
        }

        //private void showError(string message)
        //{
        //    Hashtable hash = new Hashtable();
        //    hash["error"] = 1;
        //    hash["message"] = message;
        //    context.Response.AddHeader("Content-Type", "text/html; charset=UTF-8");
        //    context.Response.Write(JsonMapper.ToJson(hash));
        //    context.Response.End();
        //}


        public bool IsReusable
        {
            get
            {
                return false;
            }
        }
    }
}