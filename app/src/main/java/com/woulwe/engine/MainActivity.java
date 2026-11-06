package com.woulwe.engine;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public final class MainActivity extends Activity {
    static { System.loadLibrary("woulwe_android"); }
    private static native void nativeCreate();
    private static native void nativeDestroy();

    private GLSurfaceView surface;
    private SceneRenderer renderer;
    private EditorOverlay overlay;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        nativeCreate();
        FrameLayout root = new FrameLayout(this);
        surface = new GLSurfaceView(this);
        surface.setEGLContextClientVersion(2);
        renderer = new SceneRenderer();
        surface.setRenderer(renderer);
        surface.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        root.addView(surface, new FrameLayout.LayoutParams(-1, -1));
        overlay = new EditorOverlay();
        root.addView(overlay, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
    }

    @Override protected void onPause() {
        if (surface != null) surface.onPause();
        super.onPause();
    }
    @Override protected void onResume() {
        super.onResume();
        if (surface != null) surface.onResume();
    }
    @Override protected void onDestroy() {
        if (surface != null) surface.onPause();
        nativeDestroy();
        super.onDestroy();
    }

    private final class EditorOverlay extends View {
        private final Paint p = new Paint(3);
        private float downX, downY, lastX, lastY;
        private boolean dragging;
        EditorOverlay() { super(MainActivity.this); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }

        private void text(Canvas c, String s, float x, float y, float size, int color, boolean bold) {
            p.setColor(color); p.setTextSize(size); p.setFakeBoldText(bold); p.setStyle(Paint.Style.FILL);
            c.drawText(s, x, y, p);
        }
        private void button(Canvas c, RectF r, String label, boolean primary) {
            p.setStyle(Paint.Style.FILL);
            p.setColor(primary ? Color.rgb(93,112,255) : Color.rgb(37,45,63));
            c.drawRoundRect(r, 13, 13, p);
            p.setColor(primary ? Color.WHITE : Color.rgb(207,216,235));
            p.setTextSize(13 * getResources().getDisplayMetrics().density);
            p.setFakeBoldText(true);
            c.drawText(label, r.left + 14 * getResources().getDisplayMetrics().density,
                r.centerY() + 5 * getResources().getDisplayMetrics().density, p);
        }
        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            float d = getResources().getDisplayMetrics().density;
            float w = getWidth(), h = getHeight();
            p.setColor(Color.argb(235, 17,22,34)); p.setStyle(Paint.Style.FILL);
            c.drawRect(0,0,w,58*d,p);
            text(c,"WOULWE",20*d,25*d,17*d,Color.WHITE,true);
            text(c,"ENGINE",20*d,43*d,9*d,Color.rgb(142,158,190),true);
            text(c,"SCENE EDITOR",116*d,35*d,12*d,Color.rgb(195,205,226),true);
            text(c,"LIVE 3D VIEW",w-125*d,35*d,10*d,Color.rgb(103,220,174),true);

            p.setColor(Color.argb(220,17,22,34)); c.drawRect(0,h-70*d,w,h,p);
            text(c,"HIERARCHY",18*d,h-45*d,10*d,Color.rgb(139,154,183),true);
            text(c,"▣  Scene",18*d,h-24*d,12*d,Color.WHITE,true);
            text(c,"Cubes: "+renderer.cubeCount,112*d,h-24*d,12*d,Color.rgb(196,207,229),false);

            float bw=108*d, bh=42*d, gap=9*d;
            float start=w-(bw*3+gap*2+16*d);
            button(c,new RectF(start,h-57*d,start+bw,h-15*d),"＋  ADD CUBE",true);
            button(c,new RectF(start+bw+gap,h-57*d,start+2*bw+gap,h-15*d),renderer.grid?"GRID ON":"GRID OFF",false);
            button(c,new RectF(start+2*(bw+gap),h-57*d,start+3*bw+2*gap,h-15*d),"RESET",false);

            text(c,"Drag to orbit  •  Pinch not required",w/2-104*d,75*d,10*d,Color.argb(190,220,228,245),false);
        }
        @Override public boolean onTouchEvent(MotionEvent e) {
            float d=getResources().getDisplayMetrics().density, w=getWidth(), h=getHeight();
            float bw=108*d, gap=9*d, start=w-(bw*3+gap*2+16*d);
            if(e.getAction()==MotionEvent.ACTION_DOWN) {
                downX=lastX=e.getX(); downY=lastY=e.getY(); dragging=false; return true;
            }
            if(e.getAction()==MotionEvent.ACTION_MOVE) {
                float dx=e.getX()-lastX, dy=e.getY()-lastY;
                if(downY>65*d && downY<h-75*d) {
                    dragging=true; renderer.orbit(dx*0.008f,dy*0.008f);
                }
                lastX=e.getX(); lastY=e.getY(); return true;
            }
            if(e.getAction()==MotionEvent.ACTION_UP) {
                if(!dragging && e.getY()>h-70*d && e.getY()<h) {
                    float x=e.getX();
                    if(x>=start && x<start+bw) renderer.addCube();
                    else if(x>=start+bw+gap && x<start+2*bw+gap) renderer.grid=!renderer.grid;
                    else if(x>=start+2*(bw+gap)) renderer.reset();
                }
                invalidate(); return true;
            }
            return true;
        }
    }

    private static final class SceneRenderer implements GLSurfaceView.Renderer {
        volatile int cubeCount=1;
        volatile boolean grid=true;
        private volatile float yaw=0.65f, pitch=0.42f;
        private int program, position, color, mvpHandle;
        private FloatBuffer cubeBuffer, gridBuffer;
        private final float[] projection=new float[16], view=new float[16], model=new float[16], vp=new float[16], mvp=new float[16];
        private long start=System.currentTimeMillis();

        void addCube(){ cubeCount=Math.min(24,cubeCount+1); }
        void reset(){ cubeCount=1; yaw=0.65f; pitch=0.42f; grid=true; }
        void orbit(float x,float y){ yaw+=x; pitch=Math.max(-1.15f,Math.min(1.15f,pitch+y)); }

        @Override public void onSurfaceCreated(GL10 gl,EGLConfig config) {
            GLES20.glClearColor(0.055f,0.075f,0.12f,1f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            String vs="attribute vec3 aPos; attribute vec4 aColor; uniform mat4 uMVP; varying vec4 vColor; void main(){ gl_Position=uMVP*vec4(aPos,1.0); vColor=aColor; }";
            String fs="precision mediump float; varying vec4 vColor; void main(){ gl_FragColor=vColor; }";
            int v=shader(GLES20.GL_VERTEX_SHADER,vs), f=shader(GLES20.GL_FRAGMENT_SHADER,fs);
            program=GLES20.glCreateProgram(); GLES20.glAttachShader(program,v); GLES20.glAttachShader(program,f); GLES20.glLinkProgram(program);
            position=GLES20.glGetAttribLocation(program,"aPos");
            color=GLES20.glGetAttribLocation(program,"aColor");
            mvpHandle=GLES20.glGetUniformLocation(program,"uMVP");
            cubeBuffer=buffer(cubeData());
            gridBuffer=buffer(gridData());
        }
        private int shader(int type,String source){
            int s=GLES20.glCreateShader(type); GLES20.glShaderSource(s,source); GLES20.glCompileShader(s); return s;
        }
        private FloatBuffer buffer(float[] a){
            FloatBuffer b=ByteBuffer.allocateDirect(a.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            b.put(a).position(0); return b;
        }
        private float[] cubeData(){
            float[][] faces={
                {-0.5f,-0.5f,0.5f, 0.5f,-0.5f,0.5f, 0.5f,0.5f,0.5f, -0.5f,0.5f,0.5f},
                {0.5f,-0.5f,-0.5f, -0.5f,-0.5f,-0.5f, -0.5f,0.5f,-0.5f, 0.5f,0.5f,-0.5f},
                {-0.5f,0.5f,0.5f, 0.5f,0.5f,0.5f, 0.5f,0.5f,-0.5f, -0.5f,0.5f,-0.5f},
                {-0.5f,-0.5f,-0.5f, 0.5f,-0.5f,-0.5f, 0.5f,-0.5f,0.5f, -0.5f,-0.5f,0.5f},
                {0.5f,-0.5f,0.5f, 0.5f,-0.5f,-0.5f, 0.5f,0.5f,-0.5f, 0.5f,0.5f,0.5f},
                {-0.5f,-0.5f,-0.5f, -0.5f,-0.5f,0.5f, -0.5f,0.5f,0.5f, -0.5f,0.5f,-0.5f}
            };
            float[][] colors={{0.25f,0.55f,1f},{0.12f,0.3f,0.72f},{0.42f,0.68f,1f},{0.12f,0.25f,0.55f},{0.32f,0.62f,0.95f},{0.18f,0.4f,0.78f}};
            float[] out=new float[36*7]; int k=0;
            for(int face=0;face<6;face++){
                int[] order={0,1,2,0,2,3};
                for(int idx:order){
                    for(int j=0;j<3;j++) out[k++]=faces[face][idx*3+j];
                    out[k++]=colors[face][0]; out[k++]=colors[face][1]; out[k++]=colors[face][2]; out[k++]=1f;
                }
            }
            return out;
        }
        private float[] gridData(){
            float[] a=new float[22*2*7]; int k=0;
            for(int i=-5;i<=5;i++){
                float v=i;
                float[][] lines={{-5,0,v,5,0,v},{v,0,-5,v,0,5}};
                for(float[] line:lines) for(int endpoint=0;endpoint<2;endpoint++){
                    int off=endpoint*3;
                    a[k++]=line[off]; a[k++]=line[off+1]; a[k++]=line[off+2];
                    float shade=(i==0)?0.42f:0.19f;
                    a[k++]=shade; a[k++]=shade+0.04f; a[k++]=shade+0.1f; a[k++]=1f;
                }
            }
            return a;
        }
        @Override public void onSurfaceChanged(GL10 gl,int width,int height){
            GLES20.glViewport(0,0,width,height);
            Matrix.perspectiveM(projection,0,52f,Math.max(0.1f,(float)width/height),0.1f,100f);
        }
        private void draw(FloatBuffer b,int count,int mode,float[] matrix){
            b.position(0); GLES20.glUniformMatrix4fv(mvpHandle,1,false,matrix,0);
            GLES20.glEnableVertexAttribArray(position); GLES20.glEnableVertexAttribArray(color);
            b.position(0); GLES20.glVertexAttribPointer(position,3,GLES20.GL_FLOAT,false,28,b);
            b.position(3); GLES20.glVertexAttribPointer(color,4,GLES20.GL_FLOAT,false,28,b);
            GLES20.glDrawArrays(mode,0,count);
            GLES20.glDisableVertexAttribArray(position); GLES20.glDisableVertexAttribArray(color);
        }
        @Override public void onDrawFrame(GL10 gl){
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(program);
            float radius=7.2f, cp=(float)Math.cos(pitch);
            float ex=(float)Math.sin(yaw)*cp*radius, ey=(float)Math.sin(pitch)*radius, ez=(float)Math.cos(yaw)*cp*radius;
            Matrix.setLookAtM(view,0,ex,ey,ez,0,0.25f,0,0,1,0);
            Matrix.multiplyMM(vp,0,projection,0,view,0);
            if(grid){ Matrix.setIdentityM(model,0); Matrix.multiplyMM(mvp,0,vp,0,model,0); draw(gridBuffer,44,GLES20.GL_LINES,mvp); }
            float time=(System.currentTimeMillis()-start)/1000f;
            for(int i=0;i<cubeCount;i++){
                float x=(i%5-2)*1.35f, z=(i/5-1)*1.35f;
                Matrix.setIdentityM(model,0);
                Matrix.translateM(model,0,x,0.65f,z);
                Matrix.rotateM(model,0,time*18f+i*19f,0.35f,1f,0.15f);
                Matrix.multiplyMM(mvp,0,vp,0,model,0);
                draw(cubeBuffer,36,GLES20.GL_TRIANGLES,mvp);
            }
        }
    }
}
