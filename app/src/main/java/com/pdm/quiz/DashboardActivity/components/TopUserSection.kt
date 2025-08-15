package com.pdm.quiz.DashboardActivity.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pdm.quiz.R


/* Descrição do componente:
/ Tem a foto de perfil do usuário, uma saudação com o nome e o saldo de pontos (moedas).
*/

@Composable
@Preview
fun TopUserSection(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = null,
            Modifier.size(size = 55.dp)
        )

        Spacer(modifier = Modifier.width(width = 16.dp))

        Text(
            text = "Bem-vindo(a), Rayane!",
            fontSize = 20.sp,
            modifier = Modifier.weight(weight = 1f)
        )
    }
}