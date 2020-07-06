package com.android.app.page;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.R;
import com.android.annotation.ViewUtils;
import com.android.app.AppConstant;
import com.android.app.BaseApplication;
import com.android.app.manager.ActivityManager;
import com.android.app.manager.FragmentManager;
import com.android.app.manager.PermissionsManager;
import com.android.app.mode.LoadingMode;
import com.android.app.mode.ToastMode;
import com.android.app.proxy.ContentViewProxy;
import com.android.app.proxy.ExceptionViewToastProxy;
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
 * Created by Relin on 2015/11/24.
 * Activity的基础类
 * Change this code when you add other method or module
 * Note:In the RelativeLayout have three Layout , first Layout is Activity ContentViewProxy
 * second Layout  can show LoadingView three can show NoNetWorkView.
 */
public abstract class BaseActivity extends AppCompatActivity implements OnHttpListener, OnImageSelectListener, PermissionsManager.OnRequestPermissionsListener {

    /**
     * 视图转换器
     */
    private LayoutInflater inflater;

    /**
     * 自定义标题
     */
    private FrameLayout defineNavigationBarView;

    /**
     * Activity容器视图
     */
    private FrameLayout base_container_view;

    /**
     * Fragment管理工具类
     */
    private FragmentManager fragmentManager;

    /**
     * 设置是否需要进入动画
     */
    private boolean isStartAnimation = true;

    /**
     * 设置是否需要退出动画
     */
    private boolean isFinishAnimation = true;

    /**
     * 图片剪切参数
     */
    protected ImageSelector imageHelper;

    /**
     * 错误视图、加载视图 - 接口
     */
    private NavigationBarView navigationBarView;

    /**
     * 内容视图- 接口
     */
    private ContentView contentView;

    /**
     * 提示文字构建器- 接口
     */
    private ToastView toastView;

    /**
     * 加载视图- 接口
     */
    private LoadingView loadingView;

    /**
     * 异常视图- 接口
     */
    private ExceptionView exceptionView;

    /**
     * 页面信息
     */
    private PageMessage pageMessage;

    /**
     * 图片选择器构建者
     */
    private ImageSelector imageSelector;

    /**
     * onResume次数
     */
    private int onResumeCount = 0;

    /**
     * 导航栏
     */
    private FrameLayout navigationBar;

    /**
     * 导航栏图片
     */
    private ImageView navigationImage;

    /**
     * 导航栏文字
     */
    private TextView navigationText;

    /**
     * 导航栏标题
     */
    private TextView navigationTitle;

    /**
     * 导航栏菜单图片
     */
    private ImageView navigationMenuImage;

    /**
     * 导航栏菜单文字
     */
    private TextView navigationMenuText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        onPrepare();
        onHttpRequest();
    }

    /**
     * 准备工作
     */
    protected void onPrepare() {
        pageMessage = new PageMessage(this);
        navigationBarView = setNavigationBarView();
        contentView = setContentView();
        toastView = setToastView();
        loadingView = setLoadingView();
        exceptionView = setExceptionView();
        fragmentManager = new FragmentManager(this);
        ActivityManager.getInstance().addActivity(this);
        //横竖屏切换
        setRequestedOrientation(setRequestedOrientation());
        //初始化控件
        initControlView();
        ViewUtils.inject(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (onResumeCount >= 1) {
            onRelive();
        }
        onResumeCount++;
    }

    /**
     * 重进页面
     */
    protected void onRelive() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imageHelper != null) {
            imageHelper.clearCache();
        }
        fragmentManager = null;
        imageHelper = null;
        //Handler内存处理
        pageMessage.removeCallbacksAndMessages(null);
        //清空无用对象
        if (navigationBarView != null) {
            navigationBarView.onDestroy();
            navigationBarView = null;
        }
        contentView.onDestroy();
        contentView = null;
        //移除栈里面的页面
        ActivityManager.getInstance().removeActivity(this);
    }

    /**
     * 设置屏幕显示方向
     *
     * @return
     */
    protected int setRequestedOrientation() {
        return BaseApplication.app.getRequestedOrientation();
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 16进制颜色
     */
    protected void setStatusBarColor(int color) {
        StatusBar.setTranslucent(this);
        StatusBar.setColor(this, getResources().getColor(color));
        StatusBar.setFontColor(this, getResources().getColor(color) == Color.WHITE);
    }

    /**
     * 在Xml里面设置
     *
     * @return 注意：这里用的ID不能包含大写字母
     */
    protected int setNavigationBarViewById() {
        return R.id.android_nav_item;
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
     * 布局ID
     **/
    protected abstract int setContentLayoutById();

    /**
     * 设置Fragment内容布局
     **/
    protected int setFragmentContainerViewById() {
        return 0;
    }

    /**
     * 设置导航栏可见性
     *
     * @param visibility
     */
    public void setNavigationBarVisibility(int visibility) {
        if (navigationBarView == null) {
            return;
        }
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
     * 设置页面视图
     * 如果需要自定义加载动画、错误视图就继承BaseViewPlug重写对应的方法就行了，
     * 重写方法的时候注释掉父类的over的代码
     *
     * @return
     */
    protected NavigationBarView setNavigationBarView() {
        return new NavigationBarViewProxy();
    }

    /**
     * 设置页面Toast
     * 重定义Toast显示页面，只需要继承ToastPlug实现对应的方法即可
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
        return new ExceptionViewToastProxy();
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
     * 获取导航栏对象
     *
     * @return
     */
    public NavigationBarView getNavigationBarView() {
        return navigationBarView;
    }

    /**
     * 请求接口数据
     **/
    public void onHttpRequest() {
        if (isDetermineNetwork() && !NetworkUtils.isAvailable(this)) {
            showLoadingDialog(LoadingMode.DIALOG);
            Message message = pageMessage.obtainMessage();
            message.what = AppConstant.WHAT_MSG_NET_OFFLINE;
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.MSG_KEY, AppConstant.EXCEPTION_MSG_NET_OFFLINE);
            message.setData(bundle);
            pageMessage.sendMessage(message);
            return;
        }
        dismissLoadingDialog();
        dismissExceptionDialog();
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
        if (!NetworkUtils.isAvailable(this)) {
            message.what = AppConstant.WHAT_MSG_NET_OFFLINE;
            Bundle bundle = new Bundle();
            bundle.putString(AppConstant.MSG_KEY, AppConstant.EXCEPTION_MSG_NET_OFFLINE);
            bundle.putInt(AppConstant.MSG_CODE, response.code());
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
    private void initControlView() {
        setContentView(R.layout.android_af_base);
        inflater = LayoutInflater.from(this);
        defineNavigationBarView = findViewById(R.id.define_title);
        base_container_view = findViewById(R.id.base_container_view);
        contentView.onCreateContentView(this, inflater, base_container_view, setContentLayoutById(), 0, this.getClass().getSimpleName());
        exceptionView.onCreateExceptionView(this, inflater, base_container_view);
        navigationBarView.onCreateNavigationBarView(inflater, base_container_view, defineNavigationBarView, setNavigationBarViewById(), setNavigationBarLayoutById());
        //导航栏
        navigationBar = findViewById(R.id.android_nav_item);
        navigationImage = findViewById(R.id.android_iv_nav_back);
        navigationText = findViewById(R.id.android_tv_nav_left);
        navigationTitle = findViewById(R.id.android_tv_nav_title);
        navigationMenuImage = findViewById(R.id.android_iv_nav_menu);
        navigationMenuText = findViewById(R.id.android_tv_nav_menu);
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
    }


    /**
     * 导航栏图片点击
     *
     * @param v
     */
    public void onNavigationImageClick(ImageView v) {
        finish();
    }

    /**
     * 导航栏文字点击
     *
     * @param v
     */
    public void onNavigationTextClick(TextView v) {

    }

    /**
     * 导航栏菜单图片点击
     *
     * @param v
     */
    public void onNavigationMenuImageClick(ImageView v) {

    }

    /**
     * 导航栏菜单文字点击
     *
     * @param v
     */
    public void onNavigationMenuTextClick(TextView v) {

    }

    /**
     * 获取导航栏
     *
     * @return
     */
    public FrameLayout getNavigationBar() {
        return navigationBar;
    }

    /**
     * 获取导航栏图标
     *
     * @return
     */
    public ImageView getNavigationImage() {
        return navigationImage;
    }

    /**
     * 获取导航栏文字
     *
     * @return
     */
    public TextView getNavigationText() {
        return navigationText;
    }

    /**
     * 获取导航栏标题
     *
     * @return
     */
    public TextView getNavigationTitle() {
        return navigationTitle;
    }

    /**
     * 获取菜单图片
     *
     * @return
     */
    public ImageView getNavigationMenuImage() {
        return navigationMenuImage;
    }

    /**
     * 获取菜单文字
     *
     * @return
     */
    public TextView getNavigationMenuText() {
        return navigationMenuText;
    }

    /**
     * 设置导航栏图标
     *
     * @param ico     资源图标
     * @param padding 内间距
     */
    public void setNavigationImageRes(int ico, int padding) {
        navigationImage.setImageResource(ico);
        navigationImage.setPadding(padding, padding, padding, padding);
    }

    /**
     * 设置导航栏文字
     *
     * @param text     文字
     * @param color    颜色
     * @param textSize 字体大小
     */
    public void setNavigationText(String text, int color, int textSize) {
        navigationText.setText(text);
        navigationText.setTextColor(color);
        navigationText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置标题栏
     *
     * @param title    标题
     * @param color    颜色
     * @param textSize 文字大小
     */
    public void setNavigationTitle(String title, int color, int textSize) {
        navigationTitle.setText(title);
        navigationTitle.setTextColor(color);
        navigationTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置菜单文字
     *
     * @param menu     菜单
     * @param color    颜色
     * @param textSize 文字大小
     */
    public void setNavigationMenuText(String menu, int color, int textSize) {
        navigationTitle.setText(menu);
        navigationTitle.setTextColor(color);
        navigationTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置菜单图片
     *
     * @param ico         图标
     * @param padding     内间距
     * @param rightMargin 右间距
     */
    public void setNavigationMenuImage(int ico, int padding, int rightMargin) {
        navigationMenuImage.setImageResource(ico);
        navigationMenuImage.setPadding(padding, padding, padding, padding);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) navigationMenuImage.getLayoutParams();
        params.rightMargin = rightMargin;
        navigationMenuImage.setLayoutParams(params);
    }

    /**==============================加载LoadingView==============================**/

    /**
     * 显示加载对话框
     *
     * @param mode 加载模式
     */
    public void showLoadingDialog(LoadingMode mode) {
        showLoadingDialog(mode, "");
    }

    /**
     * 显示加载对话框
     *
     * @param mode  加载模式
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    /**
     * ==============================6.0权限请求==============================
     **/

    public boolean isNeedCheckRunTimePermissions() {
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

    /**==============================页面切换动画==============================**/

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
        Intent intent = new Intent(this, cls);
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
        Intent intent = new Intent(this, cls);
        if (options != null) {
            intent.putExtras(options);
        }
        startActivityForResult(intent, requestCode);
        //开启页面进入动画
        startAnimation();
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
     * 设置是否需要退出页面动画
     *
     * @param isFinishAnimation
     */
    public void finishAnimation(boolean isFinishAnimation) {
        this.isFinishAnimation = isFinishAnimation;
    }

    @Override
    public void finish() {
        super.finish();
        finishAnimation();
    }

    /**
     * 页面进入动画
     */
    public void startAnimation() {
        if (isStartAnimation) {
            overridePendingTransition(R.anim.android_anim_right_in, R.anim.android_anim_left_exit);
        }
    }

    /**
     * 页面退出动画
     */
    public void finishAnimation() {
        if (isFinishAnimation) {
            overridePendingTransition(R.anim.android_anim_left_in, R.anim.android_anim_right_exit);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //点击返回添加退出动画，如果不需要就在super之前isFinishAnimation = false
        if (isFinishAnimation) {
            finishAnimation();
        }
    }

    /**
     * 获取Activity管理类
     *
     * @return
     */
    public ActivityManager getActivityManager() {
        return ActivityManager.getInstance();
    }

    /**==============================Fragment操作==============================**/

    /**
     * 添加Fragment
     *
     * @param cls Fragment类
     */
    protected void addFragment(Class<?> cls) {
        fragmentManager.addFragment(cls, null, setFragmentContainerViewById());
    }

    /**
     * 添加Fragment
     *
     * @param cls  Fragment类
     * @param data 数据对象
     */
    protected void addFragment(Class<?> cls, Object data) {
        fragmentManager.addFragment(cls, data, setFragmentContainerViewById());
    }

    /**
     * 替换Fragment
     *
     * @param cls  Fragment类
     * @param data 数据对象
     */
    protected void replaceFragment(Class<?> cls, Object data) {
        fragmentManager.replaceFragment(cls, data, setFragmentContainerViewById());
    }

    /**
     * 入栈Fragment
     *
     * @param cls  Fragment类
     * @param data 数据对象
     */
    protected void pushFragmentToBackStatck(Class<?> cls, Object data) {
        fragmentManager.pushFragmentToBackStack(cls, data, setFragmentContainerViewById());
    }

    /**
     * 显示Fragment
     *
     * @param cls  Fragment类
     * @param data 数据对象
     */
    protected void goToFragment(Class<?> cls, Object data) {
        fragmentManager.goToFragment(cls, data);
    }

    /**
     * 弹出栈顶Fragment
     *
     * @param data 数据对象
     */
    protected void popTopFragment(Object data) {
        fragmentManager.popTopFragment(data);
    }

    /**
     * 弹出显示栈底Fragment
     *
     * @param data 数据对象
     */
    protected void popToRoot(Object data) {
        fragmentManager.popToRoot(data);
    }

    /**
     * 获取当前Fragment
     *
     * @return
     */
    protected BaseFragment getCurrentFragment() {
        return fragmentManager.getCurrentFragment();
    }

    /**==================================SharePreference================================**/

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
     * @return Map<String, String>
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
     * @return boolean
     */
    public boolean isLogin() {
        return DataStorage.with(BaseApplication.app).getBoolean(DataStorage.IS_USER_LOGIN, false);
    }

    /**
     * 设置登录状态
     *
     * @param isLogin 是否登录状态
     */
    public void setLogin(boolean isLogin) {
        DataStorage.with(BaseApplication.app).put(DataStorage.IS_USER_LOGIN, isLogin);
    }

    /**
     * 显示提示文字
     *
     * @param msg 提示内容
     * @return Toast
     */
    public Toast showToast(String msg) {
        return toastView.showToast(this, msg);
    }

    /**
     * 显示提示文字
     *
     * @param mode 提示类型
     * @param msg  提示内容
     * @return Toast
     */
    public Toast showToast(ToastMode mode, String msg) {
        return toastView.showToast(this, inflater, mode, msg);
    }

}
