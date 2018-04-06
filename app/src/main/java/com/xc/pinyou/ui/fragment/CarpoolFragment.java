package com.xc.pinyou.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.adapter.WheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.OnItemSelectedListener;
import com.example.zhouwei.library.CustomPopWindow;
import com.gyf.barlibrary.ImmersionBar;
import com.xc.pinyou.R;
import com.xc.pinyou.base.BaseFragment;
import com.xc.pinyou.base.BaseRecyclerAdapter;
import com.xc.pinyou.base.BaseRecyclerHolder;
import com.xc.pinyou.bean.AddressBean;
import com.xc.pinyou.bean.Carpool;
import com.xc.pinyou.bean.Credibility;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.utils.TimeUtil;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xum19 on 2018/2/7.
 */

public class CarpoolFragment extends BaseFragment implements LocationSource, AMapLocationListener, PoiSearch.OnPoiSearchListener, RouteSearch.OnRouteSearchListener {

    private static final String TAG = "CarpoolFragment";

    private TextureMapView textureMapView;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private OnLocationChangedListener mListener;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption clientOption;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private AMapNavi aMapNavi;

    private String strLocation = null, strPeople = String.valueOf(2);

    private ImmersionBar immersionBar;

    private RelativeLayout rl, rlStartPoint, rlEndPoint, rlStartTime, rlPeople, rlSure;

    private TextView tvLocation, tvCancel, tvStattPoint, tvEndPoint, tvStartTime, tvPeople;
    private EditText etLocation;
    private RecyclerView recyclerView;
    private BaseRecyclerAdapter<PoiItem> adapter;
    private Button btnSure;

    private List<AddressBean> addressBeans;

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private LatLonPoint latLonPoint;
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems;// poi数据
    private String keyWord;

    private int flag = 0;

    private LatLonPoint startPoint , endPoint;

    private CustomPopWindow popuPeopleSelect;

    private Date startTime;

    private Integer people = 1;

    private String money = null;

    private String bmobStartPoint = "起点", bmobEndPoint = "终点";

    private float credibility = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("拼车");
        initMap(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.app_qing).fitsSystemWindows(true).init();
        return R.layout.fragment_carpool;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            immersionBar = ImmersionBar.with(this);
            immersionBar.statusBarColor(R.color.app_qing).fitsSystemWindows(true).init();
        }
    }

    @Override
    protected void findViews() {

        textureMapView = getView(R.id.carpool_mapview);

        rl = getView(R.id.rl_search);
        rlStartPoint = getView(R.id.rl_start_point);
        rlEndPoint = getView(R.id.rl_end_point);
        rlStartTime = getView(R.id.rl_start_time);
        rlPeople = getView(R.id.rl_people);
        rlSure = getView(R.id.rl_sure);

        tvLocation = getView(R.id.tv_location);
        etLocation = getView(R.id.et_location);
        tvCancel = getView(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);
        etLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                doSearchQuery(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        recyclerView = getView(R.id.recycleview);

        tvStattPoint = getView(R.id.tv_start_point);
        tvEndPoint = getView(R.id.tv_end_point);
        tvStartTime = getView(R.id.tv_start_time);
        tvPeople = getView(R.id.tv_people);
        tvStattPoint.setOnClickListener(this);
        tvEndPoint.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvPeople.setOnClickListener(this);

        btnSure = getView(R.id.btn_sure);
        btnSure.setOnClickListener(this);

    }

    protected void doSearchQuery(CharSequence charSequence) {

        keyWord = charSequence.toString().trim();
        Log.i(TAG, "doSearchQuery: " + keyWord + " " + strLocation);
        currentPage = 0;
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query = new PoiSearch.Query(keyWord, null, strLocation);
        query.setPageSize(30);// 设置每页最多返回多少条poiItem
        query.setPageNum(currentPage);// 设置查第一页

        poiSearch = new PoiSearch(context, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 异步搜索


    }

    @Override
    public void onPoiSearched(PoiResult result, int code) {

        //DialogUtils.dismissProgressDialog();
        if (code == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                Log.i(TAG, "onPoiSearched: " + "搜索的code为===="+code+", result数量=="+result.getPois().size());
                if (result.getQuery().equals(query)) {// 是否是同一次搜索
                    poiResult = result;
                    Log.i(TAG, "onPoiSearched: " + "搜索的code为===="+code+", result数量=="+poiResult.getPois().size());
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        poiItems.clear();
                        adapter.notifyDataSetChanged();
                    }
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始

                    initAdapter();
                    adapter.notifyDataSetChanged();
                    //通过listview显示搜索结果的操作省略

                }
            } else {
                Log.i(TAG, "onPoiSearched: " + "没有搜索结果");

            }
        } else {
            Log.i(TAG, "onPoiSearched: " + "搜索出现错误");

        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    private void initAdapter(){
        adapter = new BaseRecyclerAdapter<PoiItem>(context, poiItems, R.layout.location_item) {

            @Override
            public void convert(BaseRecyclerHolder holder, PoiItem item, int position, boolean isScrolling) {
                holder.setText(R.id.tv_location_city, item.getTitle());
                holder.setText(R.id.tv_address, item.getSnippet());
            }
        };

        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, final View view, int position) {
                Log.i(TAG, "onItemClick: " + flag);
               if (flag == 1){
                   tvStattPoint.setText(poiItems.get(position).getTitle());
                   bmobStartPoint = poiItems.get(position).getTitle();
                   startPoint = poiItems.get(position).getLatLonPoint();
                   Log.i(TAG, "onItemClick: " + startPoint.getLatitude() + " " + startPoint.getLongitude());
                   clearSearch();
               }else if (flag == 2){
                   tvEndPoint.setText(poiItems.get(position).getTitle());
                   bmobEndPoint = poiItems.get(position).getTitle();
                   endPoint = poiItems.get(position).getLatLonPoint();
                   addMarkers();
                   searchRoute();

                   aMap.moveCamera(CameraUpdateFactory.changeLatLng(
                           new LatLng((startPoint.getLatitude() + endPoint.getLatitude())/2,
                                   (startPoint.getLongitude() + endPoint.getLongitude())/2)));

                   Log.i(TAG, "onItemClick: " + endPoint.getLatitude() + " " + endPoint.getLongitude());
                   clearSearch();
               }else {

               }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
    }

    private void addMarkers() {
        LatLng start = new LatLng(startPoint.getLatitude(), startPoint.getLongitude());
        aMap.addMarker(new MarkerOptions()
                .position(start)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag)));
        LatLng end = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());
        aMap.addMarker(new MarkerOptions()
                .position(end)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.flag)));
    }

    public void searchRoute() {
        RouteSearch routeSearch = new RouteSearch(context);
        routeSearch.setRouteSearchListener(this);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo( startPoint, endPoint);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery( fromAndTo,
                RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION,
                null,
                null,
                "" );
        routeSearch.calculateDriveRouteAsyn(query);

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        List<DrivePath> drivePathList = result.getPaths();
        DrivePath drivePath = drivePathList.get(0);
        List<DriveStep> steps = drivePath.getSteps();

        for (DriveStep step : steps) {
            List<LatLonPoint> allPolyline = step.getPolyline();

            for(int k = 0; k < allPolyline.size(); k++){
                latLngs.add(new LatLng(allPolyline.get(k).getLatitude(), allPolyline.get(k).getLongitude()));
            }

            Polyline polyline =  aMap.addPolyline((new PolylineOptions())
                                 .addAll(latLngs)
                                 .width(10)
                                 .setDottedLine(false) //关闭虚线
                                 .color(Color.RED));
                                 }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


    @Override
    protected void formatViews() {
        poiItems = new ArrayList<>();

    }

    @Override
    protected void formatData() {

    }

    @Override
    protected void getBundle() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.tv_cancel:
               clearSearch();
                break;
            case R.id.tv_people:
                setlectPeople();
                break;
            case R.id.tv_start_point:
                rl.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                flag = 1;
                break;
            case R.id.tv_end_point:
                rl.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                flag = 2;
                break;
            case R.id.tv_start_time:
                showTimePicker();
                break;
            case R.id.btn_sure:
                if (TextUtils.isEmpty(bmobStartPoint)){
                    toast("请填写起点");
                    break;
                }
                if (TextUtils.isEmpty(bmobEndPoint)){
                    toast("请填写终点");
                    break;
                }
                if (tvStartTime.getText().toString() == null){
                    toast("请填写时间");
                    break;
                }
                if (TextUtils.isEmpty(String.valueOf(people))){
                    toast("请填写人数");
                    break;
                }
                fabiaoPinche();
                rlSure.setVisibility(View.GONE);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setlectPeople() {

        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.popu_people,null);
        handleLogic(contentView);
        popuPeopleSelect= new CustomPopWindow.PopupWindowBuilder(getContext())
                .setView(contentView)
                .setOutsideTouchable(true)
                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
                .setBgDarkAlpha(0.7f) // 控制亮度
                .create()
                .showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

    }

    private void handleLogic(View contentView) {

        WheelView wheelView = contentView.findViewById(R.id.wheelview);
        TextView peopleCancel = contentView.findViewById(R.id.tv_people_cancel);
        TextView peopleSure = contentView.findViewById(R.id.tv_people_sure);
        List<String> peopleNumber = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            peopleNumber.add(String.valueOf(i + 1));
            Log.i(TAG, "setlectPeople: " + peopleNumber);
        }

        wheelView.setAdapter(new ArrayWheelAdapter(peopleNumber, peopleNumber.size()));
        wheelView.setCurrentItem(1);
        wheelView.setIsOptions(true);
        wheelView.setTextSize(20);
        wheelView.setCyclic(false);
        wheelView.setDividerColor(context.getResources().getColor(R.color.app_gadblue));
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                strPeople = peopleNumber.get(index);
                people = Integer.parseInt(peopleNumber.get(index));
                rlSure.setVisibility(View.VISIBLE);
                calculateDistance();
            }
        });

        peopleSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvPeople.setText(strPeople + " 人");

                if (popuPeopleSelect != null)
                    popuPeopleSelect.dissmiss();
                rlSure.setVisibility(View.VISIBLE);
            }
        });

        peopleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popuPeopleSelect != null)
                    popuPeopleSelect.dissmiss();
            }
        });
    }

    private void fabiaoPinche() {

        MyUser user = BmobUser.getCurrentUser(MyUser.class);

        BmobQuery<Credibility> query = new BmobQuery<>();
        query.include("credibility");
        query.findObjects(new FindListener<Credibility>() {
            @Override
            public void done(List<Credibility> list, BmobException e) {
                if (e == null){
                    for (Credibility c:list){
                        if ((c.getUser().getObjectId()).equals(user.getObjectId())){
                            Carpool carpool = new Carpool();
                            carpool.setCarpoolId(user);
                            carpool.setCredibility(c);
                            carpool.setStartPointLat(startPoint.getLatitude());
                            carpool.setStartPointLon(startPoint.getLongitude());
                            carpool.setEndPointLat(endPoint.getLatitude());
                            carpool.setEndPointLon(endPoint.getLongitude());
                            carpool.setStartTime(new BmobDate(startTime));
                            carpool.setStartPoint(bmobStartPoint);
                            carpool.setEndPoint(bmobEndPoint);
                            carpool.setPeople(people);
                            carpool.setMoney(money);
                            carpool.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null){
                                        toast("发表成功");
                                        tvStattPoint.setText("");
                                        tvEndPoint.setText("");
                                        tvStartTime.setText("");
                                        tvPeople.setText("");
                                    }
                                    else
                                        Log.i(TAG, "done: " + e.getErrorCode() + " " + e.getMessage());

                                }
                            });
                        }else{

                        }
                    }
                }else {
                    Log.i(TAG, "发布拼车失败: " + e.getErrorCode() + e.getMessage());
                }
            }
        });

    }

    private void calculateDistance(){
        DecimalFormat    df   = new DecimalFormat("######0.00");
        float distance = AMapUtils.calculateLineDistance(new LatLng(startPoint.getLatitude(), startPoint.getLongitude()),
                new LatLng(endPoint.getLatitude(), endPoint.getLongitude()));
        distance = (float) (Math.round(distance/100d)/10d);
        money = df.format(Math.round(distance * 6));
        btnSure.setText(money + "元\n" + "确认发布");

    }

    private void showTimePicker() {

        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        //startDate.set(2013,1,1);
        Calendar endDate = Calendar.getInstance();
        //endDate.set(2020,1,1);

        //正确设置方式 原因：注意事项有说明
        startDate.set(2018, selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DATE));
        endDate.set(2018,11,31);
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(getActivity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date,View v) {//选中事件回调
                if (!compare_date(getTime(new Date()), getTime(date))){
                    toast("时间选择有误，请重新选择！");
                }else{
                    startTime = date;
                    tvStartTime.setText(getTime(date));
                }

            }
        })
                .setType(new boolean[]{false, true, true, true, true, false})// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(20)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("选择出发时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(context.getResources().getColor(R.color.app_black))//标题文字颜色
                .setSubmitColor(context.getResources().getColor(R.color.app_gadblue))//确定按钮文字颜色
                .setCancelColor(context.getResources().getColor(R.color.app_gray))//取消按钮文字颜色
                .setTitleBgColor(context.getResources().getColor(R.color.app_white))//标题背景颜色 Night mode
                .setBgColor(context.getResources().getColor(R.color.app_white))//滚轮背景颜色 Night mode
                .setTextColorOut(context.getResources().getColor(R.color.app_gray))
                .setTextColorCenter(context.getResources().getColor(R.color.app_gadblue))
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate,endDate)//起始终止年月日设定
                .setLabel(null,"月","日","时","分",null)//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(date);
    }

    private boolean compare_date(String dateNow, String date) {
        Log.i(TAG, "compare_date: " + dateNow);
        Log.i(TAG, "compare_date: " + date);
        boolean flag = false;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(dateNow);
            Date dt2 = df.parse(date);

        if (dt1.getTime() >= dt2.getTime()) {
           flag = false;
            Log.i(TAG, "compare_date: flag" + flag);
            return flag;
        } else if (dt1.getTime() < dt2.getTime()) {

            return true;
        } else {
            return flag;
        }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "compare_date: flag " + flag);
        return flag;
    }

    private void clearSearch(){
        rl.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        etLocation.setText("");
        Log.i(TAG, "clearSearch: " + poiItems.size());
        if (poiItems != null)
            poiItems.clear();

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        imm.hideSoftInputFromWindow(etLocation.getWindowToken(), 0);
    }

    private void initMap(Bundle savedInstanceState) {
        textureMapView.onCreate(savedInstanceState);

        if (aMap == null)
            aMap = textureMapView.getMap();

        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setLogoPosition(-50);

        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色  。
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null
                    && aMapLocation.getErrorCode() == 0) {
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                //设置缩放级别
                aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                //将地图移动到定位点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(aMapLocation);

                bmobStartPoint = aMapLocation.getDistrict() + " " + aMapLocation.getStreet();

                startPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                endPoint = startPoint;
                //添加图钉
                //  aMap.addMarker(getMarkerOptions(amapLocation));
                //获取定位信息
                StringBuffer buffer = new StringBuffer();
                buffer.append(aMapLocation.getProvince() + ""
                        + aMapLocation.getCity() + ""
                        + aMapLocation.getDistrict() + ""
                        + aMapLocation.getStreet() + ""
                        + aMapLocation.getStreetNum());

                Log.i(TAG, "getAddress: " + aMapLocation.getAddress());
                Log.i(TAG, "getDistrict: " + aMapLocation.getDistrict());
                Log.i(TAG, "getStreet: " + aMapLocation.getStreet());

                strLocation = aMapLocation.getCity();
                tvLocation.setText(strLocation);

                tvStattPoint.setText(aMapLocation.getStreet());

            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.i(TAG, "onLocationChanged: " + errText);
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (locationClient == null) {
            locationClient = new AMapLocationClient(getActivity());
            clientOption = new AMapLocationClientOption();
            locationClient.setLocationListener(this);
            clientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度定位
            clientOption.setOnceLocationLatest(true);//设置单次精确定位
            clientOption.setOnceLocation(true);
            locationClient.setLocationOption(clientOption);
            locationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        textureMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        textureMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        textureMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textureMapView.onDestroy();
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }

        if (immersionBar != null)
            immersionBar.destroy();
    }


}
