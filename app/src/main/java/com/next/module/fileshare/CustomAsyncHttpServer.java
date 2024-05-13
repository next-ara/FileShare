package com.next.module.fileshare;

import android.content.Context;
import android.text.TextUtils;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * ClassName:自定义异步HTTP服务器类
 *
 * @author Afton
 * @time 2024/4/15
 * @auditor
 */
public class CustomAsyncHttpServer extends AsyncHttpServer {

    //内容类型
    public static class ContentType {
        private static final String TEXT_CONTENT_TYPE = "text/html;charset=utf-8";
        private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
        private static final String BINARY_CONTENT_TYPE = "application/octet-stream";
        private static final String JS_CONTENT_TYPE = "application/javascript";
        private static final String PNG_CONTENT_TYPE = "application/x-png";
        private static final String JPG_CONTENT_TYPE = "application/jpeg";
        private static final String SWF_CONTENT_TYPE = "application/x-shockwave-flash";
        private static final String WOFF_CONTENT_TYPE = "application/x-font-woff";
        private static final String TTF_CONTENT_TYPE = "application/x-font-truetype";
        private static final String SVG_CONTENT_TYPE = "image/svg+xml";
        private static final String EOT_CONTENT_TYPE = "image/vnd.ms-fontobject";
        private static final String MP3_CONTENT_TYPE = "audio/mp3";
        private static final String MP4_CONTENT_TYPE = "video/mpeg4";
    }

    /**
     * 获取
     *
     * @param context 上下文
     * @param regex   正则
     * @param dirPath 目录路径
     */
    public void get(Context context, String regex, String dirPath) {
        super.get(regex, (asyncHttpServerRequest, asyncHttpServerResponse) -> {
            this.sendResources(context, dirPath, asyncHttpServerRequest, asyncHttpServerResponse);
        });
    }

    /**
     * 发送资源
     *
     * @param context  上下文
     * @param dirPath  目录路径
     * @param request  请求
     * @param response 响应
     */
    private void sendResources(Context context, String dirPath, final AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
        try {
            String fullPath = request.getPath();
            fullPath = fullPath.replace("%20", " ");
            String resourceName = fullPath;
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1);
            }
            if (resourceName.indexOf("?") > 0) {
                resourceName = resourceName.substring(0, resourceName.indexOf("?"));
            }
            if (!TextUtils.isEmpty(getContentTypeByResourceName(resourceName))) {
                response.setContentType(getContentTypeByResourceName(resourceName));
            }
            BufferedInputStream bInputStream = new BufferedInputStream(context.getAssets().open(dirPath + resourceName));
            response.sendStream(bInputStream, bInputStream.available());
        } catch (IOException e) {
            response.code(404).end();
        }
    }

    /**
     * 根据资源名称获取内容类型
     *
     * @param resourceName 资源名称
     * @return 内容类型
     */
    private String getContentTypeByResourceName(String resourceName) {
        if (resourceName.endsWith(".css")) {
            return ContentType.CSS_CONTENT_TYPE;
        } else if (resourceName.endsWith(".js")) {
            return ContentType.JS_CONTENT_TYPE;
        } else if (resourceName.endsWith(".swf")) {
            return ContentType.SWF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".png")) {
            return ContentType.PNG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".jpg") || resourceName.endsWith(".jpeg")) {
            return ContentType.JPG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".woff")) {
            return ContentType.WOFF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".ttf")) {
            return ContentType.TTF_CONTENT_TYPE;
        } else if (resourceName.endsWith(".svg")) {
            return ContentType.SVG_CONTENT_TYPE;
        } else if (resourceName.endsWith(".eot")) {
            return ContentType.EOT_CONTENT_TYPE;
        } else if (resourceName.endsWith(".mp3")) {
            return ContentType.MP3_CONTENT_TYPE;
        } else if (resourceName.endsWith(".mp4")) {
            return ContentType.MP4_CONTENT_TYPE;
        }

        return "";
    }
}