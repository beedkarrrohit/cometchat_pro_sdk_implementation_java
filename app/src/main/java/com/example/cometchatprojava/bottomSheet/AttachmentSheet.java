package com.example.cometchatprojava.bottomSheet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.example.cometchatprojava.R;
import com.example.cometchatprojava.databinding.LayoutBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AttachmentSheet extends BottomSheetDialogFragment implements View.OnClickListener {
     LayoutBottomSheetBinding binding ;
     BottomSheetListener bottomSheetListener;
    private static final String TAG = "AttachmentSheet";
    @Override
    public View onCreateView( LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet,container,false);
        binding = LayoutBottomSheetBinding.bind(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        binding.sendImage.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            bottomSheetListener =(BottomSheetListener) context;
        }catch (Exception e){
            Log.e(TAG, "onAttach: "+ e.getMessage());
        }
    }

    public interface BottomSheetListener{
        public void onSheetItemClicked(int id);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.send_image){
            bottomSheetListener.onSheetItemClicked(id);
            dismiss();
        }
    }
}
