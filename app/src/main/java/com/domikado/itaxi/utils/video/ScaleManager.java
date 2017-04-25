package com.domikado.itaxi.utils.video;

import android.graphics.Matrix;

import static com.domikado.itaxi.utils.video.PivotPoint.CENTER;
import static com.domikado.itaxi.utils.video.PivotPoint.CENTER_BOTTOM;
import static com.domikado.itaxi.utils.video.PivotPoint.CENTER_TOP;
import static com.domikado.itaxi.utils.video.PivotPoint.LEFT_BOTTOM;
import static com.domikado.itaxi.utils.video.PivotPoint.LEFT_CENTER;
import static com.domikado.itaxi.utils.video.PivotPoint.LEFT_TOP;
import static com.domikado.itaxi.utils.video.PivotPoint.RIGHT_BOTTOM;
import static com.domikado.itaxi.utils.video.PivotPoint.RIGHT_CENTER;
import static com.domikado.itaxi.utils.video.PivotPoint.RIGHT_TOP;

public class ScaleManager {

    private Size mViewSize;
    private Size mVideoSize;

    public ScaleManager(Size viewSize, Size videoSize) {
        mViewSize = viewSize;
        mVideoSize = videoSize;
    }

    public Matrix getScaleMatrix(int scalableType) {
        switch (scalableType) {
            case ScalableType.NONE:
                return getNoScale();

            case ScalableType.FIT_XY:
                return fitXY();
            case ScalableType.FIT_CENTER:
                return fitCenter();
            case ScalableType.FIT_START:
                return fitStart();
            case ScalableType.FIT_END:
                return fitEnd();

            case ScalableType.LEFT_TOP:
                return getOriginalScale(LEFT_TOP);
            case ScalableType.LEFT_CENTER:
                return getOriginalScale(LEFT_CENTER);
            case ScalableType.LEFT_BOTTOM:
                return getOriginalScale(LEFT_BOTTOM);
            case ScalableType.CENTER_TOP:
                return getOriginalScale(CENTER_TOP);
            case ScalableType.CENTER:
                return getOriginalScale(CENTER);
            case ScalableType.CENTER_BOTTOM:
                return getOriginalScale(CENTER_BOTTOM);
            case ScalableType.RIGHT_TOP:
                return getOriginalScale(RIGHT_TOP);
            case ScalableType.RIGHT_CENTER:
                return getOriginalScale(RIGHT_CENTER);
            case ScalableType.RIGHT_BOTTOM:
                return getOriginalScale(RIGHT_BOTTOM);

            case ScalableType.LEFT_TOP_CROP:
                return getCropScale(LEFT_TOP);
            case ScalableType.LEFT_CENTER_CROP:
                return getCropScale(LEFT_CENTER);
            case ScalableType.LEFT_BOTTOM_CROP:
                return getCropScale(LEFT_BOTTOM);
            case ScalableType.CENTER_TOP_CROP:
                return getCropScale(CENTER_TOP);
            case ScalableType.CENTER_CROP:
                return getCropScale(CENTER);
            case ScalableType.CENTER_BOTTOM_CROP:
                return getCropScale(CENTER_BOTTOM);
            case ScalableType.RIGHT_TOP_CROP:
                return getCropScale(RIGHT_TOP);
            case ScalableType.RIGHT_CENTER_CROP:
                return getCropScale(RIGHT_CENTER);
            case ScalableType.RIGHT_BOTTOM_CROP:
                return getCropScale(RIGHT_BOTTOM);

            case ScalableType.START_INSIDE:
                return startInside();
            case ScalableType.CENTER_INSIDE:
                return centerInside();
            case ScalableType.END_INSIDE:
                return endInside();

            default:
                return null;
        }
    }

    private Matrix getMatrix(float sx, float sy, float px, float py) {
        Matrix matrix = new Matrix();
        matrix.setScale(sx, sy, px, py);
        return matrix;
    }

    private Matrix getMatrix(float sx, float sy, int pivotPoint) {
        switch (pivotPoint) {
            case LEFT_TOP:
                return getMatrix(sx, sy, 0, 0);
            case LEFT_CENTER:
                return getMatrix(sx, sy, 0, mViewSize.getHeight() / 2f);
            case LEFT_BOTTOM:
                return getMatrix(sx, sy, 0, mViewSize.getHeight());
            case CENTER_TOP:
                return getMatrix(sx, sy, mViewSize.getWidth() / 2f, 0);
            case CENTER:
                return getMatrix(sx, sy, mViewSize.getWidth() / 2f, mViewSize.getHeight() / 2f);
            case CENTER_BOTTOM:
                return getMatrix(sx, sy, mViewSize.getWidth() / 2f, mViewSize.getHeight());
            case RIGHT_TOP:
                return getMatrix(sx, sy, mViewSize.getWidth(), 0);
            case RIGHT_CENTER:
                return getMatrix(sx, sy, mViewSize.getWidth(), mViewSize.getHeight() / 2f);
            case RIGHT_BOTTOM:
                return getMatrix(sx, sy, mViewSize.getWidth(), mViewSize.getHeight());
            default:
                throw new IllegalArgumentException("Illegal PivotPoint");
        }
    }

    private Matrix getNoScale() {
        float sx = mVideoSize.getWidth() / (float) mViewSize.getWidth();
        float sy = mVideoSize.getHeight() / (float) mViewSize.getHeight();
        return getMatrix(sx, sy, LEFT_TOP);
    }

    private Matrix getFitScale(int pivotPoint) {
        float sx = (float) mViewSize.getWidth() / mVideoSize.getWidth();
        float sy = (float) mViewSize.getHeight() / mVideoSize.getHeight();
        float minScale = Math.min(sx, sy);
        sx = minScale / sx;
        sy = minScale / sy;
        return getMatrix(sx, sy, pivotPoint);
    }

    private Matrix fitXY() {
        return getMatrix(1, 1, LEFT_TOP);
    }

    private Matrix fitStart() {
        return getFitScale(LEFT_TOP);
    }

    private Matrix fitCenter() {
        return getFitScale(CENTER);
    }

    private Matrix fitEnd() {
        return getFitScale(RIGHT_BOTTOM);
    }

    private Matrix getOriginalScale(int pivotPoint) {
        float sx = mVideoSize.getWidth() / (float) mViewSize.getWidth();
        float sy = mVideoSize.getHeight() / (float) mViewSize.getHeight();
        return getMatrix(sx, sy, pivotPoint);
    }

    private Matrix getCropScale(int pivotPoint) {
        float sx = (float) mViewSize.getWidth() / mVideoSize.getWidth();
        float sy = (float) mViewSize.getHeight() / mVideoSize.getHeight();
        float maxScale = Math.max(sx, sy);
        sx = maxScale / sx;
        sy = maxScale / sy;
        return getMatrix(sx, sy, pivotPoint);
    }

    private Matrix startInside() {
        if (mVideoSize.getHeight() <= mViewSize.getWidth()
            && mVideoSize.getHeight() <= mViewSize.getHeight()) {
            // video is smaller than view size
            return getOriginalScale(LEFT_TOP);
        } else {
            // either of width or height of the video is larger than view size
            return fitStart();
        }
    }

    private Matrix centerInside() {
        if (mVideoSize.getHeight() <= mViewSize.getWidth()
            && mVideoSize.getHeight() <= mViewSize.getHeight()) {
            // video is smaller than view size
            return getOriginalScale(CENTER);
        } else {
            // either of width or height of the video is larger than view size
            return fitCenter();
        }
    }

    private Matrix endInside() {
        if (mVideoSize.getHeight() <= mViewSize.getWidth()
            && mVideoSize.getHeight() <= mViewSize.getHeight()) {
            // video is smaller than view size
            return getOriginalScale(RIGHT_BOTTOM);
        } else {
            // either of width or height of the video is larger than view size
            return fitEnd();
        }
    }
}