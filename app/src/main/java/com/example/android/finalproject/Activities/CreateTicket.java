package com.example.android.finalproject.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.finalproject.Enums.TicketStatus;
import com.example.android.finalproject.R;
import com.example.android.finalproject.Ticket;
import com.example.android.finalproject.Utility.UtlImage;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class CreateTicket extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText ticketName, ticketDes, phone, address;
    ImageView imageView1, imageView2;
    Spinner prodSpin, classSpin, subClassSpin, areaSpin;
    String strProd, strClass, strSubClass, strArea;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private final static int SELECT_FILE = 2;
    private static final int FROM_CAMERA = 1;
    int imgClick = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_ticket);
        setPointer();
        setSpinners();
        if(!getPicSP("img1").equals("error")) imageView1.setImageBitmap(UtlImage.string2bitmap(getPicSP("img1")));
        if(!getPicSP("img2").equals("error")) imageView2.setImageBitmap(UtlImage.string2bitmap(getPicSP("img2")));

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgClick=1;
                selectImage();
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgClick=2;
                selectImage();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setSpinners() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.product_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.classification_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.subClassification_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.area_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        prodSpin.setAdapter(adapter);
        prodSpin.setOnItemSelectedListener(this);
        classSpin.setAdapter(adapter1);
        classSpin.setOnItemSelectedListener(this);
        subClassSpin.setAdapter(adapter2);
        subClassSpin.setOnItemSelectedListener(this);
        areaSpin.setAdapter(adapter3);
        areaSpin.setOnItemSelectedListener(this);
    }

    private void setPointer() {
        ticketName = (EditText) findViewById(R.id.ticketName);
        ticketDes = (EditText) findViewById(R.id.ticketDes);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        imageView1 = (ImageView) findViewById(R.id.img1);
        imageView2 = (ImageView) findViewById(R.id.img2);
        prodSpin = (Spinner)findViewById(R.id.productSpin);
        classSpin = (Spinner)findViewById(R.id.classificationSpin);
        subClassSpin = (Spinner)findViewById(R.id.subClassificationSpin);
        areaSpin = (Spinner)findViewById(R.id.area);
        pref = this.getSharedPreferences("strImg",MODE_PRIVATE);
        editor=pref.edit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FROM_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            btmapOptions);

                    // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
                    if(imgClick==1){
                        savePicSP("img1",UtlImage.bitmap2string(bm));
                        imageView1.setImageBitmap(bm);
                    }
                    else if(imgClick==2) {
                        savePicSP("img2",UtlImage.bitmap2string(bm));
                        imageView2.setImageBitmap(bm);
                    }

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".jpg");
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();

                String tempPath = getPath(selectedImageUri, CreateTicket.this);
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                if(imgClick==1){
                    savePicSP("img1",UtlImage.bitmap2string(bm));
                    imageView1.setImageBitmap(bm);
                }
                else if(imgClick==2) {
                    savePicSP("img2",UtlImage.bitmap2string(bm));
                    imageView2.setImageBitmap(bm);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        editor.remove("img1").commit();
        editor.remove("img2").commit();

        finish();
    }

    private String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void savePicSP(String key,String image)
    {
        editor.putString(key,image).commit();
    }

    private String getPicSP(String key)
    {
        return pref.getString(key,"error");
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTicket.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, FROM_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public void btnSend(View view) {
        //getting the info from the activity
        String name = ticketName.getText().toString();
        String des = ticketDes.getText().toString();
        String phoneN = phone.getText().toString();
        String adrs = address.getText().toString();

        if (name.isEmpty() || des.isEmpty() || phoneN.isEmpty() || adrs.isEmpty()) {
            Toast.makeText(CreateTicket.this, "Please type...", Toast.LENGTH_SHORT).show();
        }


        //creating new instance of the project
        Ticket ticket = new Ticket(strProd, strClass, strSubClass,name, des, phoneN, TicketStatus.WaitForApproval.toString(),
                strArea,adrs, getPicSP("img1"),getPicSP("img2"), getUUID());
        //calling inside method from the class to save the data
        ticket.saveTicket();

        ticketName.setText("");
        ticketDes.setText("");
        phone.setText("");
        address.setText("");

        editor.remove("img1").commit();
        editor.remove("img2").commit();

        finish();
    }

    private String getUUID() {
        //create a unique UUID
        UUID idOne = UUID.randomUUID();
        //returning the UUID
        return idOne.toString();
    }

    public void btnCancel(View view) {
        editor.remove("img1").commit();
        editor.remove("img2").commit();

        finish();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        prodSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                strProd=prodSpin.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        classSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                strClass=classSpin.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        subClassSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                strSubClass= subClassSpin.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        areaSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                strArea=areaSpin.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Toast.makeText(CreateTicket.this, "NOT", Toast.LENGTH_SHORT).show();
    }
}
