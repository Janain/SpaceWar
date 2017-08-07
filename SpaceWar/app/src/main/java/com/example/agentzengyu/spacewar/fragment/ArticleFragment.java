package com.example.agentzengyu.spacewar.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agentzengyu.spacewar.R;
import com.example.agentzengyu.spacewar.adapter.ArticleAdapter;
import com.example.agentzengyu.spacewar.adapter.IArticle;
import com.example.agentzengyu.spacewar.application.SpaceWarApp;
import com.example.agentzengyu.spacewar.entity.single.Article;

import java.util.List;

/**
 * Created by Agent ZengYu on 2017/8/7.
 */

public abstract class ArticleFragment extends Fragment implements View.OnClickListener, IArticle {
    protected SpaceWarApp app = null;
    protected Article playerArticle = null, upgradeArticle;

    protected LinearLayoutManager manager;
    protected ArticleAdapter adapter;
    protected RecyclerView recyclerView;
    protected ImageView mIvPlayer, mIvPrice;
    protected TextView mTvPlayerGrade, mTvPlayerName, mTvPlayerValue, mTvPrice;
    protected Button mBtnUpgrade;

    protected PopupWindow popupWindow;
    protected Button mBtnYes, mBtnNo;

    public ArticleFragment() {
    }

    /**
     * 初始化变量
     */
    protected void initVariable() {
        app = (SpaceWarApp) getActivity().getApplication();
        manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new ArticleAdapter(getActivity(), this);
    }

    /**
     * 初始化布局
     *
     * @param view
     */
    protected void initView(View view) {
        new PagerSnapHelper().attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(0);

        mIvPlayer = (ImageView) view.findViewById(R.id.ivPlayer);
        mIvPrice = (ImageView) view.findViewById(R.id.ivPrice);
        mTvPlayerGrade = (TextView) view.findViewById(R.id.tvPlayerGrade);
        mTvPlayerName = (TextView) view.findViewById(R.id.tvPlayerName);
        mTvPlayerValue = (TextView) view.findViewById(R.id.tvPlayerValue);
        mTvPrice = (TextView) view.findViewById(R.id.tvPrice);
        mBtnUpgrade = (Button) view.findViewById(R.id.btnUpgrade);
        mBtnUpgrade.setOnClickListener(this);

        popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
    }

    /**
     * 切换商品类型
     *
     * @param article
     * @param articles
     */
    protected void shiftArticle(Article article, List<Article> articles) {
        playerArticle = article;

        mIvPlayer.setImageResource(article.getImage());
        mTvPlayerGrade.setText("" + article.getGrade());
        mTvPlayerName.setText("" + article.getName());
        mTvPlayerValue.setText("" + article.getValue());

        adapter.update(articles);
        recyclerView.scrollToPosition(0);
    }

    /**
     * 升级
     */
    protected void upgrade() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.popupwindow_upgrade, null);
        mBtnYes = (Button) view.findViewById(R.id.btnYes);
        mBtnYes.setOnClickListener(this);
        mBtnNo = (Button) view.findViewById(R.id.btnNo);
        mBtnNo.setOnClickListener(this);

        popupWindow.setContentView(view);
        popupWindow.showAtLocation(mTvPrice, Gravity.CENTER, 0, 0);
        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        layoutParams.alpha = 0.2f;
        getActivity().getWindow().setAttributes(layoutParams);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
                layoutParams.alpha = 1f;
                getActivity().getWindow().setAttributes(layoutParams);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnYes:
                if (app.getPlayerData().getPlayer().getMoney() >= upgradeArticle.getPrice()) {
                    playerArticle = upgradeArticle;
                    app.getPlayerData().getPlayer().setMoney(app.getPlayerData().getPlayer().getMoney() - upgradeArticle.getPrice());
                    Toast.makeText(getContext(), R.string.popupwindow_upgrade_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.popupwindow_upgrade_fail, Toast.LENGTH_SHORT).show();
                }
            case R.id.btnNo:
                popupWindow.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void select(Article article) {
        upgradeArticle = article;
        mTvPrice.setText("" + article.getPrice());
        if (playerArticle.getGrade() < article.getGrade()) {
            mIvPrice.setImageResource(R.mipmap.ic_launcher);
            mBtnUpgrade.setClickable(true);
        } else {
            mIvPrice.setImageResource(R.mipmap.ic_launcher_round);
            mBtnUpgrade.setClickable(false);
        }
    }
}