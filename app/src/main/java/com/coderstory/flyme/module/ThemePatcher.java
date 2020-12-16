package com.coderstory.flyme.module;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.coderstory.flyme.plugins.IModule;
import com.coderstory.flyme.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ThemePatcher extends XposedHelper implements IModule {


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        // 主题和谐
        if (lpparam.packageName.equals("com.meizu.customizecenter") && prefs.getBoolean("enabletheme", false)) {
            if (lpparam.packageName.equals("com.meizu.customizecenter")) {
                // 拦截开机自启广播
                findAndHookMethod("com.meizu.customizecenter.admin.receiver.BootBroadcastReceiver", lpparam.classLoader, "onReceive", Context.class, Intent.class, XC_MethodReplacement.returnConstant(null));

                // 拦截试用服务
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.FontTrialService", lpparam.classLoader, "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.ThemeTrialService", lpparam.classLoader, "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));

                //device_states | doCheckState
                //8.7.1
                hookAllConstructors("com.meizu.customizecenter.manager.utilstool.a.c", lpparam.classLoader, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        findAndHookMethod("com.meizu.customizecenter.manager.utilstool.a.c", lpparam.classLoader, "e", Context.class, XC_MethodReplacement.returnConstant(0));
                    }
                });
                //findAndHookMethod("com.meizu.customizecenter.manager.utilstool.a.c", lpparam.classLoader, "e", Context.class, XC_MethodReplacement.returnConstant(0));
                // hookAllMethods("com.meizu.customizecenter.manager.utilstool.conversionutils.g", lpparam.classLoader, "a", new XC_MethodHook() {
                //     @Override
                //     protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                //         super.beforeHookedMethod(param);
                //         if (param.args.length > 2 && "doCheckState".equals(param.args[1])) {
                //             param.setResult(0);
                //         }
                //     }
                // });
                findAndHookMethod("com.meizu.net.lockscreenlibrary.manager.utilstool.baseutils.Utility", lpparam.classLoader, "isRoot", Context.class, XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.meizu.statsapp.v3.lib.plugin.f.b", lpparam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(false));

                // com.meizu.advertise.plugin
                findAndHookMethod("com.meizu.advertise.api.AdManager", lpparam.classLoader, "install", XC_MethodReplacement.returnConstant(null));

                //resetToSystemTheme
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.common.b", lpparam.classLoader, "c", XC_MethodReplacement.returnConstant(true));
                //findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.theme.common.b", lpparam.classLoader, "b", XC_MethodReplacement.returnConstant(true));

                /**
                 *
                 public void a(boolean arg4, boolean arg5) {
                 com.meizu.customizecenter.manager.utilstool.c.b.b(this.b, "startFontRestoreService");
                 Intent v0 = new Intent(this.d, FontRestoreService.class);
                 v0.putExtra(com.meizu.customizecenter.model.a.a$g.h.a(), this.e().a());
                 v0.putExtra("is_go_to_pay_font", arg5);
                 v0.putExtra("is_restore_last_key", arg4);
                 com.meizu.customizecenter.manager.utilstool.systemutills.a.a.a(this.d, v0);
                 }
                 */
                // 7.5
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.k", lpparam.classLoader, "a", Context.class, String.class, long.class, XC_MethodReplacement.returnConstant(null));

                /**
                 *  public void k() {
                 *         long v0 = SystemClock.elapsedRealtime() - this.n();
                 *         com.meizu.customizecenter.manager.utilstool.c.b.e(this.b, "checkTrialFontWhenAppStart_interval:" + v0 / 1000 + " second");
                 *         if(!this.f(v0)) {
                 *             this.g();
                 *         }
                 *     }
                 */
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.g", lpparam.classLoader, "k", XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.k", lpparam.classLoader, "k", XC_MethodReplacement.returnConstant(null));

                //"checkTrialFont:!isUsingTrialFont() Context context, String str, long j
                findAndHookMethod("com.meizu.customizecenter.manager.managermoduls.font.k", lpparam.classLoader, "a", Context.class, String.class, long.class, XC_MethodReplacement.returnConstant(null));

                Class<?> themeContentProvider = findClass("com.meizu.customizecenter.manager.utilshelper.dbhelper.dao.ThemeContentProvider", lpparam.classLoader);
                //主题混搭 ThemeContentProvider query Unknown URI
                findAndHookMethod(themeContentProvider, "query", Uri.class, String[].class, String.class, String[].class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        Object[] objs = param.args;
                        String Tag = "(ITEMS LIKE";
                        String Tag2 = "%zklockscreen;%";
                        String Tag3 = "%com.meizu.flyme.weather;%";
                        boolean result = false;
                        for (Object obj : objs) {
                            if (obj instanceof String && (((String) obj).contains(Tag) || obj.equals(Tag2) || obj.equals(Tag3))) {
                                result = true;
                            }
                        }
                        if (result) {
                            for (Object obj : objs) {
                                if (obj instanceof String[]) {
                                    for (int j = 0; j < ((String[]) obj).length; j++) {
                                        if (((String[]) obj)[j].contains("/storage/emulated/0/Customize/Themes")) {
                                            ((String[]) obj)[j] = "/storage/emulated/0/Customize%";
                                        } else if (((String[]) obj)[j].contains("/storage/emulated/0/Customize/TrialThemes")) {
                                            ((String[]) obj)[j] = "NONE";
                                        }
                                    }
                                }
                            }
                        }
                        super.beforeHookedMethod(param);
                    }
                });
            }
        }
    }


    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {

    }


}