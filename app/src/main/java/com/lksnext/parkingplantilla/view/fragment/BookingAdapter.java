package com.lksnext.parkingplantilla.view.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Fecha;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Reserva> reservaList;
    private OnBookEditListener bookListener;

    public interface OnBookEditListener {
        void onCancel(Reserva reserva);
        void onAdd15Min(Reserva reserva, View view);
    }

    public BookingAdapter(List<Reserva> reservaList, OnBookEditListener listener) {
        this.reservaList = reservaList;
        this.bookListener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_booking_item, parent, false);
        return new BookingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Reserva reserva = reservaList.get(position);
        Car.Type tipo = reserva.getPlaza().getType();
        holder.txtIdType.setText("PLAZA Nº " + reserva.getPlaza().getId() + " - PARA " + tipo.toString());
        if(tipo == Car.Type.COCHE) {
            holder.plazaTypeImage.setImageResource(R.drawable.ic_car);
            holder.txtMatricula.setText("COCHE: " + reserva.getCar());
        } else {
            holder.plazaTypeImage.setImageResource(R.drawable.ic_motorbike);
            holder.txtMatricula.setText("MOTO: " + reserva.getCar());
        }

        holder.txtFecha.setText(reserva.getDay().toString());
        holder.txtHoras.setText(reserva.getIniTime().toString() + " - " + reserva.getEndTime().toString());

        holder.txtElectrico.setText("• " + (reserva.getPlaza().isElectrico() ? "Con" : "Sin") + " Cargador Eléctrico");
        holder.txtDiscapacitados.setText("• " + (reserva.getPlaza().isParaDiscapacitados() ? "" : "No")+" Apto Para Discapacitados");
        String label = reserva.getPlaza().getEtiqueta().toString();
        if(label.equals("CERO_EMISIONES"))
            label = "\n  Cero Emisiones";
        holder.txtEtiqueta.setText("• Etiqueta Medioambiental: " + label);

        if (reserva.isCancelled()){
            holder.btnCancelar.setVisibility(View.GONE);
            holder.btnAdd15.setVisibility(View.GONE);
        }else
            holder.txtMensaje.setVisibility(View.GONE);

        if(!(reserva.getDay().compareTo(Fecha.fechaActual()) >= 0 && reserva.getEndTime().compareTo(Hora.horaActual()) < 0)){
            holder.btnCancelar.setVisibility(View.GONE);
            holder.btnAdd15.setVisibility(View.GONE);
        }

        if(reserva.getPlaza().isParaDiscapacitados())
            holder.icon1.setVisibility(View.GONE);

        if(reserva.getPlaza().isElectrico())
            holder.icon2.setVisibility(View.GONE);

        holder.btnCancelar.setOnClickListener(v -> {
            if(bookListener != null){
                bookListener.onCancel(reserva);
            }
        });

        holder.btnAdd15.setOnClickListener(v -> {
            if(bookListener != null){
                bookListener.onAdd15Min(reserva, holder.itemView);
            }
        });

    }

    public void updateList(List<Reserva> newList){
        reservaList.clear();
        reservaList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reservaList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView txtIdType;
        TextView txtMatricula;
        TextView txtEtiqueta;
        TextView txtDiscapacitados;
        TextView txtElectrico;
        TextView txtFecha;
        TextView txtHoras;
        ImageView plazaTypeImage;
        ImageView icon1;
        ImageView icon2;
        Button btnCancelar;
        ImageButton btnAdd15;
        TextView txtMensaje;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIdType = itemView.findViewById(R.id.txtIdType);
            txtDiscapacitados = itemView.findViewById(R.id.txtDiscapacitados);
            txtMatricula = itemView.findViewById(R.id.txtMatricula);
            txtEtiqueta = itemView.findViewById(R.id.txtEtiqueta);
            txtElectrico = itemView.findViewById(R.id.txtElectrico);
            txtMensaje = itemView.findViewById(R.id.txtMessage);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtHoras = itemView.findViewById(R.id.txtHoras);
            btnCancelar = itemView.findViewById(R.id.btnCancelarReserva);
            plazaTypeImage = itemView.findViewById(R.id.carTypeImage);
            btnAdd15 = itemView.findViewById(R.id.btnAdd15Min);
            icon1 = itemView.findViewById(R.id.icon1);
            icon2 = itemView.findViewById(R.id.icon2);
        }
    }
}