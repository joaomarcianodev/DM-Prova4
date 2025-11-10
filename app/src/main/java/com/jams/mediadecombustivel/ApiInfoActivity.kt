package com.jams.mediadecombustivel

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.json.JSONObject
import com.jams.mediadecombustivel.ui.theme.FuelCalculatorTheme

class ApiInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiDataString = intent.getStringExtra("API_DATA_STRING")

        setContent {
            FuelCalculatorTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ApiInfoScreen(jsonString = apiDataString)
                }
            }
        }
    }
}

// --- UI da Tela de Info da API ---
@Composable
fun ApiInfoScreen(jsonString: String?) {
    val context = LocalContext.current
    val dataList = remember { parseApiInfo(jsonString) }
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Informações da API",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Lista rolável de informações
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(dataList) { item ->
                val isLink = item.first == "API" || item.first == "Fonte dos Dados"

                InfoRow(
                    label = item.first,
                    value = item.second,
                    isLink = isLink,
                    onLinkClick = {
                        if (isLink) {
                            try {
                                uriHandler.openUri(item.second)
                            } catch (e: Exception) {
                                Log.e("ApiInfo", "Falha ao abrir link: ${item.second}", e)
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

// Um item de linha para a nossa lista
@Composable
fun InfoRow(
    label: String,
    value: String,
    isLink: Boolean = false,
    onLinkClick: () -> Unit = {}
) {
    // O modificador torna o Card clicável APENAS se for um link
    val cardModifier = if (isLink) {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onLinkClick)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    }

    Card(
        modifier = cardModifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = if (isLink) {
                    MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.tertiary, // Cor de link
                        textDecoration = TextDecoration.Underline // Sublinhado
                    )
                } else {
                    MaterialTheme.typography.bodyLarge
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Função helper para "achatar" o JSON e prepará-lo para a lista
private fun parseApiInfo(jsonString: String?): List<Pair<String, String>> {
    val list = mutableListOf<Pair<String, String>>()
    if (jsonString == null) {
        list.add("Erro" to "Nenhum dado recebido.")
        return list
    }

    try {
        val json = JSONObject(jsonString)

        list.add("API" to "https://combustivelapi.com.br")
        list.add("Fonte dos Dados" to json.getString("fonte"))
        list.add("Data da Coleta" to json.getString("data_coleta"))
        list.add("Moeda" to json.getString("moeda"))
        list.add("Tempo de Execução" to json.getString("tempo_execucao_segundos") + "s")

        // Extrai o objeto de análise
        val analise = json.getJSONObject("analise")

        // Atalho para extrair de forma segura
        fun safeExtract(tipo: String, combustivel: String): String {
            return try {
                "R$ " + analise.getJSONObject(tipo).getString(combustivel)
            } catch (e: Exception) { "N/A" }
        }
        fun safeExtractUF(tipo: String, combustivel: String): String {
            return try {
                analise.getJSONObject(tipo).getString(combustivel).uppercase()
            } catch (e: Exception) { "N/A" }
        }

        list.add("Gasolina Mais Barata" to safeExtractUF("estado_mais_barato_gasolina", "sigla") + " - " + safeExtract("estado_mais_barato_gasolina", "preco"))
        list.add("Gasolina Mais Cara" to safeExtractUF("estado_mais_caro_gasolina", "sigla") + " - " + safeExtract("estado_mais_caro_gasolina", "preco"))
        list.add("Diferença de Preço da Gasolina" to "R$ " + analise.getString("diferenca_gasolina"))
        list.add("Porcentagem do Aumento da Gasolina" to analise.getString("variacao_percentual_gasolina"))

        list.add("Diesel Mais Barato" to safeExtractUF("estado_mais_barato_diesel", "sigla") + " - " + safeExtract("estado_mais_barato_diesel", "preco"))
        list.add("Diesel Mais Caro " to safeExtractUF("estado_mais_caro_diesel", "sigla") + " - " + safeExtract("estado_mais_caro_diesel", "preco"))
        list.add("Diferença de Preço do Diesel" to "R$ " + analise.getString("diferenca_diesel"))
        list.add("Porcentagem do Aumento do Diesel" to analise.getString("variacao_percentual_diesel"))

    } catch (e: Exception) {
        list.clear()
        list.add("Erro" to "Falha ao processar os dados da API.")
    }

    return list
}