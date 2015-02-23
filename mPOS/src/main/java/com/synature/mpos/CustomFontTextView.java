package com.synature.mpos;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomFontTextView extends TextView{

	public static final String FONT_PATH = "fonts/DroidSansMono.ttf";
	
	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CustomFontTextView(Context context){	
		super(context);
	}
	
	@Override
	public void setTypeface(Typeface tf) {
		tf = Typeface.createFromAsset(getContext().getAssets(), FONT_PATH);
		super.setTypeface(tf);
	}
	
}
