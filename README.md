# Android
Android开发框架（Android development integration tools）

## 方法一  ARR依赖
[Android.arr](https://github.com/RelinRan/Android/blob/master/Android.aar)
[Android DOC](https://github.com/RelinRan/Android/blob/master/doc/index.html)
```
android {
    ....
    repositories {
        flatDir {
            dirs 'libs'
        }
    }
}

dependencies {
    implementation(name: 'Android', ext: 'aar')
}

```

## 方法二   JitPack依赖
### A.项目/build.grade
```
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
### B.项目/app/build.grade
```
dependencies {
	implementation 'com.github.RelinRan:Android:1.0.0'
}
```

## Application - 继承BaseApplication
### AndroidManifest.xml application标签配置
```
android:name=".XXXApplication"
```
### 调试模式，LOG日志可以查看请求接口
```
setDebugMode(true);
```
### 调试模式，LOG查看日志 + 以日志对话框的形式显示
```
setDebugMode(true，true);
```

## style.xml
工具类拥有常见的一些主题。
```
<style name="AppTheme" parent="Android.Theme.Transparent.NoActionBar"></style>
```
### 没有TitleBar白色背景
```
Android.Theme.Light.NoActionBar
```
### 没有覆盖背景的Dialog
```
Android.Theme.Dialog.Transparent.Background
```
### 有覆盖背景的Dialog
```
Android.Theme.Dialog.Translucent.Background
```
### 横向ProgressBar
```
android_progressbar_horizontal
```
### Dialog放大缩小动画
```
android_anim_zoom
```
### Dialog底部进入动画
```
android_anim_bottom
```

## Activity使用 - 继承BaseActivity

### findViewById找控件（注解方式）
```
@ViewInject(R.id.rg_menu)
private RadioGroup rg_menu;
```

### 设置导航栏Layout(注意：导航栏设置只能用其中一个)
```
@Override
protected int setNavigationBarLayoutById() {
   return R.layout.layout_navgation;
}

@Override
protected int setNavigationBarViewById() {
   return R.id.navigation;
}
```
### 设置内容layout
```
@Override
protected int setContentLayoutById() {
    return R.layout.aty_main;
}
```
### 显示Fragment
```
@Override
protected int setFragmentContainerViewById() {
    //Fragment布局占位ID
    return R.id.fl_content;
}

//初始化操作
@Override
protected void onPrepare() {
    super.onPrepare();
    //直接添加Fragment
    addFragment(IndexFgt.class, null);
}
```
### 点击事件
```
@OnClick({R.id.iv_back})
private void onClick(View v) {
    switch (v.getId()) {
        case R.id.iv_back:
            finish();
            break;
        }
}
```
### Toast显示
```
showToast("添加IndexFgt");//显示一般Toast
showToast(ToastMode.SUCCEED，"添加成功");//显示成功Toast
showToast(ToastMode.FAILURE,"添加失败");//显示失败Toast
```
### 网络请求
```
@Override
public void onHttpRequest() {
    super.onHttpRequest();
	//显示Loading视图
    showLoadingDialog(LoadingMode.DIALOG);
	//网络请求操作......
    RequestParams params = new RequestParams();
    params.add("limit","20");
    params.add("page","1");
    params.add(RequestParams.REQUEST_CONTENT_TYPE, RequestParams.REQUEST_CONTENT_JSON);
    OkHttp.post(Constants.BASE_URL + "/getlist", params, listener);
}
```
### 网络请求
```
@Override
public void onHttpSucceed(HttpResponse response) {
    super.onHttpSucceed(response);
    //JSON解析，主要用到JsonParser
    Body body = JsonParser.parseJSONObject(Body.class, response.body());
    if (body.getCode().equals("0")) {
         if (response.url().contains("getlist")) {

         }
    } else {
        showToast(body.getMsg());
    }
}

@Override
public void onHttpFailure(HttpResponse response) {
      super.onHttpFailure(response);
}
```

### 定制化LoadingView、ToastView、屏幕显示方向，重写如下方法：
```
    //加载动画自定义，用户只需要实现LoadingView接口自定义一个类，在return时候不用super.setLoadingView();
    @Override
    protected LoadingView setLoadingView() {
        return super.setLoadingView();
    }

    //弹框自定义，用户只需要实现LoadingView接口自定义一个类，在return时候不用super.setToastView();
    @Override
    protected ToastView setToastView() {
        return super.setToastView();
    }

    //设置屏幕显示方向，常量有BaseApplication.REQUEST_ORATION_PORTRAIT(竖屏),BaseApplication.REQUEST_ORATION_LAND（横屏）
    @Override
    protected int setRequestedOrientation() {
        return super.setRequestedOrientation();
    }

```
## Fragment - 使用方法跟Activity一致只是继承BaseFragment，重写的方法都一致。

## ActivityManager（Activity管理器）
项目有时候需要在一个页面finish的时候杀死之前的页面，那么此时就需要这个类，
注意如需要单个使用这个类在自己框架，需要在自己BaseActivity中使用方法ActivityManager.getInstance().addActivity(xxxx);
```
//清除所有页面，包含当前页面
ActivityManager.getInstance().removeAllActivity();
//清除单个页面
ActivityManager.getInstance().removeActivity(MainActivity.class);
//退出程序
ActivityManager.getInstance().AppExit(context);
```
## CaughtException - 异常捕捉 - 注意：使用这个类需要提前申请文件写入、读取权限，在Android 6.0需要动态申请权限。
```
CaughtException.Builder caughtBuilder = new CaughtException.Builder(context);
caughtBuilder.fileType(".txt");
caughtBuilder.folderName("Exception");
caughtBuilder.listener(new OnCaughtExceptionListener() {
    @Override
    public void onCaughtExceptionSucceed(File file) {

    }

    @Override
    public void onCaughtExceptionFailure(String error) {

    }
});
caughtBuilder.build();
```

##  ShapeButton - Shape背景按钮
```
    <com.android.view.ShapeButton
        android:id="@+id/btn_login"
        android:layout_width="match_parent"
        android:layout_height="@dimen/button_height"
        android:layout_marginLeft="@dimen/x118"
        android:layout_marginRight="@dimen/x118"
        android:layout_marginTop="@dimen/y110"
        android:text="登录"
        android:textColor="@color/color_white"
        android:textSize="@dimen/font_normal"
        app:radius="3dp"
        app:solid="@color/colorYellow" />
```

##  BannerPager - 轮播图
```
    //布局控件
    <com.android.view.BannerPager
        android:id="@+id/banner"
        android:layout_width="match_parent"
        android:layout_height="@dimen/y810"
        app:isAutoPlay="true"
        app:duration="3000">
	</com.android.view.BannerPager>
	
    //设置数据源
    bannerAdapter = new IndexBannerAdapter(getActivity(), bannerList);
    //设置点击事件
    banner.setOnBannerPagerClickListener(this);
    //设置页面改变事件
    banner.addOnPageChangeListener(this);
    //设置数据源
    banner.setAdapter(bannerAdapter);
	
    //Adapter
    public class IndexBannerAdapter extends BannerAdapter<Map<String, String>> {

    public IndexBannerAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoader.show(getItem(position).get("ad_pic"), holder.target);
    }

}
	
```

## RecyclerAdapter使用(RecyclerView的Adapter)
```
public class FriendsAdapter extends RecyclerAdapter<FriendsBody, FriendsAdapter.ViewHolder> {
    
    public FriendsAdapter(BaseFragment fragment) {
        super(fragment);
    }

    @Override
    public void onBindView(@NonNull ViewHolder holder, final int position) {
        holder.tv_letter.setVisibility(position != 0 ? View.GONE : View.VISIBLE);
        holder.tv_letter.setText(getItem(position).getLetter());
        holder.rb_name.setText(getItem(position).getNickname());
        holder.rb_name.setChecked(getItem(position).isCheck());
        holder.rb_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getItem(position).setCheck(!getItem(position).isCheck());
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public ViewHolder onCreateHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(createView(R.layout.item_firends, viewGroup));
    }

    public class ViewHolder extends RecyclerAdapter.ViewHolder {

        @ViewInject(R.id.tv_letter)
        private TextView tv_letter;
        @ViewInject(R.id.iv_head)
        private ImageView iv_head;
        @ViewInject(R.id.rb_name)
        private RadioButton rb_name;

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

}
```

## Adapter使用(ListView、GridView 的Adapter)
```
public class BankCardAdapter extends Adapter<BankCardBody, BankCardAdapter.ViewHolder> {

    public BankCardAdapter(BaseActivity activity) {
        super(activity);
    }

    @Override
    public ViewHolder onCreateHolder(View view, ViewGroup viewGroup, int viewType) {
        return new ViewHolder(createView(R.layout.item_bank_card, viewGroup));
    }

    @Override
    public void onBindView(ViewHolder holder, final int position) {
        holder.tv_card_no.setText(BankValidator.formatCardNo(getItem(position).getBankCardNumber()));
        holder.tv_card_name.setText(BankValidator.getname(getItem(position).getBankCardNumber()));
        holder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", getItem(position));
                getActivity().startActivity(BankCardConfirmAty.class, bundle);
            }
        });
    }

    public class ViewHolder extends Adapter.ViewHolder {

        @ViewInject(R.id.rl_item)
        private RelativeLayout rl_item;
        @ViewInject(R.id.iv_ico)
        private ImageView iv_ico;
        @ViewInject(R.id.tv_card_name)
        private TextView tv_card_name;
        @ViewInject(R.id.tv_card_no)
        private TextView tv_card_no;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
```

## RecyclerView 分割线/间隔设置
```
recyclerView.addItemDecoration(new DividerItemDecoration(LinearLayoutManager.HORIZONTAL, ContextCompat.getColor(getContext(), R.color.colorDivider), 2));
		
recyclerView.addItemDecoration(new SpaceItemDecoration(LinearLayoutManager.HORIZONTAL, 2));
		
```
## TextGroupView 使用（ImageView + TextView + TextView + TextView + ImageView + EditText）
详细使用说明 https://github.com/RelinRan/TextGroupView

## AlterDialog
```
        //单按钮
        new AlertDialog.Builder(AndroidKit.this).msg("你是在测试我吗？").cancel("取消").confirm("确认").listener(null).build().show();
        //双按钮
        new AlertDialog.Builder(AndroidKit.this).msg("你是在测试我吗？").confirm("确认").listener(null).build().show();
```
## Downloader使用(HucDownloader/Downloader)
HucDownloader主要采用的是HttpUrlConnection、Downloader只要采用的是OkHttp
但是两者使用方法一致，都是采用Builder模式。

```
HucDownloader.Builder builder = new HucDownloader.Builder()
          .url(file_url)
          .name("Git.zip")
          .folder("Downloader")
          .isBreakpoint(true)
          .listener(new OnDownloadListener() {
                @Override
                 public void onDownloading(long total, long progress, int percent) {

                 }

                 @Override
                 public void onDownloadCompleted(File file) {

                 }

                 @Override
                 public void onDownloadFailed(Exception e) {

                       }

                  });
downloader = new HucDownloader(builder);
downloader.start();
```
## ImageSelector - 图片选择器
```
//显示图片选择器
ImageSelector.Builder builder = new ImageSelector.Builder(context);
builder.crop(false);
builder.size(300);
showImageSelector(builder);

//在页面重写如下方法获取选中结果
@Override
public void onImageSelectSucceed(Uri uri) {
    super.onImageSelectSucceed(uri);
    //IOUtils是框架里面的类
    File file = IOUtils.decodeUri(context,uri);
}

@Override
public void onImageSelectFailed(String msg) {
    super.onImageSelectFailed(msg);
}
```
## ItemDialog - 选择弹框,工具可以单个字段列表数据选择
```
List<ItemDialogBody> bodies = new ArrayList<>();
String names[] = new String[]{"重庆邮电大学", "重庆大学", "重庆科技大学", "重庆交通大学"};
for (int i = 0; i < names.length; i++) {
    ItemDialogBody body = new ItemDialogBody();
    body.setName(names[i]);
    bodies.add(body);
}
ItemDialog.Builder itemBuilder = new ItemDialog.Builder(AndroidKit.this);
itemBuilder.title("选择大学");
itemBuilder.bodies(bodies);
itemBuilder.listener(new ItemDialog.OnItemDialogClickListener() {
    @Override
    public void onItemDialogClick(Dialog dialog, List<ItemDialogBody> bodies, int position) {
        showToast(bodies.get(position).getName());
    }
});
itemBuilder.build();
```

## ItemSelector使用（底部列表选择器）
```
ItemSelector.Builder builder = new ItemSelector.Builder(context);
builder.items(new String[]{"A", "B", "C"});
builder.listener(new OnItemSelectListener() {
@Override
public void onItemSelect(String content, int position) {
        showToast(content + " - " + position);
    }
});
builder.build();
```

## AddressSelector使用（地址选择器）
```
new AddressSelector.Builder(AndroidKit.this).listener(new OnAddressSelectListener() {
    @Override
    public void onAddressSelected(String province, String city, String district, String provinceId, String cityId, String districtId) {
        Log.e("Relin", province + city + district + "-" + provinceId + "," + cityId + "," + districtId);
        showToast(province + city + district + "-" + provinceId + "," + cityId + "," + districtId);
    }
}).provinceId("3")
.cityId("36")
.area("398")
.build().show();

```
## DateSelector使用(日期选择器)
```
new DateSelector.Builder(AndroidKit.this).type(DateSelector.TYPE_DATE).listener(new OnDateSelectListener() {
    @Override
    public void onDateSelected(String date) {
        showToast(date);
    }
}).year(1992).month(12).day(24).build().show();
```
## 更新对话框
```
Update.show(context, R.drawable.ic_launcher, "项目名称", "http://192.168.1.33:8080/app/SG_201906171.apk", "您有有新版本是否更新？", true);
```
## 快速自定义对话框(Dialog.Builder)
```
    public static void showRentPeriods(BaseAty aty) {
        Dialog.Builder builder = new Dialog.Builder(aty);
        builder.gravity(Gravity.BOTTOM);
        builder.width(LinearLayout.LayoutParams.MATCH_PARENT);
        builder.height((int) (Screen.height() * 0.3));
        builder.themeResId(Dialog.THEME_TRANSLUCENT);//半透明背景
        builder.layoutResId(R.layout.dialog_rent_periods);
        builder.canceledOnTouchOutside(true);
        builder.cancelable(true);
        builder.animResId(Dialog.ANIM_BOTTOM);//底部进入动画
        final Dialog dialog = builder.build();
        final RentPeriodsViewHolder holder = new RentPeriodsViewHolder();
        ViewUtils.inject(holder, dialog.contentView);
        final List<String> items = new ArrayList<>();
        for (int i = 3; i < 13; i += 3) {
            items.add(i + "期");
        }
        holder.loopView.setItems(items);
        holder.loopView.setInitPosition(0);
        holder.tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        holder.tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static class RentPeriodsViewHolder {
        @ViewInject(R.id.tv_cancel)
        private TextView tv_cancel;
        @ViewInject(R.id.tv_ok)
        private TextView tv_ok;
        @ViewInject(R.id.loopView)
        private LoopView loopView;
    }
```
## SQLiteHelper（数据库类使用）
```
//创建表格
SQLiteHelper.with(context).createTable("table_name",new String[]{"column_name_a","column_name_b"});
User user = new User();
SQLiteHelper.with(context).createTable(user);

//删除表格数据（不删除表）
SQLiteHelper.with(context).deleteTable("table_name");
SQLiteHelper.with(context).deleteTable(User.class);
SQLiteHelper.with(context).dropTable("table_name");//删除表

//删除数据
SQLiteHelper.with(context).delete("sql");
SQLiteHelper.with(context).delete(User.class,"user_id=?",new String[]{"1"});
SQLiteHelper.with(context).delete("table_name","user_id=?",new String[]{"1"});

//查询
List<Map<String, String>> list = SQLiteHelper.with(context).query("sql");
List<User> list = SQLiteHelper.with(context).query(User.class,"sql");

//插入数据
User user = new User();
SQLiteHelper.with(context).insert(user);

SQLiteHelper.with(context).insert("sql");

ContentValues values = new ContentValues();
values.put("user_id","1");
values.put("user_name","name");
SQLiteHelper.with(context).insert("table_name",values);

//更新数据
SQLiteHelper.with(context).update("sql");

User user = new User();
user.setUserName("Name");
ContentValues values = new ContentValues();
values.put("user_id","1");
values.put("user_name","name");
SQLiteHelper.with(context).update(user,values,"user_id=?",new String[]{"1"});
```
## Badge（APP桌面角标-红色圆点）
主要是显示红色圆点，但是不支持所有手机类型，目前支持小米、华为、三星、索尼。
Badge已经做了缓存处理，同时去区别了多个项目在一个手机的缓存区别。
```
//增加数量
Badge.add(cntext);
//重置数量
Badge.reset(context);
//设置数量
Badge.setNumber(context,number);
//获取数量
int number = Badge.number(context);
```
## DataStorage(数据缓存)
目前支持int string double float long Set Map<String,String> List<Map<String,String>>数据类型
```
DataStorage.with(context).put("username","xxxx");
String username = DataStorage.with(context).getString("username","");
```

## DateUtils - 时间工具类
```
//现在时间
String now = DateUtils.now(DateUtils.DATE_FORMAT_YYYY_MM_DD);

//时间戳转时间
String time = DateUtils.parseFromTimestamp(timestamp);
String time = DateUtils.parseFromTimestamp(timestamp,DateUtils.DATE_FORMAT_YYYY_MM_DD);

//时间字符串转时间对象
Date date = DateUtils.parse("2019-09-10 09:00:10");
Date date = DateUtils.parse("2019-09-10 09:00:10",DateUtils.DATE_FORMAT_YYYY_MM_DD_BLANK_24H_MM_SS);
```

## Log - 日志，使用方法跟系统的一致，只是为了打印长字符的时候能够打印完全做了换行打印。
```
com.android.utils.Log.i("Relin","xxxxxxx");
```

## StatusBar（状态栏工具类）
工具包含对沉浸状态设置、状态栏颜色、字体颜色修改（深色、浅色）
```
//如果在页面中（Activity/Fragment）可以直接调用父类方法设置
setStatusBarColor(R.color.color_white);
//详细使用方法，对应类有注释https://github.com/RelinRan/AndroidKit/blob/master/app/src/main/java/com/android/utils/StatusBar.java
```

## Uploader - 上传文件
工具可以对文件上传进行监听
```
Uploader.Builder builder = new Uploader.Builder();
builder.url(Constants.BASE_URL + "/appApi/file/uploadImage");
UploadParams params = new UploadParams();
params.addHeader("token", Token.value());
params.add("file", file);
builder.listener(listener);
builder.params(params);
builder.mediaType(Uploader.MEDIA_TYPE_FORM);
builder.build();
```

## VideoRecordAty - 视频录制
```
//AndroidManifest.xml配置
<activity android:name="com.android.video.VideoRecordAty"></activity>

//跳转页面
Bundle bundle = new Bundle();
//限制录制多少秒，如果不限制就传0
bundle.putLong(VideoRecordAty.VIDEO_DURATION,60*1000);
startActivityForResult(VideoRecordAty.class,null,520);

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode==RESULT_OK&&requestCode==520){
        //视频信息
        String path = data.getStringExtra(VideoRecordAty.VIDEO_PATH);
        String width = data.getStringExtra(VideoRecordAty.VIDEO_WIDTH);
        String height = data.getStringExtra(VideoRecordAty.VIDEO_HEIGHT);
        String duration = data.getStringExtra(VideoRecordAty.VIDEO_DURATION);
    }
}
```

