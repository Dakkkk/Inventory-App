package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;

/**
 * Created by Dawid on 2017-05-17.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;

    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri mCurrentProductUri;

    private EditText mNameEditText;

    private EditText mQuantityEditText;

    private EditText mPriceEditText;

    private EditText mSaleEditText;

    private EditText itemsToOrderEditText;

    private Button subtractOneQuantityBtn;

    private Button addOneQuantityBtn;

    private Button takePictureBtn;

    private ImageView mPictureImageView;

    private byte[] mPicture;

    private int quantityIntValue;

    private LinearLayout orderFromSupplierLayout;

    static final int REQUEST_IMAGE_CAPTURE = 1234;

    /**
     * Boolean flag that keeps track of whether the product has been edited (true) or not (false)
     */
    private boolean mProductHasChanged = false;

    private boolean mProductSuccessfullySavedOrUpdated = false;

    private boolean pictureTaken = false;


    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mProductHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new product or editing an existing one.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        orderFromSupplierLayout = (LinearLayout) findViewById(R.id.container_order_from_supplier);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mSaleEditText = (EditText) findViewById(R.id.edit_product_sale);

        mPictureImageView = (ImageView) findViewById(R.id.picture_image_view);

        subtractOneQuantityBtn = (Button) findViewById(R.id.subtract_one_quantity_btn);
        addOneQuantityBtn = (Button) findViewById(R.id.add_one_quantity_btn);
        itemsToOrderEditText = (EditText) findViewById(R.id.number_of_items_to_order);

        takePictureBtn = (Button) findViewById(R.id.take_picture_btn);

        //EditorActivityButtons
        RelativeLayout quantityButtonsLayout = (RelativeLayout) findViewById(R.id.quantity_buttons_layout);
        RelativeLayout editorTextLayoutButtons = (RelativeLayout) findViewById(R.id.editor_view_buttons_layout);
        RelativeLayout editorTextTakePictureLayout = (RelativeLayout) findViewById(R.id.editor_view_take_picture_layout);
        LinearLayout orderFromSupplierLayout = (LinearLayout) findViewById(R.id.container_order_from_supplier);
        TextView tapImageTextView = (TextView) findViewById(R.id.tap_image_text_view);

        // If the intent DOES NOT contain a product content URI, then we know that we are
        // creating a new product.
        if (mCurrentProductUri == null) {
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle(getString(R.string.editor_activity_title_new_product));
            tapImageTextView.setVisibility(View.GONE);
            editorTextLayoutButtons.setVisibility(View.GONE);
            quantityButtonsLayout.setVisibility(View.GONE);
            mPictureImageView.setVisibility(View.GONE);
            orderFromSupplierLayout.setVisibility(View.GONE);
            editorTextTakePictureLayout.setVisibility(View.VISIBLE);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_product));
            tapImageTextView.setVisibility(View.VISIBLE);
            editorTextLayoutButtons.setVisibility(View.VISIBLE);
            quantityButtonsLayout.setVisibility(View.VISIBLE);
            mPictureImageView.setVisibility(View.VISIBLE);
            orderFromSupplierLayout.setVisibility(View.VISIBLE);
            editorTextTakePictureLayout.setVisibility(View.GONE);
            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSaleEditText.setOnTouchListener(mTouchListener);
        mPictureImageView.setOnTouchListener(mTouchListener);

        Button deleteItemBtn = (Button) findViewById(R.id.delete_item);
        Button orderItemBtn = (Button) findViewById(R.id.order_item);

        deleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        orderItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderItemFromSupplier();
            }
        });


        subtractOneQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityIntValue > 0) {
                    quantityIntValue--;
                    mQuantityEditText.setText(String.valueOf(quantityIntValue));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.quantity_cant_be_below_zero), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        addOneQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityIntValue++;
                mQuantityEditText.setText(String.valueOf(quantityIntValue));
            }
        });

        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureOfTheProduct();
            }
        });

        mPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureOfTheProduct();
            }
        });

    }

    private void orderItemFromSupplier() {
        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentProductUri == null) {
            // Since no fields were modified, we can return early without creating order form supplier.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        String quantityString = mQuantityEditText.getText().toString().trim();
        StringBuilder orderNumberSB = new StringBuilder();
        String orderFromSupplier = itemsToOrderEditText.getText().toString();

        if (TextUtils.isEmpty(orderFromSupplier) || orderFromSupplier == "") {
            Toast.makeText(getApplicationContext(), getString(R.string.need_to_declare_items_number_to_order_msg), Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(orderFromSupplier) <= 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.need_to_declare_grater_number_msg), Toast.LENGTH_SHORT).show();
            return;
        }

        orderNumberSB.append(orderFromSupplier);

        String quantityAfterOrder = Integer.toString(Integer.parseInt(quantityString) +
                Integer.parseInt(orderFromSupplier));
        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityAfterOrder);

        int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

        if (rowsAffected > 0) {
            //get the product name and price from the DB instead of the EditText field
            int nameColumn = mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            String productName = mCursor.getString(nameColumn);

            int priceColumn = mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            String productPrice = mCursor.getString(priceColumn);

            StringBuilder emailSubjectSB = new StringBuilder();
            emailSubjectSB.append(productName);
            emailSubjectSB.append(" order");

            StringBuilder emailBodySB = new StringBuilder();
            emailBodySB.append("Hello, I'd like to order: ");
            emailBodySB.append(orderNumberSB.toString());
            emailBodySB.append(" units of: ");
            emailBodySB.append(productName);
            emailBodySB.append(" for the price of: ");
            emailBodySB.append(productPrice);
            emailBodySB.append("$, per unit. ");

            sendEmailToSupplier("trzy.cztery@o2.pl", emailSubjectSB.toString(), emailBodySB.toString());
            Toast.makeText(getApplicationContext(), getString(R.string.successfull_order),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmailToSupplier(String email, String subject, String body) {
        Uri uri = Uri.parse("mailto:" + email)
                .buildUpon()
                .appendQueryParameter("subject", subject)
                .appendQueryParameter("body", body)
                .build();

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
        startActivity(Intent.createChooser(emailIntent, subject));
    }


    /**
     * Get user input from editor and save product into database.
     */
    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String saleString = mSaleEditText.getText().toString().trim();


        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString)
                && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(saleString)
                ) {
            // Since no fields were modified, we can return early without creating a new product.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        //If it's an existing product it already has a picture
        if (mCurrentProductUri != null) {
            pictureTaken = true;
        }

        //User must fill in all fields to save a product
        if (TextUtils.isEmpty(nameString)
                || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(saleString)
                || !pictureTaken) {
            showMustDeclareAllValuesDialog();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_SALE, saleString);
        values.put(ProductEntry.COLUMN_PRODUCT_PICTURE, mPicture);

        // If the quantity, price or sale is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        int price = 0;
        int sale = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }

        if (!TextUtils.isEmpty(quantityString)) {
            price = Integer.parseInt(priceString);
        }

        if (!TextUtils.isEmpty(quantityString)) {
            sale = Integer.parseInt(saleString);
        }

        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductEntry.COLUMN_PRODUCT_SALE, sale);

        // Determine if this is a new or existing product by checking if mCurrentProductUri is null or not
        if (mCurrentProductUri == null) {
            // This is a NEW product, so insert a new product into the provider,
            // returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                mProductSuccessfullySavedOrUpdated = true;
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentProductUri will already identify the correct row in the database that
            // we want to modify.
            pictureTaken = true;
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                mProductSuccessfullySavedOrUpdated = true;
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void takePictureOfTheProduct() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private byte[] convertImageBitmapToByteArr(Bitmap imageBitmap) {
        byte[] byteArray;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return byteArray = stream.toByteArray();
    }

    //When the product picture was taken save it an set the ImageView to this picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mPictureImageView.setVisibility(View.VISIBLE);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mPictureImageView.setImageBitmap(imageBitmap);
            mPicture = convertImageBitmapToByteArr(imageBitmap);
            Log.d("mPicture onAct:", mPicture.toString());
            pictureTaken = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //If the mPicture is null get the value from database - when we are Editing
                //an EXISTING product, so the image exists
                if (mPicture == null && mCurrentProductUri != null) {
                    mPicture = mCursor.getBlob(
                            mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE));
                }
                saveProduct();
                // Save product to database
                // Exit activity
                if (mProductSuccessfullySavedOrUpdated) {
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the product table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SALE,
                ProductEntry.COLUMN_PRODUCT_PICTURE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        mCursor = cursor;

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int saleColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SALE);
            int pictureColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PICTURE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            quantityIntValue = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int sale = cursor.getInt(saleColumnIndex);
            byte[] pictureBytes = cursor.getBlob(pictureColumnIndex);

            if (pictureBytes != null) {
                Bitmap pictureBitmap = convertByteArrToBitmap(pictureBytes);
                mPictureImageView.setImageBitmap(pictureBitmap);
            }

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mQuantityEditText.setText(Integer.toString(quantityIntValue));
            mPriceEditText.setText(Integer.toString(price));
            mSaleEditText.setText(Integer.toString(sale));
        }
    }

    public Bitmap convertByteArrToBitmap(byte[] byteArr) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
        return bitmap;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mSaleEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showMustDeclareAllValuesDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.empty_values_msg);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentProductUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}