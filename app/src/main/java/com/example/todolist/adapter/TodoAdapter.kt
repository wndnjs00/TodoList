package com.example.todolist.adapter

//리사이클러뷰 어뎁터를 상속을 받아야지 데이터연동을 할수있음

import android.app.Activity
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.database.TodoDatabase
import com.example.todolist.databinding.DialogEditBinding
import com.example.todolist.databinding.ListItemTodoBinding
import com.example.todolist.model.TodoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var lstTodo : ArrayList<TodoInfo> = ArrayList()   //TodoInfo라는 데이터를 가진 배열리스트형태를 lasTodo변수에 담겠다
    private lateinit var roomDatabase: TodoDatabase


//    // init = 클래스가 생설될때 가징먼저 호출회는것
//    init {
//        // 샘플 리스트 아이템 인스턴스 생성(임의로 만든거) (room DB연동 후에는 삭제함)
//        val todoItem = TodoInfo()
//        todoItem.todoContent = "To Do List앱 만들기!"
//        todoItem.todoDate = "2023-06-01"
//        lstTodo.add(todoItem)
//
//        // 샘플 리스트 아이템 인스턴스 생성
//        val todoItem2 = TodoInfo()
//        todoItem2.todoContent = "블로그 작성하기"
//        todoItem2.todoDate = "2023-06-01"
//        lstTodo.add(todoItem2)
//
//        // 샘플 리스트 아이템 인스턴스 생성
//        val todoItem3 = TodoInfo()
//        todoItem3.todoContent = "기말 공부하기!"
//        todoItem3.todoDate = "2023-06-01"
//        lstTodo.add(todoItem3)
//
//    }


    fun addListItem(todoItem: TodoInfo) {
        // 4,0,1,2,3
        lstTodo.add(0, todoItem)    //최신꺼가 젤 처음나오게 하려면 0추가 (아이템 추가될때마다 최신순서로 배치)
    }


    inner class TodoViewHolder(private val binding: ListItemTodoBinding) : RecyclerView.ViewHolder(binding.root) { // binding.root에 있는 binding은 앞쪽에 선언했던 변수임
    // viewHolder = 각 리스트 아이템들을 보관하는 객체
        fun bind(todoItem : TodoInfo) {
            // 리스트 뷰 데이터를 UI에 연동
            binding.tvContent.setText(todoItem.todoContent)
            binding.tvDate.setText(todoItem.todoDate)


            // 삭제 버튼 클릭했을때
            binding.btnDelete.setOnClickListener {
                // 삭제 버튼 클릭 시 내부 로직 구현

                //다이얼로그 띄우기
                AlertDialog.Builder(binding.root.context)
//                    .setTitle("삭제")
                    .setMessage("정말 삭제하시겠습니까?")
                    .setPositiveButton("네",DialogInterface.OnClickListener { dialog, which ->

                        CoroutineScope(Dispatchers.IO).launch {
                            // db에도 적용해주기
                            val innerLstTodo = roomDatabase.todoDao().getAllReadData()     //현재 db에 저장되어있는 데이터를 모두 가지고와서 arraylist형태로 만듬
                            for (item in innerLstTodo) {
                                if (item.todoContent == todoItem.todoContent && item.todoDate == todoItem.todoDate){
                                    // database item 삭제
                                    roomDatabase.todoDao().deleteTodoDate(item)
                                }
                            }

                            // ui 삭제
                            lstTodo.remove(todoItem)    //lstTodo 배열에 담겨있는 todoItem 데이터들이 삭제
                            (binding.root.context as Activity).runOnUiThread {
                                notifyDataSetChanged()
                                Toast.makeText(binding.root.context,"삭제되었습니다", Toast.LENGTH_SHORT).show()
                            }
                        }

                    })

                    .setNegativeButton("아니오",DialogInterface.OnClickListener { dialog, which ->

                    })
                    .show()
            }


        // 리스트 수정
        binding.root.setOnClickListener {     // binding.root는 list_item_todo.xml에 있는 아이템을 나타냄
            val bindingDialog = DialogEditBinding.inflate(LayoutInflater.from(binding.root.context), binding.root, false)
//            // 기존에 작성한 데이터 보여주기
//            bindingDialog.etMemo.setText(todoItem.todoContent)

            AlertDialog.Builder(binding.root.context)
                .setTitle("수정하기")                 //팝업제목
                .setView(bindingDialog.root)        //bindingDialog를 넣는것
                .setPositiveButton("수정완료", DialogInterface.OnClickListener { dialog, which ->
                    CoroutineScope(Dispatchers.IO).launch {
                        // db에도 적용해주기
                        val innerLstTodo = roomDatabase.todoDao().getAllReadData()     //현재 db에 저장되어있는 데이터를 모두 가지고와서 arraylist형태로 만듬
                        for (item in innerLstTodo) {
                            if (item.todoContent == todoItem.todoContent && item.todoDate == todoItem.todoDate){
                                // database item 수정
                                item.todoContent =  bindingDialog.etMemo.text.toString()
                                item.todoDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
                                roomDatabase.todoDao().updateTodoDate(item)
                            }
                        }

                        // 작성완료버튼 눌렀을때 동작
                        // ui 수정
                        todoItem.todoContent =  bindingDialog.etMemo.text.toString()                // bindingDialog.etMemo.text.toString() 를 사용해서 사용자가 입력한 값으로 가져오기
                        todoItem.todoDate = SimpleDateFormat("yyyy-MM-dd").format(Date())    //최신의 date값 넣어줌 (몇분몇초인지 HH:mm:ss)

                        // 리스트 수정 (array list 수정)
                        lstTodo.set(adapterPosition, todoItem)

                        (binding.root.context as Activity).runOnUiThread {
                            notifyDataSetChanged()
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




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TodoViewHolder {
        // viewHolder가 만들어질때
        // 각 리스트 아이템이 1개씩 구성될때마다 이 오버라이드 메소드가 호출됨
        val binding = ListItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // roomDatabase 초기화
        roomDatabase = TodoDatabase.getInstance(binding.root.context)!!

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        // viewHolder가 연결될때(바인딩될때)
        holder.bind(lstTodo[position])
    }

    override fun getItemCount(): Int {
        // 리스트 총 개수
        return lstTodo.size
    }

}