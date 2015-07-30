package com.monroe.magiceightball;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.monroe.magiceightball.R;

public class BottomHalf extends android.support.v4.app.Fragment
{
        private static EditText responseBlock;


        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.fragment_bottom_half, container, false);

            responseBlock = (EditText) view.findViewById(R.id.responseBlock);

            return view;
        }

    public void setResponseBlock(String respond)
    {
        responseBlock.setText(respond);
    }
}