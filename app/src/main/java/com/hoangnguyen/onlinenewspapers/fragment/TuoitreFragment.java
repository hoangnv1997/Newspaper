package com.hoangnguyen.onlinenewspapers.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class TuoitreFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private TextView mTvTopHeadLine;
    private Fragment mFragment;
    private NewsAdapter mNewsAdapter;
    private List<News> mList;
    private LoadDataAsyncTask mLoadDataAsyncTask;
    private String mPath;
    private RelativeLayout mErrorLayout;
    private Button mButtonRetry;

    public TuoitreFragment(String mPath) {
        this.mPath = mPath;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vietnamnet_fragment, container, false);

        mProgressBar = view.findViewById(R.id.progressbar);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mNewsAdapter.clear();
                loadData();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mRecyclerView = view.findViewById(R.id.rv_news);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        setHasOptionsMenu(true);
        mErrorLayout = view.findViewById(R.id.errorLayout);
        mButtonRetry = view.findViewById(R.id.btn_retry);
        loadData();
        mButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline(getContext()) == true) {
                    mErrorLayout.setVisibility(View.GONE);
                    mFragment = new TuoitreFragment(mPath);
                    loadFragment(mFragment);
                } else {
                    Toast.makeText(getActivity(), "Không có kết nối internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public class LoadDataAsyncTask extends AsyncTask<Void, Void, List<News>> {
        private String mPath;

        public LoadDataAsyncTask(String mPath) {
            this.mPath = mPath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);

            mRecyclerView.setVisibility(View.GONE);
            mList = new ArrayList<>();

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
                    mDescription = "";
                    mUrl = element.getElementsByTag("link").first().text();
                    mPubDate = element.getElementsByTag("pubDate").first().text();

                    Element element1 = element.getElementsByTag("description").first();
                    mUrlToImage = Utils.getLinkImage(String.valueOf(element));

                    News news = new News();
                    news.setmUrl(mUrl);
                    news.setmTitle(mTitle);
                    news.setmDescription(mDescription);
                    news.setmPubDate(mPubDate);
                    news.setmUrlToImage(mUrlToImage);
                    news.setmSource(LinkRSS.TUOITRE_LINK);
                    news.setmAuthor(LinkRSS.TUOITRE_SOURCE);
                    mList.add(news);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mList;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mNewsAdapter = new NewsAdapter(mList, getContext());
            mRecyclerView.setAdapter(mNewsAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            listener();
        }
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
                News news = mList.get(position);
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("Url", news.getmUrl());
                intent.putExtra("Title", news.getmTitle());
                intent.putExtra("UrlToImage", news.getmUrlToImage());
                intent.putExtra("PubDate", news.getmPubDate());
                intent.putExtra("Source", news.getmSource());
                intent.putExtra("Author", news.getmAuthor());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_news_tuoitre, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.giao_duc:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_GIAO_DUC);
                loadFragment(mFragment);
                return true;
            case R.id.thoi_su:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_THOI_SU);
                loadFragment(mFragment);
                return true;
            case R.id.kinh_doanh:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_KINH_DOANH);
                loadFragment(mFragment);
                return true;
            case R.id.giai_tri:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_GIAI_TRI);
                loadFragment(mFragment);
                return true;
            case R.id.the_gioi:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_THE_GIOI);
                loadFragment(mFragment);
                return true;
            case R.id.the_thao:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_THE_THAO);
                loadFragment(mFragment);
                return true;
            case R.id.cong_nghe:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_CONG_NGHE);
                loadFragment(mFragment);
                return true;
            case R.id.suc_khoe:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_SUC_KHOE);
                loadFragment(mFragment);
                return true;
            case R.id.doi_song:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_DOI_SONG);
                loadFragment(mFragment);
                return true;
            case R.id.phap_luat:
                mFragment = new TuoitreFragment(LinkRSS.TUOITRE_PHAP_LUAT);
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
