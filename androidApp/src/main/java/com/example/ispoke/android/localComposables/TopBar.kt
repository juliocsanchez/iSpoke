import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.shadow
import androidx.navigation.NavController


@Composable
fun TopBar(navController: NavController){
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape( 10.dp)),

                    shape = RoundedCornerShape(
                        topStart =0.dp,
                        topEnd = 0.dp,
                        bottomStart = 15.dp,
                        bottomEnd = 15.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "iSpoke" ,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(5.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil",
                            )
                        }
                    }
                }
            }
}