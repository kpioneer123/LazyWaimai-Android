package net.comet.lazyorder.widget.section;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.comet.lazyorder.R;

public class SectionTextItemView extends SectionItemView {

    private TextView textView;

    public SectionTextItemView(Context context) {
        this(context, null);
    }

    public SectionTextItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SectionTextItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public String getText() {
        return (String) textView.getText();
    }

    public TextView getTextView() {
        return textView;
    }

    protected void renderExtensionView() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        textView = new TextView(getContext());
        textView.setTextColor(getResources().getColor(R.color.secondary_text));
        extensionLayout.addView(textView, lp);
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
