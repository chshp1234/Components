package com.common.base.glide;

import static com.bumptech.glide.request.RequestOptions.decodeTypeOf;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author csp
 * @date 2017/10/20
 */
@GlideExtension
public class MyAppGlideExtension {

    private static       RequestOptions DECODE_TYPE_GIF         = decodeTypeOf(GifDrawable.class).lock();
    private static final int            DEFAULT_MINI_THUMB_SIZE = 100;

    private MyAppGlideExtension() {}

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> miniThumb(@NonNull BaseRequestOptions<?> options) {
        return miniThumb(options, DEFAULT_MINI_THUMB_SIZE);
    }

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> miniThumb(
            @NonNull BaseRequestOptions<?> options, int size) {
        return options.fitCenter().override(size);
    }

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> roundCorner(@NonNull BaseRequestOptions<?> options) {
        return options.transform(new RoundedCorners(dp2px(5)));
    }

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> roundCorner(
            @NonNull BaseRequestOptions<?> options, float size) {
        return options.transform(new RoundedCorners(dp2px(size)));
    }

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> round(@NonNull BaseRequestOptions<?> options) {
        return options.apply(RequestOptions.bitmapTransform(new CircleCrop()));
    }

    @GlideType(GifDrawable.class)
    @NonNull
    public static RequestBuilder<GifDrawable> asMyGif(
            @NonNull RequestBuilder<GifDrawable> requestBuilder) {
        return requestBuilder.transition(new DrawableTransitionOptions()).apply(DECODE_TYPE_GIF);
    }


    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Value of px to value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    public static int px2dp(final float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
