package com.hapramp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hapramp.R;
import com.hapramp.adapters.CategoryRecyclerAdapter;
import com.hapramp.api.DataServer;
import com.hapramp.fragments.CompetitionFragment;
import com.hapramp.fragments.EarningFragment;
import com.hapramp.fragments.HomeFragment;
import com.hapramp.fragments.ProfileFragment;
import com.hapramp.fragments.SettingsFragment;
import com.hapramp.interfaces.FetchUserCallback;
import com.hapramp.models.response.FetchUserResponse;
import com.hapramp.preferences.HaprampPreferenceManager;
import com.hapramp.utils.FontManager;
import com.hapramp.views.CreateButtonView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements FetchUserCallback, CreateButtonView.ItemClickListener {


    @BindView(R.id.search_icon)
    TextView searchIcon;
    @BindView(R.id.bottomBar_home)
    TextView bottomBarHome;
    @BindView(R.id.bottomBar_competition)
    TextView bottomBarCompetition;
    @BindView(R.id.createNewBtn)
    CreateButtonView createButtonView;
    @BindView(R.id.bottomBar_profile)
    TextView bottomBarProfile;
    @BindView(R.id.bottomBar_settings)
    TextView bottomBarSettings;
    @BindView(R.id.notification_icon)
    TextView notificationIcon;
    @BindView(R.id.notification_count)
    TextView notificationCount;
    @BindView(R.id.notification_container)
    RelativeLayout notificationContainer;
    @BindView(R.id.action_bar_container)
    RelativeLayout actionBarContainer;

    CategoryRecyclerAdapter categoryRecyclerAdapter;
    @BindView(R.id.contentPlaceHolder)
    FrameLayout contentPlaceHolder;
    @BindView(R.id.bottomBar_home_text)
    TextView bottomBarHomeText;
    @BindView(R.id.bottomBar_competition_text)
    TextView bottomBarCompetitionText;
    @BindView(R.id.bottomBar_profile_text)
    TextView bottomBarProfileText;
    @BindView(R.id.bottomBar_settings_text)
    TextView bottomBarSettingsText;

    private final int BOTTOM_MENU_HOME = 7;
    private final int BOTTOM_MENU_COMP = 8;
    private final int BOTTOM_MENU_PROFILE = 9;
    private final int BOTTOM_MENU_SETTINGS = 10;
    private final int BOTTOM_MENU_EARNINGS = 11;
    private int lastMenuSelection = BOTTOM_MENU_HOME;

    private final int FRAGMENT_HOME = 12;
    private final int FRAGMENT_COMPETITION = 13;
    private final int FRAGMENT_PROFILE = 14;
    private final int FRAGMENT_SETTINGS = 15;
    private final int FRAGMENT_EARNINGS = 16;

    private Typeface materialTypface;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private CompetitionFragment competitionFragment;
    private ProfileFragment profileFragment;
    private SettingsFragment settingsFragment;
    private EarningFragment earningFragment;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setupToolbar();
        initObjects();
        attachListeners();

        if(!HaprampPreferenceManager.getInstance().isUserInfoAvailable()) {
            fetchCompleteUserInfo();
        }else{
            transactFragment(FRAGMENT_HOME);
        }

    }

    private void initObjects() {

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        competitionFragment = new CompetitionFragment();
        profileFragment = new ProfileFragment();
        settingsFragment = new SettingsFragment();
        earningFragment = new EarningFragment();
        progressDialog = new ProgressDialog(this);

    }

    private void setupToolbar() {

        materialTypface = FontManager.getInstance().getTypeFace(FontManager.FONT_MATERIAL);
        searchIcon.setTypeface(materialTypface);
        notificationIcon.setTypeface(materialTypface);
        bottomBarHome.setTypeface(materialTypface);
        bottomBarCompetition.setTypeface(materialTypface);
        bottomBarProfile.setTypeface(materialTypface);
        bottomBarSettings.setTypeface(materialTypface);

    }

    private void attachListeners() {

        bottomBarHome.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // check for the current selection
                if (lastMenuSelection == BOTTOM_MENU_HOME)
                    return;
                swapSelection(BOTTOM_MENU_HOME);
                transactFragment(FRAGMENT_HOME);
            }
        });


        bottomBarCompetition.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lastMenuSelection == BOTTOM_MENU_EARNINGS)
                    return;
                swapSelection(BOTTOM_MENU_EARNINGS);
                transactFragment(FRAGMENT_EARNINGS);
            }
        });


        bottomBarProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lastMenuSelection == BOTTOM_MENU_PROFILE)
                    return;
                swapSelection(BOTTOM_MENU_PROFILE);
                transactFragment(FRAGMENT_PROFILE);
            }
        });


        bottomBarSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lastMenuSelection == BOTTOM_MENU_SETTINGS)
                    return;
                swapSelection(BOTTOM_MENU_SETTINGS);
                transactFragment(FRAGMENT_SETTINGS);
            }
        });


        createButtonView.setItemClickListener(this);

        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, NotificationsActivity.class));
            }
        });

    }

    private void transactFragment(int fragment) {

        switch (fragment) {
            case FRAGMENT_HOME:

                fragmentManager.beginTransaction()
                        .replace(R.id.contentPlaceHolder, homeFragment)
                        .commit();

                break;
            case FRAGMENT_COMPETITION:

                fragmentManager.beginTransaction()
                        .replace(R.id.contentPlaceHolder, competitionFragment)
                        .commit();

                break;
            case FRAGMENT_PROFILE:

                fragmentManager.beginTransaction()
                        .replace(R.id.contentPlaceHolder, profileFragment)
                        .commit();

                break;
            case FRAGMENT_SETTINGS:

                fragmentManager.beginTransaction()
                        .replace(R.id.contentPlaceHolder, settingsFragment)
                        .commit();

                break;

            case FRAGMENT_EARNINGS:
                fragmentManager.beginTransaction()
                        .replace(R.id.contentPlaceHolder, earningFragment)
                        .commit();
                break;
            default:
                break;
        }
    }

    private void swapSelection(int newSelectedMenu) {
        if (newSelectedMenu == lastMenuSelection)
            return;
        resetLastSelection(lastMenuSelection);
        switch (newSelectedMenu) {
            case BOTTOM_MENU_HOME:
                bottomBarHome.setTextColor(Color.parseColor("#FF6B95"));
                bottomBarHomeText.setTextColor(Color.parseColor("#FF6B95"));
                lastMenuSelection = BOTTOM_MENU_HOME;

                break;
            case BOTTOM_MENU_COMP:
                bottomBarCompetition.setTextColor(Color.parseColor("#FF6B95"));
                bottomBarCompetitionText.setTextColor(Color.parseColor("#FF6B95"));
                lastMenuSelection = BOTTOM_MENU_COMP;

                break;
            case BOTTOM_MENU_PROFILE:
                bottomBarProfile.setTextColor(Color.parseColor("#FF6B95"));
                bottomBarProfileText.setTextColor(Color.parseColor("#FF6B95"));
                lastMenuSelection = BOTTOM_MENU_PROFILE;

                break;

            case BOTTOM_MENU_SETTINGS:
                bottomBarSettings.setTextColor(Color.parseColor("#FF6B95"));
                bottomBarSettingsText.setTextColor(Color.parseColor("#FF6B95"));
                lastMenuSelection = BOTTOM_MENU_SETTINGS;

                break;

            case BOTTOM_MENU_EARNINGS:

                bottomBarCompetition.setTextColor(Color.parseColor("#FF6B95"));
                bottomBarCompetitionText.setTextColor(Color.parseColor("#FF6B95"));
                lastMenuSelection = BOTTOM_MENU_EARNINGS;

                break;

            default:
                break;
        }
    }

    private void resetLastSelection(int lastMenuSelection) {

        switch (lastMenuSelection) {
            case BOTTOM_MENU_HOME:
                bottomBarHome.setTextColor(Color.parseColor("#818080"));
                bottomBarHomeText.setTextColor(Color.parseColor("#818080"));
                break;
            case BOTTOM_MENU_COMP:
                bottomBarCompetition.setTextColor(Color.parseColor("#818080"));
                bottomBarCompetitionText.setTextColor(Color.parseColor("#818080"));
                break;
            case BOTTOM_MENU_PROFILE:
                bottomBarProfile.setTextColor(Color.parseColor("#818080"));
                bottomBarProfileText.setTextColor(Color.parseColor("#818080"));
                break;
            case BOTTOM_MENU_SETTINGS:
                bottomBarSettings.setTextColor(Color.parseColor("#818080"));
                bottomBarSettingsText.setTextColor(Color.parseColor("#818080"));
                break;
            case BOTTOM_MENU_EARNINGS:
                bottomBarCompetition.setTextColor(Color.parseColor("#818080"));
                bottomBarCompetitionText.setTextColor(Color.parseColor("#818080"));
                break;
            default:
                break;
        }
    }

    private void fetchCompleteUserInfo() {
        showProgress("Getting Your Account...");
        DataServer.fetchUser(this);
    }

    @Override
    public void onUserFetched(FetchUserResponse userResponse) {

        hideProgress();
        HaprampPreferenceManager.getInstance().setUserInfoAvailable(true);
        HaprampPreferenceManager.getInstance().setUser(new Gson().toJson(userResponse));
        HaprampPreferenceManager.getInstance().setLoggedIn(true);
        HaprampPreferenceManager.getInstance().setUserId(String.valueOf(userResponse.id));
        HaprampPreferenceManager.getInstance().setUserEmail(userResponse.email);

        if (userResponse.organization == null) {
            redirectToOrgsPage();
        } else {
            if (userResponse.skills.size() == 0) {
                redirectToSkillsPage();
            } else {
                // Everything is set!
                // transact
                transactFragment(FRAGMENT_HOME);
            }
        }

    }


    private void redirectToOrgsPage() {
        hideProgress();
        Intent intent = new Intent(this, OrganisationActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectToSkillsPage() {
        hideProgress();
        Intent intent = new Intent(this, SkillRegistrationActivity.class);
        startActivity(intent);
        finish();
    }

    private void showProgress(String msg) {
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    private void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onUserFetchedError() {

        hideProgress();
        Toast.makeText(this, "User Fetched Error!", Toast.LENGTH_LONG).show();
        hideProgress();
    }

    @Override
    public void onUserNotExists() {

    }

    @Override
    public void onCreateArticleButtonClicked() {
        Intent intent = new Intent(this, CreateArticleActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreatePostButtonClicked() {
        Intent intent = new Intent(this, NewPostCreationActivity.class);
        startActivity(intent);
    }
}

