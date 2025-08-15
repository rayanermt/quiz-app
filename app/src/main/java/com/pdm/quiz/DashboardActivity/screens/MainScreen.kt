package com.pdm.quiz.DashboardActivity.screens

import CategoryGrid
import com.pdm.quiz.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pdm.quiz.DashboardActivity.components.CategoryHeader
import com.pdm.quiz.DashboardActivity.components.GameModeButtons
import com.pdm.quiz.DashboardActivity.components.TopUserSection

@Composable
@Preview
fun MainScreen(
    onSinglePlayerClick:() -> Unit = {},
    onBoardClick: () -> Unit = {}
) {
    val scrollState=rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id= R.color.grey))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ){
            TopUserSection()
            Spacer(modifier = Modifier.height(64.dp))
            GameModeButtons(onSinglePlayerClick)
            Spacer(modifier = Modifier.height(32.dp))
            CategoryHeader()
            CategoryGrid()
            Spacer(modifier = Modifier.height(256.dp))
            Text(
                text = "Desenvolvido por Gabriel Paiva, Pedro Augusto Lopes, Rayane Reis",
                textAlign = TextAlign.Center,
                modifier = Modifier.width(300.dp).fillMaxWidth()
            )
        }
    }
}