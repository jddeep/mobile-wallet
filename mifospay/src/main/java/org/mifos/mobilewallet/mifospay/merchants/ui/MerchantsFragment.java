package org.mifos.mobilewallet.mifospay.merchants.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.mifos.mobilewallet.core.data.fineract.entity.accounts.savings.SavingsWithAssociations;
import org.mifos.mobilewallet.mifospay.R;
import org.mifos.mobilewallet.mifospay.base.BaseActivity;
import org.mifos.mobilewallet.mifospay.base.BaseFragment;
import org.mifos.mobilewallet.mifospay.merchants.MerchantsContract;
import org.mifos.mobilewallet.mifospay.merchants.adapter.MerchantsAdapter;
import org.mifos.mobilewallet.mifospay.merchants.presenter.MerchantsPresenter;
import org.mifos.mobilewallet.mifospay.utils.Constants;
import org.mifos.mobilewallet.mifospay.utils.Toaster;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantsFragment extends BaseFragment implements MerchantsContract.MerchantsView {

    @Inject
    MerchantsPresenter mPresenter;
    MerchantsContract.MerchantsPresenter mMerchantsPresenter;

    @Inject
    MerchantsAdapter mMerchantsAdapter;

    @BindView(R.id.inc_state_view)
    View vStateView;
    @BindView(R.id.rv_merchants)
    RecyclerView mRvMerchants;
    @BindView(R.id.iv_empty_no_transaction_history)
    ImageView ivTransactionsStateIcon;

    @BindView(R.id.tv_empty_no_transaction_history_title)
    TextView tvTransactionsStateTitle;

    @BindView(R.id.merchant_fragment_layout)
    View mMerchantFragmentLayout;

    @BindView(R.id.tv_empty_no_transaction_history_subtitle)
    TextView tvTransactionsStateSubtitle;

    @BindView(R.id.pb_merchants)
    ProgressBar mMerchantProgressBar;
    private List<SavingsWithAssociations> merchantsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_merchants, container, false);
        ButterKnife.bind(this, rootView);
        mPresenter.attachView(this);
        mMerchantsPresenter.fetchMerchants();
        setupUi();
        return rootView;
    }

    private void setupUi() {
        setUpSwipeRefreshLayout();
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {

        mRvMerchants.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvMerchants.setAdapter(mMerchantsAdapter);
    }

    private void setUpSwipeRefreshLayout() {
        setSwipeEnabled(true);
        getSwipeRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSwipeRefreshLayout().setRefreshing(false);
                mPresenter.fetchMerchants();
            }
        });
    }

    @Override
    public void showEmptyStateView() {
        mMerchantFragmentLayout.setVisibility(View.GONE);
        mMerchantProgressBar.setVisibility(View.GONE);
        if (getActivity() != null) {
            vStateView.setVisibility(View.VISIBLE);
            Resources res = getResources();
            ivTransactionsStateIcon
                    .setImageDrawable(res.getDrawable(R.drawable.ic_merchants));
            tvTransactionsStateTitle
                    .setText(res.getString(R.string.empty_no_merchants_title));
            tvTransactionsStateSubtitle
                    .setText(res.getString(R.string.empty_no_merchants_subtitle));
        }
    }

    @Override
    public void showMerchants() {
        mMerchantFragmentLayout.setVisibility(View.VISIBLE);
        vStateView.setVisibility(View.GONE);
        mMerchantProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(MerchantsContract.MerchantsPresenter presenter) {
        mMerchantsPresenter = presenter;
    }

    @Override
    public void listMerchantsData(List<SavingsWithAssociations> savingsWithAssociationsList) {
        merchantsList = savingsWithAssociationsList;
        mMerchantsAdapter.setData(savingsWithAssociationsList);
    }

    @Override
    public void fetchMerchantsError() {
        hideProgressDialog();
        showToast(Constants.ERROR_FETCHING_MERCHANTS);
    }

    @Override
    public void showToast(String message) {
        Toaster.showToast(getContext(), message);
    }

    @Override
    public void showMerchantFetchProcess() {
        mMerchantFragmentLayout.setVisibility(View.GONE);
        vStateView.setVisibility(View.GONE);
        mMerchantProgressBar.setVisibility(View.VISIBLE);
    }
}
