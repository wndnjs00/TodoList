package com.example.todolist.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.room.RoomDatabase
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.database.TodoDatabase
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.databinding.DialogEditBinding
import com.example.todolist.model.TodoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding   //binding 준비
    private lateinit var todoAdapter: TodoAdapter       //adapter 준비
    private lateinit var roomDatabase: TodoDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate((layoutInflater)) //실제 activity_main.xml에 있는 리사이클러뷰를 연동
        setContentView(binding.root)


        // 어뎁터 생성
        todoAdapter = TodoAdapter()

        // 리사이클러뷰에 어뎁터 세팅
        binding.recyclerviewTodo.adapter = todoAdapter      //어뎁터를 이미 만들어놨기 때문에 연결바로가능

        // roomDatabase 초기화
        roomDatabase = TodoDatabase.getInstance(applicationContext)!!

        // 전체 데이터 load(가져옴) (코루틴 사용 - 비동기처리[순서상관없이 위에서부터 순차적으로 실행되는것])
        CoroutineScope(Dispatchers.IO).launch {
            val lstTodo = roomDatabase.todoDao().getAllReadData() as ArrayList<TodoInfo> //전체데이터 가져옴
            //어뎁터쪽에 데이터 전달
            for (todoItem in lstTodo) {
                todoAdapter.addListItem(todoItem)
            }
            // UI Thread에서 처리
            runOnUiThread {
                todoAdapter.notifyDataSetChanged()
            }
        }





        // 플로팅 작성하기 버튼 클릭했을때 (AlerDialg로 팝업창 만들기)
        binding.btnWrite.setOnClickListener {
            //val binding = ListItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)  이해 참고용
            val bindingDialog = DialogEditBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)

            AlertDialog.Builder(this)
                .setTitle("할일 기록하기")            //팝업제목
                .setView(bindingDialog.root)        //bindingDialog를 넣는것
                .setPositiveButton("작성완료", DialogInterface.OnClickListener { dialog, which ->
                    // 작성완료버튼 눌렀을때 동작
                    val todoItem = TodoInfo()   //todoItem에 사용자가 입력한값 넣음
                    todoItem.todoContent =  bindingDialog.etMemo.text.toString()                // bindingDialog.etMemo.text.toString() 를 사용해서 사용자가 입력한 값으로 가져오기
                    todoItem.todoDate = SimpleDateFormat("yyyy-MM-dd").format(Date())    //최신의 date값 넣어줌 (몇분몇초인지 HH:mm:ss)
                    todoAdapter.addListItem(todoItem)   // adepter쪽으로 리스트아이템이 전달

                    CoroutineScope(Dispatchers.IO).launch {     //코루틴 사용
                        roomDatabase.todoDao().insertTodoData(todoItem) // 데이터베이스에도 클래스데이터 삽입
                        runOnUiThread {
                            todoAdapter.notifyDataSetChanged()  // 리스트 새로고침
                        }
                    }
                })

                .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                    //취소버튼 눌렀을때 동작
                })
                .show() //마지막에 이걸 적어줘야 정상적으로 작동


        }
    }
}