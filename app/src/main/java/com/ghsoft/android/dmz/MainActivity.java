package com.ghsoft.android.dmz;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ghsoft.android.dmz.guide.GuideFragment;
import com.ghsoft.android.dmz.util.GpsInfo;
import com.ghsoft.android.dmz.util.WebDialog;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.MessageDigest;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity extends AppCompatActivity {
    private long backKeyPressedTime = 0;
    FrameLayout guide;
    SharedPreferences pref;
    SharedPreferences.Editor prefEd;
    WebView webView;
    Dialog dialog;
    GpsInfo gps;
    Handler handler = new Handler();

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        getHashKey(mContext);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        prefEd = pref.edit();

        setGuide();
        setWebView();
        Intent getIntent = getIntent();
        String url = getIntent.getStringExtra("url");
        if (url != null) {
            Intent intent = new Intent(MainActivity.this, SubWebActivity.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    }

    ///////////////// 키해시받아서 다시사작
    // 프로젝트의 해시키를 반환
    @Nullable
    public static String getHashKey(Context context) {
        final String TAG = "KeyHash";
        String keyHash = null;
        try {
            PackageInfo info =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = new String(Base64.encode(md.digest(), 0));
                Log.d(TAG, keyHash);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
        if (keyHash != null) {
            return keyHash;
        } else {
            return null;
        }
    }
    //////////////
    private void setWebView() {
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);  // 웹뷰 자바스크립트 사용 여부
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);  // window.open 사용 가능 여부
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
        webView.getSettings().setTextZoom(100);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                WebDialog webDialog = new WebDialog();
                dialog = webDialog.alertDialog(MainActivity.this, message, result);
                dialog.show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                WebDialog webDialog = new WebDialog();
                dialog = webDialog.confirmDialog(MainActivity.this, message, result);
                dialog.show();
                return true;
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                final WebView newWebView = new WebView(MainActivity.this);
                WebSettings webSettings = newWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        Log.e("newWindow_url", url);   // 여기서 화면터치 url받아옴
                        newWebView.setVisibility(View.GONE);
                        if (url.startsWith("mailto:")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            return true;
                        } else if (!url.startsWith(getString(R.string.domain))) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            return true;
                        } else {
                            Intent intent = new Intent(MainActivity.this, SubWebActivity.class);
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
        String userAgent = new WebView(getBaseContext()).getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(userAgent + " gh_mobile{"
                + FirebaseInstanceId.getInstance().getToken() + "}");
        Log.e("token", FirebaseInstanceId.getInstance().getToken());
        webView.loadUrl(getString(R.string.domain));
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
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
                    gps = new GpsInfo(MainActivity.this);
                    if (!gps.isGetLocation()) {
                        try {
//                gps.showSettingsAlert();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

                }
            });
        }
    }

    private void setGuide() {
        guide = (FrameLayout) findViewById(R.id.guide);

        if (pref.getBoolean("GuideCheck", false) == false) {
            setGuideFragment();
        } else {
            guide.setVisibility(View.GONE);
        }
    }

    public void setGuideFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.guide, new GuideFragment(onClickListener, checkedChangeListener));
        fragmentTransaction.commit();
    }   // setGuideFragment END

    // guide 프래그먼트 click event
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cancel:
                    guide.setVisibility(View.GONE);
                    break;
                case R.id.startBtn:
                    guide.setVisibility(View.GONE);
                    break;
                case R.id.check:
                    break;
            }
        }
    };

    // guide checkbox event
    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            Log.e("checkbox", b + "");
            if (b) {
                prefEd.putBoolean("GuideCheck", true);
            } else {
                prefEd.putBoolean("GuideCheck", false);
            }
            prefEd.commit();
        }
    };

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(MainActivity.this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        } else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            setResult(999);
            finish();
        }
    }
}
