package com.example.weiduapp.fragment;

import android.content.pm.FeatureGroupInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lib_core.base.mvp.BasemvpFragment;
import com.example.lib_core.base.mvp.Basepresenter;
import com.example.weiduapp.R;
import com.example.weiduapp.activity.BottomActivity;
import com.example.weiduapp.activity.LoginActivity;
import com.example.weiduapp.adapter.HomeListAdapter;
import com.example.weiduapp.adapter.ShopAdapter;
import com.example.weiduapp.api.Api;
import com.example.weiduapp.bean.BannerBean;
import com.example.weiduapp.bean.HomeListBean;
import com.example.weiduapp.bean.LoginBean;
import com.example.weiduapp.bean.RegBean;
import com.example.weiduapp.bean.ShopBase;
import com.example.weiduapp.contract.ProductContract;
import com.example.weiduapp.presenter.ProductPresenter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BasemvpFragment<ProductContract.IProductModel,ProductContract.ProductPresentervoid> implements ProductContract.IProductView,HomeListAdapter.onClickLister{


    private Unbinder bind;
    @BindView(R.id.lv)
    RecyclerView lv;
    private HomeListAdapter homeListAdapter;
    private ShopAdapter shopAdapter;


    @Override
    protected int getViewId() {
        return R.layout.fragmenthome;
    }

    @Override
    protected void initView(View view) {
        bind = ButterKnife.bind(this, view);
        lv.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeListAdapter = new HomeListAdapter(getActivity());
        homeListAdapter.initOnClick(this);
        lv.setAdapter(homeListAdapter);
    }



    @Override
    public Basepresenter initPresenter() {
        return new ProductPresenter();
    }

    @Override
    protected void initpresenter() {
        presenter.getHomeList();
        presenter.getBannerList();
    }


    /**
     * 数据获取成功
     * @param homeListBean
     */
    @Override
    public void successHomeData(HomeListBean homeListBean) {
        homeListAdapter.setList(homeListBean.result,homeListBean.result.rxxp,homeListBean.result.pzsh,homeListBean.result.mlss);
    }
    @Override
    public void successBanner(BannerBean bannerBean) {
        homeListAdapter.setListBanner(bannerBean.getResult());
    }


    @Override
    public void failure(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bind!=null){
            bind.unbind();
        }
    }
    /**
     * 无用
     * @param regBean
     */
    @Override
    public void successreg(RegBean regBean) { }

    @Override
    public void successlogin(LoginBean loginBean) { }

    @BindView(R.id.hot)
    TextView hot;
    @BindView(R.id.style)
    TextView style;
    @BindView(R.id.live)
    TextView live;
    @BindView(R.id.xlv)
    XRecyclerView xlv;
    @BindView(R.id.shop_class1)
    ImageView shop_class;
    @BindView(R.id.et_seleceshop1)
    EditText et_seleceshop;
    @BindView(R.id.select1)
    TextView select;
    @BindView(R.id.Shop_Home)
    ConstraintLayout Shop_Home;
    @BindView(R.id.shop_view)
    ConstraintLayout shop_view;

    private int labelId;
    private int page = 1;
    private int count = 5;

    @Override
    public void onclivk(int positions) {
        if (!"".equals(positions+"")){
            labelId=positions;
            Shop_Home.setVisibility(View.GONE);
            shop_view.setVisibility(View.VISIBLE);
            if (positions==1002){
                hot.setVisibility(View.VISIBLE);
                style.setVisibility(View.GONE);
                live.setVisibility(View.GONE);
            }else if (positions==1003){
                style.setVisibility(View.VISIBLE);
                hot.setVisibility(View.GONE);
                live.setVisibility(View.GONE);
            }else if (positions==1004){
                live.setVisibility(View.VISIBLE);
                hot.setVisibility(View.GONE);
                style.setVisibility(View.GONE);
            }
        }
        initShopView();
        initShopData();
    }



    private void initShopView() {
        xlv.setLayoutManager(new GridLayoutManager(getActivity(),2));
        shopAdapter = new ShopAdapter(getActivity());
        xlv.setAdapter(shopAdapter);
        xlv.setPullRefreshEnabled(true);
        xlv.setLoadingMoreEnabled(true);
        xlv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page=1;
                initShopData();
            }

            @Override
            public void onLoadMore() {
                page++;
                initShopData();
            }
        });



    }
    private void initShopData() {

        if (presenter!=null){
            presenter.getShopList(Api.HOME_SHOP+"?labelId="+labelId+"&page="+page+"&count="+count);
        }

    }

    @Override
    public void successShop(ShopBase shopBase) {

        if (page==1){
            shopAdapter.setList(shopBase.result);
        }else{
            shopAdapter.addList(shopBase.result);
        }

        xlv.loadMoreComplete();
        xlv.refreshComplete();

    }

    @Override
    public boolean onKeyDown(KeyEvent event) {

        boolean ret = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                break;
            case KeyEvent.KEYCODE_BACK:
                if (Shop_Home.getVisibility()==View.GONE){
                    Shop_Home.setVisibility(View.VISIBLE);
                    shop_view.setVisibility(View.GONE);
                    page=1;
                    ret = true;
                }
                break;
        }
        return ret;
    }



}
