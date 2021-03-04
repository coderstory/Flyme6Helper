package com.coderstory.flyme.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.coderstory.flyme.R;
import com.coderstory.flyme.adapter.AppInfo;
import com.coderstory.flyme.adapter.AppInfoAdapter;
import com.coderstory.flyme.fragment.base.BaseFragment;
import com.coderstory.flyme.view.PullToRefreshView;
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;

import per.goweii.anylayer.AnyLayer;
import per.goweii.anylayer.DialogLayer;
import per.goweii.anylayer.Layer;


public class HideAppFragment extends BaseFragment {
    private final List<AppInfo> appInfoList = new ArrayList<>();
    private final List<AppInfo> appInfoList2 = new ArrayList<>();
    private List<PackageInfo> packages = new ArrayList<>();
    private AppInfoAdapter adapter = null;
    private AppInfo appInfo = null;
    private int mPosition = 0;
    private View mView = null;
    private PullToRefreshView mPullToRefreshView;
    private List<String> hideAppList;
    private Dialog dialog;

    private void initData() {
        String list = getPrefs().getString("Hide_App_List", "");
        hideAppList = new ArrayList<>();
        hideAppList.addAll(java.util.Arrays.asList(list.split(":")));
        packages = new ArrayList<>();
        if (getContext() != null) {
            packages = getContext().getPackageManager().getInstalledPackages(0);
            initFruit();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_hideapp_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initFruit() {
        appInfoList.clear();
        appInfoList2.clear();
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (getContext() != null) {
                Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
                // 过来掉没启动器图标的app
                if (intent != null && !"com.coderstory.flyme".equals(packageInfo.packageName)) {
                    if (!hideAppList.contains(packageInfo.applicationInfo.packageName)) {
                        AppInfo appInfo = new AppInfo(packageInfo.applicationInfo.loadLabel(getContext().getPackageManager()).toString(), packageInfo.applicationInfo.loadIcon(getContext().getPackageManager()), packageInfo.packageName, false, String.valueOf(packageInfo.versionName));
                        appInfoList.add(appInfo);
                    } else {
                        AppInfo appInfo = new AppInfo(packageInfo.applicationInfo.loadLabel(getContext().getPackageManager()).toString(), packageInfo.applicationInfo.loadIcon(getContext().getPackageManager()), packageInfo.packageName, true, String.valueOf(packageInfo.versionName));
                        appInfoList2.add(appInfo);
                    }
                }
            }
        }
        appInfoList.addAll(appInfoList2);
    }

    private void showData() {
        adapter = new AppInfoAdapter(getContext(), R.layout.app_info_item, appInfoList);
        ListView listView = getContentView().findViewById(R.id.listView);
        assert listView != null;
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            mPosition = position;
            mView = view;
            appInfo = appInfoList.get(mPosition);
            Layer anyLayer = AnyLayer.dialog(getContext())
                    .contentView(R.layout.dialog_tdisable_app)
                    .cancelableOnTouchOutside(true)
                    .cancelableOnClickKeyBack(true)
                    .onClick((AnyLayer, v) -> {
                        AnyLayer.dismiss();
                    }, R.id.fl_dialog_no)
                    .onClick((AnyLayer, v) -> {
                        if (appInfo.getDisable()) {
                            // 解除隐藏
                            String tmp = "";
                            for (String s : hideAppList) {
                                if (s.equals(appInfo.getPackageName())) {
                                    tmp = s;
                                }
                            }
                            hideAppList.remove(tmp);
                        } else {
                            // 隐藏
                            hideAppList.add(appInfo.getPackageName());
                        }
                        StringBuilder value = new StringBuilder();
                        for (String s : hideAppList) {
                            value.append(s).append(":");
                        }
                        value = new StringBuilder(value.substring(0, value.length() - 1));

                        getEditor().putString("Hide_App_List", value.toString());
                        fix();
                        if (appInfo.getDisable()) {
                            appInfo.setDisable(false);
                            appInfoList.set(mPosition, appInfo);
                            mView.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null)); //正常的颜色
                        } else {
                            appInfo.setDisable(true);
                            appInfoList.set(mPosition, appInfo);
                            mView.setBackgroundColor(getResources().getColor(R.color.disableApp, null)); //冻结的颜色
                        }
                        AnyLayer.dismiss();
                    }, R.id.fl_dialog_yes);
            anyLayer.show();
            CardView cardView = (CardView) ((DialogLayer) anyLayer).getContentView();

            LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(0);
            TextView textView = (TextView) linearLayout.getChildAt(1);
            if (appInfo.getDisable()) {
                textView.setText(getString(R.string.sureAntiDisable) + appInfo.getName() + "的隐藏状态吗");

            } else {
                textView.setText("你确定要隐藏" + appInfo.getName() + getString(R.string.sureDisableAfter));
            }


        });
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_app_list;
    }

    @Override
    protected void init() {
        super.init();


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Toast.makeText(getActivity(), "本功能在Android 10及以上系统上暂时无效", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "点击应用切换 隐藏/显示 状态 【重启桌面生效】", Toast.LENGTH_LONG).show();
        }

        new MyTask().execute();

        mPullToRefreshView = getContentView().findViewById(R.id.pull_to_refresh);

        mPullToRefreshView.setOnRefreshListener(() -> mPullToRefreshView.postDelayed(() -> {
            initData();
            showData();
            adapter.notifyDataSetChanged();
            mPullToRefreshView.setRefreshing(false);
        }, 2000));
    }

    protected void showProgress() {
        if (dialog == null) {
            dialog = ProgressDialog.show(getContext(), getString(R.string.Tips_Title), getString(R.string.loadappinfo));
            dialog.show();
        }
    }

    //
    protected void closeProgress() {

        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_restrathome) {
            Layer anyLayer = AnyLayer.dialog(getContext())
                    .contentView(R.layout.dialog_tdisable_app)
                    .cancelableOnTouchOutside(true)
                    .cancelableOnClickKeyBack(true)
                    .onClick((AnyLayer, v) -> {
                        AnyLayer.dismiss();
                    }, R.id.fl_dialog_no)
                    .onClick((AnyLayer, v) -> {
                        Shell.su("killall com.android.systemui").exec();
                        Shell.su("am force-stop com.meizu.flyme.launcher").exec();
                        System.exit(0);
                    }, R.id.fl_dialog_yes);
            anyLayer.show();
            CardView cardView = (CardView) ((DialogLayer) anyLayer).getContentView();
            LinearLayout linearLayout = (LinearLayout) cardView.getChildAt(0);
            TextView textView = (TextView) linearLayout.getChildAt(1);
            textView.setText("是否重启Flyme桌面应用当前设置?");

        }

        return false;
    }

    @SuppressLint("StaticFieldLeak")
    class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

            showProgress();
        }

        @Override
        protected void onPostExecute(String param) {
            showData();

            adapter.notifyDataSetChanged();
            closeProgress();
        }


        @Override
        protected String doInBackground(String... params) {

            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            initData();
            return null;
        }
    }

}

