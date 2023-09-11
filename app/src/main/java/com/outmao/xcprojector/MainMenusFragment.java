package com.outmao.xcprojector;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.outmao.xcprojector.databinding.FragmentMainMenusBinding;


public class MainMenusFragment extends Fragment {

    public final static int MENU_ACTION_HOME=0;
    public final static int MENU_ACTION_SETUP=1;
    public final static int MENU_ACTION_MOVIES=2;
    private FragmentMainMenusBinding binding;

    private MainMenusListener listener;

    public MainMenusFragment() {
    }

    public MainMenusListener getListener() {
        return listener;
    }

    public void setListener(MainMenusListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.menuHome.setTag(MENU_ACTION_HOME);
        binding.menuSetup.setTag(MENU_ACTION_SETUP);
        binding.menuMovies.setTag(MENU_ACTION_MOVIES);
        binding.menuHome.iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem(binding.menuHome,true);
            }
        });
        binding.menuSetup.iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem(binding.menuSetup,true);
            }
        });

        binding.menuMovies.iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem(binding.menuMovies,true);
            }
        });
        onClickItem(binding.menuHome,false);
    }

    public void setSelectedItem(int action){
        View v=getView().findViewWithTag(action);
        onClickItem(v,false);
    }

    private void onClickItem(View v,boolean action){
        if(listener!=null&&action){
            boolean r=listener.onMenu((int)v.getTag());
            if(r) {
                binding.menuHome.setSelected(binding.menuHome==v);
                binding.menuSetup.setSelected(binding.menuSetup==v);
                binding.menuMovies.setSelected(binding.menuMovies==v);
            }
        }else{
            binding.menuHome.setSelected(binding.menuHome==v);
            binding.menuSetup.setSelected(binding.menuSetup==v);
            binding.menuMovies.setSelected(binding.menuMovies==v);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainMenusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    public interface MainMenusListener{
        boolean onMenu(int action);
    }

}