package com.shineofeidos.mockapiproject.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.shineofeidos.mockapiproject.MainViewModel
import com.shineofeidos.mockapiproject.UiState
import com.shineofeidos.mockapiproject.data.model.User

@Composable
fun NetworkLearningScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("网络框架学习：Retrofit & OkHttp", style = MaterialTheme.typography.titleLarge)
        Text(
            "架构：MVVM + Repository + Coroutines",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.loadUsers() }) {
            Text("加载 Mock 数据 (Users)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is UiState.Idle -> {
                Text("点击上方按钮开始网络请求")
            }
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.users) { user ->
                        UserItem(user = user)
                    }
                }
            }
            is UiState.Error -> {
                Text(
                    text = "错误: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "请确保已在 NetworkModule.kt 中配置了正确的 Mock URL",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun UserItem(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.avatar,
                contentDescription = "User Avatar",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
