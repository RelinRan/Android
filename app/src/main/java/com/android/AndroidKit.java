//package com.android;//package com.android;
//
//import android.Manifest;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.android.address.AddressSelector;
//import com.android.address.OnAddressSelectListener;
//import com.android.annotation.ViewInject;
//import com.android.annotation.ViewUtils;
//import com.android.app.dialog.AlertDialog;
//import com.android.app.dialog.Dialog;
//import com.android.app.dialog.ItemDialog;
//import com.android.app.dialog.ItemDialogBody;
//import com.android.app.dialog.ItemSelector;
//import com.android.app.dialog.OnItemSelectListener;
//import com.android.app.mode.LoadingMode;
//import com.android.app.mode.ToastMode;
//import com.android.app.page.BaseActivity;
//import com.android.app.proxy.ExceptionViewPageProxy;
//import com.android.app.proxy.LoadingViewSwipeProxy;
//import com.android.app.view.ExceptionView;
//import com.android.app.view.LoadingView;
//import com.android.date.DateSelector;
//import com.android.date.OnDateSelectListener;
//import com.android.image.ImageSelector;
//import com.android.io.HucDownloader;
//import com.android.io.OnDownloadListener;
//import com.android.net.HttpResponse;
//import com.android.net.HucCookie;
//import com.android.net.HucHttp;
//import com.android.net.OkCookie;
//import com.android.net.OkHttp;
//import com.android.net.RequestParams;
//import com.android.utils.ListUtils;
//import com.android.utils.Update;
//import com.android.video.VideoRecordAty;
//import com.android.view.BannerPager;
//import com.android.widget.BannerAdapter;
//import com.android.widget.SwipeRequestLayout;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class AndroidKit extends BaseActivity {
//
//    @ViewInject(R.id.iv_ico)
//    private ImageView iv_ico;
//    @ViewInject(R.id.lv_kit)
//    private ListView lv_kit;
//    @ViewInject(R.id.lv_image)
//    private ListView lv_image;
//    @ViewInject(R.id.prl)
//    private SwipeRequestLayout prl;
//    @ViewInject(R.id.banner)
//    private BannerPager banner;
//
//    private String names[] = {"Refresh Completed"
//            , "Load More Completed"
//            , "Show Normal Toast"
//            , "Show Succeed Toast"
//            , "Show Failed Toast"
//            , "Show Exception View"
//            , "Show Dialog Loading"
//            , "Show Content Loading"
//            , "Show AlertDialog"
//            , "Dismiss Loading"
//            , "startGalleryActivity"
//            , "startCameraActivity"
//            , "Show Date Time Dialog"
//            , "Show Image Selector Dialog"
//            , "ShowAddressDialog"
//            , "GetCookie"
//            , "HttpUrlConnect"
//            , "OkHttp"
//            , "Downloader-Start"
//            , "Downloader-Pause"
//            , "Downloader-Cancel"
//            , "JsonParser"
//            , "Update"
//            , "ItemDialog"
//            , "VideoRecord"
//            , "ItemSelector"
//    };
//
//    private String img_all[] = {
//            "https://goss3.vcg.com/creative/vcg/400/version23/VCG41499916312.jpg",
//            "https://goss.vcg.com/creative/vcg/400/version23/VCG212dfbf1a14.jpg",
//            "https://goss.vcg.com/creative/vcg/400/version23/VCG21409037867.jpg",
//            "https://goss1.vcg.com/creative/vcg/400/version23/VCG210e154c533.jpg",
//            "https://goss1.vcg.com/creative/vcg/400/version23/VCG41506843534.jpg",
//            "http://goss3.vcg.com/creative/vcg/400/version23/VCG213d5f7ed27.jpg",
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541162924526&di=78fe07cb049cd20ef8f642e481b99e86&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F20174_15_14%2Fa6p86q1590215467879.gif",
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541162720115&di=d62ac1b26cf0e7cd9b67a61b75df532d&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170401%2F94d308299fa64945ac9927f7aee9d960_th.gif",
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541389850978&di=79a59ce37703ec56d3501d8c8ce8f2a4&imgtype=0&src=http%3A%2F%2Fupfile.asqql.com%2F2009pasdfasdfic2009s305985-ts%2F2018-4%2F20184272039674321.gif",
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541390156744&di=69f3eeafc2a87e699ad9f1725fa6e1c6&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201609%2F15%2F20160915095944_atSAR.gif",
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541390097310&di=3365908ebf81292e5355306086a57e5a&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fblog%2F201412%2F04%2F20141204190821_siN45.thumb.700_0.gif",
//    };
//
//    private String img_jpg[] = {
//            "https://goss3.vcg.com/creative/vcg/400/version23/VCG41499916312.jpg",
//            "https://goss.vcg.com/creative/vcg/400/version23/VCG212dfbf1a14.jpg",
//            "https://goss.vcg.com/creative/vcg/400/version23/VCG21409037867.jpg",
//            "https://goss1.vcg.com/creative/vcg/400/version23/VCG210e154c533.jpg",
//            "https://goss1.vcg.com/creative/vcg/400/version23/VCG41506843534.jpg",
//            "http://goss3.vcg.com/creative/vcg/400/version23/VCG213d5f7ed27.jpg",
//    };
//
//    private String img_gif[] = {
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541162924526&di=78fe07cb049cd20ef8f642e481b99e86&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F20174_15_14%2Fa6p86q1590215467879.gif",
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541162720115&di=d62ac1b26cf0e7cd9b67a61b75df532d&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170401%2F94d308299fa64945ac9927f7aee9d960_th.gif",
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541389850978&di=79a59ce37703ec56d3501d8c8ce8f2a4&imgtype=0&src=http%3A%2F%2Fupfile.asqql.com%2F2009pasdfasdfic2009s305985-ts%2F2018-4%2F20184272039674321.gif",
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541390156744&di=69f3eeafc2a87e699ad9f1725fa6e1c6&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201609%2F15%2F20160915095944_atSAR.gif",
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1541390097310&di=3365908ebf81292e5355306086a57e5a&imgtype=0&src=http%3A%2F%2Fimg5.duitang.com%2Fuploads%2Fblog%2F201412%2F04%2F20141204190821_siN45.thumb.700_0.gif",
//    };
//
//    private String file_url = "http://8dx.pc6.com/wwb6/Git2180.zip";
//
//    private List<String> items;
//    private List<String> images;
//    private List<String> bannerItems;
//    private HucDownloader downloader;
//
//    @Override
//    protected boolean isDetermineNetwork() {
//        return false;
//    }
//
//    @Override
//    protected LoadingView setLoadingView() {
//        return new LoadingViewSwipeProxy();
//    }
//
//    @Override
//    protected ExceptionView setExceptionView() {
//        return new ExceptionViewPageProxy();
//    }
//
//    @Override
//    protected int setContentLayoutById() {
//        return R.layout.android_kit;
//    }
//
//    @Override
//    protected void onPrepare() {
//        super.onPrepare();
//        setStatusBarColor(R.color.colorAccent);
//        checkRunTimePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
//        bannerItems = new ArrayList<>();
//        for (int i = 0; i < img_jpg.length; i++) {
//            bannerItems.add(img_jpg[i]);
//        }
//        banner.setAdapter(new BannerAdapter<String>(this) {
//            @Override
//            public void onBindViewHolder(ViewHolder holder, int position) {
//
//            }
//        });
//        banner.setItems(bannerItems);
//
//        items = new ArrayList<>();
//        for (int i = 0; i < names.length; i++) {
//            items.add(names[i]);
//        }
//        lv_kit.setAdapter(new KitAdapter());
//
//        images = new ArrayList<>();
//        for (int i = 0; i < img_all.length; i++) {
//            images.add(img_all[i]);
//        }
//        lv_image.setAdapter(new ImageAdapter(images));
//
//
////        prl.setOnBrushRefreshListener(new BrushRequestLayout.OnBrushRefreshListener() {
////            @Override
////            public void onBrushRefresh() {
////                Log.e("RRL", "=============OnBrushRefreshListener=================");
////            }
////        });
////
////        prl.setOnBrushLoadListener(new BrushRequestLayout.OnBrushLoadListener() {
////            @Override
////            public void onBrushLoad() {
////                Log.e("RRL", "=============OnBrushLoadListener=================");
////            }
////        });
//
//        prl.setOnSwipeRefreshListener(new SwipeRequestLayout.OnSwipeRefreshListener() {
//            @Override
//            public void onSwipeRefresh() {
//
//            }
//        });
//
//        prl.setOnSwipeLoadListener(new SwipeRequestLayout.OnSwipeLoadListener() {
//            @Override
//            public void onSwipeLoad() {
//                Log.e("RRL", "=============onSwipeLoad=================");
//            }
//        });
//    }
//
//    @Override
//    public void onImageSelectSucceed(Uri uri) {
//        Log.e("RRL", "" + uri.getPath());
//        iv_ico.setImageBitmap(BitmapFactory.decodeFile(uri.getPath()));
//    }
//
//    @Override
//    public void onImageSelectFailed(String msg) {
//        Log.e("RRL", "onAcquireImageFailed " + msg);
//    }
//
//    public class KitAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return ListUtils.getSize(items);
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = null;
//            if (convertView == null) {
//                convertView = LayoutInflater.from(AndroidKit.this).inflate(R.layout.item_kit, parent, false);
//                holder = new ViewHolder();
//                ViewUtils.inject(holder, convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            holder.iv_ico.setVisibility(View.GONE);
//
//            Random random = new Random();
//            int r = random.nextInt(256);
//            int g = random.nextInt(256);
//            final int b = random.nextInt(256);
//
//            holder.tv_content.setBackgroundColor(Color.rgb(r, g, b));
//            holder.tv_content.setTextColor(Color.WHITE);
//            holder.tv_content.setText(items.get(position) + "  (#" + r + g + b + ")");
//
//            holder.tv_content.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String text = items.get(position);
//                    switch (text) {
//                        case "Refresh Completed":
//                            prl.setRefreshing(false);
//                            break;
//                        case "Load More Completed":
//                            prl.setLoading(false);
//                            break;
//                        case "Show Dialog Loading":
//                            showLoadingDialog(LoadingMode.DIALOG, "加载中，请稍后...");
//                            break;
//                        case "Show Content Loading":
//                            showLoadingDialog(LoadingMode.CONTENT, "加载中，请稍后...");
//                            break;
//                        case "Show AlertDialog":
//                            AlertDialog.Builder builder1 = new AlertDialog.Builder(AndroidKit.this);
//                            builder1.msg("测试弹框");
//                            builder1.confirm("确定");
//                            builder1.cancel("取消");
//                            builder1.translucent(true);
//                            builder1.build().show();
//                            break;
//                        case "Dismiss Loading":
//                            dismissLoadingDialog();
//                            break;
//                        case "Show Date Time Dialog":
//                            new DateSelector.Builder(AndroidKit.this).type(DateSelector.TYPE_DATE).listener(new OnDateSelectListener() {
//                                @Override
//                                public void onDateSelected(String date) {
//                                    showToast(date);
//                                }
//                            }).year(1992).month(12).day(24).build().show();
//                            break;
//                        case "Show Image Selector Dialog":
//                            showImageSelector(new ImageSelector.Builder(AndroidKit.this).crop(false));
//                            break;
//                        case "Show Succeed Toast":
//                            showToast(ToastMode.SUCCEED, "提交成功");
//                            break;
//                        case "Show Failed Toast":
//                            showToast(ToastMode.FAILURE, "提交失败");
//                            break;
//                        case "Show Alert Dialog Confirm":
//                            new AlertDialog.Builder(AndroidKit.this).msg("你是在测试我吗？").cancel("取消").confirm("确认").listener(null).build().show();
//                            break;
//                        case "Show Alert Dialog Alert":
//                            new AlertDialog.Builder(AndroidKit.this).msg("你是在测试我吗？").confirm("确认").listener(null).build().show();
//                            break;
//                        case "Show Normal Toast":
//                            showToast("登录成功");
//                            break;
//                        case "Show Exception View":
//                            showExceptionDialog("登录失败");
//                            break;
//                        case "HttpUrlConnect":
////                            RequestParams params = new RequestParams();
////                            params.add("mobile", "13758304933");
////                            params.add("pwd", "87888888");
////                            HucHttp.post("https://106.14.176.215:8443/sg/user/login ", params, AndroidKit.this);
//
////                            RequestParams params7 = new RequestParams();
////                            params7.add("page", "1");
////                            params7.add("type", "1");
////                            HucHttp.post("http://49.4.70.32:1904/sg/user/login", params7, AndroidKit.this);
//
//
//                            HucCookie.remove("106.14.176.215");
//                            RequestParams params3 = new RequestParams();
//                            params3.add("mobile", "15888333664");
//                            params3.add("pwd", "123456");
//                            HucHttp.post("https://106.14.176.215:8081/sg/user/login", params3, AndroidKit.this);
//
//
////                            RequestParams params2 = new RequestParams();
////                            params2.add("page", "1");
////                            params2.add("limit", "5");
////                            HucHttp.post( "https://106.14.176.215:8081/sg/book/list", params2, AndroidKit.this);
//
////                            HucHttp.post("http://49.4.70.32:1904/sg/user/login", params3, AndroidKit.this);
////                            HucHttp.post("http://49.4.70.32:1904/sg/user/login", params3, AndroidKit.this);
////                            HucHttp.post("http://49.4.70.32:1904/sg/user/login", params3, AndroidKit.this);
////                            HucHttp.post("http://49.4.70.32:1904/sg/user/login", params3, AndroidKit.this);
//
//                            break;
//                        case "OkHttp":
//
//                            OkCookie.remove("106.14.176.215");
//                            RequestParams params8 = new RequestParams();
//                            params8.add("mobile", "13758304933");
//                            params8.add("pwd", "87888888");
//                            OkHttp.post("https://106.14.176.215:8443/sg/user/login", params8, AndroidKit.this);
//
////                            RequestParams params6 = new RequestParams();
////                            params6.add("page", "1");
////                            params6.add("type", "1");
////                            params6.add("limit", "10");
////                            params6.add("uid", "21");
////                            OkHttp.post("http://49.4.70.32:1904/sg/user/login", params6, AndroidKit.this);
////                            OkHttp.post("http://49.4.70.32:1904/sg/user/login", params6, AndroidKit.this);
////                            OkHttp.post("http://49.4.70.32:1904/sg/user/login", params6, AndroidKit.this);
////                            OkHttp.post("http://49.4.70.32:1904/sg/user/login", params6, AndroidKit.this);
////                            OkHttp.post("http://49.4.70.32:1904/sg/user/login", params6, AndroidKit.this);
//                            break;
//                        case "HttpCookie":
//                            RequestParams params4 = new RequestParams();
//                            OkHttp.post("http://192.168.1.225:8085/dl/learning/answeredOrNotLearningCount", params4, AndroidKit.this);
//                            break;
//                        case "GetCookie":
//                            showToast(new OkCookie().getCookie("106.14.176.215").toString());
//                            Log.e("RRL", "" + new HucCookie().getCookie("106.14.176.215").toString());
//                            break;
//                        case "Downloader-Start":
//                            HucDownloader.Builder builder = new HucDownloader.Builder()
//                                    .url(file_url)
//                                    .name("Git.zip")
//                                    .folder("Downloader")
//                                    .isBreakpoint(true)
//                                    .listener(new OnDownloadListener() {
//                                        @Override
//                                        public void onDownloading(long total, long progress, int percent) {
//
//                                        }
//
//                                        @Override
//                                        public void onDownloadCompleted(File file) {
//
//                                        }
//
//                                        @Override
//                                        public void onDownloadFailed(Exception e) {
//
//                                        }
//
//                                    });
//                            downloader = new HucDownloader(builder);
//                            downloader.start();
//                            break;
//                        case "Downloader-Pause":
//                            downloader.pause();
//                            com.android.utils.Log.i("Relin", "xxxxxxx");
//                            break;
//                        case "Downloader-Cancel":
//                            downloader.cancel();
//                            break;
//                        case "startGalleryActivity":
//                            ImageSelector.Builder selectorBuilder1 = new ImageSelector.Builder(AndroidKit.this);
//                            selectorBuilder1.crop(false);
//                            selectorBuilder1.aspectX(2);
//                            selectorBuilder1.aspectY(2);
//                            selectorBuilder1.size(200);
//                            startGalleryActivity(selectorBuilder1);
//                            break;
//                        case "startCameraActivity":
//                            ImageSelector.Builder selectorBuilder2 = new ImageSelector.Builder(AndroidKit.this);
//                            selectorBuilder2.crop(false);
//                            startCameraActivity(selectorBuilder2);
//                            break;
//                        case "ShowAddressDialog":
//                            new AddressSelector.Builder(AndroidKit.this).listener(new OnAddressSelectListener() {
//                                @Override
//                                public void onAddressSelected(String province, String city, String district, String provinceId, String cityId, String districtId) {
//                                    Log.e("RRL", province + city + district + "-" + provinceId + "," + cityId + "," + districtId);
//                                    showToast(province + city + district + "-" + provinceId + "," + cityId + "," + districtId);
//                                }
//                            }).provinceId("3")
//                                    .cityId("36")
//                                    .area("398")
//                                    .build().show();
//                            break;
//                        case "JsonParser":
////                            String json = IOUtils.readAssets(AndroidKit.this,"entity.json");
////                            JsonEntity entity = JsonParser.parseJSONObject(JsonEntity.class, json);
////                            showToast(entity.getData().getRecords().get(0).getImgUri()+"");
//                            break;
//                        case "Update":
//                            Update.show(AndroidKit.this, R.drawable.android_ic_loading_01, "AndroidKit", "http://192.168.1.33:8080/app/SG_201906171.apk", "有新版本", true);
//                            break;
//                        case "ItemDialog":
//                            List<ItemDialogBody> bodies = new ArrayList<>();
//                            String names[] = new String[]{"重庆邮电大学", "重庆大学", "重庆科技大学", "重庆交通大学"};
//                            for (int i = 0; i < names.length; i++) {
//                                ItemDialogBody body = new ItemDialogBody();
//                                body.setName(names[i]);
//                                bodies.add(body);
//                            }
//                            ItemDialog.Builder itemBuilder = new ItemDialog.Builder(AndroidKit.this);
//                            itemBuilder.title("选择大学");
//                            itemBuilder.bodies(bodies);
//                            itemBuilder.listener(new ItemDialog.OnItemDialogClickListener() {
//                                @Override
//                                public void onItemDialogClick(Dialog dialog, List<ItemDialogBody> bodies, int position) {
//                                    showToast(bodies.get(position).getName());
//                                }
//                            });
//                            itemBuilder.build();
//                            break;
//                        case "VideoRecord":
//                            startActivityForResult(VideoRecordAty.class,null,520);
//                            break;
//                        case "ItemSelector":
//                            ItemSelector.Builder builder2 = new ItemSelector.Builder(AndroidKit.this);
//                            builder2.items(new String[]{"A", "B", "C"});
//                            builder2.listener(new OnItemSelectListener() {
//                                @Override
//                                public void onItemSelect(String content, int position) {
//                                    showToast(content + " - " + position);
//                                }
//                            });
//                            builder2.build();
//                            break;
//                    }
//                }
//            });
//            return convertView;
//        }
//
//        private class ViewHolder {
//            @ViewInject(R.id.tv_content)
//            private TextView tv_content;
//            @ViewInject(R.id.iv_ico)
//            private ImageView iv_ico;
//        }
//    }
//
//    @Override
//    public void onHttpSucceed(HttpResponse response) {
//        super.onHttpSucceed(response);
//        if (response.url().contains("/sg/user/login")) {
//            RequestParams params2 = new RequestParams();
//            params2.add("page", "1");
//            params2.add("limit", "5");
//            OkHttp.post("https://106.14.176.215:8081/sg/book/list", params2, AndroidKit.this);
//        }
//    }
//
//    public class ImageAdapter extends BaseAdapter {
//
//        private List<String> list;
//
//        public ImageAdapter(List<String> list) {
//            this.list = list;
//        }
//
//        @Override
//        public int getCount() {
//            return ListUtils.getSize(list);
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            ViewHolder holder;
//            if (convertView == null) {
//                convertView = LayoutInflater.from(AndroidKit.this).inflate(R.layout.item_kit, parent, false);
//                holder = new ViewHolder();
//                ViewUtils.inject(holder, convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            holder.tv_content.setVisibility(View.GONE);
////            Glide.with(AndroidKit.this).load(list.get(position)).into(holder.iv_ico);
//            return convertView;
//        }
//
//        private class ViewHolder {
//            @ViewInject(R.id.tv_content)
//            private TextView tv_content;
//            @ViewInject(R.id.iv_ico)
//            private ImageView iv_ico;
//        }
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Bundle bundle = new Bundle();
//        //限制录制多少秒，如果不限制就传0
//        bundle.putLong(VideoRecordAty.VIDEO_DURATION,60*1000);
//        startActivityForResult(VideoRecordAty.class,null,520);
//        if (resultCode==RESULT_OK&&requestCode==520){
//            //视频信息
//            String path = data.getStringExtra(VideoRecordAty.VIDEO_PATH);
//            String width = data.getStringExtra(VideoRecordAty.VIDEO_WIDTH);
//            String height = data.getStringExtra(VideoRecordAty.VIDEO_HEIGHT);
//            String duration = data.getStringExtra(VideoRecordAty.VIDEO_DURATION);
//        }
//    }
//}
