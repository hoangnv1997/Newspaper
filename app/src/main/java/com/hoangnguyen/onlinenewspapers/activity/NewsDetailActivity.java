package com.hoangnguyen.onlinenewspapers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hoangnguyen.onlinenewspapers.R;
import com.hoangnguyen.onlinenewspapers.commom.Utils;
import com.hoangnguyen.onlinenewspapers.config.LinkRSS;
import com.hoangnguyen.onlinenewspapers.model.News;

public class NewsDetailActivity extends AppCompatActivity {
    private ImageView mImageView;
    private TextView mTvAppBarTitle, mTvAppBarSubTitle, mTvTitle, mTvTime, mTvDate;
    private boolean isHideToolbarView = false;
    private Intent mIntent;
    private ShareDialog mShareDialog;
    private ShareLinkContent mShareLinkContent;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceNews;
    private String mUrl, mTitle, mUrlToImage, mPubDate, mSource, mAuthor, mDescription, mTime;
    private WebView mWebView;
    private Boolean mIsAddNews;
    private String mKey;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        init();
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        getDataFromFragment();

    }

    private void getDataFromFragment() {
        mIntent = getIntent();
        mUrl = mIntent.getStringExtra("Url");
        mTitle = mIntent.getStringExtra("Title");
        mUrlToImage = mIntent.getStringExtra("UrlToImage");
        mPubDate = mIntent.getStringExtra("PubDate");
        mSource = mIntent.getStringExtra("Source");
        mAuthor = mIntent.getStringExtra("Author");
        mDescription = mIntent.getStringExtra("Description");
        mTime = mIntent.getStringExtra("Time");
//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.error(Utils.getRandomDrawbleColor());
//
//        Glide.with(this)
//                .load(mUrlToImage)
//                .apply(requestOptions)
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .into(mImageView);
        mTvAppBarTitle.setText(mSource);
        mTvAppBarSubTitle.setText(mUrl);
//        mTvTitle.setText(mTitle);
        // mTvTime.setText(LinkRSS.VIETNAMNET_SOURCE + " \u2022 " + Utils.DateToTimeFormat(mPubDate));
        // mTvDate.setText(Utils.DateFormat(mPubDate));
        initWebView(mUrl);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(final String url) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            //            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                mWebView.setVisibility(View.GONE);
//                mProgressBar.setVisibility(View.VISIBLE);
//                 //Toast.makeText(NewsDetailActivity.this, "onPageStarted", Toast.LENGTH_SHORT).show();
//            }
//
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mWebView.loadUrl(url);
                mWebView.reload();
            }
        });

    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceNews = mDatabase.getReference("news");
        mIsAddNews = false;
        mWebView = findViewById(R.id.webView);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        //mImageView = findViewById(R.id.backdrop);
        mTvAppBarTitle = findViewById(R.id.title_on_appbar);
        mTvAppBarSubTitle = findViewById(R.id.subtitile_on_appbar);
        //mTvTitle = findViewById(R.id.title_on_cardview);
        //mTvTime = findViewById(R.id.time_on_cardview);
        //mTvDate = findViewById(R.id.tv_date);
        final LinearLayout linearLayout = findViewById(R.id.ln_title_appbar);
        mShareDialog = new ShareDialog(this);
        Toolbar toolbar = findViewById(R.id.toolbar_news_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        //collapsingToolbarLayout.setTitle("");

        //AppBarLayout appBarLayout = findViewById(R.id.app_bar_layout);
        //appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                int maxScroll = appBarLayout.getTotalScrollRange();
//                float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
//
//                if (percentage == 1f && isHideToolbarView) {
//                    mTvDate.setVisibility(View.GONE);
//                    linearLayout.setVisibility(View.VISIBLE);
//                    isHideToolbarView = !isHideToolbarView;
//                } else if (percentage < 1f && isHideToolbarView) {
//                    mTvDate.setVisibility(View.VISIBLE);
//                    linearLayout.setVisibility(View.GONE);
//                    isHideToolbarView = !isHideToolbarView;
//                }
//            }
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_detail_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.bookmark:
                if (mIsAddNews == false) {
                    mKey = mReferenceNews.push().getKey();
                    News news = new News(mTitle, mDescription, mUrl, mUrlToImage, mPubDate, mAuthor, mSource, mKey, mTime);
                    mReferenceNews.child(mKey).setValue(news).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NewsDetailActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                            mIsAddNews = true;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewsDetailActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                            mIsAddNews = false;
                        }
                    });
                } else if (mIsAddNews == true) {
                    mReferenceNews.child(mKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NewsDetailActivity.this, "Đã xóa khỏi Bookmark", Toast.LENGTH_SHORT).show();
                            mIsAddNews = false;
                        }
                    });
                }

                return true;

            case R.id.share:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    mShareLinkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(mIntent.getStringExtra("Url")))
                            .build();
                }
                mShareDialog.show(mShareLinkContent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.mWebView.canGoBack()) {
            this.mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}