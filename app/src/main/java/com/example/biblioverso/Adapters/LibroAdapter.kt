package com.example.biblioverso.Adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.biblioverso.Models.Libro
import com.example.biblioverso.R
import com.example.biblioverso.Views.LibroActivity

class LibroAdapter(listaOriginal: List<Libro>) : RecyclerView.Adapter<LibroAdapter.ViewHolder>() {
    private var listaFiltrada = listaOriginal.toMutableList()

    fun actualizarLista(nuevaLista: List<Libro>) {
        listaFiltrada = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_libro, parent, false))
    }

    override fun getItemCount(): Int = listaFiltrada.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.render(listaFiltrada[position])
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itlTitulo: TextView = itemView.findViewById(R.id.itlTitulo)
        val itlAutor: TextView = itemView.findViewById(R.id.itlAutor)
        val itlAnio: TextView = itemView.findViewById(R.id.itlAnio)
        val itlSinopsis: TextView = itemView.findViewById(R.id.itlSinopsis)
        val itlImagen: ImageView = itemView.findViewById(R.id.itlImagen)

        fun render(libro: Libro) {
            itlTitulo.text = libro.titulo
            itlAutor.text = libro.autor[0].nombre
            itlAnio.text = libro.fechaPublicacion.take(4)
            itlSinopsis.text = libro.sinopsis
            val urlSegura = libro.portada.replace("http://", "https://")
            Glide.with(itlImagen.context)
                .load(urlSegura)
                .placeholder(R.drawable.icons8_imagen_100)
                .error(R.drawable.icons8_imagen_100)
                .into(itlImagen)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, LibroActivity::class.java)
                intent.putExtra("Libro", libro)
                itemView.context.startActivity(intent)
            }
        }
    }
}