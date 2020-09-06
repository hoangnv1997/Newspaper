package com.hoangnguyen.onlinenewspapers.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hoangnguyen.onlinenewspapers.R;
import com.hoangnguyen.onlinenewspapers.activity.NewsDetailActivity;
import com.hoangnguyen.onlinenewspapers.adapter.BookmarkNewsAdapter;
import com.hoangnguyen.onlinenewspapers.adapter.NewsAdapter;
import com.hoangnguyen.onlinenewspapers.commom.Utils;
import com.hoangnguyen.onlinenewspapers.model.News;

import java.util.ArrayList;
import java.util.List;

public class BookmarkFragment extends Fragment {
    private DatabaseReference mDatabase;
    private List<News> mList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private BookmarkNewsAdapter mNewsAdapter;
    private View mView;
    private LoadDataAsynTask mLoadDataAsynTask;
    private static String mChild;
    private RelativeLayout mErrorLayout;
    private Button mButtonRetry;
    private Fragment mFragment;

    //    public BookmarkFragment(String mChild) {
//        this.mChild = mChild;
//    }
    public static BookmarkFragment newInstance(String mPath) {
        Bundle args = new Bundle();
        BookmarkFragment.mChild = mPath;
        args.putString("id", mPath);
        BookmarkFragment f = new BookmarkFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.vietnamnet_fragment, container, false);
        init();
        loadData();
        return mView;
    }

    private void init() {
        //mProgressBar = mView.findViewById(R.id.progressbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mList = new ArrayList<>();
        mSwipeRefreshLayout = mView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        mRecyclerView = mView.findViewById(R.id.rv_news);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        setHasOptionsMenu(true);
        mErrorLayout = mView.findViewById(R.id.errorLayout);
        mButtonRetry = mView.findViewById(R.id.btn_retry);
        mButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnline(getContext()) == true) {
                    mErrorLayout.setVisibility(View.GONE);
                    mFragment =  BookmarkFragment.newInstance(mChild);
                    loadFragment(mFragment);
                } else {
                    Toast.makeText(getActivity(), "Không có kết nối internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mNewsAdapter.clear();
                loadData();
            }
        });
    }


    public void loadData() {
        if (Utils.isOnline(getContext()) == false) {
            mRecyclerView.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.VISIBLE);
//            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            if (mLoadDataAsynTask != null) {
                mLoadDataAsynTask.cancel(true);
            }
            mLoadDataAsynTask = new LoadDataAsynTask(mChild);
            mLoadDataAsynTask.execute();
        }
    }

    public class LoadDataAsynTask extends AsyncTask<Void, List<News>, List<News>> {
        private String mChild;

        public LoadDataAsynTask(String mChild) {
            this.mChild = mChild;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // mProgressBar.setVisibility(View.VISIBLE);
            // mSwipeRefreshLayout.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(true);
            mRecyclerView.setVisibility(View.GONE);
        }

        @Override
        protected List<News> doInBackground(Void... voids) {
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    News news = snapshot.getValue(News.class);
                    mList.add(news);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            mDatabase.child(mChild).addChildEventListener(childEventListener);

            return mList;
        }

        @Override
        protected void onProgressUpdate(List<News>... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);
            // mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            //mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            mNewsAdapter = new BookmarkNewsAdapter(news, getContext());
            mRecyclerView.setAdapter(mNewsAdapter);
            listener();
        }
    }

    private void listener() {
        mNewsAdapter.setOnItemClickListener(new BookmarkNewsAdapter.ItemClickListener() {
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
                intent.putExtra("Time", news.getmTime());
                startActivity(intent);
            }
        });

    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bookmark, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                mDatabase.child("news").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Đã xóa tất cả tin khỏi Bookmark", Toast.LENGTH_SHORT).show();
                    }
                });
                mList.clear();
                mNewsAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
