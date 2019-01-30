package com.hapramp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hapramp.R;
import com.hapramp.models.LeaderboardModel;
import com.hapramp.preferences.HaprampPreferenceManager;
import com.hapramp.ui.activity.ProfileActivity;
import com.hapramp.utils.Constants;
import com.hapramp.utils.ImageHandler;
import com.hapramp.utils.PixelUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderItemViewHolder> {

  private ArrayList<LeaderboardModel.Winners> leaders;
  private String myUsername;

  public LeaderboardAdapter() {
    this.leaders = new ArrayList<>();
    myUsername = HaprampPreferenceManager.getInstance().getCurrentSteemUsername();
  }

  public void setLeaders(ArrayList<LeaderboardModel.Winners> leaders) {
    this.leaders = leaders;
    notifyDataSetChanged();
  }

  @NonNull
  @Override
  public LeaderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_item_view,
      parent,
      false);
    return new LeaderItemViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull LeaderItemViewHolder holder, int position) {
    holder.bind(leaders.get(position), position + 1);
  }

  @Override
  public int getItemCount() {
    return leaders.size();
  }

  class LeaderItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.rank)
    TextView rank;
    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.earning)
    TextView earning;
    @BindView(R.id.first_rank_count)
    TextView firstRankCount;
    @BindView(R.id.second_rank_count)
    TextView secondRankCount;
    @BindView(R.id.third_rank_count)
    TextView thirdRankCount;
    @BindView(R.id.winning_details_container)
    RelativeLayout winningDetailsContainer;
    private boolean isExpaned = false;
    private int fixedHeight = PixelUtils.dpToPx(64);
    private int ANIMATION_DURATION = 300;

    public LeaderItemViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void bind(final LeaderboardModel.Winners winner, int pos) {
      earning.setText(String.format("$%s", winner.getmScore()));
      username.setText(winner.getmAuthor());
      rank.setText(String.format("%d", pos));
      ImageHandler.loadCircularImage(avatar.getContext(), avatar, winner.avatarUrl(avatar.getContext()));
      checkAndMakeMyRankOutstand(winner.getmAuthor());
      firstRankCount.setText(String.valueOf(winner.getRanks()[0]));
      secondRankCount.setText(String.valueOf(winner.getRanks()[1]));
      thirdRankCount.setText(String.valueOf(winner.getRanks()[2]));
      winningDetailsContainer.setVisibility(View.GONE);
      itemView.getLayoutParams().height = fixedHeight;
      isExpaned = false;
      itemView.requestLayout();
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          navigateToProfile(avatar.getContext(), winner.getmAuthor());
        }
      });
      earning.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (!isExpaned) {
            scaleHeightAndShowView();
            isExpaned = true;
          } else {
            scaleHeightAndHideView();
            isExpaned = false;
          }
        }
      });
      winningDetailsContainer.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          scaleHeightAndHideView();
          isExpaned = false;
        }
      });
    }

    private void checkAndMakeMyRankOutstand(String leader) {
      if (leader.equals(myUsername)) {
        avatar.setBackgroundResource(R.drawable.leader_avatar_border);
      } else {
        avatar.setBackgroundResource(0);
      }
    }

    private void navigateToProfile(Context context, String username) {
      Intent intent = new Intent(context, ProfileActivity.class);
      intent.putExtra(Constants.EXTRAA_KEY_STEEM_USER_NAME, username);
      context.startActivity(intent);
    }

    private void scaleHeightAndShowView() {
      HeightScaleAnimation animation = new HeightScaleAnimation();
      int px = PixelUtils.dpToPx(72);
      animation.setInterpolator(new DecelerateInterpolator(1.0f));
      animation.setConfig(itemView, fixedHeight, true, px);
      animation.setDuration(ANIMATION_DURATION);
      // animation.setFillAfter(true);
      animation.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
          winningDetailsContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
      itemView.startAnimation(animation);
    }

    private void scaleHeightAndHideView() {
      HeightScaleAnimation animation = new HeightScaleAnimation();
      animation.setDuration(ANIMATION_DURATION);
      int px = PixelUtils.dpToPx(72);
      animation.setInterpolator(new AccelerateInterpolator(1.0f));
      animation.setConfig(itemView, fixedHeight, false, px);
      //animation.setFillAfter(true);
      winningDetailsContainer.setVisibility(View.GONE);
      animation.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
      });
      itemView.startAnimation(animation);
    }

    class HeightScaleAnimation extends Animation {
      int fixedHeight = 0;
      int animationOffset = 0;
      boolean willShow;
      View view;

      public void setConfig(View view, int fixedHeight, boolean willShow, int animationOffset) {
        this.view = view;
        this.willShow = willShow;
        this.animationOffset = animationOffset;
        this.fixedHeight = fixedHeight;
      }

      @Override
      public boolean willChangeBounds() {
        return true;
      }

      @Override
      protected void applyTransformation(float interpolatedTime, Transformation t) {
        int heightOffset;
        if (willShow) {
          heightOffset = (int) (animationOffset * interpolatedTime);
        } else {
          heightOffset = (int) (animationOffset * (1 - interpolatedTime));
        }
        float newHeight = fixedHeight + heightOffset;
        view.getLayoutParams().height = (int) newHeight;
        view.requestLayout();
      }
    }
  }
}

