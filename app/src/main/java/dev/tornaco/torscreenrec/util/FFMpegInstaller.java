package dev.tornaco.torscreenrec.util;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import org.newstand.logger.Logger;

/**
 * Created by Tornaco on 2017/7/27.
 * Licensed with Apache.
 */

public class FFMpegInstaller {

    public static void installAsync(final Context context) {
        ThreadUtil.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    FFmpeg.getInstance(context).loadBinary(new FFmpegLoadBinaryResponseHandler() {
                        @Override
                        public void onFailure() {
                            Logger.d("FFMpeg loading onFailure");
                        }

                        @Override
                        public void onSuccess() {
                            Logger.d("FFMpeg loading onSuccess");
                        }

                        @Override
                        public void onStart() {
                            Logger.d("FFMpeg loading onStart");
                        }

                        @Override
                        public void onFinish() {
                            Logger.d("FFMpeg loading onFinish");
                        }
                    });
                } catch (FFmpegNotSupportedException e) {
                    Logger.d("Fail load FFMPEG:" + e.getLocalizedMessage());
                }
            }
        }).start();
    }
}
