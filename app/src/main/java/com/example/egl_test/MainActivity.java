package com.example.egl_test;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES30;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import static android.opengl.EGL14.EGL_ALPHA_SIZE;
import static android.opengl.EGL14.EGL_BLUE_SIZE;
import static android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION;
import static android.opengl.EGL14.EGL_DEFAULT_DISPLAY;
import static android.opengl.EGL14.EGL_DEPTH_SIZE;
import static android.opengl.EGL14.EGL_GREEN_SIZE;
import static android.opengl.EGL14.EGL_HEIGHT;
import static android.opengl.EGL14.EGL_NONE;
import static android.opengl.EGL14.EGL_NO_CONTEXT;
import static android.opengl.EGL14.EGL_RED_SIZE;
import static android.opengl.EGL14.EGL_RENDERABLE_TYPE;
import static android.opengl.EGL14.EGL_WIDTH;
import static android.opengl.EGL14.eglMakeCurrent;
import static android.opengl.EGLExt.EGL_OPENGL_ES3_BIT_KHR;
import static android.opengl.GLES20.GL_NO_ERROR;
import static android.opengl.GLES20.GL_RENDERER;
import static android.opengl.GLES20.GL_VENDOR;
import static android.opengl.GLES20.GL_VERSION;
import static android.opengl.GLES20.glGetError;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EGLDisplay display = EGL14.eglGetDisplay(EGL_DEFAULT_DISPLAY);
                //EGLDisplay display = EGL14.eglGetCurrentDisplay();

                int[] version = new int[2];
                boolean initializeResult = EGL14.eglInitialize(display, version, 0, version, 1);
                if (!initializeResult) {
                    int error = EGL14.eglGetError();

                    Log.i("EGL InitError", "eglInit failed");
                    return;
                }

                Log.i("FlutterWegGL", "EGL version is " + version[0] + "." + version[1]);


                int[] attribute_list = new int[]{
                        EGL_RENDERABLE_TYPE,
                        EGL_OPENGL_ES3_BIT_KHR,
                        EGL_RED_SIZE, 8,
                        EGL_GREEN_SIZE, 8,
                        EGL_BLUE_SIZE, 8,
                        EGL_ALPHA_SIZE, 8,
                        EGL_DEPTH_SIZE, 16,
                        EGL_NONE};

                int[] configsCount = new int[1];
                EGLConfig[] configs = new EGLConfig[1];
                EGLConfig config;
                boolean chooseConfigResult = EGL14.eglChooseConfig(display, attribute_list, 0, configs, 0, 1, configsCount, 0);
                if (!chooseConfigResult) {
                    Log.i("EGL InitError", "eglChooseConfig failed");
                    return;
                }

                config = configs[0];

                int[] surfaceAttributes = new int[]{
                        EGL_WIDTH, 16,
                        EGL_HEIGHT, 16,
                        EGL_NONE
                };

                // This is just a dummy surface that it needed to make an OpenGL context current (bind it to this thread)
                EGLSurface dummySurfaceForDartSide = EGL14.eglCreatePbufferSurface(display, config, surfaceAttributes, 0);
                EGLSurface dummySurfaceForPlugin = EGL14.eglCreatePbufferSurface(display, config, surfaceAttributes, 0);

                Log.i("FlutterWegGL", "EGL Error: " + EGL14.eglGetError());

                    int[] attribList = {EGL_CONTEXT_CLIENT_VERSION, 3, EGL14.EGL_NONE};
                EGLContext eglContext = EGL14.eglCreateContext(display, config, EGL_NO_CONTEXT, attribute_list, 0);
                Log.i("FlutterWegGL", "EGL Error: " + EGL14.eglGetError());
                /// we send back the context. This might look a bit strange, but is necessary to allow this function to be called
                /// from Dart Isolates.

                if (!eglMakeCurrent(display, dummySurfaceForPlugin, dummySurfaceForPlugin, eglContext)) {
                    int eglerror = EGL14.eglGetError();

                    Log.i("FlutterWegGL", "Error: MakeCurrent " + eglerror);
                }
                String v = GLES30.glGetString(GL_VENDOR);
                int error = glGetError();
                if (error != GL_NO_ERROR)
                {

                    Log.i("FlutterWegGL", "GLError: " + error);
                }
                String r = GLES30.glGetString(GL_RENDERER);
                String v2 = GLES30.glGetString(GL_VERSION);


                Log.i("FlutterWegGL", "OpenGL initialized: Vendor:" + v + " renderer: " + r + " Version: " + v2);



                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}