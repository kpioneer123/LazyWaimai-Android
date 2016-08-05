package net.comet.lazyorder.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.Product;
import net.comet.lazyorder.model.bean.ProductCategory;
import net.comet.lazyorder.util.CollectionUtil;
import net.comet.lazyorder.util.StringFetcher;
import net.comet.lazyorder.widget.PicassoImageView;
import net.comet.lazyorder.widget.ProperRatingBar;
import net.comet.lazyorder.widget.ShoppingCountView;
import java.util.List;
import za.co.immedia.pinnedheaderlistview.SectionedBaseAdapter;

public class ProductItemAdapter extends SectionedBaseAdapter {

    private LayoutInflater mInflater;
    private List<ProductCategory> mCategories;

    public ProductItemAdapter(Activity activity) {
        mInflater = LayoutInflater.from(activity);
    }

    public void setItems(List<ProductCategory> categories) {
        mCategories = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getCountForSection(int section) {
        if (mCategories != null) {
            List<Product> products = mCategories.get(section).getProducts();
            if (!CollectionUtil.isEmpty(products)) {
                return products.size();
            }
        }
        return 0;
    }

    @Override
    public int getSectionCount() {
        return mCategories != null ? mCategories.size() : 0;
    }

    @Override
    public Product getItem(int section, int position) {
        List<Product> products = mCategories.get(section).getProducts();
        return products.get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public View getItemView(int section, int position, View convertView, ViewGroup viewGroup) {
        final ItemViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_product_item, null);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }
        Product product = getItem(section, position);
        holder.photoImg.loadProductPhoto(product);
        holder.nameTxt.setText(product.getName());
        holder.priceTxt.setText(StringFetcher.getString(R.string.label_price, product.getPrice()));
        holder.monthSalesTxt.setText(StringFetcher.getString(R.string.label_month_sales, product.getMonthSales()));
        holder.rateRatingBar.setRating(product.getRate());
        if (!TextUtils.isEmpty(product.getDescription())) {
            holder.descriptionTxt.setVisibility(View.VISIBLE);
            holder.descriptionTxt.setText(product.getDescription());
        } else {
            holder.descriptionTxt.setVisibility(View.GONE);
        }
        if (product.getLeftNum() > 0) {
            holder.shoppingCountView.setProduct(product);
            holder.shoppingCountView.setVisibility(View.VISIBLE);
            holder.leftNumTxt.setVisibility(View.GONE);
        } else {
            holder.leftNumTxt.setText(StringFetcher.getString(R.string.label_sold_out));
            holder.leftNumTxt.setVisibility(View.VISIBLE);
            holder.shoppingCountView.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getSectionHeaderView(int position, View convertView, ViewGroup viewGroup) {
        HeaderViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_product_header, viewGroup, false);
            holder = new HeaderViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        ProductCategory productCategory = mCategories.get(position);
        holder.titleTxt.setText(productCategory.getName());
        if (!TextUtils.isEmpty(productCategory.getDescription())) {
            holder.descText.setText(productCategory.getDescription());
            holder.descText.setVisibility(View.VISIBLE);
        } else {
            holder.descText.setVisibility(View.GONE);
        }

        return convertView;
    }

    ////////////////////////////////////////////
    ///            view holder               ///
    ////////////////////////////////////////////

    public static class HeaderViewHolder {
        TextView titleTxt;
        TextView descText;

        HeaderViewHolder(View headerView) {
            titleTxt = (TextView) headerView.findViewById(R.id.txt_title);
            descText = (TextView) headerView.findViewById(R.id.txt_desc);
        }
    }

    public static class ItemViewHolder {
        PicassoImageView photoImg;
        TextView nameTxt;
        TextView priceTxt;
        TextView descriptionTxt;
        TextView monthSalesTxt;
        ProperRatingBar rateRatingBar;
        TextView leftNumTxt;
        ShoppingCountView shoppingCountView;

        ItemViewHolder(View itemView) {
            photoImg = (PicassoImageView) itemView.findViewById(R.id.img_product_photo);
            nameTxt = (TextView) itemView.findViewById(R.id.txt_product_name);
            priceTxt = (TextView) itemView.findViewById(R.id.txt_product_price);
            descriptionTxt = (TextView) itemView.findViewById(R.id.txt_product_description);
            monthSalesTxt = (TextView) itemView.findViewById(R.id.txt_product_month_sales);
            rateRatingBar = (ProperRatingBar) itemView.findViewById(R.id.rating_product_rate);
            leftNumTxt = (TextView) itemView.findViewById(R.id.txt_product_left_num);
            shoppingCountView = (ShoppingCountView) itemView.findViewById(R.id.shopping_count_view);
        }
    }
}