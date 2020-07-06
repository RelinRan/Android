package com.android.app.page;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.R;
import com.android.annotation.ViewUtils;
import com.android.app.AppConstant;
import com.android.app.BaseApplication;
import com.android.app.manager.PermissionsManager;
import com.android.app.mode.LoadingMode;
import com.android.app.mode.ToastMode;
import com.android.app.proxy.ContentViewProxy;
import com.android.app.proxy.ExceptionViewPageProxy;
import com.android.app.proxy.LoadingViewSwipeProxy;
import com.android.app.proxy.NavigationBarViewProxy;
import com.android.app.proxy.ToastViewProxy;
import com.android.app.view.ContentView;
import com.android.app.view.ExceptionView;
import com.android.app.view.LoadingView;
import com.android.app.view.NavigationBarView;
import com.android.app.view.ToastView;
import com.android.image.ImageSelector;
import com.android.image.OnImageSelectListener;
import com.android.net.HttpResponse;
import com.android.net.NetworkUtils;
import com.android.net.OnHttpListener;
import com.android.utils.DataStorage;
import com.android.utils.StatusBar;

import java.util.Map;

/**
 * Created by Relin on 2015/12/test.
 * Fragment的基础类
 * Change this code when you add other method or module
 * Note:In the RelativeLayout have three Layout , first Layout is Activity ContentViewProxy
 * second Layout  can show LoadingView three can show NoNetWorkView.
 */

public abstract class BaseFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback, PermissionsManager.OnRequestPermissionsListener, OnHttpListener, OnImageSelectListener {

    /**
     * Fragment 最外层容器
     */
    private View content_view;

    /**
     * Fragment 除了标题栏的内容
     */
    private FrameLayout base_container_view;

    /**
     * 自定义标题
     */
    private FrameLayout defineNavigationBarView;

    /**
     * 设置是否需要进入动画
     */
    private boolean isStartAnimation = true;

    /**
     * 设置是否需要退出动画
     */
    private boolean isFinishAnimation = true;

    /**
     * 错误视图、加载视图的创建工具
     */
    private NavigationBarView navigationBarView;

    /**
     * 页面布局转换器
     */
    private LayoutInflater inflater;

    /**
     * 图片选择构建者
     */
    private ImageSelector imageSelector;

    /**
     * 提示文字构建器
     */
    private ToastView toastView;

    /**
     * 加载视图 - 接口
     */
    private LoadingView loadingView;

    /**
     * 异常视图 - 接口
     */
    private ExceptionView exceptionView;

    /**
     * 内容区域 - 接口
     */
    private ContentView contentView;

    /**
     * 页面信息
     */
    private PageMessage pageMessage;

    /**
     * onResume次数
     */
    private int onResumeCount = 0;

    private FrameLayout navigationBar;
    private ImageView navigationImage;
    private TextView navigationText;
    private TextView navigationTitle;
    private ImageView navigationMenuImage;
    private TextView navigationMenuText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = setContentView();
        navigationBarView = setNavigationBarView();
        toastView = setToastView();
        exceptionView = setExceptionView();
        exceptionView = setExceptionView();
        loadingView = setLoadingView();
        View layout = inflater.inflate(R.layout.android_af_base, container, false);
        initControlView(inflater, layout);
        this.inflater = inflater;
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onPrepare();
        onHttpRequest();
    }

    /**
     * 准备工作
     */
    protected void onPrepare() {
        ViewUtils.inject(this, getView());
        pageMessage = new PageMessage(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (onResumeCount >= 1) {
            onRelive();
        }
        onResumeCount++;
    }

    /**
     * 重进页面
     */
    public void onRelive() {

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        onActivityArguments(args);
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    protected void setStatusBarColor(int color) {
        if (getActivity() == null) {
            return;
        }
        StatusBar.setTranslucent(getActivity());
        StatusBar.setColor(getActivity(), getResources().getColor(color));
        StatusBar.setFontColor(getActivity(), getResources().getColor(color) == Color.WHITE);
    }

    /**
     * 桥梁参数
     *
     * @param obj
     */
    public void onActivityArguments(Object obj) {

    }

    /**
     * 自定义标题布局
     *
     * @return
     */
    protected int setNavigationBarLayoutById() {
        return 0;
    }

    /**
     * 在Xml里面设置
     *
     * @return
     */
    protected int setNavigationBarViewById() {
        return 0;
    }

    /**
     * 设置Fragment本身布局
     **/
    protected abstract int setContentLayoutById();

    /**
     * 设置Fragment内容布局
     **/
    protected int setFragmentContainerViewById() {
        return 0;
    }

    /**
     * 设置页面视图
     * 如果需要自定义加载动画、错误视图就继承BaseViewJack重写对应的方法就行了，
     * 重写方法的时候注释掉父类的over的代码
     *
     * @return
     */
    protected NavigationBarView setNavigationBarView() {
        return new NavigationBarViewProxy();
    }

    /**
     * 设置页面Toast
     * 重定义Toast显示页面，只需要继承ToastJack实现对应的方法即可
     *
     * @return
     */
    protected ToastView setToastView() {
        return new ToastViewProxy();
    }

    /**
     * 设置数据加载视图
     *
     * @return
     */
    protected LoadingView setLoadingView() {
        return new LoadingViewSwipeProxy();
    }

    /**
     * 设置数据加载失败视图
     *
     * @return
     */
    protected ExceptionView setExceptionView() {
        return new ExceptionViewPageProxy();
    }

    /**
     * 设置导航栏可见性
     *
     * @param visibility
     */
    public void setNavigationBarVisibility(int visibility) {
        navigationBarView.setNavigationBarVisibility(visibility, defineNavigationBarView);
    }

    /**
     * 设置内容区域
     *
     * @return
     */
    public ContentView setContentView() {
        return new ContentViewProxy();
    }

    /**
     * 是否判断网络
     *
     * @return
     */
    protected boolean isDetermineNetwork() {
        return BaseApplication.app.isDetermineNetwork();
    }

    /**
     * 请求接口数据
     **/
    public void onHttpRequest() {
        if (isDetermineNetwork() && !NetworkUtils.isAvailable(getActivity())) {
            Log.i(this.getClass().getSimpleName(), AppConstant.HTTP_MSG_NET_OFFLINE);
            Message message = pageMessage.obtainMessage();
            message.what = AppConstant.WHAT_MSG_NET_OFFLINE;
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.MSG_KEY, AppConstant.EXCEPTION_MSG_NET_OFFLINE);
            message.setData(bundle);
            pageMessage.sendMessage(message);
            return;
        }
        dismissExceptionDialog();
        dismissLoadingDialog();
    }

    @Override
    public void onHttpSucceed(HttpResponse response) {
        dismissExceptionDialog();
        dismissLoadingDialog();
        ContentViewProxy contentViewProxy = (ContentViewProxy) contentView;
        if (contentViewProxy != null) {
            contentViewProxy.setHttpResponse(response);
        }
    }

    @Override
    public void onHttpFailure(HttpResponse response) {
        dismissLoadingDialog();
        dismissExceptionDialog();
        ContentViewProxy contentViewProxy = (ContentViewProxy) contentView;
        if (contentViewProxy != null) {
            contentViewProxy.setHttpResponse(response);
        }
        //检查网络是否可用
        Message message = pageMessage.obtainMessage();
        if (!NetworkUtils.isAvailable(getContext())) {
            Log.i(this.getClass().getSimpleName(), AppConstant.HTTP_MSG_NET_OFFLINE);
            message.what = AppConstant.WHAT_MSG_NET_OFFLINE;
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.MSG_CODE, response.code());
            bundle.putString(AppConstant.MSG_KEY, AppConstant.EXCEPTION_MSG_NET_OFFLINE);
            message.setData(bundle);
        } else {//显示其他错误信息
            message.what = AppConstant.WHAT_MSG_RESPONSE_FAILED;
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.MSG_CODE, response.code());
            bundle.putString(AppConstant.MSG_KEY, AppConstant.EXCEPTION_MSG_RESPONSE_FAILED);
            message.setData(bundle);
        }
        pageMessage.sendMessage(message);
    }

    /**
     * 初始化需要用到的控件
     */
    private void initControlView(LayoutInflater inflater, View layout) {
        content_view = inflater.inflate(setContentLayoutById(), null);
        content_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        base_container_view = (FrameLayout) layout.findViewById(R.id.base_container_view);
        defineNavigationBarView = (FrameLayout) layout.findViewById(R.id.define_title);
        //=======设置内容=======
        contentView.onCreateContentView(getContext(), inflater, base_container_view, setContentLayoutById(), 1, this.getClass().getSimpleName());
        //=======设置标题栏=====
        navigationBarView.onCreateNavigationBarView(inflater, base_container_view, defineNavigationBarView, setNavigationBarViewById(), setNavigationBarLayoutById());
        //=====错误视图==========
        exceptionView.onCreateExceptionView(this, inflater, base_container_view);
        //导航栏
        navigationBar = layout.findViewById(R.id.android_nav_item);
        navigationImage = layout.findViewById(R.id.android_iv_nav_back);
        navigationText = layout.findViewById(R.id.android_tv_nav_left);
        navigationTitle = layout.findViewById(R.id.android_tv_nav_title);
        navigationMenuImage = layout.findViewById(R.id.android_iv_nav_menu);
        navigationMenuText = layout.findViewById(R.id.android_tv_nav_menu);
        navigationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationImageClick(navigationImage);
            }
        });
        navigationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationTextClick(navigationText);
            }
        });
        navigationMenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationMenuImageClick(navigationMenuImage);
            }
        });
        navigationMenuText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationMenuTextClick(navigationMenuText);
            }
        });
        //默认隐藏导航栏
        navigationBar.setVisibility(View.GONE);
    }


    public void onNavigationImageClick(ImageView v) {

    }

    public void onNavigationTextClick(TextView v) {

    }

    public void onNavigationMenuImageClick(ImageView v) {

    }

    public void onNavigationMenuTextClick(TextView v) {

    }

    public FrameLayout getNavigationBar() {
        return navigationBar;
    }

    public ImageView getNavigationImage() {
        return navigationImage;
    }

    public TextView getNavigationText() {
        return navigationText;
    }

    public TextView getNavigationTitle() {
        return navigationTitle;
    }

    public ImageView getNavigationMenuImage() {
        return navigationMenuImage;
    }

    public TextView getNavigationMenuText() {
        return navigationMenuText;
    }

    /**==============================加载LoadingView==============================**/

    /**
     * 显示加载对话框
     *
     * @param mode
     */
    public void showLoadingDialog(LoadingMode mode) {
        if (loadingView == null) {
            return;
        }
        loadingView.showLoadingView(inflater, base_container_view, mode, "");
    }

    /**
     * 显示加载对话框
     *
     * @param mode
     * @param toast 加载提示
     */
    public void showLoadingDialog(LoadingMode mode, String toast) {
        if (loadingView == null) {
            return;
        }
        loadingView.showLoadingView(inflater, base_container_view, mode, toast);
    }

    /**
     * 消失加载页面
     */
    public void dismissLoadingDialog() {
        if (loadingView == null) {
            return;
        }
        loadingView.dismissLoadingView(base_container_view);
    }

    /**
     * 显示异常对话框
     */
    public void showExceptionDialog(String msg) {
        if (exceptionView == null) {
            return;
        }
        exceptionView.showExceptionView(base_container_view, msg);
    }

    /**
     * 消失异常对话框
     */
    public void dismissExceptionDialog() {
        if (exceptionView == null) {
            return;
        }
        exceptionView.dismissExceptionView(base_container_view);
    }


    /**
     * ==========================图片选择处理===========================
     **/

    /**
     * 跳转到拍照
     *
     * @param builder 参数
     */
    public void startCameraActivity(ImageSelector.Builder builder) {
        if (builder == null) {
            return;
        }
        imageSelector = builder.listener(this).menuVisibility(View.GONE).build();
        imageSelector.startCameraActivity();
    }

    /**
     * 跳转到相册
     *
     * @param builder 参数
     */
    public void startGalleryActivity(ImageSelector.Builder builder) {
        if (builder == null) {
            return;
        }
        imageSelector = builder.listener(this).menuVisibility(View.GONE).build();
        imageSelector.startGalleryActivity();
    }

    /**
     * 跳转到剪裁
     *
     * @param builder 参数构
     */
    public void startCropActivity(ImageSelector.Builder builder) {
        if (builder == null) {
            return;
        }
        imageSelector = builder.listener(this).menuVisibility(View.GONE).build();
        imageSelector.startCropActivity();
    }

    /**
     * 显示图片助手
     *
     * @param builder 参数
     */
    public void showImageSelector(ImageSelector.Builder builder) {
        if (builder == null) {
            return;
        }
        imageSelector = builder.listener(this).menuVisibility(View.GONE).build();
        imageSelector.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imageSelector != null) {
            imageSelector.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onImageSelectFailed(String msg) {

    }

    @Override
    public void onImageSelectSucceed(Uri uri) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Handler内存处理
        pageMessage.removeCallbacksAndMessages(null);
        imageSelector = null;
        navigationBarView.onDestroy();
        navigationBarView = null;
        contentView.onDestroy();
        contentView = null;
    }

    /**
     * ==============================权限请求==============================
     **/

    public boolean isNeedCheckSelfPermission() {
        return PermissionsManager.isNeedCheckRunTimePermissions();
    }

    /**
     * 是否已经授权
     *
     * @param permissions Manifest.permission.xx
     * @return 方法返回值为PackageManager.PERMISSION_DENIED(没有授权)或者PackageManager.PERMISSION_GRANTED（已经授权）
     */
    public void checkRunTimePermissions(String[] permissions) {
        PermissionsManager.checkRunTimePermissions(this, permissions, AppConstant.REQUEST_CODE_PERMISSIONS, this);
    }

    /**
     * 是否已经授权
     *
     * @param permissions Manifest.permission.xx
     * @param requestCode 请求码
     * @return 方法返回值为PackageManager.PERMISSION_DENIED(没有授权)或者PackageManager.PERMISSION_GRANTED（已经授权）
     */
    public void checkRunTimePermissions(String[] permissions, int requestCode) {
        PermissionsManager.checkRunTimePermissions(this, permissions, requestCode, this);
    }

    /**
     * 权限请求的结果处理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onRequestPermissionsSucceed(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    public void onRequestPermissionsFailed(int requestCode, String[] permissions, int[] grantResults) {

    }

    /**
     * =========================Fragment操作==========================
     **/

    public void onFragmentLeave() {

    }

    public void onFragmentBack(Object data) {

    }

    /**
     * 添加Fragment
     *
     * @param fragment
     */
    public void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(setFragmentContainerViewById(), fragment);
            transaction.commit();
        } else {
            Log.i(this.getClass().getSimpleName(), "In the method addFragment during of fragment is null");
        }
    }

    /**
     * 跳转页面
     *
     * @param cls
     */
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 跳转页面
     *
     * @param cls
     * @param options
     */
    public void startActivity(Class<?> cls, Bundle options) {
        Intent intent = new Intent(getContext(), cls);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivity(intent);
        //开启页面进入动画
        startAnimation();
    }

    /**
     * 跳转有结果的页面
     *
     * @param cls
     * @param options
     * @param requestCode
     */
    public void startActivityForResult(Class<?> cls, Bundle options, int requestCode) {
        Intent intent = new Intent(getContext(), cls);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivityForResult(intent, requestCode);
        //开启页面进入动画
        finishAnimation(isFinishAnimation);
    }

    /**
     * 设置是否需要页面进入动画
     *
     * @param isStartAnimation
     */
    public void startAnimation(boolean isStartAnimation) {
        this.isStartAnimation = isStartAnimation;
    }

    /**
     * 设置是否需要页面退出动画
     *
     * @param isFinishAnimation
     */
    public void finishAnimation(boolean isFinishAnimation) {
        if (getActivity() == null) {
            return;
        }
        BaseActivity activity = (BaseActivity) getActivity();
        activity.finishAnimation(isFinishAnimation);
    }


    /**
     * 页面进入动画
     */
    public void startAnimation() {
        if (isStartAnimation) {
            getActivity().overridePendingTransition(R.anim.android_anim_right_in, R.anim.android_anim_left_exit);
        }
    }


    /**====================SharePreference====================**/

    /**
     * 设置用户信息
     *
     * @param map 用户Map信息
     */
    public void setUserInfo(Map<String, String> map) {
        DataStorage.with(BaseApplication.app).put(DataStorage.ANDROID_KIT_USER_INFO, map);
    }

    /**
     * 设置用户信息
     *
     * @param obj 用户信息
     */
    public void setUserInfo(Object obj) {
        DataStorage.with(BaseApplication.app).put(obj);
    }

    /**
     * 获取用户信息
     *
     * @return Map<String                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               ,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               String>
     */
    public Map<String, String> getUserInfo() {
        return DataStorage.with(BaseApplication.app).getStringMap(DataStorage.ANDROID_KIT_USER_INFO, null);
    }

    /**
     * 获取用户信息
     *
     * @param cls 用户类
     * @param <T> 用户对象
     * @return <T> T
     */
    public <T> T getUserInfo(Class<T> cls) {
        return DataStorage.with(BaseApplication.app).getObject(cls);
    }

    /**
     * 判断是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return DataStorage.with(BaseApplication.app).getBoolean(DataStorage.IS_USER_LOGIN, false);
    }

    /**
     * 设置登录状态
     *
     * @param isLogin 是否登录
     */
    public void setLogin(boolean isLogin) {
        DataStorage.with(BaseApplication.app).put(DataStorage.IS_USER_LOGIN, isLogin);
    }

    /**
     * 显示提示文字
     *
     * @param msg 提示内容
     */
    public Toast showToast(String msg) {
        return toastView.showToast(getContext(), msg);
    }

    /**
     * 显示提示文字
     *
     * @param mode 提示类型
     * @param msg  内容
     * @return
     */
    public Toast showToast(ToastMode mode, String msg) {
        return toastView.showToast(getContext(), inflater, mode, msg);
    }

}
