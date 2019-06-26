package com.example.selab.textconverter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.*;

enum FormType
{
    TEXT, BIN, DEC, HEX
}

public class MainActivity extends AppCompatActivity
{
    AwesomeStringConverter converter = new AwesomeStringConverter();

    Spinner from_spinner;
    Spinner to_spinner;

    FormType from = FormType.TEXT;
    FormType to = FormType.HEX;

    ArrayList<String> types = new ArrayList<>();
    ArrayList<String> encodings = new ArrayList<>();

    EditText from_text;
    TextView to_text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        print("CREATE");

        types.add("텍스트");
        types.add("2진수");
        types.add("10진수");
        types.add("16진수");

        //컨트롤 객체화 및 이벤트 등록
        setFromSpinner();
        setToSpinner();
        setFromEditText();
        setToTextView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_utf8:
                converter.setUtf8(); convert();
                break;
            case R.id.action_utf16:
                converter.setUtf16(); convert();
                break;
            case R.id.action_utf32:
                converter.setUtf32(); convert();
                break;
            case R.id.action_euckr:
                converter.setEucKr(); convert();
                break;
            default:
                break;
        }
        return true;
    }

    private void setFromSpinner()
    {
        from_spinner = findViewById(R.id.from_spinner);
        from_spinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, types));

        from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch((int)id)
                {
                case 0: from = FormType.TEXT;
                    break;
                case 1: from = FormType.BIN;
                    break;
                case 2: from = FormType.DEC;
                    break;
                case 3: from = FormType.HEX;
                    break;
                }
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {}
        });
    }

    private void setToSpinner()
    {
        to_spinner = findViewById(R.id.to_spinner);
        to_spinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, types));

        to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch((int)id)
                {
                    case 0: to = FormType.TEXT;
                        break;
                    case 1: to = FormType.BIN;
                        break;
                    case 2: to = FormType.DEC;
                        break;
                    case 3: to = FormType.HEX;
                        break;
                }
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {}
        });

        to_spinner.setSelection(3);
    }

    private void setFromEditText()
    {
        from_text = findViewById(R.id.from_text);

        from_text.addTextChangedListener(
                new TextWatcher()
                {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after)
                    {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count)
                    {
                        convert();
                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {}
                }
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setToTextView()
    {
        to_text = findViewById(R.id.to_text);

        to_text.setOnTouchListener(
                new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View view, MotionEvent event)
                    {
                        if(event.getAction()==MotionEvent.ACTION_DOWN)
                        {
                            ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            manager.setPrimaryClip(ClipData.newPlainText("converted_text", to_text.getText()));
                            Toast.makeText(MainActivity.this, "복사되었습니다.", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                }
        );
    }

    //변환함
    private void convert()
    {
        String text = from_text.getText().toString();

        switch(from)
        {
            case TEXT: converter.fromString(text); break;
            case BIN: converter.fromBin(text); break;
            case DEC: converter.fromDec(text); break;
            case HEX: converter.fromHex(text); break;
        }

        String result="";
        switch(to)
        {
            case TEXT: result = converter.toStr(); break;
            case BIN: result=converter.toBin(); break;
            case DEC: result=converter.toDec(); break;
            case HEX: result=converter.toHex(); break;
        }

        to_text.setText(result);
    }

    //위아래 바꿈
    public void swap(View view)
    {
        int temp = from_spinner.getSelectedItemPosition();
        from_spinner.setSelection(to_spinner.getSelectedItemPosition());
        to_spinner.setSelection(temp);

        String tmpStr = from_text.getText().toString();
        from_text.setText(to_text.getText());
        to_text.setText(tmpStr);
    }

    //@@디버깅용
    public static void print(String s)
    {
        System.out.println("####@@@ 으아악: "+s);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        print("DESTROY");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        print("PAUSE");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        print("RESUME");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        print("STOP");
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        print("START");
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        print("RESTART");
    }
}
