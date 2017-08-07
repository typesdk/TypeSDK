using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing.Imaging;
using System.Drawing;
using System.Runtime.InteropServices;
using System.IO;

namespace SDKPackage.PJConfig
{
    /// <summary>
    /// Photoshop PSD文件
    /// </summary>
    public class ImagePsd
    {
        private class PSDHEAD
        {
            private byte[] m_HeadBytes = new byte[26];

            public byte[] GetBytes()
            {
                return m_HeadBytes;
            }

            public PSDHEAD(byte[] p_Data)
            {
                m_HeadBytes = p_Data;
            }

            /// <summary>
            /// 版本
            /// </summary>
            public byte Version { get { return m_HeadBytes[5]; } set { m_HeadBytes[5] = value; } }

            /// <summary>
            /// 颜色数  3为24位  4位23位
            /// </summary>
            public ushort Channels
            {
                get
                {
                    return BitConverter.ToUInt16(new byte[] { m_HeadBytes[13], m_HeadBytes[12] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_HeadBytes[13] = _Value[1];
                    m_HeadBytes[12] = _Value[0];
                }
            }

            /// <summary>
            /// 
            /// </summary>
            public uint Height
            {
                get
                {
                    return BitConverter.ToUInt32(new byte[] { m_HeadBytes[17], m_HeadBytes[16], m_HeadBytes[15], m_HeadBytes[14] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_HeadBytes[17] = _Value[0];
                    m_HeadBytes[16] = _Value[1];
                    m_HeadBytes[15] = _Value[2];
                    m_HeadBytes[14] = _Value[3];
                }
            }

            /// <summary>
            /// 
            /// </summary>
            public uint Width
            {
                get
                {
                    return BitConverter.ToUInt32(new byte[] { m_HeadBytes[21], m_HeadBytes[20], m_HeadBytes[19], m_HeadBytes[18] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_HeadBytes[21] = _Value[0];
                    m_HeadBytes[20] = _Value[1];
                    m_HeadBytes[19] = _Value[2];
                    m_HeadBytes[18] = _Value[3];
                }
            }

            public ushort BitsPerPixel
            {
                get
                {
                    return BitConverter.ToUInt16(new byte[] { m_HeadBytes[23], m_HeadBytes[22] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_HeadBytes[23] = _Value[1];
                    m_HeadBytes[22] = _Value[0];
                }

            }

            public ushort ColorMode
            {
                get
                {
                    return BitConverter.ToUInt16(new byte[] { m_HeadBytes[25], m_HeadBytes[24] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_HeadBytes[25] = _Value[1];
                    m_HeadBytes[24] = _Value[0];
                }
            }

            public PSDHEAD()
            {
                m_HeadBytes[0] = 0x38;
                m_HeadBytes[1] = 0x42;
                m_HeadBytes[2] = 0x50;
                m_HeadBytes[3] = 0x53;
                m_HeadBytes[5] = 0x01;

                ColorMode = 3;
                BitsPerPixel = 8;
            }
        }
        private class ColorModel
        {
            private byte[] m_ColorData;

            public ColorPalette ColorData
            {
                get
                {
                    Bitmap _Bitmap = new Bitmap(1, 1, PixelFormat.Format8bppIndexed);
                    ColorPalette _ColorPalette = _Bitmap.Palette;
                    if (m_ColorData.Length == 0) return _ColorPalette;
                    for (int i = 0; i != 256; i++)
                    {
                        _ColorPalette.Entries[i] = Color.FromArgb(m_ColorData[i], m_ColorData[i + 256], m_ColorData[i + 512]);
                    }
                    return _ColorPalette;
                }
                set
                {
                    m_ColorData = new byte[768];
                    for (int i = 0; i != 256; i++)
                    {
                        m_ColorData[i] = value.Entries[i].R;
                        m_ColorData[i + 256] = value.Entries[i].G;
                        m_ColorData[i + 512] = value.Entries[i].B;
                    }
                }
            }

            private byte[] m_BIMSize = new byte[4];

            public uint BIMSize
            {
                get
                {
                    return BitConverter.ToUInt32(new byte[] { m_BIMSize[3], m_BIMSize[2], m_BIMSize[1], m_BIMSize[0] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_BIMSize[0] = _Value[3];
                    m_BIMSize[1] = _Value[2];
                    m_BIMSize[2] = _Value[1];
                    m_BIMSize[3] = _Value[0];
                }
            }

            public ColorModel(FileStream p_FileStream)
            {
                byte[] _CountByte = new byte[4];
                p_FileStream.Read(_CountByte, 0, 4);
                Array.Reverse(_CountByte);
                int _Count = BitConverter.ToInt32(_CountByte, 0);
                m_ColorData = new byte[_Count];
                if (_Count != 0) p_FileStream.Read(m_ColorData, 0, _Count);
                p_FileStream.Read(m_BIMSize, 0, 4);
            }

            public ColorModel()
            {
                m_ColorData = new byte[0];
            }

            public byte[] GetBytes()
            {
                MemoryStream _Memory = new MemoryStream();
                byte[] _Value = BitConverter.GetBytes(m_ColorData.Length);
                Array.Reverse(_Value);
                _Memory.Write(_Value, 0, _Value.Length);
                _Memory.Write(m_ColorData, 0, m_ColorData.Length);
                _Memory.Write(m_BIMSize, 0, 4);
                return _Memory.ToArray();
            }
        }
        private class BIM
        {
            private byte[] m_Data = new byte[] { 0x38, 0x42, 0x49, 0x4D };
            private byte[] m_TypeID = new byte[2];
            private byte[] m_Name = new byte[0];

            public ushort TypeID
            {
                get
                {
                    return BitConverter.ToUInt16(new byte[] { m_TypeID[1], m_TypeID[0] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_TypeID[0] = _Value[1];
                    m_TypeID[1] = _Value[0];
                }
            }

            public byte[] m_Value;

            public BIM(FileStream p_FileStream)
            {
                byte[] _Type = new byte[4];
                p_FileStream.Read(_Type, 0, 4);
                if (m_Data[0] == _Type[0] && m_Data[1] == _Type[1] && m_Data[2] == _Type[2] && m_Data[3] == _Type[3])
                {
                    p_FileStream.Read(m_TypeID, 0, 2);
                    int _SizeOfName = p_FileStream.ReadByte();
                    int _nSizeOfName = (int)_SizeOfName;
                    if (_nSizeOfName > 0)
                    {
                        if ((_nSizeOfName % 2) != 0) { _SizeOfName = p_FileStream.ReadByte(); }
                        m_Name = new byte[_nSizeOfName];
                        p_FileStream.Read(m_Name, 0, _nSizeOfName);
                    }
                    _SizeOfName = p_FileStream.ReadByte();
                    byte[] _CountByte = new byte[4];
                    p_FileStream.Read(_CountByte, 0, 4);
                    Array.Reverse(_CountByte);
                    int _DataCount = BitConverter.ToInt32(_CountByte, 0);
                    if (_DataCount % 2 != 0) _DataCount++;
                    m_Value = new byte[_DataCount];
                    p_FileStream.Read(m_Value, 0, _DataCount);
                    m_Read = true;
                }
            }

            private bool m_Read = false;

            public bool Read
            {
                get { return m_Read; }
                set { m_Read = value; }
            }

            #region Type=1005
            public ushort hRes
            {
                get
                {
                    return BitConverter.ToUInt16(new byte[] { m_Value[1], m_Value[0] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_Value[0] = _Value[1];
                    m_Value[1] = _Value[0];
                }
            }
            public uint hResUnit
            {
                get
                {
                    return BitConverter.ToUInt32(new byte[] { m_Value[5], m_Value[4], m_Value[3], m_Value[2] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_Value[2] = _Value[3];
                    m_Value[3] = _Value[2];
                    m_Value[4] = _Value[1];
                    m_Value[5] = _Value[0];
                }
            }
            public ushort widthUnit
            {
                get
                {
                    return BitConverter.ToUInt16(new byte[] { m_Value[7], m_Value[6] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_Value[6] = _Value[1];
                    m_Value[7] = _Value[0];
                }
            }
            public ushort vRes
            {
                get
                {
                    return BitConverter.ToUInt16(new byte[] { m_Value[9], m_Value[8] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_Value[8] = _Value[1];
                    m_Value[9] = _Value[0];
                }
            }
            public uint vResUnit
            {
                get
                {
                    return BitConverter.ToUInt32(new byte[] { m_Value[13], m_Value[12], m_Value[11], m_Value[10] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_Value[10] = _Value[3];
                    m_Value[11] = _Value[2];
                    m_Value[12] = _Value[1];
                    m_Value[13] = _Value[0];
                }
            }
            public ushort heightUnit
            {
                get
                {
                    return BitConverter.ToUInt16(new byte[] { m_Value[15], m_Value[14] }, 0);
                }
                set
                {
                    byte[] _Value = BitConverter.GetBytes(value);
                    m_Value[14] = _Value[1];
                    m_Value[15] = _Value[0];
                }
            }
            #endregion

        }
        private class LayerMaskInfo
        {

            public byte[] m_Data = new byte[0];

            public LayerMaskInfo(FileStream p_Stream)
            {
                byte[] _Count = new byte[4];
                p_Stream.Read(_Count, 0, 4);
                Array.Reverse(_Count);

                int _ReadCount = BitConverter.ToInt32(_Count, 0);

                m_Data = new byte[_ReadCount];
                if (_ReadCount != 0) p_Stream.Read(m_Data, 0, _ReadCount);
            }

            public LayerMaskInfo()
            {
            }

            public byte[] GetBytes()
            {
                MemoryStream _Memory = new MemoryStream();
                byte[] _Value = BitConverter.GetBytes(m_Data.Length);
                Array.Reverse(_Value);
                _Memory.Write(_Value, 0, _Value.Length);
                if (m_Data.Length != 0) _Memory.Write(m_Data, 0, m_Data.Length);
                return _Memory.ToArray();
            }
        }
        private class ImageData
        {
            private ushort p_Type = 0;

            private PSDHEAD m_HeaderInfo;

            public ImageData()
            {
            }
            public ImageData(FileStream p_FileStream, PSDHEAD p_HeaderInfo)
            {
                m_HeaderInfo = p_HeaderInfo;
                byte[] _ShortBytes = new byte[2];
                p_FileStream.Read(_ShortBytes, 0, 2);
                Array.Reverse(_ShortBytes);
                p_Type = BitConverter.ToUInt16(_ShortBytes, 0);
                switch (p_Type)
                {
                    case 0: //RAW DATA
                        RawData(p_FileStream);
                        break;
                    case 1:
                        RleData(p_FileStream);
                        break;
                    default:
                        throw new Exception("Type =" + p_Type.ToString());
                }
            }

            #region RLE数据
            private void RleData(FileStream p_Stream)
            {
                switch (m_HeaderInfo.ColorMode)
                {
                    case 3:  //RGB 
                        LoadRLERGB(p_Stream);
                        break;
                    case 4:  //CMYK
                        LoadRLECMYK(p_Stream);
                        break;
                    default:
                        throw new Exception("RLE ColorMode =" + m_HeaderInfo.ColorMode.ToString());
                }
            }

            private void LoadRLERGB(FileStream p_Stream)
            {
                int _Width = (int)m_HeaderInfo.Width;
                int _Height = (int)m_HeaderInfo.Height;
                
                m_PSDImage = new Bitmap(_Width, _Height, PixelFormat.Format24bppRgb);
                
                BitmapData _PSDImageData = m_PSDImage.LockBits(new Rectangle(0, 0, m_PSDImage.Width, m_PSDImage.Height), ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);
                byte[] _ImageBytes = new byte[_PSDImageData.Stride * _PSDImageData.Height];
                int _WriteIndex = 0;
                int _EndIndex = _PSDImageData.Stride * _PSDImageData.Height;
                p_Stream.Position += _Height * m_HeaderInfo.Channels * 2;

                int _Count = _Width * _Height;
                int _WrtieType = 0;
                int _HeightIndex = 0;
                int _WidthIndex = 0;
                int _Index = 0;

                while (true)
                {
                    if (_WriteIndex > _EndIndex - 1) break;
                    byte _Read = (byte)p_Stream.ReadByte();
                    if (_Read == 128) continue; //Erroe
                    if (_Read > 128)
                    {
                        _Read ^= 0x0FF;
                        _Read += 2;
                        byte _ByteValue = (byte)p_Stream.ReadByte();

                        for (byte i = 0; i != _Read; i++)
                        {
                            _WrtieType = _WriteIndex / _Count;
                            switch (_WrtieType)
                            {
                                case 0: //Red
                                    _HeightIndex = _WriteIndex / _Width;
                                    _WidthIndex = _WriteIndex % _Width;
                                    _Index = (_PSDImageData.Stride * _HeightIndex) + (_WidthIndex * 3) + 2;
                                    _ImageBytes[_Index] = _ByteValue;
                                    break;
                                case 1: //Green
                                    _HeightIndex = (_WriteIndex - _Count) / _Width;
                                    _WidthIndex = (_WriteIndex - _Count) % _Width;
                                    _Index = (_PSDImageData.Stride * _HeightIndex) + (_WidthIndex * 3) + 1;
                                    _ImageBytes[_Index] = _ByteValue;
                                    break;
                                case 2:
                                    _HeightIndex = (_WriteIndex - _Count - _Count) / _Width;
                                    _WidthIndex = (_WriteIndex - _Count - _Count) % _Width;
                                    _Index = (_PSDImageData.Stride * _HeightIndex) + (_WidthIndex * 3);
                                    _ImageBytes[_Index] = _ByteValue;
                                    break;
                            }
                            //_ImageBytes[_WriteIndex] = _ByteValue;
                            _WriteIndex++;
                        }
                    }
                    else
                    {
                        _Read++;
                        for (byte i = 0; i != _Read; i++)
                        {
                            _WrtieType = _WriteIndex / _Count;
                            switch (_WrtieType)
                            {
                                case 0: //Red
                                    _HeightIndex = _WriteIndex / _Width;
                                    _WidthIndex = _WriteIndex % _Width;
                                    _Index = (_PSDImageData.Stride * _HeightIndex) + (_WidthIndex * 3) + 2;
                                    _ImageBytes[_Index] = (byte)p_Stream.ReadByte();
                                    break;
                                case 1: //Green
                                    _HeightIndex = (_WriteIndex - _Count) / _Width;
                                    _WidthIndex = (_WriteIndex - _Count) % _Width;
                                    _Index = (_PSDImageData.Stride * _HeightIndex) + (_WidthIndex * 3) + 1;
                                    _ImageBytes[_Index] = (byte)p_Stream.ReadByte();
                                    break;
                                case 2:
                                    _HeightIndex = (_WriteIndex - _Count - _Count) / _Width;
                                    _WidthIndex = (_WriteIndex - _Count - _Count) % _Width;
                                    _Index = (_PSDImageData.Stride * _HeightIndex) + (_WidthIndex * 3);
                                    _ImageBytes[_Index] = (byte)p_Stream.ReadByte();
                                    break;
                            }
                            //_ImageBytes[_WriteIndex] = (byte)p_Stream.ReadByte();
                            _WriteIndex++;
                        }
                    }
                }
                Marshal.Copy(_ImageBytes, 0, _PSDImageData.Scan0, _ImageBytes.Length);
                m_PSDImage.UnlockBits(_PSDImageData);
            }

            private void LoadRLECMYK(FileStream p_Stream)
            {

                int _Width = (int)m_HeaderInfo.Width;
                int _Height = (int)m_HeaderInfo.Height;

                int _Count = _Width * _Height * (m_HeaderInfo.BitsPerPixel / 8) * m_HeaderInfo.Channels;
                p_Stream.Position += _Height * m_HeaderInfo.Channels * 2;
                byte[] _ImageBytes = new byte[_Count];

                int _WriteIndex = 0;
                while (true)
                {
                    if (_WriteIndex > _Count - 1) break;
                    byte _Read = (byte)p_Stream.ReadByte();
                    if (_Read == 128) continue; //Erroe
                    if (_Read > 128)
                    {
                        _Read ^= 0x0FF;
                        _Read += 2;
                        byte _ByteValue = (byte)p_Stream.ReadByte();

                        for (byte i = 0; i != _Read; i++)
                        {
                            _ImageBytes[_WriteIndex] = _ByteValue;
                            _WriteIndex++;
                        }
                    }
                    else
                    {
                        _Read++;
                        for (byte i = 0; i != _Read; i++)
                        {
                            _ImageBytes[_WriteIndex] = (byte)p_Stream.ReadByte();
                            _WriteIndex++;
                        }
                    }
                }

                m_PSDImage = new Bitmap(_Width, _Height, PixelFormat.Format24bppRgb);

                BitmapData _PSDImageData = m_PSDImage.LockBits(new Rectangle(0, 0, m_PSDImage.Width, m_PSDImage.Height), ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);
                byte[] _WriteBytes = new byte[_PSDImageData.Stride * _PSDImageData.Height];

                int _StarIndex = 0;
                int _Index = 0;
                int _Size = _Width * _Height;
                double C;
                double M;
                double Y;
                double K;
                double _MaxColours = Math.Pow(2, m_HeaderInfo.BitsPerPixel);
                int _Size2 = _Size * 2;
                int _Size3 = _Size * 3;
                for (int i = 0; i != _PSDImageData.Height; i++)
                {
                    _StarIndex = _PSDImageData.Stride * i;
                    _Index = i * _Width;
                    for (int z = 0; z != _PSDImageData.Width; z++)
                    {
                        C = 1.0 - (double)_ImageBytes[_Index + z] / _MaxColours;
                        M = 1.0 - (double)_ImageBytes[_Index + z + _Size] / _MaxColours;
                        Y = 1.0 - (double)_ImageBytes[_Index + z + _Size2] / _MaxColours;
                        K = 1.0 - (double)_ImageBytes[_Index + z + _Size3] / _MaxColours;
                        ConvertCMYKToRGB(C, M, Y, K, _WriteBytes, _StarIndex + z * 3);
                    }
                }

                Marshal.Copy(_WriteBytes, 0, _PSDImageData.Scan0, _WriteBytes.Length);
                m_PSDImage.UnlockBits(_PSDImageData);
            }
            #endregion

            #region RAW数据
            private void RawData(FileStream p_Stream)
            {
                switch (m_HeaderInfo.ColorMode)
                {
                    case 2: //Index
                        LoadRAWIndex(p_Stream);
                        return;
                    case 3:  //RGB    
                        LoadRAWRGB(p_Stream);
                        return;
                    case 4: //CMYK
                        LoadRAWCMYK(p_Stream);
                        return;
                    default:
                        throw new Exception("RAW ColorMode =" + m_HeaderInfo.ColorMode.ToString());
                }

            }

            private void LoadRAWCMYK(FileStream p_Stream)
            {
                int _Width = (int)m_HeaderInfo.Width;
                int _Height = (int)m_HeaderInfo.Height;
                m_PSDImage = new Bitmap(_Width, _Height, PixelFormat.Format24bppRgb);

                BitmapData _PSDImageData = m_PSDImage.LockBits(new Rectangle(0, 0, m_PSDImage.Width, m_PSDImage.Height), ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);
                byte[] _WriteBytes = new byte[_PSDImageData.Stride * _PSDImageData.Height];
                int _PerPixel = m_HeaderInfo.BitsPerPixel / 8;
                int _PixelsCount = _Width * _Height;
                int _BytesCount = _PixelsCount * 4 * _PerPixel;
                byte[] _ImageBytes = new byte[_BytesCount];
                p_Stream.Read(_ImageBytes, 0, _BytesCount);

                int _StarIndex = 0;
                int _Index = 0;
                int _Size = _Width * _Height;
                double C;
                double M;
                double Y;
                double K;
                double _MaxColours = Math.Pow(2, m_HeaderInfo.BitsPerPixel);
                int _Size2 = _Size * 2;
                int _Size3 = _Size * 3;

                if (_PerPixel == 2)
                {
                    _Size *= 2;
                    _Size2 *= 2;
                    _Size3 *= 2;
                }
                for (int i = 0; i != _PSDImageData.Height; i++)
                {
                    _StarIndex = _PSDImageData.Stride * i;

                    _Index = i * _Width;
                    if (_PerPixel == 2) _Index *= 2;
                    for (int z = 0; z != _PSDImageData.Width; z++)
                    {
                        switch (_PerPixel)
                        {
                            case 1:
                                C = 1.0 - (double)_ImageBytes[_Index + z] / _MaxColours;
                                M = 1.0 - (double)_ImageBytes[_Index + z + _Size] / _MaxColours;
                                Y = 1.0 - (double)_ImageBytes[_Index + z + _Size2] / _MaxColours;
                                K = 1.0 - (double)_ImageBytes[_Index + z + _Size3] / _MaxColours;
                                ConvertCMYKToRGB(C, M, Y, K, _WriteBytes, _StarIndex + z * 3);
                                break;
                            case 2:
                                C = 1.0 - (double)BitConverter.ToUInt16(_ImageBytes, _Index + z * 2) / _MaxColours;
                                M = 1.0 - (double)BitConverter.ToUInt16(_ImageBytes, _Index + z * 2 + _Size) / _MaxColours;
                                Y = 1.0 - (double)BitConverter.ToUInt16(_ImageBytes, _Index + z * 2 + _Size2) / _MaxColours;
                                K = 1.0 - (double)BitConverter.ToUInt16(_ImageBytes, _Index + z * 2 + _Size3) / _MaxColours;
                                ConvertCMYKToRGB(C, M, Y, K, _WriteBytes, _StarIndex + z * 3);
                                break;
                        }


                    }
                }
                Marshal.Copy(_WriteBytes, 0, _PSDImageData.Scan0, _WriteBytes.Length);
                m_PSDImage.UnlockBits(_PSDImageData);
            }

            /// <summary>
            /// 直接获取RGB 256色图
            /// </summary>
            /// <param name="p_Stream"></param>
            private void LoadRAWIndex(FileStream p_Stream)
            {
                int _Width = (int)m_HeaderInfo.Width;
                int _Height = (int)m_HeaderInfo.Height;
                m_PSDImage = new Bitmap(_Width, _Height, PixelFormat.Format8bppIndexed);

                BitmapData _PSDImageData = m_PSDImage.LockBits(new Rectangle(0, 0, m_PSDImage.Width, m_PSDImage.Height), ImageLockMode.ReadWrite, PixelFormat.Format8bppIndexed);
                byte[] _ImageBytes = new byte[_PSDImageData.Stride * _PSDImageData.Height];

                int _PixelsCount = _Width * _Height;
                byte[] _Data = new byte[_PixelsCount];
                p_Stream.Read(_Data, 0, _PixelsCount);

                int _ReadIndex = 0;
                int _WriteIndex = 0;
                for (int i = 0; i != _Height; i++)
                {
                    _WriteIndex = i * _PSDImageData.Stride;
                    for (int z = 0; z != _Width; z++)
                    {
                        _ImageBytes[z + _WriteIndex] = _Data[_ReadIndex];
                        _ReadIndex++;
                    }
                }

                Marshal.Copy(_ImageBytes, 0, _PSDImageData.Scan0, _ImageBytes.Length);
                m_PSDImage.UnlockBits(_PSDImageData);
            }

            /// <summary>
            /// 获取图形24B   Photo里对应为 
            /// </summary>
            /// <param name="p_Stream"></param>
            private void LoadRAWRGB(FileStream p_Stream)
            {
                int _Width = (int)m_HeaderInfo.Width;
                int _Height = (int)m_HeaderInfo.Height;
                m_PSDImage = new Bitmap(_Width, _Height, PixelFormat.Format24bppRgb);

                BitmapData _PSDImageData = m_PSDImage.LockBits(new Rectangle(0, 0, m_PSDImage.Width, m_PSDImage.Height), ImageLockMode.ReadWrite, PixelFormat.Format24bppRgb);
                byte[] _ImageBytes = new byte[_PSDImageData.Stride * _PSDImageData.Height];

                int _PixelsCount = _Width * _Height;
                int _BytesCount = _PixelsCount * 3 * (m_HeaderInfo.BitsPerPixel / 8);
                byte[] _Data = new byte[_BytesCount];
                p_Stream.Read(_Data, 0, _BytesCount);

                int _Red = 0;
                int _Green = _PixelsCount;
                int _Blue = _PixelsCount + _PixelsCount;
                int _ReadIndex = 0;
                int _WriteIndex = 0;

                if (m_HeaderInfo.BitsPerPixel == 16)
                {
                    _Green *= m_HeaderInfo.BitsPerPixel / 8;
                    _Blue *= m_HeaderInfo.BitsPerPixel / 8;
                }

                for (int i = 0; i != _Height; i++)
                {
                    _WriteIndex = i * _PSDImageData.Stride;
                    for (int z = 0; z != _Width; z++)
                    {
                        _ImageBytes[(z * 3) + 2 + _WriteIndex] = _Data[_ReadIndex + _Red];
                        _ImageBytes[(z * 3) + 1 + _WriteIndex] = _Data[_ReadIndex + _Green];
                        _ImageBytes[(z * 3) + _WriteIndex] = _Data[_ReadIndex + _Blue];
                        _ReadIndex += m_HeaderInfo.BitsPerPixel / 8;
                    }
                }
                Marshal.Copy(_ImageBytes, 0, _PSDImageData.Scan0, _ImageBytes.Length);
                m_PSDImage.UnlockBits(_PSDImageData);
            }
            #endregion

            private Bitmap m_PSDImage;

            public Bitmap PSDImage
            {
                get 
                {
                    m_PSDImage.MakeTransparent();
                    return m_PSDImage; 
                }
                set { m_PSDImage = value; }
            }


            private void ConvertCMYKToRGB(double p_C, double p_M, double p_Y, double p_K, byte[] p_DataBytes, int p_Index)
            {
                int _Red = (int)((1.0 - (p_C * (1 - p_K) + p_K)) * 255);
                int _Green = (int)((1.0 - (p_M * (1 - p_K) + p_K)) * 255);
                int _Blue = (int)((1.0 - (p_Y * (1 - p_K) + p_K)) * 255);

                if (_Red < 0) _Red = 0;
                else if (_Red > 255) _Red = 255;
                if (_Green < 0) _Green = 0;
                else if (_Green > 255) _Green = 255;
                if (_Blue < 0) _Blue = 0;
                else if (_Blue > 255) _Blue = 255;

                p_DataBytes[p_Index] = (byte)_Blue;
                p_DataBytes[p_Index + 1] = (byte)_Green;
                p_DataBytes[p_Index + 2] = (byte)_Red;
            }
        }

        private PSDHEAD m_Head;
        private ColorModel m_ColorModel;
        private IList<BIM> m_8BIMList = new List<BIM>();
        private LayerMaskInfo m_LayerMaskInfo;
        private ImageData m_ImageData;

        public ImagePsd(string p_FileFullPath)
        {
            if (!File.Exists(p_FileFullPath)) return;
            FileStream _PSD = File.Open(p_FileFullPath, FileMode.Open);
            byte[] _HeadByte = new byte[26];
            _PSD.Read(_HeadByte, 0, 26);
            m_Head = new PSDHEAD(_HeadByte);
            m_ColorModel = new ColorModel(_PSD);

            long _ReadCount = _PSD.Position;
            while (true)
            {
                BIM _Bim = new BIM(_PSD);
                if (!_Bim.Read || _PSD.Position - _ReadCount >= m_ColorModel.BIMSize) break;
                m_8BIMList.Add(_Bim);
            }
            m_LayerMaskInfo = new LayerMaskInfo(_PSD);
            m_ImageData = new ImageData(_PSD, m_Head);
            if (m_Head.ColorMode == 2) m_ImageData.PSDImage.Palette = m_ColorModel.ColorData;
            _PSD.Close();
        }

        public ImagePsd()
        {
            NewPsd();
        }

        public void NewPsd()
        {
            m_Head = new PSDHEAD(new byte[] { 0x38, 0x42, 0x50, 0x53, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00, 0x01, 0x2C, 0x00, 0x00, 0x01, 0xB4, 0x00, 0x08, 0x00, 0x03 });
            m_ColorModel = new ColorModel();
            m_ImageData = new ImageData();
            m_LayerMaskInfo = new LayerMaskInfo();
        }

        /// <summary>
        /// 保存成PSD 注意这里保存成PSD文件的BIM信息是没用的.如果你用该类打开PSD文件.如果保存到原文件上会丢失Photoshop的设置
        /// </summary>
        /// <param name="p_FileFullName">文件路径</param>
        public void Save(string p_FileFullName)
        {
            if (PSDImage != null)
            {
                Image _SetImage = PSDImage;
                NewPsd();
                PSDImage = (Bitmap)_SetImage;      //保存需要重新在载 
                int _Width = PSDImage.Width;
                int _Height = PSDImage.Height;

                m_Head.Height = (uint)_Height;
                m_Head.Width = (uint)_Width;

                byte[] _Bim = new byte[] { 0x38, 0x42, 0x49, 0x4D, 0x03, 0xED, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00, 0x96, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x96, 0x00, 0x00, 0x00, 0x01, 0x00, 0x05, 0x38, 0x42, 0x49, 0x4D, 0x03, 0xF3, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x38, 0x42, 0x49, 0x4D, 0x27, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0A, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02 };
                m_ColorModel.BIMSize = (uint)_Bim.Length;

                FileStream _SaveFile = new FileStream(p_FileFullName, FileMode.Create, FileAccess.Write);
                byte[] _Bytes = m_Head.GetBytes();
                _SaveFile.Write(_Bytes, 0, _Bytes.Length);
                _Bytes = m_ColorModel.GetBytes();
                _SaveFile.Write(_Bytes, 0, _Bytes.Length);
                _SaveFile.Write(_Bim, 0, _Bim.Length);
                _SaveFile.Write(new byte[2], 0, 2);
                _Bytes = m_LayerMaskInfo.GetBytes();
                _SaveFile.Write(_Bytes, 0, _Bytes.Length);
                Bitmap _Bitmap = new Bitmap(_Width, _Height, PixelFormat.Format24bppRgb);
                Graphics _Graphics = Graphics.FromImage(_Bitmap);
                _Graphics.DrawImage(PSDImage, 0, 0, _Width, _Height);
                _Graphics.Dispose();

                BitmapData _SaveData = _Bitmap.LockBits(new Rectangle(0, 0, _Width, _Height), ImageLockMode.ReadOnly, PixelFormat.Format24bppRgb);
                byte[] _ReadByte = new byte[_SaveData.Stride * _Height];
                Marshal.Copy(_SaveData.Scan0, _ReadByte, 0, _ReadByte.Length);
                byte[] _WriteByte = new byte[_Bitmap.Width * _Bitmap.Height * 3];
                int _Size = _Width * _Height;
                int _Size2 = _Size * 2;
                for (int i = 0; i != _Height; i++)
                {
                    int _Index = i * _SaveData.Stride;
                    int _WriteIndex = i * _Width;
                    for (int z = 0; z != _Width; z++)
                    {
                        _WriteByte[_WriteIndex + z] = _ReadByte[_Index + (z * 3) + 2];
                        _WriteByte[_WriteIndex + _Size + z] = _ReadByte[_Index + (z * 3) + 1];
                        _WriteByte[_WriteIndex + _Size2 + z] = _ReadByte[_Index + (z * 3) + 0];
                    }
                }
                _Bitmap.UnlockBits(_SaveData);
                _Bitmap.Dispose();

                _SaveFile.Write(_WriteByte, 0, _WriteByte.Length);
                _SaveFile.Close();
            }
        }

        /// <summary>
        /// PSD图形
        /// </summary>
        public Bitmap PSDImage
        {
            get { return m_ImageData.PSDImage; }
            set { m_ImageData.PSDImage = value; }
        }
    }
}