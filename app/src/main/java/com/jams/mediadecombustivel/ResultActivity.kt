package com.jams.mediadecombustivel

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jams.mediadecombustivel.ui.theme.FuelCalculatorTheme

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recupera os dados enviados pela MainActivity
        val kmPercorrido = intent.getDoubleExtra("KM_PERCORRIDO", 0.0)
        val mediaConsumo = intent.getDoubleExtra("MEDIA_CONSUMO", 0.0)
        val tipoAnalise = intent.getStringExtra("ANALISE_TIPO") ?: "Simples"

        // Dados da Análise Completa
        val valorGastoExato = intent.getDoubleExtra("VALOR_GASTO_EXATO", Double.NaN)
        val valorGastoEstimado = intent.getDoubleExtra("VALOR_GASTO_ESTIMADO", Double.NaN)
        val precoApi = intent.getDoubleExtra("PRECO_API", Double.NaN)
        val tipoCombustivel = intent.getStringExtra("TIPO_COMBUSTIVEL")
        val ufSelecionada = intent.getStringExtra("UF_SELECIONADA")
        val apiError = intent.getStringExtra("API_ERROR")


        setContent {
            FuelCalculatorTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Passa os dados para o Composable da tela
                    ResultScreen(
                        kmPercorrido = kmPercorrido,
                        mediaConsumo = mediaConsumo,
                        tipoAnalise = tipoAnalise,
                        valorGastoExato = valorGastoExato,
                        valorGastoEstimado = valorGastoEstimado,
                        precoApi = precoApi,
                        tipoCombustivel = tipoCombustivel,
                        ufSelecionada = ufSelecionada,
                        apiError = apiError
                    )
                }
            }
        }
    }
}

// --- UI da Tela de Resultado ---
@Composable
fun ResultScreen(
    kmPercorrido: Double,
    mediaConsumo: Double,
    tipoAnalise: String,
    valorGastoExato: Double,
    valorGastoEstimado: Double,
    precoApi: Double,
    tipoCombustivel: String?,
    ufSelecionada: String?,
    apiError: String?
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Análise $tipoAnalise",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- Card 1: KM Percorrido (Sempre visível) ---
        Text(
            text = "KM Percorrido",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${String.format("%.1f", kmPercorrido)} km",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Cálculo: (KM Final - KM Inicial)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Card 2: Média de Consumo (Sempre visível) ---
        Text(
            text = "Média de Consumo",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${String.format("%.2f", mediaConsumo)} km/L",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Cálculo: (KM Percorrido / Litros Abastecidos)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // --- Bloco condicional para Análise Completa ---
        if (tipoAnalise == "Completa") {
            Spacer(modifier = Modifier.height(32.dp))

            // --- Card 3: Valor Gasto Exato ---
            Text(
                text = "Valor Gasto (Exato)",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "R$ ${String.format("%.2f", valorGastoExato)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cálculo: (Valor por Litro * Litros Abastecidos)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Card 4: Valor Gasto Estimado (Condicional) ---

            // Caso 1: Erro na API
            if (apiError != null) {
                Text(
                    text = "Estimativa da API",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Não disponível",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Motivo: $apiError",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            // Caso 2: API funcionou, mas não achou o preço (Tipo ou UF errados)
            else if (valorGastoEstimado.isNaN() || precoApi.isNaN()) {
                Text(
                    text = "Estimativa da API",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Não disponível",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A API não fornece preços para '$tipoCombustivel' ou para a UF '$ufSelecionada'.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            // Caso 3: Sucesso! API achou o preço.
            else {
                Text(
                    text = "Valor Gasto (Estimado API)",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "R$ ${String.format("%.2f", valorGastoEstimado)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cálculo: (Valor API * Litros Abastecidos)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Valor API ($tipoCombustivel em $ufSelecionada): R$ ${String.format("%.2f", precoApi)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- Botão de Voltar ---
        Button(
            onClick = {
                (context as? Activity)?.finish()
            },
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Voltar")
        }
    }
}