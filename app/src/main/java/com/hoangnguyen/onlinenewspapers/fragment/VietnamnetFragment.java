package com.hoangnguyen.onlinenewspapers.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.hoangnguyen.onlinenewspapers.R;
import com.hoangnguyen.onlinenewspapers.activity.NewsDetailActivity;
import com.hoangnguyen.onlinenewspapers.adapter.NewsAdapter;
import com.hoangnguyen.onlinenewspapers.commom.Utils;
import com.hoangnguyen.onlinenewspapers.config.LinkRSS;
import com.hoangnguyen.onlinenewspapers.model.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VietnamnetFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTopHeadLine;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private List<News> mListNews;
    private NewsAdapter mNewsAdapter;
    private static String mPath;
    private LoadDataAsyncTask mLoadDataAsyncTask;
    private Fragment mFragment;
    private RelativeLayout mErrorLayout;
    private Button mButtonRetry;

    //    public VietnamnetFragment(String mPath) {
//        this.mPath = mPath;
//    }
    public static VietnamnetFragment newInstance(String mPath) {
        Bundle args = new Bundle();
        VietnamnetFragment.mPath = mPath;
        args.putString("id", mPath);
        VietnamnetFragment f = new VietnamnetFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vietnamnet_fragment, container, false);

        //mProgressBar = view.findViewById(R.id.progressbar);

        mRecyclerView = view.findViewById(R.id.rv_news);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        setHasOptionsMenu(true);


        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mErrorLayout = view.findViewById(R.id.errorLayout);
        mButtonRetry = view.findViewById(R.id.btn_retry);

        loadData();
        mButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline(getContext()) == true) {
                    mErrorLayout.setVisibility(View.GONE);
                    mFragment = VietnamnetFragment.newInstance(mPath);
                    loadFragment(mFragment);
                } else {
                    Toast.makeText(getActivity(), "Không có kết nối internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public void loadData() {
        if (Utils.isOnline(getContext()) == false) {
            mRecyclerView.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            if (mLoadDataAsyncTask != null) {
                mLoadDataAsyncTask.cancel(true);
            }
            mLoadDataAsyncTask = new LoadDataAsyncTask(mPath);
            mLoadDataAsyncTask.execute();
        }

    }


    private void listener() {
        mNewsAdapter.setOnItemClickListener(new NewsAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                News news = mListNews.get(position);
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("Url", news.getmUrl());
                intent.putExtra("Title", news.getmTitle());
                intent.putExtra("UrlToImage", news.getmUrlToImage());
                intent.putExtra("PubDate", news.getmPubDate());
                intent.putExtra("Source", news.getmSource());
                intent.putExtra("Author", news.getmAuthor());
                intent.putExtra("Description", news.getmDescription());
                intent.putExtra("Time", news.getmTime());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        mNewsAdapter.clear();
        loadData();
    }

    public class LoadDataAsyncTask extends AsyncTask<Void, Void, List<News>> {
        private String mPath;

        public LoadDataAsyncTask(String mPath) {
            this.mPath = mPath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mSwipeRefreshLayout.setRefreshing(true);
            mRecyclerView.setVisibility(View.GONE);
            mListNews = new ArrayList<>();
        }

        @Override
        protected List<News> doInBackground(Void... voids) {
            String mTitle;
            String mDescription;
            String mUrl;
            String mUrlToImage;
            String mPubDate;
            try {
                Document document = Jsoup.connect(mPath).get();
                Elements elements = document.getElementsByTag("item");
                for (int i = 0; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    mTitle = element.getElementsByTag("title").first().text();
                    mUrl = element.getElementsByTag("link").first().text();
                    mPubDate = element.getElementsByTag("pubDate").first().text();
                    mDescription = element.getElementsByTag("description").first().text();

                    Element element1 = element.getElementsByTag("media:content").first();
                    mUrlToImage = Utils.getLinkImage(String.valueOf(element1));
//                    String s = String.valueOf(element1);
//                    Log.d("aaa", mUrlToImage);
                    News news = new News();
                    news.setmTitle(mTitle);
                    news.setmDescription(mDescription);
                    news.setmUrl(mUrl);
                    news.setmUrlToImage(mUrlToImage);
                    news.setmPubDate(Utils.DateFormat(mPubDate));
                    news.setmSource(LinkRSS.VIETNAMNET_LINK);
                    news.setmAuthor(LinkRSS.VIETNAMNET_SOURCE);
                    news.setmTime(Utils.DateToTimeFormat(mPubDate));
                    Log.d("mmm", String.valueOf(news.getmTime()));
                    mListNews.add(news);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mListNews;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);

            mRecyclerView.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            mNewsAdapter = new NewsAdapter(mListNews, getContext());
            mRecyclerView.setAdapter(mNewsAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            listener();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_news, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chinh_tri:
                mFragment =  VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_CHINH_TRI);
                loadFragment(mFragment);
                return true;
            case R.id.thoi_su:
                mFragment =  VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_THOI_SU);
                loadFragment(mFragment);
                return true;
            case R.id.kinh_doanh:
                mFragment =  VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_KINH_DOANH);
                loadFragment(mFragment);
                return true;
            case R.id.giai_tri:
                mFragment =  VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_GIAI_TRI);
                loadFragment(mFragment);
                return true;
            case R.id.the_gioi:
                mFragment = VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_THE_GIOI);
                loadFragment(mFragment);
                return true;
            case R.id.the_thao:
                mFragment =  VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_THE_THAO);
                loadFragment(mFragment);
                return true;
            case R.id.cong_nghe:
                mFragment = VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_CONG_NGHE);
                loadFragment(mFragment);
                return true;
            case R.id.suc_khoe:
                mFragment = VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_SUC_KHOE);
                loadFragment(mFragment);
                return true;
            case R.id.doi_song:
                mFragment = VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_DOI_SONG);
                loadFragment(mFragment);
                return true;
            case R.id.phap_luat:
                mFragment = VietnamnetFragment.newInstance(LinkRSS.VIETNAMNET_PHAP_LUAT);
                loadFragment(mFragment);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }
}
