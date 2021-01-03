package com.example.notes

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), INoteAdapter {

    lateinit var viewModel: NoteViewModel
    private val activityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager= LinearLayoutManager(this)
        val adapter= NoteAdapter(this,this)
        recyclerview.adapter= adapter

        val txt= findViewById<TextView>(R.id.emptyText)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)
        viewModel.allNotes.observe(owner = this) { list ->
            adapter.updateList(list)
            if(adapter.itemCount == 0)
            {
                txt.isVisible= true
            }
            if(adapter.itemCount>0)
                txt.visibility= View.GONE
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AddActivity::class.java)
            startActivityForResult(intent, activityRequestCode)
        }

    }

    override fun onItemClicked(note:Note)
    {
        viewModel.deleteNote(note)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == activityRequestCode && resultCode == Activity.RESULT_OK) {
            intentData?.getStringExtra(AddActivity.EXTRA_REPLY)?.let { reply ->
                val note = Note(reply)
                viewModel.insertNote(note)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_SHORT
            
            ).show()
        }
    }

}
