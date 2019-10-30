package com.ghsoft.android.dmz;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.ghsoft.android.dmz.util.GpsInfo;
import com.ghsoft.android.dmz.util.WebDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.skt.Tmap.TMapTapi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.speech.tts.TextToSpeech.ERROR;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by admin on 2018-04-26.
 */

public class SubWebActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    public static final String INTENT_PROTOCOL_START = "intent:";
    public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
    public static final String INTENT_PROTOCOL_END = ";end;";
    public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

    // 카카오톡 로그인 (최신버전 1.4.1)
    private SessionCallback callback;
    LoginButton loginButton;

    // 네이버 로그인
    private static String OAUTH_CLIENT_ID = "J6vZQtr8l5L10HkjdUbu";
    private static String OAUTH_CLIENT_SECRET = "HMdLZnVgUX";
    private static String OAUTH_CLIENT_NAME = "DMZ";
    private static OAuthLogin mOAuthLoginModule;
    OAuthLoginButton naverLoginButton;

    // 페이스북 로그인
    private CallbackManager callbackManager;

    WebView webView;
    AQuery aQuery;
    Dialog dialog;
    GpsInfo gps;
    Handler handler = new Handler();
    private TextToSpeech tts;

    Window window;
    int vis;
    TMapTapi tmaptapi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        // SDK 초기화 (setContentView 보다 먼저 실행되어야합니다. 안그럼 에러납니다.)
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_subweb);

        tmaptapi = new TMapTapi(SubWebActivity.this);
        tmaptapi.setSKTMapAuthentication("a27968d8-7d06-4168-aba0-7a82b8ff1a17");

        Intent getIntent = getIntent();
        String url = getIntent.getStringExtra("url");

        window = getWindow();
        vis = getWindow().getDecorView().getSystemUiVisibility();

        aQuery = new AQuery(this);
        setWebView(url);
        setLogin();
        // TTS를 생성하고 OnInitListener로 초기화 한다.
        tts = new TextToSpeech(this, this);
        tts.setOnUtteranceCompletedListener(this);
    }


    private void setWebView(String url) {
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);  // 웹뷰 자바스크립트 사용 여부
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);  // window.open 사용 가능 여부
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
        webView.getSettings().setTextZoom(100);

        //줌
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.setVerticalScrollbarOverlay(false);
        webView.requestFocus(View.FOCUS_DOWN);
        webView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        webView.setDrawingCacheEnabled(true);
        webView.setAlwaysDrawnWithCacheEnabled(true);
        webView.setAnimationCacheEnabled(true);

        String wevContent = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><link rel=\"stylesheet\" href=\"style.css\"></head>"
                + "<body><img src='image.png' width=\"100px\"><div class=\"running\">I am a text rendered with INDIGO</div></body></html>";

        String internalFilePath = "file://"+getFilesDir().getAbsolutePath()+"/";
        webView.loadDataWithBaseURL(internalFilePath,wevContent, "text/html", "UTF-8", "");
////////////////////////////////////////////////////////////////////
        WebView.setWebContentsDebuggingEnabled(true);

        String userAgent = new WebView(getBaseContext()).getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(userAgent + " gh_mobile{"
                + FirebaseInstanceId.getInstance().getToken() + "}");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(INTENT_PROTOCOL_START)) {
                    final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                    final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                    if (customUrlEndIndex < 0) {
                        return false;
                    } else {
                        final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                        } catch (ActivityNotFoundException e) {
                            final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                            final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                            final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                        }
                        return true;
                    }
                } else {
                    view.loadUrl(url);
                    return true;
                }
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                WebDialog webDialog = new WebDialog();
                dialog = webDialog.alertDialog(SubWebActivity.this, message, result);
                dialog.show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                WebDialog webDialog = new WebDialog();
                dialog = webDialog.confirmDialog(SubWebActivity.this, message, result);
                dialog.show();
                return true;
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                final WebView newWebView = new WebView(SubWebActivity.this);
                WebSettings webSettings = newWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.e("aaa", url);
                        if (!url.startsWith(getString(R.string.domain))) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            return true;
                        } else {
                            newWebView.setVisibility(View.GONE);
                            Intent intent = new Intent(SubWebActivity.this, SubWebActivity.class);
                            intent.putExtra("url", url);
                            startActivity(intent);
                            return true;
                        }
                    }
                });

                ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }

        });
        webView.addJavascriptInterface(new AndroidBridge(), "dmz");
        webView.loadUrl(url);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });

    }

    @Override
    public void onInit(int status) {
        if (status != ERROR) {
            // 언어를 선택한다.
            tts.setLanguage(Locale.KOREAN);
        }
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        // tts 끝
        Log.e("Aaa", "end");
        handler.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:cla.speaked()");
            }
        });
    }

    class AndroidBridge {
        @JavascriptInterface
        public void gps(String json) {
            // gps설정 유/무 확인
            handler.post(new Runnable() {
                @Override
                public void run() {
                    gps = new GpsInfo(SubWebActivity.this);
                    if (!gps.isGetLocation()) {
                        try {
//                gps.showSettingsAlert();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(SubWebActivity.this);
                            builder.setMessage("위치기능을 켜지 않으면 정상적인 서비스 이용이 어려울 수 있으니 켜주세요.")
                                    .setPositiveButton("켜기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            gps.showSettingsAlert();
                                        }
                                    })
                                    .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .setCancelable(false)
                                    .create()
                                    .show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public void close(String json) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            });
        }
        @JavascriptInterface
        public void image(final String json) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(json,"이미지");

                    Intent intent = new Intent(SubWebActivity.this,ImageActivity.class);
                    intent.putExtra("image",json);
                    startActivity(intent);


                }
            });
        }


        @JavascriptInterface
        public boolean login(final String json) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = new JSONObject(json);
                        String type = object.getString("type");
                        if (type.equals("kakao")) {
                            loginButton.performClick();
                        } else if (type.equals("facebook")) {
                            facebookLogin();
                        } else {
                            naverLoginButton.performClick();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }

        @JavascriptInterface
        public void speakStart(final String json) {
            // 음성 시작
            if (!tts.isSpeaking()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(json);
                            String content = object.getString("text");
                            // tts end 이벤트를 잡기위한 id parameter
                            HashMap<String, String> myHashRender = new HashMap<String, String>();
                            myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "tts_id");
                            tts.speak(content, TextToSpeech.QUEUE_FLUSH, myHashRender);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void speakCancel(String json) {
            // 일시정지 or 정지
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (tts.isSpeaking()) {
                        tts.stop();
                    }
                }
            });
        }

        @JavascriptInterface
        public void speakRate(String json) {
            // tts 속도변경
            try {
                JSONObject object = new JSONObject(json);
                float rate = (float) object.getDouble("rate");
                tts.setSpeechRate(rate);    // 읽는 속도는 기본 설정
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public boolean speakStatus(String json) {
            // tts 실행 여부 return 브릿지
            return tts.isSpeaking();
        }

        @JavascriptInterface
        public void statusbar(final String json) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = new JSONObject(json);
                        String color = object.getString("color");
                        if (color.equals("#ffffff")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                //흰바탕 검정아이콘
                                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                                getWindow().getDecorView().setSystemUiVisibility(vis);
                                window.setStatusBarColor(Color.parseColor("#ffffff"));
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                //검은바탕 흰 아이콘
                                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                                getWindow().getDecorView().setSystemUiVisibility(vis);
                                window.setStatusBarColor(Color.parseColor(color));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @JavascriptInterface
        public void copy(String json) {
            try {
                JSONObject object = new JSONObject(json);
                String text = object.getString("text");
                final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setText(text);
                Toast.makeText(SubWebActivity.this, "복사되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void navi(final String json) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject object = new JSONObject(json);
                        final String title = object.getString("title");
                        final float lat = (float) object.getDouble("lat");
                        final float lon = (float) object.getDouble("lng");
                        boolean isTmapApp = tmaptapi.isTmapApplicationInstalled();
                        Log.e("aaa", isTmapApp + "");
                        if (isTmapApp) {
                            tmaptapi.invokeRoute(title, lon, lat);
                        } else {
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.skt.tmap.ku");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }

    private void setLogin() {
        // 카톡 로그인   --------------------------------------------------
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                // 카톡 로그아웃 성공
            }
        });
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        loginButton = (LoginButton) findViewById(R.id.com_kakao_login);
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isConnected()) {
                        Toast.makeText(SubWebActivity.this, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                }

                if (isConnected()) {
                    return false;
                } else {
                    return true;
                }
            }
        });

        // 카톡 로그인 END  ------------------------------------------------

        // 네이버 로그인 -----------------------------------------------------------
        InitializeNaverAPI();
        mOAuthLoginModule.logout(SubWebActivity.this);
        // 네이버 로그인 END --------------------------------------------------------

        // 페이스북 로그인 -----------------------------------------------------------------------
        callbackManager = CallbackManager.Factory.create(); //로그인 응답을 처리할 콜백 관리자
        LoginManager.getInstance().logOut();
        // 페이스북 로그인 EDN --------------------------------------------------------------------
    }

    //인터넷 연결상태 확인
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    // 카카오톡
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            //access token을 성공적으로 발급 받아 valid access token을 가지고 있는 상태. 일반적으로 로그인 후의 다음 activity로 이동한다.
            if (Session.getCurrentSession().isOpened()) { // 한 번더 세션을 체크해주었습니다.
                Log.e("token111", Session.getCurrentSession().getTokenInfo().getAccessToken());
                login("kakao", Session.getCurrentSession().getTokenInfo().getAccessToken());

            }
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Logger.e(exception);
                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        // 카톡 로그아웃 성공
                    }
                });
            }
        }
    }

    // 네이버 API 초기화
    private void InitializeNaverAPI() {
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                this,
                OAUTH_CLIENT_ID,
                OAUTH_CLIENT_SECRET,
                OAUTH_CLIENT_NAME
        );

        // 네이버 로그인 버튼 리스너 등록
        naverLoginButton = (OAuthLoginButton) findViewById(R.id.button_naverlogin);
        naverLoginButton.setOAuthLoginHandler(new OAuthLoginHandler() {
            @Override
            public void run(boolean b) {
                if (b) {
                    final String token = mOAuthLoginModule.getAccessToken(SubWebActivity.this);
                    login("naver", token);
                } else {
                }
            }
        });
    }

    private void facebookLogin() {
        // 페이스북 로그인
        LoginManager.getInstance().logInWithReadPermissions(SubWebActivity.this, Arrays.asList("public_profile", "email", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("token", loginResult.getAccessToken().getToken());
                login("facebook", loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException error) {
                LoginManager.getInstance().logOut();
            }
        });
    }

    private void login(String type, String token) {
        String url = getString(R.string.domain) + "/login/" + type;
        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        aQuery.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                try {
                    String result = object.getString("return");
                    if (result.endsWith("true")) {
                        finish();
                    } else {
                        Toast.makeText(SubWebActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.header("User-Agent", "gh_mobile{" + FirebaseInstanceId.getInstance().getToken() + "}"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TTS 객체가 남아있다면 실행을 중지하고 메모리에서 제거한다.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
    }
}
