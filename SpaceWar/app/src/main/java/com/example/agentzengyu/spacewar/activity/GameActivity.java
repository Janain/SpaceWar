package com.example.agentzengyu.spacewar.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.agentzengyu.spacewar.R;
import com.example.agentzengyu.spacewar.application.Constant;
import com.example.agentzengyu.spacewar.application.SpaceWarApp;
import com.example.agentzengyu.spacewar.entity.single.MapItem;
import com.example.agentzengyu.spacewar.view.CircleImageView;
import com.example.agentzengyu.spacewar.view.EnemyView;
import com.example.agentzengyu.spacewar.view.MapView;
import com.example.agentzengyu.spacewar.view.PlayerView;

/**
 * 游戏主界面
 */
public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getName();

    private MapView mapView;
    private PlayerView playerView;
    private EnemyView enemyView;
    private TextView mTvLife, mTvShield, mTvBomb, mTvMap, mTvNotify;
    private CircleImageView mCivShield, mCivBomb, mCivShot;

    private SpaceWarApp app = null;
    private MapReceiver mapReceiver;
    private PlayerReceiver playerReceiver;
    private EnemyReceiver enemyReceiver;
    private Handler handler = new Handler();
    private Runnable runnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        MapItem mapItem = (MapItem) getIntent().getSerializableExtra("MapItem");
        initView();
        initVariable();
        startGame(mapItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mapReceiver);
        unregisterReceiver(playerReceiver);
        unregisterReceiver(enemyReceiver);
        app.getGameService().stopGame();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        mapView = (MapView) findViewById(R.id.mvMap);
        playerView = (PlayerView) findViewById(R.id.pvPlayer);
        enemyView = (EnemyView) findViewById(R.id.evEnemy);
        mTvLife = (TextView) findViewById(R.id.tvLife);
        mTvShield = (TextView) findViewById(R.id.tvShield);
        mTvBomb = (TextView) findViewById(R.id.tvBomb);
        mTvMap = (TextView) findViewById(R.id.tvMap);
        mTvNotify = (TextView) findViewById(R.id.tvNotify);
        mCivShield = (CircleImageView) findViewById(R.id.civShield);
        mCivShield.setOnClickListener(this);
        mCivBomb = (CircleImageView) findViewById(R.id.civBomb);
        mCivBomb.setOnClickListener(this);
        mCivShot = (CircleImageView) findViewById(R.id.civShot);
        mCivShot.setOnClickListener(this);
    }

    /**
     * 初始化变量
     */
    private void initVariable() {
        app = (SpaceWarApp) getApplication();
        mapReceiver = new MapReceiver();
        playerReceiver = new PlayerReceiver();
        enemyReceiver = new EnemyReceiver();
        IntentFilter mapFilter = new IntentFilter(Constant.Game.Type.MAP);
        registerReceiver(mapReceiver, mapFilter);
        IntentFilter playerFilter = new IntentFilter(Constant.Game.Type.PLAYER);
        registerReceiver(playerReceiver, playerFilter);
        IntentFilter enemyFilter = new IntentFilter(Constant.Game.Type.ENEMY);
        registerReceiver(enemyReceiver, enemyFilter);
        runnable = new Runnable() {
            @Override
            public void run() {
                mTvNotify.setVisibility(View.GONE);
            }
        };
    }

    /**
     * 开始游戏
     *
     * @param mapItem 地图
     */
    private void startGame(MapItem mapItem) {
        mTvLife.setText("" + app.getPlayerData().getLife().getValue());
        mTvShield.setText("" + app.getPlayerData().getShield().getValue());
        mTvBomb.setText("" + app.getPlayerData().getBomb().getValue());
//        if (mapItem != null) {
//            mTvMap.setText(mapItem.getName());
        app.getGameService().startGame(mapItem);
//        } else {

//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.civShield:

                break;
            case R.id.civBomb:

                break;
            case R.id.civShot:

                break;
            default:
                break;
        }
    }

    //地图接收器
    public class MapReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra(Constant.BroadCast.STATE);
            Log.e(TAG, state);
            switch (state) {

            }
        }
    }

    //玩家接收器
    public class PlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra(Constant.BroadCast.STATE);
            Log.e(TAG, ">>> " + state);
            switch (state) {
                case Constant.Game.Type.NOTIFY:
                    String msg = intent.getStringExtra(Constant.Game.Type.NOTIFY);
                    boolean status = intent.getBooleanExtra(Constant.Game.Type.STATUS, false);
                    mTvNotify.setText("" + msg);
                    if (status) {
                        handler.postDelayed(runnable, 1000);
                    }
                    break;
                case Constant.Game.Player.AGILITY:
                    int agility = intent.getIntExtra(Constant.Game.Player.AGILITY, 100);
                    playerView.setAgility(agility);
                    break;
                case Constant.Game.Player.LEFT:
                    playerView.onLeft();
                    break;
                case Constant.Game.Player.RIGHT:
                    playerView.onRight();
                    break;
                case Constant.Game.Player.TOP:
                    playerView.onTop();
                    break;
                case Constant.Game.Player.BOTTOM:
                    playerView.onBottom();
                    break;
                case Constant.Game.Player.SHIELD_OPEN:
                    boolean open = intent.getBooleanExtra(Constant.Game.Player.SHIELD_OPEN, false);
                    playerView.shield(open);
                    break;
                case Constant.Game.Player.SHIELD_CLOSE:
                    boolean close = intent.getBooleanExtra(Constant.Game.Player.SHIELD_CLOSE, false);
                    playerView.shield(close);
                    break;
                case Constant.Game.Player.DESTROY:
                    playerView.destroy();
                    break;
                default:
                    break;
            }
        }
    }

    //敌人接收器
    public class EnemyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra(Constant.BroadCast.STATE);
            Log.e(TAG, state);
            switch (state) {

            }
        }
    }
}
