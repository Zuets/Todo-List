package io.garrit.android.demo.tododemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.garrit.android.demo.tododemo.ui.theme.TodoDemoTheme
import java.util.*

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
    var currentScreen by remember { mutableStateOf("list") }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    when (currentScreen) {
        "list" -> TaskListScreen(
            onTaskClicked = { task ->
                selectedTask = task
                currentScreen = "detail"
            },
            onCreateNew = {
                selectedTask = null
                currentScreen = "edit"
            }
        )
        "detail" -> DetailScreen(
            task = selectedTask!!,
            onBack = { currentScreen = "list" },
            onEdit = { currentScreen = "edit" }
        )
        "edit" -> TaskEditScreen(
            task = selectedTask,
            onSave = { task ->
                if (selectedTask != null) {
                    val index = taskList.indexOfFirst { it.id == selectedTask!!.id }
                    if (index != -1) taskList[index] = task
                } else {
                    taskList.add(task)
                }
                currentScreen = "list"
            },
            onCancel = { currentScreen = "list" }
        )
    }
}

@Composable
fun TaskListScreen(
    onTaskClicked: (Task) -> Unit,
    onCreateNew: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = onCreateNew,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            Text("Create New Task")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(taskList) { task ->
                TaskItem(
                    task = task,
                    onTaskClicked = { onTaskClicked(task) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onTaskClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onTaskClicked,
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

@Composable
fun DetailScreen(
    task: Task,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (task.content.isNotEmpty()) {
            Text(
                text = task.content,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(start = 48.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onEdit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit")
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
fun TaskEditScreen(
    task: Task?,
    onSave: (Task) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var content by remember { mutableStateOf(task?.content ?: "") }

    val titleError = when {
        title.isEmpty() -> "Title cannot be empty"
        title.length < 3 -> "Title must be at least 3 characters"
        title.length > 50 -> "Title cant be more than 50 characters"
        else -> null
    }

    val contentError = when {
        content.length > 120 -> "Description cant be longer than 120 characters"
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            isError = titleError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (titleError != null) {
            Text(
                text = titleError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Task description") },
            isError = contentError != null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            minLines = 5
        )
        if (contentError != null) {
            Text(
                text = contentError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val updatedTask = task?.copy(
                        title = title,
                        content = content
                    ) ?: Task(
                        title = title,
                        content = content
                    )
                    onSave(updatedTask)
                },
                modifier = Modifier.weight(1f),
                enabled = titleError == null && contentError == null
            ) {
                Text(if (task != null) "Update" else "Create")
            }
        }
    }
}
