package de.gerogerke.android.lensico.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.gerogerke.android.lensico.R;
import de.gerogerke.android.lensico.android.recycler.ImageCardsAdapter;
import de.gerogerke.android.lensico.instagramapi.InstagramAPI;
import de.gerogerke.android.lensico.instagramapi.wrapper.InstagramMedium;
import de.gerogerke.android.lensico.instagramapi.wrapper.SimpleInstagramUser;
import de.gerogerke.android.lensico.instagramapi.wrapper.TaskUpdateCallback;

/**
 * Created by Deutron on 26.12.2015.
 */
public class TabInstagramUser extends Fragment implements IUserTab {

    SimpleInstagramUser user;
    InstagramAPI api;
    InstagramAPI.OAuthDialogListener oAuthListener;

    @Bind(R.id.swiperefresh_recycler)
    SwipeRefreshLayout mSwipeRefreshRecycler;

    @Bind(R.id.recyclerview_image_cards)
    RecyclerView mRecyclerView;

    public static TabInstagramUser newInstance(SimpleInstagramUser muser) {
        TabInstagramUser f = new TabInstagramUser();
        Bundle args = new Bundle();
        args.putSerializable("user", muser);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater mInflater, @Nullable ViewGroup mContainer, @Nullable Bundle savedInstanceState) {
        final View mView = mInflater.inflate(R.layout.content_lensico, mContainer, false);
        ButterKnife.bind(this, mView);

        user = (SimpleInstagramUser) getArguments().getSerializable("user");

        mSwipeRefreshRecycler.setColorSchemeColors(R.color.colorAccent, R.color.colorAccent);

        oAuthListener = new InstagramAPI.OAuthDialogListener() {
            @Override
            public void onComplete(final String accessToken) {

            }

            @Override
            public void onError(String error) {

            }
        };

        api = InstagramAPI
                .with(getActivity());

        if(api.getToken() == null) {
            api.connect(oAuthListener);
        }

        setRefreshListener(mSwipeRefreshRecycler);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        updateRecycler();

        return mView;
    }

    public void updateRunning(final boolean running, final SwipeRefreshLayout layout) {
        layout.post(new Runnable() {
            @Override
            public void run() {
                layout.setRefreshing(running);
            }
        });
    }

    private void setRefreshListener(SwipeRefreshLayout srl) {
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRecycler();
            }
        });
    }

    public void updateRecycler() {
        updateRunning(true, mSwipeRefreshRecycler);
        api.getConnection().getAllMediaImages(user.id, new TaskUpdateCallback() {
            @Override
            public void onSuccess(Object object) {
                List<InstagramMedium> media = (List<InstagramMedium>) object;
                if(media.size() > 0) {
                    mSwipeRefreshRecycler.setVisibility(View.VISIBLE);
                    mRecyclerView.setAdapter(new ImageCardsAdapter(getActivity(), media));
                    mRecyclerView.invalidate();
                } else {
                    mSwipeRefreshRecycler.setVisibility(View.GONE);
                }
            }
        });
        updateRunning(false, mSwipeRefreshRecycler);
    }

    @Override
    public String getTitle() {
        return user != null ? user.uName : "Error";
    }
}
