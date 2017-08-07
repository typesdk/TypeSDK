using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SDKPackage.Utils
{
    public class ImageHelper
    {
        /// <summary> 
        /// 生成缩略图 
        /// </summary> 
        /// <param name="originalImagePath">源图路径（物理路径）</param> 
        /// <param name="thumbnailPath">缩略图路径（物理路径）</param> 
        /// <param name="width">缩略图宽度</param> 
        /// <param name="height">缩略图高度</param> 
        public static void CreateSDKGameDefault(string originalImagePath, string thumbnailPath, int width, int height)
        {
            //获取原始图片 
            System.Drawing.Image originalImage = System.Drawing.Image.FromFile(originalImagePath);
            //缩略图画布宽高 
            int towidth = width;
            int toheight = height;
            //原始图片写入画布坐标和宽高(用来设置裁减溢出部分) 
            int x = 0;
            int y = 0;
            int ow = originalImage.Width;
            int oh = originalImage.Height;
            //原始图片画布,设置写入缩略图画布坐标和宽高(用来原始图片整体宽高缩放) 
            int bg_x = 0;
            int bg_y = 0;
            int bg_w = towidth;
            int bg_h = toheight;
            //倍数变量 
            double multiple = 0;
            //获取宽长的或是高长与缩略图的倍数 
            if (originalImage.Width >= originalImage.Height) multiple = (double)originalImage.Width / (double)width;
            else multiple = (double)originalImage.Height / (double)height;
            //上传的图片的宽和高小等于缩略图 
            if (ow <= width && oh <= height)
            {
                //缩略图按原始宽高 
                bg_w = originalImage.Width;
                bg_h = originalImage.Height;
                //空白部分用背景色填充 
                bg_x = Convert.ToInt32(((double)towidth - (double)ow) / 2);
                bg_y = Convert.ToInt32(((double)toheight - (double)oh) / 2);
            }
            //上传的图片的宽和高大于缩略图 
            else
            {
                //宽高按比例缩放 
                bg_w = Convert.ToInt32((double)originalImage.Width / multiple);
                bg_h = Convert.ToInt32((double)originalImage.Height / multiple);
                //空白部分用背景色填充 
                bg_y = Convert.ToInt32(((double)height - (double)bg_h) / 2);
                bg_x = Convert.ToInt32(((double)width - (double)bg_w) / 2);
            }
            //新建一个bmp图片,并设置缩略图大小. 
            System.Drawing.Image bitmap = new System.Drawing.Bitmap(towidth, toheight);
            //新建一个画板 
            System.Drawing.Graphics g = System.Drawing.Graphics.FromImage(bitmap);
            //设置高质量插值法 
            g.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.HighQualityBilinear;
            //设置高质量,低速度呈现平滑程度 
            g.SmoothingMode = System.Drawing.Drawing2D.SmoothingMode.HighQuality;
            //清空画布并设置背景色 
            //g.Clear(System.Drawing.ColorTranslator.FromHtml("#F2F2F2"));
            //g.Clear();
            //在指定位置并且按指定大小绘制原图片的指定部分 
            //第一个System.Drawing.Rectangle是原图片的画布坐标和宽高,第二个是原图片写在画布上的坐标和宽高,最后一个参数是指定数值单位为像素 
            g.DrawImage(originalImage, new System.Drawing.Rectangle(bg_x, bg_y, bg_w, bg_h), new System.Drawing.Rectangle(x, y, ow, oh), System.Drawing.GraphicsUnit.Pixel);
            try
            {
                //获取图片类型 
                string fileExtension = System.IO.Path.GetExtension(originalImagePath).ToLower();
                //按原图片类型保存缩略图片,不按原格式图片会出现模糊,锯齿等问题. 
                //switch (fileExtension)
                //{
                //    case ".gif": bitmap.Save(thumbnailPath, System.Drawing.Imaging.ImageFormat.Gif); break;
                //    case ".jpg": bitmap.Save(thumbnailPath, System.Drawing.Imaging.ImageFormat.Jpeg); break;
                //    case ".bmp": bitmap.Save(thumbnailPath, System.Drawing.Imaging.ImageFormat.Bmp); break;
                //    case ".png": bitmap.Save(thumbnailPath, System.Drawing.Imaging.ImageFormat.Png); break;
                //}
                bitmap.Save(thumbnailPath, System.Drawing.Imaging.ImageFormat.Png);
            }
            catch (System.Exception e)
            {
                throw e;
            }
            finally
            {
                originalImage.Dispose();
                bitmap.Dispose();
                g.Dispose();
            }
        }
    }
}
