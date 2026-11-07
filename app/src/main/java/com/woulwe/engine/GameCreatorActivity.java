package com.woulwe.engine;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.Toast;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class GameCreatorActivity extends Activity {
    private final String[][] questions = {
        {"ЖАНР ИГРЫ", "Выбери основу игры", "Выживание", "Платформер", "Гонки", "Шутер"},
        {"МИР ИГРЫ", "Где происходит действие?", "Лес и остров", "Космос", "Город", "Фэнтези"},
        {"ВИЗУАЛЬНЫЙ СТИЛЬ", "Как должна выглядеть игра?", "Мультяшная", "Реалистичная", "Низкополигональная", "Неоновая"},
        {"ГЕЙМПЛЕЙ", "Что важнее всего?", "Исследование", "Сражения", "Строительство", "Головоломки"},
        {"ПЛАТФОРМА", "Где запускать игру?", "Android", "Windows", "Android + Windows", "Web"}
    };
    private final String[] answers = {"Выживание","Лес и остров","Мультяшная","Исследование","Android"};
    private int step = 0;
    private CreatorView view;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
        view = new CreatorView();
        setContentView(view);
    }

    private void saveGame() {
        String html = buildGame();
        Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("text/html");
        i.putExtra(Intent.EXTRA_TITLE, "WoulweGame.html");
        startActivityForResult(i, 71);
    }

    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request,result,data);
        if(request==71 && result==RESULT_OK && data!=null) {
            try {
                OutputStream out=getContentResolver().openOutputStream(data.getData());
                out.write(buildGame().getBytes(StandardCharsets.UTF_8));
                out.close();
                Toast.makeText(this,"Игра создана и сохранена. Открой HTML-файл в браузере.",Toast.LENGTH_LONG).show();
            } catch(Exception e) {
                Toast.makeText(this,"Не удалось сохранить игру: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    private String esc(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }
    private String buildGame() {
        String genre=answers[0], world=answers[1], style=answers[2], play=answers[3], target=answers[4];
        String title=genre+" — "+world;
        String color=style.equals("Неоновая")?"#07152b":style.equals("Реалистичная")?"#18251e":style.equals("Низкополигональная")?"#25344b":"#203b32";
        String accent=style.equals("Неоновая")?"#33e6ff":style.equals("Реалистичная")?"#93bd79":style.equals("Низкополигональная")?"#ffc66d":"#78e0a0";
        return "<!doctype html><html lang='ru'><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no'><title>"+esc(title)+"</title><style>*{box-sizing:border-box}body{margin:0;background:"+color+";color:white;font:16px system-ui;overflow:hidden}header{padding:12px 16px;background:#101827dd;display:flex;justify-content:space-between}canvas{display:block;width:100vw;height:calc(100dvh - 100px);touch-action:none}footer{height:52px;display:flex;justify-content:center;align-items:center;color:"+accent+"}button{background:#27364d;color:white;border:1px solid "+accent+";border-radius:12px;padding:10px 18px}</style><header><b>WOULWE GAME</b><span>"+esc(target)+"</span></header><canvas id='g'></canvas><footer><button id='restart'>Заново</button><span style='margin-left:14px'> "+esc(play)+" · Управление касанием</span></footer><script>const c=document.getElementById('g'),x=c.getContext('2d');let w,h,px=.5,py=.7,score=0,items=[],running=true;function size(){c.width=innerWidth*devicePixelRatio;c.height=(innerHeight-100)*devicePixelRatio;w=c.width;h=c.height}onresize=size;size();function spawn(){items.push({x:Math.random()*w,y:-30,r:12+Math.random()*15,s:2+Math.random()*3})}setInterval(spawn,850);function draw(){x.clearRect(0,0,w,h);x.fillStyle='"+accent+"';x.globalAlpha=.14;for(let i=0;i<20;i++){x.fillRect((i*173+score*2)%w,(i*97)%h,3,3)}x.globalAlpha=1;let p={x:px*w,y:py*h};x.fillStyle='"+accent+"';x.beginPath();x.arc(p.x,p.y,22*devicePixelRatio,0,7);x.fill();for(let a=items.length-1;a>=0;a--){let o=items[a];o.y+=o.s*devicePixelRatio;x.fillStyle='#ffb45e';x.beginPath();x.arc(o.x,o.y,o.r*devicePixelRatio,0,7);x.fill();if(Math.hypot(o.x-p.x,o.y-p.y)<(o.r+20)*devicePixelRatio){score++;items.splice(a,1)}else if(o.y>h+30)items.splice(a,1)}x.fillStyle='white';x.font='bold 22px system-ui';x.fillText('Очки: '+score,18,34);requestAnimationFrame(draw)}draw();function move(e){let r=c.getBoundingClientRect();px=Math.max(.04,Math.min(.96,(e.clientX-r.left)/r.width));py=Math.max(.12,Math.min(.92,(e.clientY-r.top)/r.height))}c.addEventListener('pointerdown',move);c.addEventListener('pointermove',e=>{if(e.buttons)move(e)});document.getElementById('restart').onclick=()=>{score=0;items=[]};</script></html>";
    }

    private final class CreatorView extends View {
        private final Paint p=new Paint(3);
        private float density;
        CreatorView(){super(GameCreatorActivity.this);density=getResources().getDisplayMetrics().density;}
        void txt(Canvas c,String s,float x,float y,float size,int col,boolean bold){p.setColor(col);p.setTextSize(size*density);p.setFakeBoldText(bold);c.drawText(s,x,y,p);}
        void card(Canvas c,float x,float y,float w,float h,String s,boolean selected){p.setColor(selected?Color.rgb(68,91,174):Color.rgb(34,43,61));c.drawRoundRect(new RectF(x,y,x+w,y+h),14*density,14*density,p);p.setColor(selected?Color.rgb(150,180,255):Color.rgb(75,88,116));p.setStyle(Paint.Style.STROKE);c.drawRoundRect(new RectF(x,y,x+w,y+h),14*density,14*density,p);p.setStyle(Paint.Style.FILL);txt(c,s,x+16*density,y+h/2+5*density,15,Color.WHITE,true);}
        @Override protected void onDraw(Canvas c){
            float w=getWidth(),h=getHeight(),d=density;
            c.drawColor(Color.rgb(13,18,30));
            txt(c,"WOULWE AI",22*d,38*d,17,Color.WHITE,true);
            txt(c,"GAME CREATOR",22*d,59*d,10,Color.rgb(128,151,205),true);
            txt(c,"Шаг "+(step+1)+" из 5",w-100*d,42*d,11,Color.LTGRAY,false);
            txt(c,questions[step][0],22*d,112*d,23,Color.WHITE,true);
            txt(c,questions[step][1],22*d,140*d,13,Color.rgb(174,187,211),false);
            float top=170*d, gap=12*d, ch=Math.min(58*d,(h-top-100*d)/4);
            for(int i=0;i<4;i++) card(c,20*d,top+i*(ch+gap),w-40*d,ch,questions[step][i+2],answers[step].equals(questions[step][i+2]));
            card(c,20*d,h-72*d,(w-52*d)/2,48*d,step==0?"ВЫЙТИ":"НАЗАД",false);
            card(c,32*d+(w-52*d)/2,h-72*d,(w-52*d)/2,48*d,step==4?"СОЗДАТЬ ИГРУ":"ДАЛЕЕ",true);
            txt(c,"Ответы можно менять — выбери подходящий вариант",22*d,h-88*d,10,Color.rgb(145,159,185),false);
        }
        @Override public boolean onTouchEvent(android.view.MotionEvent e){
            if(e.getAction()!=1)return true;
            float x=e.getX(),y=e.getY(),w=getWidth(),h=getHeight(),d=density;
            float top=170*d,gap=12*d,ch=Math.min(58*d,(h-top-100*d)/4);
            for(int i=0;i<4;i++)if(y>=top+i*(ch+gap)&&y<=top+i*(ch+gap)+ch){answers[step]=questions[step][i+2];invalidate();return true;}
            if(y>h-80*d){
                if(x<w/2){if(step==0)finish();else step--;}
                else if(step<4)step++;else saveGame();
                invalidate();
            }
            return true;
        }
    }
}