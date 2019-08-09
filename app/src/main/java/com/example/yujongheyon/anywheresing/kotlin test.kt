package com.example.yujongheyon.anywheresing

import android.app.Activity
import android.os.Bundle

/**
 * Created by yujongheyon on 2018-07-22.
 */
var name : String? = "abc"

var janu = 0
var nafla = "luke"
var nu : String? = null


class ac : Activity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra(name)
                ?: throw IllegalStateException("field $name missing in Intent")
        var test = userId
        val onc = test

        var varead = "읽기 전용이면 read가 되야되는거 아니냐 쓰기전용?"
        val etc = "얘가왜 읽기 전용이냐"

        //val 상수개념
        //var 흔히아는 변수개념
    }



    }