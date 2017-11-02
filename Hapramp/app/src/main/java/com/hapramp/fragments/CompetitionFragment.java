package com.hapramp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hapramp.CategoryRecyclerAdapter;
import com.hapramp.CompetitionRecyclerAdapter;
import com.hapramp.R;
import com.hapramp.api.DataServer;
import com.hapramp.interfaces.CompetitionFetchCallback;
import com.hapramp.interfaces.FetchSkillsResponse;
import com.hapramp.logger.L;
import com.hapramp.models.response.CompetitionResponse;
import com.hapramp.models.response.SkillsModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompetitionFragment extends Fragment implements CompetitionFetchCallback, CategoryRecyclerAdapter.OnCategoryItemClickListener, FetchSkillsResponse {


    @BindView(R.id.competitionRv)
    RecyclerView comptetionRv;
    Unbinder unbinder;
    @BindView(R.id.emptyMessage)
    TextView emptyMessage;
    @BindView(R.id.contentLoadingProgress)
    ProgressBar contentLoadingProgress;
    @BindView(R.id.sectionsRv)
    RecyclerView sectionsRv;
    @BindView(R.id.categoryLoadingProgress)
    ProgressBar categoryLoadingProgress;

    private CompetitionRecyclerAdapter recyclerAdapter;
    private Context mContext;
    private int category;
    private CategoryRecyclerAdapter categoryRecyclerAdapter;

    public CompetitionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerAdapter = new CompetitionRecyclerAdapter(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.competition_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initCategoryView();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comptetionRv.setLayoutManager(new LinearLayoutManager(mContext));
        comptetionRv.setAdapter(recyclerAdapter);
        fetchCategories();
        fetchCompetitions(-1);

    }

    private void initCategoryView() {

        categoryRecyclerAdapter = new CategoryRecyclerAdapter(mContext, this);
        sectionsRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        sectionsRv.setAdapter(categoryRecyclerAdapter);

    }

    @Override
    public void onCategoryClicked(int id) {
        L.D.m("Category", " clicked");
        fetchCompetitions(id);
    }

    private void fetchCategories() {
        showCategoryLoadingProgress();
        DataServer.fetchSkills(this);
    }

    @Override
    public void onSkillsFetched(List<SkillsModel> skillsModels) {
        hideCategoryLoadingProgress();
        categoryRecyclerAdapter.setCategories(skillsModels);
    }

    @Override
    public void onSkillFetchError() {
        hideCategoryLoadingProgress();
    }


    private void showContentLoadingProgress() {
        if (contentLoadingProgress != null)
            contentLoadingProgress.setVisibility(View.VISIBLE);
    }

    private void hideContentLoadingProgress() {
        if (contentLoadingProgress != null)
            contentLoadingProgress.setVisibility(View.GONE);
    }

    private void showCategoryLoadingProgress() {
        if (categoryLoadingProgress != null)
            categoryLoadingProgress.setVisibility(View.VISIBLE);
    }

    private void hideCategoryLoadingProgress() {
        if (categoryLoadingProgress != null)
            categoryLoadingProgress.setVisibility(View.GONE);
    }

    private void fetchCompetitions(int id) {

        hideErrorMessage();
        hideContent();
        showContentLoadingProgress();

        if (id == -1) {
            DataServer.getCompetitions(this);
        } else {
            DataServer.getCompetitionsBySkills(id, this);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCompetitionsFetched(List<CompetitionResponse> competitionResponses) {
        hideContentLoadingProgress();
        if (competitionResponses.size() > 0) {
            hideErrorMessage();
            showContent();
            recyclerAdapter.setCompetitionResponses(competitionResponses);
        } else {
            showErrorMessage();
        }

    }

    private void showContent() {
        if (comptetionRv != null)
            comptetionRv.setVisibility(View.VISIBLE);
    }

    private void hideContent() {

        if (comptetionRv != null)
            comptetionRv.setVisibility(View.GONE);
    }

    private void showErrorMessage() {
        if (emptyMessage != null)
            emptyMessage.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        if (emptyMessage != null)
            emptyMessage.setVisibility(View.GONE);
    }


    @Override
    public void onCompetitionsFetchError() {
        hideContentLoadingProgress();
        Toast.makeText(mContext, "Error Loading Content. Inconvienience is regreted :(", Toast.LENGTH_LONG).show();
        L.D.m("CompFragment", "Fetch Error: Post");
    }
}
