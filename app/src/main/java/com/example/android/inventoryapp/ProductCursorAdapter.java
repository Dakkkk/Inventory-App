package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Dawid on 2017-05-17.
 */

public class ProductCursorAdapter extends CursorRecyclerAdapter<ProductCursorAdapter.ViewHolder> {

    private Context mContext;
    private ProductsActivity activity = new ProductsActivity();


    private EditorActivity editorActivity = new EditorActivity();

    public ProductCursorAdapter(ProductsActivity context, Cursor c) {
        super(context, c);
        this.activity = context;

    }


//    @Override
//    public View newView(Context context, Cursor cursor, ViewGroup parent) {
//        // Inflate a list item view using the layout specified in list_item.xml
//        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        View tmpView = super.getView(position, convertView, parent);
//
//        Button saleOneItemBtn = (Button) tmpView.findViewById(R.id.sale_1_item_btn);
//
//        saleOneItemBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ProductsActivity mainActivity = new ProductsActivity();
//                mainActivity.saleOneItem(getCursor(), mContext, getItemId(position));
//            }
//        });
//
//        return tmpView;
//    }


//    @Override
//    public void bindView(View view, Context context, final Cursor cursor) {
//        this.mContext = context;
//        // Find individual views that we want to modify in the list item layout
//        TextView nameTextView = (TextView) view.findViewById(R.id.name);
//        TextView quantityTextView = (TextView) view.findViewById(R.id.products_quantity);
//        TextView priceTextView = (TextView) view.findViewById(R.id.products_price);
//        TextView saleTextView = (TextView) view.findViewById(R.id.products_sale);
//        ImageView productMainImageView = (ImageView) view.findViewById(R.id.product_main_img);
//
//        // Find the columns of product attributes that we're interested in
//        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
//        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
//        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
//        int saleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SALE);
//        int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE);
//
//        // Read the product attributes from the Cursor for the current product
//        String productName = cursor.getString(nameColumnIndex);
//        String productPrice = cursor.getString(priceColumnIndex);
//
//        String productQuantity = cursor.getString(quantityColumnIndex);
//
//        String productSale = cursor.getString(saleColumnIndex);
//
//        byte[] listProductImage = cursor.getBlob(imageColumnIndex);
//
//        if (listProductImage != null) {
//            Bitmap imageItemBitmapResource = editorActivity.convertByteArrToBitmap(listProductImage);
//            productMainImageView.setImageBitmap(imageItemBitmapResource);
//        } else {
//            Log.d("Adapter: ", "NULL image in DB");
//            productMainImageView.setImageResource(R.drawable.question_mark);
//        }
//
//        String currentProductQuantity =
//                Integer.toString(Integer.parseInt(productQuantity) - Integer.parseInt(productSale));
//
//        // Update the TextViews with the attributes for the current product
//        StringBuilder sbName = new StringBuilder();
//        sbName.append(productName);
//
//        StringBuilder sbQuantity = new StringBuilder();
//        sbQuantity.append("Current quantity: ");
//        sbQuantity.append(currentProductQuantity);
//        sbQuantity.append(" ");
//        sbQuantity.append(view.getResources().getString(R.string.unit_product_quantity));
//
//        StringBuilder sbPrice = new StringBuilder();
//        sbPrice.append("Price: ");
//        sbPrice.append(productPrice);
//        sbPrice.append(" ");
//        sbPrice.append(view.getResources().getString(R.string.unit_product_price));
//
//        StringBuilder sbSale = new StringBuilder();
//        sbSale.append("Sale: ");
//        sbSale.append(productSale);
//
//        nameTextView.setText(sbName);
//        quantityTextView.setText(sbQuantity);
//        priceTextView.setText(sbPrice);
//        saleTextView.setText(sbSale);
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView nameTextView;
        protected TextView priceTextView;
        protected TextView quantityTextView;
        protected TextView saleTextView;
        protected ImageView productImage;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            priceTextView = (TextView) itemView.findViewById(R.id.products_price);
            quantityTextView = (TextView) itemView.findViewById(R.id.products_quantity);
            saleTextView = (TextView) itemView.findViewById(R.id.products_sale);
            productImage = (ImageView) itemView.findViewById(R.id.product_main_img);
        }
    }

    @Override
    public ProductCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(ProductCursorAdapter.ViewHolder viewHolder, Cursor cursor) {

    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }
}
