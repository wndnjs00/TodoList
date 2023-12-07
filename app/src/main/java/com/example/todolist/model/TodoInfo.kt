package com.example.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class TodoInfo {
    //데이터를 집어넣음(각각의 요소를 집어넣음)
    //리사이클러뷰 아이템 하나에 들어갈 데이터
    var todoContent : String = ""   // 내용
    var todoDate : String = ""      // 날짜


    @PrimaryKey(autoGenerate = true)        // PrimaryKey 생성 (기본키)
    var id : Int = 0                        // id라는 변수는 Int값을 가지고, 초기값은 0
}