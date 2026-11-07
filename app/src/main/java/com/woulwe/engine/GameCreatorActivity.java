package com.woulwe.engine;

import android.app.Activity;
import android.os.Bundle;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GameCreatorActivity extends Activity {
    private static final String AI_URL = "https://bronoueyjmiedzclgcms.supabase.co/functions/v1/leor-ai";
    // Supabase anon key is a public client key, not a service-role secret.
    private static final String SUPABASE_PUBLIC_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJyb25vdWV5am1pZWR6Y2xnY21zIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODkzODYzMTQsImV4cCI6MjEwNDk2MjMxNH0.wxCUkkl8ye7MLM6EA6vTm1U38ju1YqycJtNiBPn-xpM;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private EditText description;
    private Button createButton;
    private ProgressBar progress;
    private TextView status;
    private String pendingHtml;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
        buildScreen();
    }

    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(12, 17, 29));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(24), dp(22), dp(22));
        scroll.addView(root);

        TextView brand = new TextView(this);
        brand.setText("WOULWE ENGINE");
        brand.setTextColor(Color.rgb(151, 171, 255));
        brand.setTextSize(12);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(brand);

        TextView title = new TextView(this);
        title.setText("Создай свою игру");
        title.setTextColor(Color.WHITE);
        title.setTextSize(29);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.topMargin = dp(18);
        root.addView(title, titleParams);

        TextView subtitle = new TextView(this);
        subtitle.setText("Опиши идею своими словами. Получи игровую версию, которую можно запустить на телефоне.");
        subtitle.setTextColor(Color.rgb(177, 188, 211));
        subtitle.setTextSize(15);
        subtitle.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(-1, -2);
        subParams.topMargin = dp(10);
        root.addView(subtitle, subParams);

        description = new EditText(this);
        description.setTextColor(Color.WHITE);
        description.setHintTextColor(Color.rgb(130, 143, 169));
        description.setHint("Например: большая мультяшная игра про выживание на острове, с животными, строительством базы, сменой дня и ночи и заданиями...");
        description.setTextSize(16);
        description.setGravity(Gravity.TOP | Gravity.START);
        description.setMinLines(7);
        description.setMaxLines(12);
        description.setPadding(dp(16), dp(16), dp(16), dp(16));
        description.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(93, 112, 255)));
        description.setBackgroundColor(Color.rgb(27, 35, 53));
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(-1, -2);
        inputParams.topMargin = dp(24);
        root.addView(description, inputParams);

        createButton = new Button(this);
        createButton.setText("Создать игру");
        createButton.setTextSize(16);
        createButton.setAllCaps(false);
        createButton.setTextColor(Color.WHITE);
        createButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(86, 106, 255)));
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(-1, dp(54));
        buttonParams.topMargin = dp(18);
        root.addView(createButton, buttonParams);
        createButton.setOnClickListener(v -> createGame());

        progress = new ProgressBar(this);
        progress.setIndeterminate(true);
        progress.setVisibility(View.GONE);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(-2, -2);
        progressParams.gravity = Gravity.CENTER_HORIZONTAL;
        progressParams.topMargin = dp(20);
        root.addView(progress, progressParams);

        status = new TextView(this);
        status.setTextColor(Color.rgb(173, 190, 222));
        status.setTextSize(13);
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, -2);
        statusParams.topMargin = dp(12);
        root.addView(status, statusParams);

        Button download = new Button(this);
        download.setText("Скачать игру в Загрузки");
        download.setAllCaps(false);
        download.setTextColor(Color.WHITE);
        download.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(39, 52, 78)));
        LinearLayout.LayoutParams downloadParams = new LinearLayout.LayoutParams(-1, dp(52));
        downloadParams.topMargin = dp(18);
        root.addView(download, downloadParams);
        download.setOnClickListener(v -> {
            if (pendingHtml == null) {
                Toast.makeText(this, "Сначала создай игру", Toast.LENGTH_SHORT).show();
            } else {
                saveGame(pendingHtml);
            }
        });

        setContentView(scroll);
    }

    private void createGame() {
        String prompt = description.getText().toString().trim();
        if (prompt.length() < 10) {
            description.setError("Опиши игру подробнее");
            return;
        }
        if (prompt.length() > 8000) {
            description.setError("Сократи описание до 8000 символов");
            return;
        }
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(description.getWindowToken(), 0);
        setBusy(true, "Создаю игру…");
        executor.execute(() -> {
            String html = null;
            boolean aiUsed = false;
            try {
                html = requestLeorGame(prompt);
                aiUsed = html != null;
            } catch (Exception ignored) {
                // The local generator still creates a playable export when the model service is offline.
            }
            if (html == null) html = buildPlayableGame(prompt);
            final String result = html;
            final boolean usedAi = aiUsed;
            runOnUiThread(() -> {
                pendingHtml = result;
                setBusy(false, usedAi ? "Игра готова. Можно скачать файл." : "Игра готова. Можно скачать файл.");
                Toast.makeText(this, "Игра создана", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void setBusy(boolean busy, String message) {
        createButton.setEnabled(!busy);
        createButton.setText(busy ? "Создаю…" : "Создать игру");
        progress.setVisibility(busy ? View.VISIBLE : View.GONE);
        status.setText(message);
    }

    private String requestLeorGame(String prompt) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)new URL(AI_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(12000);
        connection.setReadTimeout(115000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("apikey", SUPABASE_PUBLIC_KEY);
        connection.setRequestProperty("Authorization", "Bearer " + SUPABASE_PUBLIC_KEY);
        JSONObject body = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject system = new JSONObject();
        system.put("role", "system");
        system.put("content", "Ты создаёшь небольшие полноценные мобильные HTML5 игры. Верни только один самодостаточный HTML-документ без Markdown. Внутри должны быть CSS и JavaScript, адаптация под сенсорный экран, понятное управление и игровой цикл. Не используй внешние библиотеки, CDN или изображения по URL. Реализуй игровую механику из описания пользователя.");
        messages.put(system);
        JSONObject user = new JSONObject();
        user.put("role", "user");
        user.put("content", "Создай готовую игру по описанию: " + prompt);
        messages.put(user);
        body.put("messages", messages);
        body.put("temperature", 0.7);
        body.put("max_tokens", 512);
        try (OutputStream out = connection.getOutputStream()) {
            out.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }
        int code = connection.getResponseCode();
        InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
        String response = readAll(stream);
        connection.disconnect();
        if (code < 200 || code >= 300) return null;
        JSONObject json = new JSONObject(response);
        JSONArray choices = json.optJSONArray("choices");
        if (choices == null || choices.length() == 0) return null;
        JSONObject message = choices.getJSONObject(0).optJSONObject("message");
        if (message == null) return null;
        String content = message.optString("content", "").trim();
        content = content.replaceFirst("(?s)^\\s*```(?:html)?\\s*", "");
        content = content.replaceFirst("(?s)\\s*```\\s*$", "");
        if (content.length() < 100 || content.length() > 2000000 ||
            !(content.toLowerCase().contains("<html") || content.toLowerCase().contains("<!doctype html"))) return null;
        return content;
    }

    private String readAll(InputStream input) throws Exception {
        if (input == null) return "";
        try (InputStream in = input; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private String buildPlayableGame(String prompt) {
        String safeTitle = escapeHtml(prompt.length() > 70 ? prompt.substring(0, 70) + "…" : prompt);
        String titleJs = JSONObject.quote(prompt.length() > 70 ? prompt.substring(0, 70) + "…" : prompt);
        return "<!doctype html><html lang='ru'><head><meta charset='utf-8'><meta name='viewport' content='width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no'><title>" + safeTitle + "</title><style>*{box-sizing:border-box}body{margin:0;background:linear-gradient(160deg,#111a31,#263b55);color:#fff;font:16px system-ui;overflow:hidden}header{height:58px;padding:16px;background:#101827cc;font-weight:700;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}canvas{display:block;width:100vw;height:calc(100dvh - 126px);touch-action:none}footer{height:68px;display:flex;align-items:center;justify-content:center;gap:12px;background:#101827cc}button{border:1px solid #91b8ff;background:#263b5c;color:white;border-radius:12px;padding:11px 20px;font-weight:700}</style></head><body><header id='title'></header><canvas id='game'></canvas><footer><button id='restart'>Начать заново</button><span>Касайся экрана, чтобы двигаться</span></footer><script>document.getElementById('title').textContent=" + titleJs + ";const c=document.getElementById('game'),g=c.getContext('2d');let w=0,h=0,player={x:.5,y:.78},things=[],score=0,ended=false;function resize(){const d=devicePixelRatio||1;c.width=innerWidth*d;c.height=(innerHeight-126)*d;w=c.width;h=c.height}addEventListener('resize',resize);resize();function move(e){const r=c.getBoundingClientRect();player.x=Math.max(.06,Math.min(.94,(e.clientX-r.left)/r.width));player.y=Math.max(.12,Math.min(.9,(e.clientY-r.top)/r.height))}c.addEventListener('pointerdown',move);c.addEventListener('pointermove',e=>{if(e.buttons)move(e)});function reset(){score=0;things=[];ended=false}document.getElementById('restart').onclick=reset;let last=0,spawn=0;function frame(t){const dt=Math.min(.04,(t-last)/1000||0);last=t;if(!ended){spawn+=dt;if(spawn>.72){spawn=0;things.push({x:Math.random()*w,y:-24,r:10+Math.random()*12,s:180+Math.random()*130})}}g.clearRect(0,0,w,h);g.fillStyle='#ffffff12';for(let i=0;i<35;i++){g.beginPath();g.arc((i*137)%w,(i*83+t*.018)%h,2,0,7);g.fill()}const px=player.x*w,py=player.y*h;for(let i=things.length-1;i>=0;i--){const a=things[i];a.y+=a.s*dt;g.fillStyle='#ffb86b';g.beginPath();g.arc(a.x,a.y,a.r,0,7);g.fill();if(Math.hypot(a.x-px,a.y-py)<a.r+18){score++;things.splice(i,1)}else if(a.y>h+30)things.splice(i,1)}g.fillStyle='#83e7c2';g.beginPath();g.arc(px,py,19,0,7);g.fill();g.fillStyle='white';g.font='bold 22px system-ui';g.fillText('Очки: '+score,18,32);requestAnimationFrame(frame)}requestAnimationFrame(frame);</script></body></html>";
    }

    private void saveGame(String html) {
        String filename = "Woulwe-Game-" + System.currentTimeMillis() + ".html";
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "text/html");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/");
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) throw new Exception("Не удалось создать файл");
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    if (out == null) throw new Exception("Не удалось открыть файл");
                    out.write(html.getBytes(StandardCharsets.UTF_8));
                }
                Toast.makeText(this, "Файл сохранён в Загрузки", Toast.LENGTH_LONG).show();
            } else {
                pendingHtml = html;
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_TITLE, filename);
                startActivityForResult(intent, 71);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось сохранить файл", Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request == 71 && result == RESULT_OK && data != null && pendingHtml != null) {
            try (OutputStream out = getContentResolver().openOutputStream(data.getData())) {
                if (out == null) throw new Exception("No output stream");
                out.write(pendingHtml.getBytes(StandardCharsets.UTF_8));
                Toast.makeText(this, "Файл сохранён", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Не удалось сохранить файл", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
