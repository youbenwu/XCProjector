package com.outmao.xcprojector;

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

import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.SlideInfo;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.api.models.SlideListSubSlides;
import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.databinding.FragmentHomeBinding;
import com.outmao.xcprojector.databinding.FragmentMainMenusBinding;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    public HomeFragment() {

    }

    public View getViewById(int id){
        if(binding.viewPager.getAdapter()==null)
            return null;
        Fragment f=((SlidesAdapter)binding.viewPager.getAdapter()).getFragment(binding.viewPager.getCurrentItem());
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

    private void initViewPager(SlideListData data){
        binding.viewPager.setAdapter(new SlidesAdapter(this.getActivity(),data));
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.d("onPageSelected",position+"");
                super.onPageSelected(position);
                SlidesAdapter adapter=(SlidesAdapter)binding.viewPager.getAdapter();
                adapter.getFragments().forEach(new BiConsumer<String, SlideListFragment>() {
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
        loadData();
    }

    public final class SlidesAdapter extends FragmentStateAdapter {
        SlideListData data;

        private Map<String,SlideListFragment> _fragments=new HashMap<>();

        public SlidesAdapter(@NonNull FragmentActivity fragmentActivity, SlideListData data) {
            super(fragmentActivity);
            this.data=data;
        }
        @Override
        public Fragment createFragment(int position) {
            SlideListFragment fragment= SlideListFragment.newInstance(position+1,position==0?data:null);
            _fragments.put(position+"",fragment);
            return fragment;
        }
        @Override
        public int getItemCount() {
            return data!=null?data.getSub_slides().getLast_page():0;
        }

        public SlideListFragment getFragment(int position){
            return _fragments.get(position+"");
        }

        public Map<String, SlideListFragment> getFragments() {
            return _fragments;
        }
    }

    private void loadData(){
        HttpApiService.getInstance().slide_list(1,5)
                .subscribe(new RxSubscriber<YYResponseData<SlideListData>>() {
                    @Override
                    public void onFail(YYResponseData<SlideListData> responseData) {
                        super.onFail(responseData);
                        Toast.makeText(getContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        if(AppConfig.testData){
                            showTestData();
                        }
                    }

                    @Override
                    public void onSuccess(YYResponseData<SlideListData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("slide_list接口返回", responseData.toString());
                        if(responseData.isSuccess()){
                            SlideListData data=responseData.getData();
                            if(data.getSub_slides()!=null&&data.getSub_slides().getLast_page()>0&&data.getSub_slides().getList().size()>0){
                                initViewPager(data);
                                return;
                            }else{
                                Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        if(AppConfig.testData){
                            showTestData();
                        }
                    }

                });
    }

    private void showTestData(){
        SlideListData data=new SlideListData();
        SlideInfo info1=new SlideInfo();
        info1.setThumbs_txt(new ArrayList<>());
        info1.getThumbs_txt().add("https://qn.huwing.cn/2023/09/01/16935609583821101-1920_1080.jpg");
        info1.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1114/062621110J7/210626110J7-10-1200.jpg");
        info1.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1113/031920120534/200319120534-7-1200.jpg");
        info1.setType(1);

        SlideInfo info2=new SlideInfo();
        info2.setThumbs_txt(new ArrayList<>());
        info2.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/4k/s/02/210924233115O14-0-lp.jpg");
        info2.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1114/062621110J7/210626110J7-10-1200.jpg");
        info2.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1113/031920120534/200319120534-7-1200.jpg");
        info2.setVideo_url_txt("http://tengdamy.cn/video/video2.mp4");
        info2.setType(2);

        data.setMain_slide(info2);
        SlideListSubSlides subSlides=new SlideListSubSlides();
        subSlides.setLast_page(3);
        subSlides.setList(new ArrayList<>());
        subSlides.getList().add(info1);
        subSlides.getList().add(info2);
        subSlides.getList().add(info1);
        subSlides.getList().add(info2);
        subSlides.getList().add(info1);
        data.setSub_slides(subSlides);

        initViewPager(data);

    }



}