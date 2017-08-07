using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Text;
using System.Collections;

using System.Drawing;
using System.Drawing.Imaging;
using System.Drawing.Drawing2D;

namespace SDKPackage.PJConfig
{
    public class IconCreate
    {
        public struct favoriteImage
        {
            private string _imagePath;
            private int _x;
            private int _y;

            public int x
            {
                get
                {
                    return _x;
                }
                set
                {
                    _x = value;
                }
            }

            public int y
            {
                get
                {
                    return _y;
                }
                set
                {
                    _y = value;
                }
            }

            public string imagePath
            {
                get
                {
                    return _imagePath;
                }
                set
                {
                    _imagePath = value;
                }
            }
        }

        public static string generateCreateMark(string markPath, int picWidth, int picHeight)
        {
            Image imgMark = Image.FromFile(markPath);
            int markWidth = imgMark.Width;
            int markHeight = imgMark.Height;

            Bitmap bmPhoto = new Bitmap(picWidth, picHeight, PixelFormat.Format32bppArgb);
            bmPhoto.SetResolution(picWidth, picHeight);
            Graphics grPhoto = Graphics.FromImage(bmPhoto);
            grPhoto.SmoothingMode = SmoothingMode.HighQuality;
            grPhoto.CompositingQuality = CompositingQuality.HighQuality;
            grPhoto.InterpolationMode = InterpolationMode.High;
            grPhoto.DrawImage(
                imgMark,
                new Rectangle(picWidth - markWidth, picHeight - markHeight, picWidth, picWidth),
                0,
                0,
                picWidth,
                picWidth,
                GraphicsUnit.Pixel,
                null);
            bmPhoto.Save(markPath + ".png", ImageFormat.Png);
            return markPath + ".png";

        }

        public static string generateWinterMark(string savePath, string body_path, favoriteImage[] favorite)
        {
            //create a image object containing the photograph to watermark
            Image imgPhoto = Image.FromFile(body_path);
            int phWidth = imgPhoto.Width;
            int phHeight = imgPhoto.Height;

            //create a Bitmap the Size of the original photograph
            //Bitmap bmPhoto = new Bitmap(phWidth, phHeight, PixelFormat.Format32bppArgb);
            Bitmap bmPhoto = new Bitmap(phWidth, phHeight, PixelFormat.Format32bppArgb);
            //设置此 Bitmap 的分辨率。 
            bmPhoto.SetResolution(imgPhoto.HorizontalResolution, imgPhoto.VerticalResolution);

            //load the Bitmap into a Graphics object 
            Graphics grPhoto = Graphics.FromImage(bmPhoto);
            //Set the rendering quality for this Graphics object
            grPhoto.SmoothingMode = SmoothingMode.HighQuality;
            grPhoto.CompositingQuality = CompositingQuality.HighQuality;
            grPhoto.InterpolationMode = InterpolationMode.High;
            //haix
            for (int i = 0; i < favorite.Length; i++)
            {
                //Draws the photo Image object at original size to the graphics object.
                grPhoto.DrawImage(
                    imgPhoto,                               // Photo Image object
                    new Rectangle(0, 0, phWidth, phHeight), // Rectangle structure
                    0,                                      // x-coordinate of the portion of the source image to draw. 
                    0,                                      // y-coordinate of the portion of the source image to draw. 
                    phWidth,                                // Width of the portion of the source image to draw. 
                    phHeight,                               // Height of the portion of the source image to draw. 
                    GraphicsUnit.Pixel);                    // Units of measure


                //------------------------------------------------------------
                //Step #2 - Insert Property image,For example:hair,skirt,shoes etc.
                //------------------------------------------------------------
                //create a image object containing the watermark
                Image imgWatermark = new Bitmap(favorite[i].imagePath);
                int wmWidth = imgWatermark.Width;
                int wmHeight = imgWatermark.Height;

                //Create a Bitmap based on the previously modified photograph Bitmap
                Bitmap bmWatermark = new Bitmap(bmPhoto);
                //bmWatermark.MakeTransparent(); //使默认的透明颜色对此 Bitmap 透明。

                //bmWatermark.SetResolution(imgPhoto.HorizontalResolution, imgPhoto.VerticalResolution);
                //Load this Bitmap into a new Graphic Object
                Graphics grWatermark = Graphics.FromImage(bmWatermark);
                grWatermark.SmoothingMode = SmoothingMode.HighQuality;
                grWatermark.CompositingQuality = CompositingQuality.HighQuality;
                grWatermark.InterpolationMode = InterpolationMode.High;

                int xPosOfWm = favorite[i].x;
                int yPosOfWm = favorite[i].y;

                //叠加
                grWatermark.DrawImage(imgWatermark, new Rectangle(xPosOfWm, yPosOfWm, phWidth, phHeight),  //Set the detination Position
                0,                  // x-coordinate of the portion of the source image to draw. 
                0,                  // y-coordinate of the portion of the source image to draw. 
                wmWidth,            // Watermark Width
                wmHeight,            // Watermark Height
                GraphicsUnit.Pixel, // Unit of measurment
                null);   //ImageAttributes Object


                //Replace the original photgraphs bitmap with the new Bitmap
                imgPhoto = bmWatermark;

                grWatermark.Dispose();
                imgWatermark.Dispose();
                //grPhoto.Dispose();                
                //bmWatermark.Dispose();
            }
            //haix

            string nowTime = DateTime.Now.Year.ToString() + DateTime.Now.Month.ToString() + DateTime.Now.Day.ToString();
            nowTime += DateTime.Now.Hour.ToString() + DateTime.Now.Minute.ToString() + DateTime.Now.Second.ToString();

            string saveImagePath = savePath + "\\app_icon.png";

            //save new image to file system.
            imgPhoto.Save(saveImagePath, ImageFormat.Png);
            imgPhoto.Dispose();


            return saveImagePath;
        }


        public static string generateWinterMark_IOS(string savePath, string body_path, favoriteImage[] favorite, string fileName)
        {
            //create a image object containing the photograph to watermark
            Image imgPhoto = Image.FromFile(body_path);
            int phWidth = imgPhoto.Width;
            int phHeight = imgPhoto.Height;

            //create a Bitmap the Size of the original photograph
            //Bitmap bmPhoto = new Bitmap(phWidth, phHeight, PixelFormat.Format32bppArgb);
            Bitmap bmPhoto = new Bitmap(phWidth, phHeight, PixelFormat.Format32bppArgb);
            //设置此 Bitmap 的分辨率。 
            bmPhoto.SetResolution(imgPhoto.HorizontalResolution, imgPhoto.VerticalResolution);

            //load the Bitmap into a Graphics object 
            Graphics grPhoto = Graphics.FromImage(bmPhoto);
            //Set the rendering quality for this Graphics object
            grPhoto.SmoothingMode = SmoothingMode.HighQuality;
            grPhoto.CompositingQuality = CompositingQuality.HighQuality;
            grPhoto.InterpolationMode = InterpolationMode.High;
            //haix
            for (int i = 0; i < favorite.Length; i++)
            {
                //Draws the photo Image object at original size to the graphics object.
                grPhoto.DrawImage(
                    imgPhoto,                               // Photo Image object
                    new Rectangle(0, 0, phWidth, phHeight), // Rectangle structure
                    0,                                      // x-coordinate of the portion of the source image to draw. 
                    0,                                      // y-coordinate of the portion of the source image to draw. 
                    phWidth,                                // Width of the portion of the source image to draw. 
                    phHeight,                               // Height of the portion of the source image to draw. 
                    GraphicsUnit.Pixel);                    // Units of measure


                //------------------------------------------------------------
                //Step #2 - Insert Property image,For example:hair,skirt,shoes etc.
                //------------------------------------------------------------
                //create a image object containing the watermark
                Image imgWatermark = new Bitmap(favorite[i].imagePath);
                int wmWidth = imgWatermark.Width;
                int wmHeight = imgWatermark.Height;

                //Create a Bitmap based on the previously modified photograph Bitmap
                Bitmap bmWatermark = new Bitmap(bmPhoto);
                //bmWatermark.MakeTransparent(); //使默认的透明颜色对此 Bitmap 透明。

                //bmWatermark.SetResolution(imgPhoto.HorizontalResolution, imgPhoto.VerticalResolution);
                //Load this Bitmap into a new Graphic Object
                Graphics grWatermark = Graphics.FromImage(bmWatermark);
                grWatermark.SmoothingMode = SmoothingMode.HighQuality;
                grWatermark.CompositingQuality = CompositingQuality.HighQuality;
                grWatermark.InterpolationMode = InterpolationMode.High;

                int xPosOfWm = favorite[i].x;
                int yPosOfWm = favorite[i].y;

                //叠加
                grWatermark.DrawImage(imgWatermark, new Rectangle(xPosOfWm, yPosOfWm, phWidth, phHeight),  //Set the detination Position
                0,                  // x-coordinate of the portion of the source image to draw. 
                0,                  // y-coordinate of the portion of the source image to draw. 
                wmWidth,            // Watermark Width
                wmHeight,            // Watermark Height
                GraphicsUnit.Pixel, // Unit of measurment
                null);   //ImageAttributes Object


                //Replace the original photgraphs bitmap with the new Bitmap
                imgPhoto = bmWatermark;

                grWatermark.Dispose();
                imgWatermark.Dispose();
                //grPhoto.Dispose();                
                //bmWatermark.Dispose();
            }
            //haix

            string nowTime = DateTime.Now.Year.ToString() + DateTime.Now.Month.ToString() + DateTime.Now.Day.ToString();
            nowTime += DateTime.Now.Hour.ToString() + DateTime.Now.Minute.ToString() + DateTime.Now.Second.ToString();

            string saveImagePath = savePath + fileName + ".png"; ;

            //save new image to file system.
            imgPhoto.Save(saveImagePath, ImageFormat.Png);
            imgPhoto.Dispose();


            return saveImagePath;
        }

    }
}