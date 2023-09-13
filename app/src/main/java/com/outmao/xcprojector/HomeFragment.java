package com.outmao.xcprojector;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.AccountStatusData;
import com.outmao.xcprojector.api.models.SlideInfo;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.api.models.SlideListSubSlides;
import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.databinding.FragmentHomeBinding;
import com.outmao.xcprojector.databinding.FragmentMainMenusBinding;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.util.SharepreferencesUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class HomeFragment extends Fragment {

    public static final String save_key="slide-data";

    private FragmentHomeBinding binding;

    private int pageCount=0;

    private SlideListData data;

    private Map<String,SlideListFragment> _fragments=new HashMap<>();

    public HomeFragment() {

    }

    public View getViewById(int id){
        if(binding.viewPager.getAdapter()==null)
            return null;
        Fragment f=_fragments.get(binding.viewPager.getCurrentItem()+"");
        if(f==null)
            return null;
        View view=f.getView().findViewById(id);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public boolean nextPage(){
        if(binding.viewPager.getAdapter()==null)
            return false;
        if(binding.viewPager.getCurrentItem()<binding.viewPager.getAdapter().getItemCount()-1){
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem()+1);
            return true;
        }
        return false;
    }

    public boolean backPage(){
        if(binding.viewPager.getAdapter()==null)
            return false;
        if(binding.viewPager.getCurrentItem()>0){
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem()-1);

            return true;
        }
        return false;
    }

    private void initViewPager(){
        binding.viewPager.setAdapter(new PagerStateAdapter(getActivity()));
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected",position+"");
                super.onPageSelected(position);
                _fragments.forEach(new BiConsumer<String, SlideListFragment>() {
                    @Override
                    public void accept(String s, SlideListFragment slideListFragment) {
                        slideListFragment.onPageSelected(s.equals(position+""));
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadLocalData();
        initViewPager();
        loadData();
    }

    private final class PagerStateAdapter extends FragmentStateAdapter {


        public PagerStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            SlideListFragment fragment=_fragments.get(position+"");
            //if(fragment==null) {
                fragment = SlideListFragment.newInstance(position + 1, position==0?data:null);
                _fragments.put(position+"",fragment);
            //}
            return fragment;
        }
        @Override
        public int getItemCount() {
            return pageCount;
        }
    };

    private void loadLocalData(){
//        String json=SharepreferencesUtils.getShareInstance().getString(HomeFragment.save_key+"-1");
//        if(json!=null) {
//            SlideListData data = new Gson().fromJson(json, SlideListData.class);
//            if(data!=null){
//                pageCount=data.getSub_slides().getLast_page();
//            }
//        }
    }

    private void loadData(){
        HttpApiService.getInstance().slide_list(1,5)
                .subscribe(new RxSubscriber<YYResponseData<SlideListData>>() {
                    @Override
                    public void onFail(YYResponseData<SlideListData> responseData) {
                        super.onFail(responseData);
                        Toast.makeText(getContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        checkState();
                    }

                    @Override
                    public void onSuccess(YYResponseData<SlideListData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("slide_list接口返回", responseData.toString());
                        if(responseData.isSuccess()){
                            data=responseData.getData();
                            if(data.getSub_slides()!=null&&data.getSub_slides().getLast_page()>0&&data.getSub_slides().getList().size()>0){
                                SharepreferencesUtils.getShareInstance().putString(save_key+"-1",new Gson().toJson(data));
                                pageCount=data.getSub_slides().getLast_page();
                                binding.viewPager.getAdapter().notifyDataSetChanged();
                                return;
                            }else{
                                Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }


    private void checkState(){
        HttpApiService.getInstance().account_status()
                .subscribe(new RxSubscriber<YYResponseData<AccountStatusData>>() {
                    @Override
                    public void onFail(YYResponseData<AccountStatusData> responseData) {
                        super.onFail(responseData);
                        Log.d("account_status接口返回", responseData.toString());
                    }

                    @Override
                    public void onSuccess(YYResponseData<AccountStatusData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("account_status接口返回", responseData.toString());
                        if(responseData.isSuccess()){
                            if(responseData.getData().getStatus()==0){
                                //未激活
                                goActivate();
                                getActivity().finish();
                                return;
                            }
                        }
                    }
                });
    }

    //去激活设备
    private void goActivate(){
        Intent intent = new Intent(getActivity(), ActivateActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
    }

}