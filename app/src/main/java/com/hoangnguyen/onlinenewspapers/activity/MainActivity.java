package com.hoangnguyen.onlinenewspapers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.hoangnguyen.onlinenewspapers.R;
import com.hoangnguyen.onlinenewspapers.config.LinkRSS;
import com.hoangnguyen.onlinenewspapers.fragment.BookmarkFragment;
import com.hoangnguyen.onlinenewspapers.fragment.TuoitreFragment;
import com.hoangnguyen.onlinenewspapers.fragment.DanvietFragment;
import com.hoangnguyen.onlinenewspapers.fragment.TwentyFourFragment;
import com.hoangnguyen.onlinenewspapers.fragment.VietnamnetFragment;
import com.hoangnguyen.onlinenewspapers.fragment.VnExpressFragment;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private LinearLayout mLnVietnamnet, mLnVnExpress, mLnTuoitre, mLnDanviet, mLn24h, mLnBookmark;
    private Fragment mFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.hoangnguyen.onlinenewspapers",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

        init();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        mLnVietnamnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_MOI_NHAT);
                loadFragment(mFragment);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                getSupportActionBar().setTitle("vietnamnet.vn");

            }
        });

        mLnVnExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = VnExpressFragment.newInstance(LinkRSS.VNEXPRESS_MOI_NHAT);
                loadFragment(mFragment);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                getSupportActionBar().setTitle("vnexpress.net");
            }
        });
        mLnTuoitre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = TuoitreFragment.newInstance(LinkRSS.TUOITRE_TRANG_CHU);
                loadFragment(mFragment);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                getSupportActionBar().setTitle("tuoitre.vn");
            }
        });
        mLn24h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = TwentyFourFragment.newInstance(LinkRSS._24H_TRANG_CHU);
                loadFragment(mFragment);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                getSupportActionBar().setTitle("24h.com.vn");
            }
        });
        mLnDanviet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = DanvietFragment.newInstance(LinkRSS.DANVIET_TRANG_CHU);
                loadFragment(mFragment);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                getSupportActionBar().setTitle("danviet.vn");
            }
        });
        mLnBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = BookmarkFragment.newInstance("news");
                loadFragment(mFragment);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                getSupportActionBar().setTitle("Tin đã lưu");
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        builder.setIcon(R.drawable.ic_notification);
//        builder.setTitle("Thông báo");
//        builder.setMessage("Bạn có muốn thoát không?");
//        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });
//        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.show();
//    }

    public void init() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mLnVietnamnet = findViewById(R.id.vietnamnet);
        mLnVnExpress = findViewById(R.id.vnexpress);
        mLnDanviet = findViewById(R.id.danviet);
        mLn24h = findViewById(R.id.twentyfour);
        mLnTuoitre = findViewById(R.id.tuoitre);
        mLnBookmark = findViewById(R.id.bookmark);
        mFragment = VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_MOI_NHAT);
        loadFragment(mFragment);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        getSupportActionBar().setTitle("vietnamnet.vn");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
}