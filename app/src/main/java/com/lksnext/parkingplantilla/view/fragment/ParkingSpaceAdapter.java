package com.lksnext.parkingplantilla.view.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Car;
import com.lksnext.parkingplantilla.domain.Plaza;

import java.util.List;

public class ParkingSpaceAdapter extends RecyclerView.Adapter<ParkingSpaceAdapter.PlazaViewHolder> {
    private List<Plaza> plazaList;
    private OnPlazaBookedListener bookListener;

    public interface OnPlazaBookedListener {
        void onPlazaBooked(Plaza plaza, View itemView);
    }

    public ParkingSpaceAdapter(List<Plaza> plazaList, OnPlazaBookedListener listener) {
        this.plazaList = plazaList;
        this.bookListener = listener;
    }

    @NonNull
    @Override
    public PlazaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_parking_space_item, parent, false);
        return new PlazaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlazaViewHolder holder, int position) {
        Plaza plaza = plazaList.get(position);

        holder.txtId.setText("PLAZA Nº "+ plaza.getId());
        holder.txtType.setText("PARA "+(plaza.getType().equals(Car.Type.COCHE) ? "COCHES" : "MOTOS"));
        holder.txtElectrico.setText("• " + (plaza.isElectrico() ? "Con" : "Sin") + " Cargador Eléctrico");
        holder.txtDiscapacitados.setText("• " + (plaza.isParaDiscapacitados() ? "" : "No")+" Apto Para Discapacitados");
        String label = plaza.getEtiqueta().toString();
        if(label=="CERO_EMISIONES")
            label = "\n  Cero Emisiones";
        holder.txtEtiqueta.setText("• Etiqueta Medioambiental: " + label);

        if (plaza.getType().equals(Car.Type.COCHE))
            holder.plazaTypeImage.setImageResource(R.drawable.ic_car);
        else
            holder.plazaTypeImage.setImageResource(R.drawable.ic_motorbike);

        holder.txtMensaje.setVisibility(View.GONE);

        if(plaza.isParaDiscapacitados())
            holder.icon1.setVisibility(View.GONE);

        if(plaza.isElectrico())
            holder.icon2.setVisibility(View.GONE);

        holder.btnReservar.setOnClickListener(v -> {
            if(bookListener != null){
                bookListener.onPlazaBooked(plaza, holder.itemView);
            }
        });

    }

    public void updateList(List<Plaza> newList){
        plazaList.clear();
        plazaList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return plazaList.size();
    }

    static class PlazaViewHolder extends RecyclerView.ViewHolder {
        TextView txtId;
        TextView txtType;
        TextView txtEtiqueta;
        TextView txtDiscapacitados;
        TextView txtElectrico;
        ImageView plazaTypeImage;
        ImageView icon1;
        ImageView icon2;
        Button btnReservar;
        TextView txtMensaje;

        public PlazaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtID);
            txtDiscapacitados = itemView.findViewById(R.id.txtDiscapacitados);
            txtType = itemView.findViewById(R.id.txtSpaceType);
            txtEtiqueta = itemView.findViewById(R.id.txtEtiqueta);
            txtElectrico = itemView.findViewById(R.id.txtElectrico);
            txtMensaje = itemView.findViewById(R.id.txtMessage);
            btnReservar = itemView.findViewById(R.id.btnBook);
            plazaTypeImage = itemView.findViewById(R.id.carTypeImage);
            icon1 = itemView.findViewById(R.id.icon1);
            icon2 = itemView.findViewById(R.id.icon2);
        }
    }
}