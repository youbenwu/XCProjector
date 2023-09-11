package com.outmao.xcprojector;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.outmao.xcprojector.api.HttpApiService;
import com.outmao.xcprojector.api.models.SlideInfo;
import com.outmao.xcprojector.api.models.SlideListData;
import com.outmao.xcprojector.api.models.SlideListSubSlides;
import com.outmao.xcprojector.config.AppConfig;
import com.outmao.xcprojector.databinding.FragmentSlideListBinding;
import com.outmao.xcprojector.image.ImagePagerActivity;
import com.outmao.xcprojector.network.RxSubscriber;
import com.outmao.xcprojector.network.YYResponseData;
import com.outmao.xcprojector.video.VideoPlayActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SlideListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlideListFragment extends Fragment {

    private FragmentSlideListBinding binding;

    private static final String ARG_PAGE = "page";
    private static final String ARG_DATA = "data";
    private int page;

    private SlideListData data;

    public SlideListFragment() {
    }


    public static SlideListFragment newInstance(int page,SlideListData data) {
        SlideListFragment fragment = new SlideListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        if(data!=null) {
            args.putString(ARG_DATA, data.toString());
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getInt(ARG_PAGE);
            String json=getArguments().getString(ARG_DATA);
            if(json!=null){
                data=SlideListData.fromJson(json);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateData();
        if(data==null){
          loadData();
        }
    }


    private void loadData(){
        HttpApiService.getInstance().slide_list(1,5)
                .subscribe(new RxSubscriber<YYResponseData<SlideListData>>() {
                    @Override
                    public void onFail(YYResponseData<SlideListData> responseData) {
                        super.onFail(responseData);
                        Toast.makeText(SlideListFragment.this.getContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        if(AppConfig.testData){
                            showTestData();
                        }
                    }

                    @Override
                    public void onSuccess(YYResponseData<SlideListData> responseData) {
                        super.onSuccess(responseData);
                        Log.d("slide_list接口返回", responseData.toString());
                        if(responseData.isSuccess()){
                            data=responseData.getData();
                            if(data.getSub_slides()!=null&&data.getSub_slides().getLast_page()>0&&data.getSub_slides().getList().size()>0) {
                                updateData();
                                return;
                            }
                        }else{
                            Toast.makeText(SlideListFragment.this.getContext(), responseData.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        if(AppConfig.testData){
                            showTestData();
                        }
                    }

                });
    }


    private void updateData(){
        binding.rlView1.setVisibility(View.INVISIBLE);
        binding.rlView2.setVisibility(View.INVISIBLE);
        binding.rlView3.setVisibility(View.INVISIBLE);
        binding.rlView4.setVisibility(View.INVISIBLE);
        binding.rlView5.setVisibility(View.INVISIBLE);
        binding.rlView6.setVisibility(View.INVISIBLE);
        if(data!=null){
            //主图
            if(data.getMain_slide()!=null){
                SlideInfo info=data.getMain_slide();
                binding.videoView1.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                binding.rlView1.setVisibility(View.VISIBLE);
            }


            if(data.getSub_slides()!=null&&data.getSub_slides().getList()!=null){
                //2
                if(data.getSub_slides().getList().size()>0){
                    SlideInfo info=data.getSub_slides().getList().get(0);
                    binding.videoView2.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.rlView2.setVisibility(View.VISIBLE);
                }

                //3
                if(data.getSub_slides().getList().size()>1){
                    SlideInfo info=data.getSub_slides().getList().get(1);
                    binding.videoView3.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.rlView3.setVisibility(View.VISIBLE);
                }

                //4
                if(data.getSub_slides().getList().size()>2){
                    SlideInfo info=data.getSub_slides().getList().get(2);
                    binding.videoView4.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.rlView4.setVisibility(View.VISIBLE);
                }

                //5
                if(data.getSub_slides().getList().size()>3){
                    SlideInfo info=data.getSub_slides().getList().get(3);
                    binding.videoView5.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.rlView5.setVisibility(View.VISIBLE);
                }

                //6
                if(data.getSub_slides().getList().size()>4){
                    SlideInfo info=data.getSub_slides().getList().get(4);
                    binding.videoView6.initData(info.getVideo_url_txt(),info.getThumbs_txt());
                    binding.rlView6.setVisibility(View.VISIBLE);
                }

            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSlideListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    private void showTestData(){
        SlideListData data=new SlideListData();
        SlideInfo info1=new SlideInfo();
        info1.setThumbs_txt(new ArrayList<>());
        info1.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/4k/s/02/210924233115O14-0-lp.jpg");
        info1.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1114/062621110J7/210626110J7-10-1200.jpg");
        info1.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1113/031920120534/200319120534-7-1200.jpg");
        info1.setType(1);

        SlideInfo info2=new SlideInfo();
        info2.setThumbs_txt(new ArrayList<>());
        info2.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/4k/s/02/210924233115O14-0-lp.jpg");
        info2.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1114/062621110J7/210626110J7-10-1200.jpg");
        info2.getThumbs_txt().add("https://lmg.jj20.com/up/allimg/1113/031920120534/200319120534-7-1200.jpg");
        info2.setVideo_url_txt("https://lmg.jj20.com/up/allimg/4k/s/02/210924233115O14-0-lp.jpg");
        info2.setType(2);

        data.setMain_slide(info2);
        SlideListSubSlides subSlides=new SlideListSubSlides();
        subSlides.setLast_page(2);
        subSlides.setList(new ArrayList<>());
        subSlides.getList().add(info1);
        subSlides.getList().add(info2);
        subSlides.getList().add(info1);
        subSlides.getList().add(info2);
        subSlides.getList().add(info1);
        data.setSub_slides(subSlides);

        this.data=data;
        updateData();

    }

}