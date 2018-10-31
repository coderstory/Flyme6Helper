package com.coderstory.purify.module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.coderstory.purify.plugins.IModule;
import com.coderstory.purify.utils.XposedHelper;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FlymeRoot extends XposedHelper implements IModule {
    @SuppressLint("StaticFieldLeak")
    private static TextView WarningText;
    @SuppressLint("StaticFieldLeak")
    private static Button accept;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        if (loadPackageParam.packageName.equals("com.meizu.mznfcpay") && prefs.getBoolean("HideRootWithPay", false)) {
            findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "c", Context.class, XC_MethodReplacement.returnConstant(false));
        }
        if (loadPackageParam.packageName.equals("com.meizu.flyme.update") && prefs.getBoolean("HideRootWithUpgrade", false)) {
            // DEVICE_STATE_SERVICE
            findAndHookMethod("com.meizu.cloud.a.a.a", loadPackageParam.classLoader, "b", Context.class, XC_MethodReplacement.returnConstant(false));
        }


    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {

    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) {
    }
}