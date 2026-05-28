package parth.appdev.edgeaiassistant.ui.screens.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import parth.appdev.edgeaiassistant.data.local.model.DailyStat
import parth.appdev.edgeaiassistant.data.local.model.UsageStat

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state   = viewModel.state.collectAsState().value
    val bgColor = Color(0xFF0B1220)
    val surface = Color(0xFF1E293B)
    val primary = Color(0xFF6366F1)
    val scroll  = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(scroll)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text       = "Analytics",
            color      = Color.White,
            style      = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // ── Summary cards ────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title    = "Total Commands",
                value    = state.totalCommands.toString(),
                surface  = surface
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title    = "Avg Response",
                value    = "${state.avgExecutionTime.toInt()} ms",
                surface  = surface
            )
        }

        // ── Usage bar chart ──────────────────────────────────────────────
        if (state.usageStats.isNotEmpty()) {
            SectionCard(surface = surface, title = "Usage Breakdown") {
                UsageBarChart(
                    stats   = state.usageStats,
                    primary = primary
                )
            }
        }

        // ── 7-day trend line chart ───────────────────────────────────────
        if (state.dailyStats.isNotEmpty()) {
            SectionCard(surface = surface, title = "7-Day Activity") {
                DailyTrendChart(
                    stats   = state.dailyStats,
                    primary = primary
                )
            }
        }

        // ── Empty state ──────────────────────────────────────────────────
        if (state.totalCommands == 0) {
            Box(
                modifier         = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = "No data yet.\nStart using Edge AI to see your stats.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

// ── Reusable stat card ────────────────────────────────────────────────────
@Composable
fun StatCard(
    modifier : Modifier,
    title    : String,
    value    : String,
    surface  : Color
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = Color.Gray,  style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(6.dp))
            Text(value, color = Color.White, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold)
        }
    }
}

// ── Section card wrapper ──────────────────────────────────────────────────
@Composable
fun SectionCard(
    surface  : Color,
    title    : String,
    content  : @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text       = title,
                color      = Color.White,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

// ── Usage horizontal bar chart ────────────────────────────────────────────
@Composable
fun UsageBarChart(
    stats   : List<UsageStat>,
    primary : Color
) {
    val maxCount = stats.maxOfOrNull { it.count }?.toFloat() ?: 1f

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        stats.forEach { stat ->
            val fraction = stat.count / maxCount
            val label    = stat.intentType
                .replace("_", " ")
                .lowercase()
                .replaceFirstChar { it.uppercase() }

            Column {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label,            color = Color.White, fontSize = 13.sp)
                    Text("${stat.count}x", color = Color.Gray,  fontSize = 12.sp)
                }
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(0xFF0B1220), RoundedCornerShape(4.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(8.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(primary, Color(0xFF818CF8))
                                ),
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

// ── 7-day line trend chart ────────────────────────────────────────────────
@Composable
fun DailyTrendChart(
    stats   : List<DailyStat>,
    primary : Color
) {
    if (stats.isEmpty()) return

    val maxCount = stats.maxOfOrNull { it.count }?.toFloat()?.coerceAtLeast(1f) ?: 1f
    val hasData  = stats.any { it.count > 0 }

    if (!hasData) {
        Box(
            modifier         = Modifier.fillMaxWidth().height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Use the app more to see trends",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
        return
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(bottom = 4.dp)
    ) {
        val w         = size.width
        val h         = size.height
        val padBottom = 8f
        val padTop    = 16f
        val chartH    = h - padBottom - padTop
        val stepX     = w / (stats.size - 1).toFloat().coerceAtLeast(1f)

        // Horizontal grid lines
        listOf(0f, 0.33f, 0.66f, 1f).forEach { fraction ->
            val y = padTop + chartH * fraction
            drawLine(
                color       = Color(0xFF334155),
                start       = Offset(0f, y),
                end         = Offset(w, y),
                strokeWidth = 1f
            )
        }

        if (stats.size == 1) {
            // Single point — just draw a dot
            val x = w / 2f
            val y = padTop + chartH * (1f - stats[0].count / maxCount)
            drawCircle(color = primary, radius = 8f, center = Offset(x, y))
            drawCircle(color = Color(0xFF0B1220), radius = 4f, center = Offset(x, y))
            return@Canvas
        }

        // Build smooth path
        val path = Path()
        stats.forEachIndexed { i, stat ->
            val x = i * stepX
            val y = padTop + chartH * (1f - stat.count / maxCount)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        // Filled area under line
        val fillPath = Path().apply {
            addPath(path)
            lineTo((stats.size - 1) * stepX, padTop + chartH)
            lineTo(0f, padTop + chartH)
            close()
        }
        drawPath(
            path  = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(primary.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        // Line
        drawPath(path = path, color = primary, style = Stroke(width = 3f))

        // Dots
        stats.forEachIndexed { i, stat ->
            val x = i * stepX
            val y = padTop + chartH * (1f - stat.count / maxCount)
            drawCircle(color = primary,           radius = 5f, center = Offset(x, y))
            drawCircle(color = Color(0xFF0B1220), radius = 2f, center = Offset(x, y))
        }
    }

    // Day labels
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        stats.forEach { stat ->
            Text(
                text     = stat.day,
                color    = Color.Gray,
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}