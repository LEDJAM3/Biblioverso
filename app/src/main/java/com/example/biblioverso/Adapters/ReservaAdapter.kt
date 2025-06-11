package com.example.biblioverso.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.biblioverso.Models.ReservaAdap
import com.example.biblioverso.R
import com.example.biblioverso.Utils.formatearFechaCorta

class ReservaAdapter(private val reservas: List<ReservaAdap>) : RecyclerView.Adapter<ReservaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_reserva, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(reservas[position])
    }

    override fun getItemCount(): Int = reservas.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itrImagen: ImageView = itemView.findViewById(R.id.itrImagen)
        val itrTitulo: TextView = itemView.findViewById(R.id.itrTitulo)
        val itrFecha: TextView = itemView.findViewById(R.id.itrFecha)
        val itrEstado: TextView = itemView.findViewById(R.id.itrEstado)
        val btnCancelar: Button = itemView.findViewById(R.id.itrBtnCancelar)

        fun render(reserva: ReservaAdap) {
            itrTitulo.text = reserva.reserva.detalle_reserva[0].libro.titulo
            itrFecha.text = "Hasta: " + formatearFechaCorta(reserva.reserva.fec_limite)
            itrEstado.text = reserva.reserva.estado
            val urlSegura = reserva.reserva.detalle_reserva[0].libro.portada.replace("http://", "https://")

            if (reserva.reserva.estado == "Entregado") {
                itrEstado.setTextColor(itemView.context.getColor(R.color.verde))
                btnCancelar.visibility = View.GONE
                btnCancelar.isEnabled = false
                btnCancelar.isFocusable = false
                btnCancelar.setOnClickListener(null)
            } else {
                btnCancelar.setOnClickListener {

                }
            }

            Glide.with(itrImagen.context)
                .load(urlSegura)
                .placeholder(R.drawable.icons8_imagen_100)
                .error(R.drawable.icons8_imagen_100)
                .into(itrImagen)
        }
    }
}