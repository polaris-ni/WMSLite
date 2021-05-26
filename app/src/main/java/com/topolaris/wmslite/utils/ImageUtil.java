package com.topolaris.wmslite.utils;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author toPolaris
 * description TODO
 * @date 2021/5/25 20:24
 */
public class ImageUtil {
    private static final String TAG = "ImageUtil";
    private final AssetManager mAssetManager;

    public ImageUtil(AssetManager mAssetManager) {
        this.mAssetManager = mAssetManager;
    }

    /**
     * 获取asset目录下的图片资源
     *
     * @return Bitmap 将图片解析成Bitmap
     */
    private Bitmap getAssetResource(String filename) {
        try(InputStream inputStream = mAssetManager.open(filename)) {
            //将其打开转换成数据流
            //利用BitmapFactory将数据流进行转换成bitmap
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将bitmap转换成Base64字符串
     *
     * @param bitmap 图片
     * @return String 返回的Base64字符串
     */
    private String imageToBase64(Bitmap bitmap) {
        //以防解析错误之后bitmap为null
        if (bitmap == null) {
            return "Error";
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //此步骤为将bitmap进行压缩，我选择了原格式png，第二个参数为压缩质量，我选择了原画质，也就是100，第三个参数传入output stream去写入压缩后的数据
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将获取到的OutputStream转换成byte数组，调用Base64工具类，格式选择Base64.DEFAULT即可
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    /**
     * 将base64转成bitmap，该过程速度很快
     *
     * @param text Base64字符串
     * @return Bitmap
     */
    private Bitmap base64ToImage(String text) {
        //同样的，用base64.decode解析编码，格式跟上面一致
        byte[] bytes = Base64.decode(text, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
