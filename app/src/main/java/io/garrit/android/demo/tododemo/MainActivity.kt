package io.garrit.android.demo.tododemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.garrit.android.demo.tododemo.ui.theme.TodoDemoTheme
import java.util.UUID

val taskList = mutableStateListOf<Task>()

data class Task(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var content: String = "",
    var isChecked: MutableState<Boolean> = mutableStateOf(false)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (taskList.isEmpty()) {
            taskList.addAll(listOf(
                Task(title = "Buy groceries", content = "Milk, eggs, bread"),
                Task(title = "Call mom", content = "Discuss weekend plans"),
                Task(title = "Finish project", content = "Deadline is Friday")
            ))
        }

        setContent {
            TodoDemoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskApp()
                }
            }
        }
    }
}

@Composable
fun TaskApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextInputView()
        Spacer(modifier = Modifier.height(16.dp))
        TaskListView()
    }
}

@Composable
fun TextInputView() {
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Task description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (title.isNotEmpty()) {
                    taskList.add(Task(title = title, content = content))
                    title = ""
                    content = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }
    }
}

@Composable
fun TaskListView() {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(taskList) { task ->
            TaskItem(task = task)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = task.isChecked.value,
                    onCheckedChange = { task.isChecked.value = it },
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (task.content.isNotEmpty()) {
                        Text(
                            text = task.content,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemPreview() {
    TodoDemoTheme {
        TaskItem(
            task = Task(title = "Sample Task", content = "This is a sample task content")
        )
    }
}