import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.VerticalAlign
import com.pdm.quiz.R



@Composable
@Preview
fun CategoryGrid(){
    Column{
        Row(){
            CategoryCard("Histórico",R.drawable.cat4, Modifier.weight(1f).padding(8.dp))
            CategoryCard("Ranking",R.drawable.cat1, Modifier.weight(1f).padding(8.dp))
            CategoryCard("Sair",R.drawable.cat2, Modifier.weight(1f).padding(8.dp))
        }
    }
}

@Composable
fun CategoryCard(title: String, iconRes: Int, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.white))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(iconRes),
            contentDescription = null,
            modifier= Modifier.size(32.dp)
        )
        Spacer(modifier= Modifier.height(16.dp))
        Text(
            text=title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}