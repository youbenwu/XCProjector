package com.outmao.xcprojector.image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.outmao.xcprojector.databinding.ActivityImagePagerBinding;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ImagePagerActivity extends AppCompatActivity {

    private ActivityImagePagerBinding binding;

    private boolean auto=true;

    private List<String> images;

    private Timer timer;

    private long activeTime=0;

    private final Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            long now=System.currentTimeMillis()-10000;
            if(now>activeTime) {
                onTime();
            }
            return true;
        }
    });

    private void startTimer(){

        if(timer==null){
            timer=new Timer();
            TimerTask task=new TimerTask() {
                @Override
                public void run() {
                    handler.sendMessage(new Message());
                }
            };
            timer.schedule(task,3000,3000);

        }
    }
    private void stopTimer(){
       if(timer!=null){
           timer.cancel();
           timer=null;
       }
    }

    private void onTime(){
        if(binding.viewPager.getCurrentItem()<binding.viewPager.getAdapter().getItemCount()-1){
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem()+1);
        }else{
            binding.viewPager.setCurrentItem(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityImagePagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String json=getIntent().getStringExtra("data");
        if(json!=null) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            images = new Gson().fromJson(json,listType);
        }

        binding.viewPager.setAdapter(adapter);
        binding.viewPager.registerOnPageChangeCallback(onPageChangeCallback);
        updatePageBar();
        if(auto) {
            startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        stopTimer();
        super.onDestroy();
    }

    private void updatePageBar(){
        binding.tvPage.setText("第"+(binding.viewPager.getCurrentItem()+1)+"页 共"+binding.viewPager.getAdapter().getItemCount()+"页");
    }

   private final ViewPager2.OnPageChangeCallback onPageChangeCallback=new ViewPager2.OnPageChangeCallback() {
       @Override
       public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
           super.onPageScrolled(position, positionOffset, positionOffsetPixels);
       }

       @Override
       public void onPageSelected(int position) {
           super.onPageSelected(position);
           updatePageBar();
       }

       @Override
       public void onPageScrollStateChanged(int state) {
           super.onPageScrollStateChanged(state);
       }
   };

    private final FragmentStateAdapter adapter=new FragmentStateAdapter(this) {
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return ImageFragment.newInstance(images.get(position));
        }
        @Override
        public int getItemCount() {
            return images!=null?images.size():0;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT
                ||event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){
            activeTime=System.currentTimeMillis();
        }
        if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT){
            if(binding.viewPager.getCurrentItem()>0){
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem()-1);
            }
        }else if(event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT){
            if(binding.viewPager.getCurrentItem()<binding.viewPager.getAdapter().getItemCount()-1){
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem()+1);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}