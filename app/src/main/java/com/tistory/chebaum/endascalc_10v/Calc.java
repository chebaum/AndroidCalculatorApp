package com.tistory.chebaum.endascalc_10v;

/**
 * Created by cheba on 2017-12-22.
 */

public class Calc{
    public double finalResult, result;
    public String op, nextOp;
    public boolean op_pressed, sum_pressed, sub_pressed, enter_clicked;
    public String calcStr, str;
    public Calc(){
        finalResult = result = 0.0;
        op = nextOp = "";
        op_pressed = sum_pressed = sub_pressed = enter_clicked = false;
        calcStr = str = "";
    }
    public void setCalc(Calc c){
        this.finalResult=c.finalResult;
        this.result = c.result;
        this.op = c.op;
        this.nextOp = c.nextOp;
        this.op_pressed = c.op_pressed;
        this.sum_pressed = c.sum_pressed;
        this.sub_pressed = c.sub_pressed;
        this.enter_clicked = c.enter_clicked;
        this.calcStr = c.calcStr;
        this.str = c.str;
    }
}