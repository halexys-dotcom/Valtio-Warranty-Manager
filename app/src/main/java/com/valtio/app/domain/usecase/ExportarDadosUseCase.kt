package com.valtio.app.domain.usecase

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.valtio.app.R
import com.valtio.app.data.repository.ProdutoRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileWriter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportarDadosUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val produtoRepository: ProdutoRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    suspend fun exportarPdf(): File? {
        val produtos = produtoRepository.getAllList()
        if (produtos.isEmpty()) return null

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        val file = File(dir, "Valtio_Export_${System.currentTimeMillis()}.pdf")

        PdfWriter(file).use { writer ->
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            document.add(Paragraph(context.getString(R.string.pdf_title))
                .setBold().setFontSize(18f).setTextAlignment(TextAlignment.CENTER))
            document.add(Paragraph(context.getString(R.string.pdf_generated_on, LocalDate.now().format(dateFormatter)))
                .setTextAlignment(TextAlignment.CENTER))
            document.add(Paragraph("\n"))

            val table = Table(6)
            table.addHeaderCell(Cell().add(Paragraph(context.getString(R.string.pdf_header_name)).setBold()))
            table.addHeaderCell(Cell().add(Paragraph(context.getString(R.string.pdf_header_brand)).setBold()))
            table.addHeaderCell(Cell().add(Paragraph(context.getString(R.string.pdf_header_category)).setBold()))
            table.addHeaderCell(Cell().add(Paragraph(context.getString(R.string.pdf_header_price)).setBold()))
            table.addHeaderCell(Cell().add(Paragraph(context.getString(R.string.pdf_header_purchase_date)).setBold()))
            table.addHeaderCell(Cell().add(Paragraph(context.getString(R.string.pdf_header_warranty)).setBold()))

            produtos.forEach { p ->
                table.addCell(p.nome)
                table.addCell(p.marca)
                table.addCell(p.categoria)
                table.addCell(String.format("%.2f €", p.preco))
                table.addCell(p.dataCompra.format(dateFormatter))
                table.addCell(p.dataFimGarantia?.format(dateFormatter) ?: "N/A")
            }
            document.add(table)
            document.close()
        }
        return file
    }

    suspend fun exportarCsv(): File? {
        val produtos = produtoRepository.getAllList()
        if (produtos.isEmpty()) return null

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        val file = File(dir, "Valtio_Export_${System.currentTimeMillis()}.csv")

        FileWriter(file).use { writer ->
            writer.append("${context.getString(R.string.csv_header_name)},${context.getString(R.string.csv_header_brand)},${context.getString(R.string.csv_header_model)},${context.getString(R.string.csv_header_category)},${context.getString(R.string.csv_header_store)},${context.getString(R.string.csv_header_price)},${context.getString(R.string.csv_header_purchase_date)},${context.getString(R.string.csv_header_warranty_months)},${context.getString(R.string.csv_header_warranty_end)},${context.getString(R.string.csv_header_status)}\n")
            produtos.forEach { p ->
                writer.append("${escapeCsv(p.nome)},")
                writer.append("${escapeCsv(p.marca)},")
                writer.append("${escapeCsv(p.modelo)},")
                writer.append("${escapeCsv(p.categoria)},")
                writer.append("${escapeCsv(p.loja)},")
                writer.append("${String.format("%.2f", p.preco)},")
                writer.append("${p.dataCompra.format(dateFormatter)},")
                writer.append("${p.garantiaMeses},")
                writer.append("${p.dataFimGarantia?.format(dateFormatter) ?: "N/A"},")
                
                val statusLabel = when(p.estadoGarantia) {
                    com.valtio.app.domain.model.EstadoGarantia.ATIVA -> context.getString(R.string.warranty_status_active)
                    com.valtio.app.domain.model.EstadoGarantia.A_EXPIRAR -> context.getString(R.string.warranty_status_expiring)
                    com.valtio.app.domain.model.EstadoGarantia.EXPIRADA -> context.getString(R.string.warranty_status_expired)
                    com.valtio.app.domain.model.EstadoGarantia.SEM_GARANTIA -> context.getString(R.string.warranty_status_none)
                }
                writer.append("$statusLabel\n")
            }
        }
        return file
    }

    fun shareFile(file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (file.extension == "csv") "text/csv" else "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, context.getString(R.string.pdf_share_title))
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else value
    }
}