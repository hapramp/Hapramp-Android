package com.hapramp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hapramp.R;
import com.hapramp.models.Feed;
import com.hapramp.models.ProfileHeaderModel;
import com.hapramp.models.response.PostResponse;
import com.hapramp.views.post.PostItemView;
import com.hapramp.views.profile.ProfileHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ankit on 10/25/2017.
 */

public class PostsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_BLANK_TOP = 0;
    private final int VIEW_TYPE_ITEM = 2;
    private final int VIEW_TYPE_SHIMMER = 3;
    private final int VIEW_TYPE_PROFILE_HEADER = 1;

    public Context mContext;
    private boolean hasMoreToLoad = true;
    public List<Feed> postResponses;
    public ProfileHeaderModel profileHeaderModel;
    private boolean isAdapterForProfile;
    private int s;
    private int defaultListSize;

    public PostsRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
        postResponses = new ArrayList<>();
    }

    public void setProfileHeaderModel(ProfileHeaderModel profileHeaderModel) {
        this.profileHeaderModel = profileHeaderModel;
        defaultListSize = 1; // for header
        notifyDataSetChanged();
    }

    public void setHasMoreToLoad(boolean hasMoreToLoad) {
        this.hasMoreToLoad = hasMoreToLoad;
    }

    public void setIsAdapterForProfile(boolean forProfile) {
        this.isAdapterForProfile = forProfile;
        defaultListSize = forProfile ? 0 : 1;
    }

    public boolean itIsForProfile() {
        return isAdapterForProfile;
    }

    public void appendResult(List<Feed> newPosts) {
        Log.d("PostAdapter", "Appended " + newPosts.size());
        postResponses.addAll(newPosts);
        notifyItemInserted(postResponses.size() - (newPosts.size() - 1));
    }


    public void setPosts(List<Feed> results) {
//        this.postResponses = results;
//        notifyItemRangeInserted(0, results.size());
    }

    public void clearList() {
        postResponses.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {

        if (itIsForProfile()) {
            if (position == 0) {
                //   Log.d("Adapter", "profile header");
                return VIEW_TYPE_PROFILE_HEADER;
            }
        } else {
            // for non-profile we have blank views at top
            if (position == 0) {
                //Log.d("Adapter", "blank");
                return VIEW_TYPE_BLANK_TOP;
            }
        }

        return (position > postResponses.size()) ? VIEW_TYPE_SHIMMER : VIEW_TYPE_ITEM;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == VIEW_TYPE_ITEM) {

            View view = new PostItemView(mContext);
            viewHolder = new PostViewHolder(view);

        } else if (viewType == VIEW_TYPE_SHIMMER) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.load_more_progress_view, null);
            viewHolder = new LoadMoreViewHolder(view);

        } else if (viewType == VIEW_TYPE_PROFILE_HEADER) {

            View view = new ProfileHeaderView(mContext);
            viewHolder = new ProfileHeaderViewHolder(view);

        } else if (viewType == VIEW_TYPE_BLANK_TOP) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.blank_view, null);
            viewHolder = new BlankTopViewHolder(view);
        }

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int pos) {

        //  Log.d("Adapter", "Binding at " + pos);
        if (viewHolder instanceof LoadMoreViewHolder) {
            ///Log.d("Adapter", "Binding LoadMore at " + pos);
            if (hasMoreToLoad) {
                ((LoadMoreViewHolder) viewHolder).startSimmer();
            } else {
                ((LoadMoreViewHolder) viewHolder).hideView();
            }

        } else if (viewHolder instanceof PostViewHolder) {

            // Log.d("Adapter", "Binding Post at " + pos);
            ((PostViewHolder) viewHolder).bind(postResponses.get(pos - 1));

        } else if (viewHolder instanceof ProfileHeaderViewHolder) {

            ((ProfileHeaderViewHolder) viewHolder).bind(profileHeaderModel);

        } else if (viewHolder instanceof BlankTopViewHolder) {
            // do
            //Log.d("Adapter", "Binding Blank at " + pos);
            ((BlankTopViewHolder) viewHolder).blank.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {

        // since we have additional item at the top + one at the bottom
        s = postResponses.size();

        return s == 0 ? defaultListSize : s + 1;

    }

    private int getListSizeDefault() {
        // for home fragment  - 1 space band so : 1
        // for profile - initially it will be 0, after it loads header, it will be 1
        return defaultListSize;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        PostItemView postItemView;

        public PostViewHolder(View itemView) {
            super(itemView);
            postItemView = (PostItemView) itemView;
        }

        public void bind(final Feed postData) {
            postItemView.setPostData(postData);
        }

    }

    class LoadMoreViewHolder extends RecyclerView.ViewHolder {

        ShimmerFrameLayout shimmerFrameLayout;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);

            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_view_container);

        }

        public void startSimmer() {

            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmerAnimation();

        }

        public void hideView() {
            shimmerFrameLayout.setVisibility(View.GONE);
        }

    }

    class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {

        ProfileHeaderView profileHeaderView;

        public ProfileHeaderViewHolder(View itemView) {
            super(itemView);
            profileHeaderView = (ProfileHeaderView) itemView;
        }


        public void bind(ProfileHeaderModel profileHeaderData) {

            profileHeaderView.setProfileHeaderData(profileHeaderData);

        }
    }

    class BlankTopViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.blank)
        FrameLayout blank;

        public BlankTopViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
