package net.comet.lazyorder.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.ShoppingEntity;
import net.comet.lazyorder.ui.adapter.ShoppingCartItemView;
import net.comet.lazyorder.model.ShoppingCart;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;

public class ShoppingCartPanel extends FrameLayout {

    @Bind(R.id.txt_clear)
    TextView mClearTxt;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private RecyclerMultiAdapter mAdapter;

    public ShoppingCartPanel(Context context) {
        this(context, null);
    }

    public ShoppingCartPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_shopping_cart_panel, this);
        ButterKnife.bind(view);
        initViews();
        refreshPanel();
    }

    private void initViews() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = SmartAdapter.empty()
                .map(ShoppingEntity.class, ShoppingCartItemView.class)
                .into(mRecyclerView);
    }

    public void refreshPanel() {
        ShoppingCart shoppingCart = ShoppingCart.getInstance();
        List<ShoppingEntity> entities = shoppingCart.getShoppingList();
        mAdapter.setItems(entities);
    }

    @OnClick({R.id.txt_clear})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_clear:
                ShoppingCart.getInstance().clearAll();
                mAdapter.clearItems();
                break;
        }
    }
}