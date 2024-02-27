package com.example.hyperlinkstestingapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hyperlinkstestingapp.ui.theme.HyperlinksTestingAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HyperlinksTestingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HyperlinkTesting(textMock)
                }
            }
        }
    }
}

@Composable
fun HyperlinkTesting(text: String, modifier: Modifier = Modifier) {
    HyperLinkConverter(text = text, linkColor = Color.Green , linkDecoration = TextDecoration.None)
}

@Composable
fun HyperLinkConverter(
    modifier: Modifier = Modifier,
    text: String,
    linkColor: Color,
    linkDecoration: TextDecoration
) {
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier.padding(16.dp)) {
        val annotatedString = buildAnnotatedString {
            val urlRegex = Regex("""\b(?:https?://|www\.)\S+\b""")
            val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val phoneRegex = Regex("\\(\\d{3}\\) \\d{3}-\\d{4}")

            fun addLink(startIndex: Int, endIndex: Int, annotation: String) {
                addStyle(
                    style = SpanStyle(
                        color = linkColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(500),
                        textDecoration = linkDecoration
                    ),
                    start = startIndex,
                    end = endIndex
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = annotation,
                    start = startIndex,
                    end = endIndex
                )
            }

            urlRegex.findAll(text).forEach { result ->
                val startIndex = result.range.first
                val endIndex = result.range.last + 1
                addLink(startIndex, endIndex, result.value)
            }

            emailRegex.findAll(text).forEach { result ->
                val startIndex = result.range.first
                val endIndex = result.range.last + 1
                addLink(startIndex, endIndex, "mailto:${result.value}")
            }

            phoneRegex.findAll(text).forEach { result ->
                val startIndex = result.range.first
                val endIndex = result.range.last + 1
                addLink(startIndex, endIndex, "tel:${result.value}")
            }

            append(text)
        }

        ClickableText(
            modifier = Modifier.padding(16.dp),
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations("URL", offset, offset)
                    .firstOrNull()?.let { stringAnnotation ->
                        uriHandler.openUri(stringAnnotation.item)
                    }
            }
        )
    }
}






@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HyperlinksTestingAppTheme {
        HyperlinkTesting("Android")
    }
}

