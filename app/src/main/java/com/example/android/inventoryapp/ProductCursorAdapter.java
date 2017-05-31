package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Dawid on 2017-05-17.
 */

public class ProductCursorAdapter extends CursorAdapter {

    private Context mContext;

    private EditorActivity editorActivity = new EditorActivity();

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View tmpView = super.getView(position, convertView, parent);

        Button saleOneItemBtn = (Button) tmpView.findViewById(R.id.sale_1_item_btn);

        saleOneItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductsActivity mainActivity = new ProductsActivity();
                mainActivity.saleOneItem(getCursor(), mContext, getItemId(position));
            }
        });

        return tmpView;
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        this.mContext = context;
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.products_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.products_price);
        TextView saleTextView = (TextView) view.findViewById(R.id.products_sale);
        ImageView productMainImageView = (ImageView) view.findViewById(R.id.product_main_img);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int saleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SALE);
        int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);

        String productQuantity = cursor.getString(quantityColumnIndex);

        String productSale = cursor.getString(saleColumnIndex);

        byte[] listProductImage = cursor.getBlob(imageColumnIndex);

        if (listProductImage != null) {
            Bitmap imageItemBitmapResource = editorActivity.convertByteArrToBitmap(listProductImage);
            productMainImageView.setImageBitmap(imageItemBitmapResource);
        } else {
            Log.d("Adapter: ", "NULL image in DB");
            productMainImageView.setImageResource(R.drawable.question_mark);
        }

        String currentProductQuantity =
                Integer.toString(Integer.parseInt(productQuantity) - Integer.parseInt(productSale));

        // Update the TextViews with the attributes for the current product
        StringBuilder sbName = new StringBuilder();
        sbName.append(productName);

        StringBuilder sbQuantity = new StringBuilder();
        sbQuantity.append("Current quantity: ");
        sbQuantity.append(currentProductQuantity);
        sbQuantity.append(" ");
        sbQuantity.append(view.getResources().getString(R.string.unit_product_quantity));

        StringBuilder sbPrice = new StringBuilder();
        sbPrice.append("Price: ");
        sbPrice.append(productPrice);
        sbPrice.append(" ");
        sbPrice.append(view.getResources().getString(R.string.unit_product_price));

        StringBuilder sbSale = new StringBuilder();
        sbSale.append("Sale: ");
        sbSale.append(productSale);

        nameTextView.setText(sbName);
        quantityTextView.setText(sbQuantity);
        priceTextView.setText(sbPrice);
        saleTextView.setText(sbSale);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }
}
