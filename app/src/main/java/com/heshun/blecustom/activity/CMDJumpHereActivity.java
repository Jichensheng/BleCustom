package com.heshun.blecustom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.heshun.blecustom.R;


/**
 * author：Jics
 * 2017/6/5 17:56
 */
public class CMDJumpHereActivity extends AppCompatActivity {
	private TextView textView;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cmd_jump_here);
		Intent intent=getIntent();
		String CMDCode=intent.getStringExtra("CMDCode");
		String result=intent.getStringExtra("result");
		textView= (TextView) findViewById(R.id.tv_response);
		textView.setText("当前指令为:"+CMDCode+"\n"+result);

	}
}
