package br.com.halyson.ensharp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import br.com.halyson.ensharp.R;
import br.com.halyson.ensharp.activity.Ins1Activity;
import br.com.halyson.ensharp.activity.Ins2Activity;
import br.com.halyson.ensharp.activity.Ins3Activity;
import br.com.halyson.ensharp.activity.Ins4Activity;
import br.com.halyson.ensharp.activity.MyCctvActivity;

/**
 * Created by halyson on 18/12/14.
 */
public class Fragment3 extends Fragment {
    public static Fragment3 newInstance() {
        return new Fragment3();
    }
    private View mViewFragment3;

    ImageButton btn_ins1;
    ImageButton btn_ins2;
    ImageButton btn_ins3;
    ImageButton btn_ins4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewFragment3 = inflater.inflate(R.layout.fragment_3, container, false);

        btn_ins1 = (ImageButton) mViewFragment3.findViewById(R.id.btn_ins1);
        btn_ins1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_ins1 = new Intent(getActivity().getApplicationContext(), Ins1Activity.class);
                startActivity(intent_ins1);
            }
        });
        btn_ins2 = (ImageButton) mViewFragment3.findViewById(R.id.btn_ins2);
        btn_ins2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_ins2 = new Intent(getActivity().getApplicationContext(), Ins2Activity.class);
                startActivity(intent_ins2);
            }
        });
        btn_ins3 = (ImageButton) mViewFragment3.findViewById(R.id.btn_ins3);
        btn_ins3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_ins3 = new Intent(getActivity().getApplicationContext(), Ins3Activity.class);
                startActivity(intent_ins3);
            }
        });
        btn_ins4 = (ImageButton) mViewFragment3.findViewById(R.id.btn_ins4);
        btn_ins4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_ins4 = new Intent(getActivity().getApplicationContext(), Ins4Activity.class);
                startActivity(intent_ins4);
            }
        });

        return mViewFragment3;
    }
}
