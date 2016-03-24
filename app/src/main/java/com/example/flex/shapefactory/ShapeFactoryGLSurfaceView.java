package com.example.flex.shapefactory;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by Flex on 3/21/2016.
 */
public class ShapeFactoryGLSurfaceView extends GLSurfaceView {

    private final ShapeFactoryGLRenderer mRenderer;

    public ShapeFactoryGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);

        mRenderer = new ShapeFactoryGLRenderer();

        setRenderer(mRenderer);

        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
