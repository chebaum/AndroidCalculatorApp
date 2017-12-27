package com.tistory.chebaum.endascalc_10v;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    myDBHelper mDBHelper;
    SQLiteDatabase sqlDB;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    TextView str, calcStr;
    double finalResult, result;
    String op, nextOp;
    boolean op_pressed, sum_pressed, sub_pressed, enter_clicked;
    Button btn;
    Calc tempData=new Calc();
    int[] buttons={R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9,
            R.id.btn_backspace,R.id.btn_clr,R.id.btn_enter,R.id.btn_sum,R.id.btn_sub,R.id.btn_mul,R.id.btn_div};
    String[] filters={"finish_app","background_to_red","background_to_green","background_to_blue"};
    String cTitle, cStr;

    class BtnOnClickListener implements Button.OnClickListener{
        @Override
        public void onClick(View view){
            int id;
            switch (id = view.getId()){
                case R.id.btn0:
                case R.id.btn1:
                case R.id.btn2:
                case R.id.btn3:
                case R.id.btn4:
                case R.id.btn5:
                case R.id.btn6:
                case R.id.btn7:
                case R.id.btn8:
                case R.id.btn9:
                    number_btn_clicked(id);
                    break;
                case R.id.btn_backspace:
                    backspace_btn_clicked();
                    break;
                case R.id.btn_clr:
                    clr_btn_clicked();
                    break;
                case R.id.btn_enter:
                    enter_btn_clicked();
                    break;
                case R.id.btn_sum:
                case R.id.btn_sub:
                case R.id.btn_mul:
                case R.id.btn_div:
                    operand_btn_clicked(id);
                    break;
                //case R.id.btn_exit:
                 //   exit_btn_clicked();
                //    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        mToggle=new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        NavigationView leftNavView=(NavigationView)findViewById(R.id.leftNavBar);
        leftNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.nav_settings:
                        intent=new Intent(getApplicationContext(),SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_info:
                        intent=new Intent(getApplicationContext(),InfoActivity.class);
                        startActivity(intent);
                    default:
                        break;
                }
                return false;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BroadcastReceiver receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                switch (action){
                    case "finish_app":
                        finish();
                        return;
                    case "background_to_red":
                        findViewById(R.id.background_main).setBackgroundColor(Color.RED);
                        return;
                    case "background_to_green":
                        findViewById(R.id.background_main).setBackgroundColor(Color.GREEN);
                        return;
                    case "background_to_blue":
                        findViewById(R.id.background_main).setBackgroundColor(Color.BLUE);
                        return;
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        for(String str:filters) intentFilter.addAction(str);
        registerReceiver(receiver, intentFilter);

        mDBHelper = new myDBHelper(this.getApplicationContext());
        Button btn_db_insert=(Button)findViewById(R.id.btn_db_insert);
        btn_db_insert.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!enter_clicked) {
                    Toast.makeText(MainActivity.this, "= 버튼을 눌러 계산을 완료시키십시오", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("계산에 대한 간단한 설명 입력");
                final EditText input=new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cTitle=input.getText().toString();
                        sqlDB=mDBHelper.getWritableDatabase();
                        sqlDB.execSQL("INSERT INTO calcRecords(cTitle, cStr, cResult) VALUES ("+"'cTitle',"+"'"+str.getText().toString()+"',"+Double.toString(finalResult)+");");
                        sqlDB.close();
                        Toast.makeText(MainActivity.this, "입력되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });

        // BtnOnClickListener 객체 생성 후, 모든 버튼의 이벤트 리스너로 지정해준다.
        BtnOnClickListener onClickListener=new BtnOnClickListener();
        for(int temp:buttons) {
            btn = (Button) findViewById(temp);
            btn.setOnClickListener(onClickListener);
        }
        // 변수 초기값 지정
        finalResult=result=0.0;
        op=nextOp="";
        op_pressed=sum_pressed=sub_pressed=enter_clicked=false;
        calcStr=(TextView) findViewById(R.id.calcStr);
        str=(TextView) findViewById(R.id.str);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.topbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()){
            case R.id.exitBtn:
                exit_btn_clicked();
                return true;

            default:
                return true;
        }
    }

    public class myDBHelper extends SQLiteOpenHelper{
        public myDBHelper(Context context){
            super(context,"calcDB",null,1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE calcRecords (cId INTEGER PRIMARY KEY AUTOINCREMENT, cTitle CHAR(100), cStr CHAR(100), cResult DOUBLE);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS calcDB");
            onCreate(db);
        }
    }

    public void number_btn_clicked(int id){
        String btn_text=((Button)findViewById(id)).getText().toString();
        Toast.makeText(getApplicationContext(),btn_text+"을(를) 눌렀어요!",Toast.LENGTH_SHORT).show();
        if(calcStr.getText().toString().equals("0")||op_pressed){
            //calcStr.clearComposingText();
            calcStr.setText("");
            op_pressed=false;
        }
        if(str.getText().toString().equals("0"))
            str.setText("");
        if(enter_clicked&&!op_pressed)
            initializeForm();

        calcStr.setText(calcStr.getText().toString().concat(btn_text));
        str.setText(str.getText().toString().concat(btn_text));
        op_pressed=false;
        enter_clicked=false;
    }
    public void backspace_btn_clicked(){
        if(enter_clicked||op_pressed||str.getText().toString().equals(""))
            return;
        if(Double.parseDouble(calcStr.getText().toString())<10.0){
            calcStr.setText("0");
            str.setText(str.getText().toString().substring(0,str.getText().toString().length()-1));
            str.setText(str.getText().toString().concat("0"));
            return;
        }
        else{
            str.setText(str.getText().toString().substring(0,str.getText().toString().length()-1));
            calcStr.setText(calcStr.getText().toString().substring(0,calcStr.getText().toString().length()-1));
        }
    }
    public void clr_btn_clicked(){
        calcStr.setText("0");
        str.setText("");
        result=finalResult=0.0;
        op=nextOp="";
        op_pressed=sum_pressed=sub_pressed;
    }
    public void enter_btn_clicked(){
        if(op_pressed||enter_clicked) return; // enter 다시 누르면 이전계산 반복하도록 구현하기
        cStr=str.getText().toString(); // db에 저장위해 수식 변수에 저장
        switch (nextOp)
        {
            case "+":
            case "-":
                finalResult=calculate(finalResult, nextOp, Double.parseDouble(calcStr.getText().toString()));
                break;
            case "*":
            case "/":
                result = calculate(result, nextOp, Double.parseDouble(calcStr.getText().toString()));
                if(sum_pressed) finalResult=calculate(finalResult, "+", result);
                else if(sub_pressed) finalResult=calculate(finalResult,"-", result);
                else finalResult=result;
                break;
            default:
                break;
        }
        str.setText(Double.toString(finalResult));
        calcStr.setText(Double.toString(finalResult));
        saveStatus();
        enter_clicked=true;
    }
    public void operand_btn_clicked(int id){
        String btn_text=((Button)findViewById(id)).getText().toString();
        if(enter_clicked){
            initializeForm();
            calcStr.setText(tempData.calcStr);
            str.setText(tempData.calcStr);
        }
        enter_clicked=false;

        if(op_pressed){
            str.setText(str.getText().toString().substring(0,str.getText().toString().length()-1));
            str.setText(str.getText().toString().concat(btn_text));
            if(!isEqualOp(btn_text, nextOp)){
                loadStatus(tempData);
                //다시 자기자신 호출...
                operand_btn_clicked(id);
            }
            nextOp=btn_text;
            return;
        }
        saveStatus();
        if(nextOp.equals("")){
            if(str.getText().toString().equals(""))
                str.setText("0");
            result=(finalResult==0.0)?Double.parseDouble(calcStr.getText().toString()):0.0;
            if(btn_text.equals("+")||btn_text.equals("-")){
                if(!enter_clicked)
                    finalResult=calculate(finalResult,"+",result);
                result=0;
            }
            op_pressed=true;
            nextOp=btn_text;
            str.setText(str.getText().toString().concat(btn_text));
            return;
        }
        op_pressed=true;
        op=nextOp;
        nextOp=btn_text;
        str.setText(str.getText().toString().concat(btn_text));

        switch(nextOp)
        {
            case "+":
            case "-":
                if(op.equals("+")||op.equals("-"))
                    finalResult=calculate(finalResult, op, Double.parseDouble(calcStr.getText().toString()));
                else
                {
                    result=calculate(result, op, Double.parseDouble(calcStr.getText().toString()));
                    if(sum_pressed) finalResult=calculate(finalResult, "+", result);
                    else if(sub_pressed) finalResult=calculate(finalResult,"-",result);
                    else finalResult=result;
                }
                sum_pressed=sub_pressed=false;
                calcStr.setText(Double.toString(finalResult));
                result=0.0;
                break;
            case "*":
            case "/":
                if(op.equals("+")||op.equals("-"))
                {
                    result=Double.parseDouble(calcStr.getText().toString());
                    if(op.equals("+")) sum_pressed=true;
                    else sub_pressed=true;
                }
                else
                    result=calculate(result, op, Double.parseDouble(calcStr.getText().toString()));

                calcStr.setText(Double.toString(result));
                break;
            default:
                // error의 경우!
                break;
        }
    }
    public void exit_btn_clicked(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("정말로 종료하시겠습니까?");
        builder.setTitle("종료알림창")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert=builder.create();
        alert.setTitle("종료 알림창");
        alert.show();
    }
    public void initializeForm(){
        finalResult=result=0.0;
        op=nextOp="";
        op_pressed=sum_pressed=sub_pressed=enter_clicked=false;
        str.setText("");
        calcStr.setText("");
    }
    public double calculate(double firstNum, String op, double secondNum){
        switch (op)
        {
            case "+":
                firstNum += secondNum;
                break;
            case "-":
                firstNum -= secondNum;
                break;
            case "*":
                firstNum *= secondNum;
                break;
            case "/":
                firstNum /= secondNum;
                break;
            default:
                break;
        }
        return firstNum;
    }
    public void saveStatus(){
        tempData.finalResult = this.finalResult;
        tempData.result = this.result;
        tempData.op = this.op;
        tempData.nextOp = this.nextOp;
        tempData.op_pressed = this.op_pressed;
        tempData.sum_pressed = this.sum_pressed;
        tempData.sub_pressed = this.sub_pressed;
        tempData.enter_clicked = this.enter_clicked;
        tempData.calcStr = calcStr.getText().toString();
        tempData.str = str.getText().toString();
    }
    public void loadStatus(Calc c){
        this.finalResult = c.finalResult;
        this.result = c.result;
        this.op = c.op;
        this.nextOp = c.nextOp;
        this.op_pressed = c.op_pressed;
        this.sum_pressed = c.sum_pressed;
        this.sub_pressed = c.sub_pressed;
        this.enter_clicked = c.enter_clicked;
        this.calcStr.setText(c.calcStr);
        this.str.setText(c.str);
    }
    public boolean isEqualOp(String a, String b){
        if(a.equals("+")||a.equals("-"))
        {
            if(b.equals("+")||b.equals("-"))
                return true;
            else
                return false;
        }
        else
        {
            if(b.equals("*")||b.equals("/"))
                return true;
            else
                return false;
        }
    }

}
