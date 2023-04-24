package ua.vladmoyseienko.gridviewpagination;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button btnGenData;
    Button btnShowData;
    Button btnNext;
    Button btnPrevious;
    ArrayList<HashMap<String, Integer>> sampleData = null;
    int ELEMENTS_ON_PAGE=10;
    public static final String TAG = "MainActivity";
    Context context;
    int offsetRoot = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGenData = (Button) findViewById(R.id.btnGenerateData);
        btnGenData.setOnClickListener(onClickListener);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button)findViewById(R.id.btnPrevious);
        btnNext.setOnClickListener(onClickListener);
        btnPrevious.setOnClickListener(onClickListener);

        context = this;
        Log.d(TAG, "offsetRoot = " + offsetRoot);

    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnGenerateData:
                    showAlert();
                    break;
                case R.id.btnNext:
                    loadDataToGridView(getNexTenElements());
                    break;
                case R.id.btnPrevious:
                    loadDataToGridView(getPrevTenElements());
                    break;
            }
        }
    };

    private ArrayList<HashMap<String, Integer>> getPrevTenElements( ) {
        ArrayList<HashMap<String, Integer>> dataArray = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d(TAG, "offsetRoot = " + offsetRoot);
        this.offsetRoot -=10;

        if(offsetRoot >= 0){
            Cursor cursor = db.rawQuery("SELECT data, moreData FROM mytable LIMIT 10 OFFSET " + offsetRoot, null);

            // Получение данных
            while (cursor.moveToNext()){
                HashMap<String, Integer> map = new HashMap<>();
                map.clear();
                int data = cursor.getInt(cursor.getColumnIndexOrThrow("data"));
                int moreData = cursor.getInt(cursor.getColumnIndexOrThrow("moreData"));
                Log.d(TAG, "First 20 from database: data = " + data + " moreData = " + moreData);

                map.put("data", data);
                map.put("moreData", moreData);
                dataArray.add(map);
            }

            cursor.close();
            db.close();

            Log.d(TAG, "dataArray size = " + dataArray.size());
            Log.d(TAG, "dataArray  = " + dataArray);
            Log.d(TAG, "offsetRoot = " + offsetRoot);
            return  dataArray;
        }
        else {
            throw new RuntimeException();
        }
    }

    public ArrayList<HashMap<String, Integer>> generateSampleData(int count){
        //Генерация случайных данных
        ArrayList<HashMap<String, Integer>> generatedDataArray = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++){
            int data, moreData;
            HashMap<String, Integer> generatedData = new HashMap<>();
            generatedData.clear();
            data = i;
            moreData = random.nextInt(1000000);

            Log.d(TAG, "MainActivity: data = " + data + " moreData = " + moreData);

            generatedData.put("data", data);
            generatedData.put("moreData", moreData);
            generatedDataArray.add(generatedData);

            Log.d(TAG,"MainActivity: generatedData = " + generatedData.get("data"));
            Log.d(TAG,"MainActivity: generatedData = " + generatedData.get("moreData"));
            Log.d(TAG,"MainActivity: generatedDataArray = " + generatedDataArray);
        }
        return generatedDataArray;
    };

    public void writeDataToDatabase(ArrayList<HashMap<String, Integer>> dataArray){
        //Запись в базу данных
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < dataArray.size(); i++){
            HashMap<String, Integer> currentData = new HashMap<>();
            ContentValues contentValues = new ContentValues();
            currentData = dataArray.get(i);
            contentValues.put("data", currentData.get("data"));
            contentValues.put("moreData", currentData.get("moreData"));
            Log.d(TAG, contentValues.valueSet().toString());
            db.insert(dbHelper.getTableName(), null, contentValues);
        }
        db.close();
    }

    public ArrayList<HashMap<String, Integer>> readDataFromDatabase(){
        //чтение базы данных, используется в данном случае для дебага
        ArrayList<HashMap<String, Integer>> dataFromDatabase = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Query params
        String[] columns = {"*"};
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        //Cursor

        Cursor cursor = db.query(
                dbHelper.getTableName(),
                columns,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        int i = 0;
        // Получение данных
        while (cursor.moveToNext()){
            HashMap<String, Integer> map = new HashMap<>();
            int data = cursor.getInt(cursor.getColumnIndexOrThrow("data"));
            int moreData = cursor.getInt(cursor.getColumnIndexOrThrow("moreData"));
            Log.d(TAG, "Data from database: data = " + data + " moreData = " + moreData);

            map.put("data", data);
            map.put("moreData", moreData);
            dataFromDatabase.add(map);
        }
        Log.d(TAG, dataFromDatabase.toString());
        cursor.close();
        db.close();

        return dataFromDatabase;
    }
    public void showAlert() {
        //Вызов окна с вводом количества данных
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите число");

        // Set up the input
        final EditText input = new EditText(this);

        // Set the input type to number
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String number = input.getText().toString();
                // Do something with the number
                Log.d(TAG, "Number from alert message = " + number);
                sampleData = new ArrayList<>();
                sampleData = generateSampleData(Integer.parseInt(number));
                if (sampleData != null){
                    Log.d(TAG, "SampleData = " + sampleData);
                    writeDataToDatabase(sampleData);
                    readDataFromDatabase();
                    loadDataToGridView(getNexTenElements());

                }
                else {
                    Log.e(TAG, "SampleData is null");
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }



    public void loadDataToGridView(ArrayList<HashMap<String, Integer>> data){
        GridView gridView = (GridView) findViewById(R.id.gvMain);
        GridViewAdapter adapter = new GridViewAdapter(data, context);//установка адаптера
        gridView.setAdapter(adapter);
//        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView absListView, int i) {
//                //skip
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                // Сюда не смотрим это были тесты
//                if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount < data.size()) {
//                    ArrayList<HashMap<String, Integer>> nextData = getNexTenElements(offsetRoot);
//                    for (HashMap<String, Integer> newData : nextData) {
//                        adapter.add(newData);
//                        Log.d(TAG, "Adding newData: data = " + newData.get("data") + " moreData = " + newData.get("moreData"));
//                    }
//                }
//            }
//        });
    }

    //Получаем следующие 10 элементов с базы данных
    public ArrayList<HashMap<String, Integer>> getNexTenElements(){
        this.offsetRoot +=10;
        Log.d(TAG, "offsetRoot = " + offsetRoot);
        ArrayList<HashMap<String, Integer>> dataArray = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT data, moreData FROM mytable LIMIT 10 OFFSET " + offsetRoot, null);

        // Получение данных
        while (cursor.moveToNext()){
            HashMap<String, Integer> map = new HashMap<>();
            map.clear();
            int data = cursor.getInt(cursor.getColumnIndexOrThrow("data"));
            int moreData = cursor.getInt(cursor.getColumnIndexOrThrow("moreData"));
            Log.d(TAG, " 10 from database: data = " + data + " moreData = " + moreData);

            map.put("data", data);
            map.put("moreData", moreData);
            dataArray.add(map);
        }


        cursor.close();
        db.close();

        Log.d(TAG, "dataArray size = " + dataArray.size());
        Log.d(TAG, "dataArray  = " + dataArray);
        Log.d(TAG, "offsetRoot = " + offsetRoot);
        return  dataArray;
    }
}