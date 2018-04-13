package com.hbtl.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class FileUtils {
    public static void openFile(Context context, File file) {

        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            //获取文件file的MIME类型
            String type = getMIMEType(file);
            //设置intent的data和Type属性.
            intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
            //跳转
            //这里最好try一下,有可能会报错.比如说你的MIME类型是打开邮箱,但是你手机里面没装邮箱客户端,就会报错.
            context.startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型.
     *
     * @param file
     */
    public static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置.
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型.
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问,这个MIME_MapTable是什么?
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    public static final String[][] MIME_MapTable = {
            //{后缀名,MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    /**
     * 删除文件
     *
     * @param context  程序上下文
     * @param fileName 文件名,要在系统内保持唯一
     * @return boolean 存储成功的标志
     */
    public static boolean deleteFile(Context context, String fileName) {
        File file = new File(fileName);
        return file.delete();
        //return context.deleteFile(fileName);
    }

    // 删除指定文件夹下的所有文件...
//    public static void removeDirFiles(String path) {
//        File file = new File(path);
//        if (file.exists()) {
//            if (file.isDirectory()) {
//                File[] files = file.listFiles();
//                for (File subFile : files) {
//                    if (subFile.isDirectory())
//                        removeDirFiles(subFile.getPath());
//                    else
//                        subFile.delete();
//                }
//            }
//            file.delete();
//        }
//    }

    /**
     * 文件是否存在
     *
     * @param context
     * @param fileName
     * @return
     */
    public static boolean exists(Context context, String fileName) {
        return new File(context.getFilesDir(), fileName).exists();
    }

    /**
     * 存储文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名,要在系统内保持唯一
     * @param content  文本内容
     * @return boolean 存储成功的标志
     */
    public static boolean writeFile(Context context, String fileName, String content) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] byteContent = content.getBytes();
            fos.write(byteContent);

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return success;
    }

    /**
     * 存储文本数据
     *
     * @param filePath 文件名,要在系统内保持唯一
     * @param content  文本内容
     * @return boolean 存储成功的标志
     */
    public static boolean writeFile(String filePath, String content) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            byte[] byteContent = content.getBytes();
            fos.write(byteContent);

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return success;
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容,失败返回null
     */
    public static String readFile(Context context, String fileName) {
        if (!exists(context, fileName)) {
            return null;
        }
        FileInputStream fis = null;
        String content = null;
        try {
            fis = context.openFileInput(fileName);
            if (fis != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = fis.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                fis.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容,失败返回null
     */
    public static String readFile(String filePath) {
        if (filePath == null || !new File(filePath).exists()) {
            return null;
        }
        FileInputStream fis = null;
        String content = null;
        try {
            fis = new FileInputStream(filePath);
            if (fis != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = fis.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                fis.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 读取文本数据
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return String, 读取到的文本内容,失败返回null
     */
    public static String readAssets(Context context, String fileName) {
        InputStream is = null;
        String content = null;
        try {
            is = context.getAssets().open(fileName);
            if (is != null) {

                byte[] buffer = new byte[1024];
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                while (true) {
                    int readLength = is.read(buffer);
                    if (readLength == -1) break;
                    arrayOutputStream.write(buffer, 0, readLength);
                }
                is.close();
                arrayOutputStream.close();
                content = new String(arrayOutputStream.toByteArray());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            content = null;
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return content;
    }

    /**
     * 存储单个Parcelable对象
     *
     * @param context      程序上下文
     * @param fileName     文件名,要在系统内保持唯一
     * @param parcelObject 对象必须实现Parcelable
     * @return boolean 存储成功的标志
     */
    public static boolean writeParcelable(Context context, String fileName, Parcelable parcelObject) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            Parcel parcel = Parcel.obtain();
            parcel.writeParcelable(parcelObject, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
            byte[] data = parcel.marshall();
            parcel.recycle();//用完回收
            fos.write(data);

            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return success;
    }

    /**
     * 存储List对象
     *
     * @param context  程序上下文
     * @param fileName 文件名,要在系统内保持唯一
     * @param list     对象数组集合,对象必须实现Parcelable
     * @return boolean 存储成功的标志
     */
    public static boolean writeParcelableList(Context context, String fileName, List<Parcelable> list) {
        boolean success = false;
        FileOutputStream fos = null;
        try {
            if (list instanceof List) {
                fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                Parcel parcel = Parcel.obtain();
                parcel.writeList(list);
                byte[] data = parcel.marshall();
                parcel.recycle();//用完回收
                fos.write(data);

                success = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        return success;
    }

    /**
     * 读取单个数据对象
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return Parcelable, 读取到的Parcelable对象,失败返回null
     */
    @SuppressWarnings("unchecked")
    public static Parcelable readParcelable(Context context, String fileName, ClassLoader classLoader) {
        Parcelable parcelable = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = context.openFileInput(fileName);
            if (fis != null) {
                bos = new ByteArrayOutputStream();
                byte[] b = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }

                byte[] data = bos.toByteArray();

                Parcel parcel = Parcel.obtain();
                parcel.unmarshall(data, 0, data.length);
                parcel.setDataPosition(0);
                parcelable = parcel.readParcelable(classLoader);
                parcel.recycle();//用完回收
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            parcelable = null;
        } finally {
            if (fis != null) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bos != null) try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return parcelable;
    }

    /**
     * 读取数据对象列表
     *
     * @param context  程序上下文
     * @param fileName 文件名
     * @return List, 读取到的对象数组,失败返回null
     */
    @SuppressWarnings("unchecked")
    public static List<Parcelable> readParcelableList(Context context, String fileName, ClassLoader classLoader) {
        List<Parcelable> results = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = context.openFileInput(fileName);
            if (fis != null) {
                bos = new ByteArrayOutputStream();
                byte[] b = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(b)) != -1) {
                    bos.write(b, 0, bytesRead);
                }

                byte[] data = bos.toByteArray();

                Parcel parcel = Parcel.obtain();
                parcel.unmarshall(data, 0, data.length);
                parcel.setDataPosition(0);
                results = parcel.readArrayList(classLoader);
                parcel.recycle();//用完回收
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            results = null;
        } finally {
            if (fis != null) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bos != null) try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    public static boolean saveSerializable(Context context, String fileName, Serializable data) {
        boolean success = false;
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            oos.writeObject(data);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    public static Serializable readSerialLizable(Context context, String fileName) {
        Serializable data = null;
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(context.openFileInput(fileName));
            data = (Serializable) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    /**
     * 从assets里边读取字符串
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 复制文件
     *
     * @param srcFile
     * @param dstFile
     * @return
     */
    public static boolean copy(String srcFile, String dstFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {

            File dst = new File(dstFile);
            if (!dst.getParentFile().exists()) {
                dst.getParentFile().mkdirs();
            }

            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(dstFile);

            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return true;
    }
}
