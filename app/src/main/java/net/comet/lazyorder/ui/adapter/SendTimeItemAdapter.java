package net.comet.lazyorder.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import net.comet.lazyorder.model.bean.SendTime;
import net.comet.lazyorder.model.bean.SettleResult;

public class SendTimeItemAdapter extends BaseListAdapter<SettleResult.BookingTime> {

    public SendTimeItemAdapter(Activity activity) {
        super(activity, android.R.layout.simple_list_item_single_choice);
    }

    @Override
    protected void bindView(int position, View view, SettleResult.BookingTime item) {
        ((TextView)view.findViewById(android.R.id.text1)).setText(item.getViewTime());
    }
}